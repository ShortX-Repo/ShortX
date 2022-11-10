/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package tornaco.apps.shortx.core.action.su;
public interface ISu extends android.os.IInterface
{
  /** Default implementation for ISu. */
  public static class Default implements tornaco.apps.shortx.core.action.su.ISu
  {
    @Override public void exe(java.lang.String[] command) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements tornaco.apps.shortx.core.action.su.ISu
  {
    private static final java.lang.String DESCRIPTOR = "tornaco.apps.shortx.core.action.su.ISu";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an tornaco.apps.shortx.core.action.su.ISu interface,
     * generating a proxy if needed.
     */
    public static tornaco.apps.shortx.core.action.su.ISu asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof tornaco.apps.shortx.core.action.su.ISu))) {
        return ((tornaco.apps.shortx.core.action.su.ISu)iin);
      }
      return new tornaco.apps.shortx.core.action.su.ISu.Stub.Proxy(obj);
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
        case TRANSACTION_exe:
        {
          data.enforceInterface(descriptor);
          java.lang.String[] _arg0;
          _arg0 = data.createStringArray();
          this.exe(_arg0);
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements tornaco.apps.shortx.core.action.su.ISu
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
      @Override public void exe(java.lang.String[] command) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStringArray(command);
          boolean _status = mRemote.transact(Stub.TRANSACTION_exe, _data, null, android.os.IBinder.FLAG_ONEWAY);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().exe(command);
            return;
          }
        }
        finally {
          _data.recycle();
        }
      }
      public static tornaco.apps.shortx.core.action.su.ISu sDefaultImpl;
    }
    static final int TRANSACTION_exe = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    public static boolean setDefaultImpl(tornaco.apps.shortx.core.action.su.ISu impl) {
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
    public static tornaco.apps.shortx.core.action.su.ISu getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  public void exe(java.lang.String[] command) throws android.os.RemoteException;
}
