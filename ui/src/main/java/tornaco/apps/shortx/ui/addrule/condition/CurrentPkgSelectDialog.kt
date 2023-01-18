package tornaco.apps.shortx.ui.addrule.condition

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.core.App
import tornaco.apps.shortx.ui.base.*

class AppPkgSelectorState(val apps: List<App>, val selected: (List<App>) -> Unit) {
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
fun rememberAppPkgSelectorState(
    apps: List<App>, selected: (List<App>) -> Unit
): AppPkgSelectorState {
    return remember(apps) {
        AppPkgSelectorState(apps, selected)
    }
}

@Composable
fun AppPkgSelector(state: AppPkgSelectorState) {
    var selectedApp by remember(state) {
        mutableStateOf<App?>(null)
    }
    if (state.isShow) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(
                text = LocalI18N.current["ui.dialog.title.select.app"],
            )
        }, buttons = {
            TextButton(onClick = {
                selectedApp?.let {
                    state.selected(listOf(it))
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
                        state.selected(listOf(app))
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
        }
    }
}