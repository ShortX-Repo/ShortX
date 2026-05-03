package d;

import a.b;
import android.os.Bundle;
import cc.xposed.R;

/* JADX INFO: loaded from: classes.dex */
public class r extends t0 {
    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.custom_panels);
    }

    @Override // d.t0
    protected a.n B1() {
        return b.f0.A();
    }

    @Override // d.t0
    protected a.b[] C1(Bundle bundle) {
        return (a.b[]) bundle.getParcelableArray("result");
    }

    @Override // d.t0
    protected a.b D1(int i) {
        return new b.f0(i);
    }

    @Override // d.t0
    protected e.j E1(CharSequence charSequence, a.b[] bVarArr, boolean z) {
        return new q().R1(charSequence, bVarArr, z);
    }

    @Override // d.t0
    protected void F1() {
        N(new e.z().u(new CharSequence[]{"2 × 2", "3 × 3", "4 × 4"}), 1);
    }

    @Override // d.t0, e.j0.c
    protected void J(Bundle bundle, int i) {
        if (bundle == null) {
            return;
        }
        if (i != 1) {
            super.J(bundle, i);
            return;
        }
        int i2 = bundle.getInt("result", -1);
        if (i2 != -1) {
            H1(E1(null, new a.b[i2 == 1 ? 9 : i2 == 2 ? 16 : 4], true));
        }
    }
}
