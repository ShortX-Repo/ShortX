package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.action.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.LocalI18N
import kotlin.reflect.KClass


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
        Item(
            Action.MapNav::class,
            labelAndDescriptionForActionSelector(i18N, Action.MapNav::class),
            imageVectorForAction(Action.MapNav::class)
        ),
    )

    val selectHandler = lifeActionSelectHandler(addAction = addAction)
    ActionList(
        actionItems = actionItems,
        itemSelected = { it ->
            selectHandler(ActionSelectSource.FromClass(it))
        }
    )
}