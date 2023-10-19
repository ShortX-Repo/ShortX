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
    @Override public void executeDirectionActionById(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper evaluateContext, java.lang.String id) throws android.os.RemoteException
    {
    }
    @Override public void executeDirectionAction(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper evaluateContext, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper da) throws android.os.RemoteException
    {
    }
    @Override public void executeAction(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper evaluateContext, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper action) throws android.os.RemoteException
    {
    }
    @Override public void executeActionBlocking(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper evaluateContext, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper action) throws android.os.RemoteException
    {
    }
    @Override public void directExecuteRuleActions(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper evaluateContext, java.lang.String ruleId) throws android.os.RemoteException
    {
    }
    // Direct Actions end
    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getActionEvaluateRecords() throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getConditionEvaluateRecords() throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getFactPublishRecords() throws android.os.RemoteException
    {
      return null;
    }
    @Override public void clearEvaluateRecords() throws android.os.RemoteException
    {
    }
    @Override public void setEvaluateRecordEnabled(boolean enable) throws android.os.RemoteException
    {
    }
    @Override public boolean isEvaluateRecordEnabled() throws android.os.RemoteException
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
    @Override public boolean evaluateCondition(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper evaluateContext, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper condition) throws android.os.RemoteException
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
    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getRunningServices(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper pkg) throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getRunningAppPkgs(java.lang.String pkgSetLabel) throws android.os.RemoteException
    {
      return null;
    }
    @Override public int getGestureAreaSize() throws android.os.RemoteException
    {
      return 0;
    }
    @Override public void setGestureAreaSize(int dp) throws android.os.RemoteException
    {
    }
    @Override public void registerPkgSetObs(tornaco.apps.shortx.core.rule.IPkgSetObserver obs) throws android.os.RemoteException
    {
    }
    @Override public void unregisterPkgSetObs(tornaco.apps.shortx.core.rule.IPkgSetObserver obs) throws android.os.RemoteException
    {
    }
    @Override public void registerRuleObs(tornaco.apps.shortx.core.rule.IRuleObserver obs) throws android.os.RemoteException
    {
    }
    @Override public void unregisterRuleObs(tornaco.apps.shortx.core.rule.IRuleObserver obs) throws android.os.RemoteException
    {
    }
    @Override public void registerDAObs(tornaco.apps.shortx.core.rule.IDAObserver obs) throws android.os.RemoteException
    {
    }
    @Override public void unregisterDAObs(tornaco.apps.shortx.core.rule.IDAObserver obs) throws android.os.RemoteException
    {
    }
    @Override public void setKeyEventPromptEnabled(boolean enable) throws android.os.RemoteException
    {
    }
    @Override public boolean isKeyEventPromptEnabled() throws android.os.RemoteException
    {
      return false;
    }
    @Override public void onMediaStoreInsert(android.net.Uri uri, java.lang.String path) throws android.os.RemoteException
    {
    }
    @Override public void onMediaStoreDelete(android.net.Uri uri, java.lang.String path) throws android.os.RemoteException
    {
    }
    @Override public java.lang.String executeShellCommand(java.lang.String command) throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.util.List<java.lang.String> getWifiScanResults() throws android.os.RemoteException
    {
      return null;
    }
    // Called by SystemUI
    @Override public void attachSUImpl(java.lang.String from, tornaco.apps.shortx.core.rule.action.su.ISu su) throws android.os.RemoteException
    {
    }
    @Override public boolean isInjectedShellEnabled() throws android.os.RemoteException
    {
      return false;
    }
    @Override public void setInjectedShellEnabled(boolean enable) throws android.os.RemoteException
    {
    }
    @Override public boolean isAudioRecording() throws android.os.RemoteException
    {
      return false;
    }
    @Override public void stopAudioRecording() throws android.os.RemoteException
    {
    }
    @Override public void registerAudioRecordingObs(tornaco.apps.shortx.core.IAudioRecordingListener obs) throws android.os.RemoteException
    {
    }
    @Override public void unregisterAudioRecordingObs(tornaco.apps.shortx.core.IAudioRecordingListener obs) throws android.os.RemoteException
    {
    }
    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllAudioRecordFiles() throws android.os.RemoteException
    {
      return null;
    }
    @Override public void deleteAudioRecordFile(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper file) throws android.os.RemoteException
    {
    }
    @Override public void exportAudioRecordFile(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper file, android.os.ParcelFileDescriptor pfd) throws android.os.RemoteException
    {
    }
    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getBTBondedDevices() throws android.os.RemoteException
    {
      return null;
    }
    // Called by SystemUI
    @Override public void attachNotificationPoster(java.lang.String from, tornaco.apps.shortx.core.notification.INotificationPoster poster) throws android.os.RemoteException
    {
    }
    // Error report
    @Override public void uncaughtException(java.lang.String error) throws android.os.RemoteException
    {
    }
    @Override public void crashSystemServer() throws android.os.RemoteException
    {
    }
    // Http test
    @Override public java.util.List<java.lang.String> executeHttpRequest(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper request) throws android.os.RemoteException
    {
      return null;
    }
    // Phone
    @Override public java.util.List<android.telephony.SubscriptionInfo> getActiveSubscriptionInfoList() throws android.os.RemoteException
    {
      return null;
    }
    // Gesture record
    @Override public void addGestureRecord(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper record) throws android.os.RemoteException
    {
    }
    @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getGestureRecordById(java.lang.String id) throws android.os.RemoteException
    {
      return null;
    }
    @Override public void deleteGestureRecordById(java.lang.String id) throws android.os.RemoteException
    {
    }
    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllGestureRecords(boolean withEventData) throws android.os.RemoteException
    {
      return null;
    }
    @Override public void injectGestureRecord(java.lang.String id, float speed, tornaco.apps.shortx.core.os.SynchronousResultReceiver onComplete) throws android.os.RemoteException
    {
    }
    @Override public void startGestureRecording() throws android.os.RemoteException
    {
    }
    @Override public void stopGestureRecording(boolean save) throws android.os.RemoteException
    {
    }
    @Override public boolean isGestureRecording() throws android.os.RemoteException
    {
      return false;
    }
    // Tile
    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllStatusBarTileSettings() throws android.os.RemoteException
    {
      return null;
    }
    @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getStatusBarTileSettingsByNumber(int tileNumber) throws android.os.RemoteException
    {
      return null;
    }
    @Override public void updateStatusBarTileSetting(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper tileSetting) throws android.os.RemoteException
    {
    }
    @Override public void registerStatusBarTileSettingsChangeListener(tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener listener) throws android.os.RemoteException
    {
    }
    @Override public void unregisterStatusBarTileSettingsChangeListener(tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener listener) throws android.os.RemoteException
    {
    }
    @Override public void onTileClick(int tileNumber) throws android.os.RemoteException
    {
    }
    @Override public boolean isQSTileOptEnabled() throws android.os.RemoteException
    {
      return false;
    }
    @Override public void setQSTileOptEnabled(boolean enable) throws android.os.RemoteException
    {
    }
    // Catcher
    @Override public boolean isSettingsCatcherEnabled() throws android.os.RemoteException
    {
      return false;
    }
    @Override public void setSettingsCatcherEnabled(boolean enabled) throws android.os.RemoteException
    {
    }
    @Override public void onReadSettings(java.lang.String url, java.lang.String value) throws android.os.RemoteException
    {
    }
    @Override public void onWriteSettings(java.lang.String url, java.lang.String value) throws android.os.RemoteException
    {
    }
    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getReadSettingsRecord() throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getWriteSettingsRecord() throws android.os.RemoteException
    {
      return null;
    }
    @Override public void clearWriteSettingsRecord() throws android.os.RemoteException
    {
    }
    @Override public void clearReadSettingsRecord() throws android.os.RemoteException
    {
    }
    @Override public void setRuleEngineEnabled(java.lang.String engine, boolean enable) throws android.os.RemoteException
    {
    }
    @Override public boolean isRuleEngineEnabled(java.lang.String engine) throws android.os.RemoteException
    {
      return false;
    }
    @Override public java.lang.String executeJS(java.lang.String expression) throws android.os.RemoteException
    {
      return null;
    }
    // Rule Set
    @Override public void addOrUpdateRuleSet(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper rs) throws android.os.RemoteException
    {
    }
    @Override public void deleteRuleSet(java.lang.String id) throws android.os.RemoteException
    {
    }
    @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getRuleSetById(java.lang.String id, boolean withRules) throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllRuleSets(boolean withRules) throws android.os.RemoteException
    {
      return null;
    }
    @Override public void addOrUpdateDASet(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper ds) throws android.os.RemoteException
    {
    }
    @Override public void deleteDASet(java.lang.String id) throws android.os.RemoteException
    {
    }
    @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getDASetById(java.lang.String id, boolean withDAs) throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllDASets(boolean withDAs) throws android.os.RemoteException
    {
      return null;
    }
    @Override public void testFATALError() throws android.os.RemoteException
    {
    }
    @Override public void attachQSTileHost(tornaco.apps.shortx.core.systemui.IQSTileHost host) throws android.os.RemoteException
    {
    }
    // QSTileList
    @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getAllQSTiles() throws android.os.RemoteException
    {
      return null;
    }
    // Swipe gesture.
    @Override public int getSwipeDistanceThresholdDp() throws android.os.RemoteException
    {
      return 0;
    }
    @Override public void setSwipeDistanceThresholdDp(int dp) throws android.os.RemoteException
    {
    }
    @Override public void log(java.lang.String message) throws android.os.RemoteException
    {
    }
    @Override public int getStreamMinVolume(int type) throws android.os.RemoteException
    {
      return 0;
    }
    @Override public int getStreamMaxVolume(int type) throws android.os.RemoteException
    {
      return 0;
    }
    @Override public int getStreamVolume(int type) throws android.os.RemoteException
    {
      return 0;
    }
    @Override public java.util.List<java.lang.String> getEvaluatingActions() throws android.os.RemoteException
    {
      return null;
    }
    @Override public void registerActionEvaluateListener(tornaco.apps.shortx.core.IActionEvaluateListener listener) throws android.os.RemoteException
    {
    }
    @Override public void unregisterActionEvaluateListener(tornaco.apps.shortx.core.IActionEvaluateListener listener) throws android.os.RemoteException
    {
    }
    @Override public void setStartActivityResult(int requestCode, int resultCode, android.os.Bundle result) throws android.os.RemoteException
    {
    }
    @Override public void shortcutAppLaunched(java.lang.String packageName) throws android.os.RemoteException
    {
    }
    // Global feature toggle.
    @Override public void setRuleFeatureEnabled(boolean enable) throws android.os.RemoteException
    {
    }
    @Override public boolean isRuleFeatureEnabled() throws android.os.RemoteException
    {
      return false;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements tornaco.apps.shortx.core.IShortX
  {
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
        case TRANSACTION_version:
        {
          tornaco.apps.shortx.core.Version _result = this.version();
          reply.writeNoException();
          _Parcel.writeTypedObject(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_fingerprint:
        {
          java.lang.String _result = this.fingerprint();
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_setLogDebugEnable:
        {
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setLogDebugEnable(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_isLogDebugEnable:
        {
          boolean _result = this.isLogDebugEnable();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_getLogDir:
        {
          java.lang.String _result = this.getLogDir();
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_hasFatalError:
        {
          boolean _result = this.hasFatalError();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_writeLogsTo:
        {
          android.os.ParcelFileDescriptor _arg0;
          _arg0 = _Parcel.readTypedObject(data, android.os.ParcelFileDescriptor.CREATOR);
          this.writeLogsTo(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_addRule:
        {
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          _arg0 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
          this.addRule(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_deleteRule:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.deleteRule(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_getRuleById:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result = this.getRuleById(_arg0);
          reply.writeNoException();
          _Parcel.writeTypedObject(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_setRuleEnabled:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          this.setRuleEnabled(_arg0, _arg1);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_getAllRules:
        {
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getAllRules();
          reply.writeNoException();
          _Parcel.writeTypedList(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_getRuleCount:
        {
          int _result = this.getRuleCount();
          reply.writeNoException();
          reply.writeInt(_result);
          break;
        }
        case TRANSACTION_getInstalledApps:
        {
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getInstalledApps();
          reply.writeNoException();
          _Parcel.writeTypedList(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_getAppLabel:
        {
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          _arg0 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
          java.lang.String _result = this.getAppLabel(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_pkgToApp:
        {
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          _arg0 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result = this.pkgToApp(_arg0);
          reply.writeNoException();
          _Parcel.writeTypedObject(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_showEdgeHint:
        {
          int _arg0;
          _arg0 = data.readInt();
          this.showEdgeHint(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_addDirectAction:
        {
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          _arg0 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
          this.addDirectAction(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_deleteDirectAction:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.deleteDirectAction(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_getDirectActionById:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result = this.getDirectActionById(_arg0);
          reply.writeNoException();
          _Parcel.writeTypedObject(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_getAllDirectAction:
        {
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getAllDirectAction();
          reply.writeNoException();
          _Parcel.writeTypedList(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_getDirectActionCount:
        {
          int _result = this.getDirectActionCount();
          reply.writeNoException();
          reply.writeInt(_result);
          break;
        }
        case TRANSACTION_executeDirectionActionById:
        {
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          _arg0 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
          java.lang.String _arg1;
          _arg1 = data.readString();
          this.executeDirectionActionById(_arg0, _arg1);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_executeDirectionAction:
        {
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          _arg0 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg1;
          _arg1 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
          this.executeDirectionAction(_arg0, _arg1);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_executeAction:
        {
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          _arg0 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg1;
          _arg1 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
          this.executeAction(_arg0, _arg1);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_executeActionBlocking:
        {
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          _arg0 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg1;
          _arg1 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
          this.executeActionBlocking(_arg0, _arg1);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_directExecuteRuleActions:
        {
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          _arg0 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
          java.lang.String _arg1;
          _arg1 = data.readString();
          this.directExecuteRuleActions(_arg0, _arg1);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_getActionEvaluateRecords:
        {
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getActionEvaluateRecords();
          reply.writeNoException();
          _Parcel.writeTypedList(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_getConditionEvaluateRecords:
        {
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getConditionEvaluateRecords();
          reply.writeNoException();
          _Parcel.writeTypedList(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_getFactPublishRecords:
        {
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getFactPublishRecords();
          reply.writeNoException();
          _Parcel.writeTypedList(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_clearEvaluateRecords:
        {
          this.clearEvaluateRecords();
          reply.writeNoException();
          break;
        }
        case TRANSACTION_setEvaluateRecordEnabled:
        {
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setEvaluateRecordEnabled(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_isEvaluateRecordEnabled:
        {
          boolean _result = this.isEvaluateRecordEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_addPkgSet:
        {
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          _arg0 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
          this.addPkgSet(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_deletePkgSet:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.deletePkgSet(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_getPkgSetByLabel:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result = this.getPkgSetByLabel(_arg0);
          reply.writeNoException();
          _Parcel.writeTypedObject(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_getAllPkgSets:
        {
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getAllPkgSets(_arg0);
          reply.writeNoException();
          _Parcel.writeTypedList(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_showDanmu:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          this.showDanmu(_arg0, _arg1);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_getDanmuUISettings:
        {
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result = this.getDanmuUISettings();
          reply.writeNoException();
          _Parcel.writeTypedObject(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_setDanmuUISettings:
        {
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          _arg0 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
          this.setDanmuUISettings(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_getAppIcon:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          int _arg1;
          _arg1 = data.readInt();
          android.graphics.Bitmap _result = this.getAppIcon(_arg0, _arg1);
          reply.writeNoException();
          _Parcel.writeTypedObject(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_getActiveJobs:
        {
          java.util.List<java.lang.String> _result = this.getActiveJobs();
          reply.writeNoException();
          reply.writeStringList(_result);
          break;
        }
        case TRANSACTION_cancelJobs:
        {
          java.util.List<java.lang.String> _arg0;
          _arg0 = data.createStringArrayList();
          this.cancelJobs(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_registerJobStatusListener:
        {
          tornaco.apps.shortx.core.IJobStatusListener _arg0;
          _arg0 = tornaco.apps.shortx.core.IJobStatusListener.Stub.asInterface(data.readStrongBinder());
          this.registerJobStatusListener(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_unregisterJobStatusListener:
        {
          tornaco.apps.shortx.core.IJobStatusListener _arg0;
          _arg0 = tornaco.apps.shortx.core.IJobStatusListener.Stub.asInterface(data.readStrongBinder());
          this.unregisterJobStatusListener(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_getActivities:
        {
          int _arg0;
          _arg0 = data.readInt();
          java.lang.String _arg1;
          _arg1 = data.readString();
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getActivities(_arg0, _arg1);
          reply.writeNoException();
          _Parcel.writeTypedList(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_getReceivers:
        {
          int _arg0;
          _arg0 = data.readInt();
          java.lang.String _arg1;
          _arg1 = data.readString();
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getReceivers(_arg0, _arg1);
          reply.writeNoException();
          _Parcel.writeTypedList(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_getServices:
        {
          int _arg0;
          _arg0 = data.readInt();
          java.lang.String _arg1;
          _arg1 = data.readString();
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getServices(_arg0, _arg1);
          reply.writeNoException();
          _Parcel.writeTypedList(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_getProviders:
        {
          int _arg0;
          _arg0 = data.readInt();
          java.lang.String _arg1;
          _arg1 = data.readString();
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getProviders(_arg0, _arg1);
          reply.writeNoException();
          _Parcel.writeTypedList(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_setComponentEnabledSetting:
        {
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          _arg0 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
          int _arg1;
          _arg1 = data.readInt();
          int _arg2;
          _arg2 = data.readInt();
          this.setComponentEnabledSetting(_arg0, _arg1, _arg2);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_getComponentEnabledSetting:
        {
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          _arg0 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
          int _result = this.getComponentEnabledSetting(_arg0);
          reply.writeNoException();
          reply.writeInt(_result);
          break;
        }
        case TRANSACTION_getAppComponentInfo:
        {
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          _arg0 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result = this.getAppComponentInfo(_arg0);
          reply.writeNoException();
          _Parcel.writeTypedObject(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_addGlobalVar:
        {
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          _arg0 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
          this.addGlobalVar(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_deleteGlobalVar:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.deleteGlobalVar(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_getGlobalVarByName:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result = this.getGlobalVarByName(_arg0);
          reply.writeNoException();
          _Parcel.writeTypedObject(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_getAllGlobalVars:
        {
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getAllGlobalVars();
          reply.writeNoException();
          _Parcel.writeTypedList(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_registerGlobalVarObs:
        {
          tornaco.apps.shortx.core.rule.IGlobalVarObserver _arg0;
          _arg0 = tornaco.apps.shortx.core.rule.IGlobalVarObserver.Stub.asInterface(data.readStrongBinder());
          this.registerGlobalVarObs(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_unregisterGlobalVarObs:
        {
          tornaco.apps.shortx.core.rule.IGlobalVarObserver _arg0;
          _arg0 = tornaco.apps.shortx.core.rule.IGlobalVarObserver.Stub.asInterface(data.readStrongBinder());
          this.unregisterGlobalVarObs(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_reboot:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.reboot(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_executeMVEL:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _result = this.executeMVEL(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_evaluateCondition:
        {
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          _arg0 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg1;
          _arg1 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
          boolean _result = this.evaluateCondition(_arg0, _arg1);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_vb:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          tornaco.apps.shortx.core.ICallback _arg2;
          _arg2 = tornaco.apps.shortx.core.ICallback.Stub.asInterface(data.readStrongBinder());
          this.vb(_arg0, _arg1, _arg2);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_getAllUsers:
        {
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getAllUsers();
          reply.writeNoException();
          _Parcel.writeTypedList(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_setPointerLocationEnabled:
        {
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setPointerLocationEnabled(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_isPointerLocationEnabled:
        {
          boolean _result = this.isPointerLocationEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_getRunningServices:
        {
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          _arg0 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getRunningServices(_arg0);
          reply.writeNoException();
          _Parcel.writeTypedList(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_getRunningAppPkgs:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getRunningAppPkgs(_arg0);
          reply.writeNoException();
          _Parcel.writeTypedList(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_getGestureAreaSize:
        {
          int _result = this.getGestureAreaSize();
          reply.writeNoException();
          reply.writeInt(_result);
          break;
        }
        case TRANSACTION_setGestureAreaSize:
        {
          int _arg0;
          _arg0 = data.readInt();
          this.setGestureAreaSize(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_registerPkgSetObs:
        {
          tornaco.apps.shortx.core.rule.IPkgSetObserver _arg0;
          _arg0 = tornaco.apps.shortx.core.rule.IPkgSetObserver.Stub.asInterface(data.readStrongBinder());
          this.registerPkgSetObs(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_unregisterPkgSetObs:
        {
          tornaco.apps.shortx.core.rule.IPkgSetObserver _arg0;
          _arg0 = tornaco.apps.shortx.core.rule.IPkgSetObserver.Stub.asInterface(data.readStrongBinder());
          this.unregisterPkgSetObs(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_registerRuleObs:
        {
          tornaco.apps.shortx.core.rule.IRuleObserver _arg0;
          _arg0 = tornaco.apps.shortx.core.rule.IRuleObserver.Stub.asInterface(data.readStrongBinder());
          this.registerRuleObs(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_unregisterRuleObs:
        {
          tornaco.apps.shortx.core.rule.IRuleObserver _arg0;
          _arg0 = tornaco.apps.shortx.core.rule.IRuleObserver.Stub.asInterface(data.readStrongBinder());
          this.unregisterRuleObs(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_registerDAObs:
        {
          tornaco.apps.shortx.core.rule.IDAObserver _arg0;
          _arg0 = tornaco.apps.shortx.core.rule.IDAObserver.Stub.asInterface(data.readStrongBinder());
          this.registerDAObs(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_unregisterDAObs:
        {
          tornaco.apps.shortx.core.rule.IDAObserver _arg0;
          _arg0 = tornaco.apps.shortx.core.rule.IDAObserver.Stub.asInterface(data.readStrongBinder());
          this.unregisterDAObs(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_setKeyEventPromptEnabled:
        {
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setKeyEventPromptEnabled(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_isKeyEventPromptEnabled:
        {
          boolean _result = this.isKeyEventPromptEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_onMediaStoreInsert:
        {
          android.net.Uri _arg0;
          _arg0 = _Parcel.readTypedObject(data, android.net.Uri.CREATOR);
          java.lang.String _arg1;
          _arg1 = data.readString();
          this.onMediaStoreInsert(_arg0, _arg1);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_onMediaStoreDelete:
        {
          android.net.Uri _arg0;
          _arg0 = _Parcel.readTypedObject(data, android.net.Uri.CREATOR);
          java.lang.String _arg1;
          _arg1 = data.readString();
          this.onMediaStoreDelete(_arg0, _arg1);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_executeShellCommand:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _result = this.executeShellCommand(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_getWifiScanResults:
        {
          java.util.List<java.lang.String> _result = this.getWifiScanResults();
          reply.writeNoException();
          reply.writeStringList(_result);
          break;
        }
        case TRANSACTION_attachSUImpl:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          tornaco.apps.shortx.core.rule.action.su.ISu _arg1;
          _arg1 = tornaco.apps.shortx.core.rule.action.su.ISu.Stub.asInterface(data.readStrongBinder());
          this.attachSUImpl(_arg0, _arg1);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_isInjectedShellEnabled:
        {
          boolean _result = this.isInjectedShellEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_setInjectedShellEnabled:
        {
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setInjectedShellEnabled(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_isAudioRecording:
        {
          boolean _result = this.isAudioRecording();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_stopAudioRecording:
        {
          this.stopAudioRecording();
          reply.writeNoException();
          break;
        }
        case TRANSACTION_registerAudioRecordingObs:
        {
          tornaco.apps.shortx.core.IAudioRecordingListener _arg0;
          _arg0 = tornaco.apps.shortx.core.IAudioRecordingListener.Stub.asInterface(data.readStrongBinder());
          this.registerAudioRecordingObs(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_unregisterAudioRecordingObs:
        {
          tornaco.apps.shortx.core.IAudioRecordingListener _arg0;
          _arg0 = tornaco.apps.shortx.core.IAudioRecordingListener.Stub.asInterface(data.readStrongBinder());
          this.unregisterAudioRecordingObs(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_getAllAudioRecordFiles:
        {
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getAllAudioRecordFiles();
          reply.writeNoException();
          _Parcel.writeTypedList(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_deleteAudioRecordFile:
        {
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          _arg0 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
          this.deleteAudioRecordFile(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_exportAudioRecordFile:
        {
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          _arg0 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
          android.os.ParcelFileDescriptor _arg1;
          _arg1 = _Parcel.readTypedObject(data, android.os.ParcelFileDescriptor.CREATOR);
          this.exportAudioRecordFile(_arg0, _arg1);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_getBTBondedDevices:
        {
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getBTBondedDevices();
          reply.writeNoException();
          _Parcel.writeTypedList(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_attachNotificationPoster:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          tornaco.apps.shortx.core.notification.INotificationPoster _arg1;
          _arg1 = tornaco.apps.shortx.core.notification.INotificationPoster.Stub.asInterface(data.readStrongBinder());
          this.attachNotificationPoster(_arg0, _arg1);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_uncaughtException:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.uncaughtException(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_crashSystemServer:
        {
          this.crashSystemServer();
          reply.writeNoException();
          break;
        }
        case TRANSACTION_executeHttpRequest:
        {
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          _arg0 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
          java.util.List<java.lang.String> _result = this.executeHttpRequest(_arg0);
          reply.writeNoException();
          reply.writeStringList(_result);
          break;
        }
        case TRANSACTION_getActiveSubscriptionInfoList:
        {
          java.util.List<android.telephony.SubscriptionInfo> _result = this.getActiveSubscriptionInfoList();
          reply.writeNoException();
          _Parcel.writeTypedList(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_addGestureRecord:
        {
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          _arg0 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
          this.addGestureRecord(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_getGestureRecordById:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result = this.getGestureRecordById(_arg0);
          reply.writeNoException();
          _Parcel.writeTypedObject(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_deleteGestureRecordById:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.deleteGestureRecordById(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_getAllGestureRecords:
        {
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getAllGestureRecords(_arg0);
          reply.writeNoException();
          _Parcel.writeTypedList(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_injectGestureRecord:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          float _arg1;
          _arg1 = data.readFloat();
          tornaco.apps.shortx.core.os.SynchronousResultReceiver _arg2;
          _arg2 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.os.SynchronousResultReceiver.CREATOR);
          this.injectGestureRecord(_arg0, _arg1, _arg2);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_startGestureRecording:
        {
          this.startGestureRecording();
          reply.writeNoException();
          break;
        }
        case TRANSACTION_stopGestureRecording:
        {
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.stopGestureRecording(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_isGestureRecording:
        {
          boolean _result = this.isGestureRecording();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_getAllStatusBarTileSettings:
        {
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getAllStatusBarTileSettings();
          reply.writeNoException();
          _Parcel.writeTypedList(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_getStatusBarTileSettingsByNumber:
        {
          int _arg0;
          _arg0 = data.readInt();
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result = this.getStatusBarTileSettingsByNumber(_arg0);
          reply.writeNoException();
          _Parcel.writeTypedObject(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_updateStatusBarTileSetting:
        {
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          _arg0 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
          this.updateStatusBarTileSetting(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_registerStatusBarTileSettingsChangeListener:
        {
          tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener _arg0;
          _arg0 = tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener.Stub.asInterface(data.readStrongBinder());
          this.registerStatusBarTileSettingsChangeListener(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_unregisterStatusBarTileSettingsChangeListener:
        {
          tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener _arg0;
          _arg0 = tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener.Stub.asInterface(data.readStrongBinder());
          this.unregisterStatusBarTileSettingsChangeListener(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_onTileClick:
        {
          int _arg0;
          _arg0 = data.readInt();
          this.onTileClick(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_isQSTileOptEnabled:
        {
          boolean _result = this.isQSTileOptEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_setQSTileOptEnabled:
        {
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setQSTileOptEnabled(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_isSettingsCatcherEnabled:
        {
          boolean _result = this.isSettingsCatcherEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_setSettingsCatcherEnabled:
        {
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setSettingsCatcherEnabled(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_onReadSettings:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          this.onReadSettings(_arg0, _arg1);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_onWriteSettings:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          this.onWriteSettings(_arg0, _arg1);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_getReadSettingsRecord:
        {
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getReadSettingsRecord();
          reply.writeNoException();
          _Parcel.writeTypedList(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_getWriteSettingsRecord:
        {
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getWriteSettingsRecord();
          reply.writeNoException();
          _Parcel.writeTypedList(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_clearWriteSettingsRecord:
        {
          this.clearWriteSettingsRecord();
          reply.writeNoException();
          break;
        }
        case TRANSACTION_clearReadSettingsRecord:
        {
          this.clearReadSettingsRecord();
          reply.writeNoException();
          break;
        }
        case TRANSACTION_setRuleEngineEnabled:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          this.setRuleEngineEnabled(_arg0, _arg1);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_isRuleEngineEnabled:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _result = this.isRuleEngineEnabled(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_executeJS:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _result = this.executeJS(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_addOrUpdateRuleSet:
        {
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          _arg0 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
          this.addOrUpdateRuleSet(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_deleteRuleSet:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.deleteRuleSet(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_getRuleSetById:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result = this.getRuleSetById(_arg0, _arg1);
          reply.writeNoException();
          _Parcel.writeTypedObject(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_getAllRuleSets:
        {
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getAllRuleSets(_arg0);
          reply.writeNoException();
          _Parcel.writeTypedList(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_addOrUpdateDASet:
        {
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          _arg0 = _Parcel.readTypedObject(data, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
          this.addOrUpdateDASet(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_deleteDASet:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.deleteDASet(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_getDASetById:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result = this.getDASetById(_arg0, _arg1);
          reply.writeNoException();
          _Parcel.writeTypedObject(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_getAllDASets:
        {
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getAllDASets(_arg0);
          reply.writeNoException();
          _Parcel.writeTypedList(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_testFATALError:
        {
          this.testFATALError();
          reply.writeNoException();
          break;
        }
        case TRANSACTION_attachQSTileHost:
        {
          tornaco.apps.shortx.core.systemui.IQSTileHost _arg0;
          _arg0 = tornaco.apps.shortx.core.systemui.IQSTileHost.Stub.asInterface(data.readStrongBinder());
          this.attachQSTileHost(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_getAllQSTiles:
        {
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result = this.getAllQSTiles();
          reply.writeNoException();
          _Parcel.writeTypedObject(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_getSwipeDistanceThresholdDp:
        {
          int _result = this.getSwipeDistanceThresholdDp();
          reply.writeNoException();
          reply.writeInt(_result);
          break;
        }
        case TRANSACTION_setSwipeDistanceThresholdDp:
        {
          int _arg0;
          _arg0 = data.readInt();
          this.setSwipeDistanceThresholdDp(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_log:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.log(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_getStreamMinVolume:
        {
          int _arg0;
          _arg0 = data.readInt();
          int _result = this.getStreamMinVolume(_arg0);
          reply.writeNoException();
          reply.writeInt(_result);
          break;
        }
        case TRANSACTION_getStreamMaxVolume:
        {
          int _arg0;
          _arg0 = data.readInt();
          int _result = this.getStreamMaxVolume(_arg0);
          reply.writeNoException();
          reply.writeInt(_result);
          break;
        }
        case TRANSACTION_getStreamVolume:
        {
          int _arg0;
          _arg0 = data.readInt();
          int _result = this.getStreamVolume(_arg0);
          reply.writeNoException();
          reply.writeInt(_result);
          break;
        }
        case TRANSACTION_getEvaluatingActions:
        {
          java.util.List<java.lang.String> _result = this.getEvaluatingActions();
          reply.writeNoException();
          reply.writeStringList(_result);
          break;
        }
        case TRANSACTION_registerActionEvaluateListener:
        {
          tornaco.apps.shortx.core.IActionEvaluateListener _arg0;
          _arg0 = tornaco.apps.shortx.core.IActionEvaluateListener.Stub.asInterface(data.readStrongBinder());
          this.registerActionEvaluateListener(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_unregisterActionEvaluateListener:
        {
          tornaco.apps.shortx.core.IActionEvaluateListener _arg0;
          _arg0 = tornaco.apps.shortx.core.IActionEvaluateListener.Stub.asInterface(data.readStrongBinder());
          this.unregisterActionEvaluateListener(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_setStartActivityResult:
        {
          int _arg0;
          _arg0 = data.readInt();
          int _arg1;
          _arg1 = data.readInt();
          android.os.Bundle _arg2;
          _arg2 = _Parcel.readTypedObject(data, android.os.Bundle.CREATOR);
          this.setStartActivityResult(_arg0, _arg1, _arg2);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_shortcutAppLaunched:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.shortcutAppLaunched(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_setRuleFeatureEnabled:
        {
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setRuleFeatureEnabled(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_isRuleFeatureEnabled:
        {
          boolean _result = this.isRuleFeatureEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
      return true;
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
          _reply.readException();
          _result = _Parcel.readTypedObject(_reply, tornaco.apps.shortx.core.Version.CREATOR);
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
          _Parcel.writeTypedObject(_data, pfd, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_writeLogsTo, _data, _reply, 0);
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
          _Parcel.writeTypedObject(_data, rule, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_addRule, _data, _reply, 0);
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
          _reply.readException();
          _result = _Parcel.readTypedObject(_reply, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
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
          _Parcel.writeTypedObject(_data, pkg, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAppLabel, _data, _reply, 0);
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
          _Parcel.writeTypedObject(_data, pkg, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_pkgToApp, _data, _reply, 0);
          _reply.readException();
          _result = _Parcel.readTypedObject(_reply, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
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
          _Parcel.writeTypedObject(_data, da, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_addDirectAction, _data, _reply, 0);
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
          _reply.readException();
          _result = _Parcel.readTypedObject(_reply, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
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
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void executeDirectionActionById(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper evaluateContext, java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _Parcel.writeTypedObject(_data, evaluateContext, 0);
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_executeDirectionActionById, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void executeDirectionAction(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper evaluateContext, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper da) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _Parcel.writeTypedObject(_data, evaluateContext, 0);
          _Parcel.writeTypedObject(_data, da, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_executeDirectionAction, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void executeAction(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper evaluateContext, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper action) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _Parcel.writeTypedObject(_data, evaluateContext, 0);
          _Parcel.writeTypedObject(_data, action, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_executeAction, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void executeActionBlocking(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper evaluateContext, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper action) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _Parcel.writeTypedObject(_data, evaluateContext, 0);
          _Parcel.writeTypedObject(_data, action, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_executeActionBlocking, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void directExecuteRuleActions(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper evaluateContext, java.lang.String ruleId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _Parcel.writeTypedObject(_data, evaluateContext, 0);
          _data.writeString(ruleId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_directExecuteRuleActions, _data, _reply, 0);
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
          _reply.readException();
          _result = _reply.createTypedArrayList(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
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
          _reply.readException();
          _result = _reply.createTypedArrayList(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getFactPublishRecords() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getFactPublishRecords, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createTypedArrayList(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void clearEvaluateRecords() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_clearEvaluateRecords, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void setEvaluateRecordEnabled(boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setEvaluateRecordEnabled, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean isEvaluateRecordEnabled() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isEvaluateRecordEnabled, _data, _reply, 0);
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
          _Parcel.writeTypedObject(_data, set, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_addPkgSet, _data, _reply, 0);
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
          _reply.readException();
          _result = _Parcel.readTypedObject(_reply, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
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
          _reply.readException();
          _result = _Parcel.readTypedObject(_reply, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
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
          _Parcel.writeTypedObject(_data, settings, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setDanmuUISettings, _data, _reply, 0);
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
          _reply.readException();
          _result = _Parcel.readTypedObject(_reply, android.graphics.Bitmap.CREATOR);
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
          _data.writeStrongInterface(listener);
          boolean _status = mRemote.transact(Stub.TRANSACTION_registerJobStatusListener, _data, _reply, 0);
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
          _data.writeStrongInterface(listener);
          boolean _status = mRemote.transact(Stub.TRANSACTION_unregisterJobStatusListener, _data, _reply, 0);
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
          _Parcel.writeTypedObject(_data, component, 0);
          _data.writeInt(newState);
          _data.writeInt(flags);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setComponentEnabledSetting, _data, _reply, 0);
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
          _Parcel.writeTypedObject(_data, component, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getComponentEnabledSetting, _data, _reply, 0);
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
          _Parcel.writeTypedObject(_data, component, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAppComponentInfo, _data, _reply, 0);
          _reply.readException();
          _result = _Parcel.readTypedObject(_reply, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
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
          _Parcel.writeTypedObject(_data, gv, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_addGlobalVar, _data, _reply, 0);
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
          _reply.readException();
          _result = _Parcel.readTypedObject(_reply, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
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
          _data.writeStrongInterface(obs);
          boolean _status = mRemote.transact(Stub.TRANSACTION_registerGlobalVarObs, _data, _reply, 0);
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
          _data.writeStrongInterface(obs);
          boolean _status = mRemote.transact(Stub.TRANSACTION_unregisterGlobalVarObs, _data, _reply, 0);
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
      @Override public boolean evaluateCondition(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper evaluateContext, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper condition) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _Parcel.writeTypedObject(_data, evaluateContext, 0);
          _Parcel.writeTypedObject(_data, condition, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_evaluateCondition, _data, _reply, 0);
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
          _data.writeStrongInterface(cb);
          boolean _status = mRemote.transact(Stub.TRANSACTION_vb, _data, _reply, 0);
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
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getRunningServices(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper pkg) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _Parcel.writeTypedObject(_data, pkg, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getRunningServices, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createTypedArrayList(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getRunningAppPkgs(java.lang.String pkgSetLabel) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(pkgSetLabel);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getRunningAppPkgs, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createTypedArrayList(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public int getGestureAreaSize() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getGestureAreaSize, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void setGestureAreaSize(int dp) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(dp);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setGestureAreaSize, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void registerPkgSetObs(tornaco.apps.shortx.core.rule.IPkgSetObserver obs) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongInterface(obs);
          boolean _status = mRemote.transact(Stub.TRANSACTION_registerPkgSetObs, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void unregisterPkgSetObs(tornaco.apps.shortx.core.rule.IPkgSetObserver obs) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongInterface(obs);
          boolean _status = mRemote.transact(Stub.TRANSACTION_unregisterPkgSetObs, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void registerRuleObs(tornaco.apps.shortx.core.rule.IRuleObserver obs) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongInterface(obs);
          boolean _status = mRemote.transact(Stub.TRANSACTION_registerRuleObs, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void unregisterRuleObs(tornaco.apps.shortx.core.rule.IRuleObserver obs) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongInterface(obs);
          boolean _status = mRemote.transact(Stub.TRANSACTION_unregisterRuleObs, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void registerDAObs(tornaco.apps.shortx.core.rule.IDAObserver obs) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongInterface(obs);
          boolean _status = mRemote.transact(Stub.TRANSACTION_registerDAObs, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void unregisterDAObs(tornaco.apps.shortx.core.rule.IDAObserver obs) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongInterface(obs);
          boolean _status = mRemote.transact(Stub.TRANSACTION_unregisterDAObs, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void setKeyEventPromptEnabled(boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setKeyEventPromptEnabled, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean isKeyEventPromptEnabled() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isKeyEventPromptEnabled, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void onMediaStoreInsert(android.net.Uri uri, java.lang.String path) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _Parcel.writeTypedObject(_data, uri, 0);
          _data.writeString(path);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onMediaStoreInsert, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void onMediaStoreDelete(android.net.Uri uri, java.lang.String path) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _Parcel.writeTypedObject(_data, uri, 0);
          _data.writeString(path);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onMediaStoreDelete, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public java.lang.String executeShellCommand(java.lang.String command) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(command);
          boolean _status = mRemote.transact(Stub.TRANSACTION_executeShellCommand, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.util.List<java.lang.String> getWifiScanResults() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<java.lang.String> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getWifiScanResults, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createStringArrayList();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      // Called by SystemUI
      @Override public void attachSUImpl(java.lang.String from, tornaco.apps.shortx.core.rule.action.su.ISu su) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(from);
          _data.writeStrongInterface(su);
          boolean _status = mRemote.transact(Stub.TRANSACTION_attachSUImpl, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean isInjectedShellEnabled() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isInjectedShellEnabled, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void setInjectedShellEnabled(boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setInjectedShellEnabled, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean isAudioRecording() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isAudioRecording, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void stopAudioRecording() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_stopAudioRecording, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void registerAudioRecordingObs(tornaco.apps.shortx.core.IAudioRecordingListener obs) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongInterface(obs);
          boolean _status = mRemote.transact(Stub.TRANSACTION_registerAudioRecordingObs, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void unregisterAudioRecordingObs(tornaco.apps.shortx.core.IAudioRecordingListener obs) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongInterface(obs);
          boolean _status = mRemote.transact(Stub.TRANSACTION_unregisterAudioRecordingObs, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllAudioRecordFiles() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAllAudioRecordFiles, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createTypedArrayList(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void deleteAudioRecordFile(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper file) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _Parcel.writeTypedObject(_data, file, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_deleteAudioRecordFile, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void exportAudioRecordFile(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper file, android.os.ParcelFileDescriptor pfd) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _Parcel.writeTypedObject(_data, file, 0);
          _Parcel.writeTypedObject(_data, pfd, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_exportAudioRecordFile, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getBTBondedDevices() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getBTBondedDevices, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createTypedArrayList(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      // Called by SystemUI
      @Override public void attachNotificationPoster(java.lang.String from, tornaco.apps.shortx.core.notification.INotificationPoster poster) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(from);
          _data.writeStrongInterface(poster);
          boolean _status = mRemote.transact(Stub.TRANSACTION_attachNotificationPoster, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      // Error report
      @Override public void uncaughtException(java.lang.String error) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(error);
          boolean _status = mRemote.transact(Stub.TRANSACTION_uncaughtException, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void crashSystemServer() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_crashSystemServer, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      // Http test
      @Override public java.util.List<java.lang.String> executeHttpRequest(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper request) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<java.lang.String> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _Parcel.writeTypedObject(_data, request, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_executeHttpRequest, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createStringArrayList();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      // Phone
      @Override public java.util.List<android.telephony.SubscriptionInfo> getActiveSubscriptionInfoList() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<android.telephony.SubscriptionInfo> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getActiveSubscriptionInfoList, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createTypedArrayList(android.telephony.SubscriptionInfo.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      // Gesture record
      @Override public void addGestureRecord(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper record) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _Parcel.writeTypedObject(_data, record, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_addGestureRecord, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getGestureRecordById(java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getGestureRecordById, _data, _reply, 0);
          _reply.readException();
          _result = _Parcel.readTypedObject(_reply, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void deleteGestureRecordById(java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_deleteGestureRecordById, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllGestureRecords(boolean withEventData) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((withEventData)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAllGestureRecords, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createTypedArrayList(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void injectGestureRecord(java.lang.String id, float speed, tornaco.apps.shortx.core.os.SynchronousResultReceiver onComplete) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          _data.writeFloat(speed);
          _Parcel.writeTypedObject(_data, onComplete, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_injectGestureRecord, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void startGestureRecording() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_startGestureRecording, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void stopGestureRecording(boolean save) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((save)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_stopGestureRecording, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean isGestureRecording() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isGestureRecording, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      // Tile
      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllStatusBarTileSettings() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAllStatusBarTileSettings, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createTypedArrayList(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getStatusBarTileSettingsByNumber(int tileNumber) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(tileNumber);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getStatusBarTileSettingsByNumber, _data, _reply, 0);
          _reply.readException();
          _result = _Parcel.readTypedObject(_reply, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void updateStatusBarTileSetting(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper tileSetting) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _Parcel.writeTypedObject(_data, tileSetting, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_updateStatusBarTileSetting, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void registerStatusBarTileSettingsChangeListener(tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener listener) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongInterface(listener);
          boolean _status = mRemote.transact(Stub.TRANSACTION_registerStatusBarTileSettingsChangeListener, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void unregisterStatusBarTileSettingsChangeListener(tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener listener) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongInterface(listener);
          boolean _status = mRemote.transact(Stub.TRANSACTION_unregisterStatusBarTileSettingsChangeListener, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void onTileClick(int tileNumber) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(tileNumber);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onTileClick, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean isQSTileOptEnabled() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isQSTileOptEnabled, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void setQSTileOptEnabled(boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setQSTileOptEnabled, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      // Catcher
      @Override public boolean isSettingsCatcherEnabled() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isSettingsCatcherEnabled, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void setSettingsCatcherEnabled(boolean enabled) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((enabled)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setSettingsCatcherEnabled, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void onReadSettings(java.lang.String url, java.lang.String value) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(url);
          _data.writeString(value);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onReadSettings, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void onWriteSettings(java.lang.String url, java.lang.String value) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(url);
          _data.writeString(value);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onWriteSettings, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getReadSettingsRecord() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getReadSettingsRecord, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createTypedArrayList(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getWriteSettingsRecord() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getWriteSettingsRecord, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createTypedArrayList(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void clearWriteSettingsRecord() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_clearWriteSettingsRecord, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void clearReadSettingsRecord() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_clearReadSettingsRecord, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void setRuleEngineEnabled(java.lang.String engine, boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(engine);
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setRuleEngineEnabled, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean isRuleEngineEnabled(java.lang.String engine) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(engine);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isRuleEngineEnabled, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.lang.String executeJS(java.lang.String expression) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(expression);
          boolean _status = mRemote.transact(Stub.TRANSACTION_executeJS, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      // Rule Set
      @Override public void addOrUpdateRuleSet(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper rs) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _Parcel.writeTypedObject(_data, rs, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_addOrUpdateRuleSet, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void deleteRuleSet(java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_deleteRuleSet, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getRuleSetById(java.lang.String id, boolean withRules) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          _data.writeInt(((withRules)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_getRuleSetById, _data, _reply, 0);
          _reply.readException();
          _result = _Parcel.readTypedObject(_reply, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllRuleSets(boolean withRules) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((withRules)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAllRuleSets, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createTypedArrayList(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void addOrUpdateDASet(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper ds) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _Parcel.writeTypedObject(_data, ds, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_addOrUpdateDASet, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void deleteDASet(java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_deleteDASet, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getDASetById(java.lang.String id, boolean withDAs) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          _data.writeInt(((withDAs)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_getDASetById, _data, _reply, 0);
          _reply.readException();
          _result = _Parcel.readTypedObject(_reply, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllDASets(boolean withDAs) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((withDAs)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAllDASets, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createTypedArrayList(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void testFATALError() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_testFATALError, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void attachQSTileHost(tornaco.apps.shortx.core.systemui.IQSTileHost host) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongInterface(host);
          boolean _status = mRemote.transact(Stub.TRANSACTION_attachQSTileHost, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      // QSTileList
      @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getAllQSTiles() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAllQSTiles, _data, _reply, 0);
          _reply.readException();
          _result = _Parcel.readTypedObject(_reply, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      // Swipe gesture.
      @Override public int getSwipeDistanceThresholdDp() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getSwipeDistanceThresholdDp, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void setSwipeDistanceThresholdDp(int dp) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(dp);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setSwipeDistanceThresholdDp, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void log(java.lang.String message) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(message);
          boolean _status = mRemote.transact(Stub.TRANSACTION_log, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public int getStreamMinVolume(int type) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(type);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getStreamMinVolume, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public int getStreamMaxVolume(int type) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(type);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getStreamMaxVolume, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public int getStreamVolume(int type) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(type);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getStreamVolume, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.util.List<java.lang.String> getEvaluatingActions() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<java.lang.String> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getEvaluatingActions, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createStringArrayList();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void registerActionEvaluateListener(tornaco.apps.shortx.core.IActionEvaluateListener listener) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongInterface(listener);
          boolean _status = mRemote.transact(Stub.TRANSACTION_registerActionEvaluateListener, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void unregisterActionEvaluateListener(tornaco.apps.shortx.core.IActionEvaluateListener listener) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongInterface(listener);
          boolean _status = mRemote.transact(Stub.TRANSACTION_unregisterActionEvaluateListener, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void setStartActivityResult(int requestCode, int resultCode, android.os.Bundle result) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(requestCode);
          _data.writeInt(resultCode);
          _Parcel.writeTypedObject(_data, result, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setStartActivityResult, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void shortcutAppLaunched(java.lang.String packageName) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(packageName);
          boolean _status = mRemote.transact(Stub.TRANSACTION_shortcutAppLaunched, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      // Global feature toggle.
      @Override public void setRuleFeatureEnabled(boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setRuleFeatureEnabled, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean isRuleFeatureEnabled() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isRuleFeatureEnabled, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
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
    static final int TRANSACTION_executeDirectionAction = (android.os.IBinder.FIRST_CALL_TRANSACTION + 23);
    static final int TRANSACTION_executeAction = (android.os.IBinder.FIRST_CALL_TRANSACTION + 24);
    static final int TRANSACTION_executeActionBlocking = (android.os.IBinder.FIRST_CALL_TRANSACTION + 25);
    static final int TRANSACTION_directExecuteRuleActions = (android.os.IBinder.FIRST_CALL_TRANSACTION + 26);
    static final int TRANSACTION_getActionEvaluateRecords = (android.os.IBinder.FIRST_CALL_TRANSACTION + 27);
    static final int TRANSACTION_getConditionEvaluateRecords = (android.os.IBinder.FIRST_CALL_TRANSACTION + 28);
    static final int TRANSACTION_getFactPublishRecords = (android.os.IBinder.FIRST_CALL_TRANSACTION + 29);
    static final int TRANSACTION_clearEvaluateRecords = (android.os.IBinder.FIRST_CALL_TRANSACTION + 30);
    static final int TRANSACTION_setEvaluateRecordEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 31);
    static final int TRANSACTION_isEvaluateRecordEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 32);
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
    static final int TRANSACTION_getRunningServices = (android.os.IBinder.FIRST_CALL_TRANSACTION + 65);
    static final int TRANSACTION_getRunningAppPkgs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 66);
    static final int TRANSACTION_getGestureAreaSize = (android.os.IBinder.FIRST_CALL_TRANSACTION + 67);
    static final int TRANSACTION_setGestureAreaSize = (android.os.IBinder.FIRST_CALL_TRANSACTION + 68);
    static final int TRANSACTION_registerPkgSetObs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 69);
    static final int TRANSACTION_unregisterPkgSetObs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 70);
    static final int TRANSACTION_registerRuleObs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 71);
    static final int TRANSACTION_unregisterRuleObs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 72);
    static final int TRANSACTION_registerDAObs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 73);
    static final int TRANSACTION_unregisterDAObs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 74);
    static final int TRANSACTION_setKeyEventPromptEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 75);
    static final int TRANSACTION_isKeyEventPromptEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 76);
    static final int TRANSACTION_onMediaStoreInsert = (android.os.IBinder.FIRST_CALL_TRANSACTION + 77);
    static final int TRANSACTION_onMediaStoreDelete = (android.os.IBinder.FIRST_CALL_TRANSACTION + 78);
    static final int TRANSACTION_executeShellCommand = (android.os.IBinder.FIRST_CALL_TRANSACTION + 79);
    static final int TRANSACTION_getWifiScanResults = (android.os.IBinder.FIRST_CALL_TRANSACTION + 80);
    static final int TRANSACTION_attachSUImpl = (android.os.IBinder.FIRST_CALL_TRANSACTION + 81);
    static final int TRANSACTION_isInjectedShellEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 82);
    static final int TRANSACTION_setInjectedShellEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 83);
    static final int TRANSACTION_isAudioRecording = (android.os.IBinder.FIRST_CALL_TRANSACTION + 84);
    static final int TRANSACTION_stopAudioRecording = (android.os.IBinder.FIRST_CALL_TRANSACTION + 85);
    static final int TRANSACTION_registerAudioRecordingObs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 86);
    static final int TRANSACTION_unregisterAudioRecordingObs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 87);
    static final int TRANSACTION_getAllAudioRecordFiles = (android.os.IBinder.FIRST_CALL_TRANSACTION + 88);
    static final int TRANSACTION_deleteAudioRecordFile = (android.os.IBinder.FIRST_CALL_TRANSACTION + 89);
    static final int TRANSACTION_exportAudioRecordFile = (android.os.IBinder.FIRST_CALL_TRANSACTION + 90);
    static final int TRANSACTION_getBTBondedDevices = (android.os.IBinder.FIRST_CALL_TRANSACTION + 91);
    static final int TRANSACTION_attachNotificationPoster = (android.os.IBinder.FIRST_CALL_TRANSACTION + 92);
    static final int TRANSACTION_uncaughtException = (android.os.IBinder.FIRST_CALL_TRANSACTION + 93);
    static final int TRANSACTION_crashSystemServer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 94);
    static final int TRANSACTION_executeHttpRequest = (android.os.IBinder.FIRST_CALL_TRANSACTION + 95);
    static final int TRANSACTION_getActiveSubscriptionInfoList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 96);
    static final int TRANSACTION_addGestureRecord = (android.os.IBinder.FIRST_CALL_TRANSACTION + 97);
    static final int TRANSACTION_getGestureRecordById = (android.os.IBinder.FIRST_CALL_TRANSACTION + 98);
    static final int TRANSACTION_deleteGestureRecordById = (android.os.IBinder.FIRST_CALL_TRANSACTION + 99);
    static final int TRANSACTION_getAllGestureRecords = (android.os.IBinder.FIRST_CALL_TRANSACTION + 100);
    static final int TRANSACTION_injectGestureRecord = (android.os.IBinder.FIRST_CALL_TRANSACTION + 101);
    static final int TRANSACTION_startGestureRecording = (android.os.IBinder.FIRST_CALL_TRANSACTION + 102);
    static final int TRANSACTION_stopGestureRecording = (android.os.IBinder.FIRST_CALL_TRANSACTION + 103);
    static final int TRANSACTION_isGestureRecording = (android.os.IBinder.FIRST_CALL_TRANSACTION + 104);
    static final int TRANSACTION_getAllStatusBarTileSettings = (android.os.IBinder.FIRST_CALL_TRANSACTION + 105);
    static final int TRANSACTION_getStatusBarTileSettingsByNumber = (android.os.IBinder.FIRST_CALL_TRANSACTION + 106);
    static final int TRANSACTION_updateStatusBarTileSetting = (android.os.IBinder.FIRST_CALL_TRANSACTION + 107);
    static final int TRANSACTION_registerStatusBarTileSettingsChangeListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 108);
    static final int TRANSACTION_unregisterStatusBarTileSettingsChangeListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 109);
    static final int TRANSACTION_onTileClick = (android.os.IBinder.FIRST_CALL_TRANSACTION + 110);
    static final int TRANSACTION_isQSTileOptEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 111);
    static final int TRANSACTION_setQSTileOptEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 112);
    static final int TRANSACTION_isSettingsCatcherEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 113);
    static final int TRANSACTION_setSettingsCatcherEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 114);
    static final int TRANSACTION_onReadSettings = (android.os.IBinder.FIRST_CALL_TRANSACTION + 115);
    static final int TRANSACTION_onWriteSettings = (android.os.IBinder.FIRST_CALL_TRANSACTION + 116);
    static final int TRANSACTION_getReadSettingsRecord = (android.os.IBinder.FIRST_CALL_TRANSACTION + 117);
    static final int TRANSACTION_getWriteSettingsRecord = (android.os.IBinder.FIRST_CALL_TRANSACTION + 118);
    static final int TRANSACTION_clearWriteSettingsRecord = (android.os.IBinder.FIRST_CALL_TRANSACTION + 119);
    static final int TRANSACTION_clearReadSettingsRecord = (android.os.IBinder.FIRST_CALL_TRANSACTION + 120);
    static final int TRANSACTION_setRuleEngineEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 121);
    static final int TRANSACTION_isRuleEngineEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 122);
    static final int TRANSACTION_executeJS = (android.os.IBinder.FIRST_CALL_TRANSACTION + 123);
    static final int TRANSACTION_addOrUpdateRuleSet = (android.os.IBinder.FIRST_CALL_TRANSACTION + 124);
    static final int TRANSACTION_deleteRuleSet = (android.os.IBinder.FIRST_CALL_TRANSACTION + 125);
    static final int TRANSACTION_getRuleSetById = (android.os.IBinder.FIRST_CALL_TRANSACTION + 126);
    static final int TRANSACTION_getAllRuleSets = (android.os.IBinder.FIRST_CALL_TRANSACTION + 127);
    static final int TRANSACTION_addOrUpdateDASet = (android.os.IBinder.FIRST_CALL_TRANSACTION + 128);
    static final int TRANSACTION_deleteDASet = (android.os.IBinder.FIRST_CALL_TRANSACTION + 129);
    static final int TRANSACTION_getDASetById = (android.os.IBinder.FIRST_CALL_TRANSACTION + 130);
    static final int TRANSACTION_getAllDASets = (android.os.IBinder.FIRST_CALL_TRANSACTION + 131);
    static final int TRANSACTION_testFATALError = (android.os.IBinder.FIRST_CALL_TRANSACTION + 132);
    static final int TRANSACTION_attachQSTileHost = (android.os.IBinder.FIRST_CALL_TRANSACTION + 133);
    static final int TRANSACTION_getAllQSTiles = (android.os.IBinder.FIRST_CALL_TRANSACTION + 134);
    static final int TRANSACTION_getSwipeDistanceThresholdDp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 135);
    static final int TRANSACTION_setSwipeDistanceThresholdDp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 136);
    static final int TRANSACTION_log = (android.os.IBinder.FIRST_CALL_TRANSACTION + 137);
    static final int TRANSACTION_getStreamMinVolume = (android.os.IBinder.FIRST_CALL_TRANSACTION + 138);
    static final int TRANSACTION_getStreamMaxVolume = (android.os.IBinder.FIRST_CALL_TRANSACTION + 139);
    static final int TRANSACTION_getStreamVolume = (android.os.IBinder.FIRST_CALL_TRANSACTION + 140);
    static final int TRANSACTION_getEvaluatingActions = (android.os.IBinder.FIRST_CALL_TRANSACTION + 141);
    static final int TRANSACTION_registerActionEvaluateListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 142);
    static final int TRANSACTION_unregisterActionEvaluateListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 143);
    static final int TRANSACTION_setStartActivityResult = (android.os.IBinder.FIRST_CALL_TRANSACTION + 144);
    static final int TRANSACTION_shortcutAppLaunched = (android.os.IBinder.FIRST_CALL_TRANSACTION + 145);
    static final int TRANSACTION_setRuleFeatureEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 146);
    static final int TRANSACTION_isRuleFeatureEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 147);
  }
  public static final java.lang.String DESCRIPTOR = "tornaco.apps.shortx.core.IShortX";
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
  public void executeDirectionActionById(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper evaluateContext, java.lang.String id) throws android.os.RemoteException;
  public void executeDirectionAction(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper evaluateContext, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper da) throws android.os.RemoteException;
  public void executeAction(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper evaluateContext, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper action) throws android.os.RemoteException;
  public void executeActionBlocking(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper evaluateContext, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper action) throws android.os.RemoteException;
  public void directExecuteRuleActions(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper evaluateContext, java.lang.String ruleId) throws android.os.RemoteException;
  // Direct Actions end
  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getActionEvaluateRecords() throws android.os.RemoteException;
  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getConditionEvaluateRecords() throws android.os.RemoteException;
  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getFactPublishRecords() throws android.os.RemoteException;
  public void clearEvaluateRecords() throws android.os.RemoteException;
  public void setEvaluateRecordEnabled(boolean enable) throws android.os.RemoteException;
  public boolean isEvaluateRecordEnabled() throws android.os.RemoteException;
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
  public boolean evaluateCondition(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper evaluateContext, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper condition) throws android.os.RemoteException;
  // Condition end
  // Verify binding.
  public void vb(java.lang.String code, java.lang.String deviceId, tornaco.apps.shortx.core.ICallback cb) throws android.os.RemoteException;
  // User
  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllUsers() throws android.os.RemoteException;
  // Dev settings
  public void setPointerLocationEnabled(boolean enable) throws android.os.RemoteException;
  public boolean isPointerLocationEnabled() throws android.os.RemoteException;
  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getRunningServices(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper pkg) throws android.os.RemoteException;
  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getRunningAppPkgs(java.lang.String pkgSetLabel) throws android.os.RemoteException;
  public int getGestureAreaSize() throws android.os.RemoteException;
  public void setGestureAreaSize(int dp) throws android.os.RemoteException;
  public void registerPkgSetObs(tornaco.apps.shortx.core.rule.IPkgSetObserver obs) throws android.os.RemoteException;
  public void unregisterPkgSetObs(tornaco.apps.shortx.core.rule.IPkgSetObserver obs) throws android.os.RemoteException;
  public void registerRuleObs(tornaco.apps.shortx.core.rule.IRuleObserver obs) throws android.os.RemoteException;
  public void unregisterRuleObs(tornaco.apps.shortx.core.rule.IRuleObserver obs) throws android.os.RemoteException;
  public void registerDAObs(tornaco.apps.shortx.core.rule.IDAObserver obs) throws android.os.RemoteException;
  public void unregisterDAObs(tornaco.apps.shortx.core.rule.IDAObserver obs) throws android.os.RemoteException;
  public void setKeyEventPromptEnabled(boolean enable) throws android.os.RemoteException;
  public boolean isKeyEventPromptEnabled() throws android.os.RemoteException;
  public void onMediaStoreInsert(android.net.Uri uri, java.lang.String path) throws android.os.RemoteException;
  public void onMediaStoreDelete(android.net.Uri uri, java.lang.String path) throws android.os.RemoteException;
  public java.lang.String executeShellCommand(java.lang.String command) throws android.os.RemoteException;
  public java.util.List<java.lang.String> getWifiScanResults() throws android.os.RemoteException;
  // Called by SystemUI
  public void attachSUImpl(java.lang.String from, tornaco.apps.shortx.core.rule.action.su.ISu su) throws android.os.RemoteException;
  public boolean isInjectedShellEnabled() throws android.os.RemoteException;
  public void setInjectedShellEnabled(boolean enable) throws android.os.RemoteException;
  public boolean isAudioRecording() throws android.os.RemoteException;
  public void stopAudioRecording() throws android.os.RemoteException;
  public void registerAudioRecordingObs(tornaco.apps.shortx.core.IAudioRecordingListener obs) throws android.os.RemoteException;
  public void unregisterAudioRecordingObs(tornaco.apps.shortx.core.IAudioRecordingListener obs) throws android.os.RemoteException;
  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllAudioRecordFiles() throws android.os.RemoteException;
  public void deleteAudioRecordFile(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper file) throws android.os.RemoteException;
  public void exportAudioRecordFile(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper file, android.os.ParcelFileDescriptor pfd) throws android.os.RemoteException;
  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getBTBondedDevices() throws android.os.RemoteException;
  // Called by SystemUI
  public void attachNotificationPoster(java.lang.String from, tornaco.apps.shortx.core.notification.INotificationPoster poster) throws android.os.RemoteException;
  // Error report
  public void uncaughtException(java.lang.String error) throws android.os.RemoteException;
  public void crashSystemServer() throws android.os.RemoteException;
  // Http test
  public java.util.List<java.lang.String> executeHttpRequest(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper request) throws android.os.RemoteException;
  // Phone
  public java.util.List<android.telephony.SubscriptionInfo> getActiveSubscriptionInfoList() throws android.os.RemoteException;
  // Gesture record
  public void addGestureRecord(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper record) throws android.os.RemoteException;
  public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getGestureRecordById(java.lang.String id) throws android.os.RemoteException;
  public void deleteGestureRecordById(java.lang.String id) throws android.os.RemoteException;
  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllGestureRecords(boolean withEventData) throws android.os.RemoteException;
  public void injectGestureRecord(java.lang.String id, float speed, tornaco.apps.shortx.core.os.SynchronousResultReceiver onComplete) throws android.os.RemoteException;
  public void startGestureRecording() throws android.os.RemoteException;
  public void stopGestureRecording(boolean save) throws android.os.RemoteException;
  public boolean isGestureRecording() throws android.os.RemoteException;
  // Tile
  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllStatusBarTileSettings() throws android.os.RemoteException;
  public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getStatusBarTileSettingsByNumber(int tileNumber) throws android.os.RemoteException;
  public void updateStatusBarTileSetting(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper tileSetting) throws android.os.RemoteException;
  public void registerStatusBarTileSettingsChangeListener(tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener listener) throws android.os.RemoteException;
  public void unregisterStatusBarTileSettingsChangeListener(tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener listener) throws android.os.RemoteException;
  public void onTileClick(int tileNumber) throws android.os.RemoteException;
  public boolean isQSTileOptEnabled() throws android.os.RemoteException;
  public void setQSTileOptEnabled(boolean enable) throws android.os.RemoteException;
  // Catcher
  public boolean isSettingsCatcherEnabled() throws android.os.RemoteException;
  public void setSettingsCatcherEnabled(boolean enabled) throws android.os.RemoteException;
  public void onReadSettings(java.lang.String url, java.lang.String value) throws android.os.RemoteException;
  public void onWriteSettings(java.lang.String url, java.lang.String value) throws android.os.RemoteException;
  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getReadSettingsRecord() throws android.os.RemoteException;
  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getWriteSettingsRecord() throws android.os.RemoteException;
  public void clearWriteSettingsRecord() throws android.os.RemoteException;
  public void clearReadSettingsRecord() throws android.os.RemoteException;
  public void setRuleEngineEnabled(java.lang.String engine, boolean enable) throws android.os.RemoteException;
  public boolean isRuleEngineEnabled(java.lang.String engine) throws android.os.RemoteException;
  public java.lang.String executeJS(java.lang.String expression) throws android.os.RemoteException;
  // Rule Set
  public void addOrUpdateRuleSet(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper rs) throws android.os.RemoteException;
  public void deleteRuleSet(java.lang.String id) throws android.os.RemoteException;
  public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getRuleSetById(java.lang.String id, boolean withRules) throws android.os.RemoteException;
  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllRuleSets(boolean withRules) throws android.os.RemoteException;
  public void addOrUpdateDASet(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper ds) throws android.os.RemoteException;
  public void deleteDASet(java.lang.String id) throws android.os.RemoteException;
  public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getDASetById(java.lang.String id, boolean withDAs) throws android.os.RemoteException;
  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllDASets(boolean withDAs) throws android.os.RemoteException;
  public void testFATALError() throws android.os.RemoteException;
  public void attachQSTileHost(tornaco.apps.shortx.core.systemui.IQSTileHost host) throws android.os.RemoteException;
  // QSTileList
  public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getAllQSTiles() throws android.os.RemoteException;
  // Swipe gesture.
  public int getSwipeDistanceThresholdDp() throws android.os.RemoteException;
  public void setSwipeDistanceThresholdDp(int dp) throws android.os.RemoteException;
  public void log(java.lang.String message) throws android.os.RemoteException;
  public int getStreamMinVolume(int type) throws android.os.RemoteException;
  public int getStreamMaxVolume(int type) throws android.os.RemoteException;
  public int getStreamVolume(int type) throws android.os.RemoteException;
  public java.util.List<java.lang.String> getEvaluatingActions() throws android.os.RemoteException;
  public void registerActionEvaluateListener(tornaco.apps.shortx.core.IActionEvaluateListener listener) throws android.os.RemoteException;
  public void unregisterActionEvaluateListener(tornaco.apps.shortx.core.IActionEvaluateListener listener) throws android.os.RemoteException;
  public void setStartActivityResult(int requestCode, int resultCode, android.os.Bundle result) throws android.os.RemoteException;
  public void shortcutAppLaunched(java.lang.String packageName) throws android.os.RemoteException;
  // Global feature toggle.
  public void setRuleFeatureEnabled(boolean enable) throws android.os.RemoteException;
  public boolean isRuleFeatureEnabled() throws android.os.RemoteException;
  /** @hide */
  static class _Parcel {
    static private <T> T readTypedObject(
        android.os.Parcel parcel,
        android.os.Parcelable.Creator<T> c) {
      if (parcel.readInt() != 0) {
          return c.createFromParcel(parcel);
      } else {
          return null;
      }
    }
    static private <T extends android.os.Parcelable> void writeTypedObject(
        android.os.Parcel parcel, T value, int parcelableFlags) {
      if (value != null) {
        parcel.writeInt(1);
        value.writeToParcel(parcel, parcelableFlags);
      } else {
        parcel.writeInt(0);
      }
    }
    static private <T extends android.os.Parcelable> void writeTypedList(
        android.os.Parcel parcel, java.util.List<T> value, int parcelableFlags) {
      if (value == null) {
        parcel.writeInt(-1);
      } else {
        int N = value.size();
        int i = 0;
        parcel.writeInt(N);
        while (i < N) {
    writeTypedObject(parcel, value.get(i), parcelableFlags);
          i++;
        }
      }
    }
  }
}
