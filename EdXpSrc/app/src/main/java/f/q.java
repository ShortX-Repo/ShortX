package f;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public abstract class q {
    public static final byte[] z = new byte[0];
    private static a A = new a();
    private static long B = 0;
    private static final Hashtable<String, Drawable.ConstantState> C = new Hashtable<>();
    private static final Hashtable<String, List<g.q>> D = new Hashtable<>();

    public static class a {
        public q a(String str) {
            return new b(str);
        }
    }

    public static class b extends q {
        private final File E;

        public b(File file) {
            this.E = file;
        }

        public b(String str) {
            this.E = new File(str);
        }

        @Override // f.q
        public boolean d() {
            return this.E.exists();
        }

        @Override // f.q
        public long f() {
            return this.E.lastModified();
        }

        @Override // f.q
        public byte[] j() {
            int length = (int) this.E.length();
            if (length == 0) {
                return q.z;
            }
            byte[] bArr = new byte[length];
            try {
                FileInputStream fileInputStream = new FileInputStream(this.E);
                try {
                    if (fileInputStream.read(bArr, 0, length) != length) {
                        v.d(new IOException("Error read: " + this.E.getPath()));
                    }
                    fileInputStream.close();
                    return bArr;
                } finally {
                }
            } catch (Throwable th) {
                q.i(th);
                return null;
            }
        }
    }

    public static void b() {
        C.clear();
        D.clear();
    }

    public static q c(String str) {
        return A.a(str);
    }

    public static a e() {
        return A;
    }

    public static List<g.q> g(String str) {
        List<g.q> list = D.get(str);
        if (list != null) {
            return list;
        }
        byte[] bArrJ = c(str).j();
        if (bArrJ == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        h hVar = new h();
        hVar.i(bArrJ);
        g.q qVar = new g.q(hVar);
        while (true) {
            arrayList.add(qVar);
            if (!hVar.t()) {
                break;
            }
            qVar = new g.q(hVar);
        }
        hVar.m();
        int size = arrayList.size();
        if (size != 0) {
            if (size != 1) {
                D.put(str, arrayList);
            } else {
                arrayList.clear();
            }
        }
        return arrayList;
    }

    public static Drawable h(String str) {
        Hashtable<String, Drawable.ConstantState> hashtable = C;
        Drawable.ConstantState constantState = hashtable.get(str);
        if (constantState != null) {
            return constantState.newDrawable();
        }
        byte[] bArrJ = c(str).j();
        if (bArrJ == null) {
            return null;
        }
        Drawable drawableCreateFromStream = BitmapDrawable.createFromStream(new ByteArrayInputStream(bArrJ), str);
        if (drawableCreateFromStream == null) {
            return drawableCreateFromStream;
        }
        g.g gVar = new g.g(drawableCreateFromStream);
        Drawable.ConstantState constantState2 = gVar.getConstantState();
        if (constantState2 != null) {
            hashtable.put(str, constantState2);
        }
        return gVar;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void i(Throwable th) {
        String string;
        long jUptimeMillis = SystemClock.uptimeMillis();
        long j = B;
        if (j == 0 || jUptimeMillis - j > 4000) {
            B = jUptimeMillis;
            String message = th.getMessage();
            if (message == null) {
                string = th.toString();
            } else {
                if (!message.contains("EACCES")) {
                    v.c(message);
                    return;
                }
                string = "Cannot access file!!!";
            }
            v.c(string);
        }
    }

    public static void k(a aVar) {
        if (aVar == null) {
            aVar = new a();
        }
        A = aVar;
    }

    public abstract boolean d();

    public abstract long f();

    public abstract byte[] j();
}
