package d;

import a.s;
import android.view.View;
import android.widget.CompoundButton;
import cc.xposed.R;
import d.d;
import e.j;

/* JADX INFO: loaded from: classes.dex */
public class u1 extends c implements d.a {
    private s.f M;

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void K1(a.s sVar, CompoundButton compoundButton, boolean z) {
        if (sVar.z != z) {
            sVar.z = z;
            h().putBoolean("changed", true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void L1(View view) {
        M1();
    }

    private void M1() {
        if (h().getBoolean("changed", false)) {
            try {
                this.M.m(f());
            } catch (Throwable th) {
                g0(th);
            }
            h().putBoolean("changed", false);
        }
    }

    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.schedule);
        S(R.drawable.ic_save, new View.OnClickListener() { // from class: d.s1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.z.L1(view);
            }
        });
    }

    @Override // e.j
    protected int B0() {
        if (this.M == null) {
            this.M = s.f.h();
        }
        return this.M.n() + 1;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:63:0x01d8  */
    @Override // d.c
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected void C1(android.os.Bundle r7, int r8) {
        /*
            Method dump skipped, instruction units count: 532
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: d.u1.C1(android.os.Bundle, int):void");
    }

    @Override // e.j
    protected boolean N0(int i) {
        return i < this.M.n();
    }

    @Override // e.j
    protected View h1(int i) {
        if (i >= this.M.n()) {
            return b1();
        }
        final a.s sVarG = this.M.g(i);
        j.ViewOnTouchListenerC0005j viewOnTouchListenerC0005j = new j.ViewOnTouchListenerC0005j(sVarG.d(), sVarG.C.n(M0()), sVarG.z);
        viewOnTouchListenerC0005j.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: d.t1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                this.f370a.K1(sVarG, compoundButton, z);
            }
        });
        return viewOnTouchListenerC0005j;
    }

    @Override // e.j
    protected View i1(int i) {
        a.s sVarG = this.M.g(I0());
        if (i == 0) {
            return new j.k(this, sVarG.b());
        }
        if (i != 1) {
            if (i != 2) {
                return new j.k(this, null);
            }
        } else {
            if (sVarG instanceof s.c) {
                return new j.k(this, f.j.p(((s.c) sVarG).F));
            }
            if (sVarG instanceof s.b) {
                s.b bVar = (s.b) sVarG;
                return new j.k(f.j.k(bVar.F), f.j.f(M0(), bVar.G));
            }
        }
        return new j.g(sVarG.C.n(M0()), (CharSequence) null, D0(sVarG.C));
    }

    @Override // e.j
    protected void k1(int i) {
        if (i < this.M.n()) {
            y1(i, 3);
        } else {
            N(new e.z().u(new CharSequence[]{u(R.string.as_week), u(R.string.as_date)}), 9);
        }
    }

    @Override // e.j
    protected boolean l1(int i) {
        if (i >= this.M.n()) {
            return super.l1(i);
        }
        int[] iArr = new int[5];
        iArr[0] = this.M.g(i).z ? R.string.disable : R.string.enable;
        iArr[1] = R.string.rename;
        iArr[2] = R.string.move_up;
        iArr[3] = R.string.move_down;
        iArr[4] = R.string.delete;
        h().putIntArray("dialog_items", iArr);
        CharSequence[] charSequenceArr = new CharSequence[5];
        for (int i2 = 0; i2 < 5; i2++) {
            charSequenceArr[i2] = u(iArr[i2]);
        }
        N(new e.z().u(charSequenceArr), 7);
        return true;
    }

    @Override // e.j
    protected void o1(int i) {
        a.s sVarG = this.M.g(I0());
        if (i == 0) {
            N(new e.n0().w(null, sVarG.A, sVarG.B), 1);
            return;
        }
        if (i != 1) {
            if (i != 2) {
                return;
            }
            P(new d().I1(6, u(R.string.schedule), this.M.g(I0()).d()), 6);
        } else if (sVarG instanceof s.c) {
            N(new e.b0().v(f.j.o(), ((s.c) sVarG).F), 2);
        } else if (sVarG instanceof s.b) {
            N(new e.z().u(new CharSequence[]{u(R.string.modify_months), u(R.string.modify_days_of_month)}), 3);
        }
    }

    @Override // e.j
    protected boolean p1(int i) {
        if (i != 2) {
            return true;
        }
        F1(this.M.g(I0()).C, 6, 6);
        return true;
    }

    @Override // e.j0.c
    protected void y() {
        if (h().getBoolean("changed", false)) {
            N(new e.m().u(u(R.string.check_save_message)), 10);
        } else {
            super.y();
        }
    }
}
