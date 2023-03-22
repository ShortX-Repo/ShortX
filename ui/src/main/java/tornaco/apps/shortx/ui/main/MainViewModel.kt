package tornaco.apps.shortx.ui.main

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Commit
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import si.virag.fuzzydateformatter.FuzzyDateTimeFormatter
import tornaco.apps.shortx.core.IJobStatusListener
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.addrule.action.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.condition.labelForConditionSelector
import tornaco.apps.shortx.ui.addrule.condition.model.toCondition
import tornaco.apps.shortx.ui.addrule.fact.labelAndDescriptionForFactSelector
import tornaco.apps.shortx.ui.addrule.fact.model.toFact
import tornaco.apps.shortx.ui.base.ContextViewModel
import tornaco.apps.shortx.ui.base.LoadState
import java.util.*
import javax.inject.Inject


data class RuleUM(
    val id: String,
    val title: String,
    val description: String,
    val updateTime: String,
    val createTime: String,
    val isEnabled: Boolean,
)

data class DirectActionUM(
    val id: String,
    val title: String,
    val description: String,
    val updateTime: String,
    val createTime: String,
    val icon: ImageVector,
    val runningInsCount: Int
)

data class MainState(
    val isActivated: Boolean = false,
    val rules: LoadState<List<RuleUM>> = LoadState.Loading,
    val das: LoadState<List<DirectActionUM>> = LoadState.Loading,
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class MainViewModel @Inject constructor(@ApplicationContext private val context: Context) :
    ContextViewModel<MainState>(context) {
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
    }

    override fun onCleared() {
        super.onCleared()
        shortXManager.unregisterJobStatusListener(jobStatusListener)
    }

    override fun initState(): MainState {
        return MainState(isActivated = shortXManager.isInstalled())
    }

    fun loadRules() {
        logger.d("loadRules")
        viewModelScope.launch {
            updateState { copy(rules = LoadState.Loading) }
            withContext(Dispatchers.IO) {
                val rules = shortXManager.getAllRules()
                    .sortedByDescending { it.createTime }
                    .map { rule ->
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
                            isEnabled = rule.isEnabled
                        )
                    }
                updateState {
                    copy(rules = LoadState.Success(rules))
                }
            }
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

    fun loadDA() {
        logger.d("loadDA")
        viewModelScope.launch {
            updateState { copy(das = LoadState.Loading) }
            withContext(Dispatchers.IO) {
                val activeJobs = shortXManager.getActiveJobs().apply {
                    logger.d("activeJobs: $this")
                }
                val das = shortXManager.getAllDirectAction()
                    .sortedByDescending { it.createTime }
                    .map { directAction ->
                        val firstAction: Action? = directAction.actionsList?.let {
                            if (it.isEmpty()) {
                                null
                            } else {
                                it[0].toAction(shortXManager)
                            }
                        }
                        DirectActionUM(
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
                            } ?: Icons.Outlined.Commit,
                            runningInsCount = activeJobs.count {
                                it == directAction.id
                            }
                        )
                    }
                updateState {
                    copy(das = LoadState.Success(das))
                }
            }

        }
    }

    private fun translateDA(directAction: DirectAction) =
        directAction.description.fallbackOnEmpty(directAction.actionsList.mapNotNull {
            it.toAction(shortXManager)
        }.joinToString {
            labelAndDescriptionForActionSelector(i18N, it::class).first
        })

    private fun delayLoadDA(delay: Long) {
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

    fun deleteDA(daId: String) {
        viewModelScope.launch {
            shortXManager.deleteDirectAction(daId)
            loadDA()
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
        shortXManager.getDirectActionById(da.id)?.toByteArray()?.let {
            Base64.getEncoder().encodeToString(it)
        }

    fun ruleToShareString(rule: RuleUM): String? =
        shortXManager.getRuleById(rule.id)?.toByteArray()?.let {
            Base64.getEncoder().encodeToString(it)
        }

    fun parseShareContentRule(content: String): Rule? {
        return kotlin.runCatching {
            Rule.parseFrom(Base64.getDecoder().decode(content))?.apply {
                requireNotNull(this.id) { "Id is null. invalid rule?" }
                requireNotNull(this.title) { "Title is null. invalid rule?" }
            }
        }.getOrNull()
    }

    fun parseShareContentDA(content: String): DirectAction? {
        return kotlin.runCatching {
            DirectAction.parseFrom(Base64.getDecoder().decode(content))?.apply {
                requireNotNull(this.id) { "Id is null. invalid da?" }
                requireNotNull(this.title) { "Title is null. invalid da?" }
            }
        }.getOrNull()
    }

    fun importRule(rule: Rule) {
        viewModelScope.launch {
            val refined =
                Rule.newBuilder(rule).setId(UUID.randomUUID().toString()).setIsEnabled(false)
                    .build()
            shortXManager.addRule(refined)

            loadRules()
        }
    }

    fun importDA(da: DirectAction) {
        viewModelScope.launch {
            val refined =
                DirectAction.newBuilder(da).setId(UUID.randomUUID().toString())
                    .build()
            shortXManager.addDirectAction(refined)

            loadDA()
        }
    }
}