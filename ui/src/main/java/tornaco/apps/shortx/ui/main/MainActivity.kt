package tornaco.apps.shortx.ui.main

import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.action.ExpandNotification
import tornaco.apps.shortx.core.action.StartActivity
import tornaco.apps.shortx.core.gesture.Area
import tornaco.apps.shortx.core.gesture.GestureEvent
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.theme.ShortXTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }

    @Composable
    private fun Content() {
        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {

            MainSwitch()
            Area.values().forEach {
                AreaToggle(area = it)
            }
            ExpandNotificationButton()
            StartActivityButton()
            CurrentActions()
        }
    }

    @Composable
    private fun MainSwitch() {
        var enabled by remember {
            mutableStateOf(shortXManager.isGestureEnabled())
        }
        Switch(checked = enabled, onCheckedChange = {
            shortXManager.setGestureEnabled(!enabled)
            enabled = shortXManager.isGestureEnabled()
        })
    }

    @Composable
    private fun AreaToggle(area: Area) {
        var enabled by remember {
            mutableStateOf(shortXManager.isGestureEnabledOnArea(area))
        }

        Row(horizontalArrangement = Arrangement.Center) {
            Text(text = area.name)
            Spacer(modifier = Modifier.size(1.dp))
            Switch(checked = enabled, onCheckedChange = {
                shortXManager.setGestureEnabledOnArea(area, !enabled)
                enabled = shortXManager.isGestureEnabledOnArea(area)
            })
        }
    }

    @Composable
    private fun ExpandNotificationButton() {
        TextButton(onClick = {
            val expandNotification = ExpandNotification()
            shortXManager.setGestureAction(Area.RightCenter,
                GestureEvent.DoubleTap,
                expandNotification)
        }) {
            Text(text = "ExpandNotification-RightCenter")
        }
    }

    @Composable
    private fun StartActivityButton() {
        TextButton(onClick = {
            val startActivity = StartActivity(ComponentName(packageName,
                MainActivity::class.java.name).flattenToString())
            shortXManager.setGestureAction(Area.RightTop,
                GestureEvent.DoubleTap,
                startActivity)
        }) {
            Text(text = "StartActivity-RightTop")
        }
    }

    @Composable
    private fun CurrentActions() {
        Column {
            Text(text = "RightCenter: ${
                shortXManager.getGestureAction(Area.RightCenter, GestureEvent.DoubleTap).toBundle()
            }")

            Text(text = "RightTop: ${
                shortXManager.getGestureAction(Area.RightTop, GestureEvent.DoubleTap).toBundle()
            }")
        }
    }
}

