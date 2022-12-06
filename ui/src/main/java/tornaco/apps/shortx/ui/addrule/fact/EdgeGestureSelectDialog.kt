package tornaco.apps.shortx.ui.addrule.fact

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.ui.base.*

class EdgeGestureSelectorState(val selected: (Pair<Edge, Gesture>) -> Unit) {
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
fun rememberEdgeGestureSelectorState(selected: (Pair<Edge, Gesture>) -> Unit): EdgeGestureSelectorState {
    return remember {
        EdgeGestureSelectorState(selected)
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
                Box {
                    val dropdownState = rememberDropdownSelectorState()
                    TextButton(onClick = {
                        dropdownState.open()
                    }) {
                        Text(edge.name)
                    }
                    DropdownSelector(
                        state = dropdownState,
                        items = Edge.values().map {
                            DropdownItem(labelLines = listOf(it.name), data = it)
                        },
                        onSelect = {
                            edge = it.data
                        }
                    )
                }

                Box {
                    val dropdownState = rememberDropdownSelectorState()
                    TextButton(onClick = {
                        dropdownState.open()
                    }) {
                        Text(gesture.name)
                    }
                    DropdownSelector(
                        state = dropdownState,
                        items = Gesture.values().map {
                            DropdownItem(labelLines = listOf(it.name), data = it)
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