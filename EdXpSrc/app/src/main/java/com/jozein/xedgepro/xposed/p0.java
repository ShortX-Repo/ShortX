package com.jozein.xedgepro.xposed;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
class p0 extends j2 {
    private final w1 H;
    private boolean I;
    private Object J;
    private Object K;

    class a extends XC_MethodHook {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final int f206a;

        a() {
            int i = Build.VERSION.SDK_INT;
            this.f206a = (i < 26 || i > 28) ? 1 : 2;
        }

        protected void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            p0.this.A(methodHookParam.thisObject);
            boolean z = (((Integer) methodHookParam.args[this.f206a]).intValue() & 2) != 0;
            if (z != p0.this.I) {
                p0.this.I = z;
                p0.this.H.n3(z);
            }
        }
    }

    private static class b extends NullPointerException {
        b() {
        }
    }

    p0(ClassLoader classLoader, w1 w1Var) {
        super(Build.VERSION.SDK_INT >= 29 ? "com.android.server.inputmethod.InputMethodManagerService" : "com.android.server.InputMethodManagerService", classLoader);
        this.I = false;
        this.K = null;
        this.H = w1Var;
        try {
            o("setImeWindowStatus", new a());
        } catch (Throwable th) {
            p2.g(th);
        }
    }

    private Object F() {
        Object objU = u("mCurInputContext");
        if (objU != null) {
            return objU;
        }
        throw new b();
    }

    private String G() {
        return (String) (Build.VERSION.SDK_INT >= 33 ? r("getSelectedMethodIdLocked", new Object[0]) : u("mCurMethodId"));
    }

    private Object H() {
        if (this.K == null) {
            this.K = XposedHelpers.newInstance(XposedHelpers.findClass("com.android.internal.inputmethod.InputConnectionCommandHeader", m().getClassLoader()), new Object[]{0});
        }
        return this.K;
    }

    private Object I() {
        if (this.J == null) {
            this.J = Build.VERSION.SDK_INT >= 33 ? XposedHelpers.findClass("com.android.server.inputmethod.ImfLock", m().getClassLoader()) : u("mMethodMap");
        }
        return this.J;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void M() {
        try {
            XposedHelpers.callMethod(u("mMenuController"), "showInputMethodMenu", new Object[]{Boolean.TRUE, 0});
        } catch (Throwable th) {
            f.v.c(th.toString());
            Message message = new Message();
            message.what = 1;
            message.arg1 = 1;
            message.arg2 = 0;
            try {
                ((Handler.Callback) v()).handleMessage(message);
            } catch (Throwable th2) {
                f.v.d(th2);
            }
        }
    }

    private void O(String str, int i) {
        synchronized (I()) {
            try {
                r("setInputMethodLocked", str, Integer.valueOf(i));
            } finally {
            }
        }
    }

    void J() {
        synchronized (I()) {
            if (Build.VERSION.SDK_INT >= 30) {
                r("hideCurrentInputLocked", u("mLastImeTargetWindow"), 0, null, 3);
            } else {
                r("hideCurrentInputLocked", 0, null);
            }
        }
    }

    boolean K(int i) {
        try {
            Object objF = F();
            long jUptimeMillis = SystemClock.uptimeMillis();
            KeyEvent keyEvent = new KeyEvent(jUptimeMillis, jUptimeMillis, 0, i, 0, 0, -1, 0, 6);
            KeyEvent keyEvent2 = new KeyEvent(jUptimeMillis, SystemClock.uptimeMillis(), 1, i, 0, 0, -1, 0, 6);
            if (Build.VERSION.SDK_INT >= 33) {
                Object objH = H();
                p2.a(objF, "sendKeyEvent", objH, keyEvent);
                p2.a(objF, "sendKeyEvent", objH, keyEvent2);
            } else {
                p2.a(objF, "sendKeyEvent", keyEvent);
                p2.a(objF, "sendKeyEvent", keyEvent2);
            }
            return true;
        } catch (b unused) {
            return true;
        } catch (Throwable th) {
            f.v.d(th);
            return false;
        }
    }

    boolean L(String str) {
        if (str != null && str.length() != 0) {
            try {
                Object objF = F();
                if (Build.VERSION.SDK_INT >= 33) {
                    Object objH = H();
                    p2.a(objF, "finishComposingText", objH);
                    p2.a(objF, "commitText", objH, str, 1);
                } else {
                    p2.a(objF, "finishComposingText", new Object[0]);
                    p2.a(objF, "commitText", str, 1);
                }
                return true;
            } catch (b unused) {
            } catch (Throwable th) {
                f.v.d(th);
                return false;
            }
        }
        return true;
    }

    boolean N(int i) {
        try {
            Object objF = F();
            if (Build.VERSION.SDK_INT >= 33) {
                p2.a(objF, "performContextMenuAction", H(), Integer.valueOf(i));
            } else {
                p2.a(objF, "performContextMenuAction", Integer.valueOf(i));
            }
            return true;
        } catch (b unused) {
            return true;
        } catch (Throwable th) {
            f.v.d(th);
            return false;
        }
    }

    void P() {
        int i = Build.VERSION.SDK_INT;
        if (i >= 31) {
            ((Handler) u("mHandler")).post(new Runnable() { // from class: com.jozein.xedgepro.xposed.o0
                @Override // java.lang.Runnable
                public final void run() {
                    this.z.M();
                }
            });
            return;
        }
        if (i >= 29) {
            r("showInputMethodMenu", Boolean.TRUE, 0);
        } else if (i >= 21) {
            r("showInputMethodMenu", Boolean.TRUE);
        } else {
            r("showInputMethodMenuInternal", Boolean.TRUE);
        }
    }

    void Q() {
        synchronized (I()) {
            if (Build.VERSION.SDK_INT >= 30) {
                r("showCurrentInputLocked", u("mLastImeTargetWindow"), 0, null, 0);
            } else {
                r("showCurrentInputLocked", 0, null);
            }
        }
    }

    void R() {
        List<InputMethodInfo> enabledInputMethodList = null;
        try {
            int i = Build.VERSION.SDK_INT;
            if (((Boolean) r(i >= 28 ? "switchToPreviousInputMethod" : "switchToLastInputMethod", i >= 33 ? r("getCurTokenLocked", new Object[0]) : i >= 29 ? u("mCurToken") : null)).booleanValue()) {
                return;
            }
        } catch (Throwable th) {
            f.v.d(th);
        }
        InputMethodManager inputMethodManager = (InputMethodManager) this.H.T1().getSystemService("input_method");
        if (inputMethodManager == null) {
            return;
        }
        int iD = f.o0.d();
        if (iD != 0) {
            try {
                enabledInputMethodList = (List) XposedHelpers.callMethod(inputMethodManager, "getEnabledInputMethodListAsUser", new Object[]{Integer.valueOf(iD)});
            } catch (Throwable th2) {
                f.v.d(th2);
            }
        }
        if (enabledInputMethodList == null) {
            enabledInputMethodList = inputMethodManager.getEnabledInputMethodList();
        }
        int size = enabledInputMethodList.size();
        if (size <= 1) {
            if (size > 0) {
                return;
            }
            f.v.c("No IME.");
        } else {
            String strG = G();
            String id = enabledInputMethodList.get(0).getId();
            if (id.equals(strG)) {
                id = enabledInputMethodList.get(1).getId();
            }
            O(id, -1);
        }
    }

    @Override // com.jozein.xedgepro.xposed.j2
    protected Object x() {
        Object objectField;
        try {
            objectField = Build.VERSION.SDK_INT >= 29 ? XposedHelpers.getObjectField(g3.q("com.android.server.inputmethod.InputMethodManagerInternal"), "mService") : this.H.g2().u("mInputMethodManager");
        } catch (Throwable th) {
            f.v.d(th);
            objectField = null;
        }
        return objectField != null ? objectField : g3.m();
    }
}
