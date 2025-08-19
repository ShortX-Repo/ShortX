package tornaco.apps.shortx.ui.addrule.condition.op

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import tornaco.apps.shortx.core.proto.condition.ConditionOperator
import tornaco.apps.shortx.core.proto.condition.ConditionOperatorPayload

sealed interface Op : Parcelable {
    @Parcelize
    data object All : Op

    @Parcelize
    data object Any : Op

    @Parcelize
    data object None : Op

    @Parcelize
    data class MVEL(val expression: String) : Op
}

fun Op.toConditionOperator(): ConditionOperator {
    return when (this) {
        Op.All -> ConditionOperator.ALL
        Op.Any -> ConditionOperator.ANY
        Op.None -> ConditionOperator.NONE
        is Op.MVEL -> ConditionOperator.MVEL
    }
}

fun ConditionOperator.toOp(payload: ConditionOperatorPayload?): Op {
    return when (this) {
        ConditionOperator.ALL -> Op.All
        ConditionOperator.ANY -> Op.Any
        ConditionOperator.NONE -> Op.None
        ConditionOperator.MVEL -> Op.MVEL(payload?.expression ?: "false")
        ConditionOperator.UNRECOGNIZED -> Op.All
    }
}