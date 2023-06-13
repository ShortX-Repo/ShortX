package tornaco.apps.shortx.ui.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class ClipboardUtils {

    public static void copyToClipboard(Context context, String name, String string) {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (cmb != null) {
            cmb.setPrimaryClip(ClipData.newPlainText(name, string));
        }
    }

    public static String readClipboard(Context context) {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (cmb != null && cmb.hasPrimaryClip()) {

            ClipData primaryClip = cmb.getPrimaryClip();
            if (primaryClip == null) return null;
            ClipData.Item item = primaryClip.getItemAt(0);
            if (item == null) return null;
            return String.valueOf(item.getText());
        }
        return null;
    }
}
