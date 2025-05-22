@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.action.ShowOverlayButton
import tornaco.apps.shortx.core.proto.settings.OverlayButtonComponentType
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.ActionContent
import tornaco.apps.shortx.ui.addrule.action.asActionHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.toProtoAction
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.popupSelectorResult
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.LocalUiStore
import tornaco.apps.shortx.ui.base.MDIcon
import tornaco.apps.shortx.ui.base.MDIconSelectorActivity
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.SwitchBarNoBackground
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.overlay.OverlayContent
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import kotlin.math.roundToInt

@AndroidEntryPoint
class ShowOverlayButtonSelectorActivity : BaseSelectorActivity<Action.ShowOverlay>() {
    @Composable
    override fun BaseSelectorActivity<Action.ShowOverlay>.SelectorContent(currentData: Action.ShowOverlay?) {
        ShowOverlayButtonSelector(currentData)
    }

}

@Composable
private fun BaseSelectorActivity<Action.ShowOverlay>.ShowOverlayButtonSelector(currentAction: Action.ShowOverlay?) {
    val vm = hiltViewModel<ShowOverlayButtonSelectorViewModel>()
    LaunchedEffect(vm) {
        if (currentAction == null) {
            vm.resetState()
        } else {
            vm.initFrom(currentAction)
        }
    }
    val selectorState by vm.state.collectAsState()

    ShortXAppBarScaffold(
        title = LocalI18N.current["ui.action.show.overlay"],
        verticalScroll = false,
        onBackPressed = { cancelAndFinish() },
        actions = {
            IconButton(onClick = {
                val action = selectorState.toAction()
                setResultAndFinish(action)
            }) {
                RemixIcon(remixName = Remix.System.check_line)
            }
        },
    ) {
        Content(vm = vm)
    }
}

@Composable
private fun Content(
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

    val context = LocalContext.current
    val localUiStore = LocalUiStore.current
    val iconLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val icon = result.popupSelectorResult<MDIcon>()
            val buttonIndex =
                localUiStore.ShowOverlayButtonSelectorActivity_SelectButtonIcon_Index.get()
            icon?.let {
                vm.updateIcon(buttonIndex, it.iconNameOrBase64)
            }
        }
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        // Preview.
        stickyHeader {
            OutlinedCard(shape = ShortXCardRoundedCornerShape) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    FieldTitle(text = LocalI18N.current["ui.common.preview"])
                    StandardSpacer()
                    val action = remember(state) {
                        derivedStateOf {
                            state.toAction().copy(autoCollapse = false).toProtoAction()
                        }
                    }
                    OverlayContent(
                        action = action.value as ShowOverlayButton,
                        localVars = LocalVars.obtain(),
                        contextData = emptyMap(),
                        parameters = emptyList(),
                        onButtonClick = {},
                        onButtonLongClick = {},
                        dragHandleMotionEvent = {},
                        onExpand = {},
                        onCollapse = {}
                    )
                }
            }
            LargeSpacer()
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = SpaceBetween
            ) {
                FieldTitle(text = LocalI18N.current["ui.action.show.add.button"])
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        vm.setButtonListExpanded(!state.isButtonListExpanded)
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
        }
        if (state.isButtonListExpanded) {
            itemsIndexed(state.buttons) { index, button ->
                when (button.componentType) {
                    OverlayButtonComponentType.OverlayButtonComponentType_CircleProgress -> {
                        ProgressBarListItem(
                            index = index,
                            button = button,
                            updateLabel = { i: Int, s: String ->
                                vm.updateLabel(i, s)
                            },
                            updateProgressValue = { i: Int, s: String ->
                                vm.updateProgressValue(i, s)
                            },
                            removeButton = {
                                vm.removeButton(it)
                            }
                        )
                    }

                    else -> {
                        ButtonListItem(
                            index = index,
                            button = button,
                            evaluatingActions = state.evaluatingActions,
                            updateLabel = { i: Int, s: String ->
                                vm.updateLabel(i, s)
                            },
                            updateIcon = { i: Int, s: String ->
                                vm.updateIcon(i, s)
                            },
                            selectIcon = {
                                localUiStore.ShowOverlayButtonSelectorActivity_SelectButtonIcon_Index.set(
                                    it
                                )
                                iconLauncher.launch(
                                    BaseSelectorActivity.newIntent(
                                        context,
                                        null,
                                        MDIconSelectorActivity::class.java
                                    )
                                )
                            },
                            removeButton = {
                                vm.removeButton(it)
                            }
                        )
                    }
                }

            }
        }

        if (state.isButtonListExpanded) {
            item {
                StandardSpacer()
                // Add button
                Button(
                    modifier = Modifier.fillMaxSize(),
                    onClick = {
                        vm.addButton()
                    }) {
                    Text(text = LocalI18N.current["ui.action.show.add.button"])
                }
                // Add progress
                Button(
                    modifier = Modifier.fillMaxSize(),
                    onClick = {
                        vm.addButton(componentType = OverlayButtonComponentType.OverlayButtonComponentType_CircleProgress)
                    }) {
                    Text(text = LocalI18N.current["ui.action.show.add.progress.bar"])
                }
            }
        }

        item {
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
        }

        item {
            StandardSpacer()
            SwitchBarNoBackground(
                isChecked = state.autoCollapse,
                text = LocalI18N.current["ui.action.show.auto.collapse"]
            ) {
                vm.setAutoCollapse(it)
            }
        }

        item {
            StandardSpacer()
            SwitchBarNoBackground(
                isChecked = state.enableAutoEdge,
                text = LocalI18N.current["ui.action.show.auto.edge"]
            ) {
                vm.setEnableAutoEdge(it)
            }
        }

        item {
            StandardSpacer()
            SwitchBarNoBackground(
                isChecked = state.closeOnTouchOutside,
                text = LocalI18N.current["ui.action.close.touch.outside"]
            ) {
                vm.setCloseOnTouchOutside(it)
            }
        }

        item {
            StandardSpacer()
            SwitchBarNoBackground(
                isChecked = state.closeOnAction,
                text = LocalI18N.current["ui.action.close.on.action"]
            ) {
                vm.setCloseOnAction(it)
            }
        }

        item {
            StandardSpacer()
            SwitchBarNoBackground(
                isChecked = state.enableGlobalDrag,
                text = LocalI18N.current["ui.common.button.enable.global.drag"],
                tip = LocalI18N.current["ui.common.button.enable.global.drag.description"]
            ) {
                vm.setEnableGlobalDrag(it)
            }
        }

        item {
            StandardSpacer()
            Column(modifier = Modifier.fillMaxWidth()) {
                FieldTitle(text = LocalI18N.current["ui.common.overlay.max.width"])
                Text(text = state.maxWidth.toString())
                Slider(
                    valueRange = 20f..500f,
                    value = state.maxWidth.toFloat(),
                    onValueChange = {
                        vm.setMaxWidth(it.roundToInt())
                    })
            }
        }

        item {
            StandardSpacer()
            Column(modifier = Modifier.fillMaxWidth()) {
                FieldTitle(text = LocalI18N.current["ui.common.overlay.max.height"])
                Text(text = state.maxHeight.toString())
                Slider(
                    valueRange = 20f..500f,
                    value = state.maxHeight.toFloat(),
                    onValueChange = {
                        vm.setMaxHeight(it.roundToInt())
                    })
            }
        }
        item {
            StandardSpacer()
            Column(modifier = Modifier.fillMaxWidth()) {
                FieldTitle(text = LocalI18N.current["ui.common.button.min.width"])
                Text(text = state.buttonMinWidth.toString())
                Slider(
                    valueRange = 20f..500f,
                    value = state.buttonMinWidth.toFloat(),
                    onValueChange = {
                        vm.setButtonMinWidth(it.roundToInt())
                    })
            }
        }
        item {
            StandardSpacer()
            Column(modifier = Modifier.fillMaxWidth()) {
                FieldTitle(text = LocalI18N.current["ui.common.button.overlay.padding.v"])
                Text(text = state.overlayPaddingV.toString())
                Slider(
                    valueRange = 0f..50f,
                    value = state.overlayPaddingV.toFloat(),
                    onValueChange = {
                        vm.setOverlayPaddingV(it.roundToInt())
                    })
            }
        }
        item {
            StandardSpacer()
            Column(modifier = Modifier.fillMaxWidth()) {
                FieldTitle(text = LocalI18N.current["ui.common.button.overlay.padding.h"])
                Text(text = state.overlayPaddingH.toString())
                Slider(
                    valueRange = 0f..50f,
                    value = state.overlayPaddingH.toFloat(),
                    onValueChange = {
                        vm.setOverlayPaddingH(it.roundToInt())
                    })
            }

        }

        item {
            SectionSpacer()
            SuggestedNavEntry()
        }
    }
}

@Composable
private fun ButtonListItem(
    index: Int,
    button: Button,
    evaluatingActions: List<String>,
    updateLabel: (Int, String) -> Unit,
    updateIcon: (Int, String) -> Unit,
    selectIcon: (Int) -> Unit,
    removeButton: (Int) -> Unit,
) {
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
                    removeButton(index)
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
                    updateLabel(index, it)
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
                        updateIcon(index, "")
                    }) {
                        RemixIcon(
                            modifier = Modifier.size(18.dp),
                            remixName = Remix.Device.restart_line
                        )
                    }
                }
                if (button.icon.isBlank()) {
                    TextButton(onClick = {
                        selectIcon(index)
                    }) {
                        Text(text = "No icon")
                    }
                } else {
                    IconButton(onClick = {
                        selectIcon(index)
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
                        text = LocalI18N.current["ui.actions.single.click.title"]
                    )
                },
                actions = actionState.actions,
                evaluatingActions = evaluatingActions,
                warnings = actionState.actionWarningMessages,
                handler = actionStateHandle.asActionHandler()
            )

            StandardSpacer()
            val longClickActionStateHandle = remember(button) { button.longClickActionHandle }
            val longClickActionState by longClickActionStateHandle.state.collectAsState()

            ActionContent(
                header = {
                    SmallTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        text = LocalI18N.current["ui.actions.long.click.title"]
                    )
                },
                actions = longClickActionState.actions,
                evaluatingActions = evaluatingActions,
                warnings = longClickActionState.actionWarningMessages,
                handler = longClickActionStateHandle.asActionHandler()
            )
        }
    }
}


@Composable
private fun ProgressBarListItem(
    index: Int,
    button: Button,
    updateLabel: (Int, String) -> Unit,
    updateProgressValue: (Int, String) -> Unit,
    removeButton: (Int) -> Unit,
) {
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
                    removeButton(index)
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
                    updateLabel(index, it)
                })
            }
            StandardSpacer()

            Column(verticalArrangement = Arrangement.Center) {
                SmallTitle(
                    modifier = Modifier.padding(vertical = 12.dp),
                    LocalI18N.current["ui.action.show.overlay.progress.value"]
                )
                ShortXTextField(value = button.progressValue, onValueChange = {
                    updateProgressValue(index, it)
                })
            }
            StandardSpacer()
        }
    }
}

