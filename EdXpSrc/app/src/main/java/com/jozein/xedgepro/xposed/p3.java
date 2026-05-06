package com.jozein.xedgepro.xposed;

import android.content.Intent;
import android.os.Build;
import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
class p3 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static Field f208a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static Field f209b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static Method f210c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static Method f211d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static Field f212e;

    static Intent a(Object obj) throws IllegalAccessException, InvocationTargetException {
        Object objectField;
        if (Build.VERSION.SDK_INT >= 21) {
            if (f211d == null) {
                f211d = p2.b(obj.getClass(), "getBaseIntent", new Class[0]);
            }
            objectField = f211d.invoke(obj, new Object[0]);
        } else {
            Intent intent = (Intent) XposedHelpers.getObjectField(obj, "intent");
            if (intent != null) {
                return intent;
            }
            objectField = XposedHelpers.getObjectField(obj, "affinityIntent");
        }
        return (Intent) objectField;
    }

    static int b(Object obj) {
        if (!v.f245f) {
            return 0;
        }
        if (Build.VERSION.SDK_INT < 30) {
            return c.a(c(obj));
        }
        if (f210c == null) {
            f210c = p2.b(obj.getClass(), "getDisplayId", new Class[0]);
        }
        int iIntValue = ((Integer) f210c.invoke(obj, new Object[0])).intValue();
        if (iIntValue == -1) {
            return 0;
        }
        return iIntValue;
    }

    static Object c(Object obj) {
        if (f212e == null) {
            f212e = XposedHelpers.findField(obj.getClass(), Build.VERSION.SDK_INT >= 26 ? "mStack" : "stack");
        }
        return f212e.get(obj);
    }

    static int d(Object obj) {
        if (obj == null) {
            return -1;
        }
        try {
            if (f208a == null) {
                f208a = XposedHelpers.findField(obj.getClass(), Build.VERSION.SDK_INT > 29 ? "mTaskId" : "taskId");
            }
            return f208a.getInt(obj);
        } catch (Throwable th) {
            f.v.d(th);
            return -1;
        }
    }

    static int e(Object obj) {
        if (obj != null) {
            try {
                if (f209b == null) {
                    f209b = XposedHelpers.findField(obj.getClass(), Build.VERSION.SDK_INT > 29 ? "mUserId" : "userId");
                }
                return f209b.getInt(obj);
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
        return f.o0.d();
    }
}
