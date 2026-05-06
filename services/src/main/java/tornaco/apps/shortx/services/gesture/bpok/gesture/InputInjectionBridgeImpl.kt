package tornaco.apps.shortx.services.gesture.bpok.gesture

import android.view.InputEvent
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.input.IMSFunc

class InputInjectionBridgeImpl : InputInjectionBridge {
    private val logger = Logger("InputInjectionBridge")

    override fun injectEvent(event: InputEvent) {
        try {
            IMSFunc.injectInputEvent(event)
        } catch (e: Exception) {
            logger.e(e, "Failed to inject input event")
        }
    }

    override fun setFilterEnabled(enabled: Boolean) {
        // Filter enable/disable is handled at the hook level, not here.
        // This is a no-op for now — the hook framework manages filter state.
    }
}
