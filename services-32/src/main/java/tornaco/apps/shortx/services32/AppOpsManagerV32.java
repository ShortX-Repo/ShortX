package tornaco.apps.shortx.services32;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;

import java.io.Serializable;

import tornaco.apps.shortx.core.annotations.DoNotStrip;
import tornaco.apps.shortx.core.util.Logger;

@DoNotStrip
public class AppOpsManagerV32 {
    private static final Logger logger = new Logger("AppOpsManagerV32");

    public interface OnOpActiveChangedListenerV32 {
        void onOpActiveChanged(int code, int uid, String packageName, boolean active);
    }

    public static void startWatchingActive(
            Context context,
            int[] ops,
            OnOpActiveChangedListenerV32 listenerV32) {
        logger.w("startWatchingActive@AppOpsManagerV32");
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        appOpsManager.startWatchingActive(ops, new OnOpActiveChangedListenerImpl(listenerV32));
    }

    @SuppressLint("NewApi")
    @DoNotStrip
    private static class OnOpActiveChangedListenerImpl implements AppOpsManager.OnOpActiveChangedListener, Serializable {
        private final OnOpActiveChangedListenerV32 listenerV32;

        private OnOpActiveChangedListenerImpl(OnOpActiveChangedListenerV32 listenerV32) {
            this.listenerV32 = listenerV32;
        }

        @Override
        public void onOpActiveChanged(String op, int uid, String packageName, boolean active) {
            int opCode = AppOpsManager.strOpToOp(op);
            if (opCode != -1) {
                listenerV32.onOpActiveChanged(opCode, uid, packageName, active);
            }
        }
    }

}
