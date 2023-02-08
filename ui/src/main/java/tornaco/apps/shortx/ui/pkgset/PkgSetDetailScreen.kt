@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.pkgset

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.ui.addrule.condition.AppPkgSelector
import tornaco.apps.shortx.ui.addrule.condition.rememberAppPkgSelectorState
import tornaco.apps.shortx.ui.base.AppIcon
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.theme.ShortXTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PkgSetDetailScreen(id: String, back: () -> Unit) {
    val vm = hiltViewModel<PkgSetDetailViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.loadState(id)
    }

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val appSelectorDialogState = rememberAppPkgSelectorState(
                apps = state.selectableApps,
                selected = {
                    vm.addApps(it)
                }
            )
            AppPkgSelector(state = appSelectorDialogState)

            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    MediumTopAppBar(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        title = { Text(text = LocalI18N.current["ui.pkg.set.title"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                back()
                            }) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = "Back")
                            }
                        },
                        actions = {
                            IconButton(enabled = state.isChanged, onClick = {
                                vm.save()
                                back()
                            }) {
                                Icon(imageVector = Icons.Filled.Check, contentDescription = "Done")
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(onClick = {
                        appSelectorDialogState.show()
                    }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "add")
                    }
                }) { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = paddingValues
                ) {
                    stickyHeader {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp, top = 16.dp),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.TextFields,
                                    contentDescription = ""
                                )
                            },
                            label = {
                                Text(LocalI18N.current["ui.pkg.set.add.input.label"])
                            },
                            value = state.label,
                            onValueChange = { value ->
                                vm.updateLabel(value)
                            })
                    }

                    items(state.apps) {
                        AppItem(it) { app ->
                            vm.removeApps(listOf(app))
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun AppItem(
    app: App,
    removeApp: (App) -> Unit
) {
    Box(modifier = Modifier
        .clip(ShortXCardRoundedCornerShape)
        .clickable {
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

                IconButton(onClick = { removeApp(app) }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Remove"
                    )
                }
            }
        }
    }
}