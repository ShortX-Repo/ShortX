package com.jozein.xedgepro.xposed;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import f.f0;
import java.lang.reflect.Field;

/* JADX INFO: loaded from: classes.dex */
class n0 extends j2 {
    private static final String h0;
    private static final String i0;
    private static final boolean j0;
    private final w1 H;
    private final m2 I;
    private final n3 J;
    private Handler K;
    private boolean L;
    private boolean M;
    private boolean N;
    private boolean O;
    private boolean P;
    private boolean Q;
    private boolean R;
    private boolean S;
    private int T;
    private int U;
    private int V;
    private int W;
    private volatile boolean X;
    private int Y;
    private boolean Z;
    private boolean a0;
    private int b0;
    private int c0;
    private int d0;
    private Field e0;
    private int f0;
    private final Runnable g0;

    class a extends XC_MethodHook {
        a() {
        }

        protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            n0.this.I.A(methodHookParam.thisObject);
            if (n0.this.Z) {
                return;
            }
            n0.this.A0(n0.this.c0());
        }
    }

    class b extends XC_MethodHook {
        b() {
        }

        protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            n0.this.I.A(methodHookParam.thisObject);
            if (n0.this.Z && ((Boolean) methodHookParam.getResult()).booleanValue() && ((Integer) methodHookParam.args[1]).intValue() == 0) {
                n0.this.A0(n0.this.c0());
            }
        }
    }

    class c extends XC_MethodHook {
        c() {
        }

        protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            n0.this.I.A(methodHookParam.thisObject);
            if (n0.this.Z && ((Boolean) methodHookParam.getResult()).booleanValue() && ((Integer) methodHookParam.args[0]).intValue() == 0) {
                n0.this.A0(n0.this.c0());
            }
        }
    }

    class d extends XC_MethodHook {
        d() {
        }

        protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            n0.this.I.A(methodHookParam.thisObject);
            n0.this.A0(XposedHelpers.getIntField(methodHookParam.thisObject, "mRotation"));
        }
    }

    class e extends XC_MethodHook {
        e() {
        }

        protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            n0.this.A0(((Integer) methodHookParam.args[0]).intValue());
        }
    }

    class f extends XC_MethodHook {
        f() {
        }

        protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            int[] iArr = (int[]) n0.this.u("mNavigationBarWidthForRotation");
            int[] iArr2 = (int[]) n0.this.u("mNavigationBarHeightForRotation");
            boolean z = false;
            if (!n0.this.X ? iArr2[n0.this.U] == 0 || iArr[n0.this.U] == 0 : iArr2[n0.this.T] == 0 || iArr[n0.this.T] == 0) {
                z = true;
            }
            if (n0.this.O != z) {
                n0.this.O = z;
                n0 n0Var = n0.this;
                n0Var.y0(n0Var.Q);
            }
        }
    }

    class g extends f0.d {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final IBinder f172a = new w3();

        g() {
        }

        @Override // f.f0.d
        public Object a(String str, Object[] objArr) {
            str.hashCode();
            if (str.equals("asBinder")) {
                return this.f172a;
            }
            if (str.equals("onRotationChanged")) {
                n0.this.A0(((Integer) objArr[0]).intValue());
                return null;
            }
            f.v.c("IRotationWatcher." + str);
            return null;
        }
    }

    class h extends XC_MethodHook {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ String f174a;

        h(String str) {
            this.f174a = str;
        }

        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            if ("qemu.hw.mainkeys".equals(methodHookParam.args[0])) {
                methodHookParam.setResult(this.f174a);
            }
        }
    }

    class i extends XC_MethodHook {
        i() {
        }

        protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            int iIntValue;
            int iW;
            if ((n0.this.L || (n0.this.M && !n0.this.N)) && (iIntValue = ((Integer) methodHookParam.getResult()).intValue()) != (iW = n0.this.W(iIntValue))) {
                methodHookParam.setResult(Integer.valueOf(iW));
            }
        }
    }

    class j extends XC_MethodHook {
        j() {
        }

        protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            int iIntValue;
            int iX;
            if ((n0.this.L || (n0.this.M && !n0.this.N)) && (iIntValue = ((Integer) methodHookParam.getResult()).intValue()) != (iX = n0.this.X(iIntValue))) {
                methodHookParam.setResult(Integer.valueOf(iX));
            }
        }
    }

    class k extends XC_MethodHook {
        k() {
        }

        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            if (n0.this.L) {
                methodHookParam.setResult(Boolean.TRUE);
            }
        }
    }

    class l extends XC_MethodHook {
        l() {
        }

        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            if (n0.this.M) {
                methodHookParam.setResult(Boolean.TRUE);
            }
        }
    }

    class m extends XC_MethodHook {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        Field f180a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        Field f181b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        Field f182c;

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        Field f183d;

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        final /* synthetic */ Class f184e;

        m(Class cls) {
            this.f184e = cls;
        }

        protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            try {
                if (this.f180a == null) {
                    this.f180a = XposedHelpers.findField(this.f184e, "mDisplayContent");
                }
                if (this.f180a.get(methodHookParam.thisObject) != n0.this.d0().G()) {
                    return;
                }
                if (this.f181b == null) {
                    this.f181b = XposedHelpers.findField(this.f184e, "mSource");
                }
                Object obj = this.f181b.get(methodHookParam.thisObject);
                if (this.f183d == null) {
                    this.f183d = XposedHelpers.findField(obj.getClass(), "mVisible");
                }
                if (this.f182c == null) {
                    this.f182c = XposedHelpers.findField(this.f181b.getType(), "mType");
                }
                int i = this.f182c.getInt(obj);
                if (i == 0) {
                    n0.this.B0(this.f183d.getBoolean(obj));
                } else if (i == 1) {
                    n0.this.y0(this.f183d.getBoolean(obj));
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
    }

    class n extends XC_MethodHook {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ Field f186a;

        n(Field field) {
            this.f186a = field;
        }

        protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            try {
                n0.this.C0((String) this.f186a.get(methodHookParam.thisObject), methodHookParam.args[0] != null);
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
    }

    class o extends XC_MethodHook {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ Field f188a;

        o(Field field) {
            this.f188a = field;
        }

        protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            if (((Boolean) methodHookParam.getResult()).booleanValue()) {
                try {
                    n0.this.C0((String) this.f188a.get(methodHookParam.thisObject), ((Boolean) methodHookParam.args[0]).booleanValue());
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
        }
    }

    class p extends XC_MethodHook {
        p() {
        }

        protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            n0.this.Z = true;
            n0.this.A0(((Integer) methodHookParam.args[0]).intValue());
        }
    }

    private class q extends XC_MethodHook {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        int f191a;

        private q() {
            this.f191a = -1;
        }

        /* synthetic */ q(n0 n0Var, h hVar) {
            this();
        }

        protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            if (this.f191a != -1) {
                try {
                    Object objU = n0.this.u("mFocusedWindow");
                    if (objU == null) {
                        return;
                    }
                    XposedHelpers.setIntField(objU, "mSystemUiVisibility", this.f191a);
                    this.f191a = -1;
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
        }

        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            int intField;
            int iW;
            this.f191a = -1;
            if (n0.this.L) {
                try {
                    Object objU = n0.this.u("mFocusedWindow");
                    if (objU == null || (intField = XposedHelpers.getIntField(objU, "mSystemUiVisibility")) == (iW = n0.this.W(intField))) {
                        return;
                    }
                    XposedHelpers.setIntField(objU, "mSystemUiVisibility", iW);
                    this.f191a = intField;
                } catch (Throwable th) {
                    f.v.d(th);
                    this.f191a = -1;
                }
            }
        }
    }

    static {
        int i2 = Build.VERSION.SDK_INT;
        h0 = i2 >= 24 ? "mNavigationBarWidthForRotationDefault" : "mNavigationBarWidthForRotation";
        i0 = i2 >= 24 ? "mNavigationBarHeightForRotationDefault" : "mNavigationBarHeightForRotation";
        j0 = i2 >= 30;
    }

    /* JADX WARN: Removed duplicated region for block: B:74:0x01db A[Catch: all -> 0x01ee, TRY_LEAVE, TryCatch #7 {all -> 0x01ee, blocks: (B:72:0x01d7, B:74:0x01db), top: B:92:0x01d7 }] */
    /* JADX WARN: Removed duplicated region for block: B:94:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    n0(java.lang.Class<?> r10, java.lang.ClassLoader r11, com.jozein.xedgepro.xposed.w1 r12, a.p r13) {
        /*
            Method dump skipped, instruction units count: 499
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jozein.xedgepro.xposed.n0.<init>(java.lang.Class, java.lang.ClassLoader, com.jozein.xedgepro.xposed.w1, a.p):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void A0(int i2) {
        this.Y = i2;
        boolean z = i2 == this.U || i2 == this.W;
        if (this.X != z) {
            this.X = z;
            this.H.j3(z);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void B0(boolean z) {
        if (this.P != z) {
            this.P = z;
            this.H.o3(z);
            boolean z2 = (this.R || z) ? false : true;
            if (this.S != z2) {
                this.S = z2;
                this.H.g3(z2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void C0(String str, boolean z) {
        if (str.endsWith("StatusBar")) {
            B0(z);
            return;
        }
        if (str.endsWith("NavigationBar")) {
            y0(z);
            return;
        }
        f.v.c("Unknown tag: " + str);
    }

    private void V() {
        try {
            this.I.z("watchRotation", new g(), 0);
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int W(int i2) {
        if (this.L) {
            if (Build.VERSION.SDK_INT >= 21) {
                i2 = (i2 | 4096) & (-1073741825);
            }
            i2 = (i2 | 1028) & (-257);
        }
        if (!this.M || this.N) {
            return i2;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            i2 = (i2 | 4096) & Integer.MAX_VALUE;
        }
        return (i2 | 514) & (-257);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int X(int i2) {
        if (this.L) {
            i2 = (i2 | 1024) & (-67110913);
        }
        return (!this.M || this.N) ? i2 : i2 & (-134217729);
    }

    private int a0(a.p pVar) {
        return pVar.J() ? pVar.u(14) : this.J.a(0);
    }

    private Resources b0() {
        Context contextT1 = this.H.T1();
        return contextT1 != null ? contextT1.getResources() : Resources.getSystem();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int c0() {
        try {
            int iO = d0().O();
            this.Y = iO;
            return iO;
        } catch (Throwable unused) {
            return this.Y;
        }
    }

    private void f0() {
        try {
            Resources resourcesB0 = b0();
            int identifier = resourcesB0.getIdentifier("navigation_bar_width", "dimen", "android");
            int identifier2 = resourcesB0.getIdentifier("navigation_bar_height", "dimen", "android");
            int identifier3 = resourcesB0.getIdentifier("navigation_bar_height_landscape", "dimen", "android");
            this.b0 = (int) resourcesB0.getDimension(identifier);
            this.c0 = (int) resourcesB0.getDimension(identifier2);
            this.d0 = (int) resourcesB0.getDimension(identifier3);
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    private void g0() {
        f0();
        try {
            if (Build.VERSION.SDK_INT >= 29) {
                Resources resourcesB0 = b0();
                int identifier = resourcesB0.getIdentifier("config_reverseDefaultRotation", "boolean", "android");
                boolean z = identifier > 0 && resourcesB0.getBoolean(identifier);
                Point pointI = this.I.I(new Point());
                if (pointI.x > pointI.y) {
                    this.U = 0;
                    this.W = 2;
                    if (z) {
                        this.T = 1;
                        this.V = 3;
                    } else {
                        this.T = 3;
                        this.V = 1;
                    }
                } else {
                    this.T = 0;
                    this.V = 2;
                    if (z) {
                        this.U = 3;
                        this.W = 1;
                    } else {
                        this.U = 1;
                        this.W = 3;
                    }
                }
            } else {
                int iT = t("mPortraitRotation");
                int iT2 = t("mLandscapeRotation");
                if (iT == 0 && iT2 == 0) {
                    return;
                }
                this.T = iT;
                this.U = iT2;
                this.V = t("mUpsideDownRotation");
                int iT3 = t("mSeascapeRotation");
                this.W = iT3;
                this.X = this.U == 0 || iT3 == 0;
            }
            this.a0 = true;
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void m0() {
        int i2 = this.f0;
        this.f0 = -1;
        if (i2 < 0 || i2 == c0()) {
            return;
        }
        try {
            d0().D(-1);
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    private void s0(Object obj, boolean z) {
        if (obj == null) {
            return;
        }
        if (Build.VERSION.SDK_INT < 31) {
            XposedHelpers.callMethod(obj, z ? "showLw" : "hideLw", new Object[]{Boolean.TRUE});
            z0();
        } else {
            String str = z ? "show" : "hide";
            Boolean bool = Boolean.TRUE;
            XposedHelpers.callMethod(obj, str, new Object[]{bool, bool});
        }
    }

    private void u0(boolean z, boolean z2) {
        s0(Y(z), !z2);
    }

    private void w0(int i2, int i3, int i4) {
        int[] iArr;
        Object objU;
        if (Build.VERSION.SDK_INT >= 29) {
            Object objH = d0().H();
            iArr = (int[]) XposedHelpers.getObjectField(objH, h0);
            objU = XposedHelpers.getObjectField(objH, i0);
        } else {
            iArr = (int[]) u(h0);
            objU = u(i0);
        }
        int[] iArr2 = (int[]) objU;
        int i5 = this.T;
        if (iArr2[i5] == i3) {
            int i6 = this.U;
            if (iArr2[i6] == i4 && iArr[i5] == i2 && iArr[i6] == i2) {
                return;
            }
        }
        int i7 = this.V;
        iArr2[i7] = i3;
        iArr2[i5] = i3;
        int i8 = this.U;
        int i9 = this.W;
        iArr2[i9] = i4;
        iArr2[i8] = i4;
        iArr[i9] = i2;
        iArr[i8] = i2;
        iArr[i7] = i2;
        iArr[i5] = i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void y0(boolean z) {
        this.Q = z;
        boolean z2 = z && !this.O;
        if (this.R != z2) {
            this.R = z2;
            this.H.i3(z2);
            boolean z3 = (this.P || z2) ? false : true;
            if (this.S != z3) {
                this.S = z3;
                this.H.g3(z3);
            }
        }
    }

    Object Y(boolean z) {
        Object objH = d0().H();
        String str = z ? "mStatusBar" : "mNavigationBar";
        Object objectField = XposedHelpers.getObjectField(objH, str);
        if (objectField == null) {
            f.v.c(str + " not exists.");
        }
        return objectField;
    }

    Point Z() {
        try {
            Object objM = d0().M();
            if (objM != null) {
                Rect rectG = u3.g(objM, new Rect());
                return new Point((rectG.left + rectG.right) / 2, (rectG.top + rectG.bottom) / 2);
            }
        } catch (Throwable th) {
            f.v.d(th);
        }
        Point pointI = this.I.I(new Point());
        pointI.x /= 2;
        pointI.y /= 2;
        return pointI;
    }

    m2 d0() {
        return this.I;
    }

    void e0(Handler handler, Object obj) {
        this.K = handler;
        try {
            if (this.c0 == 0) {
                f0();
            }
            this.I.A(obj);
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    boolean h0() {
        return this.M;
    }

    boolean i0() {
        return this.L;
    }

    boolean j0() {
        return this.X;
    }

    boolean k0() {
        return this.R;
    }

    boolean l0() {
        return this.P;
    }

    void n0() {
        if (this.K == null) {
            this.K = this.H.U1();
        }
        this.I.U();
    }

    void o0() {
        g0();
    }

    void p0() {
        if (!this.a0) {
            g0();
        }
        if (Build.VERSION.SDK_INT > 27 || f.p0.p() != 1) {
            V();
        }
    }

    boolean q0() {
        try {
            if (Build.VERSION.SDK_INT < 29) {
                r("requestTransientBars", u("mStatusBar"));
                return true;
            }
        } catch (Throwable th) {
            f.v.d(th);
        }
        return false;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:15:0x0021  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    void r0(int r4) {
        /*
            r3 = this;
            int r0 = r3.c0()
            switch(r4) {
                case 1: goto L24;
                case 2: goto L21;
                case 3: goto L1e;
                case 4: goto L1b;
                case 5: goto L16;
                case 6: goto L13;
                case 7: goto L10;
                default: goto L7;
            }
        L7:
            int r4 = r3.T
            if (r0 == r4) goto L21
            int r1 = r3.V
            if (r0 != r1) goto L26
            goto L21
        L10:
            int r0 = r0 + 3
            goto L18
        L13:
            int r0 = r0 + 2
            goto L18
        L16:
            int r0 = r0 + 1
        L18:
            r4 = r0 & 3
            goto L26
        L1b:
            int r4 = r3.W
            goto L26
        L1e:
            int r4 = r3.V
            goto L26
        L21:
            int r4 = r3.U
            goto L26
        L24:
            int r4 = r3.T
        L26:
            com.jozein.xedgepro.xposed.m2 r0 = r3.d0()
            r0.D(r4)
            android.os.Handler r0 = r3.K
            java.lang.Runnable r1 = r3.g0
            r0.removeCallbacks(r1)
            r3.f0 = r4
            android.os.Handler r4 = r3.K
            java.lang.Runnable r0 = r3.g0
            r1 = 500(0x1f4, double:2.47E-321)
            r4.postDelayed(r0, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jozein.xedgepro.xposed.n0.r0(int):void");
    }

    void t0(boolean z) {
        if (this.N) {
            try {
                if (z) {
                    w0(0, 0, 0);
                } else {
                    w0(this.b0, this.c0, this.d0);
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
        this.M = z;
        if (j0) {
            u0(false, z);
        } else {
            z0();
        }
    }

    void v0(boolean z) {
        this.L = z;
        if (j0) {
            u0(true, z);
        } else {
            z0();
        }
    }

    @Override // com.jozein.xedgepro.xposed.j2
    protected Object x() {
        return this.H.v();
    }

    void x0() {
        int iU = this.H.b2().u(14);
        if (iU != this.J.a(0)) {
            this.J.c(0, iU);
        }
    }

    void z0() {
        r("updateRotation", Boolean.FALSE);
    }
}
