package tornaco.apps.shortx.ui.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.common.SensorUsage
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.TipCard
import tornaco.apps.shortx.core.i18n.I18N_R

@Composable
fun SensorUsage(sensorUsage: List<SensorUsage>) {
    val count = sensorUsage.size
    TipCard(modifier = Modifier.padding(horizontal = 8.dp)) {
        ListItem(title = LocalI18N.current[I18N_R.UI_COMMON_SENSOR_TIP_TITLE, mapOf("count" to count.toString())],
            icon = {
                RemixIcon(remixName = Remix.Device.sensor_fill)
            })
    }
}