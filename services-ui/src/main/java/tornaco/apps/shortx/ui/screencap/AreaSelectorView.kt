package tornaco.apps.shortx.ui.screencap

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.ui.BaseComposeView
import tornaco.apps.shortx.ui.FlashingBorderBox
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
            FlashingBorderBox {
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