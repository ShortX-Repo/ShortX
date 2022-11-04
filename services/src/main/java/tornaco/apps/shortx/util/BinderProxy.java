package tornaco.apps.shortx.util;

import android.os.Binder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.util.Log;

import java.io.FileDescriptor;

import util.Reflections;

public class BinderProxy extends Binder {
    private final Binder orig;
    private final InvocationHandler invocationHandler;

    public BinderProxy(Binder orig, InvocationHandler invocationHandler) {
        this.orig = orig;
        this.invocationHandler = invocationHandler;
    }

    @Override
    public String getInterfaceDescriptor() {
        return orig.getInterfaceDescriptor();
    }

    @Override
    public boolean pingBinder() {
        return orig.pingBinder();
    }

    @Override
    public boolean isBinderAlive() {
        return orig.isBinderAlive();
    }

    @Override
    public IInterface queryLocalInterface(String descriptor) {
        IInterface origRes = orig.queryLocalInterface(descriptor);
        IInterface res = invocationHandler.onQueryLocalInterface(descriptor, origRes);
        if (res != null) {
            return res;
        }
        return origRes;
    }

    @Override
    public void dump(FileDescriptor fd, String[] args) {
        orig.dump(fd, args);
    }

    @Override
    public void dumpAsync(FileDescriptor fd, String[] args) {
        orig.dumpAsync(fd, args);
    }

    @Override
    public void shellCommand(FileDescriptor in, FileDescriptor out, FileDescriptor err,
                             String[] args, ShellCallback shellCallback, ResultReceiver resultReceiver) throws RemoteException {
        orig.shellCommand(in, out, err, args, shellCallback, resultReceiver);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) {
        Log.w("BinderProxy", "onTransact: " + code);
        Boolean res = invocationHandler.onTransact(code, data, reply, flags);
        if (res != null) {
            return res;
        }
        return (boolean) Reflections.callMethod(orig, "onTransact", code, data, reply, flags);
    }

    @Override
    public void linkToDeath(DeathRecipient recipient, int flags) {
        orig.linkToDeath(recipient, flags);
    }

    @Override
    public boolean unlinkToDeath(DeathRecipient recipient, int flags) {
        return orig.unlinkToDeath(recipient, flags);
    }

    public interface InvocationHandler {
        default IInterface onQueryLocalInterface(String descriptor, IInterface iInterface) {
            return null;
        }

        default Boolean onTransact(int code, Parcel data, Parcel reply, int flags) {
            return null;
        }
    }
}