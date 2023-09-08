package tornaco.apps.shortx.services.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.proto.common.QSTileList
import tornaco.apps.shortx.core.proto.settings.DanmuUISettings
import tornaco.apps.shortx.core.rule.action.emptyByteArrayWrapper
import tornaco.apps.shortx.core.systemui.IQSTileHost
import tornaco.apps.shortx.core.util.RemoteCallbackListExt
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.Settings
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.postSafety
import java.time.Duration

class UIService(shortXContext: ShortXContext) : SystemService(shortXContext, "UIService") {
    private val qsTileHost = RemoteCallbackListExt<IQSTileHost>()

    private val uiHandler by lazy {
        require(isSystemReady) { "Pls access uiHandler after systemReady" }
        Handler(Looper.getMainLooper())
    }

    private var liveDanmuView: LiveDanmuView? = null

    override fun start(context: Context) {
        super.start(context)
        PicassoInitializer.install(context)
    }

    override fun systemReady() {
        super.systemReady()
    }

    fun showDanmu(icon: String?, text: String) {
        if (isSystemReady) {
            uiHandler.postSafety {
                val viewAlive = liveDanmuView?.isAttached() ?: false
                if (!viewAlive) {
                    liveDanmuView =
                        LiveDanmuView(context, getDanmuUISettings()).apply { attach() }
                }
                logger.d("showDanmu: $liveDanmuView $icon $text")
                liveDanmuView?.addDanmu(Danmu(icon, text))
            }
        }
    }

    fun getDanmuUISettings(): DanmuUISettings {
        return DanmuUISettings.newBuilder()
            .setAlpha(store.read(Settings.FloatSettings.DanmuAlpha))
            .setBackgroundColor(store.read(Settings.IntSettings.DanmuBgColor))
            .setDuration(store.read(Settings.IntSettings.DanmuDurationSeconds).toLong())
            .setTextColor(store.read(Settings.IntSettings.DanmuTextColor))
            .setTextSizeSp(store.read(Settings.IntSettings.DanmuTextSize))
            .build()
    }

    fun setDanmuUISettings(settings: DanmuUISettings) {
        store.write(Settings.FloatSettings.DanmuAlpha, settings.alpha)
        store.write(Settings.IntSettings.DanmuBgColor, settings.backgroundColor)
        store.write(Settings.IntSettings.DanmuDurationSeconds, settings.duration.toInt())
        store.write(Settings.IntSettings.DanmuTextColor, settings.textColor)
        store.write(Settings.IntSettings.DanmuTextSize, settings.textSizeSp)

        liveDanmuView?.apply {
            uiHandler.postSafety {
                if (isAttached()) {
                    detach()
                }
            }
        }
        liveDanmuView = null
    }

    fun attachQSTileHost(host: IQSTileHost) {
        logger.d("attachQSTileHost: $host")
        qsTileHost.clear()
        qsTileHost.register(host)

        val receiver = SynchronousResultReceiver.get()
        host.getTiles(receiver)
        val result = receiver.awaitResultNoInterrupt(Duration.ofSeconds(10))
        val tileList = QSTileList.parseFrom(result.getValue(emptyByteArrayWrapper).byteData)
        logger.d("tileList: ${tileList.tileList.joinToString { it.label }}")
    }
}