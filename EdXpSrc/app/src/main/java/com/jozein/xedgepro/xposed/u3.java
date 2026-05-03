package com.jozein.xedgepro.xposed;

import android.graphics.Rect;
import android.graphics.Region;
import android.os.Build;
import android.os.IBinder;
import android.view.WindowManager;
import de.robv.android.xposed.XposedHelpers;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
class u3 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static Field f238a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static Field f239b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static Field f240c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static Method f241d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static Method f242e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static Method f243f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static Method f244g;
    private static Method h;
    private static Method i;
    private static Method j;
    private static Class<?> k;

    static Object a(Object obj) {
        IBinder iBinder;
        try {
            if (Build.VERSION.SDK_INT >= 30) {
                if (f239b == null) {
                    f239b = XposedHelpers.findField(f(obj), "mActivityRecord");
                }
                return f239b.get(obj);
            }
            WindowManager.LayoutParams layoutParamsC = c(obj);
            if (layoutParamsC == null || (iBinder = layoutParamsC.token) == null || !iBinder.getClass().getName().endsWith("ActivityRecord$Token")) {
                return null;
            }
            if (f240c == null) {
                f240c = XposedHelpers.findField(layoutParamsC.token.getClass(), "weakActivity");
            }
            return ((WeakReference) f240c.get(layoutParamsC.token)).get();
        } catch (Throwable th) {
            f.v.d(th);
            return null;
        }
    }

    static String b(Object obj) {
        try {
            String str = c(obj).packageName;
            if (str != null) {
                return str;
            }
        } catch (Throwable th) {
            f.v.d(th);
        }
        try {
            if (Build.VERSION.SDK_INT >= 30) {
                Object objectField = XposedHelpers.getObjectField(obj, "mActivityRecord");
                if (objectField != null) {
                    return b.c(objectField).getPackage();
                }
                return null;
            }
            Object objCallMethod = XposedHelpers.callMethod(obj, "getAppToken", new Object[0]);
            if (objCallMethod != null) {
                return (String) XposedHelpers.getObjectField(((WeakReference) XposedHelpers.getObjectField(objCallMethod, "weakActivity")).get(), "packageName");
            }
            return null;
        } catch (Throwable th2) {
            f.v.d(th2);
            return null;
        }
    }

    static WindowManager.LayoutParams c(Object obj) {
        if (f238a == null) {
            f238a = XposedHelpers.findField(f(obj), "mAttrs");
        }
        return (WindowManager.LayoutParams) f238a.get(obj);
    }

    static int d(Object obj) {
        if (f243f == null) {
            f243f = XposedHelpers.findMethodExact(f(obj), "getDisplayId", new Class[0]);
        }
        return ((Integer) f243f.invoke(obj, new Object[0])).intValue();
    }

    static Region e(Object obj, Region region) throws IllegalAccessException, InvocationTargetException {
        if (j == null) {
            j = XposedHelpers.findMethodExact(f(obj), "getTouchableRegion", new Class[]{Region.class});
        }
        j.invoke(obj, region);
        return region;
    }

    private static Class<?> f(Object obj) {
        if (k == null) {
            Class<?> clsFindClass = obj.getClass();
            if (!"com.android.server.wm.WindowState".equals(clsFindClass.getName())) {
                clsFindClass = XposedHelpers.findClass("com.android.server.wm.WindowState", clsFindClass.getClassLoader());
            }
            k = clsFindClass;
        }
        return k;
    }

    static Rect g(Object obj, Rect rect) {
        if (Build.VERSION.SDK_INT >= 31) {
            if (f244g == null) {
                f244g = XposedHelpers.findMethodExact(f(obj), "getFrame", new Class[0]);
            }
            return (Rect) f244g.invoke(obj, new Object[0]);
        }
        if (h == null) {
            Class<?> clsF = f(obj);
            h = XposedHelpers.findMethodExact(clsF, "getContentFrameLw", new Class[0]);
            i = XposedHelpers.findMethodExact(clsF, "getGivenContentInsetsLw", new Class[0]);
        }
        Rect rect2 = (Rect) h.invoke(obj, new Object[0]);
        Rect rect3 = (Rect) i.invoke(obj, new Object[0]);
        rect.left = rect2.left + rect3.left;
        rect.top = rect2.top + rect3.top;
        rect.right = rect2.right + rect3.right;
        rect.bottom = rect2.bottom + rect3.bottom;
        return rect;
    }

    static boolean h(Object obj) {
        if (f241d == null) {
            f241d = XposedHelpers.findMethodExact(f(obj), "isFocused", new Class[0]);
        }
        return ((Boolean) f241d.invoke(obj, new Object[0])).booleanValue();
    }

    static boolean i(Object obj) {
        if (f242e == null) {
            f242e = XposedHelpers.findMethodExact(f(obj), "isVisibleNow", new Class[0]);
        }
        return ((Boolean) f242e.invoke(obj, new Object[0])).booleanValue();
    }
}
