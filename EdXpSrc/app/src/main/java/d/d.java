package d;

import a.b;
import android.content.Context;
import android.os.Bundle;
import cc.xposed.R;
import e.j;

/* JADX INFO: loaded from: classes.dex */
public class d extends e implements j.d {
    private a.b[] M = null;

    interface a {
    }

    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.select_action);
    }

    @Override // e.j
    protected int B0() {
        int i = h().getInt("flag", -1);
        if (i == -1) {
            i = 1;
        }
        this.M = a.b.i(i);
        return (f.l.r && (p() instanceof a)) ? this.M.length : this.M.length - 1;
    }

    @Override // d.e
    protected void B1(Bundle bundle, int i) {
        a.b f1Var;
        int i2;
        if (bundle == null) {
            return;
        }
        if (i == 1) {
            U(bundle);
        } else {
            if (i != 2) {
                if (i == 77) {
                    String string = bundle.getString("result");
                    if (string == null) {
                        return;
                    } else {
                        f1Var = new b.f1(string);
                    }
                } else if (i != 92 || (i2 = bundle.getInt("result", 0) * 10) <= 0) {
                    return;
                } else {
                    f1Var = new b.n3(i2);
                }
                C1(f1Var);
                return;
            }
            CharSequence charSequence = bundle.getCharSequence("result");
            if (charSequence != null) {
                try {
                    C1(new b.g0(Integer.parseInt(charSequence.toString())));
                    return;
                } catch (NumberFormatException e2) {
                    f.v.d(e2);
                    return;
                }
            }
        }
        L();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // e.j
    /* JADX INFO: renamed from: E1, reason: merged with bridge method [inline-methods] */
    public j.k h1(int i) {
        Context contextM0 = M0();
        a.b bVar = this.M[i];
        if (f.l.r || !(bVar instanceof f.e0)) {
            return new j.g(D0(bVar), bVar.p(contextM0), (CharSequence) null);
        }
        j.g gVar = new j.g(D0(bVar), bVar.p(contextM0), u(R.string.pro_only));
        gVar.setEnabled(false);
        return gVar;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // e.j
    /* JADX INFO: renamed from: F1, reason: merged with bridge method [inline-methods] */
    public j.k i1(int i) {
        return new j.k(this, ((b.u0) this.M[I0()]).a(M0(), i));
    }

    public d G1(int i) {
        h().putInt("flag", i);
        return this;
    }

    public d H1(int i, CharSequence charSequence) {
        b0(charSequence);
        return G1(i);
    }

    public d I1(int i, CharSequence charSequence, CharSequence charSequence2) {
        return H1(i, ((Object) charSequence) + "/" + ((Object) charSequence2));
    }

    public d J1(int i, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3) {
        return H1(i, ((Object) charSequence) + "/" + ((Object) charSequence2) + "/" + ((Object) charSequence3));
    }

    @Override // e.j
    protected boolean N0(int i) {
        return this.M[i] instanceof b.u0;
    }

    @Override // e.j.d
    public String[] a() {
        Context contextM0 = M0();
        int length = this.M.length;
        String[] strArr = new String[length];
        for (int i = 0; i < length; i++) {
            strArr[i] = this.M[i].p(contextM0).toString();
        }
        return strArr;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // e.j
    protected void k1(int i) {
        b.a0 a0Var = this.M[i];
        if (f.l.r || !(a0Var instanceof f.e0)) {
            if (a0Var instanceof b.u0) {
                y1(i, ((b.u0) a0Var).b());
                return;
            }
            int i2 = a0Var.z;
            if (i2 == 48) {
                P(new f().b0(o()), 1);
                return;
            }
            if (i2 == 72) {
                P(new t().K1(new b.d(a.b.e()), h().getInt("flag")).b0(o()), 1);
                return;
            }
            if (i2 != 75) {
                if (i2 == 86) {
                    P(new a2().b0(o()), 1);
                    return;
                }
                if (i2 == 105) {
                    P(new i0().b0(o()), 1);
                    return;
                }
                if (i2 == 115) {
                    P(new k().b0(o()), 1);
                    return;
                }
                if (i2 == 53) {
                    P(new a0().C1(true).b0(o()), 1);
                    return;
                }
                if (i2 == 54) {
                    e.y yVar = new e.y();
                    yVar.J(u(R.string.enter_duration), u(R.string.time_in_milliseconds), "1000", 7, 9);
                    N(yVar, 2);
                    return;
                }
                if (i2 == 62) {
                    P(new o().b0(o()), 1);
                    return;
                }
                if (i2 == 63) {
                    P(new s0().S1(new a.b[0], true).b0(o()), 1);
                    return;
                }
                if (i2 == 83) {
                    P(new r().I1(true).b0(o()), 1);
                    return;
                }
                if (i2 == 84) {
                    P(new r1().I1(true).b0(o()), 1);
                    return;
                }
                if (i2 == 88) {
                    P(new d2().b0(o()), 1);
                    return;
                }
                if (i2 == 89) {
                    P(new g().b0(o()), 1);
                    return;
                }
                if (i2 == 100) {
                    N(new c.j(), 1);
                    return;
                }
                if (i2 == 101) {
                    P(new p1().M1(0, a.b.r()).b0(o()), 1);
                    return;
                }
                switch (i2) {
                    case 77:
                        P(new u0().b0(o()), 77);
                        return;
                    case 78:
                        P(new v1().b0(o()), 1);
                        return;
                    case 79:
                        P(new b().T1(true).b0(o()), 1);
                        return;
                    default:
                        switch (i2) {
                            case 92:
                                N(new e.g0().y(u(R.string.vibrate_duration), null, 1, 20, 100, 10), 92);
                                return;
                            case 93:
                                P(new y1().b0(o()), 1);
                                return;
                            case 94:
                                P(new x().b0(o()), 1);
                                return;
                            default:
                                switch (i2) {
                                    case 107:
                                        P(new z1().L1(b.h3.K).b0(o()), 1);
                                        break;
                                    case 108:
                                        N(new c.o(), 1);
                                        break;
                                    case 109:
                                        N(new c.m(), 1);
                                        break;
                                    case 110:
                                        N(new c.r(), 1);
                                        break;
                                    case 111:
                                        N(new c.s(), 1);
                                        break;
                                }
                        }
                }
            }
            e0(R.string.only_if_keyguard_not_secure);
            C1(a0Var);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:45:0x0065 A[FALL_THROUGH, RETURN] */
    @Override // e.j
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected boolean l1(int r4) {
        /*
            r3 = this;
            a.b[] r0 = r3.M
            r0 = r0[r4]
            boolean r1 = f.l.r
            r2 = 1
            if (r1 != 0) goto Le
            boolean r1 = r0 instanceof f.e0
            if (r1 == 0) goto Le
            return r2
        Le:
            boolean r1 = r0 instanceof a.b.u0
            if (r1 == 0) goto L1c
            a.b$u0 r0 = (a.b.u0) r0
            int r0 = r0.b()
            r3.y1(r4, r0)
            return r2
        L1c:
            int r4 = r0.z
            if (r4 == 0) goto L65
            if (r4 == r2) goto L65
            r1 = 47
            if (r4 == r1) goto L65
            r1 = 48
            if (r4 == r1) goto L65
            r1 = 51
            if (r4 == r1) goto L65
            r1 = 68
            if (r4 == r1) goto L65
            r1 = 72
            if (r4 == r1) goto L65
            r1 = 86
            if (r4 == r1) goto L65
            r1 = 105(0x69, float:1.47E-43)
            if (r4 == r1) goto L65
            r1 = 53
            if (r4 == r1) goto L65
            r1 = 54
            if (r4 == r1) goto L65
            r1 = 83
            if (r4 == r1) goto L65
            r1 = 84
            if (r4 == r1) goto L65
            r1 = 100
            if (r4 == r1) goto L65
            r1 = 101(0x65, float:1.42E-43)
            if (r4 == r1) goto L65
            switch(r4) {
                case 61: goto L65;
                case 62: goto L65;
                case 63: goto L65;
                default: goto L59;
            }
        L59:
            switch(r4) {
                case 77: goto L65;
                case 78: goto L65;
                case 79: goto L65;
                default: goto L5c;
            }
        L5c:
            switch(r4) {
                case 92: goto L65;
                case 93: goto L65;
                case 94: goto L65;
                default: goto L5f;
            }
        L5f:
            switch(r4) {
                case 108: goto L65;
                case 109: goto L65;
                case 110: goto L65;
                case 111: goto L65;
                default: goto L62;
            }
        L62:
            r3.D1(r0)
        L65:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: d.d.l1(int):boolean");
    }

    @Override // e.j
    protected void o1(int i) {
        C1(((b.u0) this.M[I0()]).c(i));
    }

    @Override // e.j
    protected boolean p1(int i) {
        if (this.M[I0()].z == 68) {
            return true;
        }
        D1(((b.u0) this.M[I0()]).c(i));
        return true;
    }
}
