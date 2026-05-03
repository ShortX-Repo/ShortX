package adrt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;

/* JADX INFO: loaded from: classes.dex */
public class ADRTReceiver extends BroadcastReceiver {
    public static void onContext(Context context) {
        HandlerThread handlerThread = new HandlerThread("ADRTReceiver");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.adrt.RESUME");
        intentFilter.addAction("com.adrt.SET_BREAKPOINTS");
        intentFilter.addAction("com.adrt.STEP_OVER");
        intentFilter.addAction("com.adrt.STEP_IN");
        intentFilter.addAction("com.adrt.STEP_OUT");
        intentFilter.addAction("com.adrt.GET_FIELDS");
        intentFilter.addAction("com.adrt.DISCONNECT");
        intentFilter.addAction("com.adrt.KILL");
        context.getApplicationContext().registerReceiver(new ADRTReceiver(), intentFilter, null, handler);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if ("com.adrt.RESUME".equals(intent.getAction())) {
            ADRT.resume(intent.getStringArrayListExtra("breakpoints"));
            return;
        }
        if ("com.adrt.SET_BREAKPOINTS".equals(intent.getAction())) {
            ADRT.setBreakpoints(intent.getStringArrayListExtra("breakpoints"));
            return;
        }
        if ("com.adrt.STEP_OVER".equals(intent.getAction())) {
            ADRT.stepOver();
            return;
        }
        if ("com.adrt.STEP_IN".equals(intent.getAction())) {
            ADRT.stepIn();
            return;
        }
        if ("com.adrt.STEP_OUT".equals(intent.getAction())) {
            ADRT.stepOut();
            return;
        }
        if ("com.adrt.GET_FIELDS".equals(intent.getAction())) {
            ADRT.sendFields(intent.getStringExtra("path"));
        } else if ("com.adrt.DISCONNECT".equals(intent.getAction())) {
            ADRT.disconnect();
        } else if ("com.adrt.KILL".equals(intent.getAction())) {
            ADRT.stop();
        }
    }
}
