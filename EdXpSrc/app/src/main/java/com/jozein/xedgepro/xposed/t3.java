package com.jozein.xedgepro.xposed;

import android.R;
import android.content.Context;
import android.os.Build;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.Field;

/* JADX INFO: loaded from: classes.dex */
class t3 implements f.l {
    private static Field C;
    static final boolean D;
    private static final t3 E;
    private static d F;
    private static boolean G;
    private a z = null;
    private a A = null;
    private a B = null;

    /* JADX INFO: Access modifiers changed from: private */
    static class a extends FrameLayout {
        private final boolean z;

        a(Context context, boolean z, boolean z2) {
            WindowManager.LayoutParams layoutParams;
            String str;
            super(context);
            this.z = z;
            int i = Build.VERSION.SDK_INT;
            if (i >= 29) {
                setForceDarkAllowed(false);
            }
            WindowManager windowManager = (WindowManager) context.getSystemService("window");
            if (z) {
                layoutParams = new WindowManager.LayoutParams(i > 29 ? 2017 : 2014, (t3.G ? 8 : 0) | 256 | 1024 | 524288 | 16777216, 1);
                if (!t3.G) {
                    layoutParams.softInputMode = 51;
                    setFocusableInTouchMode(true);
                }
                g.a0.b(layoutParams, "Popup:t");
                if (t3.F != null) {
                    t3.F.a();
                }
            } else {
                layoutParams = new WindowManager.LayoutParams(2015, R.drawable.ic_media_route_connecting_light_21_mtrl, 1);
                if (i >= 28) {
                    layoutParams.layoutInDisplayCutoutMode = 1;
                    if (i >= 30) {
                        layoutParams.setFitInsetsTypes(0);
                    }
                }
                int i2 = 16;
                if (z2) {
                    i2 = 1048592;
                    str = "ScreenFilter";
                } else {
                    str = "Popup";
                }
                g.a0.b(layoutParams, str);
                t3.i(layoutParams, i2);
            }
            windowManager.addView(this, layoutParams);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void e(View view) {
            i(view);
            if (getChildCount() <= 0) {
                g();
            }
        }

        private void f() {
            for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
                KeyEvent.Callback childAt = getChildAt(childCount);
                if (childAt instanceof c) {
                    try {
                        ((c) childAt).a();
                    } catch (Throwable th) {
                        f.v.d(th);
                    }
                }
            }
            removeAllViews();
        }

        private void g() {
            try {
                ((WindowManager) getContext().getSystemService("window")).removeView(this);
                t3 t3VarN = t3.n();
                if (t3VarN.B == this) {
                    t3VarN.B = null;
                } else if (t3VarN.A == this) {
                    t3VarN.A = null;
                } else if (t3VarN.z == this) {
                    t3VarN.z = null;
                } else {
                    f.v.c("Removing unknown container.");
                }
                if (!this.z || t3.F == null) {
                    return;
                }
                t3.F.b();
            } catch (Throwable th) {
                f.v.d(th);
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        private void i(View view) {
            removeView(view);
            if (view instanceof c) {
                try {
                    ((c) view).a();
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
        }

        void b(View view, FrameLayout.LayoutParams layoutParams, int i) {
            view.setTag(Integer.valueOf(i));
            if (!this.z || i == 273) {
                View viewD = d(i);
                if (viewD != null) {
                    i(viewD);
                }
            } else {
                f();
            }
            addView(view, (i & 256) != 0 ? -1 : 0, layoutParams);
            if (this.z) {
                view.requestFocus();
            }
        }

        void c() {
            f();
            g();
        }

        View d(int i) {
            for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
                View childAt = getChildAt(childCount);
                Integer num = (Integer) childAt.getTag();
                if (num != null && num.intValue() == i) {
                    return childAt;
                }
            }
            return null;
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchKeyEvent(KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() != 4 || keyEvent.getAction() != 0) {
                return super.dispatchKeyEvent(keyEvent);
            }
            j();
            return true;
        }

        boolean h(int i, long j) {
            final View viewD = d(i);
            if (viewD == null) {
                return false;
            }
            if (j <= 0) {
                i(viewD);
                if (getChildCount() > 0) {
                    return true;
                }
                g();
                return true;
            }
            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            alphaAnimation.setDuration(j);
            viewD.startAnimation(alphaAnimation);
            viewD.setTag(null);
            postDelayed(new Runnable() { // from class: com.jozein.xedgepro.xposed.s3
                @Override // java.lang.Runnable
                public final void run() {
                    this.z.e(viewD);
                }
            }, j);
            return true;
        }

        boolean j() {
            boolean z = getChildCount() > 0;
            if (!h(273, 0L)) {
                c();
            }
            return z;
        }

        @Override // android.view.View
        public boolean onGenericMotionEvent(MotionEvent motionEvent) {
            View childAt = getChildAt(getChildCount() - 1);
            return childAt != null ? childAt.onGenericMotionEvent(motionEvent) : super.onGenericMotionEvent(motionEvent);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getActionMasked() != 0) {
                return true;
            }
            c();
            return true;
        }
    }

    static abstract class b extends FrameLayout implements c {
        public b(Context context) {
            super(context);
        }
    }

    interface c {
        void a();
    }

    interface d {
        void a();

        void b();
    }

    static {
        D = Build.VERSION.SDK_INT >= 27;
        E = new t3();
        F = null;
        G = true;
    }

    private t3() {
    }

    static void i(WindowManager.LayoutParams layoutParams, int i) {
        try {
            if (C == null) {
                C = XposedHelpers.findField(WindowManager.LayoutParams.class, "privateFlags");
            }
            C.setInt(layoutParams, i | C.getInt(layoutParams));
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    private void l() {
        try {
            a aVar = this.B;
            if (aVar != null) {
                aVar.c();
                this.B = null;
            }
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    private a m(Context context, int i) {
        if (i == 257 && D) {
            if (this.z == null) {
                this.z = new a(context, false, true);
            }
            return this.z;
        }
        if ((i & 255) >= 16) {
            if (this.B == null) {
                this.B = new a(context, true, false);
            }
            return this.B;
        }
        if (this.A == null) {
            this.A = new a(context, false, false);
        }
        return this.A;
    }

    static t3 n() {
        return E;
    }

    static boolean o() {
        a aVar;
        t3 t3Var = E;
        return (t3Var == null || (aVar = t3Var.B) == null || !aVar.j()) ? false : true;
    }

    static void q() {
        t3 t3Var = E;
        if (t3Var != null) {
            t3Var.l();
        }
    }

    private a r(int i) {
        return (i == 257 && D) ? this.z : (i & 255) >= 16 ? this.B : this.A;
    }

    static t3 s() {
        return E;
    }

    static void v(boolean z) {
        G = z;
    }

    static void w(d dVar) {
        F = dVar;
    }

    boolean j(View view, int i) {
        return k(view, i, null);
    }

    boolean k(View view, int i, FrameLayout.LayoutParams layoutParams) {
        if (view == null) {
            return false;
        }
        if (layoutParams == null) {
            layoutParams = new FrameLayout.LayoutParams(-1, -1);
        }
        if (i == 6) {
            l();
        }
        try {
            m(view.getContext(), i).b(view, layoutParams, i);
            return true;
        } catch (Throwable th) {
            f.v.d(th);
            return false;
        }
    }

    boolean p(int i) {
        a aVarR = r(i);
        return (aVarR == null || aVarR.d(i) == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void t(int i) {
        u(i, 0L);
    }

    void u(int i, long j) {
        a aVarR = r(i);
        if (aVarR != null) {
            aVarR.h(i, j);
        }
    }
}
