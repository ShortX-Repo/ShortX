package tornaco.apps.shortx.ui.gvar

import android.widget.Toast
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.rule.GLOBAL_VAR_REF_PREFIX
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.ShortXAlertDialog
import tornaco.apps.shortx.ui.util.ClipboardUtils

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
                TextButton(onClick = {
                    state.dismiss()

                    ClipboardUtils.copyToClipboard(
                        context,
                        "globalVar",
                        GLOBAL_VAR_REF_PREFIX + state.globalVar.name
                    )
                    Toast.makeText(
                        context,
                        i18N["ui.common.copied.to.clipboard"],
                        Toast.LENGTH_LONG
                    ).show()

                }) {
                    Text(text = stringResource(id = android.R.string.copy))
                }
            },
            title = {
                Text(text = i18N["ui.global.var.access.help.title"] + " " + state.globalVar.name)
            },
            text = {
                Text(
                    text = i18N["ui.global.var.access.help.message", mapOf("name" to GLOBAL_VAR_REF_PREFIX + state.globalVar.name)]
                )
            })
    }
}