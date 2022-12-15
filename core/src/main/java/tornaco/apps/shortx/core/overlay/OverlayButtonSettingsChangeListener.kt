package tornaco.apps.shortx.core.overlay

import android.os.Handler
import android.os.Looper

abstract class OverlayButtonSettingsChangeListener : IOverlayButtonSettingsChangeListener.Stub() {

    private val handler by lazy { Handler(Looper.getMainLooper()) }
    override fun overlayButtonSettingsChanged(index: Int) {
        handler.post {
            postOverlayButtonSettingsChanged(index)
        }
    }

    abstract fun postOverlayButtonSettingsChanged(index: Int)
}