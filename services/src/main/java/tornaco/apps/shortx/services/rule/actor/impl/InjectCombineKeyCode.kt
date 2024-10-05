package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.ctrl.Device.INJECT_MODE_ASYNC
import tornaco.apps.shortx.services.input.InputShellCommand
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object InjectCombineKeyCode : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.InjectCombineKeyCode::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val data =
            action unpack_ tornaco.apps.shortx.core.proto.action.InjectCombineKeyCode::class.java
        val keyCodes = data.keyCodeList
        logger.d("inject codes: $keyCodes")

        runCatching {
            // keycombination
            val command = if (OsUtils.isSOrAbove()) {
                "keycombination ${keyCodes.joinToString(" ")}"
            } else error("keycombination not supported with input command.")
            InputShellCommand.onCommandOrThrow(command)
        }.onFailure {
            logger.d("onCommandOrThrow error: ${it.stackTraceToString()}")
            device.pressReleaseKeycodes(
                keyCodes.toIntArray(),
                INJECT_MODE_ASYNC
            )
        }
    }

}