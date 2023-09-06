package tornaco.apps.shortx.ui.main.rule

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.IJobStatusListener
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.toJson
import tornaco.apps.shortx.core.proto.toStringPair
import tornaco.apps.shortx.core.rule.RuleObserver
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.action.model.toProtoAction
import tornaco.apps.shortx.ui.addrule.condition.model.defaultNewConditionId
import tornaco.apps.shortx.ui.addrule.condition.model.toCondition
import tornaco.apps.shortx.ui.addrule.condition.model.toProtoCondition
import tornaco.apps.shortx.ui.addrule.defaultNewRuleId
import tornaco.apps.shortx.ui.addrule.fact.model.defaultNewFactId
import tornaco.apps.shortx.ui.addrule.fact.model.toFact
import tornaco.apps.shortx.ui.addrule.fact.model.toProtoFact
import tornaco.apps.shortx.ui.base.ContextViewModel
import tornaco.apps.shortx.ui.main.RuleSortBy
import tornaco.apps.shortx.ui.main.model.RuleUM
import tornaco.apps.shortx.ui.settings.ResetEventRegistry
import tornaco.apps.shortx.ui.settings.RestoreEventRegistry
import tornaco.apps.shortx.ui.share.SharedData
import tornaco.apps.shortx.ui.share.addShareContentHeader
import java.util.*

data class RuleListState(
    val isShowRuleDescription: Boolean = true,
    val rules: List<RuleUM> = emptyList(),
    val ruleSortBy: RuleSortBy = RuleSortBy.CreateTime,
    val isLoading: Boolean = true,
)

@SuppressLint("StaticFieldLeak")
abstract class BaseRuleListViewModel(context: Context) :
    ContextViewModel<RuleListState>(context, { RuleListState() }) {
    private var loadRuleJob: Job? = null

    private val jobStatusListener = object : IJobStatusListener.Stub() {
        override fun onJobStarted(id: String?) {
            logger.d("onJobStarted: $id")
            delayLoadRule()
        }

        override fun onJobCompleted(id: String?) {
            logger.d("onJobCompleted: $id")
            delayLoadRule()
        }

        override fun onJobCancelled(id: String?) {
            logger.d("onJobCancelled: $id")
            delayLoadRule()
        }
    }

    private val ruleObs = object : RuleObserver() {
        override fun onRuleChanged(id: String) {
            super.onRuleChanged(id)
            logger.d("ruleObs, onRuleChanged: $id")
            loadRuleList()
        }

        override fun onRuleSetChanged(id: String) {
            super.onRuleSetChanged(id)
            logger.d("ruleObs, onRuleSetChanged: $id")
            loadRuleList()
        }
    }

    init {
        shortXManager.registerJobStatusListener(jobStatusListener)
        shortXManager.registerRuleObs(ruleObs)

        viewModelScope.launch {
            ResetEventRegistry.dataResetComplete.collect {
                logger.d("reload data since data has been reset")
                loadRuleList()
            }
        }

        viewModelScope.launch {
            RestoreEventRegistry.dataRestoreComplete.collect {
                logger.d("reload data since data restored")
                loadRuleList()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        shortXManager.unregisterJobStatusListener(jobStatusListener)
        shortXManager.unregisterRuleObs(ruleObs)
    }

    abstract fun loadRuleList(delay: Boolean = false)

    fun setRuleSortBy(ruleSortBy: RuleSortBy) {
        updateState { copy(ruleSortBy = ruleSortBy) }
        loadRuleList(false)
    }

    private fun delayLoadRule(delay: Long = 200L) {
        loadRuleJob?.cancel()
        loadRuleJob = viewModelScope.launch {
            delay(delay)
            loadRuleList()
        }
    }

    fun setRuleEnabled(ruleId: String, enable: Boolean) {
        viewModelScope.launch {
            shortXManager.setRuleEnabled(ruleId, enable)
        }
    }


    fun deleteRule(ruleId: String) {
        viewModelScope.launch {
            shortXManager.deleteRule(ruleId)
        }
    }

    fun dupRule(ruleId: String, token: String?) {
        logger.d("dupRule: $ruleId")
        viewModelScope.launch {
            shortXManager.getRuleById(ruleId)?.let { importRule(it, true, token) }
        }
    }

    fun stopJobs(rule: RuleUM) {
        viewModelScope.launch {
            shortXManager.cancelJobs(listOf(rule.id))
        }
    }

    fun ruleToShareString(rule: RuleUM): String? =
        shortXManager.getRuleById(rule.id)?.toJson()
            ?.addShareContentHeader(mapOf("type" to SharedData.TYPE_RULE))

    fun ruleSetToShareString(rule: RuleUM): String? =
        shortXManager.getRuleSetById(id = rule.id, withRules = true)?.toJson()
            ?.addShareContentHeader(mapOf("type" to SharedData.TYPE_RULE_SET))

    fun importRule(rule: Rule, shouldChangeId: Boolean, token: String?) {
        logger.d("importRule: $rule")
        val ruleWithSameNameExists =
            state.value.rules.firstOrNull { it.title == rule.title } != null
        val copyInd = " (Copy)"

        viewModelScope.launch {
            val refinedActions = rule.actionsList.mapNotNull {
                it.toAction(shortXManager)?.toProtoAction(defaultNewActionId())?.pack_()
            }
            val refinedFacts = rule.factsList.mapNotNull {
                it.toFact(shortXManager)?.toProtoFact(defaultNewFactId())?.pack_()
            }
            val refinedConds = rule.conditionsList.mapNotNull {
                it.toCondition(shortXManager)?.toProtoCondition(defaultNewConditionId())?.pack_()
            }

            val refined =
                Rule.newBuilder(rule)
                    .setTitle(if (ruleWithSameNameExists) "${rule.title} $copyInd" else rule.title)
                    .apply {
                        if (shouldChangeId) {
                            id = defaultNewRuleId()
                        }
                    }
                    .clearActions()
                    .addAllActions(refinedActions)
                    .clearFacts()
                    .addAllFacts(refinedFacts)
                    .clearConditions()
                    .addAllConditions(refinedConds)
                    .setCreateTime(rule.createTime)
                    .setLastUpdateTime(rule.lastUpdateTime)
                    .setVersionCode(rule.versionCode)
                    .setIsEnabled(false)
                    .addAllExtras(mutableListOf<StringPair>().apply {
                        if (!token.isNullOrEmpty()) {
                            add((BuildProp.APP_PACKAGE_NAME to token).toStringPair())
                        }
                    })
                    .build()
            shortXManager.addRule(refined)
        }
    }

    fun directExecuteRuleActions(ruleUM: RuleUM) {
        logger.d("directExecuteRuleActions: ${ruleUM.title}")
        viewModelScope.launch {
            val evaluateContext = EvaluateContext.newBuilder()
                .setSrcId(ruleUM.id)
                .setSrcEvent(ContextSrcEvent.ContextSrc_ExecuteRuleActions)
                .setSrcTitle(ruleUM.title)
                .build()
            shortXManager.directExecuteRuleActions(evaluateContext, ruleUM.id)
        }
    }

    fun moveToSet(rule: RuleUM, id: String, token: String?) {
        shortXManager.addRule(
            Rule.newBuilder(rule.rule).setRuleSetId(id)
                .addAllExtras(mutableListOf<StringPair>().apply {
                    if (!token.isNullOrEmpty()) {
                        add((BuildProp.APP_PACKAGE_NAME to token).toStringPair())
                    }
                })
                .build()
        )
    }

    fun moveOutSet(rule: Rule, token: String?) {
        shortXManager.addRule(
            Rule.newBuilder(rule).setRuleSetId("")
                .addAllExtras(mutableListOf<StringPair>().apply {
                    if (!token.isNullOrEmpty()) {
                        add((BuildProp.APP_PACKAGE_NAME to token).toStringPair())
                    }
                })
                .build()
        )
    }
}