package f;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;

/* JADX INFO: loaded from: classes.dex */
public abstract class a {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final String f409c = l.k + "SCHEDULE-";

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static int f410d = 0;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    protected final String f411a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    long f412b = 0;

    /* JADX INFO: renamed from: f.a$a, reason: collision with other inner class name */
    private static class C0006a extends a {

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        private final Context f413e;

        /* JADX INFO: renamed from: f, reason: collision with root package name */
        private final Handler f414f;

        /* JADX INFO: renamed from: g, reason: collision with root package name */
        private final c f415g;
        private final int h;
        private BroadcastReceiver i;

        /* JADX INFO: renamed from: f.a$a$a, reason: collision with other inner class name */
        class C0007a extends BroadcastReceiver {
            C0007a() {
            }

            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                try {
                    C0006a c0006a = C0006a.this;
                    long j = c0006a.f412b;
                    c0006a.f412b = 0L;
                    c0006a.f415g.a(C0006a.this, j);
                    C0006a c0006a2 = C0006a.this;
                    if (c0006a2.f412b <= 0) {
                        context.unregisterReceiver(c0006a2.i);
                        C0006a.this.i = null;
                    }
                } catch (Throwable th) {
                    v.d(th);
                }
            }
        }

        C0006a(Context context, Handler handler, c cVar, int i) {
            this.f413e = context;
            this.f414f = handler;
            this.f415g = cVar;
            this.h = i;
        }

        @Override // f.a
        public void a() {
            this.f412b = 0L;
            BroadcastReceiver broadcastReceiver = this.i;
            if (broadcastReceiver != null) {
                try {
                    this.f413e.unregisterReceiver(broadcastReceiver);
                    this.i = null;
                    ((AlarmManager) this.f413e.getSystemService("alarm")).cancel(PendingIntent.getBroadcast(this.f413e, 0, new Intent(this.f411a), Build.VERSION.SDK_INT >= 31 ? 301989888 : 268435456));
                } catch (Throwable th) {
                    v.d(th);
                }
            }
        }

        @Override // f.a
        public void d(long j) {
            if (this.i == null) {
                C0007a c0007a = new C0007a();
                this.i = c0007a;
                this.f413e.registerReceiver(c0007a, new IntentFilter(this.f411a), null, this.f414f);
            }
            AlarmManager alarmManager = (AlarmManager) this.f413e.getSystemService("alarm");
            Intent intent = new Intent(this.f411a);
            intent.setPackage("android");
            try {
                alarmManager.setExact(this.h, j, PendingIntent.getBroadcast(this.f413e, 0, intent, Build.VERSION.SDK_INT >= 31 ? 167772160 : 134217728));
                this.f412b = j;
            } catch (Throwable th) {
                v.d(th);
            }
        }
    }

    private static class b extends a implements AlarmManager.OnAlarmListener {

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        private final Context f417e;

        /* JADX INFO: renamed from: f, reason: collision with root package name */
        private final Handler f418f;

        /* JADX INFO: renamed from: g, reason: collision with root package name */
        private final c f419g;
        private final int h;
        private C0006a i = null;

        b(Context context, Handler handler, c cVar, int i) {
            this.f417e = context;
            this.f418f = handler;
            this.f419g = cVar;
            this.h = i;
        }

        @Override // f.a
        public void a() {
            C0006a c0006a = this.i;
            if (c0006a != null) {
                c0006a.a();
                return;
            }
            this.f412b = 0L;
            try {
                ((AlarmManager) this.f417e.getSystemService("alarm")).cancel(this);
            } catch (Throwable th) {
                v.d(th);
            }
        }

        @Override // f.a
        public void d(long j) {
            C0006a c0006a = this.i;
            if (c0006a != null) {
                c0006a.d(j);
                return;
            }
            AlarmManager alarmManager = (AlarmManager) this.f417e.getSystemService("alarm");
            if (this.f412b > 0) {
                try {
                    alarmManager.cancel(this);
                } catch (Throwable th) {
                    v.d(th);
                }
            }
            try {
                alarmManager.setExact(this.h, j, this.f411a, this, this.f418f);
                this.f412b = j;
            } catch (Throwable th2) {
                v.d(th2);
                C0006a c0006a2 = new C0006a(this.f417e, this.f418f, this.f419g, this.h);
                this.i = c0006a2;
                c0006a2.d(j);
            }
        }

        @Override // android.app.AlarmManager.OnAlarmListener
        public void onAlarm() {
            try {
                long j = this.f412b;
                this.f412b = 0L;
                this.f419g.a(this, j);
            } catch (Throwable th) {
                v.d(th);
            }
        }
    }

    public interface c {
        void a(a aVar, long j);
    }

    a() {
        StringBuilder sb = new StringBuilder();
        sb.append(f409c);
        int i = f410d + 1;
        f410d = i;
        sb.append(i);
        this.f411a = sb.toString();
    }

    public static a c(Context context, Handler handler, c cVar, int i) {
        return Build.VERSION.SDK_INT >= 24 ? new b(context, handler, cVar, i) : new C0006a(context, handler, cVar, i);
    }

    public abstract void a();

    public long b() {
        return this.f412b;
    }

    public abstract void d(long j);
}
