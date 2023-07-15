package tornaco.apps.shortx.services30;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;

import java.io.Serializable;

import tornaco.apps.shortx.core.annotations.DoNotStrip;
import tornaco.apps.shortx.core.util.Logger;

@DoNotStrip
public class AppOpsManagerV30 {
    private static final Logger logger = new Logger("AppOpsManagerV30");

    public interface OnOpActiveChangedListenerV30 {
        void onOpActiveChanged(int code, int uid, String packageName, boolean active);
    }

    public static void startWatchingActive(
            Context context,
            int[] ops,
            OnOpActiveChangedListenerV30 listenerV30) {
        logger.w("startWatchingActive@AppOpsManagerV30");
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        appOpsManager.startWatchingActive(ops, new OnOpActiveChangedListenerImpl(listenerV30));
    }

    @SuppressLint("NewApi")
    @DoNotStrip
    private static class OnOpActiveChangedListenerImpl implements AppOpsManager.OnOpActiveChangedListener, Serializable {
        private final OnOpActiveChangedListenerV30 listenerV30;

        private OnOpActiveChangedListenerImpl(OnOpActiveChangedListenerV30 listenerV30) {
            this.listenerV30 = listenerV30;
        }

        @Override
        public void onOpActiveChanged(String op, int uid, String packageName, boolean active) {
            int opCode = AppOpsManager.strOpToOp(op);
            if (opCode != -1) {
                listenerV30.onOpActiveChanged(opCode, uid, packageName, active);
            }
        }
    }

}
