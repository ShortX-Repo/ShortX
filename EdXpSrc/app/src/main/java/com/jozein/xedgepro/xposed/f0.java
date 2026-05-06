package com.jozein.xedgepro.xposed;

import android.content.Context;
import android.os.Handler;

/* JADX INFO: loaded from: classes.dex */
class f0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final Context f109a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private final Handler f110b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private g.r f111c = null;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private final int f112d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private final int f113e;

    f0(Context context, Handler handler, boolean z) {
        this.f109a = context;
        this.f110b = handler;
        this.f112d = z ? -16738393 : -24576;
        this.f113e = z ? 3 : 4;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void f(g.q qVar) {
        g.r rVar = this.f111c;
        if (rVar != null) {
            rVar.e(qVar);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void g() {
        t3.n().t(this.f113e);
        this.f111c = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void h() {
        if (this.f111c == null) {
            g.r rVar = new g.r(this.f109a, this.f112d);
            this.f111c = rVar;
            rVar.l(1, 0, false);
            t3.n().j(this.f111c, this.f113e);
        }
    }

    public void d(final g.q qVar) {
        this.f110b.post(new Runnable() { // from class: com.jozein.xedgepro.xposed.e0
            @Override // java.lang.Runnable
            public final void run() {
                this.z.f(qVar);
            }
        });
    }

    public void e() {
        this.f110b.post(new Runnable() { // from class: com.jozein.xedgepro.xposed.c0
            @Override // java.lang.Runnable
            public final void run() {
                this.z.g();
            }
        });
    }

    public void i() {
        this.f110b.post(new Runnable() { // from class: com.jozein.xedgepro.xposed.d0
            @Override // java.lang.Runnable
            public final void run() {
                this.z.h();
            }
        });
    }
}
