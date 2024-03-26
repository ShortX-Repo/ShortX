/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package tornaco.apps.shortx.core.notification;
public interface INotificationPoster extends android.os.IInterface
{
  /** Default implementation for INotificationPoster. */
  public static class Default implements tornaco.apps.shortx.core.notification.INotificationPoster
  {
    // PostNotification

    @Override public void post(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper data) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements tornaco.apps.shortx.core.notification.INotificationPoster
  {
    private static final java.lang.String DESCRIPTOR = "tornaco.apps.shortx.core.notification.INotificationPoster";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an tornaco.apps.shortx.core.notification.INotificationPoster interface,
     * generating a proxy if needed.
     */
    public static tornaco.apps.shortx.core.notification.INotificationPoster asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof tornaco.apps.shortx.core.notification.INotificationPoster))) {
        return ((tornaco.apps.shortx.core.notification.INotificationPoster)iin);
      }
      return new tornaco.apps.shortx.core.notification.INotificationPoster.Stub.Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      java.lang.String descriptor = DESCRIPTOR;
      switch (code)
      {
        case INTERFACE_TRANSACTION:
        {
          reply.writeString(descriptor);
          return true;
        }
        case TRANSACTION_post:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.post(_arg0);
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements tornaco.apps.shortx.core.notification.INotificationPoster
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
      // PostNotification

      @Override public void post(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper data) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((data!=null)) {
            _data.writeInt(1);
            data.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_post, _data, null, android.os.IBinder.FLAG_ONEWAY);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().post(data);
            return;
          }
        }
        finally {
          _data.recycle();
        }
      }
      public static tornaco.apps.shortx.core.notification.INotificationPoster sDefaultImpl;
    }
    static final int TRANSACTION_post = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    public static boolean setDefaultImpl(tornaco.apps.shortx.core.notification.INotificationPoster impl) {
      // Only one user of this interface can use this function
      // at a time. This is a heuristic to detect if two different
      // users in the same process use this function.
      if (Stub.Proxy.sDefaultImpl != null) {
        throw new IllegalStateException("setDefaultImpl() called twice");
      }
      if (impl != null) {
        Stub.Proxy.sDefaultImpl = impl;
        return true;
      }
      return false;
    }
    public static tornaco.apps.shortx.core.notification.INotificationPoster getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  // PostNotification

  public void post(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper data) throws android.os.RemoteException;
}
