package e;

import android.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import e.j0;

/* JADX INFO: loaded from: classes.dex */
public class z extends j0.b {

    private class b implements DialogInterface.OnClickListener {
        private b() {
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            if (i != -2) {
                if (i != z.this.e().getInt("checked", -1)) {
                    z.this.p("result", i);
                }
                z.this.dismiss();
            }
        }
    }

    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Bundle bundleE = e();
        int i = bundleE.getInt("checked", -1);
        b bVar = new b();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        CharSequence[] charSequenceArray = bundleE.getCharSequenceArray("items");
        if (i == -1) {
            builder.setItems(charSequenceArray, bVar);
        } else {
            builder.setSingleChoiceItems(charSequenceArray, i, bVar);
        }
        return builder.setNegativeButton(R.string.cancel, bVar).create();
    }

    public z u(CharSequence[] charSequenceArr) {
        e().putCharSequenceArray("items", charSequenceArr);
        return this;
    }

    public z v(CharSequence[] charSequenceArr, int i) {
        Bundle bundleE = e();
        bundleE.putInt("checked", i);
        bundleE.putCharSequenceArray("items", charSequenceArr);
        return this;
    }
}
