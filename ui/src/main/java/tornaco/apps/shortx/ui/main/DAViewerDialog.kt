package tornaco.apps.shortx.ui.main


import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addda.AddDirectActionScreen
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog

class DAViewerState(
    val title: String,
    val da: DirectAction? = null,
    val url: String? = null
) : CommonDialogState()

@Composable
fun rememberDAViewerState(
    title: String,
    da: DirectAction? = null,
    url: String? = null
): DAViewerState {
    return remember {
        DAViewerState(title, da, url)
    }
}

@Composable
fun DAViewerDialog(state: DAViewerState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            DAViewer(state)
        }
    }
}

@Composable
private fun DAViewer(state: DAViewerState) {
    AddDirectActionScreen(
        da = state.da,
        url = state.url,
        screenTitle = state.title,
        canSaveWithoutChanges = true,
        readOnly = false,
        saveIconName = Remix.System.download_line
    ) {
        state.dismiss()
    }
}