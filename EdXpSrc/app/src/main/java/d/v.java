package d;

import a.b;
import android.os.Bundle;
import android.view.View;
import cc.xposed.R;
import e.j;

/* JADX INFO: loaded from: classes.dex */
public class v extends e.j {
    static final String[] M = {"FX_KEY_CLICK", "FX_FOCUS_NAVIGATION_UP", "FX_FOCUS_NAVIGATION_DOWN", "FX_FOCUS_NAVIGATION_LEFT", "FX_FOCUS_NAVIGATION_RIGHT", "FX_KEYPRESS_STANDARD", "FX_KEYPRESS_SPACEBAR", "FX_KEYPRESS_DELETE", "FX_KEYPRESS_RETURN"};

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void C1(int i, View view) {
        V("result", i);
        L();
    }

    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.effect_sound);
    }

    @Override // e.j
    protected int B0() {
        return M.length;
    }

    @Override // e.j0.c
    protected void J(Bundle bundle, int i) {
        if (bundle == null || bundle.getInt("result", -1) != 0) {
            return;
        }
        V("result", E0());
        L();
    }

    @Override // e.j
    protected View h1(final int i) {
        j.g gVar = new j.g(M[i], (CharSequence) null, C0(R.drawable.ic_select));
        gVar.E.setOnClickListener(new View.OnClickListener() { // from class: d.u
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.z.C1(i, view);
            }
        });
        return gVar;
    }

    @Override // e.j
    protected void k1(int i) {
        b.s0.C(f(), 18, i);
    }

    @Override // e.j
    protected boolean l1(int i) {
        M(new e.z().u(new CharSequence[]{u(R.string.select)}));
        return true;
    }
}
