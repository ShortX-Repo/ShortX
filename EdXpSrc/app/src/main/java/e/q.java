package e;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import cc.xposed.R;
import e.j0;

/* JADX INFO: loaded from: classes.dex */
public class q extends j0.b {
    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void v(CharSequence charSequence, DialogInterface dialogInterface, int i) {
        ClipboardManager clipboardManager;
        if (charSequence == null || charSequence.length() == 0 || (clipboardManager = (ClipboardManager) getActivity().getSystemService("clipboard")) == null) {
            return;
        }
        clipboardManager.setText(charSequence);
        s(R.string.text_copied);
    }

    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String string;
        Activity activity = getActivity();
        int i = g().f641f;
        Bundle bundleE = e();
        ScrollView scrollView = new ScrollView(activity);
        scrollView.setFillViewport(true);
        FrameLayout frameLayout = new FrameLayout(activity);
        scrollView.addView(frameLayout);
        TextView textView = new TextView(activity);
        CharSequence charSequence = bundleE.getCharSequence("description");
        final CharSequence charSequence2 = bundleE.getCharSequence("text", "");
        if (charSequence == null) {
            string = charSequence2.toString();
        } else {
            string = ((Object) charSequence) + "\n\n" + ((Object) charSequence2);
        }
        textView.setText(string);
        textView.setTextIsSelectable(true);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -1, 17);
        int i2 = i * 2;
        layoutParams.leftMargin = i2;
        layoutParams.topMargin = i2;
        layoutParams.rightMargin = i2;
        layoutParams.bottomMargin = i2;
        frameLayout.addView(textView, layoutParams);
        return new AlertDialog.Builder(activity).setTitle(bundleE.getCharSequence("title")).setView(scrollView).setPositiveButton(android.R.string.ok, j0.b.B).setNegativeButton(android.R.string.copy, new DialogInterface.OnClickListener() { // from class: e.p
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i3) {
                this.z.v(charSequence2, dialogInterface, i3);
            }
        }).create();
    }

    public q w(CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3) {
        Bundle bundleE = e();
        bundleE.putCharSequence("title", charSequence);
        bundleE.putCharSequence("text", charSequence2);
        bundleE.putCharSequence("description", charSequence3);
        return this;
    }
}
