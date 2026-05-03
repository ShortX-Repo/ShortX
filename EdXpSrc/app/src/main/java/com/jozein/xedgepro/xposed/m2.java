package com.jozein.xedgepro.xposed;

import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.view.WindowManager;
import de.robv.android.xposed.XposedHelpers;
import f.f0;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
class m2 extends j2 {
    private final w1 H;
    private WindowManager I;
    private Object J;
    private String K;
    private boolean L;
    private Field M;
    private Method N;
    private Method O;
    private c P;
    private Object Q;
    private Point R;
    private Method S;
    private int T;

    class a extends ContentObserver {
        a(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            try {
                m2.this.S();
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
    }

    class b extends f0.d {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private Method f156a = null;

        b() {
        }

        private Object b() {
            if (this.f156a == null) {
                this.f156a = m2.this.k("getFocusedWindow", new Class[0]);
            }
            return this.f156a.invoke(m2.this.v(), new Object[0]);
        }

        @Override // f.f0.d
        protected Object a(String str, Object[] objArr) {
            Object objB;
            if ("focusChanged".equals(str)) {
                try {
                    try {
                        objB = m2.this.J().c();
                    } catch (Throwable th) {
                        f.v.d(th);
                        objB = b();
                        if (objB != null && v.f245f) {
                            try {
                                int iD = u3.d(objB);
                                if (iD != 0 && iD != -1) {
                                    return null;
                                }
                            } catch (Throwable th2) {
                                f.v.d(th2);
                            }
                        }
                    }
                    m2.this.T(objB);
                } catch (Throwable th3) {
                    f.v.d(th3);
                }
            }
            return null;
        }
    }

    class c {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private Object f158a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private Object f159b = null;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        private Method f160c = null;

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        private Field f161d = null;

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        private Method f162e = null;

        c() {
            synchronized (m2.this.P()) {
                try {
                    this.f158a = m2.this.r("getDefaultDisplayContentLocked", new Object[0]);
                } finally {
                }
            }
        }

        Object a() {
            return this.f158a;
        }

        Object b() {
            if (this.f159b == null) {
                this.f159b = XposedHelpers.callMethod(this.f158a, "getDisplayPolicy", new Object[0]);
            }
            return this.f159b;
        }

        Object c() {
            Object objInvoke;
            if (Build.VERSION.SDK_INT >= 29) {
                try {
                    if (this.f161d == null) {
                        this.f161d = XposedHelpers.findField(this.f158a.getClass(), "mCurrentFocus");
                    }
                    Object obj = this.f161d.get(this.f158a);
                    if (obj != null) {
                        return obj;
                    }
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
            if (this.f162e == null) {
                this.f162e = p2.b(this.f158a.getClass(), "findFocusedWindow", new Class[0]);
            }
            synchronized (m2.this.P()) {
                objInvoke = this.f162e.invoke(this.f158a, new Object[0]);
            }
            return objInvoke;
        }

        Object d(float f2, float f3) {
            Object objInvoke;
            try {
                if (this.f160c == null) {
                    Class<?> cls = this.f158a.getClass();
                    Class cls2 = Float.TYPE;
                    this.f160c = XposedHelpers.findMethodBestMatch(cls, "getTouchableWinAtPointLocked", new Class[]{cls2, cls2});
                }
                synchronized (m2.this.P()) {
                    objInvoke = this.f160c.invoke(this.f158a, Float.valueOf(f2), Float.valueOf(f3));
                }
                return objInvoke;
            } catch (Throwable th) {
                f.v.d(th);
                return null;
            }
        }
    }

    m2(ClassLoader classLoader, w1 w1Var) {
        super("com.android.server.wm.WindowManagerService", classLoader);
        this.J = null;
        this.K = null;
        this.L = false;
        this.M = null;
        this.N = null;
        this.O = null;
        this.P = null;
        this.Q = null;
        this.R = null;
        this.S = null;
        this.T = 0;
        this.H = w1Var;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public c J() {
        if (this.P == null) {
            synchronized (this) {
                if (this.P == null) {
                    this.P = new c();
                }
            }
        }
        return this.P;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Object P() {
        if (this.Q == null) {
            this.Q = u(Build.VERSION.SDK_INT >= 29 ? "mGlobalLock" : "mWindowMap");
        }
        return this.Q;
    }

    void D(int i) {
        r("freezeRotation", Integer.valueOf(i));
    }

    Rect E() {
        Region regionF = F();
        if (regionF == null) {
            return null;
        }
        Rect rect = new Rect();
        if (regionF.getBounds(rect)) {
            return rect;
        }
        return null;
    }

    Region F() {
        if (Build.VERSION.SDK_INT < 28) {
            return null;
        }
        if (this.O == null) {
            this.O = i("getCurrentImeTouchRegion");
        }
        return (Region) this.O.invoke(v(), new Object[0]);
    }

    Object G() {
        return J().a();
    }

    Object H() {
        return J().b();
    }

    Point I(Point point) {
        if (point == null) {
            point = new Point();
        }
        R().getDefaultDisplay().getRealSize(point);
        return point;
    }

    Object K() {
        if (Build.VERSION.SDK_INT >= 30) {
            try {
                Object objG = G();
                if (this.M == null) {
                    this.M = XposedHelpers.findField(objG.getClass(), "mFocusedApp");
                }
                Object obj = this.M.get(objG);
                if (obj != null) {
                    return obj;
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
        try {
            Object objM = M();
            if (objM != null) {
                return u3.a(objM);
            }
            return null;
        } catch (Throwable th2) {
            f.v.d(th2);
            return null;
        }
    }

    String L() {
        return this.K;
    }

    Object M() {
        Object obj = this.J;
        if (obj != null) {
            return obj;
        }
        if (Build.VERSION.SDK_INT < 26) {
            return null;
        }
        try {
            return J().c();
        } catch (Throwable th) {
            f.v.d(th);
            return null;
        }
    }

    float N() {
        return Resources.getSystem().getDisplayMetrics().xdpi;
    }

    int O() {
        if (Build.VERSION.SDK_INT < 26) {
            return t("mRotation");
        }
        if (this.N == null) {
            this.N = k("getDefaultDisplayRotation", new Class[0]);
        }
        return ((Integer) this.N.invoke(v(), new Object[0])).intValue();
    }

    Object Q(float f2, float f3) {
        return J().d(f2, f3);
    }

    WindowManager R() {
        if (this.I == null) {
            this.I = (WindowManager) this.H.T1().getSystemService("window");
        }
        return this.I;
    }

    void S() {
        this.H.V1().o0();
    }

    void T(Object obj) {
        Object obj2 = this.J;
        if (obj2 == obj) {
            return;
        }
        if (obj != null) {
            this.J = obj;
            String strB = u3.b(obj);
            if (strB == null || strB.equals(this.K)) {
                return;
            }
            String str = this.K;
            this.K = strB;
            this.H.f3(str, strB);
            return;
        }
        try {
            if (u3.i(obj2)) {
                return;
            }
            this.J = null;
            String str2 = this.K;
            if (str2 != null) {
                this.K = null;
                this.H.f3(str2, null);
            }
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    void U() {
        try {
            a aVar = new a(this.H.U1());
            this.H.T1().getContentResolver().registerContentObserver(Settings.Global.getUriFor("display_size_forced"), true, aVar);
            S();
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    void V() {
        try {
            r("reboot", Boolean.FALSE);
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    void W() {
        if (this.L) {
            return;
        }
        this.L = true;
        try {
            z("addWindowChangeListener", new b(), new Object[0]);
            if (Build.VERSION.SDK_INT < 31) {
                f.v.c("Setup WindowChangeListener");
            }
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    void X() {
        try {
            r("shutdown", Boolean.FALSE);
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    @Override // com.jozein.xedgepro.xposed.j2
    protected Object x() {
        if (this.H.y() != null) {
            Class<?> clsM = m();
            try {
                Object objU = this.H.u("mWindowManager");
                if (clsM.isInstance(objU)) {
                    return objU;
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
            try {
                Object objU2 = this.H.u("mWindowManagerFuncs");
                if (clsM.isInstance(objU2)) {
                    return objU2;
                }
            } catch (Throwable th2) {
                f.v.d(th2);
            }
        }
        return g3.u();
    }
}
