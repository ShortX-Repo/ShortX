package tornaco.apps.shortx.services.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import tornaco.apps.shortx.core.doIfDebug
import tornaco.apps.shortx.core.settings.defaultDanmuUISettings
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.postSafety

class UIService(shortXContext: ShortXContext) : SystemService(shortXContext) {
    private val uiHandler by lazy {
        require(isSystemReady) { "Pls access uiHandler after systemReady" }
        Handler(Looper.getMainLooper())
    }

    private var liveDanmuView: LiveDanmuView? = null

    override fun start(context: Context) {
        super.start(context)
        PicassoInitializer.install(context)
    }

    fun showDanmu(icon: String?, text: String) {
        if (isSystemReady) {
            uiHandler.postSafety {
                val viewAlive = liveDanmuView?.isAttached() ?: false
                if (!viewAlive) {
                    liveDanmuView =
                        LiveDanmuView(context, defaultDanmuUISettings).apply { attach() }
                }
                doIfDebug {
                    logger.d("showDanmu: $liveDanmuView $icon $text")
                }
                liveDanmuView?.addDanmu(Danmu(icon, text))
            }
        }
    }
}