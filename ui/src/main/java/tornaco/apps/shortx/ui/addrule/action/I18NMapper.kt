package tornaco.apps.shortx.ui.addrule.action

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.models.Action
import tornaco.apps.shortx.ui.base.I18N
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.PREFIX_APP_ICON


fun labelAndDescriptionForActionSelector(
    i18N: I18N,
    action: Class<out Action>
): Pair<String, String?> {
    return when (action) {
        Action.ShowToast::class.java -> {
            i18N["ui.action.show.toast"] to null
        }
        Action.ShellCommand::class.java -> {
            i18N["ui.action.shell"] to null
        }
        Action.Delay::class.java -> {
            i18N["ui.action.delay"] to null
        }
        Action.TakeScreenshot::class.java -> {
            i18N["ui.action.take.screenshot"] to null
        }
        Action.MVEL::class.java -> {
            i18N["ui.title.mvel"] to null
        }
        Action.HideOverlay::class.java -> {
            i18N["ui.action.hide.overlay"] to null
        }
        Action.ShowOverlay::class.java -> {
            i18N["ui.action.show.overlay"] to null
        }
        Action.LaunchApp::class.java -> {
            i18N["ui.action.launch.app"] to null
        }
        else -> {
            "N/A" to null
        }
    }
}

@Composable
fun labelLinesForAction(action: Action): List<String> {
    return when (action) {
        is Action.ShowToast -> {
            listOf(LocalI18N.current["ui.action.show.toast"], action.message)
        }
        is Action.ShellCommand -> {
            listOf(LocalI18N.current["ui.action.shell"], action.command)
        }
        is Action.Delay -> {
            listOf(LocalI18N.current["ui.action.delay"], action.time.toString())
        }
        Action.TakeScreenshot -> {
            listOf(LocalI18N.current["ui.action.take.screenshot"])
        }
        is Action.MVEL -> {
            listOf(LocalI18N.current["ui.title.mvel"], action.expression)
        }
        Action.HideOverlay -> {
            listOf(LocalI18N.current["ui.action.hide.overlay"])
        }
        is Action.ShowOverlay -> {
            mutableListOf(LocalI18N.current["ui.action.show.overlay"]).apply {
                add("${action.settings.size} buttons")
            }
        }
        is Action.LaunchApp -> listOf(
            LocalI18N.current["ui.action.launch.app"],
            "${action.appLabel}$PREFIX_APP_ICON${action.appPkg.pkgName}"
        )
    }
}

@Composable
fun IconForAction(action: Class<out Action>) {
    when (action) {
        Action.ShowToast::class.java -> {
            Icon(
                imageVector = Icons.Filled.Message, contentDescription = "Toast"
            )
        }
        Action.ShellCommand::class.java -> {
            Icon(
                imageVector = Icons.Filled.Tag, contentDescription = "Shell"
            )
        }
        Action.Delay::class.java -> {
            Icon(
                imageVector = Icons.Filled.Timer, contentDescription = "Delay"
            )
        }
        Action.TakeScreenshot::class.java -> {
            Icon(
                imageVector = Icons.Filled.Devices, contentDescription = "Take Screenshot"
            )
        }
        Action.MVEL::class.java -> {
            Icon(
                imageVector = Icons.Filled.Code, contentDescription = "MVEL"
            )
        }
        Action.HideOverlay::class.java -> {
            Icon(
                imageVector = Icons.Filled.Window, contentDescription = "HideOverlay"
            )
        }
        Action.ShowOverlay::class.java -> {
            Icon(
                imageVector = Icons.Filled.Window, contentDescription = "ShowOverlay"
            )
        }
        Action.LaunchApp::class.java -> {
            Icon(
                imageVector = Icons.Filled.OpenInNew, contentDescription = "LaunchApp"
            )
        }
    }
}