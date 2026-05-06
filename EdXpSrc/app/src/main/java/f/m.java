package f;

import android.content.Intent;
import android.os.Bundle;

/* JADX INFO: loaded from: classes.dex */
public class m implements s {
    private static final String C = l.k + "BYTES";
    private final Bundle A;
    private e B;
    private final Intent z;

    public m(Intent intent) {
        this.B = null;
        this.z = intent;
        this.A = null;
    }

    public m(Bundle bundle) {
        this.B = null;
        this.z = null;
        this.A = bundle;
    }

    public static a.b m(Intent intent) {
        m mVar = new m(intent);
        a.b bVarS = mVar.i() ? a.b.s(mVar) : a.b.e();
        mVar.k();
        return bVarS;
    }

    public static a.b n(Bundle bundle) {
        m mVar = new m(bundle);
        a.b bVarS = mVar.i() ? a.b.s(mVar) : a.b.e();
        mVar.k();
        return bVarS;
    }

    @Override // f.s
    public String a() {
        return this.B.a();
    }

    @Override // f.s
    public String e() {
        return this.B.e();
    }

    @Override // f.s
    public a.b g() {
        return a.b.s(this.B);
    }

    @Override // f.s
    public int h() {
        return this.B.h();
    }

    public boolean i() {
        byte[] byteArray;
        Intent intent = this.z;
        if (intent != null) {
            byteArray = intent.getByteArrayExtra(C);
        } else {
            Bundle bundle = this.A;
            byteArray = bundle != null ? bundle.getByteArray(C) : null;
        }
        if (byteArray == null) {
            byteArray = e.B;
            v.c("No data found for action.");
        }
        e eVar = new e();
        this.B = eVar;
        eVar.i(byteArray);
        return true;
    }

    public m j() {
        this.B = new e().j();
        return this;
    }

    public void k() {
        this.B.m();
        this.B = null;
    }

    public void l() {
        byte[] bArrN = this.B.n();
        Intent intent = this.z;
        if (intent != null) {
            intent.putExtra(C, bArrN);
        } else {
            Bundle bundle = this.A;
            if (bundle != null) {
                bundle.putByteArray(C, bArrN);
            } else {
                v.d(new Throwable("intent == null && bundle == null!"));
            }
        }
        this.B = null;
    }

    @Override // f.s
    /* JADX INFO: renamed from: o, reason: merged with bridge method [inline-methods] */
    public m d(int i) {
        this.B.d(i);
        return this;
    }

    @Override // f.s
    /* JADX INFO: renamed from: p, reason: merged with bridge method [inline-methods] */
    public m f(a.b bVar) {
        bVar.y(this.B);
        return this;
    }

    @Override // f.s
    /* JADX INFO: renamed from: q, reason: merged with bridge method [inline-methods] */
    public m c(String str) {
        this.B.c(str);
        return this;
    }

    @Override // f.s
    /* JADX INFO: renamed from: r, reason: merged with bridge method [inline-methods] */
    public m b(String str) {
        this.B.b(str);
        return this;
    }
}
