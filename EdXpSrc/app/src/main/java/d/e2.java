package d;

import a.b;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class e2 extends e.r {

    private static class a extends g.r implements Runnable {
        private final List<g.q> L;
        private final int M;
        private final float N;
        private final float O;
        private int P;

        public a(Context context, b.y0 y0Var) {
            super(context, -16738393);
            this.P = 0;
            l(0, 0, false);
            List<g.q> listD = y0Var.D();
            this.L = listD;
            this.M = listD == null ? 0 : listD.size();
            boolean zF = y0Var.F();
            boolean z = getResources().getConfiguration().orientation == 2;
            Point pointN = g.a0.n(context, new Point());
            float f2 = 0.0f;
            this.N = zF == z ? 0.0f : z ? -90.0f : 90.0f;
            if (zF != z) {
                f2 = (z ? pointN.y : pointN.x) / 2.0f;
            }
            this.O = f2;
            postDelayed(this, 1000L);
        }

        @Override // g.r, android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.N == 0.0f) {
                super.onDraw(canvas);
                return;
            }
            canvas.save();
            float f2 = this.N;
            float f3 = this.O;
            canvas.rotate(f2, f3, f3);
            super.onDraw(canvas);
            canvas.restore();
        }

        @Override // g.r, android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getActionMasked() != 0) {
                return true;
            }
            removeCallbacks(this);
            for (int i = this.P; i < this.M; i++) {
                e(this.L.get(i));
            }
            invalidate();
            return true;
        }

        @Override // java.lang.Runnable
        public void run() {
            int i = this.P;
            if (i < this.M) {
                e(this.L.get(i));
                int i2 = this.P + 1;
                this.P = i2;
                if ((i2 < this.M ? this.L.get(i2) : null) != null) {
                    postDelayed(this, r1.z - r0.z);
                }
                invalidate();
            }
        }
    }

    public e2 k0(b.y0 y0Var) {
        h().putParcelable("data", y0Var);
        return this;
    }

    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Context context = viewGroup.getContext();
        b.y0 y0Var = (b.y0) h().getParcelable("data");
        return y0Var != null ? new a(viewGroup.getContext(), y0Var) : new View(context);
    }
}
