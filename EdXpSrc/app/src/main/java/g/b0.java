package g;

import a.x;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import cc.xposed.R;
import f.h0;

/* JADX INFO: loaded from: classes.dex */
public class b0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final Context f523a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private final Context f524b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private final m f525c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private final AppWidgetManager f526d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private final AppWidgetHost f527e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private final a.x f528f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private final int f529g;
    private final int h;
    private Runnable i;
    private int j;
    private long k;
    private final Handler l;
    private a m;
    private int n;
    private int o;
    private int p;
    private int q;

    private static class a extends FrameLayout {
        private RectF A;
        private final float B;
        private Paint z;

        public a(Context context) {
            super(context);
            this.z = null;
            this.A = null;
            this.B = (a0.m(context) * 0.015f) / 2.0f;
        }

        public void a(boolean z) {
            RectF rectF;
            if (z) {
                float f2 = this.B;
                rectF = new RectF(f2, f2, 0.0f, 0.0f);
            } else {
                rectF = null;
            }
            this.A = rectF;
            setBackgroundColor(z ? 872390656 : 0);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.A == null) {
                return;
            }
            float f2 = this.B * 2.0f;
            if (this.z == null) {
                Paint paint = new Paint(1);
                this.z = paint;
                paint.setStrokeWidth(f2);
                this.z.setStyle(Paint.Style.STROKE);
                this.z.setColor(-24576);
            }
            this.A.right = getRight() - this.B;
            this.A.bottom = getBottom() - this.B;
            canvas.drawRoundRect(this.A, f2, f2, this.z);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    class b extends View {
        private final long A;
        private final float B;
        private final AppWidgetHostView C;
        private int D;
        private float E;
        private float F;
        private MotionEvent G;
        private MotionEvent H;
        private int I;
        private final Runnable J;
        private final Runnable K;
        private final long z;

        class a implements Runnable {
            a() {
            }

            @Override // java.lang.Runnable
            public void run() {
                if (b.this.G != null && b.this.H != null) {
                    b.this.C.dispatchTouchEvent(b.this.G);
                    b.this.C.dispatchTouchEvent(b.this.H);
                }
                b.this.g();
                b.this.D = 0;
            }
        }

        public b(Context context, AppWidgetHostView appWidgetHostView) {
            super(context);
            this.z = ViewConfiguration.getLongPressTimeout();
            this.A = ViewConfiguration.getDoubleTapTimeout();
            this.D = 0;
            this.E = 0.0f;
            this.F = 0.0f;
            this.G = null;
            this.H = null;
            this.I = -1;
            this.J = new Runnable() { // from class: g.c0
                @Override // java.lang.Runnable
                public final void run() {
                    this.z.h();
                }
            };
            this.K = new a();
            this.C = appWidgetHostView;
            int scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
            this.B = scaledTouchSlop * scaledTouchSlop;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void g() {
            this.D = -1;
            MotionEvent motionEvent = this.G;
            if (motionEvent != null) {
                motionEvent.recycle();
                this.G = null;
            }
            MotionEvent motionEvent2 = this.H;
            if (motionEvent2 != null) {
                motionEvent2.recycle();
                this.H = null;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void h() {
            this.D = 1;
            setBackgroundColor(2030209233);
        }

        private void i(MotionEvent motionEvent) {
            int i = this.D;
            if (i == 0) {
                removeCallbacks(this.J);
            } else if (i == 1) {
                b0.this.l(0, 0);
                setBackgroundColor(0);
            } else if (i == 2) {
                b0.this.o(0, 0);
                if (b0.this.m != null) {
                    b0.this.m.a(false);
                }
            } else if (i == 3) {
                this.C.dispatchTouchEvent(motionEvent);
            }
            g();
        }

        private void j(MotionEvent motionEvent) {
            removeCallbacks(this.K);
            this.I = -1;
            this.E = motionEvent.getRawX();
            this.F = motionEvent.getRawY();
            MotionEvent motionEvent2 = this.G;
            if (motionEvent2 != null) {
                motionEvent2.recycle();
                this.G = null;
            }
            MotionEvent motionEvent3 = this.H;
            if (motionEvent3 == null) {
                this.G = MotionEvent.obtain(motionEvent);
                this.D = 0;
                postDelayed(this.J, this.z);
            } else {
                motionEvent3.recycle();
                this.H = null;
                this.D = 2;
                if (b0.this.m != null) {
                    b0.this.m.a(true);
                }
            }
        }

        private void k(MotionEvent motionEvent) {
            int i = this.D;
            if (i == 0) {
                float rawX = motionEvent.getRawX() - this.E;
                float rawY = motionEvent.getRawY() - this.F;
                if ((rawX * rawX) + (rawY * rawY) <= this.B) {
                    return;
                }
                this.D = 3;
                removeCallbacks(this.J);
                MotionEvent motionEvent2 = this.G;
                if (motionEvent2 != null) {
                    this.C.dispatchTouchEvent(motionEvent2);
                    this.G.recycle();
                    this.G = null;
                }
            } else {
                if (i == 1) {
                    b0.this.l((int) (motionEvent.getRawX() - this.E), (int) (motionEvent.getRawY() - this.F));
                    return;
                }
                if (i == 2) {
                    if (this.I == -1) {
                        float rawX2 = motionEvent.getRawX() - this.E;
                        float rawY2 = motionEvent.getRawY() - this.F;
                        float f2 = rawX2 * rawX2;
                        float f3 = rawY2 * rawY2;
                        if (f2 + f3 <= this.B) {
                            return;
                        } else {
                            this.I = f2 > f3 ? 1 : 0;
                        }
                    }
                    int i2 = this.I;
                    if (i2 == 0) {
                        b0.this.o(0, (int) (motionEvent.getRawY() - this.F));
                        return;
                    } else {
                        if (i2 == 1) {
                            b0.this.o((int) (motionEvent.getRawX() - this.E), 0);
                            return;
                        }
                        return;
                    }
                }
                if (i != 3) {
                    return;
                }
            }
            this.C.dispatchTouchEvent(motionEvent);
        }

        private void l(MotionEvent motionEvent) {
            int i = this.D;
            if (i != 0) {
                if (i != 1 && i != 2) {
                    if (i == 3) {
                        this.C.dispatchTouchEvent(motionEvent);
                    }
                }
                g();
            }
            removeCallbacks(this.J);
            b0.this.j();
            g();
        }

        private void m(MotionEvent motionEvent) {
            MotionEvent motionEvent2;
            int rawY;
            removeCallbacks(this.J);
            int i = this.D;
            if (i == 0) {
                this.H = MotionEvent.obtain(motionEvent);
                postDelayed(this.K, this.A);
                return;
            }
            int rawX = 0;
            if (i == 1) {
                int rawX2 = (int) (motionEvent.getRawX() - this.E);
                int rawY2 = (int) (motionEvent.getRawY() - this.F);
                b0.this.l(rawX2, rawY2);
                b0.this.p(rawX2, rawY2);
                setBackgroundColor(0);
                motionEvent2 = this.G;
                if (motionEvent2 == null) {
                    return;
                }
            } else {
                if (i != 2) {
                    if (i != 3) {
                        return;
                    }
                    this.C.dispatchTouchEvent(motionEvent);
                    return;
                }
                if (b0.this.m != null) {
                    b0.this.m.a(false);
                }
                int i2 = this.I;
                if (i2 == -1) {
                    return;
                }
                if (i2 == 0) {
                    rawY = (int) (motionEvent.getRawY() - this.F);
                } else {
                    rawX = (int) (motionEvent.getRawX() - this.E);
                    rawY = 0;
                }
                b0.this.o(rawX, rawY);
                b0.this.q(this.C, rawX, rawY);
                ViewGroup.LayoutParams layoutParams = getLayoutParams();
                layoutParams.width = b0.this.n;
                layoutParams.height = b0.this.o;
                setLayoutParams(layoutParams);
                ViewGroup.LayoutParams layoutParams2 = this.C.getLayoutParams();
                layoutParams2.width = b0.this.n;
                layoutParams2.height = b0.this.o;
                this.C.setLayoutParams(layoutParams2);
                motionEvent2 = this.G;
                if (motionEvent2 == null) {
                    return;
                }
            }
            motionEvent2.recycle();
            this.G = null;
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked != 0) {
                if (actionMasked == 1) {
                    m(motionEvent);
                } else if (actionMasked == 2) {
                    k(motionEvent);
                } else if (actionMasked == 3) {
                    i(motionEvent);
                } else if (actionMasked == 5) {
                    l(motionEvent);
                } else if (actionMasked != 6 || this.D == 3) {
                    this.C.dispatchTouchEvent(motionEvent);
                }
                b0.this.n();
            } else {
                b0.this.m();
                j(motionEvent);
            }
            return true;
        }
    }

    public b0(Context context) {
        this(context, context);
    }

    public b0(Context context, Context context2) {
        this.i = null;
        this.j = 0;
        this.k = 0L;
        this.l = h0.a();
        this.m = null;
        this.n = 0;
        this.o = 0;
        this.p = 0;
        this.q = 0;
        this.f523a = context;
        this.f524b = context2;
        this.f525c = new m(context);
        this.f526d = AppWidgetManager.getInstance(context2);
        this.f527e = new AppWidgetHost(context2, 1);
        this.f528f = a.x.h();
        this.f529g = a.x.d(context);
        this.h = a0.n(context, new Point()).x;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void l(int i, int i2) {
        this.f525c.a(this.p + i, this.q + i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void m() {
        Runnable runnable = this.i;
        if (runnable != null) {
            this.l.removeCallbacks(runnable);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void n() {
        Runnable runnable;
        long j = this.k;
        if (j <= 0 || (runnable = this.i) == null) {
            return;
        }
        this.l.postDelayed(runnable, j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void o(int i, int i2) {
        int i3 = i + this.n;
        int i4 = i2 + this.o;
        int i5 = this.f529g;
        if (i3 < i5) {
            i3 = i5;
        } else {
            int i6 = this.h;
            if (i3 > i6) {
                i3 = i6;
            }
        }
        if (i4 < i5 || i4 > (i5 = this.h)) {
            i4 = i5;
        }
        this.f525c.c(i3, i4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void p(int i, int i2) {
        try {
            int i3 = i + this.p;
            int i4 = i2 + this.q;
            this.f528f.o(this.j, i3, i4);
            this.p = i3;
            this.q = i4;
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void q(AppWidgetHostView appWidgetHostView, int i, int i2) {
        try {
            int i3 = i + this.n;
            int i4 = i2 + this.o;
            int i5 = this.f529g;
            if (i3 < i5) {
                i3 = i5;
            } else {
                int i6 = this.h;
                if (i3 > i6) {
                    i3 = i6;
                }
            }
            if (i4 < i5 || i4 > (i5 = this.h)) {
                i4 = i5;
            }
            this.f528f.p(this.j, i3, i4);
            this.n = i3;
            this.o = i4;
            t(appWidgetHostView, i3, i4);
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    private void t(AppWidgetHostView appWidgetHostView, int i, int i2) {
        if (appWidgetHostView != null) {
            int iT = a0.t(i);
            int iT2 = a0.t(i2);
            appWidgetHostView.updateAppWidgetSize(null, iT, iT2, iT, iT2);
        }
    }

    private void u(int i, int i2, int i3) {
        int iT = a0.t(i2);
        int iT2 = a0.t(i3);
        Bundle bundle = new Bundle();
        bundle.putInt("appWidgetMinWidth", iT);
        bundle.putInt("appWidgetMinHeight", iT2);
        bundle.putInt("appWidgetMaxWidth", iT);
        bundle.putInt("appWidgetMaxHeight", iT2);
        this.f526d.updateAppWidgetOptions(i, bundle);
    }

    public void j() {
        if (this.j != 0) {
            this.f525c.b();
            this.j = 0;
            this.f527e.stopListening();
            Runnable runnable = this.i;
            if (runnable != null) {
                runnable.run();
            }
        }
        this.m = null;
    }

    public int k() {
        return this.j;
    }

    public void r(Runnable runnable) {
        this.i = runnable;
    }

    public boolean s(int i, int i2) {
        x.a aVarF;
        AppWidgetProviderInfo appWidgetInfo = this.f526d.getAppWidgetInfo(i);
        if (appWidgetInfo == null || (aVarF = this.f528f.f(i)) == null) {
            this.j = 0;
            f.v.c("Invalid widget!");
            Toast.makeText(this.f524b, R.string.invalid_widget, 0).show();
            return false;
        }
        int iC = aVarF.f55d;
        int iC2 = aVarF.f56e;
        if (iC <= 1 || iC2 <= 1) {
            int i3 = appWidgetInfo.minWidth;
            if (i3 <= 1 || appWidgetInfo.minHeight <= 1) {
                f.v.c("Error read widget size!");
                return false;
            }
            iC = a.x.c(this.f523a, i3);
            iC2 = a.x.c(this.f523a, appWidgetInfo.minHeight);
        }
        this.f527e.startListening();
        AppWidgetHostView appWidgetHostViewCreateView = this.f527e.createView(this.f524b, i, appWidgetInfo);
        a aVar = new a(this.f523a);
        aVar.addView(appWidgetHostViewCreateView, iC, iC2);
        aVar.addView(new b(this.f523a, appWidgetHostViewCreateView), iC, iC2);
        try {
            this.f525c.d(aVar, iC, iC2, aVarF.f53b, aVarF.f54c);
            u(i, iC, iC2);
            this.j = i;
            this.m = aVar;
            this.n = iC;
            this.o = iC2;
            this.p = aVarF.f53b;
            this.q = aVarF.f54c;
            this.k = ((long) i2) * 1000;
            n();
            return true;
        } catch (Throwable th) {
            f.v.d(th);
            Toast.makeText(this.f524b, th.getMessage(), 0).show();
            this.f527e.stopListening();
            this.j = 0;
            return false;
        }
    }
}
