package tornaco.apps.shortx.ui.base

import androidx.compose.ui.Modifier

fun Modifier.thenIf(modifier: Modifier, boolean: Boolean): Modifier {
    return if (boolean) return this.then(modifier) else this
}