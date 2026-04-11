@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.FUNC_PARAM_REF_PREFIX
import tornaco.apps.shortx.ui.addrule.action.mapper.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.FieldTitleWithTip
import tornaco.apps.shortx.ui.base.InputValidateResult
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.OutlineBadge
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.theme.ShortXTheme

class ExecuteFunctionDialogState(
    val onSelected: (Action.ExecuteFunction) -> Unit
) : CommonDialogState() {
    var currentSettings: Action.ExecuteFunction? by mutableStateOf(null)

    fun show(current: Action.ExecuteFunction? = null) {
        currentSettings = current
        super.show()
    }

    override fun dismiss() {
        currentSettings = null
        super.dismiss()
    }
}

@Composable
fun rememberExecuteFunctionDialogState(
    onSelected: (Action.ExecuteFunction) -> Unit
): ExecuteFunctionDialogState {
    return remember {
        ExecuteFunctionDialogState(onSelected)
    }
}

@Composable
fun ExecuteFunctionSelectorDialog(state: ExecuteFunctionDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ExecuteFunctionSelector(state)
        }
    }
}

@Composable
private fun ExecuteFunctionSelector(dialogState: ExecuteFunctionDialogState) {
    val vm = hiltViewModel<ExecuteFunctionSelectorViewModel>()
    val state by vm.state.collectAsState()
    val i18n = LocalI18N.current

    LaunchedEffect(vm, dialogState.currentSettings) {
        vm.initFrom(dialogState.currentSettings)
    }

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    ShortXTopAppBar(
                        title = { Text(i18n["ui.action.execute.function"]) },
                        navigationIcon = {
                            IconButton(onClick = { dialogState.dismiss() }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    dialogState.onSelected(
                                        Action.ExecuteFunction(
                                            functionId = state.functionId,
                                            name = state.name,
                                            parameterInputs = state.parameterInputs,
                                            parameters = state.parameters
                                        )
                                    )
                                    dialogState.dismiss()
                                },
                                enabled = state.functionId.isNotBlank() && state.loadErrorMessage == null
                            ) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }
                        }
                    )
                }
            ) { paddingValues ->
                val functionSelectorState = rememberFunctionDialogState {
                    vm.selectFunction(it.id)
                }
                FunctionSelectorDialog(functionSelectorState)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(paddingValues)
                        .animateContentSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    StandardSpacer()
                    Column(verticalArrangement = Arrangement.Center) {
                        if (state.functionId.isNotBlank()) {
                            ListItem(
                                titleLines = listOf(state.name ?: state.functionId),
                                icon = {
                                    ColoredIcon(size = 36.dp) {
                                        RemixIcon(remixName = imageVectorForAction(action = Action.ExecuteFunction::class))
                                    }
                                }
                            )
                        }
                        Button(onClick = { functionSelectorState.show() }) {
                            Text(text = i18n["ui.title.select.function"])
                        }

                        state.loadErrorMessage?.let {
                            LargeSpacer()
                            TipRow(text = it)
                        }

                        LargeSpacer()

                        AnimatedVisibility(visible = state.parameterInputs.isNotEmpty()) {
                            Column {
                                FieldTitleWithTip(
                                    text = i18n["ui.function.params"],
                                    tipText = i18n[
                                        "ui.function.params.description",
                                        mapOf("arg" to "$FUNC_PARAM_REF_PREFIX${i18n["ui.function.params.name"]}")
                                    ]
                                )
                                TinySpacer()
                                ExecuteFunctionParams(state) {
                                    vm.updateParamInput(it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExecuteFunctionParams(
    state: ExecuteFunctionSelectorState,
    updateParamInput: (FuncParameterInput) -> Unit
) {
    state.parameterInputs.forEach { input ->
        val param = state.parameters.firstOrNull { it.name == input.name }

        val editValueDialogState = rememberTextInputState(
            title = input.name,
            message = null,
            inputValidator = { InputValidateResult.OK }
        ) {
            updateParamInput(FuncParameterInput.newBuilder(input).setValue(it).build())
        }
        TextInputDialog(state = editValueDialogState)

        ListItem(
            titleLines = listOf(
                LocalI18N.current["ui.function.params.name"] + " " + input.name,
                LocalI18N.current["ui.function.params.value"] + " " + input.value,
            ),
            action1 = {
                if (param?.isRequired == true) {
                    OutlineBadge(text = LocalI18N.current["ui.function.params.is.required"])
                }
            },
            icon = {
                RemixIcon(remixName = Remix.Editor.functions)
            },
            onClick = {
                editValueDialogState.show(input.value)
            }
        )
    }
}