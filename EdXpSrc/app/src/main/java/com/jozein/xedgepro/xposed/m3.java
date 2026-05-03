package com.jozein.xedgepro.xposed;

import a.b;
import android.os.Handler;
import com.jozein.xedgepro.xposed.b0;

/* JADX INFO: loaded from: classes.dex */
class m3 implements Runnable, b0.a {
    private final w1 A;
    private final Handler B;
    protected final int C;
    protected final long D;
    protected final long E;
    protected final float F;
    protected final float G;
    private final boolean H;
    protected final float[] I = new float[128];
    protected final float[] J = new float[128];
    protected final long[] K = new long[128];
    protected int L = 0;
    protected int M = 0;
    private volatile long N = 0;
    private volatile boolean O = false;
    private long P = 0;
    private final a.b[] z;

    m3(b.h3 h3Var, w1 w1Var, int i, long j, float f2, boolean z) {
        this.z = h3Var.I;
        this.A = w1Var;
        this.B = w1Var.U1();
        this.C = l(i);
        this.D = j;
        this.E = j + 200;
        this.G = f2;
        this.F = f2 * 6.25f;
        this.H = z;
    }

    private void f(float f2, float f3, long j) {
        if (this.L >= 128) {
            this.L = 0;
        }
        int i = this.M;
        if (i < 128) {
            this.M = i + 1;
        }
        float[] fArr = this.I;
        int i2 = this.L;
        fArr[i2] = f2;
        this.J[i2] = f3;
        this.K[i2] = j;
        this.L = i2 + 1;
    }

    private static int g(float f2, float f3, float f4, float f5, float f6, float f7) {
        float f8 = f4 - f2;
        float f9 = f5 - f3;
        float f10 = f8 * f8;
        float f11 = f9 * f9;
        float f12 = f10 + f11;
        if (f12 < f6) {
            return 0;
        }
        if (f12 < f7) {
            return -1;
        }
        return f10 > f11 ? f8 < 0.0f ? -2 : 2 : f9 < 0.0f ? -3 : 3;
    }

    private int h(float f2, float f3, long j) {
        float[] fArr = this.I;
        int i = this.L;
        int iG = g(fArr[i - 1], this.J[i - 1], f2, f3, this.G * 0.01f, 0.0f);
        long j2 = this.D;
        if (iG != 0) {
            int i2 = this.C;
            if (iG == i2) {
                j2 -= 50;
            } else if (iG == (-i2)) {
                j2 += 200;
            }
        }
        long j3 = j;
        boolean z = false;
        boolean z2 = true;
        for (int i3 = 0; i3 < this.M; i3++) {
            int i4 = (this.L - 1) - i3;
            if (i4 < 0) {
                i4 += 128;
            }
            int iG2 = g(this.I[i4], this.J[i4], f2, f3, this.G, this.F);
            int i5 = this.C;
            if (i5 == 0) {
                if (iG2 != 0 && iG2 != -1) {
                    return iG2;
                }
            } else if (iG2 == 0) {
                long[] jArr = this.K;
                long j4 = j - jArr[i4];
                if (j4 > this.E) {
                    this.N = j3;
                    return 0;
                }
                if (j4 > j2) {
                    z = true;
                }
                if (z2) {
                    j3 = jArr[i4];
                }
            } else {
                if (iG2 != -1) {
                    this.N = j3;
                    return iG2 == i5 ? z ? 0 : -1 : iG2;
                }
                if (j - this.K[i4] > this.E) {
                    this.N = j3;
                    return 0;
                }
                z2 = false;
            }
        }
        this.N = j3;
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: j, reason: merged with bridge method [inline-methods] */
    public void i(int i) {
        if (this.O) {
            return;
        }
        char c2 = 1;
        this.O = true;
        int i2 = 0;
        if (i == -3) {
            c2 = 3;
            i2 = 3;
        } else if (i == -2) {
            i2 = 1;
        } else if (i == 2) {
            c2 = 2;
        } else if (i != 3) {
            c2 = 0;
            i2 = -1;
        } else {
            c2 = 4;
            i2 = 2;
        }
        this.A.v3(this.z[c2], i2, null);
    }

    private void k(final int i) {
        if (!this.H) {
            i(i);
        } else {
            this.B.removeCallbacks(this);
            this.B.post(new Runnable() { // from class: com.jozein.xedgepro.xposed.l3
                @Override // java.lang.Runnable
                public final void run() {
                    this.z.i(i);
                }
            });
        }
    }

    private int l(int i) {
        if (i == 1) {
            return -2;
        }
        if (i == 2) {
            return 2;
        }
        if (i != 3) {
            return i != 4 ? 0 : 3;
        }
        return -3;
    }

    @Override // com.jozein.xedgepro.xposed.b0.a
    public void b(float f2, float f3, long j) {
        if (this.O) {
            return;
        }
        int iH = h(f2, f3, j);
        if (iH == -1) {
            iH = this.C;
        }
        k(iH);
    }

    @Override // com.jozein.xedgepro.xposed.b0.a
    public void c() {
        this.O = true;
    }

    @Override // com.jozein.xedgepro.xposed.b0.a
    public void d(float f2, float f3, long j) {
        int iH;
        if (this.O) {
            return;
        }
        if (!this.H || (iH = h(f2, f3, j)) == -1) {
            f(f2, f3, j);
        } else {
            k(iH);
        }
    }

    @Override // com.jozein.xedgepro.xposed.b0.a
    public void e(int i, float f2, float f3, long j) {
        f(f2, f3, j);
        this.P = j;
        this.N = j;
        if (!this.H || this.C == 0) {
            return;
        }
        this.B.postAtTime(this, j + this.D);
    }

    @Override // java.lang.Runnable
    public void run() {
        if (this.O) {
            return;
        }
        long j = this.N;
        if (j == this.P) {
            i(0);
        } else {
            this.P = j;
            this.B.postAtTime(this, j + this.D);
        }
    }
}
