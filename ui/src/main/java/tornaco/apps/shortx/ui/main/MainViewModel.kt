package tornaco.apps.shortx.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.parcelize.Parcelize
import si.virag.fuzzydateformatter.FuzzyDateTimeFormatter
import tornaco.apps.shortx.core.IAudioRecordingListener
import tornaco.apps.shortx.core.IJobStatusListener
import tornaco.apps.shortx.core.proto.condition.True
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.toJson
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.IDAObserver
import tornaco.apps.shortx.core.rule.IRuleObserver
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.rule.parseShareContentDA
import tornaco.apps.shortx.core.rule.parseShareContentRule
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.addda.defaultNewDAId
import tornaco.apps.shortx.ui.addrule.action.mapper.colorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.action.model.toProtoAction
import tornaco.apps.shortx.ui.addrule.condition.mapper.labelForConditionSelector
import tornaco.apps.shortx.ui.addrule.condition.model.defaultNewConditionId
import tornaco.apps.shortx.ui.addrule.condition.model.toCondition
import tornaco.apps.shortx.ui.addrule.condition.model.toProtoCondition
import tornaco.apps.shortx.ui.addrule.defaultNewRuleId
import tornaco.apps.shortx.ui.addrule.fact.mapper.colorForFact
import tornaco.apps.shortx.ui.addrule.fact.mapper.imageVectorForFact
import tornaco.apps.shortx.ui.addrule.fact.mapper.labelAndDescriptionForFactSelector
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.model.defaultNewFactId
import tornaco.apps.shortx.ui.addrule.fact.model.toFact
import tornaco.apps.shortx.ui.addrule.fact.model.toProtoFact
import tornaco.apps.shortx.ui.base.ContextViewModel
import tornaco.apps.shortx.ui.base.afterNavLongDelay
import tornaco.apps.shortx.ui.settings.ResetEventRegistry
import tornaco.apps.shortx.ui.settings.RestoreEventRegistry
import tornaco.apps.shortx.ui.settings.pref.Pref
import tornaco.apps.shortx.ui.share.addShareContentHeader
import tornaco.apps.shortx.ui.util.ClipboardUtils
import java.nio.charset.Charset
import java.util.*
import javax.inject.Inject


data class RuleUM(
    val id: String,
    val title: String,
    val description: String,
    val updateTime: String,
    val createTime: String,
    val isEnabled: Boolean,
    val factIcon: String,
    val factIconColor: ULong,
    val actionIcon: String,
    val actionIconColor: ULong,
    val runningInsCount: Int,
    val rule: Rule,
    val versionCode: Long,

    // For online.
    val author: String = "ShortX",
    val isOnlineItem: Boolean = false,
    val url: String? = null,
    val isInstalled: Boolean = false,
    val hasUpdate: Boolean = false
)

@Parcelize
data class DirectActionUM(
    val id: String,
    val title: String,
    val description: String,
    val updateTime: String,
    val createTime: String,
    val icon: String,
    val iconColor: ULong,
    val runningInsCount: Int,
    val directAction: DirectAction,
    val versionCode: Long,

    // For online.
    val author: String = "ShortX",
    val isOnlineItem: Boolean = false,
    val url: String? = null,
    val isInstalled: Boolean = false,
    val hasUpdate: Boolean = false
) : Parcelable

data class MainState(
    val isHideSubscribeInfo: Boolean = false,
    val isActivated: Boolean = false,
    val isFrameworkUpdated: Boolean = false,
    val isIgnoreFrameworkUpdate: Boolean = false,
    val isPrivacyStatementAccepted: Boolean = false,
    val isShowRuleDescription: Boolean = true,
    val rules: List<RuleUM> = emptyList(),
    val ruleSortBy: RuleSortBy = RuleSortBy.CreateTime,
    val das: List<DirectActionUM> = emptyList(),
    val daSortBy: DASortBy = DASortBy.CreateTime,
    val isShowDetailedDAItem: Boolean = true,
    val isLoading: Boolean = true,
    val isAudioRecording: Boolean = false
)

fun <T> ContextViewModel<T>.daToDirectActionUM(
    directAction: DirectAction,
    activeJobs: List<String>
): DirectActionUM {
    val firstAction: Action? = directAction.actionsList?.let {
        if (it.isEmpty()) {
            null
        } else {
            it[0].toAction(shortXManager)
        }
    }

    return DirectActionUM(
        id = directAction.id,
        title = directAction.title,
        description = translateDA(directAction),
        updateTime = FuzzyDateTimeFormatter.getTimeAgo(
            context,
            Date(directAction.lastUpdateTime)
        ),
        createTime = FuzzyDateTimeFormatter.getTimeAgo(
            context,
            Date(directAction.createTime)
        ),
        icon = firstAction?.let {
            imageVectorForAction(it)
        } ?: Remix.Weather.sparkling_line,
        iconColor = firstAction?.let {
            colorForAction(it::class).value
        } ?: Color.Unspecified.value,
        runningInsCount = activeJobs.count {
            it == directAction.id
        },
        directAction = directAction,
        versionCode = directAction.versionCode
    )
}

private fun <T> ContextViewModel<T>.translateDA(da: DirectAction) =
    da.description.fallbackOnEmpty(da.actionsList.mapNotNull {
        it.toAction(shortXManager)
    }.joinToString {
        labelAndDescriptionForActionSelector(i18N, it::class).first
    })

fun <T> ContextViewModel<T>.ruleToRuleUM(rule: Rule, activeJobs: List<String>): RuleUM {
    val firstFact: Fact? =
        if (rule.factsList.isEmpty()) null else rule.factsList[0]?.toFact(shortXManager)
    val firstAction: Action? = rule.actionsList?.let {
        if (it.isEmpty()) {
            null
        } else {
            it[0].toAction(shortXManager)
        }
    }
    return RuleUM(
        id = rule.id,
        title = rule.title,
        description = rule.description.fallbackOnEmpty(
            translateRule(rule)
        ),
        updateTime = FuzzyDateTimeFormatter.getTimeAgo(
            context,
            Date(rule.lastUpdateTime)
        ),
        createTime = FuzzyDateTimeFormatter.getTimeAgo(
            context,
            Date(rule.createTime)
        ),
        isEnabled = rule.isEnabled,
        factIcon = firstFact?.let {
            imageVectorForFact(
                it::class
            )
        } ?: Remix.Weather.sparkling_line,
        factIconColor = firstFact?.let {
            colorForFact(it::class).value
        } ?: Color.Unspecified.value,
        actionIcon = firstAction?.let {
            imageVectorForAction(it)
        } ?: Remix.Weather.sparkling_line,
        actionIconColor = firstAction?.let {
            colorForAction(it::class).value
        } ?: Color.Unspecified.value,
        rule = rule,
        runningInsCount = activeJobs.count {
            it == rule.id
        },
        versionCode = rule.versionCode
    )
}

fun <T> ContextViewModel<T>.translateRule(rule: Rule): String {
    val fact: String = rule.factsList
        .mapNotNull { factProto ->
            factProto.toFact(shortXManager)?.let { fact ->
                labelAndDescriptionForFactSelector(i18N, fact::class).first
            }
        }.joinToString(" / ")

    val condition = rule.conditionsList
        .filterNot { it is_ True::class.java }
        .mapNotNull {
            it.toCondition(shortXManager)
        }.joinToString {
            labelForConditionSelector(i18N, it::class).joinToString()
        }

    val action = rule.actionsList.mapNotNull {
        it.toAction(shortXManager)
    }.joinToString {
        labelAndDescriptionForActionSelector(i18N, it::class).first
    }

    return if (condition.isNotBlank()) {
        i18N["ui.rule.description.auto.template", mapOf(
            "fact" to fact,
            "condition" to condition,
            "action" to action
        )]
    } else {
        i18N["ui.rule.description.auto.no.condition.template", mapOf(
            "fact" to fact,
            "condition" to condition,
            "action" to action
        )]
    }
}

sealed interface ImportResult {
    object Success : ImportResult
    data class Fail(val reason: FailReason) : ImportResult

    enum class FailReason(val description: String) {
        ParseFail("Unable to parse content")
    }
}

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class MainViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<MainState>(context, { MainState() }) {
    private val pref by lazy { Pref(context) }
    private var loadDAJob: Job? = null
    private var loadRuleJob: Job? = null

    private val _importComplete = MutableSharedFlow<ImportResult>()
    val importComplete = _importComplete.asSharedFlow()

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
                    .sortedByDescending { it.createTime }
                    .mapNotNull { directAction ->
                        runCatching {
                            daToDirectActionUM(directAction, activeJobs)
                        }.getOrElse {
                            logger.e(it, "daToDirectActionUM")
                            null
                        }
                    }
                updateState {
                    copy(das = das, isLoading = false)
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

    fun dupRule(ruleId: String) {
        logger.d("dupRule: $ruleId")
        viewModelScope.launch {
            shortXManager.getRuleById(ruleId)?.let { importRule(it, true) }
        }
    }

    fun deleteDA(daId: String) {
        viewModelScope.launch {
            shortXManager.deleteDirectAction(daId)
            loadDA()
        }
    }

    fun dupDA(daId: String) {
        viewModelScope.launch {
            shortXManager.getDirectActionById(daId)?.let {
                importDA(it, true)
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

    fun importRuleFromClipboard() {
        viewModelScope.launch {
            ClipboardUtils.readClipboard(context)?.let { clip ->
                val rule = parseShareContentRule(clip)
                if (rule != null) {
                    importRule(rule, false)
                    _importComplete.emit(ImportResult.Success)
                } else {
                    _importComplete.emit(ImportResult.Fail(ImportResult.FailReason.ParseFail))
                }
            }
        }
    }

    fun importRuleFromUri(uri: Uri) {
        viewModelScope.launch {
            val rule = parseShareContentRuleFromUri(uri)
            if (rule != null) {
                importRule(rule, false)
                _importComplete.emit(ImportResult.Success)
            } else {
                _importComplete.emit(ImportResult.Fail(ImportResult.FailReason.ParseFail))
            }
        }
    }

    private fun parseShareContentRuleFromUri(uri: Uri): Rule? {
        val text = runCatching {
            context.contentResolver.openInputStream(uri)?.use {
                it.bufferedReader(Charset.defaultCharset()).readText()
            }
        }.getOrElse {
            logger.e(it, "parseShareContentRuleFromUri")
            null
        }
        return text?.let { parseShareContentRule(it) }
    }

    fun importDAFromClipboard() {
        viewModelScope.launch {
            ClipboardUtils.readClipboard(context)?.let { clip ->
                val da = parseShareContentDA(clip)
                if (da != null) {
                    importDA(da, false)
                    _importComplete.emit(ImportResult.Success)
                } else {
                    _importComplete.emit(ImportResult.Fail(ImportResult.FailReason.ParseFail))
                }
            }
        }
    }

    fun importDAFromUri(uri: Uri) {
        viewModelScope.launch {
            val da = parseShareContentDAFromUri(uri)
            if (da != null) {
                importDA(da, false)
                _importComplete.emit(ImportResult.Success)
            } else {
                _importComplete.emit(ImportResult.Fail(ImportResult.FailReason.ParseFail))
            }
        }
    }

    private fun parseShareContentDAFromUri(uri: Uri): DirectAction? {
        val text = runCatching {
            context.contentResolver.openInputStream(uri)?.use {
                it.bufferedReader(Charset.defaultCharset()).readText()
            }
        }.getOrElse {
            logger.e(it, "parseShareContentDAFromUri")
            null
        }
        return text?.let { parseShareContentDA(it) }
    }


    fun importRule(rule: Rule, shouldChangeId: Boolean) {
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
                    .build()
            shortXManager.addRule(refined)

            loadRules()
        }
    }

    fun importDA(da: DirectAction, shouldChangeId: Boolean) {
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
}