package com.jozein.xedgepro.xposed;

import android.view.MotionEvent;
import com.jozein.xedgepro.service.BinderService;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
class g0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final w1 f121a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private final f0 f122b;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private long f125e;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private boolean f123c = false;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private boolean f124d = false;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private boolean f126f = false;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private final ArrayList<g.q> f127g = new ArrayList<>(256);

    g0(w1 w1Var) {
        this.f121a = w1Var;
        this.f122b = new f0(w1Var.T1(), w1Var.U1(), true);
    }

    void a(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (!this.f123c) {
            if (actionMasked != 0) {
                f.v.d(new IllegalStateException("Recording MotionEvent while not downed!"));
                return;
            }
            this.f123c = true;
            this.f125e = motionEvent.getDownTime();
            this.f126f = this.f121a.V1().j0();
            boolean zO = this.f121a.b2().o(6);
            this.f124d = zO;
            if (zO) {
                this.f122b.i();
            }
        }
        g.q qVar = new g.q(motionEvent, this.f125e);
        this.f127g.add(qVar);
        if (this.f124d) {
            this.f122b.d(qVar);
        }
    }

    void b(boolean z) {
        if (this.f123c) {
            this.f123c = false;
            this.f121a.a3();
            if (this.f124d) {
                this.f124d = false;
                this.f122b.e();
            }
            if (!z) {
                try {
                    BinderService.m(this.f121a.T1(), this.f126f, this.f127g);
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
            this.f127g.clear();
        }
    }
}
