package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.action.mapper.colorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.base.SelectorItem
import tornaco.apps.shortx.ui.addrule.base.SelectorList
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MDIconSelectorDialog
import tornaco.apps.shortx.ui.base.rememberMDIconSelectorState
import tornaco.apps.shortx.ui.main.SubscribeState


@Composable
fun miscActionSelectHandler(addAction: (Action) -> Unit): (ActionSelectSource) -> Boolean {
    val noActionIconSelectorState = rememberMDIconSelectorState(onSelected = {
        addAction(Action.NoAction(icon = it))
    })
    MDIconSelectorDialog(state = noActionIconSelectorState)
    return { source ->
        when (source.clazz) {
            Action.NoAction::class -> {
                noActionIconSelectorState.show()
                true
            }

            else -> {
                false
            }
        }
    }
}

@Composable
fun MiscActions(keyword: String, subscribeState: SubscribeState, addAction: (Action) -> Unit) {
    val i18N = LocalI18N.current

    val actionItems = listOf(
        SelectorItem(
            type = Action.NoAction::class,
            labelAndDescription = labelAndDescriptionForActionSelector(
                i18N,
                Action.NoAction::class
            ),
            icon = imageVectorForAction(Action.NoAction::class),
            isForSubscribedOnly = false,
            iconColor = colorForAction(Action.NoAction::class)
        ),
    ).filter {
        keyword.isBlank() || it.labelAndDescription.first.contains(keyword, ignoreCase = true)
    }

    if (actionItems.isNotEmpty()) {
        ActionCategoryTitle(ActionCategory.Misc)
    }

    val selectHandler = miscActionSelectHandler(addAction = addAction)
    SelectorList(
        items = actionItems,
        itemSelected = { it ->
            selectHandler(ActionSelectSource.FromClass(it))
        }
    )
}