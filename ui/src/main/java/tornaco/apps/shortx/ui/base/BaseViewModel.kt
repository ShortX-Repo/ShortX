package tornaco.apps.shortx.ui.base

import android.annotation.SuppressLint
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger

@SuppressLint("StaticFieldLeak")
abstract class BaseViewModel<T> constructor(private val context: Context) :
    LifeCycleAwareViewModel() {
    protected val shortXManager by lazy { context.shortXManager }

    private val _state by lazy { MutableStateFlow(initState()) }
    val state get() = _state.asStateFlow()

    protected val logger = Logger(javaClass.simpleName)

    abstract fun initState(): T

    fun updateState(block: T.() -> T) {
        _state.value = block.invoke(_state.value)
    }

}