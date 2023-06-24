package tornaco.apps.shortx.services.rule.actor.impl

import android.view.KeyEvent
import com.google.protobuf.Message
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object InjectKeyCode : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.InjectKeyCode::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val data =
            action unpack_ tornaco.apps.shortx.core.proto.action.InjectKeyCode::class.java
        val keyCode = data.keyCode
        ctrl.injectKeycode(
            KeyEvent.ACTION_DOWN,
            keyCode,
            0,
            0,
        )
        delay(100)
        ctrl.injectKeycode(
            KeyEvent.ACTION_UP,
            keyCode,
            0,
            0,
        )
    }

}