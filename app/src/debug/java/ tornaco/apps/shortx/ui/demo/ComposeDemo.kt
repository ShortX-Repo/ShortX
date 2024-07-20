package  tornaco.apps.shortx.ui.demo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import tornaco.apps.shortx.core.proto.clipboard.ClipboardItem
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.ui.FlashingBorderBox
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.clipbord.ClipboardView
import tornaco.apps.shortx.ui.screencap.RectangleCaptureScreen
import java.util.UUID

class ComposeDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(
                LocalI18N provides i18NIns
            ) {
                val context = LocalContext.current

                FlashingBorderBox {
                    RectangleCaptureScreen(
                        onSelectFullScreen = {
                            Toast
                                .makeText(context, "onSelectFullScreen", Toast.LENGTH_SHORT)
                                .show()
                        },
                        onCancel = {
                            Toast
                                .makeText(context, "Cancelled", Toast.LENGTH_SHORT)
                                .show()
                        },
                        onSelected = {
                            Toast
                                .makeText(context, "Selected Rect: $it", Toast.LENGTH_SHORT)
                                .show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TestClipboardView() {
    ClipboardView(
        context = LocalContext.current,
        i18N = i18NIns,
        clips = listOf(
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
        onDetach = {},
        inputText = {},
        addText = {},
        removeText = {},
        clearAll = {}).ComposeContent()
}