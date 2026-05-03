package cc.xposed;

import adrt.ADRT;
import adrt.ADRTThread;

/* JADX INFO: loaded from: classes.dex */
public final class BuildConfig {
    public static final boolean DEBUG = true;
    private static boolean adrt$enabled;

    static {
        ADRT.onClassLoad(143L, "cc.xposed.BuildConfig");
    }

    public BuildConfig() {
        if (adrt$enabled) {
            ADRTThread aDRTThreadOnMethodEnter = ADRT.onMethodEnter(143L);
            try {
                aDRTThreadOnMethodEnter.onStatementStart(5);
                aDRTThreadOnMethodEnter.onThisAvailable(this);
                aDRTThreadOnMethodEnter.onStatementStart(6);
            } finally {
                aDRTThreadOnMethodEnter.onMethodExit();
            }
        }
    }
}
