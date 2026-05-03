package d;

import android.content.Context;
import android.os.Bundle;
import android.widget.CompoundButton;
import cc.xposed.R;
import d.d;
import e.j;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class j0 extends c implements d.a {
    private a.z M;
    private ArrayList<Integer> N = null;

    class a implements CompoundButton.OnCheckedChangeListener {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ int f345a;

        a(int i) {
            this.f345a = i;
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            j0.this.M.t0(compoundButton.getContext(), this.f345a, z);
        }
    }

    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.keys);
    }

    @Override // e.j
    protected int B0() {
        a.z zVarH = g().h();
        this.M = zVarH;
        ArrayList<Integer> arrayListX = zVarH.x();
        this.N = arrayListX;
        return arrayListX.size() + 1;
    }

    @Override // d.c
    protected void C1(Bundle bundle, int i) {
        Context contextF = f();
        if (i == 0) {
            a.b bVar = (a.b) bundle.getParcelable("result");
            if (bVar != null) {
                int iIntValue = this.N.get(E0()).intValue();
                int iF0 = F0();
                this.M.l0(contextF, 1, iIntValue, iF0, bVar);
                j.g gVar = (j.g) K0(iF0);
                gVar.setSubText(bVar.n(contextF));
                gVar.setImageDrawable(D0(bVar));
                return;
            }
            return;
        }
        if (i == 1) {
            int i2 = bundle.getInt("result", -1);
            if (i2 == -1 || this.N.contains(Integer.valueOf(i2))) {
                return;
            }
            this.M.d0(contextF, i2);
            this.N.add(Integer.valueOf(i2));
            x0(this.N.size() - 1);
            return;
        }
        if (i != 2) {
            return;
        }
        int i3 = bundle.getInt("result");
        if (i3 <= 1) {
            ((j.ViewOnTouchListenerC0005j) L0(E0())).setChecked(i3 == 0);
        } else if (i3 == 2) {
            int iE0 = E0();
            this.M.i0(contextF, this.N.get(iE0).intValue());
            this.N.remove(iE0);
            u1(iE0);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // e.j
    /* JADX INFO: renamed from: J1, reason: merged with bridge method [inline-methods] */
    public j.k h1(int i) {
        Context contextM0 = M0();
        if (i == this.N.size()) {
            return b1();
        }
        int iIntValue = this.N.get(i).intValue();
        j.ViewOnTouchListenerC0005j viewOnTouchListenerC0005j = new j.ViewOnTouchListenerC0005j(f.p0.k(contextM0, iIntValue), null, this.M.w(iIntValue));
        viewOnTouchListenerC0005j.setOnCheckedChangeListener(new a(iIntValue));
        return viewOnTouchListenerC0005j;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // e.j
    /* JADX INFO: renamed from: K1, reason: merged with bridge method [inline-methods] */
    public j.k i1(int i) {
        Context contextM0 = M0();
        a.b bVarJ = this.M.j(this.N.get(I0()).intValue(), i);
        return new j.g(f.p0.j(contextM0, i), bVarJ.n(contextM0), D0(bVarJ));
    }

    @Override // e.j
    protected boolean N0(int i) {
        return i != this.N.size();
    }

    @Override // e.j
    protected void k1(int i) {
        if (i != this.N.size()) {
            y1(i, 3);
        } else {
            N(new c.l(), 1);
        }
    }

    @Override // e.j
    protected boolean l1(int i) {
        if (i == this.N.size()) {
            return super.l1(i);
        }
        N(new e.z().v(new CharSequence[]{u(R.string.enable), u(R.string.disable), u(R.string.delete)}, !this.M.w(this.N.get(i).intValue()) ? 1 : 0), 2);
        return true;
    }

    @Override // e.j
    protected void o1(int i) {
        Context contextM0 = M0();
        P(new d().I1(1, f.p0.k(contextM0, this.N.get(I0()).intValue()), f.p0.j(contextM0, i)), 0);
    }

    @Override // e.j
    protected boolean p1(int i) {
        F1(this.M.j(this.N.get(I0()).intValue(), i), 0, 1);
        return true;
    }
}
