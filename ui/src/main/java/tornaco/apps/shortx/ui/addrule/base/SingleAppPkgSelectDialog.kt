@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.addrule.base

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.PrebuiltStablePkgSetLabel
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.AppIcon
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.OutlineBadge
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SearchBar
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.base.rememberSearchBarState
import tornaco.apps.shortx.ui.theme.ShortXTheme

class SingleAppPkgSelectorState(
    val onAppSelected: (App) -> Unit,
) : CommonDialogState() {
    var selectedAppFilterPkgSetLabel by mutableStateOf(PrebuiltStablePkgSetLabel.All.name)

    override fun show() {
        selectedAppFilterPkgSetLabel = PrebuiltStablePkgSetLabel.All.name
        super.show()
    }
}

@Composable
fun rememberSingleAppPkgSelectorState(
    onAppSelected: (App) -> Unit
): SingleAppPkgSelectorState {
    return remember {
        SingleAppPkgSelectorState(onAppSelected)
    }
}

@Composable
fun SingleAppSelectorDialog(state: SingleAppPkgSelectorState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            SingleAppPkgSelector(state)
        }
    }
}

@Composable
private fun SingleAppPkgSelector(state: SingleAppPkgSelectorState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current["ui.dialog.title.select.app"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                state.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                    )
                }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    AppListContent(state)
                }
            }
        }
    }
}

@Composable
private fun AppListContent(selectorState: SingleAppPkgSelectorState) {
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
        stickyHeader {
            SearchBar(
                searchBarState = searchBarState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(top = 8.dp)
            )
        }

        item {
            Box(modifier = Modifier.padding(horizontal = 12.dp)) {
                val allPkgSets = shortXManager.getAllPkgSets(false)
                val dropdownState = rememberDropdownSelectorState()
                FilledTonalButton(onClick = {
                    dropdownState.open()
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RemixIcon(
                            modifier = Modifier.size(16.dp),
                            remixName = Remix.System.filter_line
                        )
                        SmallSpacer()
                        Text(text = selectorState.selectedAppFilterPkgSetLabel)
                    }

                }

                DropdownSelector(
                    widthFraction = 0.6f,
                    state = dropdownState,
                    items = allPkgSets.map {
                        DropdownItem(labelLines = listOf(it.label), data = it.label)
                    },
                    onSelect = {
                        selectorState.selectedAppFilterPkgSetLabel = it.data
                        vm.loadInstalledApps(selectorState.selectedAppFilterPkgSetLabel)
                    })

            }
        }

        items(state.installedApps.filterSearchKeyword(searchBarState.keyword)) {
            AppItem(
                app = it,
                onClick = { app ->
                    selectorState.onAppSelected(app)
                    selectorState.dismiss()
                })
        }
    }
}


@Composable
private fun AppItem(
    app: AppWithState, onClick: (App) -> Unit
) {
    Box(modifier = Modifier
        .clip(ShortXCardRoundedCornerShape)
        .clickable {
            onClick(app.app)
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
                            SmallSpacer()
                            OutlineBadge(text = LocalI18N.current["ui.common.running"])
                        }
                    }
                }
            }
        }
    }
}