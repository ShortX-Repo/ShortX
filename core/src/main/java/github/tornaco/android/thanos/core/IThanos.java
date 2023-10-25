/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package github.tornaco.android.thanos.core;
// DO NOT CHANGE ORDER.
public interface IThanos extends android.os.IInterface
{
  /** Default implementation for IThanos. */
  public static class Default implements github.tornaco.android.thanos.core.IThanos
  {
    @Override public android.os.IBinder getServiceManager() throws android.os.RemoteException
    {
      return null;
    }
    @Override public android.os.IBinder getPrefManager() throws android.os.RemoteException
    {
      return null;
    }
    @Override public github.tornaco.android.thanos.core.app.IActivityManager getActivityManager() throws android.os.RemoteException
    {
      return null;
    }
    @Override public android.os.IBinder getPkgManager() throws android.os.RemoteException
    {
      return null;
    }
    @Override public android.os.IBinder getActivityStackSupervisor() throws android.os.RemoteException
    {
      return null;
    }
    @Override public android.os.IBinder getPrivacyManager() throws android.os.RemoteException
    {
      return null;
    }
    @Override public android.os.IBinder getAppOpsService() throws android.os.RemoteException
    {
      return null;
    }
    @Override public android.os.IBinder getPushManager() throws android.os.RemoteException
    {
      return null;
    }
    @Override public android.os.IBinder getNotificationManager() throws android.os.RemoteException
    {
      return null;
    }
    @Override public android.os.IBinder getAudioManager() throws android.os.RemoteException
    {
      return null;
    }
    @Override public github.tornaco.android.thanos.core.profile.IProfileManager getProfileManager() throws android.os.RemoteException
    {
      return null;
    }
    @Override public android.os.IBinder getBackupAgent() throws android.os.RemoteException
    {
      return null;
    }
    @Override public android.os.IBinder getWindowManager() throws android.os.RemoteException
    {
      return null;
    }
    @Override public android.os.IBinder getPowerManager() throws android.os.RemoteException
    {
      return null;
    }
    @Override public android.os.IBinder getInputManager() throws android.os.RemoteException
    {
      return null;
    }
    @Override public void registerEventSubscriber(android.content.IntentFilter filter, android.os.IBinder subscriber) throws android.os.RemoteException
    {
    }
    @Override public void unRegisterEventSubscriber(android.os.IBinder subscriber) throws android.os.RemoteException
    {
    }
    @Override public java.lang.String fingerPrint() throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.lang.String getVersionName() throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.lang.String whoAreYou() throws android.os.RemoteException
    {
      return null;
    }
    @Override public boolean isLoggingEnabled() throws android.os.RemoteException
    {
      return false;
    }
    @Override public void setLoggingEnabled(boolean enable) throws android.os.RemoteException
    {
    }
    @Override public boolean hasFeature(java.lang.String feature) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean hasFrameworkInitializeError() throws android.os.RemoteException
    {
      return false;
    }
    @Override public android.os.IBinder getPluginLogger(java.lang.String pluginAlias) throws android.os.RemoteException
    {
      return null;
    }
    @Override public android.os.IBinder getInfiniteZ() throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.util.List<java.lang.String> getPatchingSource() throws android.os.RemoteException
    {
      return null;
    }
    @Override public android.os.IBinder getRS() throws android.os.RemoteException
    {
      return null;
    }
    @Override public android.os.IBinder getUsageStatsManager() throws android.os.RemoteException
    {
      return null;
    }
    @Override public android.os.IBinder getPushDelegateManager() throws android.os.RemoteException
    {
      return null;
    }
    @Override public android.os.IBinder getNetworkManager() throws android.os.RemoteException
    {
      return null;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements github.tornaco.android.thanos.core.IThanos
  {
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an github.tornaco.android.thanos.core.IThanos interface,
     * generating a proxy if needed.
     */
    public static github.tornaco.android.thanos.core.IThanos asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof github.tornaco.android.thanos.core.IThanos))) {
        return ((github.tornaco.android.thanos.core.IThanos)iin);
      }
      return new github.tornaco.android.thanos.core.IThanos.Stub.Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      java.lang.String descriptor = DESCRIPTOR;
      if (code >= android.os.IBinder.FIRST_CALL_TRANSACTION && code <= android.os.IBinder.LAST_CALL_TRANSACTION) {
        data.enforceInterface(descriptor);
      }
      switch (code)
      {
        case INTERFACE_TRANSACTION:
        {
          reply.writeString(descriptor);
          return true;
        }
      }
      switch (code)
      {
        case TRANSACTION_getServiceManager:
        {
          android.os.IBinder _result = this.getServiceManager();
          reply.writeNoException();
          reply.writeStrongBinder(_result);
          break;
        }
        case TRANSACTION_getPrefManager:
        {
          android.os.IBinder _result = this.getPrefManager();
          reply.writeNoException();
          reply.writeStrongBinder(_result);
          break;
        }
        case TRANSACTION_getActivityManager:
        {
          github.tornaco.android.thanos.core.app.IActivityManager _result = this.getActivityManager();
          reply.writeNoException();
          reply.writeStrongInterface(_result);
          break;
        }
        case TRANSACTION_getPkgManager:
        {
          android.os.IBinder _result = this.getPkgManager();
          reply.writeNoException();
          reply.writeStrongBinder(_result);
          break;
        }
        case TRANSACTION_getActivityStackSupervisor:
        {
          android.os.IBinder _result = this.getActivityStackSupervisor();
          reply.writeNoException();
          reply.writeStrongBinder(_result);
          break;
        }
        case TRANSACTION_getPrivacyManager:
        {
          android.os.IBinder _result = this.getPrivacyManager();
          reply.writeNoException();
          reply.writeStrongBinder(_result);
          break;
        }
        case TRANSACTION_getAppOpsService:
        {
          android.os.IBinder _result = this.getAppOpsService();
          reply.writeNoException();
          reply.writeStrongBinder(_result);
          break;
        }
        case TRANSACTION_getPushManager:
        {
          android.os.IBinder _result = this.getPushManager();
          reply.writeNoException();
          reply.writeStrongBinder(_result);
          break;
        }
        case TRANSACTION_getNotificationManager:
        {
          android.os.IBinder _result = this.getNotificationManager();
          reply.writeNoException();
          reply.writeStrongBinder(_result);
          break;
        }
        case TRANSACTION_getAudioManager:
        {
          android.os.IBinder _result = this.getAudioManager();
          reply.writeNoException();
          reply.writeStrongBinder(_result);
          break;
        }
        case TRANSACTION_getProfileManager:
        {
          github.tornaco.android.thanos.core.profile.IProfileManager _result = this.getProfileManager();
          reply.writeNoException();
          reply.writeStrongInterface(_result);
          break;
        }
        case TRANSACTION_getBackupAgent:
        {
          android.os.IBinder _result = this.getBackupAgent();
          reply.writeNoException();
          reply.writeStrongBinder(_result);
          break;
        }
        case TRANSACTION_getWindowManager:
        {
          android.os.IBinder _result = this.getWindowManager();
          reply.writeNoException();
          reply.writeStrongBinder(_result);
          break;
        }
        case TRANSACTION_getPowerManager:
        {
          android.os.IBinder _result = this.getPowerManager();
          reply.writeNoException();
          reply.writeStrongBinder(_result);
          break;
        }
        case TRANSACTION_getInputManager:
        {
          android.os.IBinder _result = this.getInputManager();
          reply.writeNoException();
          reply.writeStrongBinder(_result);
          break;
        }
        case TRANSACTION_registerEventSubscriber:
        {
          android.content.IntentFilter _arg0;
          _arg0 = _Parcel.readTypedObject(data, android.content.IntentFilter.CREATOR);
          android.os.IBinder _arg1;
          _arg1 = data.readStrongBinder();
          this.registerEventSubscriber(_arg0, _arg1);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_unRegisterEventSubscriber:
        {
          android.os.IBinder _arg0;
          _arg0 = data.readStrongBinder();
          this.unRegisterEventSubscriber(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_fingerPrint:
        {
          java.lang.String _result = this.fingerPrint();
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_getVersionName:
        {
          java.lang.String _result = this.getVersionName();
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_whoAreYou:
        {
          java.lang.String _result = this.whoAreYou();
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_isLoggingEnabled:
        {
          boolean _result = this.isLoggingEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_setLoggingEnabled:
        {
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setLoggingEnabled(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_hasFeature:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _result = this.hasFeature(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_hasFrameworkInitializeError:
        {
          boolean _result = this.hasFrameworkInitializeError();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_getPluginLogger:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          android.os.IBinder _result = this.getPluginLogger(_arg0);
          reply.writeNoException();
          reply.writeStrongBinder(_result);
          break;
        }
        case TRANSACTION_getInfiniteZ:
        {
          android.os.IBinder _result = this.getInfiniteZ();
          reply.writeNoException();
          reply.writeStrongBinder(_result);
          break;
        }
        case TRANSACTION_getPatchingSource:
        {
          java.util.List<java.lang.String> _result = this.getPatchingSource();
          reply.writeNoException();
          reply.writeStringList(_result);
          break;
        }
        case TRANSACTION_getRS:
        {
          android.os.IBinder _result = this.getRS();
          reply.writeNoException();
          reply.writeStrongBinder(_result);
          break;
        }
        case TRANSACTION_getUsageStatsManager:
        {
          android.os.IBinder _result = this.getUsageStatsManager();
          reply.writeNoException();
          reply.writeStrongBinder(_result);
          break;
        }
        case TRANSACTION_getPushDelegateManager:
        {
          android.os.IBinder _result = this.getPushDelegateManager();
          reply.writeNoException();
          reply.writeStrongBinder(_result);
          break;
        }
        case TRANSACTION_getNetworkManager:
        {
          android.os.IBinder _result = this.getNetworkManager();
          reply.writeNoException();
          reply.writeStrongBinder(_result);
          break;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
      return true;
    }
    private static class Proxy implements github.tornaco.android.thanos.core.IThanos
    {
      private android.os.IBinder mRemote;
      Proxy(android.os.IBinder remote)
      {
        mRemote = remote;
      }
      @Override public android.os.IBinder asBinder()
      {
        return mRemote;
      }
      public java.lang.String getInterfaceDescriptor()
      {
        return DESCRIPTOR;
      }
      @Override public android.os.IBinder getServiceManager() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.os.IBinder _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getServiceManager, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readStrongBinder();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public android.os.IBinder getPrefManager() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.os.IBinder _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getPrefManager, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readStrongBinder();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public github.tornaco.android.thanos.core.app.IActivityManager getActivityManager() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        github.tornaco.android.thanos.core.app.IActivityManager _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getActivityManager, _data, _reply, 0);
          _reply.readException();
          _result = github.tornaco.android.thanos.core.app.IActivityManager.Stub.asInterface(_reply.readStrongBinder());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public android.os.IBinder getPkgManager() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.os.IBinder _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getPkgManager, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readStrongBinder();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public android.os.IBinder getActivityStackSupervisor() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.os.IBinder _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getActivityStackSupervisor, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readStrongBinder();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public android.os.IBinder getPrivacyManager() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.os.IBinder _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getPrivacyManager, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readStrongBinder();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public android.os.IBinder getAppOpsService() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.os.IBinder _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAppOpsService, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readStrongBinder();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public android.os.IBinder getPushManager() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.os.IBinder _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getPushManager, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readStrongBinder();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public android.os.IBinder getNotificationManager() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.os.IBinder _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getNotificationManager, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readStrongBinder();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public android.os.IBinder getAudioManager() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.os.IBinder _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAudioManager, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readStrongBinder();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public github.tornaco.android.thanos.core.profile.IProfileManager getProfileManager() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        github.tornaco.android.thanos.core.profile.IProfileManager _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getProfileManager, _data, _reply, 0);
          _reply.readException();
          _result = github.tornaco.android.thanos.core.profile.IProfileManager.Stub.asInterface(_reply.readStrongBinder());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public android.os.IBinder getBackupAgent() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.os.IBinder _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getBackupAgent, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readStrongBinder();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public android.os.IBinder getWindowManager() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.os.IBinder _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getWindowManager, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readStrongBinder();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public android.os.IBinder getPowerManager() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.os.IBinder _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getPowerManager, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readStrongBinder();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public android.os.IBinder getInputManager() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.os.IBinder _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getInputManager, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readStrongBinder();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void registerEventSubscriber(android.content.IntentFilter filter, android.os.IBinder subscriber) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _Parcel.writeTypedObject(_data, filter, 0);
          _data.writeStrongBinder(subscriber);
          boolean _status = mRemote.transact(Stub.TRANSACTION_registerEventSubscriber, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void unRegisterEventSubscriber(android.os.IBinder subscriber) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder(subscriber);
          boolean _status = mRemote.transact(Stub.TRANSACTION_unRegisterEventSubscriber, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public java.lang.String fingerPrint() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_fingerPrint, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.lang.String getVersionName() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getVersionName, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.lang.String whoAreYou() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_whoAreYou, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean isLoggingEnabled() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isLoggingEnabled, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void setLoggingEnabled(boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setLoggingEnabled, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean hasFeature(java.lang.String feature) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(feature);
          boolean _status = mRemote.transact(Stub.TRANSACTION_hasFeature, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean hasFrameworkInitializeError() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_hasFrameworkInitializeError, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public android.os.IBinder getPluginLogger(java.lang.String pluginAlias) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.os.IBinder _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(pluginAlias);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getPluginLogger, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readStrongBinder();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public android.os.IBinder getInfiniteZ() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.os.IBinder _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getInfiniteZ, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readStrongBinder();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.util.List<java.lang.String> getPatchingSource() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<java.lang.String> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getPatchingSource, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createStringArrayList();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public android.os.IBinder getRS() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.os.IBinder _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getRS, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readStrongBinder();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public android.os.IBinder getUsageStatsManager() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.os.IBinder _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getUsageStatsManager, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readStrongBinder();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public android.os.IBinder getPushDelegateManager() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.os.IBinder _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getPushDelegateManager, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readStrongBinder();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public android.os.IBinder getNetworkManager() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.os.IBinder _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getNetworkManager, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readStrongBinder();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
    }
    static final int TRANSACTION_getServiceManager = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_getPrefManager = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_getActivityManager = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_getPkgManager = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_getActivityStackSupervisor = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
    static final int TRANSACTION_getPrivacyManager = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
    static final int TRANSACTION_getAppOpsService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
    static final int TRANSACTION_getPushManager = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
    static final int TRANSACTION_getNotificationManager = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
    static final int TRANSACTION_getAudioManager = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
    static final int TRANSACTION_getProfileManager = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
    static final int TRANSACTION_getBackupAgent = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
    static final int TRANSACTION_getWindowManager = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
    static final int TRANSACTION_getPowerManager = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
    static final int TRANSACTION_getInputManager = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
    static final int TRANSACTION_registerEventSubscriber = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
    static final int TRANSACTION_unRegisterEventSubscriber = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
    static final int TRANSACTION_fingerPrint = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
    static final int TRANSACTION_getVersionName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
    static final int TRANSACTION_whoAreYou = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
    static final int TRANSACTION_isLoggingEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
    static final int TRANSACTION_setLoggingEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
    static final int TRANSACTION_hasFeature = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);
    static final int TRANSACTION_hasFrameworkInitializeError = (android.os.IBinder.FIRST_CALL_TRANSACTION + 23);
    static final int TRANSACTION_getPluginLogger = (android.os.IBinder.FIRST_CALL_TRANSACTION + 24);
    static final int TRANSACTION_getInfiniteZ = (android.os.IBinder.FIRST_CALL_TRANSACTION + 25);
    static final int TRANSACTION_getPatchingSource = (android.os.IBinder.FIRST_CALL_TRANSACTION + 26);
    static final int TRANSACTION_getRS = (android.os.IBinder.FIRST_CALL_TRANSACTION + 27);
    static final int TRANSACTION_getUsageStatsManager = (android.os.IBinder.FIRST_CALL_TRANSACTION + 28);
    static final int TRANSACTION_getPushDelegateManager = (android.os.IBinder.FIRST_CALL_TRANSACTION + 29);
    static final int TRANSACTION_getNetworkManager = (android.os.IBinder.FIRST_CALL_TRANSACTION + 30);
  }
  public static final java.lang.String DESCRIPTOR = "github.tornaco.android.thanos.core.IThanos";
  public android.os.IBinder getServiceManager() throws android.os.RemoteException;
  public android.os.IBinder getPrefManager() throws android.os.RemoteException;
  public github.tornaco.android.thanos.core.app.IActivityManager getActivityManager() throws android.os.RemoteException;
  public android.os.IBinder getPkgManager() throws android.os.RemoteException;
  public android.os.IBinder getActivityStackSupervisor() throws android.os.RemoteException;
  public android.os.IBinder getPrivacyManager() throws android.os.RemoteException;
  public android.os.IBinder getAppOpsService() throws android.os.RemoteException;
  public android.os.IBinder getPushManager() throws android.os.RemoteException;
  public android.os.IBinder getNotificationManager() throws android.os.RemoteException;
  public android.os.IBinder getAudioManager() throws android.os.RemoteException;
  public github.tornaco.android.thanos.core.profile.IProfileManager getProfileManager() throws android.os.RemoteException;
  public android.os.IBinder getBackupAgent() throws android.os.RemoteException;
  public android.os.IBinder getWindowManager() throws android.os.RemoteException;
  public android.os.IBinder getPowerManager() throws android.os.RemoteException;
  public android.os.IBinder getInputManager() throws android.os.RemoteException;
  public void registerEventSubscriber(android.content.IntentFilter filter, android.os.IBinder subscriber) throws android.os.RemoteException;
  public void unRegisterEventSubscriber(android.os.IBinder subscriber) throws android.os.RemoteException;
  public java.lang.String fingerPrint() throws android.os.RemoteException;
  public java.lang.String getVersionName() throws android.os.RemoteException;
  public java.lang.String whoAreYou() throws android.os.RemoteException;
  public boolean isLoggingEnabled() throws android.os.RemoteException;
  public void setLoggingEnabled(boolean enable) throws android.os.RemoteException;
  public boolean hasFeature(java.lang.String feature) throws android.os.RemoteException;
  public boolean hasFrameworkInitializeError() throws android.os.RemoteException;
  public android.os.IBinder getPluginLogger(java.lang.String pluginAlias) throws android.os.RemoteException;
  public android.os.IBinder getInfiniteZ() throws android.os.RemoteException;
  public java.util.List<java.lang.String> getPatchingSource() throws android.os.RemoteException;
  public android.os.IBinder getRS() throws android.os.RemoteException;
  public android.os.IBinder getUsageStatsManager() throws android.os.RemoteException;
  public android.os.IBinder getPushDelegateManager() throws android.os.RemoteException;
  public android.os.IBinder getNetworkManager() throws android.os.RemoteException;
  /** @hide */
  static class _Parcel {
    static private <T> T readTypedObject(
        android.os.Parcel parcel,
        android.os.Parcelable.Creator<T> c) {
      if (parcel.readInt() != 0) {
          return c.createFromParcel(parcel);
      } else {
          return null;
      }
    }
    static private <T extends android.os.Parcelable> void writeTypedObject(
        android.os.Parcel parcel, T value, int parcelableFlags) {
      if (value != null) {
        parcel.writeInt(1);
        value.writeToParcel(parcel, parcelableFlags);
      } else {
        parcel.writeInt(0);
      }
    }
  }
}
