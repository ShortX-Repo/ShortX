package d;

import android.os.Bundle;
import cc.xposed.R;
import e.j;
import f.c0;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class f extends e implements j.d {
    private List<c0.d> M = null;

    private a.b E1(int i) {
        return this.M.get(I0()).q()[i].q();
    }

    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.select_activity);
    }

    @Override // e.j
    protected int B0() {
        if (this.M == null) {
            this.M = f.z.K(f()).R();
        }
        return this.M.size();
    }

    @Override // d.e
    protected void B1(Bundle bundle, int i) {
        if (i == 1) {
            int i2 = bundle.getInt("result", 0);
            if (i2 == 0 || i2 == 1) {
                try {
                    a.m.b().f(3, i2);
                    w1();
                } catch (Throwable th) {
                    g0(th);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // e.j
    /* JADX INFO: renamed from: F1, reason: merged with bridge method [inline-methods] */
    public j.k h1(int i) {
        c0.d dVar = this.M.get(i);
        j.g gVar = new j.g(dVar.d(), dVar.f(), dVar.i());
        if (!dVar.m()) {
            gVar.e();
        }
        return gVar;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // e.j
    /* JADX INFO: renamed from: G1, reason: merged with bridge method [inline-methods] */
    public j.k i1(int i) {
        c0.a aVar = this.M.get(I0()).q()[i];
        j.g gVar = new j.g(aVar.j(), aVar.f(), aVar.t());
        if (!aVar.u()) {
            gVar.e();
        }
        return gVar;
    }

    @Override // e.j
    protected boolean N0(int i) {
        return true;
    }

    @Override // e.j.d
    public String[] a() {
        return f.z.L(this.M);
    }

    @Override // e.j
    protected void g1(Object obj) {
        this.M = (List) obj;
    }

    @Override // e.j
    protected void k1(int i) {
        y1(i, this.M.get(i).q().length);
    }

    @Override // e.j
    protected j.i m1() {
        List<c0.d> listM = m(c0.d.class);
        this.M = listM;
        if (listM != null) {
            return null;
        }
        return new j.i.a();
    }

    @Override // e.j
    protected void n1(int i, int i2) {
        if (i < 0 || i >= this.M.size() || this.M.get(i).q().length != i2) {
            return;
        }
        super.n1(i, i2);
    }

    @Override // e.j
    protected void o1(int i) {
        C1(E1(i));
    }

    @Override // e.j, e.j0.c, android.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        Z(this.M);
    }

    @Override // e.j
    protected boolean p1(int i) {
        D1(E1(i));
        return true;
    }
}
