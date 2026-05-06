package com.jozein.xedgepro.xposed;

import a.b;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.util.EventLog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.jozein.xedgepro.ui.ActivityPerformAction;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import f.c0;
import f.o0;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
class l0 extends j2 {
    private final a.p H;
    private final w1 I;
    private final v J;
    final q2 K;
    private Context L;
    private Handler M;
    private final e N;
    private final g O;
    private final SparseIntArray P;
    private int Q;
    private ActivityManager R;
    private boolean S;
    private boolean T;
    private Method U;
    private Method V;
    private Method W;
    private Method X;
    private final Comparator<ActivityManager.RunningTaskInfo> Y;
    private final Comparator<ActivityManager.RunningTaskInfo> Z;
    private Field a0;
    private int b0;
    private Method c0;
    private Method d0;
    private Method e0;
    private Object f0;
    private Object g0;
    private Method h0;
    private Method i0;
    private Object j0;
    private Object k0;
    private boolean l0;

    class a extends XC_MethodHook {
        a() {
        }

        protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            if (!l0.this.T) {
                l0.this.A(methodHookParam.thisObject);
                return;
            }
            try {
                Object obj = methodHookParam.args[0];
                ApplicationInfo applicationInfo = (ApplicationInfo) XposedHelpers.getObjectField(obj, "info");
                l0.this.Z0(applicationInfo.packageName, XposedHelpers.getIntField(obj, "userId"));
            } catch (Throwable unused) {
            }
        }
    }

    class b extends XC_MethodHook {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final ComponentName f139a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        int f140b;

        b(int i) {
            super(i);
            this.f139a = new ComponentName(f.l.j, ActivityPerformAction.class.getName());
            this.f140b = -1;
        }

        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            a.b bVarJ;
            int i;
            if (!l0.this.T) {
                if (Build.VERSION.SDK_INT < 29) {
                    l0.this.A(methodHookParam.thisObject);
                    return;
                } else {
                    l0.this.j0 = methodHookParam.thisObject;
                    return;
                }
            }
            try {
                if (this.f140b < 0) {
                    int i2 = 0;
                    while (true) {
                        Object[] objArr = methodHookParam.args;
                        if (i2 >= objArr.length) {
                            break;
                        }
                        if (objArr[i2] instanceof Intent) {
                            this.f140b = i2;
                            break;
                        }
                        i2++;
                    }
                    if (this.f140b < 0) {
                        return;
                    }
                }
                Intent intent = (Intent) methodHookParam.args[this.f140b];
                if (!this.f139a.equals(intent.getComponent()) || (i = (bVarJ = f.l0.j(intent)).z) == 0 || i == 1) {
                    return;
                }
                l0.this.I.u3(bVarJ);
                methodHookParam.setResult(4);
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
    }

    class c extends XC_MethodHook {
        c() {
        }

        protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            try {
                int iIntValue = ((Integer) methodHookParam.args[0]).intValue();
                Object[] objArr = (Object[]) methodHookParam.args[1];
                if (iIntValue != 30001 && iIntValue != 30005) {
                    if (iIntValue != 31003) {
                        return;
                    }
                    l0.this.k1(((Integer) objArr[0]).intValue());
                    return;
                }
                int iIntValue2 = ((Integer) objArr[0]).intValue();
                ComponentName componentNameUnflattenFromString = ComponentName.unflattenFromString((String) objArr[3]);
                if (componentNameUnflattenFromString != null) {
                    int iIntValue3 = ((Integer) objArr[2]).intValue();
                    if (!l0.this.S && iIntValue2 > 1000) {
                        l0.this.S = true;
                        f.v.c("Invalid user id: " + iIntValue2);
                    }
                    if (l0.this.S) {
                        iIntValue2 = l0.this.z0(iIntValue3);
                    }
                    if (iIntValue == 30005) {
                        l0.this.f1(componentNameUnflattenFromString.getPackageName(), iIntValue2);
                    } else {
                        l0.this.d1(componentNameUnflattenFromString.getPackageName(), iIntValue2);
                    }
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
    }

    static final class d implements Comparable<d> {
        final int A;
        final String z;

        d(String str, int i) {
            this.z = str;
            this.A = i;
        }

        @Override // java.lang.Comparable
        /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
        public int compareTo(d dVar) {
            int iCompareTo = this.z.compareTo(dVar.z);
            return iCompareTo != 0 ? iCompareTo : this.A - dVar.A;
        }

        public boolean b(String str, int i) {
            return i == this.A && Objects.equals(str, this.z);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || d.class != obj.getClass()) {
                return false;
            }
            d dVar = (d) obj;
            return this.A == dVar.A && Objects.equals(this.z, dVar.z);
        }

        public int hashCode() {
            return Objects.hash(this.z, Integer.valueOf(this.A));
        }
    }

    private static final class e {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private final Map<String, f.t> f143a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private final SparseArray<Map<String, f.t>> f144b;

        private e() {
            this.f143a = new HashMap();
            this.f144b = new SparseArray<>();
        }

        /* synthetic */ e(a aVar) {
            this();
        }

        private Map<String, f.t> d(int i) {
            if (i == 0) {
                return this.f143a;
            }
            Map<String, f.t> map = this.f144b.get(i);
            if (map != null) {
                return map;
            }
            HashMap map2 = new HashMap();
            this.f144b.put(i, map2);
            return map2;
        }

        synchronized void a(String str, int i) {
            d(i).remove(str);
        }

        synchronized int b(String str, int i) {
            Map<String, f.t> mapD = d(i);
            f.t tVar = mapD.get(str);
            if (tVar != null) {
                int i2 = tVar.f488a - 1;
                tVar.f488a = i2;
                if (i2 > 0) {
                    return i2;
                }
                mapD.remove(str);
                return 0;
            }
            f.v.c("Removing package not added! " + str + "(" + i + ").");
            return 0;
        }

        synchronized int c(String str, int i) {
            f.t tVar;
            tVar = d(i).get(str);
            return tVar != null ? tVar.f488a : 0;
        }

        synchronized int e(String str, int i) {
            Map<String, f.t> mapD = d(i);
            f.t tVar = mapD.get(str);
            if (tVar == null) {
                mapD.put(str, new f.t(1));
                return 1;
            }
            int i2 = tVar.f488a + 1;
            tVar.f488a = i2;
            return i2;
        }
    }

    static class f extends c0.b {

        /* JADX INFO: renamed from: g, reason: collision with root package name */
        private final l0 f145g;
        private final ActivityManager.RunningTaskInfo h;
        private final Object i;
        private ComponentName j;
        private Intent k;

        f(l0 l0Var, ActivityManager.RunningTaskInfo runningTaskInfo, ApplicationInfo applicationInfo) {
            super(applicationInfo);
            this.f145g = l0Var;
            this.h = runningTaskInfo;
            this.j = runningTaskInfo.topActivity;
            this.k = (Build.VERSION.SDK_INT < 29 || runningTaskInfo.numActivities != 1) ? null : runningTaskInfo.baseIntent;
            this.i = null;
        }

        f(l0 l0Var, Object obj, ApplicationInfo applicationInfo, Intent intent) {
            super(applicationInfo);
            this.f145g = l0Var;
            this.i = obj;
            this.k = intent;
            this.h = null;
        }

        f(l0 l0Var, String str, int i) {
            super(f.c0.c(str, i));
            this.f145g = l0Var;
            this.i = null;
            this.h = null;
        }

        static f D(l0 l0Var, Object obj) {
            int iG = com.jozein.xedgepro.xposed.b.g(obj);
            Intent intentC = com.jozein.xedgepro.xposed.b.c(obj);
            return new f(l0Var, obj, f.c0.c(f.c0.f(intentC), iG), intentC);
        }

        void A() {
            int[] iArrE = f.o0.e(this.f145g.L);
            String strI = i();
            int iK = k();
            for (int i : iArrE) {
                if (i != iK && !this.f145g.K.t(strI, i)) {
                    this.f145g.K.q(strI, i);
                    this.f145g.I.G4(strI + f.c0.z + i + ") installed");
                    return;
                }
            }
        }

        boolean B() {
            try {
                if (this.h != null) {
                    return this.f145g.p0().id == this.h.id;
                }
                Object obj = this.i;
                return obj != null ? obj == this.f145g.n0() : this.f145g.o0().equals(i());
            } catch (Throwable th) {
                f.v.d(th);
                return false;
            }
        }

        boolean C() {
            return !m() || this.f145g.I0(i(), k());
        }

        void E(Bundle bundle) {
            int iF;
            ActivityManager.RunningTaskInfo runningTaskInfo = this.h;
            if (runningTaskInfo != null) {
                this.f145g.X0(runningTaskInfo, bundle);
                return;
            }
            Object obj = this.i;
            if (obj == null || (iF = com.jozein.xedgepro.xposed.b.f(obj)) < 0) {
                this.f145g.s1(i(), bundle, k());
            } else {
                this.f145g.W0(iF, bundle);
            }
        }

        void F(boolean z) {
            this.f145g.l1(i(), k(), z);
        }

        void G() {
            this.f145g.t1(i(), k());
        }

        void H() {
            String strI = i();
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + strI));
            if (f.l.w.equals(strI)) {
                intent.setPackage("com.android.vending");
            }
            try {
                this.f145g.q1(intent, false, null, f.o0.d());
            } catch (Throwable th) {
                f.v.c(th.toString());
                try {
                    this.f145g.q1(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/store/apps/details?id=" + strI)), false, null, f.o0.d());
                } catch (Throwable th2) {
                    f.v.d(th2);
                }
            }
        }

        a.b q() {
            ComponentName componentNameX = x();
            if (componentNameX == null) {
                return null;
            }
            return l() ? new b.f(componentNameX.getPackageName(), componentNameX.getClassName(), k()) : new b.e(componentNameX.getPackageName(), componentNameX.getClassName());
        }

        a.b r() {
            Intent intentY = y();
            if (intentY == null) {
                return null;
            }
            return l() ? new b.h(intentY, k()) : new b.g(intentY);
        }

        boolean s() {
            return this.f145g.K.i(i(), k());
        }

        boolean t() {
            if (this.i == null && this.h == null) {
                return false;
            }
            return !q2.s(y());
        }

        boolean u() {
            if (!m() || !this.f145g.K.j(i())) {
                return false;
            }
            if (this.i == null && this.h == null) {
                return this.f145g.J0(a().uid) && this.f145g.H0(i());
            }
            return true;
        }

        void v() throws IllegalAccessException, InvocationTargetException {
            Object obj = this.i;
            if (obj != null) {
                this.f145g.d0(obj);
                return;
            }
            ActivityManager.RunningTaskInfo runningTaskInfo = this.h;
            if (runningTaskInfo != null) {
                this.f145g.f0(runningTaskInfo);
            } else {
                f.v.d(new RuntimeException("Finish activity with out an activity info."));
            }
        }

        void w() {
            this.f145g.i0(i(), k());
        }

        ComponentName x() {
            Intent intentY;
            if (this.j == null && (intentY = y()) != null) {
                this.j = intentY.getComponent();
            }
            return this.j;
        }

        Intent y() {
            Object obj;
            if (this.k == null && (obj = this.i) != null) {
                try {
                    this.k = com.jozein.xedgepro.xposed.b.c(obj);
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
            return this.k;
        }

        CharSequence z() {
            try {
                ComponentName componentNameX = x();
                if (componentNameX != null) {
                    return componentNameX.flattenToShortString();
                }
                return null;
            } catch (Throwable th) {
                f.v.d(th);
                return null;
            }
        }
    }

    private static class g {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private final List<d> f146a;

        private g() {
            this.f146a = new ArrayList();
        }

        /* synthetic */ g(a aVar) {
            this();
        }

        private int h(String str, int i) {
            for (int size = this.f146a.size() - 1; size >= 0; size--) {
                if (this.f146a.get(size).b(str, i)) {
                    return size;
                }
            }
            return -1;
        }

        synchronized void a(String str, int i) {
            if (h(str, i) < 0) {
                this.f146a.add(new d(str, i));
            }
        }

        synchronized boolean b(String str, int i) {
            return h(str, i) >= 0;
        }

        synchronized List<d> c() {
            ArrayList arrayList;
            arrayList = new ArrayList(this.f146a);
            this.f146a.clear();
            return arrayList;
        }

        synchronized void d(c0.c cVar, int i) {
            for (int size = this.f146a.size() - 1; size >= 0; size--) {
                d dVar = this.f146a.get(size);
                if (dVar.A == i) {
                    cVar.a(dVar.z, i);
                }
            }
        }

        synchronized void e(List<d> list, int i) {
            for (int size = this.f146a.size() - 1; size >= 0; size--) {
                d dVar = this.f146a.get(size);
                if (dVar.A == i) {
                    list.add(dVar);
                }
            }
        }

        synchronized void f(c0.c cVar, int[] iArr) {
            for (int size = this.f146a.size() - 1; size >= 0; size--) {
                d dVar = this.f146a.get(size);
                if (f.p0.a(iArr, dVar.A) >= 0) {
                    cVar.a(dVar.z, dVar.A);
                }
            }
        }

        synchronized void g(List<d> list, int[] iArr) {
            for (int size = this.f146a.size() - 1; size >= 0; size--) {
                d dVar = this.f146a.get(size);
                if (f.p0.a(iArr, dVar.A) >= 0) {
                    list.add(dVar);
                }
            }
        }

        synchronized boolean i(String str, int i) {
            boolean z;
            int iH = h(str, i);
            if (iH >= 0) {
                this.f146a.remove(iH);
                z = true;
            } else {
                z = false;
            }
            return z;
        }
    }

    private class h extends com.jozein.xedgepro.xposed.g {

        class a extends XC_MethodHook {
            a() {
            }

            protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
                l0.this.c1(methodHookParam.thisObject);
            }
        }

        class b extends XC_MethodHook {
            b() {
            }

            protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
                if (!l0.this.l0) {
                    l0.this.l0 = true;
                }
                l0.this.e1(methodHookParam.thisObject);
            }
        }

        h(ClassLoader classLoader) {
            super("com.android.server.am.ActivityRecord", classLoader);
        }

        boolean r() {
            try {
                XC_MethodHook.Unhook unhookP = p("takeFromHistory", new a());
                try {
                    p(Build.VERSION.SDK_INT >= 26 ? "createWindowContainer" : "putInHistory", new b());
                    return true;
                } catch (Throwable th) {
                    f.v.d(th);
                    unhookP.unhook();
                    return false;
                }
            } catch (Throwable th2) {
                p2.g(th2);
            }
        }
    }

    private class i extends com.jozein.xedgepro.xposed.g {

        class a extends XC_MethodHook {
            a() {
            }

            protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
                if (l0.this.l0) {
                    return;
                }
                l0.this.c1(methodHookParam.args[0]);
            }
        }

        class b extends XC_MethodHook {
            b() {
            }

            protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
                if (l0.this.l0) {
                    return;
                }
                l0.this.e1(methodHookParam.args[0]);
            }
        }

        i(ClassLoader classLoader) {
            super("com.android.server.am.ActivityStack", classLoader);
        }

        boolean r() {
            try {
                XC_MethodHook.Unhook unhookO = o("removeActivityFromHistoryLocked", new a());
                b bVar = new b();
                int i = Build.VERSION.SDK_INT;
                if (i >= 24 && i < 26) {
                    try {
                        o("addConfigOverride", bVar);
                        return true;
                    } catch (Throwable unused) {
                    }
                }
                try {
                    o("startActivityLocked", bVar);
                    return true;
                } catch (Throwable th) {
                    f.v.d(th);
                    unhookO.unhook();
                    return false;
                }
            } catch (Throwable th2) {
                f.v.d(th2);
                return false;
            }
        }
    }

    static class j extends c0.f {
        private final l0 i;

        j(l0 l0Var, ActivityManager.RunningTaskInfo runningTaskInfo, int i) {
            super(runningTaskInfo, i);
            this.i = l0Var;
        }

        void s(Bundle bundle) {
            this.i.X0(r(), bundle);
        }

        f t() {
            return new f(this.i, r(), a());
        }
    }

    l0(ClassLoader classLoader, a.p pVar, w1 w1Var, v vVar, boolean z) {
        super("com.android.server.am.ActivityManagerService", classLoader);
        a aVar = null;
        this.L = null;
        this.M = null;
        this.N = new e(aVar);
        this.O = new g(aVar);
        this.P = new SparseIntArray();
        boolean zR = false;
        this.Q = 0;
        this.R = null;
        this.T = false;
        this.W = null;
        this.X = null;
        this.Y = new Comparator() { // from class: com.jozein.xedgepro.xposed.k0
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return l0.L0((ActivityManager.RunningTaskInfo) obj, (ActivityManager.RunningTaskInfo) obj2);
            }
        };
        this.Z = new Comparator() { // from class: com.jozein.xedgepro.xposed.j0
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return l0.M0((ActivityManager.RunningTaskInfo) obj, (ActivityManager.RunningTaskInfo) obj2);
            }
        };
        this.a0 = null;
        this.b0 = -1;
        this.c0 = null;
        this.d0 = null;
        this.e0 = null;
        this.f0 = null;
        this.g0 = null;
        this.h0 = null;
        this.i0 = null;
        this.j0 = null;
        this.k0 = null;
        this.l0 = false;
        this.H = pVar;
        this.I = w1Var;
        this.J = vVar;
        this.S = z;
        this.K = new q2(classLoader);
        try {
            if (Build.VERSION.SDK_INT < 26) {
                zR = new h(classLoader).r();
                if (new i(classLoader).r()) {
                    zR = true;
                }
            }
            if (!zR) {
                B0();
            }
        } catch (Throwable th) {
            p2.g(th);
        }
        try {
            o("handleAppDiedLocked", new a());
        } catch (Throwable th2) {
            p2.g(th2);
        }
        try {
            b bVar = new b(10000);
            if (Build.VERSION.SDK_INT >= 29) {
                new com.jozein.xedgepro.xposed.g("com.android.server.wm.ActivityTaskManagerService", classLoader).q("startActivityAsUser", bVar);
            } else {
                q("startActivityAsUser", bVar);
            }
        } catch (Throwable th3) {
            p2.g(th3);
        }
    }

    private void B0() {
        XposedHelpers.findAndHookMethod(EventLog.class, "writeEvent", new Object[]{Integer.TYPE, Object[].class, new c()});
    }

    private boolean E0(o0.a aVar, ActivityManager.RunningTaskInfo runningTaskInfo) {
        return aVar.b() || aVar.a(A0(runningTaskInfo));
    }

    private void K0(String str, int i2) {
        Q0(str, i2);
        if (i2 != 0) {
            try {
                if (this.W == null) {
                    this.W = k("killBackgroundProcesses", String.class, Integer.TYPE);
                }
                this.W.invoke(v(), str, Integer.valueOf(i2));
                return;
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
        j0().killBackgroundProcesses(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int L0(ActivityManager.RunningTaskInfo runningTaskInfo, ActivityManager.RunningTaskInfo runningTaskInfo2) {
        return runningTaskInfo.id - runningTaskInfo2.id;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int M0(ActivityManager.RunningTaskInfo runningTaskInfo, ActivityManager.RunningTaskInfo runningTaskInfo2) {
        return runningTaskInfo2.id - runningTaskInfo.id;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void N0(String str, int i2) {
        if (this.N.c(str, i2) <= 0) {
            Q0(str, i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void O0(Intent intent, Bundle bundle, int i2, String str) {
        try {
            q1(intent, true, bundle, i2);
            this.O.a(str, i2);
        } catch (Throwable th) {
            f.v.d(th);
            this.K.v(str, i2, false);
        }
    }

    private void Q0(String str, int i2) {
        if (this.O.i(str, i2)) {
            this.K.v(str, i2, false);
        }
    }

    private void R0(boolean z, Bundle bundle) {
        List<ActivityManager.RunningTaskInfo> listW0 = w0();
        int size = listW0.size();
        if (size <= 1) {
            return;
        }
        List<String> listM = this.K.m();
        ActivityManager.RunningTaskInfo runningTaskInfo = listW0.get(0);
        o0.a aVarF = f.o0.f(this.L);
        if (listM.contains(runningTaskInfo.baseActivity.getPackageName())) {
            for (int i2 = 1; i2 < size; i2++) {
                ActivityManager.RunningTaskInfo runningTaskInfo2 = listW0.get(i2);
                if (!listM.contains(runningTaskInfo2.baseActivity.getPackageName()) && E0(aVarF, runningTaskInfo2) && X0(runningTaskInfo2, bundle)) {
                    return;
                }
            }
            return;
        }
        Collections.sort(listW0, z ? this.Z : this.Y);
        int iIndexOf = listW0.indexOf(runningTaskInfo);
        for (int i3 = iIndexOf + 1; i3 < size; i3++) {
            ActivityManager.RunningTaskInfo runningTaskInfo3 = listW0.get(i3);
            if (!listM.contains(runningTaskInfo3.baseActivity.getPackageName()) && E0(aVarF, runningTaskInfo3) && X0(runningTaskInfo3, bundle)) {
                return;
            }
        }
        for (int i4 = 0; i4 < iIndexOf; i4++) {
            ActivityManager.RunningTaskInfo runningTaskInfo4 = listW0.get(i4);
            if (!listM.contains(runningTaskInfo4.baseActivity.getPackageName()) && E0(aVarF, runningTaskInfo4) && X0(runningTaskInfo4, bundle)) {
                return;
            }
        }
    }

    private void T0(ActivityManager.RunningTaskInfo runningTaskInfo, Bundle bundle, int i2) {
        if (Build.VERSION.SDK_INT >= 31) {
            V0(runningTaskInfo.taskId, i2);
        } else {
            p1(D0(runningTaskInfo), bundle, A0(runningTaskInfo), i2);
        }
    }

    private void U0(Object obj, Bundle bundle, int i2) {
        if (Build.VERSION.SDK_INT >= 31) {
            V0(p3.d(obj), i2);
        } else {
            p1(new Intent(p3.a(obj)), bundle, p3.e(obj), i2);
        }
    }

    private void V0(int i2, int i3) {
        XposedHelpers.callMethod(k0(), "moveRootTaskToDisplay", new Object[]{Integer.valueOf(i2), Integer.valueOf(i3)});
    }

    private boolean Z(Intent intent, boolean z, boolean z2, int i2) {
        int iIntValue;
        int i3 = Build.VERSION.SDK_INT;
        if (i3 >= 29) {
            intent.addFlags(4194304);
        }
        synchronized (v()) {
            iIntValue = (i3 >= 33 ? (Integer) r("broadcastIntentLocked", null, null, null, intent, null, null, 0, null, null, null, null, null, -1, null, Boolean.valueOf(z), Boolean.valueOf(z2), 0, 0, 0, 0, Integer.valueOf(i2)) : i3 >= 31 ? (Integer) r("broadcastIntentLocked", null, null, null, intent, null, null, 0, null, null, null, null, -1, null, Boolean.valueOf(z), Boolean.valueOf(z2), 0, 0, 0, 0, Integer.valueOf(i2)) : i3 >= 30 ? (Integer) r("broadcastIntentLocked", null, null, null, intent, null, null, 0, null, null, null, -1, null, Boolean.valueOf(z), Boolean.valueOf(z2), 0, 0, 0, 0, Integer.valueOf(i2)) : i3 >= 29 ? (Integer) r("broadcastIntentLocked", null, null, intent, null, null, 0, null, null, null, -1, null, Boolean.valueOf(z), Boolean.valueOf(z2), 0, 0, 0, 0, Integer.valueOf(i2)) : i3 >= 23 ? (Integer) r("broadcastIntentLocked", null, null, intent, null, null, 0, null, null, null, -1, null, Boolean.valueOf(z), Boolean.valueOf(z2), 0, 0, Integer.valueOf(i2)) : (Integer) r("broadcastIntentLocked", null, null, intent, null, null, 0, null, null, null, -1, Boolean.valueOf(z), Boolean.valueOf(z2), 0, 0, Integer.valueOf(i2))).intValue();
        }
        return iIntValue == 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void Z0(String str, int i2) {
        if (!this.T || this.N.c(str, i2) > 0) {
            return;
        }
        Q0(str, i2);
    }

    private void a1(String str, int i2) {
        this.I.b3(str);
    }

    private Object b0() {
        int i2 = Build.VERSION.SDK_INT;
        try {
            if (i2 < 29) {
                try {
                    if (this.a0 == null) {
                        this.a0 = XposedHelpers.findField(m(), i2 >= 26 ? "mLastResumedActivity" : "mFocusedActivity");
                    }
                    Object obj = this.a0.get(v());
                    if (obj != null) {
                        return obj;
                    }
                } catch (Throwable unused) {
                }
                synchronized (y0()) {
                    Object objR = r("getFocusedStack", new Object[0]);
                    if (objR != null) {
                        return XposedHelpers.callMethod(objR, Build.VERSION.SDK_INT >= 28 ? "getTopActivity" : "topActivity", new Object[0]);
                    }
                    return null;
                }
            }
            Object objK0 = k0();
            if (this.a0 == null) {
                this.a0 = XposedHelpers.findField(objK0.getClass(), "mLastResumedActivity");
            }
            Object obj2 = this.a0.get(objK0);
            if (obj2 != null) {
                return obj2;
            }
            synchronized (y0()) {
                Object objCallMethod = XposedHelpers.callMethod(objK0, "getTopDisplayFocusedStack", new Object[0]);
                if (objCallMethod != null) {
                    return XposedHelpers.callMethod(objCallMethod, i2 > 29 ? "topRunningActivity" : "getTopActivity", new Object[0]);
                }
                return null;
            }
        } catch (Throwable unused2) {
            return null;
        }
        return null;
    }

    private void b1(final String str, final int i2) {
        this.I.c3(str);
        if (this.T && !this.H.o(29) && I0(str, i2)) {
            this.M.postDelayed(new Runnable() { // from class: com.jozein.xedgepro.xposed.i0
                @Override // java.lang.Runnable
                public final void run() {
                    this.z.N0(str, i2);
                }
            }, 1000L);
        }
    }

    private int c0(int i2) {
        try {
            return p3.e(V(i2));
        } catch (Throwable th) {
            f.v.d(th);
            return -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void c1(Object obj) {
        String strD = com.jozein.xedgepro.xposed.b.d(obj);
        if (strD != null) {
            d1(strD, com.jozein.xedgepro.xposed.b.g(obj));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void d1(String str, int i2) {
        if (str == null || this.N.b(str, i2) > 0) {
            return;
        }
        b1(str, i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void e1(Object obj) {
        String strD = com.jozein.xedgepro.xposed.b.d(obj);
        if (strD != null) {
            f1(strD, com.jozein.xedgepro.xposed.b.g(obj));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void f1(String str, int i2) {
        if (str == null || this.N.e(str, i2) != 1) {
            return;
        }
        a1(str, i2);
    }

    private void h1(int i2, int i3) {
        synchronized (this.P) {
            this.P.put(i2, i3);
        }
    }

    private Object k0() {
        if (this.j0 == null) {
            try {
                this.j0 = u("mActivityTaskManager");
            } catch (Throwable th) {
                f.v.d(th);
                this.j0 = g3.j();
            }
        }
        return this.j0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void k1(int i2) {
        synchronized (this.P) {
            this.P.delete(i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Object n0() {
        Object objB0 = b0();
        if (objB0 != null && com.jozein.xedgepro.xposed.b.b(objB0) != 0) {
            objB0 = null;
        }
        return objB0 != null ? objB0 : this.I.g2().K();
    }

    private List<ActivityManager.RunningTaskInfo> r0(List<ActivityManager.RunningTaskInfo> list) {
        if (Build.VERSION.SDK_INT < 29 || !this.J.d()) {
            return list;
        }
        int size = list.size();
        ArrayList arrayList = new ArrayList(size);
        for (int i2 = 0; i2 < size; i2++) {
            try {
                ActivityManager.RunningTaskInfo runningTaskInfo = list.get(i2);
                int iA = f3.a(runningTaskInfo);
                if (iA == 0 || iA == -1) {
                    arrayList.add(runningTaskInfo);
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
        return arrayList;
    }

    private void r1(Intent intent, int i2, Bundle bundle, int i3) throws IllegalAccessException, InvocationTargetException {
        Object objR;
        String type = intent.getType();
        if (type == null && intent.getData() != null && "content".equals(intent.getData().getScheme())) {
            try {
                if (this.U == null) {
                    this.U = XposedHelpers.findMethodExact(m(), "getProviderMimeType", new Class[]{Uri.class, Integer.TYPE});
                }
                type = (String) this.U.invoke(v(), intent.getData(), Integer.valueOf(i3));
            } catch (Throwable unused) {
            }
        }
        if (Build.VERSION.SDK_INT >= 21) {
            if (this.V == null) {
                this.V = XposedHelpers.findMethodBestMatch(m(), "startActivityAsUser", new Object[]{null, null, intent, type, null, null, 0, Integer.valueOf(i2), null, bundle, Integer.valueOf(i3)});
            }
            objR = this.V.invoke(v(), null, null, intent, type, null, null, 0, Integer.valueOf(i2), null, bundle, Integer.valueOf(i3));
        } else {
            objR = r("startActivityAsUser", null, null, intent, type, null, null, 0, Integer.valueOf(i2), null, null, bundle, Integer.valueOf(i3));
        }
        int iIntValue = ((Integer) objR).intValue();
        if (iIntValue == 0 || iIntValue == 1 || iIntValue == 2 || iIntValue == 3 || iIntValue == 4) {
            return;
        }
        throw new RuntimeException("Failed to start: " + (intent.getComponent() == null ? intent.getPackage() != null ? intent.getPackage() : intent.toString() : intent.getComponent().flattenToShortString()));
    }

    private int s0() {
        if (this.b0 < 0) {
            this.b0 = XposedHelpers.getStaticIntField(ActivityManager.class, "PROCESS_STATE_CACHED_EMPTY");
        }
        return this.b0;
    }

    private Object t0() {
        if (this.g0 == null) {
            this.g0 = XposedHelpers.getObjectField(k0(), Build.VERSION.SDK_INT > 29 ? "mRootWindowContainer" : "mRootActivityContainer");
        }
        return this.g0;
    }

    private void u1(final String str, Intent intent, final Bundle bundle, final int i2) throws PackageManager.NameNotFoundException {
        this.K.v(str, i2, true);
        if (intent == null) {
            try {
                intent = f.c0.g(str, i2);
            } catch (PackageManager.NameNotFoundException e2) {
                this.K.v(str, i2, false);
                throw e2;
            }
        }
        final Intent intent2 = intent;
        this.M.postDelayed(new Runnable() { // from class: com.jozein.xedgepro.xposed.h0
            @Override // java.lang.Runnable
            public final void run() {
                this.z.O0(intent2, bundle, i2, str);
            }
        }, 250L);
    }

    private Object v1(int i2) {
        Object objV;
        Object objInvoke;
        try {
            objV = V(i2);
        } catch (Throwable th) {
            f.v.d(th);
        }
        if (objV == null) {
            return null;
        }
        if (this.i0 == null) {
            Method methodB = p2.b(objV.getClass(), "topRunningActivityLocked", new Class[0]);
            this.i0 = methodB;
            if (methodB == null) {
                this.i0 = Build.VERSION.SDK_INT >= 30 ? p2.b(objV.getClass(), "getTopNonFinishingActivity", new Class[0]) : p2.b(objV.getClass(), "getTopActivity", new Class[0]);
            }
        }
        if (this.i0 != null) {
            synchronized (y0()) {
                objInvoke = this.i0.invoke(objV, new Object[0]);
            }
            return objInvoke;
        }
        return null;
    }

    private int x0(int i2) {
        int i3;
        synchronized (this.P) {
            i3 = this.P.get(i2, -1);
        }
        return i3;
    }

    private Object y0() {
        if (this.k0 == null) {
            this.k0 = Build.VERSION.SDK_INT < 29 ? v() : XposedHelpers.getObjectField(k0(), "mGlobalLock");
        }
        return this.k0;
    }

    int A0(ActivityManager.RunningTaskInfo runningTaskInfo) {
        int iC0;
        int i2;
        int iX0 = x0(runningTaskInfo.id);
        if (iX0 >= 0) {
            return iX0;
        }
        if (Build.VERSION.SDK_INT < 29 || (iC0 = f3.c(runningTaskInfo)) < 0) {
            iC0 = c0(runningTaskInfo.id);
            if (iC0 < 0) {
                return this.Q;
            }
            i2 = runningTaskInfo.id;
        } else {
            i2 = runningTaskInfo.taskId;
        }
        h1(i2, iC0);
        return iC0;
    }

    void C0(Context context, Handler handler) {
        this.L = context;
        this.M = handler;
        this.K.p(context);
    }

    Intent D0(ActivityManager.RunningTaskInfo runningTaskInfo) {
        if (Build.VERSION.SDK_INT >= 29) {
            return new Intent(runningTaskInfo.baseIntent);
        }
        try {
            return new Intent(p3.a(V(runningTaskInfo.id)));
        } catch (Throwable th) {
            f.v.c(th.toString());
            return new Intent().setComponent(runningTaskInfo.baseActivity);
        }
    }

    boolean F0(String str) {
        for (ActivityManager.RunningServiceInfo runningServiceInfo : j0().getRunningServices(100)) {
            if (str == null) {
                if (f.l.j.equals(runningServiceInfo.service.getPackageName()) && runningServiceInfo.started) {
                    return true;
                }
            } else if (str.equals(runningServiceInfo.service.getClassName())) {
                return runningServiceInfo.started;
            }
        }
        return false;
    }

    boolean G0() {
        try {
            Object objN0 = n0();
            if (objN0 != null) {
                return q2.s(com.jozein.xedgepro.xposed.b.c(objN0));
            }
            ActivityManager.RunningTaskInfo runningTaskInfoP0 = p0();
            return Build.VERSION.SDK_INT >= 29 ? q2.s(runningTaskInfoP0.baseIntent) : runningTaskInfoP0.numActivities == 1 && this.K.n().equals(runningTaskInfoP0.baseActivity.getPackageName());
        } catch (Throwable th) {
            f.v.d(th);
            return false;
        }
    }

    boolean H0(String str) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        try {
            try {
                if (this.c0 == null) {
                    this.c0 = XposedHelpers.findMethodExact(m(), "getPackageProcessState", new Class[]{String.class, String.class});
                }
                int iIntValue = ((Integer) this.c0.invoke(v(), str, "android")).intValue();
                if (iIntValue >= 0) {
                    if (iIntValue <= s0()) {
                        return true;
                    }
                }
                return false;
            } catch (Throwable unused) {
                return true;
            }
        } catch (Throwable unused2) {
            return ((Integer) XposedHelpers.callMethod(j0(), "getPackageImportance", new Object[]{str})).intValue() <= 400;
        }
    }

    boolean I0(String str, int i2) {
        return this.O.b(str, i2);
    }

    boolean J0(int i2) {
        if (Build.VERSION.SDK_INT < 26) {
            return true;
        }
        try {
            try {
                if (this.d0 == null) {
                    this.d0 = XposedHelpers.findMethodExact(m(), "getUidProcessState", new Class[]{Integer.TYPE, String.class});
                }
                int iIntValue = ((Integer) this.d0.invoke(v(), Integer.valueOf(i2), "android")).intValue();
                if (iIntValue >= 0) {
                    if (iIntValue <= s0()) {
                        return true;
                    }
                }
                return false;
            } catch (Throwable unused) {
                return ((Integer) XposedHelpers.callMethod(j0(), "getUidImportance", new Object[]{Integer.valueOf(i2)})).intValue() <= 400;
            }
        } catch (Throwable unused2) {
            return true;
        }
    }

    void P0(int i2) {
        List<ActivityManager.RunningTaskInfo> listW0 = w0();
        List<String> listM = this.K.m();
        int size = listW0.size();
        if (size <= 1) {
            return;
        }
        if (i2 < 0 || i2 > 3) {
            i2 = 0;
        }
        Bundle bundleD = com.jozein.xedgepro.xposed.a.d(this, i2);
        o0.a aVarF = f.o0.f(this.L);
        for (int i3 = 1; i3 < size; i3++) {
            ActivityManager.RunningTaskInfo runningTaskInfo = listW0.get(i3);
            if (!listM.contains(runningTaskInfo.baseActivity.getPackageName()) && E0(aVarF, runningTaskInfo) && X0(runningTaskInfo, bundleD)) {
                return;
            }
        }
    }

    boolean S0(int i2) {
        try {
            return ((Boolean) r("moveActivityTaskToBack", com.jozein.xedgepro.xposed.b.a(v1(i2)), Boolean.TRUE)).booleanValue();
        } catch (Throwable th) {
            f.v.d(th);
            return false;
        }
    }

    void U(c0.c cVar) {
        int[] iArrE = f.o0.e(this.L);
        if (f.o0.k(iArrE)) {
            this.O.d(cVar, this.Q);
        } else {
            this.O.f(cVar, iArrE);
        }
    }

    Object V(int i2) {
        Object objInvoke;
        Object objInvoke2;
        Object objInvoke3;
        int i3 = Build.VERSION.SDK_INT;
        if (i3 >= 29) {
            Object objT0 = t0();
            if (this.h0 == null) {
                Class<?> cls = objT0.getClass();
                Class cls2 = Integer.TYPE;
                this.h0 = XposedHelpers.findMethodExact(cls, "anyTaskForId", new Class[]{cls2, cls2});
            }
            synchronized (y0()) {
                objInvoke3 = this.h0.invoke(objT0, Integer.valueOf(i2), 0);
            }
            return objInvoke3;
        }
        if (i3 < 21) {
            if (this.e0 == null) {
                this.e0 = XposedHelpers.findMethodExact(m(), "recentTaskForIdLocked", new Class[]{Integer.TYPE});
            }
            synchronized (y0()) {
                objInvoke = this.e0.invoke(v(), Integer.valueOf(i2));
            }
            return objInvoke;
        }
        if (this.f0 == null) {
            this.f0 = XposedHelpers.getObjectField(v(), "mStackSupervisor");
        }
        if (this.h0 == null) {
            this.h0 = p2.b(this.f0.getClass(), "anyTaskForIdLocked", Integer.TYPE);
        }
        synchronized (y0()) {
            objInvoke2 = this.h0.invoke(this.f0, Integer.valueOf(i2));
        }
        return objInvoke2;
    }

    void W(Intent intent, int i2) {
        X(intent, false, false, i2);
    }

    void W0(int i2, Bundle bundle) {
        if (this.J.d()) {
            try {
                Object objV = V(i2);
                int iB = p3.b(objV);
                if (iB != 0) {
                    U0(objV, bundle, iB);
                    return;
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
        try {
            j0().moveTaskToFront(i2, 2, bundle);
        } catch (Throwable th2) {
            f.v.d(th2);
        }
    }

    void X(Intent intent, boolean z, boolean z2, int i2) throws Throwable {
        try {
            Z(intent, z, z2, i2);
        } catch (InvocationTargetException e2) {
            throw e2.getTargetException();
        } catch (Throwable th) {
            f.v.c(th.toString());
            if (z) {
                Context context = this.L;
                UserHandle userHandleQ = f.o0.q(i2);
                if (z2) {
                    context.sendStickyOrderedBroadcastAsUser(intent, userHandleQ, null, null, -1, null, null);
                    return;
                } else {
                    context.sendOrderedBroadcastAsUser(intent, userHandleQ, null, null, null, -1, null, null);
                    return;
                }
            }
            Context context2 = this.L;
            UserHandle userHandleQ2 = f.o0.q(i2);
            if (z2) {
                context2.sendStickyBroadcastAsUser(intent, userHandleQ2);
            } else {
                context2.sendBroadcastAsUser(intent, userHandleQ2);
            }
        }
    }

    boolean X0(ActivityManager.RunningTaskInfo runningTaskInfo, Bundle bundle) {
        if (f3.e(runningTaskInfo)) {
            return false;
        }
        if (this.J.d()) {
            try {
                if (Build.VERSION.SDK_INT >= 29) {
                    int iA = f3.a(runningTaskInfo);
                    if (iA != 0) {
                        T0(runningTaskInfo, bundle, iA);
                        return true;
                    }
                } else {
                    Object objV = V(runningTaskInfo.id);
                    int iB = p3.b(objV);
                    if (iB != 0) {
                        U0(objV, bundle, iB);
                        return true;
                    }
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
        try {
            j0().moveTaskToFront(runningTaskInfo.id, 2, bundle);
            return true;
        } catch (Throwable th2) {
            f.v.d(th2);
            return false;
        }
    }

    void Y(Intent intent) {
        X(intent, false, false, this.Q);
    }

    void Y0(int i2) {
        if (i2 < 0 || i2 > 3) {
            i2 = 1;
        }
        R0(false, com.jozein.xedgepro.xposed.a.d(this, i2));
    }

    int a0() {
        boolean z;
        ActivityManager activityManagerJ0 = j0();
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManagerJ0.getMemoryInfo(memoryInfo);
        try {
            r("killAllBackgroundProcesses", new Object[0]);
            z = true;
        } catch (Throwable th) {
            f.v.d(th);
            z = false;
        }
        if (!z) {
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : activityManagerJ0.getRunningAppProcesses()) {
                int i2 = runningAppProcessInfo.uid;
                if (i2 >= 10000 && i2 <= 19999 && runningAppProcessInfo.importance > 200 && !runningAppProcessInfo.processName.equals("com.android.systemui") && !runningAppProcessInfo.processName.equals("android") && runningAppProcessInfo.pkgList != null) {
                    int iH = f.o0.h(runningAppProcessInfo.uid);
                    for (String str : runningAppProcessInfo.pkgList) {
                        K0(str, iH);
                    }
                }
            }
        }
        long j2 = memoryInfo.availMem;
        activityManagerJ0.getMemoryInfo(memoryInfo);
        long j3 = memoryInfo.availMem - j2;
        return (int) (j3 > 0 ? j3 / 1048576 : 0L);
    }

    void d0(Object obj) {
        if (obj == null || q2.s(com.jozein.xedgepro.xposed.b.c(obj))) {
            return;
        }
        Object objA = com.jozein.xedgepro.xposed.b.a(obj);
        int i2 = Build.VERSION.SDK_INT;
        if (i2 >= 24) {
            r("finishActivity", objA, 0, new Intent(), 0);
        } else if (i2 >= 21) {
            r("finishActivity", objA, 0, new Intent(), Boolean.FALSE);
        } else {
            r("finishActivity", objA, 0, new Intent());
        }
    }

    void e0() {
        Object objN0 = n0();
        if (objN0 != null) {
            d0(objN0);
        } else {
            f0(p0());
        }
    }

    void f0(ActivityManager.RunningTaskInfo runningTaskInfo) throws IllegalAccessException, InvocationTargetException {
        if (runningTaskInfo.numActivities != 1) {
            try {
                Object objV1 = v1(runningTaskInfo.id);
                if (objV1 != null) {
                    d0(objV1);
                    return;
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
        j1(runningTaskInfo.id);
    }

    void g0() {
        Object objN0 = n0();
        if (objN0 != null) {
            h0(objN0);
        } else {
            ActivityManager.RunningTaskInfo runningTaskInfoP0 = p0();
            i0(runningTaskInfoP0.topActivity.getPackageName(), A0(runningTaskInfoP0));
        }
    }

    void g1(int i2) {
        if (i2 < 0 || i2 > 3) {
            i2 = 0;
        }
        R0(true, com.jozein.xedgepro.xposed.a.d(this, i2));
    }

    void h0(Object obj) {
        String strD = com.jozein.xedgepro.xposed.b.d(obj);
        if (strD != null) {
            i0(strD, com.jozein.xedgepro.xposed.b.g(obj));
        }
    }

    void i0(String str, int i2) {
        if (this.K.j(str)) {
            ActivityManager activityManagerJ0 = j0();
            if (i2 != 0) {
                XposedHelpers.callMethod(activityManagerJ0, "forceStopPackageAsUser", new Object[]{str, Integer.valueOf(i2)});
            } else {
                XposedHelpers.callMethod(activityManagerJ0, "forceStopPackage", new Object[]{str});
            }
            Q0(str, i2);
            this.N.a(str, i2);
        }
    }

    boolean i1() {
        List<d> listC = this.O.c();
        for (d dVar : listC) {
            this.K.v(dVar.z, dVar.A, false);
        }
        return listC.size() > 0;
    }

    ActivityManager j0() {
        if (this.R == null) {
            this.R = (ActivityManager) this.L.getSystemService("activity");
        }
        return this.R;
    }

    void j1(int i2) throws IllegalAccessException, InvocationTargetException {
        if (this.X == null) {
            this.X = i("removeTask");
        }
        if (Build.VERSION.SDK_INT >= 21) {
            this.X.invoke(v(), Integer.valueOf(i2));
        } else {
            this.X.invoke(v(), Integer.valueOf(i2), 0);
        }
    }

    List<j> l0() {
        List<ActivityManager.RunningTaskInfo> listW0 = w0();
        List<String> listM = this.K.m();
        int size = listW0.size();
        ArrayList arrayList = new ArrayList(size);
        o0.a aVarF = f.o0.f(this.L);
        for (int i2 = 1; i2 < size; i2++) {
            ActivityManager.RunningTaskInfo runningTaskInfo = listW0.get(i2);
            if (!listM.contains(runningTaskInfo.baseActivity.getPackageName())) {
                int iA0 = A0(runningTaskInfo);
                if (aVarF.a(iA0)) {
                    try {
                        arrayList.add(new j(this, runningTaskInfo, iA0));
                    } catch (Throwable th) {
                        f.v.c(th.toString());
                    }
                }
            }
        }
        return arrayList;
    }

    boolean l1(String str, int i2, boolean z) {
        try {
        } catch (Throwable th) {
            f.v.d(th);
        }
        if (!z) {
            this.O.i(str, i2);
            if (this.K.r(str, i2)) {
                return true;
            }
            this.K.u(str, i2, true);
            return true;
        }
        if (!I0(str, i2) && this.K.r(str, i2)) {
            if (this.K.i(str, i2)) {
                this.K.u(str, i2, false);
                if (this.K.r(str, i2)) {
                    return false;
                }
                this.O.i(str, i2);
            }
            return false;
        }
        return true;
    }

    Context m0() {
        return this.L;
    }

    void m1(int i2) {
        if (i2 != this.Q) {
            this.Q = i2;
        }
    }

    void n1() {
        this.T = true;
        this.K.w();
    }

    String o0() {
        try {
            Object objN0 = n0();
            if (objN0 != null) {
                String strD = com.jozein.xedgepro.xposed.b.d(objN0);
                if (strD != null) {
                    return strD;
                }
            }
        } catch (Throwable unused) {
        }
        return p0().topActivity.getPackageName();
    }

    void o1(Intent intent, Bundle bundle, int i2, boolean z) {
        String strF = f.c0.f(intent);
        if (strF == null || this.K.r(strF, i2)) {
            q1(intent, z, bundle, i2);
            return;
        }
        if (f.c0.x(intent, 0, i2) != null) {
            try {
                q1(intent, z, bundle, i2);
                return;
            } catch (Throwable unused) {
            }
        }
        u1(strF, intent, bundle, i2);
    }

    ActivityManager.RunningTaskInfo p0() {
        if (!this.J.d()) {
            return j0().getRunningTasks(1).get(0);
        }
        List<ActivityManager.RunningTaskInfo> listV0 = v0();
        List<ActivityManager.RunningTaskInfo> listR0 = r0(listV0);
        return listR0.isEmpty() ? listV0.get(0) : listR0.get(0);
    }

    void p1(Intent intent, Bundle bundle, int i2, int i3) {
        intent.addFlags(805306368);
        if (i3 == 0 || i3 == -1) {
            q1(intent, true, bundle, i2);
            return;
        }
        Bundle bundleF = com.jozein.xedgepro.xposed.a.f(bundle, i3);
        Context contextCreateDisplayContext = this.L.createDisplayContext(((DisplayManager) this.L.getSystemService("display")).getDisplay(i3));
        if (i2 == 0) {
            contextCreateDisplayContext.startActivity(intent, bundleF);
        } else {
            XposedHelpers.callMethod(contextCreateDisplayContext, "startActivityAsUser", new Object[]{intent, bundleF, f.o0.q(i2)});
        }
    }

    f q0() {
        Object objV1;
        Object objN0 = n0();
        if (objN0 != null) {
            return f.D(this, objN0);
        }
        ActivityManager.RunningTaskInfo runningTaskInfoP0 = p0();
        if ((Build.VERSION.SDK_INT < 29 || runningTaskInfoP0.numActivities != 1) && (objV1 = v1(runningTaskInfoP0.id)) != null) {
            return f.D(this, objV1);
        }
        j jVar = new j(this, runningTaskInfoP0, A0(runningTaskInfoP0));
        return jVar.t();
    }

    void q1(Intent intent, boolean z, Bundle bundle, int i2) {
        Objects.requireNonNull(intent, "intent == null");
        if (this.J.d()) {
            bundle = com.jozein.xedgepro.xposed.a.e(bundle);
        }
        intent.addFlags(z ? 805306368 : 268435456);
        try {
            r1(intent, z ? 1 : 0, bundle, i2);
        } catch (NoSuchMethodError unused) {
            Context context = this.L;
            if (i2 == 0) {
                context.startActivity(intent, bundle);
            } else {
                XposedHelpers.callMethod(context, "startActivityAsUser", new Object[]{intent, bundle, f.o0.q(i2)});
            }
        }
    }

    void s1(String str, Bundle bundle, int i2) {
        if (this.K.r(str, i2)) {
            q1(f.c0.g(str, i2), true, bundle, i2);
            return;
        }
        try {
            q1(f.c0.g(str, i2), true, bundle, i2);
        } catch (Throwable unused) {
            u1(str, null, bundle, i2);
        }
    }

    void t1(String str, int i2) {
        try {
            q1(new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.fromParts("package", str, null)).putExtra("userId", i2), false, null, i2);
        } catch (Throwable th) {
            f.v.c(th.toString());
            if (i2 != this.Q) {
                try {
                    q1(new Intent("miui.intent.action.APP_MANAGER_APPLICATION_DETAIL").putExtra("package_name", str).putExtra("miui.intent.extra.USER_ID", i2), false, null, this.Q);
                } catch (Throwable th2) {
                    f.v.c(th2.toString());
                }
            }
        }
    }

    List<f> u0() {
        int[] iArrE = f.o0.e(this.L);
        ArrayList arrayList = new ArrayList();
        if (f.o0.k(iArrE)) {
            this.O.e(arrayList, this.Q);
        } else {
            this.O.g(arrayList, iArrE);
        }
        int size = arrayList.size();
        ArrayList arrayList2 = new ArrayList(size);
        for (int i2 = 0; i2 < size; i2++) {
            d dVar = (d) arrayList.get(i2);
            try {
                arrayList2.add(new f(this, dVar.z, dVar.A));
            } catch (Throwable unused) {
            }
        }
        return arrayList2;
    }

    List<ActivityManager.RunningTaskInfo> v0() {
        return j0().getRunningTasks(100);
    }

    List<ActivityManager.RunningTaskInfo> w0() {
        return r0(v0());
    }

    @Override // com.jozein.xedgepro.xposed.j2
    protected Object x() {
        return g3.i();
    }

    int z0(int i2) {
        int iX0 = x0(i2);
        if (iX0 >= 0) {
            return iX0;
        }
        int iC0 = c0(i2);
        if (iC0 < 0) {
            return this.Q;
        }
        h1(i2, iC0);
        return iC0;
    }
}
