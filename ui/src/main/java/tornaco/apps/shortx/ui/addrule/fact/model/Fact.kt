package tornaco.apps.shortx.ui.addrule.fact.model

import tornaco.apps.shortx.core.alarm.FixedSetting
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.AppComponent
import tornaco.apps.shortx.core.proto.common.CallState
import tornaco.apps.shortx.core.proto.common.CustomContextDataKey
import tornaco.apps.shortx.core.proto.common.KeyGesture
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.common.StatusBarTile
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.proto.common.TimeOfADay
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.proto.fact.RepeatDays
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.ui.addrule.ContextData
import tornaco.apps.shortx.ui.addrule.factContextData
import java.util.UUID

fun defaultNewFactId() = "F-" + UUID.randomUUID().toString()

sealed interface Fact {
    val id: String
    val note: String
    val customContextDataKey: CustomContextDataKey

    fun clone(id: String, note: String, customContextDataKey: CustomContextDataKey): Fact

    data class KeyEvent(
        val keyCode: Int,
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId()
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class AdvancedKeyEvent(
        val keyCode: Int,
        val gesture: KeyGesture,
        val isInterceptMode: Boolean,
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId()
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class AnyActivityStarted(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId()
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class ActivityStarted(
        val components: List<AppComponent>,
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class EdgeGesture(
        val edge: Edge, val gesture: Gesture,
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class ScreenOn(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class ScreenOff(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class UserPresent(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class SystemReady(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class Alarm(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val timeOfADay: TimeOfADay,
        val repeatDays: RepeatDays
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class RandomInPeriod(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val start: TimeOfADay,
        val end: TimeOfADay,
        val repeatDays: RepeatDays
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class FixedInPeriod(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val start: TimeOfADay,
        val end: TimeOfADay,
        val fixedBy: FixedSetting,
        val repeatDays: RepeatDays
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class AppBecomeBackground(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class AppBecomeForeground(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class AppAdded(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class AppRemoved(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class AppUpdated(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class WifiStatusChanged(
        val state: OnOffAny, override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class WifiConnectedTo(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val ssidList: List<String>
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class WifiDisconnectedFrom(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val ssidList: List<String>
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class BTStatusChanged(
        val state: OnOffAny, override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class BTConnectedTo(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val devices: List<String>
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class BTDisconnectedFrom(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val devices: List<String>
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class MobileDataStatusChanged(
        val state: OnOffAny, override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class LocationStatusChanged(
        val state: OnOffAny, override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class NFCStatusChanged(
        val state: OnOffAny, override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class DNDStatusChanged(
        val state: OnOffAny, override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class HotSpotStatusChanged(
        val state: OnOffAny, override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class FlashLightStatusChanged(
        val state: OnOffAny,
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class DarkModeStatusChanged(
        val state: OnOffAny,
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    // For match purpose.
    data class Notification(
        val titleRegex: String,
        val contentRegex: String,
        val tagRegex: String,

        val apps: List<App>,
        val pkgSets: List<PkgSet>,
    )

    data class NotificationPosted(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val n: Notification
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class NotificationRemoved(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val n: Notification
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class NotificationUpdated(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val n: Notification
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class TaskRemoved(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList(),
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class TaskRemovedAny(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class PkgStopRunning(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList(),
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class PkgStopRunningAny(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class AudioFocusGain(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList(),
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class AudioFocusLost(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList(),
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class AudioFocusChanged(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class BatteryLevelChanged(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val level: Int
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class BatteryTemperatureChanged(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val temp: Float,
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class ChargerPlug(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class ChargerUnplug(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class Broadcast(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val actions: List<String>
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class VPNConnected(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class VPNDisconnected(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class MediaStoreInsert(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),

        val filterPathRegex: String
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class OnStartOp(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),

        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList(),
        val codes: List<Int>
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class OnFinishOp(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),

        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList(),
        val codes: List<Int>
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class OnQSTileClick(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val tile: StatusBarTile
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class SystemSettingsChanged(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val urlAndValueRegex: StringPair
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class CallStateChanged(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),

        val callState: CallState,
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class ClipboardContentChanged(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),

        val content: String,
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }

    data class GlobalVarChanged(
        override val note: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),

        val gvId: String,
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(id = id, note = note, customContextDataKey = customContextDataKey)
        }
    }
}

val Fact.contextData: List<ContextData> get() = this::class.factContextData()