package com.jozein.xedgepro.xposed;

import android.content.ComponentName;
import android.os.Build;
import cc.xposed.R;

/* JADX INFO: loaded from: classes.dex */
class k3 extends j2 {
    private final w1 H;

    k3(Object obj, w1 w1Var) {
        super(obj.getClass(), obj);
        this.H = w1Var;
    }

    void B(ComponentName componentName) {
        if (Build.VERSION.SDK_INT >= 24) {
            r("addTile", componentName);
            this.H.E4(R.string.tile_added);
        }
    }

    void C(String str) {
        B(new ComponentName(f.l.j, str));
    }

    boolean D() {
        try {
            r("expandNotificationsPanel", new Object[0]);
            return true;
        } catch (Throwable th) {
            f.v.d(th);
            return false;
        }
    }

    boolean E() {
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                try {
                    r("expandSettingsPanel", null);
                    return true;
                } catch (Throwable unused) {
                }
            }
            r("expandSettingsPanel", new Object[0]);
            return true;
        } catch (Throwable th) {
            f.v.d(th);
            return false;
        }
    }

    void F(ComponentName componentName) {
        if (Build.VERSION.SDK_INT >= 24) {
            r("remTile", componentName);
            this.H.E4(R.string.tile_removed);
        }
    }

    void G(String str) {
        F(new ComponentName(f.l.j, str));
    }
}
