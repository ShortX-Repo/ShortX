package com.jozein.xedgepro.xposed;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.view.InputEvent;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityManager;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;

/* JADX INFO: loaded from: classes.dex */
class y0 extends j2 {
    private static final boolean g0;
    private static final boolean h0;
    private final w1 H;
    private final a.p I;
    private Handler J;
    private a0 K;
    private b0 L;
    private d M;
    private volatile boolean N;
    private boolean O;
    private c P;
    private volatile boolean Q;
    private boolean R;
    private Method S;
    private boolean T;
    private volatile boolean U;
    private g0 V;
    private g0 W;
    private Method X;
    private boolean Y;
    private int Z;
    private ExecutorService a0;
    private final MotionEvent.PointerProperties[] b0;
    private final MotionEvent.PointerCoords[] c0;
    private volatile Handler d0;
    private final MotionEvent.PointerProperties[] e0;
    private final MotionEvent.PointerCoords[] f0;

    class a extends XC_MethodHook {
        a(int i) {
            super(i);
        }

        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            if (!y0.this.O) {
                y0.this.A(methodHookParam.thisObject);
                return;
            }
            try {
                Object obj = methodHookParam.args[0];
                if (obj instanceof KeyEvent) {
                    y0.this.T(methodHookParam, (KeyEvent) obj);
                } else if (obj instanceof MotionEvent) {
                    y0.this.U(methodHookParam, (MotionEvent) obj);
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
    }

    class b implements AccessibilityManager.AccessibilityStateChangeListener {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ AccessibilityManager f309a;

        b(AccessibilityManager accessibilityManager) {
            this.f309a = accessibilityManager;
        }

        @Override // android.view.accessibility.AccessibilityManager.AccessibilityStateChangeListener
        public void onAccessibilityStateChanged(boolean z) {
            try {
                if (y0.this.Q) {
                    this.f309a.removeAccessibilityStateChangeListener(this);
                } else if (!z) {
                    y0.this.P();
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
    }

    private static class c {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        Object f311a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        Method f312b;

        c(Object obj) {
            Class clsFindClass = XposedHelpers.findClass("com.android.server.input.InputManagerService$InputFilterHost", obj.getClass().getClassLoader());
            Constructor<?> constructor = clsFindClass.getDeclaredConstructors()[0];
            constructor.setAccessible(true);
            this.f311a = constructor.newInstance(obj);
            this.f312b = clsFindClass.getMethod("sendInputEvent", InputEvent.class, Integer.TYPE);
        }

        void a(InputEvent inputEvent, int i) throws IllegalAccessException, InvocationTargetException {
            this.f312b.invoke(this.f311a, inputEvent, Integer.valueOf(i));
        }
    }

    private abstract class d extends XC_MethodHook {

        class a extends XC_MethodHook {
            a() {
            }

            protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
                try {
                    boolean z = methodHookParam.args[0] != null;
                    if (y0.this.Q) {
                        return;
                    }
                    y0.this.N = z;
                    if (z) {
                        return;
                    }
                    y0.this.P();
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
        }

        private d() {
        }

        /* synthetic */ d(y0 y0Var, a aVar) {
            this();
        }

        d a(ClassLoader classLoader) {
            if (Build.VERSION.SDK_INT >= 26) {
                try {
                    y0.this.o("setInputFilter", new a());
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
            return this;
        }

        public abstract int b(InputEvent inputEvent, int i, int i2);

        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            boolean z = true;
            y0.this.Q = true;
            try {
                Object[] objArr = methodHookParam.args;
                int length = objArr.length - 1;
                boolean zBooleanValue = ((Boolean) objArr[length]).booleanValue();
                if (!c()) {
                    y0.this.N = zBooleanValue;
                }
                if (y0.this.O) {
                    if (!zBooleanValue && !y0.this.B0()) {
                        z = false;
                    }
                    methodHookParam.args[length] = Boolean.valueOf(z);
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }

        boolean c() {
            StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            for (int i = 2; i < stackTrace.length; i++) {
                if (stackTrace[i].getClassName().startsWith(f.l.v)) {
                    return true;
                }
            }
            return false;
        }

        public abstract void d(boolean z);
    }

    private class e extends d {

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private Method f315b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        private Object f316c;

        private e() {
            super(y0.this, null);
        }

        /* synthetic */ e(y0 y0Var, a aVar) {
            this();
        }

        private Object e() {
            if (this.f316c == null) {
                this.f316c = y0.this.u("mPtr");
            }
            return this.f316c;
        }

        @Override // com.jozein.xedgepro.xposed.y0.d
        d a(ClassLoader classLoader) {
            this.f315b = y0.this.i("nativeInjectInputEvent");
            try {
                y0.this.o("nativeSetInputFilterEnabled", this);
            } catch (Throwable th) {
                f.v.d(th);
            }
            return super.a(classLoader);
        }

        @Override // com.jozein.xedgepro.xposed.y0.d
        public int b(InputEvent inputEvent, int i, int i2) throws IllegalAccessException, InvocationTargetException {
            int i3 = Build.VERSION.SDK_INT;
            Object objInvoke = (i3 < 29 && i3 >= 21) ? this.f315b.invoke(null, e(), inputEvent, 0, 0, 0, Integer.valueOf(i), 5000, Integer.valueOf(i2)) : this.f315b.invoke(null, e(), inputEvent, 0, 0, Integer.valueOf(i), 5000, Integer.valueOf(i2));
            return ((Integer) objInvoke).intValue();
        }

        @Override // com.jozein.xedgepro.xposed.y0.d
        public void d(boolean z) {
            try {
                y0.this.h("nativeSetInputFilterEnabled", e(), Boolean.valueOf(z));
            } catch (Throwable th) {
                p2.g(th);
            }
        }
    }

    private class f extends d {

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private Object f318b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        private Method f319c;

        private f() {
            super(y0.this, null);
        }

        /* synthetic */ f(y0 y0Var, a aVar) {
            this();
        }

        private Class<?> e(ClassLoader classLoader) {
            try {
                return XposedHelpers.findClass(XposedHelpers.findField(y0.this.m(), "mNative").getType().getName() + "$NativeImpl", classLoader);
            } catch (Throwable th) {
                f.v.d(th);
                return XposedHelpers.findClass("com.android.server.input.NativeInputManagerService$NativeImpl", classLoader);
            }
        }

        private Object f() {
            if (this.f318b == null) {
                this.f318b = y0.this.u("mNative");
            }
            return this.f318b;
        }

        @Override // com.jozein.xedgepro.xposed.y0.d
        d a(ClassLoader classLoader) {
            Class<?> clsE = e(classLoader);
            Class cls = Boolean.TYPE;
            Class cls2 = Integer.TYPE;
            this.f319c = XposedHelpers.findMethodExact(clsE, "injectInputEvent", new Class[]{InputEvent.class, cls, cls2, cls2, cls2, cls2});
            try {
                XposedHelpers.findAndHookMethod(clsE, "setInputFilterEnabled", new Object[]{cls, this});
            } catch (Throwable th) {
                f.v.d(th);
            }
            return super.a(classLoader);
        }

        @Override // com.jozein.xedgepro.xposed.y0.d
        public int b(InputEvent inputEvent, int i, int i2) {
            return ((Integer) this.f319c.invoke(f(), inputEvent, Boolean.FALSE, -1, Integer.valueOf(i), 5000, Integer.valueOf(i2))).intValue();
        }

        @Override // com.jozein.xedgepro.xposed.y0.d
        public void d(boolean z) {
            try {
                XposedHelpers.callMethod(f(), "setInputFilterEnabled", new Object[]{Boolean.valueOf(z)});
            } catch (Throwable th) {
                p2.g(th);
            }
        }
    }

    static {
        boolean z = Build.VERSION.SDK_INT >= 29;
        g0 = z;
        h0 = z;
    }

    y0(w1 w1Var, a.p pVar, ClassLoader classLoader) {
        super("com.android.server.input.InputManagerService", classLoader);
        a aVar = null;
        this.K = null;
        this.L = null;
        this.M = null;
        this.N = false;
        this.O = false;
        this.P = null;
        this.Q = false;
        this.R = false;
        this.S = null;
        this.T = true;
        this.U = false;
        this.V = null;
        this.W = null;
        this.X = null;
        this.Y = true;
        this.Z = -1;
        this.a0 = null;
        this.b0 = new MotionEvent.PointerProperties[]{new MotionEvent.PointerProperties(), new MotionEvent.PointerProperties(), new MotionEvent.PointerProperties(), new MotionEvent.PointerProperties(), new MotionEvent.PointerProperties(), new MotionEvent.PointerProperties(), new MotionEvent.PointerProperties(), new MotionEvent.PointerProperties(), new MotionEvent.PointerProperties(), new MotionEvent.PointerProperties()};
        this.c0 = new MotionEvent.PointerCoords[]{new MotionEvent.PointerCoords(), new MotionEvent.PointerCoords(), new MotionEvent.PointerCoords(), new MotionEvent.PointerCoords(), new MotionEvent.PointerCoords(), new MotionEvent.PointerCoords(), new MotionEvent.PointerCoords(), new MotionEvent.PointerCoords(), new MotionEvent.PointerCoords(), new MotionEvent.PointerCoords()};
        this.d0 = null;
        this.e0 = new MotionEvent.PointerProperties[]{new MotionEvent.PointerProperties()};
        this.f0 = new MotionEvent.PointerCoords[]{new MotionEvent.PointerCoords()};
        this.H = w1Var;
        this.I = pVar;
        try {
            Object[] objArr = new Object[3];
            objArr[0] = InputEvent.class;
            objArr[1] = Integer.TYPE;
            objArr[2] = new a(f.l.r ? 10000 : 50);
            p("filterInputEvent", objArr);
            this.M = Build.VERSION.SDK_INT >= 33 ? new f(this, aVar).a(classLoader) : new e(this, aVar).a(classLoader);
        } catch (Throwable th) {
            p2.g(th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean B0() {
        return this.N || !this.R || this.I.p(1) || this.I.p(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void P() {
        if (B0()) {
            z0(true);
        }
    }

    private void S(boolean z) {
        this.U = false;
        g0 g0Var = this.W;
        if (g0Var != null) {
            g0Var.b(z);
            this.W = null;
        }
    }

    private void b0(KeyEvent keyEvent) {
        u0(keyEvent, 2, 1644167168);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void l0(float f2, float f3) {
        try {
            long jUptimeMillis = SystemClock.uptimeMillis();
            f0(t0(jUptimeMillis, jUptimeMillis, 0, f2, f3));
            f0(t0(jUptimeMillis, jUptimeMillis + 5, 1, f2, f3));
            f.d.f(60L);
            long jUptimeMillis2 = SystemClock.uptimeMillis();
            f0(t0(jUptimeMillis2, jUptimeMillis2, 0, f2, f3));
            f0(t0(jUptimeMillis2, jUptimeMillis2 + 5, 1, f2, f3));
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void m0(int i) {
        try {
            long jUptimeMillis = SystemClock.uptimeMillis();
            b0(new KeyEvent(jUptimeMillis, jUptimeMillis, 0, i, 0, 0, -1, 0, 6));
            b0(new KeyEvent(jUptimeMillis, jUptimeMillis + 5, 1, i, 0, 0, -1, 0, 6));
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void n0(int i, int i2) {
        try {
            long jUptimeMillis = SystemClock.uptimeMillis();
            b0(new KeyEvent(jUptimeMillis, jUptimeMillis, 0, i, 0, i2));
            b0(new KeyEvent(jUptimeMillis, jUptimeMillis + 5, 1, i, 0, i2));
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void o0(int i, int i2) {
        try {
            long jUptimeMillis = SystemClock.uptimeMillis();
            b0(new KeyEvent(jUptimeMillis, jUptimeMillis, 0, i, 0, i2));
            b0(new KeyEvent(jUptimeMillis, jUptimeMillis + 5, 1, i, 0, i2));
            f.d.f(60L);
            long jUptimeMillis2 = SystemClock.uptimeMillis();
            b0(new KeyEvent(jUptimeMillis2, jUptimeMillis2, 0, i, 0, i2));
            b0(new KeyEvent(jUptimeMillis2, jUptimeMillis2 + 5, 1, i, 0, i2));
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void p0(int i, int i2) {
        try {
            long jUptimeMillis = SystemClock.uptimeMillis();
            b0(new KeyEvent(jUptimeMillis, jUptimeMillis, 0, i, 0, i2));
            long longPressTimeout = ViewConfiguration.getLongPressTimeout() + 300;
            f.d.f(longPressTimeout);
            b0(new KeyEvent(jUptimeMillis, jUptimeMillis + longPressTimeout, 1, i, 0, i2));
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void q0(float f2, float f3) {
        try {
            long jUptimeMillis = SystemClock.uptimeMillis();
            f0(t0(jUptimeMillis, jUptimeMillis, 0, f2, f3));
            long longPressTimeout = ViewConfiguration.getLongPressTimeout() + 300;
            for (long j = 100; j < longPressTimeout; j += 100) {
                f0(t0(jUptimeMillis, jUptimeMillis + j, 2, f2, f3));
            }
            f0(t0(jUptimeMillis, jUptimeMillis + longPressTimeout, 1, f2, f3));
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void r0(float f2, float f3) {
        try {
            long jUptimeMillis = SystemClock.uptimeMillis();
            f0(t0(jUptimeMillis, jUptimeMillis, 0, f2, f3));
            f0(t0(jUptimeMillis, jUptimeMillis + 5, 1, f2, f3));
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void s0(KeyEvent[] keyEventArr) {
        try {
            for (KeyEvent keyEvent : keyEventArr) {
                b0(KeyEvent.changeTimeRepeat(keyEvent, SystemClock.uptimeMillis(), 0));
            }
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    private MotionEvent t0(long j, long j2, int i, float f2, float f3) {
        MotionEvent.PointerProperties[] pointerPropertiesArr = this.b0;
        pointerPropertiesArr[0].id = 0;
        pointerPropertiesArr[0].toolType = 1;
        MotionEvent.PointerCoords[] pointerCoordsArr = this.c0;
        pointerCoordsArr[0].x = f2;
        pointerCoordsArr[0].y = f3;
        return MotionEvent.obtain(j, j2, i, 1, pointerPropertiesArr, pointerCoordsArr, 0, 0, 1.0f, 1.0f, 0, 0, 4098, 0);
    }

    private void u0(InputEvent inputEvent, int i, int i2) {
        int iB = this.M.b(inputEvent, i, i2);
        if (iB != 0) {
            if (iB == 1) {
                throw new SecurityException("Input event injection permission denied!");
            }
            if (iB == 3) {
                throw new RuntimeException("Input event injection timed out!");
            }
            throw new RuntimeException("Input event injection failed!");
        }
    }

    private void z0(boolean z) {
        Object objU;
        try {
            try {
                objU = u("mInputFilterLock");
            } catch (Throwable th) {
                p2.g(th);
                objU = null;
            }
            if (objU == null) {
                this.M.d(z);
            } else {
                synchronized (objU) {
                    this.M.d(z);
                }
            }
        } catch (Throwable th2) {
            f.v.d(th2);
        }
    }

    void A0(boolean z) {
        this.U = z;
    }

    void C0() {
        if (this.R) {
            z0(B0());
        }
    }

    void Q(Runnable runnable) {
        if (this.a0 == null) {
            synchronized (this) {
                if (this.a0 == null) {
                    this.a0 = f.d.e("Async #key");
                }
            }
        }
        this.a0.execute(runnable);
    }

    void R(Runnable runnable) {
        W().post(runnable);
    }

    void T(XC_MethodHook.MethodHookParam methodHookParam, KeyEvent keyEvent) {
        a0 a0Var = this.K;
        if (a0Var != null && a0Var.j(methodHookParam, keyEvent)) {
            methodHookParam.setResult(Boolean.FALSE);
            this.H.p5();
        }
    }

    void U(XC_MethodHook.MethodHookParam methodHookParam, MotionEvent motionEvent) {
        g0 g0Var;
        if (this.L == null || motionEvent.getDownTime() == 0) {
            return;
        }
        if (h0) {
            try {
                if (this.Y) {
                    this.Y = false;
                    this.X = XposedHelpers.findMethodExact(MotionEvent.class, "getDisplayId", new Class[0]);
                }
                Method method = this.X;
                if (method != null) {
                    int iIntValue = ((Integer) method.invoke(motionEvent, new Object[0])).intValue();
                    if (iIntValue != 0 && iIntValue != -1) {
                        return;
                    }
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            int deviceId = motionEvent.getDeviceId();
            int buttonState = motionEvent.getButtonState();
            int i = this.Z;
            if ((i < 0 || deviceId == i) && (buttonState == 0 || buttonState == 1)) {
                this.Z = deviceId;
                if (this.L.k(methodHookParam, motionEvent)) {
                    methodHookParam.setResult(Boolean.FALSE);
                    this.H.p5();
                }
                if (this.U) {
                    if (this.V == null) {
                        this.V = new g0(this.H);
                    }
                    g0Var = this.V;
                    this.W = g0Var;
                    g0Var.a(motionEvent);
                }
            } else {
                this.Z = -1;
                this.L.p();
            }
            S(false);
            return;
        }
        if (actionMasked == 1) {
            if (this.Z == motionEvent.getDeviceId()) {
                this.Z = -1;
                if (this.L.k(methodHookParam, motionEvent)) {
                    methodHookParam.setResult(Boolean.FALSE);
                    this.H.p5();
                }
                g0 g0Var2 = this.W;
                if (g0Var2 != null) {
                    g0Var2.a(motionEvent);
                    if (this.U) {
                        return;
                    }
                    S(false);
                    return;
                }
                return;
            }
            return;
        }
        if (actionMasked != 2) {
            if (actionMasked == 3) {
                if (this.Z == motionEvent.getDeviceId()) {
                    this.Z = -1;
                    if (this.L.k(methodHookParam, motionEvent)) {
                        methodHookParam.setResult(Boolean.FALSE);
                        this.H.p5();
                    }
                    S(true);
                    return;
                }
                return;
            }
            if (actionMasked != 5 && actionMasked != 6) {
                return;
            }
        }
        if (this.Z == motionEvent.getDeviceId()) {
            if (this.L.k(methodHookParam, motionEvent)) {
                methodHookParam.setResult(Boolean.FALSE);
                this.H.p5();
            }
            g0Var = this.W;
            if (g0Var == null) {
                return;
            }
            g0Var.a(motionEvent);
        }
    }

    b0 V() {
        return this.L;
    }

    Handler W() {
        if (this.d0 == null) {
            synchronized (this) {
                if (this.d0 == null) {
                    this.d0 = f.h0.e("Async #gesture");
                }
            }
        }
        return this.d0;
    }

    void X(final float f2, final float f3) {
        R(new Runnable() { // from class: com.jozein.xedgepro.xposed.q0
            @Override // java.lang.Runnable
            public final void run() {
                this.z.l0(f2, f3);
            }
        });
    }

    void Y(final int i) {
        Q(new Runnable() { // from class: com.jozein.xedgepro.xposed.t0
            @Override // java.lang.Runnable
            public final void run() {
                this.z.m0(i);
            }
        });
    }

    void Z(final int i, final int i2) {
        Q(new Runnable() { // from class: com.jozein.xedgepro.xposed.v0
            @Override // java.lang.Runnable
            public final void run() {
                this.z.n0(i, i2);
            }
        });
    }

    void a0(final int i, final int i2) {
        Q(new Runnable() { // from class: com.jozein.xedgepro.xposed.u0
            @Override // java.lang.Runnable
            public final void run() {
                this.z.o0(i, i2);
            }
        });
    }

    void c0(final int i, final int i2) {
        Q(new Runnable() { // from class: com.jozein.xedgepro.xposed.w0
            @Override // java.lang.Runnable
            public final void run() {
                this.z.p0(i, i2);
            }
        });
    }

    void d0(final float f2, final float f3) {
        R(new Runnable() { // from class: com.jozein.xedgepro.xposed.s0
            @Override // java.lang.Runnable
            public final void run() {
                this.z.q0(f2, f3);
            }
        });
    }

    void e0(MotionEvent motionEvent) {
        u0(motionEvent, 0, 1711276032);
    }

    void f0(MotionEvent motionEvent) {
        u0(motionEvent, 2, 1711276032);
    }

    void g0(float f2, float f3, float f4) {
        long jUptimeMillis = SystemClock.uptimeMillis();
        MotionEvent.PointerProperties[] pointerPropertiesArr = this.e0;
        pointerPropertiesArr[0].id = 0;
        pointerPropertiesArr[0].toolType = 3;
        MotionEvent.PointerCoords[] pointerCoordsArr = this.f0;
        pointerCoordsArr[0].x = f3;
        pointerCoordsArr[0].y = f4;
        pointerCoordsArr[0].setAxisValue(9, f2);
        e0(MotionEvent.obtain(jUptimeMillis, jUptimeMillis, 8, 1, this.e0, this.f0, 0, 0, 0.0f, 0.0f, 0, 0, 8194, 0));
    }

    void h0(final float f2, final float f3) {
        R(new Runnable() { // from class: com.jozein.xedgepro.xposed.r0
            @Override // java.lang.Runnable
            public final void run() {
                this.z.r0(f2, f3);
            }
        });
    }

    boolean i0(String str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        final KeyEvent[] events = KeyCharacterMap.load(-1).getEvents(str.toCharArray());
        if (events == null) {
            return false;
        }
        Q(new Runnable() { // from class: com.jozein.xedgepro.xposed.x0
            @Override // java.lang.Runnable
            public final void run() {
                this.z.s0(events);
            }
        });
        return true;
    }

    void j0(XC_MethodHook.MethodHookParam methodHookParam) throws IllegalAccessException, InvocationTargetException {
        if (this.N) {
            w(methodHookParam.method, methodHookParam.args);
            return;
        }
        Object[] objArr = methodHookParam.args;
        InputEvent inputEvent = (InputEvent) objArr[0];
        int iIntValue = ((Integer) objArr[1]).intValue();
        c cVar = this.P;
        if (cVar != null) {
            cVar.a(inputEvent, iIntValue);
        } else {
            u0(inputEvent, 0, iIntValue | 67108864);
        }
    }

    boolean k0() {
        return this.U;
    }

    void v0(Context context) {
        AccessibilityManager accessibilityManager;
        this.O = true;
        P();
        if (Build.VERSION.SDK_INT < 26 || (accessibilityManager = (AccessibilityManager) context.getSystemService("accessibility")) == null) {
            return;
        }
        accessibilityManager.addAccessibilityStateChangeListener(new b(accessibilityManager));
    }

    void w0() {
        this.Z = -1;
    }

    @Override // com.jozein.xedgepro.xposed.j2
    protected Object x() {
        InputManager inputManager;
        Context contextT1 = this.H.T1();
        if (contextT1 != null && (inputManager = (InputManager) contextT1.getSystemService("input")) != null) {
            try {
                Object objectField = XposedHelpers.getObjectField(inputManager, "mIm");
                if (objectField != null) {
                    return objectField;
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
        return g3.l();
    }

    void x0() {
        y2 y2VarR3 = this.H.r3();
        if (y2VarR3 != null) {
            y2VarR3.p();
        }
        d3 d3VarS3 = this.H.s3();
        if (d3VarS3 != null) {
            d3VarS3.s();
        }
    }

    void y0(w1 w1Var) {
        try {
            Handler handler = (Handler) u("mHandler");
            if (handler != null) {
                this.J = new f.h0(handler.getLooper());
            }
        } catch (Throwable th) {
            f.v.d(th);
        }
        if (this.J == null) {
            this.J = w1Var.U1();
        }
        this.K = new a0(w1Var, this, this.J);
        this.L = new b0(w1Var, this, this.J);
        try {
            this.P = new c(v());
        } catch (Throwable th2) {
            f.v.d(th2);
        }
    }
}
