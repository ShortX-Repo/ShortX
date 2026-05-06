package d;

import a.b;
import a.u;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import cc.xposed.R;
import e.j;

/* JADX INFO: loaded from: classes.dex */
public class c2 extends e.j {
    private u.b M;

    private void C1(String str) {
        b.s0.D(f(), 22, str);
    }

    private CharSequence D1(String str) {
        PackageManager packageManager = f().getPackageManager();
        try {
            return packageManager.getServiceInfo(new ComponentName(f.l.j, str), 512).loadLabel(packageManager);
        } catch (Throwable th) {
            f.v.d(th);
            return str;
        }
    }

    private boolean E1(String str) {
        try {
            int componentEnabledSetting = f().getPackageManager().getComponentEnabledSetting(new ComponentName(f.l.j, str));
            return componentEnabledSetting == 1 || componentEnabledSetting == 0;
        } catch (Throwable th) {
            f.v.d(th);
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void F1(String str, CompoundButton compoundButton, boolean z) {
        H1(str, z);
    }

    private void G1(String str) {
        b.s0.D(f(), 23, str);
    }

    private void H1(String str, boolean z) {
        int i = z ? 1 : 2;
        if (!z) {
            try {
                this.M.p(str);
                G1(str);
            } catch (Throwable th) {
                f.v.d(th);
                return;
            }
        }
        f().getPackageManager().setComponentEnabledSetting(new ComponentName(f.l.j, str), i, 1);
    }

    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.tiles);
    }

    @Override // e.j
    protected int B0() {
        u.b bVarD = a.u.d();
        this.M = bVarD;
        return bVarD.i();
    }

    @Override // e.j0.c
    protected void J(Bundle bundle, int i) {
        u.b bVar;
        b.d dVar;
        int i2;
        if (i == 1) {
            a.b bVar2 = (a.b) bundle.getParcelable("result");
            if (bVar2 == null) {
                return;
            }
            try {
                int iE0 = E0();
                if (bVar2 instanceof b.d) {
                    bVar = this.M;
                    dVar = (b.d) bVar2;
                } else {
                    bVar = this.M;
                    dVar = new b.d(bVar2);
                }
                bVar.n(iE0, dVar);
                j.f fVar = (j.f) L0(iE0);
                fVar.setSubText(bVar2.n(M0()));
                fVar.setImageDrawable(D0(bVar2));
                return;
            } catch (Throwable th) {
                g0(th);
                return;
            }
        }
        if (i == 2 && (i2 = bundle.getInt("result", -1)) >= 0) {
            int iE02 = E0();
            if (i2 == 0) {
                k1(iE02);
                return;
            }
            if (i2 == 1) {
                ((j.f) L0(iE02)).setChecked(true);
                C1(this.M.h(iE02));
            } else if (i2 == 2) {
                G1(this.M.h(iE02));
            } else {
                if (i2 != 3) {
                    return;
                }
                ((j.f) L0(iE02)).h();
            }
        }
    }

    @Override // e.j
    protected View h1(int i) {
        b.d dVarA = this.M.a(i);
        final String strH = this.M.h(i);
        j.f fVar = new j.f(D1(strH), dVarA.n(M0()), D0(dVarA), E1(strH));
        fVar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: d.b2
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                this.f328a.F1(strH, compoundButton, z);
            }
        });
        return fVar;
    }

    @Override // e.j
    protected void k1(int i) {
        P(new t().K1(this.M.a(i), 6), 1);
    }

    @Override // e.j
    protected boolean l1(int i) {
        j.f fVar = (j.f) L0(i);
        CharSequence[] charSequenceArr = new CharSequence[4];
        charSequenceArr[0] = u(R.string.edit_tile);
        charSequenceArr[1] = u(R.string.add_tile);
        charSequenceArr[2] = u(R.string.remove_tile);
        charSequenceArr[3] = u(fVar.g() ? R.string.disable_tile : R.string.enable_tile);
        N(new e.z().u(charSequenceArr), 2);
        return true;
    }
}
