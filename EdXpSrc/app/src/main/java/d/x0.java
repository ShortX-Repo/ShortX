package d;

import a.p;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import cc.xposed.R;
import e.j;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class x0 extends e.j implements p.f {
    private a.z M;
    private ArrayList<Integer> N;

    class a implements CompoundButton.OnCheckedChangeListener {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ int f372a;

        a(int i) {
            this.f372a = i;
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            for (int i = 0; i < 4; i++) {
                int iIntValue = ((Integer) x0.this.N.get(i)).intValue() & 7;
                int i2 = this.f372a;
                if (iIntValue == i2) {
                    if (z) {
                        i2 |= 8;
                    }
                    x0.this.N.set(i, Integer.valueOf(i2));
                    x0.this.E1();
                    return;
                }
            }
        }
    }

    private CharSequence D1(int i) {
        return u(p.f.i[i]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void E1() {
        int iIntValue = 0;
        for (int i = 0; i < 4; i++) {
            iIntValue |= this.N.get(i).intValue() << (i * 8);
        }
        this.M.s0(f(), 20, iIntValue);
    }

    @Override // e.j
    protected int B0() {
        c0(R.string.gesture_pointer_modes);
        this.M = g().h();
        this.N = new ArrayList<>(4);
        a.z zVar = this.M;
        int iU = zVar != null ? zVar.u(20) : 0;
        int i = 202050057;
        if (iU != 0) {
            for (int i2 = 0; i2 <= 24; i2 += 8) {
                int i3 = (iU >>> i2) & 7;
                if (i3 < 1 || i3 > 4 || this.N.contains(Integer.valueOf(i3))) {
                    this.M.s0(f(), 20, 202050057);
                    iU = 202050057;
                    break;
                }
                this.N.add(Integer.valueOf(i3));
            }
            this.N.clear();
            i = iU;
        }
        for (int i4 = 0; i4 <= 24; i4 += 8) {
            this.N.add(Integer.valueOf((i >>> i4) & 15));
        }
        return 4;
    }

    @Override // e.j0.c
    protected void J(Bundle bundle, int i) {
        ArrayList<Integer> arrayList;
        int i2;
        if (bundle != null && i == 1) {
            int i3 = bundle.getInt("result", -1);
            int iE0 = E0();
            if (i3 == 0) {
                ((j.ViewOnTouchListenerC0005j) L0(iE0)).setChecked(!r3.g());
                return;
            }
            if (i3 != 1) {
                if (i3 != 2 || iE0 >= 3) {
                    return;
                }
                arrayList = this.N;
                i2 = iE0 + 1;
            } else {
                if (iE0 <= 0) {
                    return;
                }
                arrayList = this.N;
                i2 = iE0 - 1;
            }
            arrayList.add(i2, arrayList.remove(iE0));
            a1(iE0, i2);
            E1();
        }
    }

    @Override // e.j
    protected View h1(int i) {
        int iIntValue = this.N.get(i).intValue();
        int i2 = iIntValue & 7;
        j.ViewOnTouchListenerC0005j viewOnTouchListenerC0005j = new j.ViewOnTouchListenerC0005j(D1(i2), null, (iIntValue & 8) != 0);
        viewOnTouchListenerC0005j.setOnCheckedChangeListener(new a(i2));
        return viewOnTouchListenerC0005j;
    }

    @Override // e.j
    protected void k1(int i) {
        CharSequence[] charSequenceArr = new CharSequence[3];
        charSequenceArr[0] = u((this.N.get(i).intValue() & 8) != 0 ? R.string.disable : R.string.enable);
        charSequenceArr[1] = u(R.string.move_up);
        charSequenceArr[2] = u(R.string.move_down);
        N(new e.z().u(charSequenceArr), 1);
    }
}
