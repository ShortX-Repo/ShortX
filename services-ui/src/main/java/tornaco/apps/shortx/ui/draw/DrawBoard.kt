package tornaco.apps.shortx.ui.draw

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import io.ak1.drawbox.DrawBox
import io.ak1.drawbox.rememberDrawController
import io.ak1.rangvikalp.RangVikalp
import io.ak1.rangvikalp.defaultSelectedColor
import tornaco.apps.shortx.ui.base.CommonDialogState


@Composable
fun DrawBoard(modifier: Modifier = Modifier, controlBarState: CommonDialogState) {
    val undoVisibility = remember { mutableStateOf(false) }
    val redoVisibility = remember { mutableStateOf(false) }
    val colorBarVisibility = remember { mutableStateOf(false) }
    val sizeBarVisibility = remember { mutableStateOf(false) }
    val currentColor = remember { mutableStateOf(defaultSelectedColor) }
    val currentSize = remember { mutableStateOf(10) }
    val drawController = rememberDrawController()

    Box(modifier = modifier) {
        Column {
            DrawBox(
                drawController = drawController,
                backgroundColor = Color.Transparent,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f, fill = false),
            ) { undoCount, redoCount ->
                sizeBarVisibility.value = false
                colorBarVisibility.value = false
                undoVisibility.value = undoCount != 0
                redoVisibility.value = redoCount != 0
            }

            AnimatedVisibility(visible = controlBarState.isShowing) {
                ControlsBar(
                    drawController = drawController,
                    onColorClick = {
                        colorBarVisibility.value = !colorBarVisibility.value
                        sizeBarVisibility.value = false
                    },
                    onSizeClick = {
                        sizeBarVisibility.value = !sizeBarVisibility.value
                        colorBarVisibility.value = false
                    },
                    onHideControlsBarClick = {
                        controlBarState.dismiss()
                    },
                    undoVisibility = undoVisibility,
                    redoVisibility = redoVisibility,
                    colorValue = currentColor,
                )
            }

            RangVikalp(isVisible = colorBarVisibility.value, showShades = true) {
                currentColor.value = it
                drawController.changeColor(it)
            }
            CustomSeekbar(
                isVisible = sizeBarVisibility.value,
                progress = currentSize.value,
                progressColor = MaterialTheme.colors.primary.toArgb(),
                thumbColor = currentColor.value.toArgb()
            ) {
                currentSize.value = it
                drawController.changeStrokeWidth(it.toFloat())
            }
        }

    }
}