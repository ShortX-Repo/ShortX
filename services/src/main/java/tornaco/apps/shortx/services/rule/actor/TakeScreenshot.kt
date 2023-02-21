package tornaco.apps.shortx.services.rule.actor

import android.content.Context
import android.os.Handler
import android.view.KeyEvent
import kotlinx.coroutines.delay
import tornaco.apps.shortx.services.ctrl.Controller
import tornaco.apps.shortx.services.ctrl.Device
import tornaco.apps.shortx.services.ctrl.Options

class TakeScreenshot(private val context: Context, private val handler: Handler) {
    suspend operator fun invoke() {
        val device = Device(Options())
        val ctrl = Controller(device)

        ctrl.injectKeycode(
            KeyEvent.ACTION_DOWN,
            KeyEvent.KEYCODE_SYSRQ,
            0,
            0,
        )
        delay(100)
        ctrl.injectKeycode(
            KeyEvent.ACTION_UP,
            KeyEvent.KEYCODE_SYSRQ,
            0,
            0,
        )
    }
}