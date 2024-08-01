package tornaco.apps.shortx.ui.main.rule

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.github.promeg.pinyinhelper.Pinyin
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.rule.RuleSet
import tornaco.apps.shortx.ui.base.afterNavLongDelay
import tornaco.apps.shortx.ui.main.CommonSetFilter
import tornaco.apps.shortx.ui.main.model.RuleUM
import tornaco.apps.shortx.ui.main.model.ruleToRuleUM
import java.text.Collator
import java.util.Locale
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
                val gvs = shortXManager.getAllGlobalVars()
                val allRules = shortXManager.getAllRules().apply {
                    logger.i("all rules: ${this.joinToString { it.title }}")
                }.filter {
                    it.isEnabled || !state.value.onlyShowEnabled
                }
                val allRuleSets = shortXManager.getAllRuleSets()

                val selectedFilterId = pref.selectedRuleFilter.firstOrNull()
                val selectedSetOrNull =
                    selectedFilterId?.let { allRuleSets.firstOrNull { it.id == selectedFilterId } }
                if (selectedSetOrNull == null) {
                    // This set is not exists anymore. or it is prebuilt all/flat
                    val prebuiltSet = CommonSetFilter.prebuiltOfId(selectedFilterId)
                    pref.setSelectedRuleFilter(prebuiltSet.filterId)
                    updateState { copy(selectedRuleSetFilter = prebuiltSet) }
                } else {
                    updateState {
                        copy(
                            selectedRuleSetFilter = CommonSetFilter.UserSet(
                                id = selectedFilterId,
                                title = selectedSetOrNull.title
                            )
                        )
                    }
                }

                val selectedDASetFilter = state.value.selectedRuleSetFilter

                val rules = allRules
                    .filter { rule ->
                        when (selectedDASetFilter) {
                            CommonSetFilter.PinnedAll -> {
                                allRuleSets.none { set -> rule.ruleSetId == set.id }
                            }

                            CommonSetFilter.PinnedAllFlat -> {
                                true
                            }

                            is CommonSetFilter.UserSet -> {
                                rule.ruleSetId == selectedDASetFilter.id
                            }
                        }
                    }
                    .let(sort(state.value.ruleSortBy))
                    .mapNotNull { rule ->
                        runCatching {
                            ruleToRuleUM(rule, gvs)
                        }.getOrElse {
                            logger.e(it, "ruleToRuleUM")
                            null
                        }
                    }
                val sets = allRuleSets
                    .sortedWith { o1, o2 ->
                        Collator.getInstance(Locale.CHINESE).compare(o1.title, o2.title)
                    }.map { ruleSet ->
                        val subRules = allRules.filter { it.ruleSetId == ruleSet.id }
                        RuleUM(
                            updateTime = "",
                            createTime = "",
                            rule = Rule.getDefaultInstance(),
                            versionCode = 0,
                            icon = "",
                            iconColor = Color.Red.value,
                            id = ruleSet.id,
                            title = ruleSet.title,
                            titlePinyin = Pinyin.toPinyin(ruleSet.title, "").lowercase(),
                            description = ruleSet.description,
                            isRuleSet = true,
                            isEnabled = subRules.isNotEmpty() && subRules.all { it.isEnabled },
                            hasAnyEnabled = subRules.isNotEmpty() && subRules.any { it.isEnabled },
                            itemsCount = subRules.size,
                            hasAnyParameter = false
                        )
                    }
                val ruleSetFilters = listOf(
                    CommonSetFilter.PinnedAll,
                    CommonSetFilter.PinnedAllFlat
                ) + sets.map { CommonSetFilter.UserSet(it.id, it.title) }

                val finalRules = when {
                    state.value.selectedRuleSetFilter == CommonSetFilter.PinnedAll -> {
                        sets + rules
                    }

                    selectedDASetFilter == CommonSetFilter.PinnedAllFlat -> {
                        rules
                    }

                    else -> {
                        rules
                    }
                }.filter {
                    state.value.searchQuery.isBlank() || it.title.contains(
                        state.value.searchQuery,
                        true
                    ) || it.titlePinyin.contains(
                        state.value.searchQuery.lowercase(),
                        true
                    )
                }

                updateState {
                    copy(
                        rules = finalRules,
                        ruleSetFilters = ruleSetFilters,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun createRuleSet(title: String) {
        shortXManager.addOrUpdateRuleSet(
            RuleSet.newBuilder()
                .setId(defaultNewRuleSetId())
                .setTitle(title)
                .build()
        )
    }

    fun setRuleSetEnabled(id: String, enable: Boolean) {
        shortXManager.getAllRules().filter {
            it.ruleSetId == id
        }.forEach {
            shortXManager.setRuleEnabled(it.id, enable)
        }
    }

    fun dismissRuleSet(ruleSet: RuleUM, token: String?) {
        shortXManager.getAllRules().filter {
            it.ruleSetId == ruleSet.id
        }.forEach {
            moveOutSet(it, token)
        }
    }

    fun deleteRuleSet(ruleSet: RuleUM, alsoDeleteItems: Boolean, token: String?) {
        logger.i("deleteRuleSet: ${ruleSet.title}")
        if (alsoDeleteItems) {
            shortXManager.getAllRules().filter {
                it.ruleSetId == ruleSet.id
            }.forEach {
                logger.w("deleteRuleSet, Delete rule: ${it.title}")
                shortXManager.deleteRule(it.id)
            }
        }
        shortXManager.deleteRuleSet(ruleSet.id)
    }

    fun rename(rule: RuleUM, newName: String, token: String?) {
        val originalSet = shortXManager.getRuleSetById(rule.id)
        originalSet?.let {
            shortXManager.addOrUpdateRuleSet(
                RuleSet.newBuilder(originalSet).setTitle(newName).build()
            )
        }
    }

    fun selectRuleSetFilter(filter: CommonSetFilter) {
        updateState { copy(selectedRuleSetFilter = filter) }
        viewModelScope.launch {
            pref.setSelectedRuleFilter(filter.filterId)
            loadRuleList()
        }
    }
}