package a;

import a.b;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;
import cc.xposed.R;
import com.jozein.xedgepro.ApplicationMain;
import f.h0;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class z extends p {
    private ContentObserver R = null;

    class a extends ContentObserver {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ ContentResolver f60a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final /* synthetic */ b f61b;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        a(Handler handler, ContentResolver contentResolver, b bVar) {
            super(handler);
            this.f60a = contentResolver;
            this.f61b = bVar;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            z zVar = z.this;
            int i = zVar.J;
            zVar.J = Settings.System.getInt(this.f60a, p.Q, i);
            z zVar2 = z.this;
            int i2 = i ^ zVar2.J;
            if (i2 == 0) {
                return;
            }
            if ((i2 & 2) != 0) {
                this.f61b.a(zVar2.p(1));
            } else if ((i2 & 1) != 0) {
                this.f61b.b(zVar2.p(0));
            }
        }
    }

    public interface b {
        void a(boolean z);

        void b(boolean z);
    }

    public static boolean e0() {
        if (Build.VERSION.SDK_INT < 24) {
            return false;
        }
        String str = p.P;
        return !new File(str).exists() && new File(str.replace("user_de/0", "data")).exists() && h0(true);
    }

    private static boolean h0(boolean z) {
        try {
            for (String str : j.F) {
                String strReplace = str.replace("user_de/0", "data");
                String str2 = z ? strReplace : str;
                if (!z) {
                    str = strReplace;
                }
                f.r.j(str2, str);
            }
            return true;
        } catch (Throwable th) {
            f.v.d(th);
            return false;
        }
    }

    private void j0() {
        if (!this.N) {
            return;
        }
        f.p pVarA = A();
        pVarA.j();
        pVarA.d(4).d(this.K).C();
        pVarA.d(6);
        for (int i : this.E.f21b) {
            pVarA.d(i);
        }
        pVarA.C();
        pVarA.d(7);
        for (int i2 : this.L) {
            pVarA.d(i2);
        }
        pVarA.C();
        pVarA.d(10).d(this.M.length);
        for (int i3 : this.M) {
            pVarA.d(i3);
        }
        pVarA.C();
        O(pVarA, 0, this.D);
        O(pVarA, 1, this.E.f20a);
        O(pVarA, 2, this.F);
        O(pVarA, 3, this.G);
        int i4 = 0;
        while (true) {
            a.b[] bVarArr = this.H;
            if (i4 >= bVarArr.length) {
                break;
            }
            a.b bVar = bVarArr[i4];
            if (bVar != null && bVar.z != 0) {
                pVarA.d(8).d(i4).f(bVar).C();
            }
            i4++;
        }
        int i5 = 0;
        while (true) {
            b.q1[] q1VarArr = this.I;
            if (i5 >= q1VarArr.length) {
                this.E.o(pVarA);
                pVarA.n();
                this.N = false;
                return;
            } else {
                if (q1VarArr[i5] != null) {
                    pVarA.d(9).d(i5).f(this.I[i5]).C();
                }
                i5++;
            }
        }
    }

    private void k0(Context context, int i, int i2) {
        try {
            j0();
            Intent intent = new Intent();
            new f.m(intent).j().d(7).d(i).d(i2).l();
            c(context, intent);
        } catch (Throwable th) {
            x0(context, th);
        }
    }

    public static boolean w0() {
        boolean z;
        String str;
        int i = 0;
        try {
            str = f.l.l;
        } catch (Throwable unused) {
        }
        if ((f.r.k(str) & 1) == 0) {
            new File(str).setExecutable(true, false);
            z = true;
        } else {
            z = false;
        }
        String str2 = p.P;
        if (new File(str2).exists()) {
            try {
                if ((f.r.k(str2) & 4) == 0) {
                    String[] strArr = j.F;
                    int length = strArr.length;
                    while (true) {
                        if (i >= length) {
                            break;
                        }
                        String str3 = strArr[i];
                        if (!f.r.h(str3)) {
                            f.v.c("Failed to set world readable: " + str3);
                            break;
                        }
                        i++;
                        z = true;
                    }
                    f.v.c("Set readable.");
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
        return z;
    }

    private void x0(Context context, Throwable th) {
        f.v.d(th);
        Toast.makeText(context, context.getString(R.string.save_failed) + th.getMessage(), 0).show();
    }

    @Override // a.p
    public void X(Context context, int i, boolean z) {
        int i2 = z ? 1 : 2;
        a.b m2Var = i != 0 ? i != 1 ? i != 2 ? null : new b.m2(i2) : new b.p0(i2) : new b.e1(i2);
        if (m2Var != null) {
            a.b.t(context, m2Var);
        }
    }

    @Override // a.p, a.r
    protected void b(Intent intent) {
        f.v.d(new UnsupportedOperationException("Should not used!"));
    }

    @Override // a.p
    public void c0(Context context, int i) {
        f.v.d(new UnsupportedOperationException("Use clearScreenFilterProgress(context) instead."));
    }

    @Override // a.p, a.r
    public void d(Context context, Handler handler) {
        f.v.d(new UnsupportedOperationException("Use startReceive(context, listener) instead."));
    }

    public void d0(Context context, int i) {
        this.E.c(i);
        try {
            j0();
        } catch (Throwable th) {
            x0(context, th);
        }
    }

    public boolean f0(Context context, String str) {
        int iD;
        String strTrim = str.trim();
        int length = strTrim.length();
        if (length >= 4 && strTrim.charAt(0) == '#') {
            if (strTrim.endsWith(" 1")) {
                int iD2 = p.D(strTrim.substring(1, length - 2));
                if (iD2 > 0) {
                    o0(context, iD2, true);
                    return true;
                }
            } else if (strTrim.endsWith(" 0") && (iD = p.D(strTrim.substring(1, length - 2))) > 0) {
                o0(context, iD, false);
                return true;
            }
        }
        return false;
    }

    public boolean g0(int i) {
        return ((1 << i) & u(24)) != 0;
    }

    public void i0(Context context, int i) {
        a0(i, false);
        P(1, i, 0, this.B);
        P(1, i, 1, this.B);
        P(1, i, 2, this.B);
        this.E.n(i);
        try {
            j0();
            Intent intent = new Intent();
            new f.m(intent).j().d(6).d(i).d(0).l();
            c(context, intent);
            Intent intent2 = new Intent();
            new f.m(intent2).j().d(1).d(i).d(0).f(this.B).l();
            c(context, intent2);
            Intent intent3 = new Intent();
            new f.m(intent3).j().d(1).d(i).d(1).f(this.B).l();
            c(context, intent3);
            Intent intent4 = new Intent();
            new f.m(intent4).j().d(1).d(i).d(2).f(this.B).l();
            c(context, intent4);
        } catch (Throwable th) {
            x0(context, th);
        }
    }

    public void l0(Context context, int i, int i2, int i3, a.b bVar) {
        if (bVar.z == 1 && (i == 2 || i == 3 || i == 8)) {
            bVar = this.B;
        }
        P(i, i2, i3, bVar);
        try {
            j0();
            Intent intent = new Intent();
            new f.m(intent).j().d(i).d(i2).d(i3).f(bVar).l();
            c(context, intent);
        } catch (Throwable th) {
            x0(context, th);
        }
    }

    public void m0(Context context, int i, a.b bVar) {
        R(i, bVar);
        try {
            j0();
            Intent intent = new Intent();
            new f.m(intent).j().d(8).d(i).f(bVar).l();
            c(context, intent);
        } catch (Throwable th) {
            x0(context, th);
        }
    }

    public void n0(Context context, int i, b.q1 q1Var) {
        U(i, q1Var);
        try {
            j0();
            Intent intent = new Intent();
            new f.m(intent).j().d(9).d(i).f(q1Var).l();
            c(context, intent);
        } catch (Throwable th) {
            x0(context, th);
        }
    }

    public void o0(Context context, int i, boolean z) {
        int i2 = 1;
        if (i >= 32) {
            int iU = u(21);
            int i3 = 1 << (i - 32);
            s0(context, 21, z ? i3 | iU : (i3 ^ (-1)) & iU);
            return;
        }
        V(i, z);
        try {
            j0();
            Intent intent = new Intent();
            f.m mVarD = new f.m(intent).j().d(4).d(i);
            if (!z) {
                i2 = 0;
            }
            mVarD.d(i2).l();
            c(context, intent);
        } catch (Throwable th) {
            x0(context, th);
        }
    }

    public void p0(Context context, int i) {
        if (i == 0) {
            i = -1;
        }
        s0(context, 18, i);
    }

    public void q0(Context context, int i, boolean z) {
        int iU = u(24);
        int i2 = 1 << i;
        s0(context, 24, z ? i2 | iU : (i2 ^ (-1)) & iU);
    }

    public void r0(Context context, int i, float f2) {
        Y(i, f2);
        k0(context, i, q.a(f2));
    }

    public void s0(Context context, int i, int i2) {
        Z(i, i2);
        k0(context, i, i2);
    }

    public void t0(Context context, int i, boolean z) {
        a0(i, z);
        try {
            j0();
            Intent intent = new Intent();
            new f.m(intent).j().d(6).d(i).d(z ? 1 : 0).l();
            c(context, intent);
        } catch (Throwable th) {
            x0(context, th);
        }
    }

    public boolean u0(Context context, int[] iArr) {
        if (iArr == null) {
            f.v.c("colors == null");
            return false;
        }
        b0(iArr);
        try {
            j0();
            Intent intent = new Intent();
            f.m mVarD = new f.m(intent).j().d(10).d(this.M.length);
            for (int i : this.M) {
                mVarD.d(i);
            }
            mVarD.l();
            c(context, intent);
            return true;
        } catch (Throwable th) {
            x0(context, th);
            return false;
        }
    }

    public void v0(Context context, int i) {
        if (!ApplicationMain.isModuleActivated()) {
            f.v.c("Module not activated.");
            return;
        }
        s0(context, 1, i);
        b.s0.B(context, 11);
        this.J &= -241;
        this.N = true;
    }

    public void y0(Context context, b bVar) {
        ContentResolver contentResolver = context.getContentResolver();
        int i = this.J;
        String str = p.Q;
        this.J = Settings.System.getInt(contentResolver, str, i);
        if (bVar == null) {
            return;
        }
        this.R = new a(h0.d(), contentResolver, bVar);
        contentResolver.registerContentObserver(Settings.System.getUriFor(str), true, this.R);
    }
}
