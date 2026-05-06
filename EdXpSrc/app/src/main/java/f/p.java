package f;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class p extends h {
    protected final File E;
    private boolean F = false;

    public p(String str) {
        this.E = new File(str);
    }

    public boolean E() {
        byte[] bArrJ = q.c(this.E.getPath()).j();
        super.i(bArrJ);
        return (bArrJ == null || bArrJ.length == 0) ? false : true;
    }

    @Override // f.h
    /* JADX INFO: renamed from: F, reason: merged with bridge method [inline-methods] */
    public p j() {
        return G(false);
    }

    public p G(boolean z) {
        this.F = this.E.exists() && z;
        super.j();
        return this;
    }

    public File H() {
        return this.E;
    }

    public long I() {
        return this.E.lastModified();
    }

    protected void J(byte[] bArr, int i) {
        FileOutputStream fileOutputStream;
        if (bArr == null) {
            return;
        }
        File parentFile = this.E.getParentFile();
        if (parentFile == null || parentFile.exists() || (parentFile.mkdir() && parentFile.setExecutable(true, false))) {
            FileOutputStream fileOutputStream2 = null;
            try {
                fileOutputStream = new FileOutputStream(this.E, this.F);
                try {
                    fileOutputStream.write(bArr, 0, i);
                    this.F = true;
                } catch (IOException e2) {
                    e = e2;
                    fileOutputStream2 = fileOutputStream;
                    v.c(e.toString());
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
    }

    @Override // f.h
    public void i(byte[] bArr) {
        throw new UnsupportedOperationException("Use beginRead() instead!");
    }

    @Override // f.h
    public void m() {
        l();
    }

    @Override // f.h
    public void n() {
        J(p(), q());
        l();
        this.E.setReadable(true, false);
        this.F = false;
    }

    @Override // f.h
    protected byte[] u(int i) {
        byte[] bArrP = p();
        J(bArrP, q());
        v(0);
        return i <= bArrP.length ? bArrP : new byte[((i + 16384) / 16384) * 16384];
    }
}
