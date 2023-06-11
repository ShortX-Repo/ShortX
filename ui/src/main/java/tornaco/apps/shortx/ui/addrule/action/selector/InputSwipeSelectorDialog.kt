@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.selector.*
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme

class InputSwipeSelectorState(
    val onSelected: (Action.InputSwipe) -> Unit
) {
    private var _isShow by mutableStateOf(false)
    val isShow get() = _isShow

    var startX by mutableStateOf("")
    var startY by mutableStateOf("")
    var endX by mutableStateOf("")
    var endY by mutableStateOf("")
    var swipeTime by mutableStateOf("500")

    fun show(current: Action.InputSwipe? = null) {
        this.startX = current?.startX?.toString() ?: ""
        this.startY = current?.startY?.toString() ?: ""
        this.endX = current?.endX?.toString() ?: ""
        this.endY = current?.endY?.toString() ?: ""
        this.swipeTime = current?.swipeTime?.toString() ?: "500"
        _isShow = true
    }

    fun dismiss() {
        _isShow = false
    }
}

@Composable
fun rememberInputSwipeSelectorState(
    onSelected: (Action.InputSwipe) -> Unit
): InputSwipeSelectorState {
    return remember {
        InputSwipeSelectorState(onSelected)
    }
}

@Composable
fun InputSwipeSelectorDialog(state: InputSwipeSelectorState) {
    if (state.isShow) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            InputSwipeSelector(state)
        }
    }
}

@Composable
private fun InputSwipeSelector(
    state: InputSwipeSelectorState
) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier, topBar = {
                ShortXTopAppBar(
                    modifier = Modifier,
                    title = { Text(text = LocalI18N.current["ui.action.input.swipe"]) },
                    navigationIcon = {
                        IconButton(onClick = {
                            state.dismiss()
                        }) {
                            RemixIcon(remixName = Remix.System.close_line)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            if (listOf(state.startX, state.startY, state.endX, state.endY).all {
                                    it.isNotBlank() && it.isDigitsOnly()
                                }) {
                                state.onSelected(
                                    Action.InputSwipe(
                                        startX = state.startX.toIntOrNull() ?: 0,
                                        startY = state.startY.toIntOrNull() ?: 0,
                                        endX = state.endX.toIntOrNull() ?: 0,
                                        endY = state.endY.toIntOrNull() ?: 0,
                                        swipeTime = state.swipeTime.toLongOrNull() ?: 0L
                                    )
                                )
                                state.dismiss()
                            }
                        }) {
                            Icon(imageVector = Icons.Outlined.Check, contentDescription = "Done")
                        }
                    },
                )
            }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    SmallTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        text = LocalI18N.current["ui.action.input.swipe.start.pos"]
                    )
                    ShortXTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.startX,
                        onValueChange = {
                            state.startX = it
                        },
                        label = {
                            Text(LocalI18N.current["ui.action.input.swipe.start.x"])
                        },
                        showSymbolButton = false,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    StandardSpacer()
                    ShortXTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.startY,
                        onValueChange = {
                            state.startY = it
                        },
                        label = {
                            Text(LocalI18N.current["ui.action.input.swipe.start.y"])
                        },
                        showSymbolButton = false,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    StandardSpacer()


                    // End
                    SmallTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        text = LocalI18N.current["ui.action.input.swipe.end.pos"]
                    )
                    ShortXTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.endX,
                        onValueChange = {
                            state.endX = it
                        },
                        label = {
                            Text(LocalI18N.current["ui.action.input.swipe.end.x"])
                        },
                        showSymbolButton = false,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    StandardSpacer()
                    ShortXTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.endY,
                        onValueChange = {
                            state.endY = it
                        },
                        label = {
                            Text(LocalI18N.current["ui.action.input.swipe.end.y"])
                        },
                        showSymbolButton = false,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    StandardSpacer()

                    SmallTitle(
                        modifier = Modifier.padding(top = 12.dp),
                        text = LocalI18N.current["ui.action.input.swipe.time"]
                    )
                    TipBody(text = LocalI18N.current["ui.action.input.swipe.time.description"])
                    TinySpacer()
                    ShortXTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.swipeTime,
                        onValueChange = {
                            state.swipeTime = it
                        },
                        showSymbolButton = false,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    SuggestedNavEntry()
                }
            }
        }
    }
}