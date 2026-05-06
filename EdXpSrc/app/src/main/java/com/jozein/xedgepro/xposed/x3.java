package com.jozein.xedgepro.xposed;

import android.os.Build;
import de.robv.android.xposed.SELinuxHelper;
import de.robv.android.xposed.services.BaseService;
import f.q;
import java.io.FileNotFoundException;

/* JADX INFO: loaded from: classes.dex */
class x3 extends f.q {
    private final BaseService E;
    private final String F;

    private static class b extends q.a {
        private b() {
        }

        @Override // f.q.a
        public f.q a(String str) {
            return new x3(str);
        }
    }

    private x3(String str) {
        this.F = str;
        this.E = SELinuxHelper.getAppDataFileService();
    }

    static void l() {
        try {
            if (Build.VERSION.SDK_INT >= 28 || !SELinuxHelper.isSELinuxEnforced()) {
                return;
            }
            f.q.k(new b());
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    @Override // f.q
    public boolean d() {
        try {
            return this.E.checkFileExists(this.F);
        } catch (Throwable unused) {
            return false;
        }
    }

    @Override // f.q
    public long f() {
        try {
            return this.E.getFileModificationTime(this.F);
        } catch (Throwable unused) {
            return 0L;
        }
    }

    @Override // f.q
    public byte[] j() {
        try {
            return this.E.readFile(this.F);
        } catch (FileNotFoundException e2) {
            f.v.c(e2.toString());
            return f.q.z;
        } catch (Throwable unused) {
            return f.q.z;
        }
    }
}
