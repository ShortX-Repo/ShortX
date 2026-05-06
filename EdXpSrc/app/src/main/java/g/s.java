package g;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/* JADX INFO: loaded from: classes.dex */
public class s extends Drawable {

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static final RectF f612d = new RectF();

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final Paint f613a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private final float f614b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private final int f615c;

    public s(int i, float f2) {
        Paint paint = new Paint(1);
        this.f613a = paint;
        this.f615c = i;
        this.f614b = f2;
        paint.setColor(i);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        RectF rectF = f612d;
        rectF.set(0.0f, 0.0f, bounds.width(), bounds.height());
        float f2 = this.f614b;
        canvas.drawRoundRect(rectF, f2, f2, this.f613a);
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.f613a.getAlpha();
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    @Override // android.graphics.drawable.Drawable
    public void getOutline(Outline outline) {
        outline.setRoundRect(getBounds(), this.f614b);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.f613a.setAlpha(i);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.f613a.setColorFilter(colorFilter);
    }

    @Override // android.graphics.drawable.Drawable
    public void setTint(int i) {
        this.f613a.setColor(i);
    }

    @Override // android.graphics.drawable.Drawable
    public void setTintList(ColorStateList colorStateList) {
        if (colorStateList == null) {
            this.f613a.setColor(this.f615c);
        } else {
            this.f613a.setColor(colorStateList.getDefaultColor());
        }
    }
}
