package com.jozein.xedgepro.xposed;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import java.io.FileDescriptor;

/* JADX INFO: loaded from: classes.dex */
class w3 implements IBinder {
    w3() {
    }

    @Override // android.os.IBinder
    public void dump(FileDescriptor fileDescriptor, String[] strArr) {
    }

    @Override // android.os.IBinder
    public void dumpAsync(FileDescriptor fileDescriptor, String[] strArr) {
    }

    @Override // android.os.IBinder
    public String getInterfaceDescriptor() {
        return "";
    }

    @Override // android.os.IBinder
    public boolean isBinderAlive() {
        return true;
    }

    @Override // android.os.IBinder
    public void linkToDeath(IBinder.DeathRecipient deathRecipient, int i) {
    }

    @Override // android.os.IBinder
    public boolean pingBinder() {
        return true;
    }

    @Override // android.os.IBinder
    public IInterface queryLocalInterface(String str) {
        return null;
    }

    @Override // android.os.IBinder
    public boolean transact(int i, Parcel parcel, Parcel parcel2, int i2) {
        return true;
    }

    @Override // android.os.IBinder
    public boolean unlinkToDeath(IBinder.DeathRecipient deathRecipient, int i) {
        return true;
    }
}
