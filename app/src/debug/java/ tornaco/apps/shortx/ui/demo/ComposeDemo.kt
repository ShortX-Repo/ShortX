@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package  tornaco.apps.shortx.ui.demo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.FloatingToolbarExitDirection.Companion.Bottom
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalFloatingToolbar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.clipboard.ClipboardItem
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.clipbord.ClipboardView
import java.util.UUID

class ComposeDemoActivity : BaseActivity() {
    @Composable
    override fun Content() {
        TestClipboardView()
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
        inputText = { _, _ -> },
        addText = {},
        removeText = {},
        pinItem = { _, _ -> },
        clearAll = {}).ComposeContent()
}

@Composable
fun MD3ExpToolbarDemo() {
    val exitAlwaysScrollBehavior =
        FloatingToolbarDefaults.exitAlwaysScrollBehavior(exitDirection = Bottom)
    val vibrantColors = FloatingToolbarDefaults.vibrantFloatingToolbarColors()
    Scaffold(modifier = Modifier.nestedScroll(exitAlwaysScrollBehavior)) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(text = remember { LoremIpsum().values.first() })
            }
            VerticalFloatingToolbar(
                // Always expanded as the toolbar is bottom-centered. We will use a
                // FloatingToolbarScrollBehavior to hide both the toolbar and its FAB on scroll.
                expanded = true,
                floatingActionButton = {
                    // Match the FAB to the vibrantColors. See also StandardFloatingActionButton.
                    FloatingToolbarDefaults.VibrantFloatingActionButton(
                        onClick = { /* doSomething() */ },
                    ) {
                        RemixIcon(Remix.System.add_line)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = -ScreenOffset),
                colors = vibrantColors,
                scrollBehavior = exitAlwaysScrollBehavior,
                content = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        RemixIcon(Remix.System.check_line)
                    }
                    IconButton(onClick = { /* doSomething() */ }) {
                        RemixIcon(Remix.System.check_line)
                    }
                    IconButton(onClick = { /* doSomething() */ }) {
                        RemixIcon(Remix.System.check_line)
                    }
                    IconButton(onClick = { /* doSomething() */ }) {
                        RemixIcon(Remix.System.check_line)
                    }
                },
            )
        }
    }
}