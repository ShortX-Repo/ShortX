package tornaco.apps.shortx.ui.sidebar

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.action.ShowSideBar
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.settings.OverlayButtonSetting
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util._dp
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShapeXL
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.overlay.AbstractComposeOverlay
import tornaco.apps.shortx.ui.theme.NoteTextColor
import tornaco.apps.shortx.ui.theme.ShortXServiceUITheme

private const val DRAG_HANDLE_W = 4
private const val DRAG_HANDLE_H = 64
private const val DRAG_HANDLE_H_PADDING = 12

private const val BAR_W = 68
private const val BAR_H = 320

@SuppressLint("ViewConstructor")
class SideBarView(
    private val action: ShowSideBar,
    private val contextData: Map<String, String>,
    private val localVars: LocalVars,
    private val parameters: List<FuncParameterInput>,
    private val onButtonClick: (OverlayButtonSetting) -> Unit,
    private val onButtonLongClick: (OverlayButtonSetting) -> Unit,
    private val expandState: MutableStateFlow<Boolean> = MutableStateFlow(false),
    context: Context,
    initialPos: Pair<Int, Int>,
    onPosChange: (Int, Int) -> Unit,
) : AbstractComposeOverlay(
    context = context,
    tag = "SideBar",
    autoEdge = false,
    enableGestureMoving = false,
    initialPos = initialPos,
    onPosChange = onPosChange,
    onTouchOutside = {
        expandState.value = false
        true
    }) {

    @Composable
    override fun ComposeContent() {
        ShortXServiceUITheme {
            val isExpanded by expandState.collectAsState()
            AnimatedContent(isExpanded,
                transitionSpec = {
                    fadeIn(animationSpec = tween(150, 150)) togetherWith
                            fadeOut(animationSpec = tween(150)) using
                            SizeTransform { initialSize, targetSize ->
                                if (targetState) {
                                    keyframes {
                                        // Expand horizontally first.
                                        IntSize(targetSize.width, initialSize.height) at 150
                                        durationMillis = 300
                                    }
                                } else {
                                    keyframes {
                                        // Shrink vertically first.
                                        IntSize(initialSize.width, targetSize.height) at 150
                                        durationMillis = 300
                                    }
                                }
                            }
                }) {
                if (it) {
                    Bar(onDrag = {
                        updateSize(DRAG_HANDLE_W._dp + 2 * (DRAG_HANDLE_H_PADDING._dp) to DRAG_HANDLE_H._dp)
                        expandState.value = false
                    })
                } else {
                    DragHandle(onDrag = {
                        updateSize(BAR_W._dp to BAR_H._dp)
                        expandState.value = true
                    })
                }
            }
        }
    }
}

@Composable
private fun Bar(onDrag: () -> Unit) {
    Surface(
        tonalElevation = 0.dp,
        color = MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
        shape = ShortXCardRoundedCornerShapeXL,
        modifier = Modifier
            .width(BAR_W.dp)
            .height(BAR_H.dp)
            .clip(shape = ShortXCardRoundedCornerShapeXL)
            .shadow(
                elevation = 8.dp, // 阴影高度
                shape = ShortXCardRoundedCornerShapeXL, // 圆角形状
                clip = true // 是否裁剪内容
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        val (dx, dy) = dragAmount
                        when {
                            dx > 0 -> println("Dragging right by $dx")
                            dx < 0 -> println("Dragging left by ${-dx}")
                            dy > 0 -> println("Dragging down by $dy")
                            dy < 0 -> println("Dragging up by ${-dy}")
                        }
                        onDrag()
                        change.consume()
                    }
                )
            }
    ) {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            items(20) {
                ColoredIcon {
                    RemixIcon(Remix.System.settings_fill)
                }
                StandardSpacer()
            }
        }
    }
}

@Composable
private fun DragHandle(
    onDrag: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    color: Color = NoteTextColor.copy(alpha = 0.8f),
) {
    Surface(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        val (dx, dy) = dragAmount
                        when {
                            dx > 0 -> println("Dragging right by $dx")
                            dx < 0 -> println("Dragging left by ${-dx}")
                            dy > 0 -> println("Dragging down by $dy")
                            dy < 0 -> println("Dragging up by ${-dy}")
                        }
                        onDrag()
                        change.consume()
                    }
                )
            },
        color = color,
        shape = shape
    ) {
        Box(Modifier.size(width = DRAG_HANDLE_W.dp, height = DRAG_HANDLE_H.dp))
    }
}