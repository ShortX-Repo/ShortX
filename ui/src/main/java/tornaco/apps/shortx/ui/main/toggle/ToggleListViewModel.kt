package tornaco.apps.shortx.ui.main.toggle

import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.toggles.ToggleStateHandleByStore
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.ui.base.afterNavLongDelay
import tornaco.apps.shortx.ui.main.model.ToggleEnableState
import tornaco.apps.shortx.ui.main.model.ToggleUM
import javax.inject.Inject

@HiltViewModel
class ToggleListViewModel @Inject constructor(@ApplicationContext context: Context) :
    BaseToggleListViewModel(context) {
    private var loadToggleJob: Job? = null


    override fun loadToggleList(delay: Boolean) {
        logger.d("loadToggleList")

        loadToggleJob?.cancel()
        loadToggleJob = viewModelScope.launch {
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
                        enableState = if (it.stateHandle is_ ToggleStateHandleByStore::class.java) {
                            ToggleEnableState.Loaded(
                                shortXManager.isToggleEnabled(it.id)
                            )
                        } else {
                            ToggleEnableState.Loading
                        },
                        versionCode = it.versionCode,
                        author = it.author.name,
                        isOnlineItem = false,
                    )
                }
            }
            updateState {
                copy(isLoading = false, toggles = allToggles)
            }

            // Load toggle state for By cond.
            withContext(Dispatchers.IO) {
                allToggles.filter { it.enableState is ToggleEnableState.Loading }
                    .forEach { toggle ->
                        if (isActive) {
                            logger.d("load enableState for: ${toggle.title}")
                            val isEnabled = shortXManager.isToggleEnabled(toggle.id)
                            logger.d("load complete enableState for: ${toggle.title} $isActive")
                            if (isActive) {
                                updateState {
                                    copy(
                                        toggles = state.value.toggles.toMutableList().apply {
                                            val index = indexOfFirst { it.id == toggle.id }
                                            if (index >= 0) {
                                                val updated = get(index).copy(
                                                    enableState = ToggleEnableState.Loaded(isEnabled)
                                                )
                                                set(index, updated)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
            }
        }
    }
}