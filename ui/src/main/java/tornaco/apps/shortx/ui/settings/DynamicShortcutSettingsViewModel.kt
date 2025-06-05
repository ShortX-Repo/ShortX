package tornaco.apps.shortx.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.common.DynamicShortcut
import tornaco.apps.shortx.core.proto.common.DynamicShortcutSetting
import tornaco.apps.shortx.core.proto.fact.OnStartDynamicShortcut
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import tornaco.apps.shortx.ui.main.model.RuleUM
import tornaco.apps.shortx.ui.main.model.ruleToRuleUM
import javax.inject.Inject

data class ShortcutsSettingsState(
    val shortcuts: List<DynamicShortcutSetting> = emptyList(),
    val usages: Map<DynamicShortcut, MutableList<RuleUM>> = emptyMap(),
    val isLoading: Boolean = false
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class DynamicShortcutSettingsViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<ShortcutsSettingsState>(context, { ShortcutsSettingsState() }) {
    init {
        loadState()
    }

    fun loadState() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            withContext(Dispatchers.IO) {
                val gvs = shortXManager.getAllGlobalVars()
                val shortcuts =
                    shortXManager.getAllDynamicShortcutSettings().sortedBy { it.shortcut.number }
                val usages = mutableMapOf<DynamicShortcut, MutableList<RuleUM>>()
                shortXManager.getAllRules().forEach { rule ->
                    val tileFacts =
                        rule.factsList.filter { it is_ OnStartDynamicShortcut::class.java }
                    if (tileFacts.isNotEmpty()) {
                        val ruleUM = ruleToRuleUM(rule, gvs)
                        tileFacts.forEach {
                            val onStartShortcut = it unpack_ OnStartDynamicShortcut::class.java
                            val tile = DynamicShortcut.forNumber(onStartShortcut.shortcutNumber)
                            val currentList = usages[tile] ?: mutableListOf()
                            currentList.add(ruleUM)
                            usages[tile] = currentList
                        }
                    }
                }
                updateState {
                    copy(shortcuts = shortcuts, usages = usages, isLoading = false)
                }
            }
        }
    }

    fun rename(sc: DynamicShortcut, newName: String) {
        shortXManager.getDynamicShortcutSettingsByNumber(sc.number).let {
            val updated = DynamicShortcutSetting.newBuilder(it).setLabel(newName).build()
            shortXManager.updateDynamicShortcutSetting(updated)
        }
    }

    fun setIcon(sc: DynamicShortcut, icon: String) {
        shortXManager.getDynamicShortcutSettingsByNumber(sc.number).let {
            val updated = DynamicShortcutSetting.newBuilder(it).setIconName(icon).build()
            shortXManager.updateDynamicShortcutSetting(updated)
        }
    }
}