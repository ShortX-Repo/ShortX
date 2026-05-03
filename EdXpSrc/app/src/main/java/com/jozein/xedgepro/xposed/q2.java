package com.jozein.xedgepro.xposed;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
class q2 extends p2 {
    private static final String K;
    private Context F;
    private PackageManager G;
    private Object H;
    private boolean E = false;
    private Method I = null;
    private String J = null;

    class a extends XC_MethodHook {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        int f213a = -1;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        boolean f214b = false;

        a() {
        }

        protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            if (this.f214b || !q2.this.E) {
                return;
            }
            try {
                String str = (String) methodHookParam.args[0];
                byte b2 = -1;
                int iHashCode = str.hashCode();
                if (iHashCode != 172491798) {
                    if (iHashCode != 525384130) {
                        if (iHashCode == 1544582882 && str.equals("android.intent.action.PACKAGE_ADDED")) {
                            b2 = 0;
                        }
                    } else if (str.equals("android.intent.action.PACKAGE_REMOVED")) {
                        b2 = 1;
                    }
                } else if (str.equals("android.intent.action.PACKAGE_CHANGED")) {
                    b2 = 2;
                }
                if (b2 == 0 || b2 == 1 || b2 == 2) {
                    if (this.f213a < 0) {
                        int i = 0;
                        while (true) {
                            Object[] objArr = methodHookParam.args;
                            if (i >= objArr.length) {
                                break;
                            }
                            if (objArr[i] instanceof int[]) {
                                this.f213a = i;
                                break;
                            }
                            i++;
                        }
                    }
                    Object[] objArr2 = methodHookParam.args;
                    String str2 = (String) objArr2[1];
                    int i2 = this.f213a;
                    if (i2 < 0 || objArr2[i2] == null) {
                        f.z.k0(str, str2, f.o0.h(((Bundle) objArr2[2]).getInt("android.intent.extra.UID", 0)));
                    } else {
                        f.z.l0(str, str2, (int[]) objArr2[i2]);
                    }
                }
            } catch (ClassCastException e2) {
                e = e2;
                this.f214b = true;
                f.z.m0(false);
                f.v.d(e);
            } catch (Throwable th) {
                e = th;
                f.v.d(e);
            }
        }
    }

    static {
        K = Build.VERSION.SDK_INT >= 28 ? "com.android.launcher3" : "com.android.launcher2";
    }

    q2(ClassLoader classLoader) {
        int i = Build.VERSION.SDK_INT;
        if (i < 21 || i > 29) {
            return;
        }
        try {
            new j2("com.android.server.pm.PackageManagerService", classLoader).o("sendPackageBroadcast", new a());
            f.z.m0(true);
        } catch (Throwable th) {
            p2.g(th);
        }
    }

    private Object l() {
        Object obj = this.H;
        if (obj != null) {
            return obj;
        }
        Object objK = g3.k();
        this.H = objK;
        return objK;
    }

    private String o() {
        if (this.J == null) {
            this.J = k("android.intent.category.HOME", true);
        }
        return this.J;
    }

    static boolean s(Intent intent) {
        return intent != null && "android.intent.action.MAIN".equals(intent.getAction()) && intent.hasCategory("android.intent.category.HOME") && intent.getCategories().size() == 1 && intent.getData() == null && intent.getType() == null;
    }

    boolean i(String str, int i) {
        String strO;
        if ((i == 0 && f.l.j.equals(str)) || "android".equals(str) || "com.android.systemui".equals(str)) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 28 || (strO = o()) == null || !strO.equals(str)) {
            return !n().equals(str);
        }
        return false;
    }

    boolean j(String str) {
        return ("android".equals(str) || "com.android.systemui".equals(str) || n().equals(str)) ? false : true;
    }

    String k(String str, boolean z) {
        try {
            ResolveInfo resolveInfoX = f.c0.x(new Intent("android.intent.action.MAIN").addCategory(str), (!z || Build.VERSION.SDK_INT < 24) ? 65536 : 1048576, f.o0.d());
            if (resolveInfoX != null) {
                return resolveInfoX.activityInfo.packageName;
            }
            return null;
        } catch (Throwable th) {
            f.v.d(th);
            return null;
        }
    }

    List<String> m() {
        String strO;
        ArrayList arrayList = new ArrayList();
        String strN = n();
        arrayList.add(strN);
        if (Build.VERSION.SDK_INT >= 28 && (strO = o()) != null && !strO.equals(strN) && !strO.equals("android")) {
            arrayList.add(strO);
        }
        arrayList.add("android");
        arrayList.add("com.android.systemui");
        return arrayList;
    }

    String n() {
        String strK = k("android.intent.category.HOME", false);
        return strK != null ? strK : K;
    }

    void p(Context context) {
        this.F = context;
        this.G = context.getPackageManager();
    }

    void q(String str, int i) {
        XposedHelpers.callMethod(Build.VERSION.SDK_INT >= 24 ? this.G : l(), "installExistingPackageAsUser", new Object[]{str, Integer.valueOf(i)});
    }

    boolean r(String str, int i) {
        return f.c0.c(str, i).enabled;
    }

    boolean t(String str, int i) {
        return f.z.K(this.F).V(str, i);
    }

    public void u(String str, int i, boolean z) throws IllegalAccessException, InvocationTargetException {
        int i2 = z ? 1 : 2;
        if (i == 0) {
            this.G.setApplicationEnabledSetting(str, i2, 0);
            return;
        }
        Object objL = l();
        if (this.I == null) {
            Class<?> cls = objL.getClass();
            Class cls2 = Integer.TYPE;
            this.I = XposedHelpers.findMethodBestMatch(cls, "setApplicationEnabledSetting", new Class[]{String.class, cls2, cls2, cls2, String.class});
        }
        this.I.invoke(objL, str, Integer.valueOf(i2), 0, Integer.valueOf(i), "android");
    }

    public void v(String str, int i, boolean z) {
        try {
            u(str, i, z);
        } catch (Throwable th) {
            f.v.c(th.toString());
        }
    }

    void w() {
        this.E = true;
        f.z.K(this.F);
        if (Build.VERSION.SDK_INT < 24) {
            try {
                f.c0.y(l());
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
    }
}
