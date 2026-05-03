package f;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

/* JADX INFO: loaded from: classes.dex */
public class g {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final int f437a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final int f438b;

    static {
        int iM;
        int i = Build.VERSION.SDK_INT >= 24 ? 1 : 10;
        int i2 = 255;
        try {
            iM = p0.m("config_screenBrightnessSettingMinimum", -1);
            if (iM == -1) {
                try {
                    iM = p0.m("config_screenBrightnessDim", i);
                } catch (Throwable th) {
                    th = th;
                    v.d(th);
                    if (iM != -1) {
                        i = iM;
                    }
                    f437a = i;
                    f438b = i2;
                }
            }
            int iM2 = p0.m("config_screenBrightnessSettingMaximum", 255);
            if (iM2 < 255) {
                try {
                    v.c("Got max brightness " + iM2 + ", force set to 255.");
                } catch (Throwable th2) {
                    th = th2;
                    i2 = iM2;
                    v.d(th);
                    if (iM != -1) {
                    }
                    f437a = i;
                    f438b = i2;
                }
            } else {
                i2 = iM2;
            }
        } catch (Throwable th3) {
            th = th3;
            iM = i;
        }
        i = iM;
        f437a = i;
        f438b = i2;
    }

    public static int a(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "screen_brightness", (f437a + f438b) / 2);
    }
}
