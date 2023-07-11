package tornaco.apps.shortx.services.os

import android.provider.Settings
import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.launchSafety
import tornaco.apps.shortx.services.runClearCallingIdentity
import tornaco.apps.shortx.services.systemServiceScope


class SystemSettingsService(shortXContext: ShortXContext) :
    SystemService(shortXContext, "SystemSettingsService") {

    fun setPointerLocationEnabled(enable: Boolean) {
        runBlocking {
            systemServiceScope.launchSafety {
                Settings.System.putInt(
                    context.contentResolver,
                    Settings.System.POINTER_LOCATION,
                    if (enable) 1 else 0
                )
            }.join()
        }
    }

    fun isPointerLocationEnabled(): Boolean {
        return runClearCallingIdentity {
            Settings.System.getInt(
                context.contentResolver,
                Settings.System.POINTER_LOCATION,
                0
            ) != 0
        } ?: false
    }
}