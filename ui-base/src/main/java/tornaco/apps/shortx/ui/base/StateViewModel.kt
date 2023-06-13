package tornaco.apps.shortx.ui.base

abstract class StateViewModel<T>(
    initState: () -> T,
    private val stateImpl: WithStateImpl<T> = WithStateImpl(initState)
) :
    LifeCycleAwareViewModel(), WithState<T> by stateImpl {
}