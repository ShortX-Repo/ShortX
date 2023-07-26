/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package tornaco.apps.shortx.core.os;
/** @hide */
public interface ISynchronousResultReceiver extends android.os.IInterface
{
  /** Default implementation for ISynchronousResultReceiver. */
  public static class Default implements tornaco.apps.shortx.core.os.ISynchronousResultReceiver
  {
    @Override public void send(tornaco.apps.shortx.core.os.SynchronousResultReceiver.Result resultData) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements tornaco.apps.shortx.core.os.ISynchronousResultReceiver
  {
    private static final java.lang.String DESCRIPTOR = "tornaco.apps.shortx.core.os.ISynchronousResultReceiver";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an tornaco.apps.shortx.core.os.ISynchronousResultReceiver interface,
     * generating a proxy if needed.
     */
    public static tornaco.apps.shortx.core.os.ISynchronousResultReceiver asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof tornaco.apps.shortx.core.os.ISynchronousResultReceiver))) {
        return ((tornaco.apps.shortx.core.os.ISynchronousResultReceiver)iin);
      }
      return new tornaco.apps.shortx.core.os.ISynchronousResultReceiver.Stub.Proxy(obj);
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
        case TRANSACTION_send:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.os.SynchronousResultReceiver.Result _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.os.SynchronousResultReceiver.Result.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.send(_arg0);
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements tornaco.apps.shortx.core.os.ISynchronousResultReceiver
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
      @Override public void send(tornaco.apps.shortx.core.os.SynchronousResultReceiver.Result resultData) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((resultData!=null)) {
            _data.writeInt(1);
            resultData.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_send, _data, null, android.os.IBinder.FLAG_ONEWAY);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().send(resultData);
            return;
          }
        }
        finally {
          _data.recycle();
        }
      }
      public static tornaco.apps.shortx.core.os.ISynchronousResultReceiver sDefaultImpl;
    }
    static final int TRANSACTION_send = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    public static boolean setDefaultImpl(tornaco.apps.shortx.core.os.ISynchronousResultReceiver impl) {
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
    public static tornaco.apps.shortx.core.os.ISynchronousResultReceiver getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  public void send(tornaco.apps.shortx.core.os.SynchronousResultReceiver.Result resultData) throws android.os.RemoteException;
}
