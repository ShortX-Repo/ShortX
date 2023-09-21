@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.SwitchRow
import tornaco.apps.shortx.ui.base.code.Code
import tornaco.apps.shortx.ui.base.code.CodeEditor
import tornaco.apps.shortx.ui.base.code.CodeEditorState

class ShellCommandCodeEditorState(
    title: String,
    message: String?,
    guideTextAndLink: Pair<String, String>?,
    scope: CoroutineScope,
    execute: suspend (String) -> String,
    val onSelected2: (Code, Boolean) -> Unit
) : CodeEditorState(title, message, guideTextAndLink, scope, execute, {}) {
    var isSingleShot by mutableStateOf(false)

    fun show(initialText: String? = "", isSingleShot: Boolean) {
        this.isSingleShot = isSingleShot
        super.show(initialText)
    }
}

@Composable
fun rememberShellCommandCodeEditorState(
    title: String,
    message: String? = null,
    guideTextAndLink: Pair<String, String>? = null,
    execute: suspend (String) -> String,
    onSelected: (Code, Boolean) -> Unit
): ShellCommandCodeEditorState {
    val scope = rememberCoroutineScope()
    return remember {
        ShellCommandCodeEditorState(title, message, guideTextAndLink, scope, execute, onSelected)
    }
}

@Composable
fun ShellCommandCodeEditorDialog(state: ShellCommandCodeEditorState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ShellCommandCodeEditor(state)
        }
    }
}

@Composable
private fun ShellCommandCodeEditor(
    state: ShellCommandCodeEditorState
) {
    CodeEditor(state, onSave = {
        state.onSelected2(Code(src = state.currentText, note = state.note), state.isSingleShot)
        state.dismiss()
    }) {
        SwitchRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = LocalI18N.current["ui.action.shell.single.shot"],
            isChecked = state.isSingleShot,
            onCheckChange = {
                state.isSingleShot = it
            })
    }
}
