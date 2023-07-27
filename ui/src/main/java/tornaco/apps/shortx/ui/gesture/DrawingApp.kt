@file:OptIn(ExperimentalComposeUiApi::class)

package tornaco.apps.shortx.ui.gesture

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInteropFilter
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.thenIf
import tornaco.apps.shortx.ui.gesture.gesture.MotionEventType
import tornaco.apps.shortx.ui.gesture.model.PathProperties

private val pathProp = PathProperties()
private val logger = Logger("DrawingApp")

@Composable
fun DrawingApp(
    eventSink: (MotionEvent) -> Unit
) {
    val paths = remember { mutableStateListOf<Path>() }
    var motionEvent by remember { mutableStateOf(MotionEventType.Idle) }
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    var previousPosition by remember { mutableStateOf(Offset.Zero) }
    var currentPath by remember { mutableStateOf(Path()) }

    fun handleMotionEvent(it: MotionEvent) {
        logger.d("handleMotionEvent: $it")
        when (it.action) {
            MotionEvent.ACTION_DOWN -> {
                motionEvent = MotionEventType.Down
                currentPosition = Offset(it.x, it.y)
            }

            MotionEvent.ACTION_MOVE -> {
                motionEvent = MotionEventType.Move
                currentPosition = Offset(it.x, it.y)
            }

            MotionEvent.ACTION_UP -> {
                motionEvent = MotionEventType.Up
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val drawModifier = Modifier
            .fillMaxSize()
            .thenIf(
                modifier = Modifier.pointerInteropFilter {
                    eventSink(it)
                    handleMotionEvent(it)
                    true
                },
                boolean = true
            )

        Canvas(modifier = drawModifier) {
            when (motionEvent) {
                MotionEventType.Down -> {
                    currentPath.moveTo(currentPosition.x, currentPosition.y)
                    previousPosition = currentPosition
                }

                MotionEventType.Move -> {
                    currentPath.quadraticBezierTo(
                        previousPosition.x,
                        previousPosition.y,
                        (previousPosition.x + currentPosition.x) / 2,
                        (previousPosition.y + currentPosition.y) / 2

                    )

                    previousPosition = currentPosition
                }

                MotionEventType.Up -> {
                    currentPath.lineTo(currentPosition.x, currentPosition.y)

                    // Pointer is up save current path
//                        paths[currentPath] = currentPathProperty
                    paths.add(currentPath)

                    // Since paths are keys for map, use new one for each key
                    // and have separate path for each down-move-up gesture cycle
                    currentPath = Path()
                    // If we leave this state at MotionEvent.Up it causes current path to draw
                    // line from (0,0) if this composable recomposes when draw mode is changed
                    currentPosition = Offset.Zero
                    previousPosition = currentPosition
                    motionEvent = MotionEventType.Idle
                }

                else -> Unit
            }

            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)
                paths.forEach { path ->
                    drawPath(
                        color = pathProp.color,
                        path = path,
                        style = Stroke(
                            width = pathProp.strokeWidth,
                            cap = pathProp.strokeCap,
                            join = pathProp.strokeJoin
                        )
                    )
                }

                if (motionEvent != MotionEventType.Idle) {
                    drawPath(
                        color = pathProp.color,
                        path = currentPath,
                        style = Stroke(
                            width = pathProp.strokeWidth,
                            cap = pathProp.strokeCap,
                            join = pathProp.strokeJoin
                        )
                    )
                }
                restoreToCount(checkPoint)
            }
        }

    }
}