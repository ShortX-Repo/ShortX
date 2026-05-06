package f;

import android.os.Parcel;

/* JADX INFO: loaded from: classes.dex */
public class d0 implements s {
    private final Parcel z;

    public d0(Parcel parcel) {
        this.z = parcel;
    }

    @Override // f.s
    public String a() {
        return this.z.readString();
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
        return this.z.readInt();
    }

    public boolean i() {
        return true;
    }

    public d0 j() {
        return this;
    }

    public void k() {
    }

    public void l() {
    }

    @Override // f.s
    /* JADX INFO: renamed from: m, reason: merged with bridge method [inline-methods] */
    public d0 d(int i) {
        this.z.writeInt(i);
        return this;
    }

    @Override // f.s
    /* JADX INFO: renamed from: n, reason: merged with bridge method [inline-methods] */
    public d0 f(a.b bVar) {
        bVar.y(this);
        return this;
    }

    @Override // f.s
    /* JADX INFO: renamed from: o, reason: merged with bridge method [inline-methods] */
    public d0 c(String str) {
        this.z.writeString(str);
        return this;
    }

    @Override // f.s
    /* JADX INFO: renamed from: p, reason: merged with bridge method [inline-methods] */
    public d0 b(String str) {
        return c(str);
    }
}
