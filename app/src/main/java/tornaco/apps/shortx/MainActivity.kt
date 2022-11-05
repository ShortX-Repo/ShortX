package tornaco.apps.shortx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.gesture.Area
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.theme.ShortXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShortXTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background) {
                    Content()
                }
            }
        }
    }

    @Composable
    private fun Content() {
        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {

            MainEdgeSwitch()
            Area.values().forEach {
                EdgeAreaToggle(area = it)
            }
        }
    }

    @Composable
    private fun MainEdgeSwitch() {
        var enabled by remember {
            mutableStateOf(shortXManager.isEdgeGestureEnabled)
        }
        Switch(checked = enabled, onCheckedChange = {
            shortXManager.isEdgeGestureEnabled = !enabled
            enabled = shortXManager.isEdgeGestureEnabled
        })
    }

    @Composable
    private fun EdgeAreaToggle(area: Area) {
        var enabled by remember {
            mutableStateOf(shortXManager.isEdgeGestureEnabledOnArea(area.name))
        }

        Row(horizontalArrangement = Arrangement.Center) {
            Text(text = area.name)
            Spacer(modifier = Modifier.size(1.dp))
            Switch(checked = enabled, onCheckedChange = {
                shortXManager.setEdgeGestureEnabledOnArea(area.name, !enabled)
                enabled = shortXManager.isEdgeGestureEnabledOnArea(area.name)
            })
        }
    }
}

