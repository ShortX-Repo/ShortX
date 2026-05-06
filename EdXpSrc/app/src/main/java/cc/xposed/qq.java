package cc.xposed;

import adrt.ADRT;
import adrt.ADRTThread;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class qq {
    private static boolean adrt$enabled;

    static {
        ADRT.onClassLoad(135L, "cc.xposed.qq");
    }

    public static void qq(Context context) {
        if (!adrt$enabled) {
            if (isRootSystem1()) {
                execRootCmd("chmod -R 666 /data/user_de/0/com.jozein.xedgepro/prefs/prefs");
                if (!execRootCmd("cat /data/user_de/0/com.jozein.xedgepro/prefs/prefs | while read line; do echo $line; done").contains("1 1a 0") && !execRootCmd("cat /data/user_de/0/com.jozein.xedgepro/prefs/prefs | while read line; do echo $line; done").contains("1 1A 0")) {
                    execRootCmd("echo \"1 1A 0\" >> /data/user_de/0/com.jozein.xedgepro/prefs/prefs");
                    execRootCmd("chmod -R 666 /data/user_de/0/com.jozein.xedgepro/prefs/prefs");
                    new Handler().postDelayed(new Runnable() { // from class: cc.xposed.qq.100000000
                        private static boolean adrt$enabled;

                        static {
                            ADRT.onClassLoad(135L, "cc.xposed.qq$100000000");
                        }

                        @Override // java.lang.Runnable
                        public void run() {
                            if (adrt$enabled) {
                                0.debug.run(this);
                            } else {
                                System.exit(0);
                            }
                        }
                    }, 5000);
                    Toast.makeText(context, "初次使用，修改者酷安dm梦幻,q群681634799欢迎加群反馈，闪退再次打开", 1).show();
                    return;
                }
                return;
            }
            return;
        }
        qq$0$debug.qq$(context);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isRootSystem1() {
        if (adrt$enabled) {
            return qq$0$debug.isRootSystem1$();
        }
        for (String str : new String[]{"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/"}) {
            try {
                File file = new File(new StringBuffer().append(str).append("su").toString());
                if (file != null && file.exists() && file.canExecute()) {
                    return true;
                }
            } catch (Exception e2) {
            }
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:53:0x0090 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:55:0x0063 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:57:0x005e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:63:0x0095 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:72:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.lang.String execRootCmd(java.lang.String r7) throws java.lang.Throwable {
        /*
            r1 = 0
            boolean r0 = cc.xposed.qq.adrt$enabled
            if (r0 == 0) goto La
            java.lang.String r0 = cc.xposed.qq$0$debug.execRootCmd$(r7)
        L9:
            return r0
        La:
            java.lang.String r4 = ""
            r0 = r1
            java.io.DataOutputStream r0 = (java.io.DataOutputStream) r0
            java.io.DataInputStream r1 = (java.io.DataInputStream) r1
            java.lang.Runtime r2 = java.lang.Runtime.getRuntime()     // Catch: java.lang.Exception -> L7e java.lang.Throwable -> L8a
            java.lang.String r3 = "su"
            java.lang.Process r5 = r2.exec(r3)     // Catch: java.lang.Exception -> L7e java.lang.Throwable -> L8a
            java.io.DataOutputStream r3 = new java.io.DataOutputStream     // Catch: java.lang.Exception -> L7e java.lang.Throwable -> L8a
            java.io.OutputStream r2 = r5.getOutputStream()     // Catch: java.lang.Exception -> L7e java.lang.Throwable -> L8a
            r3.<init>(r2)     // Catch: java.lang.Exception -> L7e java.lang.Throwable -> L8a
            java.io.DataInputStream r2 = new java.io.DataInputStream     // Catch: java.lang.Throwable -> La8 java.lang.Exception -> Lb1
            java.io.InputStream r0 = r5.getInputStream()     // Catch: java.lang.Throwable -> La8 java.lang.Exception -> Lb1
            r2.<init>(r0)     // Catch: java.lang.Throwable -> La8 java.lang.Exception -> Lb1
            java.lang.StringBuffer r0 = new java.lang.StringBuffer     // Catch: java.lang.Throwable -> Lab java.lang.Exception -> Lb5
            r0.<init>()     // Catch: java.lang.Throwable -> Lab java.lang.Exception -> Lb5
            java.lang.StringBuffer r0 = r0.append(r7)     // Catch: java.lang.Throwable -> Lab java.lang.Exception -> Lb5
            java.lang.String r1 = "\n"
            java.lang.StringBuffer r0 = r0.append(r1)     // Catch: java.lang.Throwable -> Lab java.lang.Exception -> Lb5
            java.lang.String r0 = r0.toString()     // Catch: java.lang.Throwable -> Lab java.lang.Exception -> Lb5
            r3.writeBytes(r0)     // Catch: java.lang.Throwable -> Lab java.lang.Exception -> Lb5
            r3.flush()     // Catch: java.lang.Throwable -> Lab java.lang.Exception -> Lb5
            java.lang.String r0 = "exit\n"
            r3.writeBytes(r0)     // Catch: java.lang.Throwable -> Lab java.lang.Exception -> Lb5
            r3.flush()     // Catch: java.lang.Throwable -> Lab java.lang.Exception -> Lb5
            r0 = 0
            java.lang.String r0 = (java.lang.String) r0     // Catch: java.lang.Throwable -> Lab java.lang.Exception -> Lb5
            r1 = r4
        L52:
            java.lang.String r0 = r2.readLine()     // Catch: java.lang.Throwable -> Lab java.lang.Exception -> Lba
            if (r0 != 0) goto L6c
            r5.waitFor()     // Catch: java.lang.Throwable -> Lab java.lang.Exception -> Lba
            r0 = r1
        L5c:
            if (r3 == 0) goto L61
            r3.close()     // Catch: java.io.IOException -> La3
        L61:
            if (r2 == 0) goto L9
            r2.close()     // Catch: java.io.IOException -> L67
            goto L9
        L67:
            r1 = move-exception
            r1.printStackTrace()
            goto L9
        L6c:
            java.lang.StringBuffer r4 = new java.lang.StringBuffer     // Catch: java.lang.Throwable -> Lab java.lang.Exception -> Lba
            r4.<init>()     // Catch: java.lang.Throwable -> Lab java.lang.Exception -> Lba
            java.lang.StringBuffer r4 = r4.append(r1)     // Catch: java.lang.Throwable -> Lab java.lang.Exception -> Lba
            java.lang.StringBuffer r0 = r4.append(r0)     // Catch: java.lang.Throwable -> Lab java.lang.Exception -> Lba
            java.lang.String r1 = r0.toString()     // Catch: java.lang.Throwable -> Lab java.lang.Exception -> Lba
            goto L52
        L7e:
            r2 = move-exception
            r3 = r4
            r6 = r0
            r0 = r2
            r2 = r6
        L83:
            r0.printStackTrace()     // Catch: java.lang.Throwable -> Lad
            r0 = r3
            r3 = r2
            r2 = r1
            goto L5c
        L8a:
            r2 = move-exception
            r3 = r0
            r0 = r2
            r2 = r1
        L8e:
            if (r3 == 0) goto L93
            r3.close()     // Catch: java.io.IOException -> L99
        L93:
            if (r2 == 0) goto L98
            r2.close()     // Catch: java.io.IOException -> L9e
        L98:
            throw r0
        L99:
            r1 = move-exception
            r1.printStackTrace()
            goto L93
        L9e:
            r1 = move-exception
            r1.printStackTrace()
            goto L98
        La3:
            r1 = move-exception
            r1.printStackTrace()
            goto L61
        La8:
            r0 = move-exception
            r2 = r1
            goto L8e
        Lab:
            r0 = move-exception
            goto L8e
        Lad:
            r0 = move-exception
            r3 = r2
            r2 = r1
            goto L8e
        Lb1:
            r0 = move-exception
            r2 = r3
            r3 = r4
            goto L83
        Lb5:
            r0 = move-exception
            r1 = r2
            r2 = r3
            r3 = r4
            goto L83
        Lba:
            r0 = move-exception
            r6 = r2
            r2 = r3
            r3 = r1
            r1 = r6
            goto L83
        */
        throw new UnsupportedOperationException("Method not decompiled: cc.xposed.qq.execRootCmd(java.lang.String):java.lang.String");
    }

    public static void method2(String str, String str2) {
        if (!adrt$enabled) {
            try {
                FileWriter fileWriter = new FileWriter(str, true);
                fileWriter.write(str2);
                fileWriter.close();
                return;
            } catch (IOException e2) {
                e2.printStackTrace();
                return;
            }
        }
        qq$0$debug.method2$(str, str2);
    }

    public boolean rootCommand(String str) throws Throwable {
        Process processExec;
        Throwable th;
        if (adrt$enabled) {
            return qq$0$debug.rootCommand(this, str);
        }
        Process process = (Process) null;
        DataOutputStream dataOutputStream = (DataOutputStream) null;
        try {
            try {
                processExec = Runtime.getRuntime().exec("su");
                try {
                    DataOutputStream dataOutputStream2 = new DataOutputStream(processExec.getOutputStream());
                    try {
                        dataOutputStream2.writeBytes(new StringBuffer().append(str).append("\n").toString());
                        dataOutputStream2.writeBytes("exit\n");
                        dataOutputStream2.flush();
                        processExec.waitFor();
                        if (dataOutputStream2 != null) {
                            try {
                                dataOutputStream2.close();
                            } catch (Exception e2) {
                            }
                        }
                        processExec.destroy();
                        return true;
                    } catch (Exception e3) {
                        dataOutputStream = dataOutputStream2;
                        if (dataOutputStream != null) {
                            try {
                                dataOutputStream.close();
                            } catch (Exception e4) {
                                return false;
                            }
                        }
                        processExec.destroy();
                        return false;
                    } catch (Throwable th2) {
                        dataOutputStream = dataOutputStream2;
                        th = th2;
                        if (dataOutputStream != null) {
                            try {
                                dataOutputStream.close();
                            } catch (Exception e5) {
                                throw th;
                            }
                        }
                        processExec.destroy();
                        throw th;
                    }
                } catch (Exception e6) {
                }
            } catch (Exception e7) {
                processExec = process;
            } catch (Throwable th3) {
                processExec = process;
                th = th3;
            }
        } catch (Throwable th4) {
            th = th4;
        }
    }

    public qq() {
        if (adrt$enabled) {
            ADRTThread aDRTThreadOnMethodEnter = ADRT.onMethodEnter(135L);
            try {
                aDRTThreadOnMethodEnter.onStatementStart(126);
                aDRTThreadOnMethodEnter.onThisAvailable(this);
                aDRTThreadOnMethodEnter.onStatementStart(127);
            } finally {
                aDRTThreadOnMethodEnter.onMethodExit();
            }
        }
    }
}
