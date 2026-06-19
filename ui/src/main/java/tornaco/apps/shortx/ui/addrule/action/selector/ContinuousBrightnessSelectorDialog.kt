@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.theme.ShortXTheme
import kotlin.math.roundToInt
import tornaco.apps.shortx.core.i18n.I18N_R

class ContinuousBrightnessDialogState(
    val onSelected: (Action.ContinuousAdjustBrightness) -> Unit
) : CommonDialogState() {
    var sensitivityScale by mutableFloatStateOf(1f)
    var minValue by mutableIntStateOf(0)
    var maxValue by mutableIntStateOf(255)

    fun show(current: Action.ContinuousAdjustBrightness? = null) {
        sensitivityScale = current?.sensitivityScale ?: 1f
        minValue = current?.minValue ?: 0
        maxValue = current?.maxValue ?: 255
        show()
    }
}

@Composable
fun rememberContinuousBrightnessDialogState(
    onSelected: (Action.ContinuousAdjustBrightness) -> Unit
): ContinuousBrightnessDialogState {
    return remember {
        ContinuousBrightnessDialogState(onSelected)
    }
}

@Composable
fun ContinuousBrightnessDialog(state: ContinuousBrightnessDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ContinuousBrightnessSelector(state)
        }
    }
}

@Composable
private fun ContinuousBrightnessSelector(dialogState: ContinuousBrightnessDialogState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    ShortXTopAppBar(
                        title = {
                            Text(
                                text = LocalI18N.current.get(
                                    key = I18N_R.UI_ACTION_CONTINUOUS_ADJUST_BRIGHTNESS,
                                    fallback = "Continuous brightness"
                                )
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { dialogState.dismiss() }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                dialogState.onSelected(
                                    Action.ContinuousAdjustBrightness(
                                        sensitivityScale = dialogState.sensitivityScale,
                                        minValue = dialogState.minValue,
                                        maxValue = dialogState.maxValue,
                                    )
                                )
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(paddingValues)
                        .animateContentSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    FieldTitle(text = "Sensitivity ${"%.2f".format(dialogState.sensitivityScale)}x")
                    MediumSpacer()
                    TipBody(text = "Higher values change brightness faster during the gesture.")
                    Slider(
                        value = dialogState.sensitivityScale,
                        valueRange = 0.25f..3f,
                        onValueChange = {
                            dialogState.sensitivityScale = (it * 20).roundToInt() / 20f
                        }
                    )

                    StandardSpacer()
                    FieldTitle(text = "Minimum ${dialogState.minValue}")
                    MediumSpacer()
                    Slider(
                        value = dialogState.minValue.toFloat(),
                        valueRange = 0f..dialogState.maxValue.toFloat(),
                        onValueChange = {
                            dialogState.minValue = it.roundToInt().coerceAtMost(dialogState.maxValue)
                        }
                    )

                    StandardSpacer()
                    FieldTitle(text = "Maximum ${dialogState.maxValue}")
                    MediumSpacer()
                    Slider(
                        value = dialogState.maxValue.toFloat(),
                        valueRange = dialogState.minValue.toFloat()..255f,
                        onValueChange = {
                            dialogState.maxValue = it.roundToInt().coerceAtLeast(dialogState.minValue)
                        }
                    )

                    StandardSpacer()
                    TipBody(text = "Effective range: ${dialogState.minValue} - ${dialogState.maxValue}")
                }
            }
        }
    }
}