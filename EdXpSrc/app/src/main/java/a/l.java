package a;

import android.content.Context;
import android.content.Intent;
import f.p0;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public abstract class l<T> extends r {
    private final f.p B;
    private f.u<Integer, T> C;
    private final boolean D;

    l(String str, String str2) {
        super(str);
        this.D = str != null;
        this.B = new f.p(str2);
        o();
    }

    private int n() {
        int iR;
        f.u<Integer, T> uVar = this.C;
        do {
            iR = p0.r();
        } while (uVar.containsKey(Integer.valueOf(iR)));
        return iR;
    }

    private void r() {
        this.B.j();
        int size = this.C.size();
        for (int i = 0; i < size; i++) {
            this.B.d(this.C.e(i).intValue());
            x(this.B, this.C.k(i));
            this.B.C();
        }
        this.B.n();
    }

    private void s(Context context, int i, T t) {
        if (this.D) {
            Intent intent = new Intent();
            f.m mVarJ = new f.m(intent).j();
            mVarJ.d(1).d(i);
            x(mVarJ, t);
            mVarJ.l();
            c(context, intent);
        }
    }

    @Override // a.r
    protected void b(Intent intent) {
        f.m mVar = new f.m(intent);
        if (mVar.i()) {
            int iH = mVar.h();
            if (iH == 1) {
                this.C.put(Integer.valueOf(mVar.h()), g(mVar));
            } else if (iH == 2) {
                this.C.remove(Integer.valueOf(mVar.h()));
            }
        }
        mVar.k();
    }

    public int e(Context context, T t) {
        int iN = n();
        this.C.put(Integer.valueOf(iN), t);
        r();
        s(context, iN, t);
        return iN;
    }

    public int f(Context context, int i, T t) {
        int iN = n();
        this.C.put(Integer.valueOf(iN), t);
        int iD = this.C.d(Integer.valueOf(iN));
        if (iD < i) {
            while (iD < i) {
                this.C.g(iD);
                iD++;
            }
        } else if (iD > i) {
            while (iD > i) {
                this.C.h(iD);
                iD--;
            }
        }
        r();
        s(context, iN, t);
        return iN;
    }

    protected abstract T g(f.s sVar);

    public T h(int i) {
        return this.C.get(Integer.valueOf(i));
    }

    public ArrayList<Integer> i() {
        return this.C.f();
    }

    public void j(int i) {
        int iD = this.C.d(Integer.valueOf(i));
        if (i < 0) {
            return;
        }
        this.C.g(iD);
        r();
    }

    public void k(int i) {
        this.C.g(i);
        r();
    }

    public void l(int i) {
        int iD = this.C.d(Integer.valueOf(i));
        if (i < 0) {
            return;
        }
        this.C.h(iD);
        r();
    }

    public void m(int i) {
        this.C.h(i);
        r();
    }

    public void o() {
        f.u<Integer, T> uVar = new f.u<>();
        if (this.B.E()) {
            do {
                uVar.put(Integer.valueOf(this.B.h()), g(this.B));
            } while (this.B.t());
        }
        this.B.m();
        this.C = uVar;
    }

    public void p(Context context, int i) {
        q(context, this.C.d(Integer.valueOf(i)));
    }

    public void q(Context context, int i) {
        if (i < 0) {
            return;
        }
        int iIntValue = this.C.e(i).intValue();
        this.C.i(i);
        r();
        if (this.D) {
            Intent intent = new Intent();
            new f.m(intent).j().d(2).d(iIntValue).l();
            c(context, intent);
        }
    }

    public void t(Context context, int i, T t) {
        this.C.put(Integer.valueOf(i), t);
        r();
        s(context, i, t);
    }

    public void u(Context context, int i, T t) {
        this.C.j(i, t);
        r();
        s(context, this.C.e(i).intValue(), t);
    }

    public int v() {
        return this.C.size();
    }

    public T w(int i) {
        return this.C.k(i);
    }

    protected abstract void x(f.s sVar, T t);
}
