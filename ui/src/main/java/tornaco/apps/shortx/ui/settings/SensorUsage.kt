package tornaco.apps.shortx.ui.settings

import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.proto.common.SensorUsage
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.TipCard

@Composable
fun SensorUsage(sensorUsage: List<SensorUsage>) {
    val count = sensorUsage.size
    TipCard {
        ListItem(title = "$count Sensor is using",
            icon = {
                RemixIcon(remixName = Remix.Device.sensor_fill)
            })
    }
}