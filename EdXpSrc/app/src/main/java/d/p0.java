package d;

import a.b;
import a.z;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import cc.xposed.R;
import com.jozein.xedgepro.ApplicationMain;
import e.j;
import e.j0;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class p0 extends e.j {
    private static final int[] T = {R.drawable.ic_gesture_enabled, R.drawable.ic_key_enabled, R.drawable.ic_pie, R.drawable.ic_shortcuts_panel, R.drawable.ic_custom_panel, R.drawable.ic_left_side_bar, R.drawable.ic_right_side_bar, R.drawable.ic_quick_settings_tiles, R.drawable.ic_schedule, R.drawable.ic_app, R.drawable.ic_more_triggers, R.drawable.ic_saved_multi_action, R.drawable.ic_gesture_records, R.drawable.ic_action_collection, R.drawable.ic_freezer, R.drawable.ic_hidden_apps, R.drawable.ic_preferences, R.drawable.ic_more};
    private static final int[] U = {R.string.gestures, R.string.keys, R.string.pie, R.string.shortcuts_panel, R.string.custom_panels, R.string.left_side_bar, R.string.right_side_bar, R.string.tiles, R.string.schedule, R.string.app_state, R.string.more_triggers, R.string.saved_multi_actions, R.string.gesture_records, R.string.action_collection, R.string.freezer, R.string.hidden_apps, R.string.preferences, R.string.more};
    private static boolean V = false;
    private f.g0 M;
    private a.z O;
    private int[] Q;
    private Runnable N = null;
    private z.b P = null;
    private BroadcastReceiver R = null;
    boolean S = false;

    class a implements z.b {
        a() {
        }

        @Override // a.z.b
        public void a(boolean z) {
            ((j.f) p0.this.L0(0)).setChecked(z);
        }

        @Override // a.z.b
        public void b(boolean z) {
            ((j.f) p0.this.L0(1)).setChecked(z);
        }
    }

    class b extends f.g0 {
        b(Handler handler, int i, long j) {
            super(handler, i, j);
        }

        @Override // f.g0
        protected boolean a(int i) {
            if (f.o0.m() != 0) {
                boolean unused = p0.V = true;
                p0.this.N(new e.m().u(p0.this.u(R.string.user_not_supported)), 1);
                return false;
            }
            if (Build.VERSION.SDK_INT < 26 && !ApplicationMain.l()) {
                boolean unused2 = p0.V = true;
                p0.this.N(new e.m().u(p0.this.u(R.string.xposed_not_installed)), 1);
                return false;
            }
            if (!ApplicationMain.isModuleActivated()) {
                return true;
            }
            boolean unused3 = p0.V = true;
            return false;
        }

        @Override // f.g0
        protected void b() {
            p0.this.M = null;
            if (p0.V) {
                return;
            }
            boolean unused = p0.V = true;
            p0.this.N(new e.m().u(p0.this.u(R.string.module_not_activated)), 2);
        }
    }

    class c extends BroadcastReceiver {
        c() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String stringExtra = intent.getStringExtra("logs");
            String strI = ApplicationMain.i();
            String strJ = ApplicationMain.j();
            boolean z = strJ == null || strJ.equals(f.l.t);
            if (!(strI == null || strI.equals(f.l.s)) || !z) {
                StringBuilder sb = new StringBuilder();
                sb.append("Please reboot to activate new version: ");
                sb.append(f.l.u);
                if (z) {
                    sb.append('-');
                    sb.append(f.l.s);
                    sb.append('(');
                    sb.append(strI);
                } else {
                    sb.append('(');
                    sb.append(strJ);
                }
                sb.append(')');
                sb.append("\n\n");
                sb.append(stringExtra);
                stringExtra = sb.toString();
            }
            p0.this.O(new l0().j0(stringExtra));
        }
    }

    private int[] Q1() {
        if (this.Q == null) {
            if (ApplicationMain.isModuleActivated()) {
                this.Q = f.l.r ? new int[]{R.string.backup_restore, R.string.rate, R.string.share, R.string.help_and_support, R.string.about, R.string.logs} : new int[]{R.string.backup_restore, R.string.rate, R.string.share, R.string.get_pro_version, R.string.help_and_support, R.string.about, R.string.logs};
            } else {
                this.Q = f.l.r ? new int[]{R.string.backup_restore, R.string.rate, R.string.share, R.string.help_and_support, R.string.about} : new int[]{R.string.backup_restore, R.string.rate, R.string.share, R.string.get_pro_version, R.string.help_and_support, R.string.about};
            }
        }
        return this.Q;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void R1(CompoundButton compoundButton, boolean z) {
        U1(compoundButton, z, 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void S1(CompoundButton compoundButton, boolean z) {
        U1(compoundButton, z, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void T1(View view) {
        P(new e0(), 5);
    }

    private void U1(CompoundButton compoundButton, boolean z, int i) {
        if (!this.S) {
            this.S = true;
            if (!ApplicationMain.isModuleActivated()) {
                e0(R.string.module_not_activated);
                compoundButton.setChecked(this.O.p(i));
                return;
            }
        }
        boolean zP = this.O.p(i);
        if (zP != z) {
            this.O.X(compoundButton.getContext(), i, z);
        }
        compoundButton.setChecked(zP);
        if (zP && z && i == 1) {
            b.s0.C(compoundButton.getContext(), 8, -1);
        }
    }

    private void W1() {
        Context contextM0 = M0();
        int i = Build.VERSION.SDK_INT;
        if (i < 30 || Environment.isExternalStorageLegacy()) {
            if (i >= 23 && (contextM0.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0 || contextM0.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0)) {
                requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
                return;
            }
        } else if (!Environment.isExternalStorageManager()) {
            String str = f.l.j;
            if (f.p0.q(contextM0, str, "android.permission.MANAGE_EXTERNAL_STORAGE")) {
                if (Y1()) {
                    try {
                        Intent intent = new Intent("android.settings.MANAGE_APP_ALL_FILES_ACCESS_PERMISSION");
                        intent.setData(Uri.fromParts("package", str, null));
                        startActivityForResult(intent, 2);
                        return;
                    } catch (Throwable th) {
                        g0(th);
                    }
                } else {
                    e0(R.string.permission_denied);
                }
            }
        }
        P(new m(), 1);
    }

    private boolean Y1() {
        try {
            int iLastModified = (int) new File(f().getApplicationInfo().sourceDir).lastModified();
            if (a.m.b().a(5) == iLastModified) {
                return false;
            }
            a.m.b().f(5, iLastModified);
            return true;
        } catch (Throwable th) {
            f.v.d(th);
            return false;
        }
    }

    private void Z1(CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.SUBJECT", charSequence2);
        intent.putExtra("android.intent.extra.TEXT", charSequence3);
        try {
            startActivity(Intent.createChooser(intent, charSequence));
        } catch (Throwable unused) {
        }
    }

    private void a2() {
        if (Build.VERSION.SDK_INT >= 26 || b2("de.robv.android.xposed.installer", 1) || b2("org.meowcat.edxposed.manager", 3)) {
            return;
        }
        b2("com.solohsu.android.edxp.manager", 1);
    }

    private boolean b2(String str, int i) {
        try {
            Intent launchIntentForPackage = f().getPackageManager().getLaunchIntentForPackage(str);
            if (launchIntentForPackage == null) {
                return false;
            }
            launchIntentForPackage.putExtra("fragment", i);
            launchIntentForPackage.putExtra("section", "modules");
            launchIntentForPackage.setFlags(805306368);
            startActivity(launchIntentForPackage);
            return true;
        } catch (Throwable unused) {
            return false;
        }
    }

    @Override // e.j0.c
    protected void B() {
        super.B();
        CharSequence charSequenceU = u(f.l.y);
        int iM = f.o0.m();
        if (iM != 0) {
            charSequenceU = ((Object) charSequenceU) + " (" + iM + ")";
        }
        d0(charSequenceU);
        if (h().getBoolean("init", false)) {
            S(R.drawable.ic_action_bar_help, new View.OnClickListener() { // from class: d.m0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.z.T1(view);
                }
            });
        }
    }

    @Override // e.j
    protected int B0() {
        return U.length;
    }

    @Override // e.j0.c
    protected void F(Intent intent) {
        super.F(intent);
        int intExtra = intent.getIntExtra("clicked", -1);
        if (intExtra >= 0) {
            V = true;
            k1(intExtra);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x003e A[PHI: r6
      0x003e: PHI (r6v3 e.j$j) = (r6v2 e.j$j), (r6v6 e.j$j) binds: [B:24:0x003c, B:19:0x002d] A[DONT_GENERATE, DONT_INLINE]] */
    @Override // e.j0.c
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected void J(android.os.Bundle r5, int r6) {
        /*
            r4 = this;
            if (r5 != 0) goto L3
            return
        L3:
            r0 = 2
            java.lang.String r1 = "result"
            r2 = 0
            if (r6 == r0) goto L43
            r0 = 3
            r3 = 1
            if (r6 == r0) goto L30
            r0 = 4
            if (r6 == r0) goto L21
            r5 = 5
            if (r6 == r5) goto L14
            goto L4c
        L14:
            android.widget.ListView r5 = r4.G0()
            if (r5 == 0) goto L4c
            r5.setSelectionFromTop(r2, r2)
            r4.x1()
            goto L4c
        L21:
            int r5 = r5.getInt(r1)
            if (r5 > r3) goto L4c
            android.view.View r6 = r4.L0(r3)
            e.j$j r6 = (e.j.ViewOnTouchListenerC0005j) r6
            if (r5 != 0) goto L3f
            goto L3e
        L30:
            int r5 = r5.getInt(r1)
            if (r5 > r3) goto L4c
            android.view.View r6 = r4.L0(r2)
            e.j$j r6 = (e.j.ViewOnTouchListenerC0005j) r6
            if (r5 != 0) goto L3f
        L3e:
            r2 = 1
        L3f:
            r6.setChecked(r2)
            goto L4c
        L43:
            boolean r5 = r5.getBoolean(r1, r2)
            if (r5 == 0) goto L4c
            r4.a2()
        L4c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: d.p0.J(android.os.Bundle, int):void");
    }

    @Override // e.j
    protected boolean N0(int i) {
        return i == 17;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:15:0x0037  */
    /* JADX WARN: Removed duplicated region for block: B:17:0x0057  */
    @Override // e.j
    /* JADX INFO: renamed from: V1, reason: merged with bridge method [inline-methods] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public e.j.k h1(int r10) {
        /*
            r9 = this;
            r0 = 1
            if (r10 == 0) goto L94
            r1 = 0
            if (r10 == r0) goto L6e
            switch(r10) {
                case 7: goto Le;
                case 8: goto La;
                case 9: goto La;
                case 10: goto La;
                case 11: goto La;
                default: goto L9;
            }
        L9:
            goto L34
        La:
            boolean r2 = f.l.r
            r0 = r0 ^ r2
            goto L35
        Le:
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 24
            if (r0 >= r2) goto L34
            e.j$g r0 = new e.j$g
            int[] r2 = d.p0.T
            r2 = r2[r10]
            android.graphics.drawable.Drawable r2 = r9.C0(r2)
            int[] r3 = d.p0.U
            r10 = r3[r10]
            java.lang.CharSequence r10 = r9.u(r10)
            r3 = 2131100092(0x7f0601bc, float:1.7812556E38)
            java.lang.CharSequence r3 = r9.u(r3)
            r0.<init>(r2, r10, r3)
            r0.setEnabled(r1)
            return r0
        L34:
            r0 = 0
        L35:
            if (r0 == 0) goto L57
            e.j$g r0 = new e.j$g
            int[] r2 = d.p0.T
            r2 = r2[r10]
            android.graphics.drawable.Drawable r2 = r9.C0(r2)
            int[] r3 = d.p0.U
            r10 = r3[r10]
            java.lang.CharSequence r10 = r9.u(r10)
            r3 = 2131100028(0x7f06017c, float:1.7812426E38)
            java.lang.CharSequence r3 = r9.u(r3)
            r0.<init>(r2, r10, r3)
            r0.setEnabled(r1)
            return r0
        L57:
            e.j$g r0 = new e.j$g
            int[] r1 = d.p0.T
            r1 = r1[r10]
            android.graphics.drawable.Drawable r1 = r9.C0(r1)
            int[] r2 = d.p0.U
            r10 = r2[r10]
            java.lang.CharSequence r10 = r9.u(r10)
            r2 = 0
            r0.<init>(r1, r10, r2)
            return r0
        L6e:
            e.j$f r0 = new e.j$f
            int[] r2 = d.p0.T
            r10 = r2[r10]
            android.graphics.drawable.Drawable r5 = r9.C0(r10)
            r10 = 2131099970(0x7f060142, float:1.7812308E38)
            java.lang.CharSequence r6 = r9.u(r10)
            r7 = 0
            a.z r10 = r9.O
            boolean r8 = r10.p(r1)
            r3 = r0
            r4 = r9
            r3.<init>(r5, r6, r7, r8)
            d.n0 r10 = new d.n0
            r10.<init>()
            r0.setOnCheckedChangeListener(r10)
            return r0
        L94:
            e.j$f r7 = new e.j$f
            int[] r1 = d.p0.T
            r10 = r1[r10]
            android.graphics.drawable.Drawable r3 = r9.C0(r10)
            r10 = 2131099926(0x7f060116, float:1.781222E38)
            java.lang.CharSequence r4 = r9.u(r10)
            r5 = 0
            a.z r10 = r9.O
            boolean r6 = r10.p(r0)
            r1 = r7
            r2 = r9
            r1.<init>(r3, r4, r5, r6)
            d.o0 r10 = new d.o0
            r10.<init>()
            r7.setOnCheckedChangeListener(r10)
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: d.p0.h1(int):e.j$k");
    }

    public p0 X1(int i) {
        h().putInt("clicked", i);
        return this;
    }

    @Override // e.j
    protected View i1(int i) {
        int[] iArrQ1 = Q1();
        CharSequence charSequenceU = u(iArrQ1[i]);
        switch (iArrQ1[i]) {
            case R.string.about /* 2131099648 */:
                return new j.g(C0(R.drawable.ic_about), charSequenceU, (CharSequence) null);
            case R.string.backup_restore /* 2131099781 */:
                if (f.l.r) {
                    return new j.g(C0(R.drawable.ic_backup_restore), charSequenceU, (CharSequence) null);
                }
                j.g gVar = new j.g(C0(R.drawable.ic_backup_restore), charSequenceU, u(R.string.pro_only));
                gVar.setEnabled(false);
                return gVar;
            case R.string.get_pro_version /* 2131099930 */:
                return new j.g(C0(R.drawable.ic_get_pro_version), charSequenceU, (CharSequence) null);
            case R.string.help_and_support /* 2131099931 */:
                return new j.g(C0(R.drawable.ic_help), charSequenceU, (CharSequence) null);
            case R.string.logs /* 2131099979 */:
                return new j.g(C0(R.drawable.ic_activity), charSequenceU, (CharSequence) null);
            case R.string.rate /* 2131100030 */:
                return new j.g(C0(R.drawable.ic_rate), charSequenceU, (CharSequence) null);
            case R.string.share /* 2131100079 */:
                return new j.g(C0(R.drawable.ic_share), charSequenceU, (CharSequence) null);
            default:
                return null;
        }
    }

    @Override // e.j
    protected void k1(int i) {
        j0.c c0Var;
        switch (i) {
            case 0:
                c0Var = new c0();
                break;
            case 1:
                c0Var = new j0();
                break;
            case 2:
                c0Var = new w0();
                break;
            case 3:
                c0Var = new w1();
                break;
            case 4:
                c0Var = new r();
                break;
            case 5:
            case 6:
                c0Var = new x1().U1(g().h(), i != 5 ? 1 : 0);
                break;
            case 7:
                if (Build.VERSION.SDK_INT < 24) {
                    return;
                } else {
                    c0Var = new c2();
                }
                break;
            case 8:
                if (!f.l.r) {
                    return;
                } else {
                    c0Var = new u1();
                }
                break;
            case 9:
                if (!f.l.r) {
                    return;
                } else {
                    c0Var = new j();
                }
                break;
            case 10:
                if (!f.l.r) {
                    return;
                } else {
                    c0Var = new q0();
                }
                break;
            case 11:
                if (!f.l.r) {
                    return;
                } else {
                    c0Var = new r1().I1(false);
                }
                break;
            case 12:
                c0Var = new a0().C1(false);
                break;
            case 13:
                c0Var = new d.b().T1(false);
                break;
            case 14:
                c0Var = new y();
                break;
            case 15:
                c0Var = new h0();
                break;
            case 16:
                c0Var = new o1();
                break;
            case 17:
                y1(17, Q1().length);
                return;
            default:
                return;
        }
        O(c0Var);
    }

    @Override // e.j
    protected boolean l1(int i) {
        e.z zVarV;
        int i2;
        if (i == 0) {
            zVarV = new e.z().v(new CharSequence[]{u(R.string.enable), u(R.string.disable)}, !this.O.p(1) ? 1 : 0);
            i2 = 3;
        } else {
            if (i != 1) {
                return false;
            }
            zVarV = new e.z().v(new CharSequence[]{u(R.string.enable), u(R.string.disable)}, !this.O.p(0) ? 1 : 0);
            i2 = 4;
        }
        N(zVarV, i2);
        return true;
    }

    @Override // e.j
    protected void o1(int i) {
        Activity activity;
        String str;
        switch (Q1()[i]) {
            case R.string.about /* 2131099648 */:
                StringBuilder sb = new StringBuilder(256);
                sb.append(f.l.u);
                String strI = ApplicationMain.i();
                String strJ = ApplicationMain.j();
                boolean z = strJ == null || strJ.equals(f.l.t);
                if (!(strI == null || strI.equals(f.l.s)) || !z) {
                    if (z) {
                        sb.append('-');
                        sb.append(f.l.s);
                        sb.append('(');
                        sb.append(strI);
                    } else {
                        sb.append('(');
                        sb.append(strJ);
                    }
                    sb.append(')');
                }
                sb.append("\n\n");
                CharSequence charSequenceU = u(R.string.about_description);
                e.a0 a0Var = new e.a0();
                CharSequence charSequenceU2 = u(R.string.about);
                sb.toString();
                N(a0Var.u(charSequenceU2, charSequenceU), 1);
                return;
            case R.string.backup_restore /* 2131099781 */:
                if (f.l.r) {
                    W1();
                    return;
                }
                return;
            case R.string.get_pro_version /* 2131099930 */:
                activity = getActivity();
                str = f.l.w;
                break;
            case R.string.help_and_support /* 2131099931 */:
                P(new e0(), 5);
                return;
            case R.string.logs /* 2131099979 */:
                if (this.R == null) {
                    this.R = new c();
                    f().registerReceiver(this.R, new IntentFilter(b.s0.O));
                }
                b.s0.B(f(), 26);
                return;
            case R.string.rate /* 2131100030 */:
                activity = getActivity();
                str = f.l.j;
                break;
            case R.string.share /* 2131100079 */:
                CharSequence charSequenceU3 = u(f.l.y);
                Z1(charSequenceU3, charSequenceU3, ((Object) u(R.string.description)) + "\nhttp://play.google.com/store/apps/details?id=" + f.l.j);
                return;
            default:
                return;
        }
        f.p0.v(activity, str);
    }

    @Override // android.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 2) {
            if (Build.VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()) {
                e0(R.string.permission_denied);
            }
            P(new m(), 1);
        }
    }

    @Override // e.j, android.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.O = g().h();
        this.P = new a();
        g().e(this.P);
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // android.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        if (this.P != null) {
            g().o(this.P);
            this.P = null;
        }
        if (this.R != null) {
            f().unregisterReceiver(this.R);
            this.R = null;
        }
    }

    @Override // android.app.Fragment
    public void onPause() {
        super.onPause();
        f.g0 g0Var = this.M;
        if (g0Var != null) {
            g0Var.f();
        }
    }

    @Override // android.app.Fragment
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i == 1) {
            int length = iArr.length;
            int i2 = 0;
            while (true) {
                if (i2 >= length) {
                    break;
                }
                if (iArr[i2] != 0) {
                    e0(R.string.permission_denied);
                    break;
                }
                i2++;
            }
            P(new m(), 1);
        }
    }

    @Override // android.app.Fragment
    public void onResume() {
        super.onResume();
        f.g0 g0Var = this.M;
        if (g0Var != null) {
            g0Var.d();
        }
    }

    @Override // e.j, e.j0.c, android.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        View view = getView();
        if (view != null) {
            view.removeCallbacks(this.N);
        }
        this.N = null;
    }

    @Override // android.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (view == null) {
            return;
        }
        if (!V) {
            this.M = new b(f.h0.a(), 2, 1500L);
        }
        Bundle bundleH = h();
        int i = bundleH.getInt("clicked", -1);
        if (i >= 0) {
            bundleH.putInt("clicked", -1);
            k1(i);
        }
    }
}
