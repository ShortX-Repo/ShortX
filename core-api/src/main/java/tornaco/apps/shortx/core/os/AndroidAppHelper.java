package tornaco.apps.shortx.core.os;

import android.app.ActivityThread;
import android.app.Application;
import android.content.pm.ApplicationInfo;

import util.Reflections;

/**
 * Contains various methods for information about the current app.
 *
 * <p>For historical reasons, this class is in the {@code android.app} package. It can't be moved
 * without breaking compatibility with existing modules.
 */
public final class AndroidAppHelper {
    private AndroidAppHelper() {
    }

    static {

    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    /**
     * Returns the name of the current process. It's usually the same as the main package name.
     */
    public static String currentProcessName() {
        String processName = ActivityThread.currentPackageName();
        if (processName == null)
            return "android";
        return processName;
    }

    /**
     * Returns information about the main application in the current process.
     *
     * <p>In a few cases, multiple apps might run in the same process, e.g. the SystemUI and the
     * Keyguard which both have {@code android:process="com.android.systemui"} set in their
     * manifest. In those cases, the first application that was initialized will be returned.
     */
    public static ApplicationInfo currentApplicationInfo() {
        ActivityThread am = ActivityThread.currentActivityThread();
        if (am == null)
            return null;

        Object boundApplication = Reflections.getObjectField(am, "mBoundApplication");
        if (boundApplication == null)
            return null;

        return (ApplicationInfo) Reflections.getObjectField(boundApplication, "appInfo");
    }

    /**
     * Returns the Android package name of the main application in the current process.
     *
     * <p>In a few cases, multiple apps might run in the same process, e.g. the SystemUI and the
     * Keyguard which both have {@code android:process="com.android.systemui"} set in their
     * manifest. In those cases, the first application that was initialized will be returned.
     */
    public static String currentPackageName() {
        ApplicationInfo ai = currentApplicationInfo();
        return (ai != null) ? ai.packageName : "android";
    }

    /**
     * Returns the main {@link android.app.Application} object in the current process.
     *
     * <p>In a few cases, multiple apps might run in the same process, e.g. the SystemUI and the
     * Keyguard which both have {@code android:process="com.android.systemui"} set in their
     * manifest. In those cases, the first application that was initialized will be returned.
     */
    public static Application currentApplication() {
        return ActivityThread.currentApplication();
    }
}