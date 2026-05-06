package f;

import a.b;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Pair;
import android.util.SparseArray;
import de.robv.android.xposed.XposedHelpers;
import g.g;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.CollationKey;
import java.text.Collator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class c0 implements l {
    private static Constructor<?> A = null;
    private static Method B = null;
    private static PackageManager C = null;
    private static Object D = null;
    private static Method E = null;
    private static Method F = null;
    private static Method G = null;
    private static Method H = null;
    private static Method I = null;
    public static String z = " (";

    public static class a extends b {

        /* JADX INFO: renamed from: g, reason: collision with root package name */
        ActivityInfo f422g;
        ComponentName h;
        private String i;

        a(ActivityInfo activityInfo) {
            super(activityInfo.applicationInfo);
            this.f422g = activityInfo;
        }

        @Override // f.c0.b
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            a aVar = (a) obj;
            return this.f423a.uid == aVar.f423a.uid && this.f422g.name.equals(aVar.f422g.name) && this.f422g.packageName.equals(aVar.f422g.packageName);
        }

        @Override // f.c0.b
        public int hashCode() {
            return this.f422g.name.hashCode() + this.f423a.uid;
        }

        @Override // f.c0.b
        public boolean m() {
            return this.f422g.enabled && this.f423a.enabled;
        }

        @Override // f.c0.b
        protected Drawable o() {
            return c0.m(this.f422g);
        }

        @Override // f.c0.b
        protected CharSequence p() {
            return c0.p(this.f422g);
        }

        public b.x q() {
            if (l()) {
                ActivityInfo activityInfo = this.f422g;
                return new b.f(activityInfo.packageName, activityInfo.name, this.f424b);
            }
            ActivityInfo activityInfo2 = this.f422g;
            return new b.e(activityInfo2.packageName, activityInfo2.name);
        }

        public final String r() {
            return this.f422g.name;
        }

        public final ComponentName s() {
            if (this.h == null) {
                ActivityInfo activityInfo = this.f422g;
                this.h = new ComponentName(activityInfo.packageName, activityInfo.name);
            }
            return this.h;
        }

        public final String t() {
            if (this.i == null) {
                ActivityInfo activityInfo = this.f422g;
                this.i = c0.A(activityInfo.packageName, activityInfo.name);
            }
            return this.i;
        }

        @Override // f.c0.b
        public String toString() {
            StringBuilder sb;
            String strI = i();
            String strT = t();
            if (this.f424b == 0) {
                sb = new StringBuilder();
                sb.append(strI);
                sb.append('/');
            } else {
                sb = new StringBuilder();
                sb.append(strI);
                sb.append("(");
                sb.append(this.f424b);
                sb.append(")/");
            }
            sb.append(strT);
            return sb.toString();
        }

        public final boolean u() {
            return this.f422g.enabled;
        }
    }

    public static class b {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final ApplicationInfo f423a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final int f424b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        private String f425c;

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        private Locale f426d;

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        private CollationKey f427e;

        /* JADX INFO: renamed from: f, reason: collision with root package name */
        private g.b f428f;

        public b(ApplicationInfo applicationInfo) {
            this.f423a = applicationInfo;
            this.f424b = o0.h(applicationInfo.uid);
        }

        private g.g b(int i) {
            int iG = o0.g(i);
            g.b bVar = this.f428f;
            if (bVar != null) {
                return bVar.b(iG);
            }
            g.g gVar = new g.g(o(), iG);
            if (i == this.f424b) {
                this.f428f = gVar.getConstantState();
            }
            return gVar;
        }

        public final ApplicationInfo a() {
            return this.f423a;
        }

        final CollationKey c(Locale locale, Collator collator) {
            CollationKey collationKey = this.f427e;
            if (collationKey != null && locale.equals(this.f426d)) {
                return collationKey;
            }
            CollationKey collationKey2 = collator.getCollationKey(g(locale));
            this.f427e = collationKey2;
            return collationKey2;
        }

        public g.g d() {
            return b(this.f424b);
        }

        public final Drawable e() {
            g.g gVarD = d();
            if (!m()) {
                g.a0.v(gVarD, true);
            }
            return gVarD;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ApplicationInfo applicationInfo = this.f423a;
            int i = applicationInfo.uid;
            ApplicationInfo applicationInfo2 = ((b) obj).f423a;
            return i == applicationInfo2.uid && applicationInfo.packageName.equals(applicationInfo2.packageName);
        }

        public final String f() {
            return g(Locale.getDefault());
        }

        String g(Locale locale) {
            if (!locale.equals(this.f426d)) {
                this.f425c = c0.l(p(), this.f424b).toString();
                this.f426d = locale;
            }
            return this.f425c;
        }

        Locale h() {
            return this.f426d;
        }

        public int hashCode() {
            return this.f423a.uid;
        }

        public final String i() {
            return this.f423a.packageName;
        }

        public g.g j() {
            return b(0);
        }

        public final int k() {
            return this.f424b;
        }

        public final boolean l() {
            return o0.j(this.f424b);
        }

        public boolean m() {
            return this.f423a.enabled;
        }

        public final boolean n() {
            int i = this.f423a.flags;
            return ((i & 1) == 0 && (i & 128) == 0) ? false : true;
        }

        protected Drawable o() {
            return c0.n(this.f423a);
        }

        protected CharSequence p() {
            return c0.q(this.f423a);
        }

        public String toString() {
            String strI = i();
            if (this.f424b == 0) {
                return strI;
            }
            return strI + "(" + this.f424b + ")";
        }
    }

    public static class c {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private final SparseArray<Set<String>> f429a = new SparseArray<>();

        public void a(String str, int i) {
            Set<String> hashSet = this.f429a.get(i);
            if (hashSet == null) {
                hashSet = new HashSet<>();
                this.f429a.append(i, hashSet);
            }
            hashSet.add(str);
        }

        public void b(Set<String> set, int i) {
            Set<String> hashSet = this.f429a.get(i);
            if (hashSet == null) {
                hashSet = new HashSet<>();
                this.f429a.append(i, hashSet);
            }
            hashSet.addAll(set);
        }

        public boolean c(String str, int i) {
            Set<String> set = this.f429a.get(-1);
            if (set != null && set.contains(str)) {
                return true;
            }
            Set<String> set2 = this.f429a.get(i);
            return set2 != null && set2.contains(str);
        }

        public boolean d() {
            int size = this.f429a.size();
            if (size <= 0) {
                return true;
            }
            for (int i = 0; i < size; i++) {
                if (!this.f429a.valueAt(i).isEmpty()) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class d extends b {
        private static final a[] i = new a[0];

        /* JADX INFO: renamed from: g, reason: collision with root package name */
        final PackageInfo f430g;
        final a[] h;

        d(PackageInfo packageInfo) {
            super(packageInfo.applicationInfo);
            this.f430g = packageInfo;
            ActivityInfo[] activityInfoArr = packageInfo.activities;
            if (activityInfoArr == null || activityInfoArr.length <= 0) {
                this.h = i;
                return;
            }
            this.h = new a[activityInfoArr.length];
            int i2 = 0;
            while (true) {
                a[] aVarArr = this.h;
                if (i2 >= aVarArr.length) {
                    return;
                }
                aVarArr[i2] = new a(packageInfo.activities[i2]);
                i2++;
            }
        }

        public a[] q() {
            return this.h;
        }
    }

    public static class e extends a {
        final ResolveInfo j;

        e(ResolveInfo resolveInfo) {
            super(resolveInfo.activityInfo);
            this.j = resolveInfo;
        }

        @Override // f.c0.b
        public g.g d() {
            g.g gVarD = super.d();
            gVarD.c();
            return gVarD;
        }

        @Override // f.c0.a, f.c0.b
        protected Drawable o() {
            return c0.o(this.j);
        }

        @Override // f.c0.a, f.c0.b
        protected CharSequence p() {
            return c0.r(this.j);
        }

        @Override // f.c0.a
        public b.x q() {
            return new b.l(this.f423a.packageName, this.f422g.name, this.f424b);
        }

        public Intent v() {
            return c0.a().setComponent(s());
        }
    }

    public static class f extends b {

        /* JADX INFO: renamed from: g, reason: collision with root package name */
        final ActivityManager.RunningTaskInfo f431g;
        final ActivityInfo h;

        public f(ActivityManager.RunningTaskInfo runningTaskInfo, int i) {
            this(runningTaskInfo, q(runningTaskInfo, i));
        }

        public f(ActivityManager.RunningTaskInfo runningTaskInfo, Pair<ApplicationInfo, ActivityInfo> pair) {
            super((ApplicationInfo) pair.first);
            this.f431g = runningTaskInfo;
            this.h = (ActivityInfo) pair.second;
        }

        private static Pair<ApplicationInfo, ActivityInfo> q(ActivityManager.RunningTaskInfo runningTaskInfo, int i) {
            try {
                ActivityInfo activityInfoB = c0.b(runningTaskInfo.baseActivity, i);
                return new Pair<>(activityInfoB.applicationInfo, activityInfoB);
            } catch (Throwable th) {
                v.c(th.toString());
                return new Pair<>(c0.c(runningTaskInfo.baseActivity.getPackageName(), i), null);
            }
        }

        @Override // f.c0.b
        public g.g d() {
            g.g gVarD = super.d();
            gVarD.c();
            return gVarD;
        }

        @Override // f.c0.b
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            return obj != null && getClass() == obj.getClass() && this.f431g.id == ((f) obj).f431g.id;
        }

        @Override // f.c0.b
        public int hashCode() {
            return this.f431g.id;
        }

        @Override // f.c0.b
        protected Drawable o() {
            ActivityInfo activityInfo = this.h;
            return activityInfo != null ? c0.m(activityInfo) : super.o();
        }

        @Override // f.c0.b
        protected CharSequence p() {
            return c0.p(this.h);
        }

        public final ActivityManager.RunningTaskInfo r() {
            return this.f431g;
        }
    }

    public static String A(String str, String str2) {
        int length = str.length();
        return (str2.length() > length && str2.charAt(length) == '.' && str2.startsWith(str)) ? str2.substring(length) : str2;
    }

    public static String B(Intent intent) {
        ComponentName component = intent.getComponent();
        if (component != null) {
            return component.flattenToShortString();
        }
        String str = intent.getPackage();
        return str != null ? str : intent.toString();
    }

    public static String C(String str, String str2) {
        return str + '/' + A(str, str2);
    }

    public static Intent a() {
        return new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER");
    }

    public static ActivityInfo b(ComponentName componentName, int i) throws PackageManager.NameNotFoundException {
        if (i == o0.m()) {
            ActivityInfo activityInfo = j().getActivityInfo(componentName, 33280);
            if (o0.h(activityInfo.applicationInfo.uid) == i) {
                return activityInfo;
            }
            throw new PackageManager.NameNotFoundException(componentName.flattenToShortString() + "(" + i + ")");
        }
        ActivityInfo[] activityInfoArr = i(componentName.getPackageName(), 33281, i).activities;
        if (activityInfoArr != null && activityInfoArr.length > 0) {
            String className = componentName.getClassName();
            for (ActivityInfo activityInfo2 : activityInfoArr) {
                if (className.equals(activityInfo2.name)) {
                    return activityInfo2;
                }
            }
        }
        throw new PackageManager.NameNotFoundException(componentName.flattenToShortString() + z + i + ").");
    }

    public static ApplicationInfo c(String str, int i) throws PackageManager.NameNotFoundException {
        Object objInvoke;
        if (i == o0.m()) {
            return j().getApplicationInfo(str, 33280);
        }
        ApplicationInfo applicationInfo = null;
        if (Build.VERSION.SDK_INT < 24) {
            Object objD = d();
            if (objD != null) {
                if (H == null) {
                    f0 f0VarN = f0.n();
                    Class<?> cls = objD.getClass();
                    Class<?> cls2 = Integer.TYPE;
                    H = f0VarN.h(cls, "getApplicationInfo", String.class, cls2, cls2);
                }
                objInvoke = H.invoke(objD, str, 33280, Integer.valueOf(i));
            }
            if (applicationInfo == null && o0.h(applicationInfo.uid) == i) {
                return applicationInfo;
            }
            throw new PackageManager.NameNotFoundException(str + z + i + ")");
        }
        if (H == null) {
            f0 f0VarN2 = f0.n();
            Class<?> cls3 = Integer.TYPE;
            H = f0VarN2.h(PackageManager.class, "getApplicationInfoAsUser", String.class, cls3, cls3);
        }
        objInvoke = H.invoke(j(), str, 33280, Integer.valueOf(i));
        applicationInfo = (ApplicationInfo) objInvoke;
        if (applicationInfo == null) {
        }
        throw new PackageManager.NameNotFoundException(str + z + i + ")");
    }

    private static Object d() {
        Object objG = D;
        if (objG != null) {
            return objG;
        }
        try {
            objG = f0.n().g(j().getClass(), "mPm");
            D = objG;
            return objG;
        } catch (Throwable th) {
            v.d(th);
            return objG;
        }
    }

    public static List<PackageInfo> e(int i, int i2) {
        if (i2 == o0.m()) {
            return j().getInstalledPackages(i);
        }
        try {
            if (G == null) {
                String str = Build.VERSION.SDK_INT >= 24 ? "getInstalledPackagesAsUser" : "getInstalledPackages";
                f0 f0VarN = f0.n();
                Class<?> cls = Integer.TYPE;
                G = f0VarN.h(PackageManager.class, str, cls, cls);
            }
            return (List) G.invoke(j(), Integer.valueOf(i), Integer.valueOf(i2));
        } catch (Throwable unused) {
            return null;
        }
    }

    public static String f(Intent intent) {
        ComponentName component;
        if (intent == null) {
            return null;
        }
        String str = intent.getPackage();
        return (str != null || (component = intent.getComponent()) == null) ? str : component.getPackageName();
    }

    public static Intent g(String str, int i) throws PackageManager.NameNotFoundException {
        Intent intentA = a();
        intentA.setPackage(str);
        List<ResolveInfo> listV = v(intentA, 0, i);
        if (listV == null || listV.size() == 0) {
            intentA.removeCategory("android.intent.category.LAUNCHER");
            intentA.addCategory("android.intent.category.INFO");
            listV = v(intentA, 0, i);
        }
        if (listV != null && listV.size() > 0) {
            ActivityInfo activityInfo = listV.get(0).activityInfo;
            intentA.setClassName(activityInfo.packageName, activityInfo.name);
            intentA.setFlags(268435456);
            return intentA;
        }
        throw new PackageManager.NameNotFoundException(str + z + i + ").");
    }

    public static <T extends Parcelable> List<T> h(Parcelable parcelable) throws NoSuchMethodException {
        Method methodJ = B;
        if (methodJ == null) {
            methodJ = f0.n().j(parcelable.getClass(), "getList", new Class[0]);
            B = methodJ;
        }
        return (List) methodJ.invoke(parcelable, new Object[0]);
    }

    public static PackageInfo i(String str, int i, int i2) throws PackageManager.NameNotFoundException {
        Object objInvoke;
        if (i2 == o0.m()) {
            return j().getPackageInfo(str, i);
        }
        PackageInfo packageInfo = null;
        if (Build.VERSION.SDK_INT < 24) {
            Object objD = d();
            if (objD != null) {
                if (I == null) {
                    f0 f0VarN = f0.n();
                    Class<?> cls = objD.getClass();
                    Class<?> cls2 = Integer.TYPE;
                    I = f0VarN.h(cls, "getPackageInfo", String.class, cls2, cls2);
                }
                objInvoke = I.invoke(objD, str, Integer.valueOf(i), Integer.valueOf(i2));
            }
            if (packageInfo == null && o0.h(packageInfo.applicationInfo.uid) == i2) {
                return packageInfo;
            }
            throw new PackageManager.NameNotFoundException(str + z + i2 + ")");
        }
        if (I == null) {
            f0 f0VarN2 = f0.n();
            Class<?> cls3 = Integer.TYPE;
            I = f0VarN2.h(PackageManager.class, "getPackageInfoAsUser", String.class, cls3, cls3);
        }
        objInvoke = I.invoke(j(), str, Integer.valueOf(i), Integer.valueOf(i2));
        packageInfo = (PackageInfo) objInvoke;
        if (packageInfo == null) {
        }
        throw new PackageManager.NameNotFoundException(str + z + i2 + ")");
    }

    public static PackageManager j() {
        if (C == null) {
            C = z.P();
        }
        return C;
    }

    public static g.g k(Drawable drawable, int i) {
        return new g.g(drawable, o0.g(i));
    }

    public static CharSequence l(CharSequence charSequence, int i) {
        return charSequence;
    }

    public static Drawable m(ActivityInfo activityInfo) {
        Drawable drawableS = s(activityInfo);
        return drawableS != null ? drawableS : s(activityInfo);
    }

    public static Drawable n(ApplicationInfo applicationInfo) {
        return s(applicationInfo);
    }

    public static Drawable o(ResolveInfo resolveInfo) {
        Drawable drawableT = t(resolveInfo);
        return drawableT != null ? drawableT : s(resolveInfo.activityInfo.applicationInfo);
    }

    public static CharSequence p(ActivityInfo activityInfo) {
        PackageManager packageManagerJ = j();
        CharSequence charSequenceLoadLabel = activityInfo.loadLabel(packageManagerJ);
        return charSequenceLoadLabel.length() > 0 ? charSequenceLoadLabel : activityInfo.applicationInfo.loadLabel(packageManagerJ);
    }

    public static CharSequence q(ApplicationInfo applicationInfo) {
        return applicationInfo.loadLabel(j());
    }

    public static CharSequence r(ResolveInfo resolveInfo) {
        PackageManager packageManagerJ = j();
        CharSequence charSequenceLoadLabel = resolveInfo.loadLabel(packageManagerJ);
        return charSequenceLoadLabel.length() > 0 ? charSequenceLoadLabel : resolveInfo.activityInfo.applicationInfo.loadLabel(packageManagerJ);
    }

    private static Drawable s(PackageItemInfo packageItemInfo) {
        return Build.VERSION.SDK_INT >= 22 ? packageItemInfo.loadUnbadgedIcon(j()) : packageItemInfo.loadIcon(j());
    }

    private static Drawable t(ResolveInfo resolveInfo) {
        if (Build.VERSION.SDK_INT < 23) {
            return resolveInfo.loadIcon(j());
        }
        PackageManager packageManagerJ = j();
        int i = resolveInfo.icon;
        if (i != 0) {
            String str = resolveInfo.resolvePackageName;
            Drawable drawable = str != null ? packageManagerJ.getDrawable(str, i, null) : null;
            if (drawable == null) {
                ActivityInfo activityInfo = resolveInfo.activityInfo;
                drawable = packageManagerJ.getDrawable(activityInfo.packageName, i, activityInfo.applicationInfo);
            }
            if (drawable != null) {
                return drawable;
            }
        }
        return resolveInfo.activityInfo.loadUnbadgedIcon(packageManagerJ);
    }

    public static <T extends Parcelable> Parcelable u(List<T> list) {
        Constructor<?> constructorE = A;
        if (constructorE == null) {
            f0 f0VarN = f0.n();
            constructorE = f0VarN.e(f0VarN.c("android.content.pm.ParceledListSlice"), List.class);
            A = constructorE;
        }
        return (Parcelable) constructorE.newInstance(list);
    }

    public static List<ResolveInfo> v(Intent intent, int i, int i2) {
        if (i2 == o0.m()) {
            return j().queryIntentActivities(intent, i);
        }
        try {
            if (F == null) {
                f0 f0VarN = f0.n();
                Class<?> cls = Integer.TYPE;
                F = f0VarN.h(PackageManager.class, "queryIntentActivitiesAsUser", Intent.class, cls, cls);
            }
            return (List) F.invoke(j(), intent, Integer.valueOf(i), Integer.valueOf(i2));
        } catch (Throwable unused) {
            return null;
        }
    }

    public static void w(Context context, BroadcastReceiver broadcastReceiver, IntentFilter intentFilter, Handler handler) {
        try {
            if (Build.VERSION.SDK_INT >= 30) {
                XposedHelpers.callMethod(context, "registerReceiverForAllUsers", new Object[]{broadcastReceiver, intentFilter, null, handler});
            } else {
                XposedHelpers.callMethod(context, "registerReceiverAsUser", new Object[]{broadcastReceiver, o0.p(), intentFilter, null, handler});
            }
        } catch (Throwable th) {
            v.d(th);
            context.registerReceiver(broadcastReceiver, intentFilter, null, handler);
        }
    }

    public static ResolveInfo x(Intent intent, int i, int i2) {
        try {
            if (i2 == o0.m()) {
                return j().resolveActivity(intent, i);
            }
            if (E == null) {
                f0 f0VarN = f0.n();
                Class<?> cls = Integer.TYPE;
                E = f0VarN.h(PackageManager.class, "resolveActivityAsUser", Intent.class, cls, cls);
            }
            ResolveInfo resolveInfo = (ResolveInfo) E.invoke(j(), intent, Integer.valueOf(i), Integer.valueOf(i2));
            if (resolveInfo == null || o0.h(resolveInfo.activityInfo.applicationInfo.uid) != i2) {
                return null;
            }
            return resolveInfo;
        } catch (Throwable unused) {
            return null;
        }
    }

    public static void y(Object obj) {
        D = obj;
    }

    static void z(PackageManager packageManager) {
        C = packageManager;
    }
}
