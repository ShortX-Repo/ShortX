package tornaco.apps.shortx.ui.main

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import tornaco.apps.shortx.ui.base.BaseViewModel
import java.util.*
import javax.inject.Inject


data class Rule(
    val id: String,
    val title: String,
    val description: String,
    val updateTime: String,
    val isEnabled: Boolean,
)

data class MainState(val rules: List<Rule> = emptyList())

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class MainViewModel @Inject constructor(@ApplicationContext private val context: Context) :
    BaseViewModel<MainState>(context) {

    override fun initState(): MainState {
        return MainState()
    }

    fun loadRules() {
        logger.d("loadRules")
        viewModelScope.launch {
            val rules = shortXManager.getAllRules().map {
                Rule(id = it.id,
                    title = it.title,
                    description = it.description,
                    updateTime = Date(it.lastUpdateTime).toString(),
                    isEnabled = it.isEnabled
                )
            }
            updateState {
                copy(rules = rules)
            }
        }
    }

    fun setRuleEnabled(ruleId: String, enable: Boolean) {
        viewModelScope.launch {
            shortXManager.setRuleEnabled(ruleId, enable)
            loadRules()
        }
    }

    fun delete(ruleId: String) {
        viewModelScope.launch {
            shortXManager.deleteRule(ruleId)
            loadRules()
        }
    }
}