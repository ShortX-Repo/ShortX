@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.ActionContent
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.theme.ShortXTheme


class ShowOverlayButtonDialogState(
    val onSelected: (ShowOverlayButtonSettings) -> Unit
) {
    var isShowing by mutableStateOf(false)

    var currentSettings: ShowOverlayButtonSettings? = null

    fun show(currentSettings: ShowOverlayButtonSettings? = null) {
        this.currentSettings = currentSettings
        isShowing = true
    }

    fun dismiss() {
        isShowing = false
    }
}

@Composable
fun rememberShowOverlayButtonDialogState(
    onSelected: (ShowOverlayButtonSettings) -> Unit
): ShowOverlayButtonDialogState {
    return remember {
        ShowOverlayButtonDialogState(onSelected)
    }
}

@Composable
fun ShowOverlayButtonDialog(state: ShowOverlayButtonDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ShowOverlayButtonSelector(state)
        }
    }
}

@Composable
private fun ShowOverlayButtonSelector(dialogState: ShowOverlayButtonDialogState) {
    val vm = hiltViewModel<ShowOverlayButtonSelectorViewModel>().apply { resetState() }
    LaunchedEffect(dialogState.currentSettings) {
        dialogState.currentSettings?.let {
            vm.initFrom(it)
        }
    }
    LaunchedEffect(vm) {
        vm.loadInstalledApps()
    }
    val state by vm.state.collectAsState()

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    TopAppBar(
                        modifier = Modifier,
                        title = { Text(LocalI18N.current["ui.action.show.overlay"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.Arrows.arrow_left_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                dialogState.onSelected(
                                    ShowOverlayButtonSettings(
                                        id = state.id,
                                        autoCollapse = state.autoCollapse,
                                        items = state.items,
                                        tag = state.tag
                                    )
                                )
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }

                        },
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
            MDIconSelectorDialog(state = iconSelector)

            OutlinedCard(
                modifier = Modifier,
                colors = CardDefaults.cardColors()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    MediumTitle(text = LocalI18N.current["ui.action.show.overlay.button.of", mapOf("index" to "${index + 1}")])
                    StandardSpacer()

                    Column(verticalArrangement = Arrangement.Center) {
                        SmallTitle(LocalI18N.current["ui.action.show.overlay.text"])
                        OutlinedTextField(value = item.label, onValueChange = {
                            vm.updateLabel(index, it)
                        })
                    }
                    StandardSpacer()

                    Column(verticalArrangement = Arrangement.Center) {
                        SmallTitle(LocalI18N.current["ui.action.show.overlay.icon"])
                        TextButton(onClick = {
                            iconSelector.show()
                        }) {
                            RemixIcon(item.icon)
                        }
                    }

                    ActionContent(
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
            }
        }

        StandardSpacer()
        Column(verticalArrangement = Arrangement.Center) {
            SmallTitle(LocalI18N.current["ui.action.show.overlay.tag"])
            TipBody(LocalI18N.current["ui.action.show.overlay.tag.description"])
            OutlinedTextField(value = state.tag, onValueChange = {
                vm.updateTag(it)
            })
        }

        StandardSpacer()
        Column(verticalArrangement = Arrangement.Center) {
            SmallTitle(text = LocalI18N.current["ui.action.show.auto.collapse"])
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