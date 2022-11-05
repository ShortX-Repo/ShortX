package tornaco.apps.shortx.actor

import android.content.Context
import android.os.Handler
import android.view.WindowManager.ScreenshotSource.SCREENSHOT_ACCESSIBILITY_ACTIONS
import com.android.internal.util.ScreenshotHelper

class TakeScreenShot(private val context: Context, private val handler: Handler) {

    operator fun invoke() {
        ScreenshotHelper(context).run {
            takeScreenshot(android.view.WindowManager.TAKE_SCREENSHOT_FULLSCREEN,
                true,
                true,
                SCREENSHOT_ACCESSIBILITY_ACTIONS,
                handler,
                null)
        }
    }
}