package com.jozein.xedgepro.xposed;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import cc.xposed.R;
import com.jozein.xedgepro.xposed.t3;

/* JADX INFO: loaded from: classes.dex */
abstract class r2 extends FrameLayout implements t3.c {
    private static final int[] N = {2, 3, 4, 4, 5, 5};
    private static final boolean[] O = {false, false, false, true, false, true};
    private static final int[] P = {R.drawable.ic_painter_gesture, R.drawable.ic_painter_line, R.drawable.ic_painter_rect, R.drawable.ic_painter_rect_fill, R.drawable.ic_painter_arrow, R.drawable.ic_painter_arrow_fill};
    private final LinearLayout.LayoutParams A;
    private final FrameLayout.LayoutParams B;
    private final FrameLayout.LayoutParams C;
    private final FrameLayout.LayoutParams D;
    private final FrameLayout.LayoutParams E;
    private final g.r F;
    private final d G;
    private final int[] H;
    private final int I;
    private final int J;
    private final g.h K;
    private int L;
    private int M;
    private final Resources z;

    class a implements View.OnTouchListener {
        int z = 0;

        a() {
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 0) {
                this.z = 0;
                r2.this.G.l();
            } else if (actionMasked == 5 && this.z < 2) {
                int pointerCount = motionEvent.getPointerCount();
                if (pointerCount > this.z) {
                    this.z = pointerCount;
                }
                if (this.z == 2) {
                    r2.this.G.o();
                    r2.this.G.setVisibility(0);
                }
            }
            return false;
        }
    }

    private abstract class b extends FrameLayout implements View.OnClickListener {
        public final ImageView z;

        public b(Context context, Drawable drawable) {
            super(context);
            ImageView imageView = new ImageView(context);
            this.z = imageView;
            imageView.setImageDrawable(drawable);
            addView(imageView, r2.this.B);
            setBackground(g.a0.l());
            setOnClickListener(this);
        }
    }

    private abstract class c extends ScrollView {

        class a extends b {
            final /* synthetic */ r2 B;
            final /* synthetic */ int C;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            a(Context context, Drawable drawable, r2 r2Var, int i) {
                super(context, drawable);
                this.B = r2Var;
                this.C = i;
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                try {
                    c.this.a((b) view, this.C);
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
        }

        public c(Context context, Drawable[] drawableArr) {
            super(context);
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            for (int i = 0; i < drawableArr.length; i++) {
                linearLayout.addView(new a(context, drawableArr[i], r2.this, i), r2.this.A);
            }
            addView(linearLayout);
            r2.this.w(this);
        }

        protected abstract void a(b bVar, int i);

        public void b() {
            setVisibility(getVisibility() == 0 ? 4 : 0);
        }
    }

    private class d extends LinearLayout implements Runnable {
        private final b A;
        private final b B;
        private final b C;
        private final b D;
        private c E;
        private c F;
        private c G;
        private boolean H;
        private boolean I;
        private int J;
        private final b z;

        class a extends b {
            final /* synthetic */ r2 B;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            a(Context context, Drawable drawable, r2 r2Var) {
                super(context, drawable);
                this.B = r2Var;
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                try {
                    r2.this.F.m();
                    d.this.m();
                    d.this.o();
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
        }

        class b extends b {
            final /* synthetic */ r2 B;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            b(Context context, Drawable drawable, r2 r2Var) {
                super(context, drawable);
                this.B = r2Var;
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                try {
                    r2.this.F.k();
                    d.this.m();
                    d.this.o();
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
        }

        class c extends b {
            final /* synthetic */ r2 B;

            class a extends c {
                final /* synthetic */ Drawable[] A;

                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                a(Context context, Drawable[] drawableArr, Drawable[] drawableArr2) {
                    super(context, drawableArr);
                    this.A = drawableArr2;
                }

                @Override // com.jozein.xedgepro.xposed.r2.c
                protected void a(b bVar, int i) {
                    r2.this.L = i;
                    d.this.B.z.setImageDrawable(this.A[r2.this.L]);
                    setVisibility(4);
                    r2.this.F.l(r2.N[r2.this.L], r2.this.M, r2.O[r2.this.L]);
                }
            }

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            c(Context context, Drawable drawable, r2 r2Var) {
                super(context, drawable);
                this.B = r2Var;
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                try {
                    if (d.this.F != null) {
                        d.this.F.setVisibility(4);
                    }
                    if (d.this.G != null) {
                        d.this.G.setVisibility(4);
                    }
                    if (d.this.E != null) {
                        d.this.E.b();
                        return;
                    }
                    int length = r2.N.length;
                    Drawable[] drawableArr = new Drawable[length];
                    for (int i = 0; i < length; i++) {
                        drawableArr[i] = r2.this.u(r2.P[i]);
                    }
                    d.this.E = new a(view.getContext(), drawableArr, drawableArr);
                    d dVar = d.this;
                    r2.this.addView(dVar.E, r2.this.C);
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
        }

        /* JADX INFO: renamed from: com.jozein.xedgepro.xposed.r2$d$d, reason: collision with other inner class name */
        class C0003d extends b {
            final /* synthetic */ r2 B;

            /* JADX INFO: renamed from: com.jozein.xedgepro.xposed.r2$d$d$a */
            class a extends c {
                a(Context context, Drawable[] drawableArr) {
                    super(context, drawableArr);
                }

                @Override // com.jozein.xedgepro.xposed.r2.c
                protected void a(b bVar, int i) {
                    r2.this.M = i;
                    ImageView imageView = d.this.C.z;
                    d dVar = d.this;
                    imageView.setImageDrawable(dVar.n(r2.this.H[i]));
                    setVisibility(4);
                    r2.this.F.l(r2.N[r2.this.L], r2.this.M, r2.O[r2.this.L]);
                }
            }

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            C0003d(Context context, Drawable drawable, r2 r2Var) {
                super(context, drawable);
                this.B = r2Var;
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                try {
                    if (d.this.E != null) {
                        d.this.E.setVisibility(4);
                    }
                    if (d.this.G != null) {
                        d.this.G.setVisibility(4);
                    }
                    if (d.this.F != null) {
                        d.this.F.b();
                        return;
                    }
                    int length = r2.this.H.length;
                    Drawable[] drawableArr = new Drawable[length];
                    for (int i = 0; i < length; i++) {
                        d dVar = d.this;
                        drawableArr[i] = dVar.n(r2.this.H[i]);
                    }
                    d.this.F = new a(view.getContext(), drawableArr);
                    d dVar2 = d.this;
                    r2.this.addView(dVar2.F, r2.this.D);
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
        }

        class e extends b {
            final int[] B;
            boolean C;
            final /* synthetic */ r2 D;

            class a extends c {
                a(Context context, Drawable[] drawableArr) {
                    super(context, drawableArr);
                }

                @Override // com.jozein.xedgepro.xposed.r2.c
                protected void a(b bVar, int i) {
                    d dVar;
                    try {
                        setVisibility(4);
                        e eVar = e.this;
                        boolean z = true;
                        switch (eVar.B[i]) {
                            case R.drawable.ic_painter_close /* 2130968693 */:
                                r2.this.r();
                                return;
                            case R.drawable.ic_painter_continue_gesture /* 2130968694 */:
                                r2.this.x(eVar.C);
                                e eVar2 = e.this;
                                if (eVar2.C) {
                                    z = false;
                                }
                                eVar2.C = z;
                                bVar.z.setImageDrawable(r2.this.u(z ? R.drawable.ic_painter_pause_gesture : R.drawable.ic_painter_continue_gesture));
                                return;
                            case R.drawable.ic_screenshot /* 2130968736 */:
                                d.this.l();
                                removeCallbacks(d.this);
                                d.this.J = 1;
                                dVar = d.this;
                                break;
                            case R.drawable.ic_screenshot_selected_region /* 2130968737 */:
                                d.this.l();
                                removeCallbacks(d.this);
                                d.this.J = 2;
                                dVar = d.this;
                                break;
                            default:
                                return;
                        }
                        postDelayed(dVar, 500L);
                    } catch (Throwable th) {
                        f.v.d(th);
                    }
                }
            }

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            e(Context context, Drawable drawable, r2 r2Var) {
                super(context, drawable);
                this.D = r2Var;
                this.B = Build.VERSION.SDK_INT >= 24 ? new int[]{R.drawable.ic_screenshot, R.drawable.ic_screenshot_selected_region, R.drawable.ic_painter_continue_gesture, R.drawable.ic_painter_close} : new int[]{R.drawable.ic_screenshot, R.drawable.ic_painter_continue_gesture, R.drawable.ic_painter_close};
                this.C = false;
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                try {
                    if (d.this.E != null) {
                        d.this.E.setVisibility(4);
                    }
                    if (d.this.F != null) {
                        d.this.F.setVisibility(4);
                    }
                    if (d.this.G != null) {
                        d.this.G.b();
                        return;
                    }
                    int length = this.B.length;
                    Drawable[] drawableArr = new Drawable[length];
                    g.h hVarA = g.h.a();
                    for (int i = 0; i < length; i++) {
                        Drawable drawableU = r2.this.u(this.B[i]);
                        hVarA.e(drawableU, r2.this.J);
                        drawableArr[i] = drawableU;
                    }
                    d.this.G = new a(view.getContext(), drawableArr);
                    d dVar = d.this;
                    r2.this.addView(dVar.G, r2.this.E);
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
        }

        class f extends b {
            final /* synthetic */ r2 B;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            f(Context context, Drawable drawable, r2 r2Var) {
                super(context, drawable);
                this.B = r2Var;
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                try {
                    d.this.l();
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
        }

        public d(Context context) {
            super(context);
            this.E = null;
            this.F = null;
            this.G = null;
            this.H = false;
            this.I = false;
            this.J = 0;
            setOrientation(0);
            a aVar = new a(context, r2.this.t(R.drawable.ic_painter_undo), r2.this);
            this.z = aVar;
            addView(aVar, r2.this.A);
            b bVar = new b(context, r2.this.t(R.drawable.ic_painter_redo), r2.this);
            this.A = bVar;
            addView(bVar, r2.this.A);
            c cVar = new c(context, r2.this.u(r2.P[r2.this.L]), r2.this);
            this.B = cVar;
            addView(cVar, r2.this.A);
            C0003d c0003d = new C0003d(context, n(r2.this.H[r2.this.M]), r2.this);
            this.C = c0003d;
            addView(c0003d, r2.this.A);
            e eVar = new e(context, r2.this.u(R.drawable.ic_painter_more), r2.this);
            this.D = eVar;
            addView(eVar, r2.this.A);
            addView(new f(context, r2.this.u(R.drawable.ic_painter_hide), r2.this), r2.this.A);
            r2.this.w(this);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void m() {
            c cVar = this.E;
            if (cVar != null) {
                cVar.setVisibility(4);
            }
            c cVar2 = this.F;
            if (cVar2 != null) {
                cVar2.setVisibility(4);
            }
            c cVar3 = this.G;
            if (cVar3 != null) {
                cVar3.setVisibility(4);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Drawable n(int i) {
            return i != 0 ? new ColorDrawable(i) : r2.this.u(R.drawable.ic_painter_eraser);
        }

        public void l() {
            m();
            setVisibility(4);
        }

        public void o() {
            g.h hVarA = g.h.a();
            boolean zH = r2.this.F.h();
            if (zH != this.H) {
                this.H = zH;
                if (zH) {
                    hVarA.e(this.z.z.getDrawable(), r2.this.J);
                } else {
                    hVarA.g(this.z.z.getDrawable());
                }
            }
            boolean zG = r2.this.F.g();
            if (zG != this.I) {
                this.I = zG;
                if (zG) {
                    hVarA.e(this.A.z.getDrawable(), r2.this.J);
                } else {
                    hVarA.g(this.A.z.getDrawable());
                }
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            int i = this.J;
            this.J = 0;
            if (i == 1 || i == 2) {
                try {
                    r2.this.v(i);
                } catch (Throwable th) {
                    f.v.d(th);
                }
            }
        }
    }

    public r2(Context context, Resources resources, int[] iArr, int i, boolean z) {
        super(context);
        this.K = g.h.a();
        this.L = 0;
        this.M = 0;
        this.z = resources;
        this.I = i;
        this.J = g.a0.r(i) ? 0 : -16777216;
        if (iArr == null || iArr.length == 0) {
            this.H = new int[]{-24576};
        } else {
            this.H = iArr;
        }
        g.x xVarB = g.x.b(context);
        int i2 = (int) (xVarB.f636a * 0.7f);
        int i3 = (int) (xVarB.f637b * 0.7f);
        this.A = new LinearLayout.LayoutParams(i2, i2);
        this.B = new FrameLayout.LayoutParams(i3, i3, 17);
        g.r rVar = new g.r(context, this.H);
        this.F = rVar;
        rVar.l(N[0], 0, O[0]);
        rVar.setOnTouchListener(new a());
        addView(rVar);
        d dVar = new d(context);
        this.G = dVar;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2, 81);
        layoutParams.bottomMargin = xVarB.f642g;
        addView(dVar, layoutParams);
        if (z) {
            dVar.l();
        }
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(-2, -2, 81);
        this.C = layoutParams2;
        layoutParams2.bottomMargin = layoutParams.bottomMargin + i2;
        int i4 = i2 / 2;
        layoutParams2.rightMargin = i4;
        FrameLayout.LayoutParams layoutParams3 = new FrameLayout.LayoutParams(-2, -2, 81);
        this.D = layoutParams3;
        layoutParams3.bottomMargin = layoutParams.bottomMargin + i2;
        layoutParams3.leftMargin = i4;
        FrameLayout.LayoutParams layoutParams4 = new FrameLayout.LayoutParams(-2, -2, 81);
        this.E = layoutParams4;
        layoutParams4.bottomMargin = layoutParams.bottomMargin + i2;
        layoutParams4.leftMargin = i2 + i4;
    }

    protected abstract void r();

    Drawable s(int i, int i2) {
        Drawable drawable = this.z.getDrawable(i);
        this.K.e(drawable, i2);
        return drawable;
    }

    Drawable t(int i) {
        return s(i, -7829368);
    }

    Drawable u(int i) {
        return s(i, this.J);
    }

    protected abstract void v(int i);

    void w(View view) {
        g.a0.w(view, this.I, this.A.height * 0.1f);
    }

    protected abstract void x(boolean z);
}
