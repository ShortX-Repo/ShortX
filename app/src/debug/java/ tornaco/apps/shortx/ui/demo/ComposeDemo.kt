package  tornaco.apps.shortx.ui.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.clipbord.ClipboardView

class ComposeDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(
                LocalI18N provides i18NIns
            ) {
                ClipboardView(applicationContext, i18NIns, {}, {}, {}).ComposeContent()
            }
        }
    }
}


