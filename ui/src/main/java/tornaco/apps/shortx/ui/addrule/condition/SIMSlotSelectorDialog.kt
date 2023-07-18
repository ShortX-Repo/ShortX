package tornaco.apps.shortx.ui.addrule.condition

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.ShortXDialog

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
        ShortXDialog(title = { Text(text = LocalI18N.current["ui.dialog.title.select.slot"]) },
            onDismissRequest = { state.dismiss() }) {

            val subInfos = shortXManager.getActiveSubscriptionInfoList().map {
                it.simSlotIndex to it.displayName
            }

            subInfos.forEach {
                ListItem(
                    title = LocalI18N.current["ui.common.mobile.data.enabled.slot",
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
                    title = LocalI18N.current["ui.conditions.mobile.data.enabled.any"],
                    onClick = {
                        state.onSelected(-1)
                        state.dismiss()
                    })
            }
        }
    }
}