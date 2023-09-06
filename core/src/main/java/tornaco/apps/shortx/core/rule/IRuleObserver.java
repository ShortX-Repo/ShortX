/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package tornaco.apps.shortx.core.rule;
public interface IRuleObserver extends android.os.IInterface
{
  /** Default implementation for IRuleObserver. */
  public static class Default implements tornaco.apps.shortx.core.rule.IRuleObserver
  {
    @Override public void onAddOrUpdate(java.lang.String id) throws android.os.RemoteException
    {
    }
    @Override public void onDelete(java.lang.String id) throws android.os.RemoteException
    {
    }
    @Override public void onEnabled(java.lang.String id) throws android.os.RemoteException
    {
    }
    @Override public void onDisabled(java.lang.String id) throws android.os.RemoteException
    {
    }
    @Override public void onSetAddOrUpdate(java.lang.String id) throws android.os.RemoteException
    {
    }
    @Override public void onSetDelete(java.lang.String id) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements tornaco.apps.shortx.core.rule.IRuleObserver
  {
    private static final java.lang.String DESCRIPTOR = "tornaco.apps.shortx.core.rule.IRuleObserver";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an tornaco.apps.shortx.core.rule.IRuleObserver interface,
     * generating a proxy if needed.
     */
    public static tornaco.apps.shortx.core.rule.IRuleObserver asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof tornaco.apps.shortx.core.rule.IRuleObserver))) {
        return ((tornaco.apps.shortx.core.rule.IRuleObserver)iin);
      }
      return new tornaco.apps.shortx.core.rule.IRuleObserver.Stub.Proxy(obj);
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
        case TRANSACTION_onAddOrUpdate:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.onAddOrUpdate(_arg0);
          return true;
        }
        case TRANSACTION_onDelete:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.onDelete(_arg0);
          return true;
        }
        case TRANSACTION_onEnabled:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.onEnabled(_arg0);
          return true;
        }
        case TRANSACTION_onDisabled:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.onDisabled(_arg0);
          return true;
        }
        case TRANSACTION_onSetAddOrUpdate:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.onSetAddOrUpdate(_arg0);
          return true;
        }
        case TRANSACTION_onSetDelete:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.onSetDelete(_arg0);
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements tornaco.apps.shortx.core.rule.IRuleObserver
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
      @Override public void onAddOrUpdate(java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onAddOrUpdate, _data, null, android.os.IBinder.FLAG_ONEWAY);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onAddOrUpdate(id);
            return;
          }
        }
        finally {
          _data.recycle();
        }
      }
      @Override public void onDelete(java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onDelete, _data, null, android.os.IBinder.FLAG_ONEWAY);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onDelete(id);
            return;
          }
        }
        finally {
          _data.recycle();
        }
      }
      @Override public void onEnabled(java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onEnabled, _data, null, android.os.IBinder.FLAG_ONEWAY);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onEnabled(id);
            return;
          }
        }
        finally {
          _data.recycle();
        }
      }
      @Override public void onDisabled(java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onDisabled, _data, null, android.os.IBinder.FLAG_ONEWAY);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onDisabled(id);
            return;
          }
        }
        finally {
          _data.recycle();
        }
      }
      @Override public void onSetAddOrUpdate(java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onSetAddOrUpdate, _data, null, android.os.IBinder.FLAG_ONEWAY);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onSetAddOrUpdate(id);
            return;
          }
        }
        finally {
          _data.recycle();
        }
      }
      @Override public void onSetDelete(java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onSetDelete, _data, null, android.os.IBinder.FLAG_ONEWAY);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onSetDelete(id);
            return;
          }
        }
        finally {
          _data.recycle();
        }
      }
      public static tornaco.apps.shortx.core.rule.IRuleObserver sDefaultImpl;
    }
    static final int TRANSACTION_onAddOrUpdate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_onDelete = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_onEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_onDisabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_onSetAddOrUpdate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
    static final int TRANSACTION_onSetDelete = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
    public static boolean setDefaultImpl(tornaco.apps.shortx.core.rule.IRuleObserver impl) {
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
    public static tornaco.apps.shortx.core.rule.IRuleObserver getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  public void onAddOrUpdate(java.lang.String id) throws android.os.RemoteException;
  public void onDelete(java.lang.String id) throws android.os.RemoteException;
  public void onEnabled(java.lang.String id) throws android.os.RemoteException;
  public void onDisabled(java.lang.String id) throws android.os.RemoteException;
  public void onSetAddOrUpdate(java.lang.String id) throws android.os.RemoteException;
  public void onSetDelete(java.lang.String id) throws android.os.RemoteException;
}
