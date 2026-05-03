package d;

import a.b;
import a.h;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import cc.xposed.R;
import e.j;

/* JADX INFO: loaded from: classes.dex */
public class o extends c {

    class a implements View.OnClickListener {
        a() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            o.this.J1();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void J1() {
        Bundle bundleH = h();
        a.h hVar = (a.h) bundleH.getParcelable("if");
        a.b bVar = (a.b) bundleH.getParcelable("then");
        a.b bVar2 = (a.b) bundleH.getParcelable("else");
        if (hVar == null || hVar.z == 0 || ((bVar == null || bVar.z == 0) && (bVar2 == null || bVar2.z == 0))) {
            e0(R.string.condition_invalid);
        } else {
            W("result", new b.d0(hVar, bVar, bVar2));
        }
        L();
    }

    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.conditional);
        S(R.drawable.ic_ok, new a());
    }

    @Override // e.j
    protected int B0() {
        return 3;
    }

    @Override // d.c
    protected void C1(Bundle bundle, int i) {
        a.b bVarE;
        Bundle bundleH;
        String str;
        if (i == 0) {
            a.h oVar = (a.h) bundle.getParcelable("result");
            if (oVar == null) {
                oVar = new h.o();
            }
            h().putParcelable("if", oVar);
            ((j.k) L0(0)).setSubText(oVar.b(M0()));
            return;
        }
        int i2 = 1;
        if (i != 1) {
            i2 = 2;
            if (i != 2) {
                return;
            }
            bVarE = (a.b) bundle.getParcelable("result");
            if (bVarE == null) {
                bVarE = a.b.e();
            }
            bundleH = h();
            str = "else";
        } else {
            bVarE = (a.b) bundle.getParcelable("result");
            if (bVarE == null) {
                bVarE = a.b.e();
            }
            bundleH = h();
            str = "then";
        }
        bundleH.putParcelable(str, bVarE);
        j.g gVar = (j.g) L0(i2);
        gVar.setSubText(bVarE.n(gVar.getContext()));
        gVar.setImageDrawable(D0(bVarE));
    }

    public o K1(a.h hVar, a.b bVar, a.b bVar2) {
        Bundle bundleH = h();
        bundleH.putParcelable("if", hVar);
        bundleH.putParcelable("then", bVar);
        bundleH.putParcelable("else", bVar2);
        return this;
    }

    @Override // e.j
    protected View h1(int i) {
        Context contextM0 = M0();
        Bundle bundleH = h();
        if (i == 0) {
            a.h oVar = (a.h) bundleH.getParcelable("if");
            if (oVar == null) {
                oVar = new h.o();
            }
            return new j.k(u(R.string.conditional_if), oVar.b(contextM0));
        }
        if (i != 1) {
            a.b bVarR = (a.b) bundleH.getParcelable("else");
            if (bVarR == null) {
                bVarR = a.b.r();
            }
            return new j.g(u(R.string.conditional_else), bVarR.n(contextM0), D0(bVarR));
        }
        a.b bVarR2 = (a.b) bundleH.getParcelable("then");
        if (bVarR2 == null) {
            bVarR2 = a.b.r();
        }
        return new j.g(u(R.string.conditional_then), bVarR2.n(contextM0), D0(bVarR2));
    }

    @Override // e.j
    protected void k1(int i) {
        d dVar;
        CharSequence charSequenceU;
        int i2;
        if (i == 0) {
            P(new p(), 0);
            return;
        }
        int i3 = 1;
        if (i != 1) {
            i3 = 2;
            if (i != 2) {
                return;
            }
            dVar = new d();
            charSequenceU = u(R.string.conditional);
            i2 = R.string.conditional_else;
        } else {
            dVar = new d();
            charSequenceU = u(R.string.conditional);
            i2 = R.string.conditional_then;
        }
        P(dVar.I1(5, charSequenceU, u(i2)), i3);
    }

    @Override // e.j
    protected boolean l1(int i) {
        if (i == 1 || i == 2) {
            a.b bVarR = (a.b) h().getParcelable(i == 1 ? "then" : "else");
            if (bVarR == null) {
                bVarR = a.b.r();
            }
            F1(bVarR, i, 5);
        }
        return true;
    }
}
