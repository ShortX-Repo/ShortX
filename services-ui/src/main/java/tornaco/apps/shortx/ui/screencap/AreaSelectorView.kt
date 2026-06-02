package tornaco.apps.shortx.ui.screencap

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.view.WindowManager
import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.proto.common.UiThemeMode
import tornaco.apps.shortx.ui.AttachableComposeView
import tornaco.apps.shortx.ui.FlashingBorderBox
import tornaco.apps.shortx.ui.theme.ShortXServiceUITheme
import tornaco.apps.shortx.ui.theme.normalized

@SuppressLint("ViewConstructor")
class AreaSelectorView(
    context: Context,
    i18N: I18N,
    onDetach: (AttachableComposeView) -> Unit,
    private val themeMode: UiThemeMode = UiThemeMode.UiThemeMode_System,
    private val onDragEnd: () -> Unit,
    private val onSelectFullScreen: () -> Unit = {},
    val selected: (Rect) -> Unit,
) : AttachableComposeView(context, i18N, onDetach) {
    override var enableLayoutAnim = false

    @Composable
    override fun ComposeContent() {
        ShortXServiceUITheme(themeMode.normalized()) {
            FlashingBorderBox {
                RectangleCaptureScreen(
                    onSelectFullScreen = {
                        onSelectFullScreen()
                        detach(false)
                    }, onSelected = {
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
                )
            }
        }
    }

    override fun createLayoutParams(): WindowManager.LayoutParams {
        return super.createLayoutParams().apply {
            flags = (WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        }
    }
}
