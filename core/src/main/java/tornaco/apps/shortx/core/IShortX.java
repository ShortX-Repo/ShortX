/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package tornaco.apps.shortx.core;
public interface IShortX extends android.os.IInterface
{
  /** Default implementation for IShortX. */
  public static class Default implements tornaco.apps.shortx.core.IShortX
  {
    @Override public tornaco.apps.shortx.core.Version version() throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.lang.String fingerprint() throws android.os.RemoteException
    {
      return null;
    }
    @Override public void addRule(tornaco.apps.shortx.core.action.ByteArrayWrapper rule) throws android.os.RemoteException
    {
    }
    @Override public void deleteRule(tornaco.apps.shortx.core.action.ByteArrayWrapper rule) throws android.os.RemoteException
    {
    }
    @Override public java.util.List<tornaco.apps.shortx.core.action.ByteArrayWrapper> getAllRules() throws android.os.RemoteException
    {
      return null;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements tornaco.apps.shortx.core.IShortX
  {
    private static final java.lang.String DESCRIPTOR = "tornaco.apps.shortx.core.IShortX";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an tornaco.apps.shortx.core.IShortX interface,
     * generating a proxy if needed.
     */
    public static tornaco.apps.shortx.core.IShortX asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof tornaco.apps.shortx.core.IShortX))) {
        return ((tornaco.apps.shortx.core.IShortX)iin);
      }
      return new tornaco.apps.shortx.core.IShortX.Stub.Proxy(obj);
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
        case TRANSACTION_version:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.Version _result = this.version();
          reply.writeNoException();
          if ((_result!=null)) {
            reply.writeInt(1);
            _result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          }
          else {
            reply.writeInt(0);
          }
          return true;
        }
        case TRANSACTION_fingerprint:
        {
          data.enforceInterface(descriptor);
          java.lang.String _result = this.fingerprint();
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        case TRANSACTION_addRule:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.action.ByteArrayWrapper _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.addRule(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_deleteRule:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.action.ByteArrayWrapper _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.deleteRule(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getAllRules:
        {
          data.enforceInterface(descriptor);
          java.util.List<tornaco.apps.shortx.core.action.ByteArrayWrapper> _result = this.getAllRules();
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements tornaco.apps.shortx.core.IShortX
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
      @Override public tornaco.apps.shortx.core.Version version() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.Version _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_version, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().version();
          }
          _reply.readException();
          if ((0!=_reply.readInt())) {
            _result = tornaco.apps.shortx.core.Version.CREATOR.createFromParcel(_reply);
          }
          else {
            _result = null;
          }
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.lang.String fingerprint() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_fingerprint, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().fingerprint();
          }
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void addRule(tornaco.apps.shortx.core.action.ByteArrayWrapper rule) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((rule!=null)) {
            _data.writeInt(1);
            rule.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_addRule, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().addRule(rule);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void deleteRule(tornaco.apps.shortx.core.action.ByteArrayWrapper rule) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((rule!=null)) {
            _data.writeInt(1);
            rule.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_deleteRule, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().deleteRule(rule);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public java.util.List<tornaco.apps.shortx.core.action.ByteArrayWrapper> getAllRules() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAllRules, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getAllRules();
          }
          _reply.readException();
          _result = _reply.createTypedArrayList(tornaco.apps.shortx.core.action.ByteArrayWrapper.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      public static tornaco.apps.shortx.core.IShortX sDefaultImpl;
    }
    static final int TRANSACTION_version = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_fingerprint = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_addRule = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_deleteRule = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_getAllRules = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
    public static boolean setDefaultImpl(tornaco.apps.shortx.core.IShortX impl) {
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
    public static tornaco.apps.shortx.core.IShortX getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  public tornaco.apps.shortx.core.Version version() throws android.os.RemoteException;
  public java.lang.String fingerprint() throws android.os.RemoteException;
  public void addRule(tornaco.apps.shortx.core.action.ByteArrayWrapper rule) throws android.os.RemoteException;
  public void deleteRule(tornaco.apps.shortx.core.action.ByteArrayWrapper rule) throws android.os.RemoteException;
  public java.util.List<tornaco.apps.shortx.core.action.ByteArrayWrapper> getAllRules() throws android.os.RemoteException;
}
