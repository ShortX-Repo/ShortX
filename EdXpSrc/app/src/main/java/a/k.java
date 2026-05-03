package a;

import android.content.Context;
import android.content.Intent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class k extends r {
    private static final String D = f.l.m + "hidden_apps";
    private static final String E = f.l.k + "HIDDEN_APPS";
    private static k F = null;
    private final f.p B;
    private Set<String> C;

    private k() {
        super(E);
        this.B = new f.p(D);
        l();
        if (this.C == null) {
            this.C = new HashSet();
        }
    }

    public static k h() {
        k kVar;
        synchronized (k.class) {
            if (F == null) {
                F = new k();
            }
            kVar = F;
        }
        return kVar;
    }

    public static void k() {
        synchronized (k.class) {
            F = null;
        }
    }

    private void n(Context context, int i, String str) {
        this.B.j();
        Iterator<String> it = this.C.iterator();
        while (it.hasNext()) {
            this.B.B(it.next());
        }
        this.B.n();
        Intent intent = new Intent();
        new f.m(intent).j().d(i).c(str).l();
        c(context, intent);
    }

    @Override // a.r
    protected void b(Intent intent) {
        int iH;
        String strA;
        HashSet hashSet;
        f.m mVar = new f.m(intent);
        if (mVar.i()) {
            iH = mVar.h();
            strA = mVar.a();
        } else {
            iH = -1;
            strA = null;
        }
        mVar.k();
        if (iH != 1) {
            if (iH != 2) {
                if (iH != 3) {
                    return;
                } else {
                    hashSet = new HashSet();
                }
            } else {
                if (strA == null || strA.length() <= 0) {
                    return;
                }
                hashSet = new HashSet(this.C);
                hashSet.remove(strA);
            }
        } else {
            if (strA == null || strA.length() <= 0) {
                return;
            }
            hashSet = new HashSet(this.C);
            hashSet.add(strA);
        }
        this.C = hashSet;
    }

    public void e(Context context, String str) {
        if (this.C.contains(str)) {
            return;
        }
        this.C.add(str);
        n(context, 1, str);
    }

    public void f(Context context) {
        if (this.C.isEmpty()) {
            return;
        }
        this.C.clear();
        n(context, 3, null);
    }

    public boolean g(String str) {
        return this.C.contains(str);
    }

    public Set<String> i() {
        return this.C;
    }

    public boolean j() {
        return this.C.isEmpty();
    }

    public void l() {
        HashSet hashSet = new HashSet();
        try {
            if (this.B.E()) {
                while (true) {
                    String strS = this.B.s();
                    if (strS == null) {
                        break;
                    } else if (strS.length() > 0) {
                        hashSet.add(strS);
                    }
                }
            }
            this.B.m();
        } catch (Throwable th) {
            f.v.d(th);
        }
        this.C = hashSet;
    }

    public void m(Context context, String str) {
        if (this.C.remove(str)) {
            n(context, 2, str);
        }
    }
}
