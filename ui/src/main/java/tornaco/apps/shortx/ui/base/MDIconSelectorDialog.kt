package tornaco.apps.shortx.ui.base


import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

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
    var iconName by remember(state) {
        mutableStateOf("")
    }
    if (state.isShow) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = "Icon")
        }, buttons = {
            TextButton(onClick = {
                if (iconName.isNotBlank()) {
                    state.onSelected(iconName)
                    state.dismiss()
                }
            }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, content = {
            val icons: List<String> = MDIconsFilled.all.keys.toList()
            LazyVerticalGrid(
                modifier = Modifier.fillMaxHeight(0.5f),
                columns = GridCells.Fixed(6),
                content = {
                    itemsIndexed(icons) { _, name ->
                        MDIconsFilled.all[name]?.let { vector ->
                            IconButton(onClick = { iconName = name }) {
                                Icon(imageVector = vector, contentDescription = name)
                            }
                        }
                    }
                })
        })
    }
}