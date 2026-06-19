package tornaco.apps.shortx.ui.gvar

import android.widget.Toast
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.rule.GLOBAL_VAR_REF_PREFIX
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.ShortXAlertDialog
import tornaco.apps.shortx.ui.base.ShortXTextButton
import tornaco.apps.shortx.ui.util.ClipboardUtils
import tornaco.apps.shortx.core.i18n.I18N_R

class GlobalVarAccessHelpDialogState(val globalVar: GlobalVar) : CommonDialogState()

@Composable
fun rememberGlobalVarAccessHelpDialogState(
    globalVar: GlobalVar
): GlobalVarAccessHelpDialogState {
    return remember {
        GlobalVarAccessHelpDialogState(globalVar)
    }
}

@Composable
fun GlobalVarAccessHelpDialog(state: GlobalVarAccessHelpDialogState) {
    if (state.isShowing) {
        val context = LocalContext.current
        val i18N = LocalI18N.current

        ShortXAlertDialog(
            onDismissRequest = {
                state.dismiss()
            },
            confirmButton = {
                ShortXTextButton(onClick = {
                    state.dismiss()

                    ClipboardUtils.copyToClipboard(
                        context,
                        "globalVar",
                        GLOBAL_VAR_REF_PREFIX + state.globalVar.name
                    )
                    Toast.makeText(
                        context,
                        i18N[I18N_R.UI_COMMON_COPIED_TO_CLIPBOARD],
                        Toast.LENGTH_LONG
                    ).show()

                }) {
                    Text(text = stringResource(id = android.R.string.copy))
                }
            },
            title = {
                Text(text = i18N[I18N_R.UI_GLOBAL_VAR_ACCESS_HELP_TITLE] + " " + state.globalVar.name)
            },
            text = {
                Text(
                    text = i18N[I18N_R.UI_GLOBAL_VAR_ACCESS_HELP_MESSAGE, mapOf("name" to GLOBAL_VAR_REF_PREFIX + state.globalVar.name)]
                )
            })
    }
}