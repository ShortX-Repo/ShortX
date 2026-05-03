package com.jozein.xedgepro.xposed;

import a.p;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;

/* JADX INFO: loaded from: classes.dex */
class i3 implements p.g {
    private static final String[] r = {"android.intent.action.BOOT_COMPLETED", "android.intent.action.SCREEN_ON", "android.intent.action.SCREEN_OFF", "android.intent.action.USER_PRESENT", "android.intent.action.BATTERY_CHANGED", "android.intent.action.BATTERY_LOW", "android.intent.action.BATTERY_OKAY", "android.intent.action.ACTION_POWER_CONNECTED", "android.intent.action.ACTION_POWER_DISCONNECTED", "android.net.wifi.STATE_CHANGE", "android.net.conn.CONNECTIVITY_CHANGE", "android.intent.action.AIRPLANE_MODE", "android.intent.action.HEADSET_PLUG", "android.hardware.usb.action.USB_STATE", "android.intent.action.REBOOT", "android.intent.action.ACTION_SHUTDOWN", "android.intent.action.TIME_SET", "android.intent.action.TIMEZONE_CHANGED", "android.intent.action.USER_SWITCHED"};
    private static final String[] s = {"android.provider.Telephony.SMS_RECEIVED", "android.intent.action.NEW_OUTGOING_CALL", "android.intent.action.PHONE_STATE"};

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private final a.p f132c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private final w1 f133d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private final Context f134e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private int f135f = 0;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private int f136g = 0;
    private int h = 0;
    private boolean i = true;
    private boolean j = true;
    private boolean k = false;
    private boolean l = false;
    private boolean m = false;
    private boolean n = false;
    private boolean o = false;
    private boolean p = false;
    private int q = 0;

    class a extends BroadcastReceiver {
        a() {
        }

        /* JADX WARN: Removed duplicated region for block: B:71:0x0102  */
        @Override // android.content.BroadcastReceiver
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void onReceive(android.content.Context r10, android.content.Intent r11) {
            /*
                Method dump skipped, instruction units count: 734
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: com.jozein.xedgepro.xposed.i3.a.onReceive(android.content.Context, android.content.Intent):void");
        }
    }

    i3(a.p pVar, w1 w1Var, Context context, Handler handler) {
        this.f132c = pVar;
        this.f133d = w1Var;
        this.f134e = context;
        try {
            L(context, handler);
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void J() {
        if (this.j) {
            this.j = false;
            this.f133d.h3();
            K(3);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void K(int i) {
        a.b bVarK;
        int i2;
        if (!f.l.r || (i2 = (bVarK = this.f132c.k(i)).z) == 1 || i2 == 0) {
            return;
        }
        this.f133d.w3(bVarK);
    }

    private void L(Context context, Handler handler) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(1000);
        for (String str : r) {
            intentFilter.addAction(str);
        }
        if (f.l.r) {
            for (String str2 : s) {
                intentFilter.addAction(str2);
            }
            if (Build.VERSION.SDK_INT >= 21) {
                intentFilter.addAction("android.intent.action.PRECISE_CALL_STATE");
            }
        }
        f.c0.w(context, new a(), intentFilter, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void M(boolean z) {
        if (this.n != z) {
            this.n = z;
            K(z ? 13 : 14);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void N(int i) {
        int i2;
        if (i == this.f136g) {
            i2 = 4;
        } else {
            int i3 = this.h;
            if (i >= i3 || this.f135f < i3) {
                if (i >= i3 && this.f135f < i3) {
                    i2 = 6;
                }
                this.f135f = i;
            }
            i2 = 5;
        }
        K(i2);
        this.f135f = i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void O(boolean z) {
        if (this.o != z) {
            this.o = z;
            K(z ? 15 : 16);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void P(boolean z) {
        if (this.m != z) {
            this.m = z;
            K(z ? 11 : 12);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void Q(boolean z) {
        if (this.k != z) {
            this.k = z;
            K(z ? 7 : 8);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void R(boolean z) {
        if (this.i != z) {
            this.i = z;
            if (z) {
                this.f133d.l3();
                K(1);
            } else {
                this.j = true;
                this.f133d.k3();
                K(2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void S(boolean z) {
        if (this.p != z) {
            this.p = z;
            K(z ? 17 : 18);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void T(boolean z) {
        if (this.l != z) {
            this.l = z;
            K(z ? 9 : 10);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void U(int i) {
        int i2;
        int i3;
        if (i != 1) {
            if (i == 2) {
                int i4 = this.q;
                if (i4 != 3) {
                    if (i4 != 6) {
                        return;
                    }
                    this.q = 7;
                    i2 = 27;
                }
            } else if (i != 3) {
                if (i != 6) {
                    int i5 = this.q;
                    if (i5 != 3 && i5 != 4) {
                        i3 = (i5 == 6 || i5 == 7) ? 28 : 25;
                        this.q = 0;
                        return;
                    }
                    K(i3);
                    this.q = 0;
                    return;
                }
                if (this.q == 6) {
                    return;
                }
                this.q = 6;
                i2 = 26;
            } else {
                if (this.q == 3) {
                    return;
                }
                this.q = 3;
                i2 = 23;
            }
            K(i2);
            return;
        }
        if (this.q != 3) {
            return;
        }
        this.q = 4;
        K(24);
    }

    boolean A() {
        return j3.f(this.f134e);
    }

    boolean B() {
        return this.q != 0;
    }

    boolean C() {
        return this.k;
    }

    boolean D() {
        return this.q == 3;
    }

    boolean E() {
        return j3.g();
    }

    boolean F() {
        return this.p;
    }

    boolean G() {
        return j3.h(this.f134e);
    }

    boolean H() {
        return this.l;
    }

    boolean I() {
        return j3.i(this.f134e);
    }

    int q() {
        return this.f135f;
    }

    boolean r() {
        return this.n;
    }

    boolean s() {
        return j3.a(this.f134e);
    }

    boolean t() {
        return j3.b(this.f134e);
    }

    boolean u() {
        return j3.c();
    }

    boolean v() {
        return this.o;
    }

    boolean w() {
        return this.j;
    }

    boolean x() {
        return j3.d(this.f134e);
    }

    boolean y() {
        return j3.e(this.f134e);
    }

    boolean z() {
        return this.m;
    }
}
