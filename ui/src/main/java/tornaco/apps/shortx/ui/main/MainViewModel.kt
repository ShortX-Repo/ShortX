package tornaco.apps.shortx.ui.main

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Commit
import androidx.compose.material.icons.outlined.Commit
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import si.virag.fuzzydateformatter.FuzzyDateTimeFormatter
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
import java.util.*
import javax.inject.Inject


data class RuleUM(
    val id: String,
    val title: String,
    val description: String,
    val updateTime: String,
    val isEnabled: Boolean,
)

data class DirectActionUM(
    val id: String,
    val title: String,
    val description: String,
    val updateTime: String,
    val icon: ImageVector
)

data class MainState(
    val isActivated: Boolean = false,
    val rules: List<RuleUM> = emptyList(),
    val das: List<DirectActionUM> = emptyList()
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class MainViewModel @Inject constructor(@ApplicationContext private val context: Context) :
    ContextViewModel<MainState>(context) {

    override fun initState(): MainState {
        return MainState(isActivated = shortXManager.isInstalled())
    }

    fun loadRules() {
        logger.d("loadRules")
        viewModelScope.launch {
            val rules = shortXManager.getAllRules().map { rule ->
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
                    isEnabled = rule.isEnabled
                )
            }
            updateState {
                copy(rules = rules)
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
            val das = shortXManager.getAllDirectAction().map { directAction ->
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
                    icon = firstAction?.let {
                        imageVectorForAction(
                            it::class
                        )
                    } ?: Icons.Outlined.Commit
                )
            }
            updateState {
                copy(das = das)
            }
        }
    }

    private fun translateDA(directAction: DirectAction) =
        directAction.description.fallbackOnEmpty(directAction.actionsList.mapNotNull {
            it.toAction(shortXManager)
        }.joinToString {
            labelAndDescriptionForActionSelector(i18N, it::class).first
        })

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
}