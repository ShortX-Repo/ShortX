@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import tornaco.apps.shortx.ui.addrule.action.ActionSelectors
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.theme.ShortXTheme


const val KEY_OVERLAY_BUTTON_SETTINGS = "selector.key.overlay.button.settings"

@Composable
fun ShowOverlayButtonSelector() {
    val navController: NavController = LocalNavHostController.current

    val vm = hiltViewModel<ShowOverlayButtonSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.loadInstalledApps()
    }
    val state by vm.state.collectAsState()

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    MediumTopAppBar(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        title = { LocalI18N.current["ui.action.show.overlay"] },
                        navigationIcon = {
                            IconButton(onClick = {}) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = "Back")
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                navController.popBackWithResult(
                                    mapOf(
                                        KEY_OVERLAY_BUTTON_SETTINGS to ShowOverlayButtonSettings(
                                            autoCollapse = state.autoCollapse, items = state.items
                                        )
                                    )
                                )
                            }) {
                                Icon(imageVector = Icons.Filled.Check, contentDescription = "Done")
                            }

                        },
                        scrollBehavior = scrollBehavior
                    )
                }) {
                Content(paddingValues = it, vm = vm)
            }
        }
    }

}

@Composable
private fun Content(paddingValues: PaddingValues, vm: ShowOverlayButtonSelectorViewModel) {
    val state by vm.state.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(paddingValues)
            .animateContentSize()
            .verticalScroll(rememberScrollState())
    ) {
        state.items.forEachIndexed { index, item ->
            val iconSelector = rememberMDIconSelectorState {
                vm.updateIcon(index, it)
            }
            MDIconSelector(state = iconSelector)
            Column {
                MediumTitle(text = LocalI18N.current["ui.action.show.button.of", mapOf("index" to "${index + 1}")])

                Row(verticalAlignment = Alignment.CenterVertically) {
                    SmallTitle(LocalI18N.current["ui.action.show.icon"])
                    TinySpacer()
                    TextButton(onClick = {
                        iconSelector.show()
                    }) {
                        Icon(
                            imageVector = MDIcons.filled(item.icon), contentDescription = item.icon
                        )
                    }
                }

                ActionSelectors(
                    header = {
                        SmallTitle(text = LocalI18N.current["ui.actions.title"])
                    }, addAction = { action ->
                        vm.addAction(index, action)
                    },
                    removeAction = { action ->
                        vm.removeAction(index, action)
                    },
                    actions = state.items[index].actions
                )
            }

            StandardSpacer()
            Divider(modifier = Modifier.fillMaxWidth())
            StandardSpacer()
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            SmallTitle(text = LocalI18N.current["ui.action.show.auto.collapse"])

            StandardSpacer()

            Switch(checked = state.autoCollapse, onCheckedChange = {
                vm.setAutoCollapse(it)
            })
        }


        StandardSpacer()

        // Add icon
        FilledTonalButton(onClick = {
            vm.addButton()
        }) {
            Text(text = LocalI18N.current["ui.action.show.add.button"])
        }
    }
}