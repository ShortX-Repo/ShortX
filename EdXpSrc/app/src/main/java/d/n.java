package d;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import cc.xposed.R;
import e.j;

/* JADX INFO: loaded from: classes.dex */
public class n extends e.j implements j.d {
    private a.b[] M;

    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.select_icon);
    }

    @Override // e.j
    protected int B0() {
        a.b[] bVarArrI = a.b.i(0);
        this.M = bVarArrI;
        return bVarArrI.length;
    }

    @Override // e.j.d
    public String[] a() {
        Context contextM0 = M0();
        int length = this.M.length;
        String[] strArr = new String[length];
        for (int i = 0; i < length; i++) {
            strArr[i] = this.M[i].p(contextM0).toString();
        }
        return strArr;
    }

    @Override // e.j
    protected View h1(int i) {
        a.b bVar = this.M[i];
        return new j.g(D0(bVar), bVar.p(M0()), (CharSequence) null);
    }

    @Override // e.j
    protected void k1(int i) {
        String resourceName = q().getResourceName(this.M[i].B);
        if (resourceName.indexOf(47) != -1) {
            resourceName = resourceName.substring(resourceName.indexOf(47) + 1);
        }
        Bundle bundle = new Bundle(1);
        bundle.putString("result", resourceName);
        U(bundle);
        L();
    }
}
