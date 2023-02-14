package tornaco.apps.shortx.ui.addrule.action

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
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

        Action.DisableBT -> listOf(LocalI18N.current["ui.action.enable", mapOf("s" to LocalI18N.current["ui.action.bt"])])
        Action.DisableDND -> listOf(LocalI18N.current["ui.action.enable", mapOf("s" to LocalI18N.current["ui.action.dnd"])])
        Action.DisableDarkMode -> listOf(LocalI18N.current["ui.action.enable", mapOf("s" to LocalI18N.current["ui.action.dark.mode"])])
        Action.DisableData -> listOf(LocalI18N.current["ui.action.enable", mapOf("s" to LocalI18N.current["ui.action.data"])])
        Action.DisableFlashLight -> listOf(LocalI18N.current["ui.action.enable", mapOf("s" to LocalI18N.current["ui.action.flashlight"])])
        Action.DisableHotSpot -> listOf(LocalI18N.current["ui.action.enable", mapOf("s" to LocalI18N.current["ui.action.hotspot"])])
        Action.DisableNFC -> listOf(LocalI18N.current["ui.action.enable", mapOf("s" to LocalI18N.current["ui.action.nfc"])])
        Action.DisableWifi -> listOf(LocalI18N.current["ui.action.enable", mapOf("s" to LocalI18N.current["ui.action.wifi"])])

        Action.EnableBT -> listOf(LocalI18N.current["ui.action.disable", mapOf("s" to LocalI18N.current["ui.action.bt"])])
        Action.EnableDND -> listOf(LocalI18N.current["ui.action.disable", mapOf("s" to LocalI18N.current["ui.action.dnd"])])
        Action.EnableDarkMode -> listOf(LocalI18N.current["ui.action.disable", mapOf("s" to LocalI18N.current["ui.action.dark.mode"])])
        Action.EnableData -> listOf(LocalI18N.current["ui.action.disable", mapOf("s" to LocalI18N.current["ui.action.data"])])
        Action.EnableFlashLight -> listOf(LocalI18N.current["ui.action.disable", mapOf("s" to LocalI18N.current["ui.action.flashlight"])])
        Action.EnableHotSpot -> listOf(LocalI18N.current["ui.action.disable", mapOf("s" to LocalI18N.current["ui.action.hotspot"])])
        Action.EnableNFC -> listOf(LocalI18N.current["ui.action.disable", mapOf("s" to LocalI18N.current["ui.action.nfc"])])
        Action.EnableWifi -> listOf(LocalI18N.current["ui.action.disable", mapOf("s" to LocalI18N.current["ui.action.wifi"])])
    }
}

fun imageVectorForAction(action: Class<out Action>): ImageVector {
    return when (action) {
        Action.ShowToast::class.java -> {
            Icons.Outlined.Message
        }
        Action.ShellCommand::class.java -> {
            Icons.Outlined.Tag
        }
        Action.Delay::class.java -> {
            Icons.Outlined.Timer
        }
        Action.TakeScreenshot::class.java -> {
            Icons.Outlined.Devices
        }
        Action.MVEL::class.java -> {
            Icons.Outlined.Code
        }
        Action.HideOverlay::class.java -> {
            Icons.Outlined.Window
        }
        Action.ShowOverlay::class.java -> {
            Icons.Outlined.Window
        }
        Action.LaunchApp::class.java -> {
            Icons.Outlined.OpenInNew
        }
        else -> {
            Icons.Outlined.Settings
        }
    }
}