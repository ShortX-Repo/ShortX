package com.jozein.xedgepro;

import a.b;
import a.e;
import a.f;
import a.j;
import a.k;
import a.m;
import a.s;
import a.u;
import a.v;
import a.w;
import a.x;
import a.z;
import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import cc.xposed.R;
import cc.xposed.qq;
import com.jozein.xedgepro.ApplicationMain;
import com.jozein.xedgepro.ui.ActivityMain;
import f.f0;
import f.h0;
import f.l;
import f.l0;
import f.p0;
import f.q;
import f.q0;
import f.v;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class ApplicationMain extends Application implements l {
    private static boolean B;
    private static boolean C;
    private static String D;
    private static String E;
    private List<z.b> A = null;
    private z z;

    class a implements z.b {
        a() {
        }

        @Override // a.z.b
        public void a(boolean z) {
            boolean unused = ApplicationMain.B = true;
            if (ApplicationMain.this.A != null) {
                Iterator it = ApplicationMain.this.A.iterator();
                while (it.hasNext()) {
                    ((z.b) it.next()).a(z);
                }
            }
        }

        @Override // a.z.b
        public void b(boolean z) {
            boolean unused = ApplicationMain.B = true;
            if (ApplicationMain.this.A != null) {
                Iterator it = ApplicationMain.this.A.iterator();
                while (it.hasNext()) {
                    ((z.b) it.next()).b(z);
                }
            }
        }
    }

    class b implements Application.ActivityLifecycleCallbacks {
        b() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void b() {
            f.g();
            s.f.h();
            b.f0.A();
            b.k2.A();
            e.A();
            b.v0.d();
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityCreated(Activity activity, Bundle bundle) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityDestroyed(Activity activity) {
            if ((activity instanceof ActivityMain) && activity.isFinishing()) {
                try {
                    ApplicationMain.this.unregisterActivityLifecycleCallbacks(this);
                } catch (Throwable th) {
                    v.d(th);
                }
                h0.a().post(new Runnable() { // from class: com.jozein.xedgepro.a
                    @Override // java.lang.Runnable
                    public final void run() {
                        ApplicationMain.b.b();
                    }
                });
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
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityStopped(Activity activity) {
        }
    }

    class c extends BroadcastReceiver {
        c() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            boolean unused = ApplicationMain.B = true;
            if (intent != null) {
                try {
                    int[] intArrayExtra = intent.getIntArrayExtra("alias");
                    if (intArrayExtra != null) {
                        q0.e(intArrayExtra);
                    }
                    String unused2 = ApplicationMain.D = intent.getStringExtra("build_time");
                    String unused3 = ApplicationMain.E = intent.getStringExtra("time");
                    if (intent.getBooleanExtra("send_files", false)) {
                        j.h(context);
                    }
                    ApplicationMain.this.unregisterReceiver(this);
                } catch (Throwable th) {
                    v.d(th);
                }
            }
        }
    }

    private ShortcutInfo f(String str, a.b bVar, int i, int i2) {
        Intent intentK = l0.k(bVar);
        CharSequence charSequenceN = bVar.n(this);
        return new ShortcutInfo.Builder(this, str).setIntent(intentK).setLongLabel(charSequenceN).setShortLabel(charSequenceN).setIcon(Icon.createWithResource(this, i)).setRank(i2).build();
    }

    public static void g() {
        B = true;
    }

    public static String i() {
        return E;
    }

    public static boolean isModuleActivated() {
        if (Build.VERSION.SDK_INT < 26 && !B && !C) {
            C = true;
            B = p();
        }
        return B;
    }

    public static String j() {
        return D;
    }

    private void k() {
        boolean zE0 = z.e0();
        z.w0();
        if (zE0) {
            b.s0.B(this, 25);
        }
        b.v0.a();
        z zVar = new z();
        this.z = zVar;
        w.a(zVar, this);
        registerActivityLifecycleCallbacks(new b());
    }

    public static boolean l() {
        return isModuleActivated() || p0.p() != 0;
    }

    private void m() {
        registerReceiver(new c(), new IntentFilter(b.s0.N));
        b.s0.B(this, 17);
    }

    private static boolean p() {
        if (Build.VERSION.SDK_INT >= 28) {
            return false;
        }
        try {
            ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
            Field declaredField = Class.forName("de.robv.android.xposed.XposedBridge", false, systemClassLoader).getDeclaredField("sHookedMethodCallbacks");
            declaredField.setAccessible(true);
            Map map = (Map) declaredField.get(null);
            Field declaredField2 = Class.forName("de.robv.android.xposed.XposedBridge$CopyOnWriteSortedSet", false, systemClassLoader).getDeclaredField("elements");
            declaredField2.setAccessible(true);
            String str = l.j + '.';
            if (map != null) {
                synchronized (map) {
                    Iterator it = map.values().iterator();
                    while (it.hasNext()) {
                        for (Object obj : (Object[]) declaredField2.get(it.next())) {
                            if (obj.getClass().getName().startsWith(str)) {
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (Throwable th) {
            v.d(th);
        }
        return false;
    }

    private void q() {
        ShortcutManager shortcutManager;
        if (Build.VERSION.SDK_INT >= 25 && (shortcutManager = (ShortcutManager) getSystemService("shortcut")) != null) {
            List<ShortcutInfo> dynamicShortcuts = shortcutManager.getDynamicShortcuts();
            if (dynamicShortcuts.size() > 0) {
                ShortcutInfo shortcutInfo = dynamicShortcuts.get(0);
                String id = shortcutInfo.getId();
                CharSequence shortLabel = shortcutInfo.getShortLabel();
                if ("gesture_control".equals(id) && new b.p0(0).n(this).equals(shortLabel)) {
                    return;
                }
                if ("key_control".equals(id) && new b.e1(0).n(this).equals(shortLabel)) {
                    return;
                }
            }
            ArrayList arrayList = new ArrayList();
            arrayList.add(f("gesture_control", new b.p0(0), R.drawable.ic_shortcut_toggle_gesture, 1));
            arrayList.add(f("key_control", new b.e1(0), R.drawable.ic_shortcut_toggle_key, 0));
            shortcutManager.addDynamicShortcuts(arrayList);
        }
    }

    public void e(z.b bVar) {
        if (bVar == null) {
            return;
        }
        if (this.A == null) {
            this.A = new ArrayList();
        }
        this.A.add(bVar);
    }

    public z h() {
        return this.z;
    }

    public void n() {
        q.b();
        if (Build.VERSION.SDK_INT >= 24) {
            u.j();
        }
        m.c();
        e.D();
        x.l();
        b.v0.f();
        this.z.g();
        this.z.L();
        w.a(this.z, this);
        v.a.h();
        k.k();
        f.l();
        s.f.k();
        b.f0.B();
        b.k2.B();
        b.y0.K();
        f.v.c("Settings reloaded.");
    }

    public void o(z.b bVar) {
        this.A.remove(bVar);
    }

    @Override // android.app.Application
    public void onCreate() {
        k();
        f.z.K(this);
        super.onCreate();
        qq.qq(this);
        this.z.y0(this, new a());
        m();
        try {
            q();
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    @Override // android.app.Application
    public void registerOnProvideAssistDataListener(Application.OnProvideAssistDataListener onProvideAssistDataListener) {
        f0.r(onProvideAssistDataListener);
        super.registerOnProvideAssistDataListener(onProvideAssistDataListener);
    }
}
