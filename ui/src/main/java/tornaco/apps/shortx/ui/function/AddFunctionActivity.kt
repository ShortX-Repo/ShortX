@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.function

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.context.ContextDataMapping.asAccessExample
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addda.FuncParamList
import tornaco.apps.shortx.ui.addrule.action.ActionContent
import tornaco.apps.shortx.ui.addrule.action.asActionHandler
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.ClickOutsideClearFocusBox
import tornaco.apps.shortx.ui.base.ConfirmDialog
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.FieldTitleWithTip
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.ShortXTextButton
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.UnderlineSmallText
import tornaco.apps.shortx.ui.base.handleSideEffect
import tornaco.apps.shortx.ui.base.rememberConfirmDialogState
import tornaco.apps.shortx.ui.util.ClipboardUtils
import tornaco.apps.shortx.core.i18n.I18N_R

@AndroidEntryPoint
class AddFunctionActivity : BaseActivity() {
    companion object {
        private const val KEY_FUNCTION_ID = "functionId"

        @JvmStatic
        fun create(context: Context) {
            context.startActivity(createIntent(context))
        }

        @JvmStatic
        fun edit(context: Context, functionId: String) {
            context.startActivity(editIntent(context, functionId))
        }

        fun createIntent(context: Context): Intent {
            return Intent(context, AddFunctionActivity::class.java)
        }

        fun editIntent(context: Context, functionId: String): Intent {
            return Intent(context, AddFunctionActivity::class.java)
                .putExtra(KEY_FUNCTION_ID, functionId)
        }
    }

    @Composable
    override fun Content() {
        val functionId = intent.getStringExtra(KEY_FUNCTION_ID)
        val screenTitle = if (functionId == null) {
            LocalI18N.current[I18N_R.UI_TITLE_ADD_FUNCTION]
        } else {
            LocalI18N.current[I18N_R.UI_TITLE_EDIT_FUNCTION]
        }
        AddFunctionScreen(functionId = functionId, screenTitle = screenTitle) {
            setResult(if (it) RESULT_OK else RESULT_CANCELED)
            finish()
        }
    }
}

@Composable
fun AddFunctionScreen(
    functionId: String? = null,
    screenTitle: String,
    readOnly: Boolean = false,
    back: (Boolean) -> Unit,
) {
    val vm = hiltViewModel<AddFunctionViewModel>()
    val state by vm.state.collectAsState()
    val actionContentState by vm.actionContentStateHandler.state.collectAsState()

    LaunchedEffect(vm) {
        vm.initFromFunctionId(functionId)
    }

    val context = LocalContext.current
    val i18n = LocalI18N.current
    vm.handleSideEffect {
        when (it) {
            is AddFunctionEffect.FunctionSaved -> {
                Toast.makeText(context, i18n[I18N_R.UI_COMMON_SAVED], Toast.LENGTH_SHORT)
                    .show()
                back(true)
            }

            is AddFunctionEffect.FunctionDeleted -> {
                back(true)
            }

            is AddFunctionEffect.AddParamFailNameExists -> {
                Toast.makeText(
                    context,
                    i18n[I18N_R.UI_FUNCTION_PARAM_EXISTS, mapOf("name" to it.name)],
                    Toast.LENGTH_SHORT
                ).show()
            }

            AddFunctionEffect.FunctionNameRequired -> {
                Toast.makeText(context, i18n[I18N_R.UI_FUNCTION_NAME_REQUIRED], Toast.LENGTH_SHORT)
                    .show()
            }

            AddFunctionEffect.FunctionDeleteNotFound -> {
                Toast.makeText(context, i18n[I18N_R.UI_FUNCTION_DELETE_NOT_FOUND], Toast.LENGTH_SHORT)
                    .show()
                back(false)
            }
        }
    }

    val backConfirmDialogState = rememberConfirmDialogState()
    ConfirmDialog(
        title = I18N_R.UI_EDIT_DISCARD_DIALOG_TITLE,
        messageHint = { i18n[I18N_R.UI_EDIT_DISCARD_DIALOG_MESSAGE] },
        confirmButton = i18n[I18N_R.UI_EDIT_DISCARD_DIALOG_DISCARD],
        dismissButton = i18n[I18N_R.UI_EDIT_DISCARD_DIALOG_STAY],
        state = backConfirmDialogState,
        data = "",
        onConfirm = { back(false) }
    )

    val deleteDialogState = rememberConfirmDialogState()
    ConfirmDialog(
        title = I18N_R.UI_COMMON_DELETE,
        messageHint = {
            i18n[I18N_R.UI_DELETE_DIALOG_CONFIRM, mapOf("wht" to state.name)]
        },
        state = deleteDialogState,
        data = state.id,
        onConfirm = { vm.delete(it) }
    )

    BackHandler {
        if (state.functionChanged || actionContentState.isChanged) {
            backConfirmDialogState.show()
        } else {
            back(false)
        }
    }

    ShortXAppBarScaffold(
        title = screenTitle,
        onBackPressed = {
            if (state.functionChanged || actionContentState.isChanged) {
                backConfirmDialogState.show()
            } else {
                back(false)
            }
        },
        actions = {
            if (state.isEditFunction) {
                IconButton(onClick = { deleteDialogState.show() }) {
                    RemixIcon(Remix.System.delete_bin_2_line)
                }
            }
            IconButton(onClick = { vm.save() }) {
                RemixIcon(Remix.System.check_line)
            }
        },
        verticalScroll = false
    ) {
        ClickOutsideClearFocusBox(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                ShortXTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(LocalI18N.current[I18N_R.UI_FUNCTION_NAME]) },
                    value = state.name,
                    readOnly = readOnly,
                    onValueChange = { vm.setName(it) },
                    showSymbolButton = false,
                )

                LargeSpacer()

                FieldTitle(text = LocalI18N.current[I18N_R.UI_FUNCTION_RETURN_TYPE])
                Spacer(modifier = Modifier.size(8.dp))
                ShortXTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.returnType,
                    readOnly = true,
                    onValueChange = {},
                    showSymbolButton = false,
                )

                LargeSpacer()

                FieldTitleWithTip(
                    text = LocalI18N.current[I18N_R.UI_FUNCTION_RESULT_CONTEXT_KEY],
                    tipText = LocalI18N.current[I18N_R.UI_FUNCTION_RESULT_CONTEXT_KEY_DESCRIPTION]
                )
                Spacer(modifier = Modifier.size(8.dp))
                ShortXTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.resultContextDataKey,
                    readOnly = readOnly,
                    onValueChange = { vm.setResultContextDataKey(it) },
                    showSymbolButton = false,
                )
                state.resultContextDataKey.takeIf { it.isNotBlank() }?.let { resultContextDataKey ->
                    val accessExample = resultContextDataKey.asAccessExample()
                    Spacer(modifier = Modifier.size(8.dp))
                    ShortXTextButton(onClick = {
                        ClipboardUtils.copyToClipboard(
                            context,
                            "functionResultContextKey",
                            accessExample
                        )
                        Toast.makeText(
                            context,
                            i18n[I18N_R.UI_COMMON_COPIED_TO_CLIPBOARD],
                            Toast.LENGTH_LONG
                        ).show()
                    }) {
                        UnderlineSmallText(
                            text = i18n[I18N_R.UI_CONTEXT_DATA_EXAMPLE, mapOf("eg" to accessExample)]
                        )
                    }
                }

                SectionSpacer()

                FuncParamList(
                    params = state.parameters,
                    i18nKeyPrefix = I18N_R.UI_FUNCTION_PARAMS,
                    add = { param, override -> vm.addParameter(param, override) },
                    remove = { vm.removeParameter(it) }
                )

                SectionSpacer()

                ActionContent(
                    srcId = state.id,
                    actions = actionContentState.actions,
                    actionAsyncMode = actionContentState.asyncMode,
                    canSelectActionAsyncMode = false,
                    evaluatingActions = emptyList(),
                    handler = vm.actionContentStateHandler.asActionHandler(),
                    readOnly = readOnly,
                    warnings = actionContentState.actionWarningMessages,
                    parameters = state.parameters
                )
            }
        }
    }
}
