package tornaco.apps.shortx.ui.theme

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.proto.common.UiThemeMode

@Composable
fun UiThemeMode.resolveDarkTheme(): Boolean {
    return when (this) {
        UiThemeMode.UiThemeMode_Light -> false
        UiThemeMode.UiThemeMode_Dark -> true
        UiThemeMode.UNRECOGNIZED, UiThemeMode.UiThemeMode_System -> isSystemInDarkTheme()
    }
}

fun UiThemeMode.normalized(): UiThemeMode {
    return if (this == UiThemeMode.UNRECOGNIZED) {
        UiThemeMode.UiThemeMode_System
    } else {
        this
    }
}

fun UiThemeMode.resolveDarkTheme(context: Context): Boolean {
    return when (normalized()) {
        UiThemeMode.UiThemeMode_Light -> false
        UiThemeMode.UiThemeMode_Dark -> true
        UiThemeMode.UNRECOGNIZED, UiThemeMode.UiThemeMode_System -> {
            val nightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            nightMode == Configuration.UI_MODE_NIGHT_YES
        }
    }
}

fun UiThemeMode.toNightMode(): Int {
    return when (normalized()) {
        UiThemeMode.UiThemeMode_Light -> AppCompatDelegate.MODE_NIGHT_NO
        UiThemeMode.UiThemeMode_Dark -> AppCompatDelegate.MODE_NIGHT_YES
        UiThemeMode.UNRECOGNIZED, UiThemeMode.UiThemeMode_System -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }
}
