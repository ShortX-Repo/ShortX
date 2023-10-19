/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package tornaco.apps.shortx.core;
public interface IStatusBarTileSettingsChangeListener extends android.os.IInterface
{
  /** Default implementation for IStatusBarTileSettingsChangeListener. */
  public static class Default implements tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener
  {
    @Override public void onTileSettingsChange(int tileNumber) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener
  {
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener interface,
     * generating a proxy if needed.
     */
    public static tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener))) {
        return ((tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener)iin);
      }
      return new tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener.Stub.Proxy(obj);
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
        case TRANSACTION_onTileSettingsChange:
        {
          int _arg0;
          _arg0 = data.readInt();
          this.onTileSettingsChange(_arg0);
          break;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
      return true;
    }
    private static class Proxy implements tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener
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
      @Override public void onTileSettingsChange(int tileNumber) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(tileNumber);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onTileSettingsChange, _data, null, android.os.IBinder.FLAG_ONEWAY);
        }
        finally {
          _data.recycle();
        }
      }
    }
    static final int TRANSACTION_onTileSettingsChange = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
  }
  public static final java.lang.String DESCRIPTOR = "tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener";
  public void onTileSettingsChange(int tileNumber) throws android.os.RemoteException;
}
