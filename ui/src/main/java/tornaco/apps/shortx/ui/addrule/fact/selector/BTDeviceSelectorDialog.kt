package tornaco.apps.shortx.ui.addrule.fact.selector


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DialogMessage
import tornaco.apps.shortx.ui.base.ErrorTipRow
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.theme.ShortXTheme

class BTDeviceSelectorState(
    val title: String,
    val message: String?,
    val showSymbolButton: Boolean,
    val keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    val inputValidator: ((String) -> Pair<Boolean, String>),
    val onSelected: (String) -> Unit
) : CommonDialogState() {
    private val logger = Logger("BTDeviceSelectorState")

    var currentValue by mutableStateOf("")

    var errorMsg by mutableStateOf("")
    var isValid by mutableStateOf(false)

    fun show(initialValue: String? = null) {
        logger.p("initialValue: $initialValue")
        this.currentValue = initialValue ?: ""
        this.errorMsg = ""
        this.isValid = true

        // Trigger validate
        val validate = inputValidator(this.currentValue)
        this.isValid = validate.first
        this.errorMsg = validate.second

        show()
    }
}

@Composable
fun rememberBTDeviceSelectorState(
    title: String,
    message: String?,
    showSymbolButton: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    inputValidator: ((String) -> Pair<Boolean, String>) = { it.isNotEmpty() to "" },
    onSelected: (String) -> Unit
): BTDeviceSelectorState {
    return remember {
        BTDeviceSelectorState(
            title,
            message,
            showSymbolButton,
            keyboardOptions,
            inputValidator,
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
                                        if (state.isValid) {
                                            state.onSelected(state.currentValue)
                                            state.dismiss()
                                        }
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
                                DialogMessage(
                                    text = it,
                                )
                                StandardSpacer()
                            }

                            ShortXTextField(
                                colors = TextFieldDefaults.textFieldColors(
                                    containerColor = Color.Transparent
                                ),
                                isError = state.isValid.not(),
                                value = state.currentValue,
                                showSymbolButton = state.showSymbolButton,
                                keyboardOptions = state.keyboardOptions,
                                onValueChange = {
                                    val validate = state.inputValidator(it)
                                    state.currentValue = it
                                    state.isValid = validate.first
                                    state.errorMsg = validate.second
                                })

                            MediumSpacer()
                            AnimatedVisibility(visible = !state.isValid && state.errorMsg.isNotEmpty()) {
                                ErrorTipRow(text = state.errorMsg)
                            }

                            SectionSpacer()

                            FieldTitle(text = LocalI18N.current["ui.fact.bt.choose.ssid"])
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
                                        state.onSelected(it.alias)
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
