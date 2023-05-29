package tornaco.apps.shortx.ui.addrule.fact.model

import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.AppComponent
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.proto.fact.Notification
import tornaco.apps.shortx.core.proto.fact.RepeatDays
import tornaco.apps.shortx.core.proto.fact.TimeOfADay
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.ui.addrule.ContextData
import tornaco.apps.shortx.ui.addrule.factContextData

sealed interface Fact {
    data class KeyEvent(val keyCode: Int) : Fact

    object AnyActivityStarted : Fact
    data class ActivityStarted(val components: List<AppComponent>) : Fact

    data class EdgeGesture(val edge: Edge, val gesture: Gesture) : Fact

    object ScreenOn : Fact
    object ScreenOff : Fact
    object UserPresent : Fact

    data class Alarm(val tag: String, val timeOfADay: TimeOfADay, val repeatDays: RepeatDays) : Fact

    data class AppBecomeBackground(
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Fact

    data class AppBecomeForeground(
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Fact

    data class WifiStatusChanged(val state: OnOffAny) : Fact
    data class BTStatusChanged(val state: OnOffAny) : Fact
    data class MobileDataStatusChanged(val state: OnOffAny) : Fact
    data class LocationStatusChanged(val state: OnOffAny) : Fact
    data class NFCStatusChanged(val state: OnOffAny) : Fact
    data class DNDStatusChanged(val state: OnOffAny) : Fact
    data class HotSpotStatusChanged(val state: OnOffAny) : Fact
    data class FlashLightStatusChanged(val state: OnOffAny) : Fact
    data class DarkModeStatusChanged(val state: OnOffAny) : Fact

    data class Notification(
        val apps: List<App>,
        val pkgSets: List<PkgSet> = emptyList(),
        val titleRegex: String? = null,
        val contentRegex: String? = null
    )

    data class NotificationPosted(
        val n: Notification
    ) : Fact

    data class NotificationRemoved(
        val n: Notification
    ) : Fact

    data class NotificationUpdated(
        val n: Notification
    ) : Fact

    data class TaskRemoved(
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList(),
    ) : Fact

    object TaskRemovedAny : Fact
}

val Fact.contextData: List<ContextData> get() = this::class.factContextData()

val allFactClasses = listOf(
    Fact.KeyEvent::class,
    Fact.AnyActivityStarted::class,
    Fact.ActivityStarted::class,
    Fact.EdgeGesture::class,

    Fact.ScreenOn::class,
    Fact.ScreenOff::class,
    Fact.UserPresent::class,

    Fact.Alarm::class,
    Fact.AppBecomeBackground::class,
    Fact.AppBecomeForeground::class,

    Fact.WifiStatusChanged::class,
    Fact.BTStatusChanged::class,
    Fact.MobileDataStatusChanged::class,
    Fact.LocationStatusChanged::class,
    Fact.NFCStatusChanged::class,
    Fact.DNDStatusChanged::class,
    Fact.HotSpotStatusChanged::class,
    Fact.FlashLightStatusChanged::class,
    Fact.DarkModeStatusChanged::class,

    Fact.NotificationPosted::class,
    Fact.NotificationRemoved::class,
    Fact.NotificationUpdated::class,

    Fact.TaskRemoved::class,
    Fact.TaskRemovedAny::class,
)