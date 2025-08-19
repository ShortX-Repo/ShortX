package tornaco.apps.shortx.ui.base

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class StateViewModel<STATE, EFFECT>(
    initState: () -> STATE,
    private val stateImpl: WithStateImpl<STATE> = WithStateImpl(initState)
) :
    LifeCycleAwareViewModel(), WithState<STATE> by stateImpl {

    private val _sideEffect = MutableSharedFlow<EFFECT>()
    val sideEffect = _sideEffect.asSharedFlow()

    fun postSideEffect(effect: EFFECT) {
        viewModelScope.launch { _sideEffect.emit(effect) }
    }
}


@SuppressLint("ComposableNaming")
@Composable
fun <STATE, EFFECT> StateViewModel<STATE, EFFECT>.handleSideEffect(onEffect: (EFFECT) -> Unit) {
    LaunchedEffect(this) {
        sideEffect.collectLatest {
            onEffect(it)
        }
    }
}