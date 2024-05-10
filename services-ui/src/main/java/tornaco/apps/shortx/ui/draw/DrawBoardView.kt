package tornaco.apps.shortx.ui.draw

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.ui.BaseComposeView
import tornaco.apps.shortx.ui.base.rememberCommonDialogState
import tornaco.apps.shortx.ui.theme.ShortXServiceUITheme

@SuppressLint("ViewConstructor")
class DrawBoardView(
    context: Context,
    i18N: I18N,
    onDetach: (BaseComposeView) -> Unit
) : BaseComposeView(context, i18N, onDetach) {
    @Composable
    override fun ComposeContent() {
        DrawBoardContent {
            detach()
        }
    }

    @Composable
    private fun DrawBoardContent(onBack: () -> Unit) {
        ShortXServiceUITheme {
            val controlBarState = rememberCommonDialogState()
            LaunchedEffect(controlBarState) {
                delay(250)
                controlBarState.show()
            }

            val requester = remember { FocusRequester() }

            DrawBoard(
                modifier = Modifier
                    .fillMaxSize()
                    .onKeyEvent {
                        if (it.key == Key.VolumeDown || it.key == Key.VolumeUp) {
                            controlBarState.show()
                        }
                        if (it.key == Key.Back) {
                            onBack()
                        }
                        true
                    }
                    .focusRequester(requester)
                    .focusable(),
                controlBarState
            )

            LaunchedEffect(Unit) {
                requester.requestFocus()
            }
        }
    }
}

