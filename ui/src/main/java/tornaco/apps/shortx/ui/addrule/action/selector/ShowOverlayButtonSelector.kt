@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.ActionContent
import tornaco.apps.shortx.ui.addrule.action.asActionHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MDIconSelectorDialog
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXSwitch
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.base.rememberMDIconSelectorState
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme
import kotlin.math.roundToInt


class ShowOverlayButtonDialogState(
    val onSelected: (Action.ShowOverlay) -> Unit
) : CommonDialogState() {
    var currentSettings: Action.ShowOverlay? = null
    var isButtonListExpanded by mutableStateOf(true)

    fun show(currentSettings: Action.ShowOverlay? = null) {
        this.currentSettings = currentSettings
        this.isButtonListExpanded = true
        show()
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
                                    settings = selectorState.buttons.map { it.toOverlayButtonSetting() },
                                    maxHeight = selectorState.maxHeight,
                                    maxWidth = selectorState.maxWidth,
                                    backgroundColor = selectorState.backgroundColor,
                                    backgroundAlpha = selectorState.backgroundAlpha
                                )

                                dialogState.onSelected(action)
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }

                        },
                    )
                }) {
                Content(paddingValues = it, dialogState = dialogState, vm = vm)
            }
        }
    }

}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    dialogState: ShowOverlayButtonDialogState,
    vm: ShowOverlayButtonSelectorViewModel
) {
    val state by vm.state.collectAsState()

    val sortDialogState = rememberShowOverlayButtonSortState(buttons = {
        state.buttons.map {
            it.toOverlayButtonSetting()
        }
    }, apply = { overlayButtonSettings ->
        vm.applySort(overlayButtonSettings.map { it.toButton() })
    })
    ShowOverlayButtonSortDialog(state = sortDialogState)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(paddingValues)
            .animateContentSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FieldTitle(text = LocalI18N.current["ui.action.show.add.button"])
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    dialogState.isButtonListExpanded = !dialogState.isButtonListExpanded
                }) {
                    RemixIcon(remixName = Remix.Arrows.expand_up_down_fill)
                }
                IconButton(onClick = {
                    sortDialogState.show()
                }) {
                    RemixIcon(remixName = Remix.Editor.sort_desc)
                }
            }
        }
        SmallSpacer()
        AnimatedVisibility(visible = dialogState.isButtonListExpanded) {
            Column {
                state.buttons.forEachIndexed { index, button ->
                    ButtonListItem(vm, index, button)
                }
                StandardSpacer()
                // Add icon
                Button(
                    modifier = Modifier.fillMaxSize(),
                    onClick = {
                        vm.addButton()
                    }) {
                    Text(text = LocalI18N.current["ui.action.show.add.button"])
                }
            }
        }

        LargeSpacer()
        Column(verticalArrangement = Arrangement.Center) {
            FieldTitle(
                modifier = Modifier.padding(vertical = 12.dp),
                LocalI18N.current["ui.action.show.overlay.tag"]
            )
            TipBody(LocalI18N.current["ui.action.show.overlay.tag.description"])
            SmallSpacer()
            ShortXTextField(value = state.tag, onValueChange = {
                vm.updateTag(it)
            })
        }

        StandardSpacer()
        Column(verticalArrangement = Arrangement.Center) {
            FieldTitle(
                modifier = Modifier.padding(vertical = 12.dp),
                text = LocalI18N.current["ui.action.show.auto.collapse"]
            )
            SmallSpacer()
            ShortXSwitch(checked = state.autoCollapse, onCheckedChange = {
                vm.setAutoCollapse(it)
            })
        }

        StandardSpacer()
        Column(modifier = Modifier.fillMaxWidth()) {
            FieldTitle(text = LocalI18N.current["ui.common.max.width"])
            Text(text = state.maxWidth.toString())
            Slider(
                valueRange = 60f..500f,
                value = state.maxWidth.toFloat(),
                onValueChange = {
                    vm.setMaxWidth(it.roundToInt())
                })
        }
        StandardSpacer()
        Column(modifier = Modifier.fillMaxWidth()) {
            FieldTitle(text = LocalI18N.current["ui.common.max.height"])
            Text(text = state.maxHeight.toString())
            Slider(
                valueRange = 60f..500f,
                value = state.maxHeight.toFloat(),
                onValueChange = {
                    vm.setMaxHeight(it.roundToInt())
                })
        }

        SectionSpacer()
        SuggestedNavEntry()
    }
}

@Composable
private fun ButtonListItem(
    vm: ShowOverlayButtonSelectorViewModel,
    index: Int,
    button: Button
) {
    val iconSelector = rememberMDIconSelectorState {
        vm.updateIcon(index, it)
    }
    MDIconSelectorDialog(state = iconSelector)

    ShortXCard(modifier = Modifier.padding(top = 16.dp)) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = SpaceBetween) {
                MediumTitle(
                    text = LocalI18N.current["ui.action.show.overlay.button.of", mapOf(
                        "index" to "${index + 1}"
                    )]
                )
                IconButton(onClick = {
                    vm.removeButton(index)
                }) {
                    RemixIcon(remixName = Remix.System.close_line)
                }
            }

            StandardSpacer()

            Column(verticalArrangement = Arrangement.Center) {
                SmallTitle(
                    modifier = Modifier.padding(vertical = 12.dp),
                    LocalI18N.current["ui.action.show.overlay.text"]
                )
                ShortXTextField(value = button.label, onValueChange = {
                    vm.updateLabel(index, it)
                })
            }
            StandardSpacer()

            Column(verticalArrangement = Arrangement.Center) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SmallTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        LocalI18N.current["ui.action.show.overlay.icon"]
                    )
                    IconButton(onClick = {
                        vm.updateIcon(index, "")
                    }) {
                        RemixIcon(
                            modifier = Modifier.size(18.dp),
                            remixName = Remix.Device.restart_line
                        )
                    }
                }
                if (button.icon.isBlank()) {
                    TextButton(onClick = {
                        iconSelector.show()
                    }) {
                        Text(text = "No icon")
                    }
                } else {
                    IconButton(onClick = {
                        iconSelector.show()
                    }) {
                        RemixIcon(button.icon)
                    }
                }
            }

            val actionStateHandle = remember(button) { button.actionHandle }
            val actionState by actionStateHandle.state.collectAsState()

            ActionContent(
                header = {
                    SmallTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        text = LocalI18N.current["ui.actions.title"]
                    )
                },
                actions = actionState.actions,
                warnings = actionState.actionWarningMessages,
                handler = actionStateHandle.asActionHandler()
            )
        }
    }
}

