package tornaco.apps.shortx.services.rule.actor.impl

import android.view.KeyEvent
import com.google.protobuf.Message
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object TakeScreenshot : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.TakeScreenshot::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) {
        InjectKeyCode.injectKey(KeyEvent.KEYCODE_SYSRQ)
    }
}