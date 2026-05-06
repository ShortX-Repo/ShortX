package a;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import com.jozein.xedgepro.ui.ActivityPerformAction;
import f.q;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class j implements f.l {
    public static final String A;
    public static final String B;
    public static final int C;
    public static final String D;
    public static final String E;
    public static final String[] F;
    private static final String[] G;
    private static final String[] H;
    private static File[] I;
    private static boolean J;
    public static final String z;

    static {
        StringBuilder sb = new StringBuilder();
        String str = f.l.v;
        sb.append(str);
        sb.append(".IMPORT");
        z = sb.toString();
        StringBuilder sb2 = new StringBuilder();
        String str2 = f.l.w;
        sb2.append(str2);
        sb2.append(".IMPORT_REPLY");
        A = sb2.toString();
        String strReplace = f.l.l.replace(str2, str);
        B = strReplace;
        C = strReplace.length();
        StringBuilder sb3 = new StringBuilder();
        String str3 = f.l.j;
        sb3.append(str3);
        sb3.append(".READ_FILES");
        D = sb3.toString();
        E = str3 + ".READ_FILES_REPLY";
        String str4 = f.l.m;
        String str5 = f.l.n;
        String str6 = f.l.o;
        F = new String[]{str4, str5, str6, f.l.p, f.l.q};
        G = new String[]{str4, str5, str6};
        H = new String[]{"prefs/collection", "prefs/tiles"};
        I = null;
        J = false;
    }

    private static void b(Context context, File file) {
        byte[] bArrJ;
        if (file.exists() && (bArrJ = new q.b(file).j()) != null) {
            context.sendBroadcast(new Intent(A).setPackage(f.l.w).putExtra("name", file.getPath().substring(C)).putExtra("buffer", bArrJ));
        }
    }

    public static void c(Context context) {
        if (I == null) {
            I = new File[F.length];
            int i = 0;
            if (f.l.r) {
                while (true) {
                    String[] strArr = F;
                    if (i < strArr.length) {
                        I[i] = new File(strArr[i].replace(f.l.w, f.l.v));
                        i++;
                    }
                }
            } else {
                while (true) {
                    String[] strArr2 = F;
                    if (i < strArr2.length) {
                        I[i] = new File(strArr2[i]);
                        i++;
                    }
                }
            }
        }
        try {
            d(context, I);
        } finally {
            context.sendBroadcast(new Intent(A).setPackage(f.l.w));
        }
    }

    private static void d(Context context, File[] fileArr) {
        if (fileArr == null) {
            return;
        }
        for (File file : fileArr) {
            if (file.exists()) {
                if (file.isDirectory()) {
                    d(context, file.listFiles());
                } else {
                    b(context, file);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void e(boolean z2, Context context) {
        Intent intent = new Intent(E);
        intent.setPackage("android");
        if (z2) {
            intent.putExtra("extra", 1);
            context.sendBroadcast(intent);
        }
        try {
            for (String str : G) {
                j(context, str);
            }
        } finally {
            intent.putExtra("extra", z2 ? 3 : 2);
            context.sendBroadcast(intent);
        }
    }

    public static void f(Context context) {
        Intent intent = new Intent(z);
        intent.setFlags(402653184);
        String str = f.l.v;
        intent.setComponent(new ComponentName(str, ActivityPerformAction.class.getName().replace(f.l.w, str)));
        context.startActivity(intent);
    }

    public static void g(final Context context, final boolean z2) {
        f.d.b(new Runnable() { // from class: a.i
            @Override // java.lang.Runnable
            public final void run() {
                j.e(z2, context);
            }
        });
    }

    public static void h(Context context) {
        try {
            if (J || !m(context)) {
                return;
            }
            J = true;
            g(context, false);
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    private static void i(Context context, File file) {
        if (file == null || !file.exists()) {
            return;
        }
        String absolutePath = file.getAbsolutePath();
        for (String str : H) {
            if (absolutePath.endsWith(str)) {
                return;
            }
        }
        byte[] bArrJ = new q.b(file).j();
        if (bArrJ == null) {
            return;
        }
        context.sendBroadcast(new Intent(E).setPackage("android").putExtra("name", absolutePath).putExtra("buffer", bArrJ).putExtra("last_modified", file.lastModified()));
        if (bArrJ.length > 65536) {
            f.v.c("sending " + absolutePath + ", size: " + bArrJ.length);
        }
    }

    private static void j(Context context, String str) {
        if (str != null) {
            File file = new File(str);
            if (file.isDirectory()) {
                l(context, file.listFiles());
            } else {
                i(context, file);
            }
        }
    }

    public static void k(Context context, String str) {
        try {
            if (m(context)) {
                j(context, str);
            }
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    private static void l(Context context, File[] fileArr) {
        if (fileArr == null) {
            return;
        }
        for (File file : fileArr) {
            if (file.exists()) {
                if (file.isDirectory()) {
                    l(context, file.listFiles());
                } else {
                    i(context, file);
                }
            }
        }
    }

    public static boolean m(Context context) {
        return context != null && p.q(context, 8);
    }
}
