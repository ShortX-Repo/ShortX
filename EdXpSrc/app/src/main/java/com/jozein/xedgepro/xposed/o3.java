package com.jozein.xedgepro.xposed;

import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
class o3 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    static final Class<?> f204a = XposedHelpers.findClass("android.os.SystemProperties", o3.class.getClassLoader());

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static final Method[] f205b = new Method[5];

    o3() {
    }

    private static Method a(String str, Class<?> cls) {
        return XposedHelpers.findMethodExact(f204a, str, new Class[]{String.class, cls});
    }

    static void b(String str, String str2) throws IllegalAccessException, InvocationTargetException {
        Method[] methodArr = f205b;
        if (methodArr[4] == null) {
            methodArr[4] = a("set", String.class);
        }
        methodArr[4].invoke(null, str, str2);
    }
}
