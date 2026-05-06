package com.jozein.xedgepro.xposed;

import android.content.Intent;
import android.os.Build;
import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static Field f87a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static Method f88b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static Field f89c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static Field f90d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static Field f91e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static Field f92f;

    static Object a(Object obj) {
        if (f92f == null) {
            f92f = XposedHelpers.findField(obj.getClass(), Build.VERSION.SDK_INT >= 33 ? "token" : "appToken");
        }
        return f92f.get(obj);
    }

    static int b(Object obj) {
        if (Build.VERSION.SDK_INT >= 26 && v.f245f) {
            try {
                if (f88b == null) {
                    f88b = p2.b(obj.getClass(), "getDisplayId", new Class[0]);
                }
                int iIntValue = ((Integer) f88b.invoke(obj, new Object[0])).intValue();
                if (iIntValue == -1) {
                    return 0;
                }
                return iIntValue;
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
        return 0;
    }

    static Intent c(Object obj) {
        if (f90d == null) {
            f90d = XposedHelpers.findField(obj.getClass(), "intent");
        }
        return (Intent) f90d.get(obj);
    }

    static String d(Object obj) {
        try {
            if (f91e == null) {
                f91e = XposedHelpers.findField(obj.getClass(), "packageName");
            }
            return (String) f91e.get(obj);
        } catch (Throwable th) {
            f.v.d(th);
            try {
                return f.c0.f(c(obj));
            } catch (Throwable th2) {
                f.v.d(th2);
                return null;
            }
        }
    }

    private static Object e(Object obj) {
        try {
            if (f89c == null) {
                f89c = XposedHelpers.findField(obj.getClass(), "task");
            }
            return f89c.get(obj);
        } catch (Throwable th) {
            f.v.d(th);
            return null;
        }
    }

    static int f(Object obj) {
        return p3.d(e(obj));
    }

    static int g(Object obj) {
        try {
            if (f87a == null) {
                f87a = XposedHelpers.findField(obj.getClass(), Build.VERSION.SDK_INT > 28 ? "mUserId" : "userId");
            }
            return f87a.getInt(obj);
        } catch (Throwable th) {
            f.v.d(th);
            return 0;
        }
    }
}
