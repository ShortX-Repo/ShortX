package tornaco.apps.shortx.ui.addrule.condition

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.core.App
import tornaco.apps.shortx.ui.base.*

class AppPkgSelectorState(val apps: List<App>, val selected: (List<App>) -> Unit) {
    private var _isShow by mutableStateOf(false)
    val isShow get() = _isShow

    private var _selectedApps by mutableStateOf(emptyList<App>())
    val selectedApps get() = _selectedApps

    fun show() {
        _isShow = true
    }

    fun dismiss() {
        _isShow = false
    }

    fun addApp(app: App) {
        _selectedApps = selectedApps.toMutableList().apply {
            if (contains(app).not()) {
                add(app)
            }
        }
    }

    fun removeApp(app: App) {
        _selectedApps = selectedApps.toMutableList().apply {
            remove(app)
        }
    }
}

@Composable
fun rememberAppPkgSelectorState(
    apps: List<App>,
    selected: (List<App>) -> Unit
): AppPkgSelectorState {
    return remember(apps) {
        AppPkgSelectorState(apps, selected)
    }
}

@Composable
fun AppPkgSelector(state: AppPkgSelectorState) {
    if (state.isShow) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(
                text = LocalI18N.current["ui.dialog.title.select.app"],
            )
        }, buttons = {
            TextButton(onClick = {
                state.selected(state.selectedApps)
                state.dismiss()
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
                    AppItem(app = it,
                        isSelected = state.selectedApps.contains(it),
                        onCheckedChange = { app, isChecked ->
                            if (isChecked) {
                                state.addApp(app)
                            } else {
                                state.removeApp(app)
                            }
                        })
                }
            }
        })
    }
}


@Composable
private fun AppItem(
    app: App,
    isSelected: Boolean,
    onCheckedChange: (App, Boolean) -> Unit
) {
    Box(modifier = Modifier
        .clip(ShortXCardRoundedCornerShape)
        .clickable {
            onCheckedChange(app, !isSelected)
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
                modifier = Modifier.fillMaxWidth(),
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
                    AppIcon(modifier = Modifier.size(38.dp), app.pkg)
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
                Checkbox(checked = isSelected, onCheckedChange = {
                    onCheckedChange(app, it)
                })
            }
        }
    }
}