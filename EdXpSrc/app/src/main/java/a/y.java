package a;

import a.b;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.jozein.xedgepro.ApplicationMain;

/* JADX INFO: loaded from: classes.dex */
public abstract class y {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private Context f57a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private BroadcastReceiver f58b;

    class a extends BroadcastReceiver {
        a() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            y.this.b();
            try {
                y.this.a(intent.getStringArrayExtra("list"));
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
    }

    public y(Context context) {
        this.f58b = null;
        this.f57a = context;
        if (!ApplicationMain.isModuleActivated()) {
            a(null);
            return;
        }
        a aVar = new a();
        this.f58b = aVar;
        this.f57a.registerReceiver(aVar, new IntentFilter(b.s0.M));
        b.s0.B(this.f57a, 9);
    }

    protected abstract void a(String[] strArr);

    public void b() {
        BroadcastReceiver broadcastReceiver = this.f58b;
        if (broadcastReceiver != null) {
            this.f57a.unregisterReceiver(broadcastReceiver);
            this.f57a = null;
            this.f58b = null;
        }
    }
}
