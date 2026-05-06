package c;

import android.os.Bundle;
import android.widget.SeekBar;
import cc.xposed.R;
import e.g0;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class n extends g0 {
    private CharSequence A(float f2) {
        if (f2 > 0.0f) {
            return String.format(Locale.ENGLISH, "%.2f inch.\n%.2f mm.", Float.valueOf(f2), Float.valueOf(f2 * 25.4f));
        }
        return ((Object) k(R.string.as_default)) + "\n";
    }

    public n B(CharSequence charSequence, float f2, float f3) {
        w(charSequence, A(f2), (int) (f2 * 200.0f), (int) (f3 * 200.0f));
        return this;
    }

    @Override // e.j0.b
    protected void o(Bundle bundle) {
        if (bundle != null) {
            bundle.putFloat("result_inch", bundle.getInt("result", 0) / 200.0f);
        }
        super.o(bundle);
    }

    @Override // e.g0, android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        z(A(i / 200.0f));
    }
}
