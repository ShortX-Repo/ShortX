package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.ui.addrule.action.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.LocalI18N
import kotlin.reflect.KClass

@Composable
fun RecentActions(addAction: (Action) -> Unit) {
    val selectorVM = hiltViewModel<ActionSelectorViewModel>()
    val actions by selectorVM.state.collectAsState()

    val i18N = LocalI18N.current
    val actionItems = actions.recentActions.map {
        Item(
            type = it,
            labelAndDescription = labelAndDescriptionForActionSelector(i18N, it),
            icon = imageVectorForAction(it)
        )
    }

    val selectHandlers = listOf(
        uiActionSelectHandler(addAction = addAction),
        appActionSelectHandler(addAction = addAction),
        settingsActionSelectHandler(addAction = addAction),
        lifeActionSelectHandler(addAction = addAction),
        extActionSelectHandler(addAction = addAction),
    )

    val itemSelected: (KClass<out Action>) -> Unit = { it ->
        for (h in selectHandlers) {
            if (h(it)) break
        }
    }
    ActionList(
        actionItems = actionItems,
        itemSelected = itemSelected
    )
}