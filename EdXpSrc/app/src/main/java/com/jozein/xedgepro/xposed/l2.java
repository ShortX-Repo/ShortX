package com.jozein.xedgepro.xposed;

import a.b;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import com.jozein.xedgepro.xposed.q3;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/* JADX INFO: loaded from: classes.dex */
class l2 extends p2 {
    static final float E;

    class a extends XC_MethodHook {
        a() {
        }

        View a(Object obj) {
            return (View) XposedHelpers.getObjectField(XposedHelpers.getSurroundingThis(obj), "mView");
        }

        /* JADX WARN: Removed duplicated region for block: B:12:0x0031  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        protected void beforeHookedMethod(de.robv.android.xposed.XC_MethodHook.MethodHookParam r6) {
            /*
                r5 = this;
                java.lang.Object[] r0 = r6.args
                r1 = 0
                r2 = r0[r1]
                boolean r2 = r2 instanceof android.view.MotionEvent
                r3 = 1
                if (r2 == 0) goto L31
                r0 = r0[r1]
                android.view.MotionEvent r0 = (android.view.MotionEvent) r0
                int r2 = r0.getActionMasked()
                r4 = 8
                if (r2 != r4) goto L31
                r2 = 9
                float r2 = r0.getAxisValue(r2)
                float r4 = com.jozein.xedgepro.xposed.l2.E
                int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                if (r2 != 0) goto L31
                java.lang.Object r2 = r6.thisObject     // Catch: java.lang.Throwable -> L2f
                android.view.View r2 = r5.a(r2)     // Catch: java.lang.Throwable -> L2f
                if (r2 == 0) goto L2f
                com.jozein.xedgepro.xposed.l2 r4 = com.jozein.xedgepro.xposed.l2.this     // Catch: java.lang.Throwable -> L2f
                com.jozein.xedgepro.xposed.l2.i(r4, r2, r0)     // Catch: java.lang.Throwable -> L2f
            L2f:
                r0 = 1
                goto L32
            L31:
                r0 = 0
            L32:
                if (r0 == 0) goto L4c
                java.lang.Object r0 = r6.thisObject
                r2 = 2
                java.lang.Object[] r2 = new java.lang.Object[r2]
                java.lang.Object[] r4 = r6.args
                r4 = r4[r1]
                r2[r1] = r4
                java.lang.Boolean r1 = java.lang.Boolean.TRUE
                r2[r3] = r1
                java.lang.String r1 = "finishInputEvent"
                de.robv.android.xposed.XposedHelpers.callMethod(r0, r1, r2)
                r0 = 0
                r6.setResult(r0)
            L4c:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.jozein.xedgepro.xposed.l2.a.beforeHookedMethod(de.robv.android.xposed.XC_MethodHook$MethodHookParam):void");
        }
    }

    class b extends f.g0 {
        final /* synthetic */ q3.a D;
        final /* synthetic */ int E;
        final /* synthetic */ int F;
        final /* synthetic */ Context G;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        b(Handler handler, int i, long j, q3.a aVar, int i2, int i3, Context context) {
            super(handler, i, j);
            this.D = aVar;
            this.E = i2;
            this.F = i3;
            this.G = context;
        }

        @Override // f.g0
        protected boolean a(int i) {
            if (!this.D.C.isAttachedToWindow()) {
                return false;
            }
            q3.a aVarG = q3.g(this.D, this.E, this.F);
            if (aVarG == null) {
                l2.this.o(this.G, null);
            } else {
                if (aVarG.C != null) {
                    return true;
                }
                l2.this.o(this.G, aVarG.A);
            }
            return false;
        }

        @Override // f.g0
        protected void b() {
            l2.m(this.G, false);
        }
    }

    static {
        E = f.l.r ? 1.2321E-6f : 1.234321E-6f;
    }

    l2(ClassLoader classLoader) {
        try {
            XposedBridge.hookAllMethods(XposedHelpers.findClass("android.view.ViewRootImpl$WindowInputEventReceiver", classLoader), "onInputEvent", new a());
        } catch (Throwable th) {
            p2.g(th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void l(final View view, MotionEvent motionEvent) {
        final int rawX = (int) motionEvent.getRawX();
        final int rawY = (int) motionEvent.getRawY();
        view.post(new Runnable() { // from class: com.jozein.xedgepro.xposed.k2
            @Override // java.lang.Runnable
            public final void run() {
                this.z.n(view, rawX, rawY);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void m(Context context, boolean z) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService("accessibility");
        if (accessibilityManager.isEnabled() == z) {
            return;
        }
        int i = Build.VERSION.SDK_INT;
        if (i >= 26) {
            XposedHelpers.setBooleanField(accessibilityManager, "mIsEnabled", z);
            XposedHelpers.callMethod(accessibilityManager, "notifyAccessibilityStateChanged", new Object[0]);
        } else if (i >= 21) {
            XposedHelpers.setBooleanField(accessibilityManager, "mIsEnabled", z);
            XposedHelpers.callMethod(accessibilityManager, "handleNotifyAccessibilityStateChanged", new Object[0]);
        } else {
            synchronized (XposedHelpers.getObjectField(accessibilityManager, "mHandler")) {
                XposedHelpers.setBooleanField(accessibilityManager, "mIsEnabled", z);
                XposedHelpers.callMethod(accessibilityManager, "notifyAccessibilityStateChangedLh", new Object[0]);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void n(View view, int i, int i2) {
        int[] iArr = new int[2];
        view.getLocationOnScreen(iArr);
        int i3 = i - iArr[0];
        int i4 = i2 - iArr[1];
        Context context = view.getContext();
        q3.a aVarE = q3.e(view, i3, i4);
        if (aVarE != null) {
            CharSequence charSequence = aVarE.A;
            if (charSequence != null) {
                o(context, charSequence);
                return;
            }
            if (aVarE.C != null) {
                AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService("accessibility");
                try {
                    if (accessibilityManager == null || !accessibilityManager.isEnabled()) {
                        m(context, true);
                        new b(f.h0.d(), 3, 150L, aVarE, i, i2, context).e();
                        return;
                    } else {
                        q3.a aVarG = q3.g(aVarE, i, i2);
                        if (aVarG != null) {
                            o(context, aVarG.A);
                            return;
                        }
                    }
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
        }
        o(context, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void o(Context context, CharSequence charSequence) {
        b.s0.G(context, 20, q3.c(charSequence));
    }
}
