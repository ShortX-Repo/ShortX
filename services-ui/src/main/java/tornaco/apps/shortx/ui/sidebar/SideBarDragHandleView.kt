package tornaco.apps.shortx.ui.sidebar

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import tornaco.apps.shortx.ui.overlay.AbstractComposeOverlay
import tornaco.apps.shortx.ui.theme.NoteTextColor
import tornaco.apps.shortx.ui.theme.ShortXServiceUITheme

private const val DRAG_HANDLE_W = 4
private const val DRAG_HANDLE_H = 64
private const val DRAG_HANDLE_H_PADDING = 12

@SuppressLint("ViewConstructor")
class SideBarDragHandleView(
    private val expandState: MutableStateFlow<Boolean> = MutableStateFlow(false),
    context: Context,
    initialPos: Pair<Int, Int>,
    onPosChange: (Int, Int) -> Unit,
    private val showBar: () -> Unit
) : AbstractComposeOverlay(
    context = context,
    tag = "SideBar",
    autoEdge = false,
    enableGestureMoving = false,
    initialPos = initialPos,
    onPosChange = onPosChange,
    onTouchOutside = { true },
) {

    @Composable
    override fun ComposeContent() {
        ShortXServiceUITheme {
            val isExpanded by expandState.collectAsState()
            DragHandle(onDrag = {
                showBar()
                expandState.value = true
            })
        }
    }

    fun showDragHandle() {
        expandState.value = false
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
            .padding(horizontal = DRAG_HANDLE_H_PADDING.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        onDrag()
                    },
                    onDrag = { change, dragAmount ->
                        val (dx, dy) = dragAmount
                        when {
                            dx > 0 -> println("Dragging right by $dx")
                            dx < 0 -> println("Dragging left by ${-dx}")
                            dy > 0 -> println("Dragging down by $dy")
                            dy < 0 -> println("Dragging up by ${-dy}")
                        }
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