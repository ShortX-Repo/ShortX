/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package tornaco.apps.shortx.core.libsu;
public interface IFileSystemService extends android.os.IInterface
{
  /** Default implementation for IFileSystemService. */
  public static class Default implements tornaco.apps.shortx.core.libsu.IFileSystemService
  {
    // File APIs
    /* (err, String) */
    @Override public tornaco.apps.shortx.core.libsu.internal.IOResult getCanonicalPath(java.lang.String path) throws android.os.RemoteException
    {
      return null;
    }
    @Override public boolean isDirectory(java.lang.String path) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean isFile(java.lang.String path) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean isHidden(java.lang.String path) throws android.os.RemoteException
    {
      return false;
    }
    @Override public long lastModified(java.lang.String path) throws android.os.RemoteException
    {
      return 0L;
    }
    @Override public long length(java.lang.String path) throws android.os.RemoteException
    {
      return 0L;
    }
    /* (err, bool) */
    @Override public tornaco.apps.shortx.core.libsu.internal.IOResult createNewFile(java.lang.String path) throws android.os.RemoteException
    {
      return null;
    }
    @Override public boolean delete(java.lang.String path) throws android.os.RemoteException
    {
      return false;
    }
    @Override public java.lang.String[] list(java.lang.String path) throws android.os.RemoteException
    {
      return null;
    }
    @Override public boolean mkdir(java.lang.String path) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean mkdirs(java.lang.String path) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean renameTo(java.lang.String path, java.lang.String dest) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean setLastModified(java.lang.String path, long time) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean setReadOnly(java.lang.String path) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean setWritable(java.lang.String path, boolean writable, boolean ownerOnly) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean setReadable(java.lang.String path, boolean readable, boolean ownerOnly) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean setExecutable(java.lang.String path, boolean executable, boolean ownerOnly) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean checkAccess(java.lang.String path, int access) throws android.os.RemoteException
    {
      return false;
    }
    @Override public long getTotalSpace(java.lang.String path) throws android.os.RemoteException
    {
      return 0L;
    }
    @Override public long getFreeSpace(java.lang.String path) throws android.os.RemoteException
    {
      return 0L;
    }
    @Override public long getUsableSpace(java.lang.String path) throws android.os.RemoteException
    {
      return 0L;
    }
    @Override public int getMode(java.lang.String path) throws android.os.RemoteException
    {
      return 0;
    }
    /* (err, bool) */
    @Override public tornaco.apps.shortx.core.libsu.internal.IOResult createLink(java.lang.String link, java.lang.String target, boolean soft) throws android.os.RemoteException
    {
      return null;
    }
    // I/O APIs

    @Override public void register(android.os.IBinder client) throws android.os.RemoteException
    {
    }
    /* (err, int) */
    @Override public tornaco.apps.shortx.core.libsu.internal.IOResult openChannel(java.lang.String path, int mode, java.lang.String fifo) throws android.os.RemoteException
    {
      return null;
    }
    /* (err) */
    @Override public tornaco.apps.shortx.core.libsu.internal.IOResult openReadStream(java.lang.String path, android.os.ParcelFileDescriptor fd) throws android.os.RemoteException
    {
      return null;
    }
    /* (err) */
    @Override public tornaco.apps.shortx.core.libsu.internal.IOResult openWriteStream(java.lang.String path, android.os.ParcelFileDescriptor fd, boolean append) throws android.os.RemoteException
    {
      return null;
    }
    @Override public void close(int handle) throws android.os.RemoteException
    {
    }
    /* (err, int) */
    @Override public tornaco.apps.shortx.core.libsu.internal.IOResult pread(int handle, int len, long offset) throws android.os.RemoteException
    {
      return null;
    }
    /* (err) */
    @Override public tornaco.apps.shortx.core.libsu.internal.IOResult pwrite(int handle, int len, long offset) throws android.os.RemoteException
    {
      return null;
    }
    /* (err, long) */
    @Override public tornaco.apps.shortx.core.libsu.internal.IOResult lseek(int handle, long offset, int whence) throws android.os.RemoteException
    {
      return null;
    }
    /* (err, long) */
    @Override public tornaco.apps.shortx.core.libsu.internal.IOResult size(int handle) throws android.os.RemoteException
    {
      return null;
    }
    /* (err) */
    @Override public tornaco.apps.shortx.core.libsu.internal.IOResult ftruncate(int handle, long length) throws android.os.RemoteException
    {
      return null;
    }
    /* (err) */
    @Override public tornaco.apps.shortx.core.libsu.internal.IOResult sync(int handle, boolean metadata) throws android.os.RemoteException
    {
      return null;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements tornaco.apps.shortx.core.libsu.IFileSystemService
  {
    private static final java.lang.String DESCRIPTOR = "tornaco.apps.shortx.core.libsu.IFileSystemService";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an tornaco.apps.shortx.core.libsu.IFileSystemService interface,
     * generating a proxy if needed.
     */
    public static tornaco.apps.shortx.core.libsu.IFileSystemService asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof tornaco.apps.shortx.core.libsu.IFileSystemService))) {
        return ((tornaco.apps.shortx.core.libsu.IFileSystemService)iin);
      }
      return new tornaco.apps.shortx.core.libsu.IFileSystemService.Stub.Proxy(obj);
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
        case TRANSACTION_getCanonicalPath:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          tornaco.apps.shortx.core.libsu.internal.IOResult _result = this.getCanonicalPath(_arg0);
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
        case TRANSACTION_isDirectory:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _result = this.isDirectory(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_isFile:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _result = this.isFile(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_isHidden:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _result = this.isHidden(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_lastModified:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          long _result = this.lastModified(_arg0);
          reply.writeNoException();
          reply.writeLong(_result);
          return true;
        }
        case TRANSACTION_length:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          long _result = this.length(_arg0);
          reply.writeNoException();
          reply.writeLong(_result);
          return true;
        }
        case TRANSACTION_createNewFile:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          tornaco.apps.shortx.core.libsu.internal.IOResult _result = this.createNewFile(_arg0);
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
        case TRANSACTION_delete:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _result = this.delete(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_list:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String[] _result = this.list(_arg0);
          reply.writeNoException();
          reply.writeStringArray(_result);
          return true;
        }
        case TRANSACTION_mkdir:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _result = this.mkdir(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_mkdirs:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _result = this.mkdirs(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_renameTo:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          boolean _result = this.renameTo(_arg0, _arg1);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_setLastModified:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          long _arg1;
          _arg1 = data.readLong();
          boolean _result = this.setLastModified(_arg0, _arg1);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_setReadOnly:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _result = this.setReadOnly(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_setWritable:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          boolean _arg2;
          _arg2 = (0!=data.readInt());
          boolean _result = this.setWritable(_arg0, _arg1, _arg2);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_setReadable:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          boolean _arg2;
          _arg2 = (0!=data.readInt());
          boolean _result = this.setReadable(_arg0, _arg1, _arg2);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_setExecutable:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          boolean _arg2;
          _arg2 = (0!=data.readInt());
          boolean _result = this.setExecutable(_arg0, _arg1, _arg2);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_checkAccess:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          int _arg1;
          _arg1 = data.readInt();
          boolean _result = this.checkAccess(_arg0, _arg1);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_getTotalSpace:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          long _result = this.getTotalSpace(_arg0);
          reply.writeNoException();
          reply.writeLong(_result);
          return true;
        }
        case TRANSACTION_getFreeSpace:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          long _result = this.getFreeSpace(_arg0);
          reply.writeNoException();
          reply.writeLong(_result);
          return true;
        }
        case TRANSACTION_getUsableSpace:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          long _result = this.getUsableSpace(_arg0);
          reply.writeNoException();
          reply.writeLong(_result);
          return true;
        }
        case TRANSACTION_getMode:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          int _result = this.getMode(_arg0);
          reply.writeNoException();
          reply.writeInt(_result);
          return true;
        }
        case TRANSACTION_createLink:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          boolean _arg2;
          _arg2 = (0!=data.readInt());
          tornaco.apps.shortx.core.libsu.internal.IOResult _result = this.createLink(_arg0, _arg1, _arg2);
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
        case TRANSACTION_register:
        {
          data.enforceInterface(descriptor);
          android.os.IBinder _arg0;
          _arg0 = data.readStrongBinder();
          this.register(_arg0);
          return true;
        }
        case TRANSACTION_openChannel:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          int _arg1;
          _arg1 = data.readInt();
          java.lang.String _arg2;
          _arg2 = data.readString();
          tornaco.apps.shortx.core.libsu.internal.IOResult _result = this.openChannel(_arg0, _arg1, _arg2);
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
        case TRANSACTION_openReadStream:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          android.os.ParcelFileDescriptor _arg1;
          if ((0!=data.readInt())) {
            _arg1 = android.os.ParcelFileDescriptor.CREATOR.createFromParcel(data);
          }
          else {
            _arg1 = null;
          }
          tornaco.apps.shortx.core.libsu.internal.IOResult _result = this.openReadStream(_arg0, _arg1);
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
        case TRANSACTION_openWriteStream:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          android.os.ParcelFileDescriptor _arg1;
          if ((0!=data.readInt())) {
            _arg1 = android.os.ParcelFileDescriptor.CREATOR.createFromParcel(data);
          }
          else {
            _arg1 = null;
          }
          boolean _arg2;
          _arg2 = (0!=data.readInt());
          tornaco.apps.shortx.core.libsu.internal.IOResult _result = this.openWriteStream(_arg0, _arg1, _arg2);
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
        case TRANSACTION_close:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          this.close(_arg0);
          return true;
        }
        case TRANSACTION_pread:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          int _arg1;
          _arg1 = data.readInt();
          long _arg2;
          _arg2 = data.readLong();
          tornaco.apps.shortx.core.libsu.internal.IOResult _result = this.pread(_arg0, _arg1, _arg2);
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
        case TRANSACTION_pwrite:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          int _arg1;
          _arg1 = data.readInt();
          long _arg2;
          _arg2 = data.readLong();
          tornaco.apps.shortx.core.libsu.internal.IOResult _result = this.pwrite(_arg0, _arg1, _arg2);
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
        case TRANSACTION_lseek:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          long _arg1;
          _arg1 = data.readLong();
          int _arg2;
          _arg2 = data.readInt();
          tornaco.apps.shortx.core.libsu.internal.IOResult _result = this.lseek(_arg0, _arg1, _arg2);
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
        case TRANSACTION_size:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          tornaco.apps.shortx.core.libsu.internal.IOResult _result = this.size(_arg0);
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
        case TRANSACTION_ftruncate:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          long _arg1;
          _arg1 = data.readLong();
          tornaco.apps.shortx.core.libsu.internal.IOResult _result = this.ftruncate(_arg0, _arg1);
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
        case TRANSACTION_sync:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          tornaco.apps.shortx.core.libsu.internal.IOResult _result = this.sync(_arg0, _arg1);
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
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements tornaco.apps.shortx.core.libsu.IFileSystemService
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
      // File APIs
      /* (err, String) */
      @Override public tornaco.apps.shortx.core.libsu.internal.IOResult getCanonicalPath(java.lang.String path) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.libsu.internal.IOResult _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(path);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getCanonicalPath, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getCanonicalPath(path);
          }
          _reply.readException();
          if ((0!=_reply.readInt())) {
            _result = tornaco.apps.shortx.core.libsu.internal.IOResult.CREATOR.createFromParcel(_reply);
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
      @Override public boolean isDirectory(java.lang.String path) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(path);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isDirectory, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isDirectory(path);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean isFile(java.lang.String path) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(path);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isFile, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isFile(path);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean isHidden(java.lang.String path) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(path);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isHidden, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isHidden(path);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public long lastModified(java.lang.String path) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        long _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(path);
          boolean _status = mRemote.transact(Stub.TRANSACTION_lastModified, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().lastModified(path);
          }
          _reply.readException();
          _result = _reply.readLong();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public long length(java.lang.String path) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        long _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(path);
          boolean _status = mRemote.transact(Stub.TRANSACTION_length, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().length(path);
          }
          _reply.readException();
          _result = _reply.readLong();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /* (err, bool) */
      @Override public tornaco.apps.shortx.core.libsu.internal.IOResult createNewFile(java.lang.String path) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.libsu.internal.IOResult _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(path);
          boolean _status = mRemote.transact(Stub.TRANSACTION_createNewFile, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().createNewFile(path);
          }
          _reply.readException();
          if ((0!=_reply.readInt())) {
            _result = tornaco.apps.shortx.core.libsu.internal.IOResult.CREATOR.createFromParcel(_reply);
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
      @Override public boolean delete(java.lang.String path) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(path);
          boolean _status = mRemote.transact(Stub.TRANSACTION_delete, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().delete(path);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.lang.String[] list(java.lang.String path) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String[] _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(path);
          boolean _status = mRemote.transact(Stub.TRANSACTION_list, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().list(path);
          }
          _reply.readException();
          _result = _reply.createStringArray();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean mkdir(java.lang.String path) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(path);
          boolean _status = mRemote.transact(Stub.TRANSACTION_mkdir, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().mkdir(path);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean mkdirs(java.lang.String path) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(path);
          boolean _status = mRemote.transact(Stub.TRANSACTION_mkdirs, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().mkdirs(path);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean renameTo(java.lang.String path, java.lang.String dest) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(path);
          _data.writeString(dest);
          boolean _status = mRemote.transact(Stub.TRANSACTION_renameTo, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().renameTo(path, dest);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean setLastModified(java.lang.String path, long time) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(path);
          _data.writeLong(time);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setLastModified, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().setLastModified(path, time);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean setReadOnly(java.lang.String path) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(path);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setReadOnly, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().setReadOnly(path);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean setWritable(java.lang.String path, boolean writable, boolean ownerOnly) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(path);
          _data.writeInt(((writable)?(1):(0)));
          _data.writeInt(((ownerOnly)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setWritable, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().setWritable(path, writable, ownerOnly);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean setReadable(java.lang.String path, boolean readable, boolean ownerOnly) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(path);
          _data.writeInt(((readable)?(1):(0)));
          _data.writeInt(((ownerOnly)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setReadable, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().setReadable(path, readable, ownerOnly);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean setExecutable(java.lang.String path, boolean executable, boolean ownerOnly) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(path);
          _data.writeInt(((executable)?(1):(0)));
          _data.writeInt(((ownerOnly)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setExecutable, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().setExecutable(path, executable, ownerOnly);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean checkAccess(java.lang.String path, int access) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(path);
          _data.writeInt(access);
          boolean _status = mRemote.transact(Stub.TRANSACTION_checkAccess, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().checkAccess(path, access);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public long getTotalSpace(java.lang.String path) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        long _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(path);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getTotalSpace, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getTotalSpace(path);
          }
          _reply.readException();
          _result = _reply.readLong();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public long getFreeSpace(java.lang.String path) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        long _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(path);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getFreeSpace, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getFreeSpace(path);
          }
          _reply.readException();
          _result = _reply.readLong();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public long getUsableSpace(java.lang.String path) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        long _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(path);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getUsableSpace, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getUsableSpace(path);
          }
          _reply.readException();
          _result = _reply.readLong();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public int getMode(java.lang.String path) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(path);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getMode, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getMode(path);
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
      /* (err, bool) */
      @Override public tornaco.apps.shortx.core.libsu.internal.IOResult createLink(java.lang.String link, java.lang.String target, boolean soft) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.libsu.internal.IOResult _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(link);
          _data.writeString(target);
          _data.writeInt(((soft)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_createLink, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().createLink(link, target, soft);
          }
          _reply.readException();
          if ((0!=_reply.readInt())) {
            _result = tornaco.apps.shortx.core.libsu.internal.IOResult.CREATOR.createFromParcel(_reply);
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
      // I/O APIs

      @Override public void register(android.os.IBinder client) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder(client);
          boolean _status = mRemote.transact(Stub.TRANSACTION_register, _data, null, android.os.IBinder.FLAG_ONEWAY);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().register(client);
            return;
          }
        }
        finally {
          _data.recycle();
        }
      }
      /* (err, int) */
      @Override public tornaco.apps.shortx.core.libsu.internal.IOResult openChannel(java.lang.String path, int mode, java.lang.String fifo) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.libsu.internal.IOResult _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(path);
          _data.writeInt(mode);
          _data.writeString(fifo);
          boolean _status = mRemote.transact(Stub.TRANSACTION_openChannel, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().openChannel(path, mode, fifo);
          }
          _reply.readException();
          if ((0!=_reply.readInt())) {
            _result = tornaco.apps.shortx.core.libsu.internal.IOResult.CREATOR.createFromParcel(_reply);
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
      /* (err) */
      @Override public tornaco.apps.shortx.core.libsu.internal.IOResult openReadStream(java.lang.String path, android.os.ParcelFileDescriptor fd) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.libsu.internal.IOResult _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(path);
          if ((fd!=null)) {
            _data.writeInt(1);
            fd.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_openReadStream, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().openReadStream(path, fd);
          }
          _reply.readException();
          if ((0!=_reply.readInt())) {
            _result = tornaco.apps.shortx.core.libsu.internal.IOResult.CREATOR.createFromParcel(_reply);
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
      /* (err) */
      @Override public tornaco.apps.shortx.core.libsu.internal.IOResult openWriteStream(java.lang.String path, android.os.ParcelFileDescriptor fd, boolean append) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.libsu.internal.IOResult _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(path);
          if ((fd!=null)) {
            _data.writeInt(1);
            fd.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          _data.writeInt(((append)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_openWriteStream, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().openWriteStream(path, fd, append);
          }
          _reply.readException();
          if ((0!=_reply.readInt())) {
            _result = tornaco.apps.shortx.core.libsu.internal.IOResult.CREATOR.createFromParcel(_reply);
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
      @Override public void close(int handle) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(handle);
          boolean _status = mRemote.transact(Stub.TRANSACTION_close, _data, null, android.os.IBinder.FLAG_ONEWAY);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().close(handle);
            return;
          }
        }
        finally {
          _data.recycle();
        }
      }
      /* (err, int) */
      @Override public tornaco.apps.shortx.core.libsu.internal.IOResult pread(int handle, int len, long offset) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.libsu.internal.IOResult _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(handle);
          _data.writeInt(len);
          _data.writeLong(offset);
          boolean _status = mRemote.transact(Stub.TRANSACTION_pread, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().pread(handle, len, offset);
          }
          _reply.readException();
          if ((0!=_reply.readInt())) {
            _result = tornaco.apps.shortx.core.libsu.internal.IOResult.CREATOR.createFromParcel(_reply);
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
      /* (err) */
      @Override public tornaco.apps.shortx.core.libsu.internal.IOResult pwrite(int handle, int len, long offset) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.libsu.internal.IOResult _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(handle);
          _data.writeInt(len);
          _data.writeLong(offset);
          boolean _status = mRemote.transact(Stub.TRANSACTION_pwrite, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().pwrite(handle, len, offset);
          }
          _reply.readException();
          if ((0!=_reply.readInt())) {
            _result = tornaco.apps.shortx.core.libsu.internal.IOResult.CREATOR.createFromParcel(_reply);
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
      /* (err, long) */
      @Override public tornaco.apps.shortx.core.libsu.internal.IOResult lseek(int handle, long offset, int whence) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.libsu.internal.IOResult _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(handle);
          _data.writeLong(offset);
          _data.writeInt(whence);
          boolean _status = mRemote.transact(Stub.TRANSACTION_lseek, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().lseek(handle, offset, whence);
          }
          _reply.readException();
          if ((0!=_reply.readInt())) {
            _result = tornaco.apps.shortx.core.libsu.internal.IOResult.CREATOR.createFromParcel(_reply);
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
      /* (err, long) */
      @Override public tornaco.apps.shortx.core.libsu.internal.IOResult size(int handle) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.libsu.internal.IOResult _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(handle);
          boolean _status = mRemote.transact(Stub.TRANSACTION_size, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().size(handle);
          }
          _reply.readException();
          if ((0!=_reply.readInt())) {
            _result = tornaco.apps.shortx.core.libsu.internal.IOResult.CREATOR.createFromParcel(_reply);
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
      /* (err) */
      @Override public tornaco.apps.shortx.core.libsu.internal.IOResult ftruncate(int handle, long length) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.libsu.internal.IOResult _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(handle);
          _data.writeLong(length);
          boolean _status = mRemote.transact(Stub.TRANSACTION_ftruncate, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().ftruncate(handle, length);
          }
          _reply.readException();
          if ((0!=_reply.readInt())) {
            _result = tornaco.apps.shortx.core.libsu.internal.IOResult.CREATOR.createFromParcel(_reply);
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
      /* (err) */
      @Override public tornaco.apps.shortx.core.libsu.internal.IOResult sync(int handle, boolean metadata) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.libsu.internal.IOResult _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(handle);
          _data.writeInt(((metadata)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_sync, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().sync(handle, metadata);
          }
          _reply.readException();
          if ((0!=_reply.readInt())) {
            _result = tornaco.apps.shortx.core.libsu.internal.IOResult.CREATOR.createFromParcel(_reply);
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
      public static tornaco.apps.shortx.core.libsu.IFileSystemService sDefaultImpl;
    }
    static final int TRANSACTION_getCanonicalPath = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_isDirectory = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_isFile = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_isHidden = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_lastModified = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
    static final int TRANSACTION_length = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
    static final int TRANSACTION_createNewFile = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
    static final int TRANSACTION_delete = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
    static final int TRANSACTION_list = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
    static final int TRANSACTION_mkdir = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
    static final int TRANSACTION_mkdirs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
    static final int TRANSACTION_renameTo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
    static final int TRANSACTION_setLastModified = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
    static final int TRANSACTION_setReadOnly = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
    static final int TRANSACTION_setWritable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
    static final int TRANSACTION_setReadable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
    static final int TRANSACTION_setExecutable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
    static final int TRANSACTION_checkAccess = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
    static final int TRANSACTION_getTotalSpace = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
    static final int TRANSACTION_getFreeSpace = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
    static final int TRANSACTION_getUsableSpace = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
    static final int TRANSACTION_getMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
    static final int TRANSACTION_createLink = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);
    static final int TRANSACTION_register = (android.os.IBinder.FIRST_CALL_TRANSACTION + 23);
    static final int TRANSACTION_openChannel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 24);
    static final int TRANSACTION_openReadStream = (android.os.IBinder.FIRST_CALL_TRANSACTION + 25);
    static final int TRANSACTION_openWriteStream = (android.os.IBinder.FIRST_CALL_TRANSACTION + 26);
    static final int TRANSACTION_close = (android.os.IBinder.FIRST_CALL_TRANSACTION + 27);
    static final int TRANSACTION_pread = (android.os.IBinder.FIRST_CALL_TRANSACTION + 28);
    static final int TRANSACTION_pwrite = (android.os.IBinder.FIRST_CALL_TRANSACTION + 29);
    static final int TRANSACTION_lseek = (android.os.IBinder.FIRST_CALL_TRANSACTION + 30);
    static final int TRANSACTION_size = (android.os.IBinder.FIRST_CALL_TRANSACTION + 31);
    static final int TRANSACTION_ftruncate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 32);
    static final int TRANSACTION_sync = (android.os.IBinder.FIRST_CALL_TRANSACTION + 33);
    public static boolean setDefaultImpl(tornaco.apps.shortx.core.libsu.IFileSystemService impl) {
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
    public static tornaco.apps.shortx.core.libsu.IFileSystemService getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  // File APIs
  /* (err, String) */
  public tornaco.apps.shortx.core.libsu.internal.IOResult getCanonicalPath(java.lang.String path) throws android.os.RemoteException;
  public boolean isDirectory(java.lang.String path) throws android.os.RemoteException;
  public boolean isFile(java.lang.String path) throws android.os.RemoteException;
  public boolean isHidden(java.lang.String path) throws android.os.RemoteException;
  public long lastModified(java.lang.String path) throws android.os.RemoteException;
  public long length(java.lang.String path) throws android.os.RemoteException;
  /* (err, bool) */
  public tornaco.apps.shortx.core.libsu.internal.IOResult createNewFile(java.lang.String path) throws android.os.RemoteException;
  public boolean delete(java.lang.String path) throws android.os.RemoteException;
  public java.lang.String[] list(java.lang.String path) throws android.os.RemoteException;
  public boolean mkdir(java.lang.String path) throws android.os.RemoteException;
  public boolean mkdirs(java.lang.String path) throws android.os.RemoteException;
  public boolean renameTo(java.lang.String path, java.lang.String dest) throws android.os.RemoteException;
  public boolean setLastModified(java.lang.String path, long time) throws android.os.RemoteException;
  public boolean setReadOnly(java.lang.String path) throws android.os.RemoteException;
  public boolean setWritable(java.lang.String path, boolean writable, boolean ownerOnly) throws android.os.RemoteException;
  public boolean setReadable(java.lang.String path, boolean readable, boolean ownerOnly) throws android.os.RemoteException;
  public boolean setExecutable(java.lang.String path, boolean executable, boolean ownerOnly) throws android.os.RemoteException;
  public boolean checkAccess(java.lang.String path, int access) throws android.os.RemoteException;
  public long getTotalSpace(java.lang.String path) throws android.os.RemoteException;
  public long getFreeSpace(java.lang.String path) throws android.os.RemoteException;
  public long getUsableSpace(java.lang.String path) throws android.os.RemoteException;
  public int getMode(java.lang.String path) throws android.os.RemoteException;
  /* (err, bool) */
  public tornaco.apps.shortx.core.libsu.internal.IOResult createLink(java.lang.String link, java.lang.String target, boolean soft) throws android.os.RemoteException;
  // I/O APIs

  public void register(android.os.IBinder client) throws android.os.RemoteException;
  /* (err, int) */
  public tornaco.apps.shortx.core.libsu.internal.IOResult openChannel(java.lang.String path, int mode, java.lang.String fifo) throws android.os.RemoteException;
  /* (err) */
  public tornaco.apps.shortx.core.libsu.internal.IOResult openReadStream(java.lang.String path, android.os.ParcelFileDescriptor fd) throws android.os.RemoteException;
  /* (err) */
  public tornaco.apps.shortx.core.libsu.internal.IOResult openWriteStream(java.lang.String path, android.os.ParcelFileDescriptor fd, boolean append) throws android.os.RemoteException;
  public void close(int handle) throws android.os.RemoteException;
  /* (err, int) */
  public tornaco.apps.shortx.core.libsu.internal.IOResult pread(int handle, int len, long offset) throws android.os.RemoteException;
  /* (err) */
  public tornaco.apps.shortx.core.libsu.internal.IOResult pwrite(int handle, int len, long offset) throws android.os.RemoteException;
  /* (err, long) */
  public tornaco.apps.shortx.core.libsu.internal.IOResult lseek(int handle, long offset, int whence) throws android.os.RemoteException;
  /* (err, long) */
  public tornaco.apps.shortx.core.libsu.internal.IOResult size(int handle) throws android.os.RemoteException;
  /* (err) */
  public tornaco.apps.shortx.core.libsu.internal.IOResult ftruncate(int handle, long length) throws android.os.RemoteException;
  /* (err) */
  public tornaco.apps.shortx.core.libsu.internal.IOResult sync(int handle, boolean metadata) throws android.os.RemoteException;
}
