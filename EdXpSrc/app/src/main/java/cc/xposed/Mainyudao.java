package cc.xposed;

import adrt.ADRT;
import adrt.ADRTThread;
import android.app.Application;
import android.content.Context;
import android.os.Looper;
import android.os.Process;
import android.widget.Toast;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread;
import java.text.SimpleDateFormat;
import java.util.Date;

/* JADX INFO: loaded from: classes.dex */
public class Mainyudao extends Application implements Thread.UncaughtExceptionHandler, Runnable {
    private static boolean adrt$enabled;

    /* JADX INFO: renamed from: 上下文, reason: contains not printable characters */
    public static Context f0;

    /* JADX INFO: renamed from: 异常处理, reason: contains not printable characters */
    private Thread.UncaughtExceptionHandler f1;

    static {
        ADRT.onClassLoad(133L, "cc.xposed.Mainyudao");
    }

    @Override // android.app.Application
    public void onCreate() {
        ADRT.onContext(this, "com.aide.ui");
        if (!adrt$enabled) {
            if (f0 == null) {
                f0 = getApplicationContext();
                super.onCreate();
                this.f1 = Thread.getDefaultUncaughtExceptionHandler();
                Thread.setDefaultUncaughtExceptionHandler(this);
                return;
            }
            return;
        }
        Mainyudao$0$debug.onCreate(this);
    }

    @Override // java.lang.Runnable
    public void run() {
        ADRT.onContext(this, "com.aide.ui");
        if (!adrt$enabled) {
            Looper.prepare();
            Toast.makeText(f0, "很抱歉,程序出现异常,即将退出\n异常日志:/sdcard/崩溃日志.java", 1).show();
            Looper.loop();
            return;
        }
        Mainyudao$0$debug.run(this);
    }

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public void uncaughtException(Thread thread, Throwable th) {
        ADRT.onContext(this, "com.aide.ui");
        if (!adrt$enabled) {
            if (th == null && this.f1 != null) {
                this.f1.uncaughtException(thread, th);
            } else {
                new Thread(this).start();
                try {
                    FileWriter fileWriter = new FileWriter("/sdcard/崩溃日志.java");
                    fileWriter.write(m0(th));
                    fileWriter.flush();
                    fileWriter.close();
                    Thread.sleep(2000L);
                } catch (Exception e2) {
                }
                this.f1.uncaughtException(thread, th);
            }
            System.exit(1);
            Process.killProcess(Process.myPid());
            return;
        }
        Mainyudao$0$debug.uncaughtException(this, thread, th);
    }

    /* JADX INFO: renamed from: 获取异常, reason: contains not printable characters */
    public static String m0(Throwable th) {
        if (adrt$enabled) {
            return Mainyudao$0$debug.m1$(th);
        }
        StringWriter stringWriter = new StringWriter();
        th.printStackTrace(new PrintWriter(stringWriter));
        return new StringBuffer().append(new SimpleDateFormat("yyyy-MM-dd HH:mm ").format(new Date())).append(stringWriter.toString()).toString();
    }

    public Mainyudao() {
        if (adrt$enabled) {
            ADRTThread aDRTThreadOnMethodEnter = ADRT.onMethodEnter(133L);
            try {
                aDRTThreadOnMethodEnter.onStatementStart(48);
                aDRTThreadOnMethodEnter.onThisAvailable(this);
                aDRTThreadOnMethodEnter.onStatementStart(52);
            } finally {
                aDRTThreadOnMethodEnter.onMethodExit();
            }
        }
    }
}
