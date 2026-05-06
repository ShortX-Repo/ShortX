package f;

import android.os.SystemClock;
import android.util.Log;
import java.util.Arrays;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class v implements l {
    private static b z;

    private static class b {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private final String[] f493a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private int f494b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        private int f495c;

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        private String f496d;

        private b() {
            this.f493a = new String[1024];
            this.f494b = 0;
            this.f495c = 0;
            this.f496d = null;
        }

        private synchronized void b(String str) {
            this.f496d = null;
            String[] strArr = this.f493a;
            int i = this.f494b;
            strArr[i] = str;
            this.f495c++;
            int i2 = i + 1;
            this.f494b = i2;
            if (i2 >= strArr.length) {
                this.f494b = 0;
            }
        }

        void a(String str) {
            long jElapsedRealtime = SystemClock.elapsedRealtime();
            int i = (int) (jElapsedRealtime % 1000);
            long j = jElapsedRealtime / 1000;
            int i2 = (int) (j & 60);
            long j2 = j / 60;
            int i3 = (int) (j2 % 60);
            long j3 = j2 / 60;
            int i4 = (int) (j3 / 24);
            b(String.format(Locale.ENGLISH, "%03d-%02d:%02d:%02d.%03d  %s\n", Integer.valueOf(i4), Integer.valueOf((int) (j3 % 24)), Integer.valueOf(i3), Integer.valueOf(i2), Integer.valueOf(i), str));
        }

        synchronized String c() {
            if (this.f495c == 0) {
                return "";
            }
            if (this.f496d == null) {
                StringBuilder sb = new StringBuilder(1024);
                int length = this.f495c;
                String[] strArr = this.f493a;
                if (length > strArr.length) {
                    length = strArr.length;
                    sb.append("...\n");
                }
                int i = this.f494b - length;
                for (int i2 = 0; i2 < length; i2++) {
                    int length2 = i + i2;
                    if (length2 < 0) {
                        length2 += this.f493a.length;
                    }
                    sb.append(this.f493a[length2]);
                }
                this.f496d = sb.toString();
            }
            return this.f496d;
        }
    }

    public static void a() {
        if (z == null) {
            z = new b();
        }
    }

    public static String b() {
        b bVar = z;
        return bVar != null ? bVar.c() : "";
    }

    public static void c(String str) {
        Log.i(l.x, str);
        if (z != null) {
            f(str);
        }
    }

    public static void d(Throwable th) {
        Log.e(l.x, "Caught throwable:", th);
        if (z != null) {
            try {
                StackTraceElement stackTraceElement = new Throwable().getStackTrace()[1];
                f(th.getClass().getName() + ": " + th.getMessage() + " - at " + stackTraceElement.getClassName() + '.' + stackTraceElement.getMethodName());
            } catch (Throwable unused) {
            }
        }
    }

    public static void e(Object... objArr) {
        int length = objArr.length;
        String string = length == 0 ? "" : length == 1 ? objArr[0] != null ? objArr[0].toString() : "null" : Arrays.deepToString(objArr);
        Log.i(l.x, string);
        if (z != null) {
            f(string);
        }
    }

    public static void f(String str) {
        b bVar = z;
        if (bVar != null) {
            bVar.a(str);
        }
    }
}
