package com.jozein.xedgepro.xposed;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.Method;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
class g extends p2 {
    private Class<?> E;
    private Method[] F = null;

    g(Class<?> cls) {
        this.E = null;
        this.E = cls;
    }

    g(String str, ClassLoader classLoader) {
        this.E = null;
        try {
            this.E = XposedHelpers.findClass(str, classLoader);
        } catch (Throwable th) {
            p2.g(th);
        }
    }

    final Object h(String str, Object... objArr) {
        return XposedHelpers.callStaticMethod(this.E, str, objArr);
    }

    final Method i(String str) {
        if (this.F == null) {
            this.F = this.E.getDeclaredMethods();
        }
        for (Method method : this.F) {
            if (method.getName().equals(str)) {
                method.setAccessible(true);
                return method;
            }
        }
        throw new NoSuchMethodError(this.E.getName() + '.' + str);
    }

    final Method j(String str, Class<?>... clsArr) {
        return XposedHelpers.findMethodBestMatch(this.E, str, clsArr);
    }

    final Method k(String str, Class<?>... clsArr) {
        return XposedHelpers.findMethodExact(this.E, str, clsArr);
    }

    final Method l(String str) {
        int length;
        if (this.F == null) {
            this.F = this.E.getDeclaredMethods();
        }
        Method method = null;
        int i = -1;
        for (Method method2 : this.F) {
            if (method2.getName().equals(str) && (length = method2.getParameterTypes().length) > i) {
                method = method2;
                i = length;
            }
        }
        if (method != null) {
            method.setAccessible(true);
            return method;
        }
        throw new NoSuchMethodError(this.E.getName() + '.' + str);
    }

    final Class<?> m() {
        return this.E;
    }

    final Set<XC_MethodHook.Unhook> n(String str, XC_MethodHook xC_MethodHook) {
        return XposedBridge.hookAllMethods(this.E, str, xC_MethodHook);
    }

    final XC_MethodHook.Unhook o(String str, XC_MethodHook xC_MethodHook) {
        return XposedBridge.hookMethod(i(str), xC_MethodHook);
    }

    final XC_MethodHook.Unhook p(String str, Object... objArr) {
        return XposedHelpers.findAndHookMethod(this.E, str, objArr);
    }

    final XC_MethodHook.Unhook q(String str, XC_MethodHook xC_MethodHook) {
        return XposedBridge.hookMethod(l(str), xC_MethodHook);
    }
}
