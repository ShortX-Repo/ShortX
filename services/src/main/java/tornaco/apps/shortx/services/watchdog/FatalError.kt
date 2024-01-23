package tornaco.apps.shortx.services.watchdog

sealed interface FatalError {
    val error: Throwable

    data class UiAutomationConnectAlreadyRegisteredError(override val error: Throwable) : FatalError
    data class UnknownError(override val error: Throwable) : FatalError
}