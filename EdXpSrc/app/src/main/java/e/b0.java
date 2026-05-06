package e;

import android.os.Bundle;

/* JADX INFO: loaded from: classes.dex */
public class b0 extends c0 {
    @Override // e.j0.b
    protected void o(Bundle bundle) {
        boolean[] booleanArray;
        if (bundle == null || (booleanArray = bundle.getBooleanArray("result")) == null) {
            return;
        }
        int i = 0;
        for (int i2 = 0; i2 < booleanArray.length; i2++) {
            if (booleanArray[i2]) {
                i |= 1 << i2;
            }
        }
        bundle.putInt("result", i);
        super.o(bundle);
    }

    public b0 v(CharSequence[] charSequenceArr, int i) {
        boolean[] zArr = new boolean[charSequenceArr.length];
        for (int i2 = 0; i2 < charSequenceArr.length; i2++) {
            boolean z = true;
            if (((1 << i2) & i) == 0) {
                z = false;
            }
            zArr[i2] = z;
        }
        u(charSequenceArr, zArr);
        return this;
    }
}
