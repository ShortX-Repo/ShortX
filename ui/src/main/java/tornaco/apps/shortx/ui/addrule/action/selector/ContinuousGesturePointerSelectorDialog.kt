@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.GesturePointerCenterSwitchMode
import tornaco.apps.shortx.ui.addrule.action.model.GesturePointerDragButton
import tornaco.apps.shortx.ui.addrule.action.model.GesturePointerReleaseButton
import tornaco.apps.shortx.ui.addrule.action.model.GesturePointerScrollMode
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXSwitch
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.theme.ShortXTheme
import kotlin.math.roundToInt
import tornaco.apps.shortx.core.i18n.I18N_R

class ContinuousGesturePointerDialogState(
    val onSelected: (Action.ContinuousGesturePointer) -> Unit
) : CommonDialogState() {
    var sensitivityScale by mutableFloatStateOf(1f)
    var releaseButton by mutableStateOf(GesturePointerReleaseButton.Primary)
    var dragButton by mutableStateOf(GesturePointerDragButton.None)
    var dragHoldDurationMs by mutableIntStateOf(350)
    var scrollMode by mutableStateOf(GesturePointerScrollMode.None)
    var scrollScale by mutableFloatStateOf(1f)
    var cancelRegionRadiusDp by mutableIntStateOf(48)
    var enableCenterActionSwitch by mutableStateOf(false)
    val centerSwitchModes = mutableStateListOf<GesturePointerCenterSwitchMode>()

    fun show(current: Action.ContinuousGesturePointer? = null) {
        sensitivityScale = current?.sensitivityScale ?: 1f
        releaseButton = current?.releaseButton ?: GesturePointerReleaseButton.Primary
        dragButton = current?.dragButton ?: GesturePointerDragButton.None
        dragHoldDurationMs = current?.dragHoldDurationMs ?: 350
        scrollMode = current?.scrollMode ?: GesturePointerScrollMode.None
        scrollScale = current?.scrollScale ?: 1f
        cancelRegionRadiusDp = current?.cancelRegionRadiusDp ?: 48
        enableCenterActionSwitch = current?.enableCenterActionSwitch ?: false
        centerSwitchModes.clear()
        centerSwitchModes.addAll(current?.centerSwitchModes ?: emptyList())
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
                                    key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER,
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
                                        releaseButton = dialogState.releaseButton,
                                        dragButton = dialogState.dragButton,
                                        dragHoldDurationMs = dialogState.dragHoldDurationMs,
                                        scrollMode = dialogState.scrollMode,
                                        scrollScale = dialogState.scrollScale,
                                        cancelRegionRadiusDp = dialogState.cancelRegionRadiusDp,
                                        enableCenterActionSwitch = dialogState.enableCenterActionSwitch || dialogState.centerSwitchModes.isNotEmpty(),
                                        centerSwitchModes = dialogState.centerSwitchModes.toList(),
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
                        key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_SENSITIVITY_TIP,
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
                        key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_RELEASE_BUTTON,
                        fallback = "Release action"
                    ))
                    MediumSpacer()
                    PointerDropdownField(
                        selectedLabel = LocalI18N.current.labelFor(dialogState.releaseButton),
                        options = GesturePointerReleaseButton.entries.map { option ->
                            LocalI18N.current.labelFor(option) to {
                                dialogState.releaseButton = option
                            }
                        }
                    )

                    StandardSpacer()
                    FieldTitle(text = LocalI18N.current.get(
                        key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_DRAG_BUTTON,
                        fallback = "Long-press drag"
                    ))
                    MediumSpacer()
                    PointerDropdownField(
                        selectedLabel = LocalI18N.current.labelFor(dialogState.dragButton),
                        options = GesturePointerDragButton.entries.map { option ->
                            LocalI18N.current.labelFor(option) to {
                                dialogState.dragButton = option
                            }
                        }
                    )

                    if (dialogState.dragButton != GesturePointerDragButton.None
                        && GesturePointerCenterSwitchMode.Drag !in dialogState.centerSwitchModes) {
                        StandardSpacer()
                        FieldTitle(
                            text = LocalI18N.current.get(
                                key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_DRAG_HOLD_DURATION,
                                fallback = "Drag hold duration"
                            ) + " ${dialogState.dragHoldDurationMs}ms"
                        )
                        MediumSpacer()
                        TipBody(
                            text = LocalI18N.current.get(
                                key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_DRAG_HOLD_DURATION_TIP,
                                fallback = "Press and hold for this long before drag starts."
                            )
                        )
                        Slider(
                            value = dialogState.dragHoldDurationMs.toFloat(),
                            valueRange = 120f..1200f,
                            onValueChange = {
                                dialogState.dragHoldDurationMs = (it / 10f).roundToInt() * 10
                            }
                        )
                    }

                    StandardSpacer()
                    FieldTitle(text = LocalI18N.current.get(
                        key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_SCROLL_MODE,
                        fallback = "Wheel mode"
                    ))
                    MediumSpacer()
                    PointerDropdownField(
                        selectedLabel = LocalI18N.current.labelFor(dialogState.scrollMode),
                        options = GesturePointerScrollMode.entries.map { option ->
                            LocalI18N.current.labelFor(option) to {
                                dialogState.scrollMode = option
                            }
                        }
                    )

                    if (dialogState.scrollMode != GesturePointerScrollMode.None) {
                        StandardSpacer()
                        FieldTitle(
                            text = LocalI18N.current.get(
                                key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_SCROLL_SCALE,
                                fallback = "Wheel sensitivity"
                            ) + " ${"%.2f".format(dialogState.scrollScale)}x"
                        )
                        MediumSpacer()
                        TipBody(
                            text = LocalI18N.current.get(
                                key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_SCROLL_SCALE_TIP,
                                fallback = "Higher values emit stronger wheel deltas from the same gesture movement."
                            )
                        )
                        Slider(
                            value = dialogState.scrollScale,
                            valueRange = 0.25f..3f,
                            onValueChange = {
                                dialogState.scrollScale = (it * 20).roundToInt() / 20f
                            }
                        )
                    }

                    StandardSpacer()
                    FieldTitle(
                        text = LocalI18N.current.get(
                            key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_CANCEL_REGION_RADIUS,
                            fallback = "Cancel circle radius"
                        ) + " ${dialogState.cancelRegionRadiusDp}dp"
                    )
                    MediumSpacer()
                    TipBody(
                        text = LocalI18N.current.get(
                            key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_CANCEL_REGION_RADIUS_TIP,
                            fallback = "If your finger is still inside this start circle when the gesture ends, the release action will be cancelled."
                        )
                    )
                    Slider(
                        value = dialogState.cancelRegionRadiusDp.toFloat(),
                        valueRange = 16f..160f,
                        onValueChange = {
                            dialogState.cancelRegionRadiusDp = it.roundToInt()
                        }
                    )

                    StandardSpacer()
                    FieldTitle(
                        text = LocalI18N.current.get(
                            key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_CENTER_SWITCH_MODES,
                            fallback = "Center circle switch modes"
                        )
                    )
                    MediumSpacer()
                    TipBody(
                        text = LocalI18N.current.get(
                            key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_CENTER_SWITCH_MODES_TIP,
                            fallback = "Select and reorder modes to cycle through via center circle. Pull out of center, back in, then out again to switch."
                        )
                    )
                    MediumSpacer()
                    GesturePointerCenterSwitchMode.entries.forEach { mode ->
                        val checked = mode in dialogState.centerSwitchModes
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { isChecked ->
                                    if (isChecked) {
                                        dialogState.centerSwitchModes.add(mode)
                                    } else {
                                        dialogState.centerSwitchModes.remove(mode)
                                    }
                                }
                            )
                            Text(
                                text = LocalI18N.current.labelFor(mode),
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }

                    StandardSpacer()
                    TipBody(text = LocalI18N.current.get(
                        key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_TIP,
                        fallback = "Move a virtual mouse pointer with the gesture. You can choose a release action, long-press drag behavior, and wheel mode."
                    ))
                }
            }
        }
    }
}

@Composable
private fun PointerDropdownField(
    selectedLabel: String,
    options: List<Pair<String, () -> Unit>>,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        ShortXTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            showSymbolButton = false,
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { (label, action) ->
                DropdownMenuItem(
                    text = { Text(text = label) },
                    onClick = {
                        action()
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun tornaco.apps.shortx.core.I18N.labelFor(button: GesturePointerReleaseButton): String {
    return when (button) {
        GesturePointerReleaseButton.None -> get(
            key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_RELEASE_BUTTON_NONE,
            fallback = "None"
        )

        GesturePointerReleaseButton.Primary -> get(
            key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_RELEASE_BUTTON_PRIMARY,
            fallback = "Primary click"
        )

        GesturePointerReleaseButton.Secondary -> get(
            key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_RELEASE_BUTTON_SECONDARY,
            fallback = "Secondary click"
        )

        GesturePointerReleaseButton.Touch -> get(
            key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_RELEASE_BUTTON_TOUCH,
            fallback = "Touch tap"
        )
    }
}

private fun tornaco.apps.shortx.core.I18N.labelFor(button: GesturePointerDragButton): String {
    return when (button) {
        GesturePointerDragButton.None -> get(
            key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_DRAG_BUTTON_NONE,
            fallback = "Disabled"
        )

        GesturePointerDragButton.Primary -> get(
            key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_DRAG_BUTTON_PRIMARY,
            fallback = "Primary drag"
        )

        GesturePointerDragButton.Secondary -> get(
            key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_DRAG_BUTTON_SECONDARY,
            fallback = "Secondary drag"
        )
    }
}

private fun tornaco.apps.shortx.core.I18N.labelFor(mode: GesturePointerScrollMode): String {
    return when (mode) {
        GesturePointerScrollMode.None -> get(
            key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_SCROLL_MODE_NONE,
            fallback = "Disabled"
        )

        GesturePointerScrollMode.Vertical -> get(
            key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_SCROLL_MODE_VERTICAL,
            fallback = "Vertical wheel"
        )

        GesturePointerScrollMode.Horizontal -> get(
            key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_SCROLL_MODE_HORIZONTAL,
            fallback = "Horizontal wheel"
        )

        GesturePointerScrollMode.Both -> get(
            key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_SCROLL_MODE_BOTH,
            fallback = "Two-axis wheel"
        )
    }
}

private fun tornaco.apps.shortx.core.I18N.labelFor(mode: GesturePointerCenterSwitchMode): String {
    return when (mode) {
        GesturePointerCenterSwitchMode.SingleClick -> get(
            key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_MODE_SINGLE,
            fallback = "Single click"
        )

        GesturePointerCenterSwitchMode.LongPress -> get(
            key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_MODE_LONG,
            fallback = "Long press"
        )

        GesturePointerCenterSwitchMode.DoubleClick -> get(
            key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_MODE_DOUBLE,
            fallback = "Double click"
        )

        GesturePointerCenterSwitchMode.Drag -> get(
            key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_MODE_DRAG,
            fallback = "Drag"
        )

        GesturePointerCenterSwitchMode.Scroll -> get(
            key = I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_MODE_SCROLL,
            fallback = "Scroll"
        )
    }
}
