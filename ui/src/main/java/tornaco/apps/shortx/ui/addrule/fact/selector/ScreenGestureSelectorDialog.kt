package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.common.Rect
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.ui.addrule.fact.mapper.displayNameKey
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.composeLogger

class ScreenGestureSelectorDialogState(
    val title: String,
    val onSelected: (Gesture, Rect?) -> Unit
) : CommonDialogState() {
    var currentGesture: Gesture? by mutableStateOf(null)
    var currentGestureArea: Rect? by mutableStateOf(null)

    fun show(
        currentGesture: Gesture? = null,
        currentGestureArea: Rect? = null
    ) {
        this.currentGesture = currentGesture
        this.currentGestureArea = currentGestureArea
        composeLogger.d("currentGesture: $currentGesture")
        composeLogger.d("currentGestureArea: $currentGestureArea")
        show()
    }
}

@Composable
fun rememberScreenGestureSelectorState(
    title: String,
    onSelected: (Gesture, Rect?) -> Unit
): ScreenGestureSelectorDialogState {
    return remember {
        ScreenGestureSelectorDialogState(title, onSelected)
    }
}

@Composable
fun ScreenGestureSelectorDialog(
    state: ScreenGestureSelectorDialogState
) {
    val i18N = LocalI18N.current

    if (state.isShowing) {
        var selectedGesture by remember(state.currentGesture) {
            mutableStateOf(state.currentGesture ?: Gesture.UNRECOGNIZED)
        }
        var rectText by remember(state.currentGestureArea) {
            val rect = state.currentGestureArea
            mutableStateOf(
                if (rect != null) "${rect.left},${rect.top},${rect.right},${rect.bottom}" else ""
            )
        }
        var rectError by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { state.dismiss() },
            title = { Text(state.title) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 手势类型选择
                    Text(
                        text = i18N["ui.fact.edge.gesture.gesture"],
                        style = MaterialTheme.typography.bodyMedium
                    )

                    val gestureMenuState = rememberMenuDialogState<String>(
                        title = i18N["ui.fact.edge.gesture.gesture"],
                        menuItems = Gesture.entries.filter { it != Gesture.UNRECOGNIZED }.map {
                            MenuDialogItem(it.name, title = i18N[it.displayNameKey()])
                        },
                        onItemSelected = { _, id ->
                            selectedGesture = Gesture.valueOf(id)
                        }
                    )
                    MenuDialog(state = gestureMenuState)

                    OutlinedButton(
                        onClick = {
                            gestureMenuState.show(
                                currentSelection = listOfNotNull(selectedGesture.takeIf { it != Gesture.UNRECOGNIZED }?.name)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (selectedGesture != Gesture.UNRECOGNIZED) {
                                i18N[selectedGesture.displayNameKey()]
                            } else {
                                i18N["ui.common.select"]
                            }
                        )
                    }

                    // 区域输入
                    Text(
                        text = i18N["ui.action.rect.src.rect"],
                        style = MaterialTheme.typography.bodyMedium
                    )

                    ShortXTextField(
                        value = rectText,
                        onValueChange = {
                            rectText = it
                            rectError = false
                        },
                        placeholder = { Text("left,top,right,bottom (可选)") },
                        isError = rectError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (rectError) {
                        Text(
                            text = i18N["ui.action.rect.src.rect.format.error"],
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (selectedGesture == Gesture.UNRECOGNIZED) {
                            return@TextButton
                        }

                        val gestureArea = if (rectText.isNotBlank()) {
                            try {
                                val parts = rectText.split(",").map { it.trim().toInt() }
                                if (parts.size == 4) {
                                    Rect.newBuilder()
                                        .setLeft(parts[0])
                                        .setTop(parts[1])
                                        .setRight(parts[2])
                                        .setBottom(parts[3])
                                        .build()
                                } else {
                                    rectError = true
                                    return@TextButton
                                }
                            } catch (e: NumberFormatException) {
                                rectError = true
                                return@TextButton
                            }
                        } else {
                            null
                        }

                        state.onSelected(selectedGesture, gestureArea)
                        state.dismiss()
                    },
                    enabled = selectedGesture != Gesture.UNRECOGNIZED
                ) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { state.dismiss() }) {
                    Text(stringResource(android.R.string.cancel))
                }
            }
        )
    }
}