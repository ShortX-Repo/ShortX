package tornaco.apps.shortx.ui.addrule.fact

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.*
import java.util.*

class EdgeGestureSelectorState(
    val showEdgeHint: (Edge) -> Unit,
    val selected: (Pair<Edge, Gesture>) -> Unit
) {
    private var _isShow by mutableStateOf(false)
    val isShow get() = _isShow

    fun show() {
        _isShow = true
    }

    fun dismiss() {
        _isShow = false
    }
}

@Composable
fun rememberEdgeGestureSelectorState(
    showEdgeHint: (Edge) -> Unit,
    selected: (Pair<Edge, Gesture>) -> Unit
): EdgeGestureSelectorState {
    return remember {
        EdgeGestureSelectorState(showEdgeHint, selected)
    }
}

@Composable
fun EdgeGestureSelector(state: EdgeGestureSelectorState) {
    var edge by remember(state) {
        mutableStateOf(Edge.BottomRight)
    }
    var gesture by remember(state) {
        mutableStateOf(Gesture.DoubleTap)
    }
    if (state.isShow) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = "Edge Gesture")
        }, buttons = {
            TextButton(onClick = {
                state.selected(edge to gesture)
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
                        SmallTitle(text = i18N["ui.fact.edge.gesture.position"])
                        ListItem(title = i18N[edge.displayNameKey()],
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
                            edge = it.data
                            state.showEdgeHint(it.data)
                        }
                    )
                }

                StandardSpacer()

                Box {
                    val dropdownState = rememberDropdownSelectorState()
                    Column {
                        SmallTitle(text = i18N["ui.fact.edge.gesture.gesture"])
                        ListItem(title = i18N[gesture.displayNameKey()],
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
                            gesture = it.data
                        }
                    )
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