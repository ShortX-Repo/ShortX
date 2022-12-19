package tornaco.apps.shortx.services.overlay

import android.os.Handler
import android.os.Looper
import tornaco.apps.shortx.core.proto.action.OverlayButtonSetting
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.postSafety

class OverlayService(shortXContext: ShortXContext) : SystemService(shortXContext) {
    private val mainHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    private val overlayClassLoader by lazy {
        OverlayClassLoader(context)
    }
    private var overlay: Any? = null

    override fun systemReady() {
        super.systemReady()
        shortXContext.appState.onPackageChanged {
            hideOverlay(dispose = true)
            logger.d("Dispose overlay object since apk updated")
        }

        shortXContext.rule.addRuleChangeListener {
            logger.d("Rule changed.")
            shortXContext.rule.getAllRules().filter {
                it.isEnabled
            }.forEach { rule ->

            }
        }
    }

    fun showOverlay(buttonSettingsList: List<OverlayButtonSetting>) = mainHandler.postSafety {
        logger.d("showOverlay")
        if (overlay == null) {
            overlay = overlayClassLoader.newComposeOverlayObject(buttonSettingsList)
        }
        overlay?.show()
    }

    fun hideOverlay(dispose: Boolean = false) = mainHandler.postSafety {
        logger.d("hideOverlay")
        overlay?.dismiss()
        if (dispose) {
            logger.d("dispose overlay.")
            overlay = null
        }
    }
}