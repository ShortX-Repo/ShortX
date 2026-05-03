package com.jozein.xedgepro.xposed;

import android.app.Application;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

/* JADX INFO: loaded from: classes.dex */
class z0 extends p2 {

    class a extends XC_MethodHook {
        a() {
        }

        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            try {
                z0.this.h((Application) methodHookParam.thisObject);
            } catch (Throwable th) {
                p2.g(th);
            }
        }
    }

    z0(ClassLoader classLoader) {
        if (f.o0.m() != 0 || f.f0.u(classLoader)) {
            return;
        }
        try {
            XposedHelpers.findAndHookMethod(Application.class, "onCreate", new Object[]{new a()});
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    void h(Application application) {
        Application.OnProvideAssistDataListener onProvideAssistDataListenerP = f.f0.p();
        if (onProvideAssistDataListenerP != null) {
            application.registerOnProvideAssistDataListener(onProvideAssistDataListenerP);
            application.unregisterOnProvideAssistDataListener(onProvideAssistDataListenerP);
        }
    }
}
