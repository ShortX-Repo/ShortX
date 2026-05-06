package com.jozein.xedgepro.xposed;

import a.g;
import android.content.Context;
import android.content.Intent;
import f.q;
import java.util.ArrayList;
import java.util.Hashtable;

/* JADX INFO: loaded from: classes.dex */
class w {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final Hashtable<String, f.o> f253a = new Hashtable<>();

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static boolean f254b = false;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static a f255c = null;

    private static class a extends q.a implements Runnable {
        private final q.a A;
        private final Context B;
        private final ArrayList<String> z = new ArrayList<>();

        a(q.a aVar, Context context) {
            this.A = aVar;
            this.B = context;
        }

        @Override // f.q.a
        public f.q a(String str) {
            f.o oVarF = w.f(str);
            if (oVarF != null) {
                return oVarF;
            }
            f.o oVar = (f.o) g.a.f(this.B).g(str);
            if (oVar != null) {
                w.e(str, oVar);
                return oVar;
            }
            this.z.add(str);
            b(this);
            q.a aVar = this.A;
            return aVar != null ? aVar.a(str) : super.a(str);
        }

        void b(Runnable runnable) {
            g.a.f(this.B).h(runnable);
        }

        @Override // java.lang.Runnable
        public void run() {
            for (String str : this.z) {
                f.o oVar = (f.o) g.a.f(this.B).g(str);
                if (oVar != null) {
                    w.e(str, oVar);
                }
            }
            this.z.clear();
        }
    }

    static void c() {
        f253a.clear();
        f.q.b();
    }

    static void d(Intent intent) {
        String stringExtra = intent.getStringExtra("name");
        if (stringExtra != null) {
            e(stringExtra, new f.o(stringExtra, intent.getLongExtra("last_modified", 0L), intent.getByteArrayExtra("buffer")));
            f254b = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void e(String str, f.o oVar) {
        f253a.put(str, oVar);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static f.o f(String str) {
        return f253a.get(str);
    }

    static boolean g() {
        return f254b;
    }

    static void h(Context context) {
        if (f255c == null) {
            a aVar = new a(f.q.e(), context);
            f255c = aVar;
            f.q.k(aVar);
        }
    }
}
