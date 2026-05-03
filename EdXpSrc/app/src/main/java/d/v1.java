package d;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import cc.xposed.R;
import e.j;
import f.c0;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class v1 extends e.j {
    private List<c0.e> M;

    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.select_shortcut);
    }

    @Override // e.j
    protected int B0() {
        if (this.M == null) {
            this.M = f.z.K(f()).T();
        }
        return this.M.size();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // e.j
    /* JADX INFO: renamed from: B1, reason: merged with bridge method [inline-methods] */
    public j.k h1(int i) {
        c0.e eVar = this.M.get(i);
        return new j.g(eVar.d(), eVar.f(), (CharSequence) null);
    }

    @Override // e.j
    protected void g1(Object obj) {
        this.M = (List) obj;
    }

    @Override // e.j
    protected void k1(int i) {
        c0.e eVar = this.M.get(i);
        Intent intent = new Intent("android.intent.action.CREATE_SHORTCUT");
        intent.setComponent(new ComponentName(eVar.i(), eVar.r()));
        startActivityForResult(intent, 0);
    }

    @Override // e.j
    protected j.i m1() {
        List<c0.e> listM = m(c0.e.class);
        this.M = listM;
        if (listM != null) {
            return null;
        }
        return new j.i.e();
    }

    @Override // android.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i2 == -1) {
            try {
                a.b bVarE = f.l0.e(intent, M0(), this.M.get(E0()).i());
                if (bVarE != null) {
                    W("result", bVarE);
                    L();
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
    }

    @Override // e.j, e.j0.c, android.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        Z(this.M);
    }
}
