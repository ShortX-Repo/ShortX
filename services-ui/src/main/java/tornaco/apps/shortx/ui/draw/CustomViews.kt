package tornaco.apps.shortx.ui.draw

import android.widget.SeekBar
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.ak1.drawbox.DrawController
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.StandardSpacer


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

        StandardSpacer()
        MenuItems(
            Remix.Arrows.arrow_drop_down_line,
            MaterialTheme.colors.primary
        ) {
            onHideControlsBarClick()
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
        RemixIcon(remixName = icon, tint = colorTint)
    }
}

@Composable
fun CustomSeekbar(
    isVisible: Boolean,
    max: Int = 200,
    progress: Int = max,
    progressColor: Int,
    thumbColor: Int,
    onProgressChanged: (Int) -> Unit
) {
    val density = LocalDensity.current
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically {
            // Slide in from 40 dp from the top.
            with(density) { -40.dp.roundToPx() }
        } + expandVertically(
            // Expand from the top.
            expandFrom = Alignment.Top
        ) + fadeIn(
            // Fade in with the initial alpha of 0.3f.
            initialAlpha = 0.3f
        ),
        exit = slideOutVertically() + shrinkVertically() + fadeOut()
    ) {
        val context = LocalContext.current
        Column(
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(text = "Stroke Width", modifier = Modifier.padding(12.dp, 0.dp, 0.dp, 0.dp))
            AndroidView(
                { SeekBar(context) },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                it.max = max
                it.progress = progress
                it.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

                    }

                    override fun onStartTrackingTouch(p0: SeekBar?) {}
                    override fun onStopTrackingTouch(p0: SeekBar?) {
                        onProgressChanged(p0?.progress ?: it.progress)
                    }
                })
            }
        }
    }
}