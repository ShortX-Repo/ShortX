package tornaco.apps.shortx.services.xposed.hooks.hook

import android.app.Application
import android.content.pm.ApplicationInfo
import tornaco.apps.shortx.core.annotations.DoNotStrip
import tornaco.apps.shortx.core.os.AndroidAppHelper

@DoNotStrip
class InjectedAndroidAppHelper {
    val currentProcessName: String?
        get() = AndroidAppHelper.currentProcessName()

    val currentPackageName: String?
        get() = AndroidAppHelper.currentPackageName()

    val currentApplication: Application?
        get() = AndroidAppHelper.currentApplication()

    val currentApplicationInfo: ApplicationInfo?
        get() = AndroidAppHelper.currentApplicationInfo()
}
