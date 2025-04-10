package tornaco.apps.shortx.ui.contextmenu

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.common.CustomContextMenu
import tornaco.apps.shortx.core.proto.common.CustomContextMenuSetting
import tornaco.apps.shortx.core.proto.fact.OnMenuActionTrigger
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import tornaco.apps.shortx.ui.main.model.RuleUM
import tornaco.apps.shortx.ui.main.model.ruleToRuleUM
import javax.inject.Inject

sealed interface CustomContextMenuUsageOwner {
    data class RuleOwner(val ruleUM: RuleUM) : CustomContextMenuUsageOwner
}

data class CustomContextMenuSettingsState(
    val menuSettings: List<CustomContextMenuSetting> = emptyList(),
    val usages: Map<CustomContextMenu, MutableList<CustomContextMenuUsageOwner>> = emptyMap(),
    val isLoading: Boolean = false
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class CustomContextMenuSettingsViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<CustomContextMenuSettingsState>(
        context,
        { CustomContextMenuSettingsState() }) {

    init {
        loadState()
    }

    fun loadState() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            withContext(Dispatchers.IO) {
                val gvs = shortXManager.getAllGlobalVars()
                val allMenuSettings = shortXManager.getAllContextMenuSettings()
                    .sortedBy { it.menu.number }
                val usages =
                    mutableMapOf<CustomContextMenu, MutableList<CustomContextMenuUsageOwner>>()
                shortXManager.getAllRules().forEach { rule ->
                    val menuFacts =
                        rule.factsList.filter { it is_ OnMenuActionTrigger::class.java }
                    if (menuFacts.isNotEmpty()) {
                        val ruleUM = ruleToRuleUM(rule, gvs)
                        menuFacts.forEach {
                            val contextMenuClick =
                                it unpack_ OnMenuActionTrigger::class.java
                            val menu =
                                CustomContextMenu.forNumber(contextMenuClick.menuNumber)
                            val currentList = usages[menu] ?: mutableListOf()
                            currentList.add(CustomContextMenuUsageOwner.RuleOwner(ruleUM))
                            usages[menu] = currentList
                        }
                    }
                }
                updateState {
                    copy(menuSettings = allMenuSettings, usages = usages, isLoading = false)
                }
            }
        }
    }

    fun rename(menu: CustomContextMenu, newName: String) {
        shortXManager.getContextMenuSettingsByMenuNumber(menu.number).let {
            val updated = CustomContextMenuSetting.newBuilder(it).setLabel(newName).build()
            shortXManager.updateContextMenuSetting(updated)
        }
        loadState()
    }
}