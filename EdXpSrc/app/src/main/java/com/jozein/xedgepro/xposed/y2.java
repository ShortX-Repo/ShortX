package com.jozein.xedgepro.xposed;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Property;
import android.view.View;
import com.jozein.xedgepro.xposed.b0;

/* JADX INFO: loaded from: classes.dex */
class y2 implements b0.a {
    private static final Property<b, Float> J = new a(Float.class, "setAnimateProgress");
    private final Context A;
    private final Handler B;
    private final a.p C;
    private final w1 D;
    private final Paint E;
    private final Paint F;
    private final Paint G;
    private final Runnable H = new Runnable() { // from class: com.jozein.xedgepro.xposed.u2
        @Override // java.lang.Runnable
        public final void run() {
            this.z.q();
        }
    };
    private b I = null;
    private final Context z;

    class a extends Property<b, Float> {
        a(Class cls, String str) {
            super(cls, str);
        }

        @Override // android.util.Property
        /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
        public Float get(b bVar) {
            return Float.valueOf(bVar.T);
        }

        @Override // android.util.Property
        /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
        public void set(b bVar, Float f2) {
            bVar.i(f2.floatValue());
        }
    }

    private class b extends View {
        private final float A;
        private final float B;
        private final Drawable[] C;
        private final Drawable[] D;
        private final int E;
        private final int F;
        private final int G;
        private final int H;
        private final int I;
        private final int J;
        private final float K;
        private final float L;
        private final RectF M;
        private final RectF N;
        private final int[] O;
        private final int[] P;
        private final Point Q;
        private int R;
        private boolean S;
        float T;
        private final int z;

        public b(int i, float f2, float f3, Drawable[] drawableArr, Drawable[] drawableArr2, int i2, int i3) {
            super(y2.this.z);
            this.Q = new Point();
            this.R = -1;
            this.S = false;
            this.T = 1.0f;
            this.z = i;
            this.A = f2;
            this.B = f3;
            this.C = drawableArr;
            this.D = drawableArr2;
            this.E = i2;
            float f4 = 160.0f / i2;
            this.L = f4;
            if (i != 1) {
                this.K = i != 2 ? i != 3 ? -80.0f : 190.0f : 10.0f;
            } else {
                this.K = 100.0f;
            }
            float fM = (g.a0.m(y2.this.z) * y2.this.C.v(26, 100)) / 100.0f;
            int i4 = (int) ((0.4f * fM) + 0.5f);
            int i5 = i4 / 2;
            int i6 = (int) ((i4 * 0.23f) + 0.5f);
            this.G = i6;
            this.F = i6;
            int i7 = (int) ((0.02f * fM) + 0.5f);
            int i8 = (int) ((fM * 0.93f) + 0.5f);
            this.I = i8;
            this.H = i8 - i4;
            int i9 = i8 - i5;
            i8 = drawableArr2 != null ? i8 + i4 : i8;
            this.J = i8;
            int i10 = i8 - i5;
            y2.this.E.setColor(i3);
            float f5 = i4 - i7;
            y2.this.E.setStrokeWidth(f5);
            y2.this.F.setColor(h(i3));
            y2.this.F.setStrokeWidth(f5);
            y2.this.G.setStrokeWidth(i7);
            float f6 = i9;
            this.M = new RectF(f2 - f6, f3 - f6, f2 + f6, f6 + f3);
            float f7 = i10;
            this.N = new RectF(f2 - f7, f3 - f7, f2 + f7, f3 + f7);
            int i11 = i2 * 2;
            this.O = new int[i11];
            this.P = new int[i11];
            float f8 = this.K + (f4 / 2.0f);
            for (int i12 = 0; i12 < i2; i12++) {
                float f9 = (this.L * i12) + f8;
                Point pointG = g(f9, i9);
                int[] iArr = this.O;
                int i13 = i12 * 2;
                iArr[i13] = pointG.x;
                int i14 = i13 + 1;
                iArr[i14] = pointG.y;
                Point pointG2 = g(f9, i10);
                int[] iArr2 = this.P;
                iArr2[i13] = pointG2.x;
                iArr2[i14] = pointG2.y;
            }
            if (e()) {
                for (int i15 = 0; i15 < i2 / 2; i15++) {
                    int i16 = (i2 - 1) - i15;
                    Drawable drawable = drawableArr[i15];
                    drawableArr[i15] = drawableArr[i16];
                    drawableArr[i16] = drawable;
                    if (drawableArr2 != null) {
                        Drawable drawable2 = drawableArr2[i15];
                        drawableArr2[i15] = drawableArr2[i16];
                        drawableArr2[i16] = drawable2;
                    }
                }
            }
            setKeepScreenOn(true);
            j();
        }

        private float a(float f2, float f3) {
            float f4 = f2 - this.A;
            float f5 = f3 - this.B;
            float fAtan2 = (((float) ((Math.atan2(f5, f4) * 180.0d) / 3.141592653589793d)) + 360.0f) % 90.0f;
            return f4 > 0.0f ? f5 > 0.0f ? fAtan2 : fAtan2 + 270.0f : f5 > 0.0f ? fAtan2 + 90.0f : fAtan2 + 180.0f;
        }

        /* JADX WARN: Removed duplicated region for block: B:39:0x00cb  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        private void b(android.graphics.Canvas r16) {
            /*
                Method dump skipped, instruction units count: 358
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: com.jozein.xedgepro.xposed.y2.b.b(android.graphics.Canvas):void");
        }

        private void c() {
            Drawable[] drawableArr = this.C;
            if (drawableArr != null) {
                for (Drawable drawable : drawableArr) {
                    if (drawable instanceof g.g) {
                        ((g.g) drawable).c();
                    }
                }
            }
            Drawable[] drawableArr2 = this.D;
            if (drawableArr2 != null) {
                for (Drawable drawable2 : drawableArr2) {
                    if (drawable2 instanceof g.g) {
                        ((g.g) drawable2).c();
                    }
                }
            }
        }

        private Point g(float f2, int i) {
            Point point = this.Q;
            double d2 = this.A;
            double d3 = f2 * 0.017453292f;
            double dCos = Math.cos(d3);
            double d4 = i;
            Double.isNaN(d4);
            Double.isNaN(d2);
            point.x = (int) (d2 + (dCos * d4) + 0.5d);
            Point point2 = this.Q;
            double d5 = this.B;
            double dSin = Math.sin(d3);
            Double.isNaN(d4);
            Double.isNaN(d5);
            point2.y = (int) (d5 + (dSin * d4) + 0.5d);
            return this.Q;
        }

        private int h(int i) {
            int i2;
            int i3;
            int iAlpha = Color.alpha(i);
            int iRed = Color.red(i);
            int iGreen = Color.green(i);
            int iBlue = Color.blue(i);
            int i4 = 0;
            if (iRed + iGreen + iBlue >= 384) {
                i2 = iRed - 48;
                if (i2 < 0) {
                    i2 = 0;
                }
                i3 = iGreen - 48;
                if (i3 < 0) {
                    i3 = 0;
                }
                int i5 = iBlue - 48;
                if (i5 >= 0) {
                    i4 = i5;
                }
            } else {
                i2 = iRed + 48;
                if (i2 > 255) {
                    i2 = 255;
                }
                i3 = iGreen + 48;
                if (i3 > 255) {
                    i3 = 255;
                }
                i4 = iBlue + 48;
                if (i4 > 255) {
                    i4 = 255;
                }
            }
            return Color.argb(iAlpha, i2, i3, i4);
        }

        private void j() {
            this.T = 0.0f;
            c();
            ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(this, (Property<b, Float>) y2.J, 0.0f, 1.0f);
            objectAnimatorOfFloat.setDuration(100L);
            objectAnimatorOfFloat.start();
        }

        public void d() {
            this.S = true;
        }

        public boolean e() {
            int i = this.z;
            return i == 1 || i == 2;
        }

        public void f() {
            int i;
            if (this.S || (i = this.R) < 0 || this.T < 1.0f) {
                return;
            }
            int i2 = 65535 & i;
            boolean z = (i >>> 16) != 0;
            if (e()) {
                i2 = (this.E - 1) - i2;
            }
            if (z) {
                i2 += 6;
            }
            a.b bVarL = y2.this.C.l(this.z, i2);
            int i3 = bVarL.z;
            if (i3 == 0 || i3 == 1) {
                return;
            }
            y2.this.D.u3(bVarL);
        }

        void i(float f2) {
            this.T = f2;
            invalidate();
        }

        public boolean k(float f2, float f3) {
            float f4 = f2 - this.A;
            float f5 = f3 - this.B;
            float f6 = (f4 * f4) + (f5 * f5);
            int i = this.H;
            if (f6 >= i * i) {
                int i2 = this.J;
                if (f6 <= i2 * i2) {
                    float fA = a(f2, f3);
                    if (this.z == 0 && fA >= 270.0f) {
                        fA -= 360.0f;
                    }
                    int i3 = 0;
                    while (i3 < this.E) {
                        float f7 = this.K;
                        float f8 = this.L;
                        float f9 = f7 + (i3 * f8);
                        if (fA > f9 && fA < f9 + f8) {
                            int i4 = this.I;
                            if (f6 > ((float) (i4 * i4))) {
                                i3 += 65536;
                            }
                            if (this.R == i3) {
                                return false;
                            }
                            this.R = i3;
                            return true;
                        }
                        i3++;
                    }
                }
            }
            if (this.R == -1) {
                return false;
            }
            this.R = -1;
            return true;
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            try {
                b(canvas);
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
    }

    y2(a.p pVar, w1 w1Var, Context context, Handler handler) {
        this.C = pVar;
        this.D = w1Var;
        this.B = handler;
        this.z = context;
        this.A = p2.d(context);
        Paint paint = new Paint(1);
        this.E = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.F = new Paint(paint);
        Paint paint2 = new Paint(1);
        this.G = paint2;
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void q() {
        b bVar = this.I;
        this.I = null;
        if (bVar != null) {
            bVar.d();
            t3.n().t(6);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void r(float f2, float f3) {
        b bVar = this.I;
        if (bVar == null || !bVar.k(f2, f3)) {
            return;
        }
        bVar.invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void s(int i, float f2, float f3) {
        int iV = this.C.v(4, -14509620);
        int iV2 = this.C.v(30, 0);
        int i2 = -1;
        if (iV2 == -1) {
            iV2 = 0;
        }
        g.h hVarA = g.h.a();
        Drawable[] drawableArr = new Drawable[6];
        Drawable[] drawableArr2 = new Drawable[6];
        int i3 = -1;
        for (int i4 = 0; i4 < 6; i4++) {
            a.b bVarL = this.C.l(i, i4);
            int i5 = bVarL.z;
            if (i5 != 0 && i5 != 1) {
                Drawable drawableM = bVarL.m(this.A);
                hVarA.e(drawableM, iV2);
                drawableArr[i4] = drawableM;
                i3 = i4;
            }
            a.b bVarL2 = this.C.l(i, i4 + 6);
            int i6 = bVarL2.z;
            if (i6 != 0 && i6 != 1) {
                Drawable drawableM2 = bVarL2.m(this.A);
                hVarA.e(drawableM2, iV2);
                drawableArr2[i4] = drawableM2;
                i2 = i4;
            }
        }
        if (i2 < 0) {
            drawableArr2 = null;
        }
        int iMax = Math.max(i3 + 1, i2 + 1);
        this.I = new b(i, f2, f3, drawableArr, drawableArr2, iMax < 1 ? 1 : iMax, iV);
        t3.n().j(this.I, 6);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void t(float f2, float f3) {
        b bVar = this.I;
        if (bVar != null) {
            bVar.k(f2, f3);
            bVar.f();
        }
        q();
    }

    @Override // com.jozein.xedgepro.xposed.b0.a
    public void b(final float f2, final float f3, long j) {
        this.B.post(new Runnable() { // from class: com.jozein.xedgepro.xposed.v2
            @Override // java.lang.Runnable
            public final void run() {
                this.z.t(f2, f3);
            }
        });
    }

    @Override // com.jozein.xedgepro.xposed.b0.a
    public void c() {
        this.B.post(this.H);
    }

    @Override // com.jozein.xedgepro.xposed.b0.a
    public void d(final float f2, final float f3, long j) {
        this.B.post(new Runnable() { // from class: com.jozein.xedgepro.xposed.w2
            @Override // java.lang.Runnable
            public final void run() {
                this.z.r(f2, f3);
            }
        });
    }

    @Override // com.jozein.xedgepro.xposed.b0.a
    public void e(final int i, final float f2, final float f3, long j) {
        this.B.post(new Runnable() { // from class: com.jozein.xedgepro.xposed.x2
            @Override // java.lang.Runnable
            public final void run() {
                this.z.s(i, f2, f3);
            }
        });
    }

    void p() {
        if (this.I != null) {
            c();
        }
    }
}
