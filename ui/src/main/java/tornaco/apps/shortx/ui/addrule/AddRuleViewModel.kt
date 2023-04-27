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
import tornaco.apps.shortx.core.proto.rule.RuleHook
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.addrule.action.ActionContentStateHandler
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.action.model.toProtoAction
import tornaco.apps.shortx.ui.addrule.condition.ConditionContentStateHandler
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

fun defaultNewRuleId() = "rule-" + UUID.randomUUID().toString()

data class AddRuleState(
    val id: String = defaultNewRuleId(),
    val title: String = "",
    val description: String = "",
    val fact: Fact? = null,
    val isEditRule: Boolean = false,
    val createTime: Long = System.currentTimeMillis(),
    val ruleChanged: Boolean = false,
    val isEnabled: Boolean = false,
    val showConditionSection: Boolean = true,
    val showAdvancedOptions: Boolean = false,
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class AddRuleViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<AddRuleState>(context, AddRuleState()) {

    private val _ruleSaved = MutableSharedFlow<String>()
    val ruleSaved = _ruleSaved.asSharedFlow()

    val actionContentStateHandler = ActionContentStateHandler()

    // Hooks
    val actionOnEnabledStateHandler = ActionContentStateHandler()
    val actionOnDisabledStateHandler = ActionContentStateHandler()
    val actionOnDeletedStateHandler = ActionContentStateHandler()

    val conditionContentStateHandler = ConditionContentStateHandler()

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

    fun setShowAdvancedOptions(show: Boolean) {
        updateState { copy(showAdvancedOptions = show) }
    }

    fun save() {
        viewModelScope.launch {
            val fact = state.value.fact ?: return@launch

            val conditions = conditionContentStateHandler.state.value.conditions
            val condOp = conditionContentStateHandler.state.value.conditionOp

            val actions = actionContentStateHandler.actions

            // Hooks
            val actionsOnEnabled = actionOnEnabledStateHandler.actions
            val actionsOnDisabled = actionOnDisabledStateHandler.actions
            val actionsOnDeleted = actionOnDeletedStateHandler.actions

            val protoFact = fact.toProtoFact().pack_()
            val protoConditions = conditions.map {
                it.toProtoCondition().pack_()
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
                .setCondOp(condOp.toConditionOperator())
                .apply {
                    if (condOp is Op.MVEL) {
                        condOpPayload =
                            ConditionOperatorPayload
                                .newBuilder()
                                .setExpression(condOp.expression)
                                .build()
                    }
                }
                .addAllActions(actions.map {
                    it.toProtoAction().pack_()
                })
                .setHook(
                    RuleHook.newBuilder()
                        .addAllActionsOnEnabled(actionsOnEnabled.map {
                            it.toProtoAction().pack_()
                        })
                        .addAllActionsOnDisabled(actionsOnDisabled.map {
                            it.toProtoAction().pack_()
                        })
                        .addAllActionsOnDeleted(actionsOnDeleted.map {
                            it.toProtoAction().pack_()
                        })
                        .build()
                )
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
                    initFromRule(rule)
                }
            }
        }
    }

    fun initFromRule(rule: Rule?) {
        viewModelScope.launch {
            rule?.let { r ->
                logger.d("initFromRule: $r")

                actionContentStateHandler.init(
                    actions = r.actionsList.mapNotNull {
                        it.toAction(
                            shortXManager
                        )
                    })

                r.hook?.let { hook ->
                    actionOnEnabledStateHandler.init(
                        actions = hook.actionsOnEnabledList.mapNotNull {
                            it.toAction(
                                shortXManager
                            )
                        })

                    actionOnDisabledStateHandler.init(
                        actions = hook.actionsOnDisabledList.mapNotNull {
                            it.toAction(
                                shortXManager
                            )
                        })

                    actionOnDeletedStateHandler.init(
                        actions = hook.actionsOnDeletedList.mapNotNull {
                            it.toAction(
                                shortXManager
                            )
                        })
                }


                conditionContentStateHandler.init(
                    conditions = r.conditionsList.mapNotNull {
                        it.toCondition(
                            shortXManager
                        )
                    },
                    conditionOp = r.condOp.toOp(r.condOpPayload)
                )

                val hasAnyHookActions: Boolean =
                    actionOnEnabledStateHandler.actions.isNotEmpty()
                            || actionOnDisabledStateHandler.actions.isNotEmpty()
                            || actionOnDeletedStateHandler.actions.isNotEmpty()
                logger.d("hasAnyHookActions: $hasAnyHookActions")

                updateState {
                    copy(
                        id = r.id,
                        title = r.title,
                        description = r.description,
                        fact = r.fact.toFact(shortXManager),
                        isEditRule = true,
                        createTime = r.createTime,
                        isEnabled = r.isEnabled,
                        showAdvancedOptions = hasAnyHookActions
                    )
                }
            }
        }
    }
}