package tornaco.apps.shortx.ui.addrule.condition.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.AppComponent
import tornaco.apps.shortx.core.proto.common.IntOp
import tornaco.apps.shortx.core.proto.common.TimeOfADayRange
import tornaco.apps.shortx.core.proto.condition.GlobalVarOp
import tornaco.apps.shortx.core.proto.condition.GlobalVarOpPayload
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import java.util.UUID

fun defaultNewConditionId() = "C-" + UUID.randomUUID().toString()

sealed interface Condition : Parcelable {
    val id: String
    val note: String
    val isInvert: Boolean

    fun clone(id: String, note: String, isInvert: Boolean): Condition

    @Parcelize
    data class CurrentForegroundApp(
        val apps: List<App>,
        val pkgSets: List<PkgSet>,
        val op: Op,
        override val note: String = "",
        override val isInvert: Boolean = false,
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(id: String, note: String, isInvert: Boolean): Condition {
            return copy(id = id, note = note, isInvert = isInvert)
        }
    }

    @Parcelize
    data class AppIsRunning(
        val apps: List<App>,
        val pkgSets: List<PkgSet>,
        val op: Op,
        override val note: String = "",
        override val isInvert: Boolean = false,
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(id: String, note: String, isInvert: Boolean): Condition {
            return copy(id = id, note = note, isInvert = isInvert)
        }
    }

    @Parcelize
    data class ServiceIsRunning(
        val services: List<AppComponent>,
        val op: Op,

        override val note: String = "",
        override val isInvert: Boolean = false,
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(id: String, note: String, isInvert: Boolean): Condition {
            return copy(id = id, note = note, isInvert = isInvert)
        }
    }

    @Parcelize
    data class ScreenIsOn(
        override val note: String = "",
        override val isInvert: Boolean = false,
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(id: String, note: String, isInvert: Boolean): Condition {
            return copy(id = id, note = note, isInvert = isInvert)
        }
    }

    @Parcelize
    data class VPNIsConnected(
        override val note: String = "",
        override val isInvert: Boolean = false,
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(id: String, note: String, isInvert: Boolean): Condition {
            return copy(id = id, note = note, isInvert = isInvert)
        }
    }


    @Parcelize
    data class AppIsNotRunning(
        val apps: List<App>,
        val pkgSets: List<PkgSet>,
        val op: Op,
        override val note: String = "",
        override val isInvert: Boolean = false,
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(id: String, note: String, isInvert: Boolean): Condition {
            return copy(id = id, note = note, isInvert = isInvert)
        }
    }

    @Parcelize
    data class AppHasAudioFocus(
        val apps: List<App>,
        val pkgSets: List<PkgSet>,
        val op: Op,

        override val note: String = "",
        override val isInvert: Boolean = false,
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(id: String, note: String, isInvert: Boolean): Condition {
            return copy(id = id, note = note, isInvert = isInvert)
        }
    }

    @Parcelize
    data class MVEL(
        val expression: String,
        override val isInvert: Boolean = false,
        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(id: String, note: String, isInvert: Boolean): Condition {
            return copy(id = id, note = note, isInvert = isInvert)
        }
    }

    @Parcelize
    data class TRUE(
        override val isInvert: Boolean = false,
        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(id: String, note: String, isInvert: Boolean): Condition {
            return copy(id = id, note = note, isInvert = isInvert)
        }
    }

    @Parcelize
    data class EvaluateGlobalVar(
        val varName: String,
        val op: GlobalVarOp,
        val payload: GlobalVarOpPayload,

        override val isInvert: Boolean = false,
        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(id: String, note: String, isInvert: Boolean): Condition {
            return copy(id = id, note = note, isInvert = isInvert)
        }
    }

    @Parcelize
    data class BatteryPercent(
        val value: Int,
        val op: IntOp,

        override val isInvert: Boolean = false,
        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(id: String, note: String, isInvert: Boolean): Condition {
            return copy(id = id, note = note, isInvert = isInvert)
        }
    }

    @Parcelize
    data class ChargeState(
        val requireIsCharge: Boolean,

        override val isInvert: Boolean = false,
        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(id: String, note: String, isInvert: Boolean): Condition {
            return copy(id = id, note = note, isInvert = isInvert)
        }
    }

    @Parcelize
    data class TimeInRange(
        val range: TimeOfADayRange,

        override val isInvert: Boolean = false,
        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(id: String, note: String, isInvert: Boolean): Condition {
            return copy(id = id, note = note, isInvert = isInvert)
        }
    }

    @Parcelize
    data class RequireWifiConnected(
        val requiredSSID: String = "*",

        override val isInvert: Boolean = false,
        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(id: String, note: String, isInvert: Boolean): Condition {
            return copy(id = id, note = note, isInvert = isInvert)
        }
    }

    @Parcelize
    data class RequireWifiDisconnected(
        override val isInvert: Boolean = false,
        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(id: String, note: String, isInvert: Boolean): Condition {
            return copy(id = id, note = note, isInvert = isInvert)
        }
    }

    @Parcelize
    data class RequireBTConnected(
        val requiredDevice: String = "*",

        override val isInvert: Boolean = false,
        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(id: String, note: String, isInvert: Boolean): Condition {
            return copy(id = id, note = note, isInvert = isInvert)
        }
    }

    @Parcelize
    data class RequireBTDisconnected(
        override val isInvert: Boolean = false,
        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(id: String, note: String, isInvert: Boolean): Condition {
            return copy(id = id, note = note, isInvert = isInvert)
        }
    }
}