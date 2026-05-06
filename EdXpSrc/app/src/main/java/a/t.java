package a;

import a.b;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/* JADX INFO: loaded from: classes.dex */
public class t {

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static final String f36f = f.l.k + "STATE_CHANGED";

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final Context f37a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private int f38b = 0;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private int f39c = 0;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private final b f40d = new b(null);

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private BroadcastReceiver f41e = null;

    class a extends BroadcastReceiver {
        a() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            int intExtra;
            if (!t.f36f.equals(intent.getAction()) || (intExtra = intent.getIntExtra("bits", -1)) < 0 || t.this.f38b == intExtra) {
                return;
            }
            int i = t.this.f38b ^ intExtra;
            t.this.f38b = intExtra;
            if (t.this.f40d.f45c <= 0) {
                return;
            }
            int i2 = 1;
            for (int i3 = 0; i3 < 31; i3++) {
                if ((i & i2) != 0) {
                    i ^= i2;
                    try {
                        t.this.f40d.c(i2, (intExtra & i2) != 0);
                    } catch (Throwable th) {
                        f.v.d(th);
                        return;
                    }
                }
                i2 <<= 1;
                if (i == 0) {
                    return;
                }
            }
        }
    }

    private static class b {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private c[] f43a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private int[] f44b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        private int f45c;

        private b() {
            this.f43a = null;
            this.f44b = null;
            this.f45c = 0;
        }

        /* synthetic */ b(a aVar) {
            this();
        }

        void b(String str, int i, c cVar) {
            int iE = u.e(str);
            if (iE < 0) {
                return;
            }
            if (this.f43a == null) {
                c[] cVarArr = new c[u.f47d.length];
                this.f43a = cVarArr;
                this.f44b = new int[cVarArr.length];
            }
            c[] cVarArr2 = this.f43a;
            if (cVarArr2[iE] != null) {
                this.f45c--;
            }
            cVarArr2[iE] = cVar;
            this.f44b[iE] = i;
            this.f45c++;
        }

        void c(int i, boolean z) {
            if (this.f45c <= 0) {
                return;
            }
            int i2 = 0;
            while (true) {
                int[] iArr = this.f44b;
                if (i2 >= iArr.length) {
                    return;
                }
                if (iArr[i2] == i) {
                    c[] cVarArr = this.f43a;
                    if (cVarArr[i2] != null) {
                        cVarArr[i2].a(z);
                    }
                }
                i2++;
            }
        }

        void d(String str) {
            int iE;
            if (this.f43a != null && (iE = u.e(str)) >= 0) {
                c[] cVarArr = this.f43a;
                c cVar = cVarArr[iE];
                cVarArr[iE] = null;
                if (cVar != null) {
                    this.f45c--;
                }
            }
        }
    }

    public interface c {
        void a(boolean z);
    }

    public t(Context context) {
        this.f37a = context;
    }

    public static boolean i(int i) {
        return m(i) > 0;
    }

    private void k() {
        int i = this.f39c;
        int i2 = this.f38b;
        if (i == i2) {
            return;
        }
        Intent intent = new Intent(f36f);
        intent.putExtra("bits", i2);
        try {
            intent.setPackage("com.android.systemui");
            this.f37a.sendBroadcast(intent);
            intent.setPackage(f.l.j);
            this.f37a.sendBroadcast(intent);
            this.f39c = i2;
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    private static int m(int i) {
        if (i == 73) {
            return 2;
        }
        if (i == 97) {
            return 64;
        }
        switch (i) {
            case 29:
                return 1;
            case 30:
                return 4;
            case 31:
                return 32;
            case 32:
                return 8;
            case 33:
                return 16;
            case 34:
                return 128;
            case 35:
                return 256;
            default:
                return -1;
        }
    }

    public void e() {
        if (this.f41e != null) {
            try {
                b.s0.B(this.f37a, 21);
                this.f37a.unregisterReceiver(this.f41e);
                this.f41e = null;
            } catch (Throwable unused) {
            }
        }
    }

    public void f() {
        a aVar = new a();
        this.f41e = aVar;
        try {
            this.f37a.registerReceiver(aVar, new IntentFilter(f36f));
            b.s0.B(this.f37a, 21);
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    public void g() {
        this.f39c = 0;
        k();
    }

    public boolean h(int i, boolean z) {
        int iM = m(i);
        return iM <= 0 ? z : (iM & this.f38b) != 0;
    }

    public void j(String str, int i, c cVar) {
        int iM = m(i);
        if (iM <= 0) {
            return;
        }
        this.f40d.b(str, iM, cVar);
    }

    public void l(int i, boolean z) {
        int i2;
        int iM = m(i);
        if (iM <= 0) {
            return;
        }
        if (z) {
            i2 = iM | this.f38b;
        } else {
            i2 = (iM ^ (-1)) & this.f38b;
        }
        this.f38b = i2;
        k();
    }

    public void n(String str) {
        this.f40d.d(str);
    }
}
