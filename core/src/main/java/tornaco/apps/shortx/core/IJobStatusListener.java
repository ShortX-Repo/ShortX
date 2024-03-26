/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package tornaco.apps.shortx.core;
public interface IJobStatusListener extends android.os.IInterface
{
  /** Default implementation for IJobStatusListener. */
  public static class Default implements tornaco.apps.shortx.core.IJobStatusListener
  {
    @Override public void onJobStarted(java.lang.String id) throws android.os.RemoteException
    {
    }
    @Override public void onJobCompleted(java.lang.String id) throws android.os.RemoteException
    {
    }
    @Override public void onJobCancelled(java.lang.String id) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements tornaco.apps.shortx.core.IJobStatusListener
  {
    private static final java.lang.String DESCRIPTOR = "tornaco.apps.shortx.core.IJobStatusListener";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an tornaco.apps.shortx.core.IJobStatusListener interface,
     * generating a proxy if needed.
     */
    public static tornaco.apps.shortx.core.IJobStatusListener asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof tornaco.apps.shortx.core.IJobStatusListener))) {
        return ((tornaco.apps.shortx.core.IJobStatusListener)iin);
      }
      return new tornaco.apps.shortx.core.IJobStatusListener.Stub.Proxy(obj);
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
        case TRANSACTION_onJobStarted:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.onJobStarted(_arg0);
          return true;
        }
        case TRANSACTION_onJobCompleted:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.onJobCompleted(_arg0);
          return true;
        }
        case TRANSACTION_onJobCancelled:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.onJobCancelled(_arg0);
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements tornaco.apps.shortx.core.IJobStatusListener
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
      @Override public void onJobStarted(java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onJobStarted, _data, null, android.os.IBinder.FLAG_ONEWAY);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onJobStarted(id);
            return;
          }
        }
        finally {
          _data.recycle();
        }
      }
      @Override public void onJobCompleted(java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onJobCompleted, _data, null, android.os.IBinder.FLAG_ONEWAY);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onJobCompleted(id);
            return;
          }
        }
        finally {
          _data.recycle();
        }
      }
      @Override public void onJobCancelled(java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onJobCancelled, _data, null, android.os.IBinder.FLAG_ONEWAY);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onJobCancelled(id);
            return;
          }
        }
        finally {
          _data.recycle();
        }
      }
      public static tornaco.apps.shortx.core.IJobStatusListener sDefaultImpl;
    }
    static final int TRANSACTION_onJobStarted = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_onJobCompleted = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_onJobCancelled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    public static boolean setDefaultImpl(tornaco.apps.shortx.core.IJobStatusListener impl) {
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
    public static tornaco.apps.shortx.core.IJobStatusListener getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  public void onJobStarted(java.lang.String id) throws android.os.RemoteException;
  public void onJobCompleted(java.lang.String id) throws android.os.RemoteException;
  public void onJobCancelled(java.lang.String id) throws android.os.RemoteException;
}
