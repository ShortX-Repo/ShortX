package tornaco.apps.shortx.ui.main.rule

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.rule.RuleSet
import tornaco.apps.shortx.ui.base.afterNavLongDelay
import tornaco.apps.shortx.ui.main.model.RuleUM
import tornaco.apps.shortx.ui.main.model.ruleToRuleUM
import java.text.Collator
import java.util.Locale
import java.util.UUID
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
                val allRules = shortXManager.getAllRules()
                val rules = allRules
                    .filter { it.ruleSetId.isNullOrEmpty() }
                    .let(sort(state.value.ruleSortBy))
                    .mapNotNull { rule ->
                        runCatching {
                            ruleToRuleUM(rule, activeJobs)
                        }.getOrElse {
                            logger.e(it, "ruleToRuleUM")
                            null
                        }
                    }
                val sets = shortXManager.getAllRuleSets()
                    .sortedWith { o1, o2 ->
                        Collator.getInstance(Locale.CHINESE).compare(o1.title, o2.title)
                    }.map { ruleSet ->
                        val subRules = allRules.filter { it.ruleSetId == ruleSet.id }
                        RuleUM(
                            updateTime = "",
                            createTime = "",
                            runningInsCount = 0,
                            rule = Rule.getDefaultInstance(),
                            versionCode = 0,
                            factIcon = "",
                            factIconColor = Color.Red.value,
                            actionIcon = "",
                            actionIconColor = Color.Red.value,
                            id = ruleSet.id,
                            title = ruleSet.title,
                            description = ruleSet.description,
                            isRuleSet = true,
                            isEnabled = subRules.isNotEmpty() && subRules.all { it.isEnabled },
                            hasAnyEnabled = subRules.isNotEmpty() && subRules.any { it.isEnabled },
                        )
                    }
                updateState {
                    copy(rules = sets + rules, isLoading = false)
                }
            }
        }
    }

    fun createRuleSet(title: String) {
        shortXManager.addOrUpdateRuleSet(
            RuleSet.newBuilder()
                .setId("RS-${UUID.randomUUID()}")
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

    fun rename(rule: RuleUM, newName: String, token: String?) {
        val originalSet = shortXManager.getRuleSetById(rule.id)
        originalSet?.let {
            shortXManager.addOrUpdateRuleSet(
                RuleSet.newBuilder(originalSet).setTitle(newName).build()
            )
        }
    }
}