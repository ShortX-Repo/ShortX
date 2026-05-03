package c;

import a.h;
import a.v;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import cc.xposed.R;
import e.j0;

/* JADX INFO: loaded from: classes.dex */
public class d extends j0.b {
    private String[] C;
    private Spinner D;
    private EditText E;
    private Spinner F;
    private EditText G;

    class a implements AdapterView.OnItemSelectedListener {
        a() {
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
            d.this.E.setEnabled(i == 0);
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void w(DialogInterface dialogInterface, int i) {
        int selectedItemPosition = this.D.getSelectedItemPosition();
        String string = selectedItemPosition != 0 ? this.C[selectedItemPosition] : this.E.getText().toString();
        String string2 = this.G.getText().toString();
        try {
            f.i.d(string);
            f.i.d(string2);
            q("result", new h.i0(string, h.i0.H[this.F.getSelectedItemPosition()].trim(), string2));
        } catch (NumberFormatException e2) {
            t(e2.getMessage());
        }
    }

    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Activity activity = getActivity();
        Bundle bundleE = e();
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        int i = g().f641f;
        linearLayout.setPadding(i, i, i, i);
        v.a aVarG = v.a.g();
        int iK = aVarG.k();
        String[] strArr = new String[iK + 1];
        this.C = strArr;
        strArr[0] = activity.getString(R.string.input_expression);
        int i2 = 0;
        while (i2 < iK) {
            int i3 = i2 + 1;
            this.C[i3] = aVarG.f(i2);
            i2 = i3;
        }
        int i4 = bundleE.getInt("variable", 0);
        this.D = new Spinner(activity);
        ArrayAdapter arrayAdapter = new ArrayAdapter(activity, android.R.layout.simple_spinner_item, this.C);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.D.setAdapter((SpinnerAdapter) arrayAdapter);
        this.D.setSelection(i4);
        linearLayout.addView(this.D);
        EditText editTextY = v.y(activity, bundleE.getCharSequence("expression1", "1"));
        this.E = editTextY;
        if (i4 != 0) {
            editTextY.setEnabled(false);
        }
        linearLayout.addView(this.E);
        this.D.setOnItemSelectedListener(new a());
        this.F = new Spinner(activity);
        ArrayAdapter arrayAdapter2 = new ArrayAdapter(activity, android.R.layout.simple_spinner_item, h.i0.H);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.F.setAdapter((SpinnerAdapter) arrayAdapter2);
        this.F.setSelection(bundleE.getInt("operator", 0));
        linearLayout.addView(this.F, -2, -2);
        EditText editTextY2 = v.y(activity, bundleE.getCharSequence("expression2", "1"));
        this.G = editTextY2;
        linearLayout.addView(editTextY2);
        TextView textView = new TextView(activity);
        textView.setText(R.string.variable_expression_hint);
        textView.setTextSize(0, r3.f640e);
        textView.setPadding(i, i, i, i);
        linearLayout.addView(textView);
        ScrollView scrollView = new ScrollView(activity);
        scrollView.setFillViewport(true);
        scrollView.addView(linearLayout);
        return new AlertDialog.Builder(activity).setView(scrollView).setNegativeButton(android.R.string.cancel, j0.b.B).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: c.c
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i5) {
                this.z.w(dialogInterface, i5);
            }
        }).create();
    }

    @Override // e.j0.b, android.app.DialogFragment, android.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        Bundle bundleE = e();
        bundleE.putInt("variable", this.D.getSelectedItemPosition());
        bundleE.putInt("operator", this.F.getSelectedItemPosition());
        bundleE.putCharSequence("expression1", this.E.getText());
        bundleE.putCharSequence("expression2", this.G.getText());
        super.onSaveInstanceState(bundle);
    }
}
