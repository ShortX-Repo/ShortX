@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.main


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.AddRuleScreen
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog

class RuleViewerState(val rule: Rule) : CommonDialogState()

@Composable
fun rememberRuleViewerState(rule: Rule): RuleViewerState {
    return remember {
        RuleViewerState(rule)
    }
}


@Composable
fun RuleViewerDialog(state: RuleViewerState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            RuleViewer(state)
        }
    }
}

@Composable
private fun RuleViewer(state: RuleViewerState) {
    AddRuleScreen(
        rule = state.rule,
        screenTitle = state.rule.title,
        canSaveWithoutChanges = true,
        readOnly = true,
        saveIconName = Remix.System.download_line
    ) {
        state.dismiss()
    }
}