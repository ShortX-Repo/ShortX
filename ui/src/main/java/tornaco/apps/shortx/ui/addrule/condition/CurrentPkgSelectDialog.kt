package tornaco.apps.shortx.ui.addrule.condition

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.pkgset.PkgSetListViewModel

class AppPkgSelectorState(
    val apps: List<App>,
    val onAppListSelected: (List<App>) -> Unit,
    val onPkgSetSelected: (String) -> Unit
) {
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
    onPkgSetSelected: (String) -> Unit = {},
    onAppListSelected: (List<App>) -> Unit = {}
): AppPkgSelectorState {
    return remember(apps) {
        AppPkgSelectorState(apps, onAppListSelected, onPkgSetSelected)
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
                state.onAppListSelected(state.selectedApps)
                state.dismiss()
            }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, content = {
            var selectedTabIndex by remember {
                mutableStateOf(0)
            }
            TabRow(selectedTabIndex = selectedTabIndex) {
                val titles = listOf(
                    LocalI18N.current["ui.dialog.title.select.app"],
                    LocalI18N.current["ui.pkg.set.title"]
                )
                titles.forEachIndexed { index, title ->
                    Tab(selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title, maxLines = 2, overflow = TextOverflow.Ellipsis
                            )
                        })
                }
            }

            if (selectedTabIndex == 0) {
                AppListContent(state)
            } else {
                PkgSetContent(state)
            }
        })
    }
}

@Composable
private fun PkgSetContent(state: AppPkgSelectorState) {
    val vm = hiltViewModel<PkgSetListViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.loadState()
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fraction = 0.68f)
    ) {
        items(state.pkgSets) {
            ListItem(title = it.label)
        }
    }
}

@Composable
private fun AppListContent(state: AppPkgSelectorState) {
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
            AppItem(
                app = it,
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
}


@Composable
private fun AppItem(
    app: App, isSelected: Boolean, onCheckedChange: (App, Boolean) -> Unit
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