package cc.xposed;

import adrt.ADRT;
import adrt.ADRTThread;
import android.app.Activity;
import android.os.Bundle;

/* JADX INFO: loaded from: classes.dex */
public class MainActivity extends Activity {
    private static boolean adrt$enabled;

    static {
        ADRT.onClassLoad(134L, "cc.xposed.MainActivity");
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        ADRT.onContext(this, "com.aide.ui");
        if (!adrt$enabled) {
            super.onCreate(bundle);
            setContentView(R.color.colorAccent);
            qq.qq(this);
            return;
        }
        MainActivity$0$debug.onCreate(this, bundle);
    }

    public MainActivity() {
        if (adrt$enabled) {
            ADRTThread aDRTThreadOnMethodEnter = ADRT.onMethodEnter(134L);
            try {
                aDRTThreadOnMethodEnter.onStatementStart(20);
                aDRTThreadOnMethodEnter.onThisAvailable(this);
                aDRTThreadOnMethodEnter.onStatementStart(22);
            } finally {
                aDRTThreadOnMethodEnter.onMethodExit();
            }
        }
    }
}
