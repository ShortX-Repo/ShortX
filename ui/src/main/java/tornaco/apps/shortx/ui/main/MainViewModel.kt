package tornaco.apps.shortx.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.parcelize.Parcelize
import si.virag.fuzzydateformatter.FuzzyDateTimeFormatter
import tornaco.apps.shortx.core.IJobStatusListener
import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.toJson
import tornaco.apps.shortx.core.proto.toProto
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.addrule.action.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.condition.labelForConditionSelector
import tornaco.apps.shortx.ui.addrule.condition.model.toCondition
import tornaco.apps.shortx.ui.addrule.fact.imageVectorForFact
import tornaco.apps.shortx.ui.addrule.fact.labelAndDescriptionForFactSelector
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.model.toFact
import tornaco.apps.shortx.ui.base.ContextViewModel
import tornaco.apps.shortx.ui.base.I18N
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.LoadState.Loading.data
import tornaco.apps.shortx.ui.settings.pref.DEFAULT_RULE_COLUMN_COUNT
import tornaco.apps.shortx.ui.settings.pref.Pref
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
    val actionIcon: String,
)

@Parcelize
data class DirectActionUM(
    val id: String,
    val title: String,
    val description: String,
    val updateTime: String,
    val createTime: String,
    val icon: String,
    val runningInsCount: Int
) : Parcelable

data class MainState(
    val isActivated: Boolean = false,
    val isFrameworkUpdated: Boolean = false,
    val isPrivacyStatementAccepted: Boolean = false,
    val isShowRuleDescription: Boolean = true,
    val ruleColumnCount: Int = DEFAULT_RULE_COLUMN_COUNT,
    val rules: LoadState<List<RuleUM>> = LoadState.Loading,
    val das: LoadState<List<DirectActionUM>> = LoadState.Loading,
)


private fun DirectAction.toDirectActionUM(
    context: Context,
    shortXManager: ShortXManager,
    i18N: I18N,
    activeJobs: List<String>
): DirectActionUM {
    val firstAction: Action? = this.actionsList?.let {
        if (it.isEmpty()) {
            null
        } else {
            it[0].toAction(shortXManager)
        }
    }

    return DirectActionUM(
        id = this.id,
        title = this.title,
        description = translateDA(shortXManager, i18N),
        updateTime = FuzzyDateTimeFormatter.getTimeAgo(
            context,
            Date(this.lastUpdateTime)
        ),
        createTime = FuzzyDateTimeFormatter.getTimeAgo(
            context,
            Date(this.createTime)
        ),
        icon = firstAction?.let {
            imageVectorForAction(
                it::class
            )
        } ?: Remix.Weather.sparkling_line,
        runningInsCount = activeJobs.count {
            it == this.id
        }
    )
}

private fun DirectAction.translateDA(shortXManager: ShortXManager, i18N: I18N) =
    this.description.fallbackOnEmpty(this.actionsList.mapNotNull {
        it.toAction(shortXManager)
    }.joinToString {
        labelAndDescriptionForActionSelector(i18N, it::class).first
    })

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class MainViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<MainState>(context) {
    private val pref by lazy { Pref(context) }
    private var loadDAJob: Job? = null

    private val jobStatusListener = object : IJobStatusListener.Stub() {
        override fun onJobStarted(id: String?) {
            logger.d("onJobStarted: $id")
            delayLoadDA(200)
        }

        override fun onJobCompleted(id: String?) {
            logger.d("onJobCompleted: $id")
            delayLoadDA(200)
        }

        override fun onJobCancelled(id: String?) {
            logger.d("onJobCancelled: $id")
            delayLoadDA(200)
        }
    }

    init {
        shortXManager.registerJobStatusListener(jobStatusListener)

        viewModelScope.launch {
            pref.privacyStatementAcceptedFlow.collectLatest {
                logger.d("isPrivacyStatementAccepted changed: $it")
                updateState { copy(isPrivacyStatementAccepted = it) }
            }
        }

        viewModelScope.launch {
            pref.uiRuleColumnCount.collectLatest {
                logger.d("uiRuleColumnCount changed: $it")
                updateState { copy(ruleColumnCount = it) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        shortXManager.unregisterJobStatusListener(jobStatusListener)
    }

    override fun initState(): MainState {
        return MainState(
            isActivated = shortXManager.isInstalled,
            isFrameworkUpdated = shortXManager.isFrameworkUpdated,
        )
    }

    fun loadRules(showLoading: Boolean = true) {
        logger.d("loadRules")
        viewModelScope.launch {
            if (showLoading) updateState { copy(rules = LoadState.Loading) }
            withContext(Dispatchers.IO) {
                val rules = shortXManager.getAllRules()
                    .sortedByDescending { it.createTime }
                    .map { rule ->
                        val firstFact: Fact? = rule.fact?.toFact(shortXManager)
                        val firstAction: Action? = rule.actionsList?.let {
                            if (it.isEmpty()) {
                                null
                            } else {
                                it[0].toAction(shortXManager)
                            }
                        }
                        RuleUM(
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
                            actionIcon = firstAction?.let {
                                imageVectorForAction(
                                    it::class
                                )
                            } ?: Remix.Weather.sparkling_line,
                        )
                    }
                updateState {
                    copy(rules = LoadState.Success(rules))
                }
            }
        }
    }

    fun toggleRuleColumnCount() {
        val count = if (state.value.ruleColumnCount == 1) 2 else 1
        viewModelScope.launch {
            Pref(context).setColumnCount(count)
        }
    }

    private fun translateRule(rule: Rule): String {
        val fact: String = rule.fact.toFact(shortXManager)?.let {
            labelAndDescriptionForFactSelector(i18N, it::class).first
        } ?: "N/A"

        val condition = rule.conditionsList.mapNotNull {
            it.toCondition(shortXManager)
        }.joinToString {
            labelForConditionSelector(i18N, it::class).joinToString()
        }

        val action = rule.actionsList.mapNotNull {
            it.toAction(shortXManager)
        }.joinToString {
            labelAndDescriptionForActionSelector(i18N, it::class).first
        }

        return i18N["ui.rule.description.auto.template", mapOf(
            "fact" to fact,
            "condition" to condition,
            "action" to action
        )]
    }

    fun loadDA(showLoading: Boolean = true) {
        logger.d("loadDA")
        viewModelScope.launch {
            if (showLoading) updateState { copy(das = LoadState.Loading) }
            withContext(Dispatchers.IO) {
                val activeJobs = shortXManager.getActiveJobs().apply {
                    logger.d("activeJobs: $this")
                }
                val das = shortXManager.getAllDirectAction()
                    .sortedByDescending { it.createTime }
                    .map { directAction ->
                        directAction.toDirectActionUM(context, shortXManager, i18N, activeJobs)
                    }
                updateState {
                    copy(das = LoadState.Success(das))
                }
            }

        }
    }

    private fun delayLoadDA(delay: Long) {
        loadDAJob?.cancel()
        loadDAJob = viewModelScope.launch {
            delay(delay)
            loadDA(false)
        }
    }

    fun setRuleEnabled(ruleId: String, enable: Boolean) {
        viewModelScope.launch {
            shortXManager.setRuleEnabled(ruleId, enable)
            loadRules(false)
        }
    }

    fun deleteRule(ruleId: String) {
        viewModelScope.launch {
            shortXManager.deleteRule(ruleId)
            loadRules(false)
        }
    }

    fun deleteDA(daId: String) {
        viewModelScope.launch {
            shortXManager.deleteDirectAction(daId)
            loadDA(false)
        }
    }

    fun executeDA(da: DirectActionUM) {
        viewModelScope.launch {
            shortXManager.executeDirectionActionById(da.id)
        }
    }

    fun stopJobs(da: DirectActionUM) {
        viewModelScope.launch {
            shortXManager.cancelJobs(listOf(da.id))
        }
    }

    fun daToShareString(da: DirectActionUM): String? =
        shortXManager.getDirectActionById(da.id)?.toJson()

    fun ruleToShareString(rule: RuleUM): String? =
        shortXManager.getRuleById(rule.id)?.toJson()

    fun parseShareContentRule(content: String): Rule? {
        return kotlin.runCatching {
            Rule.parseFrom(Rule.newBuilder().toProto(content).toByteArray())?.apply {
                requireNotNull(this.id) { "Id is null. invalid rule?" }
                requireNotNull(this.title) { "Title is null. invalid rule?" }
            }
        }.getOrNull()
    }

    fun parseShareContentDA(content: String): DirectAction? {
        return kotlin.runCatching {
            DirectAction.parseFrom(DirectAction.newBuilder().toProto(content).toByteArray())
                ?.apply {
                    requireNotNull(this.id) { "Id is null. invalid da?" }
                    requireNotNull(this.title) { "Title is null. invalid da?" }
                }
        }.getOrNull()
    }

    fun importRule(rule: Rule) {
        val ruleWithSameNameExists =
            state.value.rules.data?.firstOrNull { it.title == rule.title } != null
        val copyInd = DateUtils.formatForFileName(System.currentTimeMillis())

        viewModelScope.launch {
            val refined =
                Rule.newBuilder(rule)
                    .setTitle(if (ruleWithSameNameExists) "${rule.title} $copyInd" else rule.title)
                    .setId(UUID.randomUUID().toString())
                    .setCreateTime(System.currentTimeMillis())
                    .setLastUpdateTime(System.currentTimeMillis())
                    .setIsEnabled(false)
                    .build()
            shortXManager.addRule(refined)

            loadRules()
        }
    }

    fun importDA(da: DirectAction) {
        val daWithSameNameExists =
            state.value.das.data?.firstOrNull { it.title == da.title } != null
        val copyInd = DateUtils.formatForFileName(System.currentTimeMillis())

        viewModelScope.launch {
            val refined =
                DirectAction.newBuilder(da)
                    .setTitle(if (daWithSameNameExists) "${da.title} $copyInd" else da.title)
                    .setId(UUID.randomUUID().toString())
                    .setCreateTime(System.currentTimeMillis())
                    .setLastUpdateTime(System.currentTimeMillis())
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