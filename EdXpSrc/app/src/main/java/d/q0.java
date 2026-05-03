package d;

import a.p;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import cc.xposed.R;
import d.d;
import e.j;

/* JADX INFO: loaded from: classes.dex */
public class q0 extends c implements d.a, j.d {
    private static final int N = p.g.f33b;
    private a.z M;

    private int I1(int i) {
        return p.g.f32a[i * 2];
    }

    private CharSequence J1(int i) {
        return u(p.g.f32a[(i * 2) + 1]);
    }

    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.more_triggers);
    }

    @Override // e.j
    protected int B0() {
        this.M = g().h();
        return N;
    }

    @Override // d.c
    protected void C1(Bundle bundle, int i) {
        a.b bVar;
        if (i == 1 && (bVar = (a.b) bundle.getParcelable("result")) != null) {
            int iE0 = E0();
            Context contextM0 = M0();
            this.M.m0(contextM0, I1(iE0), bVar);
            j.g gVar = (j.g) L0(iE0);
            gVar.setSubText(bVar.n(contextM0));
            gVar.setImageDrawable(D0(bVar));
        }
    }

    @Override // e.j.d
    public String[] a() {
        String[] strArr = new String[N];
        for (int i = 0; i < N; i++) {
            strArr[i] = J1(i).toString();
        }
        return strArr;
    }

    @Override // e.j
    protected View h1(int i) {
        int iI1 = I1(i);
        a.b bVarK = this.M.k(iI1);
        j.g gVar = new j.g(J1(i), bVarK.n(M0()), D0(bVarK));
        if (Build.VERSION.SDK_INT < 21 && iI1 == 27) {
            gVar.setEnabled(false);
        }
        return gVar;
    }

    @Override // e.j
    protected void k1(int i) {
        P(new d().H1(6, J1(i)), 1);
    }

    @Override // e.j
    protected boolean l1(int i) {
        F1(this.M.k(I1(i)), 1, 6);
        return true;
    }
}
