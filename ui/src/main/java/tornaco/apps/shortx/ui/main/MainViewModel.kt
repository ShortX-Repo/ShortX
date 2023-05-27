package tornaco.apps.shortx.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.parcelize.Parcelize
import si.virag.fuzzydateformatter.FuzzyDateTimeFormatter
import tornaco.apps.shortx.core.IJobStatusListener
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.toJson
import tornaco.apps.shortx.core.proto.toProto
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.addda.defaultNewDAId
import tornaco.apps.shortx.ui.addrule.action.colorForAction
import tornaco.apps.shortx.ui.addrule.action.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.condition.labelForConditionSelector
import tornaco.apps.shortx.ui.addrule.condition.model.toCondition
import tornaco.apps.shortx.ui.addrule.defaultNewRuleId
import tornaco.apps.shortx.ui.addrule.fact.colorForFact
import tornaco.apps.shortx.ui.addrule.fact.imageVectorForFact
import tornaco.apps.shortx.ui.addrule.fact.labelAndDescriptionForFactSelector
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.model.toFact
import tornaco.apps.shortx.ui.base.ContextViewModel
import tornaco.apps.shortx.ui.base.afterNavLongDelay
import tornaco.apps.shortx.ui.settings.pref.Pref
import tornaco.apps.shortx.ui.share.addShareContentHeader
import tornaco.apps.shortx.ui.subscribe.util.isLVLChecked
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
    val rule: Rule
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
    val directAction: DirectAction
) : Parcelable

data class MainState(
    val isSubscribed: Boolean = false,
    val isActivated: Boolean = false,
    val isFrameworkUpdated: Boolean = false,
    val isIgnoreFrameworkUpdate: Boolean = false,
    val isPrivacyStatementAccepted: Boolean = false,
    val isShowRuleDescription: Boolean = true,
    val rules: List<RuleUM> = emptyList(),
    val das: List<DirectActionUM> = emptyList(),
    val isShowDetailedDAItem: Boolean = true,
    val isLoading: Boolean = true
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
            imageVectorForAction(
                it::class
            )
        } ?: Remix.Weather.sparkling_line,
        iconColor = firstAction?.let {
            colorForAction(it::class).value
        } ?: Color.Unspecified.value,
        runningInsCount = activeJobs.count {
            it == directAction.id
        },
        directAction = directAction
    )
}

private fun <T> ContextViewModel<T>.translateDA(da: DirectAction) =
    da.description.fallbackOnEmpty(da.actionsList.mapNotNull {
        it.toAction(shortXManager)
    }.joinToString {
        labelAndDescriptionForActionSelector(i18N, it::class).first
    })

fun <T> ContextViewModel<T>.ruleToRuleUM(rule: Rule): RuleUM {
    val firstFact: Fact? = rule.fact?.toFact(shortXManager)
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
            imageVectorForAction(
                it::class
            )
        } ?: Remix.Weather.sparkling_line,
        actionIconColor = firstAction?.let {
            colorForAction(it::class).value
        } ?: Color.Unspecified.value,
        rule = rule,
    )
}

fun <T> ContextViewModel<T>.translateRule(rule: Rule): String {
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

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class MainViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<MainState>(context, MainState()) {
    private val pref by lazy { Pref(context) }
    private var loadDAJob: Job? = null

    private val jobStatusListener = object : IJobStatusListener.Stub() {
        override fun onJobStarted(id: String?) {
            logger.d("onJobStarted: $id")
            delayLoadDA()
        }

        override fun onJobCompleted(id: String?) {
            logger.d("onJobCompleted: $id")
            delayLoadDA()
        }

        override fun onJobCancelled(id: String?) {
            logger.d("onJobCancelled: $id")
            delayLoadDA()
        }
    }

    init {
        updateState {
            copy(
                isActivated = shortXManager.isInstalled,
                isFrameworkUpdated = shortXManager.isFrameworkUpdated,
                isSubscribed = isLVLChecked(context)
            )
        }

        shortXManager.registerJobStatusListener(jobStatusListener)

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
    }

    fun refreshStates() {
        updateState {
            copy(
                isSubscribed = isLVLChecked(context)
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        shortXManager.unregisterJobStatusListener(jobStatusListener)
    }

    fun loadRules(delay: Boolean = false) {
        logger.d("loadRules")
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            if (delay) afterNavLongDelay()
            withContext(Dispatchers.IO) {
                val rules = shortXManager.getAllRules()
                    .sortedByDescending { it.createTime }
                    .map { rule ->
                        ruleToRuleUM(rule)
                    }
                updateState {
                    copy(rules = rules, isLoading = false)
                }
            }
        }
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
                    .map { directAction ->
                        daToDirectActionUM(directAction, activeJobs)
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

    fun setRuleEnabled(ruleId: String, enable: Boolean) {
        viewModelScope.launch {
            shortXManager.setRuleEnabled(ruleId, enable)
            loadRules()
        }
    }

    fun deleteRule(ruleId: String) {
        viewModelScope.launch {
            shortXManager.deleteRule(ruleId)
            loadRules()
        }
    }

    fun dupRule(ruleId: String) {
        viewModelScope.launch {
            shortXManager.getRuleById(ruleId)?.let { importRule(it) }
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
            shortXManager.getDirectActionById(daId)?.let { importDA(it) }
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
            ?.addShareContentHeader(mapOf("type" to "da"))

    fun ruleToShareString(rule: RuleUM): String? =
        shortXManager.getRuleById(rule.id)?.toJson()?.addShareContentHeader(mapOf("type" to "rule"))

    fun parseShareContentRule(content: String): Rule? {
        return kotlin.runCatching {
            Rule.newBuilder(
                Rule.parseFrom(Rule.newBuilder().toProto(content).toByteArray())?.apply {
                    requireNotNull(this.id) { "Id is null. invalid rule?" }
                    requireNotNull(this.title) { "Title is null. invalid rule?" }
                }).setId(defaultNewRuleId()).setCreateTime(System.currentTimeMillis())
                .setLastUpdateTime(System.currentTimeMillis()).build()
        }.getOrNull()
    }

    fun parseShareContentDA(content: String): DirectAction? {
        return kotlin.runCatching {
            DirectAction.newBuilder(DirectAction.parseFrom(
                DirectAction.newBuilder().toProto(content).toByteArray()
            )?.apply {
                requireNotNull(this.id) { "Id is null. invalid da?" }
                requireNotNull(this.title) { "Title is null. invalid da?" }
            }).setId(defaultNewDAId()).setCreateTime(System.currentTimeMillis())
                .setLastUpdateTime(System.currentTimeMillis()).build()
        }.getOrNull()
    }

    fun importRule(rule: Rule) {
        val ruleWithSameNameExists =
            state.value.rules.firstOrNull { it.title == rule.title } != null
        val copyInd = DateUtils.formatForFileName(System.currentTimeMillis())

        viewModelScope.launch {
            val refined =
                Rule.newBuilder(rule)
                    .setTitle(if (ruleWithSameNameExists) "${rule.title} $copyInd" else rule.title)
                    .setId(defaultNewRuleId())
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
            state.value.das.firstOrNull { it.title == da.title } != null
        val copyInd = DateUtils.formatForFileName(System.currentTimeMillis())

        viewModelScope.launch {
            val refined =
                DirectAction.newBuilder(da)
                    .setTitle(if (daWithSameNameExists) "${da.title} $copyInd" else da.title)
                    .setId(defaultNewDAId())
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