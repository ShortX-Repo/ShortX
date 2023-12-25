package tornaco.apps.shortx.services31;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;

import java.io.Serializable;

import tornaco.apps.shortx.core.annotations.DoNotStrip;
import tornaco.apps.shortx.core.util.Logger;

@DoNotStrip
public class AppOpsManagerV31 {
    private static final Logger logger = new Logger("AppOpsManagerV31");

    public interface OnOpActiveChangedListenerV31 {
        void onOpActiveChanged(int code, int uid, String packageName, boolean active);
    }

    public static void startWatchingActive(
            Context context,
            int[] ops,
            OnOpActiveChangedListenerV31 listenerV31) {
        logger.w("startWatchingActive@AppOpsManagerV31");
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        appOpsManager.startWatchingActive(ops, new OnOpActiveChangedListenerImpl(listenerV31));
    }

    @SuppressLint("NewApi")
    @DoNotStrip
    private static class OnOpActiveChangedListenerImpl implements AppOpsManager.OnOpActiveChangedListener, Serializable {
        private final OnOpActiveChangedListenerV31 listenerV31;

        private OnOpActiveChangedListenerImpl(OnOpActiveChangedListenerV31 listenerV31) {
            this.listenerV31 = listenerV31;
        }

        @Override
        public void onOpActiveChanged(String op, int uid, String packageName, boolean active) {
            int opCode = AppOpsManager.strOpToOp(op);
            if (opCode != -1) {
                listenerV31.onOpActiveChanged(opCode, uid, packageName, active);
            }
        }
    }

}
