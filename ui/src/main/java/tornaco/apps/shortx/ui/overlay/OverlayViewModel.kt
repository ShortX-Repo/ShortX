package tornaco.apps.shortx.ui.overlay

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tornaco.apps.shortx.ui.base.StateViewModel

data class State(val isShow: Boolean = false)

class OverlayViewModel : StateViewModel<State>() {
    fun start() {
        viewModelScope.launch {
            delay(3000)
            updateState {
                copy(isShow = false)
            }
        }
    }

    override fun initState() = State(isShow = true)
}