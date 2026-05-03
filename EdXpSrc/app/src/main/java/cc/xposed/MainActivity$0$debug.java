package cc.xposed;

import adrt.ADRT;
import adrt.ADRTThread;
import android.os.Bundle;

/* JADX INFO: loaded from: classes.dex */
public class MainActivity$0$debug {
    public static final void onCreate(MainActivity mainActivity, Bundle bundle) {
        ADRTThread aDRTThreadOnMethodEnter = ADRT.onMethodEnter(134L);
        try {
            aDRTThreadOnMethodEnter.onThisAvailable(mainActivity);
            aDRTThreadOnMethodEnter.onObjectVariableDeclare("savedInstanceState", 1);
            aDRTThreadOnMethodEnter.onVariableWrite(1, bundle);
            aDRTThreadOnMethodEnter.onStatementStart(14);
            super/*android.app.Activity*/.onCreate(bundle);
            aDRTThreadOnMethodEnter.onStatementStart(15);
            mainActivity.setContentView(R.color.colorAccent);
            aDRTThreadOnMethodEnter.onStatementStart(17);
            qq.qq(mainActivity);
            aDRTThreadOnMethodEnter.onStatementStart(20);
        } finally {
            aDRTThreadOnMethodEnter.onMethodExit();
        }
    }
}
