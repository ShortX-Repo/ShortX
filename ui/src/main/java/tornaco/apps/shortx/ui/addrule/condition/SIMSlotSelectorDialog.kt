package tornaco.apps.shortx.ui.addrule.condition

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.ShortXDialog
import tornaco.apps.shortx.core.i18n.I18N_R

class SIMSlotSelectorDialogState(
    val canSelectAny: Boolean,
    val onSelected: (Int) -> Unit
) : CommonDialogState() {
}

@Composable
fun rememberSIMSlotSelectorState(
    canSelectAny: Boolean = true,
    onSelected: (Int) -> Unit
): SIMSlotSelectorDialogState {
    return remember {
        SIMSlotSelectorDialogState(canSelectAny, onSelected)
    }
}

@Composable
fun SIMSlotSelectorDialog(state: SIMSlotSelectorDialogState) {
    if (state.isShowing) {
        ShortXDialog(title = { Text(text = LocalI18N.current[I18N_R.UI_DIALOG_TITLE_SELECT_SLOT]) },
            onDismissRequest = { state.dismiss() }) {

            val subInfos = shortXManager.getActiveSubscriptionInfoList().map {
                it.simSlotIndex to it.displayName
            }

            subInfos.forEach {
                ListItem(
                    title = LocalI18N.current[I18N_R.UI_COMMON_MOBILE_DATA_ENABLED_SLOT,
                        mapOf(
                            "slotId" to it.first.toString(),
                            "label" to it.second.toString()
                        )],
                    onClick = {
                        state.onSelected(it.first)
                        state.dismiss()
                    }
                )
            }
            if (state.canSelectAny) {
                ListItem(
                    title = LocalI18N.current[I18N_R.UI_CONDITIONS_MOBILE_DATA_ENABLED_ANY],
                    onClick = {
                        state.onSelected(-1)
                        state.dismiss()
                    })
            }
        }
    }
}