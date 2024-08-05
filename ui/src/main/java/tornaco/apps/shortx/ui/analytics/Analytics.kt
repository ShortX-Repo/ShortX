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
            get() = "cold_start"
        override val params: Map<String, String>
            get() = mapOf(
                "app_version" to BuildPropInternal.APP_VERSION_NAME
            )
    }

    data class AddAction(val type: String) : Event {
        override val eventName: String
            get() = "add_action"
        override val params: Map<String, String>
            get() = mapOf("type" to type)
    }

    data class AddFact(val type: String) : Event {
        override val eventName: String
            get() = "add_fact"
        override val params: Map<String, String>
            get() = mapOf("type" to type)
    }

    data class AddCondition(val type: String) : Event {
        override val eventName: String
            get() = "add_condition"
        override val params: Map<String, String>
            get() = mapOf("type" to type)
    }
}