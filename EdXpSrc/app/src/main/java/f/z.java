package f;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import com.jozein.xedgepro.ApplicationMain;
import com.jozein.xedgepro.ui.ActivityPerformAction;
import f.c0;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/* JADX INFO: loaded from: classes.dex */
public abstract class z {

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static volatile z f499d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static boolean f500e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static final f f501f = new f();

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static final Comparator<ResolveInfo> f502g = new Comparator() { // from class: f.x
        @Override // java.util.Comparator
        public final int compare(Object obj, Object obj2) {
            return z.Y((ResolveInfo) obj, (ResolveInfo) obj2);
        }
    };

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final Context f503a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final PackageManager f504b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private e f505c;

    /* JADX INFO: Access modifiers changed from: private */
    static class b extends z {
        private final boolean h;
        private int i;
        private final List<h> j;
        private int[] k;
        private List<c0.d> l;
        private List<c0.d> m;

        class a implements Application.ActivityLifecycleCallbacks {

            /* JADX INFO: renamed from: a, reason: collision with root package name */
            private boolean f506a = false;

            a() {
            }

            private boolean b(Activity activity) {
                return (activity == null || activity.getClass() == ActivityPerformAction.class) ? false : true;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void c() {
                if (b.this.z0()) {
                    b.this.L0();
                }
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public void onActivityCreated(Activity activity, Bundle bundle) {
                if (b(activity) && bundle == null) {
                    if (b.this.i > 0) {
                        b.s0(b.this);
                    } else {
                        b.this.i = 1;
                        this.f506a = true;
                    }
                }
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public void onActivityDestroyed(Activity activity) {
                if (b(activity) && activity.isFinishing()) {
                    if (b.this.i > 1) {
                        b.t0(b.this);
                        return;
                    }
                    b.this.i = 0;
                    this.f506a = false;
                    b.this.w0();
                }
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public void onActivityPaused(Activity activity) {
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public void onActivityResumed(Activity activity) {
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public void onActivityStarted(Activity activity) {
                if (this.f506a) {
                    this.f506a = false;
                    h0.a().post(new Runnable() { // from class: f.a0
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.z.c();
                        }
                    });
                }
            }

            @Override // android.app.Application.ActivityLifecycleCallbacks
            public void onActivityStopped(Activity activity) {
            }
        }

        /* JADX INFO: renamed from: f.z$b$b, reason: collision with other inner class name */
        class C0011b extends BroadcastReceiver {
            C0011b() {
            }

            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                ApplicationMain.g();
                try {
                    b.this.D0(intent);
                } catch (Throwable th) {
                    v.d(th);
                }
            }
        }

        b(Context context) {
            super(context);
            this.i = 0;
            this.j = new ArrayList();
            this.h = Build.VERSION.SDK_INT < 30 || p0.q(context, l.j, "android.permission.QUERY_ALL_PACKAGES");
            F0(context);
        }

        private void A0(g gVar) {
            int iC = gVar.c();
            int i = gVar.f511b;
            if (i == -4) {
                B0(gVar.f513d, iC);
                b0(gVar.f513d, iC);
            } else if (i == -3) {
                PackageInfo packageInfoB = gVar.b();
                C0(packageInfoB, iC);
                a0(packageInfoB, iC);
            } else {
                if (i != -2) {
                    return;
                }
                PackageInfo packageInfoB2 = gVar.b();
                C0(packageInfoB2, iC);
                Z(packageInfoB2, iC);
            }
        }

        private void E0(g gVar) {
            try {
                List listA = gVar.a();
                if (listA != null) {
                    List<c0.d> listO0 = z.o0(listA);
                    Collections.sort(listO0, z.f501f);
                    synchronized (this) {
                        this.l = listO0;
                        this.m = listO0;
                        this.k = gVar.f512c;
                    }
                }
            } catch (Throwable th) {
                v.d(th);
            }
        }

        private void F0(Context context) {
            Application application;
            if (context instanceof Application) {
                application = (Application) context;
            } else if (context instanceof Activity) {
                application = ((Activity) context).getApplication();
            } else if (context instanceof Service) {
                application = ((Service) context).getApplication();
            } else {
                context = context.getApplicationContext();
                application = (Application) context;
            }
            application.registerActivityLifecycleCallbacks(new a());
            application.registerReceiver(new C0011b(), new IntentFilter(g.f509f), null, x0());
        }

        private h G0(int i) {
            synchronized (this.j) {
                int size = this.j.size();
                for (int i2 = 0; i2 < size; i2++) {
                    h hVar = this.j.get(i2);
                    if (hVar.f517a == i) {
                        this.j.remove(i2);
                        return hVar;
                    }
                }
                return null;
            }
        }

        private void H0(h hVar) {
            synchronized (this.j) {
                this.j.remove(hVar);
            }
        }

        private g I0(int i, int[] iArr) {
            g gVarE = null;
            if (h0.c(x0())) {
                v.d(new UnsupportedOperationException("Waiting on ui thread!!!"));
                return null;
            }
            h hVar = new h(i, iArr);
            u0(hVar);
            try {
                gVarE = hVar.e(this.f503a);
            } catch (Throwable th) {
                v.d(th);
            }
            H0(hVar);
            return gVarE;
        }

        private <T extends Parcelable> List<T> J0(int i, int[] iArr) {
            g gVarI0 = I0(i, iArr);
            if (gVarI0 == null) {
                return null;
            }
            try {
                return gVarI0.a();
            } catch (Throwable th) {
                v.d(th);
                return null;
            }
        }

        private void K0() {
            int[] iArrE = o0.e(this.f503a);
            new h(0, v0(iArrE) ? 1 : 2, iArrE).d(this.f503a);
        }

        static /* synthetic */ int s0(b bVar) {
            int i = bVar.i + 1;
            bVar.i = i;
            return i;
        }

        static /* synthetic */ int t0(b bVar) {
            int i = bVar.i - 1;
            bVar.i = i;
            return i;
        }

        private void u0(h hVar) {
            synchronized (this.j) {
                this.j.add(hVar);
            }
        }

        private boolean v0(int[] iArr) {
            if (this.h) {
                if (iArr == null || iArr.length == 0) {
                    return true;
                }
                if (iArr.length == 1 && iArr[0] == 0) {
                    return true;
                }
            }
            return false;
        }

        private List<c0.d> y0(int i) {
            if (this.h && i == 0) {
                return null;
            }
            List<c0.d> list = this.m;
            if (list != null) {
                return list;
            }
            if (!z0()) {
                K0();
            }
            return null;
        }

        void B0(String str, int i) {
            List<c0.d> list;
            int[] iArr;
            int iJ;
            synchronized (this) {
                list = this.l;
                iArr = this.k;
            }
            if (list == null || (iJ = z.j(list, str, i)) < 0) {
                return;
            }
            ArrayList arrayList = new ArrayList(list);
            arrayList.remove(iJ);
            synchronized (this) {
                this.l = arrayList;
                this.m = arrayList;
                this.k = iArr;
            }
        }

        void C0(PackageInfo packageInfo, int i) {
            List<c0.d> list;
            int[] iArr;
            synchronized (this) {
                list = this.l;
                iArr = this.k;
            }
            if (list != null) {
                ArrayList arrayList = new ArrayList(list);
                c0.d dVar = new c0.d(packageInfo);
                int iJ = z.j(arrayList, packageInfo.packageName, i);
                if (iJ >= 0) {
                    arrayList.set(iJ, dVar);
                } else {
                    arrayList.add(iJ ^ (-1), dVar);
                }
                synchronized (this) {
                    this.l = arrayList;
                    this.m = arrayList;
                    this.k = iArr;
                }
            }
        }

        void D0(Intent intent) {
            g gVarE = g.e(intent);
            if (gVarE != null) {
                if (gVarE.f511b <= 0) {
                    A0(gVarE);
                    return;
                }
                int i = gVarE.f510a;
                if (i == 0) {
                    E0(gVarE);
                    return;
                }
                h hVarG0 = G0(i);
                if (hVarG0 != null) {
                    hVarG0.a(gVarE);
                }
            }
        }

        @Override // f.z
        c0.d J(String str, int i) {
            return z.n(y0(i), str, i);
        }

        void L0() {
            K0();
        }

        @Override // f.z
        List<c0.d> S(boolean z, boolean z2) {
            List<c0.d> listO0;
            int[] iArr;
            synchronized (this) {
                listO0 = this.l;
                iArr = this.k;
            }
            int[] iArrE = o0.e(this.f503a);
            if (listO0 == null || !z.W(iArr, iArrE)) {
                try {
                    List listJ0 = J0(5, iArrE);
                    if (listJ0 != null) {
                        listO0 = z.o0(listJ0);
                        Collections.sort(listO0, z.f501f);
                        synchronized (this) {
                            this.l = listO0;
                            this.m = listO0;
                            this.k = iArrE;
                        }
                    }
                } catch (Throwable th) {
                    v.d(th);
                }
            }
            if (listO0 == null) {
                listO0 = z.o0(this.f504b.getInstalledPackages(z2 ? 33281 : 0));
            }
            List<c0.d> listL = z.l(listO0, z, z2);
            Collections.sort(listL, new d(Locale.getDefault()));
            return listL;
        }

        @Override // f.z
        List<AppWidgetProviderInfo> d0() {
            List<AppWidgetProviderInfo> listJ0;
            return (this.h || (listJ0 = J0(8, null)) == null) ? super.d0() : listJ0;
        }

        @Override // f.z
        List<ResolveInfo> g0(int[] iArr) {
            List<ResolveInfo> listJ0;
            return (v0(iArr) || (listJ0 = J0(6, iArr)) == null) ? super.g0(iArr) : listJ0;
        }

        @Override // f.z
        List<PackageInfo> h0(int i, int[] iArr) {
            if (!v0(iArr)) {
                List<PackageInfo> listJ0 = J0((i & 1) != 0 ? 5 : 4, iArr);
                if (listJ0 != null) {
                    return listJ0;
                }
            }
            return super.h0(i, iArr);
        }

        @Override // f.z
        List<ResolveInfo> i0() {
            List<ResolveInfo> listJ0;
            return (this.h || (listJ0 = J0(7, null)) == null) ? super.i0() : listJ0;
        }

        void w0() {
            synchronized (this) {
                this.l = null;
                this.k = null;
            }
            new h(0, 3, null).d(this.f503a);
        }

        Handler x0() {
            return h0.a();
        }

        boolean z0() {
            return this.i > 0;
        }
    }

    private static class c extends z {
        c(Context context) {
            super(context);
        }
    }

    private static final class d implements Comparator<c0.b> {
        private final Collator A;
        final Locale z;

        d(Locale locale) {
            this.z = locale;
            this.A = "en".equals(locale.getLanguage()) ? null : Collator.getInstance(locale);
        }

        @Override // java.util.Comparator
        /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
        public int compare(c0.b bVar, c0.b bVar2) {
            boolean zM = bVar.m();
            boolean zM2 = bVar2.m();
            if (zM != zM2) {
                return (zM ? 1 : 0) - (zM2 ? 1 : 0);
            }
            Collator collator = this.A;
            int iCompareTo = collator != null ? bVar.c(this.z, collator).compareTo(bVar2.c(this.z, this.A)) : bVar.g(this.z).compareToIgnoreCase(bVar2.g(this.z));
            return iCompareTo != 0 ? iCompareTo : bVar.k() - bVar2.k();
        }
    }

    public interface e {
        void a(c0.d dVar);

        void b(String str, int i);

        void c(c0.d dVar);
    }

    private static final class f implements Comparator<c0.b> {
        private f() {
        }

        @Override // java.util.Comparator
        /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
        public int compare(c0.b bVar, c0.b bVar2) {
            int iCompareTo = bVar.i().compareTo(bVar2.i());
            return iCompareTo != 0 ? iCompareTo : bVar.k() - bVar2.k();
        }

        public int b(c0.b bVar, String str, int i) {
            int iCompareTo = bVar.i().compareTo(str);
            return iCompareTo != 0 ? iCompareTo : bVar.k() - i;
        }
    }

    private static final class g {

        /* JADX INFO: renamed from: f, reason: collision with root package name */
        static final String f509f = l.k + "REPLY_PACKAGES";

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final int f510a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final int f511b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        final int[] f512c;

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        final String f513d;

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        private final Parcelable f514e;

        private g(int i, int i2, int[] iArr, String str, Parcelable parcelable) {
            this.f510a = i;
            this.f511b = i2;
            this.f512c = iArr;
            this.f513d = str;
            this.f514e = parcelable;
        }

        g(int i, PackageInfo packageInfo, String str, int i2) {
            this(i2, i, null, str, packageInfo);
        }

        g(h hVar, List<? extends Parcelable> list) {
            this(hVar.f517a, hVar.f518b, hVar.f519c, null, c0.u(list));
        }

        static String d(int i) {
            return i != -4 ? i != -3 ? i != -2 ? h.b(i) : "PACKAGE_ADDED" : "PACKAGE_CHANGED" : "PACKAGE_REMOVED";
        }

        static g e(Intent intent) {
            if (intent == null || !f509f.equals(intent.getAction())) {
                return null;
            }
            return new g(intent.getIntExtra("id", 0), intent.getIntExtra("option", 0), intent.getIntArrayExtra("users"), intent.getStringExtra("package"), intent.getParcelableExtra("data"));
        }

        <T extends Parcelable> List<T> a() {
            Parcelable parcelable = this.f514e;
            if (parcelable == null) {
                return null;
            }
            return c0.h(parcelable);
        }

        PackageInfo b() {
            return (PackageInfo) this.f514e;
        }

        int c() {
            return this.f510a;
        }

        void f(Context context) {
            context.sendBroadcast(new Intent(f509f).setPackage(l.j).putExtra("id", this.f510a).putExtra("option", this.f511b).putExtra("users", this.f512c).putExtra("package", this.f513d).putExtra("data", this.f514e));
        }

        public String toString() {
            StringBuilder sb;
            String str;
            if (this.f511b > 0) {
                sb = new StringBuilder();
                sb.append("ReplyData{ id=");
                sb.append(this.f510a);
                sb.append(" option=");
                sb.append(d(this.f511b));
                sb.append(", ");
                sb.append(this.f514e);
                str = "}";
            } else {
                sb = new StringBuilder();
                sb.append("ReplyData{ ");
                sb.append(d(this.f511b));
                sb.append(", ");
                sb.append(this.f513d);
                sb.append("(");
                sb.append(this.f510a);
                str = ")}";
            }
            sb.append(str);
            return sb.toString();
        }
    }

    private static final class h {

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        static final String f515e = l.k + "REQUEST_PACKAGES";

        /* JADX INFO: renamed from: f, reason: collision with root package name */
        private static final AtomicInteger f516f = new AtomicInteger(1);

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final int f517a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final int f518b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        final int[] f519c;

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        private volatile g f520d;

        h(int i, int i2, int[] iArr) {
            this.f517a = i;
            this.f518b = i2;
            this.f519c = iArr;
        }

        h(int i, int[] iArr) {
            this.f517a = f516f.getAndIncrement();
            this.f518b = i;
            this.f519c = iArr;
        }

        static String b(int i) {
            switch (i) {
                case 0:
                    return "INVALID_OPTION";
                case 1:
                    return "REGISTER_SYNC";
                case 2:
                    return "REGISTER_SYNC_AND_GET";
                case 3:
                    return "UNREGISTER_SYN";
                case 4:
                    return "GET_PACKAGES";
                case 5:
                    return "GET_PACKAGES_WITH_ACTIVITIES";
                case 6:
                    return "GET_LAUNCHER_ACTIVITIES";
                case 7:
                    return "GET_SHORTCUTS";
                case 8:
                    return "GET_APP_WIDGET_PROVIDERS";
                default:
                    return Integer.toString(i);
            }
        }

        static h c(Intent intent) {
            if (f515e.equals(intent.getAction())) {
                return new h(intent.getIntExtra("id", 0), intent.getIntExtra("option", 0), intent.getIntArrayExtra("users"));
            }
            return null;
        }

        void a(g gVar) {
            synchronized (this) {
                this.f520d = gVar;
                notifyAll();
            }
        }

        void d(Context context) {
            context.sendBroadcast(new Intent(f515e).setPackage("android").putExtra("id", this.f517a).putExtra("option", this.f518b).putExtra("users", this.f519c));
        }

        g e(Context context) {
            d(context);
            synchronized (this) {
                long jUptimeMillis = SystemClock.uptimeMillis() + 6000;
                while (this.f520d == null) {
                    long jUptimeMillis2 = jUptimeMillis - SystemClock.uptimeMillis();
                    if (jUptimeMillis2 <= 0) {
                        return null;
                    }
                    try {
                        wait(jUptimeMillis2);
                    } catch (InterruptedException unused) {
                    }
                }
                return this.f520d;
            }
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            return obj != null && h.class == obj.getClass() && this.f517a == ((h) obj).f517a;
        }

        public int hashCode() {
            return this.f517a;
        }

        public String toString() {
            return "RequestData{id=" + this.f517a + ", option=" + b(this.f518b) + ", userIds=" + Arrays.toString(this.f519c) + '}';
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class i extends z {
        private boolean h;
        private BroadcastReceiver i;
        private Handler j;
        private int[] k;
        private Locale l;
        private List<c0.e> m;
        private long n;

        class a extends BroadcastReceiver {
            a() {
            }

            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                try {
                    i.this.y0(intent);
                } catch (Throwable th) {
                    v.d(th);
                }
            }
        }

        class b extends BroadcastReceiver {
            b() {
            }

            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                try {
                    Uri data = intent.getData();
                    if (data == null) {
                        return;
                    }
                    i.this.v0(intent.getAction(), data.getSchemeSpecificPart(), o0.h(intent.getIntExtra("android.intent.extra.UID", 0)));
                } catch (Throwable th) {
                    v.d(th);
                }
            }
        }

        i(Context context) {
            super(context);
            this.h = false;
            this.n = 0L;
            z0();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void y0(Intent intent) {
            A0();
            h hVarC = h.c(intent);
            if (hVarC != null) {
                int i = hVarC.f518b;
                if (i == 1) {
                    this.h = true;
                    return;
                }
                if (i == 2) {
                    this.h = true;
                } else if (i == 3) {
                    this.h = false;
                    return;
                }
                B0(hVarC);
            }
        }

        private void z0() {
            this.f503a.registerReceiver(new a(), new IntentFilter(h.f515e), null, s0());
        }

        void A0() {
            if (z.f500e || this.i != null) {
                return;
            }
            this.i = new b();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
            intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
            intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
            intentFilter.addDataScheme("package");
            c0.w(this.f503a, this.i, intentFilter, s0());
        }

        void B0(h hVar) {
            List listH0;
            switch (hVar.f518b) {
                case 2:
                case 5:
                    listH0 = h0(33281, hVar.f519c);
                    break;
                case 3:
                default:
                    v.c("Unknown option: " + hVar.f518b);
                    return;
                case 4:
                    listH0 = h0(0, hVar.f519c);
                    break;
                case 6:
                    listH0 = g0(hVar.f519c);
                    break;
                case 7:
                    listH0 = i0();
                    break;
                case 8:
                    listH0 = d0();
                    break;
            }
            new g(hVar, listH0).f(this.f503a);
        }

        @Override // f.z
        List<c0.e> O(c0.c cVar, int i) {
            return h0.c(s0()) ? t0(cVar, i) : super.O(cVar, i);
        }

        @Override // f.z
        void c0(final String str, final String str2, final int i) {
            s0().post(new Runnable() { // from class: f.b0
                @Override // java.lang.Runnable
                public final void run() {
                    this.z.v0(str, str2, i);
                }
            });
        }

        Handler s0() {
            if (this.j == null) {
                this.j = h0.b();
            }
            return this.j;
        }

        List<c0.e> t0(c0.c cVar, int i) {
            long j;
            int[] iArr;
            A0();
            Locale locale = Locale.getDefault();
            int[] iArrE = o0.e(this.f503a);
            long jUptimeMillis = SystemClock.uptimeMillis();
            List<c0.e> listP0 = null;
            if (this.m != null) {
                if (jUptimeMillis <= this.n && z.W(iArrE, this.k)) {
                    listP0 = this.m;
                } else {
                    this.m = null;
                }
            }
            if (listP0 == null) {
                listP0 = z.p0(g0(iArrE));
                j = (iArrE == null || (iArr = this.k) == null || iArrE.length > iArr.length) ? 60000L : 7200000L;
            } else {
                j = 0;
            }
            if (this.m == null || !locale.equals(this.l)) {
                Collections.sort(listP0, new d(locale));
            }
            this.k = iArrE;
            this.l = locale;
            this.m = listP0;
            if (j > 0) {
                this.n = j + SystemClock.uptimeMillis();
            }
            return z.m(listP0, cVar, i);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Removed duplicated region for block: B:35:0x0090  */
        /* JADX INFO: renamed from: u0, reason: merged with bridge method [inline-methods] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void v0(java.lang.String r8, java.lang.String r9, int r10) {
            /*
                r7 = this;
                if (r8 == 0) goto La6
                if (r9 != 0) goto L6
                goto La6
            L6:
                android.content.Context r0 = r7.f503a     // Catch: java.lang.Throwable -> La2
                boolean r0 = f.o0.i(r0, r10)     // Catch: java.lang.Throwable -> La2
                if (r0 == 0) goto La6
                r0 = 0
                r1 = -1
                int r2 = r8.hashCode()     // Catch: java.lang.Throwable -> La2
                r3 = 172491798(0xa480416, float:9.630418E-33)
                r4 = 2
                r5 = 1
                r6 = 0
                if (r2 == r3) goto L3b
                r3 = 525384130(0x1f50b9c2, float:4.419937E-20)
                if (r2 == r3) goto L31
                r3 = 1544582882(0x5c1076e2, float:1.6265244E17)
                if (r2 == r3) goto L27
                goto L44
            L27:
                java.lang.String r2 = "android.intent.action.PACKAGE_ADDED"
                boolean r8 = r8.equals(r2)     // Catch: java.lang.Throwable -> La2
                if (r8 == 0) goto L44
                r1 = 0
                goto L44
            L31:
                java.lang.String r2 = "android.intent.action.PACKAGE_REMOVED"
                boolean r8 = r8.equals(r2)     // Catch: java.lang.Throwable -> La2
                if (r8 == 0) goto L44
                r1 = 2
                goto L44
            L3b:
                java.lang.String r2 = "android.intent.action.PACKAGE_CHANGED"
                boolean r8 = r8.equals(r2)     // Catch: java.lang.Throwable -> La2
                if (r8 == 0) goto L44
                r1 = 1
            L44:
                r8 = 33281(0x8201, float:4.6637E-41)
                if (r1 == 0) goto L62
                if (r1 == r5) goto L56
                if (r1 == r4) goto L4e
                goto L91
            L4e:
                r6 = -4
                r7.w0(r9, r10)     // Catch: java.lang.Throwable -> La2
                r7.b0(r9, r10)     // Catch: java.lang.Throwable -> La2
                goto L91
            L56:
                r6 = -3
                android.content.pm.PackageInfo r0 = f.c0.i(r9, r8, r10)     // Catch: java.lang.Throwable -> La2
                r7.x0(r0, r10)     // Catch: java.lang.Throwable -> La2
                r7.a0(r0, r10)     // Catch: java.lang.Throwable -> La2
                goto L91
            L62:
                android.content.pm.PackageInfo r0 = f.c0.i(r9, r8, r10)     // Catch: java.lang.Throwable -> La2
                r7.x0(r0, r10)     // Catch: java.lang.Throwable -> La2
                r7.Z(r0, r10)     // Catch: java.lang.Throwable -> La2
                if (r10 != 0) goto L90
                java.lang.String r8 = f.l.j     // Catch: java.lang.Throwable -> La2
                boolean r1 = r8.equals(r9)     // Catch: java.lang.Throwable -> La2
                if (r1 == 0) goto L90
                java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> La2
                r1.<init>()     // Catch: java.lang.Throwable -> La2
                java.lang.String r2 = "<<< "
                r1.append(r2)     // Catch: java.lang.Throwable -> La2
                r1.append(r8)     // Catch: java.lang.Throwable -> La2
                java.lang.String r8 = " reinstalled >>>"
                r1.append(r8)     // Catch: java.lang.Throwable -> La2
                java.lang.String r8 = r1.toString()     // Catch: java.lang.Throwable -> La2
                f.v.c(r8)     // Catch: java.lang.Throwable -> La2
                goto L91
            L90:
                r6 = -2
            L91:
                boolean r8 = r7.h     // Catch: java.lang.Throwable -> La2
                if (r8 == 0) goto La6
                if (r6 == 0) goto La6
                f.z$g r8 = new f.z$g     // Catch: java.lang.Throwable -> La2
                r8.<init>(r6, r0, r9, r10)     // Catch: java.lang.Throwable -> La2
                android.content.Context r9 = r7.f503a     // Catch: java.lang.Throwable -> La2
                r8.f(r9)     // Catch: java.lang.Throwable -> La2
                goto La6
            La2:
                r8 = move-exception
                f.v.d(r8)
            La6:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: f.z.i.v0(java.lang.String, java.lang.String, int):void");
        }

        void w0(String str, int i) {
            List<c0.e> list = this.m;
            if (list != null) {
                this.m = z.j0(list, str, i);
            }
        }

        void x0(PackageInfo packageInfo, int i) {
            List<c0.e> list = this.m;
            if (list != null) {
                this.m = z.j0(list, packageInfo.packageName, i);
                List<ResolveInfo> listF0 = f0(c0.a().setPackage(packageInfo.packageName), 33280, i);
                if (listF0 == null || listF0.size() <= 0) {
                    return;
                }
                this.m.addAll(z.p0(listF0));
                this.l = null;
            }
        }
    }

    z(Context context) {
        Context applicationContext = context.getApplicationContext();
        context = applicationContext != null ? applicationContext : context;
        this.f503a = context;
        this.f504b = context.getPackageManager();
    }

    public static z K(Context context) {
        if (f499d == null) {
            c0.z(context.getPackageManager());
            synchronized (z.class) {
                if (f499d == null) {
                    f499d = o0.l() ? new i(context) : o0.m() == 0 ? new b(context) : new c(context);
                }
            }
        }
        return f499d;
    }

    public static <E extends c0.b> String[] L(List<E> list) {
        if (list == null) {
            return new String[0];
        }
        int size = list.size();
        String[] strArr = new String[size];
        Locale locale = Locale.getDefault();
        for (int i2 = 0; i2 < size; i2++) {
            strArr[i2] = list.get(i2).g(locale);
        }
        return strArr;
    }

    static PackageManager P() {
        return f499d.f504b;
    }

    static boolean W(int[] iArr, int[] iArr2) {
        return Arrays.equals(iArr, iArr2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean X(String str, int i2, c0.e eVar) {
        return str.equals(eVar.i()) && i2 == eVar.k();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int Y(ResolveInfo resolveInfo, ResolveInfo resolveInfo2) {
        ActivityInfo activityInfo = resolveInfo.activityInfo;
        int i2 = activityInfo.applicationInfo.uid;
        ActivityInfo activityInfo2 = resolveInfo2.activityInfo;
        int i3 = i2 - activityInfo2.applicationInfo.uid;
        if (i3 != 0) {
            return i3;
        }
        int iCompareTo = activityInfo.name.compareTo(activityInfo2.name);
        return iCompareTo != 0 ? iCompareTo : resolveInfo.activityInfo.packageName.compareTo(resolveInfo2.activityInfo.packageName);
    }

    public static <E extends c0.b> int i(List<E> list, c0.b bVar) {
        if (list.isEmpty()) {
            return -1;
        }
        return Collections.binarySearch(list, bVar, new d(Locale.getDefault()));
    }

    static <T extends c0.b> int j(List<T> list, String str, int i2) {
        int size = list.size() - 1;
        int i3 = 0;
        while (i3 <= size) {
            int i4 = (i3 + size) >>> 1;
            int iB = f501f.b(list.get(i4), str, i2);
            if (iB < 0) {
                i3 = i4 + 1;
            } else {
                if (iB <= 0) {
                    return i4;
                }
                size = i4 - 1;
            }
        }
        return i3 ^ (-1);
    }

    static List<c0.e> j0(List<c0.e> list, final String str, final int i2) {
        if (Build.VERSION.SDK_INT >= 24) {
            list.removeIf(new Predicate() { // from class: f.y
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return z.X(str, i2, (c0.e) obj);
                }
            });
            return list;
        }
        int size = list.size();
        ArrayList arrayList = new ArrayList(size);
        for (int i3 = 0; i3 < size; i3++) {
            c0.e eVar = list.get(i3);
            if (!str.equals(eVar.i()) || i2 != eVar.k()) {
                arrayList.add(eVar);
            }
        }
        return arrayList;
    }

    public static <E extends c0.b> void k(List<E> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        Locale locale = Locale.getDefault();
        if (locale.equals(list.get(0).h())) {
            return;
        }
        Collections.sort(list, new d(locale));
    }

    public static void k0(String str, String str2, int i2) {
        z zVar = f499d;
        if (zVar != null) {
            zVar.c0(str, str2, i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<c0.d> l(List<c0.d> list, boolean z, boolean z2) {
        if (list == null || list.size() == 0) {
            return list;
        }
        int size = list.size();
        ArrayList arrayList = new ArrayList(size);
        for (int i2 = 0; i2 < size; i2++) {
            c0.d dVar = list.get(i2);
            if ((!z2 || dVar.h.length > 0) && (z || !dVar.l())) {
                arrayList.add(dVar);
            }
        }
        return arrayList;
    }

    public static void l0(String str, String str2, int[] iArr) {
        z zVar = f499d;
        if (zVar == null || iArr == null) {
            return;
        }
        for (int i2 : iArr) {
            zVar.c0(str, str2, i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:22:0x003a  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x0050  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.util.List<f.c0.e> m(java.util.List<f.c0.e> r7, f.c0.c r8, int r9) {
        /*
            if (r8 == 0) goto L9
            boolean r0 = r8.d()
            if (r0 == 0) goto L9
            r8 = 0
        L9:
            if (r8 != 0) goto Lf
            if (r9 == 0) goto Le
            goto Lf
        Le:
            return r7
        Lf:
            r0 = r9 & 15
            r1 = 983040(0xf0000, float:1.377532E-39)
            r9 = r9 & r1
            int r1 = r7.size()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>(r1)
            r3 = 0
        L1e:
            if (r3 >= r1) goto L67
            java.lang.Object r4 = r7.get(r3)
            f.c0$e r4 = (f.c0.e) r4
            r5 = 1
            if (r0 != r5) goto L30
            boolean r5 = r4.n()
            if (r5 != 0) goto L3a
            goto L64
        L30:
            r5 = 2
            if (r0 != r5) goto L3a
            boolean r5 = r4.n()
            if (r5 == 0) goto L3a
            goto L64
        L3a:
            r5 = 65536(0x10000, float:9.1835E-41)
            if (r9 != r5) goto L45
            boolean r5 = r4.m()
            if (r5 != 0) goto L50
            goto L64
        L45:
            r5 = 131072(0x20000, float:1.83671E-40)
            if (r9 != r5) goto L50
            boolean r5 = r4.m()
            if (r5 == 0) goto L50
            goto L64
        L50:
            if (r8 == 0) goto L61
            java.lang.String r5 = r4.i()
            int r6 = r4.k()
            boolean r5 = r8.c(r5, r6)
            if (r5 == 0) goto L61
            goto L64
        L61:
            r2.add(r4)
        L64:
            int r3 = r3 + 1
            goto L1e
        L67:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: f.z.m(java.util.List, f.c0$c, int):java.util.List");
    }

    public static void m0(boolean z) {
        f500e = z;
    }

    static c0.d n(List<c0.d> list, String str, int i2) {
        int iJ;
        if (list == null || (iJ = j(list, str, i2)) < 0) {
            return null;
        }
        return list.get(iJ);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<c0.d> o0(List<PackageInfo> list) {
        int size = list.size();
        ArrayList arrayList = new ArrayList(size);
        for (int i2 = 0; i2 < size; i2++) {
            arrayList.add(new c0.d(list.get(i2)));
        }
        return arrayList;
    }

    private Drawable p(ComponentName componentName, String str, String str2, int i2) {
        c0.a aVarI = I(str, str2, i2);
        if (aVarI != null) {
            return aVarI.d();
        }
        if (componentName == null) {
            componentName = new ComponentName(str, str2);
        }
        return c0.k(c0.m(c0.b(componentName, i2)), i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<c0.e> p0(List<ResolveInfo> list) {
        int size = list.size();
        ArrayList arrayList = new ArrayList(size);
        for (int i2 = 0; i2 < size; i2++) {
            arrayList.add(new c0.e(list.get(i2)));
        }
        return arrayList;
    }

    private CharSequence w(ComponentName componentName, String str, String str2, int i2) {
        c0.a aVarI = I(str, str2, i2);
        if (aVarI != null) {
            return aVarI.f();
        }
        if (componentName == null) {
            componentName = new ComponentName(str, str2);
        }
        return c0.l(c0.p(c0.b(componentName, i2)), i2);
    }

    public final CharSequence A(String str, String str2, int i2) {
        return w(null, str, str2, i2);
    }

    public u<String, ArrayList<AppWidgetProviderInfo>> B() {
        List<AppWidgetProviderInfo> listD0 = d0();
        u<String, ArrayList<AppWidgetProviderInfo>> uVar = new u<>();
        if (listD0 != null) {
            for (AppWidgetProviderInfo appWidgetProviderInfo : listD0) {
                String packageName = appWidgetProviderInfo.provider.getPackageName();
                ArrayList<AppWidgetProviderInfo> arrayList = uVar.get(packageName);
                if (arrayList == null) {
                    arrayList = new ArrayList<>();
                    uVar.put(packageName, arrayList);
                }
                arrayList.add(appWidgetProviderInfo);
            }
        }
        return uVar;
    }

    public final Drawable C(String str) {
        return D(str, o0.d());
    }

    public final Drawable D(String str, int i2) {
        c0.d dVarJ = J(str, i2);
        return dVarJ != null ? dVarJ.d() : c0.k(c0.n(c0.c(str, i2)), i2);
    }

    public final ApplicationInfo E(String str) {
        return F(str, o0.d());
    }

    public final ApplicationInfo F(String str, int i2) {
        c0.d dVarJ = J(str, i2);
        return dVarJ != null ? dVarJ.f423a : c0.c(str, i2);
    }

    public final CharSequence G(String str) {
        return H(str, o0.d());
    }

    public final CharSequence H(String str, int i2) {
        c0.d dVarJ = J(str, i2);
        return dVarJ != null ? dVarJ.f() : c0.l(c0.q(c0.c(str, i2)), i2);
    }

    c0.a I(String str, String str2, int i2) {
        c0.d dVarJ = J(str, i2);
        if (dVarJ == null) {
            return null;
        }
        for (c0.a aVar : dVarJ.q()) {
            if (aVar.r().equals(str2)) {
                return aVar;
            }
        }
        return null;
    }

    c0.d J(String str, int i2) {
        return null;
    }

    public final List<c0.e> M() {
        return O(null, 0);
    }

    public final List<c0.e> N(c0.c cVar, int i2) {
        return O(cVar, i2);
    }

    List<c0.e> O(c0.c cVar, int i2) {
        List<c0.e> listM = m(p0(g0(o0.e(this.f503a))), cVar, i2);
        Collections.sort(listM, new d(Locale.getDefault()));
        return listM;
    }

    public final List<c0.d> Q(boolean z) {
        return S(z, false);
    }

    public final List<c0.d> R() {
        return S(true, true);
    }

    List<c0.d> S(boolean z, boolean z2) {
        List<c0.d> listL = l(o0(h0(z2 ? 33281 : 0, z ? o0.e(this.f503a) : null)), z, z2);
        Collections.sort(listL, new d(Locale.getDefault()));
        return listL;
    }

    public List<c0.e> T() {
        List<ResolveInfo> listI0 = i0();
        int size = listI0.size();
        ArrayList arrayList = new ArrayList(size);
        for (int i2 = 0; i2 < size; i2++) {
            arrayList.add(new c0.e(listI0.get(i2)));
        }
        Collections.sort(arrayList, new d(Locale.getDefault()));
        return arrayList;
    }

    public final boolean U(String str) {
        return V(str, o0.d());
    }

    public final boolean V(String str, int i2) {
        if (J(str, i2) != null) {
            return true;
        }
        try {
            c0.c(str, i2);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    final void Z(PackageInfo packageInfo, int i2) {
        if (this.f505c != null) {
            c0.d dVarJ = J(packageInfo.packageName, i2);
            if (dVarJ == null) {
                dVarJ = new c0.d(packageInfo);
            }
            try {
                this.f505c.c(dVarJ);
            } catch (Throwable th) {
                v.d(th);
            }
        }
    }

    final void a0(PackageInfo packageInfo, int i2) {
        if (this.f505c != null) {
            c0.d dVarJ = J(packageInfo.packageName, i2);
            if (dVarJ == null) {
                dVarJ = new c0.d(packageInfo);
            }
            try {
                this.f505c.a(dVarJ);
            } catch (Throwable th) {
                v.d(th);
            }
        }
    }

    final void b0(String str, int i2) {
        e eVar = this.f505c;
        if (eVar != null) {
            try {
                eVar.b(str, i2);
            } catch (Throwable th) {
                v.d(th);
            }
        }
    }

    void c0(String str, String str2, int i2) {
    }

    List<AppWidgetProviderInfo> d0() {
        return AppWidgetManager.getInstance(this.f503a).getInstalledProviders();
    }

    List<ResolveInfo> e0(Intent intent, int i2, int[] iArr) {
        List<ResolveInfo> listF0;
        if (iArr == null || iArr.length == 0) {
            listF0 = f0(intent, i2, 0);
        } else if (iArr.length == 1) {
            listF0 = f0(intent, i2, iArr[0]);
        } else {
            ArrayList arrayList = new ArrayList();
            for (int i3 : iArr) {
                List<ResolveInfo> listF02 = f0(intent, i2, i3);
                if (listF02 != null) {
                    arrayList.addAll(listF02);
                }
            }
            listF0 = arrayList;
        }
        return (listF0 == null || listF0.size() <= 0) ? this.f504b.queryIntentActivities(intent, i2) : listF0;
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x0057  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    java.util.List<android.content.pm.ResolveInfo> f0(android.content.Intent r9, int r10, int r11) {
        /*
            r8 = this;
            java.util.List r0 = f.c0.v(r9, r10, r11)
            if (r0 == 0) goto L5e
            int r1 = r0.size()
            if (r1 <= 0) goto L5e
            if (r10 == 0) goto L5e
            r10 = 0
            java.util.List r9 = f.c0.v(r9, r10, r11)
            r11 = 1
            if (r9 == 0) goto L25
            int r1 = r9.size()
            if (r1 != 0) goto L1e
            r9 = 0
            goto L25
        L1e:
            if (r1 <= r11) goto L25
            java.util.Comparator<android.content.pm.ResolveInfo> r1 = f.z.f502g
            java.util.Collections.sort(r9, r1)
        L25:
            int r1 = r0.size()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>(r1)
            r3 = 0
        L2f:
            if (r3 >= r1) goto L5d
            java.lang.Object r4 = r0.get(r3)
            android.content.pm.ResolveInfo r4 = (android.content.pm.ResolveInfo) r4
            android.content.pm.ActivityInfo r5 = r4.activityInfo
            boolean r5 = r5.enabled
            if (r5 == 0) goto L5a
            if (r9 == 0) goto L57
            java.util.Comparator<android.content.pm.ResolveInfo> r5 = f.z.f502g
            int r5 = java.util.Collections.binarySearch(r9, r4, r5)
            if (r5 < 0) goto L49
            r5 = 1
            goto L4a
        L49:
            r5 = 0
        L4a:
            android.content.pm.ActivityInfo r6 = r4.activityInfo
            android.content.pm.ApplicationInfo r6 = r6.applicationInfo
            boolean r7 = r6.enabled
            if (r5 == r7) goto L57
            if (r5 != 0) goto L55
            goto L5a
        L55:
            r6.enabled = r11
        L57:
            r2.add(r4)
        L5a:
            int r3 = r3 + 1
            goto L2f
        L5d:
            r0 = r2
        L5e:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: f.z.f0(android.content.Intent, int, int):java.util.List");
    }

    List<ResolveInfo> g0(int[] iArr) {
        return e0(c0.a(), 33280, iArr);
    }

    List<PackageInfo> h0(int i2, int[] iArr) {
        List<PackageInfo> listE;
        if (iArr == null || iArr.length == 0) {
            listE = c0.e(i2, 0);
        } else if (iArr.length == 1) {
            listE = c0.e(i2, iArr[0]);
        } else {
            ArrayList arrayList = new ArrayList();
            for (int i3 : iArr) {
                List<PackageInfo> listE2 = c0.e(i2, i3);
                if (listE2 != null) {
                    arrayList.addAll(listE2);
                }
            }
            listE = arrayList;
        }
        return (listE == null || listE.size() <= 0) ? this.f504b.getInstalledPackages(i2) : listE;
    }

    List<ResolveInfo> i0() {
        return e0(new Intent("android.intent.action.CREATE_SHORTCUT"), 0, null);
    }

    public void n0(e eVar) {
        if (eVar != null && this.f505c != null) {
            v.c("Override original listener!");
        }
        this.f505c = eVar;
    }

    public final Drawable o(ComponentName componentName, int i2) {
        return p(componentName, componentName.getPackageName(), componentName.getClassName(), i2);
    }

    public final Drawable q(Intent intent) {
        return r(intent, o0.d());
    }

    public final Drawable r(Intent intent, int i2) throws PackageManager.NameNotFoundException {
        ComponentName component = intent.getComponent();
        if (component != null) {
            return o(component, i2);
        }
        ResolveInfo resolveInfoX = c0.x(intent, 65536, i2);
        if (resolveInfoX != null) {
            return c0.k(c0.o(resolveInfoX), i2);
        }
        String str = intent.getPackage();
        if (str != null) {
            return D(str, i2);
        }
        throw new PackageManager.NameNotFoundException(intent.toUri(0));
    }

    public final Drawable s(String str, String str2) {
        return t(str, str2, o0.d());
    }

    public final Drawable t(String str, String str2, int i2) {
        return p(null, str, str2, i2);
    }

    public final CharSequence u(ComponentName componentName) {
        return v(componentName, o0.d());
    }

    public final CharSequence v(ComponentName componentName, int i2) {
        return w(componentName, componentName.getPackageName(), componentName.getClassName(), i2);
    }

    public final CharSequence x(Intent intent) {
        return y(intent, o0.d());
    }

    public final CharSequence y(Intent intent, int i2) throws PackageManager.NameNotFoundException {
        ComponentName component = intent.getComponent();
        if (component != null) {
            return v(component, i2);
        }
        ResolveInfo resolveInfoX = c0.x(intent, 65536, i2);
        if (resolveInfoX != null) {
            return c0.l(c0.r(resolveInfoX), i2);
        }
        String str = intent.getPackage();
        if (str != null) {
            return H(str, i2);
        }
        throw new PackageManager.NameNotFoundException(intent.toUri(0));
    }

    public final CharSequence z(String str, String str2) {
        return A(str, str2, o0.d());
    }
}
