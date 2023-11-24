package tornaco.apps.shortx.ui.main.toggle

import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.ui.base.afterNavLongDelay
import tornaco.apps.shortx.ui.main.model.ToggleUM
import javax.inject.Inject

@HiltViewModel
class ToggleListViewModel @Inject constructor(@ApplicationContext context: Context) :
    BaseToggleListViewModel(context) {
    override fun loadToggleList(delay: Boolean) {
        logger.d("loadToggleList")
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            if (delay) afterNavLongDelay()
            val allToggles = withContext(Dispatchers.IO) {
                shortXManager.getAllToggles().map {
                    ToggleUM(
                        id = it.id,
                        title = it.title,
                        description = it.description,
                        updateTime = "",
                        createTime = "",
                        isEnabled = shortXManager.isToggleEnabled(it.id),
                        versionCode = it.versionCode,
                        author = it.author.name,
                        isOnlineItem = false,
                    )
                }
            }
            updateState {
                copy(isLoading = false, toggles = allToggles)
            }
        }
    }
}