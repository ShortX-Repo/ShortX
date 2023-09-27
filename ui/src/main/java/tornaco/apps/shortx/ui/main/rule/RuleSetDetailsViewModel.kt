package tornaco.apps.shortx.ui.main.rule

import android.content.Context
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.ui.main.model.ruleToRuleUM

class RuleSetDetailsViewModel constructor(
    context: Context,
    private val ruleSetId: String
) :
    BaseRuleListViewModel(context) {
    override fun loadRuleList(delay: Boolean) {
        logger.d("loadRuleList")
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            withContext(Dispatchers.IO) {
                val activeJobs = shortXManager.getActiveJobs().apply {
                    logger.d("activeJobs: $this")
                }
                val gvs = shortXManager.getAllGlobalVars()
                val rules = shortXManager.getAllRules()
                    .filter { it.ruleSetId == ruleSetId }
                    .let(sort(state.value.ruleSortBy))
                    .mapNotNull { rule ->
                        runCatching {
                            ruleToRuleUM(rule, activeJobs, gvs)
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