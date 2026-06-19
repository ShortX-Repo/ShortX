@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.condition.SIMSlotSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.rememberSIMSlotSelectorState
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R

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
                    title = { Text(text = LocalI18N.current[I18N_R.UI_ACTION_SEND_SMS]) },
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
                            Text(LocalI18N.current[I18N_R.UI_ACTION_SEND_SMS_TO])
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
                            Text(LocalI18N.current[I18N_R.UI_ACTION_SEND_SMS_MESSAGE])
                        },
                    )
                    StandardSpacer()


                    val slotIdString = (state.slotId >= 0).select(
                        LocalI18N.current[I18N_R.UI_COMMON_MOBILE_DATA_ENABLED_SLOT, mapOf(
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