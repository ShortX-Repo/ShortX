package tornaco.apps.shortx.services29;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;

import com.elvishew.xlog.XLog;

import java.io.Serializable;

import tornaco.apps.shortx.core.annotations.DoNotStrip;

@DoNotStrip
public class AppOpsManagerV29 {

    public interface OnOpActiveChangedListenerV29 {
        void onOpActiveChanged(int code, int uid, String packageName, boolean active);
    }

    public static void startWatchingActive(
            Context context,
            int[] ops,
            OnOpActiveChangedListenerV29 listenerV29) {
        XLog.w("startWatchingActive@AppOpsManagerV29");
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        appOpsManager.startWatchingActive(ops, new OnOpActiveChangedListenerImpl(listenerV29));
    }

    @SuppressLint("NewApi")
    @DoNotStrip
    private static class OnOpActiveChangedListenerImpl implements AppOpsManager.OnOpActiveChangedListener, Serializable {
        private final OnOpActiveChangedListenerV29 listenerV29;

        private OnOpActiveChangedListenerImpl(OnOpActiveChangedListenerV29 listenerV29) {
            this.listenerV29 = listenerV29;
        }

        @Override
        public void onOpActiveChanged(int code, int uid, String packageName, boolean active) {
            listenerV29.onOpActiveChanged(code, uid, packageName, active);
        }
    }

}
