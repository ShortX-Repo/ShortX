package tornaco.apps.shortx.ui.addrule

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.condition.ConditionOperatorPayload
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.addrule.action.ActionContentStateHandler
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.action.model.toProtoAction
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.model.toCondition
import tornaco.apps.shortx.ui.addrule.condition.model.toProtoCondition
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.addrule.condition.op.toConditionOperator
import tornaco.apps.shortx.ui.addrule.condition.op.toOp
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.model.toFact
import tornaco.apps.shortx.ui.addrule.fact.model.toProtoFact
import tornaco.apps.shortx.ui.base.ContextViewModel
import java.util.*
import javax.inject.Inject

data class AddRuleState(
    val id: String = "rule-" + UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val fact: Fact? = null,
    val conditions: List<Condition> = listOf(Condition.TRUE),
    val conditionOp: Op = Op.All,
    val isEditRule: Boolean = false,
    val createTime: Long = System.currentTimeMillis(),
    val ruleChanged: Boolean = false,
    val isEnabled: Boolean = false,
    val showConditionSection: Boolean = true,
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class AddRuleViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<AddRuleState>(context, AddRuleState()) {

    private val _ruleSaved = MutableSharedFlow<String>()
    val ruleSaved = _ruleSaved.asSharedFlow()

    val actionContentStateHandler = ActionContentStateHandler()

    fun setTitle(title: String) {
        if (title != state.value.title) {
            updateState {
                copy(title = title, ruleChanged = true)
            }
        }
    }

    fun setDescription(description: String) {
        if (description != state.value.description) {
            updateState {
                copy(description = description, ruleChanged = true)
            }
        }
    }

    fun setFact(fact: Fact?) {
        if (fact != state.value.fact) {
            updateState {
                copy(fact = fact, ruleChanged = true)
            }
        }
    }

    fun showEdgeHint(edge: Edge) {
        shortXManager.showEdgeHint(edge)
    }

    fun addCondition(condition: Condition) {
        updateState {
            copy(conditions = conditions.toMutableList().apply {
                add(condition)
            }, ruleChanged = true)
        }
    }

    fun removeCondition(condition: Condition) {
        updateState {
            copy(conditions = conditions.toMutableList().apply {
                remove(condition)
            }, ruleChanged = true)
        }
    }

    fun setConditionOp(op: Op) {
        if (op != state.value.conditionOp) {
            updateState {
                copy(
                    ruleChanged = true,
                    conditionOp = op
                )
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            val fact = state.value.fact ?: return@launch
            val conditions = state.value.conditions
            val actions = actionContentStateHandler.state.value.actions

            val protoFact = fact.toProtoFact().pack_()
            val protoConditions = conditions.map {
                it.toProtoCondition().pack_()
            }
            val protoActions = actions.map {
                it.toProtoAction().pack_()
            }
            val rule = Rule.newBuilder()
                .setId(state.value.id)
                .setCreateTime(state.value.createTime)
                .setLastUpdateTime(System.currentTimeMillis())
                .setFact(protoFact)
                .setTitle(
                    state.value.title.fallbackOnEmpty(
                        DateUtils.formatShortForMessageTime(
                            System.currentTimeMillis()
                        )
                    )
                )
                .setDescription(state.value.description.fallbackOnEmpty(""))
                .addAllConditions(protoConditions)
                .setCondOp(state.value.conditionOp.toConditionOperator())
                .apply {
                    val op = state.value.conditionOp
                    if (op is Op.MVEL) {
                        condOpPayload =
                            ConditionOperatorPayload
                                .newBuilder()
                                .setExpression(op.expression)
                                .build()
                    }
                }
                .addAllActions(protoActions)
                .setIsEnabled(state.value.isEnabled)
                .build()

            shortXManager.addRule(rule)
            _ruleSaved.emit(rule.id)
        }
    }

    fun delete(ruleId: String) {
        viewModelScope.launch {
            shortXManager.deleteRule(ruleId)
        }
    }

    fun initFromRuleId(ruleId: String?) {
        ruleId?.let { id ->
            viewModelScope.launch {
                shortXManager.getRuleById(id)?.let { rule ->
                    logger.d("initFromRule: $rule")

                    actionContentStateHandler.init(actions = rule.actionsList.mapNotNull {
                        it.toAction(
                            shortXManager
                        )
                    })

                    updateState {
                        copy(
                            id = rule.id,
                            title = rule.title,
                            description = rule.description,
                            fact = rule.fact.toFact(shortXManager),
                            conditions = rule.conditionsList.mapNotNull {
                                it.toCondition(
                                    shortXManager
                                )
                            },
                            conditionOp = rule.condOp.toOp(rule.condOpPayload),
                            isEditRule = true,
                            createTime = rule.createTime,
                            isEnabled = rule.isEnabled
                        )
                    }
                }
            }
        }
    }
}