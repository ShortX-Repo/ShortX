package tornaco.apps.shortx.ui.addrule.action.mapper

import android.content.ComponentName
import android.media.AudioManager
import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.proto.action.BreakActionExecuteScope
import tornaco.apps.shortx.core.proto.action.ContextMenuAction
import tornaco.apps.shortx.core.proto.action.MapApp
import tornaco.apps.shortx.core.proto.action.MediaPlaybackAction
import tornaco.apps.shortx.core.proto.action.NavType
import tornaco.apps.shortx.core.proto.action.RingerMode
import tornaco.apps.shortx.core.proto.action.ScrollViewToLocation
import tornaco.apps.shortx.core.proto.common.ActionOnError
import tornaco.apps.shortx.core.proto.common.AudioSource
import tornaco.apps.shortx.core.proto.common.OnOffToggle
import tornaco.apps.shortx.core.proto.common.ScreenRotateDegree
import tornaco.apps.shortx.core.proto.common.VolumeDirection
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.toType
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.PkgUtils
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.condition.mapper.labelForConditionSelector
import tornaco.apps.shortx.ui.addrule.fact.mapper.getLabelForKeyCode
import tornaco.apps.shortx.ui.addrule.labelLinesFromAppsAndPkgSets
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.PREFIX_APP_ICON
import tornaco.apps.shortx.ui.base.PREFIX_MD_ICON
import java.net.URL
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

        Action.ExecuteJS::class -> {
            i18N["ui.title.js"] to null
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

        Action.LaunchAppByPkg::class -> {
            i18N["ui.action.launch.app.by.pkg"] to null
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

        Action.StartActivityIntentUri::class -> {
            i18N["ui.action.start.activity.intent.uri"] to null
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
        Action.StartAppProcessByPkg::class -> i18N["ui.action.start.app.process.by.pkg"] to null
        Action.SetAppInactiveByPkg::class -> i18N["ui.action.set.app.inactive.by.pkg"] to null
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
        Action.RemoveNotificationForPackage::class -> i18N["ui.action.remove.app.notification"] to null
        Action.RemoveNotificationForPackageByPkg::class -> i18N["ui.action.remove.app.notification.by.pkg"] to null
        Action.WhileLoop::class -> i18N["ui.action.while.loop"] to null
        Action.WriteGV::class -> i18N["ui.action.write.global.var"] to null
        Action.CreateGV::class -> i18N["ui.action.create.global.var"] to null
        Action.DeleteGV::class -> i18N["ui.action.delete.global.var"] to null
        Action.CreateLocalVar::class -> i18N["ui.action.create.local.var"] to null
        Action.WriteLocalVar::class -> i18N["ui.action.write.local.var"] to null
        Action.MapNav::class -> i18N["ui.action.map.nav"] to null
        Action.MapQueryBus::class -> i18N["ui.action.map.nav.bus.query"] to null
        Action.PostNotification::class -> i18N["ui.action.post.notification"] to null
        Action.RemoveNotification::class -> i18N["ui.action.remove.notification"] to null
        Action.ClickNotification::class -> i18N["ui.action.click.notification"] to null
        Action.ClickNotificationActionButton::class -> i18N["ui.action.click.notification.button"] to null
        Action.FromDA::class -> i18N["ui.action.from.da"] to null
        Action.FindAndClickViewByText::class -> i18N["ui.action.find.click.view.by.text"] to null
        Action.FindAndClickViewById::class -> i18N["ui.action.find.click.view.by.id"] to null
        Action.FindAndClickMatchedView::class -> i18N["ui.action.find.click.view.matched"] to i18N["ui.action.find.click.view.matched.description"]
        Action.InputTap::class -> i18N["ui.action.input.tap"] to null
        Action.InputSwipe::class -> i18N["ui.action.input.swipe"] to null
        Action.WaitForIdle::class -> i18N["ui.action.wait.for.idle"] to i18N["ui.action.wait.for.idle.description"]
        Action.IfThenElse::class -> i18N["ui.action.if.then.else"] to null
        Action.CreatePkgSet::class -> i18N["ui.pkg.set.add.title"] to null
        Action.SetRingerMode::class -> i18N["ui.action.set.ringer.mode"] to null
        Action.SetBrightness::class -> i18N["ui.action.set.brightness"] to null
        Action.StopService::class -> i18N["ui.action.stop.service"] to null
        Action.StartService::class -> i18N["ui.action.start.service"] to null
        Action.RemoveTasks::class -> i18N["ui.action.remove.tasks"] to null
        Action.RemoveTasksByPkg::class -> i18N["ui.action.remove.tasks.by.pkg"] to null
        Action.KillProcessByName::class -> i18N["ui.action.kill.process.by.name"] to null
        Action.HttpRequest::class -> i18N["ui.action.http.request"] to null
        Action.InjectKeyCode::class -> i18N["ui.action.inject.keycode"] to null
        Action.InjectCombineKeyCode::class -> i18N["ui.action.inject.combine.keycode"] to null
        Action.StopCurrentApp::class -> i18N["ui.action.stop.current.foreground.app"] to null
        Action.AdjustVolume::class -> i18N["ui.action.adjust.volume"] to null
        Action.SetVolume::class -> i18N["ui.action.set.volume"] to null
        Action.Vibrate::class -> i18N["ui.action.vibrate"] to null
        Action.AudioRecording::class -> i18N["ui.action.audio.recording"] to null
        Action.StopAudioRecording::class -> i18N["ui.action.audio.recording.stop"] to null
        Action.SwitchMobileDataSlot::class -> i18N["ui.action.switch.mobile.data.slot"] to null
        Action.StartGestureRecording::class -> i18N["ui.action.gesture.record.start"] to null
        Action.StopGestureRecording::class -> i18N["ui.action.gesture.record.stop"] to null
        Action.ToggleGestureRecording::class -> i18N["ui.action.gesture.record.toggle"] to null
        Action.InjectGestureRecording::class -> i18N["ui.action.gesture.record.inject"] to null
        Action.EnableUniversalCopy::class -> i18N["ui.action.enable.universal.copy"] to null
        Action.EnableViewIdViewer::class -> i18N["ui.action.enable.view.id.viewer"] to null
        Action.TTS::class -> i18N["ui.action.tts"] to null
        Action.SetWallpaper::class -> i18N["ui.action.set.wallpaper"] to null
        Action.SetRuleEnabled::class -> i18N["ui.action.set.rule.enabled"] to null
        Action.ClickTile::class -> i18N["ui.action.click.tile"] to null
        Action.MediaPlayback::class -> i18N["ui.action.media.play.back"] to null
        Action.BiometricVerify::class -> i18N["ui.action.biometric.verify"] to null
        Action.AppShortcut::class -> i18N["ui.action.shortcut"] to null
        Action.GetTextFromScreenNode::class -> i18N["ui.action.get.text.from.node"] to null
        Action.SetAPMModeEnabled::class -> i18N["ui.action.set.apm.mode"] to null
        Action.SetScreenTimeout::class -> i18N["ui.action.set.screen.timeout"] to null
        Action.SetScreenRotate::class -> i18N["ui.action.set.screen.rotate"] to null
        Action.ScrollViewTo::class -> i18N["ui.action.scroll.view.to"] to null
        Action.WaitUtilConditionMatch::class -> i18N["ui.action.wait.util.condition.match"] to null
        Action.PerformContextMenuAction::class -> i18N["ui.action.perform.context.menu.action"] to null
        Action.ShowDrawBoard::class -> i18N["ui.action.show.draw.board"] to null
        Action.ConnectWifi::class -> i18N["ui.action.connect.wifi"] to null
        Action.DisconnectCurrentWifi::class -> i18N["ui.action.disconnect.wifi"] to null
        Action.StayAwake::class -> i18N["ui.action.stay.awake"] to null
        Action.Toggle5G::class -> i18N["5G"] to null
        Action.ForEachPkgSet::class -> i18N["ui.action.for.each.pkg.set"] to null
        Action.LockDeviceNow::class -> i18N["ui.action.lock.screen"] to null
        Action.SetMasterSync::class -> i18N["ui.action.sync"] to null
        Action.ShowClipboardView::class -> i18N["ui.action.show.clip.board"] to null
        Action.PlayRingtone::class -> i18N["ui.action.ringtone"] to null
        Action.DownloadFile::class -> i18N["ui.action.download.file"] to null
        Action.SendSMS::class -> i18N["ui.action.send.sms"] to null
        Action.StopAllActions::class -> i18N["ui.action.stop.all.actions"] to null
        Action.SwitchCase::class -> i18N.get(
            key = "ui.action.switch.case",
            fallback = "Switch case"
        ) to null

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
            listOfNotNull(
                i18N["ui.action.shell"],
                action.singleShot.select(i18N["ui.action.shell.single.shot"], null),
                action.command
            )
        }

        is Action.InputText -> {
            listOf(i18N["ui.action.input.text"], action.text)
        }

        is Action.Delay -> {
            listOf(i18N["ui.action.delay"], action.time)
        }

        is Action.TakeScreenshot -> {
            listOf(i18N["ui.action.take.screenshot"])
        }

        is Action.MVEL -> {
            listOf(i18N["ui.title.mvel"], action.expression)
        }

        is Action.ExecuteJS -> {
            listOf(i18N["ui.title.js"], action.expression)
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

        is Action.LaunchAppByPkg -> listOf(
            i18N["ui.action.launch.app.by.pkg"],
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

        is Action.StartAppProcessByPkg -> listOf(i18N["ui.action.start.app.process.by.pkg"])

        is Action.SetAppInactiveByPkg -> listOf(i18N["ui.action.set.app.inactive.by.pkg"])

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
            listOf(i18N["ui.action.brk"], i18N[action.scope.labelKey()])
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

        is Action.StartActivityIntentUri -> {
            listOf(
                i18N["ui.action.start.activity.intent.uri"],
                action.intentUri.take(30)
            )
        }

        is Action.ExpandNotification -> {
            listOf(i18N["ui.action.expand.notification"])
        }

        is Action.RemoveNotificationForPackage -> {
            listOf(i18N["ui.action.remove.app.notification"])
        }

        is Action.RemoveNotificationForPackageByPkg -> {
            listOf(i18N["ui.action.remove.app.notification.by.pkg"])
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

        is Action.MapQueryBus -> listOf(
            i18N["ui.action.map.nav.bus.query"],
            action.bus
        )

        is Action.PostNotification -> {
            listOf(
                i18N["ui.action.post.notification"],
                action.tag,
                action.title,
                action.message
            )
        }

        is Action.RemoveNotification -> {
            listOf(i18N["ui.action.remove.notification"])
        }

        is Action.ClickNotification -> {
            listOf(i18N["ui.action.click.notification"])
        }

        is Action.ClickNotificationActionButton -> {
            listOf(i18N["ui.action.click.notification.button"], action.button.label)
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

        is Action.FindAndClickViewById -> {
            listOf(
                i18N["ui.action.find.click.view.by.id"],
                action.viewId
            )
        }

        is Action.FindAndClickMatchedView -> {
            listOf(i18N["ui.action.find.click.view.matched"])
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

            add(i18N["ui.action.if"] + "($condition)")
            add(i18N["ui.action.then"] + "{ $ifAction }")
            add(i18N["ui.action.else"] + "{ $elseAction }")
        }

        is Action.WaitUtilConditionMatch -> listOf(
            i18N["ui.action.wait.util.condition.match"],
            action.conditions.joinToString {
                labelForConditionSelector(i18N, it::class).joinToString()
            }
        )

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

        is Action.StartService -> {
            val name: ComponentName? = if (action.pkgName != null && action.className != null) {
                ComponentName(action.pkgName, action.className)
            } else {
                null
            }
            listOfNotNull(
                i18N["ui.action.start.service"],
                name?.let { "${it.flattenToShortString()}$PREFIX_APP_ICON${name.packageName}" }
            )
        }

        is Action.RemoveTasks -> {
            mutableListOf(i18N["ui.action.remove.tasks"]).labelLinesFromAppsAndPkgSets(
                action.apps,
                action.pkgSets
            )
        }

        is Action.RemoveTasksByPkg -> {
            listOf(i18N["ui.action.remove.tasks.by.pkg"])
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
                runCatching { URL(action.url).host }.getOrElse { action.url }
            )
        }

        is Action.InjectKeyCode -> {
            listOf(
                i18N["ui.action.inject.keycode"],
                getLabelForKeyCode(i18N, action.code)
            )
        }

        is Action.InjectCombineKeyCode -> {
            listOf(
                i18N["ui.action.inject.combine.keycode"],
                action.codes.joinToString {
                    getLabelForKeyCode(i18N, it)
                }
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

        is Action.SetVolume -> {
            listOf(
                i18N["ui.action.set.volume"],
                i18N[action.type.streamTypeLabelKey()] + " " + action.index
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

        is Action.StopAudioRecording -> {
            listOf(i18N["ui.action.audio.recording.stop"])
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

        is Action.EnableViewIdViewer -> listOf(
            i18N["ui.action.enable.view.id.viewer"]
        )

        is Action.TTS -> listOf(
            i18N["ui.action.tts"],
            action.text
        )

        is Action.SetWallpaper -> listOf(
            i18N["ui.action.set.wallpaper"],
            action.url
        )

        is Action.SetRuleEnabled -> listOf(
            i18N["ui.action.set.rule.enabled"],
            action.ruleLabel
        )

        is Action.ClickTile -> listOf(
            i18N["ui.action.click.tile"],
            action.tile.label
        )

        is Action.MediaPlayback -> listOf(
            i18N["ui.action.media.play.back"],
            i18N[action.action.labelKey()]
        )

        is Action.BiometricVerify -> listOf(
            i18N["ui.action.biometric.verify"]
        )

        is Action.AppShortcut -> listOf(
            i18N["ui.action.shortcut"],
            "${action.label}$PREFIX_APP_ICON${PkgUtils.packageNameOf(action.intent)}"
        )

        is Action.GetTextFromScreenNode -> listOf(
            i18N["ui.action.get.text.from.node"],
            action.nodeId.fallbackOnEmpty(i18N["ui.action.get.text.from.node.get.all"])
        )

        is Action.SetAPMModeEnabled -> listOf(
            i18N["ui.action.set.apm.mode"],
            action.isEnableAPM.select(
                i18N["ui.common.on"],
                i18N["ui.common.off"],
            )
        )

        is Action.SetScreenTimeout -> listOf(
            i18N["ui.action.set.screen.timeout"],
            "${action.timeout}"
        )

        is Action.SetScreenRotate -> listOf(
            i18N["ui.action.set.screen.rotate"],
            i18N[action.degree.actionLabelKey()]
        )

        is Action.ScrollViewTo -> listOf(
            i18N["ui.action.scroll.view.to"],
            i18N[action.location.labelKey()],
        )

        is Action.PerformContextMenuAction -> listOf(
            i18N["ui.action.perform.context.menu.action"],
            i18N[action.action.toAndroidContextMenuActionLabelKey()],
        )

        is Action.ShowDrawBoard -> listOf(
            i18N["ui.action.show.draw.board"],
        )

        is Action.ConnectWifi -> listOf(
            i18N["ui.action.connect.wifi"],
            action.ssid
        )

        is Action.DisconnectCurrentWifi -> listOf(
            i18N["ui.action.disconnect.wifi"],
        )

        is Action.StayAwake -> listOf(
            i18N["ui.action.stay.awake"],
            i18N[action.stay.labelKey()]
        )

        is Action.Toggle5G -> listOf(
            i18N["5G"],
            i18N[action.onOff.labelKey()]
        )

        is Action.ForEachPkgSet -> listOf(
            i18N["ui.action.for.each.pkg.set"],
            action.pkgSet.label.fallbackOnEmpty("N/A")
        )

        is Action.LockDeviceNow -> listOf(
            i18N["ui.action.lock.screen"],
        )

        is Action.SetMasterSync -> listOf(
            i18N["ui.action.sync"],
        )

        is Action.ShowClipboardView -> listOf(
            i18N["ui.action.show.clip.board"],
        )

        is Action.PlayRingtone -> listOf(
            i18N["ui.action.ringtone"],
            action.ringtone.title
        )

        is Action.DownloadFile -> listOf(
            i18N["ui.action.download.file"],
            action.subject.title
        )

        is Action.SwitchCase -> listOf(
            i18N.get(
                key = "ui.action.switch.case",
                fallback = "Switch case"
            ),
            "${action.cases.size} cases"
        )

        is Action.SendSMS -> listOf(
            i18N["ui.action.send.sms"],
            (action.slotId >= 0).select(
                i18N["ui.common.mobile.data.enabled.slot", mapOf(
                    "slotId" to action.slotId.toString(),
                    "label" to (shortXManager.getSlotLabel(action.slotId) ?: "")
                )],
                "N/A"
            ),
            action.to,
            action.message
        )

        is Action.StopAllActions -> listOf(
            i18N["ui.action.stop.all.actions"]
        )
    }
}

fun ScrollViewToLocation.labelKey() =
    when (this) {
        ScrollViewToLocation.Top -> "ui.action.scroll.view.to.top"
        ScrollViewToLocation.Bottom -> "ui.action.scroll.view.to.bottom"
        ScrollViewToLocation.UNRECOGNIZED -> "N/A"
        ScrollViewToLocation.Forward -> "ui.action.scroll.view.to.fw"
        ScrollViewToLocation.Backward -> "ui.action.scroll.view.to.bw"
        ScrollViewToLocation.TopForce -> "ui.action.scroll.view.to.top.force"
        ScrollViewToLocation.BottomForce -> "ui.action.scroll.view.to.bottom.force"
    }

fun ScrollViewToLocation.summaryKey() = "${this.labelKey()}.summary"


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

fun OnOffToggle.labelKey() = when (this) {
    OnOffToggle.OnOffToggle_On -> "ui.common.on"
    OnOffToggle.OnOffToggle_Off -> "ui.common.off"
    OnOffToggle.OnOffToggle_Toggle -> "ui.common.toggle"
    OnOffToggle.UNRECOGNIZED -> "UNRECOGNIZED"
}

fun NavType.labelKey() = when (this) {
    NavType.NavType_Car -> "ui.action.map.nav.type.car"
    NavType.NavType_Ride -> "ui.action.map.nav.type.ride"
    NavType.UNRECOGNIZED -> "UNRECOGNIZED"
}

fun BreakActionExecuteScope.labelKey() = when (this) {
    BreakActionExecuteScope.BreakActionExecuteScope_Current -> "ui.action.brk.scope.current"
    BreakActionExecuteScope.BreakActionExecuteScope_Parent -> "ui.action.brk.scope.parent"
    BreakActionExecuteScope.BreakActionExecuteScope_Root -> "ui.action.brk.scope.root"
    BreakActionExecuteScope.UNRECOGNIZED -> "UNRECOGNIZED"
}

fun MediaPlaybackAction.labelKey() = when (this) {
    MediaPlaybackAction.MediaPlaybackAction_Play -> "ui.action.media.play.back.play"
    MediaPlaybackAction.MediaPlaybackAction_Pause -> "ui.action.media.play.back.pause"
    MediaPlaybackAction.MediaPlaybackAction_SkipToNext -> "ui.action.media.play.back.next"
    MediaPlaybackAction.MediaPlaybackAction_SkipToPrevious -> "ui.action.media.play.back.previous"
    MediaPlaybackAction.MediaPlaybackAction_FastForward -> "ui.action.media.play.back.ff"
    MediaPlaybackAction.MediaPlaybackAction_Rewind -> "ui.action.media.play.back.rewind"
    MediaPlaybackAction.MediaPlaybackAction_Stop -> "ui.action.media.play.back.stop"
    MediaPlaybackAction.UNRECOGNIZED -> "UNRECOGNIZED"
}

fun Int.streamTypeLabelKey(): String {
    return when (this) {
        AudioManager.STREAM_VOICE_CALL -> "ui.action.audio.stream.voice.call"
        AudioManager.STREAM_SYSTEM -> "ui.action.audio.stream.system"
        AudioManager.STREAM_RING -> "ui.action.audio.stream.ring"
        AudioManager.STREAM_MUSIC -> "ui.action.audio.stream.music"
        AudioManager.STREAM_ALARM -> "ui.action.audio.stream.alarm"
        AudioManager.STREAM_NOTIFICATION -> "ui.action.audio.stream.notification"
        else -> "N/A"
    }
}

fun ScreenRotateDegree.actionLabelKey(): String {
    return "ui.action.screen.rotate.degree.${name.lowercase()}"
}

fun ContextMenuAction.toAndroidContextMenuActionLabelKey(): String {
    return "ui.action.perform.context.menu.action.${this.name.lowercase()}"
}