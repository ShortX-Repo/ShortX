package tornaco.apps.shortx.ui.addrule.condition.op

import tornaco.apps.shortx.core.proto.condition.ConditionOperator

sealed interface Op {
    object All : Op
    object Any : Op
    object None : Op
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