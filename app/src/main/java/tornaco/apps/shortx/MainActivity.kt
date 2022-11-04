package tornaco.apps.shortx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
                    Greeting()
                }
            }
        }

    }

    @Composable
    fun Greeting() {
        var enabled by remember {
            mutableStateOf(shortXManager.isEdgeGestureEnabled())
        }
        Switch(checked = enabled, onCheckedChange = {
            shortXManager.setEdgeGestureEnabled(!enabled)
            enabled = shortXManager.isEdgeGestureEnabled()
        })
    }
}

