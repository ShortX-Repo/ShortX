package d;

import a.b;
import a.p;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.CompoundButton;
import cc.xposed.R;
import com.jozein.xedgepro.ApplicationMain;
import e.j;
import e.j0;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class o1 extends e.j {
    private a.z M;
    private CharSequence[] N;
    private CharSequence[] O;
    private CharSequence[] P;
    private CharSequence[] Q;

    private static int R1(int i, int i2) {
        return i == 0 ? i2 : i;
    }

    private int S1() {
        int iV = this.M.v(34, 0);
        if (iV >= p.h.f34a.length) {
            return 1;
        }
        return iV;
    }

    public static String T1(int i) {
        return String.format(Locale.ENGLISH, "#%08X", Integer.valueOf(i));
    }

    public static String U1(int i) {
        return String.format(Locale.ENGLISH, "#%06X", Integer.valueOf(i & 16777215));
    }

    private CharSequence V1(float f2) {
        return String.format(Locale.ENGLISH, "%.2f inch, %.2f mm.", Float.valueOf(f2), Float.valueOf(f2 * 25.4f));
    }

    private CharSequence W1(int i, float f2) {
        return V1(this.M.t(i, f2));
    }

    private CharSequence X1() {
        int iU = this.M.u(7);
        if (iU == 0) {
            return u(R.string.never);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < p.e.f31a.length; i++) {
            if (((1 << i) & iU) != 0) {
                if (sb.length() != 0) {
                    sb.append(", ");
                }
                sb.append(this.Q[i]);
            }
        }
        return sb.toString();
    }

    private CharSequence Y1(int i) {
        return u(i != 0 ? i != 1 ? R.string.default_icon : R.string.circle_icon : R.string.colorful_circle_icon);
    }

    private int Z1() {
        int iU = this.M.u(14);
        return iU == 1 ? R.string.force_no_nav_bar : iU == 2 ? R.string.force_has_nav_bar : R.string.system_default;
    }

    private CharSequence a2(int i) {
        return u(i != 1 ? i != 2 ? i != 3 ? i != 4 ? R.string.light_theme : R.string.auto_dark_mode : R.string.black_theme : R.string.light_gray_theme : R.string.dark_theme);
    }

    private int b2() {
        if (this.M.o(25)) {
            return 2;
        }
        return this.M.o(24) ? 1 : 0;
    }

    private int c2() {
        return this.M.o(6) ? this.M.o(7) ? 3 : 1 : this.M.o(7) ? 2 : 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void d2(Context context, CompoundButton compoundButton, boolean z) {
        this.M.o0(context, 8, z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void e2(Context context, CompoundButton compoundButton, boolean z) {
        this.M.o0(context, 33, z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void f2(Context context, CompoundButton compoundButton, boolean z) {
        this.M.o0(context, 10, z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void g2(Context context, CompoundButton compoundButton, boolean z) {
        this.M.o0(context, 26, z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void h2(Context context, CompoundButton compoundButton, boolean z) {
        this.M.o0(context, 11, z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void i2(Context context, CompoundButton compoundButton, boolean z) {
        this.M.o0(context, 34, !z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void j2(Context context, CompoundButton compoundButton, boolean z) {
        this.M.o0(context, 29, z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void k2(Context context, CompoundButton compoundButton, boolean z) {
        this.M.o0(context, 32, z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void l2(CompoundButton compoundButton, boolean z) {
        boolean z2 = !z;
        this.M.o0(compoundButton.getContext(), 27, z2);
        ((j.k) L0(5)).setEnabled(z2);
        e0(z2 ? R.string.gestures_triggers_when_swiping_on_hint : R.string.gestures_triggers_when_swiping_off_hint);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void m2(CompoundButton compoundButton, boolean z) {
        this.M.o0(compoundButton.getContext(), 40, z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void n2(Context context, CompoundButton compoundButton, boolean z) {
        this.M.o0(context, 31, !z);
        L0(17).setEnabled(z);
        b.s0.B(compoundButton.getContext(), 15);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void o2(Context context, CompoundButton compoundButton, boolean z) {
        this.M.o0(context, 42, z);
        ((j.g) L0(29)).setEnabled(z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void p2(Context context, CompoundButton compoundButton, boolean z) {
        this.M.o0(context, 37, z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void q2(Context context, CompoundButton compoundButton, boolean z) {
        this.M.o0(context, 4, z);
        L0(37).setEnabled(z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void r2(Context context, CompoundButton compoundButton, boolean z) {
        this.M.o0(context, 9, z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void s2(Context context, CompoundButton compoundButton, boolean z) {
        this.M.o0(context, 35, z);
        b.s0.F(context, 19, z);
    }

    private CharSequence t2(int i) {
        if (i == 0) {
            return "0: " + ((Object) u(R.string.by_two_finger));
        }
        return i + " s";
    }

    private CharSequence u2(int i) {
        if (i == 0) {
            i = 300;
        }
        return i + " ms";
    }

    private CharSequence v2(int i) {
        if (i == 0) {
            return "0: " + ((Object) u(R.string.system_default));
        }
        return i + " ms";
    }

    private void x2(int i) {
        Context contextF = f();
        if (i == 1) {
            this.M.o0(contextF, 24, true);
        } else {
            if (i == 2) {
                this.M.o0(contextF, 24, true);
                this.M.o0(contextF, 25, true);
                return;
            }
            this.M.o0(contextF, 24, false);
        }
        this.M.o0(contextF, 25, false);
    }

    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.preferences);
    }

    @Override // e.j
    protected int B0() {
        this.M = g().h();
        int i = 0;
        this.N = new CharSequence[]{u(R.string.important), u(R.string.general), u(R.string.verbose)};
        this.P = new CharSequence[]{u(R.string.never), u(R.string.gesture_recording), u(R.string.gesture_injecting), u(R.string.both)};
        this.O = new CharSequence[p.h.f34a.length];
        int i2 = 0;
        while (true) {
            CharSequence[] charSequenceArr = this.O;
            if (i2 >= charSequenceArr.length) {
                break;
            }
            charSequenceArr[i2] = u(p.h.f34a[i2]);
            i2++;
        }
        this.Q = new CharSequence[p.e.f31a.length];
        while (true) {
            CharSequence[] charSequenceArr2 = this.Q;
            if (i >= charSequenceArr2.length) {
                return 52;
            }
            charSequenceArr2[i] = u(p.e.f31a[i]);
            i++;
        }
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    @Override // e.j0.c
    protected void J(Bundle bundle, int i) {
        float fT;
        j.k kVar;
        CharSequence charSequenceV1;
        int i2;
        int i3;
        View viewL0;
        j.k kVar2;
        CharSequence string;
        int i4;
        StringBuilder sb;
        j.g gVar;
        ColorDrawable colorDrawable;
        boolean z;
        if (bundle == null) {
        }
        Context contextF = f();
        z = true;
        boolean z2 = true;
        switch (i) {
            case 0:
            case 1:
                int i5 = i != 0 ? 4 : 1;
                a.m mVarB = a.m.b();
                int iA = mVarB.a(i5);
                int i6 = bundle.getInt("result", iA);
                if (iA != i6) {
                    try {
                        mVarB.f(i5, i6);
                        getActivity().recreate();
                    } catch (Throwable th) {
                        g0(th);
                        return;
                    }
                }
                break;
            case 2:
                float f2 = bundle.getFloat("result_inch", this.M.t(36, 0.12f));
                this.M.r0(contextF, 36, f2);
                float f3 = f2 <= 0.0f ? 0.12f : f2;
                ((j.k) L0(2)).setSubText(V1(f3));
                fT = this.M.t(37, f3);
                ((j.k) L0(3)).setSubText(V1(fT));
                b.s0.C(contextF, 8, -1);
                break;
            case 3:
                fT = bundle.getFloat("result_inch", this.M.t(37, 0.12f));
                this.M.r0(contextF, 37, fT);
                if (fT <= 0.0f) {
                    fT = this.M.t(36, 0.12f);
                }
                ((j.k) L0(3)).setSubText(V1(fT));
                b.s0.C(contextF, 8, -1);
                break;
            case 5:
                float f4 = bundle.getFloat("result_inch", this.M.t(38, 0.0f));
                this.M.r0(contextF, 38, f4);
                if (f4 <= 0.0f) {
                    f4 = 0.3f;
                }
                kVar = (j.k) L0(5);
                charSequenceV1 = V1(f4);
                kVar.setSubText(charSequenceV1);
                break;
            case 8:
                int i7 = bundle.getInt("result", -1);
                if (i7 != -1) {
                    i2 = i7 * 50;
                    this.M.s0(contextF, 10, i2);
                    i3 = 8;
                    viewL0 = L0(i3);
                    kVar = (j.k) viewL0;
                    charSequenceV1 = v2(i2);
                    kVar.setSubText(charSequenceV1);
                }
                break;
            case 9:
                int i8 = bundle.getInt("result", -1);
                if (i8 != -1) {
                    i2 = i8 * 50;
                    this.M.s0(contextF, 11, i2);
                    i3 = 9;
                    viewL0 = L0(i3);
                    kVar = (j.k) viewL0;
                    charSequenceV1 = v2(i2);
                    kVar.setSubText(charSequenceV1);
                }
                break;
            case 10:
                int i9 = bundle.getInt("result", -1);
                if (i9 != -1) {
                    i2 = i9 * 50;
                    this.M.s0(contextF, 12, i2);
                    viewL0 = L0(10);
                    kVar = (j.k) viewL0;
                    charSequenceV1 = v2(i2);
                    kVar.setSubText(charSequenceV1);
                }
                break;
            case 11:
                int i10 = bundle.getInt("result", -1);
                if (i10 != -1) {
                    i2 = i10 * 50;
                    this.M.s0(contextF, 13, i2);
                    i3 = 11;
                    viewL0 = L0(i3);
                    kVar = (j.k) viewL0;
                    charSequenceV1 = v2(i2);
                    kVar.setSubText(charSequenceV1);
                }
                break;
            case 13:
                int i11 = bundle.getInt("result", -1);
                if (i11 != -1) {
                    int i12 = i11 * 50;
                    this.M.s0(contextF, 29, i12);
                    kVar = (j.k) L0(13);
                    charSequenceV1 = u2(i12);
                    kVar.setSubText(charSequenceV1);
                }
                break;
            case 14:
                int i13 = bundle.getInt("result", -1);
                if (i13 != -1) {
                    this.M.s0(contextF, 27, i13);
                    kVar = (j.k) L0(14);
                    charSequenceV1 = t2(i13);
                    kVar.setSubText(charSequenceV1);
                }
                break;
            case 15:
                int i14 = bundle.getInt("result", -1);
                if (i14 > 0) {
                    int i15 = i14 * 10;
                    this.M.s0(contextF, 17, i15);
                    kVar = (j.k) L0(15);
                    charSequenceV1 = Integer.toString(i15);
                    kVar.setSubText(charSequenceV1);
                }
                break;
            case 17:
                int i16 = bundle.getInt("result", -1);
                if (i16 >= 0) {
                    this.M.p0(contextF, i16);
                    b.s0.B(f(), 15);
                    kVar2 = (j.k) L0(17);
                    string = Integer.toString(this.M.r());
                    kVar2.setSubText(string);
                }
                break;
            case 20:
                int i17 = bundle.getInt("result", -1);
                if (i17 != -1) {
                    i4 = i17 * 10;
                    this.M.s0(contextF, 15, i4);
                    kVar = (j.k) L0(20);
                    sb = new StringBuilder();
                    sb.append(i4);
                    sb.append("%");
                    charSequenceV1 = sb.toString();
                    kVar.setSubText(charSequenceV1);
                }
                break;
            case 21:
                int iR1 = R1(bundle.getInt("result"), -14509620);
                this.M.s0(contextF, 16, iR1);
                gVar = (j.g) L0(i);
                gVar.setSubText(T1(iR1));
                colorDrawable = new ColorDrawable(iR1);
                gVar.setImageDrawable(colorDrawable);
                break;
            case 22:
                int i18 = bundle.getInt("result", -1);
                if (i18 != -1) {
                    i4 = i18 * 5;
                    this.M.s0(contextF, 26, i4);
                    kVar = (j.k) L0(22);
                    sb = new StringBuilder();
                    sb.append(i4);
                    sb.append("%");
                    charSequenceV1 = sb.toString();
                    kVar.setSubText(charSequenceV1);
                }
                break;
            case 23:
                int iR12 = R1(bundle.getInt("result"), -14509620);
                this.M.s0(contextF, 4, iR12);
                gVar = (j.g) L0(i);
                gVar.setSubText(T1(iR12));
                colorDrawable = new ColorDrawable(iR12);
                gVar.setImageDrawable(colorDrawable);
                break;
            case 24:
                int iR13 = R1(bundle.getInt("result"), -1);
                this.M.s0(contextF, 30, iR13);
                gVar = (j.g) L0(i);
                gVar.setSubText(U1(iR13));
                colorDrawable = new ColorDrawable(iR13);
                gVar.setImageDrawable(colorDrawable);
                break;
            case 25:
                int i19 = bundle.getInt("result", -1);
                if (i19 != -1) {
                    i4 = i19 * 5;
                    this.M.s0(contextF, 28, i4);
                    kVar = (j.k) L0(25);
                    sb = new StringBuilder();
                    sb.append(i4);
                    sb.append("%");
                    charSequenceV1 = sb.toString();
                    kVar.setSubText(charSequenceV1);
                }
                break;
            case 26:
                int iR14 = R1(bundle.getInt("result"), -16764873);
                this.M.s0(contextF, 2, iR14);
                gVar = (j.g) L0(i);
                gVar.setSubText(T1(iR14));
                colorDrawable = new ColorDrawable(iR14);
                gVar.setImageDrawable(colorDrawable);
                break;
            case 27:
                int iR15 = R1(bundle.getInt("result"), -1);
                this.M.s0(contextF, 31, iR15);
                gVar = (j.g) L0(i);
                gVar.setSubText(U1(iR15));
                colorDrawable = new ColorDrawable(iR15);
                gVar.setImageDrawable(colorDrawable);
                break;
            case 29:
                int iR16 = R1(bundle.getInt("result"), -15303552);
                this.M.s0(contextF, 32, iR16);
                gVar = (j.g) L0(i);
                gVar.setSubText(U1(iR16));
                colorDrawable = new ColorDrawable(iR16);
                gVar.setImageDrawable(colorDrawable);
                break;
            case 30:
                int iV = this.M.v(33, -16764873);
                int iR17 = R1(bundle.getInt("result"), iV);
                if (iV != iR17) {
                    this.M.s0(contextF, 33, iR17);
                    gVar = (j.g) L0(i);
                    gVar.setSubText(T1(iR17));
                    colorDrawable = new ColorDrawable(iR17);
                    gVar.setImageDrawable(colorDrawable);
                }
                break;
            case 31:
                int iR18 = R1(bundle.getInt("result"), 1428300288);
                this.M.v0(contextF, iR18);
                gVar = (j.g) L0(i);
                gVar.setSubText(T1(iR18));
                colorDrawable = new ColorDrawable(iR18);
                gVar.setImageDrawable(colorDrawable);
                break;
            case 32:
                int iR19 = R1(bundle.getInt("result"), 2130739111);
                this.M.s0(contextF, 22, iR19);
                gVar = (j.g) L0(i);
                gVar.setSubText(T1(iR19));
                colorDrawable = new ColorDrawable(iR19);
                gVar.setImageDrawable(colorDrawable);
                break;
            case 33:
                int iR110 = R1(bundle.getInt("result"), -16764873);
                this.M.s0(contextF, 25, iR110);
                gVar = (j.g) L0(i);
                gVar.setSubText(U1(iR110));
                colorDrawable = new ColorDrawable(iR110);
                gVar.setImageDrawable(colorDrawable);
                break;
            case 37:
                int iV2 = this.M.v(23, 0);
                int i20 = bundle.getInt("result", iV2);
                if (iV2 != i20) {
                    this.M.s0(contextF, 23, i20);
                    kVar = (j.k) L0(37);
                    charSequenceV1 = v.M[i20];
                    kVar.setSubText(charSequenceV1);
                }
                break;
            case 38:
                int i21 = bundle.getInt("result", -1);
                if (i21 >= 0) {
                    this.M.s0(f(), 34, i21);
                    int iS1 = S1();
                    ((j.k) L0(38)).setSubText(this.O[iS1]);
                    L0(39).setEnabled(iS1 == 1);
                }
                break;
            case 39:
                int i22 = bundle.getInt("result", -1);
                if (i22 != -1) {
                    this.M.s0(contextF, 6, i22);
                    ((j.k) L0(39)).setSubText(Integer.toString(this.M.v(6, 50)));
                    if (S1() == 1) {
                        b.s0.B(contextF, 10);
                    }
                }
                break;
            case 41:
                int i23 = bundle.getInt("result");
                if (i23 == 1) {
                    z = true;
                    z2 = false;
                } else if (i23 == 2) {
                    z = false;
                } else if (i23 == 3) {
                    z = true;
                }
                this.M.o0(contextF, 6, z);
                this.M.o0(contextF, 7, z2);
                kVar = (j.k) L0(41);
                charSequenceV1 = this.P[i23];
                kVar.setSubText(charSequenceV1);
                break;
            case 42:
                int i24 = bundle.getInt("result", -1);
                if (i24 >= 0) {
                    this.M.s0(contextF, 7, i24);
                    kVar2 = (j.k) L0(42);
                    string = X1();
                    kVar2.setSubText(string);
                }
                break;
            case 43:
                int i25 = bundle.getInt("result", -1);
                if (i25 >= 0) {
                    x2(i25);
                    kVar2 = (j.k) L0(43);
                    string = this.N[b2()];
                    kVar2.setSubText(string);
                }
                break;
            case 51:
                int iU = this.M.u(14);
                int i26 = bundle.getInt("result", iU);
                if (iU != i26) {
                    this.M.s0(contextF, 14, i26);
                    b.s0.B(contextF, 28);
                    ((j.k) L0(51)).setSubText(Z1());
                }
                break;
        }
    }

    @Override // e.j
    protected void k1(int i) {
        j0.b bVarV;
        j0.c zVar;
        int i2;
        int i3 = 1;
        switch (i) {
            case 0:
                CharSequence[] charSequenceArr = {a2(0), a2(1), a2(2), a2(3), a2(4)};
                int iA = a.m.b().a(1);
                if (iA != 1 && iA != 2 && iA != 3 && iA != 4) {
                    iA = 0;
                }
                N(new e.z().v(charSequenceArr, iA), 0);
                break;
            case 1:
                CharSequence[] charSequenceArr2 = {Y1(0), Y1(1), Y1(2)};
                int iA2 = a.m.b().a(4);
                bVarV = new e.z().v(charSequenceArr2, (iA2 == 0 || iA2 == 1 || iA2 == 2) ? iA2 : 0);
                N(bVarV, i3);
                break;
            case 2:
                N(new c.n().B(u(R.string.edge_width), this.M.t(36, 0.12f), 0.8f), 2);
                break;
            case 3:
                c.n nVar = new c.n();
                CharSequence charSequenceU = u(R.string.edge_height);
                a.z zVar2 = this.M;
                N(nVar.B(charSequenceU, zVar2.t(37, zVar2.t(36, 0.12f)), 0.8f), 3);
                break;
            case 4:
            case 6:
            case 7:
            case 12:
            case 16:
            case 28:
            case 35:
            case 36:
            case 40:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
                ((j.ViewOnTouchListenerC0005j) L0(i)).h();
                break;
            case 5:
                if (this.M.o(27)) {
                    N(new c.n().B(u(R.string.distance_to_trigger_swipe), this.M.t(38, 0.3f), 1.0f), 5);
                }
                break;
            case 8:
                int iU = this.M.u(10);
                if (iU == 0) {
                    iU = ViewConfiguration.getDoubleTapTimeout();
                }
                bVarV = new e.g0().y(u(R.string.gesture_double_tap_timeout), null, 0, iU / 50, 12, 50);
                i3 = 8;
                N(bVarV, i3);
                break;
            case 9:
                int iU2 = this.M.u(11);
                if (iU2 == 0) {
                    iU2 = ViewConfiguration.getLongPressTimeout();
                }
                bVarV = new e.g0().y(u(R.string.gesture_long_press_timeout), null, 0, iU2 / 50, 20, 50);
                i3 = 9;
                N(bVarV, i3);
                break;
            case 10:
                int iU3 = this.M.u(12);
                if (iU3 == 0) {
                    iU3 = ViewConfiguration.getDoubleTapTimeout();
                }
                bVarV = new e.g0().y(u(R.string.key_double_click_timeout), null, 0, iU3 / 50, 12, 50);
                i3 = 10;
                N(bVarV, i3);
                break;
            case 11:
                int iU4 = this.M.u(13);
                if (iU4 == 0) {
                    iU4 = ViewConfiguration.getLongPressTimeout();
                }
                bVarV = new e.g0().y(u(R.string.key_long_click_timeout), null, 0, iU4 / 50, 20, 50);
                i3 = 11;
                N(bVarV, i3);
                break;
            case 13:
                bVarV = new e.g0().y(u(R.string.key_long_click_timeout), null, 2, this.M.v(29, 300) / 50, 20, 50);
                i3 = 13;
                N(bVarV, i3);
                break;
            case 14:
                N(new e.g0().x(u(R.string.floating_widget_timeout), null, 0, this.M.u(27), 60), 14);
                break;
            case 15:
                bVarV = new e.g0().y(u(R.string.scroll_speed), null, 1, this.M.v(17, 100) / 10, 40, 10);
                i3 = 15;
                N(bVarV, i3);
                break;
            case 17:
                if (!this.M.o(31)) {
                    bVarV = new e.g0().x(u(R.string.clip_history_size), null, 0, this.M.r(), 99999);
                    i3 = 17;
                    N(bVarV, i3);
                }
                break;
            case 18:
                zVar = new z();
                i2 = 18;
                P(zVar, i2);
                break;
            case 19:
                zVar = new x0();
                i2 = 19;
                P(zVar, i2);
                break;
            case 20:
                bVarV = new e.g0().y(u(R.string.gesture_pointer_scale), null, 20, this.M.v(15, 250) / 10, 50, 10);
                i3 = 20;
                N(bVarV, i3);
                break;
            case 21:
                bVarV = new e.o().H(this.M.v(16, -14509620), 255);
                i3 = 21;
                N(bVarV, i3);
                break;
            case 22:
                bVarV = new e.g0().y(u(R.string.pie_scale), null, 15, this.M.v(26, 100) / 5, 40, 5);
                i3 = 22;
                N(bVarV, i3);
                break;
            case 23:
                bVarV = new e.o().H(this.M.v(4, -14509620), 255);
                i3 = 23;
                N(bVarV, i3);
                break;
            case 24:
                bVarV = new e.o().H(this.M.v(30, -1), 0);
                i3 = 24;
                N(bVarV, i3);
                break;
            case 25:
                bVarV = new e.g0().y(u(R.string.panel_scale), null, 15, this.M.v(28, 100) / 5, 40, 5);
                i3 = 25;
                N(bVarV, i3);
                break;
            case 26:
                bVarV = new e.o().H(this.M.v(2, -16764873), 255);
                i3 = 26;
                N(bVarV, i3);
                break;
            case 27:
                N(new e.o().H(this.M.v(31, -1), 0), 27);
                break;
            case 29:
                if (this.M.o(42)) {
                    N(new e.o().H(this.M.v(32, -15303552), 0), 29);
                }
                break;
            case 30:
                bVarV = new e.o().H(this.M.v(33, -16764873), 255);
                i3 = 30;
                N(bVarV, i3);
                break;
            case 31:
                if (ApplicationMain.isModuleActivated() || this.M.C() == 0) {
                    N(new e.o().H(this.M.B(), 208), 31);
                } else {
                    e0(R.string.module_not_activated);
                }
                break;
            case 32:
                bVarV = new e.o().H(this.M.v(22, 2130739111), 255);
                i3 = 32;
                N(bVarV, i3);
                break;
            case 33:
                bVarV = new e.o().H(this.M.v(25, -16764873), 0);
                i3 = 33;
                N(bVarV, i3);
                break;
            case 34:
                zVar = new v0();
                i2 = 34;
                P(zVar, i2);
                break;
            case 37:
                if (this.M.o(4)) {
                    P(new v(), 37);
                }
                break;
            case 38:
                bVarV = new e.z().v(this.O, S1());
                i3 = 38;
                N(bVarV, i3);
                break;
            case 39:
                if (S1() == 1) {
                    bVarV = new e.g0().w(u(R.string.vibrate_strength), null, this.M.v(6, 50), 100);
                    i3 = 39;
                    N(bVarV, i3);
                }
                break;
            case 41:
                bVarV = new e.z().v(this.P, c2());
                i3 = 41;
                N(bVarV, i3);
                break;
            case 42:
                bVarV = new e.b0().v(this.Q, this.M.u(7));
                i3 = 42;
                N(bVarV, i3);
                break;
            case 43:
                bVarV = new e.z().v(this.N, b2());
                i3 = 43;
                N(bVarV, i3);
                break;
            case 51:
                bVarV = new e.z().v(new CharSequence[]{u(R.string.system_default), u(R.string.force_no_nav_bar), u(R.string.force_has_nav_bar)}, this.M.u(14));
                i3 = 51;
                N(bVarV, i3);
                break;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // e.j
    /* JADX INFO: renamed from: w2, reason: merged with bridge method [inline-methods] */
    public j.k h1(int i) {
        final Context contextM0 = M0();
        switch (i) {
            case 0:
                return new j.k(u(R.string.theme), a2(a.m.b().a(1)));
            case 1:
                return new j.k(u(R.string.icon_style_in_app), Y1(a.m.b().a(4)));
            case 2:
                return new j.k(u(R.string.edge_width), W1(36, 0.12f));
            case 3:
                return new j.k(u(R.string.edge_height), W1(37, this.M.t(36, 0.12f)));
            case 4:
                j.ViewOnTouchListenerC0005j viewOnTouchListenerC0005j = new j.ViewOnTouchListenerC0005j(u(R.string.swipe_triggers_after_finger_upped), null, !this.M.o(27));
                viewOnTouchListenerC0005j.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: d.y0
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        this.f377a.l2(compoundButton, z);
                    }
                });
                return viewOnTouchListenerC0005j;
            case 5:
                j.k kVar = new j.k(u(R.string.distance_to_trigger_swipe), W1(38, 0.3f));
                kVar.setEnabled(this.M.o(27));
                return kVar;
            case 6:
                j.ViewOnTouchListenerC0005j viewOnTouchListenerC0005j2 = new j.ViewOnTouchListenerC0005j(u(R.string.disable_gesture_on_soft_keyboard), null, this.M.o(8));
                viewOnTouchListenerC0005j2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: d.n1
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        this.f364a.d2(contextM0, compoundButton, z);
                    }
                });
                return viewOnTouchListenerC0005j2;
            case 7:
                j.ViewOnTouchListenerC0005j viewOnTouchListenerC0005j3 = new j.ViewOnTouchListenerC0005j(u(R.string.disable_gesture_on_keyguard), null, this.M.o(33));
                viewOnTouchListenerC0005j3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: d.e1
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        this.f334a.e2(contextM0, compoundButton, z);
                    }
                });
                return viewOnTouchListenerC0005j3;
            case 8:
                return new j.k(u(R.string.gesture_double_tap_timeout), v2(this.M.u(10)));
            case 9:
                return new j.k(u(R.string.gesture_long_press_timeout), v2(this.M.u(11)));
            case 10:
                return new j.k(u(R.string.key_double_click_timeout), v2(this.M.u(12)));
            case 11:
                return new j.k(u(R.string.key_long_click_timeout), v2(this.M.u(13)));
            case 12:
                j.ViewOnTouchListenerC0005j viewOnTouchListenerC0005j4 = new j.ViewOnTouchListenerC0005j(u(R.string.sub_gesture_triggers_when_swiping), null, this.M.o(40));
                viewOnTouchListenerC0005j4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: d.f1
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        this.f336a.m2(compoundButton, z);
                    }
                });
                return viewOnTouchListenerC0005j4;
            case 13:
                return new j.k(u(R.string.gesture_hold_timeout), u2(this.M.v(29, 300)));
            case 14:
                return new j.k(u(R.string.floating_widget_timeout), t2(this.M.u(27)));
            case 15:
                return new j.k(u(R.string.scroll_speed), Integer.toString(this.M.v(17, 100)));
            case 16:
                j.ViewOnTouchListenerC0005j viewOnTouchListenerC0005j5 = new j.ViewOnTouchListenerC0005j(u(R.string.save_clip_data), null, !this.M.o(31));
                viewOnTouchListenerC0005j5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: d.z0
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        this.f380a.n2(contextM0, compoundButton, z);
                    }
                });
                return viewOnTouchListenerC0005j5;
            case 17:
                j.k kVar2 = new j.k(u(R.string.clip_history_size), Integer.toString(this.M.r()));
                kVar2.setEnabled(!this.M.o(31));
                return kVar2;
            case 18:
                return new j.k(this, u(R.string.action_game_mode));
            case 19:
                return new j.k(this, u(R.string.gesture_pointer_modes));
            case 20:
                return new j.k(u(R.string.gesture_pointer_scale), this.M.v(15, 250) + "%");
            case 21:
                int iV = this.M.v(16, -14509620);
                return new j.g(u(R.string.gesture_pointer_color), T1(iV), new ColorDrawable(iV));
            case 22:
                return new j.k(u(R.string.pie_scale), this.M.v(26, 100) + "%");
            case 23:
                int iV2 = this.M.v(4, -14509620);
                return new j.g(u(R.string.pie_color), T1(iV2), new ColorDrawable(iV2));
            case 24:
                int iV3 = this.M.v(30, -1);
                return new j.g(u(R.string.pie_icon_color), U1(iV3), new ColorDrawable(iV3));
            case 25:
                return new j.k(u(R.string.panel_scale), this.M.v(28, 100) + "%");
            case 26:
                int iV4 = this.M.v(2, -16764873);
                return new j.g(u(R.string.panel_color), T1(iV4), new ColorDrawable(iV4));
            case 27:
                int iV5 = this.M.v(31, -1);
                return new j.g(u(R.string.panel_icon_color), U1(iV5), new ColorDrawable(iV5));
            case 28:
                j.ViewOnTouchListenerC0005j viewOnTouchListenerC0005j6 = new j.ViewOnTouchListenerC0005j(u(R.string.circle_icon_on_panels), null, this.M.o(42));
                viewOnTouchListenerC0005j6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: d.a1
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        this.f322a.o2(contextM0, compoundButton, z);
                    }
                });
                return viewOnTouchListenerC0005j6;
            case 29:
                int iV6 = this.M.v(32, -15303552);
                j.g gVar = new j.g(u(R.string.panel_circle_background_color), U1(iV6), new ColorDrawable(iV6));
                gVar.setEnabled(this.M.o(42));
                return gVar;
            case 30:
                int iV7 = this.M.v(33, -16764873);
                return new j.g(u(R.string.drawer_color), T1(iV7), new ColorDrawable(iV7));
            case 31:
                int iB = this.M.B();
                return new j.g(u(R.string.screen_filter_color), T1(iB), new ColorDrawable(iB));
            case 32:
                int iV8 = this.M.v(22, 2130739111);
                return new j.g(u(R.string.slide_adjust_color), T1(iV8), new ColorDrawable(iV8));
            case 33:
                int iV9 = this.M.v(25, -16764873);
                return new j.g(u(R.string.clipboard_color), U1(iV9), new ColorDrawable(iV9));
            case 34:
                return new j.k(this, u(R.string.pen_colors));
            case 35:
                j.ViewOnTouchListenerC0005j viewOnTouchListenerC0005j7 = new j.ViewOnTouchListenerC0005j(u(R.string.do_not_show_pen_toolbar_automatically), u(R.string.do_not_show_pen_toolbar_automatically_hint), this.M.o(37));
                viewOnTouchListenerC0005j7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: d.h1
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        this.f341a.p2(contextM0, compoundButton, z);
                    }
                });
                return viewOnTouchListenerC0005j7;
            case 36:
                j.ViewOnTouchListenerC0005j viewOnTouchListenerC0005j8 = new j.ViewOnTouchListenerC0005j(u(R.string.sound_effect), null, this.M.o(4));
                viewOnTouchListenerC0005j8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: d.g1
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        this.f339a.q2(contextM0, compoundButton, z);
                    }
                });
                return viewOnTouchListenerC0005j8;
            case 37:
                j.k kVar3 = new j.k(u(R.string.effect_sound), v.M[this.M.v(23, 0)]);
                kVar3.setEnabled(this.M.o(4));
                return kVar3;
            case 38:
                return new j.k(u(R.string.vibrate_effect), this.O[S1()]);
            case 39:
                j.k kVar4 = new j.k(u(R.string.vibrate_strength), Integer.toString(this.M.v(6, 50)));
                kVar4.setEnabled(S1() == 1);
                return kVar4;
            case 40:
                j.ViewOnTouchListenerC0005j viewOnTouchListenerC0005j9 = new j.ViewOnTouchListenerC0005j(u(R.string.effect_only_screen_on), null, this.M.o(9));
                viewOnTouchListenerC0005j9.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: d.c1
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        this.f330a.r2(contextM0, compoundButton, z);
                    }
                });
                return viewOnTouchListenerC0005j9;
            case 41:
                return new j.k(u(R.string.visual_effect), this.P[c2()]);
            case 42:
                return new j.k(u(R.string.notify_for_actions), X1());
            case 43:
                return new j.k(u(R.string.toast_level), this.N[b2()]);
            case 44:
                j.ViewOnTouchListenerC0005j viewOnTouchListenerC0005j10 = new j.ViewOnTouchListenerC0005j(u(R.string.windows_not_focusable), u(R.string.windows_not_focusable_hint), this.M.o(35));
                viewOnTouchListenerC0005j10.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: d.l1
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        this.f352a.s2(contextM0, compoundButton, z);
                    }
                });
                return viewOnTouchListenerC0005j10;
            case 45:
                j.ViewOnTouchListenerC0005j viewOnTouchListenerC0005j11 = new j.ViewOnTouchListenerC0005j(u(R.string.show_text_on_shortcut_panel), null, this.M.o(10));
                viewOnTouchListenerC0005j11.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: d.d1
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        this.f332a.f2(contextM0, compoundButton, z);
                    }
                });
                return viewOnTouchListenerC0005j11;
            case 46:
                j.ViewOnTouchListenerC0005j viewOnTouchListenerC0005j12 = new j.ViewOnTouchListenerC0005j(u(R.string.show_text_on_side_bar), null, this.M.o(26));
                viewOnTouchListenerC0005j12.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: d.j1
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        this.f347a.g2(contextM0, compoundButton, z);
                    }
                });
                return viewOnTouchListenerC0005j12;
            case 47:
                j.ViewOnTouchListenerC0005j viewOnTouchListenerC0005j13 = new j.ViewOnTouchListenerC0005j(u(R.string.show_text_on_other_drawers), null, this.M.o(11));
                viewOnTouchListenerC0005j13.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: d.k1
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        this.f349a.h2(contextM0, compoundButton, z);
                    }
                });
                return viewOnTouchListenerC0005j13;
            case 48:
                j.ViewOnTouchListenerC0005j viewOnTouchListenerC0005j14 = new j.ViewOnTouchListenerC0005j(u(R.string.disable_auto_brightness_when_brightness_adjusted), null, !this.M.o(34));
                viewOnTouchListenerC0005j14.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: d.i1
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        this.f343a.i2(contextM0, compoundButton, z);
                    }
                });
                return viewOnTouchListenerC0005j14;
            case 49:
                j.ViewOnTouchListenerC0005j viewOnTouchListenerC0005j15 = new j.ViewOnTouchListenerC0005j(u(R.string.auto_freeze_only_app_died), null, this.M.o(29));
                viewOnTouchListenerC0005j15.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: d.b1
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        this.f326a.j2(contextM0, compoundButton, z);
                    }
                });
                return viewOnTouchListenerC0005j15;
            case 50:
                j.ViewOnTouchListenerC0005j viewOnTouchListenerC0005j16 = new j.ViewOnTouchListenerC0005j(u(R.string.allows_perform_by_broadcast), null, this.M.o(32));
                viewOnTouchListenerC0005j16.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: d.m1
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        this.f361a.k2(contextM0, compoundButton, z);
                    }
                });
                return viewOnTouchListenerC0005j16;
            case 51:
                return new j.k(this, R.string.force_nav_bar_state, Z1());
            default:
                return null;
        }
    }
}
