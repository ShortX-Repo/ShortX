package d;

import a.b;
import android.os.Bundle;
import cc.xposed.R;
import com.jozein.xedgepro.ApplicationMain;
import e.j;
import f.c0;
import f.z;
import java.lang.ref.WeakReference;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class y extends e.j implements j.d {
    private a M;

    private static class a implements z.e {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final List<c0.d> f374a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private final f.z f375b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        private WeakReference<y> f376c;

        a(y yVar, List<c0.d> list) {
            this.f374a = list;
            f.z zVarK = f.z.K(yVar.f());
            this.f375b = zVarK;
            zVarK.n0(this);
            g(yVar);
        }

        static a d(y yVar, Object obj) {
            if (!(obj instanceof a)) {
                return null;
            }
            a aVar = (a) obj;
            f.z.k(aVar.f374a);
            aVar.g(yVar);
            return aVar;
        }

        @Override // f.z.e
        public void a(c0.d dVar) {
            f(dVar);
        }

        @Override // f.z.e
        public void b(String str, int i) {
            int iI1 = y.I1(this.f374a, str, i);
            if (iI1 >= 0) {
                this.f374a.remove(iI1);
                y yVarE = e();
                if (yVarE != null) {
                    yVarE.u1(iI1);
                }
            }
        }

        @Override // f.z.e
        public void c(c0.d dVar) {
            f(dVar);
        }

        y e() {
            WeakReference<y> weakReference = this.f376c;
            if (weakReference != null) {
                return weakReference.get();
            }
            return null;
        }

        void f(c0.d dVar) {
            y yVarE = e();
            int iIndexOf = this.f374a.indexOf(dVar);
            int i = f.z.i(this.f374a, dVar);
            if (i < 0) {
                i ^= -1;
            }
            if (iIndexOf < 0) {
                this.f374a.add(i, dVar);
                if (yVarE != null) {
                    yVarE.x0(i);
                    return;
                }
                return;
            }
            if (i > iIndexOf) {
                i--;
            }
            if (iIndexOf == i) {
                this.f374a.set(iIndexOf, dVar);
                if (yVarE != null) {
                    yVarE.Q0(iIndexOf);
                    return;
                }
                return;
            }
            this.f374a.remove(iIndexOf);
            this.f374a.add(i, dVar);
            if (yVarE != null) {
                yVarE.u1(iIndexOf);
                yVarE.x0(i);
            }
        }

        void g(y yVar) {
            this.f376c = new WeakReference<>(yVar);
        }

        void h() {
            this.f375b.n0(null);
        }
    }

    private void H1() {
        a aVar = this.M;
        if (aVar != null) {
            aVar.h();
            this.M = null;
        }
    }

    static int I1(List<c0.d> list, String str, int i) {
        if (str == null) {
            return -1;
        }
        int size = list.size();
        for (int i2 = 0; i2 < size; i2++) {
            c0.d dVar = list.get(i2);
            if (str.equals(dVar.i()) && i == dVar.k()) {
                return i2;
            }
        }
        return -1;
    }

    private static boolean J1(c0.d dVar) {
        if (!ApplicationMain.isModuleActivated()) {
            return false;
        }
        if (!dVar.m()) {
            return true;
        }
        String strI = dVar.i();
        return ((dVar.k() == 0 && f.l.j.equals(strI)) || "android".equals(strI) || "com.android.systemui".equals(strI)) ? false : true;
    }

    private void L1(int i, c0.d dVar) {
        if (i == R.string.freeze) {
            if (dVar.n()) {
                N(new e.m().u(u(R.string.unsafe_to_freeze_system_app)), 1);
                return;
            } else {
                M1(dVar, false);
                return;
            }
        }
        if (i == R.string.unfreeze) {
            if (ApplicationMain.isModuleActivated()) {
                M1(dVar, true);
                return;
            } else {
                N(new e.m().u(u(R.string.module_not_activated)), 2);
                return;
            }
        }
        if (i == R.string.launch) {
            if (ApplicationMain.isModuleActivated()) {
                a.b.t(f(), new b.l(dVar.i(), null, dVar.k()));
                return;
            } else if (dVar.m()) {
                f.p0.t(f(), dVar.i());
                return;
            } else {
                e0(R.string.module_not_activated);
                return;
            }
        }
        if (i == R.string.view_on_play_store) {
            f.p0.v(getActivity(), dVar.i());
            return;
        }
        if (i == R.string.app_info) {
            if (dVar.k() == f.o0.m() || !ApplicationMain.isModuleActivated()) {
                f.p0.u(f(), dVar.i());
            } else {
                b.s0.E(f(), 29, dVar.i(), dVar.k());
            }
        }
    }

    private void M1(c0.d dVar, boolean z) {
        b.s0.E(f(), z ? 2 : 1, dVar.i(), dVar.k());
    }

    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.freezer);
    }

    @Override // e.j
    protected int B0() {
        if (this.M == null) {
            this.M = new a(this, f.z.K(f()).Q(true));
        }
        return this.M.f374a.size();
    }

    @Override // e.j0.c
    protected void G() {
        super.G();
        H1();
    }

    @Override // e.j0.c
    protected void J(Bundle bundle, int i) {
        if (bundle == null) {
            return;
        }
        Bundle bundleH = h();
        int iI1 = I1(this.M.f374a, bundleH.getString("package"), bundleH.getInt("user_id", 0));
        if (iI1 < 0) {
            iI1 = E0();
        }
        c0.d dVar = this.M.f374a.get(iI1);
        if (i != 0) {
            if (i == 1 && bundle.getBoolean("result", false)) {
                M1(dVar, false);
                return;
            }
            return;
        }
        int i2 = bundle.getInt("result");
        int[] intArray = bundleH.getIntArray("item_ids");
        if (intArray == null || i2 < 0 || i2 >= intArray.length) {
            return;
        }
        L1(intArray[i2], dVar);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // e.j
    /* JADX INFO: renamed from: K1, reason: merged with bridge method [inline-methods] */
    public j.g h1(int i) {
        c0.d dVar = this.M.f374a.get(i);
        j.g gVar = new j.g(dVar.e(), dVar.f(), dVar.i());
        if (!dVar.m()) {
            gVar.e();
        }
        return gVar;
    }

    @Override // e.j.d
    public final String[] a() {
        return f.z.L(this.M.f374a);
    }

    protected void finalize() throws Throwable {
        super.finalize();
        H1();
    }

    @Override // e.j
    protected void g1(Object obj) {
        if (obj instanceof List) {
            this.M = new a(this, (List) obj);
        }
    }

    @Override // e.j
    protected void k1(int i) {
        int[] iArr;
        c0.d dVar = this.M.f374a.get(i);
        boolean zJ1 = J1(dVar);
        Bundle bundleH = h();
        bundleH.putString("package", dVar.i());
        bundleH.putInt("user_id", dVar.k());
        if (zJ1) {
            iArr = new int[4];
            iArr[0] = dVar.m() ? R.string.freeze : R.string.unfreeze;
            iArr[1] = R.string.launch;
            iArr[2] = R.string.view_on_play_store;
            iArr[3] = R.string.app_info;
        } else {
            iArr = new int[]{R.string.launch, R.string.view_on_play_store, R.string.app_info};
        }
        bundleH.putIntArray("item_ids", iArr);
        CharSequence[] charSequenceArr = new CharSequence[iArr.length];
        for (int i2 = 0; i2 < iArr.length; i2++) {
            charSequenceArr[i2] = u(iArr[i2]);
        }
        N(new e.z().u(charSequenceArr), 0);
    }

    @Override // e.j
    protected final j.i m1() {
        a aVarD = a.d(this, s());
        this.M = aVarD;
        if (aVarD != null) {
            return null;
        }
        return new j.i.d(true);
    }

    @Override // e.j, e.j0.c, android.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        a aVar = this.M;
        if (aVar != null) {
            Z(aVar);
        }
        super.onSaveInstanceState(bundle);
    }
}
