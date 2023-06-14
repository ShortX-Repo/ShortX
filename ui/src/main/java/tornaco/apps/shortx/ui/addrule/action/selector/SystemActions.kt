package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.proto.action.RingerMode
import tornaco.apps.shortx.ui.addrule.action.mapper.colorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.mapper.labelKey
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.base.SelectorItem
import tornaco.apps.shortx.ui.addrule.base.SelectorList
import tornaco.apps.shortx.ui.addrule.fact.selector.BrightnessValueSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberBrightnessValueSelectorState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.SelectionDialog
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.rememberSelectionDialogState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import kotlin.reflect.full.createInstance


@Composable
fun systemActionSelectHandler(addAction: (Action) -> Unit): (ActionSelectSource) -> Boolean {
    val i18N = LocalI18N.current
    val ringerModeSelectDialogState = rememberSelectionDialogState(
        title = i18N["ui.action.set.ringer.mode"],
        message = null,
        selectionLabel2Ids = listOf(RingerMode.normal, RingerMode.vibrate, RingerMode.silent).map {
            i18N[it.labelKey()] to it.name
        },
        onSelect = {
            val mode: RingerMode = RingerMode.valueOf(it)
            addAction(Action.SetRingerMode(mode = mode))
        }
    )
    SelectionDialog(state = ringerModeSelectDialogState)


    val setBrightnessState = rememberBrightnessValueSelectorState {
        addAction(Action.SetBrightness(value = it))
    }
    BrightnessValueSelector(state = setBrightnessState)

    val writeClipboardSelectorDialogState = rememberTextInputState(
        title = LocalI18N.current["ui.action.write.clipboard"],
        message = null,
        inputValidator = { true to "" },
        onSelected = {
            addAction(Action.WriteClipboard(text = it))
        })
    TextInputDialog(state = writeClipboardSelectorDialogState)

    return { source ->
        when (source.clazz) {
            Action.ReadClipboard::class -> {
                addAction(Action.ReadClipboard())
                true
            }

            Action.WriteClipboard::class -> {
                writeClipboardSelectorDialogState.show(source.getActionAs<Action.WriteClipboard>()?.text)
                true
            }

            Action.SetRingerMode::class -> {
                ringerModeSelectDialogState.show()
                true
            }

            Action.SetBrightness::class -> {
                setBrightnessState.show(100)
                true
            }

            Action.DisableBT::class,
            Action.DisableLocation::class,
            Action.DisableDND::class,
            Action.DisableDarkMode::class,
            Action.DisableData::class,
            Action.DisableFlashLight::class,
            Action.DisableHotSpot::class,
            Action.DisableNFC::class,
            Action.DisableWifi::class,
            Action.DisableAutoBrightness::class,

            Action.EnableBT::class,
            Action.EnableLocation::class,
            Action.EnableDND::class,
            Action.EnableDarkMode::class,
            Action.EnableData::class,
            Action.EnableFlashLight::class,
            Action.EnableHotSpot::class,
            Action.EnableNFC::class,
            Action.EnableAutoBrightness::class,
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
        Action.SetRingerMode::class,
        Action.ReadClipboard::class,
        Action.WriteClipboard::class,
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
        Action.EnableAutoBrightness::class,
        Action.DisableAutoBrightness::class,
        Action.SetBrightness::class,
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