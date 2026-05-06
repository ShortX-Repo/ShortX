package d;

import android.os.Bundle;
import cc.xposed.R;

/* JADX INFO: loaded from: classes.dex */
public class u0 extends a {
    @Override // e.j0.c
    protected void B() {
        super.B();
        CharSequence charSequence = h().getCharSequence("title");
        if (charSequence != null) {
            d0(charSequence);
        } else {
            c0(R.string.select_app);
        }
    }

    public u0 C1(CharSequence charSequence) {
        h().putCharSequence("title", charSequence);
        return this;
    }

    @Override // e.j
    protected void k1(int i) {
        Bundle bundle = new Bundle(1);
        bundle.putString("result", this.M.get(i).i());
        U(bundle);
        L();
    }
}
