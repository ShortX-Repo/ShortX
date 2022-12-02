package tornaco.apps.shortx.ui.base

import androidx.compose.runtime.staticCompositionLocalOf


val LocalI18N = staticCompositionLocalOf<I18N> {
    noLocalProvidedFor("LocalI18N")
}

private fun noLocalProvidedFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}