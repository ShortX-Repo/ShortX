package com.jozein.xedgepro.xposed;

import android.content.Context;
import android.os.Environment;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
class p2 implements f.l {
    static final String A = Environment.getDataDirectory() + "/system/xedge/";
    private static final String B = '[' + f.l.x + ']';
    private static Context C;
    private static ClassLoader D;
    static final String z;

    static {
        String str;
        String str2;
        try {
            str = System.getenv("ANDROID_CACHE");
        } catch (Throwable th) {
            f.v.d(th);
            str = null;
        }
        if (str != null) {
            str2 = str + '/';
        } else {
            str2 = "/cache/";
        }
        z = str2;
        C = null;
        D = null;
    }

    p2() {
    }

    static Object a(Object obj, String str, Object... objArr) {
        Class<?> superclass = obj.getClass();
        NoSuchMethodError noSuchMethodError = null;
        do {
            try {
                return XposedHelpers.findMethodBestMatch(superclass, str, objArr).invoke(obj, objArr);
            } catch (NoSuchMethodError e2) {
                if (noSuchMethodError == null) {
                    noSuchMethodError = e2;
                }
                superclass = superclass.getSuperclass();
                if (superclass == Object.class) {
                    throw noSuchMethodError;
                }
            }
        } while (superclass != null);
        throw noSuchMethodError;
    }

    static Method b(Class<?> cls, String str, Class<?>... clsArr) {
        NoSuchMethodError noSuchMethodError = null;
        do {
            try {
                return XposedHelpers.findMethodExact(cls, str, clsArr);
            } catch (NoSuchMethodError e2) {
                if (noSuchMethodError == null) {
                    noSuchMethodError = e2;
                }
                cls = cls.getSuperclass();
                if (cls == Object.class) {
                    throw noSuchMethodError;
                }
            }
        } while (cls != null);
        throw noSuchMethodError;
    }

    static ClassLoader c() {
        ClassLoader classLoader = D;
        return classLoader != null ? classLoader : p2.class.getClassLoader();
    }

    static Context d(Context context) {
        if (C == null && D == null) {
            e(context.getClassLoader());
        }
        try {
            C = context.createPackageContext(f.l.j, 2);
        } catch (Throwable th) {
            f.v.d(th);
        }
        return C;
    }

    static void e(ClassLoader classLoader) {
        D = classLoader;
    }

    static void f(String str) {
        XposedBridge.log(B + ' ' + str);
        f.v.f(str);
    }

    static void g(Throwable th) {
        String str = th.getClass().getName() + ": " + th.getMessage();
        XposedBridge.log(B + ' ' + str);
        f.v.f(str);
    }
}
