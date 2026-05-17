package tornaco.apps.shortx.ui.main.rule

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.IJobStatusListener
import tornaco.apps.shortx.core.proto.common.ConflictStrategy
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.rule.RuleSet
import tornaco.apps.shortx.core.proto.toJson
import tornaco.apps.shortx.core.proto.toStringPair
import tornaco.apps.shortx.core.rule.RuleObserver
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.ui.addda.defaultNewDAId
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
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.LoadState.Loading.data
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import tornaco.apps.shortx.ui.main.CommonSetFilter
import tornaco.apps.shortx.ui.main.RuleSortBy
import tornaco.apps.shortx.ui.main.model.RuleUM
import tornaco.apps.shortx.ui.settings.ResetEventRegistry
import tornaco.apps.shortx.ui.settings.RestoreEventRegistry
import tornaco.apps.shortx.ui.settings.pref.Pref
import tornaco.apps.shortx.ui.share.SharedData
import tornaco.apps.shortx.ui.share.addShareContentHeader
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import java.util.UUID

fun defaultNewRuleSetId() = "RS-${UUID.randomUUID()}"

data class RuleListState(
    val isShowRuleDescription: Boolean = true,
    val ruleSortBy: RuleSortBy = RuleSortBy.CreateTime,
    val showDescription: Boolean = true,
    val onlyShowEnabled: Boolean = true,
    val searchQuery: String = "",
)

data class RuleList(
    val rules: List<RuleUM> = emptyList(),
    val ruleSetFilters: List<CommonSetFilter> = emptyList(),
    val selectedRuleSetFilter: CommonSetFilter = CommonSetFilter.PinnedAll,
)

@SuppressLint("StaticFieldLeak")
abstract class BaseRuleListViewModel(context: Context) :
    ShortXContextViewModel<RuleListState>(context, { RuleListState() }) {
    val pref by lazy { Pref(context) }

    private val loadActiveJobRequest: MutableStateFlow<Long> =
        MutableStateFlow(System.currentTimeMillis())
    protected val loadRuleRequest: MutableStateFlow<Long> =
        MutableStateFlow(System.currentTimeMillis())

    abstract val ruleList: StateFlow<LoadState<RuleList>>

    @OptIn(FlowPreview::class)
    val activeJobIds: StateFlow<List<String>> by lazy {
        loadActiveJobRequest
            .sample(300).map {
                shortXManager.getActiveJobs().apply {
                    logger.d("activeJobs: $this")
                }
            }.stateIn(viewModelScope, SharingStarted.Lazily, initialValue = emptyList())
    }

    private val jobStatusListener = object : IJobStatusListener.Stub() {
        override fun onJobStarted(id: String?) {
            logger.d("onJobStarted: $id")
            requestLoadActiveJobs()
        }

        override fun onJobCompleted(id: String?) {
            logger.d("onJobCompleted: $id")
            requestLoadActiveJobs()
        }

        override fun onJobCancelled(id: String?) {
            logger.d("onJobCancelled: $id")
            requestLoadActiveJobs()
        }
    }

    private val ruleObs = object : RuleObserver() {
        override fun onRuleChanged(id: String) {
            super.onRuleChanged(id)
            logger.d("ruleObs, onRuleChanged: $id")
            requestLoadRuleList("onRuleChanged")

            if (id.isEmpty()) {
                LVLStateHolder.fab()
            }
        }

        override fun onRuleSetChanged(id: String) {
            super.onRuleSetChanged(id)
            logger.d("ruleObs, onRuleSetChanged: $id")
            requestLoadRuleList("onRuleSetChanged")
        }
    }

    init {
        shortXManager.registerJobStatusListener(jobStatusListener)
        shortXManager.registerRuleObs(ruleObs)

        viewModelScope.launch {
            ResetEventRegistry.dataResetComplete.collect {
                logger.d("reload data since data has been reset")
                requestLoadRuleList("dataResetComplete")
            }
        }

        viewModelScope.launch {
            RestoreEventRegistry.dataRestoreComplete.collect {
                logger.d("reload data since data restored")
                requestLoadRuleList("dataRestoreComplete")
            }
        }

        viewModelScope.launch {
            pref.uiOnlyShowEnabled.collectLatest {
                updateState { copy(onlyShowEnabled = it) }
            }
        }

        viewModelScope.launch {
            pref.uiShowRuleDescription.collectLatest {
                updateState { copy(showDescription = it) }
            }
        }

        viewModelScope.launch {
            pref.ruleSortBy.collectLatest { sortByName ->
                val sortBy = RuleSortBy.values().find { it.name == sortByName } ?: RuleSortBy.CreateTime
                logger.d("ruleSortBy changed: $sortByName -> $sortBy")
                updateState { copy(ruleSortBy = sortBy) }
                requestLoadRuleList("sortByChanged")
            }
        }

        requestLoadActiveJobs()
    }

    override fun onCleared() {
        super.onCleared()
        shortXManager.unregisterJobStatusListener(jobStatusListener)
        shortXManager.unregisterRuleObs(ruleObs)
    }

    fun requestLoadRuleList(reason: String) {
        logger.d("requestLoadRuleList- $reason")
        loadRuleRequest.update { System.currentTimeMillis() }
    }

    fun requestLoadActiveJobs() {
        loadActiveJobRequest.update { System.currentTimeMillis() }
    }

    fun setRuleSortBy(ruleSortBy: RuleSortBy) {
        updateState { copy(ruleSortBy = ruleSortBy) }
        viewModelScope.launch {
            pref.setRuleSortBy(ruleSortBy.name)
        }
        requestLoadRuleList("setRuleSortBy")
    }

    fun setRuleEnabled(ruleId: String, enable: Boolean) {
        viewModelScope.launch {
            shortXManager.setRuleEnabled(ruleId, enable)
        }
    }

    fun extractAsDA(title: String, rule: RuleUM, token: String?) {
        viewModelScope.launch {
            shortXManager.getRuleById(rule.id)?.let {
                val da = DirectAction.newBuilder()
                    .setId(defaultNewDAId())
                    .setTitle(title)
                    .addAllActions(it.actionsList)
                    .setConflictPolicy(ConflictStrategy.ConflictStrategy_ExecuteBoth)
                    .setCreateTime(System.currentTimeMillis())
                    .setLastUpdateTime(System.currentTimeMillis())
                    .setVersionCode(1)
                    .addAllExtras(mutableListOf<StringPair>().apply {
                        if (!token.isNullOrEmpty()) {
                            add((BuildPropInternal.APP_PACKAGE_NAME to token).toStringPair())
                        }
                    })
                    .build()

                shortXManager.addDirectAction(da)
            }
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
        shortXManager.getRuleById(rule.id)?.let {
            Rule.newBuilder(it)
                .setId("SHARE-" + defaultNewRuleId())
                .build()
        }?.toJson()
            ?.addShareContentHeader(mapOf("type" to SharedData.TYPE_RULE))

    fun ruleSetToShareString(rule: RuleUM): String? =
        shortXManager.getRuleSetById(id = rule.id, withRules = true)?.let {
            RuleSet.newBuilder(it)
                .setId("SHARE-" + defaultNewRuleSetId())
                .build()
        }?.toJson()
            ?.addShareContentHeader(mapOf("type" to SharedData.TYPE_RULE_SET))

    fun rulesToShareString(title: String, rules: List<RuleUM>): String? =
        RuleSet.newBuilder()
            .setId("SHARE-" + defaultNewRuleSetId())
            .setTitle(title)
            .setDescription(title)
            .setRuleList(
                tornaco.apps.shortx.core.proto.rule.RuleList.newBuilder()
                    .addAllRules(rules.map { it.rule })
                    .build()
            )
            .build()
            .toJson()
            .addShareContentHeader(mapOf("type" to SharedData.TYPE_RULE_SET))

    fun importRule(rule: Rule, shouldChangeId: Boolean, token: String?) {
        logger.d("importRule: $rule")
        val ruleWithSameNameExists =
            ruleList.value.data?.rules?.firstOrNull { it.title == rule.title } != null
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
                            add((BuildPropInternal.APP_PACKAGE_NAME to token).toStringPair())
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
        // Should load new Rule here, because it maybe changed after Rule list is loaded.
        val originalRule = shortXManager.getRuleById(rule.id) ?: return
        shortXManager.addRule(
            Rule.newBuilder(originalRule).setRuleSetId(id)
                .addAllExtras(mutableListOf<StringPair>().apply {
                    if (!token.isNullOrEmpty()) {
                        add((BuildPropInternal.APP_PACKAGE_NAME to token).toStringPair())
                    }
                })
                .build()
        )
    }

    fun moveOutSet(rule: Rule, token: String?) {
        // Should load new Rule here, because it maybe changed after Rule list is loaded.
        val originalRule = shortXManager.getRuleById(rule.id) ?: return
        shortXManager.addRule(
            Rule.newBuilder(originalRule).setRuleSetId("")
                .addAllExtras(mutableListOf<StringPair>().apply {
                    if (!token.isNullOrEmpty()) {
                        add((BuildPropInternal.APP_PACKAGE_NAME to token).toStringPair())
                    }
                })
                .build()
        )
    }

    fun updateSearchQuery(query: String) {
        viewModelScope.launch {
            updateState { copy(searchQuery = query) }
        }
    }

    fun toggleShowRuleDescription() {
        viewModelScope.launch {
            pref.setShowRuleDescription(!state.value.showDescription)
        }
    }

    fun toggleOnlyShowEnabled() {
        viewModelScope.launch {
            val newState = !state.value.onlyShowEnabled
            pref.onlyShowEnabled(newState)
            updateState { copy(onlyShowEnabled = newState) }

            if (newState) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, i18N["ui.rule.only.show.enabled"], Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    fun pinRule(rule: RuleUM) {
        viewModelScope.launch {
            pref.pinRule(rule.id)
            requestLoadRuleList("pinRule")
        }
    }

    fun unpinRule(rule: RuleUM) {
        viewModelScope.launch {
            pref.unpinRule(rule.id)
            requestLoadRuleList("unpinRule")
        }
    }

    fun togglePin(rule: RuleUM) {
        if (rule.isPinned) {
            unpinRule(rule)
        } else {
            pinRule(rule)
        }
    }
}
