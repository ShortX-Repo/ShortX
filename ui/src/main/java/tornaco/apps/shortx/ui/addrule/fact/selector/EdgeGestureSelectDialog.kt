package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.res.Remix
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
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import java.util.Locale

class EdgeGestureSelectorState(
    val showEdgeHint: (Edge) -> Unit,
    val selected: (Triple<Edge, Gesture, Boolean>) -> Unit
) : CommonDialogState() {
    var edge by mutableStateOf(Edge.BottomRight)
    var gesture by mutableStateOf(Gesture.DoubleTap)
    var isIntercept by mutableStateOf(false)

    fun show(edge: Edge?, gesture: Gesture?, isIntercept: Boolean?) {
        this.edge = edge ?: Edge.BottomRight
        this.gesture = gesture ?: Gesture.SingleTap
        this.isIntercept = isIntercept ?: false

        show()
    }
}

@Composable
fun rememberEdgeGestureSelectorState(
    showEdgeHint: (Edge) -> Unit,
    selected: (Triple<Edge, Gesture, Boolean>) -> Unit
): EdgeGestureSelectorState {
    return remember {
        EdgeGestureSelectorState(showEdgeHint, selected)
    }
}

@Composable
fun EdgeGestureSelector(state: EdgeGestureSelectorState) {
    if (state.isShowing) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = LocalI18N.current["ui.fact.edge.gesture"])
        }, buttons = {
            TextButton(onClick = {
                state.selected(Triple(state.edge, state.gesture, state.isIntercept))
                state.dismiss()
            }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, content = {
            Column(modifier = Modifier.fillMaxWidth()) {
                val i18N = LocalI18N.current
                Box {
                    val dropdownState = rememberDropdownSelectorState()

                    Column {
                        SmallTitle(
                            modifier = Modifier.padding(vertical = 12.dp),
                            text = i18N["ui.fact.edge.gesture.position"]
                        )
                        ListItem(title = i18N[state.edge.displayNameKey()],
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
                        items = Edge.values().filterNot { it == Edge.UNRECOGNIZED }.map {
                            DropdownItem(labelLines = listOf(i18N[it.displayNameKey()]), data = it)
                        },
                        onSelect = {
                            state.edge = it.data
                            state.showEdgeHint(it.data)
                        }
                    )
                }

                StandardSpacer()

                Box {
                    val dropdownState = rememberDropdownSelectorState()
                    Column {
                        SmallTitle(
                            modifier = Modifier.padding(vertical = 12.dp),
                            text = i18N["ui.fact.edge.gesture.gesture"]
                        )
                        ListItem(title = i18N[state.gesture.displayNameKey()],
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
                        items = Gesture.values().filterNot { it == Gesture.UNRECOGNIZED }.map {
                            DropdownItem(labelLines = listOf(i18N[it.displayNameKey()]), data = it)
                        },
                        onSelect = {
                            state.gesture = it.data
                        }
                    )
                }

                StandardSpacer()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FieldTitle(text = LocalI18N.current["ui.fact.key.event.adv.is.intercept.mode"])
                    ShortXSwitch(checked = state.isIntercept, onCheckedChange = {
                        state.isIntercept = it
                    })
                }
                StandardSpacer()

                AnimatedVisibility(visible = state.isIntercept) {
                    TipRow(text = LocalI18N.current["ui.fact.motion.event.adv.summary"])
                    StandardSpacer()
                    StandardSpacer()
                }
            }
        })
    }
}

fun Gesture.displayNameKey(): String {
    val prefix = "ui.fact.edge.gesture"
    return "$prefix.${name.lowercase(Locale.ENGLISH)}"
}

fun Edge.displayNameKey(): String {
    val prefix = "ui.fact.edge"
    return "$prefix.${name.lowercase(Locale.ENGLISH)}"
}