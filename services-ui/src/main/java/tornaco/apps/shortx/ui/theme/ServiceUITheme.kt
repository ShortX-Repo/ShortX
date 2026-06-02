package tornaco.apps.shortx.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import tornaco.apps.shortx.core.proto.common.UiThemeMode
import tornaco.apps.shortx.ui.base.LocalShortXColorSchema
import tornaco.apps.shortx.ui.theme.resolveDarkTheme


@Composable
fun ShortXServiceUITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }

        darkTheme -> DarkColors
        else -> LightColors
    }
    val shortXColorScheme = when {
        darkTheme -> ShortXColorScheme(
            checkedThumb = Color.White,
            checkedTrack = colorScheme.primaryContainer,
            uncheckedThumb = Color(0xFFEAEAEA),
            uncheckedTrack = Color(0xFF777777),
        )

        else -> ShortXColorScheme(
            checkedThumb = Color.White,
            checkedTrack = colorScheme.primary,
            uncheckedThumb = Color(0xFFfafafa),
            uncheckedTrack = Color(0xFFdddddd),
        )
    }

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()

    DisposableEffect(systemUiController, useDarkIcons) {
        // Update all of the system bar colors to be transparent, and use
        // dark icons if we're in light theme
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
        )

        // setStatusBarColor() and setNavigationBarColor() also exist

        onDispose {}
    }

    CompositionLocalProvider(LocalShortXColorSchema provides shortXColorScheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

@Composable
fun ShortXServiceUITheme(
    themeMode: UiThemeMode,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    ShortXServiceUITheme(
        darkTheme = themeMode.resolveDarkTheme(),
        dynamicColor = dynamicColor,
        content = content,
    )
}
