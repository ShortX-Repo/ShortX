package tornaco.apps.shortx.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.ui.BaseComposeView
import tornaco.apps.shortx.ui.base.ColorDefaults
import tornaco.apps.shortx.ui.theme.ShortXServiceUITheme

@SuppressLint("ViewConstructor")
abstract class DialogView(
    context: Context,
    i18N: I18N,
    onDetach: (BaseComposeView) -> Unit,
) : BaseComposeView(context, i18N, onDetach) {

    @Composable
    override fun ComposeContent() {
        ShortXServiceUITheme {
            val interactionSource = remember { MutableInteractionSource() }
            val requester = remember { FocusRequester() }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onKeyEvent {
                        if (it.key == Key.Back) {
                            detach()
                        }
                        true
                    }
                    .focusRequester(requester)
                    .focusable()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = {
                            detach()
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                LaunchedEffect(Unit) {
                    requester.requestFocus()
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .fillMaxHeight(0.7f),
                    shape = RoundedCornerShape(24.dp),
                    color = ColorDefaults.backgroundSurfaceColor(),
                    shadowElevation = 8.dp,
                ) {
                    DialogContent()
                }
            }
        }
    }

    @Composable
    abstract fun DialogContent()
}