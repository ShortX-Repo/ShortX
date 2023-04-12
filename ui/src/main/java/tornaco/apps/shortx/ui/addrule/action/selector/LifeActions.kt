package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.action.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.LocalI18N

@Composable
fun LifeActions(addAction: (Action) -> Unit) {
    val i18N = LocalI18N.current

    val mapNavDialogState = rememberMapNavDialogState(onSelected = {
        addAction(it)
    })
    MapNavDialog(state = mapNavDialogState)


    val actionItems = listOf(
        Item(
            Action.MapNav::class,
            labelAndDescriptionForActionSelector(i18N, Action.MapNav::class),
            imageVectorForAction(Action.MapNav::class)
        ),
    )

    ActionList(
        actionItems = actionItems,
        itemSelected = { it ->
            when (it) {
                Action.MapNav::class -> {
                    mapNavDialogState.show()
                }
            }
        })
}