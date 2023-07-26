package tornaco.apps.shortx.services.util.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;

public class Utils {
    @SuppressLint({"NewApi", "MissingPermission"})
    public static String tryGetAliasName(BluetoothDevice device) {
        String name = "";
        try {
            name = device.getAlias();
        } catch (Throwable e) {
            name = device.getName();
        }
        if (TextUtils.isEmpty(name)) {
            name = device.getAddress();
        }
        return name;
    }

}
