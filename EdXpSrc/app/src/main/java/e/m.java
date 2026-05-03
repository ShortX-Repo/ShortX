package e;

import android.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import e.j0;

/* JADX INFO: loaded from: classes.dex */
public class m extends j0.b {

    class a implements DialogInterface.OnClickListener {
        a() {
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            Bundle bundle = new Bundle(1);
            bundle.putBoolean("result", i == -1);
            m.this.o(bundle);
        }
    }

    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        a aVar = new a();
        Bundle bundleE = e();
        CharSequence charSequence = bundleE.getCharSequence("title");
        if (charSequence == null) {
            charSequence = k(R.string.dialog_alert_title);
        }
        return new AlertDialog.Builder(getActivity()).setTitle(charSequence).setIcon(R.drawable.ic_dialog_alert).setMessage(bundleE.getCharSequence("message")).setPositiveButton(R.string.yes, aVar).setNegativeButton(R.string.cancel, aVar).create();
    }

    public m u(CharSequence charSequence) {
        return v(null, charSequence);
    }

    public m v(CharSequence charSequence, CharSequence charSequence2) {
        Bundle bundleE = e();
        bundleE.putCharSequence("title", charSequence);
        bundleE.putCharSequence("message", charSequence2);
        return this;
    }
}
