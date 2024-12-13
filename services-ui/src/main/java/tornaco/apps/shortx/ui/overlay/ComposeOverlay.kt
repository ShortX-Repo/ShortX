package tornaco.apps.shortx.ui.overlay

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.Keep
import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.action.ShowOverlayButton
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.settings.OverlayButtonSetting

@SuppressLint("ViewConstructor")
@Keep
class ComposeOverlay(
    context: Context,
    private val action: ShowOverlayButton,
    private val contextData: Map<String, String>,
    private val localVars: LocalVars,
    private val parameters: List<FuncParameterInput>,
    private val onButtonClick: (OverlayButtonSetting) -> Unit,
    private val onButtonLongClick: (OverlayButtonSetting) -> Unit,
    initialPos: Pair<Int, Int>,
    onPosChange: (Int, Int) -> Unit,
    onTouchOutside: () -> Unit,
) : AbstractComposeOverlay(
    context = context,
    tag = action.tag,
    autoEdge = action.disableAutoEdge,
    enableGestureMoving = true,
    initialPos = initialPos,
    onPosChange = onPosChange,
    onTouchOutside = {
        onTouchOutside()
        false
    }
) {

    @Composable
    override fun ComposeContent() {
        OverlayContent(
            action = action,
            localVars = localVars,
            contextData = contextData,
            parameters = parameters,
            dragHandleMotionEvent = {
                touchListener.onTouch(
                    this@ComposeOverlay, it
                )
            },
            onButtonClick = { onButtonClick(it) },
            onButtonLongClick = { onButtonLongClick(it) },
            onCollapse = {
                if (autoEdge) {
                    rescheduleEdgeRunnable()
                }
            },
            onExpand = {}
        )
    }

}
