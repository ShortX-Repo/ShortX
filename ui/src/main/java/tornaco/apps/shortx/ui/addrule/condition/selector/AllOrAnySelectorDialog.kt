package tornaco.apps.shortx.ui.addrule.condition.selector

import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.condition.mapper.iconForOp
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.MenuDialogState
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.core.i18n.I18N_R

@Composable
fun <DATA> rememberAppAllAnyMenuDialogState(
    title: String = LocalI18N.current[I18N_R.UI_CONDITION_SELECTOR_APP_ALL_ANY_OP_TITLE],
    message: String? = LocalI18N.current[I18N_R.UI_CONDITION_SELECTOR_APP_ALL_ANY_OP_MESSAGE],
    menuItems: List<MenuDialogItem> = listOf(
        MenuDialogItem(
            id = "all",
            title = LocalI18N.current[I18N_R.UI_CONDITION_SELECTOR_APP_OP_ALL_DESC],
            iconName = iconForOp(Op.All)
        ),
        MenuDialogItem(
            id = "any",
            title = LocalI18N.current[I18N_R.UI_CONDITION_SELECTOR_APP_OP_ANY_DESC],
            iconName = iconForOp(Op.Any)
        ),
    ),
    onItemSelected: (DATA?, Op) -> Unit
): MenuDialogState<DATA> {
    val state = rememberMenuDialogState<DATA>(
        title = title,
        message = message,
        menuItems = menuItems,
        onItemSelected = { data: DATA?, s: String ->
            val op = if (s == "all") {
                Op.All
            } else {
                Op.Any
            }
            onItemSelected(data, op)
        })

    return state
}


@Composable
fun <DATA> rememberComponentAllAnyMenuDialogState(
    title: String = LocalI18N.current[I18N_R.UI_CONDITION_SELECTOR_COMPONENT_ALL_ANY_OP_TITLE],
    message: String? = LocalI18N.current[I18N_R.UI_CONDITION_SELECTOR_COMPONENT_ALL_ANY_OP_MESSAGE],
    menuItems: List<MenuDialogItem> = listOf(
        MenuDialogItem(
            id = "all",
            title = LocalI18N.current[I18N_R.UI_CONDITION_SELECTOR_COMPONENT_OP_ALL_DESC],
            iconName = iconForOp(Op.All)
        ),
        MenuDialogItem(
            id = "any",
            title = LocalI18N.current[I18N_R.UI_CONDITION_SELECTOR_COMPONENT_OP_ANY_DESC],
            iconName = iconForOp(Op.Any)
        ),
    ),
    onItemSelected: (DATA?, Op) -> Unit
): MenuDialogState<DATA> {
    val state = rememberMenuDialogState<DATA>(
        title = title,
        message = message,
        menuItems = menuItems,
        onItemSelected = { data: DATA?, s: String ->
            val op = if (s == "all") {
                Op.All
            } else {
                Op.Any
            }
            onItemSelected(data, op)
        })

    return state
}

