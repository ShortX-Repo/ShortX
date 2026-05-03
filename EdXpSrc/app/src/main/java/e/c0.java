package e;

import android.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import e.j0;

/* JADX INFO: loaded from: classes.dex */
public class c0 extends j0.b {

    class a implements DialogInterface.OnClickListener {
        final /* synthetic */ Bundle z;

        a(Bundle bundle) {
            this.z = bundle;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            boolean[] booleanArray = this.z.getBooleanArray("result");
            boolean[] booleanArray2 = this.z.getBooleanArray("ori");
            if (booleanArray == null || booleanArray2 == null) {
                return;
            }
            for (int i2 = 0; i2 < booleanArray2.length; i2++) {
                if (booleanArray[i2] != booleanArray2[i2]) {
                    c0.this.o(this.z);
                    return;
                }
            }
        }
    }

    class b implements DialogInterface.OnMultiChoiceClickListener {
        b() {
        }

        @Override // android.content.DialogInterface.OnMultiChoiceClickListener
        public void onClick(DialogInterface dialogInterface, int i, boolean z) {
        }
    }

    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Bundle bundleE = e();
        return new AlertDialog.Builder(getActivity()).setMultiChoiceItems(bundleE.getCharSequenceArray("items"), bundleE.getBooleanArray("result"), new b()).setNegativeButton(R.string.cancel, j0.b.B).setPositiveButton(R.string.ok, new a(bundleE)).create();
    }

    public c0 u(CharSequence[] charSequenceArr, boolean[] zArr) {
        Bundle bundleE = e();
        bundleE.putBooleanArray("result", zArr);
        bundleE.putCharSequenceArray("items", charSequenceArr);
        boolean[] zArr2 = new boolean[zArr.length];
        System.arraycopy(zArr, 0, zArr2, 0, zArr.length);
        bundleE.putBooleanArray("ori", zArr2);
        return this;
    }
}
