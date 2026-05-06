package c;

import a.b;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.xposed.R;
import e.j0;
import e.y;
import g.x;

/* JADX INFO: loaded from: classes.dex */
public class m extends j0.b {
    private int C;
    private EditText D;

    class a implements CompoundButton.OnCheckedChangeListener {
        a() {
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            m mVar = m.this;
            if (z) {
                m.w(mVar, 1);
            } else {
                m.v(mVar, -2);
            }
        }
    }

    class b implements CompoundButton.OnCheckedChangeListener {
        b() {
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            m mVar = m.this;
            if (z) {
                m.w(mVar, 2);
            } else {
                m.v(mVar, -3);
            }
        }
    }

    class c implements CompoundButton.OnCheckedChangeListener {
        c() {
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            m mVar = m.this;
            if (z) {
                m.w(mVar, 4);
            } else {
                m.v(mVar, -5);
            }
        }
    }

    class d implements DialogInterface.OnClickListener {
        d() {
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            m.this.q("result", new b.u1(m.this.C, m.this.D.getText().toString()));
        }
    }

    static /* synthetic */ int v(m mVar, int i) {
        int i2 = i & mVar.C;
        mVar.C = i2;
        return i2;
    }

    static /* synthetic */ int w(m mVar, int i) {
        int i2 = i | mVar.C;
        mVar.C = i2;
        return i2;
    }

    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Bundle bundleE = e();
        this.C = bundleE.getInt("defaults", -1);
        CharSequence charSequence = bundleE.getCharSequence("content", "");
        Activity activity = getActivity();
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        x xVarG = g();
        int i = xVarG.f641f;
        linearLayout.setPadding(i, i, i, i);
        TextView textView = new TextView(activity);
        textView.setText(k(R.string.toast_description));
        linearLayout.addView(textView);
        EditText editText = new EditText(activity);
        this.D = editText;
        r(editText);
        this.D.setInputType(131073);
        this.D.setHint(R.string.notification_hint);
        this.D.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1024)});
        this.D.setText(charSequence);
        this.D.setSelection(charSequence.length());
        linearLayout.addView(this.D);
        y.L(xVarG, textView, this.D);
        CheckBox checkBox = new CheckBox(activity);
        checkBox.setText(R.string.sound);
        checkBox.setChecked((this.C & 1) != 0);
        checkBox.setOnCheckedChangeListener(new a());
        linearLayout.addView(checkBox);
        CheckBox checkBox2 = new CheckBox(activity);
        checkBox2.setText(R.string.vibrate);
        checkBox2.setChecked((this.C & 2) != 0);
        checkBox2.setOnCheckedChangeListener(new b());
        linearLayout.addView(checkBox2);
        CheckBox checkBox3 = new CheckBox(activity);
        checkBox3.setText(R.string.light);
        checkBox3.setChecked((this.C & 4) != 0);
        checkBox3.setOnCheckedChangeListener(new c());
        linearLayout.addView(checkBox3);
        return new AlertDialog.Builder(activity).setTitle(R.string.action_notify).setView(linearLayout).setPositiveButton(android.R.string.ok, new d()).setNegativeButton(android.R.string.cancel, j0.b.B).create();
    }

    @Override // e.j0.b, android.app.DialogFragment, android.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        Bundle bundleE = e();
        bundleE.putCharSequence("content", this.D.getText());
        bundleE.putInt("defaults", this.C);
    }

    public m z(CharSequence charSequence, int i) {
        Bundle bundleE = e();
        bundleE.putCharSequence("content", charSequence);
        bundleE.putInt("defaults", i);
        return this;
    }
}
