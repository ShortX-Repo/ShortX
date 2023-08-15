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
import tornaco.apps.shortx.core.platform.PackageNames.mediaProvider
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.common.TimeOfADay
import tornaco.apps.shortx.core.proto.fact.WeekDay
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.settings.defaultStatusBarTileSetting
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.toComponentName
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.selector.displayNameKey
import tornaco.apps.shortx.ui.addrule.labelLinesFromAppsAndPkgSets
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.PREFIX_APP_ICON
import kotlin.reflect.KClass


fun labelAndDescriptionForFactSelector(i18n: I18N, fact: KClass<out Fact>): Pair<String, String?> {
    return when (fact) {
        Fact.ActivityStarted::class -> i18n["ui.fact.activity.started"] to i18n["ui.fact.activity.started.specific"]
        Fact.AnyActivityStarted::class -> i18n["ui.fact.activity.started"] to i18n["ui.fact.activity.started.any"]
        Fact.EdgeGesture::class -> i18n["ui.fact.edge.gesture"] to null
        Fact.KeyEvent::class -> i18n["ui.fact.key.event"] to null
        Fact.AdvancedKeyEvent::class -> i18n["ui.fact.key.event.adv"] to null
        Fact.ScreenOff::class -> i18n["ui.fact.screen.off"] to null
        Fact.ScreenOn::class -> i18n["ui.fact.screen.on"] to null
        Fact.UserPresent::class -> i18n["ui.fact.screen.user.present"] to null
        Fact.SystemReady::class -> i18n["ui.fact.system.ready"] to null
        Fact.Alarm::class -> i18n["ui.fact.time.of.a.day"] to null
        Fact.RandomInPeriod::class -> i18n["ui.fact.time.of.a.day.random"] to null
        Fact.FixedInPeriod::class -> i18n["ui.fact.time.of.a.day.fixed"] to null
        Fact.AppBecomeBackground::class -> i18n["ui.fact.app.become.bg"] to null
        Fact.AppBecomeForeground::class -> i18n["ui.fact.app.become.fg"] to null

        Fact.WifiStatusChanged::class -> i18n["ui.fact.wifi.state.changed"] to null
        Fact.WifiConnectedTo::class -> i18n["ui.fact.wifi.connect.to"] to null
        Fact.WifiDisconnectedFrom::class -> i18n["ui.fact.wifi.disconnected.from"] to null

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
            i18n["ui.fact.media.insert"] to i18n["ui.fact.media.insert.summary", mapOf("mediaPkgName" to mediaProvider)]
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
        is Fact.ActivityStarted -> mutableListOf(i18N["ui.fact.activity.started"]).apply {
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

        is Fact.EdgeGesture -> listOf(
            i18N["ui.fact.edge.gesture"],
            i18N[fact.edge.displayNameKey()],
            i18N[fact.gesture.displayNameKey()]
        )

        is Fact.KeyEvent -> listOf(
            i18N["ui.fact.key.event"],
            getLabelForKeyCode(i18N, keyCode = fact.keyCode)
        )

        is Fact.AdvancedKeyEvent -> listOf(
            i18N["ui.fact.key.event.adv"],
            getLabelForKeyCode(
                i18N,
                keyCode = fact.keyCode
            ) + " " + i18N[fact.gesture.displayNameKey()]
        )

        is Fact.ScreenOff -> listOf(i18N["ui.fact.screen.off"])
        is Fact.ScreenOn -> listOf(i18N["ui.fact.screen.on"])
        is Fact.UserPresent -> listOf(i18N["ui.fact.screen.user.present"])
        is Fact.SystemReady -> listOf(i18N["ui.fact.system.ready"])

        is Fact.Alarm -> {
            listOf(
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
                }
            )
        }

        is Fact.RandomInPeriod -> {
            listOf(
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
                }
            )
        }

        is Fact.FixedInPeriod -> {
            listOf(
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
                }
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
                (shortXManager.getStatusBarTileSettingsByNumber(fact.tile.number)
                    ?: defaultStatusBarTileSetting(fact.tile))
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