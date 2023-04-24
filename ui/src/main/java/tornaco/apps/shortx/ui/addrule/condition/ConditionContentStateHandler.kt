package tornaco.apps.shortx.ui.addrule.condition

import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.addrule.ConditionHandler
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.base.I18N
import tornaco.apps.shortx.ui.base.WithState
import tornaco.apps.shortx.ui.base.WithStateImpl
import tornaco.apps.shortx.ui.base.i18NIns

data class ConditionContentState(
    val conditions: List<Condition> = listOf(Condition.TRUE()),
    val conditionOp: Op = Op.All,
    val isChanged: Boolean = false,
    val conditionWarningMessages: Map<String, List<String>> = emptyMap(),
)

fun ConditionContentStateHandler.asConditionHandler(): ConditionHandler {
    return ConditionHandler(
        addCondition = this::addCondition,
        removeCondition = this::removeCondition,
        setConditionOp = this::setConditionOp
    )
}

class ConditionContentStateHandler(
    private val withState: WithStateImpl<ConditionContentState> = WithStateImpl(
        ConditionContentState()
    )
) : WithState<ConditionContentState> by withState {
    private val shortXManager = tornaco.apps.shortx.core.shortXManager
    private val logger = Logger("ConditionContentStateHandler")
    private val i18N = i18NIns

    fun addCondition(condition: Condition) {
        updateState {
            copy(conditions = conditions.toMutableList().apply {
                add(condition)
            }, isChanged = true)
        }
        checkUpdateWarnings()
    }

    fun removeCondition(condition: Condition) {
        updateState {
            copy(conditions = conditions.toMutableList().apply {
                remove(condition)
            }, isChanged = true)
        }
        checkUpdateWarnings()
    }

    fun setConditionOp(op: Op) {
        if (op != state.value.conditionOp) {
            updateState {
                copy(
                    isChanged = true,
                    conditionOp = op
                )
            }
        }
    }

    fun init(
        conditions: List<Condition>,
        conditionOp: Op
    ) {
        updateState { copy(conditions = conditions, conditionOp = conditionOp) }
        checkUpdateWarnings()
    }

    private fun checkUpdateWarnings() {
        val conditionMessages = state.value.conditions.groupBy { it.id }.mapValues {
            it.value.generateWarningMessages(shortXManager, i18N)
        }
        updateState {
            copy(conditionWarningMessages = conditionMessages)
        }
    }
}

fun List<Condition>.getGlobalVarRefs(): List<String> {
    return filterIsInstance(Condition.EvaluateGlobalVar::class.java).map { it.varId }
}

fun List<Condition>.generateWarningMessages(
    shortXManager: ShortXManager,
    i18N: I18N
): List<String> {
    val conditionMessages = getGlobalVarRefs().filter { shortXManager.getGlobalVarById(it) == null }
        .map { shortXManager.getCachedGlobalVarName(it) ?: "N/A" }
        .mapIndexed { index, it ->
            "${index + 1}. " + i18N["ui.condition.warning.message.global.var.missing", mapOf("name" to it)]
        }
    return conditionMessages
}