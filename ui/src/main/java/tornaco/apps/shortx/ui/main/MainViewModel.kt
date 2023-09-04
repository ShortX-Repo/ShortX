package tornaco.apps.shortx.ui.main

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import si.virag.fuzzydateformatter.FuzzyDateTimeFormatter
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.IAudioRecordingListener
import tornaco.apps.shortx.core.IJobStatusListener
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.da.DirectActionSet
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.toJson
import tornaco.apps.shortx.core.proto.toStringPair
import tornaco.apps.shortx.core.rule.IDAObserver
import tornaco.apps.shortx.core.rule.IRuleObserver
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
import tornaco.apps.shortx.ui.base.ContextViewModel
import tornaco.apps.shortx.ui.base.afterNavLongDelay
import tornaco.apps.shortx.ui.main.model.DirectActionUM
import tornaco.apps.shortx.ui.main.model.MainState
import tornaco.apps.shortx.ui.main.model.RuleUM
import tornaco.apps.shortx.ui.main.model.daToDirectActionUM
import tornaco.apps.shortx.ui.main.model.ruleToRuleUM
import tornaco.apps.shortx.ui.main.rule.sort
import tornaco.apps.shortx.ui.settings.ResetEventRegistry
import tornaco.apps.shortx.ui.settings.RestoreEventRegistry
import tornaco.apps.shortx.ui.settings.pref.Pref
import tornaco.apps.shortx.ui.share.addShareContentHeader
import java.text.Collator
import java.util.*
import javax.inject.Inject

sealed interface ImportResult {
    object Success : ImportResult
    data class Fail(val reason: FailReason) : ImportResult

    enum class FailReason(val description: String) {
        ParseFail("Unable to parse content")
    }
}

@SuppressLint("StaticFieldLeak")
@HiltViewModel
open class MainViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<MainState>(context, { MainState() }) {
    private val pref by lazy { Pref(context) }
    private var loadDAJob: Job? = null
    private var loadRuleJob: Job? = null

    private val jobStatusListener = object : IJobStatusListener.Stub() {
        override fun onJobStarted(id: String?) {
            logger.d("onJobStarted: $id")
            delayLoadDA()
            delayLoadRule()
        }

        override fun onJobCompleted(id: String?) {
            logger.d("onJobCompleted: $id")
            delayLoadDA()
            delayLoadRule()
        }

        override fun onJobCancelled(id: String?) {
            logger.d("onJobCancelled: $id")
            delayLoadDA()
            delayLoadRule()
        }
    }

    private val ruleObs = object : IRuleObserver.Stub() {
        override fun onAddOrUpdate(id: String?) {
            logger.d("ruleObs, onAddOrUpdate: $id")
            loadRules()
        }

        override fun onDelete(id: String?) {
            logger.d("ruleObs, onDelete: $id")
            loadRules()
        }
    }
    private val daObs = object : IDAObserver.Stub() {
        override fun onAddOrUpdate(id: String?) {
            logger.d("daObs, onAddOrUpdate: $id")
            loadDA()
        }

        override fun onDelete(id: String?) {
            logger.d("daObs, onDelete: $id")
            loadDA()
        }
    }

    private val audioRecordingObs = object : IAudioRecordingListener.Stub() {
        override fun onStartRecording() {
            logger.d("audioRecordingObs, onStartRecording")
            updateState { copy(isAudioRecording = shortXManager.isAudioRecording()) }
        }

        override fun onStopRecording() {
            logger.d("audioRecordingObs, onStopRecording")
            updateState { copy(isAudioRecording = shortXManager.isAudioRecording()) }
        }
    }

    init {
        updateState {
            copy(
                isActivated = shortXManager.isInstalled,
                isFrameworkUpdated = shortXManager.isFrameworkUpdated,
                isAudioRecording = shortXManager.isAudioRecording()
            )
        }

        shortXManager.registerJobStatusListener(jobStatusListener)
        shortXManager.registerRuleObs(ruleObs)
        shortXManager.registerDAObs(daObs)
        shortXManager.registerAudioRecordingObs(audioRecordingObs)

        viewModelScope.launch {
            pref.privacyStatementAcceptedFlow.collectLatest {
                logger.d("isPrivacyStatementAccepted changed: $it")
                updateState { copy(isPrivacyStatementAccepted = it) }
            }
        }

        viewModelScope.launch {
            pref.uiShowDetailedDAItemContent.collectLatest {
                logger.d("uiShowDetailedDAItemContent changed: $it")
                updateState { copy(isShowDetailedDAItem = it) }
            }
        }

        viewModelScope.launch {
            pref.ignoreFrameworkUpdate.collectLatest {
                logger.d("ignoreFrameworkUpdate changed: $it")
                updateState { copy(isIgnoreFrameworkUpdate = it) }
            }
        }

        viewModelScope.launch {
            ResetEventRegistry.dataResetComplete.collect {
                logger.d("reload data since data has been reset")
                loadRules()
                loadDA()
            }
        }

        viewModelScope.launch {
            RestoreEventRegistry.dataRestoreComplete.collect {
                logger.d("reload data since data restored")
                loadRules()
                loadDA()
            }
        }

        viewModelScope.launch {
            pref.hideSubscribeInfoFlow.collectLatest {
                updateState { copy(isHideSubscribeInfo = it) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        shortXManager.unregisterJobStatusListener(jobStatusListener)
        shortXManager.unregisterRuleObs(ruleObs)
        shortXManager.unregisterDAObs(daObs)
        shortXManager.unregisterAudioRecordingObs(audioRecordingObs)
    }

    fun loadRules(delay: Boolean = false) {
        logger.d("loadRules")
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            if (delay) afterNavLongDelay()
            withContext(Dispatchers.IO) {
                val activeJobs = shortXManager.getActiveJobs().apply {
                    logger.d("activeJobs: $this")
                }
                val rules = shortXManager.getAllRules()
                    .let(sort(state.value.ruleSortBy))
                    .mapNotNull { rule ->
                        runCatching {
                            ruleToRuleUM(rule, activeJobs)
                        }.getOrElse {
                            logger.e(it, "ruleToRuleUM")
                            null
                        }
                    }
                updateState {
                    copy(rules = rules, isLoading = false)
                }
            }
        }
    }


    fun setRuleSortBy(ruleSortBy: RuleSortBy) {
        updateState { copy(ruleSortBy = ruleSortBy) }
        loadRules(false)
    }

    fun toggleShowDetailedDAItem() {
        viewModelScope.launch {
            pref.setShowDetailedDAItemContent(!state.value.isShowDetailedDAItem)
        }
    }


    fun loadDA(delay: Boolean = false) {
        logger.d("loadDA")
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            if (delay) afterNavLongDelay()
            withContext(Dispatchers.IO) {
                val activeJobs = shortXManager.getActiveJobs().apply {
                    logger.d("activeJobs: $this")
                }
                val das = shortXManager.getAllDirectAction()
                    .filter {
                        it.directActionSetId.isNullOrEmpty()
                    }
                    .sortedByDescending { it.createTime }
                    .mapNotNull { directAction ->
                        runCatching {
                            daToDirectActionUM(directAction, activeJobs)
                        }.getOrElse {
                            logger.e(it, "daToDirectActionUM")
                            null
                        }
                    }

                val sets = shortXManager.getAllDASets()
                    .sortedWith { o1, o2 ->
                        Collator.getInstance(Locale.CHINESE).compare(o1.title, o2.title)
                    }
                    .map {
                        DirectActionUM(
                            id = it.id,
                            title = it.title,
                            description = it.description,
                            updateTime = FuzzyDateTimeFormatter.getTimeAgo(
                                context,
                                Date(it.lastUpdateTime)
                            ),
                            createTime = FuzzyDateTimeFormatter.getTimeAgo(
                                context,
                                Date(it.createTime)
                            ),
                            icon = it.icon,
                            iconColor = Color.Red.value,
                            runningInsCount = 0,
                            directAction = DirectAction.getDefaultInstance(),
                            versionCode = 0,
                            isDirectActionSet = true
                        )
                    }

                updateState {
                    copy(das = sets + das, isLoading = false)
                }
            }

        }
    }

    private fun delayLoadDA(delay: Long = 200L) {
        loadDAJob?.cancel()
        loadDAJob = viewModelScope.launch {
            delay(delay)
            loadDA()
        }
    }

    private fun delayLoadRule(delay: Long = 200L) {
        loadRuleJob?.cancel()
        loadRuleJob = viewModelScope.launch {
            delay(delay)
            loadRules()
        }
    }

    fun setRuleEnabled(ruleId: String, enable: Boolean) {
        viewModelScope.launch {
            shortXManager.setRuleEnabled(ruleId, enable)
            loadRules()
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

    fun deleteRule(ruleId: String) {
        viewModelScope.launch {
            shortXManager.deleteRule(ruleId)
            loadRules()
        }
    }

    fun dupRule(ruleId: String, token: String?) {
        logger.d("dupRule: $ruleId")
        viewModelScope.launch {
            shortXManager.getRuleById(ruleId)?.let { importRule(it, true, token) }
        }
    }

    fun deleteDA(daId: String) {
        viewModelScope.launch {
            shortXManager.deleteDirectAction(daId)
            loadDA()
        }
    }

    fun dupDA(daId: String, token: String?) {
        viewModelScope.launch {
            shortXManager.getDirectActionById(daId)?.let {
                importDA(it, true, token)
            }
        }
    }

    fun executeDA(da: DirectActionUM) {
        viewModelScope.launch {
            val evaluateContext = EvaluateContext.newBuilder()
                .setSrcId(da.id)
                .setSrcEvent(ContextSrcEvent.ContextSrc_DirectAction)
                .setSrcTitle(da.title)
                .build()
            shortXManager.executeDirectionActionById(
                context = evaluateContext,
                id = da.id
            )
        }
    }

    fun stopJobs(da: DirectActionUM) {
        viewModelScope.launch {
            shortXManager.cancelJobs(listOf(da.id))
        }
    }

    fun stopJobs(rule: RuleUM) {
        viewModelScope.launch {
            shortXManager.cancelJobs(listOf(rule.id))
        }
    }

    fun daToShareString(da: DirectActionUM): String? =
        shortXManager.getDirectActionById(da.id)?.toJson()
            ?.addShareContentHeader(mapOf("type" to "da"))

    fun ruleToShareString(rule: RuleUM): String? =
        shortXManager.getRuleById(rule.id)?.toJson()?.addShareContentHeader(mapOf("type" to "rule"))

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

            loadRules()
        }
    }

    fun importDA(da: DirectAction, shouldChangeId: Boolean, token: String?) {
        val daWithSameNameExists =
            state.value.das.firstOrNull { it.title == da.title } != null
        val copyInd = " (Copy)"

        viewModelScope.launch {
            val refinedActions = da.actionsList.mapNotNull {
                it.toAction(shortXManager)?.toProtoAction(defaultNewActionId())?.pack_()
            }

            val refined =
                DirectAction.newBuilder(da)
                    .setTitle(if (daWithSameNameExists) "${da.title} $copyInd" else da.title)
                    .apply {
                        if (shouldChangeId) id = defaultNewDAId()
                    }
                    .clearActions()
                    .addAllActions(refinedActions)
                    .setCreateTime(da.createTime)
                    .setLastUpdateTime(da.lastUpdateTime)
                    .setVersionCode(da.versionCode)
                    .addAllExtras(mutableListOf<StringPair>().apply {
                        if (!token.isNullOrEmpty()) {
                            add((BuildProp.APP_PACKAGE_NAME to token).toStringPair())
                        }
                    })
                    .build()
            shortXManager.addDirectAction(refined)

            loadDA()
        }
    }

    fun reboot() {
        shortXManager.reboot()
    }

    fun acceptPrivacyStatement() {
        viewModelScope.launch {
            pref.setPrivacyStatementAccepted(true)
        }
    }

    fun createNewDASet(title: String) {
        shortXManager.addOrUpdateDASet(
            DirectActionSet
                .newBuilder()
                .setTitle(title).setId("DS-${UUID.randomUUID()}")
                .setCreateTime(System.currentTimeMillis())
                .setLastUpdateTime(System.currentTimeMillis())
                .build()
        )
        loadDA()
    }

    fun addDAToDASet(da: DirectActionUM, setId: String) {
        shortXManager.addDirectAction(
            DirectAction.newBuilder(da.directAction).setDirectActionSetId(setId).build()
        )
    }
}