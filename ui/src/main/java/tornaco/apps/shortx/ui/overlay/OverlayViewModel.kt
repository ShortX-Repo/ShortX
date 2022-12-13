package tornaco.apps.shortx.ui.overlay

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tornaco.apps.shortx.ui.base.StateViewModel
import java.util.*

class OverlayViewModel : StateViewModel<String>() {
    fun start() {
        viewModelScope.launch {
            while (true) {
                updateState {
                    Date().toString()
                }
                delay(1000)
            }
        }
    }

    override fun initState(): String {
        return "X"
    }
}