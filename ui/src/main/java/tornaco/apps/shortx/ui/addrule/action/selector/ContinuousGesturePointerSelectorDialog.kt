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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
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

class ContinuousGesturePointerDialogState(
    val onSelected: (Action.ContinuousGesturePointer) -> Unit
) : CommonDialogState() {
    var sensitivityScale by mutableFloatStateOf(1f)
    var clickOnRelease by mutableStateOf(true)

    fun show(current: Action.ContinuousGesturePointer? = null) {
        sensitivityScale = current?.sensitivityScale ?: 1f
        clickOnRelease = current?.clickOnRelease ?: true
        show()
    }
}

@Composable
fun rememberContinuousGesturePointerDialogState(
    onSelected: (Action.ContinuousGesturePointer) -> Unit
): ContinuousGesturePointerDialogState {
    return remember {
        ContinuousGesturePointerDialogState(onSelected)
    }
}

@Composable
fun ContinuousGesturePointerDialog(state: ContinuousGesturePointerDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ContinuousGesturePointerSelector(state)
        }
    }
}

@Composable
private fun ContinuousGesturePointerSelector(dialogState: ContinuousGesturePointerDialogState) {
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
                                    key = "ui.action.continuous.gesture.pointer",
                                    fallback = "Gesture pointer"
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
                                    Action.ContinuousGesturePointer(
                                        sensitivityScale = dialogState.sensitivityScale,
                                        clickOnRelease = dialogState.clickOnRelease,
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
                    TipBody(text = LocalI18N.current.get(
                        key = "ui.action.continuous.gesture.pointer.sensitivity.tip",
                        fallback = "Higher values move the gesture pointer faster during the gesture."
                    ))
                    Slider(
                        value = dialogState.sensitivityScale,
                        valueRange = 0.25f..3f,
                        onValueChange = {
                            dialogState.sensitivityScale = (it * 20).roundToInt() / 20f
                        }
                    )

                    StandardSpacer()
                    FieldTitle(text = LocalI18N.current.get(
                        key = "ui.action.continuous.gesture.pointer.click.on.release",
                        fallback = "Tap on release"
                    ))
                    MediumSpacer()
                    Switch(
                        checked = dialogState.clickOnRelease,
                        onCheckedChange = { dialogState.clickOnRelease = it }
                    )

                    StandardSpacer()
                    TipBody(text = LocalI18N.current.get(
                        key = "ui.action.continuous.gesture.pointer.tip",
                        fallback = "Move a virtual mouse pointer with the gesture. If tap on release is enabled, lifting your finger sends a primary click."
                    ))
                }
            }
        }
    }
}