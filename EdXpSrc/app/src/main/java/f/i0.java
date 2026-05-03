package f;

import a.b;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.Toast;
import cc.xposed.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class i0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final ArrayList<Process> f450a = new ArrayList<>();

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static volatile long f451b = 0;

    public static class a {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private final String f452a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private final int f453b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        private int f454c = 0;

        public a(String str) {
            this.f452a = str;
            this.f453b = str.length();
        }

        private String a(int i, int i2) {
            int i3;
            char cCharAt;
            StringBuilder sb = new StringBuilder(i2 - i);
            while (true) {
                boolean z = false;
                while (i < i2) {
                    i3 = i + 1;
                    cCharAt = this.f452a.charAt(i);
                    char c2 = '\\';
                    if (z) {
                        if (cCharAt == '\"') {
                            c2 = '\"';
                        } else if (cCharAt == '\'') {
                            c2 = '\'';
                        } else if (cCharAt != '\\') {
                            if (cCharAt == 'b') {
                                c2 = '\b';
                            } else if (cCharAt == 'f') {
                                c2 = '\f';
                            } else if (cCharAt == 'n') {
                                c2 = '\n';
                            } else if (cCharAt == 'r') {
                                c2 = '\r';
                            } else {
                                if (cCharAt != 't') {
                                    break;
                                }
                                c2 = '\t';
                            }
                        }
                        sb.append(c2);
                        z = false;
                    } else if (cCharAt == '\\') {
                        z = true;
                    } else {
                        sb.append(cCharAt);
                    }
                    i = i3;
                }
                return sb.toString();
                sb.append(cCharAt);
                i = i3;
            }
        }

        public String[] b() {
            ArrayList arrayList = new ArrayList();
            while (true) {
                String strC = c();
                if (strC == null) {
                    return (String[]) arrayList.toArray(new String[arrayList.size()]);
                }
                arrayList.add(strC);
            }
        }

        public String c() {
            int i;
            char cCharAt = 0;
            while (true) {
                int i2 = this.f454c;
                if (i2 >= this.f453b || (cCharAt = this.f452a.charAt(i2)) > ' ') {
                    break;
                }
                this.f454c++;
            }
            int i3 = this.f454c;
            if (i3 >= this.f453b) {
                return null;
            }
            if (cCharAt == '\"' || cCharAt == '\'') {
                this.f454c = i3 + 1;
                boolean z = false;
                boolean z2 = false;
                while (true) {
                    int i4 = this.f454c;
                    if (i4 >= this.f453b) {
                        break;
                    }
                    String str = this.f452a;
                    this.f454c = i4 + 1;
                    char cCharAt2 = str.charAt(i4);
                    if (cCharAt2 != '\\') {
                        if (cCharAt2 == cCharAt && !z) {
                            break;
                        }
                        z = false;
                    } else {
                        z = !z;
                        z2 = true;
                    }
                }
                i3++;
                i = this.f454c - 1;
                if (z2 && cCharAt == '\"') {
                    return a(i3, i);
                }
            } else {
                while (true) {
                    int i5 = this.f454c;
                    if (i5 >= this.f453b || this.f452a.charAt(i5) <= ' ') {
                        break;
                    }
                    this.f454c++;
                }
                i = this.f454c;
            }
            if (i3 < i) {
                return this.f452a.substring(i3, i);
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class b implements Runnable {
        private final Context A;
        private final Handler B;
        private final boolean C;
        private final boolean D;
        private final long E = SystemClock.uptimeMillis();
        private final b.a0 z;

        b(b.a0 a0Var, Context context, Handler handler, boolean z, boolean z2) {
            this.z = a0Var;
            this.A = context;
            this.B = handler;
            this.C = z;
            this.D = z2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void b(CharSequence charSequence) {
            Toast.makeText(this.A, charSequence, 0).show();
        }

        private void d(String str) {
        }

        void c(final CharSequence charSequence) {
            Handler handler;
            if (this.A == null || (handler = this.B) == null) {
                return;
            }
            handler.post(new Runnable() { // from class: f.j0
                @Override // java.lang.Runnable
                public final void run() {
                    this.z.b(charSequence);
                }
            });
        }

        void e(Throwable th) {
            String message = th.getMessage();
            if ((th instanceof IOException) && message != null && (message.contains("EPIPE") || message.contains("EBADF") || message.contains("Stream closed"))) {
                v.c(message);
                return;
            }
            c(message);
            v.d(th);
            d("Execution failed: " + this.z.K + ", " + th.toString());
        }

        @Override // java.lang.Runnable
        public void run() {
            Context context;
            CharSequence charSequenceN;
            if (this.E < i0.f451b) {
                return;
            }
            try {
                b.a0 a0Var = this.z;
                Process processD = i0.d(a0Var.J ? "su" : "sh", a0Var.A());
                if (processD == null) {
                    v.c("Empty shell: " + this.z.K);
                    return;
                }
                boolean z = false;
                synchronized (i0.f450a) {
                    if (this.E < i0.f451b) {
                        z = true;
                    } else {
                        i0.f450a.add(processD);
                    }
                }
                if (z) {
                    processD.destroy();
                    return;
                }
                try {
                    if (processD.waitFor() != 0) {
                        String strE = i0.e(processD.getErrorStream());
                        v.c("Execution failed: " + this.z.K + ", \n" + strE);
                        d("Execution failed: " + this.z.K + ", " + strE);
                        if (this.A != null) {
                            charSequenceN = this.A.getString(R.string.run_command_failed) + " " + strE;
                            c(charSequenceN);
                        }
                    } else {
                        String strE2 = i0.e(processD.getInputStream());
                        v.c("Console output: \n" + strE2);
                        if (this.D) {
                            charSequenceN = strE2;
                            if (this.C) {
                                charSequenceN = strE2;
                                if (this.A != null) {
                                    charSequenceN = ((Object) this.z.n(this.A)) + "\n" + strE2;
                                }
                            }
                        } else if (this.C && (context = this.A) != null) {
                            charSequenceN = this.z.n(context);
                        }
                        c(charSequenceN);
                    }
                    synchronized (i0.f450a) {
                        i0.f450a.remove(processD);
                    }
                } catch (Throwable th) {
                    try {
                        e(th);
                        synchronized (i0.f450a) {
                            i0.f450a.remove(processD);
                        }
                    } catch (Throwable th2) {
                        synchronized (i0.f450a) {
                            i0.f450a.remove(processD);
                            throw th2;
                        }
                    }
                }
            } catch (Throwable th3) {
                e(th3);
            }
        }
    }

    public static void c(b.a0 a0Var, Context context, Handler handler, boolean z, boolean z2) {
        if (a0Var == null) {
            v.d(new NullPointerException("Command == null"));
        } else {
            d.b(new b(a0Var, context, handler, z, z2));
        }
    }

    static Process d(String str, String[] strArr) throws IOException {
        if (strArr == null || strArr.length == 0) {
            return null;
        }
        String[] strArrF = f(strArr);
        if (strArrF.length == 0) {
            return null;
        }
        try {
            Process processExec = Runtime.getRuntime().exec(str);
            OutputStream outputStream = processExec.getOutputStream();
            for (String str2 : strArrF) {
                outputStream.write((str2 + '\n').getBytes(p0.z));
                outputStream.flush();
            }
            outputStream.write("exit\n".getBytes(p0.z));
            outputStream.flush();
            return processExec;
        } catch (IOException e2) {
            if (strArrF.length != 1 || !"sh".equals(str) || !o0.l()) {
                throw e2;
            }
            return Runtime.getRuntime().exec(new a(strArrF[0]).b());
        }
    }

    static String e(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line = bufferedReader.readLine();
        if (line != null) {
            while (true) {
                sb.append(line);
                line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    private static String[] f(String[] strArr) {
        ArrayList arrayList = new ArrayList(strArr.length);
        for (String str : strArr) {
            if (str != null) {
                String strTrim = str.trim();
                if (strTrim.length() > 0 && strTrim.charAt(0) != '#') {
                    arrayList.add(strTrim);
                }
            }
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    public static void g() {
        f451b = SystemClock.uptimeMillis();
        ArrayList<Process> arrayList = f450a;
        synchronized (arrayList) {
            if (arrayList.size() > 0) {
                Iterator<Process> it = arrayList.iterator();
                while (it.hasNext()) {
                    try {
                        it.next().destroy();
                    } catch (Throwable th) {
                        v.d(th);
                    }
                }
                f450a.clear();
            }
        }
    }
}
