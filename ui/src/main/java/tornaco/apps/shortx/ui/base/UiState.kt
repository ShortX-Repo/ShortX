package tornaco.apps.shortx.ui.base

sealed interface UiState<out R> {
    object Loading : UiState<Nothing>
    data class Loaded<out T>(val data: T) : UiState<T>
    data class Error(val err: Throwable) : UiState<Nothing>
}