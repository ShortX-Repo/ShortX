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
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.label
import tornaco.apps.shortx.core.rule.toType
import tornaco.apps.shortx.core.toComponentName
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.addrule.condition.SingleAppSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.rememberSingleAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.fact.AppComponentSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.ComponentType
import tornaco.apps.shortx.ui.addrule.fact.rememberAppComponentSelectorState
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
                    vm.addOrUpdateValue(listOf(it))
                }
            )
            TextInputDialog(state = textInputState)

            val appComponentSelectorState = rememberAppComponentSelectorState(
                types = ComponentType.values().toList(),
                onAppComponentSelected = { infoList ->
                    vm.addOrUpdateValue(infoList.map {
                        it.component.toComponentName().flattenToShortString()
                    })
                }
            )
            AppComponentSelectorDialog(state = appComponentSelectorState)

            val singleAppSelectorState = rememberSingleAppPkgSelectorState {
                appComponentSelectorState.show(it.pkg)
            }
            SingleAppSelectorDialog(state = singleAppSelectorState)

            val addMenuState = rememberMenuDialogState(
                title = LocalI18N.current["ui.common.add"], menuItems = listOf(
                    MenuDialogItem(
                        title = LocalI18N.current["ui.common.manually.input"],
                        onClick = {
                            textInputState.show()
                        }),
                    MenuDialogItem(
                        title = LocalI18N.current["ui.title.select.component"],
                        onClick = {
                            singleAppSelectorState.show()
                        })
                )
            )
            MenuDialog(state = addMenuState)

            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    MediumTopAppBar(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        title = {
                            Text(
                                text = (LocalI18N.current["ui.global.var.title"] + "\t" + state.globalVar?.type?.toType()
                                    ?.label().fallbackOnEmpty(""))
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                back()
                            }) {
                                RemixIcon(remixName = Remix.Arrows.arrow_left_line)
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                },
                floatingActionButton = {
                    if (state.isList) {
                        FloatingActionButton(onClick = {
                            addMenuState.show()
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
                            StandardSpacer()
                        }

                        items(state.valueItems) {
                            ListItem(title = it.value, icon = {
                                val hint = it.hint
                                if (hint is TextHint.AppComponent) {
                                    AppIcon(modifier = Modifier.size(24.dp), pkg = hint.appPkg)
                                }
                            }) {
                                if (!state.isList) {
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