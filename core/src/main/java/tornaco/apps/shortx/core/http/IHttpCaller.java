/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package tornaco.apps.shortx.core.http;
public interface IHttpCaller extends android.os.IInterface
{
  /** Default implementation for IHttpCaller. */
  public static class Default implements tornaco.apps.shortx.core.http.IHttpCaller
  {
    @Override public tornaco.apps.shortx.core.http.ParcelableResponseData call(java.lang.String url, java.lang.String method, java.util.Map<java.lang.String,java.lang.String> headers, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper requestBody, boolean withCookieJar, boolean trustAllCerts) throws android.os.RemoteException
    {
      return null;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements tornaco.apps.shortx.core.http.IHttpCaller
  {
    private static final java.lang.String DESCRIPTOR = "tornaco.apps.shortx.core.http.IHttpCaller";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an tornaco.apps.shortx.core.http.IHttpCaller interface,
     * generating a proxy if needed.
     */
    public static tornaco.apps.shortx.core.http.IHttpCaller asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof tornaco.apps.shortx.core.http.IHttpCaller))) {
        return ((tornaco.apps.shortx.core.http.IHttpCaller)iin);
      }
      return new tornaco.apps.shortx.core.http.IHttpCaller.Stub.Proxy(obj);
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
        case TRANSACTION_call:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          java.util.Map<java.lang.String,java.lang.String> _arg2;
          {
            int N = data.readInt();
            _arg2 = N < 0 ? null : new java.util.HashMap<>();
            java.util.stream.IntStream.range(0, N).forEach(i -> {
              String k = data.readString();
              java.lang.String v;
              v = data.readString();
              _arg2.put(k, v);
            });
          }
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg3;
          if ((0!=data.readInt())) {
            _arg3 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg3 = null;
          }
          boolean _arg4;
          _arg4 = (0!=data.readInt());
          boolean _arg5;
          _arg5 = (0!=data.readInt());
          tornaco.apps.shortx.core.http.ParcelableResponseData _result = this.call(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
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
    private static class Proxy implements tornaco.apps.shortx.core.http.IHttpCaller
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
      @Override public tornaco.apps.shortx.core.http.ParcelableResponseData call(java.lang.String url, java.lang.String method, java.util.Map<java.lang.String,java.lang.String> headers, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper requestBody, boolean withCookieJar, boolean trustAllCerts) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.http.ParcelableResponseData _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(url);
          _data.writeString(method);
          if (headers == null) {
            _data.writeInt(-1);
          } else {
            _data.writeInt(headers.size());
            headers.forEach((k, v) -> {
              _data.writeString(k);
              _data.writeString(v);
            });
          }
          if ((requestBody!=null)) {
            _data.writeInt(1);
            requestBody.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          _data.writeInt(((withCookieJar)?(1):(0)));
          _data.writeInt(((trustAllCerts)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_call, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().call(url, method, headers, requestBody, withCookieJar, trustAllCerts);
          }
          _reply.readException();
          if ((0!=_reply.readInt())) {
            _result = tornaco.apps.shortx.core.http.ParcelableResponseData.CREATOR.createFromParcel(_reply);
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
      public static tornaco.apps.shortx.core.http.IHttpCaller sDefaultImpl;
    }
    static final int TRANSACTION_call = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    public static boolean setDefaultImpl(tornaco.apps.shortx.core.http.IHttpCaller impl) {
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
    public static tornaco.apps.shortx.core.http.IHttpCaller getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  public tornaco.apps.shortx.core.http.ParcelableResponseData call(java.lang.String url, java.lang.String method, java.util.Map<java.lang.String,java.lang.String> headers, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper requestBody, boolean withCookieJar, boolean trustAllCerts) throws android.os.RemoteException;
}
