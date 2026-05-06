package d;

import android.os.Bundle;
import e.j;
import f.c0;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public abstract class a extends e.j implements j.d {
    protected List<c0.d> M;

    @Override // e.j
    protected int B0() {
        if (this.M == null) {
            this.M = f.z.K(f()).Q(false);
        }
        return this.M.size();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // e.j
    /* JADX INFO: renamed from: B1, reason: merged with bridge method [inline-methods] */
    public j.k h1(int i) {
        c0.d dVar = this.M.get(i);
        j.g gVar = new j.g(dVar.d(), dVar.f(), dVar.i());
        if (!dVar.m()) {
            gVar.e();
        }
        return gVar;
    }

    @Override // e.j.d
    public final String[] a() {
        return f.z.L(this.M);
    }

    @Override // e.j
    protected void g1(Object obj) {
        this.M = (List) obj;
    }

    @Override // e.j
    protected j.i m1() {
        List<c0.d> listM = m(c0.d.class);
        this.M = listM;
        if (listM != null) {
            return null;
        }
        return new j.i.d(false);
    }

    @Override // e.j, e.j0.c, android.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        if (this.M != null && s() == null) {
            Z(this.M);
        }
        super.onSaveInstanceState(bundle);
    }
}
