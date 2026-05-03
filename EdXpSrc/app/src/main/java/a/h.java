package a;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import cc.xposed.R;
import f.i;

/* JADX INFO: loaded from: classes.dex */
public abstract class h implements f.l, Parcelable {
    private static h[] B;
    public static final Parcelable.Creator<h> CREATOR = new a();
    private final int A;
    public final int z;

    class a implements Parcelable.Creator<h> {
        a() {
        }

        @Override // android.os.Parcelable.Creator
        /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
        public h createFromParcel(Parcel parcel) {
            f.d0 d0Var = new f.d0(parcel);
            h hVarD = d0Var.i() ? h.d(d0Var) : new o();
            d0Var.k();
            return hVarD;
        }

        @Override // android.os.Parcelable.Creator
        /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
        public h[] newArray(int i) {
            return new h[i];
        }
    }

    public static final class a0 extends h {
        public final boolean C;

        public a0(boolean z) {
            super(33, R.string.condition_orientation);
            this.C = z;
        }

        @Override // a.h
        public String b(Context context) {
            StringBuilder sb = new StringBuilder();
            sb.append('(');
            sb.append(context.getString(this.C ? R.string.orientation_landscape : R.string.orientation_portrait));
            sb.append(')');
            return sb.toString();
        }

        @Override // a.h
        public void e(f.s sVar) {
            super.e(sVar);
            sVar.d(this.C ? 1 : 0);
        }
    }

    public static final class b extends h {
        public b() {
            super(15, R.string.condition_airplane_mode_on);
        }
    }

    public static final class b0 extends h {
        public b0() {
            super(18, R.string.condition_power_connected);
        }
    }

    public static final class c extends h {
        public final h C;
        public final h D;

        public c(h hVar, h hVar2) {
            super(1, R.string.condition_and);
            this.C = hVar;
            this.D = hVar2;
        }

        @Override // a.h
        public String b(Context context) {
            if (this.C == null || this.D == null) {
                return super.b(context);
            }
            return '(' + this.C.b(context) + " && " + this.D.b(context) + ')';
        }

        @Override // a.h
        public void e(f.s sVar) {
            super.e(sVar);
            this.C.e(sVar);
            this.D.e(sVar);
        }
    }

    public static final class c0 extends h {
        public c0() {
            super(24, R.string.condition_ringing);
        }
    }

    public static final class d extends h {
        public final String C;

        private d() {
            this((String) null);
        }

        /* synthetic */ d(a aVar) {
            this();
        }

        public d(String str) {
            super(32, R.string.condition_app_focused);
            this.C = str;
        }

        @Override // a.h
        public String b(Context context) {
            String string = this.C;
            try {
                string = f.z.K(context).G(this.C).toString();
            } catch (Throwable th) {
                f.v.c(th.toString());
            }
            return "(" + string + ' ' + context.getString(R.string.focused) + ')';
        }

        @Override // a.h
        public void e(f.s sVar) {
            super.e(sVar);
            sVar.c(this.C);
        }
    }

    public static final class d0 extends h {
        public d0() {
            super(4, R.string.condition_screen_on);
        }
    }

    public static final class e extends h {
        public e() {
            super(6, R.string.condition_auto_brightness);
        }
    }

    public static final class e0 extends h {
        public e0() {
            super(34, R.string.condition_soft_keyboard_showing);
        }
    }

    public static final class f extends h {
        public f() {
            super(7, R.string.condition_auto_rotation);
        }
    }

    public static final class f0 extends h {
        public f0() {
            super(35, R.string.condition_status_bar_showing);
        }
    }

    public static final class g extends h {
        public static final String[] E = {" == ", " > ", " >= ", " < ", " <= ", " != "};
        public final int C;
        public final int D;

        private g() {
            this(-1, -1);
        }

        public g(int i, int i2) {
            super(26, R.string.condition_battery_level);
            this.C = i;
            this.D = i2;
        }

        /* synthetic */ g(a aVar) {
            this();
        }

        @Override // a.h
        public String b(Context context) {
            return '(' + ((Object) super.c(context)) + E[this.C] + this.D + ')';
        }

        @Override // a.h
        public void e(f.s sVar) {
            super.e(sVar);
            sVar.d(this.C).d(this.D);
        }
    }

    public static final class g0 extends h {
        public g0() {
            super(13, R.string.condition_sync_on);
        }
    }

    /* JADX INFO: renamed from: a.h$h, reason: collision with other inner class name */
    public static final class C0001h extends h {
        public C0001h() {
            super(11, R.string.condition_bluetooth_on);
        }
    }

    public static final class h0 extends h {
        public h0() {
            super(22, R.string.condition_usb_plugged);
        }
    }

    public static final class i extends h {
        public i() {
            super(16, R.string.condition_gesture_enabled);
        }
    }

    public static final class i0 extends h {
        public static final String[] H = {" == ", " > ", " >= ", " < ", " <= ", " != "};
        private final String C;
        private final String D;
        private final String E;
        private f.i F;
        private f.i G;

        private i0() {
            this(null, null, null);
        }

        /* synthetic */ i0(a aVar) {
            this();
        }

        private i0(f.s sVar) {
            this(sVar.e(), sVar.a(), sVar.e());
        }

        /* synthetic */ i0(f.s sVar, a aVar) {
            this(sVar);
        }

        public i0(String str, String str2, String str3) {
            super(37, R.string.condition_variable);
            this.F = null;
            this.G = null;
            this.C = str;
            this.D = str2;
            this.E = str3;
        }

        private int f(i.f fVar) {
            if (this.F == null) {
                this.F = f.i.d(this.C);
            }
            return this.F.a(fVar);
        }

        private int g(i.f fVar) {
            if (this.G == null) {
                this.G = f.i.d(this.E);
            }
            return this.G.a(fVar);
        }

        @Override // a.h
        public String b(Context context) {
            if (this.C == null) {
                return super.b(context);
            }
            return "((" + this.C + ") " + this.D + " (" + this.E + "))";
        }

        @Override // a.h
        public void e(f.s sVar) {
            super.e(sVar);
            sVar.b(this.C).c(this.D).b(this.E);
        }

        public boolean h(i.f fVar) {
            String str = this.D;
            str.hashCode();
            switch (str) {
                case "<":
                    return f(fVar) < g(fVar);
                case ">":
                    return f(fVar) > g(fVar);
                case "!=":
                    return f(fVar) != g(fVar);
                case "<=":
                    return f(fVar) <= g(fVar);
                case "==":
                    return f(fVar) == g(fVar);
                case ">=":
                    return f(fVar) >= g(fVar);
                default:
                    throw new RuntimeException("Invalid operator: " + this.D);
            }
        }
    }

    public static final class j extends h {
        public j() {
            super(21, R.string.condition_headset_plugged);
        }
    }

    public static final class j0 extends h {
        public j0() {
            super(14, R.string.condition_wifi_ap_on);
        }
    }

    public static final class k extends h {
        public final int C;

        public k() {
            this(0);
        }

        public k(int i) {
            super(29, R.string.condition_in_days_of_month);
            this.C = i;
        }

        @Override // a.h
        public String b(Context context) {
            return "(" + ((Object) c(context)) + "(" + f.j.f(context, this.C) + "))";
        }

        @Override // a.h
        public void e(f.s sVar) {
            super.e(sVar);
            sVar.d(this.C);
        }
    }

    public static final class k0 extends h {
        public k0() {
            super(19, R.string.condition_wifi_connected);
        }
    }

    public static final class l extends h {
        public final int C;

        private l() {
            this(0);
        }

        public l(int i) {
            super(28, R.string.condition_in_days_of_week);
            this.C = i;
        }

        /* synthetic */ l(a aVar) {
            this();
        }

        @Override // a.h
        public String b(Context context) {
            return "(" + ((Object) c(context)) + "(" + f.j.p(this.C) + "))";
        }

        @Override // a.h
        public void e(f.s sVar) {
            super.e(sVar);
            sVar.d(this.C);
        }
    }

    public static final class l0 extends h {
        public final String C;

        private l0() {
            this((String) null);
        }

        /* synthetic */ l0(a aVar) {
            this();
        }

        public l0(f.s sVar) {
            this(Uri.decode(sVar.a()));
        }

        public l0(String str) {
            super(38, R.string.condition_wifi_connected_to);
            this.C = str;
        }

        @Override // a.h
        public String b(Context context) {
            return "(" + ((Object) c(context)) + ": " + this.C + ')';
        }

        @Override // a.h
        public void e(f.s sVar) {
            super.e(sVar);
            sVar.c(Uri.encode(this.C));
        }
    }

    public static final class m extends h {
        public final int C;

        public m() {
            this(0);
        }

        public m(int i) {
            super(30, R.string.condition_in_months);
            this.C = i;
        }

        @Override // a.h
        public String b(Context context) {
            return "(" + ((Object) c(context)) + "(" + f.j.k(this.C) + "))";
        }

        @Override // a.h
        public void e(f.s sVar) {
            super.e(sVar);
            sVar.d(this.C);
        }
    }

    public static final class m0 extends h {
        public final String C;

        private m0(String str) {
            super(31, R.string.condition_wifi_connected_to);
            this.C = str;
        }

        /* synthetic */ m0(String str, a aVar) {
            this(str);
        }

        @Override // a.h
        public String b(Context context) {
            return "(" + ((Object) c(context)) + ": " + this.C + ')';
        }

        @Override // a.h
        public void e(f.s sVar) {
            super.e(sVar);
            sVar.c(this.C);
        }

        public l0 f() {
            return new l0(this.C);
        }
    }

    public static final class n extends h {
        public final int C;
        public final int D;
        public final int E;
        public final int F;

        private n() {
            this(0, 0, 0, 0);
        }

        public n(int i, int i2, int i3, int i4) {
            super(27, R.string.condition_time_period);
            this.C = i;
            this.D = i2;
            this.E = i3;
            this.F = i4;
        }

        /* synthetic */ n(a aVar) {
            this();
        }

        private n(f.s sVar) {
            this(sVar.h(), sVar.h(), sVar.h(), sVar.h());
        }

        /* synthetic */ n(f.s sVar, a aVar) {
            this(sVar);
        }

        @Override // a.h
        public String b(Context context) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.C < 10 ? "(0" : "(");
            sb.append(this.C);
            sb.append(this.D < 10 ? ":0" : ":");
            sb.append(this.D);
            sb.append(this.E < 10 ? " - 0" : " - ");
            sb.append(this.E);
            sb.append(this.F >= 10 ? ":" : ":0");
            sb.append(this.F);
            sb.append(")");
            return sb.toString();
        }

        @Override // a.h
        public void e(f.s sVar) {
            super.e(sVar);
            sVar.d(this.C).d(this.D).d(this.E).d(this.F);
        }
    }

    public static final class n0 extends h {
        public n0() {
            super(8, R.string.condition_wifi_on);
        }
    }

    public static final class o extends h {
        public o() {
            super(0, R.string.condition_invalid);
        }
    }

    public static final class p extends h {
        public p() {
            super(17, R.string.condition_key_enabled);
        }
    }

    public static final class q extends h {
        public q() {
            super(5, R.string.condition_keyguard_on);
        }
    }

    public static final class r extends h {
        public r() {
            super(10, R.string.condition_location_on);
        }
    }

    public static final class s extends h {
        public s() {
            super(9, R.string.condition_mobile_data_on);
        }
    }

    public static final class t extends h {
        public t() {
            super(23, R.string.condition_music_active);
        }
    }

    public static final class u extends h {
        public u() {
            super(36, R.string.condition_nav_bar_showing);
        }
    }

    public static final class v extends h {
        public v() {
            super(20, R.string.condition_net_connected);
        }
    }

    public static final class w extends h {
        public w() {
            super(12, R.string.condition_nfc_on);
        }
    }

    public static final class x extends h {
        public final h C;

        public x(h hVar) {
            super(3, R.string.condition_not);
            this.C = hVar;
        }

        @Override // a.h
        public String b(Context context) {
            if (this.C == null) {
                return super.b(context);
            }
            return "(!" + this.C.b(context) + ')';
        }

        @Override // a.h
        public void e(f.s sVar) {
            super.e(sVar);
            this.C.e(sVar);
        }
    }

    public static final class y extends h {
        public y() {
            super(25, R.string.condition_off_hook);
        }
    }

    public static final class z extends h {
        public final h C;
        public final h D;

        public z(h hVar, h hVar2) {
            super(2, R.string.condition_or);
            this.C = hVar;
            this.D = hVar2;
        }

        @Override // a.h
        public String b(Context context) {
            if (this.C == null || this.D == null) {
                return super.b(context);
            }
            return '(' + this.C.b(context) + " || " + this.D.b(context) + ')';
        }

        @Override // a.h
        public void e(f.s sVar) {
            super.e(sVar);
            this.C.e(sVar);
            this.D.e(sVar);
        }
    }

    protected h(int i2, int i3) {
        this.z = i2;
        this.A = i3;
    }

    public static h[] a() {
        h[] hVarArr;
        synchronized (h.class) {
            if (B == null) {
                a aVar = null;
                B = new h[]{new c(null, null), new z(null, null), new x(null), new d0(), new q(), new e(), new f(), new n0(), new s(), new r(), new C0001h(), new w(), new g0(), new j0(), new b(), new i(), new p(), new b0(), new k0(), new v(), new j(), new h0(), new t(), new c0(), new y(), new a0(false), new e0(), new f0(), new u(), new g(aVar), new i0(aVar), new n(aVar), new l(aVar), new k(), new m(), new l0(aVar), new d(aVar)};
            }
            hVarArr = B;
        }
        return hVarArr;
    }

    public static h d(f.s sVar) {
        if (!f.l.r) {
            return new o();
        }
        int iH = sVar.h();
        a aVar = null;
        switch (iH) {
            case 1:
                return new c(d(sVar), d(sVar));
            case 2:
                return new z(d(sVar), d(sVar));
            case 3:
                return new x(d(sVar));
            case 4:
                return new d0();
            case 5:
                return new q();
            case 6:
                return new e();
            case 7:
                return new f();
            case 8:
                return new n0();
            case 9:
                return new s();
            case 10:
                return new r();
            case 11:
                return new C0001h();
            case 12:
                return new w();
            case 13:
                return new g0();
            case 14:
                return new j0();
            case 15:
                return new b();
            case 16:
                return new i();
            case 17:
                return new p();
            case 18:
                return new b0();
            case 19:
                return new k0();
            case 20:
                return new v();
            case 21:
                return new j();
            case 22:
                return new h0();
            case 23:
                return new t();
            case 24:
                return new c0();
            case 25:
                return new y();
            case 26:
                return new g(sVar.h(), sVar.h());
            case 27:
                return new n(sVar, aVar);
            case 28:
                return new l(sVar.h());
            case 29:
                return new k(sVar.h());
            case 30:
                return new m(sVar.h());
            case 31:
                return new m0(sVar.a(), aVar).f();
            case 32:
                return new d(sVar.a());
            case 33:
                return new a0(sVar.h() != 0);
            case 34:
                return new e0();
            case 35:
                return new f0();
            case 36:
                return new u();
            case 37:
                return new i0(sVar, aVar);
            case 38:
                return new l0(sVar);
            default:
                f.v.c("Unknown condition id: " + iH);
                return new o();
        }
    }

    public String b(Context context) {
        return '(' + context.getString(this.A) + ')';
    }

    public CharSequence c(Context context) {
        return context.getText(this.A);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public void e(f.s sVar) {
        sVar.d(this.z);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i2) {
        f.d0 d0VarJ = new f.d0(parcel).j();
        e(d0VarJ);
        d0VarJ.l();
    }
}
