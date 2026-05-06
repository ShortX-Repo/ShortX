package f;

import android.os.Build;
import android.os.Environment;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
class k {
    static String a() {
        return "2210241503";
    }

    static String b() {
        String strD = d();
        return strD.contains("pro") ? strD.substring(0, strD.length() - 3) : strD;
    }

    static String c() {
        String strD = d();
        if (Build.VERSION.SDK_INT >= 24) {
            String str = Environment.getDataDirectory() + "/user_de/0/" + strD + "/";
            try {
                if (new File(str).exists()) {
                    return str;
                }
            } catch (Throwable unused) {
            }
        }
        return Environment.getDataDirectory() + "/data/" + strD + "/";
    }

    static String d() {
        return "com.jozein.xedgepro";
    }

    static String e() {
        String strD = d();
        if (strD.contains("pro")) {
            return strD;
        }
        return strD + "pro";
    }

    static String f() {
        return "v8.0.1";
    }
}
