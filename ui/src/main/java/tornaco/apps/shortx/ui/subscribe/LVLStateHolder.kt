package tornaco.apps.shortx.ui.subscribe

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import tornaco.apps.shortx.ui.subscribe.util.getLVLToken


object LVLStateHolder : ContainerHost<LVLStateHolder.State, LVLStateHolder.Effect> {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    data class State(val isSubscribed: Boolean, val token: String?)
    sealed interface Effect {
        data class StateChanged(val state: State) : Effect

        // FeatureAccessBlocked
        data object FAB : Effect
    }

    override val container: Container<State, Effect> = scope.container(
        initialState = State(false, null)
    )

    fun setSubscribed(token: String) = intent {
        reduce {
            state.copy(isSubscribed = true, token = token)
        }
        postSideEffect(Effect.StateChanged(State(isSubscribed = true, token = token)))
    }

    fun setNotSubscribed() = intent {
        reduce {
            state.copy(isSubscribed = false, token = null)
        }
        postSideEffect(Effect.StateChanged(State(isSubscribed = false, token = null)))
    }

    fun fab() = intent {
        postSideEffect(Effect.FAB)
    }

    fun init(context: Context) {
        runBlocking {
            setNotSubscribed()
            getLVLToken { token ->
                if (!token.isNullOrEmpty()) {
                    setSubscribed(token)
                }
            }
        }
    }
}