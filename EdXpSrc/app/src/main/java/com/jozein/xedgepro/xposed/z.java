package com.jozein.xedgepro.xposed;

import android.os.Build;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import de.robv.android.xposed.XC_MethodHook;

/* JADX INFO: loaded from: classes.dex */
abstract class z {
    static final boolean E;
    private final Handler A;
    final y0 z;
    private volatile boolean D = false;
    private final Runnable B = new Runnable() { // from class: com.jozein.xedgepro.xposed.y
        @Override // java.lang.Runnable
        public final void run() {
            this.z.h();
        }
    };
    private final Runnable C = new Runnable() { // from class: com.jozein.xedgepro.xposed.x
        @Override // java.lang.Runnable
        public final void run() {
            this.z.i();
        }
    };

    static {
        E = Build.VERSION.SDK_INT > 29;
    }

    z(y0 y0Var, Handler handler) {
        this.z = y0Var;
        this.A = handler;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void h() {
        try {
            synchronized (this) {
                if (this.D) {
                    e();
                }
            }
        } catch (Throwable th) {
            f.v.d(th);
        }
        this.D = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void i() {
        try {
            synchronized (this) {
                if (this.D) {
                    f();
                }
            }
        } catch (Throwable th) {
            f.v.d(th);
        }
        this.D = false;
    }

    protected boolean c(XC_MethodHook.MethodHookParam methodHookParam, KeyEvent keyEvent) {
        return false;
    }

    protected boolean d(XC_MethodHook.MethodHookParam methodHookParam, MotionEvent motionEvent) {
        return false;
    }

    protected abstract void e();

    protected abstract void f();

    void g(XC_MethodHook.MethodHookParam methodHookParam) {
        try {
            this.z.j0(methodHookParam);
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    final boolean j(XC_MethodHook.MethodHookParam methodHookParam, KeyEvent keyEvent) {
        boolean zC;
        if (!this.D) {
            return c(methodHookParam, keyEvent);
        }
        synchronized (this) {
            zC = c(methodHookParam, keyEvent);
        }
        return zC;
    }

    final boolean k(XC_MethodHook.MethodHookParam methodHookParam, MotionEvent motionEvent) {
        boolean zD;
        if (!this.D) {
            return d(methodHookParam, motionEvent);
        }
        synchronized (this) {
            zD = d(methodHookParam, motionEvent);
        }
        return zD;
    }

    protected void l(long j) {
        this.D = true;
        this.A.postDelayed(this.B, j);
    }

    protected void m(long j) {
        this.D = true;
        this.A.postDelayed(this.C, j);
    }

    protected void n() {
        if (this.D) {
            this.D = false;
            this.A.removeCallbacks(this.B);
        }
    }

    protected void o() {
        if (this.D) {
            this.D = false;
            this.A.removeCallbacks(this.C);
        }
    }
}
