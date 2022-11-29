package tornaco.apps.shortx.services.xposed.hooks.hook

import de.robv.android.xposed.XposedHelpers

object PackageNames {
    const val android = "android"
}

object Methods {
    const val start = "start"
    const val systemReady = "systemReady"
    const val filterInputEvent = "filterInputEvent"
    const val reportEvent = "reportEvent"
}

object Fields {
    const val mContext = "mContext"
}

object Classes {

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.amsClass(): Class<*> {
        return XposedHelpers.findClass("com.android.server.am.ActivityManagerService", this)
    }

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.activeServicesClass(): Class<*> {
        return XposedHelpers.findClass("com.android.server.am.ActiveServices", this)
    }

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.inputManagerService(): Class<*> {
        return XposedHelpers.findClass("com.android.server.input.InputManagerService", this)
    }

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.phoneWindowManagerClass(): Class<*> =
        XposedHelpers.findClass("com.android.server.policy.PhoneWindowManager", this)

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.windowManagerServiceClass(): Class<*> =
        XposedHelpers.findClass("com.android.server.wm.WindowManagerService", this)

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.usageStatsServiceClass(): Class<*> =
        XposedHelpers.findClass("com.android.server.usage.UsageStatsService", this)


}