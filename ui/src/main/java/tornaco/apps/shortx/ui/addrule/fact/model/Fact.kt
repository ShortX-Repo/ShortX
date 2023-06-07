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
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.ui.addrule.ContextData
import tornaco.apps.shortx.ui.addrule.factContextData
import java.util.UUID

fun defaultNewFactId() = "F-" + UUID.randomUUID().toString()

sealed interface Fact {
    val id: String
    val note: String

    fun clone(id: String, note: String): Fact

    data class KeyEvent(
        val keyCode: Int,
        override val note: String = "",
        override val id: String = defaultNewFactId()
    ) : Fact {
        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class AnyActivityStarted(
        override val note: String = "",
        override val id: String = defaultNewFactId()
    ) : Fact {
        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class ActivityStarted(
        val components: List<AppComponent>,
        override val note: String = "",
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class EdgeGesture(
        val edge: Edge, val gesture: Gesture,
        override val note: String = "",
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class ScreenOn(
        override val note: String = "",
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class ScreenOff(
        override val note: String = "",
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class UserPresent(
        override val note: String = "",
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class SystemReady(
        override val note: String = "",
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class Alarm(
        override val note: String = "",
        override val id: String = defaultNewFactId(),
        val tag: String = "Alarm-${DateUtils.formatForFileName(System.currentTimeMillis())}",
        val timeOfADay: TimeOfADay,
        val repeatDays: RepeatDays
    ) : Fact {
        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class AppBecomeBackground(
        override val note: String = "",
        override val id: String = defaultNewFactId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Fact {
        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class AppBecomeForeground(
        override val note: String = "",
        override val id: String = defaultNewFactId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Fact {
        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class AppAdded(
        override val note: String = "",
        override val id: String = defaultNewFactId(),
    ) : Fact {
        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class AppRemoved(
        override val note: String = "",
        override val id: String = defaultNewFactId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Fact {
        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class AppUpdated(
        override val note: String = "",
        override val id: String = defaultNewFactId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Fact {
        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class WifiStatusChanged(
        val state: OnOffAny, override val note: String = "",
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class BTStatusChanged(
        val state: OnOffAny, override val note: String = "",
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class MobileDataStatusChanged(
        val state: OnOffAny, override val note: String = "",
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class LocationStatusChanged(
        val state: OnOffAny, override val note: String = "",
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class NFCStatusChanged(
        val state: OnOffAny, override val note: String = "",
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class DNDStatusChanged(
        val state: OnOffAny, override val note: String = "",
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class HotSpotStatusChanged(
        val state: OnOffAny, override val note: String = "",
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class FlashLightStatusChanged(
        val state: OnOffAny,
        override val note: String = "",
        override val id: String = defaultNewFactId(),
    ) : Fact {
        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class DarkModeStatusChanged(
        val state: OnOffAny,
        override val note: String = "",
        override val id: String = defaultNewFactId(),
    ) : Fact {
        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class Notification(
        val apps: List<App>,
        val pkgSets: List<PkgSet> = emptyList(),
        val titleRegex: String? = null,
        val contentRegex: String? = null
    )

    data class NotificationPosted(
        override val note: String = "",
        override val id: String = defaultNewFactId(),
        val n: Notification
    ) : Fact {
        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class NotificationRemoved(
        override val note: String = "",
        override val id: String = defaultNewFactId(),
        val n: Notification
    ) : Fact {
        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class NotificationUpdated(
        override val note: String = "",
        override val id: String = defaultNewFactId(),
        val n: Notification
    ) : Fact {
        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class TaskRemoved(
        override val note: String = "",
        override val id: String = defaultNewFactId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList(),
    ) : Fact {
        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class TaskRemovedAny(
        override val note: String = "",
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class PkgStopRunning(
        override val note: String = "",
        override val id: String = defaultNewFactId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList(),
    ) : Fact {
        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class PkgStopRunningAny(
        override val note: String = "",
        override val id: String = defaultNewFactId(),
    ) : Fact {
        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class AudioFocusGain(
        override val note: String = "",
        override val id: String = defaultNewFactId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList(),
    ) : Fact {
        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class AudioFocusLost(
        override val note: String = "",
        override val id: String = defaultNewFactId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList(),
    ) : Fact {
        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }

    data class AudioFocusChanged(
        override val note: String = "",
        override val id: String = defaultNewFactId(),
    ) : Fact {
        override fun clone(id: String, note: String): Fact {
            return copy(id = id, note = note)
        }
    }
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
    Fact.SystemReady::class,

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

    Fact.AppAdded::class,
    Fact.AppRemoved::class,
    Fact.AppUpdated::class,

    Fact.PkgStopRunning::class,
    Fact.PkgStopRunningAny::class,

    Fact.AudioFocusGain::class,
    Fact.AudioFocusLost::class,
    Fact.AudioFocusChanged::class,
)