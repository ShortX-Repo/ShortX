package com.jozein.xedgepro.service;

import android.content.ComponentName;
import android.content.Intent;
import cc.xposed.R;
import f.l;
import g.b0;

/* JADX INFO: loaded from: classes.dex */
public class ServiceWidget extends a {
    private static final String G = l.k + "WIDGET";
    private b0 F;

    public static Intent f(int i, int i2) {
        Intent intent = new Intent(G);
        intent.setComponent(new ComponentName(l.j, ServiceWidget.class.getName()));
        intent.putExtra("widget_id", i);
        intent.putExtra("dismiss_delay", i2);
        return intent;
    }

    @Override // com.jozein.xedgepro.service.a
    protected void c() {
        a(R.drawable.ic_floating_widget, getText(R.string.action_floating_widget));
    }

    @Override // com.jozein.xedgepro.service.a
    public boolean d(Intent intent) {
        int intExtra;
        if (!G.equals(intent.getAction()) || (intExtra = intent.getIntExtra("widget_id", 0)) == 0 || this.F.k() == intExtra) {
            return false;
        }
        return this.F.s(intExtra, intent.getIntExtra("dismiss_delay", 0));
    }

    @Override // com.jozein.xedgepro.service.a, android.app.Service
    public void onCreate() {
        super.onCreate();
        b0 b0Var = new b0(this);
        this.F = b0Var;
        b0Var.r(new Runnable() { // from class: b.c
            @Override // java.lang.Runnable
            public final void run() {
                this.z.stopSelf();
            }
        });
    }

    @Override // com.jozein.xedgepro.service.a, android.app.Service
    public void onDestroy() {
        super.onDestroy();
        b0 b0Var = this.F;
        if (b0Var != null) {
            b0Var.j();
            this.F = null;
        }
    }
}
