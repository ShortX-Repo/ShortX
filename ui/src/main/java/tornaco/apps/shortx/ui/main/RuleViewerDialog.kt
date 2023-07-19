package tornaco.apps.shortx.ui.main


import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.AddRuleScreen
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog

class RuleViewerState(
    val title: String,
    val rule: Rule? = null,
    val url: String? = null
) :
    CommonDialogState()

@Composable
fun rememberRuleViewerState(
    title: String,
    rule: Rule? = null,
    url: String? = null
): RuleViewerState {
    return remember {
        RuleViewerState(title, rule, url)
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
        url = state.url,
        screenTitle = state.title,
        canSaveWithoutChanges = true,
        readOnly = false,
        saveIconName = Remix.System.download_line
    ) {
        state.dismiss()
    }
}