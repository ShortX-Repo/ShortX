package g;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.widget.EditText;

/* JADX INFO: loaded from: classes.dex */
public class j extends Drawable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final Paint f561a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private final int f562b;

    public j(EditText editText, int i) {
        Paint paint = new Paint(1);
        this.f561a = paint;
        paint.setColor(editText.getHighlightColor() | (-16777216));
        this.f562b = i;
        paint.setStrokeWidth(i);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        int iWidth = bounds.width();
        float fHeight = bounds.height() - this.f562b;
        canvas.drawLine(0.0f, fHeight, iWidth, fHeight, this.f561a);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.f561a.setAlpha(i);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.f561a.setColorFilter(colorFilter);
    }
}
