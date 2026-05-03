package com.jozein.xedgepro.service;

import a.b;
import a.e;
import a.g;
import a.j;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.Process;
import android.util.Pair;
import android.widget.Toast;
import cc.xposed.R;
import com.jozein.xedgepro.service.BinderService;
import com.jozein.xedgepro.ui.ActivityPerformAction;
import f.h0;
import f.i0;
import f.l;
import f.m;
import f.o;
import f.o0;
import f.q;
import f.v;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class BinderService extends Service implements l {
    private static final String z = l.k + "REMOTE_ACCESS";
    public static final String A = l.j + ".action.COMMIT";
    private static final Parcelable B = Bundle.EMPTY;
    private static int C = 0;
    private static boolean D = false;

    public static class a extends g.b {

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        private final BinderService f79c;

        a(BinderService binderService) {
            this.f79c = binderService;
            int iE = e();
            if (iE <= 0 || iE == Process.myUid()) {
                return;
            }
            b.s0.A(binderService, "BinderService running on uid " + Process.myUid() + ", but package uid " + iE + ".");
        }

        public static boolean g(Context context, ServiceConnection serviceConnection) {
            Intent intent = new Intent(BinderService.z);
            String str = l.j;
            intent.setComponent(new ComponentName(str, BinderService.class.getName()));
            intent.setPackage(str);
            intent.addFlags(32);
            return context.bindService(intent, serviceConnection, 1);
        }

        @Override // a.g
        public Parcelable a(String str) {
            q.b bVar = new q.b(str);
            return new o(str, bVar.f(), bVar.d() ? bVar.j() : null);
        }

        @Override // a.g
        public void b(final String str, final Parcelable parcelable) {
            final Context applicationContext = this.f79c.getApplicationContext();
            byte b2 = -1;
            try {
                int iHashCode = str.hashCode();
                if (iHashCode != -1796252732) {
                    if (iHashCode == 270141475 && str.equals("COMMIT_COMMAND")) {
                        b2 = 0;
                    }
                } else if (str.equals("STOP_ALL")) {
                    b2 = 1;
                }
                if (b2 == 0) {
                    BinderService.l(applicationContext, (Bundle) parcelable);
                } else if (b2 != 1) {
                    h0.f(h0.a(), new Runnable() { // from class: b.b
                        @Override // java.lang.Runnable
                        public final void run() {
                            BinderService.e(applicationContext, str, parcelable);
                        }
                    });
                } else {
                    BinderService.E();
                }
            } catch (Throwable th) {
                v.d(th);
            }
        }
    }

    private static void A(Context context) {
        j.h(context);
    }

    public static void B(boolean z2) {
        C = z2 ? 2 : 0;
    }

    public static void C(Context context, Intent intent) {
        intent.setComponent(new ComponentName(l.j, ActivityPerformAction.class.getName()));
        intent.setFlags(402653184);
        try {
            context.startActivity(intent);
        } catch (Throwable th) {
            v.d(th);
        }
    }

    public static void D(Context context) {
        if (D) {
            D = false;
            g(context, "STOP_ALL", B);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void E() {
        i0.g();
    }

    private static Bundle F(b bVar) {
        Bundle bundle = new Bundle();
        m mVar = new m(bundle);
        mVar.j();
        bVar.y(mVar);
        mVar.l();
        return bundle;
    }

    static void G(final Context context, final int i) {
        h0.f(h0.a(), new Runnable() { // from class: b.a
            @Override // java.lang.Runnable
            public final void run() {
                BinderService.x(context, i);
            }
        });
    }

    private static Intent H(String str, Parcelable parcelable) {
        return new Intent(A).setPackage(l.j).putExtra("action", str).putExtra("data", parcelable);
    }

    private static Intent I(Pair<String, Parcelable>[] pairArr) {
        if (pairArr.length == 1) {
            return H((String) pairArr[0].first, (Parcelable) pairArr[0].second);
        }
        String[] strArr = new String[pairArr.length];
        Parcelable[] parcelableArr = new Parcelable[pairArr.length];
        for (int i = 0; i < pairArr.length; i++) {
            strArr[i] = (String) pairArr[i].first;
            parcelableArr[i] = (Parcelable) pairArr[i].second;
        }
        return new Intent(A).setPackage(l.j).putExtra("actions", strArr).putExtra("data", parcelableArr);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void e(Context context, String str, Parcelable parcelable) {
        o(context, str, parcelable);
    }

    public static boolean f(Context context) {
        return t(context, "CHECK_SERVICE", B);
    }

    private static void g(Context context, String str, Parcelable parcelable) {
        if (C == 1 || o0.d() != 0) {
            s(context, str, parcelable);
        } else if (C == 2) {
            t(context, str, parcelable);
        } else {
            r(context, str, parcelable);
        }
    }

    public static void h(Context context, b bVar) {
        g(context, "COMMIT_ACTION", F(bVar));
    }

    private static void i(Context context, Bundle bundle) {
        b bVarV = v(bundle);
        if (bVarV == null || bVarV.z == 0) {
            return;
        }
        e.A().E(context, bVarV);
        G(context, R.string.added_to_collection);
    }

    private static void j(Context context) {
        j.h(context);
    }

    public static void k(Context context, b.a0 a0Var, boolean z2, boolean z3) {
        Bundle bundleF = F(a0Var);
        bundleF.putBoolean("show_toast", z2);
        bundleF.putBoolean("toast_output", z3);
        g(context, "COMMIT_COMMAND", bundleF);
        D = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void l(Context context, Bundle bundle) {
        b.a0 a0Var = (b.a0) v(bundle);
        if (a0Var != null) {
            i0.c(a0Var, context, h0.a(), bundle.getBoolean("show_toast", false), bundle.getBoolean("toast_output", false));
        }
    }

    public static void m(Context context, boolean z2, ArrayList<g.q> arrayList) {
        if (!w(arrayList)) {
            v.c("Invalid gesture!");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("landscape", z2);
        m mVar = new m(bundle);
        mVar.j();
        int size = arrayList.size();
        mVar.d(size);
        for (int i = 0; i < size; i++) {
            arrayList.get(i).b(mVar);
        }
        mVar.l();
        g(context, "COMMIT_GESTURE", bundle);
    }

    private static void n(Context context, Bundle bundle) {
        if (bundle == null) {
            return;
        }
        m mVar = new m(bundle);
        ArrayList arrayList = null;
        if (mVar.i()) {
            int iH = mVar.h();
            ArrayList arrayList2 = new ArrayList(iH);
            for (int i = 0; i < iH; i++) {
                arrayList2.add(new g.q(mVar));
            }
            arrayList = arrayList2;
        }
        mVar.k();
        if (!w(arrayList)) {
            v.c("Invalid gesture!");
            return;
        }
        b.y0.O(context, arrayList, bundle.getBoolean("landscape", context.getResources().getConfiguration().orientation == 2));
        G(context, R.string.saved);
        context.sendBroadcast(new Intent(b.y0.K).setPackage(l.j));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void o(Context context, String str, Parcelable parcelable) {
        if (str == null) {
            v.c("action == null");
        }
        switch (str) {
            case "COMMIT_ACTION":
                i(context, (Bundle) parcelable);
                break;
            case "STOP_ALL":
                E();
                break;
            case "COMMIT_GESTURE":
                n(context, (Bundle) parcelable);
                break;
            case "COMMIT_COMMAND":
                l(context, (Bundle) parcelable);
                break;
            case "CHECK_SERVICE":
                j(context);
                break;
            case "COMMIT_SERVICE":
                q(context, (Intent) parcelable);
                break;
            default:
                if (!j.D.equals(str)) {
                    v.c("Unknown action: " + str);
                    break;
                } else {
                    A(context);
                    break;
                }
                break;
        }
    }

    public static void p(Context context, Intent intent) {
        if (C == 2) {
            com.jozein.xedgepro.service.a.e(context, intent);
        } else {
            r(context, "COMMIT_SERVICE", intent);
        }
    }

    private static void q(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        com.jozein.xedgepro.service.a.e(context, intent);
    }

    public static void r(Context context, String str, Parcelable parcelable) {
        C(context, H(str, parcelable));
    }

    public static boolean s(Context context, String str, Parcelable parcelable) {
        return g.a.f(context).d(str, parcelable);
    }

    private static boolean t(Context context, String str, Parcelable parcelable) {
        Intent intentH = H(str, parcelable);
        intentH.setComponent(new ComponentName(l.j, AdapterService.class.getName()));
        return com.jozein.xedgepro.service.a.e(context, intentH);
    }

    public static boolean u(Context context, Intent intent) {
        if (!A.equals(intent.getAction())) {
            return false;
        }
        Context applicationContext = context.getApplicationContext();
        String stringExtra = intent.getStringExtra("action");
        if (stringExtra != null) {
            Parcelable parcelableExtra = intent.getParcelableExtra("data");
            if (parcelableExtra == null) {
                return true;
            }
            o(applicationContext, stringExtra, parcelableExtra);
            return true;
        }
        String[] stringArrayExtra = intent.getStringArrayExtra("actions");
        Parcelable[] parcelableArrayExtra = intent.getParcelableArrayExtra("data");
        if (stringArrayExtra == null || parcelableArrayExtra == null) {
            return true;
        }
        for (int i = 0; i < stringArrayExtra.length; i++) {
            o(applicationContext, stringArrayExtra[i], parcelableArrayExtra[i]);
        }
        return true;
    }

    private static b v(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        return m.n(bundle);
    }

    private static boolean w(List<g.q> list) {
        int size;
        return list != null && (size = list.size()) > 1 && (list.get(0).A & 255) == 0 && (list.get(size - 1).A & 255) == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void x(Context context, int i) {
        Toast.makeText(context, i, 0).show();
    }

    public static void y(Context context, Pair<String, Parcelable>[] pairArr) {
        if (pairArr == null || pairArr.length == 0) {
            return;
        }
        try {
            C(context, I(pairArr));
        } catch (Throwable th) {
            v.d(th);
        }
    }

    public static void z(Context context) {
        r(context, j.D, B);
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return new a(this);
    }

    @Override // android.app.Service
    public void onRebind(Intent intent) {
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        return true;
    }
}
