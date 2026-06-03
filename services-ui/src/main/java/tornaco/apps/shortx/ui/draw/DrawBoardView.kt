package tornaco.apps.shortx.ui.draw

import android.annotation.SuppressLint
import android.content.Context
import android.view.WindowManager
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
import tornaco.apps.shortx.core.proto.common.UiThemeMode
import tornaco.apps.shortx.ui.AttachableComposeView
import tornaco.apps.shortx.ui.base.rememberCommonDialogState
import tornaco.apps.shortx.ui.theme.ShortXServiceUITheme
import tornaco.apps.shortx.ui.theme.normalized

@SuppressLint("ViewConstructor")
class DrawBoardView(
    context: Context,
    i18N: I18N,
    private val themeMode: UiThemeMode = UiThemeMode.UiThemeMode_System,
    onDetach: (AttachableComposeView) -> Unit
) : AttachableComposeView(context, i18N, onDetach) {
    @Composable
    override fun ComposeContent() {
        DrawBoardContent {
            detach()
        }
    }

    @Composable
    private fun DrawBoardContent(onBack: () -> Unit) {
        ShortXServiceUITheme(themeMode.normalized()) {
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

    override fun createLayoutParams(): WindowManager.LayoutParams {
        return super.createLayoutParams().apply {
            flags = (WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        }
    }
}
