package tornaco.apps.shortx.services.ui

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.core.BuildPropExt.onDebugBuild
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.proto.common.QSTileList
import tornaco.apps.shortx.core.proto.settings.DanmuUISettings
import tornaco.apps.shortx.core.res.PicassoInitializer
import tornaco.apps.shortx.core.rule.action.emptyByteArrayWrapper
import tornaco.apps.shortx.core.systemui.IQSTileHost
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.RemoteCallbackListExt
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.data.Settings
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.ui.danmu.DanmakuService
import tornaco.apps.shortx.services.watchdog.postSafety
import java.time.Duration

class UIService(shortXContext: ShortXContext) : SystemService(shortXContext, "UIService") {
    private val qsTileHost = RemoteCallbackListExt<IQSTileHost>()

    private val uiHandler by lazy {
        require(isSystemReady) { "Pls access uiHandler after systemReady" }
        Handler(Looper.getMainLooper())
    }

    private var liveDanmuView: LiveDanmuView? = null
    private var danmuService: DanmakuService? = null

    override fun systemReady() {
        super.systemReady()
        PicassoInitializer.install(
            context,
            Dirs.picassoCacheDir(),
            BuildPropInternal.APP_PACKAGE_NAME
        )
    }

    fun showDanmu(id: String = "ShortX-Internal", icon: String?, text: String) {
        if (isSystemReady) {
            uiHandler.postSafety {
                if (OsUtils.isROrAbove() && false) {
                    showDanmuCRDroidDanmuService(icon, text)
                } else {
                    showDanmuFallback(id = id, icon = icon, text = text)
                }
            }
        }
    }

    fun cancelDanmuForId(id: String) {
        if (isSystemReady) {
            uiHandler.postSafety {
                val viewAlive = liveDanmuView?.isAttached() ?: false
                if (viewAlive) liveDanmuView?.cancelDanmuFor(id)
            }
        }
    }

    private fun showDanmuCRDroidDanmuService(icon: String?, text: String) {
        if (danmuService == null) {
            danmuService = DanmakuService(context)
        }
        logger.d("showDanmuCRDroidDanmuService: $danmuService $icon $text")
        danmuService?.showNotificationAsOverlay(text)
    }


    private fun showDanmuFallback(id: String, icon: String?, text: String) {
        val viewAlive = liveDanmuView?.isAttached() ?: false
        if (!viewAlive) {
            liveDanmuView =
                LiveDanmuView(context, getDanmuUISettings()).apply { attach() }
        }
        logger.d("showDanmuFallback: $liveDanmuView $icon $text")
        liveDanmuView?.addDanmu(Danmu(id = id, icon = icon, text = text))
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

        onDebugBuild {
            val receiver = SynchronousResultReceiver.get()
            host.getTiles(receiver)
            val result = runBlocking { receiver.awaitResultNoInterrupt(Duration.ofSeconds(10)) }
            val tileList = QSTileList.parseFrom(result.getValue(emptyByteArrayWrapper)?.byteData)
            logger.d("tileList: ${tileList.tileList.joinToString { it.label }}")

            tileList.tileList.forEach {
                val state = host.getState(it.tileSpec)
                logger.d("state: ${it.label} $state")
            }
        }
    }

    fun getAllQSTiles(): QSTileList {
        val itemCount = qsTileHost.registeredCallbackCount
        if (itemCount == 0) {
            logger.d("getAllQSTiles, no registered qsTileHost.")
            return QSTileList.getDefaultInstance()
        }
        val first = qsTileHost.getRegisteredCallbackItem(0)
        val receiver = SynchronousResultReceiver.get()
        first.getTiles(receiver)
        val result = runBlocking { receiver.awaitResultNoInterrupt(Duration.ofSeconds(5)) }
        val list = runCatching {
            QSTileList.parseFrom(result.getValue(emptyByteArrayWrapper)?.byteData)
        }.getOrElse {
            logger.e(it, "getAllQSTiles")
            QSTileList.getDefaultInstance()
        }
        logger.d("getAllQSTiles, return: ${list.tileList.map { it.tileSpec }}")
        return list
    }

    fun clickTile(tileSpec: String) {
        val itemCount = qsTileHost.registeredCallbackCount
        if (itemCount == 0) {
            throw Error("no registered qsTileHost")
        }
        logger.d("ClickTile: $tileSpec")
        val first = qsTileHost.getRegisteredCallbackItem(0)
        first.click(tileSpec)
    }

    fun longClickTile(tileSpec: String) {
        val itemCount = qsTileHost.registeredCallbackCount
        if (itemCount == 0) {
            throw Error("no registered qsTileHost")
        }
        logger.d("longClickTile: $tileSpec")
        val first = qsTileHost.getRegisteredCallbackItem(0)
        first.longClick(tileSpec)
    }

    fun getTileState(tileSpec: String): Int {
        val itemCount = qsTileHost.registeredCallbackCount
        if (itemCount == 0) {
            throw Error("no registered qsTileHost")
        }
        logger.d("getTileState: $tileSpec")
        val first = qsTileHost.getRegisteredCallbackItem(0)
        return first.getState(tileSpec)
    }
}