@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.gvar

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import tornaco.apps.shortx.core.os.USER_MASK_ANY
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.label
import tornaco.apps.shortx.core.rule.toType
import tornaco.apps.shortx.core.toAppComponent
import tornaco.apps.shortx.core.toComponentName
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.addrule.base.SingleAppSelectorDialog
import tornaco.apps.shortx.ui.addrule.base.rememberSingleAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.AppComponentSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.ComponentType
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberAppComponentSelectorState
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.theme.ShortXTheme


class GlobalVarDetailActivity : BaseActivity() {
    companion object {
        private const val KEY_NAME = "name"

        @JvmStatic
        fun start(context: Context, name: String) {
            val starter = Intent(context, GlobalVarDetailActivity::class.java).apply {
                putExtra(KEY_NAME, name)
            }
            context.startActivity(starter)
        }
    }

    @Composable
    override fun Content() {
        intent.getStringExtra(KEY_NAME)
            ?.let { GlobalVarDetailScreen(name = it, back = { finish() }) }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GlobalVarDetailScreen(name: String, back: () -> Unit) {
    val vm = hiltViewModel<GlobalVarDetailViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.loadState(name)
    }

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val valueInputState = rememberTextInputState(
                title = LocalI18N.current["ui.global.var.input.value"],
                message = null,
                onSelected = {
                    vm.addOrUpdateValue(listOf(it))
                }
            )
            TextInputDialog(state = valueInputState)

            val nameInputState = rememberTextInputState(
                title = LocalI18N.current["ui.global.var.name"],
                message = null,
                onSelected = {
                    vm.updateName(it)
                }
            )
            TextInputDialog(state = nameInputState)

            val appComponentSelectorState = rememberAppComponentSelectorState(
                types = ComponentType.values().toList(),
                onAppComponentSelected = { infoList ->
                    vm.addOrUpdateValue(infoList.map {
                        it.component.toComponentName().flattenToShortString()
                    })
                }
            )
            AppComponentSelectorDialog(state = appComponentSelectorState)

            val singleAppSelectorState = rememberSingleAppPkgSelectorState { app ->
                appComponentSelectorState.show(app.pkg, state.valueItems.mapNotNull {
                    if (it.hint is TextHint.AppComponent) {
                        ComponentName.unflattenFromString(it.value)?.toAppComponent(USER_MASK_ANY)
                    } else null
                })
            }
            SingleAppSelectorDialog(state = singleAppSelectorState)

            val addMenuState = rememberMenuDialogState<String>(
                title = LocalI18N.current["ui.common.add"],
                menuItems = listOf(
                    MenuDialogItem(
                        id = "input",
                        title = LocalI18N.current["ui.common.manually.input"],
                    ),
                    MenuDialogItem(
                        id = "select",
                        title = LocalI18N.current["ui.title.select.component"],
                    )
                ),
                onItemSelected = { _, id ->
                    if (id == "input") {
                        valueInputState.show()
                    } else {
                        singleAppSelectorState.show()
                    }
                }
            )
            MenuDialog(state = addMenuState)

            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    ShortXMediumTopAppBar(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        title = {
                            Text(
                                text = (state.globalVar?.type?.toType()?.label()
                                    .fallbackOnEmpty("N/A"))
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                back()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
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
                            StandardSpacer()
                            CategoryTitle(title = LocalI18N.current["ui.global.var.name"])
                        }

                        stickyHeader {
                            ListItem(title = globalVar.name, onClick = {
                                nameInputState.show()
                            })
                        }

                        stickyHeader {
                            StandardSpacer()
                            CategoryTitle(title = LocalI18N.current["ui.global.var.value"])
                            StandardSpacer()
                        }

                        itemsIndexed(state.valueItems) { index, it ->
                            ListItem(title = it.value,
                                icon = {
                                    val hint = it.hint
                                    if (hint is TextHint.AppComponent) {
                                        AppIcon(modifier = Modifier.size(24.dp), pkg = hint.appPkg)
                                    } else {
                                        RemixIcon(remixName = Remix.Development.code_line)
                                    }
                                }, action1 = {
                                    if (state.isList) {
                                        IconButton(onClick = {
                                            vm.removeAtForList(index)
                                        }) {
                                            RemixIcon(remixName = Remix.System.close_line)
                                        }
                                    }
                                }) {
                                if (!state.isList) {
                                    valueInputState.show()
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}