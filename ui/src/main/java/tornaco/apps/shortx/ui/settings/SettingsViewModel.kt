package tornaco.apps.shortx.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.ui.base.ContextViewModel
import javax.inject.Inject

data class SettingsState(
    val isEnabled: Boolean = false
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class SettingsViewModel @Inject constructor(@ApplicationContext private val context: Context) :
    ContextViewModel<SettingsState>(context) {
    override fun initState(): SettingsState {
        return SettingsState()
    }


}