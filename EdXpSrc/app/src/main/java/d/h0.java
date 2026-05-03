package d;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import cc.xposed.R;
import e.j;
import f.c0;

/* JADX INFO: loaded from: classes.dex */
public class h0 extends a {
    private a.k N;

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void E1(String str, CompoundButton compoundButton, boolean z) {
        try {
            if (z) {
                this.N.e(compoundButton.getContext(), str);
            } else {
                this.N.m(compoundButton.getContext(), str);
            }
        } catch (Throwable th) {
            g0(th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void F1(View view) {
        a.k kVar = this.N;
        if (kVar == null || kVar.j()) {
            return;
        }
        N(new e.m().u(u(R.string.check_clear_all)), 1);
    }

    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.hidden_apps);
        S(R.drawable.ic_delete, new View.OnClickListener() { // from class: d.f0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.z.F1(view);
            }
        });
    }

    @Override // d.a, e.j
    protected int B0() {
        this.N = a.k.h();
        return super.B0();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // d.a, e.j
    /* JADX INFO: renamed from: B1 */
    public j.k h1(int i) {
        c0.d dVar = this.M.get(i);
        final String strI = dVar.i();
        j.f fVar = new j.f(dVar.d(), dVar.f(), strI, this.N.g(strI));
        fVar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: d.g0
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                this.f337a.E1(strI, compoundButton, z);
            }
        });
        if (!dVar.m()) {
            fVar.e();
        }
        return fVar;
    }

    @Override // e.j0.c
    protected void J(Bundle bundle, int i) {
        if (bundle != null && i == 1 && bundle.getBoolean("result", false)) {
            try {
                this.N.f(f());
                int iH0 = H0();
                for (int i2 = 0; i2 < iH0; i2++) {
                    ((j.f) L0(i2)).setChecked(false);
                }
            } catch (Throwable th) {
                g0(th);
            }
        }
    }

    @Override // e.j
    protected void k1(int i) {
        ((j.f) L0(i)).h();
    }
}
