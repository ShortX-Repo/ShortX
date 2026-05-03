package com.jozein.xedgepro.service;

import android.content.Intent;
import android.os.Handler;
import f.h0;

/* JADX INFO: loaded from: classes.dex */
public class AdapterService extends a implements Runnable {
    private boolean F = false;

    @Override // com.jozein.xedgepro.service.a
    protected boolean d(Intent intent) {
        if (BinderService.u(this, intent)) {
            Handler handlerA = h0.a();
            if (this.F) {
                handlerA.removeCallbacks(this);
            }
            this.F = true;
            handlerA.postDelayed(this, 5000L);
        }
        return this.F;
    }

    @Override // com.jozein.xedgepro.service.a, android.app.Service
    public void onDestroy() {
        super.onDestroy();
        if (this.F) {
            this.F = false;
            h0.a().removeCallbacks(this);
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        this.F = false;
        stopSelf();
    }
}
