package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.action.mapper.colorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.base.SelectorList
import tornaco.apps.shortx.ui.addrule.base.SelectorItem
import tornaco.apps.shortx.ui.base.LocalI18N
import kotlin.reflect.full.createInstance


@Composable
fun systemActionSelectHandler(addAction: (Action) -> Unit): (ActionSelectSource) -> Boolean {
    return { source ->
        when (source.clazz) {
            Action.ReadClipboard::class -> {
                addAction(Action.ReadClipboard())
                true
            }

            Action.DisableBT::class,
            Action.DisableDND::class,
            Action.DisableDarkMode::class,
            Action.DisableData::class,
            Action.DisableFlashLight::class,
            Action.DisableHotSpot::class,
            Action.DisableNFC::class,
            Action.DisableWifi::class,

            Action.EnableBT::class,
            Action.EnableDND::class,
            Action.EnableDarkMode::class,
            Action.EnableData::class,
            Action.EnableFlashLight::class,
            Action.EnableHotSpot::class,
            Action.EnableNFC::class,
            Action.EnableWifi::class -> {
                addAction(source.clazz.createInstance())
                true
            }

            else -> false
        }
    }
}

@Composable
fun SystemActions(addAction: (Action) -> Unit) {
    val i18N = LocalI18N.current
    val actionItems = listOf(
        Action.ReadClipboard::class,
        Action.EnableWifi::class,
        Action.DisableWifi::class,
        Action.EnableBT::class,
        Action.DisableBT::class,
        Action.EnableData::class,
        Action.DisableData::class,
        Action.EnableHotSpot::class,
        Action.DisableHotSpot::class,
        Action.EnableNFC::class,
        Action.DisableNFC::class,
        Action.EnableDND::class,
        Action.DisableDND::class,
        Action.EnableFlashLight::class,
        Action.DisableFlashLight::class,
        Action.EnableDarkMode::class,
        Action.DisableDarkMode::class,
    ).map {
        SelectorItem(
            it,
            labelAndDescriptionForActionSelector(i18N, it),
            imageVectorForAction(it),
            iconColor = colorForAction(it)
        )
    }

    val selectHandler = systemActionSelectHandler(addAction = addAction)
    SelectorList(
        items = actionItems,
        itemSelected = { it ->
            selectHandler(ActionSelectSource.FromClass(it))
        }
    )
}