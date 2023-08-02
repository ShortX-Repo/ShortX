package tornaco.apps.shortx.services.util.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;

import tornaco.apps.shortx.core.util.Logger;

public class Utils {
    private static final Logger LOGGER = new Logger("");

    @SuppressLint({"NewApi", "MissingPermission"})
    public static String tryGetAliasName(BluetoothDevice device) {
        if (device == null) return "NULL";
        String name = "";
        try {
            name = device.getAlias();
        } catch (Throwable e) {
            name = device.getName();
        }
        if (TextUtils.isEmpty(name)) {
            LOGGER.w("Can not get BluetoothDevice name, use address instead.");
            name = device.getAddress();
        }
        return name;
    }

}
