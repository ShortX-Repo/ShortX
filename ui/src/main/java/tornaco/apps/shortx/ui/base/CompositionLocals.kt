@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.base

import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController


val LocalI18N = staticCompositionLocalOf<I18N> {
    noLocalProvidedFor("LocalI18N")
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

private fun noLocalProvidedFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}