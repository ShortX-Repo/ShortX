package com.jozein.xedgepro.xposed;

import android.content.Context;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
class r3 extends g {
    private final Object G;
    private final a.p H;
    private final Vibrator I;
    private final AudioAttributes J;
    private Method K;
    private Method L;

    r3(Object obj, Class<?> cls, Context context, a.p pVar) {
        super(cls);
        this.K = null;
        this.L = null;
        this.G = obj;
        this.H = pVar;
        Vibrator vibrator = (Vibrator) context.getSystemService("vibrator");
        if (vibrator == null || !vibrator.hasVibrator()) {
            this.I = null;
            this.J = null;
            f.v.c("No vibrator.");
        } else {
            this.I = vibrator;
            if (Build.VERSION.SDK_INT >= 21) {
                this.J = new AudioAttributes.Builder().setContentType(4).setUsage(13).build();
            } else {
                this.J = null;
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x001d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean r(int r8) {
        /*
            r7 = this;
            r0 = 4
            r1 = 0
            r2 = 3
            if (r8 == r2) goto L14
            if (r8 == r0) goto Ld
            r0 = 5
            if (r8 == r0) goto Lb
            goto L1d
        Lb:
            r0 = 0
            goto L1e
        Ld:
            int r8 = android.os.Build.VERSION.SDK_INT
            r3 = 21
            if (r8 < r3) goto L1d
            goto L1e
        L14:
            int r8 = android.os.Build.VERSION.SDK_INT
            r0 = 28
            if (r8 < r0) goto L1d
            r0 = 17
            goto L1e
        L1d:
            r0 = 3
        L1e:
            int r8 = android.os.Build.VERSION.SDK_INT     // Catch: java.lang.Throwable -> L89
            r3 = 29
            r4 = 2
            r5 = 1
            if (r8 < r3) goto L5f
            java.lang.reflect.Method r8 = r7.K     // Catch: java.lang.Throwable -> L89
            if (r8 != 0) goto L40
            java.lang.String r8 = "performHapticFeedback"
            java.lang.Class[] r3 = new java.lang.Class[r2]     // Catch: java.lang.Throwable -> L89
            java.lang.Class r6 = java.lang.Integer.TYPE     // Catch: java.lang.Throwable -> L89
            r3[r1] = r6     // Catch: java.lang.Throwable -> L89
            java.lang.Class r6 = java.lang.Boolean.TYPE     // Catch: java.lang.Throwable -> L89
            r3[r5] = r6     // Catch: java.lang.Throwable -> L89
            java.lang.Class<java.lang.String> r6 = java.lang.String.class
            r3[r4] = r6     // Catch: java.lang.Throwable -> L89
            java.lang.reflect.Method r8 = r7.j(r8, r3)     // Catch: java.lang.Throwable -> L89
            r7.K = r8     // Catch: java.lang.Throwable -> L89
        L40:
            java.lang.reflect.Method r8 = r7.K     // Catch: java.lang.Throwable -> L89
            java.lang.Object r3 = r7.G     // Catch: java.lang.Throwable -> L89
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch: java.lang.Throwable -> L89
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch: java.lang.Throwable -> L89
            r2[r1] = r0     // Catch: java.lang.Throwable -> L89
            java.lang.Boolean r0 = java.lang.Boolean.TRUE     // Catch: java.lang.Throwable -> L89
            r2[r5] = r0     // Catch: java.lang.Throwable -> L89
            java.lang.String r0 = f.l.x     // Catch: java.lang.Throwable -> L89
            r2[r4] = r0     // Catch: java.lang.Throwable -> L89
            java.lang.Object r8 = r8.invoke(r3, r2)     // Catch: java.lang.Throwable -> L89
            java.lang.Boolean r8 = (java.lang.Boolean) r8     // Catch: java.lang.Throwable -> L89
            boolean r8 = r8.booleanValue()     // Catch: java.lang.Throwable -> L89
            return r8
        L5f:
            java.lang.reflect.Method r8 = r7.K     // Catch: java.lang.Throwable -> L89
            if (r8 != 0) goto L6b
            java.lang.String r8 = "performHapticFeedbackLw"
            java.lang.reflect.Method r8 = r7.i(r8)     // Catch: java.lang.Throwable -> L89
            r7.K = r8     // Catch: java.lang.Throwable -> L89
        L6b:
            java.lang.reflect.Method r8 = r7.K     // Catch: java.lang.Throwable -> L89
            java.lang.Object r3 = r7.G     // Catch: java.lang.Throwable -> L89
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch: java.lang.Throwable -> L89
            r6 = 0
            r2[r1] = r6     // Catch: java.lang.Throwable -> L89
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch: java.lang.Throwable -> L89
            r2[r5] = r0     // Catch: java.lang.Throwable -> L89
            java.lang.Boolean r0 = java.lang.Boolean.TRUE     // Catch: java.lang.Throwable -> L89
            r2[r4] = r0     // Catch: java.lang.Throwable -> L89
            java.lang.Object r8 = r8.invoke(r3, r2)     // Catch: java.lang.Throwable -> L89
            java.lang.Boolean r8 = (java.lang.Boolean) r8     // Catch: java.lang.Throwable -> L89
            boolean r8 = r8.booleanValue()     // Catch: java.lang.Throwable -> L89
            return r8
        L89:
            r8 = move-exception
            f.v.d(r8)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jozein.xedgepro.xposed.r3.r(int):boolean");
    }

    private void u(int i) {
        int i2 = 20;
        if (i != 3 && i != 4 && i == 5) {
            i2 = 50;
        }
        t(i2);
    }

    private void v() {
        int iV = this.H.v(6, 50);
        if (iV > 100) {
            iV = 100;
        }
        t(iV);
    }

    /* JADX WARN: Removed duplicated region for block: B:8:0x000b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void w(int r6) {
        /*
            r5 = this;
            r0 = 3
            r1 = 1
            r2 = 0
            if (r6 == r0) goto L1d
            r0 = 4
            if (r6 == r0) goto L14
            r0 = 5
            if (r6 == r0) goto Ld
        Lb:
            r0 = 0
            goto L1e
        Ld:
            int r6 = android.os.Build.VERSION.SDK_INT
            r3 = 28
            if (r6 < r3) goto Lb
            goto L1e
        L14:
            int r6 = android.os.Build.VERSION.SDK_INT
            r0 = 27
            if (r6 < r0) goto Lb
            r6 = 2
            r0 = 2
            goto L1e
        L1d:
            r0 = 1
        L1e:
            int r6 = android.os.Build.VERSION.SDK_INT
            r3 = 29
            if (r6 < r3) goto L29
            android.os.VibrationEffect r6 = android.os.VibrationEffect.createPredefined(r0)
            goto L4e
        L29:
            java.lang.reflect.Method r6 = r5.L
            if (r6 != 0) goto L3d
            java.lang.Class<android.os.VibrationEffect> r6 = android.os.VibrationEffect.class
            java.lang.Class[] r3 = new java.lang.Class[r1]
            java.lang.Class r4 = java.lang.Integer.TYPE
            r3[r2] = r4
            java.lang.String r4 = "get"
            java.lang.reflect.Method r6 = de.robv.android.xposed.XposedHelpers.findMethodExact(r6, r4, r3)
            r5.L = r6
        L3d:
            java.lang.reflect.Method r6 = r5.L
            r3 = 0
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r1[r2] = r0
            java.lang.Object r6 = r6.invoke(r3, r1)
            android.os.VibrationEffect r6 = (android.os.VibrationEffect) r6
        L4e:
            android.os.Vibrator r0 = r5.I
            android.media.AudioAttributes r1 = r5.J
            r0.vibrate(r6, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jozein.xedgepro.xposed.r3.w(int):void");
    }

    void s() {
        int iV;
        if (this.I == null || (iV = this.H.v(34, 0)) == 0) {
            return;
        }
        if (iV == 1) {
            v();
        } else {
            if (r(iV)) {
                return;
            }
            if (Build.VERSION.SDK_INT >= 26) {
                try {
                    w(iV);
                    return;
                } catch (Throwable unused) {
                }
            }
            u(iV);
        }
    }

    void t(int i) {
        Vibrator vibrator = this.I;
        if (vibrator == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(i, -1), this.J);
        } else {
            vibrator.vibrate(i);
        }
    }
}
