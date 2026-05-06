package d;

import android.os.Bundle;
import cc.xposed.R;
import com.jozein.xedgepro.ui.ActivityPerformAction;
import e.j;
import e.j0;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public abstract class t0 extends e.j {
    private a.n M;
    private ArrayList<Integer> N;
    private int[] O = null;
    private int[] P = null;
    private CharSequence[] Q = null;
    private CharSequence[] R = null;

    @Override // e.j
    protected int B0() {
        a.n nVarB1 = B1();
        this.M = nVarB1;
        this.N = nVarB1.i();
        int i = 0;
        boolean z = h().getBoolean("selectable", false);
        int[] iArr = z ? null : new int[]{R.string.move_up, R.string.move_down, R.string.delete};
        this.O = iArr;
        int[] iArr2 = new int[5];
        iArr2[0] = z ? R.string.view : R.string.edit_action;
        iArr2[1] = 17039361;
        iArr2[2] = R.string.perform;
        iArr2[3] = R.string.rename;
        iArr2[4] = R.string.action_info;
        this.P = iArr2;
        if (iArr != null) {
            this.Q = new CharSequence[iArr.length];
            int i2 = 0;
            while (true) {
                int[] iArr3 = this.O;
                if (i2 >= iArr3.length) {
                    break;
                }
                this.Q[i2] = u(iArr3[i2]);
                i2++;
            }
        }
        this.R = new CharSequence[this.P.length];
        while (true) {
            int[] iArr4 = this.P;
            if (i >= iArr4.length) {
                break;
            }
            this.R[i] = u(iArr4[i]);
            i++;
        }
        return z ? this.N.size() : this.N.size() + 1;
    }

    protected abstract a.n B1();

    protected abstract a.b[] C1(Bundle bundle);

    protected abstract a.b D1(int i);

    protected abstract e.j E1(CharSequence charSequence, a.b[] bVarArr, boolean z);

    protected abstract void F1();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // e.j
    /* JADX INFO: renamed from: G1, reason: merged with bridge method [inline-methods] */
    public j.k h1(int i) {
        if (i >= this.N.size()) {
            return b1();
        }
        int iIntValue = this.N.get(i).intValue();
        return new j.g(D0(D1(iIntValue)), this.M.h(iIntValue).f11a, (CharSequence) null);
    }

    protected void H1(e.j jVar) {
        P(jVar, 65539);
    }

    public t0 I1(boolean z) {
        h().putBoolean("selectable", z);
        return this;
    }

    @Override // e.j0.c
    protected void J(Bundle bundle, int i) {
        e.j jVarE1;
        j0.b bVarW;
        if (bundle == null) {
            return;
        }
        try {
            switch (i) {
                case 65535:
                case 65536:
                    int iE0 = E0();
                    int i2 = bundle.getInt("result", -1);
                    if (i2 != -1) {
                        int i3 = i == 65535 ? this.O[i2] : this.P[i2];
                        int i4 = 65540;
                        switch (i3) {
                            case android.R.string.copy:
                                r().o().putParcelable("clipboard", D1(this.N.get(iE0).intValue()));
                                break;
                            case R.string.action_info /* 2131099690 */:
                                bVarW = new c.b().w(D1(this.N.get(iE0).intValue()));
                                N(bVarW, i4);
                                break;
                            case R.string.delete /* 2131099853 */:
                                this.M.p(M0(), this.N.get(iE0).intValue());
                                this.N.remove(iE0);
                                u1(iE0);
                                break;
                            case R.string.edit_action /* 2131099886 */:
                                a.o oVarH = this.M.h(this.N.get(iE0).intValue());
                                a.b[] bVarArr = oVarH.f12b;
                                int length = bVarArr.length;
                                a.b[] bVarArr2 = new a.b[length];
                                System.arraycopy(bVarArr, 0, bVarArr2, 0, length);
                                jVarE1 = E1(oVarH.f11a, bVarArr2, true);
                                i4 = 65538;
                                P(jVarE1, i4);
                                break;
                            case R.string.move_down /* 2131099992 */:
                                if (iE0 < this.N.size() - 1) {
                                    this.M.j(this.N.get(iE0).intValue());
                                    ArrayList<Integer> arrayList = this.N;
                                    int i5 = iE0 + 1;
                                    arrayList.add(i5, arrayList.remove(iE0));
                                    a1(iE0, i5);
                                }
                                break;
                            case R.string.move_up /* 2131099993 */:
                                if (iE0 > 0) {
                                    this.M.l(this.N.get(iE0).intValue());
                                    ArrayList<Integer> arrayList2 = this.N;
                                    int i6 = iE0 - 1;
                                    arrayList2.add(i6, arrayList2.remove(iE0));
                                    a1(iE0, i6);
                                }
                                break;
                            case R.string.perform /* 2131100015 */:
                                ActivityPerformAction.a(f(), D1(this.N.get(iE0).intValue()));
                                break;
                            case R.string.rename /* 2131100034 */:
                                e.y yVar = new e.y();
                                yVar.J(u(R.string.enter_name), null, this.M.h(this.N.get(iE0).intValue()).f11a, 3, 64);
                                i4 = 65537;
                                bVarW = yVar;
                                N(bVarW, i4);
                                break;
                            case R.string.view /* 2131100194 */:
                                a.o oVarH2 = this.M.h(this.N.get(iE0).intValue());
                                jVarE1 = E1(oVarH2.f11a, oVarH2.f12b, false);
                                P(jVarE1, i4);
                                break;
                        }
                        return;
                    }
                    return;
                case 65537:
                    CharSequence charSequence = bundle.getCharSequence("result");
                    if (charSequence == null) {
                        return;
                    }
                    int iE02 = E0();
                    int iIntValue = this.N.get(iE02).intValue();
                    this.M.t(M0(), iIntValue, new a.o(charSequence.toString(), this.M.h(iIntValue).f12b));
                    ((j.k) L0(iE02)).setText(charSequence);
                    break;
                case 65538:
                    a.b[] bVarArrC1 = C1(bundle);
                    if (bVarArrC1 == null) {
                        return;
                    }
                    int iE03 = E0();
                    int iIntValue2 = this.N.get(iE03).intValue();
                    this.M.t(M0(), iIntValue2, new a.o(this.M.h(iIntValue2).f11a, bVarArrC1));
                    Q0(iE03);
                    break;
                case 65539:
                    a.b[] bVarArrC12 = C1(bundle);
                    if (bVarArrC12 == null) {
                        return;
                    }
                    this.N.add(Integer.valueOf(this.M.e(M0(), new a.o(null, bVarArrC12))));
                    x0(E0());
                    break;
                default:
                    return;
            }
        } catch (Throwable th) {
            g0(th);
        }
    }

    @Override // e.j
    protected void k1(int i) {
        if (i >= this.N.size()) {
            F1();
        } else if (!h().getBoolean("selectable", false)) {
            N(new e.z().u(this.Q), 65535);
        } else {
            W("result", D1(this.N.get(i).intValue()));
            L();
        }
    }

    @Override // e.j
    protected boolean l1(int i) {
        if (i >= this.N.size()) {
            return super.l1(i);
        }
        N(new e.z().u(this.R), 65536);
        return true;
    }
}
