package f;

import f.q;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class f extends e {
    protected final File C;
    private final boolean D;
    private boolean E = false;

    public f(File file, boolean z) {
        this.C = file;
        this.D = z;
    }

    public f(String str, boolean z) {
        this.C = new File(str);
        this.D = z;
    }

    public f A(boolean z) {
        this.E = this.C.exists() && z;
        super.j();
        return this;
    }

    public void B() {
        C(o(), p());
        l();
        if (!this.D) {
            this.C.setReadable(true, false);
        }
        this.E = false;
    }

    protected void C(byte[] bArr, int i) {
        FileOutputStream fileOutputStream;
        if (bArr == null) {
            return;
        }
        File parentFile = this.C.getParentFile();
        if (!parentFile.exists() && (!parentFile.mkdir() || !parentFile.setExecutable(true, this.D))) {
            v.c("Error make world readable dir!");
            return;
        }
        FileOutputStream fileOutputStream2 = null;
        try {
            fileOutputStream = new FileOutputStream(this.C, this.E);
            try {
                fileOutputStream.write(bArr, 0, i);
                this.E = true;
            } catch (IOException e2) {
                e = e2;
                fileOutputStream2 = fileOutputStream;
                v.d(e);
                l();
                fileOutputStream = fileOutputStream2;
            }
        } catch (IOException e3) {
            e = e3;
        }
        if (fileOutputStream != null) {
            try {
                fileOutputStream.close();
            } catch (IOException e4) {
                v.d(e4);
            }
        }
    }

    @Override // f.e
    public void i(byte[] bArr) {
        throw new UnsupportedOperationException("Use beginRead() instead!");
    }

    @Override // f.e
    public void m() {
        l();
    }

    @Override // f.e
    protected byte[] r(int i, byte[] bArr, int i2) {
        C(bArr, i2);
        s(0);
        return i <= bArr.length ? bArr : new byte[((i + 16384) / 16384) * 16384];
    }

    public boolean y() {
        byte[] bArrJ = this.D ? new q.b(this.C.getPath()).j() : q.c(this.C.getPath()).j();
        super.i(bArrJ);
        return (bArrJ == null || bArrJ.length == 0) ? false : true;
    }

    @Override // f.e
    /* JADX INFO: renamed from: z, reason: merged with bridge method [inline-methods] */
    public f j() {
        return A(false);
    }
}
