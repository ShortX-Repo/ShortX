package d;

import a.b;
import android.R;
import android.app.ActionBar;
import android.os.Bundle;
import com.jozein.xedgepro.ui.ActivityPerformAction;
import e.j0;

/* JADX INFO: loaded from: classes.dex */
public abstract class c extends e.j {
    /* JADX WARN: Removed duplicated region for block: B:21:0x002a A[FALL_THROUGH] */
    /*  JADX ERROR: UnsupportedOperationException in pass: RegionMakerVisitor
        java.lang.UnsupportedOperationException
        	at java.base/java.util.Collections$UnmodifiableCollection.add(Collections.java:1092)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker$1.leaveRegion(SwitchRegionMaker.java:390)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:70)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:23)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.insertBreaksForCase(SwitchRegionMaker.java:370)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.insertBreaks(SwitchRegionMaker.java:85)
        	at jadx.core.dex.visitors.regions.PostProcessRegions.leaveRegion(PostProcessRegions.java:33)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:70)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
        	at java.base/java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1117)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
        	at java.base/java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1117)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:19)
        	at jadx.core.dex.visitors.regions.PostProcessRegions.process(PostProcessRegions.java:23)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:31)
        */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private int[] B1(int r4, boolean r5) {
        /*
            Method dump skipped, instruction units count: 220
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: d.c.B1(int, boolean):int[]");
    }

    /* JADX WARN: Removed duplicated region for block: B:38:0x010c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void D1(a.b r7, int r8) {
        /*
            Method dump skipped, instruction units count: 382
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: d.c.D1(a.b, int):void");
    }

    protected abstract void C1(Bundle bundle, int i);

    protected void E1(int i, int i2) {
        ActionBar actionBar = getActivity().getActionBar();
        P(new d().H1(i, actionBar != null ? actionBar.getTitle() : null), i2);
    }

    protected void F1(a.b bVar, int i, int i2) {
        G1(bVar, i, i2, true);
    }

    protected void G1(a.b bVar, int i, int i2, boolean z) {
        Bundle bundleH = h();
        bundleH.putParcelable("action", bVar);
        bundleH.putInt("code", i);
        bundleH.putInt("flag", i2);
        a.b bVar2 = (a.b) r().o().getParcelable("clipboard");
        int[] iArrB1 = B1(bVar.z, z);
        CharSequence[] charSequenceArr = new CharSequence[iArrB1.length];
        for (int i3 = 0; i3 < iArrB1.length; i3++) {
            charSequenceArr[i3] = u(iArrB1[i3]);
            if (iArrB1[i3] == 17039371 && bVar2 != null) {
                charSequenceArr[i3] = ((Object) charSequenceArr[i3]) + "(" + ((Object) bVar2.n(getActivity())) + ')';
            }
        }
        bundleH.putIntArray("items", iArrB1);
        N(new e.z().u(charSequenceArr), 65535);
    }

    protected void H1(CharSequence charSequence) {
        h().putCharSequence("next_sub_title", charSequence);
    }

    @Override // e.j0.c
    protected final void J(Bundle bundle, int i) {
        Bundle bundleH;
        int[] intArray;
        j0.b bVarW;
        if (bundle == null) {
            return;
        }
        String str = null;
        a.b bVarB = null;
        str = null;
        switch (i) {
            case 65535:
                int i2 = bundle.getInt("result", -1);
                if (i2 < 0 || (intArray = (bundleH = h()).getIntArray("items")) == null) {
                    return;
                }
                int i3 = intArray[i2];
                if (i3 == 17039371) {
                    a.b bVar = (a.b) r().o().getParcelable("clipboard");
                    if (bVar == null) {
                        return;
                    }
                    bundleH.putParcelable("result", bVar);
                    C1(bundleH, bundleH.getInt("code"));
                    return;
                }
                a.b dVar = (a.b) bundleH.getParcelable("action");
                if (dVar == null) {
                    return;
                }
                switch (i3) {
                    case R.string.copy:
                        r().o().putParcelable("clipboard", dVar);
                        break;
                    case cc.xposed.R.string.action_info /* 2131099690 */:
                        bVarW = new c.b().w(dVar);
                        N(bVarW, 65537);
                        break;
                    case cc.xposed.R.string.edit_icon_and_label /* 2131099887 */:
                        if (dVar.z != 72) {
                            dVar = new b.d(dVar);
                            break;
                        }
                    case cc.xposed.R.string.edit_action /* 2131099886 */:
                        D1(dVar, bundleH.getInt("code"));
                        break;
                    case cc.xposed.R.string.export_as_command /* 2131099899 */:
                        try {
                            a.z zVarH = g().h();
                            if (zVarH == null || !zVarH.o(32)) {
                                str = "#" + ((Object) u(cc.xposed.R.string.perform_by_broadcast_not_allowed));
                            }
                        } catch (Throwable th) {
                            f.v.d(th);
                        }
                        bVarW = new e.q().w(dVar.n(f()), a.b.g(dVar), str);
                        N(bVarW, 65537);
                        break;
                    case cc.xposed.R.string.perform /* 2131100015 */:
                        ActivityPerformAction.a(f(), dVar);
                        break;
                    case cc.xposed.R.string.put_on_launcher /* 2131100029 */:
                        f.l0.i(f(), dVar);
                        break;
                }
                return;
            case 65536:
                bundle = h();
                a.b bVar2 = (a.b) bundle.getParcelable("action");
                if (bVar2 instanceof b.g) {
                    bVarB = ((b.g) bVar2).B();
                } else if (bVar2 instanceof b.h) {
                    bVarB = ((b.h) bVar2).B();
                }
                if (bVarB == null) {
                    return;
                }
                bundle.putParcelable("result", bVarB);
                i = bundle.getInt("code");
                break;
            case 65537:
                return;
            case 65538:
                CharSequence charSequence = bundle.getCharSequence("result");
                if (charSequence != null) {
                    try {
                        int i4 = Integer.parseInt(charSequence.toString());
                        Bundle bundleH2 = h();
                        bundleH2.putParcelable("result", new b.g0(i4));
                        C1(bundleH2, bundleH2.getInt("code"));
                        return;
                    } catch (NumberFormatException e2) {
                        f.v.d(e2);
                        return;
                    }
                }
                return;
        }
        C1(bundle, i);
    }
}
