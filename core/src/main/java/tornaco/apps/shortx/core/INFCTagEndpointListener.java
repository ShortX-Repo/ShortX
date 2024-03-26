/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package tornaco.apps.shortx.core;
public interface INFCTagEndpointListener extends android.os.IInterface
{
  /** Default implementation for INFCTagEndpointListener. */
  public static class Default implements tornaco.apps.shortx.core.INFCTagEndpointListener
  {
    @Override public void onDispatchTagEndpoint(byte[] uid) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements tornaco.apps.shortx.core.INFCTagEndpointListener
  {
    private static final java.lang.String DESCRIPTOR = "tornaco.apps.shortx.core.INFCTagEndpointListener";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an tornaco.apps.shortx.core.INFCTagEndpointListener interface,
     * generating a proxy if needed.
     */
    public static tornaco.apps.shortx.core.INFCTagEndpointListener asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof tornaco.apps.shortx.core.INFCTagEndpointListener))) {
        return ((tornaco.apps.shortx.core.INFCTagEndpointListener)iin);
      }
      return new tornaco.apps.shortx.core.INFCTagEndpointListener.Stub.Proxy(obj);
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
        case TRANSACTION_onDispatchTagEndpoint:
        {
          data.enforceInterface(descriptor);
          byte[] _arg0;
          _arg0 = data.createByteArray();
          this.onDispatchTagEndpoint(_arg0);
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements tornaco.apps.shortx.core.INFCTagEndpointListener
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
      @Override public void onDispatchTagEndpoint(byte[] uid) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeByteArray(uid);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onDispatchTagEndpoint, _data, null, android.os.IBinder.FLAG_ONEWAY);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onDispatchTagEndpoint(uid);
            return;
          }
        }
        finally {
          _data.recycle();
        }
      }
      public static tornaco.apps.shortx.core.INFCTagEndpointListener sDefaultImpl;
    }
    static final int TRANSACTION_onDispatchTagEndpoint = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    public static boolean setDefaultImpl(tornaco.apps.shortx.core.INFCTagEndpointListener impl) {
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
    public static tornaco.apps.shortx.core.INFCTagEndpointListener getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  public void onDispatchTagEndpoint(byte[] uid) throws android.os.RemoteException;
}
