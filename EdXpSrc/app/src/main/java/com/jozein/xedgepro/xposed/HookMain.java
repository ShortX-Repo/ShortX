package com.jozein.xedgepro.xposed;

import a.b;
import a.s;
import android.content.ClipData;
import android.os.Build;
import android.os.Process;
import android.view.View;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.SELinuxHelper;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class HookMain extends p2 implements IXposedHookZygoteInit, IXposedHookLoadPackage {
    private static boolean J;
    private a.p E;
    private a.k F;
    private a.f G;
    private s.e H;
    private a.v I;

    private void h(ClassLoader classLoader) {
        if (J) {
            return;
        }
        J = true;
        new i2();
        new l2(classLoader);
    }

    private void i(ClassLoader classLoader) {
        if (new File("/data/system_de/disable_xedge").exists()) {
            f.v.c("Found file /data/system_de/disable_xedge, disable hook!!!");
            return;
        }
        l();
        m();
        p2.e(classLoader);
        if (this.E == null) {
            j();
        }
        new w1(classLoader, this.E, this.G, this.F, this.H, this.I);
        p2.f("System hooked.");
        int i = Build.VERSION.SDK_INT;
        if (i == 29 || (i >= 21 && i <= 23)) {
            XposedHelpers.findAndHookMethod(View.class, i >= 24 ? "startDragAndDrop" : "startDrag", new Object[]{ClipData.class, View.DragShadowBuilder.class, Object.class, Integer.TYPE, XC_MethodReplacement.returnConstant(Boolean.FALSE)});
        }
    }

    private void j() {
        if (Build.VERSION.SDK_INT >= 21) {
            x3.l();
            try {
                String str = f.l.l;
                if ((f.r.k(str) & 1) == 0) {
                    if (SELinuxHelper.isSELinuxEnforced() || Process.myUid() != 0) {
                        p2.f(str + " not executable!");
                    } else {
                        f.v.c(new File(str).setExecutable(true, false) ? "Set app data folder executable." : "Failed to set app data folder executable.");
                    }
                }
            } catch (Throwable th) {
                p2.g(th);
            }
        }
        this.E = new a.p();
        this.F = a.k.h();
        this.G = a.f.g();
        this.H = new s.e();
        this.I = new a.v();
        b.f0.A();
        b.k2.A();
        b.y0.L();
    }

    private boolean k() {
        if (Build.VERSION.SDK_INT < 21) {
            return false;
        }
        try {
            return SELinuxHelper.isSELinuxEnforced();
        } catch (Throwable unused) {
            return false;
        }
    }

    private void l() {
        if (this.E == null) {
            f.v.c("---------------------------------------- Starting " + f.l.x + " ----------------------------------------");
        }
    }

    private void m() {
        f.v.a();
        StringBuilder sb = new StringBuilder(256);
        sb.append(f.l.u);
        int iP = f.p0.p();
        sb.append(iP != 1 ? iP != 2 ? iP != 3 ? "? " : ", activated by LSPosed. " : ", activated by EdXposed. " : ". ");
        sb.append("\n    ");
        sb.append(Build.MANUFACTURER);
        sb.append('-');
        sb.append(Build.MODEL);
        sb.append(", ");
        sb.append(Build.DEVICE);
        sb.append(", Android ");
        sb.append(Build.VERSION.RELEASE);
        sb.append(", SDK ");
        sb.append(Build.VERSION.SDK_INT);
        sb.append(", ROM: ");
        sb.append(Build.DISPLAY);
        sb.append('(');
        sb.append(Build.ID);
        sb.append(')');
        if (k()) {
            sb.append(", SELinux enforced.");
        } else {
            sb.append('.');
        }
        p2.f(sb.toString());
    }

    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        try {
            if (Build.VERSION.SDK_INT >= 21 && "android".equals(loadPackageParam.packageName)) {
                i(loadPackageParam.classLoader);
            } else if (f.l.j.equals(loadPackageParam.packageName)) {
                new z0(loadPackageParam.classLoader);
            }
        } catch (Throwable th) {
            p2.g(th);
        }
    }

    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
        try {
            if (this.E == null && Process.myUid() == 0) {
                l();
                j();
            }
            ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
            if (Build.VERSION.SDK_INT < 21) {
                i(systemClassLoader);
            }
            h(systemClassLoader);
        } catch (Throwable th) {
            p2.g(th);
        }
    }
}
