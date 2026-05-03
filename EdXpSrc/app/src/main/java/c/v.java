package c;

import a.b;
import a.v;
import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import e.j0;
import e.y;
import g.x;

/* JADX INFO: loaded from: classes.dex */
public class v extends j0.b {
    private static final String[] G = {" = ", " += ", " -= "};
    private String[] C;
    private Spinner D;
    private Spinner E;
    private EditText F;

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ CharSequence w(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
        if (i == i2) {
            return null;
        }
        while (i < i2) {
            char cCharAt = charSequence.charAt(i);
            if ((cCharAt < 'A' || cCharAt > 'Z') && ((cCharAt < 'a' || cCharAt > 'z') && ((cCharAt < '0' || cCharAt > '9') && " _$+-*/%()<>&^|~".indexOf(cCharAt) < 0))) {
                return "";
            }
            i++;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void x(DialogInterface dialogInterface, int i) {
        String string = this.F.getText().toString();
        String str = this.C[this.D.getSelectedItemPosition()];
        try {
            f.i.d(string);
            q("result", new b.p2(str, G[this.E.getSelectedItemPosition()].trim(), string));
        } catch (Throwable th) {
            t(th.getMessage());
            f.v.d(th);
        }
    }

    public static EditText y(Context context, CharSequence charSequence) {
        EditText editText = new EditText(context);
        editText.setInputType(1);
        editText.setText(charSequence);
        if (charSequence != null) {
            editText.setSelection(charSequence.length());
        }
        editText.setFilters(new InputFilter[]{new InputFilter() { // from class: c.u
            @Override // android.text.InputFilter
            public final CharSequence filter(CharSequence charSequence2, int i, int i2, Spanned spanned, int i3, int i4) {
                return v.w(charSequence2, i, i2, spanned, i3, i4);
            }
        }, new InputFilter.LengthFilter(32)});
        return editText;
    }

    public v A(String str, String str2, String str3) {
        Bundle bundleE = e();
        bundleE.putString("variable", str);
        int i = 0;
        while (true) {
            String[] strArr = G;
            if (i >= strArr.length) {
                break;
            }
            if (strArr[i].contains(str2)) {
                bundleE.putInt("operator", i);
                break;
            }
            i++;
        }
        bundleE.putCharSequence("expression", str3);
        return this;
    }

    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Activity activity = getActivity();
        Bundle bundleE = e();
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        x xVarG = g();
        int i = xVarG.f641f;
        linearLayout.setPadding(i, i, i, i);
        v.a aVarG = v.a.g();
        int iK = aVarG.k();
        this.C = new String[aVarG.k()];
        String string = bundleE.getString("variable");
        int i2 = -1;
        for (int i3 = 0; i3 < iK; i3++) {
            this.C[i3] = aVarG.f(i3);
            if (i2 == -1 && string != null && string.equals(this.C[i3])) {
                i2 = i3;
            }
        }
        this.D = new Spinner(activity);
        ArrayAdapter arrayAdapter = new ArrayAdapter(activity, R.layout.simple_spinner_item, this.C);
        arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        this.D.setAdapter((SpinnerAdapter) arrayAdapter);
        if (i2 != -1) {
            this.D.setSelection(i2);
        }
        linearLayout.addView(this.D);
        this.E = new Spinner(activity);
        ArrayAdapter arrayAdapter2 = new ArrayAdapter(activity, R.layout.simple_spinner_item, G);
        arrayAdapter2.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        this.E.setAdapter((SpinnerAdapter) arrayAdapter2);
        int i4 = bundleE.getInt("operator", -1);
        if (i4 > 0) {
            this.E.setSelection(i4);
        }
        linearLayout.addView(this.E, -2, -2);
        EditText editTextY = y(activity, bundleE.getCharSequence("expression", "1"));
        this.F = editTextY;
        r(editTextY);
        linearLayout.addView(this.F, -1, -2);
        TextView textView = new TextView(activity);
        textView.setText(cc.xposed.R.string.variable_expression_hint);
        textView.setTextSize(0, xVarG.f640e);
        textView.setPadding(i, i, i, i);
        linearLayout.addView(textView);
        y.L(xVarG, textView, this.F);
        ScrollView scrollView = new ScrollView(activity);
        scrollView.setFillViewport(true);
        scrollView.addView(linearLayout);
        return new AlertDialog.Builder(activity).setView(scrollView).setNegativeButton(R.string.cancel, j0.b.B).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { // from class: c.t
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i5) {
                this.z.x(dialogInterface, i5);
            }
        }).create();
    }

    @Override // e.j0.b, android.app.DialogFragment, android.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        Bundle bundleE = e();
        bundleE.putString("variable", this.C[this.D.getSelectedItemPosition()]);
        bundleE.putInt("operator", this.E.getSelectedItemPosition());
        bundleE.putCharSequence("expression", this.F.getText());
        super.onSaveInstanceState(bundle);
    }

    public v z(String str) {
        e().putString("variable", str);
        return this;
    }
}
