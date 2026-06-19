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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.FUNC_PARAM_REF_PREFIX
import tornaco.apps.shortx.ui.addrule.action.mapper.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.FieldTitleWithTip
import tornaco.apps.shortx.ui.base.InputValidateResult
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.OutlineBadge
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R

@AndroidEntryPoint
class FromDASelectorActivity : BaseSelectorActivity<Action.FromDA>() {
    @Composable
    override fun BaseSelectorActivity<Action.FromDA>.SelectorContent(currentData: Action.FromDA?) {
        FromDASelector(currentData = currentData)
    }

}

@Composable
private fun BaseSelectorActivity<Action.FromDA>.FromDASelector(currentData: Action.FromDA?) {
    val vm = hiltViewModel<FromDADialogSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.initFrom(currentData)
    }

    val state by vm.state.collectAsState()

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(LocalI18N.current[I18N_R.UI_ACTION_FROM_DA]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                cancelAndFinish()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                if (state.daId.isNotBlank()) {
                                    setResultAndFinish(
                                        Action.FromDA(
                                            daId = state.daId,
                                            title = state.title,
                                            description = state.description,
                                            parameters = state.parameters,
                                            parameterInputs = state.parameterInputs
                                        )
                                    )
                                }
                            }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }

                        },
                    )
                }) { paddingValues ->

                val daSelectorState = rememberDADialogState {
                    vm.selectDA(it.id)
                }
                DASelectorDialog(daSelectorState)

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
                        if (state.daId.isNotBlank()) {
                            ListItem(
                                titleLines = listOf(state.title ?: state.daId),
                                icon = {
                                    ColoredIcon(size = 36.dp) {
                                        RemixIcon(remixName = imageVectorForAction(action = Action.FromDA::class))
                                    }
                                })
                        }
                        Button(onClick = {
                            daSelectorState.show()
                        }) {
                            Text(text = LocalI18N.current[I18N_R.UI_TITLE_SELECT_DA])
                        }

                        LargeSpacer()

                        AnimatedVisibility(visible = state.parameterInputs.isNotEmpty()) {
                            Column {
                                FieldTitleWithTip(
                                    text = LocalI18N.current[I18N_R.UI_DA_PARAMS],
                                    tipText = LocalI18N.current[I18N_R.UI_DA_PARAMS_DESCRIPTION,
                                        mapOf("arg" to "$FUNC_PARAM_REF_PREFIX${LocalI18N.current[I18N_R.UI_DA_PARAMS_NAME]}")]
                                )
                                TinySpacer()
                                DAFunctionParams(state) {
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
private fun DAFunctionParams(
    fromDA: FromDASelectorState,
    updateParamInput: (FuncParameterInput) -> Unit
) {
    fromDA.parameterInputs.forEach { input ->
        val param = fromDA.parameters.firstOrNull { it.name == input.name }

        val editValueDialogState =
            rememberTextInputState(title = input.name, message = null, inputValidator = {
                InputValidateResult.OK
            }) {
                updateParamInput(FuncParameterInput.newBuilder(input).setValue(it).build())
            }
        TextInputDialog(state = editValueDialogState)

        ListItem(
            titleLines = listOf(
                LocalI18N.current[I18N_R.UI_DA_PARAMS_NAME] + " " + input.name,
                LocalI18N.current[I18N_R.UI_DA_PARAMS_VALUE] + " " + input.value,
            ),
            action1 = {
                if (param?.isRequired == true) {
                    OutlineBadge(text = LocalI18N.current[I18N_R.UI_DA_PARAMS_IS_REQUIRED])
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