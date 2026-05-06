package d;

import android.os.Bundle;
import cc.xposed.R;
import com.jozein.xedgepro.ui.ActivityPerformAction;

/* JADX INFO: loaded from: classes.dex */
public abstract class e extends e.j {
    protected abstract void B1(Bundle bundle, int i);

    protected void C1(a.b bVar) {
        W("result", bVar);
        L();
    }

    protected void D1(a.b bVar) {
        h().putParcelable("action", bVar);
        int i = bVar.z;
        CharSequence[] charSequenceArr = (i == 0 || i == 1 || i == 51 || i == 68 || i == 106 || i == 107) ? new CharSequence[]{u(R.string.select)} : new CharSequence[]{u(R.string.select), u(R.string.perform), u(R.string.put_on_launcher), u(R.string.export_as_command)};
        e.z zVar = new e.z();
        zVar.u(charSequenceArr);
        N(zVar, 65535);
    }

    @Override // e.j0.c
    protected final void J(Bundle bundle, int i) {
        if (bundle == null) {
            return;
        }
        if (i != 65535) {
            B1(bundle, i);
            return;
        }
        int i2 = bundle.getInt("result", -1);
        a.b bVar = (a.b) h().getParcelable("action");
        if (bVar == null) {
            return;
        }
        if (i2 == 0) {
            C1(bVar);
            return;
        }
        if (i2 == 1) {
            ActivityPerformAction.a(f(), bVar);
            return;
        }
        if (i2 == 2) {
            f.l0.i(f(), bVar);
            return;
        }
        if (i2 != 3) {
            return;
        }
        String str = null;
        try {
            a.z zVarH = g().h();
            if (zVarH == null || !zVarH.o(32)) {
                str = "#" + ((Object) u(R.string.perform_by_broadcast_not_allowed));
            }
        } catch (Throwable th) {
            f.v.d(th);
        }
        N(new e.q().w(bVar.n(f()), a.b.g(bVar), str), 65536);
    }
}
