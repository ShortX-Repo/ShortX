package com.jozein.xedgepro.xposed;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import f.f0;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/* JADX INFO: loaded from: classes.dex */
class j2 extends g {
    private volatile Object G;

    j2(Class<?> cls) {
        super(cls);
        this.G = null;
    }

    j2(Class<?> cls, Object obj) {
        super(cls);
        this.G = null;
        this.G = obj;
    }

    j2(String str, ClassLoader classLoader) {
        super(str, classLoader);
        this.G = null;
    }

    void A(Object obj) {
        if (obj != null && this.G == null && s(obj)) {
            this.G = obj;
        }
    }

    final Object r(String str, Object... objArr) {
        return XposedHelpers.findMethodBestMatch(m(), str, objArr).invoke(v(), objArr);
    }

    boolean s(Object obj) {
        if (obj != null && m().isInstance(obj)) {
            return true;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Object ");
        sb.append(obj != null ? obj.getClass().getName() : "null");
        sb.append(" not instance of ");
        sb.append(m().getName());
        f.v.d(new RuntimeException(sb.toString()));
        return false;
    }

    final int t(String str) {
        return XposedHelpers.getIntField(v(), str);
    }

    final Object u(String str) {
        return XposedHelpers.getObjectField(v(), str);
    }

    final Object v() {
        if (this.G == null) {
            Object objX = x();
            if (s(objX)) {
                this.G = objX;
            }
        }
        return this.G;
    }

    final Object w(Member member, Object[] objArr) {
        return XposedBridge.invokeOriginalMethod(member, v(), objArr);
    }

    protected Object x() {
        throw new NullPointerException("Instance not got for " + m().getName());
    }

    final Object y() {
        return this.G;
    }

    final void z(String str, f0.d dVar, Object... objArr) throws IllegalAccessException, InvocationTargetException {
        Method methodI = i(str);
        Class<?> cls = methodI.getParameterTypes()[0];
        Object objNewProxyInstance = Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls}, dVar);
        Object objV = v();
        int length = objArr.length;
        if (length == 0) {
            methodI.invoke(objV, objNewProxyInstance);
            return;
        }
        if (length == 1) {
            methodI.invoke(objV, objNewProxyInstance, objArr[0]);
            return;
        }
        if (length == 2) {
            methodI.invoke(objV, objNewProxyInstance, objArr[0], objArr[1]);
            return;
        }
        if (length == 3) {
            methodI.invoke(objV, objNewProxyInstance, objArr[0], objArr[1], objArr[2]);
            return;
        }
        if (length == 4) {
            methodI.invoke(objV, objNewProxyInstance, objArr[0], objArr[1], objArr[2], objArr[3]);
            return;
        }
        if (length == 5) {
            methodI.invoke(objV, objNewProxyInstance, objArr[0], objArr[1], objArr[2], objArr[3], objArr[4]);
            return;
        }
        Object[] objArr2 = new Object[objArr.length + 1];
        objArr2[0] = objNewProxyInstance;
        System.arraycopy(objArr, 0, objArr2, 1, objArr.length);
        methodI.invoke(objV, objArr2);
    }
}
