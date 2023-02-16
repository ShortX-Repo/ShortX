package tornaco.apps.shortx.ui.addrule.action

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import tornaco.apps.shortx.ui.addrule.labelLinesFromAppsAndPkgSets
import tornaco.apps.shortx.ui.addrule.models.Action
import tornaco.apps.shortx.ui.base.I18N
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.PREFIX_APP_ICON
import kotlin.reflect.KClass

fun labelAndDescriptionForActionSelector(
    i18N: I18N,
    action: KClass<out Action>
): Pair<String, String?> {
    return when (action) {
        Action.ShowToast::class -> {
            i18N["ui.action.show.toast"] to null
        }
        Action.ShellCommand::class -> {
            i18N["ui.action.shell"] to null
        }
        Action.Delay::class -> {
            i18N["ui.action.delay"] to null
        }
        Action.TakeScreenshot::class -> {
            i18N["ui.action.take.screenshot"] to null
        }
        Action.MVEL::class -> {
            i18N["ui.title.mvel"] to null
        }
        Action.HideOverlay::class -> {
            i18N["ui.action.hide.overlay"] to null
        }
        Action.ShowOverlay::class -> {
            i18N["ui.action.show.overlay"] to null
        }
        Action.LaunchApp::class -> {
            i18N["ui.action.launch.app"] to null
        }

        Action.DisableBT::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.bt"])] to null
        Action.DisableDND::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.dnd"])] to null
        Action.DisableDarkMode::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.dark.mode"])] to null
        Action.DisableData::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.data"])] to null
        Action.DisableFlashLight::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.flashlight"])] to null
        Action.DisableHotSpot::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.hotspot"])] to null
        Action.DisableNFC::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.nfc"])] to null
        Action.DisableWifi::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.wifi"])] to null

        Action.EnableBT::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.bt"])] to null
        Action.EnableDND::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.dnd"])] to null
        Action.EnableDarkMode::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.dark.mode"])] to null
        Action.EnableData::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.data"])] to null
        Action.EnableFlashLight::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.flashlight"])] to null
        Action.EnableHotSpot::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.hotspot"])] to null
        Action.EnableNFC::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.nfc"])] to null
        Action.EnableWifi::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.wifi"])] to null

        Action.StopApp::class -> i18N["ui.action.stop.app"] to null
        Action.StartAppProcess::class -> i18N["ui.action.start.app.process"] to null
        Action.EnableApp::class -> i18N["ui.action.enable.app"] to null
        Action.DisableApp::class -> i18N["ui.action.disable.app"] to null
        Action.SuspendApp::class -> i18N["ui.action.suspend.app"] to null
        Action.UnsuspendApp::class -> i18N["ui.action.unsuspend.app"] to null

        else -> {
            "N/A" to null
        }
    }
}

@Composable
fun labelLinesForAction(action: Action): List<String> {
    val i18N = LocalI18N.current
    return when (action) {
        is Action.ShowToast -> {
            listOf(i18N["ui.action.show.toast"], action.message)
        }
        is Action.ShellCommand -> {
            listOf(i18N["ui.action.shell"], action.command)
        }
        is Action.Delay -> {
            listOf(i18N["ui.action.delay"], action.time.toString())
        }
        Action.TakeScreenshot -> {
            listOf(i18N["ui.action.take.screenshot"])
        }
        is Action.MVEL -> {
            listOf(i18N["ui.title.mvel"], action.expression)
        }
        Action.HideOverlay -> {
            listOf(i18N["ui.action.hide.overlay"])
        }
        is Action.ShowOverlay -> {
            mutableListOf(i18N["ui.action.show.overlay"]).apply {
                add("${action.settings.size} buttons")
            }
        }
        is Action.LaunchApp -> listOf(
            i18N["ui.action.launch.app"],
            "${action.appLabel}$PREFIX_APP_ICON${action.appPkg.pkgName}"
        )

        Action.DisableBT -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.bt"])])
        Action.DisableDND -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.dnd"])])
        Action.DisableDarkMode -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.dark.mode"])])
        Action.DisableData -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.data"])])
        Action.DisableFlashLight -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.flashlight"])])
        Action.DisableHotSpot -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.hotspot"])])
        Action.DisableNFC -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.nfc"])])
        Action.DisableWifi -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.wifi"])])

        Action.EnableBT -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.bt"])])
        Action.EnableDND -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.dnd"])])
        Action.EnableDarkMode -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.dark.mode"])])
        Action.EnableData -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.data"])])
        Action.EnableFlashLight -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.flashlight"])])
        Action.EnableHotSpot -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.hotspot"])])
        Action.EnableNFC -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.nfc"])])
        Action.EnableWifi -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.wifi"])])


        is Action.StopApp -> mutableListOf(i18N["ui.action.stop.app"]).labelLinesFromAppsAndPkgSets(
            action.apps,
            action.pkgSets
        )
        is Action.StartAppProcess -> mutableListOf(i18N["ui.action.start.app.process"]).labelLinesFromAppsAndPkgSets(
            action.apps,
            action.pkgSets
        )
        is Action.EnableApp -> mutableListOf(i18N["ui.action.enable.app"]).labelLinesFromAppsAndPkgSets(
            action.apps,
            action.pkgSets
        )
        is Action.DisableApp -> mutableListOf(i18N["ui.action.disable.app"]).labelLinesFromAppsAndPkgSets(
            action.apps,
            action.pkgSets
        )
        is Action.SuspendApp -> mutableListOf(i18N["ui.action.suspend.app"]).labelLinesFromAppsAndPkgSets(
            action.apps,
            action.pkgSets
        )
        is Action.UnsuspendApp -> mutableListOf(i18N["ui.action.unsuspend.app"]).labelLinesFromAppsAndPkgSets(
            action.apps,
            action.pkgSets
        )
    }
}

fun imageVectorForAction(action: KClass<out Action>): ImageVector {
    return when (action) {
        Action.ShowToast::class -> {
            Icons.Outlined.Message
        }
        Action.ShellCommand::class -> {
            Icons.Outlined.Tag
        }
        Action.Delay::class -> {
            Icons.Outlined.Timer
        }
        Action.TakeScreenshot::class -> {
            Icons.Outlined.Devices
        }
        Action.MVEL::class -> {
            Icons.Outlined.Code
        }
        Action.HideOverlay::class -> {
            Icons.Outlined.Window
        }
        Action.ShowOverlay::class -> {
            Icons.Outlined.Window
        }
        Action.LaunchApp::class -> {
            Icons.Outlined.OpenInNew
        }

        Action.DisableBT::class -> {
            Icons.Outlined.BluetoothDisabled
        }
        Action.DisableDND::class -> {
            Icons.Outlined.DoDisturbOff
        }
        Action.DisableDarkMode::class -> {
            Icons.Outlined.DarkMode
        }
        Action.DisableData::class -> {
            Icons.Outlined.SignalCellular0Bar
        }
        Action.DisableFlashLight::class -> {
            Icons.Outlined.FlashlightOff
        }
        Action.DisableHotSpot::class -> {
            Icons.Outlined.WifiTetheringOff
        }
        Action.DisableNFC::class -> {
            Icons.Outlined.Nfc
        }
        Action.DisableWifi::class -> {
            Icons.Outlined.WifiOff
        }

        Action.EnableBT::class -> {
            Icons.Outlined.Bluetooth
        }
        Action.EnableDND::class -> {
            Icons.Outlined.DoNotDisturb
        }
        Action.EnableDarkMode::class -> {
            Icons.Outlined.DarkMode
        }
        Action.EnableData::class -> {
            Icons.Outlined.SignalCellular4Bar
        }
        Action.EnableFlashLight::class -> {
            Icons.Outlined.FlashlightOn
        }
        Action.EnableHotSpot::class -> {
            Icons.Outlined.WifiTethering
        }
        Action.EnableNFC::class -> {
            Icons.Outlined.Nfc
        }
        Action.EnableWifi::class -> {
            Icons.Outlined.Wifi
        }

        Action.StopApp::class -> {
            Icons.Outlined.StopCircle
        }
        Action.StartAppProcess::class -> {
            Icons.Outlined.PlayArrow
        }
        Action.EnableApp::class -> {
            Icons.Outlined.ToggleOn
        }
        Action.DisableApp::class -> {
            Icons.Outlined.AcUnit
        }
        Action.SuspendApp::class -> {
            Icons.Outlined.AdminPanelSettings
        }
        Action.UnsuspendApp::class -> {
            Icons.Outlined.VerifiedUser
        }


        else -> {
            Icons.Outlined.Settings
        }
    }
}