package g;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

/* JADX INFO: loaded from: classes.dex */
public class n extends i {

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private final Drawable f565d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private final Drawable[] f566e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private final int f567f;

    public n(Context context, a.b[] bVarArr, Drawable drawable) {
        int i;
        int i2 = 0;
        int i3 = 4;
        if (bVarArr.length == 0) {
            i3 = 0;
        } else if (bVarArr.length <= 4) {
            i3 = 2;
        } else if (bVarArr.length <= 9) {
            i3 = 3;
        }
        this.f567f = i3;
        this.f565d = drawable;
        this.f566e = new Drawable[i3 * i3];
        while (true) {
            Drawable[] drawableArr = this.f566e;
            if (i2 >= drawableArr.length) {
                return;
            }
            a.b bVar = bVarArr[i2];
            if (bVar != null && (i = bVar.z) != 0 && i != 1) {
                drawableArr[i2] = bVar.o(context);
            }
            i2++;
        }
    }

    public n(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4, Drawable drawable5) {
        this(new Drawable[]{drawable, drawable2, drawable3, drawable4}, drawable5);
    }

    public n(Drawable[] drawableArr, Drawable drawable) {
        drawableArr = drawableArr == null ? a0.z : drawableArr;
        this.f566e = drawableArr;
        this.f565d = drawable;
        int i = 4;
        if (drawableArr.length <= 4) {
            i = 2;
        } else if (drawableArr.length <= 9) {
            i = 3;
        }
        this.f567f = i;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        a(canvas, this.f565d);
        for (Drawable drawable : this.f566e) {
            a(canvas, drawable);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        Drawable drawable = this.f565d;
        if (drawable != null) {
            return drawable.getIntrinsicHeight();
        }
        for (Drawable drawable2 : this.f566e) {
            if (drawable2 != null) {
                return drawable2.getIntrinsicWidth() * this.f567f;
            }
        }
        return super.getIntrinsicHeight();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        Drawable drawable = this.f565d;
        if (drawable != null) {
            return drawable.getIntrinsicWidth();
        }
        for (Drawable drawable2 : this.f566e) {
            if (drawable2 != null) {
                return drawable2.getIntrinsicWidth() * this.f567f;
            }
        }
        return super.getIntrinsicWidth();
    }

    @Override // android.graphics.drawable.Drawable
    public void setBounds(int i, int i2, int i3, int i4) {
        Drawable drawable;
        super.setBounds(i, i2, i3, i4);
        Drawable drawable2 = this.f565d;
        if (drawable2 != null) {
            drawable2.setBounds(i, i2, i3, i4);
        }
        int i5 = this.f567f;
        if (i5 == 0) {
            return;
        }
        int i6 = (i3 - i) / i5;
        int i7 = (i4 - i2) / i5;
        for (int i8 = 0; i8 < this.f567f; i8++) {
            int i9 = (i7 * i8) + i2;
            int i10 = 0;
            while (true) {
                int i11 = this.f567f;
                if (i10 < i11) {
                    int i12 = (i11 * i8) + i10;
                    Drawable[] drawableArr = this.f566e;
                    if (i12 < drawableArr.length && (drawable = drawableArr[i12]) != null) {
                        int i13 = (i6 * i10) + i;
                        drawable.setBounds(i13, i9, i13 + i6, i9 + i7);
                    }
                    i10++;
                }
            }
        }
    }
}
