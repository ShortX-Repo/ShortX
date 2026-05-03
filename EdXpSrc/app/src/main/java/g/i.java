package g;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;

/* JADX INFO: loaded from: classes.dex */
public abstract class i extends Drawable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private int f558a = 255;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private ColorFilter f559b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private ColorStateList f560c;

    protected void a(Canvas canvas, Drawable drawable) {
        if (canvas == null || drawable == null) {
            return;
        }
        drawable.setAlpha(this.f558a);
        drawable.setColorFilter(this.f559b);
        int i = Build.VERSION.SDK_INT;
        if (i >= 21) {
            drawable.setTintList(this.f560c);
        }
        drawable.draw(canvas);
        drawable.setAlpha(255);
        drawable.setColorFilter(null);
        if (i >= 21) {
            drawable.setTintList(null);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.f558a;
    }

    @Override // android.graphics.drawable.Drawable
    public final ColorFilter getColorFilter() {
        return this.f559b;
    }

    @Override // android.graphics.drawable.Drawable
    public final int getOpacity() {
        return -2;
    }

    @Override // android.graphics.drawable.Drawable
    public final void setAlpha(int i) {
        this.f558a = i;
    }

    @Override // android.graphics.drawable.Drawable
    public final void setColorFilter(ColorFilter colorFilter) {
        this.f559b = colorFilter;
    }

    @Override // android.graphics.drawable.Drawable
    public final void setTintList(ColorStateList colorStateList) {
        this.f560c = colorStateList;
    }
}
