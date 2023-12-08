package tornaco.apps.shortx.ui.draw

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.ak1.drawbox.DrawController
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipDialog
import tornaco.apps.shortx.ui.base.rememberTipDialogState


/**
 * Created by akshay on 29/12/21
 * https://ak1.io
 */

@Composable
fun ControlsBar(
    drawController: DrawController,
    onColorClick: () -> Unit,
    onSizeClick: () -> Unit,
    onHideControlsBarClick: () -> Unit,
    undoVisibility: MutableState<Boolean>,
    redoVisibility: MutableState<Boolean>,
    colorValue: MutableState<Color>,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(androidx.compose.material3.MaterialTheme.colorScheme.surface)
    ) {
        val tipDialog = rememberTipDialogState(
            title = LocalI18N.current["ui.action.show.draw.board"],
            tip = LocalI18N.current["ui.action.show.draw.board.tip"]
        )
        TipDialog(state = tipDialog)

        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceAround) {
            MenuItems(
                Remix.Arrows.arrow_go_back_fill,
                if (undoVisibility.value) MaterialTheme.colors.primary else MaterialTheme.colors.primaryVariant
            ) {
                if (undoVisibility.value) drawController.unDo()
            }
            MenuItems(
                Remix.Arrows.arrow_go_forward_fill,
                if (redoVisibility.value) MaterialTheme.colors.primary else MaterialTheme.colors.primaryVariant
            ) {
                if (redoVisibility.value) drawController.reDo()
            }
            MenuItems(
                Remix.System.delete_bin_7_line,
                if (redoVisibility.value || undoVisibility.value) MaterialTheme.colors.primary else MaterialTheme.colors.primaryVariant
            ) {
                drawController.reset()
            }
            MenuItems(
                Remix.Design.palette_line,
                colorValue.value
            ) {
                onColorClick()
            }
            MenuItems(
                Remix.System.checkbox_blank_circle_line,
                MaterialTheme.colors.primary
            ) {
                onSizeClick()
            }
            MenuItems(
                Remix.System.information_fill,
                MaterialTheme.colors.primary
            ) {
                tipDialog.show()
            }

            StandardSpacer()
            MenuItems(
                Remix.Arrows.arrow_drop_down_line,
                MaterialTheme.colors.primary
            ) {
                onHideControlsBarClick()
            }
        }
    }
}

@Composable
fun RowScope.MenuItems(
    icon: String,
    colorTint: Color,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick, modifier = Modifier.weight(1f, true)
    ) {
        ColoredIcon(size = 28.dp) {
            RemixIcon(modifier = Modifier.size(15.dp), remixName = icon, tint = colorTint)
        }
    }
}

@Composable
fun CustomSeekbar(
    isVisible: Boolean,
    max: Int = 200,
    progress: Int = max,
    onProgressChanged: (Int) -> Unit
) {
    AnimatedVisibility(
        visible = isVisible
    ) {
        Column(
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Slider(value = progress.toFloat(),
                valueRange = 10f..max.toFloat(),
                onValueChange = {
                    onProgressChanged(it.toInt())
                })
        }
    }
}