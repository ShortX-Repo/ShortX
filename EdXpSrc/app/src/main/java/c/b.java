package c;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;
import cc.xposed.R;
import e.j0;

/* JADX INFO: loaded from: classes.dex */
public class b extends j0.b {
    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void v(CharSequence charSequence, DialogInterface dialogInterface, int i) {
        ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService("clipboard");
        if (clipboardManager != null) {
            clipboardManager.setText(charSequence);
            s(R.string.text_copied);
        }
    }

    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Activity activity = getActivity();
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(activity);
        int i = g().f641f;
        horizontalScrollView.setPadding(i, i, i, i);
        horizontalScrollView.setFillViewport(true);
        ScrollView scrollView = new ScrollView(activity);
        scrollView.setFillViewport(true);
        horizontalScrollView.addView(scrollView);
        FrameLayout frameLayout = new FrameLayout(activity);
        scrollView.addView(frameLayout);
        TextView textView = new TextView(activity);
        r(textView);
        a.b bVar = (a.b) e().getParcelable("action");
        final StringBuilder sbK = bVar != null ? bVar.k(activity) : null;
        textView.setText(sbK);
        textView.setTextIsSelectable(true);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -1, 17);
        int i2 = i * 2;
        layoutParams.leftMargin = i2;
        layoutParams.topMargin = i2;
        layoutParams.rightMargin = i2;
        layoutParams.bottomMargin = i2;
        frameLayout.addView(textView, layoutParams);
        return new AlertDialog.Builder(activity).setTitle(R.string.action_info).setView(horizontalScrollView).setPositiveButton(android.R.string.ok, j0.b.B).setNegativeButton(android.R.string.copy, new DialogInterface.OnClickListener() { // from class: c.a
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i3) {
                this.z.v(sbK, dialogInterface, i3);
            }
        }).create();
    }

    public b w(a.b bVar) {
        e().putParcelable("action", bVar);
        return this;
    }
}
