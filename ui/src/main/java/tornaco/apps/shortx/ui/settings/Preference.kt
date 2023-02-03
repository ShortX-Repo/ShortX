package tornaco.apps.shortx.ui.settings

import androidx.compose.ui.graphics.vector.ImageVector

sealed interface Preference {
    val title: String
    val summary: String?
    val icon: ImageVector?

    object Divider : Preference {
        override val title: String = "Divider"
        override val summary: String? = null
        override val icon: ImageVector? = null
    }

    data class Category(
        override val title: String,
    ) : Preference {
        override val summary: String? = null
        override val icon: ImageVector? = null
    }

    data class TextPreference(
        override val title: String,
        override val summary: String? = null,
        override val icon: ImageVector? = null,
        val onClick: (() -> Unit)? = null
    ) : Preference

    data class SwitchPreference(
        override val title: String,
        override val summary: String? = null,
        override val icon: ImageVector? = null,
        val value: Boolean,
        val onCheckChanged: (Boolean) -> Unit
    ) : Preference

    data class ExpandablePreference(
        override val title: String,
        override val summary: String? = null,
        override val icon: ImageVector? = null,
        val preferences: List<Preference>
    ) : Preference
}