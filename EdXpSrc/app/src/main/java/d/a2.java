package d;

import a.b;
import android.os.Bundle;
import android.view.View;
import cc.xposed.R;
import e.j;

/* JADX INFO: loaded from: classes.dex */
public class a2 extends e {
    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.action_successive_adjust);
    }

    @Override // e.j
    protected int B0() {
        return 15;
    }

    @Override // d.e
    protected void B1(Bundle bundle, int i) {
    }

    @Override // e.j
    protected boolean N0(int i) {
        return true;
    }

    @Override // e.j
    protected View h1(int i) {
        return new j.k(this, b.i3.z(M0(), i));
    }

    @Override // e.j
    protected View i1(int i) {
        return new j.k(this, b.i3.A(M0(), I0(), i == 0));
    }

    @Override // e.j
    protected void k1(int i) {
        y1(i, 2);
    }

    @Override // e.j
    protected void o1(int i) {
        C1(new b.i3(I0(), i == 0));
    }

    @Override // e.j
    protected boolean p1(int i) {
        D1(new b.i3(I0(), i == 0));
        return true;
    }
}
