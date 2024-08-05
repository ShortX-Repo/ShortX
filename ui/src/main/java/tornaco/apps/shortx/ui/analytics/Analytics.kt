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

    data class AddAction(val type: String) : Event {
        override val eventName: String
            get() = "AddAction"
        override val params: Map<String, String>
            get() = mapOf("Type" to type)
    }

    data class AddFact(val type: String) : Event {
        override val eventName: String
            get() = "AddFact"
        override val params: Map<String, String>
            get() = mapOf("Type" to type)
    }

    data class AddCondition(val type: String) : Event {
        override val eventName: String
            get() = "AddCondition"
        override val params: Map<String, String>
            get() = mapOf("Type" to type)
    }
}