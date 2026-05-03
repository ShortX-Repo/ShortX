package a;

import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.SparseArray;
import g.a0;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class x implements f.l {
    private static x B;
    private static int C;
    private final SparseArray<a> A;
    private final f.p z;

    public static class a {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        public final String f52a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        public int f53b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        public int f54c;

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        public int f55d;

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        public int f56e;

        public a(String str, int i, int i2, int i3, int i4) {
            this.f52a = str;
            this.f55d = i;
            this.f56e = i2;
            this.f53b = i3;
            this.f54c = i4;
        }
    }

    private x() {
        f.p pVar = new f.p(f.l.q);
        this.z = pVar;
        this.A = new SparseArray<>();
        if (pVar.E()) {
            do {
                this.A.append(this.z.h(), new a(this.z.a(), this.z.h(), this.z.h(), this.z.h(), this.z.h()));
            } while (this.z.t());
        }
        this.z.m();
    }

    public static int c(Context context, int i) {
        return (((a0.t(i) - 30) / 70) + 1) * d(context);
    }

    public static int d(Context context) {
        if (C == 0) {
            int i = g.x.b(context).f636a;
            Point pointN = a0.n(context, new Point());
            C = (i + (Math.min(pointN.x, pointN.y) / 4)) / 2;
        }
        return C;
    }

    private a g(String str) {
        synchronized (this.A) {
            int size = this.A.size();
            for (int i = 0; i < size; i++) {
                if (str.equals(this.A.valueAt(i).f52a)) {
                    return this.A.valueAt(i);
                }
            }
            return null;
        }
    }

    public static x h() {
        x xVar;
        synchronized (x.class) {
            if (B == null) {
                B = new x();
            }
            xVar = B;
        }
        return xVar;
    }

    public static String j(Context context, AppWidgetProviderInfo appWidgetProviderInfo) {
        if (appWidgetProviderInfo == null) {
            return "-";
        }
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                return appWidgetProviderInfo.loadLabel(context.getPackageManager());
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
        return appWidgetProviderInfo.label;
    }

    public static Drawable k(Context context, AppWidgetProviderInfo appWidgetProviderInfo) {
        if (appWidgetProviderInfo == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            return appWidgetProviderInfo.loadPreviewImage(context, -1);
        }
        try {
            Resources resourcesForApplication = context.getPackageManager().getResourcesForApplication(appWidgetProviderInfo.provider.getPackageName());
            int i = appWidgetProviderInfo.previewImage;
            if (i <= 0) {
                i = appWidgetProviderInfo.icon;
            }
            return resourcesForApplication.getDrawable(i);
        } catch (Throwable th) {
            f.v.d(th);
            return null;
        }
    }

    public static void l() {
        synchronized (x.class) {
            B = null;
        }
    }

    private void n() {
        this.z.j();
        int size = this.A.size();
        for (int i = 0; i < size; i++) {
            a aVarValueAt = this.A.valueAt(i);
            this.z.d(this.A.keyAt(i)).c(aVarValueAt.f52a).d(aVarValueAt.f55d).d(aVarValueAt.f56e).d(aVarValueAt.f53b).d(aVarValueAt.f54c).C();
        }
        this.z.n();
    }

    public void a(int i, AppWidgetProviderInfo appWidgetProviderInfo, Context context) {
        int i2;
        int iC;
        int iC2;
        int i3 = appWidgetProviderInfo.minWidth;
        int i4 = appWidgetProviderInfo.minHeight;
        String strFlattenToShortString = appWidgetProviderInfo.provider.flattenToShortString();
        if (i3 <= 1 || i4 <= 1) {
            a aVarG = g(strFlattenToShortString);
            int i5 = 0;
            if (aVarG != null) {
                i5 = aVarG.f55d;
                i2 = aVarG.f56e;
            } else {
                f.v.c("Error load size!");
                i2 = 0;
            }
            iC = i2;
            iC2 = i5;
        } else {
            iC2 = c(context, i3);
            iC = c(context, i4);
        }
        a aVar = new a(strFlattenToShortString, iC2, iC, 0, 0);
        synchronized (this.A) {
            this.A.put(i, aVar);
        }
        n();
    }

    public void b() {
        synchronized (this.A) {
            this.A.clear();
        }
        this.z.j().n();
    }

    public int e(String str) {
        synchronized (this.A) {
            int size = this.A.size();
            for (int i = 0; i < size; i++) {
                if (str.equals(this.A.valueAt(i).f52a)) {
                    return this.A.keyAt(i);
                }
            }
            return 0;
        }
    }

    public a f(int i) {
        a aVar;
        synchronized (this.A) {
            aVar = this.A.get(i);
        }
        return aVar;
    }

    public ArrayList<Integer> i() {
        ArrayList<Integer> arrayList = new ArrayList<>();
        synchronized (this.A) {
            int size = this.A.size();
            for (int i = 0; i < size; i++) {
                arrayList.add(Integer.valueOf(this.A.keyAt(i)));
            }
        }
        return arrayList;
    }

    public void m(int i) {
        synchronized (this.A) {
            this.A.delete(i);
        }
        n();
    }

    public void o(int i, int i2, int i3) {
        a aVar;
        synchronized (this.A) {
            aVar = this.A.get(i, null);
        }
        if (aVar != null) {
            aVar.f53b = i2;
            aVar.f54c = i3;
            n();
        }
    }

    public void p(int i, int i2, int i3) {
        a aVar;
        synchronized (this.A) {
            aVar = this.A.get(i, null);
        }
        if (aVar != null) {
            aVar.f55d = i2;
            aVar.f56e = i3;
            n();
        }
    }
}
