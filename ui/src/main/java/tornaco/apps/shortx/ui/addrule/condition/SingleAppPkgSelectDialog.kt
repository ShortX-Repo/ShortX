@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.addrule.condition

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.theme.ShortXTheme

class SingleAppPkgSelectorState(
    val onAppSelected: (App) -> Unit,
) {
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
fun rememberSingleAppPkgSelectorState(
    onAppSelected: (App) -> Unit
): SingleAppPkgSelectorState {
    return remember {
        SingleAppPkgSelectorState(onAppSelected)
    }
}

@Composable
fun SingleAppSelectorDialog(state: SingleAppPkgSelectorState) {
    if (state.isShow) {
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
                    TopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current["ui.dialog.title.select.app"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                state.dismiss()
                            }) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = "Back")
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
            SearchBar(searchBarState)
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
    app: App, onClick: (App) -> Unit
) {
    Box(modifier = Modifier
        .clip(ShortXCardRoundedCornerShape)
        .clickable {
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
            }
        }
    }
}