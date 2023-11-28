package tornaco.apps.shortx.ui.base.code

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import tornaco.apps.shortx.core.os.CancellationSignal
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.addrule.action.model.Action
import javax.inject.Inject

data class ShellCommandEditorState(
    val command: String = "",
    val isInSystemUI: Boolean = true,
    val isSingleShot: Boolean = false,
    val isExecuting: Boolean = false,
)

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class ShellCommandEditorViewModel @Inject constructor() : ViewModel(),
    ContainerHost<ShellCommandEditorState, String> {
    private var currentSignal: CancellationSignal? = null

    override val container: Container<ShellCommandEditorState, String> = container(
        initialState = ShellCommandEditorState()
    )

    fun initWith(shellCommand: Action.ShellCommand?) = intent {
        reduce {
            state.copy(
                command = shellCommand?.command ?: "",
                isInSystemUI = shortXManager.isInjectedShellEnabled(),
                isSingleShot = shellCommand?.singleShot ?: false,
                isExecuting = false
            )
        }
    }

    fun execute() = intent {
        currentSignal?.cancel()
        reduce {
            state.copy(isExecuting = true)
        }
        currentSignal = shortXManager.executeShellCommand(state.command) {
            val asString = it.outList.joinToString(System.lineSeparator())
            intent {
                postSideEffect(asString)
                reduce {
                    state.copy(isExecuting = false)
                }
            }
        }

    }

    fun updateCommand(command: String) = intent {
        reduce {
            state.copy(command = command)
        }
    }

    fun setSingleShot(it: Boolean) = intent {
        reduce { state.copy(isSingleShot = it) }
    }

    fun stopExecute() = intent {
        currentSignal?.cancel()
        reduce {
            state.copy(isExecuting = false)
        }
    }
}