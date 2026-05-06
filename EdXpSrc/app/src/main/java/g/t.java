package g;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/* JADX INFO: loaded from: classes.dex */
public class t extends i {

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private final boolean f616d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private final float f617e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private final Drawable f618f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private final Drawable f619g;

    public t(Drawable drawable, Drawable drawable2, float f2) {
        this(drawable, drawable2, f2, false);
    }

    public t(Drawable drawable, Drawable drawable2, float f2, boolean z) {
        this.f616d = z;
        this.f618f = drawable;
        this.f619g = drawable2;
        this.f617e = f2;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        a(canvas, this.f618f);
        a(canvas, this.f619g);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        Drawable drawable = this.f619g;
        if (drawable != null) {
            return drawable.getIntrinsicHeight();
        }
        return this.f618f != null ? (int) (r0.getIntrinsicHeight() / this.f617e) : super.getIntrinsicHeight();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        Drawable drawable = this.f619g;
        if (drawable != null) {
            return drawable.getIntrinsicWidth();
        }
        return this.f618f != null ? (int) (r0.getIntrinsicWidth() / this.f617e) : super.getIntrinsicWidth();
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        Drawable drawable = this.f618f;
        if (drawable != null) {
            if (this.f616d) {
                int i = rect.left;
                int i2 = rect.top;
                int i3 = rect.right;
                int i4 = rect.bottom;
                float f2 = this.f617e;
                drawable.setBounds(i, i2, ((int) ((i3 - i) * f2)) + i, ((int) ((i4 - i2) * f2)) + i2);
            } else {
                int iCenterX = rect.centerX();
                int iCenterY = rect.centerY();
                int iWidth = (int) ((rect.width() * this.f617e) / 2.0f);
                int iHeight = (int) ((rect.height() * this.f617e) / 2.0f);
                this.f618f.setBounds(iCenterX - iWidth, iCenterY - iHeight, iCenterX + iWidth, iCenterY + iHeight);
            }
        }
        Drawable drawable2 = this.f619g;
        if (drawable2 != null) {
            drawable2.setBounds(rect);
        }
    }
}
