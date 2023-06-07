package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.ui.addrule.action.mapper.colorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.base.SelectorList
import tornaco.apps.shortx.ui.addrule.base.SelectorItem
import tornaco.apps.shortx.ui.base.LocalI18N
import kotlin.reflect.KClass

@Composable
fun RecentActions(addAction: (Action) -> Unit) {
    val selectorVM = hiltViewModel<ActionSelectorViewModel>()
    val actions by selectorVM.state.collectAsState()

    val i18N = LocalI18N.current
    val actionItems = actions.recentActions.map {
        SelectorItem(
            type = it,
            labelAndDescription = labelAndDescriptionForActionSelector(i18N, it),
            icon = imageVectorForAction(it),
            iconColor = colorForAction(it)
        )
    }

    val selectHandlers = listOf(
        uiActionSelectHandler(addAction = addAction),
        appActionSelectHandler(addAction = addAction),
        systemActionSelectHandler(addAction = addAction),
        lifeActionSelectHandler(addAction = addAction),
        extActionSelectHandler(addAction = addAction),
    )

    val itemSelected: (KClass<out Action>) -> Unit = { it ->
        for (h in selectHandlers) {
            if (h(ActionSelectSource.FromClass(it))) break
        }
    }
    SelectorList(
        actionItems = actionItems,
        itemSelected = itemSelected
    )
}