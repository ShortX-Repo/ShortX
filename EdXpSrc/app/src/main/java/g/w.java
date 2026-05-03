package g;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

/* JADX INFO: loaded from: classes.dex */
public class w extends i {

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static final float[] f632g = {0.0f, 0.75f, 0.6f, 0.5f, 0.4f};
    private static final float[] h = {0.0f, 0.0f, 0.4f, 0.25f, 0.2f};

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private final Drawable f633d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private final Drawable[] f634e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private final int f635f;

    public w(Context context, a.b[] bVarArr) {
        this(context, bVarArr, null);
    }

    public w(Context context, a.b[] bVarArr, Drawable drawable) {
        this.f633d = drawable;
        int iMin = Math.min(bVarArr.length, 4);
        this.f635f = iMin;
        this.f634e = new Drawable[iMin];
        for (int i = 0; i < this.f635f; i++) {
            this.f634e[i] = bVarArr[i].o(context);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        a(canvas, this.f633d);
        for (int i = this.f635f - 1; i >= 0; i--) {
            a(canvas, this.f634e[i]);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        Drawable drawable = this.f633d;
        if (drawable != null) {
            drawable.getIntrinsicHeight();
        }
        for (int i = 0; i < this.f635f; i++) {
            Drawable[] drawableArr = this.f634e;
            if (drawableArr[i] != null) {
                return drawableArr[i].getIntrinsicHeight();
            }
        }
        return super.getIntrinsicHeight();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        Drawable drawable = this.f633d;
        if (drawable != null) {
            return drawable.getIntrinsicWidth();
        }
        for (int i = 0; i < this.f635f; i++) {
            Drawable[] drawableArr = this.f634e;
            if (drawableArr[i] != null) {
                return drawableArr[i].getIntrinsicWidth();
            }
        }
        return super.getIntrinsicWidth();
    }

    @Override // android.graphics.drawable.Drawable
    public void setBounds(int i, int i2, int i3, int i4) {
        super.setBounds(i, i2, i3, i4);
        Drawable drawable = this.f633d;
        if (drawable != null) {
            drawable.setBounds(i, i2, i3, i4);
        }
        int i5 = i3 - i;
        int length = this.f635f;
        float[] fArr = f632g;
        if (length >= fArr.length) {
            length = fArr.length - 1;
        }
        float f2 = i5;
        int i6 = (int) (fArr[length] * f2);
        int i7 = (int) (f2 * h[length]);
        for (int i8 = 0; i8 < this.f635f; i8++) {
            Drawable[] drawableArr = this.f634e;
            if (drawableArr[i8] != null) {
                int i9 = i7 * i8;
                int i10 = i + i9;
                int i11 = i4 - i9;
                drawableArr[i8].setBounds(i10, i11 - i6, i10 + i6, i11);
            }
        }
    }
}
