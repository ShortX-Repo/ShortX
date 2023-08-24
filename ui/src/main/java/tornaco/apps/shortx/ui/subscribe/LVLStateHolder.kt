package tornaco.apps.shortx.ui.subscribe

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.ui.subscribe.util.getLVLToken
import tornaco.apps.shortx.ui.subscribe.util.isLVLChecked


object LVLStateHolder {
    data class State(val isSubscribed: Boolean, val token: String?)

    private val _subscribeState = MutableStateFlow(State(false, null))
    val subscribeState: StateFlow<State> get() = _subscribeState.asStateFlow()

    suspend fun setSubscribed(token: String) {
        _subscribeState.emit(State(true, token))
    }

    suspend fun setNotSubscribed() {
        _subscribeState.emit(State(false, null))
    }

    fun init(context: Context) {
        runBlocking {
            setNotSubscribed()

            isLVLChecked(context) { bool ->
                if (bool) {
                    getLVLToken(context) { token ->
                        if (token.isNotEmpty()) {
                            setSubscribed(token)
                        }
                    }
                }
            }
        }
    }

}