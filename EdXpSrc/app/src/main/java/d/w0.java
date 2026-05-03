package d;

import android.content.Context;
import android.os.Bundle;
import cc.xposed.R;
import d.d;
import e.j;

/* JADX INFO: loaded from: classes.dex */
public class w0 extends c implements d.a {
    private a.z M;
    private CharSequence[] N = null;

    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.pie);
    }

    @Override // e.j
    protected int B0() {
        this.N = new CharSequence[]{u(R.string.edge_left), u(R.string.edge_right), u(R.string.edge_top), u(R.string.edge_bottom)};
        this.M = g().h();
        return this.N.length;
    }

    @Override // d.c
    protected void C1(Bundle bundle, int i) {
        Context contextF = f();
        a.b bVar = (a.b) bundle.getParcelable("result");
        if (bVar != null) {
            int iE0 = E0();
            int iF0 = F0();
            this.M.l0(contextF, 2, iE0, iF0, bVar);
            j.g gVar = (j.g) K0(iF0);
            gVar.setSubText(bVar.n(contextF));
            gVar.setImageDrawable(D0(bVar));
        }
    }

    public String I1(int i) {
        return i < 6 ? getString(R.string.pie_level_1_item_index_f, Integer.valueOf(i + 1)) : getString(R.string.pie_level_2_item_index_f, Integer.valueOf((i + 1) - 6));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // e.j
    /* JADX INFO: renamed from: J1, reason: merged with bridge method [inline-methods] */
    public j.k h1(int i) {
        return new j.k(this, this.N[i]);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // e.j
    /* JADX INFO: renamed from: K1, reason: merged with bridge method [inline-methods] */
    public j.k i1(int i) {
        Context contextM0 = M0();
        a.b bVarL = this.M.l(I0(), i);
        return new j.g(I1(i), bVarL.n(contextM0), D0(bVarL));
    }

    @Override // e.j
    protected boolean N0(int i) {
        return true;
    }

    @Override // e.j
    protected void k1(int i) {
        y1(i, 12);
    }

    @Override // e.j
    protected void o1(int i) {
        O(new d().J1(6, u(R.string.pie), this.N[I0()], I1(i)));
    }

    @Override // e.j
    protected boolean p1(int i) {
        F1(this.M.l(I0(), i), 1, 6);
        return true;
    }
}
