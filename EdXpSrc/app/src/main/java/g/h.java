package g;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import cc.xposed.R;

/* JADX INFO: loaded from: classes.dex */
public abstract class h {

    private static class b extends h {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private int f549a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private ColorFilter f550b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        private final ColorFilter f551c;

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        private final ColorFilter f552d;

        private b() {
            this.f551c = new PorterDuffColorFilter(-7829368, PorterDuff.Mode.SRC_ATOP);
            this.f552d = new PorterDuffColorFilter(-16777216, PorterDuff.Mode.SRC_ATOP);
        }

        @Override // g.h
        void c(Drawable drawable) {
            drawable.setColorFilter(null);
        }

        @Override // g.h
        void f(Drawable drawable, int i) {
            ColorFilter colorFilter;
            if (i == -16777216) {
                colorFilter = this.f552d;
            } else if (i == -7829368) {
                colorFilter = this.f551c;
            } else {
                ColorFilter porterDuffColorFilter = this.f550b;
                if (porterDuffColorFilter == null || i != this.f549a) {
                    porterDuffColorFilter = new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_ATOP);
                }
                colorFilter = porterDuffColorFilter;
            }
            drawable.setColorFilter(colorFilter);
        }

        @Override // g.h
        public void h(Drawable drawable, Resources resources) {
            if (this.f550b == null) {
                this.f549a = resources.getColor(R.color.colorPrimary);
                this.f550b = new PorterDuffColorFilter(this.f549a, PorterDuff.Mode.SRC_ATOP);
            }
            if (drawable != null) {
                drawable.setColorFilter(this.f550b);
            }
        }
    }

    private static class c {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        static final h f553a;

        static {
            f553a = Build.VERSION.SDK_INT >= 21 ? new d() : new b();
        }
    }

    private static class d extends h {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private int f554a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private ColorStateList f555b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        private final ColorStateList f556c;

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        private final ColorStateList f557d;

        private d() {
            this.f556c = ColorStateList.valueOf(-7829368);
            this.f557d = ColorStateList.valueOf(-16777216);
        }

        @Override // g.h
        void c(Drawable drawable) {
            drawable.setTintList(null);
        }

        @Override // g.h
        void f(Drawable drawable, int i) {
            ColorStateList colorStateListValueOf;
            if (i == -16777216) {
                colorStateListValueOf = this.f557d;
            } else if (i == -7829368) {
                colorStateListValueOf = this.f556c;
            } else {
                ColorStateList colorStateList = this.f555b;
                colorStateListValueOf = (colorStateList == null || i != this.f554a) ? ColorStateList.valueOf(i) : colorStateList;
            }
            drawable.setTintList(colorStateListValueOf);
        }

        @Override // g.h
        public void h(Drawable drawable, Resources resources) {
            if (this.f555b == null) {
                int color = resources.getColor(R.color.colorPrimary);
                this.f554a = color;
                this.f555b = ColorStateList.valueOf(color);
            }
            if (drawable != null) {
                drawable.setTintList(this.f555b);
            }
        }
    }

    public static h a() {
        return c.f553a;
    }

    public final void b(Drawable drawable) {
        if (drawable != null) {
            c(drawable);
        }
    }

    abstract void c(Drawable drawable);

    public final void d(Drawable drawable) {
        if (drawable != null) {
            e(drawable, -16777216);
        }
    }

    public final void e(Drawable drawable, int i) {
        if (drawable != null) {
            if (i == 0) {
                b(drawable);
            } else {
                f(drawable, i);
            }
        }
    }

    abstract void f(Drawable drawable, int i);

    public final void g(Drawable drawable) {
        if (drawable != null) {
            e(drawable, -7829368);
        }
    }

    public abstract void h(Drawable drawable, Resources resources);
}
