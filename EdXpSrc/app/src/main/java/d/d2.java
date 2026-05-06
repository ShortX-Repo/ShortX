package d;

import a.b;
import a.v;
import android.os.Bundle;
import android.view.View;
import cc.xposed.R;
import e.j;
import e.j0;

/* JADX INFO: loaded from: classes.dex */
public class d2 extends e.j {
    private v.a M;

    @Override // e.j
    protected int B0() {
        c0(R.string.variables);
        v.a aVarG = v.a.g();
        this.M = aVarG;
        return aVarG.k() + 1;
    }

    @Override // e.j0.c
    protected void J(Bundle bundle, int i) {
        if (bundle == null) {
            return;
        }
        try {
            if (i == 1) {
                CharSequence charSequence = bundle.getCharSequence("result");
                if (charSequence == null) {
                    return;
                }
                if (this.M.e(f(), charSequence.toString())) {
                    x0(this.M.k() - 1);
                }
            } else {
                if (i != 2) {
                    if (i != 3) {
                        return;
                    }
                    U(bundle);
                    L();
                    return;
                }
                int i2 = bundle.getInt("result", -1);
                int iE0 = E0();
                if (i2 == 0) {
                    b.s0.D(f(), 14, this.M.f(iE0));
                } else {
                    if (i2 != 1) {
                        return;
                    }
                    this.M.i(f(), this.M.f(iE0));
                    u1(iE0);
                }
            }
        } catch (Throwable th) {
            g0(th);
        }
    }

    @Override // e.j
    protected View h1(int i) {
        return i < this.M.k() ? new j.k(this, this.M.f(i)) : b1();
    }

    @Override // e.j
    protected void k1(int i) {
        j0.b bVarJ;
        int i2;
        if (i < this.M.k()) {
            bVarJ = new c.v().z(this.M.f(i));
            i2 = 3;
        } else {
            bVarJ = new e.y().J(u(R.string.enter_name), u(R.string.variable_name_hint), null, 6, 32);
            i2 = 1;
        }
        N(bVarJ, i2);
    }

    @Override // e.j
    protected boolean l1(int i) {
        if (i >= this.M.k()) {
            return super.l1(i);
        }
        N(new e.z().u(new CharSequence[]{u(R.string.show_value), u(R.string.delete)}), 2);
        return true;
    }

    @Override // android.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.M == null || H0() == this.M.k()) {
            return;
        }
        w1();
    }
}
