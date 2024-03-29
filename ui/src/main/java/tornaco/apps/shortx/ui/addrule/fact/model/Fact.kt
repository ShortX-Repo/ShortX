package tornaco.apps.shortx.ui.addrule.fact.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import tornaco.apps.shortx.core.alarm.FixedSetting
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.AppComponent
import tornaco.apps.shortx.core.proto.common.CallState
import tornaco.apps.shortx.core.proto.common.CustomContextDataKey
import tornaco.apps.shortx.core.proto.common.KeyGesture
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.common.ProcessName
import tornaco.apps.shortx.core.proto.common.RegexMatchOptions
import tornaco.apps.shortx.core.proto.common.ScreenRotateDegree
import tornaco.apps.shortx.core.proto.common.StatusBarTile
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.proto.common.TimeOfADay
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.proto.fact.Notification
import tornaco.apps.shortx.core.proto.fact.RepeatDays
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.ui.addrule.ContextData
import tornaco.apps.shortx.ui.addrule.factContextData
import java.util.UUID

fun defaultNewFactId() = "F-" + UUID.randomUUID().toString()

sealed interface Fact : Parcelable {
    val id: String
    val isDisabled: Boolean
    val note: String
    val tag: String
    val customContextDataKey: CustomContextDataKey

    fun clone(
        id: String,
        note: String,
        tag: String,
        isDisabled: Boolean,
        customContextDataKey: CustomContextDataKey
    ): Fact

    @Parcelize
    data class AnyFact(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId()
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class KeyEvent(
        val keyCode: Int,
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId()
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class AdvancedKeyEvent(
        val keyCode: Int,
        val gesture: KeyGesture,
        val isInterceptMode: Boolean,
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId()
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class AnyActivityStarted(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId()
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class ActivityStarted(
        val components: List<AppComponent>,
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class ActivityStopped(
        val components: List<AppComponent>,
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class EdgeGesture(
        val edge: Edge,
        val gesture: Gesture,
        val isIntercept: Boolean,
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class ScreenOn(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class ScreenOff(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class UserPresent(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class UserPresentAtTheFirstTime(
        override val note: String = "", override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class SystemReady(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class Alarm(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val timeOfADay: TimeOfADay,
        val repeatDays: RepeatDays
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class RandomInPeriod(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val start: TimeOfADay,
        val end: TimeOfADay,
        val repeatDays: RepeatDays
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class FixedInPeriod(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
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
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class AppBecomeBackground(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class AppBecomeForeground(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class AppAdded(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class AppRemoved(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class AppUpdated(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class WifiStatusChanged(
        val state: OnOffAny,
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class WifiConnectedTo(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val ssidList: List<String>
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class ConnectedWifiSignalLevelChanged(
        override val note: String = "", override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),

        val level: Int,
        val rssi: Int
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class WifiDisconnectedFrom(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val ssidList: List<String>
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class BTStatusChanged(
        val state: OnOffAny,
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class BTConnectedTo(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val devices: List<String>
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class BTDisconnectedFrom(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val devices: List<String>
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class MobileDataStatusChanged(
        val state: OnOffAny,
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class LocationStatusChanged(
        val state: OnOffAny,
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class NFCStatusChanged(
        val state: OnOffAny,
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class NFCTagDiscover(
        val uid: com.google.protobuf.ByteString,
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class DNDStatusChanged(
        val state: OnOffAny,
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }


    // Fact class
    @Parcelize
    data class APMStatusChanged(
        val state: OnOffAny,
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class HotSpotStatusChanged(
        val state: OnOffAny,
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class FlashLightStatusChanged(
        val state: OnOffAny,
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class DarkModeStatusChanged(
        val state: OnOffAny,
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // For match purpose.
    // TODO: Should move to common.

    // Fact class
    @Parcelize
    data class Notification(
        val titleRegex: String,
        val titleMatchOptions: RegexMatchOptions,

        val contentRegex: String,
        val contentMatchOptions: RegexMatchOptions,

        val tag: String,

        val apps: List<App>,
        val pkgSets: List<PkgSet>,
    ) : Parcelable {
        fun toProtoNotification(): tornaco.apps.shortx.core.proto.fact.Notification {
            return tornaco.apps.shortx.core.proto.fact.Notification.newBuilder()
                .addAllApps(apps.map { it.pkg })
                .addAllPkgSets(pkgSets.map { it.label })
                .setTitle(titleRegex)
                .setContentText(contentRegex)
                .setTitleRegexOptions(titleMatchOptions)
                .setContentRegexOptions(contentMatchOptions)
                .setTag(tag)
                .build()
        }
    }

    // Fact class
    @Parcelize
    data class NotificationPosted(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val n: Notification
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class NotificationRemoved(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val n: Notification
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class NotificationUpdated(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val n: Notification
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class AppProcessStarted(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val processNames: List<ProcessName>
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class TaskRemoved(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList(),
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class TaskRemovedAny(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {

        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class PkgStopRunning(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList(),
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class PkgStopRunningAny(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class AudioFocusGain(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList(),
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class AudioFocusLost(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList(),
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class AudioFocusChanged(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class BatteryLevelChanged(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val level: Int
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class BatteryTemperatureChanged(
        override val note: String = "", override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val temp: Float,
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class ChargerPlug(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class ChargerUnplug(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class Broadcast(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val actions: List<String>
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class VPNConnected(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class VPNDisconnected(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class MediaStoreInsert(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),

        val filterPathRegex: String,
        val matchOptions: RegexMatchOptions
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class OnStartOp(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),

        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList(),
        val codes: List<Int>
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class OnFinishOp(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),

        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList(),
        val codes: List<Int>
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class OnQSTileClick(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val tile: StatusBarTile
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class SystemSettingsChanged(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val urlAndValueRegex: StringPair,
        val options: RegexMatchOptions
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class CallStateChanged(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),

        val callState: CallState,
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class ClipboardContentChanged(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),

        val content: String,
        val options: RegexMatchOptions,
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class GlobalVarChanged(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),

        val gvId: String,
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class HeadsetPlug(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),

        val isPlug: Boolean,
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class HasFoundNodeOnScreen(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),
        val packageName: String,
        val componentName: String,
        val detectTimeout: Long,
        val matchers: List<NodeMatcher>
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class ScreenRotate(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),

        val degree: ScreenRotateDegree
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class WindowRotationChange(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),

        val degree: ScreenRotateDegree
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class IMEVisibilityChange(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),

        val isShown: Boolean
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class AppGainWindowFocus(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),

        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class AppLostWindowFocus(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),

        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // Fact class
    @Parcelize
    data class DeepLinkCall(
        override val note: String = "",
        override val isDisabled: Boolean = false,
        override val tag: String = "",
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewFactId(),

        val deepLinkTag: String
    ) : Fact {
        override fun clone(
            id: String,
            note: String,
            tag: String,
            isDisabled: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Fact {
            return copy(
                id = id,
                note = note,
                tag = tag,
                isDisabled = isDisabled,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // END SEAL CLASS
}

sealed interface NodeMatcher : Parcelable {
    val displayLabel: String
    val displayMessage: String

    @Parcelize
    data class Text(val text: String, val isRegex: Boolean, val regexOptions: RegexMatchOptions) :
        NodeMatcher {
        override val displayLabel: String
            get() = "ui.fact.has.found.node.on.screen.view.text"
        override val displayMessage: String
            get() = this.text
    }

    @Parcelize
    data class ViewId(val viewId: String) : NodeMatcher {
        override val displayLabel: String
            get() = "ui.fact.has.found.node.on.screen.view.id"
        override val displayMessage: String
            get() = this.viewId
    }
}

val Fact.contextData: List<ContextData> get() = this::class.factContextData()