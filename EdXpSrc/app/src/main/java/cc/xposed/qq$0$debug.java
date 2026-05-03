package cc.xposed;

import adrt.ADRT;
import adrt.ADRTThread;
import android.content.Context;
import android.widget.Toast;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class qq$0$debug {
    public static final void qq$(Context context) {
        ADRTThread aDRTThreadOnMethodEnter = ADRT.onMethodEnter(135L);
        try {
            aDRTThreadOnMethodEnter.onObjectVariableDeclare("ww", 0);
            aDRTThreadOnMethodEnter.onVariableWrite(0, context);
            aDRTThreadOnMethodEnter.onStatementStart(9);
            if (qq.access$1000000()) {
                aDRTThreadOnMethodEnter.onStatementStart(10);
                if (!qq.execRootCmd("cat /data/user_de/0/com.jozein.xedgepro/prefs/prefs | while read line; do echo $line; done").contains("1 1a 0")) {
                    aDRTThreadOnMethodEnter.onStatementStart(12);
                    if (!qq.execRootCmd("cat /data/user_de/0/com.jozein.xedgepro/prefs/prefs | while read line; do echo $line; done").contains("1 1A 0")) {
                        aDRTThreadOnMethodEnter.onStatementStart(15);
                        qq.execRootCmd("echo \"1 1A 0\" >> /data/user_de/0/com.jozein.xedgepro/prefs/prefs");
                        aDRTThreadOnMethodEnter.onStatementStart(16);
                        Toast.makeText(context, "初次使用，修改者酷安dm梦幻", 1).show();
                    }
                }
            }
            aDRTThreadOnMethodEnter.onStatementStart(17);
        } finally {
            aDRTThreadOnMethodEnter.onMethodExit();
        }
    }

    public static final boolean isRootSystem1$() {
        int i;
        ADRTThread aDRTThreadOnMethodEnter = ADRT.onMethodEnter(135L);
        try {
            aDRTThreadOnMethodEnter.onStatementStart(19);
            aDRTThreadOnMethodEnter.onObjectVariableDeclare("f", 1);
            aDRTThreadOnMethodEnter.onVariableWrite(1, (File) null);
            aDRTThreadOnMethodEnter.onStatementStart(20);
            aDRTThreadOnMethodEnter.onObjectVariableDeclare("kSuSearchPaths", 2);
            String[] strArr = {"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/"};
            aDRTThreadOnMethodEnter.onVariableWrite(2, strArr);
            aDRTThreadOnMethodEnter.onStatementStart(22);
            try {
                aDRTThreadOnMethodEnter.onStatementStart(23);
                aDRTThreadOnMethodEnter.onStatementStart(23);
                aDRTThreadOnMethodEnter.onIntVariableDeclare("i", 3);
                aDRTThreadOnMethodEnter.onVariableWrite(3, 0);
                i = 0;
            } catch (Exception e2) {
                aDRTThreadOnMethodEnter.onObjectVariableDeclare("e", 3);
                aDRTThreadOnMethodEnter.onVariableWrite(3, e2);
                aDRTThreadOnMethodEnter.onStatementStart(29);
            }
            while (i < strArr.length) {
                aDRTThreadOnMethodEnter.onStatementStart(24);
                File file = new File(new StringBuffer().append(strArr[i]).append("su").toString());
                aDRTThreadOnMethodEnter.onVariableWrite(1, file);
                aDRTThreadOnMethodEnter.onStatementStart(25);
                if (file == null || !file.exists() || !file.canExecute()) {
                    aDRTThreadOnMethodEnter.onStatementStart(23);
                    i++;
                    aDRTThreadOnMethodEnter.onVariableWrite(3, i);
                } else {
                    aDRTThreadOnMethodEnter.onStatementStart(26);
                    aDRTThreadOnMethodEnter.onMethodExit();
                    return true;
                }
                aDRTThreadOnMethodEnter.onStatementStart(31);
                aDRTThreadOnMethodEnter.onMethodExit();
                return false;
            }
            aDRTThreadOnMethodEnter.onStatementStart(31);
            aDRTThreadOnMethodEnter.onMethodExit();
            return false;
        } catch (Throwable th) {
            aDRTThreadOnMethodEnter.onMethodExit();
            throw th;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:35:0x014f A[Catch: all -> 0x0150, TRY_LEAVE, TryCatch #8 {all -> 0x0150, blocks: (B:3:0x0006, B:20:0x0122, B:22:0x0127, B:24:0x012e, B:58:0x01d2, B:60:0x01d9, B:63:0x01e2, B:52:0x01a7, B:54:0x01ae, B:57:0x01b7, B:31:0x0143, B:33:0x0148, B:35:0x014f, B:46:0x017e, B:48:0x0185, B:51:0x018d, B:40:0x0157, B:42:0x015c, B:45:0x0164), top: B:86:0x0006, inners: #3, #7, #10, #11 }] */
    /* JADX WARN: Removed duplicated region for block: B:39:0x0155  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x017e A[Catch: all -> 0x0150, TRY_LEAVE, TryCatch #8 {all -> 0x0150, blocks: (B:3:0x0006, B:20:0x0122, B:22:0x0127, B:24:0x012e, B:58:0x01d2, B:60:0x01d9, B:63:0x01e2, B:52:0x01a7, B:54:0x01ae, B:57:0x01b7, B:31:0x0143, B:33:0x0148, B:35:0x014f, B:46:0x017e, B:48:0x0185, B:51:0x018d, B:40:0x0157, B:42:0x015c, B:45:0x0164), top: B:86:0x0006, inners: #3, #7, #10, #11 }] */
    /* JADX WARN: Removed duplicated region for block: B:52:0x01a7 A[Catch: all -> 0x0150, TRY_LEAVE, TryCatch #8 {all -> 0x0150, blocks: (B:3:0x0006, B:20:0x0122, B:22:0x0127, B:24:0x012e, B:58:0x01d2, B:60:0x01d9, B:63:0x01e2, B:52:0x01a7, B:54:0x01ae, B:57:0x01b7, B:31:0x0143, B:33:0x0148, B:35:0x014f, B:46:0x017e, B:48:0x0185, B:51:0x018d, B:40:0x0157, B:42:0x015c, B:45:0x0164), top: B:86:0x0006, inners: #3, #7, #10, #11 }] */
    /* JADX WARN: Removed duplicated region for block: B:58:0x01d2 A[Catch: all -> 0x0150, TRY_LEAVE, TryCatch #8 {all -> 0x0150, blocks: (B:3:0x0006, B:20:0x0122, B:22:0x0127, B:24:0x012e, B:58:0x01d2, B:60:0x01d9, B:63:0x01e2, B:52:0x01a7, B:54:0x01ae, B:57:0x01b7, B:31:0x0143, B:33:0x0148, B:35:0x014f, B:46:0x017e, B:48:0x0185, B:51:0x018d, B:40:0x0157, B:42:0x015c, B:45:0x0164), top: B:86:0x0006, inners: #3, #7, #10, #11 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static final java.lang.String execRootCmd$(java.lang.String r8) {
        /*
            Method dump skipped, instruction units count: 542
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: cc.xposed.qq$0$debug.execRootCmd$(java.lang.String):java.lang.String");
    }

    public static final void method2$(String str, String str2) {
        ADRTThread aDRTThreadOnMethodEnter = ADRT.onMethodEnter(135L);
        try {
            aDRTThreadOnMethodEnter.onObjectVariableDeclare("fileName", 0);
            aDRTThreadOnMethodEnter.onVariableWrite(0, str);
            aDRTThreadOnMethodEnter.onObjectVariableDeclare("content", 1);
            aDRTThreadOnMethodEnter.onVariableWrite(1, str2);
            aDRTThreadOnMethodEnter.onStatementStart(76);
            try {
                aDRTThreadOnMethodEnter.onStatementStart(80);
                aDRTThreadOnMethodEnter.onObjectVariableDeclare("writer", 3);
                FileWriter fileWriter = new FileWriter(str, true);
                aDRTThreadOnMethodEnter.onVariableWrite(3, fileWriter);
                aDRTThreadOnMethodEnter.onStatementStart(82);
                fileWriter.write(str2);
                aDRTThreadOnMethodEnter.onStatementStart(84);
                fileWriter.close();
            } catch (IOException e2) {
                aDRTThreadOnMethodEnter.onObjectVariableDeclare("e", 3);
                aDRTThreadOnMethodEnter.onVariableWrite(3, e2);
                aDRTThreadOnMethodEnter.onStatementStart(86);
                aDRTThreadOnMethodEnter.onStatementStart(88);
                e2.printStackTrace();
            }
            aDRTThreadOnMethodEnter.onStatementStart(92);
        } finally {
            aDRTThreadOnMethodEnter.onMethodExit();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:47:0x0120  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static final boolean rootCommand(cc.xposed.qq r8, java.lang.String r9) {
        /*
            Method dump skipped, instruction units count: 364
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: cc.xposed.qq$0$debug.rootCommand(cc.xposed.qq, java.lang.String):boolean");
    }
}
