package tornaco.apps.shortx.ui.base.code.js

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import tornaco.apps.shortx.core.proto.common.CoroutineContext
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.code.ParcelableCode
import javax.inject.Inject

data class JSEditorState(
    val expression: String = "",
    val coroutineContext: CoroutineContext = CoroutineContext.CoroutineContext_Default,
    val isExecuting: Boolean = false,
)

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class JSEditorViewModel @Inject constructor() : ViewModel(),
    ContainerHost<JSEditorState, String> {
    override val container: Container<JSEditorState, String> = container(
        initialState = JSEditorState()
    )

    fun initWith(js: ParcelableCode?) = intent {
        reduce {
            state.copy(
                expression = js?.expression ?: "",
                coroutineContext = js?.coroutineContext
                    ?: CoroutineContext.CoroutineContext_Default,
                isExecuting = false
            )
        }
    }

    fun execute() = intent {
        reduce {
            state.copy(isExecuting = true)
        }
        val res = shortXManager.executeJS(state.expression)
        postSideEffect(res)
        reduce {
            state.copy(isExecuting = false)
        }
    }

    fun updateExpression(expression: String) = intent {
        reduce {
            state.copy(expression = expression)
        }
    }

    fun setCoroutineContext(context: CoroutineContext) = intent {
        reduce {
            state.copy(coroutineContext = context)
        }
    }
}