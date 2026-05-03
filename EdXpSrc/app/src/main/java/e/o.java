package e;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import e.j0;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class o extends j0.b {
    private int C;
    private int D;
    private int E;
    private ImageView F;
    private EditText G;
    private ColorDrawable H;
    private final SeekBar[] I = new SeekBar[4];
    private boolean J = true;

    class a implements InputFilter {
        a() {
        }

        @Override // android.text.InputFilter
        public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
            if (i == i2) {
                return null;
            }
            while (i < i2) {
                char cCharAt = charSequence.charAt(i);
                if ((cCharAt < 'A' || cCharAt > 'F') && ((cCharAt < 'a' || cCharAt > 'f') && (cCharAt < '0' || cCharAt > '9'))) {
                    return "";
                }
                i++;
            }
            return null;
        }
    }

    class b implements TextWatcher {
        b() {
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
            if (editable.length() > 0) {
                try {
                    int i = (int) Long.parseLong(editable.toString(), 16);
                    if (!o.this.J) {
                        i |= -16777216;
                    }
                    for (int i2 = 0; i2 < 4; i2++) {
                        SeekBar seekBar = o.this.I[i2];
                        if (seekBar != null) {
                            seekBar.setProgress((i >>> (i2 * 8)) & 255);
                        }
                    }
                    o.this.C = i;
                } catch (Throwable th) {
                    o.this.t(th.getMessage());
                    f.v.d(th);
                    return;
                }
            } else {
                for (int i3 = 0; i3 < 4; i3++) {
                    SeekBar seekBar2 = o.this.I[i3];
                    if (seekBar2 != null) {
                        seekBar2.setProgress(0);
                    }
                }
                o.this.C = 0;
            }
            o.this.H.setColor(o.this.C);
            o.this.F.invalidate();
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    }

    class c implements SeekBar.OnSeekBarChangeListener {
        final /* synthetic */ int z;

        c(int i) {
            this.z = i;
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            if (z) {
                o oVar = o.this;
                int i2 = oVar.C;
                int i3 = this.z;
                oVar.C = (i << i3) | (i2 & ((255 << i3) ^ (-1)));
                o.this.G.setText(o.this.E());
            }
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    private static class d extends Drawable {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private int f402a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private final Paint f403b;

        public d(int i) {
            Paint paint = new Paint(1);
            this.f403b = paint;
            this.f402a = i;
            paint.setColor(i);
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            Paint paint;
            Canvas canvas2;
            float f2;
            float f3;
            float f4;
            float f5;
            Rect bounds = getBounds();
            int iMin = Math.min(bounds.width(), bounds.height()) / 2;
            int iCenterX = bounds.centerX();
            int iCenterY = bounds.centerY();
            if (this.f402a != 0) {
                f2 = iCenterX - iMin;
                f3 = iCenterY - iMin;
                f4 = iCenterX + iMin;
                f5 = iCenterY + iMin;
                paint = this.f403b;
                canvas2 = canvas;
            } else {
                this.f403b.setColor(-3355444);
                float f6 = iCenterX - iMin;
                float f7 = iCenterY - iMin;
                float f8 = iCenterX;
                float f9 = iCenterY;
                canvas.drawRect(f6, f7, f8, f9, this.f403b);
                float f10 = iCenterX + iMin;
                float f11 = iCenterY + iMin;
                canvas.drawRect(f8, f9, f10, f11, this.f403b);
                this.f403b.setColor(-7829368);
                canvas.drawRect(f8, f7, f10, f9, this.f403b);
                paint = this.f403b;
                canvas2 = canvas;
                f2 = f6;
                f3 = f9;
                f4 = f8;
                f5 = f11;
            }
            canvas2.drawRect(f2, f3, f4, f5, paint);
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -3;
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int i) {
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }
    }

    private View D(Context context, int i, int i2) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        ImageView imageView = new ImageView(context);
        imageView.setImageDrawable(new d(i));
        int i3 = this.D;
        imageView.setPadding(i3, i3, 0, i3);
        linearLayout.addView(imageView, new LinearLayout.LayoutParams(-1, -1, 6.0f));
        SeekBar seekBar = new SeekBar(context);
        seekBar.setMax(i2 == 24 ? e().getInt("max_alpha", 255) : 255);
        seekBar.setProgress(255 & (this.C >>> i2));
        seekBar.setOnSeekBarChangeListener(new c(i2));
        int i4 = this.E;
        int i5 = this.D;
        seekBar.setPadding(i4, i5, i4, i5);
        linearLayout.addView(seekBar, new LinearLayout.LayoutParams(-1, -2, 1.0f));
        this.I[i2 / 8] = seekBar;
        return linearLayout;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String E() {
        return this.J ? String.format(Locale.ENGLISH, "%08X", Integer.valueOf(this.C)) : String.format(Locale.ENGLISH, "%06X", Integer.valueOf(this.C & 16777215));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void F(DialogInterface dialogInterface, int i) {
        Bundle bundle = new Bundle(1);
        bundle.putInt("result", this.C);
        o(bundle);
    }

    public o G(int i) {
        e().putInt("color", i);
        return this;
    }

    public o H(int i, int i2) {
        Bundle bundleE = e();
        bundleE.putInt("color", i);
        bundleE.putInt("max_alpha", i2);
        return this;
    }

    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Activity activity = getActivity();
        this.C = e().getInt("color", -7829368);
        g.x xVarG = g();
        int i = xVarG.f641f * 2;
        this.D = i;
        this.E = i * 2;
        this.J = e().getInt("max_alpha", 255) > 0;
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        EditText editText = new EditText(activity);
        this.G = editText;
        y.L(xVarG, null, editText);
        this.G.setInputType(1);
        InputFilter[] inputFilterArr = new InputFilter[3];
        inputFilterArr[0] = new InputFilter.LengthFilter(this.J ? 8 : 6);
        inputFilterArr[1] = new InputFilter.AllCaps();
        inputFilterArr[2] = new a();
        this.G.setFilters(inputFilterArr);
        this.G.setText(E());
        this.G.addTextChangedListener(new b());
        linearLayout.addView(this.G);
        this.F = new ImageView(activity);
        ColorDrawable colorDrawable = new ColorDrawable(this.C);
        this.H = colorDrawable;
        this.F.setImageDrawable(colorDrawable);
        ImageView imageView = this.F;
        int i2 = this.E;
        imageView.setPadding(i2, this.D, i2, 0);
        linearLayout.addView(this.F, -1, this.D * 4);
        if (this.J) {
            linearLayout.addView(D(activity, 0, 24));
        }
        linearLayout.addView(D(activity, -65536, 16));
        linearLayout.addView(D(activity, -16711936, 8));
        linearLayout.addView(D(activity, -16776961, 0));
        ScrollView scrollView = new ScrollView(activity);
        scrollView.setFillViewport(true);
        scrollView.addView(linearLayout);
        return new AlertDialog.Builder(activity).setView(scrollView).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { // from class: e.n
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i3) {
                this.z.F(dialogInterface, i3);
            }
        }).setNegativeButton(R.string.cancel, j0.b.B).create();
    }

    @Override // e.j0.b, android.app.DialogFragment, android.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        e().putInt("color", this.C);
        super.onSaveInstanceState(bundle);
    }
}
