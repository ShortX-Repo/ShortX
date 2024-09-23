@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.condition

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.common.OnOffToggle
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.addrule.action.mapper.labelKey
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.ColorDefaults
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.theme.ShortXTheme

class ToggleSlotSelectorDialogState(
    val onSelected: (Action.ToggleSlot) -> Unit
) : CommonDialogState() {

    fun show(current: Action.ToggleSlot? = null) {
        this.state = current?.state ?: OnOffToggle.OnOffToggle_On
        this.slotId = current?.slotId ?: 0

        show()
    }

    var state: OnOffToggle by mutableStateOf(OnOffToggle.OnOffToggle_On)
    var slotId by mutableIntStateOf(0)
}

@Composable
fun rememberToggleSlotSelectorState(
    onSelected: (Action.ToggleSlot) -> Unit
): ToggleSlotSelectorDialogState {
    return remember {
        ToggleSlotSelectorDialogState(onSelected)
    }
}

@Composable
fun ToggleSlotSelectorDialog(dialogState: ToggleSlotSelectorDialogState) {
    if (dialogState.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { dialogState.dismiss() }) {
            ShortXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(modifier = Modifier,
                        topBar = {
                            ShortXTopAppBar(
                                modifier = Modifier,
                                title = { Text(text = LocalI18N.current["ui.action.toggle.slot"]) },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        dialogState.dismiss()
                                    }) {
                                        RemixIcon(remixName = Remix.System.close_line)
                                    }
                                },
                                actions = {
                                    IconButton(onClick = {
                                        dialogState.dismiss()
                                        dialogState.onSelected(
                                            Action.ToggleSlot(
                                                state = dialogState.state,
                                                slotId = dialogState.slotId
                                            )
                                        )
                                    }) {
                                        RemixIcon(remixName = Remix.System.check_line)
                                    }
                                }
                            )
                        }) { paddingValues ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                                .padding(16.dp)
                        ) {
                            // On off
                            FieldTitle(text = LocalI18N.current["ui.common.on"])
                            TinySpacer()
                            val onOffState = rememberDropdownSelectorState()
                            Box {
                                FilledTonalButton(
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = ColorDefaults.backgroundSurfaceColor(
                                            1.dp
                                        )
                                    ), onClick = {
                                        onOffState.open()
                                    }) {
                                    Text(LocalI18N.current[dialogState.state.labelKey()])
                                }
                                DropdownSelector(
                                    state = onOffState,
                                    items = OnOffToggle.entries.toMutableList().apply {
                                        remove(OnOffToggle.UNRECOGNIZED)
                                    }.map {
                                        DropdownItem(
                                            labelLines = listOf(LocalI18N.current[it.labelKey()]),
                                            data = it,
                                        )
                                    }
                                ) {
                                    dialogState.state = it.data
                                }
                            }
                            StandardSpacer()

                            // Slot
                            val slotIdState = rememberSIMSlotSelectorState(
                                canSelectAny = false,
                                onSelected = {
                                    dialogState.slotId = it
                                })
                            SIMSlotSelectorDialog(state = slotIdState)

                            FieldTitle(text = LocalI18N.current["ui.action.switch.mobile.data.slot"])
                            TinySpacer()
                            FilledTonalButton(
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = ColorDefaults.backgroundSurfaceColor(
                                        1.dp
                                    )
                                ), onClick = {
                                    slotIdState.show()
                                }) {
                                Text(
                                    (dialogState.slotId >= 0).select(
                                        LocalI18N.current["ui.common.mobile.data.enabled.slot", mapOf(
                                            "slotId" to dialogState.slotId.toString(),
                                            "label" to (shortXManager.getSlotLabel(dialogState.slotId)
                                                ?: "")
                                        )],
                                        "N/A"
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}