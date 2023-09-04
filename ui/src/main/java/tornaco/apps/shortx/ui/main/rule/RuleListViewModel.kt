package tornaco.apps.shortx.ui.main.rule

import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.ui.base.afterNavLongDelay
import tornaco.apps.shortx.ui.main.model.ruleToRuleUM
import javax.inject.Inject

@HiltViewModel
class RuleListViewModel @Inject constructor(@ApplicationContext context: Context) :
    BaseRuleListViewModel(context) {
    override fun loadRuleList(delay: Boolean) {
        logger.d("loadRuleList")
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
}