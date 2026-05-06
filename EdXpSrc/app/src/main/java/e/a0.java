package e;

import android.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import e.j0;

/* JADX INFO: loaded from: classes.dex */
public class a0 extends j0.b {
    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Bundle bundleE = e();
        return new AlertDialog.Builder(getActivity()).setTitle(bundleE.getCharSequence("title", "")).setMessage(bundleE.getCharSequence("message", "")).setPositiveButton(R.string.ok, j0.b.B).create();
    }

    public a0 u(CharSequence charSequence, CharSequence charSequence2) {
        Bundle bundleE = e();
        bundleE.putCharSequence("title", charSequence);
        bundleE.putCharSequence("message", charSequence2);
        return this;
    }
}
