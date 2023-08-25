package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import tornaco.apps.shortx.core.proto.action.MapApp
import tornaco.apps.shortx.ui.addrule.action.mapper.colorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.base.SelectorItem
import tornaco.apps.shortx.ui.addrule.base.SelectorList
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.subscribe.isForSubscribedOnly


@Composable
fun lifeActionSelectHandler(addAction: (Action) -> Unit): (ActionSelectSource) -> Boolean {
    val mapNavDialogState = rememberMapNavDialogState(onSelected = {
        addAction(it)
    })
    MapNavDialog(state = mapNavDialogState)

    return { source ->
        when (source.clazz) {
            Action.MapNav::class -> {
                mapNavDialogState.show(source.getActionAs())
                true
            }

            Action.MapQueryBus::class -> {
                addAction(Action.MapQueryBus(city = "", bus = "251", mapApp = MapApp.Gaode))
                true
            }

            else -> {
                false
            }
        }
    }
}

@Composable
fun LifeActions(
    keyword: String,
    subscribeState: LVLStateHolder.State,
    addAction: (Action) -> Unit
) {
    val i18N = LocalI18N.current

    val actionItems = listOf(
        Action.MapNav::class,
        Action.MapQueryBus::class,
    ).map {
        SelectorItem(
            type = it,
            labelAndDescription = labelAndDescriptionForActionSelector(i18N, it),
            icon = imageVectorForAction(it),
            isForSubscribedOnly = remember(subscribeState) {
                !subscribeState.isSubscribed && isForSubscribedOnly(it)
            },
            iconColor = colorForAction(it)
        )
    }
        .filter {
            keyword.isBlank() || it.labelAndDescription.first.contains(keyword, ignoreCase = true)
        }

    if (actionItems.isNotEmpty()) {
        ActionCategoryTitle(ActionCategory.Life)
    }

    val selectHandler = lifeActionSelectHandler(addAction = addAction)
    SelectorList(
        items = actionItems,
        itemSelected = { it ->
            selectHandler(ActionSelectSource.FromClass(it))
        }
    )
}