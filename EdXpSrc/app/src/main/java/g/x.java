package g;

import android.content.Context;
import android.util.DisplayMetrics;

/* JADX INFO: loaded from: classes.dex */
public final class x {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f636a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f637b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f638c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final int f639d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final int f640e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final int f641f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public final int f642g;

    private x(int i, float f2, float f3, float f4) {
        this.f636a = i;
        float f5 = i;
        this.f637b = (int) (f2 * f5);
        int i2 = (int) (f3 * f5);
        this.f639d = i2;
        int i3 = (i2 * 4) / 3;
        this.f638c = i3;
        this.f640e = (i3 + i2) / 2;
        int i4 = (int) (f5 * f4);
        this.f641f = i4;
        this.f642g = i4 * 3;
    }

    public static x a(Context context) {
        return new x(d(context), 0.75f, 0.2f, 0.1f);
    }

    public static x b(Context context) {
        return new x((d(context) * 4) / 3, 0.56f, 0.14f, 0.1f);
    }

    public static x c(Context context) {
        return new x(d(context), 0.65f, 0.2f, 0.1f);
    }

    private static int d(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int iMin = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
        float f2 = iMin * 0.15f;
        float f3 = displayMetrics.xdpi * 0.375f;
        int i = (int) (("android".equals(context.getPackageName()) ? f2 + (f3 * 3.0f) : (f2 + f3) + ((displayMetrics.density * 60.0f) * 2.0f)) / 4.0f);
        int i2 = iMin / 12;
        if (i < i2) {
            i = i2;
        }
        int i3 = iMin / 5;
        return i > i3 ? i3 : i;
    }
}
