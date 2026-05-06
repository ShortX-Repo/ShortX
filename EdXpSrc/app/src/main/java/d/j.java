package d;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import cc.xposed.R;
import d.d;
import e.j;
import f.c0;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class j extends c implements d.a, j.d {
    private static final int[] O = a.f.F;
    private static final int[] P = a.f.E;
    private a.f M;
    private List<c0.d> N;

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void J1(View view) {
        if (this.M.i()) {
            return;
        }
        N(new e.m().u(u(R.string.check_clear_all)), 2);
    }

    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.app_state);
        S(R.drawable.ic_delete, new View.OnClickListener() { // from class: d.i
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.z.J1(view);
            }
        });
    }

    @Override // e.j
    protected int B0() {
        this.M = a.f.g();
        if (this.N == null) {
            this.N = f.z.K(f()).Q(false);
        }
        return this.N.size();
    }

    @Override // d.c
    protected void C1(Bundle bundle, int i) {
        try {
            if (i == 1) {
                a.b bVar = (a.b) bundle.getParcelable("result");
                if (bVar != null) {
                    int iE0 = E0();
                    int iF0 = F0();
                    String strI = this.N.get(iE0).i();
                    this.M.j(f(), strI, P[iF0], bVar);
                    ((j.g) L0(iE0)).setHighLight(this.M.h(strI));
                    j.g gVar = (j.g) K0(iF0);
                    gVar.setImageDrawable(D0(bVar));
                    gVar.setSubText(bVar.n(gVar.getContext()));
                    return;
                }
                return;
            }
            if (i != 2) {
                return;
            }
            if (bundle.getBoolean("result", false)) {
                Context contextF = f();
                this.M.e(contextF);
                int iI0 = I0();
                for (int i2 = 0; i2 < O.length; i2++) {
                    j.g gVar2 = (j.g) K0(i2);
                    if (gVar2 != null) {
                        a.b bVarF = this.M.f(this.N.get(iI0).i(), P[i2]);
                        gVar2.setSubText(bVarF.n(contextF));
                        gVar2.setImageDrawable(D0(bVarF));
                    }
                }
                t1();
                e0(R.string.saved);
            }
        } catch (Throwable th) {
            g0(th);
        }
    }

    @Override // e.j
    protected boolean N0(int i) {
        return true;
    }

    @Override // e.j.d
    public String[] a() {
        return f.z.L(this.N);
    }

    @Override // e.j
    protected void g1(Object obj) {
        this.N = (List) obj;
    }

    @Override // e.j
    protected View h1(int i) {
        c0.d dVar = this.N.get(i);
        String strI = dVar.i();
        j.g gVar = new j.g(dVar.e(), dVar.f(), strI);
        if (!dVar.m()) {
            gVar.e();
        }
        gVar.setHighLight(this.M.h(strI));
        return gVar;
    }

    @Override // e.j
    protected View i1(int i) {
        a.b bVarF = this.M.f(this.N.get(I0()).i(), P[i]);
        return new j.g(u(O[i]), bVarF.n(M0()), D0(bVarF));
    }

    @Override // e.j
    protected void k1(int i) {
        y1(i, P.length);
    }

    @Override // e.j
    protected j.i m1() {
        List<c0.d> listM = m(c0.d.class);
        this.N = listM;
        if (listM != null) {
            return null;
        }
        return new j.i.d(false);
    }

    @Override // e.j
    protected void o1(int i) {
        P(new d().J1(6, u(R.string.app_state), this.N.get(I0()).i(), u(O[i])), 1);
    }

    @Override // e.j, e.j0.c, android.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        Z(this.N);
    }

    @Override // e.j
    protected boolean p1(int i) {
        F1(this.M.f(this.N.get(I0()).i(), P[i]), 1, 6);
        return true;
    }
}
