@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.gvar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.theme.ShortXTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GlobalVarDetailScreen(id: String, back: () -> Unit) {
    val vm = hiltViewModel<GlobalVarDetailViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.loadState(id)
    }

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
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
                            value = state.name,
                            onValueChange = { value ->
                                vm.updateLabel(value)
                            })
                    }
                }
            }
        }
    }
}