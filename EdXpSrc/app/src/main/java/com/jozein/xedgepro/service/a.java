package com.jozein.xedgepro.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.IBinder;
import cc.xposed.R;
import f.l;
import f.v;
import f.w;

/* JADX INFO: loaded from: classes.dex */
public abstract class a extends Service implements l {
    private static final String B;
    public static final String C;
    private static w D;
    private static int E;
    private boolean z = false;
    private final BroadcastReceiver A = new C0002a();

    /* JADX INFO: renamed from: com.jozein.xedgepro.service.a$a, reason: collision with other inner class name */
    class C0002a extends BroadcastReceiver {
        C0002a() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            try {
                a.this.stopSelf();
            } catch (Throwable th) {
                v.d(th);
            }
        }
    }

    static {
        StringBuilder sb = new StringBuilder();
        String str = l.k;
        sb.append(str);
        sb.append("STOP_SELF");
        B = sb.toString();
        C = str + "STOP_SERVICE";
        D = null;
        E = 1073741823;
    }

    private w b() {
        if (D == null) {
            String string = getString(l.y);
            D = w.a(string, string);
        }
        return D;
    }

    public static boolean e(Context context, Intent intent) {
        intent.setPackage(l.j);
        intent.addFlags(32);
        return (Build.VERSION.SDK_INT >= 26 ? context.startForegroundService(intent) : context.startService(intent)) != null;
    }

    protected void a(int i, CharSequence charSequence) {
        if (this.z) {
            return;
        }
        this.z = true;
        try {
            Notification.Builder builderB = b().c(0).d(w.A).b(this);
            builderB.setContentTitle(getText(l.y)).setContentText(charSequence).setSmallIcon(i > 0 ? i : R.drawable.ic_launcher);
            if (i > 0) {
                if (Build.VERSION.SDK_INT >= 23) {
                    builderB.setLargeIcon(Icon.createWithResource(l.j, i));
                } else {
                    builderB.setLargeIcon(BitmapFactory.decodeResource(getResources(), i));
                }
            }
            Intent intent = new Intent(this, getClass());
            intent.setAction(B);
            builderB.setContentIntent(PendingIntent.getService(this, 0, intent, Build.VERSION.SDK_INT >= 31 ? 33554432 : 0));
            int i2 = E + 1;
            E = i2;
            startForeground(i2, builderB.build());
        } catch (Throwable th) {
            v.d(th);
        }
    }

    protected void c() {
        a(0, null);
    }

    protected abstract boolean d(Intent intent);

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        registerReceiver(this.A, new IntentFilter(C));
        if (this.z || Build.VERSION.SDK_INT < 26) {
            return;
        }
        c();
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.A);
    }

    @Override // android.app.Service
    public final int onStartCommand(Intent intent, int i, int i2) {
        if (!B.equals(intent.getAction())) {
            try {
                if (d(intent)) {
                    return 2;
                }
            } catch (Throwable th) {
                v.d(th);
            }
        }
        stopSelf();
        return 2;
    }
}
