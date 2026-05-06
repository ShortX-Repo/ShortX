package d;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;
import cc.xposed.R;
import e.j0;

/* JADX INFO: loaded from: classes.dex */
public class l0 extends j0.c {
    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void i0(View view) {
        ClipboardManager clipboardManager;
        CharSequence charSequence = h().getCharSequence("logs", "");
        if (charSequence.length() <= 0 || (clipboardManager = (ClipboardManager) getActivity().getSystemService("clipboard")) == null) {
            return;
        }
        clipboardManager.setText(charSequence);
        e0(R.string.text_copied);
    }

    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.logs);
        S(R.drawable.ic_content_copy, new View.OnClickListener() { // from class: d.k0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.z.i0(view);
            }
        });
    }

    public l0 j0(CharSequence charSequence) {
        h().putCharSequence("logs", charSequence);
        return this;
    }

    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Context context = viewGroup != null ? viewGroup.getContext() : f();
        ScrollView scrollView = new ScrollView(context);
        scrollView.setFillViewport(true);
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(context);
        scrollView.addView(horizontalScrollView);
        TextView textView = new TextView(context);
        Y(textView);
        textView.setText(h().getCharSequence("logs", ""));
        textView.setTextIsSelectable(true);
        horizontalScrollView.addView(textView);
        return scrollView;
    }
}
