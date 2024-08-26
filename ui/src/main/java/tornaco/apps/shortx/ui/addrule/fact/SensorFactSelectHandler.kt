package tornaco.apps.shortx.ui.addrule.fact

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import tornaco.apps.shortx.core.proto.fact.SensorValueTrend
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.LocalI18N

@Composable
fun sensorFactSelectHandler(addFact: (Fact) -> Unit): (FactSelectSource) -> Boolean {
    val context = LocalContext.current
    val i18N = LocalI18N.current
    return { source ->
        when (source.clazz) {
            Fact.LightSensor::class -> {
                addFact(
                    Fact.LightSensor(
                        constraints = emptyList(),
                        trend = SensorValueTrend.SensorValueTrend_Any
                    )
                )
                true
            }

            else -> false
        }
    }
}
