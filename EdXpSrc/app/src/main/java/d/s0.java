package d;

import a.b;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import cc.xposed.R;
import d.d;
import e.j;
import java.util.ArrayList;
import java.util.Collections;

/* JADX INFO: loaded from: classes.dex */
public class s0 extends c implements d.a {
    private static final int[] N = {R.string.insert, R.string.move_up, R.string.move_down, R.string.delete};
    private ArrayList<a.b> M;

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void L1(View view) {
        if (this.M != null) {
            W("result", new b.q1(this.M));
        }
        L();
    }

    @Override // e.j0.c
    protected void B() {
        super.B();
        R1();
    }

    @Override // e.j
    protected int B0() {
        this.M = J1();
        return h().getBoolean("editable") ? this.M.size() + 1 : this.M.size();
    }

    @Override // d.c
    protected void C1(Bundle bundle, int i) {
        a.b bVar;
        int size;
        if (i == 1) {
            int iE0 = E0();
            int i2 = bundle.getInt("result", -1);
            if (i2 >= 0) {
                switch (N[i2]) {
                    case R.string.delete /* 2131099853 */:
                        P1(iE0);
                        break;
                    case R.string.insert /* 2131099949 */:
                        E1(K1(), 3);
                        break;
                    case R.string.move_down /* 2131099992 */:
                        if (iE0 < this.M.size() - 1) {
                            O1(iE0, false);
                        }
                        break;
                    case R.string.move_up /* 2131099993 */:
                        if (iE0 > 0) {
                            O1(iE0, true);
                        }
                        break;
                }
                return;
            }
            return;
        }
        if (i == 2) {
            bVar = (a.b) bundle.getParcelable("result");
            if (bVar == null) {
                return;
            } else {
                size = this.M.size();
            }
        } else {
            if (i != 3) {
                if (i == 4) {
                    a.b bVar2 = (a.b) bundle.getParcelable("result");
                    if (bVar2 != null) {
                        Q1(E0(), bVar2);
                        return;
                    }
                    return;
                }
                if (i != 5) {
                    return;
                }
                if (bundle.getBoolean("result", false)) {
                    W("result", new b.q1(this.M));
                }
                L();
                return;
            }
            bVar = (a.b) bundle.getParcelable("result");
            if (bVar == null) {
                return;
            } else {
                size = E0();
            }
        }
        M1(size, bVar);
    }

    protected final ArrayList<a.b> J1() {
        ArrayList<a.b> arrayList = this.M;
        if (arrayList != null) {
            return arrayList;
        }
        ArrayList<a.b> parcelableArrayList = h().getParcelableArrayList("actions");
        return parcelableArrayList == null ? new ArrayList<>() : parcelableArrayList;
    }

    protected int K1() {
        return 4;
    }

    protected void M1(int i, a.b bVar) {
        this.M.add(i, bVar);
        x0(i);
        h().putBoolean("changed", true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // e.j
    /* JADX INFO: renamed from: N1, reason: merged with bridge method [inline-methods] */
    public j.k h1(int i) {
        Context contextM0 = M0();
        if (i >= this.M.size()) {
            return b1();
        }
        a.b bVar = this.M.get(i);
        return new j.g(D0(bVar), bVar.n(contextM0), (CharSequence) null);
    }

    protected void O1(int i, boolean z) {
        int i2 = z ? i - 1 : i + 1;
        ArrayList<a.b> arrayList = this.M;
        arrayList.add(i2, arrayList.remove(i));
        a1(i, i2);
        h().putBoolean("changed", true);
    }

    protected void P1(int i) {
        this.M.remove(i);
        u1(i);
        h().putBoolean("changed", true);
    }

    protected void Q1(int i, a.b bVar) {
        this.M.set(i, bVar);
        j.g gVar = (j.g) L0(i);
        gVar.setImageDrawable(D0(bVar));
        gVar.setText(bVar.n(M0()));
        h().putBoolean("changed", true);
    }

    protected void R1() {
        c0(R.string.action_multi_action);
        S(R.drawable.ic_ok, new View.OnClickListener() { // from class: d.r0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.z.L1(view);
            }
        });
    }

    public s0 S1(a.b[] bVarArr, boolean z) {
        ArrayList<a.b> arrayList = new ArrayList<>(bVarArr.length);
        this.M = arrayList;
        Collections.addAll(arrayList, bVarArr);
        Bundle bundleH = h();
        bundleH.putParcelableArrayList("actions", this.M);
        bundleH.putBoolean("editable", z);
        return this;
    }

    @Override // e.j
    protected void k1(int i) {
        if (i == this.M.size()) {
            E1(K1(), 2);
            return;
        }
        if (!h().getBoolean("editable")) {
            return;
        }
        CharSequence[] charSequenceArr = new CharSequence[N.length];
        int i2 = 0;
        while (true) {
            int[] iArr = N;
            if (i2 >= iArr.length) {
                N(new e.z().u(charSequenceArr), 1);
                return;
            } else {
                charSequenceArr[i2] = u(iArr[i2]);
                i2++;
            }
        }
    }

    @Override // e.j
    protected boolean l1(int i) {
        if (i >= this.M.size()) {
            return super.l1(i);
        }
        G1(this.M.get(i), 4, K1(), h().getBoolean("editable"));
        return true;
    }

    @Override // e.j, e.j0.c, android.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        h().putParcelableArrayList("actions", this.M);
        super.onSaveInstanceState(bundle);
    }

    @Override // e.j0.c
    protected void y() {
        if (h().getBoolean("changed", false)) {
            N(new e.m().u(u(R.string.check_save_message)), 5);
        } else {
            super.y();
        }
    }
}
