package tornaco.apps.shortx.ui.screencap

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.ui.BaseComposeView
import tornaco.apps.shortx.ui.theme.ShortXServiceUITheme

@SuppressLint("ViewConstructor")
class AreaSelectorView(
    context: Context,
    i18N: I18N,
    onDetach: (BaseComposeView) -> Unit,
    val selected: (Rect) -> Unit,
) : BaseComposeView(context, i18N, onDetach) {

    @Composable
    override fun ComposeContent() {
        ShortXServiceUITheme {
            BackHandler {
                detach()
            }
            RectangleCaptureScreen {
                selected(
                    Rect(
                        it.left.toInt(),
                        it.top.toInt(),
                        it.right.toInt(),
                        it.bottom.toInt()
                    )
                )
            }
        }
    }
}