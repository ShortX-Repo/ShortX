package a;

import android.content.Context;
import android.content.Intent;
import cc.xposed.R;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class f extends r {
    private static final String D = f.l.m + "app_triggers";
    public static final int[] E = {1, 2, 3, 4};
    public static final int[] F = {R.string.app_started, R.string.app_stopped, R.string.app_got_focus, R.string.app_lost_focus};
    private static f G = null;
    private final f.p B;
    private Map<String, b[]> C;

    private f() {
        super(f.l.k + "APP_STATES");
        this.B = new f.p(D);
        m();
    }

    public static f g() {
        f fVar;
        synchronized (f.class) {
            if (G == null) {
                G = new f();
            }
            fVar = G;
        }
        return fVar;
    }

    private void k(String str, int i, b bVar) {
        int i2 = bVar.z;
        if (i2 == 1 || i2 == 0) {
            bVar = null;
        }
        b[] bVarArr = this.C.get(str);
        if (bVarArr == null) {
            if (bVar != null) {
                b[] bVarArr2 = new b[4];
                bVarArr2[i - 1] = bVar;
                this.C.put(str, bVarArr2);
                return;
            }
            return;
        }
        bVarArr[i - 1] = bVar;
        for (b bVar2 : bVarArr) {
            if (bVar2 != null) {
                return;
            }
        }
        this.C.remove(str);
    }

    public static void l() {
        synchronized (f.class) {
            G = null;
        }
    }

    private void n() {
        this.B.j();
        for (Map.Entry<String, b[]> entry : this.C.entrySet()) {
            String key = entry.getKey();
            b[] value = entry.getValue();
            for (int i = 0; i < 4; i++) {
                b bVar = value[i];
                if (bVar != null) {
                    this.B.d(i + 1).c(key).f(bVar).C();
                }
            }
        }
        this.B.n();
    }

    @Override // a.r
    protected void b(Intent intent) {
        f.m mVar = new f.m(intent);
        if (mVar.i()) {
            int iH = mVar.h();
            if (iH == 1) {
                String strA = mVar.a();
                int iH2 = mVar.h();
                b bVarG = mVar.g();
                b[] bVarArr = this.C.get(strA);
                if (bVarArr != null) {
                    bVarArr[iH2 - 1] = bVarG;
                } else if (bVarG != null) {
                    b[] bVarArr2 = new b[4];
                    bVarArr2[iH2 - 1] = bVarG;
                    HashMap map = new HashMap(this.C);
                    map.put(strA, bVarArr2);
                    this.C = map;
                }
            } else if (iH == 2) {
                this.C = new HashMap();
            }
        }
        mVar.k();
    }

    public void e(Context context) {
        this.C.clear();
        n();
        Intent intent = new Intent();
        new f.m(intent).j().d(2).l();
        c(context, intent);
    }

    public b f(String str, int i) {
        b bVar;
        b[] bVarArr = this.C.get(str);
        return (bVarArr == null || (bVar = bVarArr[i + (-1)]) == null) ? b.r() : bVar;
    }

    public boolean h(String str) {
        return this.C.get(str) != null;
    }

    public boolean i() {
        return this.C.isEmpty();
    }

    public void j(Context context, String str, int i, b bVar) {
        k(str, i, bVar);
        n();
        Intent intent = new Intent();
        new f.m(intent).j().d(1).c(str).d(i).f(bVar).l();
        c(context, intent);
    }

    public void m() {
        this.C = new HashMap();
        try {
            if (this.B.E()) {
                int iH = this.B.h();
                String strA = this.B.a();
                b bVarG = this.B.g();
                while (true) {
                    k(strA, iH, bVarG);
                    if (!this.B.t()) {
                        break;
                    }
                    iH = this.B.h();
                    strA = this.B.a();
                    bVarG = this.B.g();
                }
            }
            this.B.m();
        } catch (Throwable th) {
            f.v.d(th);
        }
    }
}
