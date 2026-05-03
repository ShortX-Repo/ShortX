package e;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import e.j0;

/* JADX INFO: loaded from: classes.dex */
public class g0 extends j0.b implements SeekBar.OnSeekBarChangeListener {
    private SeekBar D;
    private TextView C = null;
    private boolean E = false;

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void v(DialogInterface dialogInterface, int i) {
        p("result", this.D.getProgress() + e().getInt("min", 0));
    }

    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Activity activity = getActivity();
        Bundle bundleE = e();
        int i = g().f641f;
        int i2 = bundleE.getInt("min", 0);
        int i3 = bundleE.getInt("max");
        int i4 = bundleE.getInt("progress", i2);
        CharSequence charSequence = bundleE.getCharSequence("title");
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(i, i, i, i);
        CharSequence charSequence2 = bundleE.getCharSequence("description", null);
        this.E = charSequence2 == null;
        TextView textView = new TextView(activity);
        this.C = textView;
        textView.setTextSize(0, r1.f638c);
        this.C.setPadding(i, i, i, 0);
        TextView textView2 = this.C;
        if (this.E) {
            charSequence2 = Integer.toString((i4 + i2) * bundleE.getInt("scale", 1));
        }
        textView2.setText(charSequence2);
        linearLayout.addView(this.C);
        SeekBar seekBar = new SeekBar(activity);
        this.D = seekBar;
        seekBar.setMax(i3 - i2);
        this.D.setProgress(i4);
        int i5 = i * 3;
        this.D.setPadding(i5, i, i5, i);
        this.D.setOnSeekBarChangeListener(this);
        linearLayout.addView(this.D, -1, -2);
        return new AlertDialog.Builder(activity).setTitle(charSequence).setView(linearLayout).setNegativeButton(R.string.cancel, j0.b.B).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { // from class: e.f0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i6) {
                this.z.v(dialogInterface, i6);
            }
        }).create();
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        if (this.E) {
            Bundle bundleE = e();
            this.C.setText(Integer.toString((i + bundleE.getInt("min", 0)) * bundleE.getInt("scale", 1)));
        }
    }

    @Override // e.j0.b, android.app.DialogFragment, android.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        if (this.D != null) {
            e().putInt("progress", this.D.getProgress() + e().getInt("min", 0));
        }
        super.onSaveInstanceState(bundle);
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public g0 w(CharSequence charSequence, CharSequence charSequence2, int i, int i2) {
        return y(charSequence, charSequence2, 0, i, i2, 1);
    }

    public g0 x(CharSequence charSequence, CharSequence charSequence2, int i, int i2, int i3) {
        return y(charSequence, charSequence2, i, i2, i3, 1);
    }

    public g0 y(CharSequence charSequence, CharSequence charSequence2, int i, int i2, int i3, int i4) {
        Bundle bundleE = e();
        bundleE.putCharSequence("title", charSequence);
        bundleE.putCharSequence("description", charSequence2);
        bundleE.putInt("progress", i2 - i);
        bundleE.putInt("min", i);
        bundleE.putInt("max", i3);
        bundleE.putInt("scale", i4);
        return this;
    }

    public void z(CharSequence charSequence) {
        TextView textView = this.C;
        if (textView != null) {
            textView.setText(charSequence);
        }
    }
}
