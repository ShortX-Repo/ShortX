package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.action.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.LocalI18N
import kotlin.reflect.full.createInstance

@Composable
fun SettingsActions(addAction: (Action) -> Unit) {
    val i18N = LocalI18N.current
    val actionItems = listOf(
        Item(
            Action.EnableWifi::class,
            labelAndDescriptionForActionSelector(i18N, Action.EnableWifi::class),
            imageVectorForAction(Action.EnableWifi::class)
        ),

        Item(
            Action.DisableWifi::class,
            labelAndDescriptionForActionSelector(i18N, Action.DisableWifi::class),
            imageVectorForAction(Action.DisableWifi::class)
        ),

        Item(
            Action.EnableBT::class,
            labelAndDescriptionForActionSelector(i18N, Action.EnableBT::class),
            imageVectorForAction(Action.EnableBT::class)
        ),

        Item(
            Action.DisableBT::class,
            labelAndDescriptionForActionSelector(i18N, Action.DisableBT::class),
            imageVectorForAction(Action.DisableBT::class)
        ),


        Item(
            Action.EnableData::class,
            labelAndDescriptionForActionSelector(i18N, Action.EnableData::class),
            imageVectorForAction(Action.EnableData::class)
        ),

        Item(
            Action.DisableData::class,
            labelAndDescriptionForActionSelector(i18N, Action.DisableData::class),
            imageVectorForAction(Action.DisableData::class)
        ),

        Item(
            Action.EnableHotSpot::class,
            labelAndDescriptionForActionSelector(i18N, Action.EnableHotSpot::class),
            imageVectorForAction(Action.EnableHotSpot::class)
        ),

        Item(
            Action.DisableHotSpot::class,
            labelAndDescriptionForActionSelector(i18N, Action.DisableHotSpot::class),
            imageVectorForAction(Action.DisableHotSpot::class)
        ),

        Item(
            Action.EnableNFC::class,
            labelAndDescriptionForActionSelector(i18N, Action.EnableNFC::class),
            imageVectorForAction(Action.EnableNFC::class)
        ),

        Item(
            Action.DisableNFC::class,
            labelAndDescriptionForActionSelector(i18N, Action.DisableNFC::class),
            imageVectorForAction(Action.DisableNFC::class)
        ),

        Item(
            Action.EnableDND::class,
            labelAndDescriptionForActionSelector(i18N, Action.EnableDND::class),
            imageVectorForAction(Action.EnableDND::class)
        ),

        Item(
            Action.DisableDND::class,
            labelAndDescriptionForActionSelector(i18N, Action.DisableDND::class),
            imageVectorForAction(Action.DisableDND::class)
        ),

        Item(
            Action.EnableFlashLight::class,
            labelAndDescriptionForActionSelector(i18N, Action.EnableFlashLight::class),
            imageVectorForAction(Action.EnableFlashLight::class)
        ),

        Item(
            Action.DisableFlashLight::class,
            labelAndDescriptionForActionSelector(i18N, Action.DisableFlashLight::class),
            imageVectorForAction(Action.DisableFlashLight::class)
        ),

        Item(
            Action.EnableDarkMode::class,
            labelAndDescriptionForActionSelector(i18N, Action.EnableDarkMode::class),
            imageVectorForAction(Action.EnableDarkMode::class)
        ),

        Item(
            Action.DisableDarkMode::class,
            labelAndDescriptionForActionSelector(i18N, Action.DisableDarkMode::class),
            imageVectorForAction(Action.DisableDarkMode::class)
        )
    )


    ActionList(
        actionItems = actionItems,
        itemSelected = { actionKClass ->
            when (actionKClass) {
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
                    addAction(actionKClass.createInstance())
                }
            }
        }
    )
}