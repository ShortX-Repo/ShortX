package tornaco.apps.shortx.ui.addrule.condition.selector

import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.condition.mapper.iconForOp
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.MenuDialogState
import tornaco.apps.shortx.ui.base.rememberMenuDialogState

@Composable
fun <DATA> rememberAllAnyMenuDialogState(
    title: String = LocalI18N.current["ui.condition.selector.app.all.any.op.title"],
    conditionTitle: String = "",
    message: String? = LocalI18N.current["ui.condition.selector.app.all.any.op.message", mapOf("cond" to conditionTitle)],
    menuItems: List<MenuDialogItem> = listOf(
        MenuDialogItem(
            id = "all",
            title = LocalI18N.current["ui.condition.selector.app.op.all.desc"],
            iconName = iconForOp(Op.All)
        ),
        MenuDialogItem(
            id = "any",
            title = LocalI18N.current["ui.condition.selector.app.op.any.desc"],
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

