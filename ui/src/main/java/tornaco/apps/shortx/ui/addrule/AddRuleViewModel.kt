package tornaco.apps.shortx.ui.addrule

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.proto.common.ConflictStrategy
import tornaco.apps.shortx.core.proto.condition.ConditionOperatorPayload
import tornaco.apps.shortx.core.proto.rule.AsyncMode
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.rule.RuleHook
import tornaco.apps.shortx.core.proto.rule.RuleQuit
import tornaco.apps.shortx.core.rule.Security
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.rule.parseShareContentRule
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
import tornaco.apps.shortx.ui.addrule.fact.FactContentStateHandler
import tornaco.apps.shortx.ui.addrule.fact.model.toFact
import tornaco.apps.shortx.ui.addrule.fact.model.toProtoFact
import tornaco.apps.shortx.ui.base.ContextViewModel
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.settings.pref.Pref
import java.net.URL
import java.util.*
import javax.inject.Inject

fun defaultNewRuleId() = "rule-" + UUID.randomUUID().toString()

data class AddRuleState(
    val id: String = defaultNewRuleId(),
    val title: String = "",
    val description: String = "",
    val versionCode: Long = 1L,
    val isEditRule: Boolean = false,
    val createTime: Long = System.currentTimeMillis(),
    val ruleChanged: Boolean = false,
    val isEnabled: Boolean = false,
    val showAdvancedOptions: Boolean = false,
    val isQuitEnabled: Boolean = false,
    val asyncMode: AsyncMode = AsyncMode.AsyncMode_Async,
    val conflictStrategy: ConflictStrategy = ConflictStrategy.ConflictStrategy_ExecuteBoth,
    // For online.
    val onlineDALoadState: LoadState<Unit> = LoadState.Success(Unit),
    val securityCheckRes: Boolean = true
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class AddRuleViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<AddRuleState>(context, { AddRuleState() }) {

    private val _ruleSaved = MutableSharedFlow<String>()
    val ruleSaved = _ruleSaved.asSharedFlow()

    val pref by lazy { Pref(context) }

    val actionContentStateHandler = ActionContentStateHandler()

    // Hooks
    val actionOnEnabledStateHandler = ActionContentStateHandler()
    val actionOnDisabledStateHandler = ActionContentStateHandler()
    val actionOnDeletedStateHandler = ActionContentStateHandler()

    val conditionContentStateHandler = ConditionContentStateHandler()

    val factContentStateHandler = FactContentStateHandler()

    val quitFactContentStateHandler = FactContentStateHandler()
    val quitConditionContentStateHandler = ConditionContentStateHandler()

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

    fun setShowAdvancedOptions(show: Boolean) {
        updateState { copy(showAdvancedOptions = show) }
    }

    fun setAsyncMode(asyncMode: AsyncMode) {
        updateState { copy(asyncMode = asyncMode, ruleChanged = true) }
    }

    fun setConflictStrategy(conflictStrategy: ConflictStrategy) {
        updateState { copy(conflictStrategy = conflictStrategy, ruleChanged = true) }
    }

    fun save() {
        viewModelScope.launch {
            val authorPref = pref.shareAuthorInfo.first()

            val conditions = conditionContentStateHandler.conditions
            val condOp = conditionContentStateHandler.conditionOp

            val actions = actionContentStateHandler.actions

            // Hooks
            val actionsOnEnabled = actionOnEnabledStateHandler.actions
            val actionsOnDisabled = actionOnDisabledStateHandler.actions
            val actionsOnDeleted = actionOnDeletedStateHandler.actions

            val facts = factContentStateHandler.facts

            // Quits
            val quitFacts = quitFactContentStateHandler.facts
            val quitConditions = quitConditionContentStateHandler.conditions
            val quitCondOp = quitConditionContentStateHandler.conditionOp

            val rule = Rule.newBuilder()
                .setId(state.value.id)
                .setCreateTime(state.value.createTime)
                .setLastUpdateTime(System.currentTimeMillis())
                .addAllFacts(facts.map { it.toProtoFact().pack_() })
                .setTitle(
                    state.value.title.fallbackOnEmpty(
                        DateUtils.formatHHMMSS(
                            System.currentTimeMillis()
                        )
                    )
                )
                .setDescription(state.value.description.fallbackOnEmpty(""))
                .addAllConditions(conditions.map {
                    it.toProtoCondition().pack_()
                })
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
                .setQuit(
                    RuleQuit.newBuilder()
                        .addAllFacts(quitFacts.map { it.toProtoFact().pack_() })
                        .addAllConditions(quitConditions.map {
                            it.toProtoCondition().pack_()
                        })
                        .setCondOp(quitCondOp.toConditionOperator())
                        .apply {
                            if (quitCondOp is Op.MVEL) {
                                condOpPayload =
                                    ConditionOperatorPayload
                                        .newBuilder()
                                        .setExpression(quitCondOp.expression)
                                        .build()
                            }
                        }
                        .setIsEnabled(state.value.isQuitEnabled)
                        .build()
                )
                .setIsEnabled(state.value.isEnabled)
                .setAsyncMode(state.value.asyncMode)
                .setConflictPolicy(state.value.conflictStrategy)
                .setAuthor(authorPref)
                .setVersionCode(state.value.versionCode)
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

                factContentStateHandler.init(
                    facts = r.factsList.mapNotNull {
                        it.toFact(shortXManager)
                    }
                )

                quitFactContentStateHandler.init(
                    facts = r.quit.factsList.mapNotNull {
                        it.toFact(shortXManager)
                    }
                )
                quitConditionContentStateHandler.init(
                    conditions = r.quit.conditionsList.mapNotNull {
                        it.toCondition(
                            shortXManager
                        )
                    },
                    conditionOp = r.quit.condOp.toOp(r.condOpPayload)
                )

                val hasAnyHookActions: Boolean =
                    actionOnEnabledStateHandler.actions.isNotEmpty()
                            || actionOnDisabledStateHandler.actions.isNotEmpty()
                            || actionOnDeletedStateHandler.actions.isNotEmpty()
                            || quitFactContentStateHandler.facts.isNotEmpty()
                            || quitConditionContentStateHandler.conditions.isNotEmpty()
                logger.d("hasAnyHookActions: $hasAnyHookActions")

                updateState {
                    copy(
                        id = r.id,
                        title = r.title,
                        description = r.description,
                        versionCode = r.versionCode,
                        isEditRule = true,
                        createTime = r.createTime,
                        isEnabled = r.isEnabled,
                        showAdvancedOptions = hasAnyHookActions,
                        isQuitEnabled = r.quit.isEnabled,
                        asyncMode = r.asyncMode,
                        conflictStrategy = r.conflictPolicy,
                        securityCheckRes = Security.checkSecurity(r)
                    )
                }
            }
        }
    }

    fun initFromUrl(url: String?) {
        url?.let { _ ->
            logger.d("initFromUrl: $url")
            viewModelScope.launch {
                updateState { copy(onlineDALoadState = LoadState.Loading) }
                withContext(Dispatchers.IO) {
                    if (BuildProp.APP_IS_DEBUG) {
                        delay(1000)
                    }
                    runCatching {
                        val content = URL(url).readText()
                        val rule = parseShareContentRule(content)
                        updateState {
                            copy(onlineDALoadState = LoadState.Success(Unit))
                        }
                        requireNotNull(rule) { "Fail to parse Rule: \n$content" }
                        initFromRule(rule)
                    }.onFailure {
                        logger.e(it)
                        updateState {
                            copy(onlineDALoadState = LoadState.Error(it))
                        }
                    }
                }
            }
        }
    }

    fun toggleQuitEnable() {
        updateState { copy(isQuitEnabled = !isQuitEnabled, ruleChanged = true) }
    }
}