package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.ctrl.Device.INJECT_MODE_ASYNC
import tornaco.apps.shortx.services.input.InputShellCommand
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

        injectKey(keyCode)
    }

    fun injectKey(keyCode: Int) {
        runCatching {
            InputShellCommand.onCommandOrThrow("keyevent $keyCode")
        }.onFailure {
            logger.d("onCommandOrThrow error: ${it.stackTraceToString()}")
            // Not work on LineageOS 21.0. why?
            device.pressReleaseKeycode(
                keyCode,
                INJECT_MODE_ASYNC
            )
        }
    }

}