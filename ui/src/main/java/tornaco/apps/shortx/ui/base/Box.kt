@file:OptIn(ExperimentalComposeUiApi::class)

package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@Composable
fun ClickOutsideClearFocusBox(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    Box(modifier = modifier
        .clickable(
            interactionSource = interactionSource,
            indication = null    // this gets rid of the ripple effect
        ) {
            keyboardController?.hide()
            focusManager.clearFocus(true)
        }) {
        content()
    }
}