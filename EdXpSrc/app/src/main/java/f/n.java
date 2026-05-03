package f;

import android.net.Uri;
import java.io.File;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class n {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final String f468a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private final p f469b;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private long f471d = 0;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private final u<String, String> f470c = new u<>();

    public n(String str) {
        this.f468a = str;
        this.f469b = new p(str + "_list");
    }

    private void h() {
        this.f469b.j();
        int size = this.f470c.size();
        for (int i = 0; i < size; i++) {
            this.f469b.c(this.f470c.e(i)).c(this.f470c.k(i)).C();
        }
        this.f469b.n();
        this.f471d = this.f469b.I();
    }

    public String a(String str) {
        String hexString;
        String strEncode = Uri.encode(str);
        f();
        do {
            hexString = Integer.toHexString(p0.r());
        } while (this.f470c.containsKey(hexString));
        this.f469b.G(true).c(hexString).c(strEncode).C().n();
        this.f470c.put(hexString, strEncode);
        this.f471d = this.f469b.I();
        return hexString;
    }

    public String b(String str) {
        if (str == null) {
            return null;
        }
        f();
        String str2 = this.f470c.get(str);
        if (str2 != null) {
            return Uri.decode(str2);
        }
        return null;
    }

    public Set<String> c() {
        f();
        return this.f470c.keySet();
    }

    public void d(String str) {
        if (str == null) {
            return;
        }
        f();
        int iD = this.f470c.d(str);
        if (iD == -1) {
            return;
        }
        this.f470c.g(iD);
        h();
    }

    public void e(String str) {
        if (str == null) {
            return;
        }
        f();
        int iD = this.f470c.d(str);
        if (iD == -1) {
            return;
        }
        this.f470c.h(iD);
        h();
    }

    public void f() {
        long jI = this.f469b.I();
        if (jI <= 0 || jI == this.f471d) {
            return;
        }
        this.f470c.clear();
        if (this.f469b.E()) {
            do {
                this.f470c.put(this.f469b.a(), this.f469b.a());
            } while (this.f469b.t());
        }
        this.f469b.m();
        this.f471d = jI;
    }

    public void g(String str) {
        f();
        if (!this.f470c.containsKey(str)) {
            v.c("not contains: " + str);
            return;
        }
        this.f470c.remove(str);
        new File(this.f468a + str).delete();
        h();
    }

    public void i(String str, String str2) {
        if (str == null) {
            return;
        }
        f();
        if (this.f470c.containsKey(str)) {
            this.f470c.put(str, Uri.encode(str2));
            h();
        }
    }
}
