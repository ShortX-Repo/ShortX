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
    @Override public java.lang.String getLogDir() throws android.os.RemoteException
    {
      return null;
    }
    @Override public boolean hasFatalError() throws android.os.RemoteException
    {
      return false;
    }
    @Override public void writeLogsTo(android.os.ParcelFileDescriptor pfd) throws android.os.RemoteException
    {
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
    @Override public int getRuleCount() throws android.os.RemoteException
    {
      return 0;
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
    @Override public int getDirectActionCount() throws android.os.RemoteException
    {
      return 0;
    }
    @Override public void executeDirectionActionById(java.lang.String id) throws android.os.RemoteException
    {
    }
    @Override public void executeAction(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper action) throws android.os.RemoteException
    {
    }
    @Override public void executeActionBlocking(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper action) throws android.os.RemoteException
    {
    }
    // Direct Actions end

    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getActionEvaluateRecords() throws android.os.RemoteException
    {
      return null;
    }
    @Override public void clearActionEvaluateRecords() throws android.os.RemoteException
    {
    }
    @Override public void setActionEvaluateRecorderEnabled(boolean enable) throws android.os.RemoteException
    {
    }
    @Override public boolean isActionEvaluateRecorderEnabled() throws android.os.RemoteException
    {
      return false;
    }
    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getConditionEvaluateRecords() throws android.os.RemoteException
    {
      return null;
    }
    @Override public void clearConditionEvaluateRecords() throws android.os.RemoteException
    {
    }
    @Override public void setConditionEvaluateRecorderEnabled(boolean enable) throws android.os.RemoteException
    {
    }
    @Override public boolean isConditionEvaluateRecorderEnabled() throws android.os.RemoteException
    {
      return false;
    }
    // Pkg Set

    @Override public void addPkgSet(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper set) throws android.os.RemoteException
    {
    }
    @Override public void deletePkgSet(java.lang.String label) throws android.os.RemoteException
    {
    }
    @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getPkgSetByLabel(java.lang.String label) throws android.os.RemoteException
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
    @Override public void deleteGlobalVar(java.lang.String name) throws android.os.RemoteException
    {
    }
    @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getGlobalVarByName(java.lang.String name) throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllGlobalVars() throws android.os.RemoteException
    {
      return null;
    }
    @Override public void registerGlobalVarObs(tornaco.apps.shortx.core.rule.IGlobalVarObserver obs) throws android.os.RemoteException
    {
    }
    @Override public void unregisterGlobalVarObs(tornaco.apps.shortx.core.rule.IGlobalVarObserver obs) throws android.os.RemoteException
    {
    }
    // Global var end

    @Override public void reboot(java.lang.String reason) throws android.os.RemoteException
    {
    }
    @Override public java.lang.String executeMVEL(java.lang.String expression) throws android.os.RemoteException
    {
      return null;
    }
    // Condition start

    @Override public boolean evaluateCondition(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper condition) throws android.os.RemoteException
    {
      return false;
    }
    // Condition end
    // Verify binding.

    @Override public void vb(java.lang.String code, java.lang.String deviceId, tornaco.apps.shortx.core.ICallback cb) throws android.os.RemoteException
    {
    }
    // User

    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllUsers() throws android.os.RemoteException
    {
      return null;
    }
    // Dev settings

    @Override public void setPointerLocationEnabled(boolean enable) throws android.os.RemoteException
    {
    }
    @Override public boolean isPointerLocationEnabled() throws android.os.RemoteException
    {
      return false;
    }
    // Ext

    @Override public void directExecuteRuleActions(java.lang.String ruleId) throws android.os.RemoteException
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
        case TRANSACTION_getLogDir:
        {
          data.enforceInterface(descriptor);
          java.lang.String _result = this.getLogDir();
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        case TRANSACTION_hasFatalError:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.hasFatalError();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_writeLogsTo:
        {
          data.enforceInterface(descriptor);
          android.os.ParcelFileDescriptor _arg0;
          if ((0!=data.readInt())) {
            _arg0 = android.os.ParcelFileDescriptor.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.writeLogsTo(_arg0);
          reply.writeNoException();
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
        case TRANSACTION_getRuleCount:
        {
          data.enforceInterface(descriptor);
          int _result = this.getRuleCount();
          reply.writeNoException();
          reply.writeInt(_result);
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
        case TRANSACTION_getDirectActionCount:
        {
          data.enforceInterface(descriptor);
          int _result = this.getDirectActionCount();
          reply.writeNoException();
          reply.writeInt(_result);
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
        case TRANSACTION_executeAction:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.executeAction(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_executeActionBlocking:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.executeActionBlocking(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getActionEvaluateRecords:
        {
          data.enforceInterface(descriptor);
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getActionEvaluateRecords();
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_clearActionEvaluateRecords:
        {
          data.enforceInterface(descriptor);
          this.clearActionEvaluateRecords();
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_setActionEvaluateRecorderEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setActionEvaluateRecorderEnabled(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isActionEvaluateRecorderEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isActionEvaluateRecorderEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_getConditionEvaluateRecords:
        {
          data.enforceInterface(descriptor);
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getConditionEvaluateRecords();
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_clearConditionEvaluateRecords:
        {
          data.enforceInterface(descriptor);
          this.clearConditionEvaluateRecords();
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_setConditionEvaluateRecorderEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setConditionEvaluateRecorderEnabled(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isConditionEvaluateRecorderEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isConditionEvaluateRecorderEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
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
        case TRANSACTION_getPkgSetByLabel:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result = this.getPkgSetByLabel(_arg0);
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
        case TRANSACTION_getGlobalVarByName:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result = this.getGlobalVarByName(_arg0);
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
        case TRANSACTION_registerGlobalVarObs:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.IGlobalVarObserver _arg0;
          _arg0 = tornaco.apps.shortx.core.rule.IGlobalVarObserver.Stub.asInterface(data.readStrongBinder());
          this.registerGlobalVarObs(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_unregisterGlobalVarObs:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.IGlobalVarObserver _arg0;
          _arg0 = tornaco.apps.shortx.core.rule.IGlobalVarObserver.Stub.asInterface(data.readStrongBinder());
          this.unregisterGlobalVarObs(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_reboot:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.reboot(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_executeMVEL:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _result = this.executeMVEL(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        case TRANSACTION_evaluateCondition:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          boolean _result = this.evaluateCondition(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_vb:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          tornaco.apps.shortx.core.ICallback _arg2;
          _arg2 = tornaco.apps.shortx.core.ICallback.Stub.asInterface(data.readStrongBinder());
          this.vb(_arg0, _arg1, _arg2);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getAllUsers:
        {
          data.enforceInterface(descriptor);
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getAllUsers();
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_setPointerLocationEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setPointerLocationEnabled(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isPointerLocationEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isPointerLocationEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_directExecuteRuleActions:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.directExecuteRuleActions(_arg0);
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
      @Override public java.lang.String getLogDir() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getLogDir, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getLogDir();
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
      @Override public boolean hasFatalError() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_hasFatalError, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().hasFatalError();
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
      @Override public void writeLogsTo(android.os.ParcelFileDescriptor pfd) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((pfd!=null)) {
            _data.writeInt(1);
            pfd.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_writeLogsTo, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().writeLogsTo(pfd);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
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
      @Override public int getRuleCount() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getRuleCount, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getRuleCount();
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
      @Override public int getDirectActionCount() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getDirectActionCount, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getDirectActionCount();
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
      @Override public void executeAction(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper action) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((action!=null)) {
            _data.writeInt(1);
            action.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_executeAction, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().executeAction(action);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void executeActionBlocking(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper action) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((action!=null)) {
            _data.writeInt(1);
            action.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_executeActionBlocking, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().executeActionBlocking(action);
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

      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getActionEvaluateRecords() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getActionEvaluateRecords, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getActionEvaluateRecords();
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
      @Override public void clearActionEvaluateRecords() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_clearActionEvaluateRecords, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().clearActionEvaluateRecords();
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void setActionEvaluateRecorderEnabled(boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setActionEvaluateRecorderEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setActionEvaluateRecorderEnabled(enable);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean isActionEvaluateRecorderEnabled() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isActionEvaluateRecorderEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isActionEvaluateRecorderEnabled();
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
      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getConditionEvaluateRecords() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getConditionEvaluateRecords, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getConditionEvaluateRecords();
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
      @Override public void clearConditionEvaluateRecords() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_clearConditionEvaluateRecords, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().clearConditionEvaluateRecords();
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void setConditionEvaluateRecorderEnabled(boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setConditionEvaluateRecorderEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setConditionEvaluateRecorderEnabled(enable);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean isConditionEvaluateRecorderEnabled() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isConditionEvaluateRecorderEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isConditionEvaluateRecorderEnabled();
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
      @Override public void deletePkgSet(java.lang.String label) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(label);
          boolean _status = mRemote.transact(Stub.TRANSACTION_deletePkgSet, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().deletePkgSet(label);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getPkgSetByLabel(java.lang.String label) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(label);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getPkgSetByLabel, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getPkgSetByLabel(label);
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
      @Override public void deleteGlobalVar(java.lang.String name) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(name);
          boolean _status = mRemote.transact(Stub.TRANSACTION_deleteGlobalVar, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().deleteGlobalVar(name);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getGlobalVarByName(java.lang.String name) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(name);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getGlobalVarByName, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getGlobalVarByName(name);
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
      @Override public void registerGlobalVarObs(tornaco.apps.shortx.core.rule.IGlobalVarObserver obs) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder((((obs!=null))?(obs.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_registerGlobalVarObs, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().registerGlobalVarObs(obs);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void unregisterGlobalVarObs(tornaco.apps.shortx.core.rule.IGlobalVarObserver obs) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder((((obs!=null))?(obs.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_unregisterGlobalVarObs, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().unregisterGlobalVarObs(obs);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      // Global var end

      @Override public void reboot(java.lang.String reason) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(reason);
          boolean _status = mRemote.transact(Stub.TRANSACTION_reboot, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().reboot(reason);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public java.lang.String executeMVEL(java.lang.String expression) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(expression);
          boolean _status = mRemote.transact(Stub.TRANSACTION_executeMVEL, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().executeMVEL(expression);
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
      // Condition start

      @Override public boolean evaluateCondition(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper condition) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((condition!=null)) {
            _data.writeInt(1);
            condition.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_evaluateCondition, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().evaluateCondition(condition);
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
      // Condition end
      // Verify binding.

      @Override public void vb(java.lang.String code, java.lang.String deviceId, tornaco.apps.shortx.core.ICallback cb) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(code);
          _data.writeString(deviceId);
          _data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_vb, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().vb(code, deviceId, cb);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      // User

      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllUsers() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAllUsers, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getAllUsers();
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
      // Dev settings

      @Override public void setPointerLocationEnabled(boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setPointerLocationEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setPointerLocationEnabled(enable);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean isPointerLocationEnabled() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isPointerLocationEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isPointerLocationEnabled();
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
      // Ext

      @Override public void directExecuteRuleActions(java.lang.String ruleId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(ruleId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_directExecuteRuleActions, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().directExecuteRuleActions(ruleId);
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
    static final int TRANSACTION_setLogDebugEnable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_isLogDebugEnable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_getLogDir = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
    static final int TRANSACTION_hasFatalError = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
    static final int TRANSACTION_writeLogsTo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
    static final int TRANSACTION_addRule = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
    static final int TRANSACTION_deleteRule = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
    static final int TRANSACTION_getRuleById = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
    static final int TRANSACTION_setRuleEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
    static final int TRANSACTION_getAllRules = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
    static final int TRANSACTION_getRuleCount = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
    static final int TRANSACTION_getInstalledApps = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
    static final int TRANSACTION_getAppLabel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
    static final int TRANSACTION_pkgToApp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
    static final int TRANSACTION_showEdgeHint = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
    static final int TRANSACTION_addDirectAction = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
    static final int TRANSACTION_deleteDirectAction = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
    static final int TRANSACTION_getDirectActionById = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
    static final int TRANSACTION_getAllDirectAction = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
    static final int TRANSACTION_getDirectActionCount = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
    static final int TRANSACTION_executeDirectionActionById = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);
    static final int TRANSACTION_executeAction = (android.os.IBinder.FIRST_CALL_TRANSACTION + 23);
    static final int TRANSACTION_executeActionBlocking = (android.os.IBinder.FIRST_CALL_TRANSACTION + 24);
    static final int TRANSACTION_getActionEvaluateRecords = (android.os.IBinder.FIRST_CALL_TRANSACTION + 25);
    static final int TRANSACTION_clearActionEvaluateRecords = (android.os.IBinder.FIRST_CALL_TRANSACTION + 26);
    static final int TRANSACTION_setActionEvaluateRecorderEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 27);
    static final int TRANSACTION_isActionEvaluateRecorderEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 28);
    static final int TRANSACTION_getConditionEvaluateRecords = (android.os.IBinder.FIRST_CALL_TRANSACTION + 29);
    static final int TRANSACTION_clearConditionEvaluateRecords = (android.os.IBinder.FIRST_CALL_TRANSACTION + 30);
    static final int TRANSACTION_setConditionEvaluateRecorderEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 31);
    static final int TRANSACTION_isConditionEvaluateRecorderEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 32);
    static final int TRANSACTION_addPkgSet = (android.os.IBinder.FIRST_CALL_TRANSACTION + 33);
    static final int TRANSACTION_deletePkgSet = (android.os.IBinder.FIRST_CALL_TRANSACTION + 34);
    static final int TRANSACTION_getPkgSetByLabel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 35);
    static final int TRANSACTION_getAllPkgSets = (android.os.IBinder.FIRST_CALL_TRANSACTION + 36);
    static final int TRANSACTION_showDanmu = (android.os.IBinder.FIRST_CALL_TRANSACTION + 37);
    static final int TRANSACTION_getDanmuUISettings = (android.os.IBinder.FIRST_CALL_TRANSACTION + 38);
    static final int TRANSACTION_setDanmuUISettings = (android.os.IBinder.FIRST_CALL_TRANSACTION + 39);
    static final int TRANSACTION_getAppIcon = (android.os.IBinder.FIRST_CALL_TRANSACTION + 40);
    static final int TRANSACTION_getActiveJobs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 41);
    static final int TRANSACTION_cancelJobs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 42);
    static final int TRANSACTION_registerJobStatusListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 43);
    static final int TRANSACTION_unregisterJobStatusListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 44);
    static final int TRANSACTION_getActivities = (android.os.IBinder.FIRST_CALL_TRANSACTION + 45);
    static final int TRANSACTION_getReceivers = (android.os.IBinder.FIRST_CALL_TRANSACTION + 46);
    static final int TRANSACTION_getServices = (android.os.IBinder.FIRST_CALL_TRANSACTION + 47);
    static final int TRANSACTION_getProviders = (android.os.IBinder.FIRST_CALL_TRANSACTION + 48);
    static final int TRANSACTION_setComponentEnabledSetting = (android.os.IBinder.FIRST_CALL_TRANSACTION + 49);
    static final int TRANSACTION_getComponentEnabledSetting = (android.os.IBinder.FIRST_CALL_TRANSACTION + 50);
    static final int TRANSACTION_getAppComponentInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 51);
    static final int TRANSACTION_addGlobalVar = (android.os.IBinder.FIRST_CALL_TRANSACTION + 52);
    static final int TRANSACTION_deleteGlobalVar = (android.os.IBinder.FIRST_CALL_TRANSACTION + 53);
    static final int TRANSACTION_getGlobalVarByName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 54);
    static final int TRANSACTION_getAllGlobalVars = (android.os.IBinder.FIRST_CALL_TRANSACTION + 55);
    static final int TRANSACTION_registerGlobalVarObs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 56);
    static final int TRANSACTION_unregisterGlobalVarObs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 57);
    static final int TRANSACTION_reboot = (android.os.IBinder.FIRST_CALL_TRANSACTION + 58);
    static final int TRANSACTION_executeMVEL = (android.os.IBinder.FIRST_CALL_TRANSACTION + 59);
    static final int TRANSACTION_evaluateCondition = (android.os.IBinder.FIRST_CALL_TRANSACTION + 60);
    static final int TRANSACTION_vb = (android.os.IBinder.FIRST_CALL_TRANSACTION + 61);
    static final int TRANSACTION_getAllUsers = (android.os.IBinder.FIRST_CALL_TRANSACTION + 62);
    static final int TRANSACTION_setPointerLocationEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 63);
    static final int TRANSACTION_isPointerLocationEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 64);
    static final int TRANSACTION_directExecuteRuleActions = (android.os.IBinder.FIRST_CALL_TRANSACTION + 65);
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
  public java.lang.String getLogDir() throws android.os.RemoteException;
  public boolean hasFatalError() throws android.os.RemoteException;
  public void writeLogsTo(android.os.ParcelFileDescriptor pfd) throws android.os.RemoteException;
  // Rules start

  public void addRule(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper rule) throws android.os.RemoteException;
  public void deleteRule(java.lang.String id) throws android.os.RemoteException;
  public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getRuleById(java.lang.String id) throws android.os.RemoteException;
  public void setRuleEnabled(java.lang.String ruleId, boolean enable) throws android.os.RemoteException;
  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllRules() throws android.os.RemoteException;
  public int getRuleCount() throws android.os.RemoteException;
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
  public int getDirectActionCount() throws android.os.RemoteException;
  public void executeDirectionActionById(java.lang.String id) throws android.os.RemoteException;
  public void executeAction(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper action) throws android.os.RemoteException;
  public void executeActionBlocking(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper action) throws android.os.RemoteException;
  // Direct Actions end

  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getActionEvaluateRecords() throws android.os.RemoteException;
  public void clearActionEvaluateRecords() throws android.os.RemoteException;
  public void setActionEvaluateRecorderEnabled(boolean enable) throws android.os.RemoteException;
  public boolean isActionEvaluateRecorderEnabled() throws android.os.RemoteException;
  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getConditionEvaluateRecords() throws android.os.RemoteException;
  public void clearConditionEvaluateRecords() throws android.os.RemoteException;
  public void setConditionEvaluateRecorderEnabled(boolean enable) throws android.os.RemoteException;
  public boolean isConditionEvaluateRecorderEnabled() throws android.os.RemoteException;
  // Pkg Set

  public void addPkgSet(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper set) throws android.os.RemoteException;
  public void deletePkgSet(java.lang.String label) throws android.os.RemoteException;
  public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getPkgSetByLabel(java.lang.String label) throws android.os.RemoteException;
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
  public void deleteGlobalVar(java.lang.String name) throws android.os.RemoteException;
  public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getGlobalVarByName(java.lang.String name) throws android.os.RemoteException;
  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllGlobalVars() throws android.os.RemoteException;
  public void registerGlobalVarObs(tornaco.apps.shortx.core.rule.IGlobalVarObserver obs) throws android.os.RemoteException;
  public void unregisterGlobalVarObs(tornaco.apps.shortx.core.rule.IGlobalVarObserver obs) throws android.os.RemoteException;
  // Global var end

  public void reboot(java.lang.String reason) throws android.os.RemoteException;
  public java.lang.String executeMVEL(java.lang.String expression) throws android.os.RemoteException;
  // Condition start

  public boolean evaluateCondition(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper condition) throws android.os.RemoteException;
  // Condition end
  // Verify binding.

  public void vb(java.lang.String code, java.lang.String deviceId, tornaco.apps.shortx.core.ICallback cb) throws android.os.RemoteException;
  // User

  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllUsers() throws android.os.RemoteException;
  // Dev settings

  public void setPointerLocationEnabled(boolean enable) throws android.os.RemoteException;
  public boolean isPointerLocationEnabled() throws android.os.RemoteException;
  // Ext

  public void directExecuteRuleActions(java.lang.String ruleId) throws android.os.RemoteException;
}
