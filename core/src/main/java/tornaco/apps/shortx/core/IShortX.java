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
    @Override public void setLogDebugEnable(boolean enable) throws android.os.RemoteException
    {
    }
    @Override public boolean isLogDebugEnable() throws android.os.RemoteException
    {
      return false;
    }
    // Rules start

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
    // Rules end

    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getInstalledApps() throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.lang.String getAppLabel(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper pkg) throws android.os.RemoteException
    {
      return null;
    }
    @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper pkgToApp(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper pkg) throws android.os.RemoteException
    {
      return null;
    }
    @Override public void showEdgeHint(int edgeNumber) throws android.os.RemoteException
    {
    }
    // Direct Actions start

    @Override public void addDirectAction(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper da) throws android.os.RemoteException
    {
    }
    @Override public void deleteDirectAction(java.lang.String id) throws android.os.RemoteException
    {
    }
    @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getDirectActionById(java.lang.String id) throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllDirectAction() throws android.os.RemoteException
    {
      return null;
    }
    @Override public void executeDirectionActionById(java.lang.String id) throws android.os.RemoteException
    {
    }
    // Direct Actions end
    // Pkg Set

    @Override public void addPkgSet(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper set) throws android.os.RemoteException
    {
    }
    @Override public void deletePkgSet(java.lang.String id) throws android.os.RemoteException
    {
    }
    @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getPkgSetById(java.lang.String id) throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllPkgSets(boolean withPkgList) throws android.os.RemoteException
    {
      return null;
    }
    // Pkg Set end
    // UI start

    @Override public void showDanmu(java.lang.String text, java.lang.String icon) throws android.os.RemoteException
    {
    }
    @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getDanmuUISettings() throws android.os.RemoteException
    {
      return null;
    }
    @Override public void setDanmuUISettings(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper settings) throws android.os.RemoteException
    {
    }
    @Override public android.graphics.Bitmap getAppIcon(java.lang.String pkgName, int userId) throws android.os.RemoteException
    {
      return null;
    }
    // UI end
    // List with rule id or DA id

    @Override public java.util.List<java.lang.String> getActiveJobs() throws android.os.RemoteException
    {
      return null;
    }
    @Override public void cancelJobs(java.util.List<java.lang.String> jobs) throws android.os.RemoteException
    {
    }
    @Override public void registerJobStatusListener(tornaco.apps.shortx.core.IJobStatusListener listener) throws android.os.RemoteException
    {
    }
    @Override public void unregisterJobStatusListener(tornaco.apps.shortx.core.IJobStatusListener listener) throws android.os.RemoteException
    {
    }
    // Components start

    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getActivities(int userId, java.lang.String packageName) throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getReceivers(int userId, java.lang.String packageName) throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getServices(int userId, java.lang.String packageName) throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getProviders(int userId, java.lang.String packageName) throws android.os.RemoteException
    {
      return null;
    }
    @Override public void setComponentEnabledSetting(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper component, int newState, int flags) throws android.os.RemoteException
    {
    }
    @Override public int getComponentEnabledSetting(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper component) throws android.os.RemoteException
    {
      return 0;
    }
    @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getAppComponentInfo(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper component) throws android.os.RemoteException
    {
      return null;
    }
    // Components end
    // Global var start

    @Override public void addGlobalVar(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper gv) throws android.os.RemoteException
    {
    }
    @Override public void deleteGlobalVar(java.lang.String id) throws android.os.RemoteException
    {
    }
    @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getGlobalVarById(java.lang.String id) throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllGlobalVars() throws android.os.RemoteException
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
        case TRANSACTION_setLogDebugEnable:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setLogDebugEnable(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isLogDebugEnable:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isLogDebugEnable();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
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
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getInstalledApps();
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_getAppLabel:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          java.lang.String _result = this.getAppLabel(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        case TRANSACTION_pkgToApp:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result = this.pkgToApp(_arg0);
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
        case TRANSACTION_showEdgeHint:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          this.showEdgeHint(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_addDirectAction:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.addDirectAction(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_deleteDirectAction:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.deleteDirectAction(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getDirectActionById:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result = this.getDirectActionById(_arg0);
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
        case TRANSACTION_getAllDirectAction:
        {
          data.enforceInterface(descriptor);
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getAllDirectAction();
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_executeDirectionActionById:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.executeDirectionActionById(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_addPkgSet:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.addPkgSet(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_deletePkgSet:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.deletePkgSet(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getPkgSetById:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result = this.getPkgSetById(_arg0);
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
        case TRANSACTION_getAllPkgSets:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getAllPkgSets(_arg0);
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_showDanmu:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          this.showDanmu(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getDanmuUISettings:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result = this.getDanmuUISettings();
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
        case TRANSACTION_setDanmuUISettings:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.setDanmuUISettings(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getAppIcon:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          int _arg1;
          _arg1 = data.readInt();
          android.graphics.Bitmap _result = this.getAppIcon(_arg0, _arg1);
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
        case TRANSACTION_getActiveJobs:
        {
          data.enforceInterface(descriptor);
          java.util.List<java.lang.String> _result = this.getActiveJobs();
          reply.writeNoException();
          reply.writeStringList(_result);
          return true;
        }
        case TRANSACTION_cancelJobs:
        {
          data.enforceInterface(descriptor);
          java.util.List<java.lang.String> _arg0;
          _arg0 = data.createStringArrayList();
          this.cancelJobs(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_registerJobStatusListener:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.IJobStatusListener _arg0;
          _arg0 = tornaco.apps.shortx.core.IJobStatusListener.Stub.asInterface(data.readStrongBinder());
          this.registerJobStatusListener(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_unregisterJobStatusListener:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.IJobStatusListener _arg0;
          _arg0 = tornaco.apps.shortx.core.IJobStatusListener.Stub.asInterface(data.readStrongBinder());
          this.unregisterJobStatusListener(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getActivities:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          java.lang.String _arg1;
          _arg1 = data.readString();
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getActivities(_arg0, _arg1);
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_getReceivers:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          java.lang.String _arg1;
          _arg1 = data.readString();
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getReceivers(_arg0, _arg1);
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_getServices:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          java.lang.String _arg1;
          _arg1 = data.readString();
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getServices(_arg0, _arg1);
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_getProviders:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          java.lang.String _arg1;
          _arg1 = data.readString();
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getProviders(_arg0, _arg1);
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_setComponentEnabledSetting:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          int _arg1;
          _arg1 = data.readInt();
          int _arg2;
          _arg2 = data.readInt();
          this.setComponentEnabledSetting(_arg0, _arg1, _arg2);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getComponentEnabledSetting:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          int _result = this.getComponentEnabledSetting(_arg0);
          reply.writeNoException();
          reply.writeInt(_result);
          return true;
        }
        case TRANSACTION_getAppComponentInfo:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result = this.getAppComponentInfo(_arg0);
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
        case TRANSACTION_addGlobalVar:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.addGlobalVar(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_deleteGlobalVar:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.deleteGlobalVar(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getGlobalVarById:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result = this.getGlobalVarById(_arg0);
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
        case TRANSACTION_getAllGlobalVars:
        {
          data.enforceInterface(descriptor);
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getAllGlobalVars();
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
      @Override public void setLogDebugEnable(boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setLogDebugEnable, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setLogDebugEnable(enable);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean isLogDebugEnable() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isLogDebugEnable, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isLogDebugEnable();
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
      // Rules start

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
      // Rules end

      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getInstalledApps() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getInstalledApps, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getInstalledApps();
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
      @Override public java.lang.String getAppLabel(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper pkg) throws android.os.RemoteException
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
      @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper pkgToApp(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper pkg) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((pkg!=null)) {
            _data.writeInt(1);
            pkg.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_pkgToApp, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().pkgToApp(pkg);
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
      // Direct Actions start

      @Override public void addDirectAction(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper da) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((da!=null)) {
            _data.writeInt(1);
            da.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_addDirectAction, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().addDirectAction(da);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void deleteDirectAction(java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_deleteDirectAction, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().deleteDirectAction(id);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getDirectActionById(java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getDirectActionById, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getDirectActionById(id);
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
      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllDirectAction() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAllDirectAction, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getAllDirectAction();
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
      @Override public void executeDirectionActionById(java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_executeDirectionActionById, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().executeDirectionActionById(id);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      // Direct Actions end
      // Pkg Set

      @Override public void addPkgSet(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper set) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((set!=null)) {
            _data.writeInt(1);
            set.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_addPkgSet, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().addPkgSet(set);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void deletePkgSet(java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_deletePkgSet, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().deletePkgSet(id);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getPkgSetById(java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getPkgSetById, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getPkgSetById(id);
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
      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllPkgSets(boolean withPkgList) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((withPkgList)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAllPkgSets, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getAllPkgSets(withPkgList);
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
      // Pkg Set end
      // UI start

      @Override public void showDanmu(java.lang.String text, java.lang.String icon) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(text);
          _data.writeString(icon);
          boolean _status = mRemote.transact(Stub.TRANSACTION_showDanmu, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().showDanmu(text, icon);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getDanmuUISettings() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getDanmuUISettings, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getDanmuUISettings();
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
      @Override public void setDanmuUISettings(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper settings) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((settings!=null)) {
            _data.writeInt(1);
            settings.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_setDanmuUISettings, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setDanmuUISettings(settings);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public android.graphics.Bitmap getAppIcon(java.lang.String pkgName, int userId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.graphics.Bitmap _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(pkgName);
          _data.writeInt(userId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAppIcon, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getAppIcon(pkgName, userId);
          }
          _reply.readException();
          if ((0!=_reply.readInt())) {
            _result = android.graphics.Bitmap.CREATOR.createFromParcel(_reply);
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
      // UI end
      // List with rule id or DA id

      @Override public java.util.List<java.lang.String> getActiveJobs() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<java.lang.String> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getActiveJobs, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getActiveJobs();
          }
          _reply.readException();
          _result = _reply.createStringArrayList();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void cancelJobs(java.util.List<java.lang.String> jobs) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStringList(jobs);
          boolean _status = mRemote.transact(Stub.TRANSACTION_cancelJobs, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().cancelJobs(jobs);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void registerJobStatusListener(tornaco.apps.shortx.core.IJobStatusListener listener) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_registerJobStatusListener, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().registerJobStatusListener(listener);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void unregisterJobStatusListener(tornaco.apps.shortx.core.IJobStatusListener listener) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_unregisterJobStatusListener, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().unregisterJobStatusListener(listener);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      // Components start

      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getActivities(int userId, java.lang.String packageName) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(userId);
          _data.writeString(packageName);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getActivities, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getActivities(userId, packageName);
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
      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getReceivers(int userId, java.lang.String packageName) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(userId);
          _data.writeString(packageName);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getReceivers, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getReceivers(userId, packageName);
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
      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getServices(int userId, java.lang.String packageName) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(userId);
          _data.writeString(packageName);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getServices, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getServices(userId, packageName);
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
      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getProviders(int userId, java.lang.String packageName) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(userId);
          _data.writeString(packageName);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getProviders, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getProviders(userId, packageName);
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
      @Override public void setComponentEnabledSetting(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper component, int newState, int flags) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((component!=null)) {
            _data.writeInt(1);
            component.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          _data.writeInt(newState);
          _data.writeInt(flags);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setComponentEnabledSetting, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setComponentEnabledSetting(component, newState, flags);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public int getComponentEnabledSetting(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper component) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((component!=null)) {
            _data.writeInt(1);
            component.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_getComponentEnabledSetting, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getComponentEnabledSetting(component);
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
      @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getAppComponentInfo(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper component) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((component!=null)) {
            _data.writeInt(1);
            component.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAppComponentInfo, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getAppComponentInfo(component);
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
      // Components end
      // Global var start

      @Override public void addGlobalVar(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper gv) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((gv!=null)) {
            _data.writeInt(1);
            gv.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_addGlobalVar, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().addGlobalVar(gv);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void deleteGlobalVar(java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_deleteGlobalVar, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().deleteGlobalVar(id);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getGlobalVarById(java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getGlobalVarById, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getGlobalVarById(id);
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
      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllGlobalVars() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAllGlobalVars, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getAllGlobalVars();
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
      public static tornaco.apps.shortx.core.IShortX sDefaultImpl;
    }
    static final int TRANSACTION_version = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_fingerprint = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_setLogDebugEnable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_isLogDebugEnable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_addRule = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
    static final int TRANSACTION_deleteRule = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
    static final int TRANSACTION_getRuleById = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
    static final int TRANSACTION_setRuleEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
    static final int TRANSACTION_getAllRules = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
    static final int TRANSACTION_getInstalledApps = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
    static final int TRANSACTION_getAppLabel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
    static final int TRANSACTION_pkgToApp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
    static final int TRANSACTION_showEdgeHint = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
    static final int TRANSACTION_addDirectAction = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
    static final int TRANSACTION_deleteDirectAction = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
    static final int TRANSACTION_getDirectActionById = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
    static final int TRANSACTION_getAllDirectAction = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
    static final int TRANSACTION_executeDirectionActionById = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
    static final int TRANSACTION_addPkgSet = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
    static final int TRANSACTION_deletePkgSet = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
    static final int TRANSACTION_getPkgSetById = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
    static final int TRANSACTION_getAllPkgSets = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
    static final int TRANSACTION_showDanmu = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);
    static final int TRANSACTION_getDanmuUISettings = (android.os.IBinder.FIRST_CALL_TRANSACTION + 23);
    static final int TRANSACTION_setDanmuUISettings = (android.os.IBinder.FIRST_CALL_TRANSACTION + 24);
    static final int TRANSACTION_getAppIcon = (android.os.IBinder.FIRST_CALL_TRANSACTION + 25);
    static final int TRANSACTION_getActiveJobs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 26);
    static final int TRANSACTION_cancelJobs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 27);
    static final int TRANSACTION_registerJobStatusListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 28);
    static final int TRANSACTION_unregisterJobStatusListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 29);
    static final int TRANSACTION_getActivities = (android.os.IBinder.FIRST_CALL_TRANSACTION + 30);
    static final int TRANSACTION_getReceivers = (android.os.IBinder.FIRST_CALL_TRANSACTION + 31);
    static final int TRANSACTION_getServices = (android.os.IBinder.FIRST_CALL_TRANSACTION + 32);
    static final int TRANSACTION_getProviders = (android.os.IBinder.FIRST_CALL_TRANSACTION + 33);
    static final int TRANSACTION_setComponentEnabledSetting = (android.os.IBinder.FIRST_CALL_TRANSACTION + 34);
    static final int TRANSACTION_getComponentEnabledSetting = (android.os.IBinder.FIRST_CALL_TRANSACTION + 35);
    static final int TRANSACTION_getAppComponentInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 36);
    static final int TRANSACTION_addGlobalVar = (android.os.IBinder.FIRST_CALL_TRANSACTION + 37);
    static final int TRANSACTION_deleteGlobalVar = (android.os.IBinder.FIRST_CALL_TRANSACTION + 38);
    static final int TRANSACTION_getGlobalVarById = (android.os.IBinder.FIRST_CALL_TRANSACTION + 39);
    static final int TRANSACTION_getAllGlobalVars = (android.os.IBinder.FIRST_CALL_TRANSACTION + 40);
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
  public void setLogDebugEnable(boolean enable) throws android.os.RemoteException;
  public boolean isLogDebugEnable() throws android.os.RemoteException;
  // Rules start

  public void addRule(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper rule) throws android.os.RemoteException;
  public void deleteRule(java.lang.String id) throws android.os.RemoteException;
  public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getRuleById(java.lang.String id) throws android.os.RemoteException;
  public void setRuleEnabled(java.lang.String ruleId, boolean enable) throws android.os.RemoteException;
  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllRules() throws android.os.RemoteException;
  // Rules end

  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getInstalledApps() throws android.os.RemoteException;
  public java.lang.String getAppLabel(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper pkg) throws android.os.RemoteException;
  public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper pkgToApp(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper pkg) throws android.os.RemoteException;
  public void showEdgeHint(int edgeNumber) throws android.os.RemoteException;
  // Direct Actions start

  public void addDirectAction(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper da) throws android.os.RemoteException;
  public void deleteDirectAction(java.lang.String id) throws android.os.RemoteException;
  public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getDirectActionById(java.lang.String id) throws android.os.RemoteException;
  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllDirectAction() throws android.os.RemoteException;
  public void executeDirectionActionById(java.lang.String id) throws android.os.RemoteException;
  // Direct Actions end
  // Pkg Set

  public void addPkgSet(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper set) throws android.os.RemoteException;
  public void deletePkgSet(java.lang.String id) throws android.os.RemoteException;
  public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getPkgSetById(java.lang.String id) throws android.os.RemoteException;
  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllPkgSets(boolean withPkgList) throws android.os.RemoteException;
  // Pkg Set end
  // UI start

  public void showDanmu(java.lang.String text, java.lang.String icon) throws android.os.RemoteException;
  public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getDanmuUISettings() throws android.os.RemoteException;
  public void setDanmuUISettings(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper settings) throws android.os.RemoteException;
  public android.graphics.Bitmap getAppIcon(java.lang.String pkgName, int userId) throws android.os.RemoteException;
  // UI end
  // List with rule id or DA id

  public java.util.List<java.lang.String> getActiveJobs() throws android.os.RemoteException;
  public void cancelJobs(java.util.List<java.lang.String> jobs) throws android.os.RemoteException;
  public void registerJobStatusListener(tornaco.apps.shortx.core.IJobStatusListener listener) throws android.os.RemoteException;
  public void unregisterJobStatusListener(tornaco.apps.shortx.core.IJobStatusListener listener) throws android.os.RemoteException;
  // Components start

  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getActivities(int userId, java.lang.String packageName) throws android.os.RemoteException;
  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getReceivers(int userId, java.lang.String packageName) throws android.os.RemoteException;
  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getServices(int userId, java.lang.String packageName) throws android.os.RemoteException;
  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getProviders(int userId, java.lang.String packageName) throws android.os.RemoteException;
  public void setComponentEnabledSetting(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper component, int newState, int flags) throws android.os.RemoteException;
  public int getComponentEnabledSetting(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper component) throws android.os.RemoteException;
  public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getAppComponentInfo(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper component) throws android.os.RemoteException;
  // Components end
  // Global var start

  public void addGlobalVar(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper gv) throws android.os.RemoteException;
  public void deleteGlobalVar(java.lang.String id) throws android.os.RemoteException;
  public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getGlobalVarById(java.lang.String id) throws android.os.RemoteException;
  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllGlobalVars() throws android.os.RemoteException;
}
