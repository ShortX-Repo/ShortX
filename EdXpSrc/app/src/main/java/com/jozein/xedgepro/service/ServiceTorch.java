package com.jozein.xedgepro.service;

import a.b;
import android.content.ComponentName;
import android.content.Intent;
import android.widget.Toast;
import cc.xposed.R;
import f.h0;
import f.l;
import f.m;
import f.n0;

/* JADX INFO: loaded from: classes.dex */
public class ServiceTorch extends com.jozein.xedgepro.service.a {
    private n0 F = null;

    class a implements n0.b {
        a() {
        }

        @Override // f.n0.b
        public void a() {
            ServiceTorch.this.stopSelf();
        }

        @Override // f.n0.b
        public void b() {
        }
    }

    public static Intent f(b.l3 l3Var, boolean z, boolean z2) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(l.j, ServiceTorch.class.getName()));
        new m(intent).j().f(l3Var).d(z ? 1 : 0).d(z2 ? 1 : 0).l();
        return intent;
    }

    private void g(boolean z) {
        n0 n0Var = this.F;
        if (n0Var != null && n0Var.a()) {
            this.F.e();
            if (z) {
                Toast.makeText(getApplicationContext(), new b.l3(2).n(this), 0).show();
            }
        }
        this.F = null;
        stopSelf();
    }

    private void h(boolean z, boolean z2) {
        if (this.F == null) {
            this.F = n0.b(h0.a(), this, new a());
        }
        if (!this.F.g()) {
            stopSelf();
            return;
        }
        if (z) {
            Toast.makeText(getApplicationContext(), new b.l3(1).n(this), 0).show();
        }
        if (z2) {
            c();
        }
    }

    @Override // com.jozein.xedgepro.service.a
    protected void c() {
        a(R.drawable.ic_torch, getText(R.string.action_torch));
    }

    @Override // com.jozein.xedgepro.service.a
    protected boolean d(Intent intent) {
        m mVar = new m(intent);
        if (mVar.i()) {
            b bVarS = b.s(mVar);
            if (bVarS.z == 32) {
                b.l3 l3Var = (b.l3) bVarS;
                n0 n0Var = this.F;
                boolean z = n0Var != null && n0Var.a();
                int i = l3Var.I;
                boolean z2 = i == 1 || !(i == 2 || z);
                if (z != z2) {
                    if (z2) {
                        h(mVar.h() != 0, mVar.h() != 0);
                    } else {
                        g(mVar.h() != 0);
                    }
                }
                mVar.k();
                return true;
            }
        }
        mVar.k();
        return false;
    }

    @Override // com.jozein.xedgepro.service.a, android.app.Service
    public void onDestroy() {
        super.onDestroy();
        n0 n0Var = this.F;
        if (n0Var != null) {
            n0Var.e();
        }
    }
}
