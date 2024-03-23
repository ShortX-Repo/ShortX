package tornaco.apps.shortx.core.platform

import tornaco.apps.shortx.core.platform.ClassNames.ACTIVITY_RECORD_AM
import tornaco.apps.shortx.core.platform.ClassNames.ACTIVITY_RECORD_WM
import tornaco.apps.shortx.core.platform.ClassNames.AMS
import tornaco.apps.shortx.core.platform.ClassNames.ATMS
import tornaco.apps.shortx.core.platform.ClassNames.CLIPBOARD_SERVICE
import tornaco.apps.shortx.core.platform.ClassNames.MEDIA_DOCUMENTS_PROVIDER
import tornaco.apps.shortx.core.platform.ClassNames.NFC_SERVICE_HANDLER
import tornaco.apps.shortx.core.platform.ClassNames.NOTIFICATION_USAGE_STATS_SERVICE
import tornaco.apps.shortx.core.platform.ClassNames.PHONE_WINDOW_MANAGER
import tornaco.apps.shortx.core.platform.ClassNames.PROCESS_LIST
import tornaco.apps.shortx.core.platform.ClassNames.PROCESS_RECORD
import tornaco.apps.shortx.core.platform.ClassNames.VPN
import util.Reflections
import util.ReflectionsExt
import java.lang.reflect.Method

object Classes {

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.amsClass(): Class<*> {
        return Reflections.findClass(AMS, this)
    }

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.amsShellCommandClass(): Class<*> {
        return Reflections.findClass("com.android.server.am.ActivityManagerShellCommand", this)
    }

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.atmsClass(): Class<*> {
        return Reflections.findClass(ATMS, this)
    }

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.activeServicesClass(): Class<*> {
        return Reflections.findClass("com.android.server.am.ActiveServices", this)
    }

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.activityStarterClass(): Class<*> {
        return Reflections.findClass("com.android.server.wm.ActivityStarter", this)
    }

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.activityMetricsLoggerClass(): Class<*> {
        return Reflections.findClass("com.android.server.wm.ActivityMetricsLogger", this)
    }

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.inputManagerService(): Class<*> {
        return Reflections.findClass("com.android.server.input.InputManagerService", this)
    }

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.imeService(): Class<*> {
        return Reflections.findClass(
            "com.android.server.inputmethod.InputMethodManagerService",
            this
        )
    }

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.accManager(): Class<*> {
        return Reflections.findClass(
            "com.android.server.accessibility.UiAutomationManager",
            this
        )
    }

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.accProxyManager(): Class<*> {
        return Reflections.findClass(
            "com.android.server.accessibility.AccessibilityUserState",
            this
        )
    }

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.windowManagerServiceClass(): Class<*> =
        Reflections.findClass("com.android.server.wm.WindowManagerService", this)

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.usageStatsServiceClass(): Class<*> =
        Reflections.findClass("com.android.server.usage.UsageStatsService", this)

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.printManagerImplClass(): Class<*> =
        Reflections.findClass(
            "com.android.server.print.PrintManagerService\$PrintManagerImpl",
            this
        )

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.appWidgetServiceImplClass(): Class<*> =
        Reflections.findClass(
            "com.android.server.appwidget.AppWidgetServiceImpl",
            this
        )

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.notificationUsageStatsClass(): Class<*> =
        Reflections.findClass(
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
        Reflections.findClass(
            PROCESS_LIST,
            this
        )

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.processRecordClass(): Class<*> =
        Reflections.findClass(
            PROCESS_RECORD,
            this
        )

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.activityRecordClass(): Class<*> =
        ReflectionsExt.anyClassFromNames(
            this, arrayOf(
                ACTIVITY_RECORD_WM,
                ACTIVITY_RECORD_AM
            )
        )

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.vpnClass(): Class<*> =
        Reflections.findClass(
            VPN,
            this
        )

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.mediaDocumentProviderClass(): Class<*> =
        Reflections.findClass(
            MEDIA_DOCUMENTS_PROVIDER,
            this
        )

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.nfcServiceHandlerClass(): Class<*> =
        Reflections.findClass(
            NFC_SERVICE_HANDLER,
            this
        )

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.phoneWindowManagerClass(): Class<*> =
        Reflections.findClass(
            PHONE_WINDOW_MANAGER,
            this
        )

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.clipboardServiceClass(): Class<*> =
        Reflections.findClass(
            CLIPBOARD_SERVICE,
            this
        )

    @Throws(ClassNotFoundException::class)
    fun ClassLoader.clipboardServiceImplClass(): Class<*> =
        Reflections.findClass(
            "$CLIPBOARD_SERVICE\$ClipboardImpl",
            this
        )


    fun Class<*>.activityStoppedMethod(): Method? {
        return declaredMethods.firstOrNull { it.name == Methods.activityStopped || it.name == Methods.activityStoppedLocked }
    }
}