package com.jozein.xedgepro.xposed;

import android.content.Context;
import android.widget.AbsListView;
import android.widget.Toast;
import de.robv.android.xposed.XposedHelpers;
import java.io.File;
import java.lang.reflect.Constructor;

/* JADX INFO: loaded from: classes.dex */
class o2 {

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static final String f198d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static int f199e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static Constructor<?> f200f;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final Context f201a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private final int f202b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private boolean f203c = false;

    static {
        String str = p2.z + "miui_fling_fix";
        f198d = str;
        f199e = new File(str).exists() ? 1 : -1;
        f200f = null;
    }

    o2(Context context, int i) {
        this.f201a = context;
        this.f202b = i;
    }

    private boolean d(Throwable th) {
        for (StackTraceElement stackTraceElement : th.getStackTrace()) {
            if ("com.miui.internal.dynamicanimation.animation.DynamicAnimation".equals(stackTraceElement.getClassName())) {
                return true;
            }
        }
        return false;
    }

    void a(AbsListView absListView) {
        if (f199e != 1) {
            return;
        }
        try {
            if (f200f == null) {
                Constructor<?> declaredConstructor = XposedHelpers.findClass(AbsListView.class.getName() + "$FlingRunnable", AbsListView.class.getClassLoader()).getDeclaredConstructor(AbsListView.class);
                f200f = declaredConstructor;
                declaredConstructor.setAccessible(true);
            }
            XposedHelpers.setObjectField(absListView, "mFlingRunnable", f200f.newInstance(absListView));
        } catch (Throwable th) {
            f199e = 0;
            f.v.d(th);
        }
    }

    void b(Throwable th) {
        if (this.f203c) {
            return;
        }
        this.f203c = true;
        if (f199e == -1) {
            if (d(th)) {
                f199e = 1;
                f.v.c("MIUI system bug has occurred! Try to fix it.");
                Toast.makeText(this.f201a, "MIUI system bug has occurred! Try to fix it.", 1).show();
                try {
                    new File(f198d).createNewFile();
                } catch (Throwable th2) {
                    f.v.d(th2);
                }
            } else {
                f199e = 0;
            }
        }
        t3.n().t(this.f202b);
    }

    boolean c() {
        return this.f203c;
    }
}
