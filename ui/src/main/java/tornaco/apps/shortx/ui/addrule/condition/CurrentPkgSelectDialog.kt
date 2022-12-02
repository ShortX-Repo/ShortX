@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package tornaco.apps.shortx.ui.addrule.condition

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.core.App
import tornaco.apps.shortx.ui.base.*

class CurrentPkgSelectorState(val apps: List<App>, val selected: (App) -> Unit) {
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
fun rememberCurrentPkgSelectorState(
    apps: List<App>, selected: (App) -> Unit
): CurrentPkgSelectorState {
    return remember(apps) {
        CurrentPkgSelectorState(apps, selected)
    }
}

@Composable
fun CurrentPkgSelector(state: CurrentPkgSelectorState) {
    var selectedApp by remember(state) {
        mutableStateOf<App?>(null)
    }
    if (state.isShow) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(
                text = LocalI18N.current["ui.dialog.title.current.app"],
            )
        }, buttons = {
            TextButton(onClick = {
                selectedApp?.let {
                    state.selected(it)
                    state.dismiss()
                }
            }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, content = {
            val searchBarState = rememberSearchBarState(state.apps.size)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(fraction = 0.68f)
            ) {
                item {
                    SearchBar(searchBarState)
                }
                items(state.apps.filter {
                    searchBarState.keyword.isBlank() || it.label.contains(searchBarState.keyword)
                }) {
                    AppItem(app = it, onClick = { app ->
                        selectedApp = app
                        state.selected(app)
                        state.dismiss()
                    })
                }
            }
        })
    }
}


@Composable
private fun AppItem(
    app: App, onClick: (App) -> Unit
) {
    Box(modifier = Modifier.clickable {
        onClick(app)
    }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .heightIn(min = 64.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(end = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                AppIcon(modifier = Modifier.size(38.dp), app)
                Spacer(modifier = Modifier.size(12.dp))

                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        text = app.label, fontSize = 14.sp
                    )
                    SmallSpacer()
                    Text(
                        text = app.pkg.pkgName, fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun rememberSearchBarState(totalAppsCount: Int): SearchBarState {
    return remember {
        SearchBarState(totalAppsCount)
    }
}


@Composable
private fun SearchBar(searchBarState: SearchBarState) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
    ) {
        val focusRequester = remember {
            FocusRequester()
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchTextField(searchBarState, focusRequester)
        }
    }
}

@Composable
private fun SearchTextField(searchBarState: SearchBarState, focusRequester: FocusRequester) {
    TextField(modifier = Modifier
        .fillMaxWidth()
        .focusRequester(focusRequester),
        value = searchBarState.keyword,
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        label = {
            Text(
                text = LocalI18N.current["ui.search.title.search.n.apps", mapOf(
                    "count" to searchBarState.totalAppsCount.toString()
                )]
            )
        },
        shape = RoundedCornerShape(6.dp),
        maxLines = 1,
        trailingIcon = {
            if (searchBarState.keyword.isEmpty()) {
                IconButton(onClick = {
                }) {
                    Icon(
                        imageVector = Icons.Filled.Search, contentDescription = "Search"
                    )
                }
            } else {
                IconButton(onClick = {
                    searchBarState.inputKeyword("")
                }) {
                    Icon(
                        imageVector = Icons.Filled.Close, contentDescription = "Clear keywords"
                    )
                }
            }
        },
        onValueChange = {
            searchBarState.inputKeyword(it)
        })
}

private class SearchBarState(val totalAppsCount: Int) {
    private var _keyword by mutableStateOf("")
    val keyword get() = _keyword

    fun inputKeyword(value: String) {
        _keyword = value
    }
}