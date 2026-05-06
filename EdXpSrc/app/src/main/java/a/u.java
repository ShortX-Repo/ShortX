package a;

import a.b;
import com.jozein.xedgepro.service.SuperTileService;

/* JADX INFO: loaded from: classes.dex */
public class u {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final String f46c = f.l.m + "tiles";

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    static final String[] f47d = SuperTileService.F;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static b f48e = null;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final b.d f49a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final b.d[] f50b;

    public static class b extends u {

        /* JADX INFO: renamed from: f, reason: collision with root package name */
        private final SuperTileService[] f51f;

        private b() {
            super();
            this.f51f = new SuperTileService[u.f47d.length];
        }

        private void m() {
            f.p pVarC = c();
            pVarC.j();
            pVarC.d(u.f47d.length).C();
            int i = 0;
            while (true) {
                String[] strArr = u.f47d;
                if (i >= strArr.length) {
                    pVarC.n();
                    return;
                } else {
                    pVarC.c(strArr[i]).f(this.f50b[i]).C();
                    i++;
                }
            }
        }

        boolean k() {
            for (int i = 0; i < u.f47d.length; i++) {
                if (this.f51f[i] != null) {
                    return true;
                }
            }
            return false;
        }

        public void l(SuperTileService superTileService) {
            int iE = u.e(superTileService.getClass().getName());
            if (iE >= 0) {
                this.f51f[iE] = superTileService;
            }
        }

        public void n(int i, b.d dVar) {
            this.f50b[i] = dVar;
            m();
            SuperTileService superTileService = this.f51f[i];
            if (superTileService != null) {
                superTileService.f();
            }
        }

        public void o(SuperTileService superTileService) {
            int iE = u.e(superTileService.getClass().getName());
            if (iE >= 0) {
                this.f51f[iE] = null;
            }
        }

        public void p(String str) {
            int iE = u.e(str);
            if (iE >= 0) {
                this.f51f[iE] = null;
            }
        }

        void q() {
            boolean z = true;
            for (int i = 0; i < u.f47d.length; i++) {
                if (this.f51f[i] != null) {
                    if (z) {
                        g();
                        z = false;
                    }
                    this.f51f[i].f();
                }
            }
        }
    }

    private u() {
        this.f49a = new b.d(a.b.r());
        this.f50b = new b.d[f47d.length];
        g();
    }

    public static b d() {
        b bVar;
        synchronized (b.class) {
            if (f48e == null) {
                f48e = new b();
            }
            bVar = f48e;
        }
        return bVar;
    }

    static int e(String str) {
        int length = f47d.length;
        int length2 = str.length();
        char cCharAt = str.charAt(length2 - 1);
        char cCharAt2 = str.charAt(length2 - 2);
        for (int i = 0; i < length; i++) {
            String str2 = f47d[i];
            int length3 = str2.length();
            if (str2.charAt(length3 - 1) == cCharAt && str2.charAt(length3 - 2) == cCharAt2) {
                return i;
            }
        }
        return -1;
    }

    public static void j() {
        b bVar;
        synchronized (b.class) {
            b bVar2 = f48e;
            if (bVar2 != null && !bVar2.k()) {
                f48e = null;
            }
            bVar = f48e;
        }
        if (bVar != null) {
            bVar.q();
        }
    }

    public b.d a(int i) {
        return this.f50b[i];
    }

    public b.d b(String str) {
        int iE = e(str);
        return iE >= 0 ? this.f50b[iE] : this.f49a;
    }

    f.p c() {
        return new f.p(f46c);
    }

    void f(String str, b.d dVar) {
        int iE = e(str);
        if (iE >= 0) {
            this.f50b[iE] = dVar;
        }
    }

    void g() {
        for (int i = 0; i < f47d.length; i++) {
            this.f50b[i] = this.f49a;
        }
        f.p pVarC = c();
        if (pVarC.E()) {
            int iH = pVarC.h();
            for (int i2 = 0; i2 < iH && pVarC.t(); i2++) {
                f(pVarC.a(), (b.d) pVarC.g());
            }
        }
        pVarC.m();
    }

    public String h(int i) {
        return f47d[i];
    }

    public int i() {
        return this.f50b.length;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(256);
        int length = f47d.length;
        sb.append('[');
        sb.append(length);
        sb.append(" tiles ");
        for (int i = 0; i < length; i++) {
            sb.append('[');
            sb.append(f47d[i]);
            sb.append(' ');
            sb.append(this.f50b[i].K);
            sb.append("] ");
        }
        sb.append(']');
        return sb.toString();
    }
}
