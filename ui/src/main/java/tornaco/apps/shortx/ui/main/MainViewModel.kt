package tornaco.apps.shortx.ui.main

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import si.virag.fuzzydateformatter.FuzzyDateTimeFormatter
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
            val rules = shortXManager.getAllRules().map {
                RuleUM(
                    id = it.id,
                    title = it.title,
                    description = it.description,
                    updateTime = FuzzyDateTimeFormatter.getTimeAgo(
                        context,
                        Date(it.lastUpdateTime)
                    ),
                    isEnabled = it.isEnabled
                )
            }
            updateState {
                copy(rules = rules)
            }
        }
    }

    fun loadDirections() {
        logger.d("loadDirections")
        viewModelScope.launch {
            val das = shortXManager.getAllDirectAction().map {
                DirectActionUM(
                    id = it.id,
                    title = it.title,
                    description = it.description,
                    updateTime = FuzzyDateTimeFormatter.getTimeAgo(
                        context,
                        Date(it.lastUpdateTime)
                    ),
                )
            }
            updateState {
                copy(das = das)
            }
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
            loadDirections()
        }
    }

    fun executeDA(da: DirectActionUM) {
        viewModelScope.launch {
            shortXManager.executeDirectionActionById(da.id)
        }
    }
}