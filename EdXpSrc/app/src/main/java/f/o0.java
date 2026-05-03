package f;

import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.SparseArray;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
public class o0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final int[] f473a = new int[0];

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static int f474b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static int[] f475c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static boolean f476d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static SparseArray<UserHandle> f477e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static boolean f478f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static Method f479g;
    private static boolean h;
    private static Method i;

    public static class a {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private final int f480a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private int[] f481b;

        a(Context context) {
            int i = o0.f474b;
            this.f480a = i;
            this.f481b = null;
            if (o0.f476d) {
                int[] iArrE = o0.e(context);
                this.f481b = iArrE;
                if (iArrE != null && iArrE.length == 1 && iArrE[0] == i) {
                    this.f481b = o0.f473a;
                }
            }
        }

        public boolean a(int i) {
            int[] iArr = this.f481b;
            if (iArr == null) {
                return true;
            }
            if (iArr.length == 0) {
                return i == this.f480a;
            }
            for (int i2 : iArr) {
                if (i2 == i) {
                    return true;
                }
            }
            return false;
        }

        public boolean b() {
            return this.f481b == null;
        }
    }

    static {
        int iM = m();
        f474b = iM;
        f475c = new int[]{iM};
        f476d = false;
        int i2 = Build.VERSION.SDK_INT;
        f478f = i2 >= 24;
        f479g = null;
        h = i2 >= 24;
        i = null;
    }

    public static int d() {
        return f474b;
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x0053 A[Catch: all -> 0x006f, TryCatch #1 {all -> 0x006f, blocks: (B:4:0x0006, B:6:0x0010, B:16:0x004b, B:18:0x0053, B:20:0x005f, B:15:0x0042, B:9:0x0015, B:11:0x001a, B:12:0x002e), top: B:29:0x0006, inners: #0 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static int[] e(android.content.Context r7) {
        /*
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 21
            if (r0 < r1) goto L73
            java.lang.String r0 = "user"
            java.lang.Object r7 = r7.getSystemService(r0)     // Catch: java.lang.Throwable -> L6f
            android.os.UserManager r7 = (android.os.UserManager) r7     // Catch: java.lang.Throwable -> L6f
            if (r7 == 0) goto L73
            boolean r0 = f.o0.h     // Catch: java.lang.Throwable -> L6f
            r1 = 0
            if (r0 == 0) goto L4b
            java.lang.reflect.Method r0 = f.o0.i     // Catch: java.lang.Throwable -> L41
            r2 = 1
            if (r0 != 0) goto L2e
            f.f0 r0 = f.f0.n()     // Catch: java.lang.Throwable -> L41
            java.lang.Class<android.os.UserManager> r3 = android.os.UserManager.class
            java.lang.String r4 = "getEnabledProfileIds"
            java.lang.Class[] r5 = new java.lang.Class[r2]     // Catch: java.lang.Throwable -> L41
            java.lang.Class r6 = java.lang.Integer.TYPE     // Catch: java.lang.Throwable -> L41
            r5[r1] = r6     // Catch: java.lang.Throwable -> L41
            java.lang.reflect.Method r0 = r0.h(r3, r4, r5)     // Catch: java.lang.Throwable -> L41
            f.o0.i = r0     // Catch: java.lang.Throwable -> L41
        L2e:
            java.lang.reflect.Method r0 = f.o0.i     // Catch: java.lang.Throwable -> L41
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch: java.lang.Throwable -> L41
            int r3 = f.o0.f474b     // Catch: java.lang.Throwable -> L41
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch: java.lang.Throwable -> L41
            r2[r1] = r3     // Catch: java.lang.Throwable -> L41
            java.lang.Object r0 = r0.invoke(r7, r2)     // Catch: java.lang.Throwable -> L41
            int[] r0 = (int[]) r0     // Catch: java.lang.Throwable -> L41
            return r0
        L41:
            r0 = move-exception
            f.o0.h = r1     // Catch: java.lang.Throwable -> L6f
            java.lang.String r0 = r0.toString()     // Catch: java.lang.Throwable -> L6f
            f.v.c(r0)     // Catch: java.lang.Throwable -> L6f
        L4b:
            int r0 = f.o0.f474b     // Catch: java.lang.Throwable -> L6f
            int r2 = m()     // Catch: java.lang.Throwable -> L6f
            if (r0 != r2) goto L73
            java.util.List r7 = r7.getUserProfiles()     // Catch: java.lang.Throwable -> L6f
            int r0 = r7.size()     // Catch: java.lang.Throwable -> L6f
            int[] r2 = new int[r0]     // Catch: java.lang.Throwable -> L6f
        L5d:
            if (r1 >= r0) goto L6e
            java.lang.Object r3 = r7.get(r1)     // Catch: java.lang.Throwable -> L6f
            android.os.UserHandle r3 = (android.os.UserHandle) r3     // Catch: java.lang.Throwable -> L6f
            int r3 = r3.hashCode()     // Catch: java.lang.Throwable -> L6f
            r2[r1] = r3     // Catch: java.lang.Throwable -> L6f
            int r1 = r1 + 1
            goto L5d
        L6e:
            return r2
        L6f:
            r7 = move-exception
            f.v.d(r7)
        L73:
            int[] r7 = f.o0.f475c
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: f.o0.e(android.content.Context):int[]");
    }

    public static a f(Context context) {
        return new a(context);
    }

    public static int g(int i2) {
        if (Build.VERSION.SDK_INT < 21 || i2 == 0 || i2 == f474b) {
            return 0;
        }
        return i2 < 500 ? 1 : 2;
    }

    public static int h(int i2) {
        return i2 / 100000;
    }

    public static boolean i(Context context, int i2) {
        UserManager userManager;
        if (i2 == f474b || !f476d || Build.VERSION.SDK_INT < 21) {
            return true;
        }
        if (f478f && (userManager = (UserManager) context.getSystemService("user")) != null) {
            try {
                if (f479g == null) {
                    f0 f0VarN = f0.n();
                    Class<?> cls = Integer.TYPE;
                    f479g = f0VarN.h(UserManager.class, "isSameProfileGroup", cls, cls);
                }
                return ((Boolean) f479g.invoke(userManager, Integer.valueOf(i2), Integer.valueOf(f474b))).booleanValue();
            } catch (Throwable th) {
                f478f = false;
                v.c(th.toString());
            }
        }
        int[] iArrE = e(context);
        if (iArrE != null && iArrE.length > 0) {
            for (int i3 : iArrE) {
                if (i3 == i2) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean j(int i2) {
        return (Build.VERSION.SDK_INT < 21 || i2 == 0 || i2 == f474b) ? false : true;
    }

    public static boolean k(int[] iArr) {
        return iArr == null || iArr.length == 0 || (iArr.length == 1 && iArr[0] == f474b);
    }

    public static boolean l() {
        return Process.myUid() <= 1000;
    }

    public static int m() {
        return h(Process.myUid());
    }

    private static UserHandle n(int i2) {
        Parcel parcelObtain = Parcel.obtain();
        parcelObtain.writeInt(i2);
        UserHandle userHandle = new UserHandle(parcelObtain);
        parcelObtain.recycle();
        return userHandle;
    }

    public static void o(int i2) {
        String str;
        if (i2 == f474b) {
            return;
        }
        f474b = i2;
        f476d = true;
        f475c = new int[]{i2};
        if (i2 == 0) {
            str = "Switching to main user.";
        } else {
            str = "Switching to user " + i2 + ", multi-user not full supported.";
        }
        v.c(str);
    }

    public static UserHandle p() {
        return q(-1);
    }

    public static UserHandle q(int i2) {
        UserHandle userHandle;
        SparseArray<UserHandle> sparseArray = f477e;
        if (sparseArray != null && (userHandle = sparseArray.get(i2)) != null) {
            return userHandle;
        }
        UserHandle userHandleN = n(i2);
        SparseArray<UserHandle> sparseArrayClone = sparseArray != null ? sparseArray.clone() : new SparseArray<>();
        sparseArrayClone.append(i2, userHandleN);
        f477e = sparseArrayClone;
        return userHandleN;
    }

    public static UserHandle r() {
        return q(0);
    }
}
