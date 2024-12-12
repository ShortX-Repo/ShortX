package tornaco.apps.shortx.ui.sidebar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util._dp
import tornaco.apps.shortx.core.util.windowManager
import tornaco.apps.shortx.ui.AttachableComposeView
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShapeXL
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.theme.ShortXServiceUITheme

private const val DRAG_HANDLE_W = 6
private const val DRAG_HANDLE_H = 72
private const val DRAG_HANDLE_H_PADDING = 12

private const val BAR_W = 72
private const val BAR_H = 200

@SuppressLint("ViewConstructor")
class SideBarView(
    context: Context,
    i18N: I18N,
    onDetach: (AttachableComposeView) -> Unit,
) : AttachableComposeView(context, i18N, onDetach) {
    override var enableLayoutAnim = false

    @Composable
    override fun ComposeContent() {
        ShortXServiceUITheme {
            var isExpanded by remember {
                mutableStateOf(false)
            }
            AnimatedContent(isExpanded) {
                if (it) {
                    Bar(onDrag = {
                        updateSize(DRAG_HANDLE_W._dp + 2 * (DRAG_HANDLE_H_PADDING._dp) to DRAG_HANDLE_H._dp)
                        isExpanded = false
                    })
                } else {
                    DragHandle(onDrag = {
                        updateSize(BAR_W._dp to . _dp)
                        isExpanded = true
                    })
                }
            }
        }
    }

    fun updateSize(size: Pair<Int, Int>) {
        post {
            lp.width = size.first
            lp.height = size.second
            context.windowManager.updateViewLayout(this, lp)
        }
    }

    override fun createLayoutParams(): WindowManager.LayoutParams {
        val lp = WindowManager.LayoutParams()
        lp.x = 80
        lp.y = 800
        lp.gravity = Gravity.START or Gravity.TOP
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
        lp.format = PixelFormat.RGBA_8888
        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        lp.flags =
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        lp.packageName = context.packageName
        lp.windowAnimations = android.R.style.Animation_Dialog
        return lp
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
                    onDragStart = { offset ->
                        println("Drag started at $offset")
                    },
                    onDragEnd = {
                        println("Drag ended")
                    },
                    onDragCancel = {
                        println("Drag cancelled")
                    },
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
        LazyColumn(contentPadding = PaddingValues(vertical = 16.dp)) {
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
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Surface(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        println("Drag started at $offset")
                    },
                    onDragEnd = {
                        println("Drag ended")
                    },
                    onDragCancel = {
                        println("Drag cancelled")
                    },
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