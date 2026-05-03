package a;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

/* JADX INFO: loaded from: classes.dex */
public abstract class r implements f.l {
    private BroadcastReceiver A = null;
    private final String z;

    class a extends BroadcastReceiver {
        a() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            try {
                r.this.b(intent);
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
    }

    r(String str) {
        this.z = str;
    }

    protected abstract void b(Intent intent);

    protected void c(Context context, Intent intent) {
        intent.setAction(this.z).setPackage("android");
        context.sendBroadcast(intent);
    }

    public void d(Context context, Handler handler) {
        try {
            a aVar = new a();
            this.A = aVar;
            context.registerReceiver(aVar, new IntentFilter(this.z), null, handler);
        } catch (Throwable th) {
            f.v.d(th);
        }
    }
}
