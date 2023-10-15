package tornaco.apps.shortx.ui.base

import android.app.Activity
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.ui.theme.ShortXColorScheme


val LocalI18N = staticCompositionLocalOf<I18N> {
    noLocalProvidedFor("LocalI18N")
}

val LocalActivity = staticCompositionLocalOf<Activity> {
    noLocalProvidedFor("LocalActivity")
}

val LocalNavHostController = staticCompositionLocalOf<NavHostController> {
    noLocalProvidedFor("NavHostController")
}

val LocalDrawerState = staticCompositionLocalOf<DrawerState> {
    noLocalProvidedFor("DrawerState")
}

val LocalAppName = staticCompositionLocalOf<String> {
    noLocalProvidedFor("LocalAppName")
}

val LocalShortXColorSchema = staticCompositionLocalOf<ShortXColorScheme> {
    noLocalProvidedFor("LocalShortXColorSchema")
}

val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
    noLocalProvidedFor("LocalSnackbarHostState")
}

val LocalShortXProps = staticCompositionLocalOf<List<String>> {
    // LocalShortXProps
    noLocalProvidedFor("ShortX")
}

private fun noLocalProvidedFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}