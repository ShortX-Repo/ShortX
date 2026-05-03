package com.jozein.xedgepro.xposed;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Xml;
import de.robv.android.xposed.XposedHelpers;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
class n2 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String[] f194a = {"allowed", "ignored", "denied", "default", "foreground"};

    static String[] a(Context context) {
        try {
            ArrayList<String> arrayListB = b();
            return (String[]) arrayListB.toArray(new String[arrayListB.size()]);
        } catch (Throwable th) {
            f.v.d(th);
            try {
                List<WifiConfiguration> configuredNetworks = ((WifiManager) context.getSystemService("wifi")).getConfiguredNetworks();
                int size = configuredNetworks.size();
                String[] strArr = new String[size];
                for (int i = 0; i < size; i++) {
                    strArr[i] = f(configuredNetworks.get(i).SSID);
                }
                return strArr;
            } catch (Throwable th2) {
                f.v.d(th2);
                return null;
            }
        }
    }

    private static ArrayList<String> b() throws Throwable {
        InputStreamReader inputStreamReader;
        FileInputStream fileInputStream;
        BufferedReader bufferedReader;
        String strDecode;
        String text;
        int i = Build.VERSION.SDK_INT;
        BufferedReader bufferedReader2 = null;
        FileInputStream fileInputStream2 = null;
        bufferedReader2 = null;
        bufferedReader2 = null;
        if (i >= 26) {
            StringBuilder sb = new StringBuilder();
            sb.append(Environment.getDataDirectory());
            sb.append(i > 29 ? "/misc/apexdata/com.android.wifi/WifiConfigStore.xml" : "/misc/wifi/WifiConfigStore.xml");
            File file = new File(sb.toString());
            XmlPullParser xmlPullParserNewPullParser = Xml.newPullParser();
            try {
                FileInputStream fileInputStream3 = new FileInputStream(file);
                try {
                    xmlPullParserNewPullParser.setInput(fileInputStream3, "UTF-8");
                    ArrayList<String> arrayList = new ArrayList<>();
                    String attributeValue = null;
                    for (int eventType = xmlPullParserNewPullParser.getEventType(); eventType != 1; eventType = xmlPullParserNewPullParser.next()) {
                        if (eventType == 0 || eventType == 2) {
                            if (xmlPullParserNewPullParser.getAttributeCount() > 0) {
                                attributeValue = xmlPullParserNewPullParser.getAttributeValue(0);
                            }
                        } else if (eventType == 3) {
                            attributeValue = null;
                        } else if (eventType == 4 && "SSID".equals(attributeValue) && (text = xmlPullParserNewPullParser.getText()) != null) {
                            arrayList.add(f(text.trim()));
                        }
                    }
                    fileInputStream3.close();
                    return arrayList;
                } catch (Throwable th) {
                    th = th;
                    fileInputStream2 = fileInputStream3;
                    if (fileInputStream2 != null) {
                        fileInputStream2.close();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } else {
            try {
                fileInputStream = new FileInputStream(new File(Environment.getDataDirectory() + "/misc/wifi/wpa_supplicant.conf"));
                try {
                    inputStreamReader = new InputStreamReader(fileInputStream);
                    try {
                        bufferedReader = new BufferedReader(inputStreamReader);
                    } catch (Throwable th3) {
                        th = th3;
                    }
                } catch (Throwable th4) {
                    th = th4;
                    inputStreamReader = null;
                }
            } catch (Throwable th5) {
                th = th5;
                inputStreamReader = null;
                fileInputStream = null;
            }
            try {
                ArrayList<String> arrayList2 = new ArrayList<>();
                while (true) {
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        bufferedReader.close();
                        inputStreamReader.close();
                        fileInputStream.close();
                        return arrayList2;
                    }
                    String strTrim = line.trim();
                    if (strTrim.startsWith("ssid=")) {
                        if (strTrim.charAt(5) == '\"') {
                            strDecode = strTrim.substring(6, strTrim.length() - 1);
                        } else {
                            StringBuilder sb2 = new StringBuilder();
                            String strSubstring = strTrim.substring(5);
                            int length = strSubstring.length();
                            for (int i2 = 0; i2 < length; i2++) {
                                if (i2 % 2 == 0) {
                                    sb2.append('%');
                                }
                                sb2.append(strSubstring.charAt(i2));
                            }
                            strDecode = Uri.decode(sb2.toString());
                        }
                        arrayList2.add(strDecode);
                    }
                }
            } catch (Throwable th6) {
                th = th6;
                bufferedReader2 = bufferedReader;
                if (bufferedReader2 != null) {
                    bufferedReader2.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                throw th;
            }
        }
    }

    static void c(Context context, String str, int i, int i2) {
        try {
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService("appops");
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(str, 0);
            if (((Integer) XposedHelpers.callMethod(appOpsManager, "checkOpNoThrow", new Object[]{Integer.valueOf(i), Integer.valueOf(applicationInfo.uid), str})).intValue() != i2) {
                XposedHelpers.callMethod(appOpsManager, "setMode", new Object[]{Integer.valueOf(i), Integer.valueOf(applicationInfo.uid), str, Integer.valueOf(i2)});
            }
        } catch (Throwable th) {
            f.v.d(th);
        }
    }

    static void d(Context context, String str, boolean z) {
        int i = z ? 0 : Build.VERSION.SDK_INT >= 21 ? 3 : 1;
        c(context, str, 24, i);
        int i2 = Build.VERSION.SDK_INT;
        if (i2 >= 28) {
            c(context, str, 70, i);
            c(context, str, 76, i);
            if (i2 >= 30) {
                if (f.p0.q(context, str, "android.permission.QUERY_ALL_PACKAGES")) {
                    c(context, str, 91, i);
                }
                if (f.p0.q(context, str, "android.permission.MANAGE_EXTERNAL_STORAGE")) {
                    c(context, f.l.j, 92, i);
                }
            }
        }
        e(context, str, z);
    }

    static void e(Context context, String str, boolean z) {
        int i = Build.VERSION.SDK_INT;
        if (i >= 23) {
            try {
                if (((PowerManager) context.getSystemService("power")).isIgnoringBatteryOptimizations(str) == z) {
                    return;
                }
                if (i >= 30) {
                    Object objT = g3.t("com.android.server.DeviceIdleInternal");
                    if (z) {
                        ArrayList arrayList = new ArrayList(1);
                        arrayList.add(str);
                        XposedHelpers.callMethod(objT, "addPowerSaveWhitelistAppsInternal", new Object[]{arrayList});
                    } else {
                        XposedHelpers.callMethod(objT, "removePowerSaveWhitelistAppInternal", new Object[]{str});
                    }
                } else {
                    Object objT2 = g3.t("com.android.server.DeviceIdleController$LocalService");
                    if (z) {
                        XposedHelpers.callMethod(objT2, "addPowerSaveWhitelistAppInternal", new Object[]{str});
                    } else {
                        XposedHelpers.callMethod(objT2, "removePowerSaveWhitelistAppInternal", new Object[]{str});
                    }
                }
            } catch (Throwable th) {
                f.v.d(th);
            }
        }
    }

    static String f(String str) {
        return (str == null || str.length() < 2 || str.charAt(0) != '\"') ? str : str.substring(1, str.length() - 1);
    }
}
