package tornaco.apps.shortx.services.overlay

import android.os.Handler
import android.os.Looper
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
            overlay?.dismiss()
            overlay = null
            logger.d("Dispose overlay object since apk updated")
        }
    }

    fun showOverlay() = mainHandler.postSafety {
        logger.d("showOverlay")
        if (overlay == null) {
            overlay = overlayClassLoader.getShortXObject()
        }
        overlay?.show()
    }

    fun hideOverlay() = mainHandler.postSafety {
        logger.d("hideOverlay")
        overlay?.dismiss()
    }
}