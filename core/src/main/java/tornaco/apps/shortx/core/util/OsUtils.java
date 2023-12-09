package tornaco.apps.shortx.core.util;

import android.os.Build;
import android.os.SystemProperties;
import android.text.TextUtils;

import util.Reflections;

/**
 * Created by guohao4 on 2017/10/24.
 * Email: Tornaco@163.com
 */

public abstract class OsUtils {

    public static boolean isMIUI() {
        return !TextUtils.isEmpty(SystemProperties.get("ro.miui.ui.version.code"));
    }

    public static boolean isColorOS() {
        try {
            Reflections.findClass("android.app.OplusActivityManager", ClassLoader.getSystemClassLoader());
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    public static boolean isLOS() {
        return false;
    }

    public static boolean isMOrAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean isNOrAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    public static boolean isOOrAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    public static boolean isO() {
        return Build.VERSION.SDK_INT == 26 || Build.VERSION.SDK_INT == 27;
    }

    public static boolean isPOrAbove() {
        return Build.VERSION.SDK_INT >= 28;
    }

    // android 9
    public static boolean isP() {
        return Build.VERSION.SDK_INT == 28;
    }

    public static boolean isPOrBelow() {
        return Build.VERSION.SDK_INT <= 28;
    }

    // android 10
    public static boolean isQOrAbove() {
        return Build.VERSION.SDK_INT >= 29;
    }

    public static boolean isQ() {
        return Build.VERSION.SDK_INT == 29;
    }

    // Android 11
    public static boolean isR() {
        return Build.VERSION.SDK_INT == 30;
    }

    // android 11
    public static boolean isROrAbove() {
        return Build.VERSION.SDK_INT >= 30;
    }

    // android 12
    public static boolean isSOrAbove() {
        return Build.VERSION.SDK_INT >= 31;
    }

    // android 13
    public static boolean isTOrAbove() {
        return Build.VERSION.SDK_INT >= 33;
    }

    // android 14
    public static boolean isUOrAbove() {
        return Build.VERSION.SDK_INT >= 34;
    }

}
