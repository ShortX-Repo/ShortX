package d;

import android.os.Bundle;
import cc.xposed.R;

/* JADX INFO: loaded from: classes.dex */
public class h extends a {
    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.select_icon);
    }

    @Override // e.j
    protected void k1(int i) {
        Bundle bundle = new Bundle(1);
        bundle.putString("result", this.M.get(i).i());
        U(bundle);
        L();
    }
}
