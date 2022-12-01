package tornaco.apps.shortx.ui.addrule.models

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.ui.graphics.vector.ImageVector
import tornaco.apps.shortx.core.proto.action.Delay
import tornaco.apps.shortx.core.proto.action.ShellCommand
import tornaco.apps.shortx.core.proto.action.ShowToast
import tornaco.apps.shortx.core.proto.action.TakeScreenshot
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_

sealed class Action(val label: (Context) -> String, val icon: ImageVector) {
    object TakeScreenshot : Action({ "Take Screenshot" }, Icons.Filled.Key)
    data class ShellCommand(val command: String) :
        Action({ "ShellCommand $command" }, Icons.Filled.Key)

    data class Delay(val time: Long) :
        Action({ "Delay $time" }, Icons.Filled.Key)

    data class ShowToast(val message: String) :
        Action({ "ShowToast $message" }, Icons.Filled.Key)
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
        else -> null
    }
}