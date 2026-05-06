package com.jozein.xedgepro.ui;

import a.b;
import a.j;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.jozein.xedgepro.ApplicationMain;
import com.jozein.xedgepro.service.BinderService;
import f.l;
import f.l0;
import f.o0;
import f.v;

/* JADX INFO: loaded from: classes.dex */
public class ActivityPerformAction extends Activity {
    public static void a(Context context, b bVar) {
        b(context, bVar, 0L);
    }

    public static void b(Context context, b bVar, long j) {
        if (!ApplicationMain.isModuleActivated()) {
            try {
                try {
                    int i = bVar.z;
                    if (i == 47) {
                        e(context, ((b.m) bVar).K);
                        return;
                    }
                    if (i == 48) {
                        b.e eVar = (b.e) bVar;
                        d(context, eVar.K, eVar.L);
                        return;
                    }
                    if (i != 76) {
                        if (i == 78) {
                            c(context, ((b.s2) bVar).K);
                            return;
                        }
                        switch (i) {
                            case 113:
                                b.f fVar = (b.f) bVar;
                                if (fVar.M == o0.m()) {
                                    d(context, fVar.K, fVar.L);
                                    return;
                                }
                                return;
                            case 114:
                                b.h hVar = (b.h) bVar;
                                if (hVar.L == o0.m()) {
                                    c(context, hVar.K);
                                    return;
                                }
                                return;
                            case 115:
                                b.l lVar = (b.l) bVar;
                                try {
                                    String str = lVar.L;
                                    if (str != null) {
                                        d(context, lVar.K, str);
                                    } else {
                                        e(context, lVar.K);
                                    }
                                    return;
                                } catch (Throwable unused) {
                                    e(context, lVar.K);
                                    return;
                                }
                        }
                    }
                    c(context, ((b.g) bVar).K);
                } catch (Throwable th) {
                    v.d(th);
                }
            } catch (ActivityNotFoundException | SecurityException unused2) {
            }
        }
        if (j <= 0) {
            b.t(context, bVar);
        } else {
            b.v(context, bVar, j);
        }
    }

    public static void c(Context context, Intent intent) {
        intent.addFlags(805306368);
        context.startActivity(intent);
    }

    public static void d(Context context, String str, String str2) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(str, str2));
        c(context, intent);
    }

    public static void e(Context context, String str) {
        Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(str);
        if (launchIntentForPackage != null) {
            c(context, launchIntentForPackage);
            return;
        }
        v.c("Main activity not found: " + str);
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        try {
            if (!l.r && j.z.equals(intent.getAction())) {
                j.c(getApplicationContext());
            } else if (!BinderService.u(this, intent)) {
                b bVarJ = l0.j(intent);
                if (bVarJ == null || bVarJ.z == 0) {
                    v.c("No data for action found in " + intent.toUri(0));
                } else {
                    b(getApplicationContext(), bVarJ, 200L);
                }
            }
        } catch (Throwable th) {
            v.d(th);
        }
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        finish();
    }
}
