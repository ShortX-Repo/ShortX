package tornaco.apps.shortx.ui.code

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import tornaco.apps.shortx.core.proto.common.CodeLibraryItem
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog

class CodeLibraryViewerState(
    val title: String,
    val item: CodeLibraryItem? = null,
    val url: String? = null,
) : CommonDialogState()

@Composable
fun rememberCodeLibraryViewerState(
    title: String,
    item: CodeLibraryItem? = null,
    url: String? = null,
): CodeLibraryViewerState {
    return remember {
        CodeLibraryViewerState(title, item, url)
    }
}

@Composable
fun CodeLibraryViewerDialog(state: CodeLibraryViewerState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            CodeLibraryEditorScreen(
                importItem = state.item,
                url = state.url,
                screenTitle = state.title,
                saveIconName = Remix.System.download_line,
                closeAfterSave = true,
            ) {
                state.dismiss()
            }
        }
    }
}