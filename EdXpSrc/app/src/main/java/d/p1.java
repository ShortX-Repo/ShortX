package d;

import a.b;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import cc.xposed.R;
import d.d;
import e.j;

/* JADX INFO: loaded from: classes.dex */
public class p1 extends c implements d.a {
    private int M = 0;
    private a.b N = a.b.r();

    class a implements View.OnClickListener {
        a() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            p1.this.W("result", new b.f2(p1.this.M, p1.this.N));
            p1.this.L();
        }
    }

    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.action_repeat);
        S(R.drawable.ic_ok, new a());
    }

    @Override // e.j
    protected int B0() {
        Bundle bundleH = h();
        this.M = bundleH.getInt("count", 0);
        a.b bVar = (a.b) bundleH.getParcelable("action");
        this.N = bVar;
        if (bVar != null) {
            return 2;
        }
        this.N = a.b.r();
        return 2;
    }

    @Override // d.c
    protected void C1(Bundle bundle, int i) {
        if (i == 1) {
            CharSequence charSequence = bundle.getCharSequence("result");
            if (charSequence != null) {
                try {
                    this.M = Integer.parseInt(charSequence.toString());
                    ((j.k) L0(0)).setSubText(Integer.toString(this.M));
                    h().putBoolean("changed", true);
                    return;
                } catch (NumberFormatException e2) {
                    f.v.d(e2);
                    return;
                }
            }
            return;
        }
        if (i != 2) {
            if (i != 3) {
                return;
            }
            if (bundle.getBoolean("result", false)) {
                W("result", new b.f2(this.M, this.N));
            }
            L();
            return;
        }
        a.b bVar = (a.b) bundle.getParcelable("result");
        if (bVar != null) {
            this.N = bVar;
            Context contextM0 = M0();
            j.g gVar = (j.g) L0(1);
            gVar.setSubText(bVar.n(contextM0));
            gVar.setImageDrawable(bVar.m(contextM0));
            h().putBoolean("changed", true);
        }
    }

    public p1 M1(int i, a.b bVar) {
        Bundle bundleH = h();
        bundleH.putInt("count", i);
        bundleH.putParcelable("action", bVar);
        return this;
    }

    @Override // e.j
    protected View h1(int i) {
        if (i == 0) {
            return new j.k(u(R.string.count), Integer.toString(this.M));
        }
        Context contextM0 = M0();
        return new j.g(u(R.string.action), this.N.n(contextM0), this.N.m(contextM0));
    }

    @Override // e.j
    protected void k1(int i) {
        if (i != 0) {
            E1(3, 2);
            return;
        }
        e.y yVar = new e.y();
        yVar.J(u(R.string.enter_count), null, Integer.toString(this.M), 7, 9);
        N(yVar, 1);
    }

    @Override // e.j
    protected boolean l1(int i) {
        if (i == 0) {
            return super.l1(i);
        }
        F1(this.N, 2, 3);
        return true;
    }

    @Override // e.j, e.j0.c, android.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putInt("count", this.M);
        bundle.putParcelable("action", this.N);
        super.onSaveInstanceState(bundle);
    }

    @Override // e.j0.c
    protected void y() {
        if (h().getBoolean("changed", false)) {
            N(new e.m().u(u(R.string.check_save_message)), 3);
        } else {
            super.y();
        }
    }
}
