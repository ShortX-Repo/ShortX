package tornaco.apps.shortx.ui.addrule.models

import tornaco.apps.shortx.core.proto.action.*
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_

sealed interface Action {
    object TakeScreenshot : Action
    data class ShellCommand(val command: String) : Action
    data class Delay(val time: Long) : Action
    data class ShowToast(val message: String) : Action
    data class MVEL(val expression: String) : Action
    object ShowOverlay : Action
    object HideOverlay : Action
}

fun Action.toProtoAction(): ProtoMessage {
    return when (this) {
        is Action.TakeScreenshot -> {
            TakeScreenshot.newBuilder().build()
        }
        is Action.ShellCommand -> {
            ShellCommand.newBuilder().setCommand(command).build()
        }
        is Action.Delay -> {
            Delay.newBuilder().setTime(time).build()
        }
        is Action.ShowToast -> {
            ShowToast.newBuilder().setMessage(message).build()
        }
        is Action.MVEL -> {
            ExecuteMVEL.newBuilder().setExpression(expression).build()
        }
        Action.HideOverlay -> {
            HideOverlayButton.newBuilder().build()
        }
        Action.ShowOverlay -> {
            HideOverlayButton.newBuilder().build()
        }
    }
}

fun ProtoAny.toAction(): Action? {
    return when {
        this is_ TakeScreenshot::class.java -> {
            Action.TakeScreenshot
        }
        this is_ ShellCommand::class.java -> {
            val data = this unpack_ ShellCommand::class.java
            Action.ShellCommand(command = data.command)
        }
        this is_ ShowToast::class.java -> {
            val data = this unpack_ ShowToast::class.java
            Action.ShowToast(message = data.message)
        }
        this is_ Delay::class.java -> {
            val data = this unpack_ Delay::class.java
            Action.Delay(time = data.time)
        }
        this is_ ExecuteMVEL::class.java -> {
            val data = this unpack_ ExecuteMVEL::class.java
            Action.MVEL(expression = data.expression)
        }
        this is_ ShowOverlayButton::class.java -> {
            Action.ShowOverlay
        }
        this is_ HideOverlayButton::class.java -> {
            Action.HideOverlay
        }
        else -> null
    }
}