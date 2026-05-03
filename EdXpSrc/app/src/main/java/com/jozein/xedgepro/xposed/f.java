package com.jozein.xedgepro.xposed;

import android.content.ContentResolver;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Build;
import com.jozein.xedgepro.xposed.h3;
import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
abstract class f {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final boolean f99a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static boolean f100b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static f f101c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static f f102d;

    private static abstract class a extends f {

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        private final int f103e;

        a(int i) {
            this.f103e = i;
        }

        private int l(float f2) {
            int i = this.f103e;
            return c((int) Math.sqrt(f2 * i * i));
        }

        private float n(int i) {
            int i2 = this.f103e;
            float f2 = (i * i) / (i2 * i2);
            if (f2 <= 0.0f) {
                return 0.0f;
            }
            if (f2 >= 1.0f) {
                return 1.0f;
            }
            return f2;
        }

        @Override // com.jozein.xedgepro.xposed.f
        public void b(Context context, boolean z) {
            float fN;
            float fM = m(context);
            int iL = l(fM);
            if (z) {
                fN = n(iL + 1);
                if (fN <= fM) {
                    return;
                }
            } else {
                fN = n(iL);
                if (fN >= fM) {
                    fN = n(iL - 1);
                }
                if (fN >= fM) {
                    return;
                }
            }
            o(context, fN);
        }

        @Override // com.jozein.xedgepro.xposed.f
        public int f() {
            return this.f103e;
        }

        @Override // com.jozein.xedgepro.xposed.f
        public void k(Context context, int i) {
            o(context, n(i));
        }

        protected abstract float m(Context context);

        protected abstract void o(Context context, float f2);
    }

    private static class b extends f {

        /* JADX INFO: renamed from: f, reason: collision with root package name */
        private static final int f104f = f.g.f437a;

        /* JADX INFO: renamed from: g, reason: collision with root package name */
        private static final int f105g = f.g.f438b;

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        private final int[] f106e;

        b(boolean z) {
            this.f106e = m(z ? 32 : 16);
        }

        private int l(int i) {
            int i2 = 0;
            while (true) {
                int[] iArr = this.f106e;
                if (i2 >= iArr.length) {
                    return iArr.length - 1;
                }
                if (iArr[i2] >= i) {
                    return i2;
                }
                i2++;
            }
        }

        private static int[] m(int i) {
            int[] iArr = new int[i + 1];
            int i2 = f105g;
            float f2 = (i2 - r2) / (i * i);
            iArr[0] = f104f;
            iArr[i] = i2;
            for (int i3 = 1; i3 < i; i3++) {
                float f3 = i3;
                int i4 = (int) ((f2 * f3 * f3) + f104f);
                int i5 = i3 - 1;
                if (i4 <= iArr[i5]) {
                    i4 = iArr[i5] + 1;
                }
                iArr[i3] = i4;
            }
            return iArr;
        }

        private int n(int i) {
            if (i <= 0) {
                return this.f106e[0];
            }
            int[] iArr = this.f106e;
            return i >= iArr.length ? iArr[iArr.length - 1] : iArr[i];
        }

        @Override // com.jozein.xedgepro.xposed.f
        public void b(Context context, boolean z) {
            int iN;
            ContentResolver contentResolver = context.getContentResolver();
            int[] iArr = this.f106e;
            int iA = h3.c.a(contentResolver, "screen_brightness", iArr[iArr.length / 2]);
            int iL = l(iA);
            if (z) {
                iN = n(iL + 1);
                if (iN <= iA) {
                    return;
                }
            } else {
                iN = n(iL);
                if (iN >= iA) {
                    iN = n(iL - 1);
                }
                if (iN >= iA) {
                    return;
                }
            }
            h3.c.b(context.getContentResolver(), "screen_brightness", iN);
        }

        @Override // com.jozein.xedgepro.xposed.f
        public int f() {
            return this.f106e.length - 1;
        }

        @Override // com.jozein.xedgepro.xposed.f
        public void k(Context context, int i) {
            h3.c.b(context.getContentResolver(), "screen_brightness", n(i));
        }
    }

    private static class c extends a {

        /* JADX INFO: renamed from: f, reason: collision with root package name */
        private final Method f107f;

        /* JADX INFO: renamed from: g, reason: collision with root package name */
        private final Method f108g;

        c(boolean z) {
            super(z ? 64 : 16);
            Class cls = Integer.TYPE;
            this.f107f = XposedHelpers.findMethodExact(DisplayManager.class, "getBrightness", new Class[]{cls});
            this.f108g = XposedHelpers.findMethodExact(DisplayManager.class, "setBrightness", new Class[]{cls, Float.TYPE});
        }

        @Override // com.jozein.xedgepro.xposed.f.a
        protected float m(Context context) {
            DisplayManager displayManager = (DisplayManager) context.getSystemService("display");
            if (displayManager != null) {
                try {
                    return ((Float) this.f107f.invoke(displayManager, 0)).floatValue();
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
            f.d();
            return 0.5f;
        }

        @Override // com.jozein.xedgepro.xposed.f.a
        protected void o(Context context, float f2) {
            DisplayManager displayManager = (DisplayManager) context.getSystemService("display");
            if (displayManager != null) {
                try {
                    this.f108g.invoke(displayManager, 0, Float.valueOf(f2));
                    float fM = m(context);
                    if (fM != f2) {
                        f.d();
                        f.v.c("Failed to set brightness to " + f2 + ", brightness: " + fM);
                        return;
                    }
                    return;
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
            f.d();
        }
    }

    static {
        boolean z = Build.VERSION.SDK_INT >= 30;
        f99a = z;
        f100b = z;
        f101c = null;
        f102d = null;
    }

    f() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void d() {
        synchronized (f.class) {
            f100b = false;
            f101c = null;
            f102d = null;
        }
    }

    static f e(Context context, boolean z) {
        if (z) {
            if (f102d == null) {
                synchronized (f.class) {
                    if (f102d == null) {
                        f102d = h(context, true);
                    }
                }
            }
            return f102d;
        }
        if (f101c == null) {
            synchronized (f.class) {
                if (f101c == null) {
                    f101c = h(context, false);
                }
            }
        }
        return f101c;
    }

    private static f h(Context context, boolean z) {
        if (f100b && Build.VERSION.SDK_INT >= 31) {
            try {
                c cVar = new c(z);
                float fM = cVar.m(context);
                if (fM <= 1.0f) {
                    return cVar;
                }
                f.v.c("Invalid brightness float: " + fM);
                d();
            } catch (Throwable th) {
                f.v.d(th);
                d();
            }
        }
        return new b(z);
    }

    public static void j(Context context, int i) {
        h3.c.b(context.getContentResolver(), "screen_brightness", i);
    }

    public abstract void b(Context context, boolean z);

    int c(int i) {
        int iF = f();
        if (i <= 0) {
            return 0;
        }
        return i >= iF ? iF : i;
    }

    public abstract int f();

    public boolean g(Context context) {
        return h3.c.a(context.getContentResolver(), "screen_brightness_mode", 0) == 1;
    }

    public void i(Context context, boolean z) {
        if (g(context) != z) {
            h3.c.b(context.getContentResolver(), "screen_brightness_mode", z ? 1 : 0);
        }
    }

    public abstract void k(Context context, int i);
}
