package tornaco.apps.shortx.services.xposed.hooks.hook

import de.robv.android.xposed.XposedHelpers
import tornaco.apps.shortx.core.platform.ClassNames.AMS
import tornaco.apps.shortx.core.platform.ClassNames.NOTIFICATION_USAGE_STATS_SERVICE
import tornaco.apps.shortx.core.platform.ClassNames.PROCESS_LIST
import tornaco.apps.shortx.core.platform.ClassNames.PROCESS_RECORD
import util.ReflectionsExt

object Classes {

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.amsClass(): Class<*> {
        return XposedHelpers.findClass(AMS, this)
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

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.printManagerImplClass(): Class<*> =
        XposedHelpers.findClass(
            "com.android.server.print.PrintManagerService\$PrintManagerImpl",
            this
        )

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.appWidgetServiceImplClass(): Class<*> =
        XposedHelpers.findClass(
            "com.android.server.appwidget.AppWidgetServiceImpl",
            this
        )

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.notificationUsageStatsClass(): Class<*> =
        XposedHelpers.findClass(
            NOTIFICATION_USAGE_STATS_SERVICE,
            this
        )

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.activityStackSupervisorClass(methodName: String): Class<*> =
        ReflectionsExt.anyClassFromNames(
            this,
            methodName,
            arrayOf(
                "com.android.server.am.ActivityStackSupervisor",
                "com.android.server.wm.ActivityStackSupervisor",
                // Android S emulator named.
                "com.android.server.wm.ActivityTaskSupervisor"
            )
        )

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.processListClass(): Class<*> =
        XposedHelpers.findClass(
            PROCESS_LIST,
            this
        )

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.processRecordClass(): Class<*> =
        XposedHelpers.findClass(
            PROCESS_RECORD,
            this
        )

}