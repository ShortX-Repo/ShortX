package tornaco.apps.shortx.ui.main.rule

import android.content.Context
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.afterNavDelay
import tornaco.apps.shortx.ui.main.model.ruleToRuleUM

class RuleSetDetailsViewModel(
    context: Context,
    private val ruleSetId: String
) :
    BaseRuleListViewModel(context) {

    @OptIn(FlowPreview::class)
    override val ruleList: StateFlow<LoadState<RuleList>> by lazy {
        combine(
            loadRuleRequest.debounce(300),
            state.map { it.onlyShowEnabled },
            state.map { it.ruleSortBy },
            state.map { it.searchQuery }) { _, _, _, _ ->
            runCatching {
                LoadState.Success(getRuleList())
            }.getOrElse {
                logger.e(it, "load rule list")
                LoadState.Error(it)
            }
        }.onEach {
            if (it is LoadState.Success) {
                pref.setSelectedRuleFilter(it.data.selectedRuleSetFilter.filterId)
            }
        }.debounce(300)
            .stateIn(viewModelScope, SharingStarted.Lazily, initialValue = LoadState.Loading)
    }

    private suspend fun getRuleList(): RuleList {
        logger.d("loadRuleList")
        val startLoadingTime = System.currentTimeMillis()
        val gvs = shortXManager.getAllGlobalVars()
        val rules = shortXManager.getAllRules()
            .filter { it.isEnabled || !state.value.onlyShowEnabled }
            .filter { it.ruleSetId == ruleSetId }
            .let(sort(state.value.ruleSortBy))
            .mapNotNull { rule ->
                runCatching {
                    ruleToRuleUM(rule, gvs)
                }.getOrElse {
                    logger.e(it, "ruleToRuleUM")
                    null
                }
            }

        if (rules.size > 3) {
            afterNavDelay(System.currentTimeMillis() - startLoadingTime)
        }

        return RuleList(rules = rules)
    }
}