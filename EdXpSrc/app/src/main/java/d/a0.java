package d;

import a.b;
import android.os.Bundle;
import cc.xposed.R;
import e.j;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class a0 extends e.j {
    private List<b.y0> M;
    private CharSequence[] N;
    private int[] O;

    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.gesture_records);
    }

    @Override // e.j
    protected int B0() {
        this.M = b.y0.E();
        b.y0.H();
        int i = 0;
        int[] iArr = h().getBoolean("selectable", false) ? new int[]{R.string.select, R.string.view} : new int[]{R.string.view, R.string.move_up, R.string.move_down, R.string.rename, R.string.delete};
        this.O = iArr;
        this.N = new CharSequence[iArr.length];
        while (true) {
            int[] iArr2 = this.O;
            if (i >= iArr2.length) {
                return this.M.size() + 1;
            }
            this.N[i] = u(iArr2[i]);
            i++;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // e.j
    /* JADX INFO: renamed from: B1, reason: merged with bridge method [inline-methods] */
    public j.k h1(int i) {
        if (i >= this.M.size()) {
            return b1();
        }
        b.y0 y0Var = this.M.get(i);
        String strA = y0Var.A();
        StringBuilder sb = new StringBuilder();
        sb.append((Object) u(y0Var.F() ? R.string.orientation_landscape : R.string.orientation_portrait));
        sb.append(", ");
        sb.append(y0Var.C());
        sb.append("ms");
        return new j.k(strA, sb.toString());
    }

    public a0 C1(boolean z) {
        h().putBoolean("selectable", z);
        return this;
    }

    @Override // e.j0.c
    protected void G() {
        super.G();
        b.y0.Q(f());
    }

    @Override // e.j0.c
    protected void J(Bundle bundle, int i) {
        CharSequence charSequence;
        if (bundle == null) {
            return;
        }
        try {
            if (i == 1) {
                int iE0 = E0();
                switch (this.O[bundle.getInt("result", -2)]) {
                    case R.string.delete /* 2131099853 */:
                        b.y0.M(this.M.get(iE0));
                        this.M.remove(iE0);
                        u1(iE0);
                        break;
                    case R.string.move_down /* 2131099992 */:
                        if (iE0 >= this.M.size() - 1) {
                            return;
                        }
                        b.y0.I(this.M.get(iE0));
                        List<b.y0> list = this.M;
                        int i2 = iE0 + 1;
                        list.add(i2, list.remove(iE0));
                        a1(iE0, i2);
                        break;
                    case R.string.move_up /* 2131099993 */:
                        if (iE0 <= 0) {
                            return;
                        }
                        b.y0.J(this.M.get(iE0));
                        List<b.y0> list2 = this.M;
                        int i3 = iE0 - 1;
                        list2.add(i3, list2.remove(iE0));
                        a1(iE0, i3);
                        break;
                    case R.string.rename /* 2131100034 */:
                        e.y yVar = new e.y();
                        yVar.K(u(R.string.enter_name), null, this.M.get(iE0).A());
                        N(yVar, 3);
                        return;
                    case R.string.select /* 2131100065 */:
                        W("result", this.M.get(iE0));
                        L();
                        return;
                    case R.string.view /* 2131100194 */:
                        e2 e2Var = new e2();
                        e2Var.k0(this.M.get(iE0));
                        P(e2Var, 4);
                        return;
                    default:
                        return;
                }
            } else {
                if (i == 2) {
                    b.y0 y0Var = (b.y0) bundle.getParcelable("result");
                    if (y0Var != null) {
                        this.M.add(y0Var);
                        x0(this.M.size() - 1);
                        return;
                    }
                    return;
                }
                if (i != 3 || (charSequence = bundle.getCharSequence("result")) == null) {
                    return;
                }
                int iE02 = E0();
                this.M.get(iE02).P(charSequence.toString());
                ((j.k) L0(iE02)).setText(charSequence);
            }
        } catch (Throwable th) {
            g0(th);
        }
    }

    @Override // e.j
    protected void k1(int i) {
        if (i < this.M.size()) {
            N(new e.z().u(this.N), 1);
        } else {
            P(new d0(), 2);
        }
    }
}
