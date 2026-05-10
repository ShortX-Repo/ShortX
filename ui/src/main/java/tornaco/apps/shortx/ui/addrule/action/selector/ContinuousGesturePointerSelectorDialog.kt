@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
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

class ContinuousGesturePointerDialogState(
    val onSelected: (Action.ContinuousGesturePointer) -> Unit
) : CommonDialogState() {
    var sensitivityScale by mutableFloatStateOf(1f)
    var releaseButton by mutableStateOf(GesturePointerReleaseButton.Primary)
    var dragButton by mutableStateOf(GesturePointerDragButton.None)
    var dragHoldDurationMs by mutableIntStateOf(350)
    var scrollMode by mutableStateOf(GesturePointerScrollMode.None)
    var scrollScale by mutableFloatStateOf(1f)

    fun show(current: Action.ContinuousGesturePointer? = null) {
        sensitivityScale = current?.sensitivityScale ?: 1f
        releaseButton = current?.releaseButton ?: GesturePointerReleaseButton.Primary
        dragButton = current?.dragButton ?: GesturePointerDragButton.None
        dragHoldDurationMs = current?.dragHoldDurationMs ?: 350
        scrollMode = current?.scrollMode ?: GesturePointerScrollMode.None
        scrollScale = current?.scrollScale ?: 1f
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
                                        releaseButton = dialogState.releaseButton,
                                        dragButton = dialogState.dragButton,
                                        dragHoldDurationMs = dialogState.dragHoldDurationMs,
                                        scrollMode = dialogState.scrollMode,
                                        scrollScale = dialogState.scrollScale,
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
                        key = "ui.action.continuous.gesture.pointer.release.button",
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
                        key = "ui.action.continuous.gesture.pointer.drag.button",
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

                    if (dialogState.dragButton != GesturePointerDragButton.None) {
                        StandardSpacer()
                        FieldTitle(
                            text = LocalI18N.current.get(
                                key = "ui.action.continuous.gesture.pointer.drag.hold.duration",
                                fallback = "Drag hold duration"
                            ) + " ${dialogState.dragHoldDurationMs}ms"
                        )
                        MediumSpacer()
                        TipBody(
                            text = LocalI18N.current.get(
                                key = "ui.action.continuous.gesture.pointer.drag.hold.duration.tip",
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
                        key = "ui.action.continuous.gesture.pointer.scroll.mode",
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
                                key = "ui.action.continuous.gesture.pointer.scroll.scale",
                                fallback = "Wheel sensitivity"
                            ) + " ${"%.2f".format(dialogState.scrollScale)}x"
                        )
                        MediumSpacer()
                        TipBody(
                            text = LocalI18N.current.get(
                                key = "ui.action.continuous.gesture.pointer.scroll.scale.tip",
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
                    TipBody(text = LocalI18N.current.get(
                        key = "ui.action.continuous.gesture.pointer.tip",
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
            key = "ui.action.continuous.gesture.pointer.release.button.none",
            fallback = "None"
        )

        GesturePointerReleaseButton.Primary -> get(
            key = "ui.action.continuous.gesture.pointer.release.button.primary",
            fallback = "Primary click"
        )

        GesturePointerReleaseButton.Secondary -> get(
            key = "ui.action.continuous.gesture.pointer.release.button.secondary",
            fallback = "Secondary click"
        )

        GesturePointerReleaseButton.Touch -> get(
            key = "ui.action.continuous.gesture.pointer.release.button.touch",
            fallback = "Touch tap"
        )
    }
}

private fun tornaco.apps.shortx.core.I18N.labelFor(button: GesturePointerDragButton): String {
    return when (button) {
        GesturePointerDragButton.None -> get(
            key = "ui.action.continuous.gesture.pointer.drag.button.none",
            fallback = "Disabled"
        )

        GesturePointerDragButton.Primary -> get(
            key = "ui.action.continuous.gesture.pointer.drag.button.primary",
            fallback = "Primary drag"
        )

        GesturePointerDragButton.Secondary -> get(
            key = "ui.action.continuous.gesture.pointer.drag.button.secondary",
            fallback = "Secondary drag"
        )
    }
}

private fun tornaco.apps.shortx.core.I18N.labelFor(mode: GesturePointerScrollMode): String {
    return when (mode) {
        GesturePointerScrollMode.None -> get(
            key = "ui.action.continuous.gesture.pointer.scroll.mode.none",
            fallback = "Disabled"
        )

        GesturePointerScrollMode.Vertical -> get(
            key = "ui.action.continuous.gesture.pointer.scroll.mode.vertical",
            fallback = "Vertical wheel"
        )

        GesturePointerScrollMode.Horizontal -> get(
            key = "ui.action.continuous.gesture.pointer.scroll.mode.horizontal",
            fallback = "Horizontal wheel"
        )

        GesturePointerScrollMode.Both -> get(
            key = "ui.action.continuous.gesture.pointer.scroll.mode.both",
            fallback = "Two-axis wheel"
        )
    }
}