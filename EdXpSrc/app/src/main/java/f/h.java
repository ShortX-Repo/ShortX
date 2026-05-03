package f;

/* JADX INFO: loaded from: classes.dex */
public class h implements l, s {
    private static final byte[] C = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70};
    protected static final byte[] D = new byte[0];
    private byte[] z = null;
    private int A = 0;
    private char[] B = null;

    private void A(int i) {
        int i2 = 28;
        while (i2 > 0 && ((i >>> i2) & 15) == 0) {
            i2 -= 4;
        }
        while (i2 >= 0) {
            byte[] bArr = this.z;
            int i3 = this.A;
            this.A = i3 + 1;
            bArr[i3] = C[(i >>> i2) & 15];
            i2 -= 4;
        }
    }

    private void D(String str) {
        if (this.B == null) {
            this.B = new char[8192];
        }
        int length = str.length();
        str.getChars(0, length, this.B, 0);
        for (int i = 0; i < length; i++) {
            byte[] bArr = this.z;
            int i2 = this.A;
            this.A = i2 + 1;
            bArr[i2] = (byte) this.B[i];
        }
    }

    private void k(int i) {
        int i2 = this.A;
        if (i2 + i >= this.z.length) {
            this.z = u(i2 + i);
        }
    }

    private int r() {
        int i;
        int i2 = 0;
        while (true) {
            int i3 = this.A;
            byte[] bArr = this.z;
            if (i3 >= bArr.length) {
                break;
            }
            byte b2 = bArr[i3];
            if (b2 >= 48 && b2 <= 57) {
                i = b2 - 48;
            } else {
                if (b2 < 65 || b2 > 70) {
                    break;
                }
                i = b2 - 55;
            }
            i2 = (i2 << 4) | i;
            this.A = i3 + 1;
        }
        return i2;
    }

    public h B(String str) {
        if (this.z == null) {
            return this;
        }
        c(str);
        this.z[this.A - 1] = 10;
        return this;
    }

    public h C() {
        int i;
        byte[] bArr = this.z;
        if (bArr != null && (i = this.A) != 0 && bArr[i - 1] == 32) {
            bArr[i - 1] = 10;
        }
        return this;
    }

    @Override // f.s
    public String a() {
        char c2;
        int i = this.A;
        byte[] bArr = this.z;
        if (i >= bArr.length) {
            return null;
        }
        if (bArr[i] == 32) {
            this.A = i + 1;
        }
        if (this.B == null) {
            this.B = new char[8192];
        }
        int i2 = 0;
        while (true) {
            int i3 = this.A;
            byte[] bArr2 = this.z;
            if (i3 >= bArr2.length || (c2 = (char) bArr2[i3]) == ' ' || c2 == '\n') {
                break;
            }
            this.B[i2] = c2;
            this.A = i3 + 1;
            i2++;
        }
        if (i2 == 0 || (i2 == 1 && this.B[0] == '`')) {
            return null;
        }
        return String.valueOf(this.B, 0, i2);
    }

    @Override // f.s
    public String e() {
        int iH = h();
        if (iH == -1) {
            return null;
        }
        if (iH == 0) {
            return "";
        }
        int i = this.A + 1;
        this.A = i;
        byte[] bArr = this.z;
        if (i >= bArr.length) {
            return "";
        }
        int i2 = i + iH;
        this.A = i2;
        if (i2 > i + iH) {
            int length = bArr.length;
            this.A = length;
            iH = length - i;
        }
        return new String(bArr, i, iH, p0.z);
    }

    @Override // f.s
    public a.b g() {
        return a.b.s(this);
    }

    @Override // f.s
    public int h() {
        int i = this.A;
        byte[] bArr = this.z;
        if (i >= bArr.length) {
            return 0;
        }
        if (bArr[i] == 32) {
            this.A = i + 1;
        }
        int i2 = this.A;
        if (i2 < bArr.length && bArr[i2] == 32) {
            this.A = i2 + 1;
        }
        return r();
    }

    public void i(byte[] bArr) {
        if (bArr == null) {
            bArr = D;
        }
        this.z = bArr;
        this.A = 0;
    }

    public h j() {
        this.z = new byte[16384];
        this.A = 0;
        return this;
    }

    protected void l() {
        this.z = D;
        this.A = 0;
    }

    public void m() {
        this.z = null;
    }

    public void n() {
        v.d(new Throwable("Should not be used."));
    }

    public byte[] o() {
        int i = this.A;
        byte[] bArr = new byte[i];
        System.arraycopy(this.z, 0, bArr, 0, i);
        this.z = null;
        this.A = 0;
        return bArr;
    }

    protected byte[] p() {
        return this.z;
    }

    protected int q() {
        return this.A;
    }

    public String s() {
        int i = this.A;
        byte[] bArr = this.z;
        if (i >= bArr.length) {
            return null;
        }
        if (bArr[i] == 32) {
            this.A = i + 1;
        }
        if (this.B == null) {
            this.B = new char[8192];
        }
        int i2 = 0;
        while (true) {
            int i3 = this.A;
            byte[] bArr2 = this.z;
            if (i3 >= bArr2.length) {
                break;
            }
            this.A = i3 + 1;
            char c2 = (char) bArr2[i3];
            if (c2 == '\n') {
                break;
            }
            this.B[i2] = c2;
            i2++;
        }
        if (i2 == 0) {
            return null;
        }
        return String.valueOf(this.B, 0, i2);
    }

    public boolean t() {
        int i;
        byte[] bArr;
        do {
            i = this.A;
            bArr = this.z;
            if (i >= bArr.length) {
                break;
            }
            this.A = i + 1;
        } while (bArr[i] != 10);
        return this.A < bArr.length;
    }

    protected byte[] u(int i) {
        byte[] bArr = new byte[((i + 16384) / 16384) * 16384];
        System.arraycopy(this.z, 0, bArr, 0, this.A);
        return bArr;
    }

    protected void v(int i) {
        this.A = i;
    }

    @Override // f.s
    /* JADX INFO: renamed from: w, reason: merged with bridge method [inline-methods] */
    public h d(int i) {
        k(9);
        if (this.z == null) {
            return this;
        }
        A(i);
        byte[] bArr = this.z;
        int i2 = this.A;
        this.A = i2 + 1;
        bArr[i2] = 32;
        return this;
    }

    @Override // f.s
    /* JADX INFO: renamed from: x, reason: merged with bridge method [inline-methods] */
    public h f(a.b bVar) {
        if (this.z == null) {
            return this;
        }
        bVar.y(this);
        return this;
    }

    @Override // f.s
    /* JADX INFO: renamed from: y, reason: merged with bridge method [inline-methods] */
    public h c(String str) {
        if (this.z == null) {
            return this;
        }
        String str2 = "`";
        if (str == null || str.length() == 0) {
            str = "`";
        }
        int length = str.length();
        if (length > 8192) {
            v.c("String length > MAX_STRING_LENGTH");
            length = 1;
        } else {
            str2 = str;
        }
        k(length + 1);
        D(str2);
        byte[] bArr = this.z;
        int i = this.A;
        this.A = i + 1;
        bArr[i] = 32;
        return this;
    }

    @Override // f.s
    /* JADX INFO: renamed from: z, reason: merged with bridge method [inline-methods] */
    public h b(String str) {
        if (this.z == null) {
            return this;
        }
        if (str == null) {
            d(-1);
        } else {
            if (str.length() > 0) {
                byte[] bytes = str.getBytes(p0.z);
                d(bytes.length);
                k(bytes.length + 1);
                for (byte b2 : bytes) {
                    byte[] bArr = this.z;
                    int i = this.A;
                    this.A = i + 1;
                    bArr[i] = b2;
                }
                byte[] bArr2 = this.z;
                int i2 = this.A;
                this.A = i2 + 1;
                bArr2[i2] = 32;
            } else {
                d(0);
            }
        }
        return this;
    }
}
