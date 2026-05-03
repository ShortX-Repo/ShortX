package com.jozein.xedgepro.xposed;

import a.b;
import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import com.jozein.xedgepro.xposed.w1;
import de.robv.android.xposed.XC_MethodHook;

/* JADX INFO: loaded from: classes.dex */
class a0 extends z {
    private final a.p F;
    private final w1 G;
    private XC_MethodHook.MethodHookParam H;
    private XC_MethodHook.MethodHookParam I;
    private boolean J;
    private boolean K;
    private boolean L;
    private int M;
    private int N;
    private int O;
    private long P;
    private long Q;
    private long R;
    private a S;

    interface a {
        void a();

        void f();
    }

    a0(w1 w1Var, y0 y0Var, Handler handler) {
        super(y0Var, handler);
        this.H = null;
        this.I = null;
        this.J = false;
        this.K = false;
        this.L = false;
        this.M = 0;
        this.N = 0;
        this.O = -1;
        this.P = 0L;
        this.Q = 500L;
        this.R = 300L;
        this.S = null;
        this.G = w1Var;
        this.F = w1Var.b2();
    }

    private boolean A(XC_MethodHook.MethodHookParam methodHookParam, KeyEvent keyEvent) {
        a.b bVarQ = q(keyEvent.getKeyCode(), 2);
        if (bVarQ.z != 0) {
            this.J = true;
            this.H = null;
            this.G.u3(bVarQ);
            return true;
        }
        XC_MethodHook.MethodHookParam methodHookParam2 = this.H;
        if (methodHookParam2 != null) {
            g(methodHookParam2);
            this.H = null;
        }
        return t(methodHookParam);
    }

    private boolean B(XC_MethodHook.MethodHookParam methodHookParam, KeyEvent keyEvent) {
        a aVar = this.S;
        if (aVar != null) {
            aVar.f();
            this.S = null;
        }
        int i = this.M + 1;
        this.M = i;
        if (this.K) {
            if (this.J) {
                return true;
            }
            if (i == 2) {
                o();
                g(this.H);
                this.H = null;
                this.K = false;
            }
        }
        if (this.L) {
            return t(methodHookParam);
        }
        return false;
    }

    private boolean C(XC_MethodHook.MethodHookParam methodHookParam, KeyEvent keyEvent) {
        n();
        KeyEvent keyEvent2 = (KeyEvent) this.I.args[0];
        if (keyEvent.getKeyCode() == keyEvent2.getKeyCode() && keyEvent.getEventTime() - keyEvent2.getEventTime() < this.R) {
            return w(methodHookParam, keyEvent);
        }
        u();
        return x(methodHookParam, keyEvent);
    }

    private a.b D(int i) {
        return this.G.y4(this.F.j(i, 1), true);
    }

    private void p(a.b bVar) {
        if (bVar.z != 86) {
            this.G.u3(bVar);
            return;
        }
        w1.w0 w0VarF2 = this.G.f2((b.i3) bVar);
        this.S = w0VarF2;
        w0VarF2.a();
    }

    private a.b q(int i, int i2) {
        return this.G.y4(this.F.j(i, i2), false);
    }

    private boolean r(int i) {
        return this.F.I(i);
    }

    private XC_MethodHook.MethodHookParam s(XC_MethodHook.MethodHookParam methodHookParam) {
        if (z.E) {
            Object[] objArr = methodHookParam.args;
            objArr[0] = new KeyEvent((KeyEvent) objArr[0]);
        }
        return methodHookParam;
    }

    private boolean t(XC_MethodHook.MethodHookParam methodHookParam) {
        if (!z.E) {
            return false;
        }
        g(s(methodHookParam));
        return true;
    }

    private void u() {
        a.b bVarQ = q(((KeyEvent) this.H.args[0]).getKeyCode(), 0);
        if (bVarQ.z == 0) {
            g(this.H);
            g(this.I);
        } else {
            this.J = true;
            this.G.u3(bVarQ);
        }
        this.H = null;
        this.I = null;
    }

    private boolean v(XC_MethodHook.MethodHookParam methodHookParam, KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        if (D(keyCode).z != 0) {
            this.I = s(methodHookParam);
            l(this.R);
            this.G.b1(this.R + 200);
            return true;
        }
        a.b bVarQ = q(keyCode, 0);
        if (bVarQ.z != 0) {
            this.H = null;
            this.I = null;
            this.G.u3(bVarQ);
            return true;
        }
        XC_MethodHook.MethodHookParam methodHookParam2 = this.H;
        if (methodHookParam2 != null) {
            g(methodHookParam2);
            this.H = null;
        }
        return t(methodHookParam);
    }

    private boolean w(XC_MethodHook.MethodHookParam methodHookParam, KeyEvent keyEvent) {
        a.b bVarQ = q(keyEvent.getKeyCode(), 1);
        if (bVarQ.z != 0) {
            this.K = true;
            this.J = true;
            this.H = null;
            this.I = null;
            p(bVarQ);
            return true;
        }
        g(this.H);
        g(this.I);
        this.K = false;
        this.J = false;
        this.H = null;
        this.I = null;
        return false;
    }

    private boolean x(XC_MethodHook.MethodHookParam methodHookParam, KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        this.L = false;
        if (this.G.q2() && this.F.G(keyCode)) {
            this.K = true;
            this.J = true;
            return true;
        }
        if (!r(keyCode)) {
            this.K = false;
            this.J = false;
            return false;
        }
        this.P = keyEvent.getEventTime();
        this.H = s(methodHookParam);
        this.L = z.E;
        this.K = true;
        this.J = false;
        long jU = this.F.u(13);
        this.Q = jU;
        if (jU == 0) {
            this.Q = ViewConfiguration.getLongPressTimeout();
        }
        long jU2 = this.F.u(12);
        this.R = jU2;
        if (jU2 == 0) {
            this.R = ViewConfiguration.getDoubleTapTimeout();
        }
        if (this.G.w2()) {
            m(this.Q);
        }
        return true;
    }

    private boolean y(XC_MethodHook.MethodHookParam methodHookParam, KeyEvent keyEvent) {
        if (this.M >= 1) {
            return (keyEvent.getKeyCode() == this.N && keyEvent.getDeviceId() == this.O) ? this.K || this.L : B(methodHookParam, keyEvent);
        }
        this.M = 1;
        this.N = keyEvent.getKeyCode();
        this.O = keyEvent.getDeviceId();
        if (!this.G.r2()) {
            return this.I != null ? C(methodHookParam, keyEvent) : x(methodHookParam, keyEvent);
        }
        this.G.u1();
        this.K = true;
        this.J = true;
        Intent intent = new Intent(b.s0.L);
        intent.setPackage(f.l.j);
        new f.m(intent).j().d(this.N).l();
        try {
            this.G.R1().W(intent, 0);
        } catch (Throwable th) {
            f.v.d(th);
        }
        return true;
    }

    private boolean z(XC_MethodHook.MethodHookParam methodHookParam, KeyEvent keyEvent) {
        a aVar = this.S;
        if (aVar != null) {
            aVar.f();
            this.S = null;
        }
        this.N = 0;
        this.O = -1;
        int i = this.M - 1;
        this.M = i;
        if (this.K) {
            if (this.J) {
                return true;
            }
            if (i == 0) {
                o();
                return keyEvent.getEventTime() - this.P < this.Q ? v(methodHookParam, keyEvent) : A(methodHookParam, keyEvent);
            }
        }
        if (this.L) {
            return t(methodHookParam);
        }
        return false;
    }

    @Override // com.jozein.xedgepro.xposed.z
    protected boolean c(XC_MethodHook.MethodHookParam methodHookParam, KeyEvent keyEvent) {
        int action = keyEvent.getAction();
        if (action == 0) {
            return y(methodHookParam, keyEvent);
        }
        if (action != 1) {
            return false;
        }
        return z(methodHookParam, keyEvent);
    }

    @Override // com.jozein.xedgepro.xposed.z
    protected void e() {
        u();
    }

    @Override // com.jozein.xedgepro.xposed.z
    protected void f() {
        a.b bVarQ = q(((KeyEvent) this.H.args[0]).getKeyCode(), 2);
        if (bVarQ.z == 0) {
            XC_MethodHook.MethodHookParam methodHookParam = this.H;
            if (methodHookParam != null) {
                g(methodHookParam);
                this.H = null;
            }
        } else {
            this.J = true;
            p(bVarQ);
        }
        this.H = null;
    }
}
