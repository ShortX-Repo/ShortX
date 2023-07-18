@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.condition

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.ShortXDialog

class SIMSlotSelectorDialogState(
    val onSelected: (Int) -> Unit
) : CommonDialogState() {

    private var _slot by mutableStateOf(-1)
    val slot get() = _slot

    private fun setSlot(slot: Int) {
        _slot = slot
    }
}

@Composable
fun rememberSIMSlotSelectorState(
    onSelected: (Int) -> Unit
): SIMSlotSelectorDialogState {
    return remember {
        SIMSlotSelectorDialogState(onSelected)
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
                    title = LocalI18N.current["ui.conditions.mobile.data.enabled.slot",
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
            ListItem(title = LocalI18N.current["ui.conditions.mobile.data.enabled.any"], onClick = {
                state.onSelected(-1)
                state.dismiss()
            })
        }
    }
}