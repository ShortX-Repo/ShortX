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
import tornaco.apps.shortx.ui.addrule.action.asActionHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme


class ShowOverlayButtonDialogState(
    val onSelected: (Action.ShowOverlay) -> Unit
) {
    var isShowing by mutableStateOf(false)

    var currentSettings: Action.ShowOverlay? = null

    fun show(currentSettings: Action.ShowOverlay? = null) {
        this.currentSettings = currentSettings
        isShowing = true
    }

    fun dismiss() {
        isShowing = false
    }
}

@Composable
fun rememberShowOverlayButtonDialogState(
    onSelected: (Action.ShowOverlay) -> Unit
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
    val vm = hiltViewModel<ShowOverlayButtonSelectorViewModel>()
    LaunchedEffect(vm) {
        val currentSettings = dialogState.currentSettings
        if (currentSettings == null) {
            vm.resetState()
        } else {
            vm.initFrom(currentSettings)
        }
    }
    val selectorState by vm.state.collectAsState()

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(LocalI18N.current["ui.action.show.overlay"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                val action = Action.ShowOverlay(
                                    id = selectorState.id,
                                    autoCollapse = selectorState.autoCollapse,
                                    tag = selectorState.tag,
                                    settings = selectorState.buttons.map { it.toOverlayButtonSetting() }
                                )

                                dialogState.onSelected(action)
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
        state.buttons.forEachIndexed { index, button ->
            val iconSelector = rememberMDIconSelectorState {
                vm.updateIcon(index, it)
            }
            MDIconSelectorDialog(state = iconSelector)

            ShortXCard(modifier = Modifier.padding(top = 16.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    MediumTitle(text = LocalI18N.current["ui.action.show.overlay.button.of", mapOf("index" to "${index + 1}")])
                    StandardSpacer()

                    Column(verticalArrangement = Arrangement.Center) {
                        SmallTitle(LocalI18N.current["ui.action.show.overlay.text"])
                        ShortXTextField(value = button.label, onValueChange = {
                            vm.updateLabel(index, it)
                        })
                    }
                    StandardSpacer()

                    Column(verticalArrangement = Arrangement.Center) {
                        SmallTitle(LocalI18N.current["ui.action.show.overlay.icon"])
                        TextButton(onClick = {
                            iconSelector.show()
                        }) {
                            RemixIcon(button.icon)
                        }
                    }

                    val actionStateHandle = remember(button) { button.actionHandle }
                    val actionState by actionStateHandle.state.collectAsState()

                    ActionContent(
                        header = {
                            SmallTitle(text = LocalI18N.current["ui.actions.title"])
                        },
                        actions = actionState.actions,
                        warnings = actionState.actionWarningMessages,
                        handler = actionStateHandle.asActionHandler()
                    )
                }
            }
        }

        StandardSpacer()
        Column(verticalArrangement = Arrangement.Center) {
            SmallTitle(LocalI18N.current["ui.action.show.overlay.tag"])
            TipBody(LocalI18N.current["ui.action.show.overlay.tag.description"])
            ShortXTextField(value = state.tag, onValueChange = {
                vm.updateTag(it)
            })
        }

        StandardSpacer()
        Column(verticalArrangement = Arrangement.Center) {
            SmallTitle(text = LocalI18N.current["ui.action.show.auto.collapse"])
            ShortXSwitch(checked = state.autoCollapse, onCheckedChange = {
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

        SuggestedNavEntry()
    }
}