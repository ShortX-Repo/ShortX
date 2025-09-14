package tornaco.apps.shortx.ui.screencap

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.ui.base.LocalI18N

enum class InteractionMode {
    DRAWING,    // 正在绘制新矩形
    DRAGGING,   // 正在拖动已有矩形
    IDLE        // 空闲状态，显示确认按钮
}

@Composable
fun RectangleCaptureScreen(
    onDragEnd: () -> Unit = {},
    onSelectFullScreen: () -> Unit = {},
    onSelected: (Rect) -> Unit = {},
) {
    var startOffset by remember { mutableStateOf<Offset?>(null) }
    var endOffset by remember { mutableStateOf<Offset?>(null) }
    var interactionMode by remember { mutableStateOf(InteractionMode.IDLE) }
    var dragStartOffset by remember { mutableStateOf<Offset?>(null) }

    val currentRect = remember(startOffset, endOffset) {
        startOffset?.let { start ->
            endOffset?.let { end ->
                Rect(
                    left = start.x.coerceAtMost(end.x),
                    top = start.y.coerceAtMost(end.y),
                    right = start.x.coerceAtLeast(end.x),
                    bottom = start.y.coerceAtLeast(end.y)
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        when (interactionMode) {
                            InteractionMode.IDLE -> {
                                // 重新开始绘制
                                startOffset = offset
                                endOffset = offset
                                interactionMode = InteractionMode.DRAWING
                            }

                            else -> {
                                // 其他状态下的点击暂时忽略
                            }
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        when {
                            currentRect?.contains(offset) == true -> {
                                // 在矩形内开始拖拽，进入拖动模式
                                interactionMode = InteractionMode.DRAGGING
                                dragStartOffset = offset
                            }

                            else -> {
                                // 在矩形外开始拖拽，绘制新矩形
                                startOffset = offset
                                endOffset = offset
                                interactionMode = InteractionMode.DRAWING
                            }
                        }
                    },
                    onDrag = { change, _ ->
                        when (interactionMode) {
                            InteractionMode.DRAWING -> {
                                endOffset = change.position
                            }

                            InteractionMode.DRAGGING -> {
                                dragStartOffset?.let { dragStart ->
                                    currentRect?.let { rect ->
                                        val deltaX = change.position.x - dragStart.x
                                        val deltaY = change.position.y - dragStart.y

                                        startOffset = Offset(
                                            rect.left + deltaX,
                                            rect.top + deltaY
                                        )
                                        endOffset = Offset(
                                            rect.right + deltaX,
                                            rect.bottom + deltaY
                                        )
                                        dragStartOffset = change.position
                                    }
                                }
                            }

                            else -> {}
                        }
                    },
                    onDragEnd = {
                        when (interactionMode) {
                            InteractionMode.DRAWING, InteractionMode.DRAGGING -> {
                                interactionMode = InteractionMode.IDLE
                                onDragEnd()
                            }

                            else -> {}
                        }
                    }
                )
            }
    ) {
        // 绘制矩形
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            currentRect?.let { rect ->
                // 绘制矩形边框
                drawRect(
                    color = Color.Red,
                    topLeft = Offset(rect.left, rect.top),
                    size = androidx.compose.ui.geometry.Size(
                        width = rect.width,
                        height = rect.height
                    ),
                    style = Stroke(width = 3.dp.toPx())
                )

                // 绘制拖拽提示点（四个角）
                val cornerSize = 12.dp.toPx()
                listOf(
                    Offset(rect.left, rect.top),
                    Offset(rect.right, rect.top),
                    Offset(rect.left, rect.bottom),
                    Offset(rect.right, rect.bottom)
                ).forEach { corner ->
                    drawCircle(
                        color = Color.Red,
                        radius = cornerSize / 2,
                        center = corner
                    )
                }
            }
        }

        // 确认按钮区域
        if (interactionMode == InteractionMode.IDLE && currentRect != null) {
            val i18N = LocalI18N.current

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onSelectFullScreen,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(i18N["ui.action.select.full.screen"] ?: "选择全屏")
                }

                Button(
                    onClick = {
                        currentRect?.let { onSelected(it) }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(i18N["ui.action.confirm.selection"] ?: "确认选择")
                }
            }
        }

        // 操作提示文本
        if (currentRect == null) {
            val i18N = LocalI18N.current
            Text(
                text = i18N["ui.action.drag.to.select.area"] ?: "拖拽选择区域",
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
