package tornaco.apps.shortx.ui.base

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController


val LocalI18N = staticCompositionLocalOf<I18N> {
    noLocalProvidedFor("LocalI18N")
}

val LocalNavHostController = staticCompositionLocalOf<NavHostController> {
    noLocalProvidedFor("NavHostController")
}

private fun noLocalProvidedFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}