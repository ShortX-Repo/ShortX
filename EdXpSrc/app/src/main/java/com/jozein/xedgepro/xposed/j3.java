package com.jozein.xedgepro.xposed;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import com.jozein.xedgepro.xposed.h3;
import de.robv.android.xposed.XposedHelpers;

/* JADX INFO: loaded from: classes.dex */
class j3 {
    static boolean a(Context context) {
        return h3.c.a(context.getContentResolver(), "screen_brightness_mode", 0) != 0;
    }

    static boolean b(Context context) {
        return h3.c.a(context.getContentResolver(), "accelerometer_rotation", 0) != 0;
    }

    static boolean c() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null) {
            return false;
        }
        int state = defaultAdapter.getState();
        return state == 12 || state == 11;
    }

    static boolean d(Context context) {
        return h3.b.a(context.getContentResolver(), "location_mode", 0) != 0;
    }

    static boolean e(Context context) {
        try {
            if (Build.VERSION.SDK_INT < 21) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
                if (connectivityManager != null) {
                    return ((Boolean) XposedHelpers.callMethod(connectivityManager, "getMobileDataEnabled", new Object[0])).booleanValue();
                }
            } else {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
                if (telephonyManager != null) {
                    return ((Boolean) XposedHelpers.callMethod(telephonyManager, "getDataEnabled", new Object[0])).booleanValue();
                }
            }
        } catch (Throwable th) {
            f.v.d(th);
        }
        return false;
    }

    static boolean f(Context context) {
        NfcAdapter defaultAdapter;
        NfcManager nfcManager = (NfcManager) context.getSystemService("nfc");
        if (nfcManager == null || (defaultAdapter = nfcManager.getDefaultAdapter()) == null) {
            return false;
        }
        int iIntValue = ((Integer) XposedHelpers.callMethod(defaultAdapter, "getAdapterState", new Object[0])).intValue();
        return iIntValue == 3 || iIntValue == 2;
    }

    static boolean g() {
        return ContentResolver.getMasterSyncAutomatically();
    }

    static boolean h(Context context) {
        int iIntValue = ((Integer) XposedHelpers.callMethod((WifiManager) context.getSystemService("wifi"), "getWifiApState", new Object[0])).intValue();
        return iIntValue == 12 || iIntValue == 13;
    }

    static boolean i(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
        if (wifiManager == null) {
            return false;
        }
        int wifiState = wifiManager.getWifiState();
        return wifiState == 3 || wifiState == 2;
    }
}
