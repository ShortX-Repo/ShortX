package e;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ProgressBar;
import e.j0;

/* JADX INFO: loaded from: classes.dex */
public class e0 extends j0.b {
    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean v(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
        return true;
    }

    @Override // e.j0.b
    protected void n() {
    }

    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Activity activity = getActivity();
        AlertDialog alertDialogCreate = new AlertDialog.Builder(activity).setView(new ProgressBar(activity)).setTitle(e().getCharSequence("title")).setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: e.d0
            @Override // android.content.DialogInterface.OnKeyListener
            public final boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                return e0.v(dialogInterface, i, keyEvent);
            }
        }).create();
        alertDialogCreate.setCanceledOnTouchOutside(false);
        alertDialogCreate.setCancelable(false);
        return alertDialogCreate;
    }
}
