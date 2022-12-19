package tornaco.apps.shortx.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.ui.base.ContextViewModel
import tornaco.apps.shortx.ui.overlay.BUTTON_COUNT
import javax.inject.Inject


data class SettingsState(
    val buttonIcons: List<String>
)

private const val DEFAULT_ICON_NAME = BuildProp.DEFAULT_OVERLAY_BUTTON_ICON

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class SettingsViewModel @Inject constructor(@ApplicationContext private val context: Context) :
    ContextViewModel<SettingsState>(context) {

    override fun initState(): SettingsState {
        return SettingsState(buttonIcons = mutableListOf<String>().apply {
            repeat(BUTTON_COUNT) {
                add(DEFAULT_ICON_NAME)
            }
        })
    }

    fun loadState() {

    }

    fun setIconForButtonIndex(index: Int, name: String) {
        updateState {
            copy(buttonIcons = buttonIcons.toMutableList().apply {
                add(index, name)
            })
        }
    }

}