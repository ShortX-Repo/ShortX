package github.tornaco.android.thanos.core.app;

import android.content.Context;
import android.os.RemoteException;

import com.elvishew.xlog.XLog;

import github.tornaco.android.thanos.core.IThanos;

public class ThanosManager {
    public static final String PROXIED_ANDROID_SERVICE_NAME = Context.DROPBOX_SERVICE;
    public static final int IPC_TRANS_CODE_THANOS_SERVER = "github.tornaco.android.thanos.core.IPC_TRANS_CODE_THANOS_SERVER".hashCode();

    private IThanos service;
    private Context context;

    public ThanosManager(Context context, IThanos service) {
        this.context = context;
        this.service = service;
    }

    public boolean isServiceInstalled() {
        boolean firstCheck = service != null && service.asBinder() != null && service.asBinder().isBinderAlive();
        if (!firstCheck) {
            return false;
        }
        try {
            String answer = service.whoAreYou();
            if (answer == null) {
                return false;
            }
            if (!answer.contains("Thanox")) {
                return false;
            }
        } catch (Throwable e) {
            XLog.e("Ask for thanox server error", e);
            return false;
        }
        return true;
    }


    public IActivityManager getActivityManager() throws RemoteException {
        return service.getActivityManager();
    }


    public static ThanosManager from(Context context) {
        return new ThanosManager(context, ThanosManagerNative.getDefault());
    }
}
