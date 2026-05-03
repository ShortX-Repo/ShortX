package f;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import cc.xposed.R;

/* JADX INFO: loaded from: classes.dex */
public class q0 implements l {
    private static final int[] A;
    private static int[] B;
    private static String[] z = {"volume_call", null, "volume_ringtone", "volume_music", "volume_alarm", "volume_notification", "volume_bluetooth_call", null, null, null};

    static {
        int[] iArr = {R.string.volume_stream_voice_call, R.string.volume_stream_system, R.string.volume_stream_ring, R.string.volume_stream_music, R.string.volume_stream_alarm, R.string.volume_stream_notification, R.string.volume_stream_bluetooth_sco, R.string.volume_stream_system_enforced, R.string.volume_stream_dtmf, R.string.volume_stream_tts};
        A = iArr;
        B = iArr;
    }

    public static int a(Context context, int i) {
        return ((AudioManager) context.getSystemService("audio")).getStreamMaxVolume(i);
    }

    public static int b(Context context, int i) {
        return ((AudioManager) context.getSystemService("audio")).getStreamVolume(i);
    }

    public static int[] c() {
        return B;
    }

    public static CharSequence d(Context context, int i) {
        int identifier;
        if (i >= 0) {
            int[] iArr = B;
            if (i < iArr.length) {
                int i2 = iArr[i];
                Resources resources = context.getResources();
                String[] strArr = z;
                if (strArr != null) {
                    z = null;
                    for (int i3 = 0; i3 < strArr.length; i3++) {
                        try {
                            if (strArr[i3] != null && (identifier = resources.getIdentifier(strArr[i3], "string", "android")) > 0) {
                                A[i3] = identifier;
                            }
                        } catch (Throwable th) {
                            v.d(th);
                        }
                    }
                }
                if (i2 != i) {
                    int[] iArr2 = A;
                    if (i2 < iArr2.length) {
                        return context.getString(iArr2[i]) + '(' + context.getString(iArr2[i2]) + ')';
                    }
                }
                return context.getText(A[i]);
            }
        }
        return context.getText(R.string.volume_stream_default);
    }

    public static void e(int[] iArr) {
        String str;
        if (iArr != null && iArr.length >= A.length) {
            B = iArr;
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Invalid alias: ");
        if (iArr == null) {
            str = "null";
        } else {
            str = "length " + iArr.length;
        }
        sb.append(str);
        v.c(sb.toString());
    }
}
