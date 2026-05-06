package a;

import android.net.Uri;
import f.p0;

/* JADX INFO: loaded from: classes.dex */
public class o {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final b[] f10c = new b[0];

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f11a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final b[] f12b;

    public o(f.s sVar) {
        this.f11a = Uri.decode(sVar.a());
        int iH = sVar.h();
        if (iH <= 0) {
            this.f12b = f10c;
            return;
        }
        this.f12b = new b[iH];
        for (int i = 0; i < iH; i++) {
            this.f12b[i] = sVar.g();
        }
    }

    public o(String str, b[] bVarArr) {
        this.f11a = str == null ? p0.f() : str;
        this.f12b = bVarArr == null ? f10c : bVarArr;
    }

    public void a(f.s sVar) {
        sVar.c(Uri.encode(this.f11a)).d(this.f12b.length);
        for (b bVar : this.f12b) {
            bVar.y(sVar);
        }
    }
}
