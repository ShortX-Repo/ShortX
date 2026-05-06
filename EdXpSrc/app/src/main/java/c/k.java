package c;

import a.b;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import cc.xposed.R;
import e.j0;

/* JADX INFO: loaded from: classes.dex */
public class k extends j0.b {
    private int C;
    private int D;
    private int E;

    class a implements DialogInterface.OnClickListener {
        a() {
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            Bundle bundle = new Bundle(1);
            bundle.putParcelable("result", new b.z0(k.this.C, k.this.D, k.this.E));
            k.this.o(bundle);
        }
    }

    class b implements CompoundButton.OnCheckedChangeListener {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ int f67a;

        b(int i) {
            this.f67a = i;
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            if (z) {
                k.this.D = this.f67a;
            }
        }
    }

    class c implements CompoundButton.OnCheckedChangeListener {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ int f69a;

        c(int i) {
            this.f69a = i;
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            k kVar = k.this;
            if (z) {
                k.z(kVar, this.f69a);
            } else {
                k.y(kVar, this.f69a ^ (-1));
            }
        }
    }

    private CheckBox B(Context context, CharSequence charSequence, int i) {
        CheckBox checkBox = new CheckBox(context);
        checkBox.setText(charSequence);
        if ((this.E & i) != 0) {
            checkBox.setChecked(true);
        }
        checkBox.setOnCheckedChangeListener(new c(i));
        return checkBox;
    }

    private RadioButton C(Context context, int i, int i2) {
        RadioButton radioButton = new RadioButton(context);
        radioButton.setText(i);
        radioButton.setOnCheckedChangeListener(new b(i2));
        return radioButton;
    }

    static /* synthetic */ int y(k kVar, int i) {
        int i2 = i & kVar.E;
        kVar.E = i2;
        return i2;
    }

    static /* synthetic */ int z(k kVar, int i) {
        int i2 = i | kVar.E;
        kVar.E = i2;
        return i2;
    }

    public k D(int i, int i2, int i3) {
        Bundle bundleE = e();
        bundleE.putInt("keycode", i);
        bundleE.putInt("key_action", i2);
        bundleE.putInt("modifiers", i3);
        return this;
    }

    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Activity activity = getActivity();
        Bundle bundleE = e();
        this.C = bundleE.getInt("keycode", 0);
        this.D = bundleE.getInt("key_action", 0);
        this.E = bundleE.getInt("modifiers", 0);
        int i = g().f641f;
        ScrollView scrollView = new ScrollView(activity);
        scrollView.setPadding(i, i, i, i);
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        scrollView.addView(linearLayout);
        RadioGroup radioGroup = new RadioGroup(activity);
        radioGroup.setOrientation(0);
        radioGroup.setPadding(0, i, 0, i * 2);
        linearLayout.addView(radioGroup);
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(-2, -2, 1.0f);
        RadioButton radioButtonC = C(activity, R.string.key_click, 0);
        radioGroup.addView(radioButtonC, layoutParams);
        RadioButton radioButtonC2 = C(activity, R.string.key_double_click, 1);
        radioGroup.addView(radioButtonC2, layoutParams);
        RadioButton radioButtonC3 = C(activity, R.string.key_long_press, 2);
        radioGroup.addView(radioButtonC3, layoutParams);
        int i2 = this.D;
        if (i2 == 0) {
            radioButtonC.setChecked(true);
        } else if (i2 == 1) {
            radioButtonC2.setChecked(true);
        } else if (i2 == 2) {
            radioButtonC3.setChecked(true);
        }
        LinearLayout linearLayout2 = new LinearLayout(activity);
        linearLayout2.setOrientation(0);
        linearLayout.addView(linearLayout2);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, -2, 1.0f);
        LinearLayout linearLayout3 = new LinearLayout(activity);
        linearLayout3.setOrientation(1);
        linearLayout2.addView(linearLayout3, layoutParams2);
        linearLayout3.addView(B(activity, "Shift", 1), layoutParams2);
        linearLayout3.addView(B(activity, "Alt", 2), layoutParams2);
        linearLayout3.addView(B(activity, "Ctrl", 4096), layoutParams2);
        LinearLayout linearLayout4 = new LinearLayout(activity);
        linearLayout4.setOrientation(1);
        linearLayout2.addView(linearLayout4, layoutParams2);
        linearLayout4.addView(B(activity, "Meta", 65536), layoutParams2);
        linearLayout4.addView(B(activity, "Fn", 8), layoutParams2);
        linearLayout4.addView(B(activity, "Sym", 4), layoutParams2);
        return new AlertDialog.Builder(activity).setTitle(KeyEvent.keyCodeToString(this.C)).setView(scrollView).setPositiveButton(android.R.string.ok, new a()).setNegativeButton(android.R.string.cancel, j0.b.B).create();
    }

    @Override // e.j0.b, android.app.DialogFragment, android.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        D(this.C, this.D, this.E);
        super.onSaveInstanceState(bundle);
    }
}
