package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.action.mapper.colorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.base.SelectorList
import tornaco.apps.shortx.ui.addrule.base.SelectorItem
import tornaco.apps.shortx.ui.base.LocalI18N


@Composable
fun lifeActionSelectHandler(addAction: (Action) -> Unit): (ActionSelectSource) -> Boolean {
    val mapNavDialogState = rememberMapNavDialogState(onSelected = {
        addAction(it)
    })
    MapNavDialog(state = mapNavDialogState)

    return { source ->
        when (source.clazz) {
            Action.MapNav::class -> {
                mapNavDialogState.show()
                true
            }

            else -> {
                false
            }
        }
    }
}

@Composable
fun LifeActions(addAction: (Action) -> Unit) {
    val i18N = LocalI18N.current


    val actionItems = listOf(
        SelectorItem(
            type = Action.MapNav::class,
            labelAndDescription = labelAndDescriptionForActionSelector(i18N, Action.MapNav::class),
            icon = imageVectorForAction(Action.MapNav::class),
            iconColor = colorForAction(Action.MapNav::class)
        ),
    )

    val selectHandler = lifeActionSelectHandler(addAction = addAction)
    SelectorList(
        actionItems = actionItems,
        itemSelected = { it ->
            selectHandler(ActionSelectSource.FromClass(it))
        }
    )
}