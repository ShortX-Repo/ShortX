package com.jozein.xedgepro.xposed;

import android.app.ActivityManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import de.robv.android.xposed.XposedHelpers;

/* JADX INFO: loaded from: classes.dex */
class g3 extends p2 {
    private static Class<?> E;
    private static Class<?> F;
    private static final String[] G = {"activity", "android.app.IActivityManager", "activity_task", "android.app.IActivityTaskManager", "package", "android.content.pm.IPackageManager", "window", "android.view.IWindowManager", "input", "android.hardware.input.IInputManager", "input_method", "com.android.internal.view.IInputMethodManager", "accessibility", "android.view.accessibility.IAccessibilityManager", "statusbar", "com.android.internal.statusbar.IStatusBarService"};

    static Object h() {
        return n("accessibility");
    }

    static Object i() {
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                Object objCallStaticMethod = XposedHelpers.callStaticMethod(ActivityManager.class, "getService", new Object[0]);
                if (objCallStaticMethod != null) {
                    return objCallStaticMethod;
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
        return n("activity");
    }

    static Object j() {
        if (Build.VERSION.SDK_INT < 29) {
            throw new UnsupportedOperationException();
        }
        try {
            Object objCallStaticMethod = XposedHelpers.callStaticMethod(ActivityManager.class, "getTaskService", new Object[0]);
            if (objCallStaticMethod != null) {
                return objCallStaticMethod;
            }
        } catch (Throwable th) {
            f.v.d(th);
        }
        return n("activity");
    }

    static Object k() {
        try {
            Object objCallStaticMethod = XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.AppGlobals", PackageManager.class.getClassLoader()), "getPackageManager", new Object[0]);
            if (objCallStaticMethod != null) {
                return objCallStaticMethod;
            }
        } catch (Throwable th) {
            f.v.d(th);
        }
        return n("package");
    }

    static Object l() {
        return n("input");
    }

    static Object m() {
        return n("input_method");
    }

    private static Object n(String str) {
        int i = 0;
        while (true) {
            String[] strArr = G;
            if (i >= strArr.length) {
                throw new IllegalArgumentException("Interface name not added for: " + str);
            }
            if (strArr[i].equals(str)) {
                return o(strArr[i + 1], str);
            }
            i += 2;
        }
    }

    static Object o(String str, String str2) {
        IBinder iBinderR = r(str2);
        if (XposedHelpers.findClass(str, p2.c()).isInstance(iBinderR)) {
            return iBinderR;
        }
        return XposedHelpers.callStaticMethod(XposedHelpers.findClass(str + ".Stub", p2.c()), "asInterface", new Object[]{iBinderR});
    }

    static Object p(Class<?> cls) {
        if (E == null) {
            E = XposedHelpers.findClass("com.android.server.LocalServices", cls.getClassLoader());
        }
        return XposedHelpers.callStaticMethod(E, "getService", new Object[]{cls});
    }

    static Object q(String str) {
        return p(XposedHelpers.findClass(str, p2.c()));
    }

    static IBinder r(String str) {
        if (F == null) {
            F = XposedHelpers.findClass("android.os.ServiceManager", p2.c());
        }
        return (IBinder) XposedHelpers.callStaticMethod(F, "getService", new Object[]{str});
    }

    static Object s() {
        return n("statusbar");
    }

    static Object t(String str) {
        return XposedHelpers.getSurroundingThis(q(str));
    }

    static Object u() {
        int i = Build.VERSION.SDK_INT;
        if (i >= 21) {
            try {
                Object objT = t(i >= 28 ? "com.android.server.wm.WindowManagerInternal" : "android.view.WindowManagerInternal");
                if (objT != null) {
                    return objT;
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
            try {
                Object objCallStaticMethod = XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.view.WindowManagerGlobal", p2.c()), "getWindowManagerService", new Object[0]);
                if (objCallStaticMethod != null) {
                    return objCallStaticMethod;
                }
            } catch (Throwable th2) {
                f.v.d(th2);
            }
        }
        return n("window");
    }
}
