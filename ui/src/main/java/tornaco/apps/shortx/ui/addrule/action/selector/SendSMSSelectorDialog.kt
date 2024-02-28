@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.selector.*
import tornaco.apps.shortx.ui.addrule.condition.SIMSlotSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.rememberSIMSlotSelectorState
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme

class SendSMSSelectorState(
    val onSelected: (Action.SendSMS) -> Unit
) : CommonDialogState() {
    var slotId: Int by mutableStateOf(0)
    var to: String by mutableStateOf("")
    var message: String by mutableStateOf("")

    fun show(current: Action.SendSMS? = null) {
        this.slotId = current?.slotId ?: 0
        this.to = current?.to ?: ""
        this.message = current?.message ?: ""

        super.show()
    }
}

@Composable
fun rememberSendSMSSelectorState(
    onSelected: (Action.SendSMS) -> Unit
): SendSMSSelectorState {
    return remember {
        SendSMSSelectorState(onSelected)
    }
}

@Composable
fun SendSMSSelectorDialog(state: SendSMSSelectorState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            SendSMSSelector(state)
        }
    }
}

@Composable
private fun SendSMSSelector(
    state: SendSMSSelectorState
) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier, topBar = {
                ShortXTopAppBar(
                    modifier = Modifier,
                    title = { Text(text = LocalI18N.current["ui.action.send.sms"]) },
                    navigationIcon = {
                        IconButton(onClick = {
                            state.dismiss()
                        }) {
                            RemixIcon(remixName = Remix.System.close_line)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            if (state.slotId >= 0 && state.to.isNotBlank()) {
                                state.dismiss()
                                state.onSelected(
                                    Action.SendSMS(
                                        slotId = state.slotId,
                                        message = state.message,
                                        to = state.to
                                    )
                                )
                            }
                        }) {
                            Icon(imageVector = Icons.Outlined.Check, contentDescription = "Done")
                        }
                    },
                )
            }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                ) {
                    ShortXTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.to,
                        onValueChange = {
                            state.to = it
                        },
                        label = {
                            Text(LocalI18N.current["ui.action.send.sms.to"])
                        },
                        showSymbolButton = false,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    StandardSpacer()


                    ShortXTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.message,
                        onValueChange = {
                            state.message = it
                        },
                        label = {
                            Text(LocalI18N.current["ui.action.send.sms.message"])
                        },
                    )
                    StandardSpacer()


                    val slotIdString = (state.slotId >= 0).select(
                        LocalI18N.current["ui.common.mobile.data.enabled.slot", mapOf(
                            "slotId" to state.slotId.toString(),
                            "label" to (shortXManager.getSlotLabel(state.slotId) ?: "")
                        )],
                        "N/A"
                    )

                    val slotSelectorDialogState = rememberSIMSlotSelectorState {
                        state.slotId = it
                    }
                    SIMSlotSelectorDialog(state = slotSelectorDialogState)

                    FilledTonalButton(onClick = {
                        slotSelectorDialogState.show()
                    }) {
                        Text(text = slotIdString)
                    }


                    StandardSpacer()
                    StandardSpacer()

                    SuggestedNavEntry()
                }
            }
        }
    }
}