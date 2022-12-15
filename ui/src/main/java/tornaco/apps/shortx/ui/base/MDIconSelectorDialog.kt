package tornaco.apps.shortx.ui.base


import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import tornaco.apps.shortx.ui.base.MDIcons.filled

class MDIconSelectorState(val onSelected: (String) -> Unit) {
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
fun rememberMDIconSelectorState(onSelected: (String) -> Unit): MDIconSelectorState {
    return remember {
        MDIconSelectorState(onSelected)
    }
}

@Composable
fun MDIconSelector(state: MDIconSelectorState) {
    if (state.isShow) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = "Icon")
        }, buttons = {
        }, content = {
            val icons: List<String> = filled().keys.toList()
            LazyVerticalGrid(
                modifier = Modifier.fillMaxHeight(0.5f),
                columns = GridCells.Fixed(6),
                content = {
                    itemsIndexed(icons) { _, name ->
                        filled()[name]?.let { vector ->
                            IconButton(onClick = {
                                state.onSelected(name)
                                state.dismiss()
                            }) {
                                Icon(imageVector = vector, contentDescription = name)
                            }
                        }
                    }
                })
        })
    }
}