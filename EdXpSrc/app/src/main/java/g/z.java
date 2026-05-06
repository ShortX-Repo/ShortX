package g;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/* JADX INFO: loaded from: classes.dex */
public final class z extends Drawable {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static final Rect f643e = new Rect();

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static final Rect f644f = new Rect();

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static final Paint f645g;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final Drawable f646a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private final int f647b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private final int f648c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private Bitmap f649d;

    static {
        Paint paint = new Paint(1);
        f645g = paint;
        paint.setStyle(Paint.Style.FILL);
    }

    public z(Drawable drawable, int i, int i2) {
        this.f646a = drawable;
        this.f647b = i;
        this.f648c = i2;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        int iWidth = bounds.width();
        int iMin = Math.min(iWidth, bounds.height());
        int iCenterX = bounds.centerX();
        int iCenterY = bounds.centerY();
        int i = this.f648c;
        boolean z = i != 0;
        if (z) {
            Paint paint = f645g;
            paint.setColor(i);
            canvas.drawCircle(iCenterX, iCenterY, iMin / 2, paint);
        }
        if (this.f646a == null) {
            return;
        }
        int i2 = (int) ((iMin * (z ? 0.6f : 0.8f)) / 2.0f);
        Rect rect = f644f;
        rect.set(iCenterX - i2, iCenterY - i2, iCenterX + i2, iCenterY + i2);
        int i3 = this.f647b;
        if (i3 == 0 || i3 == -1) {
            this.f646a.setBounds(rect);
            this.f646a.draw(canvas);
            return;
        }
        Bitmap bitmap = this.f649d;
        if (bitmap == null || bitmap.getWidth() * 2 < iWidth) {
            h.a().e(this.f646a, this.f647b);
            this.f649d = a0.i(this.f646a, rect.width(), rect.height());
            h.a().b(this.f646a);
        }
        Rect rect2 = f643e;
        rect2.set(0, 0, this.f649d.getWidth(), this.f649d.getHeight());
        canvas.drawBitmap(this.f649d, rect2, rect, f645g);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    @Override // android.graphics.drawable.Drawable
    public void getOutline(Outline outline) {
        if (this.f648c == 0) {
            super.getOutline(outline);
        } else {
            outline.setRoundRect(getBounds(), Math.min(r0.width(), r0.height()) / 2);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        f.v.d(new UnsupportedOperationException("set color filter!"));
    }

    @Override // android.graphics.drawable.Drawable
    public void setTintList(ColorStateList colorStateList) {
        f.v.d(new UnsupportedOperationException("set tint list!"));
    }
}
