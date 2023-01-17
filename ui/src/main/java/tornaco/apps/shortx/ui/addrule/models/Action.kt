package tornaco.apps.shortx.ui.addrule.models

import tornaco.apps.shortx.core.Pkg
import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.proto.action.*
import tornaco.apps.shortx.core.proto.common.AppPkg
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
    data class ShowOverlay(val autoCollapse: Boolean, val settings: List<OverlayButtonSetting>) :
        Action

    object HideOverlay : Action
    data class LaunchApp(val appPkg: AppPkg, val appLabel: String) : Action
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
        is Action.ShowOverlay -> {
            ShowOverlayButton.newBuilder().setAutoCollapse(autoCollapse)
                .addAllButtonSettings(settings).build()
        }
        is Action.LaunchApp -> {
            LaunchApp.newBuilder().setAppPkg(appPkg).build()
        }
    }
}

fun ProtoAny.toAction(shortXManager: ShortXManager): Action? {
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
            val data = this unpack_ ShowOverlayButton::class.java
            Action.ShowOverlay(autoCollapse = data.autoCollapse, settings = data.buttonSettingsList)
        }
        this is_ HideOverlayButton::class.java -> {
            Action.HideOverlay
        }
        this is_ LaunchApp::class.java -> {
            val data = this unpack_ LaunchApp::class.java
            val pkg = Pkg(data.appPkg.pkgName, userId = data.appPkg.userId)
            val appLabel = shortXManager.getAppLabel(pkg)
            Action.LaunchApp(data.appPkg, appLabel = appLabel)
        }
        else -> null
    }
}