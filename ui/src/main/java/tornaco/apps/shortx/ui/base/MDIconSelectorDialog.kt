@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.base


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.ui.base.MDIcons.filled
import tornaco.apps.shortx.ui.theme.ShortXTheme

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
fun MDIconSelectorDialog(state: MDIconSelectorState) {
    if (state.isShow) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            MDIconSelector(state)
        }
    }
}

@Composable
private fun MDIconSelector(state: MDIconSelectorState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                modifier = Modifier,
                topBar = {
                    TopAppBar(
                        modifier = Modifier,
                        title = { Text("Icons") },
                        navigationIcon = {
                            IconButton(onClick = {
                                state.dismiss()
                            }) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = "Back")
                            }
                        },
                        actions = {
                        }
                    )
                }) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    val icons: List<String> = filled().keys.toList()
                    val searchBarState = rememberSearchBarState(icons.size)
                    SearchBar(searchBarState)

                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxSize(),
                        columns = GridCells.Fixed(3),
                        content = {
                            itemsIndexed(icons.filter {
                                searchBarState.keyword.isBlank() || it.contains(searchBarState.keyword)
                            }) { _, name ->
                                filled()[name]?.let { vector ->
                                    Column(
                                        modifier = Modifier
                                            .clickable {
                                                state.onSelected(name)
                                                state.dismiss()
                                            }
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            modifier = Modifier.size(32.dp),
                                            imageVector = vector,
                                            contentDescription = name
                                        )
                                        TinySpacer()
                                        Text(
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp),
                                            text = name,
                                            overflow = TextOverflow.Visible,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        })
                }
            }
        }
    }
}