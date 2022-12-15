package tornaco.apps.shortx.services.overlay

import android.os.Handler
import android.os.Looper
import android.os.RemoteCallbackList
import tornaco.apps.shortx.core.overlay.IOverlayButtonSettingsChangeListener
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.Settings
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.postSafety

class OverlayService(shortXContext: ShortXContext) : SystemService(shortXContext) {
    private val mainHandler by lazy {
        Handler(Looper.getMainLooper())
    }
    private val overlayButtonSettingsChangeListenerCallbacks by lazy {
        RemoteCallbackList<IOverlayButtonSettingsChangeListener>()
    }

    private val overlayClassLoader by lazy {
        OverlayClassLoader(context)
    }
    private var overlay: Any? = null

    override fun systemReady() {
        super.systemReady()
        shortXContext.appState.onPackageChanged {
            hideOverlay()
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

    fun getOverlayButtonIconName(index: Int): String {
        return store.read(Settings.StringSettings.OverlayButtonIconForIndex(index))
    }

    fun setOverlayButtonIconName(index: Int, name: String) {
        store.write(Settings.StringSettings.OverlayButtonIconForIndex(index), name)
        overlayButtonIconChanged(index)
    }

    fun registerOverlayButtonSettingsChangeListener(listener: IOverlayButtonSettingsChangeListener) {
        overlayButtonSettingsChangeListenerCallbacks.register(listener)
    }

    fun unRegisterOverlayButtonSettingsChangeListener(listener: IOverlayButtonSettingsChangeListener) {
        overlayButtonSettingsChangeListenerCallbacks.unregister(listener)
    }

    private fun overlayButtonIconChanged(index: Int) = handler.postSafety {
        overlayButtonSettingsChangeListenerCallbacks.broadcast {
            kotlin.runCatching { it.overlayButtonSettingsChanged(index) }.onFailure {
                logger.e(it, "Error call overlayButtonSettingsChanged")
            }
        }
    }
}