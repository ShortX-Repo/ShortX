package tornaco.apps.shortx.ui.main.toggle

import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import tornaco.apps.shortx.ui.base.afterNavLongDelay
import javax.inject.Inject

@HiltViewModel
class ToggleListViewModel @Inject constructor(@ApplicationContext context: Context) :
    BaseToggleListViewModel(context) {
    override fun loadToggleList(delay: Boolean) {
        logger.d("loadToggleList")
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            if (delay) afterNavLongDelay()
            val allToggles = shortXManager.getAllToggles().map {

            }
        }
    }
}