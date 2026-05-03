package f;

/* JADX INFO: loaded from: classes.dex */
public class e implements s {
    public static final byte[] B = new byte[0];
    private byte[] z = null;
    private int A = 0;

    private void k(int i) {
        int i2 = this.A;
        int i3 = i2 + i;
        byte[] bArr = this.z;
        if (i3 >= bArr.length) {
            this.z = r(i + i2, bArr, i2);
        }
    }

    private int q() {
        byte[] bArr = this.z;
        int i = this.A;
        int i2 = i + 1;
        this.A = i2;
        int i3 = bArr[i] & 255;
        int i4 = i2 + 1;
        this.A = i4;
        int i5 = bArr[i2] & 255;
        int i6 = i4 + 1;
        this.A = i6;
        int i7 = bArr[i4] & 255;
        this.A = i6 + 1;
        return ((bArr[i6] & 255) << 24) | i3 | (i5 << 8) | (i7 << 16);
    }

    private void x(int i) {
        k(4);
        byte[] bArr = this.z;
        int i2 = this.A;
        int i3 = i2 + 1;
        this.A = i3;
        bArr[i2] = (byte) (i & 255);
        int i4 = i3 + 1;
        this.A = i4;
        bArr[i3] = (byte) ((i >> 8) & 255);
        int i5 = i4 + 1;
        this.A = i5;
        bArr[i4] = (byte) ((i >> 16) & 255);
        this.A = i5 + 1;
        bArr[i5] = (byte) ((i >> 24) & 255);
    }

    @Override // f.s
    public String a() {
        if (this.z.length - this.A < 4) {
            v.c("Out of range.");
            return null;
        }
        int iQ = q();
        if (iQ == -1) {
            return null;
        }
        if (iQ == 0) {
            return "";
        }
        if (iQ <= -1) {
            this.A -= 4;
            v.c(iQ < -2 ? "Invalid data." : "Error read string! Original data: int.");
            return null;
        }
        int i = this.A;
        int i2 = iQ + i;
        byte[] bArr = this.z;
        if (i2 <= bArr.length) {
            this.A = i + iQ;
            return new String(bArr, i, iQ, p0.z);
        }
        v.c("Error read string: data lost.");
        int i3 = this.A;
        byte[] bArr2 = this.z;
        this.A = bArr2.length;
        return new String(bArr2, i3, bArr2.length - i3, p0.z);
    }

    @Override // f.s
    public String e() {
        return a();
    }

    @Override // f.s
    public a.b g() {
        return a.b.s(this);
    }

    @Override // f.s
    public int h() {
        if (this.z.length - this.A >= 8) {
            int iQ = q();
            if (iQ == -2) {
                return q();
            }
            this.A -= 4;
            v.c(iQ < -2 ? "Invalid data." : "Error read int! Original data: string.");
            return 0;
        }
        v.d(new IndexOutOfBoundsException("pos=" + this.A + ", length=" + this.z.length));
        return 0;
    }

    public void i(byte[] bArr) {
        if (bArr == null) {
            bArr = B;
        }
        this.z = bArr;
        this.A = 0;
    }

    public e j() {
        if (this.z == null) {
            this.z = new byte[16384];
        }
        this.A = 0;
        return this;
    }

    protected void l() {
        this.z = B;
        this.A = 0;
    }

    public void m() {
        this.z = B;
        this.A = 0;
    }

    public byte[] n() {
        int i = this.A;
        byte[] bArr = new byte[i];
        System.arraycopy(this.z, 0, bArr, 0, i);
        this.z = null;
        this.A = 0;
        return bArr;
    }

    protected byte[] o() {
        return this.z;
    }

    protected int p() {
        return this.A;
    }

    protected byte[] r(int i, byte[] bArr, int i2) {
        byte[] bArr2 = new byte[((i + 16384) / 16384) * 16384];
        System.arraycopy(this.z, 0, bArr2, 0, this.A);
        return bArr2;
    }

    protected void s(int i) {
        this.A = i;
    }

    @Override // f.s
    /* JADX INFO: renamed from: t, reason: merged with bridge method [inline-methods] */
    public e d(int i) {
        x(-2);
        x(i);
        return this;
    }

    @Override // f.s
    /* JADX INFO: renamed from: u, reason: merged with bridge method [inline-methods] */
    public e f(a.b bVar) {
        bVar.y(this);
        return this;
    }

    @Override // f.s
    /* JADX INFO: renamed from: v, reason: merged with bridge method [inline-methods] */
    public e c(String str) {
        if (str == null) {
            x(-1);
            return this;
        }
        if (str.length() == 0) {
            x(0);
            return this;
        }
        byte[] bytes = str.getBytes(p0.z);
        x(bytes.length);
        k(bytes.length);
        byte[] bArr = this.z;
        for (byte b2 : bytes) {
            int i = this.A;
            this.A = i + 1;
            bArr[i] = b2;
        }
        return this;
    }

    @Override // f.s
    /* JADX INFO: renamed from: w, reason: merged with bridge method [inline-methods] */
    public e b(String str) {
        return c(str);
    }
}
