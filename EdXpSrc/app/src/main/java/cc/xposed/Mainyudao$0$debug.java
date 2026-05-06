package cc.xposed;

import adrt.ADRT;
import adrt.ADRTThread;
import android.os.Looper;
import android.os.Process;
import android.widget.Toast;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/* JADX INFO: loaded from: classes.dex */
public class Mainyudao$0$debug {
    public static final void onCreate(Mainyudao mainyudao) {
        ADRTThread aDRTThreadOnMethodEnter = ADRT.onMethodEnter(133L);
        try {
            aDRTThreadOnMethodEnter.onThisAvailable(mainyudao);
            aDRTThreadOnMethodEnter.onStatementStart(13);
            if (Mainyudao.f0 == null) {
                aDRTThreadOnMethodEnter.onStatementStart(13);
                Mainyudao.f0 = mainyudao.getApplicationContext();
                aDRTThreadOnMethodEnter.onStatementStart(14);
                super/*android.app.Application*/.onCreate();
                aDRTThreadOnMethodEnter.onStatementStart(15);
                mainyudao.f1 = Thread.getDefaultUncaughtExceptionHandler();
                aDRTThreadOnMethodEnter.onStatementStart(16);
                Thread.setDefaultUncaughtExceptionHandler(mainyudao);
            }
            aDRTThreadOnMethodEnter.onStatementStart(19);
        } finally {
            aDRTThreadOnMethodEnter.onMethodExit();
        }
    }

    public static final void run(Mainyudao mainyudao) {
        ADRTThread aDRTThreadOnMethodEnter = ADRT.onMethodEnter(133L);
        try {
            aDRTThreadOnMethodEnter.onThisAvailable(mainyudao);
            aDRTThreadOnMethodEnter.onStatementStart(22);
            Looper.prepare();
            aDRTThreadOnMethodEnter.onStatementStart(23);
            Toast.makeText(Mainyudao.f0, "很抱歉,程序出现异常,即将退出\n异常日志:/sdcard/崩溃日志.java", 1).show();
            aDRTThreadOnMethodEnter.onStatementStart(24);
            Looper.loop();
            aDRTThreadOnMethodEnter.onStatementStart(25);
        } finally {
            aDRTThreadOnMethodEnter.onMethodExit();
        }
    }

    public static final void uncaughtException(Mainyudao mainyudao, Thread thread, Throwable th) {
        ADRTThread aDRTThreadOnMethodEnter = ADRT.onMethodEnter(133L);
        try {
            aDRTThreadOnMethodEnter.onThisAvailable(mainyudao);
            aDRTThreadOnMethodEnter.onObjectVariableDeclare("线程", 1);
            aDRTThreadOnMethodEnter.onVariableWrite(1, thread);
            aDRTThreadOnMethodEnter.onObjectVariableDeclare("异常", 2);
            aDRTThreadOnMethodEnter.onVariableWrite(2, th);
            aDRTThreadOnMethodEnter.onStatementStart(29);
            if (th == null && mainyudao.f1 != null) {
                aDRTThreadOnMethodEnter.onStatementStart(30);
                mainyudao.f1.uncaughtException(thread, th);
            } else {
                aDRTThreadOnMethodEnter.onStatementStart(31);
                new Thread(mainyudao).start();
                aDRTThreadOnMethodEnter.onStatementStart(32);
                try {
                    aDRTThreadOnMethodEnter.onStatementStart(33);
                    aDRTThreadOnMethodEnter.onObjectVariableDeclare("fw", 4);
                    FileWriter fileWriter = new FileWriter("/sdcard/崩溃日志.java");
                    aDRTThreadOnMethodEnter.onVariableWrite(4, fileWriter);
                    aDRTThreadOnMethodEnter.onStatementStart(34);
                    fileWriter.write(Mainyudao.m0(th));
                    aDRTThreadOnMethodEnter.onStatementStart(35);
                    fileWriter.flush();
                    aDRTThreadOnMethodEnter.onStatementStart(36);
                    fileWriter.close();
                    aDRTThreadOnMethodEnter.onStatementStart(37);
                    Thread.sleep(2000L);
                } catch (Exception e2) {
                    aDRTThreadOnMethodEnter.onObjectVariableDeclare("e", 4);
                    aDRTThreadOnMethodEnter.onVariableWrite(4, e2);
                    aDRTThreadOnMethodEnter.onStatementStart(38);
                }
                aDRTThreadOnMethodEnter.onStatementStart(39);
                mainyudao.f1.uncaughtException(thread, th);
            }
            aDRTThreadOnMethodEnter.onStatementStart(41);
            System.exit(1);
            aDRTThreadOnMethodEnter.onStatementStart(41);
            Process.killProcess(Process.myPid());
            aDRTThreadOnMethodEnter.onStatementStart(42);
        } finally {
            aDRTThreadOnMethodEnter.onMethodExit();
        }
    }

    /* JADX INFO: renamed from: 获取异常$, reason: contains not printable characters */
    public static final String m1$(Throwable th) {
        ADRTThread aDRTThreadOnMethodEnter = ADRT.onMethodEnter(133L);
        try {
            aDRTThreadOnMethodEnter.onObjectVariableDeclare("异常", 0);
            aDRTThreadOnMethodEnter.onVariableWrite(0, th);
            aDRTThreadOnMethodEnter.onStatementStart(44);
            aDRTThreadOnMethodEnter.onObjectVariableDeclare("writer", 2);
            StringWriter stringWriter = new StringWriter();
            aDRTThreadOnMethodEnter.onVariableWrite(2, stringWriter);
            aDRTThreadOnMethodEnter.onStatementStart(45);
            aDRTThreadOnMethodEnter.onObjectVariableDeclare("printWriter", 3);
            PrintWriter printWriter = new PrintWriter(stringWriter);
            aDRTThreadOnMethodEnter.onVariableWrite(3, printWriter);
            aDRTThreadOnMethodEnter.onStatementStart(46);
            th.printStackTrace(printWriter);
            aDRTThreadOnMethodEnter.onStatementStart(47);
            return new StringBuffer().append(new SimpleDateFormat("yyyy-MM-dd HH:mm ").format(new Date())).append(stringWriter.toString()).toString();
        } finally {
            aDRTThreadOnMethodEnter.onMethodExit();
        }
    }
}
