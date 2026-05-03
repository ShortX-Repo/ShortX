package c;

import a.b;
import a.z;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import cc.xposed.R;
import com.jozein.xedgepro.ApplicationMain;
import e.j0;
import e.y;
import f.h0;
import f.i0;
import g.x;
import java.util.StringTokenizer;

/* JADX INFO: loaded from: classes.dex */
public class j extends j0.b {
    private CheckBox C;
    private CheckBox D;
    private EditText E;
    private z F = null;

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void A(View view) {
        Editable text = this.E.getText();
        if (text.length() == 0) {
            return;
        }
        String string = text.toString();
        try {
            if (this.F == null) {
                this.F = d().h();
            }
            z zVar = this.F;
            if (zVar != null && zVar.f0(c(), string)) {
                t(string);
                return;
            }
            b.a0 a0VarY = y(string);
            if (!a0VarY.I && !ApplicationMain.isModuleActivated()) {
                i0.c(a0VarY, c(), h0.a(), this.F.o(25), this.F.o(36));
                return;
            }
            a.b.t(this.E.getContext(), a0VarY);
        } catch (Throwable th) {
            t(th.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void B(CompoundButton compoundButton, boolean z) {
        if (z) {
            this.D.setChecked(false);
            Editable text = this.E.getText();
            if (text.length() != 0 && z(text.toString())) {
                s(R.string.su_not_supported_on_system);
                compoundButton.setChecked(false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void C(CompoundButton compoundButton, boolean z) {
        if (z) {
            this.C.setChecked(false);
            Editable text = this.E.getText();
            if (text.length() != 0 && z(text.toString())) {
                s(R.string.not_need_to_exec_su_explicitly);
                compoundButton.setChecked(false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void D(DialogInterface dialogInterface, int i) {
        Editable text = this.E.getText();
        if (text.length() == 0) {
            return;
        }
        String string = text.toString();
        Bundle bundle = new Bundle(1);
        bundle.putParcelable("result", y(string));
        o(bundle);
    }

    private b.a0 y(String str) {
        boolean zIsChecked = this.D.isChecked();
        if (zIsChecked) {
            this.C.setChecked(false);
        }
        boolean zIsChecked2 = this.C.isChecked();
        boolean z = z(str);
        if (z && zIsChecked2) {
            this.C.setChecked(false);
            s(R.string.su_not_supported_on_system);
            zIsChecked2 = false;
        }
        if (z && zIsChecked) {
            this.D.setChecked(false);
            s(R.string.not_need_to_exec_su_explicitly);
            zIsChecked = false;
        }
        return new b.a0(zIsChecked2, zIsChecked, str);
    }

    private static boolean z(String str) {
        StringTokenizer stringTokenizer = new StringTokenizer(str, "\r\n");
        int iCountTokens = stringTokenizer.countTokens();
        for (int i = 0; i < iCountTokens; i++) {
            String strTrim = stringTokenizer.nextToken().trim();
            if ("su".equals(strTrim) || strTrim.startsWith("su ")) {
                return true;
            }
        }
        return false;
    }

    public j E(CharSequence charSequence, boolean z, boolean z2) {
        Bundle bundleE = e();
        bundleE.putCharSequence("text", charSequence);
        bundleE.putBoolean("system", z);
        bundleE.putBoolean("root", z2);
        return this;
    }

    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Bundle bundleE = e();
        Activity activity = getActivity();
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        int i = g().f641f;
        linearLayout.setPadding(i, i, i, i);
        g.p pVarM = m(f(R.drawable.ic_play));
        pVarM.setOnClickListener(new View.OnClickListener() { // from class: c.g
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.z.A(view);
            }
        });
        x xVarG = g();
        int i2 = xVarG.f637b;
        linearLayout.addView(pVarM, i2, i2);
        EditText editText = new EditText(activity);
        this.E = editText;
        r(editText);
        y.L(xVarG, null, this.E);
        this.E.setHint(R.string.command_hint);
        this.E.setInputType(131073);
        CharSequence charSequence = bundleE.getCharSequence("text");
        if (charSequence == null) {
            charSequence = "";
        }
        this.E.setText(charSequence);
        this.E.setSelection(charSequence.length());
        this.E.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16384)});
        linearLayout.addView(this.E);
        CheckBox checkBox = new CheckBox(activity);
        this.C = checkBox;
        checkBox.setChecked(bundleE.getBoolean("system", false));
        this.C.setText(R.string.command_on_system_process);
        linearLayout.addView(this.C);
        this.C.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: c.h
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                this.f65a.B(compoundButton, z);
            }
        });
        CheckBox checkBox2 = new CheckBox(activity);
        this.D = checkBox2;
        checkBox2.setChecked(bundleE.getBoolean("root", false));
        this.D.setText(R.string.command_root_access);
        linearLayout.addView(this.D);
        this.D.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: c.i
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                this.f66a.C(compoundButton, z);
            }
        });
        return new AlertDialog.Builder(activity).setTitle(R.string.action_command).setView(linearLayout).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: c.f
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i3) {
                this.z.D(dialogInterface, i3);
            }
        }).setNegativeButton(android.R.string.cancel, j0.b.B).create();
    }

    @Override // e.j0.b, android.app.DialogFragment, android.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        Bundle bundleE = e();
        bundleE.putBoolean("system", this.C.isChecked());
        bundleE.putBoolean("root", this.D.isChecked());
        bundleE.putCharSequence("text", this.E.getText());
    }
}
