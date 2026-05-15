package tornaco.apps.shortx.ui.base.code.mvel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import tornaco.apps.shortx.core.proto.common.CoroutineContext
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.code.ParcelableCode
import javax.inject.Inject

data class MVELEditorState(
    val expression: String = "",
    val coroutineContext: CoroutineContext = CoroutineContext.CoroutineContext_Default,
    val parameterInputs: List<FuncParameterInput> = emptyList(),
    val isExecuting: Boolean = false,
)

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class MVELEditorViewModel @Inject constructor() : ViewModel(),
    ContainerHost<MVELEditorState, String> {
    override val container: Container<MVELEditorState, String> = container(
        initialState = MVELEditorState()
    )

    fun initWith(code: ParcelableCode?) = intent {
        reduce {
            state.copy(
                expression = code?.expression ?: "",
                coroutineContext = code?.coroutineContext
                    ?: CoroutineContext.CoroutineContext_Default,
                parameterInputs = code?.parameterInputs ?: emptyList(),
                isExecuting = false
            )
        }
    }

    fun execute() = intent {
        reduce {
            state.copy(isExecuting = true)
        }
        val res = shortXManager.executeMVEL(
            expression = state.expression,
            context = state.coroutineContext,
            parameterInputs = state.parameterInputs
        )
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
