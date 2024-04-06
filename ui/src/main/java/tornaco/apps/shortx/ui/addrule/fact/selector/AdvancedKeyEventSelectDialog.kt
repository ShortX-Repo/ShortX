package tornaco.apps.shortx.ui.addrule.fact.selector

import android.view.KeyEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.common.KeyGesture
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.fact.mapper.displayNameKey
import tornaco.apps.shortx.ui.addrule.fact.mapper.getLabelForKeyCode
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXDialog
import tornaco.apps.shortx.ui.base.ShortXSwitch
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.base.rememberTextInputState

class AdvancedKeyEventSelectorState(val selected: (Fact.AdvancedKeyEvent) -> Unit) :
    CommonDialogState() {
    private var _gesture by mutableStateOf(KeyGesture.KeyPress)
    val gesture get() = _gesture

    private var _keyCode by mutableStateOf(KeyEvent.KEYCODE_VOLUME_UP)
    val keyCode get() = _keyCode

    private var _isInterceptMode by mutableStateOf(false)
    val isInterceptMode get() = _isInterceptMode

    fun setKeyCode(code: Int) {
        _keyCode = code
    }

    fun setGesture(gesture: KeyGesture) {
        _gesture = gesture
    }

    fun setIsInterceptMode(isInterceptMode: Boolean) {
        _isInterceptMode = isInterceptMode
    }

    fun show(advancedKeyEvent: Fact.AdvancedKeyEvent? = null) {
        this._keyCode = advancedKeyEvent?.keyCode ?: KeyEvent.KEYCODE_VOLUME_UP
        this._gesture = advancedKeyEvent?.gesture ?: KeyGesture.KeyPress
        this._isInterceptMode = advancedKeyEvent?.isInterceptMode ?: false

        show()
    }
}

@Composable
fun rememberAdvancedKeyEventSelectorState(selected: (Fact.AdvancedKeyEvent) -> Unit): AdvancedKeyEventSelectorState {
    return remember {
        AdvancedKeyEventSelectorState(selected)
    }
}

@Composable
fun AdvancedKeyEventSelector(state: AdvancedKeyEventSelectorState) {
    if (state.isShowing) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = LocalI18N.current["ui.fact.key.event.adv"])
        }, buttons = {
            TextButton(onClick = {
                state.selected(
                    Fact.AdvancedKeyEvent(
                        keyCode = state.keyCode,
                        gesture = state.gesture,
                        isInterceptMode = state.isInterceptMode
                    )
                )
                state.dismiss()
            }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, content = {
            val customKeyCodeDialogState = rememberTextInputState(
                title = LocalI18N.current["ui.action.custom.keycode"],
                message = null,
                showSymbolButton = false,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onSelected = {
                    val code = it.toIntOrNull()
                    if (code != null) {
                        state.setKeyCode(code)
                    }
                }
            )
            TextInputDialog(state = customKeyCodeDialogState)

            Column(modifier = Modifier.fillMaxWidth()) {
                val i18N = LocalI18N.current

                // Key.
                Box {
                    val dropdownState = rememberDropdownSelectorState()
                    Column {
                        FieldTitle(
                            modifier = Modifier.padding(vertical = 12.dp),
                            text = i18N["ui.fact.key.event.adv"]
                        )
                        ListItem(
                            icon = {
                                RemixIcon(remixName = Remix.Device.keyboard_line)
                            },
                            title = i18N[getLabelForKeyCode(i18N, state.keyCode)],
                            action1 = {
                                IconButton(onClick = { dropdownState.open() }) {
                                    RemixIcon(remixName = Remix.Arrows.arrow_right_s_line)
                                }
                            },
                            onClick = {
                                dropdownState.open()
                            })
                    }
                    DropdownSelector(
                        state = dropdownState,
                        items = arrayOf(
                            AdvancedKeyEventItem(
                                LocalI18N.current["ui.keycode.label.back"],
                                KeyEvent.KEYCODE_BACK
                            ),
                            AdvancedKeyEventItem(
                                LocalI18N.current["ui.keycode.label.home"],
                                KeyEvent.KEYCODE_HOME
                            ),
                            AdvancedKeyEventItem(
                                LocalI18N.current["ui.keycode.label.volume.down"],
                                KeyEvent.KEYCODE_VOLUME_DOWN
                            ),
                            AdvancedKeyEventItem(
                                LocalI18N.current["ui.keycode.label.volume.up"],
                                KeyEvent.KEYCODE_VOLUME_UP
                            ),
                            AdvancedKeyEventItem(
                                LocalI18N.current["ui.keycode.label.power"],
                                KeyEvent.KEYCODE_POWER
                            ),
                            AdvancedKeyEventItem(
                                LocalI18N.current["ui.action.custom.keycode"],
                                KeyEvent.KEYCODE_UNKNOWN
                            ),
                        ).map {
                            DropdownItem(
                                data = it.keyCode,
                                labelLines = listOf(LocalI18N.current[it.labelKey])
                            )
                        },
                        onSelect = {
                            if (it.data == KeyEvent.KEYCODE_UNKNOWN) {
                                customKeyCodeDialogState.show()
                            } else {
                                state.setKeyCode(it.data)
                            }
                        }
                    )
                }

                StandardSpacer()


                // Gesture.
                Box {
                    val dropdownState = rememberDropdownSelectorState()
                    Column {
                        FieldTitle(
                            modifier = Modifier.padding(vertical = 12.dp),
                            text = i18N["ui.fact.edge.gesture.gesture"]
                        )
                        ListItem(
                            icon = {
                                RemixIcon(remixName = Remix.Editor.sketching)
                            },
                            title = i18N[state.gesture.displayNameKey()],
                            action1 = {
                                IconButton(onClick = { dropdownState.open() }) {
                                    RemixIcon(remixName = Remix.Arrows.arrow_right_s_line)
                                }
                            },
                            onClick = {
                                dropdownState.open()
                            })
                    }
                    DropdownSelector(
                        state = dropdownState,
                        items = KeyGesture.entries.filterNot { it == KeyGesture.UNRECOGNIZED }
                            .map {
                                DropdownItem(
                                    labelLines = listOf(i18N[it.displayNameKey()]),
                                    data = it
                                )
                            },
                        onSelect = {
                            state.setGesture(it.data)
                        }
                    )
                }
                StandardSpacer()
                // isInterceptMode

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FieldTitle(text = LocalI18N.current["ui.fact.key.event.adv.is.intercept.mode"])
                    ShortXSwitch(checked = state.isInterceptMode, onCheckedChange = {
                        state.setIsInterceptMode(it)
                    })
                }
                StandardSpacer()

                AnimatedVisibility(visible = state.isInterceptMode) {
                    TipRow(text = LocalI18N.current["ui.fact.key.event.adv.summary"])
                    StandardSpacer()
                    StandardSpacer()
                }
            }

        })
    }
}

private data class AdvancedKeyEventItem(
    val labelKey: String,
    val keyCode: Int
)