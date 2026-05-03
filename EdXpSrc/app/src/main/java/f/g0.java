package f;

import android.os.Handler;

/* JADX INFO: loaded from: classes.dex */
public abstract class g0 implements Runnable {
    private final int A;
    private final long B;
    private int C = 0;
    private final Handler z;

    public g0(Handler handler, int i, long j) {
        this.z = handler;
        this.A = i;
        this.B = j;
    }

    private void c() {
        try {
            b();
        } catch (Throwable th) {
            v.d(th);
        }
        this.C = this.A;
    }

    protected abstract boolean a(int i);

    protected abstract void b();

    public final void d() {
        this.C = 0;
        this.z.removeCallbacks(this);
        e();
    }

    public final void e() {
        if (this.C < this.A) {
            this.z.postDelayed(this, this.B);
        }
    }

    public final void f() {
        this.C = this.A;
        this.z.removeCallbacks(this);
    }

    /* JADX WARN: Removed duplicated region for block: B:7:0x001a A[Catch: all -> 0x001e, TRY_LEAVE, TryCatch #0 {all -> 0x001e, blocks: (B:2:0x0000, B:4:0x0009, B:6:0x0012, B:7:0x001a), top: B:13:0x0000 }] */
    @Override // java.lang.Runnable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void run() {
        /*
            r3 = this;
            int r0 = r3.C     // Catch: java.lang.Throwable -> L1e
            boolean r0 = r3.a(r0)     // Catch: java.lang.Throwable -> L1e
            r1 = 1
            if (r0 != r1) goto L1a
            int r0 = r3.C     // Catch: java.lang.Throwable -> L1e
            int r0 = r0 + r1
            r3.C = r0     // Catch: java.lang.Throwable -> L1e
            int r1 = r3.A     // Catch: java.lang.Throwable -> L1e
            if (r0 >= r1) goto L1a
            android.os.Handler r0 = r3.z     // Catch: java.lang.Throwable -> L1e
            long r1 = r3.B     // Catch: java.lang.Throwable -> L1e
            r0.postDelayed(r3, r1)     // Catch: java.lang.Throwable -> L1e
            goto L25
        L1a:
            r3.c()     // Catch: java.lang.Throwable -> L1e
            goto L25
        L1e:
            r0 = move-exception
            f.v.d(r0)
            r3.c()
        L25:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: f.g0.run():void");
    }
}
