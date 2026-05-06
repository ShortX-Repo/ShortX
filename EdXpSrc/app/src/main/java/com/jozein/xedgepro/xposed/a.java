package com.jozein.xedgepro.xposed;

import android.app.ActivityOptions;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import cc.xposed.R;

/* JADX INFO: loaded from: classes.dex */
class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static Bundle[] f82a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static Bundle[] f83b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static Bundle f84c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static Point f85d;

    private static void a(Bundle[] bundleArr, Context context, int i, boolean z) {
        int i2 = i + 0;
        int i3 = R.anim.fade_out;
        bundleArr[i2] = g(context, R.anim.slide_in_left, z ? R.anim.slide_out_right : R.anim.fade_out);
        bundleArr[i + 1] = g(context, R.anim.slide_in_right, z ? R.anim.slide_out_left : R.anim.fade_out);
        bundleArr[i + 2] = g(context, R.anim.slide_in_top, z ? R.anim.slide_out_bottom : R.anim.fade_out);
        int i4 = i + 3;
        if (z) {
            i3 = R.anim.slide_out_top;
        }
        bundleArr[i4] = g(context, R.anim.slide_in_bottom, i3);
    }

    private static void b(Bundle[] bundleArr, int i, int i2, int i3) {
        float f2 = i2;
        int i4 = (int) (f2 * 0.15f);
        float f3 = i3;
        int i5 = (int) (0.15f * f3);
        int i6 = (int) (f2 * 0.7f);
        int i7 = (int) (f3 * 0.7f);
        bundleArr[i + 0] = h(-i2, i5, i6, i7);
        bundleArr[i + 1] = h(i2, i5, i6, i7);
        bundleArr[i + 2] = h(i4, -i3, i6, i7);
        bundleArr[i + 3] = h(i4, i3, i6, i7);
    }

    static Bundle c(View view) {
        return ActivityOptions.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight()).toBundle();
    }

    static Bundle d(l0 l0Var, int i) {
        if (i >= 0 && i <= 3) {
            if (f.o0.d() == 0) {
                if (f82a == null) {
                    f82a = new Bundle[8];
                    Context contextD = p2.d(l0Var.m0());
                    a(f82a, contextD, 0, false);
                    a(f82a, contextD, 4, true);
                }
                return l0Var.G0() ? f82a[i] : f82a[i + 4];
            }
            if (Build.VERSION.SDK_INT >= 23) {
                if (f85d == null) {
                    f85d = new Point();
                }
                Point pointK = g.a0.k(l0Var.m0(), f85d);
                int i2 = pointK.x;
                int i3 = pointK.y;
                boolean z = i2 < i3;
                if (f83b == null) {
                    Bundle[] bundleArr = new Bundle[8];
                    f83b = bundleArr;
                    if (z) {
                        b(bundleArr, 0, i2, i3);
                        b(f83b, 4, pointK.y, pointK.x);
                    } else {
                        b(bundleArr, 4, i2, i3);
                        b(f83b, 0, pointK.y, pointK.x);
                    }
                }
                Bundle[] bundleArr2 = f83b;
                return z ? bundleArr2[i] : bundleArr2[i + 4];
            }
        }
        if (f84c == null) {
            f84c = g(l0Var.m0(), android.R.anim.fade_in, android.R.anim.fade_out);
        }
        return f84c;
    }

    static Bundle e(Bundle bundle) {
        return f(bundle, 0);
    }

    static Bundle f(Bundle bundle, int i) {
        if (Build.VERSION.SDK_INT >= 26 && v.f245f) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putInt("android.activity.launchDisplayId", i);
        }
        return bundle;
    }

    private static Bundle g(Context context, int i, int i2) {
        Bundle bundle = ActivityOptions.makeCustomAnimation(context, i, i2).toBundle();
        if (Build.VERSION.SDK_INT >= 31) {
            bundle.putBoolean("android:activity.overrideTaskTransition", true);
        }
        return bundle;
    }

    private static Bundle h(int i, int i2, int i3, int i4) {
        Bundle bundle = new Bundle();
        bundle.putString("android:activity.packageName", "android");
        bundle.putInt("android:activity.animType", 2);
        bundle.putInt("android:activity.animStartX", i);
        bundle.putInt("android:activity.animStartY", i2);
        bundle.putInt("android:activity.animWidth", i3);
        bundle.putInt("android:activity.animHeight", i4);
        return bundle;
    }

    static Bundle i(Bundle bundle, int i) {
        if (Build.VERSION.SDK_INT >= 28) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putInt("android.activity.windowingMode", i);
        }
        return bundle;
    }
}
