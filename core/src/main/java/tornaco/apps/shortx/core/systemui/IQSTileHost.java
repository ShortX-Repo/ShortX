/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package tornaco.apps.shortx.core.systemui;
public interface IQSTileHost extends android.os.IInterface
{
  /** Default implementation for IQSTileHost. */
  public static class Default implements tornaco.apps.shortx.core.systemui.IQSTileHost
  {
    @Override public void getTiles(tornaco.apps.shortx.core.os.SynchronousResultReceiver receiver) throws android.os.RemoteException
    {
    }
    @Override public void click(java.lang.String tileSpec) throws android.os.RemoteException
    {
    }
    @Override public void longClick(java.lang.String tileSpec) throws android.os.RemoteException
    {
    }
    @Override public int getState(java.lang.String tileSpec) throws android.os.RemoteException
    {
      return 0;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements tornaco.apps.shortx.core.systemui.IQSTileHost
  {
    private static final java.lang.String DESCRIPTOR = "tornaco.apps.shortx.core.systemui.IQSTileHost";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an tornaco.apps.shortx.core.systemui.IQSTileHost interface,
     * generating a proxy if needed.
     */
    public static tornaco.apps.shortx.core.systemui.IQSTileHost asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof tornaco.apps.shortx.core.systemui.IQSTileHost))) {
        return ((tornaco.apps.shortx.core.systemui.IQSTileHost)iin);
      }
      return new tornaco.apps.shortx.core.systemui.IQSTileHost.Stub.Proxy(obj);
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
        case TRANSACTION_getTiles:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.os.SynchronousResultReceiver _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.os.SynchronousResultReceiver.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.getTiles(_arg0);
          return true;
        }
        case TRANSACTION_click:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.click(_arg0);
          return true;
        }
        case TRANSACTION_longClick:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.longClick(_arg0);
          return true;
        }
        case TRANSACTION_getState:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          int _result = this.getState(_arg0);
          reply.writeNoException();
          reply.writeInt(_result);
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements tornaco.apps.shortx.core.systemui.IQSTileHost
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
      @Override public void getTiles(tornaco.apps.shortx.core.os.SynchronousResultReceiver receiver) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((receiver!=null)) {
            _data.writeInt(1);
            receiver.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_getTiles, _data, null, android.os.IBinder.FLAG_ONEWAY);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().getTiles(receiver);
            return;
          }
        }
        finally {
          _data.recycle();
        }
      }
      @Override public void click(java.lang.String tileSpec) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(tileSpec);
          boolean _status = mRemote.transact(Stub.TRANSACTION_click, _data, null, android.os.IBinder.FLAG_ONEWAY);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().click(tileSpec);
            return;
          }
        }
        finally {
          _data.recycle();
        }
      }
      @Override public void longClick(java.lang.String tileSpec) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(tileSpec);
          boolean _status = mRemote.transact(Stub.TRANSACTION_longClick, _data, null, android.os.IBinder.FLAG_ONEWAY);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().longClick(tileSpec);
            return;
          }
        }
        finally {
          _data.recycle();
        }
      }
      @Override public int getState(java.lang.String tileSpec) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(tileSpec);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getState, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getState(tileSpec);
          }
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      public static tornaco.apps.shortx.core.systemui.IQSTileHost sDefaultImpl;
    }
    static final int TRANSACTION_getTiles = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_click = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_longClick = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_getState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    public static boolean setDefaultImpl(tornaco.apps.shortx.core.systemui.IQSTileHost impl) {
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
    public static tornaco.apps.shortx.core.systemui.IQSTileHost getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  public void getTiles(tornaco.apps.shortx.core.os.SynchronousResultReceiver receiver) throws android.os.RemoteException;
  public void click(java.lang.String tileSpec) throws android.os.RemoteException;
  public void longClick(java.lang.String tileSpec) throws android.os.RemoteException;
  public int getState(java.lang.String tileSpec) throws android.os.RemoteException;
}
