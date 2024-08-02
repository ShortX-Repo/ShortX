package tornaco.apps.shortx.ui.analytics

import tornaco.apps.shortx.core.BuildPropInternal

interface Analytics {
    fun logEvent(event: Event)
}

val analytics by lazy { Provider().provideAnalytics() }

sealed interface Event {
    val eventName: String
    val params: Map<String, String>

    data object AppColdStart : Event {
        override val eventName: String
            get() = "AppColdStart"
        override val params: Map<String, String>
            get() = mapOf(
                "AppVersion" to BuildPropInternal.APP_VERSION_NAME
            )
    }
}