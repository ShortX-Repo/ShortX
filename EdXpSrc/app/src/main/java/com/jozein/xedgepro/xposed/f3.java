package com.jozein.xedgepro.xposed;

import android.app.ActivityManager;
import android.app.TaskInfo;
import android.os.Build;
import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
class f3 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static Field f114a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static Field f115b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static Method f116c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static Field f117d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static Field f118e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static Method f119f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static Field f120g;

    static int a(ActivityManager.RunningTaskInfo runningTaskInfo) {
        if (Build.VERSION.SDK_INT >= 29 && v.f245f) {
            try {
                if (f114a == null) {
                    f114a = XposedHelpers.findField(TaskInfo.class, "displayId");
                }
                int i = f114a.getInt(runningTaskInfo);
                if (i == -1) {
                    return 0;
                }
                return i;
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
        return 0;
    }

    static int b(ActivityManager.RunningTaskInfo runningTaskInfo) {
        if (f120g == null) {
            f120g = XposedHelpers.findField(ActivityManager.RunningTaskInfo.class, "stackId");
        }
        return f120g.getInt(runningTaskInfo);
    }

    static int c(ActivityManager.RunningTaskInfo runningTaskInfo) {
        if (Build.VERSION.SDK_INT >= 29) {
            try {
                if (f115b == null) {
                    f115b = XposedHelpers.findField(TaskInfo.class, "userId");
                }
                return f115b.getInt(runningTaskInfo);
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
        return f.o0.d();
    }

    static int d(ActivityManager.RunningTaskInfo runningTaskInfo) {
        int i;
        try {
            i = Build.VERSION.SDK_INT;
        } catch (Throwable th) {
            f.v.d(th);
        }
        if (i >= 31) {
            if (f116c == null) {
                f116c = XposedHelpers.findMethodExact(TaskInfo.class, "getWindowingMode", new Class[0]);
            }
            return ((Integer) f116c.invoke(runningTaskInfo, new Object[0])).intValue();
        }
        if (i >= 28) {
            if (f117d == null) {
                f117d = XposedHelpers.findField(ActivityManager.RunningTaskInfo.class, "configuration");
            }
            if (f118e == null) {
                f118e = XposedHelpers.findField(f117d.getType(), "windowConfiguration");
            }
            if (f119f == null) {
                f119f = XposedHelpers.findMethodExact(f118e.getType(), "getWindowingMode", new Class[0]);
            }
            return ((Integer) f119f.invoke(f118e.get(f117d.get(runningTaskInfo)), new Object[0])).intValue();
        }
        return 0;
    }

    static boolean e(ActivityManager.RunningTaskInfo runningTaskInfo) {
        try {
            int i = Build.VERSION.SDK_INT;
            return i >= 28 ? d(runningTaskInfo) == 2 : i >= 24 && b(runningTaskInfo) == 4;
        } catch (Throwable th) {
            f.v.c(th.toString());
        }
        return false;
    }

    static boolean f(ActivityManager.RunningTaskInfo runningTaskInfo) {
        try {
            int i = Build.VERSION.SDK_INT;
            if (i < 28) {
                return i >= 24 && b(runningTaskInfo) == 3;
            }
            int iD = d(runningTaskInfo);
            return i >= 33 ? iD == 6 : iD == 3 || iD == 4;
        } catch (Throwable th) {
            f.v.c(th.toString());
        }
        return false;
    }
}
