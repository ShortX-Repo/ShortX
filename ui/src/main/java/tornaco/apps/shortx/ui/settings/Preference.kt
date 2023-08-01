package tornaco.apps.shortx.ui.settings


sealed interface Preference {
    val title: String
    val summary: String?
    val hasLongSummary: Boolean
    val icon: String?
    val badge: String?

    object Divider : Preference {
        override val title: String = "Divider"
        override val summary: String? = null
        override val hasLongSummary: Boolean
            get() = false
        override val icon: String? = null
        override val badge: String? = null
    }

    data class Category(
        override val title: String,
    ) : Preference {
        override val summary: String? = null
        override val hasLongSummary: Boolean
            get() = false
        override val icon: String? = null
        override val badge: String? = null
    }

    data class TextPreference(
        override val title: String,
        override val summary: String? = null,
        override val hasLongSummary: Boolean = false,
        override val icon: String? = null,
        override val badge: String? = null,
        val onClick: (() -> Unit)? = null,
    ) : Preference

    data class SwitchPreference(
        override val title: String,
        override val summary: String? = null,
        override val hasLongSummary: Boolean = false,
        override val icon: String? = null,
        override val badge: String? = null,
        val value: Boolean,
        val onCheckChanged: (Boolean) -> Unit
    ) : Preference

    data class ExpandablePreference(
        override val title: String,
        override val summary: String? = null,
        override val hasLongSummary: Boolean = false,
        override val icon: String? = null,
        override val badge: String? = null,
        val preferences: List<Preference>,
        val onClick: (() -> Unit)? = null
    ) : Preference
}