package tornaco.apps.shortx.services28;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;

import com.elvishew.xlog.XLog;

import java.io.Serializable;

import tornaco.apps.shortx.core.annotations.DoNotStrip;

@DoNotStrip
public class AppOpsManagerV28 {

    public interface OnOpActiveChangedListenerV28 {
        void onOpActiveChanged(int code, int uid, String packageName, boolean active);
    }

    public static void startWatchingActive(
            Context context,
            int[] ops,
            OnOpActiveChangedListenerV28 listenerV28) {
        XLog.w("startWatchingActive@AppOpsManagerV28");
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        appOpsManager.startWatchingActive(ops, new OnOpActiveChangedListenerImpl(listenerV28));
    }

    @SuppressLint("NewApi")
    @DoNotStrip
    private static class OnOpActiveChangedListenerImpl implements AppOpsManager.OnOpActiveChangedListener, Serializable {
        private final OnOpActiveChangedListenerV28 listenerV28;

        private OnOpActiveChangedListenerImpl(OnOpActiveChangedListenerV28 listenerV28) {
            this.listenerV28 = listenerV28;
        }

        @Override
        public void onOpActiveChanged(int code, int uid, String packageName, boolean active) {
            listenerV28.onOpActiveChanged(code, uid, packageName, active);
        }
    }

}
