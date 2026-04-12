package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.fact.ScreenCorner
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.fact.mapper.displayNameKey
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXDialog
import tornaco.apps.shortx.ui.base.ShortXSwitch
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState

class CornerSwipeToCenterSelectorState(
    private val selected: (Fact.CornerSwipeToCenter) -> Unit,
) : CommonDialogState() {
    private var _corner by mutableStateOf(ScreenCorner.CornerTopLeft)
    val corner get() = _corner

    private var _enableGestureHint by mutableStateOf(true)
    val enableGestureHint get() = _enableGestureHint

    private var _isIntercept by mutableStateOf(false)
    val isIntercept get() = _isIntercept

    fun setCorner(corner: ScreenCorner) {
        _corner = corner
    }

    fun setEnableGestureHint(enable: Boolean) {
        _enableGestureHint = enable
    }

    fun setIsIntercept(isIntercept: Boolean) {
        _isIntercept = isIntercept
    }

    fun confirm() {
        selected(
            Fact.CornerSwipeToCenter(
                corner = corner,
                enableGestureHint = enableGestureHint,
                isIntercept = isIntercept,
            )
        )
        dismiss()
    }

    fun show(fact: Fact.CornerSwipeToCenter? = null) {
        _corner = fact?.corner ?: ScreenCorner.CornerTopLeft
        _enableGestureHint = fact?.enableGestureHint ?: true
        _isIntercept = fact?.isIntercept ?: false
        show()
    }
}

@Composable
fun rememberCornerSwipeToCenterSelectorState(
    selected: (Fact.CornerSwipeToCenter) -> Unit,
): CornerSwipeToCenterSelectorState {
    return remember { CornerSwipeToCenterSelectorState(selected) }
}

@Composable
fun CornerSwipeToCenterSelector(state: CornerSwipeToCenterSelectorState) {
    if (!state.isShowing) return

    ShortXDialog(
        onDismissRequest = { state.dismiss() },
        title = { DialogTitle(text = LocalI18N.current["ui.fact.corner.swipe.to.center"]) },
        buttons = {
            TextButton(onClick = { state.confirm() }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        },
        content = {
            val i18n = LocalI18N.current
            Box {
                val dropdownState = rememberDropdownSelectorState()
                Column(modifier = Modifier.fillMaxWidth()) {
                    FieldTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        text = i18n["ui.fact.corner.swipe.to.center"]
                    )
                    ListItem(
                        icon = { RemixIcon(remixName = Remix.Arrows.corner_down_right_fill) },
                        title = i18n[state.corner.displayNameKey()],
                        action1 = {
                            IconButton(onClick = { dropdownState.open() }) {
                                RemixIcon(remixName = Remix.Arrows.arrow_right_s_line)
                            }
                        },
                        onClick = { dropdownState.open() }
                    )
                    StandardSpacer()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FieldTitle(text = i18n["ui.fact.corner.gesture.indicator"])
                        ShortXSwitch(
                            checked = state.enableGestureHint,
                            onCheckedChange = { state.setEnableGestureHint(it) }
                        )
                    }
                    StandardSpacer()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FieldTitle(text = i18n["ui.fact.key.event.adv.is.intercept.mode"])
                        ShortXSwitch(
                            checked = state.isIntercept,
                            onCheckedChange = { state.setIsIntercept(it) }
                        )
                    }
                }
                DropdownSelector(
                    state = dropdownState,
                    items = ScreenCorner.entries.filter { it != ScreenCorner.UNRECOGNIZED }
                        .map {
                            DropdownItem(
                                data = it,
                                labelLines = listOf(i18n[it.displayNameKey()])
                            )
                        },
                    onSelect = { state.setCorner(it.data) }
                )
            }
        }
    )
}