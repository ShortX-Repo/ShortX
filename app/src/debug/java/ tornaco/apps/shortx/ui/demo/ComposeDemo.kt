package  tornaco.apps.shortx.ui.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import tornaco.apps.shortx.core.proto.clipboard.ClipboardItem
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.clipbord.ClipboardView
import java.util.UUID

class ComposeDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(
                LocalI18N provides i18NIns
            ) {
                ClipboardView(
                    applicationContext,
                    i18NIns,
                    listOf(
                        ClipboardItem.newBuilder().setId(UUID.randomUUID().toString()).setValue(
                            "Set privacy boundaries\n" +
                                    "\n" +
                                    "Take more control of your mobile privacy.\n" +
                                    "Seamlessly manage how your data is shared and used. Access all your privacy settings in one place and easily change permissions whenever you feel like it."
                        ).build(),
                        ClipboardItem.newBuilder().setId(UUID.randomUUID().toString())
                            .setValue("ur privacy settings in on").build(),
                        ClipboardItem.newBuilder().setId(UUID.randomUUID().toString())
                            .setValue("ta is shared and used. Access ").build(),
                        ClipboardItem.newBuilder().setId(UUID.randomUUID().toString())
                            .setValue("112233").build(),
                        ClipboardItem.newBuilder().setId(UUID.randomUUID().toString()).setValue(
                            "Thanks for visiting.\n" +
                                    "We'd like to ask you a few questions about your experience to help us improve our website. It should take about a minute."
                        ).build(),
                    ),
                    {},
                    {},
                    {},
                    {}).ComposeContent()
            }
        }
    }
}


