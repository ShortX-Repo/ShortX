package tornaco.apps.shortx.ui.addrule.action.mapper

import android.content.ComponentName
import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.proto.action.MapApp
import tornaco.apps.shortx.core.proto.action.RingerMode
import tornaco.apps.shortx.core.proto.common.ActionOnError
import tornaco.apps.shortx.core.proto.common.AudioSource
import tornaco.apps.shortx.core.proto.common.VolumeDirection
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.toType
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.condition.mapper.labelForConditionSelector
import tornaco.apps.shortx.ui.addrule.fact.mapper.getLabelForKeyCode
import tornaco.apps.shortx.ui.addrule.labelLinesFromAppsAndPkgSets
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

        Action.StartLastApp::class -> {
            i18N["ui.action.start.last.app"] to null
        }

        Action.WakeupScreen::class -> {
            i18N["ui.action.screen.on"] to null
        }

        Action.SleepScreen::class -> {
            i18N["ui.action.screen.off"] to null
        }

        Action.ReadClipboard::class -> {
            i18N["ui.action.read.clipboard"] to null
        }

        Action.WriteClipboard::class -> {
            i18N["ui.action.write.clipboard"] to null
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

        Action.StartActivityIntent::class -> {
            i18N["ui.action.start.activity.intent"] to null
        }

        Action.StartActivityUrlSchema::class -> {
            i18N["ui.action.start.activity.url.schema"] to null
        }

        Action.DisableBT::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.bt"])] to null
        Action.DisableDND::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.dnd"])] to null
        Action.DisableLocation::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.gps"])] to null
        Action.DisableDarkMode::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.dark.mode"])] to null
        Action.DisableData::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.data"])] to null
        Action.DisableFlashLight::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.flashlight"])] to null
        Action.DisableHotSpot::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.hotspot"])] to null
        Action.DisableNFC::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.nfc"])] to null
        Action.DisableWifi::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.wifi"])] to null
        Action.DisableAutoBrightness::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.auto.brightness"])] to null
        Action.DisableSensorsOff::class -> i18N["ui.action.disable", mapOf("s" to i18N["ui.action.sensors.off"])] to i18N["ui.action.sensors.off.description"]

        Action.EnableBT::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.bt"])] to null
        Action.EnableDND::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.dnd"])] to null
        Action.EnableLocation::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.gps"])] to null
        Action.EnableDarkMode::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.dark.mode"])] to null
        Action.EnableData::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.data"])] to null
        Action.EnableFlashLight::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.flashlight"])] to null
        Action.EnableHotSpot::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.hotspot"])] to null
        Action.EnableNFC::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.nfc"])] to null
        Action.EnableWifi::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.wifi"])] to null
        Action.EnableAutoBrightness::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.auto.brightness"])] to null
        Action.EnableSensorsOff::class -> i18N["ui.action.enable", mapOf("s" to i18N["ui.action.sensors.off"])] to i18N["ui.action.sensors.off.description"]

        Action.StopApp::class -> i18N["ui.action.stop.app"] to null
        Action.StopAppByPkg::class -> i18N["ui.action.stop.app.by.pkg"] to null
        Action.StartAppProcess::class -> i18N["ui.action.start.app.process"] to null
        Action.SetAppInactive::class -> i18N["ui.action.set.app.inactive"] to null
        Action.EnableApp::class -> i18N["ui.action.enable.app"] to null
        Action.DisableApp::class -> i18N["ui.action.disable.app"] to null
        Action.EnableAppByPkg::class -> i18N["ui.action.enable.app.by.pkg"] to null
        Action.DisableAppByPkg::class -> i18N["ui.action.disable.app.by.pkg"] to null
        Action.SuspendApp::class -> i18N["ui.action.suspend.app"] to null
        Action.UnSuspendApp::class -> i18N["ui.action.unsuspend.app"] to null
        Action.SuspendAppByPkg::class -> i18N["ui.action.suspend.app.by.pkg"] to null
        Action.UnSuspendAppByPkg::class -> i18N["ui.action.unsuspend.app.by.pkg"] to null

        Action.Brk::class -> i18N["ui.action.brk"] to null
        Action.ShowAlertDialog::class -> i18N["ui.action.show.dialog"] to null
        Action.ShowMenuDialog::class -> i18N["ui.action.show.menu.dialog"] to null
        Action.ShowTextFieldDialog::class -> i18N["ui.action.show.text.field.dialog"] to null
        Action.ExpandNotification::class -> i18N["ui.action.expand.notification"] to null
        Action.RemoveNotificationForPackage::class -> i18N["ui.action.remove.notification"] to null
        Action.WhileLoop::class -> i18N["ui.action.while.loop"] to null
        Action.WriteGV::class -> i18N["ui.action.write.global.var"] to null
        Action.CreateGV::class -> i18N["ui.action.create.global.var"] to null
        Action.DeleteGV::class -> i18N["ui.action.delete.global.var"] to null
        Action.CreateLocalVar::class -> i18N["ui.action.create.local.var"] to null
        Action.WriteLocalVar::class -> i18N["ui.action.write.local.var"] to null
        Action.MapNav::class -> i18N["ui.action.map.nav"] to null
        Action.PostNotification::class -> i18N["ui.action.post.notification"] to null
        Action.FromDA::class -> i18N["ui.action.from.da"] to null
        Action.FindAndClickViewByText::class -> i18N["ui.action.find.click.view.by.text"] to null
        Action.InputTap::class -> i18N["ui.action.input.tap"] to null
        Action.InputSwipe::class -> i18N["ui.action.input.swipe"] to null
        Action.WaitForIdle::class -> i18N["ui.action.wait.for.idle"] to i18N["ui.action.wait.for.idle.description"]
        Action.IfThenElse::class -> i18N["ui.action.if.then.else"] to null
        Action.CreatePkgSet::class -> i18N["ui.pkg.set.add.title"] to null
        Action.SetRingerMode::class -> i18N["ui.action.set.ringer.mode"] to null
        Action.SetBrightness::class -> i18N["ui.action.set.brightness"] to null
        Action.StopService::class -> i18N["ui.action.stop.service"] to null
        Action.RemoveTasks::class -> i18N["ui.action.remove.tasks"] to null
        Action.KillProcessByName::class -> i18N["ui.action.kill.process.by.name"] to null
        Action.HttpRequest::class -> i18N["ui.action.http.request"] to null
        Action.InjectKeyCode::class -> i18N["ui.action.inject.keycode"] to null
        Action.StopCurrentApp::class -> i18N["ui.action.stop.current.foreground.app"] to null
        Action.AdjustVolume::class -> i18N["ui.action.adjust.volume"] to null
        Action.Vibrate::class -> i18N["ui.action.vibrate"] to null
        Action.AudioRecording::class -> i18N["ui.action.audio.recording"] to null
        Action.SwitchMobileDataSlot::class -> i18N["ui.action.switch.mobile.data.slot"] to null
        Action.StartGestureRecording::class -> i18N["ui.action.gesture.record.start"] to null
        Action.StopGestureRecording::class -> i18N["ui.action.gesture.record.stop"] to null
        Action.ToggleGestureRecording::class -> i18N["ui.action.gesture.record.toggle"] to null
        Action.InjectGestureRecording::class -> i18N["ui.action.gesture.record.inject"] to null
        Action.EnableUniversalCopy::class -> i18N["ui.action.enable.universal.copy"] to null
        Action.TTS::class -> i18N["ui.action.tts"] to null
        Action.SetWallpaper::class -> i18N["ui.action.set.wallpaper"] to null
        Action.NoAction::class -> i18N["ui.action.no.action"] to null

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

        is Action.StartLastApp -> {
            listOf(i18N["ui.action.start.last.app"])
        }

        is Action.SleepScreen -> {
            listOf(i18N["ui.action.screen.off"])
        }

        is Action.WakeupScreen -> {
            listOf(i18N["ui.action.screen.on"])
        }

        is Action.ReadClipboard -> {
            listOf(i18N["ui.action.read.clipboard"])
        }

        is Action.WriteClipboard -> {
            listOf(i18N["ui.action.write.clipboard"], action.text)
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
            "${action.app.label}$PREFIX_APP_ICON${action.app.pkg.pkgName}"
        )

        is Action.DisableBT -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.bt"])])
        is Action.DisableDND -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.dnd"])])
        is Action.DisableLocation -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.gps"])])
        is Action.DisableDarkMode -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.dark.mode"])])
        is Action.DisableData -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.data"])])
        is Action.DisableFlashLight -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.flashlight"])])
        is Action.DisableHotSpot -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.hotspot"])])
        is Action.DisableNFC -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.nfc"])])
        is Action.DisableWifi -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.wifi"])])
        is Action.DisableAutoBrightness -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.auto.brightness"])])
        is Action.DisableSensorsOff -> listOf(i18N["ui.action.disable", mapOf("s" to i18N["ui.action.sensors.off"])])

        is Action.EnableBT -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.bt"])])
        is Action.EnableDND -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.dnd"])])
        is Action.EnableLocation -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.gps"])])
        is Action.EnableDarkMode -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.dark.mode"])])
        is Action.EnableData -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.data"])])
        is Action.EnableFlashLight -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.flashlight"])])
        is Action.EnableHotSpot -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.hotspot"])])
        is Action.EnableNFC -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.nfc"])])
        is Action.EnableWifi -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.wifi"])])
        is Action.EnableAutoBrightness -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.auto.brightness"])])
        is Action.EnableSensorsOff -> listOf(i18N["ui.action.enable", mapOf("s" to i18N["ui.action.sensors.off"])])


        is Action.StopApp -> mutableListOf(i18N["ui.action.stop.app"]).labelLinesFromAppsAndPkgSets(
            action.apps,
            action.pkgSets
        )

        is Action.StopAppByPkg -> listOf(i18N["ui.action.stop.app.by.pkg"])

        is Action.StartAppProcess -> mutableListOf(i18N["ui.action.start.app.process"]).labelLinesFromAppsAndPkgSets(
            action.apps,
            action.pkgSets
        )

        is Action.SetAppInactive -> mutableListOf(i18N["ui.action.set.app.inactive"]).labelLinesFromAppsAndPkgSets(
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

        is Action.EnableAppByPkg -> listOf(i18N["ui.action.enable.app.by.pkg"])

        is Action.DisableAppByPkg -> listOf(i18N["ui.action.disable.app.by.pkg"])

        is Action.SuspendApp -> mutableListOf(i18N["ui.action.suspend.app"]).labelLinesFromAppsAndPkgSets(
            action.apps,
            action.pkgSets
        )

        is Action.UnSuspendApp -> mutableListOf(i18N["ui.action.unsuspend.app"]).labelLinesFromAppsAndPkgSets(
            action.apps,
            action.pkgSets
        )

        is Action.SuspendAppByPkg -> listOf(i18N["ui.action.suspend.app.by.pkg"])

        is Action.UnSuspendAppByPkg -> listOf(i18N["ui.action.unsuspend.app.by.pkg"])

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

        is Action.ShowTextFieldDialog -> {
            listOf(
                i18N["ui.action.show.text.field.dialog"],
                action.title,
                action.message
            )
        }

        is Action.ShowMenuDialog -> {
            listOf(
                i18N["ui.action.show.menu.dialog"],
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

        is Action.StartActivityIntent -> {
            val name: ComponentName? = if (action.pkgName != null && action.className != null) {
                ComponentName(action.pkgName, action.className)
            } else {
                null
            }
            listOfNotNull(
                i18N["ui.action.start.activity.intent"],
                action.action,
                name?.let { "${it.flattenToShortString()}$PREFIX_APP_ICON${name.packageName}" }
            )
        }

        is Action.StartActivityUrlSchema -> {
            listOf(
                i18N["ui.action.start.activity.url.schema"],
                action.urlSchema
            )
        }

        is Action.ExpandNotification -> {
            listOf(i18N["ui.action.expand.notification"])
        }

        is Action.RemoveNotificationForPackage -> {
            listOf(i18N["ui.action.remove.notification"])
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

        is Action.CreateGV -> {
            listOfNotNull(
                i18N["ui.action.create.global.var"],
                action.globalVar.name,
                action.globalVar.type.toType()?.name
            )
        }

        is Action.DeleteGV -> {
            listOfNotNull(
                i18N["ui.action.delete.global.var"],
                action.varName,
            )
        }

        is Action.CreateLocalVar -> {
            listOfNotNull(
                i18N["ui.action.create.local.var"],
                action.localVar.name,
                action.localVar.type.toType()?.name
            )
        }

        is Action.WriteLocalVar -> {
            listOfNotNull(
                i18N["ui.action.write.local.var"],
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
                    add(i18N["ui.action.from.da.not.found"] + "${PREFIX_MD_ICON}${Remix.System.alert_fill}")
                }
            }
        }

        is Action.FindAndClickViewByText -> {
            listOf(
                i18N["ui.action.find.click.view.by.text"],
                action.text
            )
        }

        is Action.InputTap -> {
            listOf(
                i18N["ui.action.input.tap"],
                "[${action.x}, ${action.y}]"
            )
        }

        is Action.InputSwipe -> {
            listOf(
                i18N["ui.action.input.swipe"],
                "[${action.startX}, ${action.startY}] -> [${action.endX}, ${action.endY}]"
            )
        }

        is Action.WaitForIdle -> listOf(
            i18N["ui.action.wait.for.idle"],
            i18N["ui.action.wait.for.idle.description"]
        )

        is Action.IfThenElse -> mutableListOf(
            i18N["ui.action.if.then.else"]
        ).apply {
            val condition = action.ifConditions.joinToString {
                labelForConditionSelector(i18N, it::class).joinToString()
            }

            val ifAction = action.ifActions.joinToString {
                labelAndDescriptionForActionSelector(i18N, it::class).first
            }

            val elseAction = action.elseActions.joinToString {
                labelAndDescriptionForActionSelector(i18N, it::class).first
            }

            add(i18N["ui.action.if"] + " \uD83C\uDFAF " + condition + " ")
            add(i18N["ui.action.then"] + " ➡️ " + ifAction + " ")
            add(i18N["ui.action.else"] + " ➡️ " + elseAction + " ")
        }

        is Action.NoAction -> {
            listOf(i18N["ui.action.no.action"])
        }

        is Action.CreatePkgSet -> {
            listOfNotNull(i18N["ui.pkg.set.add.title"], action.label, action.description)
        }

        is Action.SetRingerMode -> {
            listOf(i18N["ui.action.set.ringer.mode"], i18N[action.mode.labelKey()])
        }

        is Action.SetBrightness -> {
            listOf(i18N["ui.action.set.brightness"], action.value.toString())
        }

        is Action.StopService -> {
            listOf(
                i18N["ui.action.stop.service"],
                i18N["ui.action.stop.service.summary", mapOf("count" to action.services.size.toString())]
            )
        }

        is Action.RemoveTasks -> {
            mutableListOf(i18N["ui.action.remove.tasks"]).labelLinesFromAppsAndPkgSets(
                action.apps,
                action.pkgSets
            )
        }

        is Action.KillProcessByName -> {
            listOf(
                i18N["ui.action.kill.process.by.name"],
                i18N["ui.action.kill.process.by.name.summary", mapOf("count" to "${action.processList.size}")]
            )
        }

        is Action.HttpRequest -> {
            listOf(
                i18N["ui.action.http.request"],
                action.url
            )
        }

        is Action.InjectKeyCode -> {
            listOf(
                i18N["ui.action.inject.keycode"],
                getLabelForKeyCode(i18N, action.code)
            )
        }

        is Action.StopCurrentApp -> {
            listOf(
                i18N["ui.action.stop.current.foreground.app"],
            )
        }

        is Action.AdjustVolume -> {
            listOf(
                i18N["ui.action.adjust.volume"],
                i18N[action.direction.labelKey()]
            )
        }

        is Action.Vibrate -> {
            listOf(
                i18N["ui.action.vibrate"],
            )
        }

        is Action.AudioRecording -> {
            listOf(
                i18N["ui.action.audio.recording"],
                i18N[action.src.labelKey()]
            )
        }

        is Action.SwitchMobileDataSlot -> {
            listOf(
                i18N["ui.action.switch.mobile.data.slot"],
                (action.slotId >= 0).select(
                    i18N["ui.common.mobile.data.enabled.slot", mapOf(
                        "slotId" to action.slotId.toString(),
                        "label" to (shortXManager.getSlotLabel(action.slotId) ?: "")
                    )],
                    "N/A"
                )
            )
        }

        is Action.StartGestureRecording -> listOf(i18N["ui.action.gesture.record.start"])
        is Action.StopGestureRecording -> listOf(i18N["ui.action.gesture.record.stop"])
        is Action.ToggleGestureRecording -> listOf(i18N["ui.action.gesture.record.toggle"])
        is Action.InjectGestureRecording -> listOf(
            i18N["ui.action.gesture.record.inject"],
            i18N["ui.action.gesture.record.inject.speed"] + " ${action.speed}x"
        )

        is Action.EnableUniversalCopy -> listOf(
            i18N["ui.action.enable.universal.copy"]
        )

        is Action.TTS -> listOf(
            i18N["ui.action.tts"],
            action.text
        )

        is Action.SetWallpaper -> listOf(
            i18N["ui.action.set.wallpaper"],
            action.url
        )
    }
}

fun RingerMode.labelKey() = when (this) {
    RingerMode.silent -> "ui.action.set.ringer.mode.silent"
    RingerMode.vibrate -> "ui.action.set.ringer.mode.vibrate"
    RingerMode.normal -> "ui.action.set.ringer.mode.normal"
    RingerMode.UNRECOGNIZED -> "N/A"
}

fun VolumeDirection.labelKey() = when (this) {
    VolumeDirection.ADJUST_SAME -> {
        "ui.action.adjust.volume.same"
    }

    VolumeDirection.ADJUST_RAISE -> {
        "ui.action.adjust.volume.raise"
    }

    VolumeDirection.ADJUST_LOWER -> {
        "ui.action.adjust.volume.lower"
    }

    VolumeDirection.ADJUST_MUTE -> {
        "ui.action.adjust.volume.mute"
    }

    VolumeDirection.ADJUST_UNMUTE -> {
        "ui.action.adjust.volume.unmute"
    }

    VolumeDirection.ADJUST_TOGGLE_MUTE -> {
        "ui.action.adjust.volume.toggle.mute"
    }

    VolumeDirection.UNRECOGNIZED -> {
        "N/A"
    }
}

fun AudioSource.labelKey() = when (this) {
    AudioSource.Mic -> "ui.action.audio.src.mic"
    AudioSource.Internal -> "ui.action.audio.src.internal"
    AudioSource.MicAndInternal -> "ui.action.audio.src.mic.internal"
    AudioSource.UNRECOGNIZED -> "UNRECOGNIZED"
}

fun ActionOnError.labelKey() = when (this) {
    ActionOnError.Break -> {
        "ui.actions.on.error.break"
    }

    ActionOnError.Continue -> {
        "ui.actions.on.error.continue"
    }

    ActionOnError.UNRECOGNIZED -> {
        ""
    }
}