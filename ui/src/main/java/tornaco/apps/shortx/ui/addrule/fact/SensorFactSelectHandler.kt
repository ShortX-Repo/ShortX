package tornaco.apps.shortx.ui.addrule.fact

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import tornaco.apps.shortx.core.proto.fact.ProximitySensorState
import tornaco.apps.shortx.ui.addrule.fact.mapper.displayNameKey
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.selector.AccelerometerSensorSelectorActivity
import tornaco.apps.shortx.ui.addrule.fact.selector.LightSensorSelectorActivity
import tornaco.apps.shortx.ui.addrule.fact.selector.ProximitySensorSelectorActivity
import tornaco.apps.shortx.ui.addrule.fact.selector.SoundSelectorActivity
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.popupSelectorResult
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.core.i18n.I18N_R

@Composable
fun sensorFactSelectHandler(addFact: (Fact) -> Unit): (FactSelectSource) -> Boolean {
    val context = LocalContext.current
    val i18N = LocalI18N.current

    val lightSensorLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val fact = result.popupSelectorResult<Fact.LightSensor>()
            fact?.let { addFact(it) }
        }
    )

    val soundLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val fact = result.popupSelectorResult<Fact.Sound>()
            fact?.let { addFact(it) }
        }
    )

    val proximitySensorSensorLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val fact = result.popupSelectorResult<Fact.ProximitySensor>()
            fact?.let { addFact(it) }
        }
    )

    val accSensorSensorLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val fact = result.popupSelectorResult<Fact.AccelerometerSensor>()
            fact?.let { addFact(it) }
        }
    )

    val proximitySensorStateMenu = rememberMenuDialogState<String>(
        title = i18N[I18N_R.UI_FACT_SENSOR_PROXIMITY_STATE],
        menuItems = ProximitySensorState.entries.filterNot { it == ProximitySensorState.UNRECOGNIZED }
            .map {
                MenuDialogItem(
                    id = it.name,
                    title = i18N[it.displayNameKey()]
                )
            },
        onItemSelected = { _, id ->
            val state = ProximitySensorState.valueOf(id)
            addFact(Fact.ProximitySensorStateChanged(state = state))
        }
    )
    MenuDialog(state = proximitySensorStateMenu)

    return { source ->
        when (source.clazz) {
            Fact.LightSensor::class -> {
                lightSensorLauncher.launch(
                    BaseSelectorActivity.newIntent(
                        context,
                        source.getFactAs<Fact.LightSensor>(),
                        LightSensorSelectorActivity::class.java
                    )
                )
                true
            }

            Fact.Sound::class -> {
                soundLauncher.launch(
                    BaseSelectorActivity.newIntent(
                        context,
                        source.getFactAs<Fact.Sound>(),
                        SoundSelectorActivity::class.java
                    )
                )
                true
            }

            Fact.ProximitySensor::class -> {
                proximitySensorSensorLauncher.launch(
                    BaseSelectorActivity.newIntent(
                        context,
                        source.getFactAs<Fact.ProximitySensor>(),
                        ProximitySensorSelectorActivity::class.java
                    )
                )
                true
            }

            Fact.AccelerometerSensor::class -> {
                accSensorSensorLauncher.launch(
                    BaseSelectorActivity.newIntent(
                        context,
                        source.getFactAs<Fact.AccelerometerSensor>(),
                        AccelerometerSensorSelectorActivity::class.java
                    )
                )
                true
            }

            Fact.ProximitySensorStateChanged::class -> {
                proximitySensorStateMenu.show(currentSelection = listOfNotNull(source.getFactAs<Fact.ProximitySensorStateChanged>()?.state?.name))
                true
            }

            Fact.ShakeDevice::class -> {
                addFact(Fact.ShakeDevice())
                true
            }

            else -> false
        }
    }
}
