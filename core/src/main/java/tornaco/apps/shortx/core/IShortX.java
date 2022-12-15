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
    @Override public void addRule(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper rule) throws android.os.RemoteException
    {
    }
    @Override public void deleteRule(java.lang.String id) throws android.os.RemoteException
    {
    }
    @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getRuleById(java.lang.String id) throws android.os.RemoteException
    {
      return null;
    }
    @Override public void setRuleEnabled(java.lang.String ruleId, boolean enable) throws android.os.RemoteException
    {
    }
    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllRules() throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.util.List<tornaco.apps.shortx.core.App> getInstalledApps() throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.lang.String getAppLabel(tornaco.apps.shortx.core.Pkg pkg) throws android.os.RemoteException
    {
      return null;
    }
    @Override public void showEdgeHint(int edgeNumber) throws android.os.RemoteException
    {
    }
    @Override public java.lang.String getOverlayButtonIconName(int index) throws android.os.RemoteException
    {
      return null;
    }
    @Override public void setOverlayButtonIconName(int index, java.lang.String name) throws android.os.RemoteException
    {
    }
    @Override public void registerOverlayButtonSettingsChangeListener(tornaco.apps.shortx.core.overlay.IOverlayButtonSettingsChangeListener listener) throws android.os.RemoteException
    {
    }
    @Override public void unRegisterOverlayButtonSettingsChangeListener(tornaco.apps.shortx.core.overlay.IOverlayButtonSettingsChangeListener listener) throws android.os.RemoteException
    {
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
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
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
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.deleteRule(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getRuleById:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result = this.getRuleById(_arg0);
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
        case TRANSACTION_setRuleEnabled:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          this.setRuleEnabled(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getAllRules:
        {
          data.enforceInterface(descriptor);
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getAllRules();
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_getInstalledApps:
        {
          data.enforceInterface(descriptor);
          java.util.List<tornaco.apps.shortx.core.App> _result = this.getInstalledApps();
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_getAppLabel:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          java.lang.String _result = this.getAppLabel(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        case TRANSACTION_showEdgeHint:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          this.showEdgeHint(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getOverlayButtonIconName:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          java.lang.String _result = this.getOverlayButtonIconName(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        case TRANSACTION_setOverlayButtonIconName:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          java.lang.String _arg1;
          _arg1 = data.readString();
          this.setOverlayButtonIconName(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_registerOverlayButtonSettingsChangeListener:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.overlay.IOverlayButtonSettingsChangeListener _arg0;
          _arg0 = tornaco.apps.shortx.core.overlay.IOverlayButtonSettingsChangeListener.Stub.asInterface(data.readStrongBinder());
          this.registerOverlayButtonSettingsChangeListener(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_unRegisterOverlayButtonSettingsChangeListener:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.overlay.IOverlayButtonSettingsChangeListener _arg0;
          _arg0 = tornaco.apps.shortx.core.overlay.IOverlayButtonSettingsChangeListener.Stub.asInterface(data.readStrongBinder());
          this.unRegisterOverlayButtonSettingsChangeListener(_arg0);
          reply.writeNoException();
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
      @Override public void addRule(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper rule) throws android.os.RemoteException
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
      @Override public void deleteRule(java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_deleteRule, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().deleteRule(id);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getRuleById(java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getRuleById, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getRuleById(id);
          }
          _reply.readException();
          if ((0!=_reply.readInt())) {
            _result = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(_reply);
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
      @Override public void setRuleEnabled(java.lang.String ruleId, boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(ruleId);
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setRuleEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setRuleEnabled(ruleId, enable);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllRules() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAllRules, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getAllRules();
          }
          _reply.readException();
          _result = _reply.createTypedArrayList(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.util.List<tornaco.apps.shortx.core.App> getInstalledApps() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.App> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getInstalledApps, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getInstalledApps();
          }
          _reply.readException();
          _result = _reply.createTypedArrayList(tornaco.apps.shortx.core.App.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.lang.String getAppLabel(tornaco.apps.shortx.core.Pkg pkg) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((pkg!=null)) {
            _data.writeInt(1);
            pkg.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAppLabel, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getAppLabel(pkg);
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
      @Override public void showEdgeHint(int edgeNumber) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(edgeNumber);
          boolean _status = mRemote.transact(Stub.TRANSACTION_showEdgeHint, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().showEdgeHint(edgeNumber);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public java.lang.String getOverlayButtonIconName(int index) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(index);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getOverlayButtonIconName, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getOverlayButtonIconName(index);
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
      @Override public void setOverlayButtonIconName(int index, java.lang.String name) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(index);
          _data.writeString(name);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setOverlayButtonIconName, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setOverlayButtonIconName(index, name);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void registerOverlayButtonSettingsChangeListener(tornaco.apps.shortx.core.overlay.IOverlayButtonSettingsChangeListener listener) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_registerOverlayButtonSettingsChangeListener, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().registerOverlayButtonSettingsChangeListener(listener);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void unRegisterOverlayButtonSettingsChangeListener(tornaco.apps.shortx.core.overlay.IOverlayButtonSettingsChangeListener listener) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_unRegisterOverlayButtonSettingsChangeListener, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().unRegisterOverlayButtonSettingsChangeListener(listener);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      public static tornaco.apps.shortx.core.IShortX sDefaultImpl;
    }
    static final int TRANSACTION_version = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_fingerprint = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_addRule = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_deleteRule = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_getRuleById = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
    static final int TRANSACTION_setRuleEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
    static final int TRANSACTION_getAllRules = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
    static final int TRANSACTION_getInstalledApps = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
    static final int TRANSACTION_getAppLabel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
    static final int TRANSACTION_showEdgeHint = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
    static final int TRANSACTION_getOverlayButtonIconName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
    static final int TRANSACTION_setOverlayButtonIconName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
    static final int TRANSACTION_registerOverlayButtonSettingsChangeListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
    static final int TRANSACTION_unRegisterOverlayButtonSettingsChangeListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
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
  public void addRule(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper rule) throws android.os.RemoteException;
  public void deleteRule(java.lang.String id) throws android.os.RemoteException;
  public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getRuleById(java.lang.String id) throws android.os.RemoteException;
  public void setRuleEnabled(java.lang.String ruleId, boolean enable) throws android.os.RemoteException;
  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllRules() throws android.os.RemoteException;
  public java.util.List<tornaco.apps.shortx.core.App> getInstalledApps() throws android.os.RemoteException;
  public java.lang.String getAppLabel(tornaco.apps.shortx.core.Pkg pkg) throws android.os.RemoteException;
  public void showEdgeHint(int edgeNumber) throws android.os.RemoteException;
  public java.lang.String getOverlayButtonIconName(int index) throws android.os.RemoteException;
  public void setOverlayButtonIconName(int index, java.lang.String name) throws android.os.RemoteException;
  public void registerOverlayButtonSettingsChangeListener(tornaco.apps.shortx.core.overlay.IOverlayButtonSettingsChangeListener listener) throws android.os.RemoteException;
  public void unRegisterOverlayButtonSettingsChangeListener(tornaco.apps.shortx.core.overlay.IOverlayButtonSettingsChangeListener listener) throws android.os.RemoteException;
}
