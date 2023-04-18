package tornaco.apps.shortx.ui.addrule.action

import android.content.ComponentName
import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.proto.action.MapApp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.labelLinesFromAppsAndPkgSets
import tornaco.apps.shortx.ui.base.I18N
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.PREFIX_APP_ICON
import tornaco.apps.shortx.ui.base.PREFIX_MD_ICON
import kotlin.reflect.KClass

fun labelAndDescriptionForActionSelector(
    i18N: I18N,
    action: KClass<out Action>
): Pair<String, String?> {
    return when (action) {
        Action.ShowToast::class -> {
            i18N["ui.action.show.toast"] to null
        }
        Action.ShowDanmu::class -> {
            i18N["ui.action.show.danmu"] to null
        }
        Action.ShellCommand::class -> {
            i18N["ui.action.shell"] to null
        }
        Action.InputText::class -> {
            i18N["ui.action.input.text"] to null
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
        Action.HideAllOverlay::class -> {
            i18N["ui.action.hide.all.overlay"] to null
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
        Action.StartActivity::class -> {
            i18N["ui.action.start.activity"] to null
        }

        Action.DisableBT::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.bt"])] to null
        Action.DisableDND::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.dnd"])] to null
        Action.DisableGPS::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.gps"])] to null
        Action.DisableDarkMode::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.dark.mode"])] to null
        Action.DisableData::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.data"])] to null
        Action.DisableFlashLight::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.flashlight"])] to null
        Action.DisableHotSpot::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.hotspot"])] to null
        Action.DisableNFC::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.nfc"])] to null
        Action.DisableWifi::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.wifi"])] to null

        Action.EnableBT::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.bt"])] to null
        Action.EnableDND::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.dnd"])] to null
        Action.EnableGPS::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.gps"])] to null
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
        Action.UnSuspendApp::class -> i18N["ui.action.unsuspend.app"] to null

        Action.Brk::class -> i18N["ui.action.brk"] to null
        Action.ShowAlertDialog::class -> i18N["ui.action.show.dialog"] to null
        Action.ExpandNotification::class -> i18N["ui.action.expand.notification"] to null
        Action.WhileLoop::class -> i18N["ui.action.while.loop"] to null
        Action.WriteGV::class -> i18N["ui.action.write.global.var"] to null
        Action.MapNav::class -> i18N["ui.action.map.nav"] to null
        Action.PostNotification::class -> i18N["ui.action.post.notification"] to null
        Action.FromDA::class -> i18N["ui.action.from.da"] to null
        Action.FindAndClickViewByText::class -> i18N["ui.action.find.click.view.by.text"] to null

        else -> {
            "N/A" to null
        }
    }
}

@Composable
fun labelLinesForAction(action: Action): List<String> {
    val i18N = LocalI18N.current
    return labelLinesForAction(i18N, action)
}

fun labelLinesForAction(i18N: I18N, action: Action): List<String> {
    return when (action) {
        is Action.ShowToast -> {
            listOf(i18N["ui.action.show.toast"], action.message)
        }
        is Action.ShowDanmu -> {
            listOf(i18N["ui.action.show.danmu"], action.text)
        }
        is Action.ShellCommand -> {
            listOf(i18N["ui.action.shell"], action.command)
        }
        is Action.InputText -> {
            listOf(i18N["ui.action.input.text"], action.text)
        }
        is Action.Delay -> {
            listOf(i18N["ui.action.delay"], action.time.toString())
        }
        is Action.TakeScreenshot -> {
            listOf(i18N["ui.action.take.screenshot"])
        }
        is Action.MVEL -> {
            listOf(i18N["ui.title.mvel"], action.expression)
        }
        is Action.HideAllOverlay -> {
            listOf(i18N["ui.action.hide.all.overlay"])
        }
        is Action.HideOverlay -> {
            listOf(
                i18N["ui.action.hide.overlay"],
                (action.tags.joinToString())
            )
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

        is Action.DisableBT -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.bt"])])
        is Action.DisableDND -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.dnd"])])
        is Action.DisableGPS -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.gps"])])
        is Action.DisableDarkMode -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.dark.mode"])])
        is Action.DisableData -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.data"])])
        is Action.DisableFlashLight -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.flashlight"])])
        is Action.DisableHotSpot -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.hotspot"])])
        is Action.DisableNFC -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.nfc"])])
        is Action.DisableWifi -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.wifi"])])

        is Action.EnableBT -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.bt"])])
        is Action.EnableDND -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.dnd"])])
        is Action.EnableGPS -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.gps"])])
        is Action.EnableDarkMode -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.dark.mode"])])
        is Action.EnableData -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.data"])])
        is Action.EnableFlashLight -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.flashlight"])])
        is Action.EnableHotSpot -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.hotspot"])])
        is Action.EnableNFC -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.nfc"])])
        is Action.EnableWifi -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.wifi"])])


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
        is Action.UnSuspendApp -> mutableListOf(i18N["ui.action.unsuspend.app"]).labelLinesFromAppsAndPkgSets(
            action.apps,
            action.pkgSets
        )
        is Action.Brk -> {
            listOf(i18N["ui.action.brk"])
        }
        is Action.ShowAlertDialog -> {
            listOf(
                i18N["ui.action.show.dialog"],
                action.title,
                action.message
            )
        }
        is Action.StartActivity -> {
            val name: ComponentName? = ComponentName.unflattenFromString(
                action.componentNameAsString
            )
            if (name == null) {
                listOf(
                    i18N["ui.action.start.activity"]
                )
            } else {
                listOf(
                    i18N["ui.action.start.activity"],
                    "${action.componentNameAsString}$PREFIX_APP_ICON${name.packageName}"
                )
            }

        }
        is Action.ExpandNotification -> {
            listOf(i18N["ui.action.expand.notification"])
        }
        is Action.RemoteMVEL -> {
            listOf(
                action.action.title,
                action.action.description
            )
        }
        is Action.WhileLoop -> {
            listOf(
                i18N["ui.action.while.loop"]
            )
        }
        is Action.WriteGV -> {
            listOf(
                i18N["ui.action.write.global.var"],
                action.varName,
                action.valueAsString
            )
        }
        is Action.MapNav -> listOf(
            i18N["ui.action.map.nav"],
            when (action.mapApp) {
                MapApp.Gaode -> {
                    i18N["ui.action.map.gaode"]
                }
                MapApp.Baidu -> {
                    i18N["ui.action.map.baidu"]
                }
                else -> {
                    "N/A"
                }
            }
        )
        is Action.PostNotification -> {
            listOf(
                i18N["ui.action.post.notification"],
                action.tag,
                action.title,
                action.message
            )
        }
        is Action.FromDA -> {
            mutableListOf(
                i18N["ui.action.from.da"],
            ).apply {
                if (action.title != null) {
                    add(action.title)
                    if (!action.description.isNullOrBlank()) {
                        add(action.description)
                    }
                } else {
                    add(i18N["ui.action.from.da.not.found"] + "${PREFIX_MD_ICON}${Remix.System.alert_line}")
                }
            }
        }
        is Action.FindAndClickViewByText -> {
            listOf(
                i18N["ui.action.find.click.view.by.text"],
                action.text
            )
        }
    }
}

fun imageVectorForAction(action: KClass<out Action>): String {
    return when (action) {
        Action.ShowToast::class -> {
            Remix.Communication.chat_2_line
        }
        Action.ShowDanmu::class -> {
            Remix.Communication.chat_1_line
        }
        Action.ShellCommand::class -> {
            Remix.Development.terminal_box_line
        }
        Action.InputText::class -> {
            Remix.Editor.text
        }
        Action.Delay::class -> {
            Remix.System.timer_line
        }
        Action.TakeScreenshot::class -> {
            Remix.Design.screenshot_line
        }
        Action.MVEL::class -> {
            Remix.Development.code_line
        }
        Action.HideAllOverlay::class -> {
            Remix.Business.window_line
        }
        Action.HideOverlay::class -> {
            Remix.Business.window_line
        }
        Action.ShowOverlay::class -> {
            Remix.Business.window_2_line
        }
        Action.LaunchApp::class -> {
            Remix.Arrows.arrow_right_up_line
        }

        Action.DisableBT::class -> {
            Remix.Device.bluetooth_line
        }
        Action.DisableDND::class -> {
            Remix.Weather.moon_line
        }
        Action.DisableGPS::class -> {
            Remix.Device.gps_line
        }
        Action.DisableDarkMode::class -> {
            Remix.Weather.sun_line
        }
        Action.DisableData::class -> {
            Remix.Arrows.arrow_up_down_line
        }
        Action.DisableFlashLight::class -> {
            Remix.Others.lightbulb_line
        }
        Action.DisableHotSpot::class -> {
            Remix.Device.hotspot_line
        }
        Action.DisableNFC::class -> {
            Remix.Device.nfc_fill
        }
        Action.DisableWifi::class -> {
            Remix.Device.wifi_off_line
        }

        Action.EnableBT::class -> {
            Remix.Device.bluetooth_connect_line
        }
        Action.EnableDND::class -> {
            Remix.Weather.moon_line
        }
        Action.EnableGPS::class -> {
            Remix.Device.gps_line
        }
        Action.EnableDarkMode::class -> {
            Remix.Weather.sun_line
        }
        Action.EnableData::class -> {
            Remix.Arrows.arrow_up_down_line
        }
        Action.EnableFlashLight::class -> {
            Remix.Others.lightbulb_line
        }
        Action.EnableHotSpot::class -> {
            Remix.Device.hotspot_line
        }
        Action.EnableNFC::class -> {
            Remix.Device.nfc_fill
        }
        Action.EnableWifi::class -> {
            Remix.Device.wifi_line
        }

        Action.StopApp::class -> {
            Remix.Media.stop_line
        }
        Action.StartAppProcess::class -> {
            Remix.Media.play_line
        }
        Action.EnableApp::class -> {
            Remix.System.toggle_line
        }
        Action.DisableApp::class -> {
            Remix.System.toggle_fill
        }
        Action.SuspendApp::class -> {
            Remix.UserAndFaces.admin_line
        }
        Action.UnSuspendApp::class -> {
            Remix.UserAndFaces.admin_line
        }
        Action.Brk::class -> {
            Remix.System.close_circle_line
        }
        Action.ShowAlertDialog::class -> {
            Remix.Communication.chat_4_line
        }
        Action.StartActivity::class -> {
            Remix.Logos.android_line
        }
        Action.ExpandNotification::class -> {
            Remix.System.notification_badge_line
        }
        Action.PostNotification::class -> {
            Remix.Media.notification_line
        }
        Action.RemoteMVEL::class -> {
            Remix.Business.cloud_line
        }
        Action.WhileLoop::class -> {
            Remix.System.loop_left_line
        }
        Action.WriteGV::class -> {
            Remix.Document.file_edit_line
        }
        Action.MapNav::class -> {
            Remix.Map.navigation_line
        }
        Action.FromDA::class -> {
            Remix.Editor.link
        }
        Action.FindAndClickViewByText::class -> {
            Remix.Business.window_2_line
        }

        else -> {
            Remix.Document.file_unknow_line
        }
    }
}