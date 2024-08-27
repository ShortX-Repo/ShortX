package tornaco.apps.shortx.ui.addrule.fact

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.selector.LightSensorSelectorActivity
import tornaco.apps.shortx.ui.addrule.fact.selector.ProximitySensorSelectorActivity
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.popupSelectorResult
import tornaco.apps.shortx.ui.base.LocalI18N

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

    val proximitySensorSensorLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val fact = result.popupSelectorResult<Fact.ProximitySensor>()
            fact?.let { addFact(it) }
        }
    )

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

            else -> false
        }
    }
}
