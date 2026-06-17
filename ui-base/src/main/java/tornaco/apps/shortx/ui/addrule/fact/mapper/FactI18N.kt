package tornaco.apps.shortx.ui.addrule.fact.mapper

import android.view.KeyEvent
import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.alarm.FixedSetting
import tornaco.apps.shortx.core.alarm.isEveryDay
import tornaco.apps.shortx.core.alarm.isNo
import tornaco.apps.shortx.core.alarm.toLocalTime
import tornaco.apps.shortx.core.common.sortedDays
import tornaco.apps.shortx.core.compat.AppOpsManagerCompat
import tornaco.apps.shortx.core.ops.opsLabelKey
import tornaco.apps.shortx.core.platform.PackageNames.mediaProviderPackages
import tornaco.apps.shortx.core.proto.condition.CalendarDayType
import tornaco.apps.shortx.core.proto.common.KeyGesture
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.common.ScreenOnTimeStartFrom
import tornaco.apps.shortx.core.proto.common.ScreenRotateDegree
import tornaco.apps.shortx.core.proto.common.TimeOfADay
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.proto.fact.ScreenCorner
import tornaco.apps.shortx.core.proto.fact.ProximitySensorState
import tornaco.apps.shortx.core.proto.fact.SensorValueTrend
import tornaco.apps.shortx.core.proto.fact.SunRise
import tornaco.apps.shortx.core.proto.fact.WeekDay
import tornaco.apps.shortx.core.proto.fact.WorkdayFilter
import tornaco.apps.shortx.core.proto.feature.AppFeature
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.toDisplayName
import tornaco.apps.shortx.core.settings.defaultDynamicShortcutSettings
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.toComponentName
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.addrule.condition.mapper.labelKey as calendarDayTypeLabelKey
import tornaco.apps.shortx.ui.addrule.fact.model.EdgeGestureStepModel
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.labelLinesFromAppsAndPkgSets
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.PREFIX_APP_ICON
import tornaco.apps.shortx.ui.base.PREFIX_MD_ICON
import tornaco.apps.shortx.ui.base.STYLE_COLOR_RED
import tornaco.apps.shortx.ui.base.labelKey
import java.util.Locale
import kotlin.reflect.KClass


fun labelAndDescriptionForFactSelector(i18n: I18N, fact: KClass<out Fact>): Pair<String, String?> {
    return when (fact) {
        Fact.AnyFact::class -> "Any(For dev)" to null
        Fact.AppProcessStarted::class -> i18n["ui.fact.process.started"] to null
        Fact.AppProcessRemoved::class -> i18n["ui.fact.process.removed"] to null
        Fact.ActivityStarted::class -> i18n["ui.fact.activity.started"] to i18n["ui.fact.activity.started.specific"]
        Fact.ActivityStopped::class -> i18n["ui.fact.activity.stopped"] to null
        Fact.ActivityDestroyed::class -> i18n["ui.fact.activity.destroyed"] to null
        Fact.AnyActivityStarted::class -> i18n["ui.fact.activity.started"] to i18n["ui.fact.activity.started.any"]
        Fact.AnyActivityStopped::class -> i18n["ui.fact.activity.stopped"] to i18n["ui.fact.activity.started.any"]
        Fact.AnyActivityDestroyed::class -> i18n["ui.fact.activity.destroyed"] to i18n["ui.fact.activity.started.any"]
        Fact.EdgeGesture::class -> i18n["ui.fact.edge.gesture"] to null
        Fact.CornerSwipeToCenter::class -> i18n["ui.fact.corner.swipe.to.center"] to null
        Fact.ScreenGesture::class -> i18n["ui.fact.screen.gesture"] to null
        Fact.CombineKeyEvent::class -> i18n["ui.action.inject.combine.keycode"] to null
        Fact.KeyEvent::class -> i18n["ui.fact.key.event"] to null
        Fact.AdvancedKeyEvent::class -> i18n["ui.fact.key.event.adv"] to null
        Fact.ScreenOff::class -> i18n["ui.fact.screen.off"] to null
        Fact.ScreenOn::class -> i18n["ui.fact.screen.on"] to null
        Fact.UserPresent::class -> i18n["ui.fact.screen.user.present"] to null
        Fact.UserPresentAtTheFirstTime::class -> i18n["ui.fact.screen.user.present.at.the.first.time"] to null
        Fact.SystemReady::class -> i18n["ui.fact.system.ready"] to null
        Fact.Alarm::class -> i18n["ui.fact.time.of.a.day"] to null
        Fact.RandomInPeriod::class -> i18n["ui.fact.time.of.a.day.random"] to null
        Fact.FixedInPeriod::class -> i18n["ui.fact.time.of.a.day.fixed"] to null
        Fact.AppBecomeBackground::class -> i18n["ui.fact.app.become.bg"] to null
        Fact.AppBecomeForeground::class -> i18n["ui.fact.app.become.fg"] to null

        Fact.WifiStatusChanged::class -> i18n["ui.fact.wifi.state.changed"] to null
        Fact.WifiConnectedTo::class -> i18n["ui.fact.wifi.connect.to"] to null
        Fact.WifiDisconnectedFrom::class -> i18n["ui.fact.wifi.disconnected.from"] to null
        Fact.ConnectedWifiSignalLevelChanged::class -> i18n["ui.fact.wifi.signal.changed"] to null
        Fact.NFCTagDiscover::class -> i18n["ui.fact.nfc.tag"] to null

        Fact.BTStatusChanged::class -> {
            i18n["ui.fact.bt.state.changed"] to null
        }

        Fact.BTConnectedTo::class -> {
            i18n["ui.fact.bt.connected.to"] to null
        }

        Fact.BTDisconnectedFrom::class -> {
            i18n["ui.fact.bt.disconnected.from"] to null
        }

        Fact.DNDStatusChanged::class -> {
            i18n["ui.fact.dnd.state.changed"] to null
        }

        Fact.APMStatusChanged::class -> {
            i18n["ui.condition.apm.mode"] to null
        }

        Fact.DarkModeStatusChanged::class -> {
            i18n["ui.fact.dark.mode.state.changed"] to null
        }

        Fact.FlashLightStatusChanged::class -> {
            i18n["ui.fact.flash.light.state.changed"] to null
        }

        Fact.HotSpotStatusChanged::class -> {
            i18n["ui.fact.hotspot.state.changed"] to null
        }

        Fact.LocationStatusChanged::class -> {
            i18n["ui.fact.location.state.changed"] to null
        }

        Fact.MobileDataStatusChanged::class -> {
            i18n["ui.fact.mobile.data.state.changed"] to null
        }

        Fact.NFCStatusChanged::class -> {
            i18n["ui.fact.nfc.state.changed"] to null
        }

        Fact.NotificationPosted::class -> {
            i18n["ui.fact.n.posted"] to null
        }

        Fact.NotificationUpdated::class -> {
            i18n["ui.fact.n.updated"] to null
        }

        Fact.NotificationRemoved::class -> {
            i18n["ui.fact.n.removed"] to null
        }

        Fact.TaskRemoved::class -> {
            i18n["ui.fact.task.removed"] to i18n["ui.fact.task.removed.specific"]
        }

        Fact.TaskRemovedAny::class -> {
            i18n["ui.fact.task.removed"] to i18n["ui.fact.task.removed.any"]
        }

        Fact.PkgStopRunning::class -> {
            i18n["ui.fact.pkg.stop.running"] to i18n["ui.fact.pkg.stop.running.specific"]
        }

        Fact.PkgStopRunningAny::class -> {
            i18n["ui.fact.pkg.stop.running"] to i18n["ui.fact.pkg.stop.running.any"]
        }

        Fact.PkgStartRunning::class -> {
            i18n["ui.fact.pkg.start.running"] to i18n["ui.fact.pkg.start.running.specific"]
        }

        Fact.PkgStartRunningAny::class -> {
            i18n["ui.fact.pkg.start.running"] to i18n["ui.fact.pkg.start.running.any"]
        }

        Fact.AppAdded::class -> {
            i18n["ui.fact.app.installed"] to null
        }

        Fact.AppRemoved::class -> {
            i18n["ui.fact.app.uninstalled"] to null
        }

        Fact.AppUpdated::class -> {
            i18n["ui.fact.app.updated"] to null
        }

        Fact.AudioFocusChanged::class -> {
            i18n["ui.fact.audio.focus.changed"] to null
        }

        Fact.AudioFocusGain::class -> {
            i18n["ui.fact.audio.focus.gain"] to null
        }

        Fact.AudioFocusLost::class -> {
            i18n["ui.fact.audio.focus.lost"] to null
        }

        Fact.BatteryLevelChanged::class -> {
            i18n["ui.fact.battery.level.changed"] to null
        }

        Fact.BatteryTemperatureChanged::class -> {
            i18n["ui.fact.battery.temp.changed"] to null
        }

        Fact.ChargerPlug::class -> {
            i18n["ui.fact.battery.charge.plug"] to null
        }

        Fact.ChargerUnplug::class -> {
            i18n["ui.fact.battery.charge.unplug"] to null
        }

        Fact.Broadcast::class -> {
            i18n["ui.fact.broadcast"] to null
        }

        Fact.VPNConnected::class -> {
            i18n["ui.fact.vpn.connected"] to null
        }

        Fact.VPNDisconnected::class -> {
            i18n["ui.fact.vpn.disconnected"] to null
        }

        Fact.MediaStoreInsert::class -> {
            i18n["ui.fact.media.insert"] to i18n["ui.fact.media.insert.summary", mapOf("mediaPkgName" to mediaProviderPackages.joinToString())]
        }

        Fact.MediaStoreDelete::class -> {
            i18n["ui.fact.media.delete"] to i18n["ui.fact.media.delete.summary", mapOf("mediaPkgName" to mediaProviderPackages.joinToString())]
        }

        Fact.OnStartOp::class -> {
            i18n["ui.fact.op.start"] to null
        }

        Fact.OnFinishOp::class -> {
            i18n["ui.fact.op.finish"] to null
        }

        Fact.OnQSTileClick::class -> {
            i18n["ui.settings.sb.tile"] to null
        }

        Fact.OnStartDynamicShortcut::class -> {
            i18n["ui.settings.dynamic.shortcut"] to null
        }

        Fact.OnMenuActionTrigger::class -> {
            i18n["ui.fact.custom.menu.action.trigger"] to null
        }

        Fact.SystemSettingsChanged::class -> {
            i18n["ui.fact.system.settings.changed"] to null
        }

        Fact.CallStateChanged::class -> {
            i18n["ui.fact.call.state.changed"] to null
        }

        Fact.ClipboardContentChanged::class -> {
            i18n["ui.fact.clipboard.content.changed"] to null
        }

        Fact.GlobalVarChanged::class -> {
            i18n["ui.fact.global.var.changed"] to null
        }

        Fact.HasFoundNodeOnScreen::class -> {
            i18n["ui.fact.has.found.node.on.screen"] to null
        }

        Fact.HeadsetPlug::class -> {
            i18n["ui.fact.headset.plug"] to null
        }

        Fact.ScreenRotate::class -> {
            i18n["ui.fact.screen.rotate"] to null
        }

        Fact.WindowRotationChange::class -> {
            i18n["ui.fact.window.rotate"] to null
        }

        Fact.IMEVisibilityChange::class -> {
            i18n["ui.fact.ime.visibility.change"] to null
        }

        Fact.AppGainWindowFocus::class -> {
            i18n["ui.fact.app.gain.window.focus"] to null
        }

        Fact.AppLostWindowFocus::class -> {
            i18n["ui.fact.app.lost.window.focus"] to null
        }

        Fact.DeepLinkCall::class -> {
            i18n["ui.fact.deeplink.call"] to null
        }

        Fact.IncomingShare::class -> {
            i18n.get(key = "ui.fact.incoming.share", fallback = "Incoming share") to null
        }

        Fact.Logcat::class -> {
            i18n["ui.fact.logcat"] to null
        }

        Fact.MethodHook::class -> {
            i18n["Method hook"] to null
        }

        Fact.BackNavStart::class -> {
            i18n["ui.fact.back.nav.start"] to null
        }

        Fact.BackNavDone::class -> {
            i18n["ui.fact.back.nav.done"] to null
        }

        Fact.ScreenOnTime::class -> {
            i18n["ui.fact.screen.on.time"] to null
        }

        Fact.CpuAvgAvailability::class -> {
            i18n["ui.fact.cpu.availability"] to null
        }

        Fact.SunRiseSet::class -> {
            i18n["ui.fact.sun.rise.set"] to null
        }

        Fact.UsbDeviceAttached::class -> {
            i18n["ui.fact.usb.device.attached"] to null
        }

        Fact.UsbDeviceDetached::class -> {
            i18n["ui.fact.usb.device.detached"] to null
        }

        Fact.LightSensor::class -> {
            i18n["ui.fact.sensor.light"] to null
        }

        Fact.AccelerometerSensor::class -> {
            i18n["ui.fact.sensor.accelerometer"] to null
        }

        Fact.ProximitySensor::class -> {
            i18n["ui.fact.sensor.proximity"] to null
        }

        Fact.Sound::class -> {
            i18n["ui.fact.sensor.sound"] to null
        }

        Fact.ProximitySensorStateChanged::class -> {
            i18n["ui.fact.sensor.proximity.state"] to null
        }

        Fact.ShakeDevice::class -> {
            i18n["ui.fact.sensor.shake.device"] to null
        }

        else -> "N/A" to null
    }
}

@Composable
fun labelForFact(fact: Fact): List<String> {
    val i18N = LocalI18N.current
    return labelForFact(i18N, fact)
}

fun labelForFact(i18N: I18N, fact: Fact): List<String> {
    return when (fact) {
        is Fact.AnyFact -> listOf("Any(For dev)")
        is Fact.ActivityStarted -> mutableListOf(i18N["ui.fact.activity.started"]).apply {
            addAll(fact.components.map {
                "${
                    it.toComponentName().flattenToShortString()
                }$PREFIX_APP_ICON${it.pkg.pkgName}"
            })
        }

        is Fact.ActivityStopped -> mutableListOf(i18N["ui.fact.activity.stopped"]).apply {
            addAll(fact.components.map {
                "${
                    it.toComponentName().flattenToShortString()
                }$PREFIX_APP_ICON${it.pkg.pkgName}"
            })
        }

        is Fact.ActivityDestroyed -> mutableListOf(i18N["ui.fact.activity.destroyed"]).apply {
            addAll(fact.components.map {
                "${
                    it.toComponentName().flattenToShortString()
                }$PREFIX_APP_ICON${it.pkg.pkgName}"
            })
        }

        is Fact.AnyActivityStarted -> listOf(
            i18N["ui.fact.activity.started"],
            i18N["ui.fact.activity.started.any"]
        )

        is Fact.AnyActivityStopped -> listOf(
            i18N["ui.fact.activity.stopped"],
            i18N["ui.fact.activity.started.any"]
        )

        is Fact.AnyActivityDestroyed -> listOf(
            i18N["ui.fact.activity.destroyed"],
            i18N["ui.fact.activity.started.any"]
        )

        is Fact.AppProcessStarted -> listOf(
            i18N["ui.fact.process.started"],
        )

        is Fact.AppProcessRemoved -> listOf(
            i18N["ui.fact.process.removed"],
        )

        is Fact.EdgeGesture -> listOfNotNull(
            i18N["ui.fact.edge.gesture"],
            // 如果有自定义Rect，显示自定义Rect描述；否则显示边缘位置
            if (fact.customRect != null) {
                i18N["ui.fact.edge.gesture.custom.rect.description", mapOf(
                    "left" to fact.customRect.left.toString(),
                    "top" to fact.customRect.top.toString(),
                    "right" to fact.customRect.right.toString(),
                    "bottom" to fact.customRect.bottom.toString()
                )]
            } else {
                i18N[fact.edge.displayNameKey()]
            }
                    + " "
                    + i18N[fact.gesture.displayNameKey()]
                    + fact.childSteps.joinToString(separator = "", prefix = "") {
                " -> " + i18N[it.displayNameKey()]
            },
            (!fact.enableGestureHint && fact.gesture.supportsGestureIndicator()).select(
                i18N["ui.fact.edge.gesture.indicator.disabled"],
                null
            ),
            (!fact.enableGestureHaptic && fact.gesture.supportsGestureIndicator()).select(
                i18N["ui.fact.edge.gesture.haptic.disabled"],
                null
            ),
            fact.disableWhenIME.select(
                i18N["ui.fact.edge.gesture.disable.when.ime"],
                null
            ),
            ((fact.customRect == null) && fact.useAbsoluteArea).select(
                i18N["ui.fact.edge.gesture.absolute.area.enabled"],
                null
            ),
            i18N.gestureDisabledMessageOrNull()
        )

        is Fact.CornerSwipeToCenter -> listOfNotNull(
            i18N["ui.fact.corner.swipe.to.center"],
            i18N[fact.corner.displayNameKey()],
            (!fact.enableGestureHint).select(i18N["ui.fact.corner.gesture.indicator.disabled"], null),
            (!fact.enableGestureHaptic).select(i18N["ui.fact.corner.gesture.haptic.disabled"], null),
            i18N.gestureDisabledMessageOrNull()
        )

        is Fact.ScreenGesture -> listOfNotNull(
            i18N["ui.fact.screen.gesture"],
            i18N[fact.gesture.displayNameKey()],
            i18N.gestureDisabledMessageOrNull()
        )

        is Fact.CombineKeyEvent -> listOfNotNull(
            i18N["ui.action.inject.combine.keycode"],
            getLabelForKeyCode(i18N, fact.keyCode1)
                    + " & " + getLabelForKeyCode(
                i18N, fact.keyCode2
            ),
            i18N.keyEventDisabledMessageOrNull()
        )

        is Fact.KeyEvent -> listOfNotNull(
            i18N["ui.fact.key.event"],
            getLabelForKeyCode(i18N, keyCode = fact.keyCode),
            i18N.keyEventDisabledMessageOrNull()
        )

        is Fact.AdvancedKeyEvent -> listOfNotNull(
            i18N["ui.fact.key.event.adv"],
            getLabelForKeyCode(
                i18N,
                keyCode = fact.keyCode
            ) + " " + i18N[fact.gesture.displayNameKey()],
            i18N.keyEventDisabledMessageOrNull()
        )

        is Fact.ScreenOff -> listOf(i18N["ui.fact.screen.off"])
        is Fact.ScreenOn -> listOf(i18N["ui.fact.screen.on"])
        is Fact.UserPresent -> listOf(i18N["ui.fact.screen.user.present"])
        is Fact.UserPresentAtTheFirstTime -> listOf(i18N["ui.fact.screen.user.present.at.the.first.time"])
        is Fact.SystemReady -> listOf(i18N["ui.fact.system.ready"])

        is Fact.Alarm -> {
            listOfNotNull(
                i18N["ui.fact.time.of.a.day.at", mapOf("time" to fact.timeOfADay.toDisplayTime())],
                if (fact.repeatDays.isNo) {
                    i18N["ui.fact.time.of.a.day.repeat.at", mapOf(
                        "days" to i18N["ui.fact.time.of.a.day.repeat.no"]
                    )]
                } else if (fact.repeatDays.isEveryDay) {
                    i18N["ui.fact.time.of.a.day.repeat.at", mapOf(
                        "days" to i18N["ui.fact.time.of.a.day.repeat.everyday"]
                    )]
                } else {
                    i18N["ui.fact.time.of.a.day.repeat.at", mapOf(
                        "days" to fact.repeatDays.sortedDays.joinToString(
                            " "
                        ) {
                            getLongLabelForWeekDay(weekDay = it, i18n = i18N)
                        })]
                },
                fact.workdayFilter.labelLineOrNull(i18N)
            )
        }

        is Fact.RandomInPeriod -> {
            listOfNotNull(
                i18N["ui.fact.time.of.a.day.random"],
                i18N["ui.fact.time.of.a.day.random.at", mapOf("time" to fact.start.toDisplayTime() + " - " + fact.end.toDisplayTime())],
                if (fact.repeatDays.isNo) {
                    i18N["ui.fact.time.of.a.day.repeat.at", mapOf(
                        "days" to i18N["ui.fact.time.of.a.day.repeat.no"]
                    )]
                } else if (fact.repeatDays.isEveryDay) {
                    i18N["ui.fact.time.of.a.day.repeat.at", mapOf(
                        "days" to i18N["ui.fact.time.of.a.day.repeat.everyday"]
                    )]
                } else {
                    i18N["ui.fact.time.of.a.day.repeat.at", mapOf(
                        "days" to fact.repeatDays.sortedDays.joinToString(
                            " "
                        ) {
                            getLongLabelForWeekDay(weekDay = it, i18n = i18N)
                        })]
                },
                fact.workdayFilter.labelLineOrNull(i18N)
            )
        }

        is Fact.FixedInPeriod -> {
            listOfNotNull(
                i18N["ui.fact.time.of.a.day.fixed"],
                i18N["ui.fact.time.of.a.day.at", mapOf("time" to fact.start.toDisplayTime() + " - " + fact.end.toDisplayTime())],
                fact.fixedBy.label(i18N),
                if (fact.repeatDays.isNo) {
                    i18N["ui.fact.time.of.a.day.repeat.at", mapOf(
                        "days" to i18N["ui.fact.time.of.a.day.repeat.no"]
                    )]
                } else if (fact.repeatDays.isEveryDay) {
                    i18N["ui.fact.time.of.a.day.repeat.at", mapOf(
                        "days" to i18N["ui.fact.time.of.a.day.repeat.everyday"]
                    )]
                } else {
                    i18N["ui.fact.time.of.a.day.repeat.at", mapOf(
                        "days" to fact.repeatDays.sortedDays.joinToString(
                            " "
                        ) {
                            getLongLabelForWeekDay(weekDay = it, i18n = i18N)
                        })]
                },
                fact.workdayFilter.labelLineOrNull(i18N)
            )
        }

        is Fact.AppBecomeBackground -> mutableListOf(
            i18N["ui.fact.app.become.bg"]
        ).labelLinesFromAppsAndPkgSets(fact.apps, fact.pkgSets)

        is Fact.AppBecomeForeground -> mutableListOf(
            i18N["ui.fact.app.become.fg"]
        ).labelLinesFromAppsAndPkgSets(fact.apps, fact.pkgSets)

        is Fact.WifiStatusChanged -> {
            listOf(
                i18N["ui.fact.wifi.state.changed"],
                fact.state.label(i18N)
            )
        }

        is Fact.WifiConnectedTo -> {
            listOf(
                i18N["ui.fact.wifi.connect.to"],
                fact.ssidList.joinToString()
            )
        }

        is Fact.WifiDisconnectedFrom -> {
            listOf(
                i18N["ui.fact.wifi.disconnected.from"],
                fact.ssidList.joinToString()
            )
        }

        is Fact.BTStatusChanged -> {
            listOf(
                i18N["ui.fact.bt.state.changed"],
                fact.state.label(i18N)
            )
        }

        is Fact.BTConnectedTo -> {
            listOf(
                i18N["ui.fact.bt.connected.to"],
                fact.devices.joinToString()
            )
        }

        is Fact.BTDisconnectedFrom -> {
            listOf(
                i18N["ui.fact.bt.disconnected.from"],
                fact.devices.joinToString()
            )
        }

        is Fact.DNDStatusChanged -> {
            listOf(
                i18N["ui.fact.dnd.state.changed"],
                fact.state.label(i18N)
            )
        }

        is Fact.APMStatusChanged -> {
            listOf(
                i18N["ui.condition.apm.mode"],
                fact.state.label(i18N)
            )
        }

        is Fact.DarkModeStatusChanged -> {
            listOf(
                i18N["ui.fact.dark.mode.state.changed"],
                fact.state.label(i18N)
            )
        }

        is Fact.FlashLightStatusChanged -> {
            listOf(
                i18N["ui.fact.flash.light.state.changed"],
                fact.state.label(i18N)
            )
        }

        is Fact.HotSpotStatusChanged -> {
            listOf(
                i18N["ui.fact.hotspot.state.changed"],
                fact.state.label(i18N)
            )
        }

        is Fact.LocationStatusChanged -> {
            listOf(
                i18N["ui.fact.location.state.changed"],
                fact.state.label(i18N)
            )
        }

        is Fact.MobileDataStatusChanged -> {
            listOf(
                i18N["ui.fact.mobile.data.state.changed"],
                fact.state.label(i18N)
            )
        }

        is Fact.NFCStatusChanged -> {
            listOf(
                i18N["ui.fact.nfc.state.changed"],
                fact.state.label(i18N)
            )
        }

        is Fact.NotificationPosted -> {
            mutableListOf(
                i18N["ui.fact.n.posted"],
            ).labelLinesFromAppsAndPkgSets(fact.n.apps, fact.n.pkgSets)
        }

        is Fact.NotificationRemoved -> {
            mutableListOf(
                i18N["ui.fact.n.removed"],
            ).labelLinesFromAppsAndPkgSets(fact.n.apps, fact.n.pkgSets)
        }

        is Fact.NotificationUpdated -> {
            mutableListOf(
                i18N["ui.fact.n.updated"],
            ).labelLinesFromAppsAndPkgSets(fact.n.apps, fact.n.pkgSets)
        }

        is Fact.TaskRemoved -> {
            mutableListOf(
                i18N["ui.fact.task.removed"],
            ).labelLinesFromAppsAndPkgSets(fact.apps, fact.pkgSets)
        }

        is Fact.TaskRemovedAny -> {
            listOf(
                i18N["ui.fact.task.removed"],
                i18N["ui.fact.task.removed.any"]
            )
        }

        is Fact.PkgStopRunning -> {
            mutableListOf(
                i18N["ui.fact.pkg.stop.running"],
            ).labelLinesFromAppsAndPkgSets(fact.apps, fact.pkgSets)
        }

        is Fact.PkgStopRunningAny -> {
            listOf(
                i18N["ui.fact.pkg.stop.running"],
                i18N["ui.fact.pkg.stop.running.any"]
            )
        }

        is Fact.PkgStartRunning -> {
            mutableListOf(
                i18N["ui.fact.pkg.start.running"],
            ).labelLinesFromAppsAndPkgSets(fact.apps, fact.pkgSets)
        }

        is Fact.PkgStartRunningAny -> {
            listOf(
                i18N["ui.fact.pkg.start.running"],
                i18N["ui.fact.pkg.start.running.any"]
            )
        }

        is Fact.AppAdded -> {
            listOf(i18N["ui.fact.app.installed"])
        }

        is Fact.AppRemoved -> {
            mutableListOf(i18N["ui.fact.app.uninstalled"])
                .labelLinesFromAppsAndPkgSets(fact.apps, fact.pkgSets)
        }

        is Fact.AppUpdated -> {
            mutableListOf(i18N["ui.fact.app.updated"])
                .labelLinesFromAppsAndPkgSets(fact.apps, fact.pkgSets)
        }

        is Fact.AudioFocusChanged -> {
            listOf(i18N["ui.fact.audio.focus.changed"])
        }

        is Fact.AudioFocusGain -> {
            mutableListOf(i18N["ui.fact.audio.focus.gain"])
                .labelLinesFromAppsAndPkgSets(fact.apps, fact.pkgSets)
        }

        is Fact.AudioFocusLost -> {
            mutableListOf(i18N["ui.fact.audio.focus.lost"])
                .labelLinesFromAppsAndPkgSets(fact.apps, fact.pkgSets)
        }

        is Fact.BatteryLevelChanged -> {
            listOfNotNull(
                i18N["ui.fact.battery.level.changed"],
                if (fact.level > 0) "${fact.level}%" else null
            )
        }

        is Fact.BatteryTemperatureChanged -> {
            listOfNotNull(
                i18N["ui.fact.battery.temp.changed"],
                if (fact.temp > 0f) "${fact.temp}°C" else null
            )
        }

        is Fact.ChargerPlug -> {
            listOf(i18N["ui.fact.battery.charge.plug"])
        }

        is Fact.ChargerUnplug -> {
            listOf(i18N["ui.fact.battery.charge.unplug"])
        }

        is Fact.Broadcast -> {
            mutableListOf(
                i18N["ui.fact.broadcast"],
                fact.actions.joinToString(System.lineSeparator())
            )
        }

        is Fact.VPNConnected -> {
            listOf(
                i18N["ui.fact.vpn.connected"]
            )
        }

        is Fact.VPNDisconnected -> {
            listOf(
                i18N["ui.fact.vpn.disconnected"]
            )
        }

        is Fact.MediaStoreInsert -> {
            listOf(
                i18N["ui.fact.media.insert"],
                fact.filterPathRegex
            )
        }

        is Fact.MediaStoreDelete -> {
            listOf(
                i18N["ui.fact.media.delete"],
                fact.filterPathRegex
            )
        }

        is Fact.OnStartOp -> {
            mutableListOf(
                i18N["ui.fact.op.start"],
                fact.codes.map { i18N[it.opsLabelKey()] }.distinct().joinToString()
            ).labelLinesFromAppsAndPkgSets(fact.apps, fact.pkgSets)
        }

        is Fact.OnFinishOp -> {
            mutableListOf(
                i18N["ui.fact.op.finish"],
                fact.codes.map { i18N[it.opsLabelKey()] }.distinct().joinToString()
            ).labelLinesFromAppsAndPkgSets(fact.apps, fact.pkgSets)
        }

        is Fact.OnQSTileClick -> {
            listOf(
                i18N["ui.settings.sb.tile"],
                shortXManager.getStatusBarTileSettingsByNumber(fact.tile.number)
                    .label
            )
        }

        is Fact.OnStartDynamicShortcut -> {
            mutableListOf(
                i18N["ui.settings.dynamic.shortcut"],
                (shortXManager.getDynamicShortcutSettingsByNumber(fact.shortcut.number)
                    ?: defaultDynamicShortcutSettings(fact.shortcut))
                    .label
            ).labelLinesFromAppsAndPkgSets(fact.apps, fact.pkgSets)
        }

        is Fact.OnMenuActionTrigger -> {
            listOf(
                i18N["ui.fact.custom.menu.action.trigger"],
                shortXManager.getContextMenuSettingsByMenuNumber(fact.menu.number)
                    .label
            )
        }

        is Fact.SystemSettingsChanged -> {
            listOf(
                i18N["ui.fact.system.settings.changed"],
                fact.urlAndValueRegex.first,
                fact.urlAndValueRegex.second,
            )
        }

        is Fact.CallStateChanged -> {
            listOf(
                i18N["ui.fact.call.state.changed"],
                i18N["ui.fact.call.state.${fact.callState.name.lowercase()}"],
            )
        }

        is Fact.ClipboardContentChanged -> {
            listOf(
                i18N["ui.fact.clipboard.content.changed"],
                fact.content
            )
        }

        is Fact.GlobalVarChanged -> {
            listOf(
                i18N["ui.fact.global.var.changed"],
                fact.gvId
            )
        }

        is Fact.HasFoundNodeOnScreen -> {
            listOf(i18N["ui.fact.has.found.node.on.screen"])
        }

        is Fact.HeadsetPlug -> {
            listOf(
                i18N["ui.fact.headset.plug"],
                fact.isPlug.select(
                    i18N["ui.fact.headset.plug.in"],
                    i18N["ui.fact.headset.plug.out"]
                )
            )
        }

        is Fact.ScreenRotate -> {
            listOf(
                i18N["ui.fact.screen.rotate"],
                i18N[fact.degree.factLabelKey()]
            )
        }

        is Fact.WindowRotationChange -> {
            listOf(
                i18N["ui.fact.window.rotate"],
                i18N[fact.degree.factLabelKey()]
            )
        }

        is Fact.IMEVisibilityChange -> {
            listOf(
                i18N["ui.fact.ime.visibility.change"],
                fact.isShown.select(
                    i18N["ui.fact.ime.visibility.show"],
                    i18N["ui.fact.ime.visibility.hide"]
                )
            )
        }

        is Fact.AppGainWindowFocus -> {
            mutableListOf(
                i18N["ui.fact.app.gain.window.focus"]
            ).labelLinesFromAppsAndPkgSets(fact.apps, fact.pkgSets)
        }

        is Fact.AppLostWindowFocus -> {
            mutableListOf(
                i18N["ui.fact.app.lost.window.focus"]
            ).labelLinesFromAppsAndPkgSets(fact.apps, fact.pkgSets)
        }

        is Fact.DeepLinkCall -> {
            listOf(
                i18N["ui.fact.deeplink.call"],
                fact.deepLinkTag
            )
        }

        is Fact.IncomingShare -> {
            listOf(
                i18N.get(key = "ui.fact.incoming.share", fallback = "Incoming share")
            )
        }

        is Fact.ConnectedWifiSignalLevelChanged -> {
            listOf(
                i18N["ui.fact.wifi.signal.changed"],
                fact.level.toString()
            )
        }

        is Fact.NFCTagDiscover -> {
            listOf(
                i18N["ui.fact.nfc.tag"],
                fact.uid.joinToString("")
            )
        }

        is Fact.Logcat -> {
            listOf(
                i18N["ui.fact.logcat"],
                "${i18N[fact.regexMatchOptions.labelKey()]} ${fact.regex}"
            )
        }

        is Fact.MethodHook -> {
            listOf(
                i18N["Method hook"],
                "${fact.className} ${fact.methodName}$PREFIX_APP_ICON${fact.packageName}"
            )
        }

        is Fact.BackNavStart -> {
            listOf(
                i18N["ui.fact.back.nav.start"],
            )
        }

        is Fact.BackNavDone -> {
            listOf(
                i18N["ui.fact.back.nav.done"],
            )
        }

        is Fact.ScreenOnTime -> {
            listOf(
                i18N["ui.fact.screen.on.time"],
                "${i18N[fact.from.factLabelKey()]} ${fact.timeInSeconds}s"
            )
        }

        is Fact.SunRiseSet -> {
            listOf(i18N[fact.rise.factLabelKey()])
        }

        is Fact.CpuAvgAvailability -> {
            listOf(i18N["ui.fact.cpu.availability"])
        }

        is Fact.UsbDeviceAttached -> {
            listOf(
                i18N["ui.fact.usb.device.attached"],
            )
        }

        is Fact.UsbDeviceDetached -> {
            listOf(
                i18N["ui.fact.usb.device.detached"],
            )
        }

        is Fact.LightSensor -> {
            listOfNotNull(
                i18N["ui.fact.sensor.light"],
                fact.constraints.takeIf { it.isNotEmpty() }
                    ?.joinToString { it.op.toDisplayName() + " " + it.payload.value.orEmpty() }
            )
        }

        is Fact.ProximitySensor -> {
            listOfNotNull(
                i18N["ui.fact.sensor.proximity"],
                fact.constraints.takeIf { it.isNotEmpty() }
                    ?.joinToString { it.op.toDisplayName() + " " + it.payload.value.orEmpty() }
            )
        }

        is Fact.Sound -> {
            listOfNotNull(
                i18N["ui.fact.sensor.sound"],
                fact.constraints.takeIf { it.isNotEmpty() }
                    ?.joinToString { it.op.toDisplayName() + " " + it.payload.value.orEmpty() }
            )
        }

        is Fact.AccelerometerSensor -> {
            listOfNotNull(
                i18N["ui.fact.sensor.accelerometer"],
                fact.constraintsX.takeIf { it.isNotEmpty() }
                    ?.joinToString { it.op.toDisplayName() + " " + it.payload.value.orEmpty() },
                fact.constraintsY.takeIf { it.isNotEmpty() }
                    ?.joinToString { it.op.toDisplayName() + " " + it.payload.value.orEmpty() },
                fact.constraintsZ.takeIf { it.isNotEmpty() }
                    ?.joinToString { it.op.toDisplayName() + " " + it.payload.value.orEmpty() },
            )
        }

        is Fact.ProximitySensorStateChanged -> {
            listOf(
                i18N["ui.fact.sensor.proximity.state"],
                i18N[fact.state.displayNameKey()]
            )
        }

        is Fact.ShakeDevice -> {
            listOf(i18N["ui.fact.sensor.shake.device"])
        }
    }
}

@Composable
fun getLabelForKeyCode(keyCode: Int): String {
    return getLabelForKeyCode(LocalI18N.current, keyCode)
}

fun getLabelForKeyCode(i18N: I18N, keyCode: Int): String {
    return when (keyCode) {
        KeyEvent.KEYCODE_VOLUME_DOWN -> i18N["ui.keycode.label.volume.down"]
        KeyEvent.KEYCODE_VOLUME_UP -> i18N["ui.keycode.label.volume.up"]
        KeyEvent.KEYCODE_BACK -> i18N["ui.keycode.label.back"]
        KeyEvent.KEYCODE_HOME -> i18N["ui.keycode.label.home"]
        KeyEvent.KEYCODE_POWER -> i18N["ui.keycode.label.power"]
        else -> i18N["ui.action.custom.keycode"] + " " + keyCode.toString()
    }
}

@Composable
fun getShortLabelForWeekDay(weekDay: WeekDay): String {
    return when (weekDay) {
        WeekDay.MONDAY -> LocalI18N.current["ui.fact.weekday.monday.short"]
        WeekDay.TUESDAY -> LocalI18N.current["ui.fact.weekday.tuesday.short"]
        WeekDay.WEDNESDAY -> LocalI18N.current["ui.fact.weekday.wednesday.short"]
        WeekDay.THURSDAY -> LocalI18N.current["ui.fact.weekday.thursday.short"]
        WeekDay.FRIDAY -> LocalI18N.current["ui.fact.weekday.friday.short"]
        WeekDay.SATURDAY -> LocalI18N.current["ui.fact.weekday.saturday.short"]
        WeekDay.SUNDAY -> LocalI18N.current["ui.fact.weekday.sunday.short"]
        else -> {
            "N/A"
        }
    }
}

fun getLongLabelForWeekDay(i18n: I18N, weekDay: WeekDay): String {
    return when (weekDay) {
        WeekDay.MONDAY -> i18n["ui.fact.weekday.monday.long"]
        WeekDay.TUESDAY -> i18n["ui.fact.weekday.tuesday.long"]
        WeekDay.WEDNESDAY -> i18n["ui.fact.weekday.wednesday.long"]
        WeekDay.THURSDAY -> i18n["ui.fact.weekday.thursday.long"]
        WeekDay.FRIDAY -> i18n["ui.fact.weekday.friday.long"]
        WeekDay.SATURDAY -> i18n["ui.fact.weekday.saturday.long"]
        WeekDay.SUNDAY -> i18n["ui.fact.weekday.sunday.long"]
        else -> {
            "N/A"
        }
    }
}

fun TimeOfADay.toDisplayTime(): String {
    val time = toLocalTime()
    return DateUtils.formatLocalTime(time)
}

fun OnOffAny.label(i18n: I18N): String {
    return when (this) {
        OnOffAny.On -> {
            i18n["ui.common.on"]
        }

        OnOffAny.Off -> {
            i18n["ui.common.off"]
        }

        OnOffAny.Any -> {
            i18n["ui.common.any"]
        }

        OnOffAny.UNRECOGNIZED -> {
            "UNRECOGNIZED"
        }
    }
}

fun FixedSetting.label(i18n: I18N): String {
    return when (this) {
        is FixedSetting.Interval -> i18n["ui.fact.time.of.a.day.fixed.by.interval"] + " " + displayDuration

        is FixedSetting.Times -> i18n["ui.fact.time.of.a.day.fixed.by.times"] + " " + this.times
    }
}

fun Int.opsIconName(): String {
    return when (this) {
        AppOpsManagerCompat.OP_GPS -> Remix.Map.map_pin_2_fill
        AppOpsManagerCompat.OP_CAMERA -> Remix.Media.camera_2_fill
        AppOpsManagerCompat.OP_RECORD_AUDIO -> Remix.Media.record_circle_fill
        else -> ""
    }
}

fun ScreenRotateDegree.factLabelKey(): String {
    return "ui.fact.screen.rotate.degree.${name.lowercase()}"
}


fun ScreenOnTimeStartFrom.factLabelKey(): String {
    return "ui.fact.screen.on.time.${name.lowercase()}"
}

fun SunRise.factLabelKey(): String {
    return when (this) {
        SunRise.SunRise_Rise -> "ui.fact.sun.rise.set.rise"
        SunRise.SunRise_Set -> "ui.fact.sun.rise.set.set"
        SunRise.UNRECOGNIZED -> "N/A"
    }
}

private fun WorkdayFilter.labelLineOrNull(i18N: I18N): String? {
    if (dayType == CalendarDayType.AnyDay || dayType == CalendarDayType.UNRECOGNIZED) {
        return null
    }
    return i18N["ui.conditions.calendar.day.match"] + ": " + i18N[dayType.calendarDayTypeLabelKey()]
}

fun Gesture.displayNameKey(): String {
    val prefix = "ui.fact.edge.gesture"
    return "$prefix.${name.lowercase(Locale.ENGLISH)}"
}

fun EdgeGestureStepModel.displayNameKey(): String {
    val prefix = "ui.fact.edge.gesture.child"
    return "$prefix.${name.lowercase(Locale.ENGLISH)}"
}

fun Edge.displayNameKey(): String {
    val prefix = "ui.fact.edge"
    return "$prefix.${name.lowercase(Locale.ENGLISH)}"
}

fun ScreenCorner.displayNameKey(): String {
    return when (this) {
        ScreenCorner.CornerTopLeft -> "ui.fact.corner.topleft"
        ScreenCorner.CornerTopRight -> "ui.fact.corner.topright"
        ScreenCorner.CornerBottomLeft -> "ui.fact.corner.bottomleft"
        ScreenCorner.CornerBottomRight -> "ui.fact.corner.bottomright"
        else -> "ui.fact.corner.topleft"
    }
}

fun Gesture.supportsGestureIndicator(): Boolean {
    return when (this) {
        Gesture.SwipeFromTop,
        Gesture.SwipeFromBottom,
        Gesture.SwipeFromLeft,
        Gesture.SwipeFromRight -> true

        else -> false
    }
}

fun KeyGesture.displayNameKey(): String {
    val prefix = "ui.fact.key.gesture"
    return "$prefix.${name.lowercase(Locale.ENGLISH)}"
}

fun I18N.keyEventDisabledMessageOrNull(): String? {
    return if (!shortXManager.isAppFeatureEnabled(AppFeature.AppFeature_KeyEvent.number)) {
        "${this["ui.settings.keyevent.disabled.warn"]} $STYLE_COLOR_RED ${PREFIX_MD_ICON}${Remix.System.alert_fill}"
    } else {
        null
    }
}

fun I18N.gestureDisabledMessageOrNull(): String? {
    return if (!shortXManager.isAppFeatureEnabled(AppFeature.AppFeature_EdgeGesture.number)) {
        "${this["ui.settings.edge.gesture.disabled.warn"]} $STYLE_COLOR_RED ${PREFIX_MD_ICON}${Remix.System.alert_fill}"
    } else {
        null
    }
}

fun SensorValueTrend.displayNameKey(): String {
    return when (this) {
        SensorValueTrend.SensorValueTrend_Any -> "ui.common.any"
        SensorValueTrend.SensorValueTrend_Increase -> "ui.fact.sensor.increase.to"
        SensorValueTrend.SensorValueTrend_Decrease -> "ui.fact.sensor.decrease.to"
        SensorValueTrend.UNRECOGNIZED -> "N/A"
    }
}

fun ProximitySensorState.displayNameKey(): String {
    return when (this) {
        ProximitySensorState.ProximitySensorState_Approaching -> "ui.fact.sensor.proximity.state.approaching"
        ProximitySensorState.ProximitySensorState_Departing -> "ui.fact.sensor.proximity.state.departing"
        ProximitySensorState.ProximitySensorState_Any -> "ui.fact.sensor.proximity.state.any"
        else -> "N/A"
    }
}