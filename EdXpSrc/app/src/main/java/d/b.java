package d;

import cc.xposed.R;

/* JADX INFO: loaded from: classes.dex */
public class b extends s0 {
    private a.e O;

    @Override // d.s0, e.j
    protected int B0() {
        a.e eVarA = a.e.A();
        this.O = eVarA;
        eVarA.C();
        int iV = this.O.v();
        a.b[] bVarArr = new a.b[iV];
        for (int i = 0; i < iV; i++) {
            bVarArr[i] = this.O.w(i);
        }
        S1(bVarArr, !h().getBoolean("select", false));
        return super.B0();
    }

    @Override // e.j0.c
    protected void G() {
        super.G();
        a.e.A().F(f());
    }

    @Override // d.s0
    protected int K1() {
        return 7;
    }

    @Override // d.s0
    protected void M1(int i, a.b bVar) {
        try {
            if (i == this.O.v()) {
                this.O.e(M0(), bVar);
            } else {
                this.O.f(M0(), i, bVar);
            }
            super.M1(i, bVar);
        } catch (Throwable th) {
            g0(th);
        }
    }

    @Override // d.s0
    protected void O1(int i, boolean z) {
        try {
            if (z) {
                this.O.m(i);
            } else {
                this.O.k(i);
            }
            super.O1(i, z);
        } catch (Throwable th) {
            g0(th);
        }
    }

    @Override // d.s0
    protected void P1(int i) {
        try {
            this.O.q(M0(), i);
            super.P1(i);
        } catch (Throwable th) {
            g0(th);
        }
    }

    @Override // d.s0
    protected void Q1(int i, a.b bVar) {
        try {
            this.O.u(M0(), i, bVar);
            super.Q1(i, bVar);
        } catch (Throwable th) {
            g0(th);
        }
    }

    @Override // d.s0
    protected void R1() {
        c0(R.string.action_collection);
    }

    public b T1(boolean z) {
        h().putBoolean("select", z);
        return this;
    }

    @Override // d.s0, e.j
    protected void k1(int i) {
        if (!h().getBoolean("select", false)) {
            super.k1(i);
        } else {
            W("result", this.O.w(i));
            L();
        }
    }

    @Override // d.s0, e.j0.c
    protected void y() {
        L();
    }
}
