@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.pkgset.PkgSetListViewModel
import tornaco.apps.shortx.ui.theme.ShortXTheme

class AppPkgSelectorDialogState(
    val onAppListSelected: (List<App>) -> Unit,
    val onPkgSetSelected: ((List<PkgSet>) -> Unit)?
) {
    private val logger = Logger("AppPkgSelectorDialogState")

    private var _isShow by mutableStateOf(false)
    val isShow get() = _isShow

    private var _selectedApps by mutableStateOf(emptyList<App>())
    val selectedApps get() = _selectedApps

    private var _selectedPkgSets by mutableStateOf(emptyList<PkgSet>())
    val selectedPkgSets get() = _selectedPkgSets

    fun show(
        currentAppList: List<App>? = null,
        currentPkgSet: List<PkgSet>? = null
    ) {
        logger.d("show, currentAppList: $currentAppList, currentPkgSet: $currentPkgSet")

        _selectedApps = currentAppList ?: emptyList()
        _selectedPkgSets = currentPkgSet ?: emptyList()

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

    fun addPkgSet(pkgSet: PkgSet) {
        _selectedPkgSets = selectedPkgSets.toMutableList().apply {
            if (firstOrNull { it.label == pkgSet.label } == null) {
                add(pkgSet)
            }
        }
    }

    fun removePkgSet(pkgSet: PkgSet) {
        _selectedPkgSets = selectedPkgSets.toMutableList().apply {
            removeIf { it.label == pkgSet.label }
        }
    }
}

@Composable
fun rememberAppPkgSelectorState(
    onAppListSelected: (List<App>) -> Unit = {},
    onPkgSetSelected: ((List<PkgSet>) -> Unit)? = null
): AppPkgSelectorDialogState {
    return remember {
        AppPkgSelectorDialogState(onAppListSelected, onPkgSetSelected)
    }
}

@Composable
fun AppPkgSelectorDialog(state: AppPkgSelectorDialogState) {
    if (state.isShow) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            AppPkgSelector(state)
        }
    }
}

@Composable
private fun AppPkgSelector(state: AppPkgSelectorDialogState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            var selectedTabIndex by remember {
                mutableStateOf(0)
            }
            Scaffold(modifier = Modifier,
                topBar = {
                    TopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current["ui.dialog.title.select.app"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                state.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                if (selectedTabIndex == 0) {
                                    state.onAppListSelected(state.selectedApps)
                                } else {
                                    state.onPkgSetSelected?.invoke(state.selectedPkgSets.filter {
                                        shortXManager.getPkgSetByLabel(it.label) != null
                                    })
                                }
                                state.dismiss()

                            }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }
                        }
                    )
                }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    TabRow(selectedTabIndex = selectedTabIndex) {
                        val titles = mutableListOf(
                            LocalI18N.current["ui.dialog.title.select.app"],
                        ).apply {
                            if (state.onPkgSetSelected != null) {
                                add(LocalI18N.current["ui.pkg.set.title"])
                            }
                        }
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
                }
            }
        }
    }
}

@Composable
private fun PkgSetContent(selectorState: AppPkgSelectorDialogState) {
    val vm = hiltViewModel<PkgSetListViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.loadState()
    }

    if (state.isLoading) {
        Loading(modifier = Modifier.fillMaxSize())
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(state.pkgSets) {
                ListItem(
                    icon = {
                        RemixIcon(remixName = Remix.Document.folder_2_line)
                    },
                    titleLines = listOf(
                        it.label,
                        LocalI18N.current["ui.pkg.set.items.count", mapOf("count" to "${it.appPkgsCount}")]
                    ),
                    action1 = {
                        Checkbox(
                            checked = selectorState.selectedPkgSets.contains(it),
                            onCheckedChange = { checked ->
                                if (checked) {
                                    selectorState.addPkgSet(it)
                                } else {
                                    selectorState.removePkgSet(it)
                                }
                            })
                    })
            }
        }
    }

}

@Composable
private fun AppListContent(dialogState: AppPkgSelectorDialogState) {
    val vm = hiltViewModel<AppSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.loadInstalledApps()
    }
    val state by vm.state.collectAsState()

    val searchBarState = rememberSearchBarState(state.installedApps.size)
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            SearchBar(
                searchBarState = searchBarState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )
        }
        items(state.installedApps.filterSearchKeyword(searchBarState.keyword)) { app ->
            AppItem(
                app = app,
                isSelected = dialogState.selectedApps.firstOrNull { app.app.pkg == it.pkg } != null,
                onCheckedChange = { checkedApp, isChecked ->
                    if (isChecked) {
                        dialogState.addApp(checkedApp)
                    } else {
                        dialogState.removeApp(checkedApp)
                    }
                })
        }
    }
}


@Composable
private fun AppItem(
    app: AppWithState, isSelected: Boolean, onCheckedChange: (App, Boolean) -> Unit
) {
    Box(modifier = Modifier
        .clip(ShortXCardRoundedCornerShape)
        .clickable {
            onCheckedChange(app.app, !isSelected)
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
                    AppIcon(modifier = Modifier.size(38.dp), app.app.pkg)
                    Spacer(modifier = Modifier.size(12.dp))

                    Column(verticalArrangement = Arrangement.Center) {
                        Text(
                            text = app.app.label, fontSize = 14.sp
                        )
                        SmallSpacer()
                        Text(
                            text = app.app.pkg.pkgName, fontSize = 12.sp
                        )

                        if (app.isRunning) {
                            OutlineBadge(text = LocalI18N.current["ui.common.running"])
                        }
                    }
                }
                Checkbox(checked = isSelected, onCheckedChange = {
                    onCheckedChange(app.app, it)
                })
            }
        }
    }
}