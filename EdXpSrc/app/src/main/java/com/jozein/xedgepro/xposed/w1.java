package com.jozein.xedgepro.xposed;

import a.b;
import a.h;
import a.s;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.Display;
import android.view.MotionEvent;
import android.view.RoundedCorner;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cc.xposed.R;
import com.jozein.xedgepro.service.AdapterService;
import com.jozein.xedgepro.service.BinderService;
import com.jozein.xedgepro.service.ServiceTorch;
import com.jozein.xedgepro.service.ServiceWidget;
import com.jozein.xedgepro.xposed.a0;
import com.jozein.xedgepro.xposed.b0;
import com.jozein.xedgepro.xposed.h3;
import com.jozein.xedgepro.xposed.l0;
import com.jozein.xedgepro.xposed.t2;
import com.jozein.xedgepro.xposed.t3;
import com.jozein.xedgepro.xposed.v3;
import com.jozein.xedgepro.xposed.w1;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import f.a;
import f.c0;
import f.f0;
import f.i;
import f.n0;
import g.l;
import g.o;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: loaded from: classes.dex */
class w1 extends j2 {
    private static final AtomicInteger S0 = new AtomicInteger(0);
    private static final String T0 = f.l.k + "NOTIFICATION.";
    private r3 A0;
    private Toast B0;
    private volatile boolean C0;
    private k3 D0;
    private final Runnable E0;
    private final Runnable F0;
    private t2 G0;
    private final a.p H;
    private final q0[] H0;
    private final a.k I;
    private PowerManager.WakeLock I0;
    private final a.f J;
    private f.n0 J0;
    private final s.e K;
    private View K0;
    private final a.v L;
    private boolean L0;
    private final i.f M;
    private final p0 M0;
    private final i.f N;
    private f.m0 N0;
    private final y0 O;
    private int O0;
    private final com.jozein.xedgepro.xposed.l0 P;
    private f.w P0;
    private final com.jozein.xedgepro.xposed.n0 Q;
    private final n0 Q0;
    private final com.jozein.xedgepro.xposed.p0 R;
    private int R0;
    private final com.jozein.xedgepro.xposed.v S;
    private i3 T;
    private a.t U;
    private com.jozein.xedgepro.xposed.e V;
    private v3 W;
    private y2 X;
    private d3 Y;
    private final com.jozein.xedgepro.xposed.r Z;
    private Context a0;
    private Context b0;
    private Handler c0;
    private PowerManager d0;
    private PowerManager.WakeLock e0;
    private boolean f0;
    private int g0;
    private volatile long h0;
    private final boolean i0;
    private boolean j0;
    private boolean k0;
    private boolean l0;
    private boolean m0;
    private boolean n0;
    private f.g0 o0;
    private float p0;
    private boolean q0;
    private long r0;
    private boolean s0;
    private Method t0;
    private final Runnable u0;
    private boolean v0;
    private volatile boolean w0;
    private Rect x0;
    private int y0;
    private Method z0;

    class a extends XC_MethodHook {
        a() {
        }

        protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            w1.this.A4(((Integer) methodHookParam.args[0]).intValue());
        }
    }

    class a0 extends FrameLayout {
        a0(Context context) {
            super(context);
        }

        @Override // android.view.View
        public boolean onGenericMotionEvent(MotionEvent motionEvent) {
            View childAt = getChildAt(0);
            return childAt != null ? childAt.onGenericMotionEvent(motionEvent) : super.onGenericMotionEvent(motionEvent);
        }
    }

    class b extends BroadcastReceiver {
        b() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                if (a.j.E.equals(action)) {
                    w1.this.e3(intent);
                } else if (a.b.C.equals(action)) {
                    w1.this.w3(f.m.m(intent));
                } else if (a.b.D.equals(action)) {
                    if (w1.this.H.o(32)) {
                        w1.this.w3(a.b.f(intent.getStringExtra("data")));
                    } else {
                        w1.this.K4(R.string.perform_by_broadcast_not_allowed);
                    }
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
    }

    class b0 extends ListView {
        final /* synthetic */ o2 z;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        b0(Context context, o2 o2Var) {
            super(context);
            this.z = o2Var;
        }

        @Override // android.view.View
        public boolean hasWindowFocus() {
            return true;
        }

        @Override // android.widget.AbsListView, android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (this.z.c()) {
                return true;
            }
            try {
                return super.onTouchEvent(motionEvent);
            } catch (Throwable th) {
                this.z.b(th);
                return true;
            }
        }
    }

    class c implements t3.d {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        boolean f258a = false;

        c() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void d() {
            try {
                w1.this.e5(1);
            } catch (Throwable th) {
                f.v.c(th.toString());
            }
        }

        @Override // com.jozein.xedgepro.xposed.t3.d
        public void a() {
            this.f258a = w1.this.w0 && !w1.this.H.o(35);
        }

        @Override // com.jozein.xedgepro.xposed.t3.d
        public void b() {
            if (this.f258a) {
                this.f258a = false;
                w1.this.c0.postDelayed(new Runnable() { // from class: com.jozein.xedgepro.xposed.x1
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.z.d();
                    }
                }, 200L);
            }
        }
    }

    class c0 implements n0 {
        c0() {
        }

        @Override // com.jozein.xedgepro.xposed.w1.n0
        public boolean a() {
            return false;
        }

        @Override // com.jozein.xedgepro.xposed.w1.n0
        public a.b b() {
            return null;
        }
    }

    class d extends f.g0 {
        d(Handler handler, int i, long j) {
            super(handler, i, j);
        }

        @Override // f.g0
        protected boolean a(int i) {
            if (w1.this.n0) {
                return false;
            }
            if (!BinderService.f(w1.this.a0)) {
                return true;
            }
            boolean zF0 = w1.this.P.F0(AdapterService.class.getName());
            if (!zF0) {
                f.v.d(new Throwable("Service was prevented!"));
            }
            w1.this.d3(zF0);
            return false;
        }

        @Override // f.g0
        protected void b() {
            if (w1.this.n0) {
                return;
            }
            if ((w1.this.m0 || !w1.this.t2()) && w1.S0.get() >= 2) {
                w1.this.d3(false);
            }
        }
    }

    class d0 extends XC_MethodHook {
        d0() {
        }

        protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            w1.this.A(methodHookParam.thisObject);
            f.v.c("Initializing...");
            try {
                w1.this.a0 = (Context) methodHookParam.args[0];
                w1 w1Var = w1.this;
                w1Var.c0 = w1Var.L1();
                com.jozein.xedgepro.xposed.n0 n0Var = w1.this.Q;
                Handler handler = w1.this.c0;
                Object[] objArr = methodHookParam.args;
                n0Var.e0(handler, objArr[1] != null ? objArr[1] : objArr[2]);
                w1.this.i2();
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
    }

    class e extends View {
        final RectF A;
        final /* synthetic */ Paint B;
        final /* synthetic */ Paint C;
        final float z;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        e(Context context, Paint paint, Paint paint2) {
            super(context);
            this.B = paint;
            this.C = paint2;
            this.z = w1.this.c2() * g.a0.j();
            this.A = new RectF();
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            this.A.right = getRight();
            this.A.bottom = getBottom();
            RectF rectF = this.A;
            float f2 = this.z;
            canvas.drawRoundRect(rectF, f2, f2, this.B);
            RectF rectF2 = this.A;
            float f3 = this.z;
            canvas.drawRoundRect(rectF2, f3, f3, this.C);
        }
    }

    class e0 extends XC_MethodHook {
        e0() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void b() {
            if (!w1.this.j0) {
                w1.this.i2();
                Object objU = null;
                try {
                    objU = w1.this.u("mWindowManager");
                    if (objU == null) {
                        objU = w1.this.u("mWindowManagerFuncs");
                    }
                } catch (Throwable th) {
                    f.v.d(th);
                }
                w1.this.Q.e0(w1.this.c0, objU);
            }
            w1.this.p3();
        }

        protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            w1.this.A(methodHookParam.thisObject);
            f.v.c("System booted.");
            if (w1.this.c0 == null) {
                w1 w1Var = w1.this;
                w1Var.c0 = w1Var.L1();
            }
            w1.this.c0.post(new Runnable() { // from class: com.jozein.xedgepro.xposed.b2
                @Override // java.lang.Runnable
                public final void run() {
                    this.z.b();
                }
            });
        }
    }

    class f extends v0 {
        f(CharSequence charSequence) {
            super(w1.this, charSequence, null);
        }

        @Override // com.jozein.xedgepro.xposed.w1.v0
        protected int m() {
            return 14;
        }

        @Override // com.jozein.xedgepro.xposed.w1.v0
        protected void r(int i) {
            int i2 = i + 1;
            w1.this.H.c0(w1.this.a0, i2 >= 1 ? i2 : 1);
            if (w1.this.K0 != null) {
                w1.this.K0.setBackgroundColor(w1.this.H.B());
            }
        }
    }

    class f0 extends XC_MethodHook {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final int f263a;

        f0() {
            this.f263a = Build.VERSION.SDK_INT > 28 ? 0 : 1;
        }

        protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            if (w1.this.k0) {
                try {
                    w1.this.Q.d0().T(methodHookParam.args[this.f263a]);
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
        }
    }

    class g extends v0 {
        final ContentResolver K;
        final com.jozein.xedgepro.xposed.f L;

        g(CharSequence charSequence) {
            super(w1.this, charSequence, null);
            this.K = w1.this.a0.getContentResolver();
            this.L = com.jozein.xedgepro.xposed.f.e(w1.this.a0, true);
        }

        @Override // com.jozein.xedgepro.xposed.w1.v0
        protected int m() {
            if (!w1.this.H.o(34) && h3.c.a(this.K, "screen_brightness_mode", 0) != 0) {
                h3.c.b(this.K, "screen_brightness_mode", 0);
            }
            return this.L.f();
        }

        @Override // com.jozein.xedgepro.xposed.w1.v0
        protected void r(int i) {
            this.L.k(w1.this.a0, i);
        }
    }

    class g0 extends XC_MethodHook {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final Runnable f265a = new a();

        class a implements Runnable {
            a() {
            }

            @Override // java.lang.Runnable
            public void run() {
                View view = w1.this.K0;
                if (view != null) {
                    view.setVisibility(0);
                    view.invalidate();
                }
            }
        }

        g0() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void c(XC_MethodHook.MethodHookParam methodHookParam, long j) {
            try {
                XposedBridge.invokeOriginalMethod(methodHookParam.method, methodHookParam.thisObject, methodHookParam.args);
                w1.this.c0.postDelayed(this.f265a, j);
            } catch (Throwable th) {
                f.v.d(th);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void d(final XC_MethodHook.MethodHookParam methodHookParam, final long j) {
            View view = w1.this.K0;
            if (view != null) {
                view.setVisibility(4);
                view.invalidate();
            }
            w1.this.c0.postDelayed(new Runnable() { // from class: com.jozein.xedgepro.xposed.c2
                @Override // java.lang.Runnable
                public final void run() {
                    this.z.c(methodHookParam, j);
                }
            }, 700L);
        }

        /* JADX WARN: Removed duplicated region for block: B:12:0x002d  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        protected void beforeHookedMethod(final de.robv.android.xposed.XC_MethodHook.MethodHookParam r8) {
            /*
                r7 = this;
                com.jozein.xedgepro.xposed.w1 r0 = com.jozein.xedgepro.xposed.w1.this
                android.view.View r0 = com.jozein.xedgepro.xposed.w1.Z0(r0)
                if (r0 != 0) goto L9
                return
            L9:
                java.lang.reflect.Member r0 = r8.method
                java.lang.String r0 = r0.getName()
                java.lang.String r1 = "takeScreenshot"
                boolean r0 = r1.equals(r0)
                r1 = 6000(0x1770, double:2.9644E-320)
                r3 = 2000(0x7d0, double:9.88E-321)
                r5 = 2
                if (r0 == 0) goto L2f
                java.lang.Object[] r0 = r8.args
                int r6 = r0.length
                if (r6 <= 0) goto L2d
                r6 = 0
                r0 = r0[r6]
                java.lang.Integer r0 = (java.lang.Integer) r0
                int r0 = r0.intValue()
                if (r0 != r5) goto L2d
                goto L39
            L2d:
                r1 = r3
                goto L39
            L2f:
                java.lang.Object r0 = r8.thisObject
                java.lang.String r6 = "mScreenshotType"
                int r0 = de.robv.android.xposed.XposedHelpers.getIntField(r0, r6)
                if (r0 != r5) goto L2d
            L39:
                com.jozein.xedgepro.xposed.w1 r0 = com.jozein.xedgepro.xposed.w1.this
                android.os.Handler r0 = com.jozein.xedgepro.xposed.w1.j0(r0)
                com.jozein.xedgepro.xposed.d2 r3 = new com.jozein.xedgepro.xposed.d2
                r3.<init>()
                r0.post(r3)
                r0 = 0
                r8.setResult(r0)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.jozein.xedgepro.xposed.w1.g0.beforeHookedMethod(de.robv.android.xposed.XC_MethodHook$MethodHookParam):void");
        }
    }

    class h extends v0 {
        int K;
        int L;

        h(CharSequence charSequence) {
            super(w1.this, charSequence, null);
            this.K = -1;
            this.L = 0;
        }

        @Override // com.jozein.xedgepro.xposed.w1.v0
        protected int m() {
            return 32;
        }

        @Override // com.jozein.xedgepro.xposed.w1.v0
        protected void r(int i) {
            if (this.K == -1) {
                this.K = i;
            }
            int i2 = i - this.K;
            int i3 = this.L;
            if (i2 != i3) {
                int i4 = i2 < i3 ? i3 - i2 : i2 - i3;
                for (int i5 = 0; i5 < i4; i5++) {
                    w1.this.V2(i2 < this.L);
                }
                this.L = i2;
            }
        }
    }

    class h0 extends XC_MethodHook {
        h0() {
        }

        protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            try {
                if (w1.this.Z.V()) {
                    w1 w1Var = w1.this;
                    w1Var.o5(w1Var.w0 ? w1.this.Q.d0().E() : null);
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
    }

    class i extends v0 {
        final /* synthetic */ int K;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        i(CharSequence charSequence, int i) {
            super(w1.this, charSequence, null);
            this.K = i;
        }

        @Override // com.jozein.xedgepro.xposed.w1.v0
        protected int m() {
            return w1.this.V.e(this.K);
        }

        @Override // com.jozein.xedgepro.xposed.w1.v0
        protected void r(int i) {
            w1.this.V.k(this.K, i, 0);
        }
    }

    class i0 extends XC_MethodHook {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private final Region f268a = new Region();

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private final Rect f269b = new Rect();

        i0() {
        }

        protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            if (w1.this.w0) {
                try {
                    Object obj = methodHookParam.args[0];
                    if (u3.c(obj).type == 2011) {
                        try {
                            u3.e(obj, this.f268a).getBounds(this.f269b);
                        } catch (Throwable th) {
                            f.v.d(th);
                            u3.g(obj, this.f269b);
                        }
                        w1.this.o5(this.f269b);
                    }
                } catch (Throwable th2) {
                    f.v.d(th2);
                }
            }
        }
    }

    class j extends w0 {
        final float C;
        final Point D;
        final /* synthetic */ b.i3 E;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        j(Handler handler, b.i3 i3Var) {
            super(handler, null);
            this.E = i3Var;
            this.C = w1.this.H.v(17, 100) / 100.0f;
            this.D = w1.this.Q.Z();
        }

        @Override // com.jozein.xedgepro.xposed.w1.w0
        protected void g() {
            y0 y0Var = w1.this.O;
            float f2 = this.E.J ? this.C : -this.C;
            Point point = this.D;
            y0Var.g0(f2, point.x, point.y);
        }
    }

    class j0 extends XC_MethodHook {
        j0() {
        }

        protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            w1.this.y1();
        }
    }

    class k implements i.f {
        k() {
        }

        @Override // f.i.f
        public int a(String str) {
            return w1.this.L.g(str).f488a;
        }

        @Override // f.i.f
        public f.t b(String str) {
            return w1.this.L.g(str);
        }
    }

    private class k0 extends BaseAdapter implements AdapterView.OnItemClickListener {
        private final int A;
        private final int B;
        private final int C;
        private final AbsListView.LayoutParams D;
        private final PorterDuffColorFilter E;
        private int[] F;
        private final l0.f G;
        private final Resources H;
        private final int z;

        /* JADX WARN: Removed duplicated region for block: B:21:0x0099  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        k0(com.jozein.xedgepro.xposed.l0.f r6) {
            /*
                r4 = this;
                com.jozein.xedgepro.xposed.w1.this = r5
                r4.<init>()
                r4.G = r6
                a.p r0 = com.jozein.xedgepro.xposed.w1.c0(r5)
                r1 = 33
                r2 = -16764873(0xffffffffff003037, float:-1.7039153E38)
                int r0 = r0.v(r1, r2)
                boolean r0 = g.a0.r(r0)
                r1 = -1
                if (r0 == 0) goto L21
                r4.z = r1
                r0 = 0
                r4.E = r0
                goto L2e
            L21:
                r0 = -16777216(0xffffffffff000000, float:-1.7014118E38)
                r4.z = r0
                android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
                android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.SRC_ATOP
                r2.<init>(r0, r3)
                r4.E = r2
            L2e:
                android.content.Context r0 = com.jozein.xedgepro.xposed.w1.X(r5)
                g.x r0 = g.x.c(r0)
                int r2 = r0.f636a
                r4.A = r2
                int r3 = r0.f637b
                r4.B = r3
                int r0 = r0.f641f
                r4.C = r0
                android.widget.AbsListView$LayoutParams r0 = new android.widget.AbsListView$LayoutParams
                r0.<init>(r1, r2)
                r4.D = r0
                android.content.Context r5 = com.jozein.xedgepro.xposed.w1.z0(r5)
                android.content.res.Resources r5 = r5.getResources()
                r4.H = r5
                java.util.ArrayList r5 = new java.util.ArrayList
                r5.<init>()
                r0 = 0
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                r5.add(r0)
                boolean r0 = r6.B()
                if (r0 != 0) goto L6e
                r0 = 1
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                r5.add(r0)
            L6e:
                boolean r0 = r6.u()
                if (r0 == 0) goto L7c
                r0 = 2
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                r5.add(r0)
            L7c:
                boolean r0 = r6.C()
                if (r0 == 0) goto L8b
                r0 = 3
            L83:
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                r5.add(r0)
                goto L93
            L8b:
                boolean r0 = r6.s()
                if (r0 == 0) goto L93
                r0 = 4
                goto L83
            L93:
                android.content.ComponentName r6 = r6.x()
                if (r6 == 0) goto La1
                r6 = 6
                java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
                r5.add(r6)
            La1:
                r6 = 7
                java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
                r5.add(r6)
                r6 = 5
                java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
                r5.add(r6)
                int[] r5 = r4.d(r5)
                r4.F = r5
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.jozein.xedgepro.xposed.w1.k0.<init>(com.jozein.xedgepro.xposed.w1, com.jozein.xedgepro.xposed.l0$f):void");
        }

        private void b(a.b bVar) {
            if (bVar != null) {
                BinderService.h(w1.this.a0, bVar);
            }
        }

        private Drawable c(int i) {
            Drawable drawable = this.H.getDrawable(i);
            g.h.a().b(drawable);
            return drawable;
        }

        private int[] d(List<Integer> list) {
            int size = list.size();
            int[] iArr = new int[size];
            for (int i = 0; i < size; i++) {
                iArr[i] = list.get(i).intValue();
            }
            return iArr;
        }

        private LinearLayout e(int i, int i2) {
            return f(c(i), this.H.getText(i2));
        }

        private LinearLayout f(Drawable drawable, CharSequence charSequence) {
            return g(drawable, charSequence, null);
        }

        private LinearLayout g(Drawable drawable, CharSequence charSequence, CharSequence charSequence2) {
            LinearLayout linearLayout = new LinearLayout(w1.this.a0);
            linearLayout.setOrientation(0);
            linearLayout.setLayoutParams(this.D);
            int i = this.C;
            linearLayout.setPadding(i + i, 0, 0, 0);
            ImageView imageView = new ImageView(w1.this.a0);
            drawable.setColorFilter(this.E);
            imageView.setImageDrawable(drawable);
            int i2 = this.C;
            imageView.setPadding(i2, 0, i2, 0);
            int i3 = this.B;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(i3, i3);
            layoutParams.gravity = 16;
            linearLayout.addView(imageView, 0, layoutParams);
            TextView textView = new TextView(w1.this.a0);
            textView.setText(charSequence);
            textView.setTextSize(0, this.A / 3.0f);
            textView.setSingleLine(true);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setTextColor(this.z);
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, -2, 1.0f);
            layoutParams2.gravity = 16;
            if (charSequence2 != null) {
                LinearLayout linearLayout2 = new LinearLayout(w1.this.a0);
                linearLayout2.setOrientation(1);
                linearLayout2.addView(textView);
                TextView textView2 = new TextView(w1.this.a0);
                textView2.setText(charSequence2);
                textView2.setTextSize(0, this.A / 4.0f);
                textView2.setSingleLine(true);
                textView2.setEllipsize(TextUtils.TruncateAt.END);
                textView2.setTextColor(this.z);
                linearLayout2.addView(textView2);
                linearLayout.addView(linearLayout2, layoutParams2);
            } else {
                linearLayout.addView(textView, layoutParams2);
            }
            return linearLayout;
        }

        private void h(AdapterView<?> adapterView) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(9);
            if (this.G.t()) {
                arrayList.add(10);
            }
            if (this.G.q() != null) {
                arrayList.add(11);
            }
            if (this.G.r() != null) {
                arrayList.add(12);
            }
            this.F = d(arrayList);
            notifyDataSetChanged();
            if (adapterView instanceof ListView) {
                ListView listView = (ListView) adapterView;
                listView.setPressed(false);
                listView.jumpDrawablesToCurrentState();
                listView.setSelectionFromTop(0, 0);
            }
            g.a0.z(adapterView, new TranslateAnimation(0.0f, 0.0f, this.A * this.F.length, 0.0f), ((((long) this.F.length) * 66) * 3) / 4);
        }

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.F.length;
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return null;
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            try {
                switch (this.F[i]) {
                    case 0:
                        LinearLayout linearLayoutF = f(this.G.d(), this.G.f());
                        linearLayoutF.setPadding(linearLayoutF.getPaddingLeft() / 2, 0, 0, 0);
                        return linearLayoutF;
                    case 1:
                        return e(R.drawable.ic_play, R.string.launch);
                    case 2:
                        return e(R.drawable.ic_kill_app, R.string.stop_app);
                    case 3:
                        return e(R.drawable.ic_unfreeze, R.string.unfreeze);
                    case 4:
                        return e(R.drawable.ic_freezer, R.string.freeze);
                    case 5:
                        return e(R.drawable.ic_about, R.string.app_info);
                    case 6:
                        return e(R.drawable.ic_activity, R.string.action_activity);
                    case 7:
                        return e(R.drawable.ic_play_store, R.string.view_on_play_store);
                    case 8:
                        return e(R.drawable.ic_freezer, R.string.confirm_freeze);
                    case 9:
                        LinearLayout linearLayoutG = g(this.G.d(), this.G.f(), this.G.z());
                        linearLayoutG.setPadding(linearLayoutG.getPaddingLeft() / 2, 0, 0, 0);
                        return linearLayoutG;
                    case 10:
                        return e(R.drawable.ic_finish_activity, R.string.action_finish_activity);
                    case 11:
                        return e(R.drawable.ic_collect_activity, R.string.collect_activity);
                    case 12:
                        return e(R.drawable.ic_collect_activity_with_params, R.string.collect_activity_with_params);
                    case 13:
                        return f(c(R.drawable.ic_app), "Install exists");
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
            return new View(w1.this.a0);
        }

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean isEnabled(int i) {
            return i != 0;
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            a.b bVarQ;
            try {
            } catch (Throwable th) {
                w1.this.G4(th.getMessage());
                f.v.d(th);
            }
            switch (this.F[i]) {
                case 0:
                case 9:
                    break;
                case 1:
                    this.G.E(com.jozein.xedgepro.xposed.a.d(w1.this.P, 3));
                    try {
                        t3.n().t(19);
                    } catch (Throwable th2) {
                        f.v.d(th2);
                    }
                    break;
                case 2:
                    this.G.w();
                    t3.n().t(19);
                    break;
                case 3:
                    this.G.F(false);
                    t3.n().t(19);
                    break;
                case 4:
                    this.F = new int[]{0, 8};
                    notifyDataSetChanged();
                    g.a0.z(adapterView, new TranslateAnimation(0.0f, 0.0f, this.A * this.F.length, 0.0f), ((((long) this.F.length) * 66) * 3) / 4);
                    break;
                case 5:
                    this.G.G();
                    t3.n().t(19);
                    break;
                case 6:
                    h(adapterView);
                    break;
                case 7:
                    this.G.H();
                    t3.n().t(19);
                    break;
                case 8:
                    this.G.F(true);
                    t3.n().t(19);
                    break;
                case 10:
                    this.G.v();
                    t3.n().t(19);
                    break;
                case 11:
                    bVarQ = this.G.q();
                    b(bVarQ);
                    t3.n().t(19);
                    break;
                case 12:
                    bVarQ = this.G.r();
                    b(bVarQ);
                    t3.n().t(19);
                    break;
                case 13:
                    this.G.A();
                    t3.n().t(19);
                    break;
                default:
                    t3.n().t(19);
                    break;
            }
        }
    }

    class l extends w0 {
        final /* synthetic */ b.i3 C;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        l(Handler handler, b.i3 i3Var) {
            super(handler, null);
            this.C = i3Var;
        }

        @Override // com.jozein.xedgepro.xposed.w1.w0
        protected void g() {
            w1.this.V2(this.C.J);
        }
    }

    private class l0 implements n0 {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private final b.d0 f273a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private n0 f274b = null;

        l0(b.d0 d0Var) {
            this.f273a = d0Var;
        }

        @Override // com.jozein.xedgepro.xposed.w1.n0
        public boolean a() {
            n0 n0Var = this.f274b;
            return n0Var == null || n0Var.a();
        }

        @Override // com.jozein.xedgepro.xposed.w1.n0
        public a.b b() {
            w1 w1Var;
            a.b bVar;
            if (this.f274b == null) {
                if (w1.this.l1(this.f273a.I, false)) {
                    w1Var = w1.this;
                    bVar = this.f273a.J;
                } else {
                    w1Var = w1.this;
                    bVar = this.f273a.K;
                }
                this.f274b = w1Var.X2(bVar);
            }
            return this.f274b.b();
        }
    }

    class m extends w0 {
        final /* synthetic */ b.i3 C;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        m(Handler handler, b.i3 i3Var) {
            super(handler, null);
            this.C = i3Var;
        }

        @Override // com.jozein.xedgepro.xposed.w1.w0
        protected void g() {
            w1.this.e1(this.C.J);
        }
    }

    private class m0 extends View {
        private final Paint A;
        private final Paint B;
        private final Rect C;
        private final int D;
        private final boolean z;

        private m0(boolean z, int i) {
            super(w1.this.a0);
            Paint paint = new Paint(1);
            this.A = paint;
            Paint paint2 = new Paint(1);
            this.B = paint2;
            this.C = new Rect();
            this.z = z;
            paint.setColor(2147454720);
            paint2.setColor(2130739111);
            this.D = i;
        }

        /* synthetic */ m0(w1 w1Var, boolean z, int i, k kVar) {
            this(z, i);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            float f2;
            float f3;
            canvas.getClipBounds(this.C);
            int i = this.D;
            if (i == 0) {
                canvas.drawColor(this.A.getColor());
                return;
            }
            if (this.z) {
                int i2 = this.C.bottom / 3;
                if ((i & 1) != 0) {
                    canvas.drawRect(0.0f, 0.0f, r1.right, i2, this.A);
                }
                int i3 = this.D;
                if ((i3 & 2) != 0) {
                    canvas.drawRect(0.0f, i2, this.C.right, i2 * 2, i3 == 7 ? this.B : this.A);
                }
                if ((this.D & 4) == 0) {
                    return;
                }
                f2 = 0.0f;
                f3 = i2 * 2;
            } else {
                int i4 = this.C.right / 3;
                if ((i & 1) != 0) {
                    canvas.drawRect(0.0f, 0.0f, i4, r1.bottom, this.A);
                }
                int i5 = this.D;
                if ((i5 & 2) != 0) {
                    canvas.drawRect(i4, 0.0f, i4 * 2, this.C.bottom, i5 == 7 ? this.B : this.A);
                }
                if ((this.D & 4) == 0) {
                    return;
                }
                f2 = i4 * 2;
                f3 = 0.0f;
            }
            Rect rect = this.C;
            canvas.drawRect(f2, f3, rect.right, rect.bottom, this.A);
        }
    }

    class n extends w0 {
        final /* synthetic */ b.i3 C;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        n(Handler handler, b.i3 i3Var) {
            super(handler, null);
            this.C = i3Var;
        }

        @Override // com.jozein.xedgepro.xposed.w1.w0
        protected void g() {
            w1.this.d1(this.C.J);
        }
    }

    private interface n0 {
        boolean a();

        a.b b();
    }

    class o extends w0 {
        final int C;
        final int D;
        final int E;
        final /* synthetic */ b.i3 F;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        o(Handler handler, b.i3 i3Var) {
            super(handler, null);
            this.F = i3Var;
            this.C = i3Var.J ? 1 : -1;
            int i = i3Var.I - 3;
            this.D = i;
            int i2 = 4;
            if (w1.this.f0 && i <= 4) {
                i2 = 5;
            }
            this.E = i2;
        }

        @Override // com.jozein.xedgepro.xposed.w1.w0
        protected void g() {
            w1.this.V.a(this.D, this.C, this.E);
        }
    }

    private class o0 implements n0 {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private final a.b[] f276a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private int f277b = 0;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        private final n0[] f278c;

        o0(a.b[] bVarArr) {
            this.f276a = bVarArr;
            this.f278c = new n0[bVarArr.length];
        }

        @Override // com.jozein.xedgepro.xposed.w1.n0
        public boolean a() {
            n0[] n0VarArr = this.f278c;
            int length = n0VarArr.length - 1;
            int i = this.f277b;
            if (i >= length) {
                return i == length && (n0VarArr[length] == null || n0VarArr[length].a());
            }
            return true;
        }

        @Override // com.jozein.xedgepro.xposed.w1.n0
        public a.b b() {
            int i = this.f277b;
            n0[] n0VarArr = this.f278c;
            if (i >= n0VarArr.length) {
                return null;
            }
            if (n0VarArr[i] == null) {
                n0VarArr[i] = w1.this.X2(this.f276a[i]);
            }
            a.b bVarB = this.f278c[this.f277b].b();
            if (bVarB != null) {
                return bVarB;
            }
            this.f277b++;
            return b();
        }
    }

    class p implements Runnable {
        p() {
        }

        @Override // java.lang.Runnable
        public void run() {
            w1.this.O.V().S();
            t3.n().u(7, 100L);
        }
    }

    private class p0 implements Runnable, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {
        private final ArrayList<MediaPlayer> z;

        private p0() {
            this.z = new ArrayList<>();
        }

        /* synthetic */ p0(w1 w1Var, k kVar) {
            this();
        }

        private String c(int i) {
            return i != 2 ? i != 4 ? "notification" : "alarm" : "ringtone";
        }

        private int d(int i) {
            if (i != 2) {
                return i != 4 ? 2 : 4;
            }
            return 1;
        }

        void a(Uri uri, int i, boolean z) throws IOException {
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(w1.this.a0, uri);
            } catch (IOException unused) {
                f.v.c("Error playing " + uri + ". Using default " + c(i) + " instead.");
                mediaPlayer.setDataSource(w1.this.a0, RingtoneManager.getDefaultUri(d(i)));
            }
            mediaPlayer.setAudioStreamType(i);
            mediaPlayer.setLooping(z);
            if (!z) {
                mediaPlayer.setOnCompletionListener(this);
            }
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepareAsync();
        }

        void b() {
            Iterator<MediaPlayer> it = this.z.iterator();
            while (it.hasNext()) {
                it.next().release();
            }
            this.z.clear();
        }

        @Override // android.media.MediaPlayer.OnCompletionListener
        public void onCompletion(MediaPlayer mediaPlayer) {
            try {
                mediaPlayer.release();
                this.z.remove(mediaPlayer);
                if (this.z.size() == 0) {
                    w1.this.c0.removeCallbacks(this);
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }

        @Override // android.media.MediaPlayer.OnPreparedListener
        public void onPrepared(MediaPlayer mediaPlayer) {
            try {
                mediaPlayer.start();
                int size = this.z.size();
                if (size > 8) {
                    this.z.remove(0).release();
                }
                this.z.add(mediaPlayer);
                if (size > 0) {
                    w1.this.c0.removeCallbacks(this);
                    w1.this.c0.postDelayed(this, 600000L);
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            b();
        }
    }

    class q implements Runnable {
        final int B;
        private final com.jozein.xedgepro.xposed.f0 C;
        final /* synthetic */ List D;
        final /* synthetic */ Handler E;
        long z = 0;
        int A = 0;

        q(List list, Handler handler) {
            this.D = list;
            this.E = handler;
            this.B = list.size();
            this.C = w1.this.H.o(7) ? new com.jozein.xedgepro.xposed.f0(w1.this.a0, w1.this.c0, false) : null;
        }

        void a() {
            com.jozein.xedgepro.xposed.f0 f0Var = this.C;
            if (f0Var != null) {
                f0Var.e();
            }
        }

        /* JADX WARN: Code restructure failed: missing block: B:29:0x0086, code lost:
        
            a();
         */
        /* JADX WARN: Code restructure failed: missing block: B:30:0x0089, code lost:
        
            return;
         */
        @Override // java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void run() {
            /*
                r9 = this;
                long r0 = r9.z
                r2 = 0
                int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r4 != 0) goto L15
                long r0 = android.os.SystemClock.uptimeMillis()
                r9.z = r0
                com.jozein.xedgepro.xposed.f0 r0 = r9.C
                if (r0 == 0) goto L15
                r0.i()
            L15:
                r0 = r2
            L16:
                int r4 = r9.A
                int r5 = r9.B
                r6 = 1
                if (r4 >= r5) goto L8a
                com.jozein.xedgepro.xposed.w1 r4 = com.jozein.xedgepro.xposed.w1.this
                boolean r4 = com.jozein.xedgepro.xposed.w1.u0(r4)
                if (r4 != 0) goto L86
                com.jozein.xedgepro.xposed.w1 r4 = com.jozein.xedgepro.xposed.w1.this
                long r7 = r9.z
                boolean r4 = com.jozein.xedgepro.xposed.w1.v0(r4, r7)
                if (r4 == 0) goto L30
                goto L86
            L30:
                java.util.List r4 = r9.D
                int r5 = r9.A
                int r7 = r5 + 1
                r9.A = r7
                java.lang.Object r4 = r4.get(r5)
                g.q r4 = (g.q) r4
                int r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r5 != 0) goto L48
                long r0 = r9.z
                int r5 = r4.z
                long r7 = (long) r5
                long r0 = r0 + r7
            L48:
                com.jozein.xedgepro.xposed.w1 r5 = com.jozein.xedgepro.xposed.w1.this     // Catch: java.lang.Throwable -> L7e
                com.jozein.xedgepro.xposed.y0 r5 = com.jozein.xedgepro.xposed.w1.p0(r5)     // Catch: java.lang.Throwable -> L7e
                long r7 = r9.z     // Catch: java.lang.Throwable -> L7e
                android.view.MotionEvent r7 = r4.a(r7, r0)     // Catch: java.lang.Throwable -> L7e
                r5.f0(r7)     // Catch: java.lang.Throwable -> L7e
                com.jozein.xedgepro.xposed.f0 r5 = r9.C     // Catch: java.lang.Throwable -> L7e
                if (r5 == 0) goto L5e
                r5.d(r4)     // Catch: java.lang.Throwable -> L7e
            L5e:
                int r5 = r9.A
                int r7 = r9.B
                if (r5 >= r7) goto L16
                int r4 = r4.A
                r4 = r4 & 255(0xff, float:3.57E-43)
                if (r4 != r6) goto L16
                android.os.Handler r0 = r9.E
                long r1 = r9.z
                java.util.List r3 = r9.D
                java.lang.Object r3 = r3.get(r5)
                g.q r3 = (g.q) r3
                int r3 = r3.z
                long r3 = (long) r3
                long r1 = r1 + r3
                r0.postAtTime(r9, r1)
                goto L8a
            L7e:
                r0 = move-exception
                f.v.d(r0)
                r9.a()
                return
            L86:
                r9.a()
                return
            L8a:
                int r0 = r9.A
                int r1 = r9.B
                int r1 = r1 - r6
                if (r0 < r1) goto L94
                r9.a()
            L94:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.jozein.xedgepro.xposed.w1.q.run():void");
        }
    }

    class q0 {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private NotificationManager f280a = null;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private BroadcastReceiver f281b = null;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        private int f282c = -1;

        class a extends BroadcastReceiver {

            /* JADX INFO: renamed from: a, reason: collision with root package name */
            final /* synthetic */ a.b f284a;

            a(a.b bVar) {
                this.f284a = bVar;
            }

            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                try {
                    q0.this.a();
                    a.b bVar = this.f284a;
                    if (bVar != null) {
                        w1.this.w3(bVar);
                    }
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
        }

        q0() {
        }

        void a() {
            if (this.f281b != null) {
                w1.this.a0.unregisterReceiver(this.f281b);
                this.f281b = null;
                this.f280a.cancel(this.f282c);
            }
        }

        void b(CharSequence charSequence, int i, a.b bVar) {
            c(charSequence, null, i, 0, false, true, bVar);
        }

        void c(CharSequence charSequence, CharSequence charSequence2, int i, int i2, boolean z, boolean z2, a.b bVar) {
            Context context = w1.this.b0;
            int i3 = Build.VERSION.SDK_INT;
            Context context2 = i3 >= 29 ? w1.this.a0 : context;
            this.f282c = w1.M0(w1.this);
            String str = w1.T0 + this.f282c;
            CharSequence text = context.getText(f.l.y);
            Intent intent = new Intent(str);
            intent.setPackage("android");
            PendingIntent broadcast = PendingIntent.getBroadcast(w1.this.a0, 0, intent, i3 >= 31 ? 67108864 : 0);
            if (w1.this.P0 == null) {
                w1.this.P0 = f.w.a(f.l.x, text);
            }
            w1.this.P0.c(i2);
            if (z2) {
                w1.this.P0.d(f.w.D);
            }
            Notification.Builder deleteIntent = w1.this.P0.b(context2).setContentTitle(text).setContentText(charSequence).setContentIntent(broadcast).setDeleteIntent(broadcast);
            if (i3 >= 29) {
                Icon iconCreateWithResource = Icon.createWithResource(context, i);
                deleteIntent.setLargeIcon(iconCreateWithResource).setSmallIcon(iconCreateWithResource);
            } else {
                deleteIntent.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), i)).setSmallIcon(i);
            }
            if (charSequence2 != null) {
                deleteIntent.setTicker(charSequence2);
            }
            if (z) {
                Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();
                bigTextStyle.setBigContentTitle(text);
                bigTextStyle.bigText(charSequence);
                deleteIntent.setStyle(bigTextStyle);
            }
            Notification notificationBuild = deleteIntent.build();
            if (z2) {
                notificationBuild.flags |= 32;
            }
            NotificationManager notificationManager = (NotificationManager) context2.getSystemService("notification");
            this.f280a = notificationManager;
            notificationManager.notify(this.f282c, notificationBuild);
            this.f281b = new a(bVar);
            w1.this.a0.registerReceiver(this.f281b, new IntentFilter(str), null, w1.this.c0);
        }
    }

    class r extends r2 {
        boolean Q;

        r(Context context, Resources resources, int[] iArr, int i, boolean z) {
            super(context, resources, iArr, i, z);
            this.Q = false;
        }

        @Override // com.jozein.xedgepro.xposed.t3.c
        public void a() {
            if (this.Q) {
                this.Q = false;
                w1.this.S4(new b.p0(1));
            }
        }

        @Override // com.jozein.xedgepro.xposed.r2
        protected void r() {
            w1.this.t3();
        }

        @Override // com.jozein.xedgepro.xposed.r2
        protected void v(int i) {
            w1.this.C4(i);
        }

        @Override // com.jozein.xedgepro.xposed.r2
        protected void x(boolean z) {
            w1.this.S4(new b.p0(z ? 1 : 2));
            this.Q = !z;
        }
    }

    private class r0 implements Runnable, a.c {
        private final long A;
        private final PowerManager.WakeLock B;
        private final String C;
        private f.a D = null;
        private final n0 z;

        r0(a.b bVar, long j) {
            this.z = w1.this.X2(bVar);
            this.A = j;
            String str = "Queue-" + w1.T0(w1.this);
            this.C = str;
            PowerManager.WakeLock wakeLockNewWakeLock = w1.this.d0.newWakeLock(1, str);
            this.B = wakeLockNewWakeLock;
            wakeLockNewWakeLock.setReferenceCounted(false);
        }

        private void b() {
            this.B.acquire();
        }

        private void d() {
            try {
                a.b bVarB = this.z.b();
                if (bVarB != null && bVarB.z != 102 && !w1.this.s2(this.A)) {
                    long j = 0;
                    if (bVarB.z == 54) {
                        j = ((b.g0) bVarB).I;
                    } else {
                        try {
                            w1.this.y3(bVarB, -1, null);
                        } catch (Throwable th) {
                            f.v.d(th);
                            w1.this.F4(bVarB, th);
                            e();
                            return;
                        }
                    }
                    if (this.z.a()) {
                        f(j);
                        return;
                    } else {
                        e();
                        return;
                    }
                }
                e();
            } catch (Throwable th2) {
                f.v.d(th2);
                e();
            }
        }

        private void e() {
            this.B.release();
        }

        private void f(long j) {
            if (j < 30000) {
                b();
                w1.this.c0.postDelayed(this, j);
            } else {
                if (this.D == null) {
                    this.D = f.a.c(w1.this.a0, w1.this.c0, this, 2);
                }
                this.D.d(SystemClock.elapsedRealtime() + j);
                e();
            }
        }

        @Override // f.a.c
        public void a(f.a aVar, long j) {
            try {
                c();
            } catch (Throwable th) {
                f.v.d(th);
            }
        }

        void c() {
            b();
            d();
        }

        protected void finalize() {
            if (this.B.isHeld()) {
                this.B.release();
                f.v.c("WakeLock finalized while still held " + this.C);
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                d();
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
    }

    class s implements t2.e {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        List<c0.e> f286a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final /* synthetic */ int f287b;

        s(int i) {
            this.f287b = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void h(int i) {
            w1.this.Z3(this.f286a.get(i));
        }

        @Override // com.jozein.xedgepro.xposed.t2.e
        public boolean a() {
            return w1.this.H.o(11);
        }

        @Override // com.jozein.xedgepro.xposed.t2.e
        public boolean b(final int i) {
            w1.this.c0.post(new Runnable() { // from class: com.jozein.xedgepro.xposed.y1
                @Override // java.lang.Runnable
                public final void run() {
                    this.z.h(i);
                }
            });
            return true;
        }

        @Override // com.jozein.xedgepro.xposed.t2.e
        public Drawable c(int i) {
            return this.f286a.get(i).d();
        }

        @Override // com.jozein.xedgepro.xposed.t2.e
        public CharSequence d(int i) {
            return this.f286a.get(i).f();
        }

        @Override // com.jozein.xedgepro.xposed.t2.e
        public void e(int i, View view) {
            w1.this.p4(this.f286a.get(i), com.jozein.xedgepro.xposed.a.c(view));
        }

        @Override // com.jozein.xedgepro.xposed.t2.e
        public void f() {
        }

        @Override // com.jozein.xedgepro.xposed.t2.e
        public int getCount() {
            c0.c cVar = new c0.c();
            cVar.b(w1.this.I.i(), -1);
            w1.this.P.U(cVar);
            List<c0.e> listN = f.z.K(w1.this.a0).N(cVar, this.f287b | 65536);
            this.f286a = listN;
            return listN.size();
        }
    }

    private class s0 implements n0 {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private final a.b f289a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private int f290b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        private n0 f291c;

        s0(b.f2 f2Var) {
            a.b bVar = f2Var.J;
            this.f289a = bVar;
            this.f290b = f2Var.I;
            this.f291c = w1.this.X2(bVar);
        }

        @Override // com.jozein.xedgepro.xposed.w1.n0
        public boolean a() {
            int i = this.f290b;
            if (i <= 1) {
                return i == 1 && this.f291c.a();
            }
            return true;
        }

        @Override // com.jozein.xedgepro.xposed.w1.n0
        public a.b b() {
            if (this.f290b <= 0) {
                return null;
            }
            a.b bVarB = this.f291c.b();
            if (bVarB != null) {
                return bVarB;
            }
            int i = this.f290b - 1;
            this.f290b = i;
            if (i <= 0) {
                return bVarB;
            }
            n0 n0VarX2 = w1.this.X2(this.f289a);
            this.f291c = n0VarX2;
            return n0VarX2.b();
        }
    }

    class t implements t2.e {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        List<l0.f> f293a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        List<c0.e> f294b;

        t() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void h(int i) {
            List<l0.f> list = this.f293a;
            if (list != null) {
                int size = list.size();
                if (i < size) {
                    w1.this.Y3(this.f293a.get(i));
                    return;
                }
                i -= size;
            }
            w1.this.Z3(this.f294b.get(i));
        }

        @Override // com.jozein.xedgepro.xposed.t2.e
        public boolean a() {
            return w1.this.H.o(11);
        }

        @Override // com.jozein.xedgepro.xposed.t2.e
        public boolean b(final int i) {
            w1.this.c0.post(new Runnable() { // from class: com.jozein.xedgepro.xposed.z1
                @Override // java.lang.Runnable
                public final void run() {
                    this.z.h(i);
                }
            });
            return true;
        }

        @Override // com.jozein.xedgepro.xposed.t2.e
        public Drawable c(int i) {
            List<l0.f> list = this.f293a;
            if (list != null) {
                int size = list.size();
                if (i < size) {
                    return this.f293a.get(i).d();
                }
                i -= size;
            }
            g.g gVarD = this.f294b.get(i).d();
            g.a0.v(gVarD, true);
            return gVarD;
        }

        @Override // com.jozein.xedgepro.xposed.t2.e
        public CharSequence d(int i) {
            c0.e eVar;
            List<l0.f> list = this.f293a;
            if (list == null) {
                eVar = this.f294b.get(i);
            } else {
                int size = list.size();
                if (i < size) {
                    eVar = this.f293a.get(i);
                } else {
                    i -= size;
                    eVar = this.f294b.get(i);
                }
            }
            return eVar.f();
        }

        @Override // com.jozein.xedgepro.xposed.t2.e
        public void e(int i, View view) {
            Bundle bundleC = com.jozein.xedgepro.xposed.a.c(view);
            List<l0.f> list = this.f293a;
            if (list != null) {
                int size = list.size();
                if (i < size) {
                    this.f293a.get(i).E(bundleC);
                    return;
                }
                i -= size;
            }
            w1.this.p4(this.f294b.get(i), bundleC);
        }

        @Override // com.jozein.xedgepro.xposed.t2.e
        public void f() {
        }

        @Override // com.jozein.xedgepro.xposed.t2.e
        public int getCount() {
            this.f293a = w1.this.P.u0();
            c0.c cVar = new c0.c();
            cVar.b(w1.this.I.i(), -1);
            for (l0.f fVar : this.f293a) {
                cVar.a(fVar.i(), fVar.k());
            }
            List<c0.e> listN = f.z.K(w1.this.a0).N(cVar, 131072);
            this.f294b = listN;
            return listN.size() + this.f293a.size();
        }
    }

    private class t0 implements t2.e {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final g.o f296a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final a.b[] f297b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        final boolean f298c;

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        w0 f299d = null;

        t0(a.b[] bVarArr, boolean z) {
            this.f296a = new o.b(w1.this.H, w1.this.b0.getResources());
            this.f297b = bVarArr;
            this.f298c = z;
        }

        private Drawable g(a.b bVar) {
            int i = bVar.z;
            if (i == 0) {
                return null;
            }
            boolean z = true;
            if (i == 1) {
                return null;
            }
            if (i != 70) {
                if (i == 72) {
                    b.d dVar = (b.d) bVar;
                    if (!dVar.B()) {
                        return g(dVar.K);
                    }
                }
            } else if (((b.g2) bVar).I == 0) {
                int iD = w1.this.V.d();
                if (iD == 0) {
                    return this.f296a.a(R.drawable.ic_ringer_mode_silent);
                }
                if (iD == 1) {
                    return this.f296a.a(R.drawable.ic_ringer_mode_vibrate);
                }
            }
            g.o oVar = this.f296a;
            Drawable drawableM = bVar.m(w1.this.b0);
            if ((bVar instanceof b.k3) && !w1.this.Q1(bVar)) {
                z = false;
            }
            return oVar.e(drawableM, z);
        }

        private CharSequence h(a.b bVar) {
            WifiManager wifiManager;
            int i = bVar.z;
            if (i == 0 || i == 1) {
                return null;
            }
            if (i != 21) {
                if (i != 70) {
                    if (i == 72) {
                        b.d dVar = (b.d) bVar;
                        if (!dVar.C()) {
                            return h(dVar.K);
                        }
                    }
                } else if (((b.g2) bVar).I == 0) {
                    int iD = w1.this.V.d();
                    if (iD == 0) {
                        return w1.this.b0.getText(R.string.ringer_mode_silent);
                    }
                    if (iD == 1) {
                        return w1.this.b0.getText(R.string.ringer_mode_vibrate);
                    }
                    if (iD == 2) {
                        return w1.this.b0.getText(R.string.ringer_mode_normal);
                    }
                }
            } else if (w1.this.T.H() && (wifiManager = (WifiManager) w1.this.a0.getSystemService("wifi")) != null) {
                String ssid = wifiManager.getConnectionInfo().getSSID();
                String strSubstring = ssid.substring(1, ssid.length() - 1);
                if (!w1.this.T.z()) {
                    try {
                        return Html.fromHtml("<font color=\"#888888\">" + strSubstring + "</font>");
                    } catch (Throwable th) {
                        f.v.d(th);
                    }
                }
                return strSubstring;
            }
            return bVar instanceof b.k3 ? bVar.p(w1.this.b0) : bVar.n(w1.this.b0);
        }

        @Override // com.jozein.xedgepro.xposed.t2.e
        public boolean a() {
            return this.f298c;
        }

        @Override // com.jozein.xedgepro.xposed.t2.e
        public boolean b(int i) {
            a.b bVarY4 = w1.this.y4(this.f297b[i], true);
            if (bVarY4.z != 86) {
                return false;
            }
            w0 w0VarF2 = w1.this.f2((b.i3) bVarY4);
            this.f299d = w0VarF2;
            w0VarF2.a();
            w1.this.y4(this.f297b[i], false);
            return true;
        }

        @Override // com.jozein.xedgepro.xposed.t2.e
        public Drawable c(int i) {
            return g(this.f297b[i]);
        }

        @Override // com.jozein.xedgepro.xposed.t2.e
        public CharSequence d(int i) {
            return h(this.f297b[i]);
        }

        @Override // com.jozein.xedgepro.xposed.t2.e
        public void e(int i, View view) {
            w1.this.v3(this.f297b[i], -1, com.jozein.xedgepro.xposed.a.c(view));
        }

        @Override // com.jozein.xedgepro.xposed.t2.e
        public void f() {
            w0 w0Var = this.f299d;
            if (w0Var != null) {
                w0Var.f();
                this.f299d = null;
            }
        }

        @Override // com.jozein.xedgepro.xposed.t2.e
        public int getCount() {
            return this.f297b.length;
        }
    }

    class u implements t2.e {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final List<l0.j> f301a;

        u() {
            this.f301a = w1.this.P.l0();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void h(int i) {
            try {
                w1.this.Y3(this.f301a.get(i).t());
            } catch (Throwable th) {
                f.v.d(th);
            }
        }

        @Override // com.jozein.xedgepro.xposed.t2.e
        public boolean a() {
            return w1.this.H.o(11);
        }

        @Override // com.jozein.xedgepro.xposed.t2.e
        public boolean b(final int i) {
            w1.this.c0.post(new Runnable() { // from class: com.jozein.xedgepro.xposed.a2
                @Override // java.lang.Runnable
                public final void run() {
                    this.z.h(i);
                }
            });
            return true;
        }

        @Override // com.jozein.xedgepro.xposed.t2.e
        public Drawable c(int i) {
            l0.j jVar = this.f301a.get(i);
            g.g gVarD = jVar.d();
            g.a0.v(gVarD, w1.this.P.I0(jVar.i(), jVar.k()));
            return gVarD;
        }

        @Override // com.jozein.xedgepro.xposed.t2.e
        public CharSequence d(int i) {
            return this.f301a.get(i).f();
        }

        @Override // com.jozein.xedgepro.xposed.t2.e
        public void e(int i, View view) {
            this.f301a.get(i).s(com.jozein.xedgepro.xposed.a.c(view));
        }

        @Override // com.jozein.xedgepro.xposed.t2.e
        public void f() {
        }

        @Override // com.jozein.xedgepro.xposed.t2.e
        public int getCount() {
            return this.f301a.size();
        }
    }

    private static class u0 implements n0 {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private a.b f303a;

        u0(a.b bVar) {
            this.f303a = bVar;
        }

        @Override // com.jozein.xedgepro.xposed.w1.n0
        public boolean a() {
            return this.f303a != null;
        }

        @Override // com.jozein.xedgepro.xposed.w1.n0
        public a.b b() {
            a.b bVar = this.f303a;
            this.f303a = null;
            return bVar;
        }
    }

    class v implements i.f {
        v() {
        }

        @Override // f.i.f
        public int a(String str) {
            return w1.this.L.g(str).f488a;
        }

        @Override // f.i.f
        public f.t b(String str) {
            f.t tVarG = w1.this.L.g(str);
            if (tVarG != null) {
                return new f.t(tVarG.f488a);
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    abstract class v0 implements b0.a {
        private final CharSequence A;
        private final int B;
        private boolean C;
        private float D;
        private float E;
        private float F;
        private int G;
        private int H;
        private View I;
        private final Paint z;

        class a extends View {
            final Rect z;

            a(Context context) {
                super(context);
                this.z = new Rect();
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                canvas.getClipBounds(this.z);
                if (v0.this.H == v0.this.G) {
                    canvas.drawRect(this.z, v0.this.z);
                    return;
                }
                if (v0.this.H != 0) {
                    if (v0.this.C) {
                        canvas.drawRect(0.0f, 0.0f, (v0.this.H * this.z.right) / v0.this.G, this.z.bottom, v0.this.z);
                        return;
                    }
                    Rect rect = this.z;
                    canvas.drawRect(0.0f, ((v0.this.G - v0.this.H) * this.z.bottom) / v0.this.G, rect.right, rect.bottom, v0.this.z);
                }
            }
        }

        private v0(CharSequence charSequence) {
            this.H = -1;
            Paint paint = new Paint(1);
            this.z = paint;
            paint.setColor(w1.this.H.v(22, 2130739111));
            this.A = charSequence;
            this.B = (int) (g.a0.j() * 0.1f);
        }

        /* synthetic */ v0(w1 w1Var, CharSequence charSequence, k kVar) {
            this(charSequence);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void n() {
            t3.n().t(5);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void o() {
            if (this.I != null) {
                t3.n().t(5);
                this.I = null;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void p(int i) {
            View view = this.I;
            if (view != null) {
                view.invalidate();
            }
            r(i);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void q(FrameLayout.LayoutParams layoutParams) {
            a aVar = new a(w1.this.a0);
            this.I = aVar;
            aVar.setKeepScreenOn(true);
            t3.n().k(this.I, 5, layoutParams);
            CharSequence charSequence = this.A;
            if (charSequence != null) {
                w1.this.J4(charSequence);
            }
        }

        @Override // com.jozein.xedgepro.xposed.b0.a
        public final void b(float f2, float f3, long j) {
            d(f2, f3, j);
            c();
        }

        @Override // com.jozein.xedgepro.xposed.b0.a
        public final void c() {
            w1.this.c0.post(new Runnable() { // from class: com.jozein.xedgepro.xposed.e2
                @Override // java.lang.Runnable
                public final void run() {
                    this.z.o();
                }
            });
        }

        /* JADX WARN: Removed duplicated region for block: B:13:0x0024  */
        @Override // com.jozein.xedgepro.xposed.b0.a
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public final void d(float r2, float r3, long r4) {
            /*
                r1 = this;
                boolean r4 = r1.C
                r5 = 1073741824(0x40000000, float:2.0)
                r0 = 0
                if (r4 == 0) goto L1e
                float r3 = r1.D
                int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r4 > 0) goto Le
                goto L36
            Le:
                float r4 = r1.E
                int r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                if (r4 < 0) goto L15
                goto L24
            L15:
                float r2 = r2 - r3
                float r3 = r1.F
                float r4 = r3 / r5
                float r2 = r2 + r4
                float r2 = r2 / r3
                int r0 = (int) r2
                goto L36
            L1e:
                float r4 = r1.D
                int r4 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
                if (r4 > 0) goto L27
            L24:
                int r0 = r1.G
                goto L36
            L27:
                float r4 = r1.E
                int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                if (r2 < 0) goto L2e
                goto L36
            L2e:
                float r4 = r4 - r3
                float r2 = r1.F
                float r3 = r2 / r5
                float r4 = r4 + r3
                float r4 = r4 / r2
                int r0 = (int) r4
            L36:
                int r2 = r1.H
                if (r2 == r0) goto L4a
                r1.H = r0
                com.jozein.xedgepro.xposed.w1 r2 = com.jozein.xedgepro.xposed.w1.this
                android.os.Handler r2 = com.jozein.xedgepro.xposed.w1.j0(r2)
                com.jozein.xedgepro.xposed.f2 r3 = new com.jozein.xedgepro.xposed.f2
                r3.<init>()
                r2.post(r3)
            L4a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.jozein.xedgepro.xposed.w1.v0.d(float, float, long):void");
        }

        @Override // com.jozein.xedgepro.xposed.b0.a
        public final void e(int i, float f2, float f3, long j) {
            final FrameLayout.LayoutParams layoutParams;
            int iQ = g.a0.q();
            if (i == 0) {
                this.C = false;
                layoutParams = new FrameLayout.LayoutParams(iQ, -1, 3);
            } else if (i != 1) {
                this.C = true;
                layoutParams = i != 3 ? new FrameLayout.LayoutParams(-1, iQ, 48) : new FrameLayout.LayoutParams(-1, iQ, 80);
            } else {
                this.C = false;
                layoutParams = new FrameLayout.LayoutParams(iQ, -1, 5);
            }
            Point pointI = w1.this.g2().I(new Point());
            this.D = this.B;
            this.E = (this.C ? pointI.x : pointI.y) - r1;
            int iM = m();
            this.G = iM;
            this.F = (this.E - this.D) / iM;
            w1.this.c0.post(new Runnable() { // from class: com.jozein.xedgepro.xposed.g2
                @Override // java.lang.Runnable
                public final void run() {
                    this.z.q(layoutParams);
                }
            });
            d(f2, f3, j);
        }

        protected void finalize() {
            View view = this.I;
            if (view != null) {
                view.post(new Runnable() { // from class: com.jozein.xedgepro.xposed.h2
                    @Override // java.lang.Runnable
                    public final void run() {
                        w1.v0.n();
                    }
                });
            }
        }

        protected abstract int m();

        protected abstract void r(int i);
    }

    class w extends f0.d {
        w() {
        }

        @Override // f.f0.d
        protected Object a(String str, Object[] objArr) {
            return null;
        }
    }

    static abstract class w0 implements Runnable, a0.a, b0.a {
        private final Handler A;
        private boolean B;
        private final int z;

        private w0(Handler handler) {
            this.B = false;
            this.A = handler;
            this.z = ViewConfiguration.getKeyRepeatDelay();
        }

        /* synthetic */ w0(Handler handler, k kVar) {
            this(handler);
        }

        @Override // com.jozein.xedgepro.xposed.a0.a
        public final void a() {
            this.A.post(this);
        }

        @Override // com.jozein.xedgepro.xposed.b0.a
        public final void b(float f2, float f3, long j) {
            f();
        }

        @Override // com.jozein.xedgepro.xposed.b0.a
        public final void c() {
            f();
        }

        @Override // com.jozein.xedgepro.xposed.b0.a
        public final void d(float f2, float f3, long j) {
        }

        @Override // com.jozein.xedgepro.xposed.b0.a
        public final void e(int i, float f2, float f3, long j) {
            a();
        }

        @Override // com.jozein.xedgepro.xposed.a0.a
        public final void f() {
            this.B = true;
            this.A.removeCallbacks(this);
        }

        protected abstract void g();

        @Override // java.lang.Runnable
        public void run() {
            if (this.B) {
                return;
            }
            try {
                g();
                this.A.postDelayed(this, this.z);
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
    }

    class x extends ResultReceiver {
        final /* synthetic */ b.k3 z;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        x(Handler handler, b.k3 k3Var) {
            super(handler);
            this.z = k3Var;
        }

        @Override // android.os.ResultReceiver
        protected void onReceiveResult(int i, Bundle bundle) {
            if (i == 0) {
                try {
                    w1.this.I4(this.z, true);
                } catch (Throwable unused) {
                }
            } else {
                f.v.c("Failed to turn on Wifi hotspot with error code : " + i);
            }
        }
    }

    class y implements n0.b {
        y() {
        }

        @Override // f.n0.b
        public void a() {
            w1.this.j1(1);
            w1.this.U.l(32, false);
        }

        @Override // f.n0.b
        public void b() {
            w1.this.d4(1, new b.l3(2), R.drawable.ic_torch);
            w1.this.U.l(32, true);
        }
    }

    class z implements l.e {
        z() {
        }

        @Override // g.l.e
        public void a(Intent intent) {
            w1.this.n4(intent);
        }

        @Override // g.l.e
        public boolean b(CharSequence charSequence) {
            ClipboardManager clipboardManager;
            if (charSequence.length() != 0 && (clipboardManager = (ClipboardManager) w1.this.a0.getSystemService("clipboard")) != null) {
                clipboardManager.setText(charSequence);
                w1 w1Var = w1.this;
                w1Var.J4(w1Var.b0.getText(R.string.text_copied));
                w1.this.Z.N(charSequence);
            }
            return true;
        }
    }

    public w1(ClassLoader classLoader, a.p pVar, a.f fVar, a.k kVar, s.e eVar, a.v vVar) {
        super(Build.VERSION.SDK_INT < 23 ? "com.android.internal.policy.impl.PhoneWindowManager" : "com.android.server.policy.PhoneWindowManager", classLoader);
        this.M = new k();
        this.N = new v();
        this.W = null;
        this.X = null;
        this.Y = null;
        this.d0 = null;
        this.e0 = null;
        this.f0 = true;
        this.g0 = 0;
        this.h0 = 0L;
        this.j0 = false;
        this.k0 = false;
        this.l0 = false;
        this.m0 = false;
        this.n0 = false;
        this.o0 = null;
        this.p0 = -1.0f;
        this.q0 = false;
        this.r0 = 0L;
        this.s0 = false;
        this.t0 = null;
        this.u0 = new p();
        this.v0 = false;
        this.w0 = false;
        this.x0 = null;
        this.y0 = -1;
        this.z0 = null;
        this.A0 = null;
        this.B0 = null;
        this.C0 = false;
        this.D0 = null;
        this.E0 = new Runnable() { // from class: com.jozein.xedgepro.xposed.u1
            @Override // java.lang.Runnable
            public final void run() {
                this.z.G1();
            }
        };
        this.F0 = new Runnable() { // from class: com.jozein.xedgepro.xposed.b1
            @Override // java.lang.Runnable
            public final void run() {
                this.z.I1();
            }
        };
        this.G0 = null;
        this.H0 = new q0[7];
        this.I0 = null;
        this.J0 = null;
        this.K0 = null;
        this.L0 = false;
        this.M0 = new p0(this, null);
        this.N0 = null;
        this.O0 = 0;
        this.P0 = null;
        this.Q0 = new c0();
        this.R0 = 0;
        this.H = pVar;
        this.J = fVar;
        this.I = kVar;
        this.K = eVar;
        this.L = vVar;
        this.O = new y0(this, pVar, classLoader);
        this.Q = new com.jozein.xedgepro.xposed.n0(m(), classLoader, this, pVar);
        boolean zContains = m().getSimpleName().contains("Miui");
        this.i0 = zContains;
        com.jozein.xedgepro.xposed.v vVar2 = new com.jozein.xedgepro.xposed.v(this);
        this.S = vVar2;
        com.jozein.xedgepro.xposed.l0 l0Var = new com.jozein.xedgepro.xposed.l0(classLoader, pVar, this, vVar2, zContains);
        this.P = l0Var;
        this.Z = new com.jozein.xedgepro.xposed.r(classLoader, this, l0Var, pVar);
        try {
            o("init", new d0());
        } catch (Throwable th) {
            p2.g(th);
        }
        try {
            p("systemBooted", new e0());
        } catch (Throwable th2) {
            p2.g(th2);
        }
        int i2 = Build.VERSION.SDK_INT;
        if (i2 < 31) {
            try {
                f0 f0Var = new f0();
                if (i2 >= 29) {
                    o("onDefaultDisplayFocusChangedLw", f0Var);
                } else if (i2 < 24 || i2 > 27) {
                    o("focusChangedLw", f0Var);
                } else {
                    try {
                        XposedHelpers.findAndHookMethod("com.android.server.policy.OemPhoneWindowManager", classLoader, "focusChangedLw", new Object[]{"android.view.WindowManagerPolicy$WindowState", "android.view.WindowManagerPolicy$WindowState", f0Var});
                    } catch (Throwable unused) {
                        o("focusChangedLw", f0Var);
                    }
                }
            } catch (Throwable th3) {
                p2.g(th3);
            }
        }
        if (!t3.D) {
            try {
                g0 g0Var = new g0();
                if (Build.VERSION.SDK_INT <= 27) {
                    o("takeScreenshot", g0Var);
                } else {
                    XposedHelpers.findAndHookMethod(m().getName() + "$ScreenshotRunnable", classLoader, "run", new Object[]{g0Var});
                }
            } catch (Throwable th4) {
                p2.g(th4);
            }
        }
        this.R = new com.jozein.xedgepro.xposed.p0(classLoader, this);
        try {
            int i3 = Build.VERSION.SDK_INT;
            if (i3 < 29) {
                o("layoutWindowLw", new i0());
            } else if (i3 == 29) {
                XposedHelpers.findAndHookMethod("com.android.server.wm.DisplayContent", classLoader, "adjustForImeIfNeeded", new Object[]{new h0()});
            }
        } catch (Throwable th5) {
            p2.g(th5);
        }
        try {
            j0 j0Var = new j0();
            p(Build.VERSION.SDK_INT < 21 ? "showGlobalActionsDialog" : "showGlobalActions", j0Var);
            p("sendCloseSystemWindows", String.class, j0Var);
        } catch (Throwable th6) {
            p2.g(th6);
        }
        try {
            p("setCurrentUserLw", Integer.TYPE, new a());
        } catch (Throwable th7) {
            p2.g(th7);
        }
        v3.f(classLoader);
    }

    private void A1(int i2) {
        int i3;
        int i4;
        y0 y0Var;
        int i5 = 4096;
        if (i2 == 0) {
            i3 = android.R.id.cut;
            i4 = 52;
        } else if (i2 == 1) {
            i3 = android.R.id.copy;
            i4 = 31;
        } else if (i2 == 2) {
            i3 = android.R.id.paste;
            i4 = 50;
        } else {
            if (i2 != 3) {
                if (i2 == 4) {
                    y0Var = this.O;
                } else {
                    if (i2 != 5) {
                        return;
                    }
                    y0Var = this.O;
                    i5 = 4097;
                }
                y0Var.Z(54, i5);
                return;
            }
            i3 = android.R.id.selectAll;
            i4 = 29;
        }
        if (this.R.N(i3)) {
            return;
        }
        this.O.Z(i4, 4096);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void A2(b.i3 i3Var) {
        G4(i3Var.n(this.b0));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void A3() {
        if (this.l0) {
            return;
        }
        this.l0 = true;
        W3(false);
        try {
            if (!this.H.J()) {
                H3();
            }
            this.Z.k0(this.a0, this.c0);
            t3.v(this.H.o(35));
            t3.w(new c());
            this.Q.n0();
            this.O.v0(this.a0);
            this.b0 = p2.d(this.a0);
            if (this.H.p(2)) {
                d5(new b.m2(1));
            }
            u3(this.H.k(0));
        } catch (Throwable th) {
            f.v.d(th);
        }
        if (this.n0) {
            this.o0 = null;
        }
        this.P.n1();
        f.v.c("Perform boot completed.");
    }

    private void B1(b.s0 s0Var) {
        String str;
        switch (s0Var.I) {
            case 1:
                w1(s0Var.J, s0Var.K);
                return;
            case 2:
                E1(s0Var.J, s0Var.K);
                return;
            case 3:
            case 6:
            case 7:
            case 12:
            case 13:
            case 16:
            case 24:
            default:
                str = "Unknown option id: " + s0Var.I;
                break;
            case 4:
                this.v0 = true;
                return;
            case 5:
                this.v0 = false;
                return;
            case 8:
                a4(s0Var.K);
                return;
            case 9:
                Q3();
                return;
            case 10:
                U2();
                return;
            case 11:
                this.H.c0(this.a0, 0);
                n5();
                return;
            case 14:
                G4(s0Var.J + ": " + this.L.g(s0Var.J).f488a);
                return;
            case 15:
                this.Z.q0();
                return;
            case 17:
                P3();
                return;
            case 18:
                this.V.i(s0Var.K);
                return;
            case 19:
                t3.v(s0Var.z());
                return;
            case 20:
                m5(s0Var.J);
                return;
            case 21:
                this.U.g();
                return;
            case 22:
                e2().C(s0Var.J);
                return;
            case 23:
                e2().G(s0Var.J);
                return;
            case 25:
                G3();
                return;
            case 26:
                N3();
                return;
            case 27:
                str = s0Var.J;
                break;
            case 28:
                this.Q.x0();
                return;
            case 29:
                this.P.t1(s0Var.J, s0Var.K);
                return;
        }
        f.v.c(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void B2(b.y0 y0Var) {
        k2(y0Var, true);
    }

    private void B3(b.y1 y1Var) throws IOException {
        String str = y1Var.I;
        if (str == null) {
            return;
        }
        if (str.length() == 0) {
            this.M0.b();
        } else {
            this.M0.a(Uri.parse(y1Var.I), y1Var.J, y1Var.K);
        }
    }

    private void B4() {
        C4(1);
    }

    private void C1(int i2) {
        int i3;
        if (i2 == 0) {
            i3 = 126;
        } else if (i2 == 1) {
            i3 = 85;
        } else if (i2 == 2) {
            i3 = 86;
        } else if (i2 == 3) {
            i3 = 88;
        } else if (i2 != 4) {
            return;
        } else {
            i3 = 87;
        }
        O3(i3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void C2() {
        this.O.A0(false);
        j1(3);
    }

    private void C3(int i2) {
        if (!this.f0 || t2()) {
            return;
        }
        y1();
        this.P.g1(i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void C4(int i2) {
        if (this.f0) {
            if (i2 == 2) {
                if (Build.VERSION.SDK_INT >= 24) {
                    try {
                        D4(2);
                    } catch (Throwable unused) {
                        this.O.Z(47, 69633);
                    }
                    this.O.V().R();
                    return;
                }
                return;
            }
            try {
                if (Build.VERSION.SDK_INT < 24) {
                    r("takeScreenshot", new Object[0]);
                } else {
                    D4(1);
                }
            } catch (Throwable unused2) {
                m2(120);
            }
        }
    }

    private void D1(b.i3 i3Var) {
        int i2 = i3Var.I;
        if (i2 == 0) {
            e1(i3Var.J);
            return;
        }
        if (i2 == 1) {
            d1(i3Var.J);
            return;
        }
        if (i2 == 13) {
            float fV = this.H.v(17, 100) / 100.0f;
            if (!i3Var.J) {
                fV = -fV;
            }
            M3(fV);
            return;
        }
        if (i2 == 14) {
            V2(i3Var.J);
            return;
        }
        int i3 = i2 - 3;
        int i4 = 4;
        if (this.f0 && i3 <= 4) {
            i4 = 5;
        }
        this.V.a(i3, i3Var.J ? 1 : -1, i4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void D2() {
        d3(false);
    }

    private void D3() {
        CharSequence text = ((ClipboardManager) this.a0.getSystemService("clipboard")).getText();
        if (text == null) {
            return;
        }
        d2().o(text.toString(), false, true);
    }

    private void D4(int i2) {
        int i3 = Build.VERSION.SDK_INT;
        if (i3 >= 33) {
            r("handleScreenShot", Integer.valueOf(i2), 2);
        } else {
            if (i3 <= 27) {
                r("takeScreenshot", Integer.valueOf(i2));
                return;
            }
            Runnable runnable = (Runnable) u("mScreenshotRunnable");
            XposedHelpers.callMethod(runnable, "setScreenshotType", new Object[]{Integer.valueOf(i2)});
            runnable.run();
        }
    }

    private void E1(String str, int i2) {
        CharSequence charSequenceG = f.z.K(this.a0).G(str);
        if (this.P.l1(str, i2, false)) {
            J4(this.b0.getString(R.string.app_thawed_f, charSequenceG));
        } else {
            L4(this.b0.getString(R.string.error_thaw_app_f, charSequenceG));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void E2(boolean z2) {
        t3.q();
        this.Z.R();
        x4();
        T4(new b.r0(2));
        u3(this.H.k(19));
        u3(this.H.k(z2 ? 21 : 20));
    }

    private void E3() {
        this.c0.postDelayed(new Runnable() { // from class: com.jozein.xedgepro.xposed.q1
            @Override // java.lang.Runnable
            public final void run() {
                this.z.H2();
            }
        }, m3());
    }

    private void F1() {
        y1();
        if (this.f0) {
            if (t2() && u2()) {
                return;
            }
            if (this.Q.l0() || !this.Q.q0()) {
                G1();
            } else {
                this.c0.postDelayed(this.E0, 200L);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void F2() {
        y1();
        W4(new b.d1(2));
        T4(new b.r0(2));
        f.m0 m0Var = this.N0;
        if (m0Var != null && !m0Var.m()) {
            m0Var.n();
            this.N0 = null;
        }
        this.Z.m0();
    }

    private void F3() {
        this.P.i1();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void F4(a.b bVar, Throwable th) {
        String message;
        if (bVar.z != 65535) {
            Context context = this.b0;
            message = context.getString(R.string.perform_failed) + ((Object) bVar.n(context)) + ", " + th;
        } else {
            if (th instanceof InvocationTargetException) {
                th = ((InvocationTargetException) th).getTargetException();
            }
            message = th.getMessage();
            if (message == null) {
                message = th.toString();
            }
        }
        L4(message);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void G1() {
        if (e2().D() || P1().performGlobalAction(4)) {
            return;
        }
        this.O.Z(42, 65536);
    }

    private void G3() {
        f.v.c("Start reload settings.");
        this.H.g();
        com.jozein.xedgepro.xposed.w.c();
        H3();
    }

    private void H1() {
        y1();
        if (this.f0) {
            if (t2() && u2()) {
                return;
            }
            if (this.Q.l0() || !this.Q.q0()) {
                I1();
            } else {
                this.c0.postDelayed(this.F0, 200L);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void H2() {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                this.Q.d0().V();
                return;
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
        this.d0.reboot(null);
    }

    private void H4(int i2) {
        if (this.k0 && this.f0 && this.H.o(24)) {
            G4(this.b0.getText(i2));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void I1() {
        if (e2().E()) {
            return;
        }
        P1().performGlobalAction(5);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void I2() {
        String[] strArrA = n2.a(this.a0);
        Intent intent = new Intent(b.s0.M);
        intent.putExtra("list", strArrA);
        intent.setPackage(f.l.j);
        try {
            this.P.W(intent, 0);
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void I4(b.k3 k3Var, boolean z2) {
    }

    private void J1(int i2) {
        boolean z2 = true;
        if (i2 != 0) {
            if (i2 != 1) {
                return;
            } else {
                z2 = false;
            }
        }
        M3(z2 ? 100000.0f : -100000.0f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void J2(Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable th) {
            f.v.d(th);
        }
        runnable.run();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void J4(CharSequence charSequence) {
        if (this.k0 && this.f0 && this.H.o(24)) {
            G4(charSequence);
        }
    }

    private void K1() {
        if (this.f0 || !this.H.o(9)) {
            if (this.H.o(4)) {
                this.V.i(this.H.v(23, 0));
            }
            U2();
        }
    }

    private void K3(b.a0 a0Var) {
        long j2;
        if (!f.l.r) {
            throw new RuntimeException("!");
        }
        if (a0Var.z() == null) {
            return;
        }
        boolean zO = this.H.o(25);
        boolean zO2 = this.H.o(36);
        if (a0Var.I) {
            f.i0.c(a0Var, this.b0, this.c0, zO, zO2);
            j2 = 4000;
        } else {
            BinderService.k(this.a0, a0Var, zO, zO2);
            j2 = 5000;
        }
        b1(j2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void K4(int i2) {
        if (this.k0 && this.f0) {
            L4(this.b0.getText(i2));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Handler L1() {
        Handler h0Var;
        Handler handler;
        Throwable th;
        f.h0 h0Var2;
        Looper looperMyLooper = Looper.myLooper();
        if (looperMyLooper == null || !Thread.currentThread().getName().equals("android.ui")) {
            try {
                h0Var = (Handler) u("mHandler");
            } catch (Throwable th2) {
                f.v.d(th2);
                h0Var = null;
            }
            if (h0Var == null) {
                try {
                    handler = (Handler) XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.android.server.UiThread", m().getClassLoader()), "getHandler", new Object[0]);
                } catch (Throwable th3) {
                    handler = h0Var;
                    th = th3;
                }
                if (handler != null) {
                    try {
                        h0Var = new f.h0(handler.getLooper());
                    } catch (Throwable th4) {
                        th = th4;
                        f.v.d(th);
                        h0Var = handler;
                    }
                } else {
                    h0Var = handler;
                }
            }
            h0Var2 = (h0Var != null || looperMyLooper == null) ? new f.h0(Looper.getMainLooper()) : new f.h0(looperMyLooper);
        } else {
            h0Var2 = new f.h0(looperMyLooper);
        }
        f.h0.g(h0Var2);
        return h0Var2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void L2() {
        this.Q.d0().X();
    }

    private void L3() {
        t3();
        t2 t2VarY1 = Y1();
        if (t2VarY1.l(2)) {
            t2VarY1.j();
        } else {
            t2VarY1.o(new u(), 2);
        }
    }

    private void L4(CharSequence charSequence) {
        if (this.k0 && this.f0) {
            Toast toast = this.B0;
            if (toast != null) {
                toast.cancel();
                this.B0 = null;
            }
            Toast toastMakeText = Toast.makeText(this.a0, charSequence, 1);
            if (f.o0.d() != 0) {
                S3(toastMakeText);
            }
            toastMakeText.show();
        }
    }

    static /* synthetic */ int M0(w1 w1Var) {
        int i2 = w1Var.O0 + 1;
        w1Var.O0 = i2;
        return i2;
    }

    private void M1() {
        if (t2()) {
            return;
        }
        y1();
        this.P.e0();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void M2(Intent intent) {
        try {
            this.P.q1(intent, false, null, f.o0.d());
        } catch (Throwable th) {
            f.v.d(th);
            L4(th.getMessage());
        }
    }

    private void M3(float f2) {
        Point pointZ = this.Q.Z();
        this.O.g0(f2, pointZ.x, pointZ.y);
    }

    private void M4(a.b bVar) {
        if (this.k0 && this.f0 && this.H.o(25)) {
            G4(bVar.n(this.b0));
        }
    }

    private String N1(String str) {
        if (str == null) {
            return null;
        }
        return str.replace("%b", Integer.toString(this.T.q()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void N2(float f2, float f3, boolean z2, String str) {
        if (z2) {
            m5(str);
            return;
        }
        try {
            this.O.g0(l2.E, f2, f3);
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    private void N3() {
        Intent intent = new Intent(b.s0.O);
        intent.putExtra("logs", f.v.b());
        intent.setPackage(f.l.j);
        this.P.W(intent, 0);
    }

    private void N4(b.k3 k3Var) {
        ContentResolver contentResolver = this.a0.getContentResolver();
        int iA = h3.a.a(contentResolver, "airplane_mode_on", 0);
        int iS2 = S2(k3Var.I, iA);
        if (iA != iS2) {
            h3.a.b(contentResolver, "airplane_mode_on", iS2);
            Intent intent = new Intent("android.intent.action.AIRPLANE_MODE");
            intent.putExtra("state", iS2 != 0);
            this.P.Y(intent);
            I4(k3Var, iS2 != 0);
        }
    }

    private void O1() {
        t3();
        t2 t2VarY1 = Y1();
        if (t2VarY1.l(1)) {
            t2VarY1.j();
        } else {
            t2VarY1.o(new t(), 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void O2(final float f2, final float f3) {
        P1().c(f2, f3, new v3.d() { // from class: com.jozein.xedgepro.xposed.l1
            @Override // com.jozein.xedgepro.xposed.v3.d
            public final void a(boolean z2, String str) {
                this.f151a.N2(f2, f3, z2, str);
            }
        });
    }

    private void O3(int i2) {
        this.V.b(i2);
    }

    private void O4(b.k3 k3Var) {
        int i2 = k3Var.I;
        ContentResolver contentResolver = this.a0.getContentResolver();
        int iA = h3.c.a(contentResolver, "screen_brightness_mode", 0);
        int iS2 = S2(i2, iA);
        if (iA != iS2) {
            h3.c.b(contentResolver, "screen_brightness_mode", iS2);
            I4(k3Var, iS2 != 0);
        }
    }

    private v3 P1() {
        if (this.W == null) {
            this.W = new v3(this);
        }
        return this.W;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void P2() {
        t3 t3VarS = t3.s();
        if (t3VarS != null) {
            t3VarS.u(8, 200L);
        }
    }

    private void P3() {
        Intent intentPutExtra = new Intent(b.s0.N).setPackage(f.l.j).putExtra("alias", f.q0.c()).putExtra("time", f.l.s).putExtra("build_time", f.l.t);
        if (W2()) {
            intentPutExtra.putExtra("send_files", true);
        }
        this.P.W(intentPutExtra, 0);
    }

    private void P4(b.k3 k3Var) {
        int i2 = k3Var.I;
        ContentResolver contentResolver = this.a0.getContentResolver();
        int iA = h3.c.a(contentResolver, "accelerometer_rotation", 0);
        int iS2 = S2(i2, iA);
        if (iA != iS2) {
            h3.c.b(contentResolver, "accelerometer_rotation", iS2);
            h3.a(contentResolver, Settings.System.getUriFor("accelerometer_rotation"), null);
            I4(k3Var, iS2 != 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean Q1(a.b bVar) {
        int i2 = bVar.z;
        if (i2 == 73) {
            return this.Q.i0();
        }
        if (i2 == 97) {
            return this.L0;
        }
        switch (i2) {
            case 18:
                return this.V.d() == 2;
            case 19:
                return this.T.s();
            case 20:
                return this.T.t();
            case 21:
                return this.T.I();
            case 22:
                return this.T.y();
            case 23:
                return this.T.x();
            case 24:
                return this.T.u();
            case 25:
                return this.T.A();
            case 26:
                return this.T.E();
            case 27:
                return this.T.G();
            case 28:
                return this.T.r();
            case 29:
                return this.Q.h0();
            case 30:
                return this.I0 != null;
            default:
                switch (i2) {
                    case 32:
                        return x2();
                    case 33:
                        return this.K0 != null;
                    case 34:
                        return this.H.p(1);
                    case 35:
                        return this.H.p(0);
                    default:
                        return true;
                }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void Q2(int i2) {
        this.Z.P(i2);
    }

    private void Q3() {
        f.d.b(new Runnable() { // from class: com.jozein.xedgepro.xposed.t1
            @Override // java.lang.Runnable
            public final void run() {
                this.z.I2();
            }
        });
    }

    private void Q4(b.k3 k3Var) {
        int i2 = k3Var.I;
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        switch (defaultAdapter.getState()) {
            case 10:
            case 13:
                if (i2 == 0 || i2 == 1) {
                    defaultAdapter.enable();
                    I4(k3Var, true);
                }
                break;
            case 11:
            case 12:
                if (i2 == 0 || i2 == 2) {
                    defaultAdapter.disable();
                    I4(k3Var, false);
                }
                break;
        }
    }

    private void R2(int i2) {
        if (!this.f0 || t2()) {
            return;
        }
        y1();
        this.P.P0(i2);
    }

    private void R3(int i2) {
        if (!this.H.o(34)) {
            com.jozein.xedgepro.xposed.f.e(this.a0, false).i(this.a0, false);
        }
        com.jozein.xedgepro.xposed.f.j(this.a0, i2);
    }

    private void R4(b.k3 k3Var) {
        boolean z2 = this.L0;
        boolean zT2 = T2(k3Var.I, z2);
        if (z2 != zT2) {
            this.L0 = zT2;
            if (zT2) {
                d4(6, new b.o0(2), R.drawable.ic_game_mode);
            } else {
                j1(6);
            }
            this.U.l(k3Var.z, zT2);
            I4(k3Var, zT2);
        }
    }

    private static int S2(int i2, int i3) {
        return (i2 != 1 && (i2 == 2 || i3 != 0)) ? 0 : 1;
    }

    private static void S3(Toast toast) {
        try {
            t3.i((WindowManager.LayoutParams) XposedHelpers.getObjectField(XposedHelpers.getObjectField(toast, "mTN"), "mParams"), 16);
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void S4(b.k3 k3Var) {
        boolean z2 = true;
        boolean zP = this.H.p(1);
        boolean zT2 = T2(k3Var.I, zP);
        if (zP != zT2) {
            this.H.X(this.a0, 1, zT2);
            boolean zP2 = this.H.p(0);
            boolean z3 = zP || zP2;
            if (!zT2 && !zP2) {
                z2 = false;
            }
            if (z3 != z2) {
                this.O.C0();
            }
            if (zT2) {
                d4(4, new b.p0(2), R.drawable.ic_gesture_enabled);
            } else {
                j1(4);
            }
            this.U.l(k3Var.z, zT2);
            I4(k3Var, zT2);
        }
    }

    static /* synthetic */ int T0(w1 w1Var) {
        int i2 = w1Var.R0 + 1;
        w1Var.R0 = i2;
        return i2;
    }

    private static boolean T2(int i2, boolean z2) {
        if (i2 != 1) {
            return (i2 == 2 || z2) ? false : true;
        }
        return true;
    }

    private void T4(b.k3 k3Var) {
        boolean zK0;
        boolean zT2;
        if (this.f0 && zK0 != (zT2 = T2(k3Var.I, (zK0 = this.O.k0())))) {
            y0 y0Var = this.O;
            if (zT2) {
                y0Var.A0(true);
                d4(3, new b.r0(2), R.drawable.ic_gesture_record);
            } else {
                y0Var.A0(false);
                j1(3);
            }
            this.U.l(k3Var.z, zT2);
            I4(k3Var, zT2);
        }
    }

    private void U2() {
        try {
            if (this.A0 == null) {
                this.A0 = new r3(v(), m(), this.a0, this.H);
            }
            this.A0.s();
        } catch (Throwable unused) {
        }
    }

    private void U3(b.p2 p2Var) {
        int iA;
        synchronized (this.M) {
            iA = p2Var.A(this.M);
        }
        J4(((Object) p2Var.n(this.b0)) + ", " + iA);
    }

    private void U4(b.k3 k3Var) {
        boolean zH0 = this.Q.h0();
        boolean zT2 = T2(k3Var.I, zH0);
        if (zT2 != zH0) {
            this.Q.t0(zT2);
            this.U.l(k3Var.z, zT2);
            I4(k3Var, zT2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void V2(boolean z2) {
        int i2 = z2 ? 21 : 22;
        if (this.R.K(i2)) {
            return;
        }
        this.O.Y(i2);
    }

    private void V3(b.k3 k3Var, boolean z2) {
        int i2 = Build.VERSION.SDK_INT;
        if (i2 <= 29 || !z2) {
            if (i2 >= 26) {
                ConnectivityManager connectivityManager = (ConnectivityManager) this.a0.getSystemService("connectivity");
                x xVar = z2 ? new x(this.c0, k3Var) : null;
                try {
                    if (z2) {
                        Object objectField = XposedHelpers.getObjectField(connectivityManager, "mService");
                        if (i2 >= 27) {
                            XposedHelpers.callMethod(objectField, "startTethering", new Object[]{0, xVar, Boolean.FALSE, "android"});
                            return;
                        } else {
                            XposedHelpers.callMethod(objectField, "startTethering", new Object[]{0, xVar, Boolean.FALSE});
                            return;
                        }
                    }
                    XposedHelpers.callMethod(connectivityManager, "stopTethering", new Object[]{0});
                } catch (Throwable unused) {
                    Object objectField2 = XposedHelpers.getObjectField(XposedHelpers.getObjectField(connectivityManager, "mService"), "mTethering");
                    if (z2) {
                        XposedHelpers.callMethod(objectField2, "startTethering", new Object[]{0, xVar, Boolean.FALSE});
                        return;
                    }
                    XposedHelpers.callMethod(objectField2, "stopTethering", new Object[]{0});
                }
            } else {
                XposedHelpers.callMethod(this.a0.getSystemService("wifi"), "setWifiApEnabled", new Object[]{null, Boolean.valueOf(z2)});
            }
        } else {
            Object systemService = this.a0.getSystemService("tethering");
            n1 n1Var = new Executor() { // from class: com.jozein.xedgepro.xposed.n1
                @Override // java.util.concurrent.Executor
                public final void execute(Runnable runnable) {
                    w1.J2(runnable);
                }
            };
            ClassLoader classLoaderC = p2.c();
            XposedHelpers.callMethod(systemService, "startTethering", new Object[]{0, n1Var, Proxy.newProxyInstance(classLoaderC, new Class[]{XposedHelpers.findClass("android.net.TetheringManager$StartTetheringCallback", classLoaderC)}, new w())});
        }
        I4(k3Var, z2);
    }

    private void V4(b.k3 k3Var) {
        boolean zI0 = this.Q.i0();
        boolean zT2 = T2(k3Var.I, zI0);
        if (zT2 != zI0) {
            this.Q.v0(zT2);
            this.U.l(k3Var.z, zT2);
            I4(k3Var, zT2);
        }
    }

    private boolean W2() {
        return !this.H.J() || (!com.jozein.xedgepro.xposed.w.g() && a.p.q(this.a0, 8));
    }

    private void W3(boolean z2) {
        if (z2 || Build.VERSION.SDK_INT >= 31) {
            this.Q.d0().W();
        }
    }

    private void W4(b.k3 k3Var) {
        boolean z2 = this.I0 != null;
        boolean zT2 = T2(k3Var.I, z2);
        if (z2 == zT2) {
            return;
        }
        if (zT2) {
            PowerManager.WakeLock wakeLockNewWakeLock = this.d0.newWakeLock(10, f.l.x + ":awake");
            this.I0 = wakeLockNewWakeLock;
            wakeLockNewWakeLock.setReferenceCounted(false);
            this.I0.acquire();
            d4(0, new b.d1(2), R.drawable.ic_keep_screen_on);
        } else {
            this.I0.release();
            this.I0 = null;
            j1(0);
        }
        this.U.l(k3Var.z, zT2);
        I4(k3Var, zT2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public n0 X2(a.b bVar) {
        int i2 = bVar.z;
        return (i2 == 0 || i2 == 1) ? this.Q0 : i2 != 62 ? i2 != 63 ? i2 != 72 ? i2 != 84 ? i2 != 101 ? new u0(bVar) : new s0((b.f2) bVar) : new o0(((b.k2) bVar).z()) : X2(((b.d) bVar).K) : new o0(((b.q1) bVar).I) : new l0((b.d0) bVar);
    }

    private void X3() {
        if (this.f0) {
            t3();
            t2 t2VarY1 = Y1();
            if (t2VarY1.l(0)) {
                t2VarY1.j();
                return;
            }
            int i2 = 0;
            int i3 = 0;
            for (int i4 = 0; i4 < 4; i4++) {
                for (int i5 = 0; i5 < 4; i5++) {
                    int i6 = this.H.m(i4, i5).z;
                    if (i6 != 0 && i6 != 1) {
                        if (i4 > i3) {
                            i3 = i4;
                        }
                        if (i5 > i2) {
                            i2 = i5;
                        }
                    }
                }
            }
            int i7 = i2 + 1;
            int i8 = i3 + 1;
            a.b[] bVarArr = new a.b[i8 * i7];
            int i9 = 0;
            for (int i10 = 0; i10 < i8; i10++) {
                int i11 = 0;
                while (i11 < i7) {
                    bVarArr[i9] = this.H.m(i10, i11);
                    i11++;
                    i9++;
                }
            }
            t2VarY1.n(new t0(bVarArr, this.H.o(10)), 1, i7, 0);
        }
    }

    private void X4(b.k3 k3Var) {
        boolean zP = this.H.p(0);
        boolean zT2 = T2(k3Var.I, zP);
        if (zP != zT2) {
            this.H.X(this.a0, 0, zT2);
            boolean zP2 = this.H.p(1);
            if ((zP || zP2) != (zT2 || zP2)) {
                this.O.C0();
            }
            if (zT2) {
                d4(5, new b.e1(2), R.drawable.ic_key_enabled);
            } else {
                j1(5);
            }
            this.U.l(k3Var.z, zT2);
            I4(k3Var, zT2);
        }
    }

    private void Y2(int i2) {
        if (!this.f0 || t2()) {
            return;
        }
        y1();
        this.P.Y0(i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void Y3(l0.f fVar) {
        y1();
        a0 a0Var = new a0(this.a0);
        o2 o2Var = new o2(this.a0, 19);
        b0 b0Var = new b0(this.a0, o2Var);
        o2Var.a(b0Var);
        b0Var.setBackgroundColor(this.H.v(33, -16764873));
        b0Var.setSelector(g.a0.l());
        k0 k0Var = new k0(this, fVar);
        b0Var.setAdapter((ListAdapter) k0Var);
        b0Var.setOnItemClickListener(k0Var);
        b0Var.setDividerHeight(0);
        a0Var.addView(b0Var, new FrameLayout.LayoutParams(-1, -2, 80));
        float count = k0Var.getCount();
        if (count > 4.0f) {
            count = 4.2f;
        }
        int i2 = (int) (k0Var.A * count);
        t3.n().k(a0Var, 19, new FrameLayout.LayoutParams(-1, i2, 80));
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(new TranslateAnimation(0.0f, 0.0f, i2 / 2.0f, 0.0f));
        animationSet.addAnimation(new AlphaAnimation(0.0f, 1.0f));
        g.a0.z(b0Var, animationSet, (long) (((count * 66.0f) * 2.0f) / 3.0f));
    }

    private void Y4(b.k3 k3Var) {
        ContentResolver contentResolver = this.a0.getContentResolver();
        int iA = h3.b.a(contentResolver, "location_mode", 0);
        int iS2 = S2(k3Var.I, iA);
        if (iA != iS2) {
            if (iS2 != 0) {
                iS2 = 3;
            }
            h3.b.b(contentResolver, "location_mode", iS2);
            this.P.Y(new Intent("android.location.MODE_CHANGED"));
            I4(k3Var, iS2 != 0);
        }
    }

    private void Z2(b.u1 u1Var) {
        String strI = f.m0.i(N1(u1Var.J), this.a0);
        new q0().c(strI, strI, R.drawable.ic_launcher, u1Var.I, true, false, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void Z3(c0.e eVar) {
        try {
            Y3(new l0.f(this.P, eVar.i(), eVar.k()));
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    private void Z4(b.k3 k3Var) {
        boolean zT2;
        int i2 = k3Var.I;
        if (Build.VERSION.SDK_INT < 21) {
            ConnectivityManager connectivityManager = (ConnectivityManager) this.a0.getSystemService("connectivity");
            boolean zBooleanValue = ((Boolean) XposedHelpers.callMethod(connectivityManager, "getMobileDataEnabled", new Object[0])).booleanValue();
            zT2 = T2(i2, zBooleanValue);
            if (zBooleanValue == zT2) {
                return;
            } else {
                XposedHelpers.callMethod(XposedHelpers.getObjectField(connectivityManager, "mService"), "setMobileDataEnabled", new Object[]{Boolean.valueOf(zT2)});
            }
        } else {
            TelephonyManager telephonyManager = (TelephonyManager) this.a0.getSystemService("phone");
            boolean zBooleanValue2 = ((Boolean) XposedHelpers.callMethod(telephonyManager, "getDataEnabled", new Object[0])).booleanValue();
            zT2 = T2(i2, zBooleanValue2);
            if (zBooleanValue2 == zT2) {
                return;
            } else {
                XposedHelpers.callMethod(telephonyManager, "setDataEnabled", new Object[]{Boolean.valueOf(zT2)});
            }
        }
        I4(k3Var, zT2);
    }

    private void a5(b.k3 k3Var) {
        int i2 = k3Var.I;
        NfcAdapter defaultAdapter = NfcAdapter.getDefaultAdapter(this.a0);
        if (defaultAdapter == null) {
            return;
        }
        int iIntValue = ((Integer) XposedHelpers.callMethod(defaultAdapter, "getAdapterState", new Object[0])).intValue();
        if (iIntValue != 1) {
            if (iIntValue == 2 || iIntValue == 3) {
                if (i2 == 0 || i2 == 2) {
                    XposedHelpers.callMethod(defaultAdapter, "disable", new Object[0]);
                    I4(k3Var, false);
                    return;
                }
                return;
            }
            if (iIntValue != 4) {
                return;
            }
        }
        if (i2 == 0 || i2 == 1) {
            XposedHelpers.callMethod(defaultAdapter, "enable", new Object[0]);
            I4(k3Var, true);
        }
    }

    private void b4(b.m0 m0Var) {
        if (f.o0.d() == 0) {
            BinderService.p(this.a0, ServiceWidget.f(m0Var.I, this.H.u(27)));
        }
    }

    private void b5() {
        if (this.f0) {
            t3 t3VarN = t3.n();
            if (t3VarN.p(273)) {
                t3VarN.t(273);
            } else {
                t3VarN.j(new r(this.a0, this.b0.getResources(), this.H.y(), this.H.v(2, -16764873), this.H.o(37)), 273);
            }
        }
    }

    private void c1(b.i iVar) {
        int i2 = iVar.I;
        if (i2 == 0) {
            int i3 = iVar.J;
            if (i3 == -2) {
                e1(false);
                return;
            }
            if (i3 == -1) {
                e1(true);
                return;
            }
            this.H.c0(this.a0, i3 >= 1 ? i3 : 1);
            View view = this.K0;
            if (view != null) {
                view.setBackgroundColor(this.H.B());
                return;
            }
            return;
        }
        if (i2 == 1) {
            int i4 = iVar.J;
            if (i4 == -2) {
                d1(false);
                return;
            } else if (i4 != -1) {
                R3(i4);
                return;
            } else {
                d1(true);
                return;
            }
        }
        int i5 = i2 - 3;
        int i6 = 4;
        if (this.f0 && i5 <= 4) {
            i6 = 5;
        }
        int i7 = iVar.J;
        if (i7 == -2) {
            this.V.a(i5, -1, i6);
        } else if (i7 != -1) {
            this.V.k(i5, i7, i6);
        } else {
            this.V.a(i5, 1, i6);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float c2() {
        Display display;
        if (this.p0 < 0.0f) {
            int i2 = Build.VERSION.SDK_INT;
            if (i2 < 28) {
                this.p0 = 0.0f;
            } else {
                if (i2 >= 29) {
                    try {
                        float fS1 = s1(((Float) XposedHelpers.callMethod(g2().G(), "getWindowCornerRadius", new Object[0])).floatValue());
                        this.p0 = fS1;
                        if (fS1 > 0.0f) {
                            return fS1;
                        }
                    } catch (Throwable th) {
                        f.v.c(th.toString());
                    }
                }
                if (Build.VERSION.SDK_INT >= 31 && (display = this.a0.getDisplay()) != null) {
                    RoundedCorner roundedCorner = display.getRoundedCorner(0);
                    int radius = roundedCorner == null ? 0 : roundedCorner.getRadius();
                    float fS12 = s1(Math.min(radius, display.getRoundedCorner(2) != null ? r0.getRadius() : 0));
                    this.p0 = fS12;
                    if (fS12 > 0.0f) {
                        return fS12;
                    }
                }
                Resources resources = this.a0.getResources();
                int identifier = resources.getIdentifier("rounded_corner_radius", "dimen", "android");
                float dimension = identifier > 0 ? resources.getDimension(identifier) : 0.0f;
                int identifier2 = resources.getIdentifier("rounded_corner_radius_top", "dimen", "android");
                float dimension2 = identifier2 > 0 ? resources.getDimension(identifier2) : 0.0f;
                if (dimension2 <= 0.0f) {
                    dimension2 = dimension;
                }
                int identifier3 = resources.getIdentifier("rounded_corner_radius_bottom", "dimen", "android");
                float dimension3 = identifier3 > 0 ? resources.getDimension(identifier3) : 0.0f;
                if (dimension3 > 0.0f) {
                    dimension = dimension3;
                }
                this.p0 = s1(Math.min(dimension2, dimension));
            }
        }
        return this.p0;
    }

    private void c4() {
        if (t2()) {
            return;
        }
        Y3(this.P.q0());
    }

    private void c5() {
        if (t2()) {
            return;
        }
        y1();
        try {
            if (Build.VERSION.SDK_INT >= 25) {
                XposedHelpers.callMethod(XposedHelpers.getObjectField(XposedHelpers.getSurroundingThis(r("getStatusBarManagerInternal", new Object[0])), "mBar"), "toggleRecentApps", new Object[0]);
            } else {
                r("toggleRecentApps", new Object[0]);
            }
        } catch (Throwable th) {
            if (P1().performGlobalAction(3)) {
                throw th;
            }
            f.v.d(th);
            m2(187);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void d1(boolean z2) {
        com.jozein.xedgepro.xposed.f fVarE = com.jozein.xedgepro.xposed.f.e(this.a0, false);
        if (!this.H.o(34)) {
            fVarE.i(this.a0, false);
        }
        fVarE.b(this.a0, z2);
    }

    private f.m0 d2() {
        if (this.N0 == null) {
            this.N0 = new f.m0(this.a0);
        }
        return this.N0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void d3(boolean z2) {
        if (this.n0) {
            return;
        }
        this.n0 = true;
        this.o0 = null;
        if (!z2) {
            f.v.c("Cannot start service!");
        }
        BinderService.B(z2);
        if (W2()) {
            BinderService.z(this.a0);
            this.c0.postDelayed(new Runnable() { // from class: com.jozein.xedgepro.xposed.o1
                @Override // java.lang.Runnable
                public final void run() {
                    this.z.A3();
                }
            }, 6000L);
        } else if (this.H.J()) {
            A3();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void d4(int i2, b.k3 k3Var, int i3) {
        if (i2 <= this.H0.length && (this.H.u(7) & (1 << i2)) != 0) {
            this.H0[i2] = new q0();
            this.H0[i2].b(k3Var.n(this.b0), i3, k3Var);
        }
    }

    private void d5(b.k3 k3Var) {
        if (this.K0 != null) {
            if (k3Var.I != 1) {
                t3.n().u(257, 1000L);
                this.K0 = null;
                this.H.X(this.a0, 2, false);
                j1(2);
                this.U.l(k3Var.z, false);
                I4(k3Var, false);
                return;
            }
            return;
        }
        if (k3Var.I != 2) {
            this.K0 = new View(this.a0);
            this.K0.setBackgroundColor(this.H.B());
            if (!t3.n().j(this.K0, 257)) {
                this.K0 = null;
                return;
            }
            AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
            alphaAnimation.setDuration(1000L);
            this.K0.startAnimation(alphaAnimation);
            this.H.X(this.a0, 2, true);
            d4(2, new b.m2(2), R.drawable.ic_screen_filter);
            this.U.l(k3Var.z, true);
            I4(k3Var, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void e1(boolean z2) {
        int iC = this.H.C();
        int i2 = z2 ? iC + 1 : iC - 1;
        this.H.c0(this.a0, i2 >= 1 ? i2 : 1);
        View view = this.K0;
        if (view != null) {
            view.setBackgroundColor(this.H.B());
        }
    }

    private k3 e2() {
        Object objS;
        if (this.D0 == null) {
            try {
                objS = r("getStatusBarService", new Object[0]);
            } catch (Throwable th) {
                f.v.d(th);
                objS = g3.s();
            }
            this.D0 = new k3(objS, this);
        }
        return this.D0;
    }

    private void e4() {
        y1();
        if (this.f0) {
            try {
                r(Build.VERSION.SDK_INT < 21 ? "showGlobalActionsDialog" : "showGlobalActions", new Object[0]);
            } catch (Throwable th) {
                if (Build.VERSION.SDK_INT < 21) {
                    throw th;
                }
                f.v.d(th);
                P1().performGlobalAction(6);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void e5(int i2) {
        if (i2 == 3) {
            if (this.w0) {
                try {
                    this.R.P();
                    return;
                } catch (Throwable unused) {
                    ((InputMethodManager) this.a0.getSystemService("input_method")).showInputMethodPicker();
                    return;
                }
            }
            return;
        }
        if (i2 == 4) {
            this.R.R();
            return;
        }
        try {
            if (this.w0) {
                if (i2 == 1) {
                } else {
                    this.R.J();
                }
            } else if (i2 == 2) {
            } else {
                this.R.Q();
            }
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    private void f4(int i2) {
        if (this.f0) {
            t3();
            a.b[] bVarArr = this.H.n(i2).I;
            if (bVarArr == null) {
                return;
            }
            int i3 = i2 == 0 ? 3 : 4;
            t2 t2VarY1 = Y1();
            if (t2VarY1.l(i3)) {
                t2VarY1.j();
            } else {
                t2VarY1.n(new t0(bVarArr, this.H.o(26)), i2 != 0 ? 4 : 3, 1, i3);
            }
        }
    }

    private void f5() {
        if (t2()) {
            return;
        }
        y1();
        int i2 = Build.VERSION.SDK_INT;
        int i3 = 0;
        if (i2 < 33) {
            if (i2 >= 24) {
                try {
                    XposedHelpers.callMethod(r("getStatusBarManagerInternal", new Object[0]), "toggleSplitScreen", new Object[0]);
                    return;
                } catch (Throwable th) {
                    f.v.c(th.toString());
                    P1().performGlobalAction(7);
                    return;
                }
            }
            return;
        }
        List<ActivityManager.RunningTaskInfo> listW0 = this.P.w0();
        int size = listW0.size();
        if (size < 2) {
            return;
        }
        ActivityManager.RunningTaskInfo runningTaskInfo = listW0.get(0);
        if (q2.s(runningTaskInfo.baseIntent)) {
            return;
        }
        ActivityManager.RunningTaskInfo runningTaskInfo2 = null;
        int iMin = Math.min(size, 4);
        while (true) {
            if (i3 >= iMin) {
                break;
            }
            ActivityManager.RunningTaskInfo runningTaskInfo3 = listW0.get(i3);
            if (f3.f(runningTaskInfo3)) {
                runningTaskInfo2 = runningTaskInfo3;
                break;
            }
            i3++;
        }
        if (runningTaskInfo2 != null) {
            for (int i4 = i3 + 1; i4 < size; i4++) {
                ActivityManager.RunningTaskInfo runningTaskInfo4 = listW0.get(i4);
                if (f3.f(runningTaskInfo4)) {
                    this.P.S0(runningTaskInfo4.taskId);
                    return;
                }
            }
            return;
        }
        if (size >= 3 || !q2.s(listW0.get(1).baseIntent)) {
            int iC = f3.c(runningTaskInfo);
            Intent intentAddFlags = new Intent(runningTaskInfo.baseIntent).addFlags(4096);
            Bundle bundle = ActivityOptions.makeTaskLaunchBehind().toBundle();
            com.jozein.xedgepro.xposed.a.i(bundle, 6);
            this.P.q1(intentAddFlags, true, bundle, iC);
        }
    }

    private void g4(String str) {
        if (str == null) {
            return;
        }
        L4(f.m0.i(N1(str), this.a0));
    }

    private void g5(b.k3 k3Var) {
        boolean masterSyncAutomatically = ContentResolver.getMasterSyncAutomatically();
        boolean zT2 = T2(k3Var.I, masterSyncAutomatically);
        if (masterSyncAutomatically != zT2) {
            ContentResolver.setMasterSyncAutomatically(zT2);
            I4(k3Var, zT2);
        }
    }

    private void h1(int i2) {
        t3();
        t2 t2VarY1 = Y1();
        int i3 = i2 + 32;
        if (t2VarY1.l(i3)) {
            t2VarY1.j();
        } else {
            t2VarY1.o(new s(i2), i3);
        }
    }

    private void h2() {
        Intent intent;
        y1();
        if (t2()) {
            return;
        }
        try {
            h("awakenDreams", new Object[0]);
            r("sendCloseSystemWindows", new Object[0]);
        } catch (Throwable unused) {
        }
        try {
            intent = (Intent) r("createHomeDockIntent", new Object[0]);
        } catch (Throwable unused2) {
            intent = null;
        }
        if (intent == null) {
            intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
            intent.addFlags(270532608);
        }
        try {
            this.P.q1(intent, false, null, f.o0.d());
        } catch (Throwable th) {
            f.v.d(th);
            m2(3);
        }
    }

    private void h4() {
        this.c0.postDelayed(new Runnable() { // from class: com.jozein.xedgepro.xposed.s1
            @Override // java.lang.Runnable
            public final void run() {
                this.z.L2();
            }
        }, m3());
    }

    private void h5(b.l3 l3Var) {
        if (Build.VERSION.SDK_INT >= 23) {
            j5(l3Var);
        } else {
            i5(l3Var);
        }
    }

    private void i1() {
        if (t3.o()) {
            return;
        }
        m2(4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void i2() {
        if (this.j0) {
            return;
        }
        if (this.a0 == null) {
            Context context = (Context) u("mContext");
            this.a0 = context;
            if (context == null) {
                Context context2 = (Context) this.P.u("mContext");
                this.a0 = context2;
                if (context2 == null) {
                    return;
                }
            }
        }
        this.j0 = true;
        Point pointK = g.a0.k(this.a0, new Point());
        this.g0 = (pointK.x << 16) | pointK.y;
        f.v.c("Screen size: " + pointK.x + ", " + pointK.y);
        this.P.C0(this.a0, this.c0);
        this.T = new i3(this.H, this, this.a0, this.c0);
        this.U = new a.t(this.a0);
        this.S.e();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(a.b.C);
        intentFilter.addAction(a.j.E);
        intentFilter.addAction(a.b.D);
        this.a0.registerReceiver(new b(), intentFilter, null, this.c0);
        W3(Build.VERSION.SDK_INT >= 28 && this.i0);
    }

    private void i4(b.y2 y2Var) {
        boolean z2 = this.f0;
        int i2 = y2Var.I;
        if (!z2) {
            if (i2 == 1) {
                return;
            }
            XposedHelpers.callMethod(this.d0, "wakeUp", new Object[]{Long.valueOf(SystemClock.uptimeMillis())});
        } else {
            if (i2 == 2) {
                return;
            }
            try {
                XposedHelpers.callMethod(this.d0, "goToSleep", new Object[]{Long.valueOf(SystemClock.uptimeMillis())});
            } catch (Throwable th) {
                if (Build.VERSION.SDK_INT < 28) {
                    throw th;
                }
                f.v.d(th);
                P1().performGlobalAction(8);
            }
        }
    }

    private void i5(b.l3 l3Var) {
        if (f.o0.d() != 0) {
            return;
        }
        boolean zX2 = x2();
        if (zX2 == T2(l3Var.I, zX2)) {
            return;
        }
        BinderService.p(this.a0, ServiceTorch.f(l3Var, this.f0 && this.H.o(24), (this.H.u(7) & 2) != 0));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void j1(int i2) {
        q0[] q0VarArr = this.H0;
        if (q0VarArr[i2] != null) {
            q0VarArr[i2].a();
            this.H0[i2] = null;
        }
    }

    private void j2() {
        if (this.J0 == null) {
            this.J0 = f.n0.b(this.c0, new com.jozein.xedgepro.xposed.u(this.a0, this.P), new y());
        }
    }

    private void j4() throws IllegalAccessException, InvocationTargetException {
        try {
            XposedHelpers.callMethod(XposedHelpers.getObjectField(this.d0, "mService"), "crash", new Object[]{"Soft reboot"});
        } catch (Throwable th) {
            f.v.c(th.toString());
            o3.b("ctl.restart", "zygote");
        }
    }

    private void j5(b.l3 l3Var) {
        f.n0 n0Var = this.J0;
        if (T2(l3Var.I, n0Var != null && n0Var.a())) {
            j2();
            this.J0.g();
        } else {
            f.n0 n0Var2 = this.J0;
            if (n0Var2 != null) {
                n0Var2.e();
            }
        }
    }

    private boolean k1(h.g gVar) {
        int iQ = this.T.q();
        int i2 = gVar.C;
        if (i2 == 0) {
            return iQ == gVar.D;
        }
        if (i2 == 1) {
            return iQ > gVar.D;
        }
        if (i2 == 2) {
            return iQ >= gVar.D;
        }
        if (i2 == 3) {
            return iQ < gVar.D;
        }
        if (i2 == 4) {
            return iQ <= gVar.D;
        }
        if (i2 == 5) {
            return iQ != gVar.D;
        }
        throw new RuntimeException("Invalid check battery level.");
    }

    private void k2(final b.y0 y0Var, boolean z2) {
        if (this.f0) {
            if (this.Q.j0() != y0Var.F()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Gesture injection ");
                sb.append(y0Var.F() ? "landscape" : "portrait");
                throw new UnsupportedOperationException(sb.toString());
            }
            List<g.q> listD = y0Var.D();
            if (listD != null && listD.size() >= 2) {
                l2(listD);
            } else if (z2) {
                f.v.c("Invalid gesture!");
            } else {
                this.c0.postDelayed(new Runnable() { // from class: com.jozein.xedgepro.xposed.f1
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.z.B2(y0Var);
                    }
                }, 500L);
            }
        }
    }

    private void k4(b.c3 c3Var) {
        String str;
        if (c3Var != null && (str = c3Var.I) != null && str.length() != 0) {
            d2().o(N1(c3Var.I), true, true);
            return;
        }
        f.m0 m0Var = this.N0;
        if (m0Var != null) {
            m0Var.n();
            this.N0 = null;
        }
    }

    private void k5(b.k3 k3Var) {
        int i2 = k3Var.I;
        WifiManager wifiManager = (WifiManager) this.a0.getSystemService("wifi");
        int wifiState = wifiManager.getWifiState();
        if (wifiState == 0 || wifiState == 1) {
            if (i2 == 0 || i2 == 1) {
                wifiManager.setWifiEnabled(true);
                I4(k3Var, true);
                return;
            }
            return;
        }
        if (wifiState == 2 || wifiState == 3) {
            if (i2 == 0 || i2 == 2) {
                wifiManager.setWifiEnabled(false);
                I4(k3Var, false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean l1(a.h hVar, boolean z2) {
        if (!f.l.r) {
            throw new RuntimeException("!");
        }
        switch (hVar.z) {
            case 1:
                h.c cVar = (h.c) hVar;
                if (l1(cVar.C, z2)) {
                    return l1(cVar.D, z2);
                }
                return false;
            case 2:
                h.z zVar = (h.z) hVar;
                return l1(zVar.C, z2) || l1(zVar.D, z2);
            case 3:
                return !l1(((h.x) hVar).C, z2);
            case 4:
                return this.f0;
            case 5:
                return t2();
            case 6:
                return this.T.s();
            case 7:
                return this.T.t();
            case 8:
                return this.T.I();
            case 9:
                return this.T.y();
            case 10:
                return this.T.x();
            case 11:
                return this.T.u();
            case 12:
                return this.T.A();
            case 13:
                return this.T.E();
            case 14:
                return this.T.G();
            case 15:
                return this.T.r();
            case 16:
                return this.H.p(1);
            case 17:
                return this.H.p(0);
            case 18:
                return this.T.C();
            case 19:
                return this.T.H();
            case 20:
                return this.T.z();
            case 21:
                return this.T.v();
            case 22:
                return this.T.F();
            case 23:
                return this.V.g();
            case 24:
                return this.T.D();
            case 25:
                return this.T.B();
            case 26:
                return k1((h.g) hVar);
            case 27:
                return p1((h.n) hVar);
            case 28:
                return m1((h.l) hVar);
            case 29:
                return n1((h.k) hVar);
            case 30:
                return o1((h.m) hVar);
            case 31:
                return r1(((h.m0) hVar).C);
            case 32:
                return p2(((h.d) hVar).C);
            case 33:
                return this.Q.j0() == ((h.a0) hVar).C;
            case 34:
                return this.w0;
            case 35:
                return this.Q.l0();
            case 36:
                return this.Q.k0();
            case 37:
                return q1((h.i0) hVar, z2);
            case 38:
                return r1(((h.l0) hVar).C);
            default:
                throw new RuntimeException("Unknown condition id: " + hVar.z);
        }
    }

    private void l2(List<g.q> list) {
        Handler handlerW = this.O.W();
        this.C0 = false;
        handlerW.post(new q(list, handlerW));
    }

    private void l4(Intent intent, Bundle bundle, int i2) {
        this.P.o1(intent, bundle, i2, true);
    }

    private void l5(b.k3 k3Var) {
        int i2 = k3Var.I;
        switch (((Integer) XposedHelpers.callMethod((WifiManager) this.a0.getSystemService("wifi"), "getWifiApState", new Object[0])).intValue()) {
            case 10:
            case 11:
                if (i2 == 0 || i2 == 1) {
                    V3(k3Var, true);
                }
                break;
            case 12:
            case 13:
                if (i2 == 0 || i2 == 2) {
                    V3(k3Var, false);
                }
                break;
        }
    }

    private boolean m1(h.l lVar) {
        return (lVar.C & (1 << new f.j().d())) != 0;
    }

    private void m2(int i2) {
        this.O.Z(i2, 0);
    }

    private void m4(String str, String str2, Bundle bundle, int i2) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(str, str2));
        l4(intent, bundle, i2);
    }

    private void m5(String str) {
        if (str == null || str.length() == 0) {
            H4(R.string.nothing_fetched);
        } else {
            y1();
            this.Z.S(str, null, new z(), null);
        }
    }

    private boolean n1(h.k kVar) {
        f.j jVar = new f.j();
        int iC = jVar.c();
        int i2 = kVar.C;
        if (((1 << iC) & i2) == 0) {
            return (i2 & Integer.MIN_VALUE) != 0 && iC == jVar.a() - 1;
        }
        return true;
    }

    private void n2(b.z0 z0Var) {
        int i2 = z0Var.J;
        if (i2 == 0) {
            this.O.Z(z0Var.I, z0Var.K);
            return;
        }
        if (i2 == 1) {
            this.O.a0(z0Var.I, z0Var.K);
        } else if (i2 != 2) {
            f.v.c("Unknown key action.");
        } else {
            this.O.c0(z0Var.I, z0Var.K);
        }
    }

    private void n5() {
        View view = this.K0;
        if (view != null) {
            view.setBackgroundColor(this.H.B());
        }
    }

    private boolean o1(h.m mVar) {
        return (mVar.C & (1 << new f.j().i())) != 0;
    }

    private void o4(b.l lVar, Bundle bundle) {
        if (lVar.L != null) {
            try {
                l4(f.c0.a().setClassName(lVar.K, lVar.L), bundle, lVar.B());
                return;
            } catch (Throwable unused) {
            }
        }
        q4(lVar.K, bundle, lVar.B());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void o5(Rect rect) {
        final int i2;
        if (rect == null || rect.bottom == 0) {
            rect = null;
            i2 = -1;
        } else {
            i2 = rect.top;
        }
        this.x0 = rect;
        if (i2 == this.y0) {
            return;
        }
        this.y0 = i2;
        if (Build.VERSION.SDK_INT >= 30 || !this.Z.V()) {
            return;
        }
        this.c0.post(new Runnable() { // from class: com.jozein.xedgepro.xposed.e1
            @Override // java.lang.Runnable
            public final void run() {
                this.z.Q2(i2);
            }
        });
    }

    private boolean p1(h.n nVar) {
        int i2 = (nVar.C << 16) | nVar.D;
        int i3 = nVar.F | (nVar.E << 16);
        f.j jVar = new f.j();
        int iH = jVar.h() | (jVar.g() << 16);
        if (i2 == i3) {
            return false;
        }
        return i2 < i3 ? iH >= i2 && iH < i3 : iH >= i2 || iH < i3;
    }

    private boolean p2(String str) {
        return str.equals(this.Q.d0().L());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void p4(c0.e eVar, Bundle bundle) {
        l4(eVar.v(), bundle, eVar.k());
    }

    private boolean q1(h.i0 i0Var, boolean z2) {
        boolean zH;
        if (z2) {
            return i0Var.h(this.N);
        }
        synchronized (this.M) {
            zH = i0Var.h(this.M);
        }
        return zH;
    }

    private void q4(String str, Bundle bundle, int i2) {
        this.P.s1(str, bundle, i2);
    }

    private void q5(int i2) {
        if (this.A0 == null) {
            this.A0 = new r3(v(), m(), this.a0, this.H);
        }
        this.A0.t(i2);
    }

    private boolean r1(String str) {
        if (this.T.H()) {
            return str.equals(n2.f(((WifiManager) this.a0.getSystemService("wifi")).getConnectionInfo().getSSID()));
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void r4() {
        if (this.n0) {
            return;
        }
        f.g0 g0Var = this.o0;
        if (g0Var != null) {
            g0Var.d();
            return;
        }
        d dVar = new d(this.c0, 3, 3000L);
        this.o0 = dVar;
        dVar.run();
    }

    private float s1(float f2) {
        if (f2 > 0.0f) {
            float fJ = f2 / g.a0.j();
            if (fJ < 0.39370078f) {
                return fJ;
            }
            Resources resources = this.a0.getResources();
            int identifier = resources.getIdentifier("config_supportsRoundedCornersOnWindows", "bool", "android");
            f.v.c("Ignored rounded corner radius: " + fJ + ", supports rounded corners: " + (identifier > 0 && resources.getBoolean(identifier)));
        }
        return 0.0f;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean s2(long j2) {
        return j2 <= this.h0;
    }

    private void s4(Intent intent, Bundle bundle) {
        this.P.o1(intent, bundle, 0, false);
    }

    private void t1() {
        J4(this.b0.getString(R.string.memory_released_size_f, Integer.valueOf(this.P.a0())));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void t3() {
        t3 t3VarS = t3.s();
        if (t3VarS != null) {
            t3VarS.t(273);
        }
    }

    private boolean u2() {
        try {
            return Build.VERSION.SDK_INT < 24 ? ((Boolean) r("isKeyguardSecure", new Object[0])).booleanValue() : ((Boolean) r("isKeyguardSecure", Integer.valueOf(f.o0.d()))).booleanValue();
        } catch (Throwable th) {
            f.v.d(th);
            return true;
        }
    }

    private void u4() {
        if (this.q0) {
            x4();
            return;
        }
        if (!(t2() && this.H.o(33)) && SystemClock.uptimeMillis() - this.r0 >= 1000) {
            this.Z.R();
            this.q0 = true;
            this.O.V().Q(true);
            Paint paint = new Paint(1);
            paint.setStrokeWidth(g.a0.j() * 0.08f);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(2144128204);
            Paint paint2 = new Paint(1);
            paint2.setStrokeWidth(g.a0.j() * 0.06f);
            paint2.setStyle(Paint.Style.STROKE);
            paint2.setColor(-16738393);
            t3.n().j(new e(this.a0, paint, paint2), 8);
        }
    }

    private void v1(b.f0 f0Var) {
        int i2;
        if (this.f0) {
            t3();
            t2 t2VarY1 = Y1();
            if (t2VarY1.l(f0Var.I + 64)) {
                t2VarY1.j();
                return;
            }
            a.b[] bVarArrZ = f0Var.z();
            if (bVarArrZ == null) {
                f.v.c("Custom panel not exists!");
                return;
            }
            int length = bVarArrZ.length;
            int i3 = length - 1;
            while (true) {
                if (i3 <= 0) {
                    i2 = 1;
                    break;
                } else {
                    if (bVarArrZ[i3].z != 0 && bVarArrZ[i3].z != 1) {
                        i2 = i3 + 1;
                        break;
                    }
                    i3--;
                }
            }
            if (i2 != length) {
                a.b[] bVarArr = new a.b[i2];
                System.arraycopy(bVarArrZ, 0, bVarArr, 0, i2);
                bVarArrZ = bVarArr;
            }
            t2VarY1.n(new t0(bVarArrZ, this.H.o(10)), 1, length <= 9 ? length > 4 ? 3 : 2 : 4, f0Var.I + 64);
        }
    }

    private void v4() {
        this.h0 = SystemClock.uptimeMillis();
        this.M0.b();
        k4(new b.c3(""));
        y1();
        x4();
        T4(new b.r0(2));
        h5(new b.l3(2));
        if (f.l.r) {
            f.i0.g();
            BinderService.D(this.a0);
        }
        Intent intent = new Intent(com.jozein.xedgepro.service.a.C);
        intent.setPackage(f.l.j);
        this.P.W(intent, 0);
    }

    private void w1(String str, int i2) {
        CharSequence charSequenceG = f.z.K(this.a0).G(str);
        if (this.P.l1(str, i2, true)) {
            J4(this.b0.getString(R.string.app_frozen_f, charSequenceG));
        } else {
            L4(this.b0.getString(R.string.error_freeze_app_f, charSequenceG));
        }
    }

    private void x1() {
        if (this.f0 && t2() && !u2()) {
            int i2 = Build.VERSION.SDK_INT;
            if (i2 <= 25) {
                r("dismissKeyguardLw", new Object[0]);
            } else if (i2 <= 27) {
                r("dismissKeyguardLw", null);
            } else {
                r("dismissKeyguardLw", null, null);
            }
        }
    }

    private boolean x2() {
        if (Build.VERSION.SDK_INT >= 23) {
            f.n0 n0Var = this.J0;
            if (n0Var != null && n0Var.a()) {
                return true;
            }
        } else if (f.o0.d() == 0 && this.P.F0(ServiceTorch.class.getName())) {
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: x3, reason: merged with bridge method [inline-methods] */
    public void G2(a.b bVar, int i2, Bundle bundle) {
        String string;
        if (g1(bVar)) {
            try {
                if (y3(bVar, i2, bundle)) {
                    K1();
                }
                if (!this.f0 || bVar.z == 65535) {
                    return;
                }
                p5();
            } catch (PackageManager.NameNotFoundException e2) {
                String message = e2.getMessage();
                if (message != null) {
                    string = message + " not found!";
                } else {
                    string = e2.toString();
                }
                f.v.c(string);
                G4(string);
            } catch (Throwable th) {
                f.v.d(th);
                F4(bVar, th);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void y1() {
        try {
            t3.q();
            x4();
            t3();
            this.Z.R();
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    private void y2(String str) {
        this.P.i0(str, f.o0.d());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:87:0x0194  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean y3(a.b r5, int r6, android.os.Bundle r7) throws java.lang.IllegalAccessException, java.io.IOException, java.lang.reflect.InvocationTargetException {
        /*
            Method dump skipped, instruction units count: 966
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jozein.xedgepro.xposed.w1.y3(a.b, int, android.os.Bundle):boolean");
    }

    private void z1(a.b bVar) {
        if (!f.l.r) {
            throw new RuntimeException("!");
        }
        new r0(bVar, SystemClock.uptimeMillis()).c();
    }

    private void z2() {
        if (t2()) {
            return;
        }
        y1();
        this.P.g0();
    }

    private void z3(int i2) throws IllegalAccessException, InvocationTargetException {
        switch (i2) {
            case 44:
                E3();
                break;
            case 45:
                j4();
                break;
            case 46:
                h4();
                break;
            default:
                f.v.c("Unknown id: " + i2);
                break;
        }
    }

    private void z4(int i2) {
        int iD = this.V.d();
        if (i2 != 1) {
            if (i2 == 2) {
                i = 1;
            } else {
                if (i2 != 3) {
                    int i3 = iD + 1;
                    i = i3 <= 2 ? i3 : 0;
                    this.V.j(i);
                    if (i == 1 && this.V.d() == iD) {
                        f.v.c("Can not set ringer mode to vibrate. Skip it.");
                        this.V.j(2);
                        return;
                    }
                    return;
                }
                i = 2;
            }
        }
        if (iD != i) {
            this.V.j(i);
        }
    }

    void A4(int i2) {
        f.o0.o(i2);
        this.P.m1(i2);
    }

    void E4(int i2) {
        G4(this.b0.getText(i2));
    }

    void G4(CharSequence charSequence) {
        Toast toastMakeText;
        if (Build.VERSION.SDK_INT >= 24) {
            Toast toast = this.B0;
            if (toast != null) {
                toast.cancel();
            }
            this.B0 = Toast.makeText(this.a0, charSequence, 0);
            if (f.o0.d() != 0) {
                toastMakeText = this.B0;
                S3(toastMakeText);
            }
        } else {
            Toast toast2 = this.B0;
            if (toast2 == null) {
                toastMakeText = Toast.makeText(this.a0, charSequence, 0);
                this.B0 = toastMakeText;
                S3(toastMakeText);
            } else {
                toast2.setText(charSequence);
            }
        }
        this.B0.show();
    }

    void H3() {
        String str;
        try {
            if (this.H.J()) {
                v4();
            }
        } catch (Throwable th) {
            f.v.d(th);
        }
        if (this.H.L()) {
            this.I.l();
            this.J.m();
            this.K.e();
            this.L.h();
            b.f0.A().o();
            b.k2.A().o();
            b.y0.L();
            if (f.l.r) {
                this.K.n(false);
            }
            this.Q.x0();
            str = "Settings reloaded.";
        } else {
            str = "Failed to reload settings!";
        }
        f.v.c(str);
    }

    void I3() {
        this.c0.removeCallbacks(this.u0);
    }

    void J3(int i2) {
        if (this.f0) {
            this.Q.r0(i2);
        }
    }

    com.jozein.xedgepro.xposed.l0 R1() {
        return this.P;
    }

    v0 S1(b.z2 z2Var) {
        CharSequence charSequenceN = this.H.o(24) ? z2Var.n(this.b0) : null;
        int i2 = z2Var.I;
        if (i2 == 0) {
            return new f(charSequenceN);
        }
        if (i2 == 1) {
            return new g(charSequenceN);
        }
        if (i2 != 13) {
            return new i(charSequenceN, i2 == 2 ? this.V.c() : i2 - 3);
        }
        return new h(charSequenceN);
    }

    Context T1() {
        return this.a0;
    }

    void T3() {
        this.C0 = true;
    }

    Handler U1() {
        return this.c0;
    }

    com.jozein.xedgepro.xposed.n0 V1() {
        return this.Q;
    }

    y0 W1() {
        return this.O;
    }

    int X1() {
        if (!this.w0) {
            return -1;
        }
        if (Build.VERSION.SDK_INT < 29) {
            return this.y0;
        }
        try {
            Rect rectE = this.Q.d0().E();
            if (rectE != null) {
                return rectE.top;
            }
        } catch (Throwable th) {
            f.v.d(th);
        }
        return -1;
    }

    t2 Y1() {
        if (this.G0 == null) {
            this.G0 = new t2(this);
        }
        return this.G0;
    }

    y2 Z1() {
        if (this.X == null) {
            this.X = new y2(this.H, this, this.a0, this.c0);
        }
        return this.X;
    }

    d3 a2(int i2) {
        if (this.Y == null) {
            this.Y = new d3(this);
        }
        this.Y.y(i2);
        return this.Y;
    }

    void a3() {
        this.c0.post(new Runnable() { // from class: com.jozein.xedgepro.xposed.p1
            @Override // java.lang.Runnable
            public final void run() {
                this.z.C2();
            }
        });
    }

    void a4(int i2) {
        FrameLayout.LayoutParams layoutParams;
        m0 m0Var;
        Point pointI = g2().I(new Point());
        Point pointY = this.O.V().y();
        int i3 = pointY.x;
        int i4 = pointY.y;
        int i5 = pointI.y - (i4 * 2);
        int i6 = pointI.x - (i3 * 2);
        FrameLayout frameLayout = new FrameLayout(this.a0);
        int i7 = 4;
        int i8 = 2;
        boolean z2 = false;
        byte b2 = 0;
        byte b3 = 0;
        byte b4 = 0;
        byte b5 = 0;
        byte b6 = 0;
        byte b7 = 0;
        byte b8 = 0;
        byte b9 = 0;
        byte b10 = 0;
        byte b11 = 0;
        byte b12 = 0;
        byte b13 = 0;
        byte b14 = 0;
        boolean z3 = true;
        boolean z4 = true;
        boolean z5 = true;
        boolean z6 = true;
        boolean z7 = true;
        boolean z8 = true;
        boolean z9 = true;
        boolean z10 = true;
        boolean z11 = true;
        boolean z12 = true;
        boolean z13 = true;
        boolean z14 = true;
        boolean z15 = true;
        boolean z16 = true;
        k kVar = null;
        switch (i2) {
            case 0:
                layoutParams = new FrameLayout.LayoutParams(i3, i5, 19);
                m0Var = new m0(this, z4 ? 1 : 0, b2 == true ? 1 : 0, kVar);
                break;
            case 1:
                layoutParams = new FrameLayout.LayoutParams(i3, i5, 21);
                m0Var = new m0(this, z5 ? 1 : 0, b3 == true ? 1 : 0, kVar);
                break;
            case 2:
                layoutParams = new FrameLayout.LayoutParams(i6, i4, 49);
                m0Var = new m0(this, b5 == true ? 1 : 0, b4 == true ? 1 : 0, kVar);
                break;
            case 3:
                layoutParams = new FrameLayout.LayoutParams(i6, i4, 81);
                m0Var = new m0(this, b7 == true ? 1 : 0, b6 == true ? 1 : 0, kVar);
                break;
            case 4:
                layoutParams = new FrameLayout.LayoutParams(i3, i5, 19);
                m0Var = new m0(this, z7 ? 1 : 0, z6 ? 1 : 0, kVar);
                break;
            case 5:
                layoutParams = new FrameLayout.LayoutParams(i3, i5, 19);
                m0Var = new m0(this, z8 ? 1 : 0, i8, kVar);
                break;
            case 6:
                layoutParams = new FrameLayout.LayoutParams(i3, i5, 19);
                m0Var = new m0(this, z9 ? 1 : 0, i7, kVar);
                break;
            case 7:
                layoutParams = new FrameLayout.LayoutParams(i3, i5, 21);
                m0Var = new m0(this, z11 ? 1 : 0, z10 ? 1 : 0, kVar);
                break;
            case 8:
                layoutParams = new FrameLayout.LayoutParams(i3, i5, 21);
                m0Var = new m0(this, z12 ? 1 : 0, i8, kVar);
                break;
            case 9:
                layoutParams = new FrameLayout.LayoutParams(i3, i5, 21);
                m0Var = new m0(this, z13 ? 1 : 0, i7, kVar);
                break;
            case 10:
                layoutParams = new FrameLayout.LayoutParams(i6, i4, 49);
                m0Var = new m0(this, b8 == true ? 1 : 0, z14 ? 1 : 0, kVar);
                break;
            case 11:
                layoutParams = new FrameLayout.LayoutParams(i6, i4, 49);
                m0Var = new m0(this, b9 == true ? 1 : 0, i8, kVar);
                break;
            case 12:
                layoutParams = new FrameLayout.LayoutParams(i6, i4, 49);
                m0Var = new m0(this, b10 == true ? 1 : 0, i7, kVar);
                break;
            case 13:
                layoutParams = new FrameLayout.LayoutParams(i6, i4, 81);
                m0Var = new m0(this, b11 == true ? 1 : 0, z15 ? 1 : 0, kVar);
                break;
            case 14:
                layoutParams = new FrameLayout.LayoutParams(i6, i4, 81);
                m0Var = new m0(this, b12 == true ? 1 : 0, i8, kVar);
                break;
            case 15:
                layoutParams = new FrameLayout.LayoutParams(i6, i4, 81);
                m0Var = new m0(this, b13 == true ? 1 : 0, i7, kVar);
                break;
            default:
                int i9 = 7;
                frameLayout.addView(new m0(this, z3, i9, kVar), new FrameLayout.LayoutParams(i3, i5, 19));
                frameLayout.addView(new m0(this, z16 ? 1 : 0, i9, kVar), new FrameLayout.LayoutParams(i3, i5, 21));
                frameLayout.addView(new m0(this, z2, i9, kVar), new FrameLayout.LayoutParams(i6, i4, 49));
                layoutParams = new FrameLayout.LayoutParams(i6, i4, 81);
                m0Var = new m0(this, b14 == true ? 1 : 0, i9, kVar);
                break;
        }
        frameLayout.addView(m0Var, layoutParams);
        final t3 t3VarN = t3.n();
        t3VarN.j(frameLayout, 2);
        this.c0.postDelayed(new Runnable() { // from class: com.jozein.xedgepro.xposed.k1
            @Override // java.lang.Runnable
            public final void run() {
                t3VarN.t(2);
            }
        }, 2000L);
    }

    void b1(long j2) {
        PowerManager.WakeLock wakeLock;
        if (this.f0 || (wakeLock = this.e0) == null) {
            return;
        }
        try {
            wakeLock.acquire(j2);
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    a.p b2() {
        return this.H;
    }

    void b3(String str) {
        if (S0.incrementAndGet() == 2 && !this.n0) {
            this.c0.post(new r1(this));
        }
        u3(this.J.f(str, 1));
    }

    void c3(String str) {
        u3(this.J.f(str, 2));
    }

    void e3(Intent intent) {
        int intExtra = intent.getIntExtra("extra", 0);
        if (intExtra == 1) {
            com.jozein.xedgepro.xposed.w.c();
            v4();
            return;
        }
        if (intExtra != 2) {
            if (intExtra != 3) {
                com.jozein.xedgepro.xposed.w.d(intent);
                return;
            }
        } else if (this.l0 && this.H.J()) {
            return;
        }
        this.H.g();
        com.jozein.xedgepro.xposed.w.h(this.a0);
        H3();
        A3();
    }

    boolean f1() {
        return this.l0;
    }

    w0 f2(final b.i3 i3Var) {
        w0 mVar;
        try {
            switch (i3Var.I) {
                case 0:
                    mVar = new m(this.c0, i3Var);
                    break;
                case 1:
                    mVar = new n(this.c0, i3Var);
                    break;
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                    mVar = new o(this.c0, i3Var);
                    break;
                case 13:
                    mVar = new j(this.c0, i3Var);
                    break;
                case 14:
                    mVar = new l(this.c0, i3Var);
                    break;
                default:
                    return null;
            }
            if (this.H.o(24)) {
                this.c0.post(new Runnable() { // from class: com.jozein.xedgepro.xposed.g1
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.z.A2(i3Var);
                    }
                });
            }
            return mVar;
        } catch (Throwable th) {
            f.v.d(th);
            return null;
        }
    }

    void f3(String str, String str2) {
        if (f1()) {
            if (str != null) {
                u3(this.J.f(str, 4));
            }
            if (str2 != null) {
                u3(this.J.f(str2, 3));
            }
        }
    }

    boolean g1(a.b bVar) {
        return bVar != null && (bVar.z == 65535 || f1());
    }

    m2 g2() {
        return this.Q.d0();
    }

    void g3(boolean z2) {
        u3(this.H.k(z2 ? 35 : 36));
    }

    void h3() {
        if (this.m0) {
            return;
        }
        this.m0 = true;
        f.v.c("First unlocked.");
        W3(false);
        r4();
        this.c0.postDelayed(new Runnable() { // from class: com.jozein.xedgepro.xposed.c1
            @Override // java.lang.Runnable
            public final void run() {
                this.z.D2();
            }
        }, 8000L);
    }

    void i3(boolean z2) {
        u3(this.H.k(z2 ? 33 : 34));
    }

    void j3(final boolean z2) {
        int i2 = this.g0;
        this.g0 = (i2 << 16) | (i2 >>> 16);
        if (this.l0) {
            this.c0.post(new Runnable() { // from class: com.jozein.xedgepro.xposed.j1
                @Override // java.lang.Runnable
                public final void run() {
                    this.z.E2(z2);
                }
            });
        }
    }

    void k3() {
        this.f0 = false;
        this.O.w0();
        if (f1()) {
            this.c0.post(new Runnable() { // from class: com.jozein.xedgepro.xposed.v1
                @Override // java.lang.Runnable
                public final void run() {
                    this.z.F2();
                }
            });
        }
    }

    void l3() {
        this.f0 = true;
        this.O.x0();
    }

    long m3() {
        if (this.s0) {
            return 500L;
        }
        this.s0 = true;
        f.v.c("Shutting down");
        return (this.Z.m0() || this.P.i1()) ? 2000L : 500L;
    }

    void n3(boolean z2) {
        this.w0 = z2;
        if (!z2) {
            o5(null);
        }
        u3(this.H.k(z2 ? 29 : 30));
    }

    void n4(final Intent intent) {
        this.c0.postDelayed(new Runnable() { // from class: com.jozein.xedgepro.xposed.i1
            @Override // java.lang.Runnable
            public final void run() {
                this.z.M2(intent);
            }
        }, 200L);
    }

    void o2(String str, boolean z2) {
        if (str == null || str.length() == 0) {
            f.v.c("Inject empty text.");
            return;
        }
        if (z2) {
            str = f.m0.i(N1(str), this.a0);
        }
        if (this.Z.U(str) || this.R.L(str)) {
            return;
        }
        this.O.i0(str);
    }

    void o3(boolean z2) {
        if (f1()) {
            u3(this.H.k(z2 ? 31 : 32));
        }
    }

    void p3() {
        if (this.k0) {
            return;
        }
        this.k0 = true;
        this.O.y0(this);
        this.H.d(this.a0, this.c0);
        this.U.l(34, this.H.p(1));
        this.U.l(35, this.H.p(0));
        this.Q.p0();
        this.S.e();
        if (f.l.r) {
            this.K.j(new s.e.a() { // from class: com.jozein.xedgepro.xposed.a1
                @Override // a.s.e.a
                public final void a(a.b bVar) {
                    this.f86a.w3(bVar);
                }
            });
            this.K.d(this.a0, this.c0);
        }
        try {
            this.V = new com.jozein.xedgepro.xposed.e(this);
            PowerManager powerManager = (PowerManager) this.a0.getSystemService("power");
            this.d0 = powerManager;
            PowerManager.WakeLock wakeLockNewWakeLock = powerManager.newWakeLock(1, f.l.x + ":job");
            this.e0 = wakeLockNewWakeLock;
            wakeLockNewWakeLock.setReferenceCounted(true);
        } catch (Throwable th) {
            f.v.d(th);
        }
        b.f0.A().d(this.a0, this.c0);
        this.I.d(this.a0, this.c0);
        if (f.l.r) {
            this.J.d(this.a0, this.c0);
            this.L.d(this.a0, this.c0);
            b.k2.A().d(this.a0, this.c0);
        }
        f.q0.e(this.V.f());
        if (Build.VERSION.SDK_INT >= 23) {
            j2();
        }
        boolean z2 = !a.p.f();
        if (this.H.p(8) != z2) {
            this.H.X(this.a0, 8, z2);
        }
        if (z2) {
            f.v.c("Cannot read settings!");
            com.jozein.xedgepro.xposed.w.h(this.a0);
        } else {
            f.v.c("Can read settings.");
            if (!this.H.J()) {
                H3();
            }
        }
        n2.d(this.a0, f.l.j, true);
        if (this.n0) {
            return;
        }
        this.c0.postDelayed(new r1(this), 6000L);
    }

    void p5() {
        if (this.f0) {
            try {
                if (this.z0 == null) {
                    this.z0 = XposedHelpers.findMethodBestMatch(PowerManager.class, "userActivity", new Class[]{Long.TYPE, Boolean.TYPE});
                }
                this.z0.invoke(this.d0, Long.valueOf(SystemClock.uptimeMillis()), Boolean.FALSE);
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
    }

    boolean q2() {
        return this.L0;
    }

    void q3() {
        if (this.k0 && f.l.r) {
            this.K.n(false);
        }
    }

    boolean r2() {
        return this.v0;
    }

    y2 r3() {
        return this.X;
    }

    d3 s3() {
        return this.Y;
    }

    boolean t2() {
        try {
            if (this.t0 == null) {
                this.t0 = k("keyguardOn", new Class[0]);
            }
            return ((Boolean) this.t0.invoke(v(), new Object[0])).booleanValue();
        } catch (Throwable th) {
            f.v.d(th);
            return this.T.w();
        }
    }

    void t4(final float f2, final float f3) {
        this.c0.post(new Runnable() { // from class: com.jozein.xedgepro.xposed.d1
            @Override // java.lang.Runnable
            public final void run() {
                this.z.O2(f2, f3);
            }
        });
    }

    void u1() {
        this.v0 = false;
    }

    void u3(a.b bVar) {
        v3(bVar, -1, null);
    }

    boolean v2(int i2, int i3) {
        if (!this.w0) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= 29) {
            try {
                Region regionF = this.Q.d0().F();
                if (regionF != null) {
                    return regionF.contains(i2, i3);
                }
                return false;
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
        Rect rect = this.x0;
        return rect != null && !t2() && i2 >= rect.left && i2 <= rect.right && i3 >= rect.top && i3 <= rect.bottom;
    }

    void v3(final a.b bVar, final int i2, final Bundle bundle) {
        int i3;
        if (!g1(bVar) || (i3 = bVar.z) == 0 || i3 == 1) {
            return;
        }
        if (i3 == 51 || i3 == 68 || i3 == 102 || i3 == 106 || i3 == 107) {
            f.v.d(new Throwable("Invalid action: " + bVar));
        }
        b1(500L);
        this.c0.postDelayed(new Runnable() { // from class: com.jozein.xedgepro.xposed.h1
            @Override // java.lang.Runnable
            public final void run() {
                this.z.G2(bVar, i2, bundle);
            }
        }, 0L);
    }

    boolean w2() {
        return this.f0;
    }

    void w3(a.b bVar) {
        G2(bVar, -1, null);
    }

    void w4(long j2) {
        this.c0.removeCallbacks(this.u0);
        this.c0.postDelayed(this.u0, j2);
    }

    @Override // com.jozein.xedgepro.xposed.j2
    protected Object x() {
        m2 m2VarD0 = this.Q.d0();
        if (m2VarD0.y() == null) {
            m2VarD0.A(g3.u());
        }
        return m2VarD0.u("mPolicy");
    }

    void x4() {
        if (this.q0) {
            this.q0 = false;
            try {
                this.O.V().Q(false);
                this.r0 = SystemClock.uptimeMillis();
                this.c0.post(new Runnable() { // from class: com.jozein.xedgepro.xposed.m1
                    @Override // java.lang.Runnable
                    public final void run() {
                        w1.P2();
                    }
                });
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
    }

    a.b y4(a.b bVar, boolean z2) {
        try {
            int i2 = bVar.z;
            if (i2 != 62) {
                return i2 != 72 ? bVar : y4(((b.d) bVar).K, z2);
            }
            b.d0 d0Var = (b.d0) bVar;
            return y4(l1(d0Var.I, z2) ? d0Var.J : d0Var.K, z2);
        } catch (Throwable th) {
            f.v.d(th);
            return a.b.e();
        }
    }
}
