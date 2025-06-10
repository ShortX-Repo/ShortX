package tornaco.apps.shortx.ui.base

import androidx.compose.runtime.staticCompositionLocalOf

val LocalUiSettings = staticCompositionLocalOf<UiSettings> {
    error("LocalUiSettings not provided")
}

data class UiSettings(
    val uiColorfulCard: Boolean,
    val uiExperimental: Boolean,
)