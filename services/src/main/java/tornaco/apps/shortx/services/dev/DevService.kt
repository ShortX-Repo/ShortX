package tornaco.apps.shortx.services.dev

import android.os.UserHandle
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.util.launcherApps
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.launchSafety
import tornaco.apps.shortx.services.systemServiceScope


// For test purpose.
class DevService(shortXContext: ShortXContext) : SystemService(shortXContext, "DevService") {
    override fun systemReady() {
        super.systemReady()

        systemServiceScope.launchSafety {
            delay(20000)
            testGetShortcut()
        }
    }

    private fun testGetShortcut() {
        logger.d("testGetShortcut")
        val widget = WidgetManagerHelper(context)
        logger.d(
            widget.allProviders
                .apply {
                    logger.d("testGetShortcut count: ${this.size}")
                }
                .joinToString(System.lineSeparator()) {
                    "${it.provider} ${it.label}"
                })

        val launcher = context.launcherApps
        launcher.getShortcutConfigActivityList(null, UserHandle.CURRENT).apply {
            logger.d("${this.joinToString()}")
        }
    }
}