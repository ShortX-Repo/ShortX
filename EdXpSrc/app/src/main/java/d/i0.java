package d;

import android.os.Bundle;
import android.view.KeyEvent;
import cc.xposed.R;
import e.j;

/* JADX INFO: loaded from: classes.dex */
public class i0 extends e implements j.d {
    private static final int M;

    static {
        int iMax = Math.max(KeyEvent.getMaxKeyCode(), 320);
        int i = 220;
        while (i <= iMax && KeyEvent.keyCodeToString(i).charAt(0) > '9') {
            i++;
        }
        M = i - 1;
    }

    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.select_key_event);
    }

    @Override // e.j
    protected int B0() {
        return M;
    }

    @Override // d.e
    protected void B1(Bundle bundle, int i) {
        U(bundle);
        L();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // e.j
    /* JADX INFO: renamed from: E1, reason: merged with bridge method [inline-methods] */
    public j.k h1(int i) {
        int i2 = i + 1;
        return new j.k(KeyEvent.keyCodeToString(i2), ((Object) u(R.string.code)) + Integer.toString(i2));
    }

    @Override // e.j.d
    public String[] a() {
        String[] strArr = new String[M];
        int i = 0;
        while (i < M) {
            int i2 = i + 1;
            strArr[i] = KeyEvent.keyCodeToString(i2);
            if (strArr[i].startsWith("KEYCODE_")) {
                strArr[i] = strArr[i].substring(8);
            }
            i = i2;
        }
        return strArr;
    }

    @Override // e.j
    protected void k1(int i) {
        N(new c.k().D(i + 1, 0, 0), 1);
    }
}
