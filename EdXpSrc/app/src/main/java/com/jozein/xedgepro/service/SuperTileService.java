package com.jozein.xedgepro.service;

import a.b;
import a.t;
import a.u;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import cc.xposed.R;
import com.jozein.xedgepro.ui.ActivityMain;
import f.l;
import f.v;
import g.a0;

/* JADX INFO: loaded from: classes.dex */
public abstract class SuperTileService extends TileService {
    private static int D;
    private static t E;
    public static String[] F = {Tile1.class.getName(), Tile2.class.getName(), Tile3.class.getName(), Tile4.class.getName(), Tile5.class.getName(), Tile6.class.getName(), Tile7.class.getName(), Tile8.class.getName(), Tile9.class.getName(), Tile10.class.getName(), Tile11.class.getName(), Tile12.class.getName()};
    private u.b A;
    private b.d B;
    private final String z = getClass().getName();
    private boolean C = false;

    public static final class Tile1 extends SuperTileService {
    }

    public static final class Tile10 extends SuperTileService {
    }

    public static final class Tile11 extends SuperTileService {
    }

    public static final class Tile12 extends SuperTileService {
    }

    public static final class Tile2 extends SuperTileService {
    }

    public static final class Tile3 extends SuperTileService {
    }

    public static final class Tile4 extends SuperTileService {
    }

    public static final class Tile5 extends SuperTileService {
    }

    public static final class Tile6 extends SuperTileService {
    }

    public static final class Tile7 extends SuperTileService {
    }

    public static final class Tile8 extends SuperTileService {
    }

    public static final class Tile9 extends SuperTileService {
    }

    private Icon b(b.d dVar) {
        int i;
        return (dVar.B() || !((i = dVar.K.z) == 0 || i == 1)) ? Icon.createWithBitmap(a0.a(dVar, this)) : Icon.createWithResource(l.j, R.drawable.ic_launcher);
    }

    private CharSequence c(b.d dVar) {
        int i;
        if (!dVar.C() && ((i = dVar.K.z) == 0 || i == 1)) {
            PackageManager packageManager = getPackageManager();
            try {
                return packageManager.getServiceInfo(new ComponentName(l.j, getClass().getName()), 786432).loadLabel(packageManager);
            } catch (Throwable th) {
                v.d(th);
            }
        }
        return dVar.n(this);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x002e, code lost:
    
        if (com.jozein.xedgepro.service.SuperTileService.E != null) goto L22;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean d() {
        /*
            r4 = this;
            a.b$d r0 = r4.B
            a.b r0 = a.b.x(r0)
            int r0 = r0.z
            r1 = 0
            if (r0 == 0) goto L38
            r2 = 1
            if (r0 == r2) goto L38
            r3 = 73
            if (r0 == r3) goto L2c
            r3 = 97
            if (r0 == r3) goto L2c
            switch(r0) {
                case 29: goto L2c;
                case 30: goto L2c;
                case 31: goto L2c;
                case 32: goto L2c;
                case 33: goto L2c;
                case 34: goto L23;
                case 35: goto L1a;
                default: goto L19;
            }
        L19:
            goto L37
        L1a:
            a.t r3 = com.jozein.xedgepro.service.SuperTileService.E
            if (r3 != 0) goto L30
            boolean r0 = a.p.q(r4, r1)
            return r0
        L23:
            a.t r1 = com.jozein.xedgepro.service.SuperTileService.E
            if (r1 != 0) goto L30
            boolean r0 = a.p.q(r4, r2)
            return r0
        L2c:
            a.t r1 = com.jozein.xedgepro.service.SuperTileService.E
            if (r1 == 0) goto L37
        L30:
            a.t r1 = com.jozein.xedgepro.service.SuperTileService.E
            boolean r0 = r1.h(r0, r2)
            return r0
        L37:
            return r2
        L38:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jozein.xedgepro.service.SuperTileService.d():boolean");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void e(boolean z) {
        try {
            k(z, null, null);
        } catch (Throwable th) {
            v.d(th);
        }
    }

    private void g() {
        this.A.l(this);
        this.B = this.A.b(this.z);
        i();
    }

    private void h() {
        this.A.o(this);
    }

    private void i() {
        k(d(), c(this.B), b(this.B));
    }

    private void j() {
        k(d(), c(this.B), null);
    }

    public final void f() {
        this.B = this.A.b(this.z);
        i();
    }

    protected void k(boolean z, CharSequence charSequence, Icon icon) {
        Tile qsTile = getQsTile();
        if (qsTile == null) {
            return;
        }
        boolean z2 = false;
        boolean z3 = true;
        if (z != (qsTile.getState() == 2)) {
            qsTile.setState(z ? 2 : 1);
            z2 = true;
        }
        if (charSequence != null && !charSequence.equals(qsTile.getLabel())) {
            qsTile.setLabel(charSequence);
            z2 = true;
        }
        if (icon != null) {
            qsTile.setIcon(icon);
        } else {
            z3 = z2;
        }
        if (z3) {
            qsTile.updateTile();
        }
    }

    @Override // android.service.quicksettings.TileService
    public void onClick() {
        try {
            int i = this.B.K.z;
            if (i == 0 || i == 1) {
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.setComponent(new ComponentName(l.j, ActivityMain.class.getName()));
                intent.setFlags(268435456);
                intent.putExtra("clicked", 7);
                try {
                    startActivityAndCollapse(intent);
                } catch (Throwable th) {
                    v.d(th);
                }
            } else {
                j();
                b.t(this, this.B);
            }
        } catch (Throwable th2) {
            v.d(th2);
        }
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        try {
            u.b bVarD = u.d();
            this.A = bVarD;
            this.B = bVarD.b(this.z);
            int i = D;
            D = i + 1;
            if (i == 0) {
                t tVar = new t(getApplicationContext());
                E = tVar;
                tVar.f();
            }
        } catch (Throwable th) {
            v.d(th);
        }
    }

    @Override // android.service.quicksettings.TileService, android.app.Service
    public void onDestroy() {
        t tVar;
        super.onDestroy();
        try {
            if (this.C) {
                this.C = false;
                h();
            }
            int i = D - 1;
            D = i;
            if (i != 0 || (tVar = E) == null) {
                return;
            }
            tVar.e();
            E = null;
        } catch (Throwable th) {
            v.d(th);
        }
    }

    @Override // android.service.quicksettings.TileService
    public void onStartListening() {
        try {
            if (this.C) {
                j();
            } else {
                this.C = true;
                g();
            }
            b bVarX = b.x(this.B);
            if (E == null || !t.i(bVarX.z)) {
                return;
            }
            E.j(this.z, bVarX.z, new t.c() { // from class: b.d
                @Override // a.t.c
                public final void a(boolean z) {
                    this.f63a.e(z);
                }
            });
        } catch (Throwable th) {
            v.d(th);
        }
    }

    @Override // android.service.quicksettings.TileService
    public void onStopListening() {
        t tVar = E;
        if (tVar != null) {
            try {
                tVar.n(this.z);
            } catch (Throwable th) {
                v.d(th);
            }
        }
    }

    @Override // android.service.quicksettings.TileService
    public void onTileRemoved() {
        if (this.C) {
            this.C = false;
            try {
                h();
            } catch (Throwable th) {
                v.d(th);
            }
        }
    }
}
