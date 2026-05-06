package a;

import android.content.Context;
import android.os.Build;

/* JADX INFO: loaded from: classes.dex */
public class w {
    public static void a(z zVar, Context context) {
        m mVarB = m.b();
        try {
            if (mVarB.a(0) == 0) {
                b(zVar, context);
                mVarB.f(0, 1);
            }
            int iA = mVarB.a(2);
            if (iA < 80) {
                c(zVar, context, mVarB, iA);
                mVarB.f(2, 80);
            }
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    private static void b(z zVar, Context context) {
        zVar.d0(context, 24);
        zVar.d0(context, 25);
        zVar.d0(context, 4);
        zVar.d0(context, 3);
        zVar.d0(context, 187);
        zVar.d0(context, 82);
        zVar.d0(context, 79);
        zVar.o0(context, 4, true);
        zVar.o0(context, 6, true);
        zVar.o0(context, 7, true);
        zVar.o0(context, 8, true);
        zVar.o0(context, 24, true);
        zVar.o0(context, 27, true);
        zVar.s0(context, 7, 67);
        zVar.u0(context, new int[]{-3139818, -24576, -16089593, -16611119});
    }

    private static void c(z zVar, Context context, m mVar, int i) {
        f.v.c("New version: " + f.l.t);
        if (i < 32 && zVar.u(24) == 0) {
            zVar.s0(context, 7, zVar.u(7) | 64);
            zVar.s0(context, 24, 983055);
        }
        if (i < 47) {
            int iV = zVar.v(19, -1);
            zVar.s0(context, 30, iV);
            zVar.s0(context, 31, iV);
            zVar.s0(context, 33, zVar.v(2, -16764873));
        }
        if (i < 56) {
            zVar.s0(context, 34, zVar.o(5) ? 1 : 0);
            try {
                mVar.f(4, 1);
                f.v.c("set style");
            } catch (Throwable th) {
                f.v.d(th);
            }
            if (Build.VERSION.SDK_INT < 29 || mVar.a(1) != 0) {
                return;
            }
            try {
                mVar.f(1, 4);
                f.v.c("set auto dark mode.");
            } catch (Throwable th2) {
                f.v.d(th2);
            }
        }
    }
}
