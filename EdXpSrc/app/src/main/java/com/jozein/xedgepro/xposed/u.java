package com.jozein.xedgepro.xposed;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

/* JADX INFO: loaded from: classes.dex */
public class u extends ContextWrapper implements f.l {
    private final l0 A;
    private final Context z;

    public u(Context context, l0 l0Var) {
        super(context);
        this.z = p2.d(context);
        this.A = l0Var;
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public Context getApplicationContext() {
        return getBaseContext();
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public Resources getResources() {
        return this.z.getResources();
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void startActivity(Intent intent) {
        startActivity(intent, null);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void startActivity(Intent intent, Bundle bundle) {
        intent.addFlags(268435456);
        try {
            this.A.q1(intent, false, bundle, f.o0.d());
        } catch (Throwable th) {
            f.v.d(th);
        }
    }
}
