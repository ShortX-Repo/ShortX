package c;

import a.h;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import cc.xposed.R;
import e.j0;

/* JADX INFO: loaded from: classes.dex */
public class e extends j0.b {
    private static final String[] C = new String[101];

    class a implements DialogInterface.OnClickListener {
        final /* synthetic */ Spinner A;
        final /* synthetic */ Spinner z;

        a(Spinner spinner, Spinner spinner2) {
            this.z = spinner;
            this.A = spinner2;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            e.this.q("result", new h.g(this.z.getSelectedItemPosition(), this.A.getSelectedItemPosition()));
        }
    }

    static {
        for (int i = 0; i < 101; i++) {
            C[i] = Integer.toString(i);
        }
    }

    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Activity activity = getActivity();
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(0);
        int i = g().f642g;
        linearLayout.setPadding(i, i, i, i);
        TextView textView = new TextView(activity);
        textView.setText(R.string.condition_battery_level);
        linearLayout.addView(textView);
        Spinner spinner = new Spinner(activity);
        ArrayAdapter arrayAdapter = new ArrayAdapter(activity, android.R.layout.simple_spinner_item, h.g.E);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter((SpinnerAdapter) arrayAdapter);
        linearLayout.addView(spinner);
        Spinner spinner2 = new Spinner(activity);
        ArrayAdapter arrayAdapter2 = new ArrayAdapter(activity, android.R.layout.simple_spinner_item, C);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter((SpinnerAdapter) arrayAdapter2);
        linearLayout.addView(spinner2);
        return new AlertDialog.Builder(activity).setView(linearLayout).setNegativeButton(android.R.string.cancel, j0.b.B).setPositiveButton(android.R.string.ok, new a(spinner, spinner2)).create();
    }
}
