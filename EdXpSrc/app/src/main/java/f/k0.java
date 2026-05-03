package f;

import android.content.Intent;

/* JADX INFO: loaded from: classes.dex */
public class k0 implements s {
    private int A = 0;
    private final Intent z;

    public k0(Intent intent) {
        this.z = intent;
    }

    @Override // f.s
    public String a() {
        Intent intent = this.z;
        StringBuilder sb = new StringBuilder();
        sb.append("__");
        int i = this.A;
        this.A = i + 1;
        sb.append(i);
        return intent.getStringExtra(sb.toString());
    }

    @Override // f.s
    public String e() {
        return a();
    }

    @Override // f.s
    public a.b g() {
        return a.b.s(this);
    }

    @Override // f.s
    public int h() {
        Intent intent = this.z;
        StringBuilder sb = new StringBuilder();
        sb.append("__");
        int i = this.A;
        this.A = i + 1;
        sb.append(i);
        return intent.getIntExtra(sb.toString(), 0);
    }

    public boolean i() {
        this.A = 0;
        return true;
    }

    public k0 j() {
        return this;
    }

    public void k() {
    }

    public void l() {
    }

    @Override // f.s
    /* JADX INFO: renamed from: m, reason: merged with bridge method [inline-methods] */
    public k0 d(int i) {
        Intent intent = this.z;
        StringBuilder sb = new StringBuilder();
        sb.append("__");
        int i2 = this.A;
        this.A = i2 + 1;
        sb.append(i2);
        intent.putExtra(sb.toString(), i);
        return this;
    }

    @Override // f.s
    /* JADX INFO: renamed from: n, reason: merged with bridge method [inline-methods] */
    public k0 f(a.b bVar) {
        bVar.y(this);
        return this;
    }

    @Override // f.s
    /* JADX INFO: renamed from: o, reason: merged with bridge method [inline-methods] */
    public k0 c(String str) {
        Intent intent = this.z;
        StringBuilder sb = new StringBuilder();
        sb.append("__");
        int i = this.A;
        this.A = i + 1;
        sb.append(i);
        intent.putExtra(sb.toString(), str);
        return this;
    }

    @Override // f.s
    /* JADX INFO: renamed from: p, reason: merged with bridge method [inline-methods] */
    public k0 b(String str) {
        return c(str);
    }
}
