package github.tornaco.android.thanos.core.app;

import static github.tornaco.android.thanos.core.app.ThanosManager.PROXIED_ANDROID_SERVICE_NAME;

import android.content.Context;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;

import github.tornaco.android.thanos.core.IThanos;
import tornaco.apps.shortx.core.util.Logger;

public class ThanosManagerNative {
    private static IThanos localService;
    private static Logger LOGGER = new Logger("ThanosManagerNative");

    public static void setLocalService(IThanos localService) {
        LOGGER.w("ThanosManagerNative, setLocalService: " + localService);
        ThanosManagerNative.localService = localService;
    }

    private static final Singleton<IThanos> sIThanosSingleton = new Singleton<IThanos>() {
        @Override
        protected IThanos create() {
            if (localService != null) {
                return localService;
            }
            IThanos thanos = IThanos.Stub.asInterface(
                    ServiceManager.getService(Context.TV_INPUT_SERVICE));
            if (thanos != null) {
                return thanos;
            }

            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                IBinder backup = ServiceManager.getService(PROXIED_ANDROID_SERVICE_NAME);
                if (backup == null) {
                    LOGGER.w("Get Thanos from IPC_TRANS_CODE_THANOS_SERVER, service is null.");
                    return null;
                }
                data.writeInterfaceToken(IThanos.class.getName());
                backup.transact(ThanosManager.IPC_TRANS_CODE_THANOS_SERVER, data, reply, 0);
                IBinder binder = reply.readStrongBinder();
                LOGGER.d("Get Thanos from IPC_TRANS_CODE_THANOS_SERVER " + binder);
                return IThanos.Stub.asInterface(binder);
            } catch (RemoteException e) {
                LOGGER.e(e, "Get Thanos from IPC_TRANS_CODE_THANOS_SERVER err");
            } finally {
                data.recycle();
                reply.recycle();
            }
            return null;
        }
    };

    public static IThanos getDefault() {
        return sIThanosSingleton.get();
    }

    public static IThanos getLocalService() {
        return localService;
    }

    public static boolean isServiceInstalled() {
        return getDefault() != null;
    }
}


abstract class Singleton<T> {
    private T mInstance;

    protected abstract T create();

    public final T get() {
        synchronized (this) {
            if (mInstance == null) {
                mInstance = create();
            }
            return mInstance;
        }
    }
}
