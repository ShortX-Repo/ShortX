package tornaco.apps.shortx.ui.screencap

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun RectangleCaptureScreen(
    onDragEnd: () -> Unit = {},
    onSelectFullScreen: () -> Unit = {},
    onCancel: () -> Unit = {},
    onSelected: (Rect) -> Unit = {},
) {
    var startOffset by remember { mutableStateOf<Offset?>(null) }
    var endOffset by remember { mutableStateOf<Offset?>(null) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        if (startOffset == null && endOffset == null) {
                            onSelectFullScreen()
                        }
                    },
                    onLongPress = {
                        onCancel()
                    },
                    onTap = { offset ->
                        startOffset?.let { start ->
                            endOffset?.let { end ->
                                val rect = Rect(
                                    left = start.x.coerceAtMost(end.x),
                                    top = start.y.coerceAtMost(end.y),
                                    right = start.x.coerceAtLeast(end.x),
                                    bottom = start.y.coerceAtLeast(end.y)
                                )
                                if (rect.contains(offset)) {
                                    onSelected(rect)
                                }
                            }
                        }
                    })
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        startOffset = offset
                        endOffset = offset
                    },
                    onDrag = { change, _ ->
                        endOffset = change.position
                    },
                    onDragEnd = {
                        onDragEnd()
                    }
                )
            }
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            startOffset?.let { start ->
                endOffset?.let { end ->
                    drawRect(
                        color = Color.Red,
                        topLeft = start,
                        size = androidx.compose.ui.geometry.Size(
                            width = end.x - start.x,
                            height = end.y - start.y
                        ),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
        }
    }
}
