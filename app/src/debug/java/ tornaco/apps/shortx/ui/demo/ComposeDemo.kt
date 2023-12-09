package  tornaco.apps.shortx.ui.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.ui.clipbord.ClipboardView

class ComposeDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClipboardView(applicationContext, i18NIns) {}.ComposeContent()
        }
    }
}


