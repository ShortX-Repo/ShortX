package f;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.view.KeyEvent;
import cc.xposed.R;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class p0 implements l {
    public static final Charset z = StandardCharsets.UTF_8;
    private static final char[] A = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final byte[] B = new byte[0];
    private static int C = -1;

    public static int a(int[] iArr, int i) {
        for (int i2 = 0; i2 < iArr.length; i2++) {
            if (iArr[i2] == i) {
                return i2;
            }
        }
        return -1;
    }

    public static int b(Object[] objArr, Object obj, int i) {
        int i2 = 0;
        if (obj != null) {
            while (i2 < i) {
                if (obj.equals(objArr[i2])) {
                    return i2;
                }
                i2++;
            }
            return -1;
        }
        while (i2 < i) {
            if (objArr[i2] == null) {
                return i2;
            }
            i2++;
        }
        return -1;
    }

    public static void c(Object[] objArr, int i, int i2) {
        if (i == i2) {
            return;
        }
        Object obj = objArr[i];
        if (i > i2) {
            System.arraycopy(objArr, i2, objArr, i2 + 1, i - i2);
        } else {
            System.arraycopy(objArr, i + 1, objArr, i, i2 - i);
        }
        objArr[i2] = obj;
    }

    public static int d(int[] iArr, int i) {
        int length = iArr.length - 1;
        int i2 = 0;
        while (i2 <= length) {
            int i3 = (i2 + length) >>> 1;
            int i4 = iArr[i3] - i;
            if (i4 < 0) {
                i2 = i3 + 1;
            } else {
                if (i4 <= 0) {
                    return i3;
                }
                length = i3 - 1;
            }
        }
        return i2 ^ (-1);
    }

    public static int e(int i, int i2, int i3) {
        return i < i2 ? i2 : i > i3 ? i3 : i;
    }

    public static String f() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    }

    public static byte[] g(String str) {
        int i;
        int i2;
        if (str == null || str.length() == 0) {
            return B;
        }
        int length = str.length() / 2;
        byte[] bArr = new byte[length];
        for (int i3 = 0; i3 < length; i3++) {
            int i4 = i3 * 2;
            char cCharAt = str.charAt(i4);
            if (cCharAt >= '0' && cCharAt <= '9') {
                i = cCharAt - '0';
            } else {
                if (cCharAt < 'A' || cCharAt > 'F') {
                    throw new InvalidParameterException("Invalid data: " + str);
                }
                i = (cCharAt - 'A') + 10;
            }
            char cCharAt2 = str.charAt(i4 + 1);
            if (cCharAt2 >= '0' && cCharAt2 <= '9') {
                i2 = cCharAt2 - '0';
            } else {
                if (cCharAt2 < 'A' || cCharAt2 > 'F') {
                    throw new InvalidParameterException("Invalid data: " + str);
                }
                i2 = (cCharAt2 - 'A') + 10;
            }
            bArr[i3] = (byte) ((i2 << 4) | i);
        }
        return bArr;
    }

    public static String h(String str) {
        if (str == null) {
            return null;
        }
        return Integer.toString(Integer.parseInt(str) - 1);
    }

    public static String i(byte[] bArr) {
        if (bArr == null || bArr.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(bArr.length);
        for (byte b2 : bArr) {
            char[] cArr = A;
            sb.append(cArr[b2 & 15]);
            sb.append(cArr[(b2 >> 4) & 15]);
        }
        return sb.toString();
    }

    public static String j(Context context, int i) {
        int i2;
        if (i == 0) {
            i2 = R.string.key_click;
        } else if (i == 1) {
            i2 = R.string.key_double_click;
        } else {
            if (i != 2) {
                return null;
            }
            i2 = R.string.key_long_press;
        }
        return context.getString(i2);
    }

    public static String k(Context context, int i) {
        int i2;
        if (i == 3) {
            i2 = R.string.key_home;
        } else if (i == 4) {
            i2 = R.string.key_back;
        } else if (i == 79) {
            i2 = R.string.key_headset_hook;
        } else if (i == 82) {
            i2 = R.string.key_menu;
        } else if (i != 187) {
            switch (i) {
                case 24:
                    i2 = R.string.key_volume_up;
                    break;
                case 25:
                    i2 = R.string.key_volume_down;
                    break;
                case 26:
                    i2 = R.string.key_power;
                    break;
                default:
                    return KeyEvent.keyCodeToString(i);
            }
        } else {
            i2 = R.string.key_recent_apps;
        }
        return context.getString(i2);
    }

    public static int l(String str) {
        Resources system = Resources.getSystem();
        int identifier = system.getIdentifier(str, "dimen", "android");
        if (identifier > 0) {
            return system.getDimensionPixelSize(identifier);
        }
        return 0;
    }

    public static int m(String str, int i) {
        Resources system = Resources.getSystem();
        int identifier = system.getIdentifier(str, "integer", "android");
        return identifier > 0 ? system.getInteger(identifier) : i;
    }

    public static String n(String str) {
        Resources system = Resources.getSystem();
        int identifier = system.getIdentifier(str, "string", "android");
        if (identifier > 0) {
            return system.getString(identifier);
        }
        return null;
    }

    public static String o(String str) {
        if (str == null) {
            return null;
        }
        return Integer.toString(Integer.parseInt(str) + 1);
    }

    public static int p() {
        int i;
        if (C == -1) {
            boolean z2 = false;
            for (StackTraceElement stackTraceElement : new Throwable().getStackTrace()) {
                String className = stackTraceElement.getClassName();
                if (className.contains("LspHooker") || className.contains("lsposed")) {
                    i = 3;
                    C = i;
                    return i;
                }
                if (className.contains("elderdrivers")) {
                    i = 2;
                    C = i;
                    return i;
                }
                if (!z2 && className.startsWith("de.robv.android.xposed")) {
                    z2 = true;
                }
            }
            if (z2) {
                C = 1;
            } else {
                C = 0;
            }
        }
        return C;
    }

    public static boolean q(Context context, String str, String str2) {
        if (context != null && str != null && str2 != null) {
            try {
                for (String str3 : context.getPackageManager().getPackageInfo(str, 4096).requestedPermissions) {
                    if (str2.equals(str3)) {
                        return true;
                    }
                }
            } catch (Throwable th) {
                v.d(th);
            }
        }
        return false;
    }

    public static int r() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public static void s(Context context, Intent intent) {
        intent.addFlags(805306368);
        try {
            context.startActivity(intent);
        } catch (Throwable th) {
            v.d(th);
        }
    }

    public static void t(Context context, String str) {
        Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(str);
        if (launchIntentForPackage != null) {
            s(context, launchIntentForPackage);
            return;
        }
        v.c("Main activity not found: " + str);
    }

    public static void u(Context context, String str) {
        s(context, new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.fromParts("package", str, null)));
    }

    public static void v(Activity activity, String str) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + str));
        intent.addFlags(1208483840);
        if (l.w.equals(str)) {
            intent.setPackage("com.android.vending");
        }
        try {
            try {
                activity.startActivity(intent);
            } catch (Throwable unused) {
                Intent intent2 = new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/store/apps/details?id=" + str));
                intent2.addFlags(1208483840);
                activity.startActivity(intent2);
            }
        } catch (Throwable unused2) {
        }
    }
}
