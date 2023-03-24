@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.gvar

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.ui.base.*
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
            val textInputState = rememberTextInputState(
                title = LocalI18N.current["ui.global.var.input.value"],
                message = null,
                onSelected = {
                    vm.addOrUpdateValue(it)
                }
            )
            TextInputDialog(state = textInputState)

            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    MediumTopAppBar(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        title = {
                            Text(
                                text = (LocalI18N.current["ui.global.var.title"] + "\t" + state.globalVar?.type?.toType()
                                    ?.label())
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                back()
                            }) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = "Back")
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                },
                floatingActionButton = {
                    if (state.isList) {
                        FloatingActionButton(onClick = {
                            textInputState.show()
                        }) {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = "add")
                        }
                    }
                }) { paddingValues ->

                val globalVar = state.globalVar
                if (globalVar == null) {
                    LoadError()
                } else {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = paddingValues
                    ) {

                        stickyHeader {
                            CategoryTitle(title = LocalI18N.current["ui.global.var.name"])
                        }

                        stickyHeader {
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                value = globalVar.name,
                                onValueChange = { value ->
                                    vm.updateName(value)
                                })
                        }

                        stickyHeader {
                            CategoryTitle(title = LocalI18N.current["ui.global.var.value"])
                        }

                        items(state.valueItems) {
                            ListItem(title = it) {
                                if (state.isList) {

                                } else {
                                    textInputState.show()
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}