package tornaco.apps.shortx.services.rule.actor.impl

import android.view.KeyEvent
import com.google.protobuf.Message
import kotlinx.coroutines.delay
import tornaco.apps.shortx.services.ctrl.Controller
import tornaco.apps.shortx.services.ctrl.Device
import tornaco.apps.shortx.services.ctrl.Options
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object TakeScreenshot : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.TakeScreenshot::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
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