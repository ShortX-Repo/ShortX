package d;

import a.b;
import android.os.Bundle;
import cc.xposed.R;

/* JADX INFO: loaded from: classes.dex */
public class r1 extends t0 {
    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.saved_multi_actions);
    }

    @Override // d.t0
    protected a.n B1() {
        return b.k2.A();
    }

    @Override // d.t0
    protected a.b[] C1(Bundle bundle) {
        b.q1 q1Var = (b.q1) bundle.getParcelable("result");
        return q1Var != null ? q1Var.I : new a.b[0];
    }

    @Override // d.t0
    protected a.b D1(int i) {
        return new b.k2(i);
    }

    @Override // d.t0
    protected e.j E1(CharSequence charSequence, a.b[] bVarArr, boolean z) {
        return new s0().S1(bVarArr, z);
    }

    @Override // d.t0
    protected void F1() {
        H1(E1(null, new a.b[0], true));
    }
}
