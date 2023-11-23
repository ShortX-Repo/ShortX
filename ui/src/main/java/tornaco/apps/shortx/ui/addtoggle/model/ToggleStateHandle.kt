package tornaco.apps.shortx.ui.addtoggle.model

sealed interface ToggleStateHandle {
    data object ByStore : ToggleStateHandle
    data object ByCondition : ToggleStateHandle
}