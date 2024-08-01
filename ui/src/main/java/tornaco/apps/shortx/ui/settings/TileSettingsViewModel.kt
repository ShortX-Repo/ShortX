package tornaco.apps.shortx.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener
import tornaco.apps.shortx.core.proto.common.StatusBarTile
import tornaco.apps.shortx.core.proto.common.StatusBarTileSetting
import tornaco.apps.shortx.core.proto.fact.OnQSTileClick
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import tornaco.apps.shortx.ui.main.model.RuleUM
import tornaco.apps.shortx.ui.main.model.ToggleUM
import tornaco.apps.shortx.ui.main.model.ruleToRuleUM
import tornaco.apps.shortx.ui.main.toggle.toggleToToggleUM
import javax.inject.Inject

sealed interface TileUsageOwner {
    data class RuleOwner(val ruleUM: RuleUM) : TileUsageOwner
    data class ToggleOwner(val toggleUM: ToggleUM) : TileUsageOwner
}

data class TileSettingsState(
    val tiles: List<StatusBarTileSetting> = emptyList(),
    val usages: Map<StatusBarTile, MutableList<TileUsageOwner>> = emptyMap(),
    val isLoading: Boolean = false
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class TileSettingsViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<TileSettingsState>(context, { TileSettingsState() }) {
    private val onTileSettingsChangeListener =
        object : IStatusBarTileSettingsChangeListener.Stub() {
            override fun onTileSettingsChange(tileNumber: Int) {
                loadState()
            }
        }

    init {
        shortXManager.registerStatusBarTileSettingsChangeListener(onTileSettingsChangeListener)
        loadState()
    }

    override fun onCleared() {
        super.onCleared()
        shortXManager.unregisterStatusBarTileSettingsChangeListener(onTileSettingsChangeListener)
    }

    fun loadState() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            withContext(Dispatchers.IO) {
                val gvs = shortXManager.getAllGlobalVars()
                val tiles = shortXManager.getAllStatusBarTileSettings().sortedBy { it.tile.number }
                val usages = mutableMapOf<StatusBarTile, MutableList<TileUsageOwner>>()
                shortXManager.getAllRules().forEach { rule ->
                    val tileFacts = rule.factsList.filter { it is_ OnQSTileClick::class.java }
                    if (tileFacts.isNotEmpty()) {
                        val ruleUM = ruleToRuleUM(rule, gvs)
                        tileFacts.forEach {
                            val onQSTileClick = it unpack_ OnQSTileClick::class.java
                            val tile = StatusBarTile.forNumber(onQSTileClick.tileNumber)
                            val currentList = usages[tile] ?: mutableListOf()
                            currentList.add(TileUsageOwner.RuleOwner(ruleUM))
                            usages[tile] = currentList
                        }
                    }
                }
                shortXManager.getAllToggles().forEach { toggle ->
                    if (toggle.hasTileSetting()) {
                        val toggleUM = toggle.toggleToToggleUM(shortXManager)
                        val tile = StatusBarTile.forNumber(toggle.tileSetting.bondToTileNumber)
                        val currentList = usages[tile] ?: mutableListOf()
                        currentList.add(TileUsageOwner.ToggleOwner(toggleUM))
                        usages[tile] = currentList
                    }
                }

                updateState {
                    copy(tiles = tiles, usages = usages, isLoading = false)
                }
            }
        }
    }

    fun rename(tile: StatusBarTile, newName: String) {
        shortXManager.getStatusBarTileSettingsByNumber(tile.number)?.let {
            val updated = StatusBarTileSetting.newBuilder(it).setLabel(newName).build()
            shortXManager.updateStatusBarTileSetting(updated)
        }
    }

    fun setIcon(tile: StatusBarTile, icon: String) {
        shortXManager.getStatusBarTileSettingsByNumber(tile.number)?.let {
            val updated = StatusBarTileSetting.newBuilder(it).setIconName(icon).build()
            shortXManager.updateStatusBarTileSetting(updated)
        }
    }
}