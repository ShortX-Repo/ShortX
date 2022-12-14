package tornaco.apps.shortx.ui.settings

import androidx.compose.ui.graphics.vector.ImageVector

sealed interface Preference {
    val title: String
    val summary: String?
    val icon: ImageVector?

    data class TextPreference(
        override val title: String,
        override val summary: String? = null,
        override val icon: ImageVector? = null
    ) :
        Preference

    data class SwitchPreference(
        override val title: String,
        override val summary: String? = null,
        val key: String,
        override val icon: ImageVector? = null,
    ) : Preference
}