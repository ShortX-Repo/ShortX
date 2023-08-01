package tornaco.apps.shortx.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener
import tornaco.apps.shortx.core.proto.common.StatusBarTile
import tornaco.apps.shortx.core.proto.common.StatusBarTileSetting
import tornaco.apps.shortx.ui.base.ContextViewModel
import javax.inject.Inject

data class TileSettingsState(
    val tiles: List<StatusBarTileSetting> = emptyList()
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class TileSettingsViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<TileSettingsState>(context, { TileSettingsState() }) {
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
            val tiles = shortXManager.getAllStatusBarTileSettings().sortedBy { it.tile.number }
            updateState {
                copy(tiles = tiles)
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