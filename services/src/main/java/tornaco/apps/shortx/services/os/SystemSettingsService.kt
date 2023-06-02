package tornaco.apps.shortx.services.os

import android.provider.Settings
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.postSafety
import tornaco.apps.shortx.services.runClearCallingIdentity


class SystemSettingsService(shortXContext: ShortXContext) : SystemService(shortXContext) {

    fun setPointerLocationEnabled(enable: Boolean) {
        handler.postSafety {
            Settings.System.putInt(
                context.contentResolver,
                Settings.System.POINTER_LOCATION,
                if (enable) 1 else 0
            )
        }
    }

    fun isPointerLocationEnabled(): Boolean {
        return runClearCallingIdentity {
            Settings.System.getInt(
                context.getContentResolver(),
                Settings.System.POINTER_LOCATION,
                0
            ) != 0
        } ?: false
    }
}