package tornaco.apps.shortx.ui.screencap

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.ui.BaseComposeView
import tornaco.apps.shortx.ui.theme.ShortXServiceUITheme

@SuppressLint("ViewConstructor")
class AreaSelectorView(
    context: Context,
    i18N: I18N,
    onDetach: (BaseComposeView) -> Unit,
    private val onDragEnd: () -> Unit,
    val selected: (Rect) -> Unit,
) : BaseComposeView(context, i18N, onDetach) {

    @Composable
    override fun ComposeContent() {
        ShortXServiceUITheme {
            Box(
                Modifier
                    .fillMaxSize()
                    .onKeyEvent {
                        if (it.key == Key.Back) {
                            detach()
                        }
                        true
                    }) {
                RectangleCaptureScreen(onDragEnd) {
                    selected(
                        Rect(
                            it.left.toInt(),
                            it.top.toInt(),
                            it.right.toInt(),
                            it.bottom.toInt()
                        )
                    )
                    detach(false)
                }
            }
        }
    }
}