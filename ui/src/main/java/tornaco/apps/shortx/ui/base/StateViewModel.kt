package tornaco.apps.shortx.ui.base

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class StateViewModel<T> :
    LifeCycleAwareViewModel() {
    private val _state by lazy { MutableStateFlow(initState()) }
    val state get() = _state.asStateFlow()

    abstract fun initState(): T

    fun updateState(block: T.() -> T) {
        _state.value = block.invoke(_state.value)
    }

}