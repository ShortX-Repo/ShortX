package adrt;

import android.content.Context;
import android.content.pm.PackageManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/* JADX INFO: loaded from: classes.dex */
public class ADRTLogCatReader implements Runnable {
    private static Context context;

    public static void onContext(Context context2, String str) {
        if (context == null) {
            context = context2.getApplicationContext();
            if ((context2.getApplicationInfo().flags & 2) != 0) {
                try {
                    context2.getPackageManager().getPackageInfo(str, 128);
                    ADRTSender.onContext(context, str);
                    new Thread(new ADRTLogCatReader(), "LogCat").start();
                } catch (PackageManager.NameNotFoundException e2) {
                }
            }
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("logcat -v threadtime").getInputStream()), 20);
            while (true) {
                String line = bufferedReader.readLine();
                if (line != null) {
                    ADRTSender.sendLogcatLines(new String[]{line});
                } else {
                    return;
                }
            }
        } catch (IOException e2) {
        }
    }
}
