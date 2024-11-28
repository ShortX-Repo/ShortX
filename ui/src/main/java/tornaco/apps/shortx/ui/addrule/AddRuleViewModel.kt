package tornaco.apps.shortx.ui.addrule

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.IActionEvaluateListener
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.action.SendSMS
import tornaco.apps.shortx.core.proto.common.ConflictStrategy
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.proto.condition.ConditionOperatorPayload
import tornaco.apps.shortx.core.proto.containsAnyTypeUrl
import tornaco.apps.shortx.core.proto.func.FuncParameter
import tornaco.apps.shortx.core.proto.rule.AsyncMode
import tornaco.apps.shortx.core.proto.rule.Quit_EvaluatePolicy
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.rule.RuleHook
import tornaco.apps.shortx.core.proto.rule.RuleQuit
import tornaco.apps.shortx.core.proto.toStringPair
import tornaco.apps.shortx.core.rule.Security
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.rule.parseShareContentRule
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.core.util.maskAll
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
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.ShortXContextViewModelWithEffect
import tornaco.apps.shortx.ui.settings.pref.Pref
import java.net.URL
import java.util.UUID
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
    val showGeeksOptions: Boolean = false,
    val isQuitEnabled: Boolean = false,
    val quitEvaluatePolicy: Quit_EvaluatePolicy = Quit_EvaluatePolicy.Quit_EvaluatePolicy_Always,
    val asyncMode: AsyncMode = AsyncMode.AsyncMode_Async,
    val conflictStrategy: ConflictStrategy = ConflictStrategy.ConflictStrategy_ExecuteBoth,
    val ruleSetId: String = "",
    val evaluatingActions: List<String> = emptyList(),
    // Geeks options
    val instanceIdGen: RuleInstanceIdGen = RuleInstanceIdGen.RuleInstanceIdGenDefault,
    // For online.
    val onlineDALoadState: LoadState<Unit> = LoadState.Success(Unit),
    val securityCheckRes: Boolean = true,
    val hasSMSAction: Boolean = false,
    // Param
    val parameters: List<FuncParameter> = emptyList()
)

sealed interface AddRuleEffect {
    data class RuleSaved(val ruleId: String) : AddRuleEffect
    data class AddParamFailNameExists(val name: String) : AddRuleEffect
}


@SuppressLint("StaticFieldLeak")
@HiltViewModel
class AddRuleViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModelWithEffect<AddRuleState, AddRuleEffect>(context, { AddRuleState() }) {

    val pref by lazy { Pref(context) }

    val actionContentStateHandler = ActionContentStateHandler(
        parameterInputsProvider = {
            state.value.parameters
        }
    )

    // Hooks
    val actionOnEnabledStateHandler = ActionContentStateHandler(
        parameterInputsProvider = {
            state.value.parameters
        }
    )
    val actionOnDisabledStateHandler = ActionContentStateHandler(
        parameterInputsProvider = {
            state.value.parameters
        }
    )
    val actionOnDeletedStateHandler = ActionContentStateHandler(
        parameterInputsProvider = {
            state.value.parameters
        }
    )

    val conditionContentStateHandler = ConditionContentStateHandler()

    val factContentStateHandler = FactContentStateHandler()

    val quitFactContentStateHandler = FactContentStateHandler()
    val quitConditionContentStateHandler = ConditionContentStateHandler()
    val quitActionStateHandler = ActionContentStateHandler(
        parameterInputsProvider = {
            state.value.parameters
        }
    )

    private val actionEvaluateListener = object : IActionEvaluateListener.Stub() {
        override fun onStart(id: String) {
            logger.d("actionEvaluateListener#onStart: $id")
            updateState { copy(evaluatingActions = shortXManager.getEvaluatingActions()) }
        }

        override fun onComplete(id: String) {
            logger.d("actionEvaluateListener#onComplete: $id")
            updateState { copy(evaluatingActions = shortXManager.getEvaluatingActions()) }
        }
    }

    init {
        shortXManager.registerActionEvaluateListener(actionEvaluateListener)
    }

    override fun onCleared() {
        super.onCleared()
        shortXManager.unregisterActionEvaluateListener(actionEvaluateListener)
    }

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

    fun setShowGeeksOptions(show: Boolean) {
        updateState { copy(showGeeksOptions = show) }
    }

    fun setInsGen(gen: RuleInstanceIdGen) {
        updateState { copy(showGeeksOptions = true, instanceIdGen = gen, ruleChanged = true) }
    }

    fun setAsyncMode(asyncMode: AsyncMode) {
        updateState { copy(asyncMode = asyncMode, ruleChanged = true) }
    }

    fun setConflictStrategy(conflictStrategy: ConflictStrategy) {
        updateState { copy(conflictStrategy = conflictStrategy, ruleChanged = true) }
    }

    // Save with token to check subscribe state.
    fun save(token: String?) {
        logger.logOnDebug("save with token: ${token?.maskAll()}")
        viewModelScope.launch {
            val authorPref = pref.shareAuthorInfo.first()

            val conditions = conditionContentStateHandler.conditions
            val condOp = conditionContentStateHandler.conditionOp

            val actions = actionContentStateHandler.actions
            val actionAsyncMode = actionContentStateHandler.state.value.asyncMode

            // Hooks
            val actionsOnEnabled = actionOnEnabledStateHandler.actions
            val actionsOnDisabled = actionOnDisabledStateHandler.actions
            val actionsOnDeleted = actionOnDeletedStateHandler.actions
            val actionsOnEnabledAsyncMode = actionOnEnabledStateHandler.state.value.asyncMode
            val actionsOnDisabledAsyncMode = actionOnDisabledStateHandler.state.value.asyncMode
            val actionsOnDeletedAsyncMode = actionOnDeletedStateHandler.state.value.asyncMode

            val facts = factContentStateHandler.facts

            // Quits
            val quitFacts = quitFactContentStateHandler.facts
            val quitConditions = quitConditionContentStateHandler.conditions
            val quitCondOp = quitConditionContentStateHandler.conditionOp
            val quitActions = quitActionStateHandler.actions
            val quitActionsAsyncMode = quitActionStateHandler.state.value.asyncMode

            val ruleInsGenProtoOrNull = state.value.instanceIdGen.toProto()

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
                .setActionAsyncMode(actionAsyncMode)
                .setHook(
                    RuleHook.newBuilder()
                        .addAllActionsOnEnabled(actionsOnEnabled.map {
                            it.toProtoAction().pack_()
                        })
                        .setActionsOnEnabledAsyncMode(actionsOnEnabledAsyncMode)
                        .addAllActionsOnDisabled(actionsOnDisabled.map {
                            it.toProtoAction().pack_()
                        })
                        .setActionsOnDisabledAsyncMode(actionsOnDisabledAsyncMode)
                        .addAllActionsOnDeleted(actionsOnDeleted.map {
                            it.toProtoAction().pack_()
                        })
                        .setActionsOnDeletedAsyncMode(actionsOnDeletedAsyncMode)
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
                        .addAllActions(quitActions.map { it.toProtoAction().pack_() })
                        .setActionAsyncMode(quitActionsAsyncMode)
                        .setIsEnabled(state.value.isQuitEnabled)
                        .setEvaluatePolicy(state.value.quitEvaluatePolicy)
                        .build()
                )
                .setIsEnabled(state.value.isEnabled)
                .setAsyncMode(state.value.asyncMode)
                .setConflictPolicy(state.value.conflictStrategy)
                .setAuthor(authorPref)
                .setVersionCode(state.value.versionCode)
                .addAllExtras(mutableListOf<StringPair>().apply {
                    if (!token.isNullOrEmpty()) {
                        add((BuildPropInternal.APP_PACKAGE_NAME to token).toStringPair())
                    }
                })
                .setRuleSetId(state.value.ruleSetId)
                .apply {
                    if (ruleInsGenProtoOrNull == null) {
                        clearRuleInstanceIdGenerator()
                    } else {
                        setRuleInstanceIdGenerator(ruleInsGenProtoOrNull)
                    }
                }
                .addAllParameters(state.value.parameters)
                .build()

            shortXManager.addRule(rule)
            postSideEffect(AddRuleEffect.RuleSaved(rule.id))
        }
    }

    fun delete(ruleId: String) {
        viewModelScope.launch {
            shortXManager.deleteRule(ruleId)
        }
    }

    fun initRuleSetIdIfNonNull(ruleSetId: String?) {
        ruleSetId?.let {
            updateState { copy(ruleSetId = it) }
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
                    },
                    actionAsyncMode = r.actionAsyncMode
                )

                r.hook?.let { hook ->
                    actionOnEnabledStateHandler.init(
                        actions = hook.actionsOnEnabledList.mapNotNull {
                            it.toAction(
                                shortXManager
                            )
                        },
                        actionAsyncMode = hook.actionsOnEnabledAsyncMode
                    )

                    actionOnDisabledStateHandler.init(
                        actions = hook.actionsOnDisabledList.mapNotNull {
                            it.toAction(
                                shortXManager
                            )
                        },
                        actionAsyncMode = hook.actionsOnDisabledAsyncMode
                    )

                    actionOnDeletedStateHandler.init(
                        actions = hook.actionsOnDeletedList.mapNotNull {
                            it.toAction(
                                shortXManager
                            )
                        },
                        actionAsyncMode = hook.actionsOnDeletedAsyncMode
                    )
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
                    conditionOp = r.quit.condOp.toOp(r.quit.condOpPayload)
                )
                quitActionStateHandler.init(
                    actions = r.quit.actionsList.mapNotNull {
                        it.toAction(
                            shortXManager
                        )
                    },
                    actionAsyncMode = r.quit.actionAsyncMode
                )

                val hasAnyHookActions: Boolean =
                    actionOnEnabledStateHandler.actions.isNotEmpty()
                            || actionOnDisabledStateHandler.actions.isNotEmpty()
                            || actionOnDeletedStateHandler.actions.isNotEmpty()
                            || quitFactContentStateHandler.facts.isNotEmpty()
                            || quitConditionContentStateHandler.conditions.isNotEmpty()
                logger.d("hasAnyHookActions: $hasAnyHookActions")

                val smsTyprUrls = listOf(
                    SendSMS.getDefaultInstance().pack_().typeUrl,
                )
                val hasAnySMSAction = rule.containsAnyTypeUrl(smsTyprUrls)

                val hasAnyGeeksOptions: Boolean = r.hasRuleInstanceIdGenerator()
                logger.d("hasAnyGeeksOptions: $hasAnyGeeksOptions")

                val evaluatingActions = shortXManager.getEvaluatingActions()

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
                        showGeeksOptions = hasAnyGeeksOptions,
                        instanceIdGen = r.ruleInstanceIdGenerator.toRuleInstanceIdGen(),
                        isQuitEnabled = r.quit.isEnabled,
                        quitEvaluatePolicy = r.quit.evaluatePolicy.takeIf { it != Quit_EvaluatePolicy.UNRECOGNIZED }
                            ?: Quit_EvaluatePolicy.Quit_EvaluatePolicy_Always,
                        asyncMode = r.asyncMode,
                        conflictStrategy = r.conflictPolicy,
                        evaluatingActions = evaluatingActions,
                        ruleSetId = r.ruleSetId,
                        securityCheckRes = Security.checkSecurity(r),
                        hasSMSAction = hasAnySMSAction,
                        parameters = r.parametersList,
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
                    if (BuildPropInternal.APP_IS_DEBUG) {
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

    fun setQuitEvaluatePolicy(quitEvaluatePolicy: Quit_EvaluatePolicy) {
        updateState { copy(quitEvaluatePolicy = quitEvaluatePolicy, ruleChanged = true) }
    }

    fun addParameter(parameter: FuncParameter, override: Boolean) {
        if (!override && state.value.parameters.any { it.name.lowercase() == parameter.name.lowercase() }) {
            postSideEffect(AddRuleEffect.AddParamFailNameExists(parameter.name))
            return
        }
        updateState {
            copy(parameters = parameters.toMutableList().apply {
                val index = indexOfFirst { it.name == parameter.name }
                if (index >= 0) {
                    set(index, parameter)
                } else {
                    add(parameter)
                }
            }, ruleChanged = true)
        }
    }

    fun removeParameter(parameter: FuncParameter) {
        updateState {
            copy(parameters = parameters.toMutableList().apply {
                removeIf { it.name == parameter.name }
            }, ruleChanged = true)
        }
    }
}