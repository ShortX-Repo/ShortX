package tornaco.apps.shortx.ui.addrule.fact.selector


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.base.TipCard
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R

class BTDeviceSelectorState(
    val title: String,
    val message: String?,
    val showTimeoutSelector: Boolean,
    val onSelected: (String, String, String) -> Unit
) : CommonDialogState() {
    private val logger = Logger("BTDeviceSelectorState")

    var deviceName by mutableStateOf("")
    var deviceAddress by mutableStateOf("")
    var timeout by mutableStateOf("")

    fun show(
        initialNameValue: String? = null,
        initialAddressValue: String? = null,
        timeout: String? = "5000"
    ) {
        logger.logOnDebug("initialValue: $initialNameValue")
        this.deviceName = initialNameValue ?: ""
        this.deviceAddress = initialAddressValue ?: ""
        this.timeout = timeout ?: "5000"

        show()
    }
}

@Composable
fun rememberBTDeviceSelectorState(
    title: String,
    message: String? = null,
    showTimeoutSelector: Boolean = false,
    onSelected: (String, String, String) -> Unit
): BTDeviceSelectorState {
    return remember {
        BTDeviceSelectorState(
            title,
            message,
            showTimeoutSelector,
            onSelected
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BTDeviceSelectorDialog(state: BTDeviceSelectorState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ShortXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(modifier = Modifier,
                        topBar = {
                            ShortXTopAppBar(
                                modifier = Modifier,
                                title = { Text(text = state.title) },
                                actions = {
                                    IconButton(onClick = {
                                        state.onSelected(
                                            state.deviceName,
                                            state.deviceAddress,
                                            state.timeout
                                        )
                                        state.dismiss()
                                    }) {
                                        RemixIcon(remixName = Remix.System.check_line)
                                    }
                                },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        state.dismiss()
                                    }) {
                                        RemixIcon(remixName = Remix.System.close_line)
                                    }
                                }
                            )
                        }) { paddingValues ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            state.message?.let {
                                TipCard(
                                    modifier = Modifier,
                                    tip = it
                                )
                                LargeSpacer()
                            }

                            FieldTitle(
                                text = LocalI18N.current[I18N_R.UI_FACT_BT_DEVICE_ALIAS],
                            )
                            TinySpacer()
                            TipBody(LocalI18N.current[I18N_R.UI_FACT_BT_DEVICE_ALIAS_SUMMARY])
                            TinySpacer()
                            ShortXTextField(
                                value = state.deviceName,
                                showSymbolButton = true,
                                onValueChange = {
                                    state.deviceName = it
                                })
                            StandardSpacer()

                            FieldTitle(
                                text = LocalI18N.current[I18N_R.UI_FACT_BT_DEVICE_ADDRESS],
                            )
                            TinySpacer()
                            TipBody(text = LocalI18N.current[I18N_R.UI_FACT_BT_DEVICE_ADDRESS_TIP])
                            TinySpacer()
                            ShortXTextField(
                                value = state.deviceAddress,
                                showSymbolButton = true,
                                onValueChange = {
                                    state.deviceAddress = it
                                })
                            SectionSpacer()


                            if (state.showTimeoutSelector) {
                                FieldTitle(
                                    text = LocalI18N.current[I18N_R.UI_CONDITIONS_BT_DEVICE_FOUND_TIMEOUT],
                                )
                                TinySpacer()
                                ShortXTextField(
                                    value = state.timeout,
                                    onValueChange = {
                                        state.timeout = it
                                    })

                            }

                            SectionSpacer()

                            FieldTitle(text = LocalI18N.current[I18N_R.UI_FACT_BT_CHOOSE_SSID])
                            val deviceList = remember {
                                shortXManager.getBTBondedDevices()
                            }
                            deviceList.forEach {
                                tornaco.apps.shortx.ui.base.ListItem(
                                    title = it.alias,
                                    icon = {
                                        RemixIcon(remixName = Remix.Device.bluetooth_fill)
                                    },
                                    onClick = {
                                        state.onSelected(it.alias, it.address, state.timeout)
                                        state.dismiss()
                                    })
                            }

                        }
                    }
                }
            }
        }
    }
}
