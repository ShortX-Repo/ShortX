package tornaco.apps.shortx

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import tornaco.apps.shortx.core.proto.action.ShellCommand
import tornaco.apps.shortx.core.proto.action.TakeScreenshot
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.shortXManager

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ActionTest {
    private val uiDevice by lazy {
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Test
    fun testTakeScreenshot() {
        val evaluateContext = EvaluateContext.newBuilder()
            .setSrcEvent(ContextSrcEvent.ContextSrc_ExecuteOnDAEditor)
            .setSrcTitle("Test")
            .build()
        shortXManager.executeActionBlocking(
            context = evaluateContext,
            action = TakeScreenshot.newBuilder().build().pack_()
        )
    }

    @Test
    fun testShellCommand() = runTest {
        val evaluateContext = EvaluateContext.newBuilder()
            .setSrcEvent(ContextSrcEvent.ContextSrc_ExecuteOnDAEditor)
            .setSrcTitle("Test")
            .build()
        shortXManager.executeActionBlocking(
            context = evaluateContext,
            action = ShellCommand.newBuilder().setCommand("settings put system pointer_location 1")
                .build()
                .pack_()
        )
        Thread.sleep(1000)
        uiDevice.executeShellCommand("settings get system pointer_location")
            .trimShellRes() shouldBe "1"

        shortXManager.executeActionBlocking(
            context = evaluateContext,
            action = ShellCommand.newBuilder().setCommand("settings put system pointer_location 0")
                .build()
                .pack_()
        )
        Thread.sleep(1000)
        uiDevice.executeShellCommand("settings get system pointer_location")
            .trimShellRes() shouldBe "0"
    }
}