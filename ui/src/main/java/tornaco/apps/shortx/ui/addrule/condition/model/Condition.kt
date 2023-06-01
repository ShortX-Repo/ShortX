package tornaco.apps.shortx.ui.addrule.condition.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.IntOp
import tornaco.apps.shortx.core.proto.condition.GlobalVarOp
import tornaco.apps.shortx.core.proto.condition.GlobalVarOpPayload
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import java.util.UUID

fun defaultNewConditionId() = "C-" + UUID.randomUUID().toString()

sealed interface Condition : Parcelable {
    val id: String
    val note: String

    @Parcelize
    data class CurrentForegroundApp(
        val apps: List<App>, val pkgSets: List<PkgSet>,
        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition

    @Parcelize
    data class MVEL(
        val expression: String,
        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition

    @Parcelize
    data class TRUE(
        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition

    @Parcelize
    data class EvaluateGlobalVar(
        val varName: String,
        val op: GlobalVarOp,
        val payload: GlobalVarOpPayload,

        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition

    @Parcelize
    data class BatteryPercent(
        val value: Int,
        val op: IntOp,

        override val note: String = "",
        override val id: String = defaultNewConditionId()
    ) : Condition
}

val allConditionClasses = listOf(
    Condition.CurrentForegroundApp::class,
    Condition.MVEL::class,
    Condition.TRUE::class,
    Condition.EvaluateGlobalVar::class,
    Condition.BatteryPercent::class,
)
