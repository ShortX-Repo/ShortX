package tornaco.apps.shortx.ui.demo

import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import tornaco.apps.shortx.core.util.MapUtils

@Composable
fun MapDemo() {
    val context = LocalContext.current
    FilledTonalButton(onClick = {
        MapUtils.openGaodeNaviMap(
            context = context,
            appName = "shortx",
            poiname = "poi",
            latitude = "34.34",
            longitude = "108.94"
        )
    }) {
        Text(text = "Gaode Nav")
    }

    FilledTonalButton(onClick = {
        MapUtils.openBaiduNaviMap(context = context, longitude = "108.94", latitude = "34.34")
    }) {
        Text(text = "Baidu Nav")
    }
}