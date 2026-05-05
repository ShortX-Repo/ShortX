package tornaco.apps.shortx.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXAlertDialog
import tornaco.apps.shortx.ui.base.ShortXTextButton


class DeleteSetConfirmDialogState(val setTitle: String, val itemsCount: Int) : CommonDialogState() {
    var alsoDeleteItems by mutableStateOf(false)

    override fun show() {
        alsoDeleteItems = false
        super.show()
    }
}

@Composable
fun rememberDeleteSetConfirmDialogState(
    setTitle: String,
    itemsCount: Int
): DeleteSetConfirmDialogState {
    return remember {
        DeleteSetConfirmDialogState(setTitle, itemsCount)
    }
}

@Composable
fun DeleteSetConfirmDialog(
    state: DeleteSetConfirmDialogState,
    onConfirm: (Boolean) -> Unit
) {
    if (state.isShowing) {
        val i18N = LocalI18N.current
        ShortXAlertDialog(
            onDismissRequest = {
                state.dismiss()
            },
            confirmButton = {
                ShortXTextButton(onClick = {
                    onConfirm(state.alsoDeleteItems)
                    state.dismiss()
                }) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            },
            dismissButton = {
                ShortXTextButton(onClick = { state.dismiss() }) {
                    Text(text = stringResource(id = android.R.string.cancel))
                }
            },
            title = {
                Text(text = i18N["ui.common.delete"])
            },
            text = {
                Column {
                    Text(text = i18N["ui.delete.dialog.confirm", mapOf("wht" to state.setTitle)])
                    AnimatedVisibility(visible = state.itemsCount > 0) {
                        SectionSpacer()
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Checkbox(
                                checked = state.alsoDeleteItems,
                                onCheckedChange = { state.alsoDeleteItems = it })
                            Text(text = i18N["ui.delete.dialog.also.delete.item", mapOf("count" to state.itemsCount.toString())])
                        }
                    }
                }
            })
    }
}