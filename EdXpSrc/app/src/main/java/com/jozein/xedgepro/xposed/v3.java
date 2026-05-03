package com.jozein.xedgepro.xposed;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import com.jozein.xedgepro.xposed.q3;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.Field;

/* JADX INFO: loaded from: classes.dex */
class v3 extends AccessibilityService {
    private static boolean C;
    private static boolean D;
    private static final Rect E = new Rect();
    private final AccessibilityManager A;
    private final Object B;
    private final w1 z;

    class a extends XC_MethodHook {
        a() {
        }

        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            if (v3.C) {
                try {
                    methodHookParam.args[0] = Boolean.TRUE;
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
        }
    }

    class b extends XC_MethodHook {
        b() {
        }

        protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            if (v3.C) {
                try {
                    int iIntValue = ((Integer) methodHookParam.getResult()).intValue();
                    if ((iIntValue & 1) == 0) {
                        methodHookParam.setResult(Integer.valueOf(iIntValue | 1));
                    }
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
        }
    }

    class c extends f.g0 {
        private boolean D;
        private boolean E;
        final /* synthetic */ float F;
        final /* synthetic */ float G;
        final /* synthetic */ d H;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        c(Handler handler, int i, long j, float f2, float f3, d dVar) {
            super(handler, i, j);
            this.F = f2;
            this.G = f3;
            this.H = dVar;
            this.D = false;
            this.E = false;
        }

        @Override // f.g0
        protected boolean a(int i) {
            AccessibilityNodeInfo rootInActiveWindow = v3.this.getRootInActiveWindow();
            if (rootInActiveWindow == null) {
                return true;
            }
            this.D = true;
            q3.a aVarF = q3.f(rootInActiveWindow, (int) this.F, (int) this.G);
            if (aVarF == null) {
                this.E = true;
                this.H.a(true, null);
                return false;
            }
            if (aVarF.B.contains("Layout") || aVarF.B.contains("WebView")) {
                return true;
            }
            this.E = true;
            this.H.a(true, aVarF.b());
            return false;
        }

        @Override // f.g0
        protected void b() {
            try {
                v3.this.i(false);
            } catch (Throwable th) {
                f.v.d(th);
            }
            if (this.E) {
                return;
            }
            this.H.a(this.D, null);
        }
    }

    interface d {
        void a(boolean z, String str);
    }

    v3(w1 w1Var) {
        this.z = w1Var;
        Context contextT1 = w1Var.T1();
        AccessibilityManager accessibilityManager = (AccessibilityManager) contextT1.getSystemService("accessibility");
        this.A = accessibilityManager;
        Object objD = d(accessibilityManager);
        this.B = objD;
        XposedHelpers.setIntField(this, "mConnectionId", e(objD));
        attachBaseContext(contextT1);
    }

    private static Object d(AccessibilityManager accessibilityManager) {
        Object objCallMethod;
        try {
            Object objectField = XposedHelpers.getObjectField(accessibilityManager, "mService");
            if (objectField != null) {
                return objectField;
            }
            synchronized (XposedHelpers.getObjectField(accessibilityManager, "mLock")) {
                objCallMethod = XposedHelpers.callMethod(accessibilityManager, "getServiceLocked", new Object[0]);
            }
            return objCallMethod;
        } catch (Throwable th) {
            f.v.d(th);
            return g3.h();
        }
    }

    private static int e(Object obj) {
        Object objCallMethod;
        String str;
        int i = Build.VERSION.SDK_INT;
        if (i >= 21) {
            if (i >= 26) {
                objCallMethod = XposedHelpers.callMethod(obj, "getInteractionBridge", new Object[0]);
            } else {
                synchronized (XposedHelpers.getObjectField(obj, "mLock")) {
                    objCallMethod = XposedHelpers.callMethod(obj, "getInteractionBridgeLocked", new Object[0]);
                }
            }
            str = "mConnectionId";
        } else {
            synchronized (XposedHelpers.getObjectField(obj, "mLock")) {
                objCallMethod = XposedHelpers.callMethod(obj, "getQueryBridge", new Object[0]);
            }
            str = "mId";
        }
        return XposedHelpers.getIntField(objCallMethod, str);
    }

    static void f(ClassLoader classLoader) {
        g gVar;
        String str;
        XC_MethodHook bVar;
        int i = Build.VERSION.SDK_INT;
        if (i >= 26) {
            try {
                if (i >= 30) {
                    gVar = new g("com.android.server.accessibility.AccessibilityUserState", classLoader);
                    str = "getClientStateLocked";
                    bVar = new a();
                } else {
                    gVar = new g("com.android.server.accessibility.AccessibilityManagerService$UserState", classLoader);
                    str = "getClientState";
                    bVar = new b();
                }
                gVar.o(str, bVar);
                D = true;
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
    }

    private boolean g(float f2, float f3) {
        if (Build.VERSION.SDK_INT < 24) {
            return true;
        }
        try {
            return u3.h(this.z.g2().Q(f2, f3));
        } catch (Throwable th) {
            f.v.d(th);
            return true;
        }
    }

    private void h(boolean z) {
        C = z;
        if (Build.VERSION.SDK_INT < 31 || D) {
            return;
        }
        try {
            Object objectField = XposedHelpers.getObjectField(this.B, "mUiAutomationManager");
            Field fieldFindField = XposedHelpers.findField(objectField.getClass(), "mUiAutomationFlags");
            int i = fieldFindField.getInt(objectField);
            fieldFindField.setInt(objectField, z ? i | 2 : i & (-3));
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void i(boolean z) {
        h(z);
        if (Build.VERSION.SDK_INT >= 31) {
            XposedHelpers.callMethod(this.B, "scheduleUpdateClientsIfNeeded", new Object[]{XposedHelpers.callMethod(this.B, "getCurrentUserState", new Object[0])});
        } else {
            synchronized (XposedHelpers.getObjectField(this.B, "mLock")) {
                XposedHelpers.callMethod(this.B, "scheduleUpdateClientsIfNeededLocked", new Object[]{XposedHelpers.callMethod(this.B, "getCurrentUserStateLocked", new Object[0])});
            }
        }
    }

    void c(float f2, float f3, d dVar) {
        if (Build.VERSION.SDK_INT < 26 || !g(f2, f3)) {
            dVar.a(false, null);
            return;
        }
        try {
            if (!this.A.isEnabled()) {
                i(true);
                new c(this.z.U1(), 3, 150L, f2, f3, dVar).e();
                return;
            }
            AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
            if (rootInActiveWindow == null) {
                dVar.a(false, null);
            } else {
                q3.a aVarF = q3.f(rootInActiveWindow, (int) f2, (int) f3);
                dVar.a(true, aVarF != null ? aVarF.b() : null);
            }
        } catch (Throwable th) {
            f.v.d(th);
            dVar.a(false, null);
        }
    }

    @Override // android.accessibilityservice.AccessibilityService
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
    }

    @Override // android.accessibilityservice.AccessibilityService
    public void onInterrupt() {
    }
}
