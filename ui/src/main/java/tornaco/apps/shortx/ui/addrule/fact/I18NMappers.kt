package tornaco.apps.shortx.ui.addrule.fact

import android.view.KeyEvent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import tornaco.apps.shortx.core.alarm.isEveryDay
import tornaco.apps.shortx.core.alarm.isNo
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.fact.TimeOfADay
import tornaco.apps.shortx.core.proto.fact.WeekDay
import tornaco.apps.shortx.core.toComponentName
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.labelLinesFromAppsAndPkgSets
import tornaco.apps.shortx.ui.base.I18N
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.PREFIX_APP_ICON
import java.util.*
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
            "${fact.edge}",
            "${fact.gesture}"
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

fun imageVectorForFact(fact: KClass<out Fact>): ImageVector {
    return when (fact) {
        Fact.ActivityStarted::class -> Icons.Outlined.Update
        Fact.AnyActivityStarted::class -> Icons.Outlined.Update
        Fact.EdgeGesture::class -> Icons.Outlined.Gesture
        Fact.KeyEvent::class -> Icons.Outlined.Keyboard
        Fact.ScreenOff::class -> Icons.Outlined.Lock
        Fact.ScreenOn::class -> Icons.Outlined.Smartphone
        Fact.UserPresent::class -> Icons.Outlined.LockOpen
        Fact.Alarm::class -> Icons.Outlined.Alarm
        Fact.AppBecomeBackground::class -> Icons.Outlined.FiberDvr
        Fact.AppBecomeForeground::class -> Icons.Outlined.Aod
        Fact.WifiStatusChanged::class -> Icons.Outlined.Wifi
        Fact.BTStatusChanged::class -> Icons.Outlined.Bluetooth
        Fact.LocationStatusChanged::class -> Icons.Outlined.LocationOn
        Fact.NFCStatusChanged::class -> Icons.Outlined.Nfc
        Fact.MobileDataStatusChanged::class -> Icons.Outlined.MobiledataOff
        Fact.HotSpotStatusChanged::class -> Icons.Outlined.WifiTethering
        Fact.DarkModeStatusChanged::class -> Icons.Outlined.DarkMode
        Fact.FlashLightStatusChanged::class -> Icons.Outlined.FlashlightOn
        Fact.NotificationPosted::class -> Icons.Outlined.Notifications
        Fact.NotificationUpdated::class -> Icons.Outlined.Notifications
        Fact.NotificationRemoved::class -> Icons.Outlined.Notifications

        else -> {
            Icons.Filled.Warning
        }
    }
}
