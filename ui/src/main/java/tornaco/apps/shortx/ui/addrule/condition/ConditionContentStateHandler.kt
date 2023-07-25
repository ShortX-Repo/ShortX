package tornaco.apps.shortx.ui.addrule.condition

import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.UiClipboard
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.model.defaultNewConditionId
import tornaco.apps.shortx.ui.addrule.condition.model.toCondition
import tornaco.apps.shortx.ui.addrule.condition.model.toProtoCondition
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.base.WithState
import tornaco.apps.shortx.ui.base.WithStateImpl
import tornaco.apps.shortx.ui.base.i18NIns

data class ConditionContentState(
    val conditions: List<Condition> = emptyList(),
    val conditionOp: Op = Op.All,
    val isChanged: Boolean = false,
    val conditionWarningMessages: Map<String, List<String>> = emptyMap(),
)

fun ConditionContentStateHandler.asConditionHandler(): ConditionHandler {
    return ConditionHandler(
        addCondition = this::addCondition,
        removeCondition = this::removeCondition,
        setConditionOp = this::setConditionOp,
        copy = { conditionId ->
            UiClipboard.copy(UiClipboard.ClipboardContent.ConditionContent(
                conditions.first { it.id == conditionId }
            ))
        },
        insertAbove = { condition, anchorId ->
            insertConditionAbove(condition, anchorId)
        },
        insertBelow = { condition, anchorId ->
            insertConditionBelow(condition, anchorId)
        },
        enableDisable = {

        },
        invert = { conditionId ->
            val currentCondition = conditions.first { it.id == conditionId }
            addCondition(
                currentCondition.clone(
                    id = currentCondition.id,
                    note = currentCondition.note,
                    isInvert = !currentCondition.isInvert
                )
            )
        }
    )
}

class ConditionContentStateHandler(
    private val withState: WithStateImpl<ConditionContentState> = WithStateImpl { ConditionContentState() }
) : WithState<ConditionContentState> by withState {
    private val shortXManager = tornaco.apps.shortx.core.shortXManager
    private val logger = Logger("ConditionContentStateHandler")
    private val i18N = i18NIns

    val conditions get() = state.value.conditions
    val conditionOp get() = state.value.conditionOp

    fun addCondition(condition: Condition) {
        updateState {
            copy(conditions = conditions.toMutableList().apply {
                val existingIndex = indexOfFirst { it.id == condition.id }
                if (existingIndex >= 0) {
                    set(existingIndex, condition)
                } else {
                    add(condition)
                }
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

    fun insertConditionAbove(condition: Condition, anchorId: String) {
        val refinedCondition = requireNotNull(
            condition.toProtoCondition(overrideId = defaultNewConditionId()).pack_()
                .toCondition(shortXManager)
        ) {
            "refinedCondition is null"
        }
        updateState {
            copy(conditions = conditions.toMutableList().apply {
                val existingIndex = indexOfFirst { it.id == anchorId }
                if (existingIndex >= 0) {
                    add(existingIndex, refinedCondition)
                } else {
                    add(refinedCondition)
                }
            }, isChanged = true)
        }
        checkUpdateWarnings()
    }

    fun insertConditionBelow(action: Condition, anchorId: String) {
        val refinedCondition = requireNotNull(
            action.toProtoCondition(overrideId = defaultNewConditionId()).pack_()
                .toCondition(shortXManager)
        ) {
            "refinedCondition is null"
        }
        updateState {
            copy(conditions = conditions.toMutableList().apply {
                val existingIndex = indexOfFirst { it.id == anchorId }
                if (existingIndex >= 0) {
                    add(existingIndex + 1, refinedCondition)
                } else {
                    add(refinedCondition)
                }
            }, isChanged = true)
        }
        checkUpdateWarnings()
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
    return filterIsInstance(Condition.EvaluateGlobalVar::class.java).map { it.varName }
}

fun List<Condition>.generateWarningMessages(
    shortXManager: ShortXManager,
    i18N: I18N
): List<String> {
    return emptyList()
}