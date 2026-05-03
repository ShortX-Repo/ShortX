package e;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import e.j0;

/* JADX INFO: loaded from: classes.dex */
public class y extends j0.b {
    private TextView C;
    private EditText D;

    public static String A(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
        StringBuilder sb = new StringBuilder((((spanned.length() - i4) + i3) + i2) - i);
        sb.append((CharSequence) spanned);
        sb.replace(i3, i4, charSequence.subSequence(i, i2).toString());
        return sb.toString();
    }

    private static boolean B(String str) {
        for (int length = str.length() - 1; length >= 0; length--) {
            if ("/\\:*?\"<>|".indexOf(str.charAt(length)) >= 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean C(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException unused) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ CharSequence D(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
        if (i == i2) {
            return null;
        }
        while (i < i2) {
            char cCharAt = charSequence.charAt(i);
            if ((cCharAt < 'A' || cCharAt > 'Z') && ((cCharAt < 'a' || cCharAt > 'z') && !((cCharAt >= '0' && cCharAt <= '9') || cCharAt == '_' || cCharAt == '$'))) {
                return "";
            }
            i++;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ CharSequence E(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
        if (i == i2 || B(charSequence.subSequence(i, i2).toString())) {
            return null;
        }
        return "";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ CharSequence F(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
        if (i == i2) {
            return null;
        }
        while (i < i2) {
            char cCharAt = charSequence.charAt(i);
            if (cCharAt <= 0 || cCharAt >= 128) {
                return "";
            }
            i++;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ CharSequence G(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
        char cCharAt;
        if (i == i2) {
            return null;
        }
        if (i3 == 0 && charSequence.length() > 0 && '0' <= (cCharAt = charSequence.charAt(0)) && cCharAt <= '9') {
            return "";
        }
        while (i < i2) {
            char cCharAt2 = charSequence.charAt(i);
            if ((cCharAt2 < 'A' || cCharAt2 > 'Z') && ((cCharAt2 < 'a' || cCharAt2 > 'z') && !((cCharAt2 >= '0' && cCharAt2 <= '9') || cCharAt2 == '_' || cCharAt2 == '$'))) {
                return "";
            }
            i++;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ CharSequence H(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
        if (i == i2 || C(A(charSequence, i, i2, spanned, i3, i4))) {
            return null;
        }
        return "";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void I(DialogInterface dialogInterface, int i) {
        Bundle bundle = new Bundle(1);
        bundle.putCharSequence("result", this.D.getText());
        o(bundle);
    }

    public static void L(g.x xVar, TextView textView, EditText editText) {
        if (textView != null) {
            textView.setTextSize(0, xVar.f639d);
        }
        if (editText != null) {
            editText.setTextSize(0, xVar.f640e);
        }
    }

    public y J(CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, int i, int i2) {
        Bundle bundleE = e();
        bundleE.putCharSequence("title", charSequence);
        bundleE.putCharSequence("description", charSequence2);
        bundleE.putCharSequence("text", charSequence3);
        bundleE.putInt("input_type", i);
        bundleE.putInt("max_length", i2);
        return this;
    }

    public y K(CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3) {
        return J(charSequence, charSequence2, charSequence3, 3, 64);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        InputFilter inputFilter;
        Bundle bundleE = e();
        Activity activity = getActivity();
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        g.x xVarG = g();
        int i = xVarG.f641f;
        linearLayout.setPadding(i, i, i, i);
        TextView textView = new TextView(activity);
        this.C = textView;
        textView.setText(bundleE.getCharSequence("description"));
        linearLayout.addView(this.C);
        EditText editText = new EditText(activity);
        this.D = editText;
        r(editText);
        switch (bundleE.getInt("input_type")) {
            case 1:
                this.D.setInputType(2);
                inputFilter = null;
                break;
            case 2:
                this.D.setInputType(1);
                inputFilter = new InputFilter() { // from class: e.w
                    @Override // android.text.InputFilter
                    public final CharSequence filter(CharSequence charSequence, int i2, int i3, Spanned spanned, int i4, int i5) {
                        return y.D(charSequence, i2, i3, spanned, i4, i5);
                    }
                };
                break;
            case 3:
                this.D.setInputType(1);
                inputFilter = null;
                break;
            case 4:
                this.D.setInputType(131073);
                inputFilter = new InputFilter() { // from class: e.x
                    @Override // android.text.InputFilter
                    public final CharSequence filter(CharSequence charSequence, int i2, int i3, Spanned spanned, int i4, int i5) {
                        return y.F(charSequence, i2, i3, spanned, i4, i5);
                    }
                };
                break;
            case 5:
                this.D.setInputType(131073);
                inputFilter = null;
                break;
            case 6:
                this.D.setInputType(1);
                inputFilter = new InputFilter() { // from class: e.v
                    @Override // android.text.InputFilter
                    public final CharSequence filter(CharSequence charSequence, int i2, int i3, Spanned spanned, int i4, int i5) {
                        return y.G(charSequence, i2, i3, spanned, i4, i5);
                    }
                };
                break;
            case 7:
                this.D.setInputType(2);
                inputFilter = new InputFilter() { // from class: e.t
                    @Override // android.text.InputFilter
                    public final CharSequence filter(CharSequence charSequence, int i2, int i3, Spanned spanned, int i4, int i5) {
                        return y.H(charSequence, i2, i3, spanned, i4, i5);
                    }
                };
                break;
            case 8:
                this.D.setInputType(1);
                inputFilter = new InputFilter() { // from class: e.u
                    @Override // android.text.InputFilter
                    public final CharSequence filter(CharSequence charSequence, int i2, int i3, Spanned spanned, int i4, int i5) {
                        return y.E(charSequence, i2, i3, spanned, i4, i5);
                    }
                };
                break;
            default:
                inputFilter = null;
                break;
        }
        this.D.setFilters(inputFilter != null ? new InputFilter[]{new InputFilter.LengthFilter(bundleE.getInt("max_length")), inputFilter} : new InputFilter[]{new InputFilter.LengthFilter(bundleE.getInt("max_length"))});
        CharSequence charSequence = bundleE.getCharSequence("text", "");
        this.D.setText(charSequence);
        this.D.setSelection(charSequence.length());
        linearLayout.addView(this.D);
        L(xVarG, this.C, this.D);
        return new AlertDialog.Builder(activity).setTitle(bundleE.getCharSequence("title")).setView(linearLayout).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { // from class: e.s
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                this.z.I(dialogInterface, i2);
            }
        }).setNegativeButton(R.string.cancel, j0.b.B).create();
    }

    @Override // e.j0.b, android.app.DialogFragment, android.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        Bundle bundleE = e();
        bundleE.putCharSequence("description", this.C.getText());
        bundleE.putCharSequence("text", this.D.getText());
    }
}
