package tornaco.apps.shortx.ui.addrule.condition.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.AppComponent
import tornaco.apps.shortx.core.proto.common.CustomContextDataKey
import tornaco.apps.shortx.core.proto.common.IntOp
import tornaco.apps.shortx.core.proto.common.ScreenRotateDegree
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.proto.common.TimeOfADayRange
import tornaco.apps.shortx.core.proto.condition.GlobalVarOp
import tornaco.apps.shortx.core.proto.condition.GlobalVarOpPayload
import tornaco.apps.shortx.core.proto.condition.PlugType
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.ui.addrule.ContextData
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.addrule.conditionContextData
import tornaco.apps.shortx.ui.addrule.fact.model.NodeMatcher
import java.util.UUID

fun defaultNewConditionId() = "C-" + UUID.randomUUID().toString()

sealed interface Condition : Parcelable {
    val id: String
    val note: String
    val isInvert: Boolean
    val customContextDataKey: CustomContextDataKey

    fun clone(
        id: String,
        note: String,
        isInvert: Boolean,
        customContextDataKey: CustomContextDataKey
    ): Condition

    @Parcelize
    data class CurrentForegroundApp(
        val apps: List<App>,
        val pkgSets: List<PkgSet>,
        val op: Op,
        override val note: String = "",
        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class CurrentForegroundAppByPkg(
        val stringPairs: List<StringPair>,
        val op: Op,
        override val note: String = "",
        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class AppHasNotification(
        val apps: List<App>,
        val pkgSets: List<PkgSet>,
        val op: Op,
        override val note: String = "",
        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class AppIsRunning(
        val apps: List<App>,
        val pkgSets: List<PkgSet>,
        val op: Op,
        override val note: String = "",
        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class ServiceIsRunning(
        val services: List<AppComponent>,
        val op: Op,

        override val note: String = "",
        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class ScreenIsOn(
        override val note: String = "",
        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class VPNIsConnected(
        override val note: String = "",
        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }


    @Parcelize
    data class AppIsNotRunning(
        val apps: List<App>,
        val pkgSets: List<PkgSet>,
        val op: Op,
        override val note: String = "",
        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class AppHasAudioFocus(
        val apps: List<App>,
        val pkgSets: List<PkgSet>,
        val op: Op,

        override val note: String = "",
        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class MVEL(
        val expression: String,
        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class TRUE(
        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class EvaluateGlobalVar(
        val varName: String,
        val op: GlobalVarOp,
        val payload: GlobalVarOpPayload,

        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class EvaluateLocalVar(
        val varName: String,
        val op: GlobalVarOp,
        val payload: GlobalVarOpPayload,

        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class BatteryPercent(
        val value: Int,
        val op: IntOp,

        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class ChargeState(
        val requireIsCharge: Boolean,

        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class PlugState(
        val type: PlugType,

        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class TimeInRange(
        val range: TimeOfADayRange,

        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class RequireWifiConnected(
        val requiredSSID: String = "*",

        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class RequireWifiDisconnected(
        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class RequireBTConnected(
        val requiredDevice: String = "*",

        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class RequireBTDisconnected(
        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class RequireMobileDataEnabled(
        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val note: String = "",
        override val id: String = defaultNewConditionId(),

        val slot: Int
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class KeyguardIsLocked(
        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val note: String = "",
        override val id: String = defaultNewConditionId(),
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class ScreenOrientationIsPort(
        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val note: String = "",
        override val id: String = defaultNewConditionId(),
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class IsInCall(
        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val note: String = "",
        override val id: String = defaultNewConditionId(),
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class IsRinging(
        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val note: String = "",
        override val id: String = defaultNewConditionId(),
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }


    @Parcelize
    data class HasNodeOnScreen(
        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val note: String = "",
        override val id: String = defaultNewConditionId(),
        val packageName: String,
        val componentName: String,
        val detectTimeout: Long,
        val matchers: List<NodeMatcher>
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class IsRuleEnabled(
        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val note: String = "",
        override val id: String = defaultNewConditionId(),
        val ruleId: String,
        val isEnabled: Boolean,
        val ruleLabel: String,
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class IsHeadsetPlug(
        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val note: String = "",
        override val id: String = defaultNewConditionId(),
        val isPlug: Boolean
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class RequireScreenRotate(
        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val note: String = "",
        override val id: String = defaultNewConditionId(),
        val degree: ScreenRotateDegree
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class RequireWindowRotation(
        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val note: String = "",
        override val id: String = defaultNewConditionId(),
        val degree: ScreenRotateDegree
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    @Parcelize
    data class RequireDelay(
        override val isInvert: Boolean = false,
        override val customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance(),
        override val note: String = "",
        override val id: String = defaultNewConditionId(),
        val timeString: String
    ) : Condition {
        override fun clone(
            id: String,
            note: String,
            isInvert: Boolean,
            customContextDataKey: CustomContextDataKey
        ): Condition {
            return copy(
                id = id,
                note = note,
                isInvert = isInvert,
                customContextDataKey = customContextDataKey
            )
        }
    }

    // SEALED CLASS END
}

val Condition.contextData: List<ContextData> get() = this::class.conditionContextData()