@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import tornaco.apps.shortx.core.proto.common.GestureRecord
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.gesture.GestureRecordItem
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R


class GestureRecordDialogState(
    val onSelected: (GestureRecord) -> Unit
) : CommonDialogState() {
    var availableRecords by mutableStateOf<List<GestureRecord>>(emptyList())

    override fun show() {
        this.availableRecords = shortXManager.getAllGestureRecords(false)
        super.show()
    }
}

@Composable
fun rememberGestureRecordDialogState(
    onSelected: (GestureRecord) -> Unit
): GestureRecordDialogState {
    return remember {
        GestureRecordDialogState(onSelected)
    }
}

@Composable
fun GestureRecordSelectorDialog(state: GestureRecordDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            GestureRecordSelector(state)
        }
    }
}

@Composable
private fun GestureRecordSelector(dialogState: GestureRecordDialogState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(LocalI18N.current[I18N_R.UI_ACTION_GESTURE_RECORD_SELECT]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                        },
                    )
                }) { paddingValues ->

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = paddingValues
                ) {
                    items(dialogState.availableRecords) {
                        GestureRecordItem(record = it) {
                            dialogState.onSelected(it)
                            dialogState.dismiss()
                        }
                    }
                }

            }
        }
    }

}