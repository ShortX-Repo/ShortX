package d;

import a.p;
import android.view.View;
import android.widget.CompoundButton;
import cc.xposed.R;
import e.j;

/* JADX INFO: loaded from: classes.dex */
public class z extends e.j implements p.a {
    private a.z M;

    class a implements CompoundButton.OnCheckedChangeListener {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ int f378a;

        a(int i) {
            this.f378a = i;
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            z.this.M.q0(compoundButton.getContext(), this.f378a, z);
        }
    }

    @Override // e.j0.c
    protected void B() {
        super.B();
        c0(R.string.action_game_mode);
    }

    @Override // e.j
    protected int B0() {
        this.M = g().h();
        return 2;
    }

    @Override // e.j
    protected boolean N0(int i) {
        return true;
    }

    @Override // e.j
    protected View h1(int i) {
        return new j.k(this, u(i == 0 ? R.string.ignore_edges : R.string.intercept_keys));
    }

    @Override // e.j
    protected View i1(int i) {
        int i2;
        int i3;
        if (I0() == 0) {
            i2 = p.a.f13a[i];
            i3 = p.a.f15c[i];
        } else {
            i2 = p.a.f14b[i];
            i3 = p.a.f16d[i];
        }
        j.ViewOnTouchListenerC0005j viewOnTouchListenerC0005j = new j.ViewOnTouchListenerC0005j(u(i2), null, this.M.g0(i3));
        viewOnTouchListenerC0005j.setOnCheckedChangeListener(new a(i3));
        return viewOnTouchListenerC0005j;
    }

    @Override // e.j
    protected void k1(int i) {
        y1(i, i == 0 ? p.a.f13a.length : p.a.f14b.length);
    }

    @Override // e.j
    protected void o1(int i) {
        ((j.ViewOnTouchListenerC0005j) K0(i)).h();
    }
}
