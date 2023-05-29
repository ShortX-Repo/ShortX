package tornaco.apps.shortx.ui.addrule.fact

import android.view.KeyEvent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import tornaco.apps.shortx.core.alarm.isEveryDay
import tornaco.apps.shortx.core.alarm.isNo
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.fact.TimeOfADay
import tornaco.apps.shortx.core.proto.fact.WeekDay
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.toComponentName
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.addrule.action.ActionColorPalette
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.model.allFactClasses
import tornaco.apps.shortx.ui.addrule.labelLinesFromAppsAndPkgSets
import tornaco.apps.shortx.ui.base.I18N
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.PREFIX_APP_ICON
import java.util.Calendar
import kotlin.reflect.KClass


fun labelAndDescriptionForFactSelector(i18n: I18N, fact: KClass<out Fact>): Pair<String, String?> {
    return when (fact) {
        Fact.ActivityStarted::class -> i18n["ui.fact.activity.started"] to i18n["ui.fact.activity.started.specific"]
        Fact.AnyActivityStarted::class -> i18n["ui.fact.activity.started"] to i18n["ui.fact.activity.started.any"]
        Fact.EdgeGesture::class -> i18n["ui.fact.edge.gesture"] to null
        Fact.KeyEvent::class -> i18n["ui.fact.key.event"] to null
        Fact.ScreenOff::class -> i18n["ui.fact.screen.off"] to null
        Fact.ScreenOn::class -> i18n["ui.fact.screen.on"] to null
        Fact.UserPresent::class -> i18n["ui.fact.screen.user.present"] to null
        Fact.Alarm::class -> i18n["ui.fact.time.of.a.day"] to null
        Fact.AppBecomeBackground::class -> i18n["ui.fact.app.become.bg"] to null
        Fact.AppBecomeForeground::class -> i18n["ui.fact.app.become.fg"] to null
        Fact.WifiStatusChanged::class -> i18n["ui.fact.wifi.state.changed"] to null

        Fact.BTStatusChanged::class -> {
            i18n["ui.fact.bt.state.changed"] to null
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

        Fact.ScreenOff -> listOf(i18N["ui.fact.screen.off"])
        Fact.ScreenOn -> listOf(i18N["ui.fact.screen.on"])
        Fact.UserPresent -> listOf(i18N["ui.fact.screen.user.present"])
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
                        "days" to fact.repeatDays.daysList.joinToString(
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
            mutableListOf(
                i18N["ui.fact.wifi.state.changed"],
                fact.state.label(i18N)
            )
        }

        is Fact.BTStatusChanged -> {
            mutableListOf(
                i18N["ui.fact.bt.state.changed"],
                fact.state.label(i18N)
            )
        }

        is Fact.DNDStatusChanged -> {
            mutableListOf(
                i18N["ui.fact.dnd.state.changed"],
                fact.state.label(i18N)
            )
        }

        is Fact.DarkModeStatusChanged -> {
            mutableListOf(
                i18N["ui.fact.dark.mode.state.changed"],
                fact.state.label(i18N)
            )
        }

        is Fact.FlashLightStatusChanged -> {
            mutableListOf(
                i18N["ui.fact.flash.light.state.changed"],
                fact.state.label(i18N)
            )
        }

        is Fact.HotSpotStatusChanged -> {
            mutableListOf(
                i18N["ui.fact.hotspot.state.changed"],
                fact.state.label(i18N)
            )
        }

        is Fact.LocationStatusChanged -> {
            mutableListOf(
                i18N["ui.fact.location.state.changed"],
                fact.state.label(i18N)
            )
        }

        is Fact.MobileDataStatusChanged -> {
            mutableListOf(
                i18N["ui.fact.mobile.data.state.changed"],
                fact.state.label(i18N)
            )
        }

        is Fact.NFCStatusChanged -> {
            mutableListOf(
                i18N["ui.fact.nfc.state.changed"],
                fact.state.label(i18N)
            )
        }

        is Fact.NotificationPosted -> {
            mutableListOf(
                i18N["ui.fact.n.posted"],
                fact.n.titleRegex.fallbackOnEmpty(""),
                fact.n.contentRegex.fallbackOnEmpty("")
            ).filterNot { it.isBlank() }
        }

        is Fact.NotificationRemoved -> {
            mutableListOf(
                i18N["ui.fact.n.removed"],
                fact.n.titleRegex.fallbackOnEmpty(""),
                fact.n.contentRegex.fallbackOnEmpty("")
            ).filterNot { it.isBlank() }
        }

        is Fact.NotificationUpdated -> {
            mutableListOf(
                i18N["ui.fact.n.updated"],
                fact.n.titleRegex.fallbackOnEmpty(""),
                fact.n.contentRegex.fallbackOnEmpty("")
            ).filterNot { it.isBlank() }
        }

        is Fact.TaskRemoved -> {
            mutableListOf(
                i18N["ui.fact.task.removed"],
            ).labelLinesFromAppsAndPkgSets(fact.apps, fact.pkgSets)
        }

        is Fact.TaskRemovedAny -> {
            mutableListOf(
                i18N["ui.fact.task.removed"],
                i18N["ui.fact.task.removed.any"]
            )
        }
    }
}

@Composable
private fun getLabelForKeyCode(keyCode: Int): String {
    return getLabelForKeyCode(LocalI18N.current, keyCode)
}

private fun getLabelForKeyCode(i18N: I18N, keyCode: Int): String {
    return when (keyCode) {
        KeyEvent.KEYCODE_VOLUME_DOWN -> i18N["ui.keycode.label.volume.down"]
        KeyEvent.KEYCODE_VOLUME_UP -> i18N["ui.keycode.label.volume.up"]
        KeyEvent.KEYCODE_BACK -> i18N["ui.keycode.label.back"]
        KeyEvent.KEYCODE_HOME -> i18N["ui.keycode.label.home"]
        KeyEvent.KEYCODE_POWER -> i18N["ui.keycode.label.power"]
        else -> keyCode.toString()
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
    val date = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minutes)
        set(Calendar.SECOND, 0)
    }.time
    return DateUtils.formatShortForMessageTime(date.time)
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

fun imageVectorForFact(fact: KClass<out Fact>): String {
    return when (fact) {
        Fact.ActivityStarted::class -> Remix.Logos.android_fill
        Fact.AnyActivityStarted::class -> Remix.Logos.android_fill
        Fact.EdgeGesture::class -> Remix.Editor.sketching
        Fact.KeyEvent::class -> Remix.Device.keyboard_fill
        Fact.ScreenOff::class -> Remix.Device.smartphone_fill
        Fact.ScreenOn::class -> Remix.Device.smartphone_fill
        Fact.UserPresent::class -> Remix.Device.phone_lock_fill
        Fact.Alarm::class -> Remix.System.alarm_fill
        Fact.AppBecomeBackground::class -> Remix.System.refresh_fill
        Fact.AppBecomeForeground::class -> Remix.System.star_fill
        Fact.WifiStatusChanged::class -> Remix.Device.wifi_fill
        Fact.BTStatusChanged::class -> Remix.Device.bluetooth_fill
        Fact.LocationStatusChanged::class -> Remix.Device.gps_fill
        Fact.NFCStatusChanged::class -> Remix.Device.nfc_fill
        Fact.MobileDataStatusChanged::class -> Remix.Arrows.arrow_up_down_fill
        Fact.HotSpotStatusChanged::class -> Remix.Device.hotspot_fill
        Fact.DarkModeStatusChanged::class -> Remix.Weather.sun_fill
        Fact.FlashLightStatusChanged::class -> Remix.Others.lightbulb_fill
        Fact.NotificationPosted::class -> Remix.System.notification_badge_fill
        Fact.NotificationUpdated::class -> Remix.System.notification_badge_fill
        Fact.NotificationRemoved::class -> Remix.System.notification_badge_fill
        Fact.TaskRemoved::class -> Remix.System.checkbox_multiple_blank_fill
        Fact.TaskRemovedAny::class -> Remix.System.checkbox_multiple_blank_fill

        else -> {
            Remix.Document.file_unknow_fill
        }
    }
}


fun colorForFact(fact: KClass<out Fact>): Color {
    val index =
        runCatching { allFactClasses.indexOf(fact) % FactColorPalettes.size }.getOrElse { 0 }
    return FactColorPalettes[index]
}

val FactColorPalettes = ActionColorPalette
