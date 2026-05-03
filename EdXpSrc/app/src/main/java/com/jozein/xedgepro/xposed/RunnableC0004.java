package com.jozein.xedgepro.xposed;

import android.icu.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/* JADX INFO: renamed from: com.jozein.xedgepro.xposed.粘贴, reason: contains not printable characters */
/* JADX INFO: loaded from: classes.dex */
public class RunnableC0004 implements Runnable {
    private r rr;

    RunnableC0004(r rVar) {
        this.rr = rVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        String strM4 = m4();
        if (strM4 == null || strM4.length() <= 0) {
            return;
        }
        xx(strM4);
    }

    /* JADX INFO: renamed from: 获取当前时间, reason: contains not printable characters */
    private String m4() {
        return new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    private void xx(String str) {
        this.rr.I.o2(str, false);
    }
}
