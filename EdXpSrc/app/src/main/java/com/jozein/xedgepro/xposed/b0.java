package com.jozein.xedgepro.xposed;

import a.b;
import a.p;
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import de.robv.android.xposed.XC_MethodHook;

/* JADX INFO: loaded from: classes.dex */
class b0 extends z implements p.b {
    private static final float n0 = -((float) Math.tan(0.8726646259971648d));
    private final MotionEvent.PointerProperties[] F;
    private final MotionEvent.PointerCoords[] G;
    private final a.p H;
    private final w1 I;
    private final m2 J;
    private final Point K;
    private float L;
    private float M;
    private float N;
    private float O;
    private float P;
    private long Q;
    private long R;
    private float S;
    private float T;
    private XC_MethodHook.MethodHookParam U;
    private XC_MethodHook.MethodHookParam V;
    private float W;
    private float X;
    private int Y;
    private int Z;
    private boolean a0;
    private boolean b0;
    private boolean c0;
    private float d0;
    private boolean e0;
    private a f0;
    private int g0;
    private float h0;
    private float i0;
    private boolean j0;
    private boolean k0;
    private b l0;
    private boolean m0;

    interface a {
        void b(float f2, float f3, long j);

        void c();

        void d(float f2, float f3, long j);

        void e(int i, float f2, float f3, long j);
    }

    interface b {
        void a(float f2, float f3);
    }

    b0(w1 w1Var, y0 y0Var, Handler handler) {
        super(y0Var, handler);
        this.F = new MotionEvent.PointerProperties[]{new MotionEvent.PointerProperties(), new MotionEvent.PointerProperties(), new MotionEvent.PointerProperties(), new MotionEvent.PointerProperties(), new MotionEvent.PointerProperties(), new MotionEvent.PointerProperties(), new MotionEvent.PointerProperties(), new MotionEvent.PointerProperties(), new MotionEvent.PointerProperties(), new MotionEvent.PointerProperties()};
        this.G = new MotionEvent.PointerCoords[]{new MotionEvent.PointerCoords(), new MotionEvent.PointerCoords(), new MotionEvent.PointerCoords(), new MotionEvent.PointerCoords(), new MotionEvent.PointerCoords(), new MotionEvent.PointerCoords(), new MotionEvent.PointerCoords(), new MotionEvent.PointerCoords(), new MotionEvent.PointerCoords(), new MotionEvent.PointerCoords()};
        this.Q = 500L;
        this.R = 300L;
        this.U = null;
        this.V = null;
        this.c0 = false;
        this.e0 = false;
        this.f0 = null;
        this.g0 = -1;
        this.h0 = 0.0f;
        this.i0 = 0.0f;
        this.j0 = false;
        this.k0 = false;
        this.l0 = null;
        this.m0 = false;
        this.I = w1Var;
        this.J = w1Var.g2();
        this.H = w1Var.b2();
        this.K = new Point();
    }

    private boolean A(MotionEvent motionEvent) {
        float fQ = q(this.W, this.X, motionEvent.getRawX(), motionEvent.getRawY());
        return fQ > this.P && Math.sqrt((double) this.d0) - Math.sqrt((double) fQ) < ((double) this.N);
    }

    private int B(float f2, float f3) {
        if (this.m0) {
            this.m0 = false;
            this.I.x4();
            return -2;
        }
        if (this.H.o(8) && this.I.v2((int) f2, (int) f3)) {
            return -1;
        }
        float f4 = this.S;
        if (f2 <= f4) {
            float f5 = this.T;
            if (f3 > f5) {
                int i = this.K.y;
                if (f3 < i - f5) {
                    float f6 = (i - (f5 * 2.0f)) / 3.0f;
                    float f7 = f3 - f5;
                    if (f7 < f6) {
                        return 4;
                    }
                    return f7 > f6 * 2.0f ? 6 : 5;
                }
            }
        } else {
            Point point = this.K;
            int i2 = point.x;
            if (f2 >= i2 - f4) {
                float f8 = this.T;
                if (f3 > f8) {
                    int i3 = point.y;
                    if (f3 < i3 - f8) {
                        float f9 = (i3 - (f8 * 2.0f)) / 3.0f;
                        float f10 = f3 - f8;
                        if (f10 < f9) {
                            return 7;
                        }
                        return f10 > f9 * 2.0f ? 9 : 8;
                    }
                }
            } else {
                float f11 = this.T;
                if (f3 <= f11) {
                    if (f2 > f4 && f2 < i2 - f4) {
                        float f12 = (i2 - (f4 * 2.0f)) / 3.0f;
                        float f13 = f2 - f4;
                        if (f13 < f12) {
                            return 10;
                        }
                        return f13 > f12 * 2.0f ? 12 : 11;
                    }
                } else if (f3 >= point.y - f11 && f2 > f4 && f2 < i2 - f4) {
                    float f14 = (i2 - (f4 * 2.0f)) / 3.0f;
                    float f15 = f2 - f4;
                    if (f15 < f14) {
                        return 13;
                    }
                    return f15 > f14 * 2.0f ? 15 : 14;
                }
            }
        }
        return -1;
    }

    private int C(float f2, float f3, float f4, float f5) {
        float f6 = f4 - f2;
        float f7 = f5 - f3;
        float f8 = f6 * f6;
        float f9 = f7 * f7;
        if (f8 + f9 < this.L) {
            return 0;
        }
        return f8 > f9 ? f6 < 0.0f ? 3 : 4 : f7 < 0.0f ? 5 : 6;
    }

    private XC_MethodHook.MethodHookParam D(XC_MethodHook.MethodHookParam methodHookParam) {
        if (z.E) {
            Object[] objArr = methodHookParam.args;
            MotionEvent motionEvent = (MotionEvent) objArr[0];
            objArr[0] = MotionEvent.obtain(motionEvent);
            motionEvent.recycle();
        }
        return methodHookParam;
    }

    private boolean E(XC_MethodHook.MethodHookParam methodHookParam) {
        if (!z.E) {
            return false;
        }
        g(D(methodHookParam));
        return true;
    }

    private boolean F(MotionEvent motionEvent) {
        p();
        motionEvent.recycle();
        return true;
    }

    private boolean G(XC_MethodHook.MethodHookParam methodHookParam, MotionEvent motionEvent) {
        this.Z = 0;
        this.a0 = false;
        this.c0 = false;
        if (this.e0) {
            this.e0 = false;
            return false;
        }
        if (this.I.t2() && this.H.o(33)) {
            return false;
        }
        z();
        this.W = motionEvent.getRawX();
        float rawY = motionEvent.getRawY();
        this.X = rawY;
        this.Y = B(this.W, rawY);
        this.d0 = 0.0f;
        if (this.I.q2() && this.H.F(this.Y)) {
            return false;
        }
        int i = this.Y;
        if (i != -2 && !this.H.H(i)) {
            return false;
        }
        this.b0 = true;
        this.c0 = z.E;
        this.U = D(methodHookParam);
        long jU = this.H.u(11);
        this.Q = jU;
        if (jU == 0) {
            this.Q = ViewConfiguration.getLongPressTimeout();
        }
        long jU2 = this.H.u(10);
        this.R = jU2;
        if (jU2 == 0) {
            this.R = ViewConfiguration.getDoubleTapTimeout();
        }
        m(this.Q);
        return true;
    }

    private boolean H(XC_MethodHook.MethodHookParam methodHookParam, MotionEvent motionEvent) {
        a.b bVarW = w(this.Z);
        if (bVarW.z == 0) {
            this.b0 = false;
            XC_MethodHook.MethodHookParam methodHookParam2 = this.U;
            if (methodHookParam2 != null) {
                g(methodHookParam2);
                this.U = null;
            }
            return E(methodHookParam);
        }
        motionEvent.recycle();
        XC_MethodHook.MethodHookParam methodHookParam3 = this.U;
        if (methodHookParam3 != null) {
            ((MotionEvent) methodHookParam3.args[0]).recycle();
            this.U = null;
        }
        int i = bVarW.z;
        if (i != 1 && i != 51 && i != 68 && i != 106) {
            if (i == 107) {
                bVarW = ((b.h3) bVarW).I[v(this.Z)];
            }
            N(bVarW, u(this.Z));
        }
        return true;
    }

    private boolean I(XC_MethodHook.MethodHookParam methodHookParam, MotionEvent motionEvent) {
        int iC = C(this.W, this.X, motionEvent.getRawX(), motionEvent.getRawY());
        int i = this.Z;
        if (i == 0) {
            if (iC != 0) {
                this.Z = iC;
                o();
                if (O(this.Z).z == 0) {
                    this.b0 = false;
                    XC_MethodHook.MethodHookParam methodHookParam2 = this.U;
                    if (methodHookParam2 != null) {
                        g(methodHookParam2);
                        this.U = null;
                    }
                    return E(methodHookParam);
                }
            }
        } else {
            if (i != iC) {
                return F(motionEvent);
            }
            float fQ = q(this.W, this.X, motionEvent.getRawX(), motionEvent.getRawY());
            if (fQ > this.d0) {
                this.d0 = fQ;
                if (fQ > this.P && (this.H.o(27) || O(this.Z).z == 107)) {
                    this.b0 = true;
                    this.a0 = true;
                    XC_MethodHook.MethodHookParam methodHookParam3 = this.U;
                    if (methodHookParam3 != null) {
                        ((MotionEvent) methodHookParam3.args[0]).recycle();
                        this.U = null;
                    }
                    s(w(this.Z), u(this.Z), this.Z, motionEvent.getEventTime());
                }
            }
        }
        motionEvent.recycle();
        return true;
    }

    private boolean J(XC_MethodHook.MethodHookParam methodHookParam, MotionEvent motionEvent) {
        if (this.Z != 0) {
            return F(motionEvent);
        }
        o();
        this.b0 = false;
        this.a0 = false;
        g(this.U);
        this.U = null;
        return E(methodHookParam);
    }

    private boolean K(XC_MethodHook.MethodHookParam methodHookParam, MotionEvent motionEvent) {
        n();
        MotionEvent motionEvent2 = (MotionEvent) this.V.args[0];
        if (q(this.W, this.X, motionEvent.getRawX(), motionEvent.getRawY()) < this.M) {
            long eventTime = motionEvent.getEventTime();
            long eventTime2 = eventTime - motionEvent2.getEventTime();
            if (eventTime2 < 40) {
                motionEvent2.recycle();
                this.V = null;
                XC_MethodHook.MethodHookParam methodHookParam2 = this.U;
                if (methodHookParam2 != null) {
                    ((MotionEvent) methodHookParam2.args[0]).recycle();
                    this.U = null;
                }
                return G(methodHookParam, motionEvent);
            }
            if (eventTime2 < this.R) {
                a.b bVarW = w(1);
                if (bVarW.z == 0) {
                    g(this.U);
                    g(this.V);
                    this.b0 = false;
                    this.a0 = false;
                    this.U = null;
                    this.V = null;
                    return false;
                }
                this.b0 = true;
                this.a0 = true;
                motionEvent.recycle();
                motionEvent2.recycle();
                this.V = null;
                XC_MethodHook.MethodHookParam methodHookParam3 = this.U;
                if (methodHookParam3 != null) {
                    ((MotionEvent) methodHookParam3.args[0]).recycle();
                    this.U = null;
                }
                s(bVarW, -1, 1, eventTime);
                return true;
            }
        }
        L();
        return G(methodHookParam, motionEvent);
    }

    private void L() {
        a.b bVarW = w(0);
        if (bVarW.z == 0) {
            g(this.U);
            g(this.V);
            this.b0 = false;
            this.a0 = false;
            this.U = null;
            this.V = null;
            return;
        }
        this.b0 = true;
        this.a0 = true;
        XC_MethodHook.MethodHookParam methodHookParam = this.U;
        if (methodHookParam != null) {
            ((MotionEvent) methodHookParam.args[0]).recycle();
            this.U = null;
        }
        XC_MethodHook.MethodHookParam methodHookParam2 = this.V;
        if (methodHookParam2 != null) {
            ((MotionEvent) methodHookParam2.args[0]).recycle();
            this.V = null;
        }
        N(bVarW, -1);
    }

    private boolean M(XC_MethodHook.MethodHookParam methodHookParam, MotionEvent motionEvent) {
        int iC = C(this.W, this.X, motionEvent.getRawX(), motionEvent.getRawY());
        int i = this.Z;
        if (i != 0) {
            if (i == iC && A(motionEvent)) {
                return H(methodHookParam, motionEvent);
            }
            this.b0 = false;
            motionEvent.recycle();
            XC_MethodHook.MethodHookParam methodHookParam2 = this.U;
            if (methodHookParam2 != null) {
                ((MotionEvent) methodHookParam2.args[0]).recycle();
                this.U = null;
            }
            return true;
        }
        o();
        if (iC != 0) {
            if (this.c0) {
                return E(methodHookParam);
            }
            return false;
        }
        if (motionEvent.getEventTime() - motionEvent.getDownTime() >= this.Q) {
            this.Z = 2;
            return H(methodHookParam, motionEvent);
        }
        if (O(1).z != 0) {
            this.V = D(methodHookParam);
            l(this.R);
            return true;
        }
        a.b bVarW = w(0);
        if (bVarW.z == 0) {
            g(this.U);
            this.U = null;
            this.b0 = false;
            return E(methodHookParam);
        }
        motionEvent.recycle();
        XC_MethodHook.MethodHookParam methodHookParam3 = this.U;
        if (methodHookParam3 != null) {
            ((MotionEvent) methodHookParam3.args[0]).recycle();
            this.U = null;
        }
        this.V = null;
        N(bVarW, -1);
        return true;
    }

    private void N(a.b bVar, int i) {
        if (bVar.z != 65537) {
            this.I.v3(bVar, i, null);
        } else {
            this.I.t4(this.W, this.X);
        }
    }

    private a.b O(int i) {
        return x(i, true);
    }

    private void T(XC_MethodHook.MethodHookParam methodHookParam, MotionEvent motionEvent) {
        int pointerCount = motionEvent.getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            this.F[i].id = motionEvent.getPointerId(i);
            this.F[i].toolType = 1;
            this.G[i].x = motionEvent.getX(i) - this.h0;
            this.G[i].y = motionEvent.getY(i) - this.i0;
        }
        methodHookParam.args[0] = MotionEvent.obtain(motionEvent.getDownTime(), motionEvent.getEventTime(), motionEvent.getAction(), pointerCount, this.F, this.G, motionEvent.getMetaState(), motionEvent.getButtonState(), motionEvent.getXPrecision(), motionEvent.getYPrecision(), motionEvent.getDeviceId(), 0, motionEvent.getSource(), 0);
        g(methodHookParam);
        b bVar = this.l0;
        if (bVar != null) {
            bVar.a(motionEvent.getRawX() - this.h0, motionEvent.getRawY() - this.i0);
        }
        motionEvent.recycle();
    }

    static float q(float f2, float f3, float f4, float f5) {
        float f6 = f4 - f2;
        float f7 = f5 - f3;
        return (f6 * f6) + (f7 * f7);
    }

    private void r() {
        if (this.j0 || this.l0 != null) {
            this.j0 = false;
            this.h0 = 0.0f;
            this.i0 = 0.0f;
            this.g0 = -1;
            this.I.w4(0L);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:30:0x0083  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x008d A[Catch: all -> 0x00a8, TryCatch #1 {all -> 0x00a8, blocks: (B:3:0x0003, B:21:0x004a, B:18:0x001f, B:20:0x0023, B:28:0x0076, B:32:0x0087, B:37:0x009b, B:34:0x008d, B:36:0x0096, B:23:0x0050, B:24:0x005a, B:25:0x005d, B:26:0x0066, B:27:0x006f, B:38:0x00a5), top: B:51:0x0003 }] */
    /* JADX WARN: Removed duplicated region for block: B:35:0x0093  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x0096 A[Catch: all -> 0x00a8, TryCatch #1 {all -> 0x00a8, blocks: (B:3:0x0003, B:21:0x004a, B:18:0x001f, B:20:0x0023, B:28:0x0076, B:32:0x0087, B:37:0x009b, B:34:0x008d, B:36:0x0096, B:23:0x0050, B:24:0x005a, B:25:0x005d, B:26:0x0066, B:27:0x006f, B:38:0x00a5), top: B:51:0x0003 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void s(a.b r14, int r15, int r16, long r17) {
        /*
            Method dump skipped, instruction units count: 210
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jozein.xedgepro.xposed.b0.s(a.b, int, int, long):void");
    }

    private void t(int i, MotionEvent motionEvent) {
        try {
            if (i == 1) {
                this.f0.b((int) motionEvent.getRawX(), (int) motionEvent.getRawY(), motionEvent.getEventTime());
            } else {
                if (i != 3) {
                    this.f0.d((int) motionEvent.getRawX(), (int) motionEvent.getRawY(), motionEvent.getEventTime());
                    return;
                }
                this.f0.c();
            }
            this.f0 = null;
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    private static int u(int i) {
        if (i == 3) {
            return 1;
        }
        if (i == 4) {
            return 0;
        }
        if (i != 5) {
            return i != 6 ? -1 : 2;
        }
        return 3;
    }

    private static int v(int i) {
        if (i == 3) {
            return 1;
        }
        if (i == 4) {
            return 2;
        }
        if (i != 5) {
            return i != 6 ? 0 : 4;
        }
        return 3;
    }

    private a.b w(int i) {
        return x(i, false);
    }

    private a.b x(int i, boolean z) {
        int i2 = this.Y;
        if (i2 == -2) {
            return i == 0 ? new b.f3() : a.b.e();
        }
        a.b bVarY4 = this.H.s(i2) ? this.I.y4(this.H.i(this.Y, i), z) : a.b.e();
        if (bVarY4.z == 0) {
            int iH = a.p.h(this.Y);
            if (this.H.s(iH)) {
                return this.I.y4(this.H.i(iH, i), z);
            }
        }
        return bVarY4;
    }

    private void z() {
        float fN = this.J.N();
        float f2 = fN * fN;
        this.L = 0.0225f * f2;
        float f3 = 0.09f * f2;
        this.M = f3;
        this.P = f3;
        this.N = fN * 0.3f;
        this.O = 0.0064f * f2;
        this.J.I(this.K);
        float fT = this.H.t(36, 0.12f);
        float fT2 = this.H.t(36, fT);
        this.S = fT * fN;
        this.T = fT2 * fN;
        float fT3 = this.H.t(38, 0.3f);
        this.P = fT3 * fT3 * f2;
    }

    void P(float f2, float f3, b bVar) {
        this.g0 = (((int) f2) << 16) | ((int) f3);
        this.k0 = false;
        this.l0 = bVar;
    }

    void Q(boolean z) {
        this.m0 = z;
    }

    void R() {
        this.e0 = true;
    }

    void S() {
        this.g0 = -1;
        this.k0 = true;
    }

    /* JADX WARN: Removed duplicated region for block: B:30:0x0072  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x00b1  */
    @Override // com.jozein.xedgepro.xposed.z
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected boolean d(de.robv.android.xposed.XC_MethodHook.MethodHookParam r6, android.view.MotionEvent r7) {
        /*
            Method dump skipped, instruction units count: 258
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jozein.xedgepro.xposed.b0.d(de.robv.android.xposed.XC_MethodHook$MethodHookParam, android.view.MotionEvent):boolean");
    }

    @Override // com.jozein.xedgepro.xposed.z
    protected void e() {
        L();
    }

    @Override // com.jozein.xedgepro.xposed.z
    protected void f() {
        a.b bVarW = w(2);
        if (bVarW.z == 0) {
            this.b0 = false;
            g(this.U);
            this.U = null;
            return;
        }
        this.a0 = true;
        this.b0 = true;
        XC_MethodHook.MethodHookParam methodHookParam = this.U;
        if (methodHookParam != null) {
            ((MotionEvent) methodHookParam.args[0]).recycle();
            this.U = null;
        }
        s(bVarW, -1, 2, SystemClock.uptimeMillis());
    }

    void p() {
        this.a0 = true;
        this.g0 = -1;
        if (this.Z == 0) {
            o();
        }
        r();
        try {
            a aVar = this.f0;
            if (aVar != null) {
                aVar.c();
            }
            XC_MethodHook.MethodHookParam methodHookParam = this.U;
            if (methodHookParam != null) {
                ((MotionEvent) methodHookParam.args[0]).recycle();
            }
        } catch (Throwable th) {
            f.v.d(th);
        }
        this.f0 = null;
        this.U = null;
    }

    Point y() {
        float fN = this.J.N();
        float fT = this.H.t(36, 0.12f);
        return new Point((int) (fT * fN), (int) (this.H.t(36, fT) * fN));
    }
}
