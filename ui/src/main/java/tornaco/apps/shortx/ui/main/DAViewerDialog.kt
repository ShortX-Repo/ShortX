@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.main


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addda.AddDirectActionScreen
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog

class DAViewerState(val da: DirectAction) : CommonDialogState()

@Composable
fun rememberDAViewerState(rule: DirectAction): DAViewerState {
    return remember {
        DAViewerState(rule)
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
        screenTitle = state.da.title,
        canSaveWithoutChanges = true,
        readOnly = true,
        saveIconName = Remix.System.download_line
    ) {
        state.dismiss()
    }
}