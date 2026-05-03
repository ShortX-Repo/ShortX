package d;

import a.b;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import cc.xposed.R;
import d.d;
import e.j;

/* JADX INFO: loaded from: classes.dex */
public class z1 extends c implements d.a {
    private a.b[] M;
    private CharSequence[] N;

    class a implements View.OnClickListener {
        a() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (z1.this.M != null) {
                z1.this.W("result", new b.h3(z1.this.M));
            }
            z1.this.L();
        }
    }

    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.action_sub_gestures);
        S(R.drawable.ic_ok, new a());
    }

    @Override // e.j
    protected int B0() {
        a.b[] bVarArr = (a.b[]) h().getParcelableArray("actions");
        this.M = bVarArr;
        if (bVarArr == null) {
            this.M = b.h3.K;
        }
        int length = b.h3.J.length;
        this.N = new CharSequence[length];
        for (int i = 0; i < length; i++) {
            this.N[i] = u(b.h3.J[i]);
        }
        return length;
    }

    @Override // d.c
    protected void C1(Bundle bundle, int i) {
        if (i != 1) {
            if (i != 2) {
                return;
            }
            if (bundle.getBoolean("result", false)) {
                W("result", new b.h3(this.M));
            }
            L();
            return;
        }
        Context contextM0 = M0();
        a.b bVar = (a.b) bundle.getParcelable("result");
        if (bVar != null) {
            int iE0 = E0();
            this.M[iE0] = bVar;
            h().putBoolean("changed", true);
            j.g gVar = (j.g) L0(iE0);
            gVar.setSubText(bVar.n(contextM0));
            gVar.setImageDrawable(D0(bVar));
        }
    }

    public z1 L1(a.b[] bVarArr) {
        a.b[] bVarArr2;
        if (bVarArr != null) {
            bVarArr2 = new a.b[bVarArr.length];
            System.arraycopy(bVarArr, 0, bVarArr2, 0, bVarArr.length);
        } else {
            bVarArr2 = null;
        }
        h().putParcelableArray("actions", bVarArr2);
        return this;
    }

    @Override // e.j
    protected View h1(int i) {
        a.b bVar = this.M[i];
        return new j.g(this.N[i], bVar.n(M0()), D0(bVar));
    }

    @Override // e.j
    protected void k1(int i) {
        P(new d().I1(6, o(), this.N[i]), 1);
    }

    @Override // e.j
    protected boolean l1(int i) {
        F1(this.M[i], 1, 1);
        return true;
    }

    @Override // e.j, e.j0.c, android.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        h().putParcelableArray("actions", this.M);
        super.onSaveInstanceState(bundle);
    }

    @Override // e.j0.c
    protected void y() {
        if (h().getBoolean("changed", false)) {
            N(new e.m().u(u(R.string.check_save_message)), 2);
        } else {
            super.y();
        }
    }
}
