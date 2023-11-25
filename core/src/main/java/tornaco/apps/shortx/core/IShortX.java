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
    @Override public void setEvaluateRecordEnabled(java.lang.String id, boolean enable) throws android.os.RemoteException
    {
    }
    @Override public boolean isEvaluateRecordEnabled(java.lang.String id) throws android.os.RemoteException
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
    @Override public void reportFATALError(java.lang.String errorMessage) throws android.os.RemoteException
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
    // Sensor usage

    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> querySensorUsages() throws android.os.RemoteException
    {
      return null;
    }
    @Override public void registerSensorUsageListener(tornaco.apps.shortx.core.ISensorUsageListener listener) throws android.os.RemoteException
    {
    }
    @Override public void unregisterSensorUsageListener(tornaco.apps.shortx.core.ISensorUsageListener listener) throws android.os.RemoteException
    {
    }
    // N

    @Override public void setPostNByAppEnabled(boolean enable) throws android.os.RemoteException
    {
    }
    @Override public boolean isPostNByAppEnabled() throws android.os.RemoteException
    {
      return false;
    }
    @Override public void installApk(java.lang.String packageName, android.os.ParcelFileDescriptor fd, tornaco.apps.shortx.core.ICallback onSuccess, tornaco.apps.shortx.core.ICallback onFailure) throws android.os.RemoteException
    {
    }
    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getInstalledShortcutApps() throws android.os.RemoteException
    {
      return null;
    }
    // Http request log.

    @Override public void setHttpRequestLogEnabled(boolean enabled) throws android.os.RemoteException
    {
    }
    @Override public boolean isHttpRequestLogEnabled() throws android.os.RemoteException
    {
      return false;
    }
    @Override public android.os.ParcelFileDescriptor getHttpRequestLogFD() throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.lang.String getHttpRequestLogPath() throws android.os.RemoteException
    {
      return null;
    }
    @Override public void clearHttpRequestLogs() throws android.os.RemoteException
    {
    }
    // Toggle start

    @Override public void addToggle(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper toggle) throws android.os.RemoteException
    {
    }
    @Override public void setToggleEnabled(java.lang.String id, boolean enable) throws android.os.RemoteException
    {
    }
    @Override public boolean isToggleEnabled(java.lang.String id) throws android.os.RemoteException
    {
      return false;
    }
    @Override public void deleteToggle(java.lang.String id) throws android.os.RemoteException
    {
    }
    @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getToggleById(java.lang.String id) throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllToggles() throws android.os.RemoteException
    {
      return null;
    }
    @Override public int getToggleCount() throws android.os.RemoteException
    {
      return 0;
    }
    @Override public void registerToggleObs(tornaco.apps.shortx.core.rule.IToggleObserver obs) throws android.os.RemoteException
    {
    }
    @Override public void unregisterToggleObs(tornaco.apps.shortx.core.rule.IToggleObserver obs) throws android.os.RemoteException
    {
    }
    // Toggle end
    // Flags, used to disable hook

    @Override public boolean hasHookFlags(java.lang.String flag) throws android.os.RemoteException
    {
      return false;
    }
    @Override public void addHookFlags(java.lang.String flag) throws android.os.RemoteException
    {
    }
    @Override public void removeHookFlags(java.lang.String flag) throws android.os.RemoteException
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
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          java.lang.String _arg1;
          _arg1 = data.readString();
          this.executeDirectionActionById(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_executeDirectionAction:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg1;
          if ((0!=data.readInt())) {
            _arg1 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg1 = null;
          }
          this.executeDirectionAction(_arg0, _arg1);
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
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg1;
          if ((0!=data.readInt())) {
            _arg1 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg1 = null;
          }
          this.executeAction(_arg0, _arg1);
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
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg1;
          if ((0!=data.readInt())) {
            _arg1 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg1 = null;
          }
          this.executeActionBlocking(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_directExecuteRuleActions:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          java.lang.String _arg1;
          _arg1 = data.readString();
          this.directExecuteRuleActions(_arg0, _arg1);
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
        case TRANSACTION_getConditionEvaluateRecords:
        {
          data.enforceInterface(descriptor);
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getConditionEvaluateRecords();
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_getFactPublishRecords:
        {
          data.enforceInterface(descriptor);
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getFactPublishRecords();
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_clearEvaluateRecords:
        {
          data.enforceInterface(descriptor);
          this.clearEvaluateRecords();
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_setEvaluateRecordEnabled:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          this.setEvaluateRecordEnabled(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isEvaluateRecordEnabled:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _result = this.isEvaluateRecordEnabled(_arg0);
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
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg1;
          if ((0!=data.readInt())) {
            _arg1 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg1 = null;
          }
          boolean _result = this.evaluateCondition(_arg0, _arg1);
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
        case TRANSACTION_getRunningServices:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getRunningServices(_arg0);
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_getRunningAppPkgs:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getRunningAppPkgs(_arg0);
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_getGestureAreaSize:
        {
          data.enforceInterface(descriptor);
          int _result = this.getGestureAreaSize();
          reply.writeNoException();
          reply.writeInt(_result);
          return true;
        }
        case TRANSACTION_setGestureAreaSize:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          this.setGestureAreaSize(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_registerPkgSetObs:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.IPkgSetObserver _arg0;
          _arg0 = tornaco.apps.shortx.core.rule.IPkgSetObserver.Stub.asInterface(data.readStrongBinder());
          this.registerPkgSetObs(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_unregisterPkgSetObs:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.IPkgSetObserver _arg0;
          _arg0 = tornaco.apps.shortx.core.rule.IPkgSetObserver.Stub.asInterface(data.readStrongBinder());
          this.unregisterPkgSetObs(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_registerRuleObs:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.IRuleObserver _arg0;
          _arg0 = tornaco.apps.shortx.core.rule.IRuleObserver.Stub.asInterface(data.readStrongBinder());
          this.registerRuleObs(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_unregisterRuleObs:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.IRuleObserver _arg0;
          _arg0 = tornaco.apps.shortx.core.rule.IRuleObserver.Stub.asInterface(data.readStrongBinder());
          this.unregisterRuleObs(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_registerDAObs:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.IDAObserver _arg0;
          _arg0 = tornaco.apps.shortx.core.rule.IDAObserver.Stub.asInterface(data.readStrongBinder());
          this.registerDAObs(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_unregisterDAObs:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.IDAObserver _arg0;
          _arg0 = tornaco.apps.shortx.core.rule.IDAObserver.Stub.asInterface(data.readStrongBinder());
          this.unregisterDAObs(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_setKeyEventPromptEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setKeyEventPromptEnabled(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isKeyEventPromptEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isKeyEventPromptEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_onMediaStoreInsert:
        {
          data.enforceInterface(descriptor);
          android.net.Uri _arg0;
          if ((0!=data.readInt())) {
            _arg0 = android.net.Uri.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          java.lang.String _arg1;
          _arg1 = data.readString();
          this.onMediaStoreInsert(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_onMediaStoreDelete:
        {
          data.enforceInterface(descriptor);
          android.net.Uri _arg0;
          if ((0!=data.readInt())) {
            _arg0 = android.net.Uri.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          java.lang.String _arg1;
          _arg1 = data.readString();
          this.onMediaStoreDelete(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_executeShellCommand:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _result = this.executeShellCommand(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        case TRANSACTION_getWifiScanResults:
        {
          data.enforceInterface(descriptor);
          java.util.List<java.lang.String> _result = this.getWifiScanResults();
          reply.writeNoException();
          reply.writeStringList(_result);
          return true;
        }
        case TRANSACTION_attachSUImpl:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          tornaco.apps.shortx.core.rule.action.su.ISu _arg1;
          _arg1 = tornaco.apps.shortx.core.rule.action.su.ISu.Stub.asInterface(data.readStrongBinder());
          this.attachSUImpl(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isInjectedShellEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isInjectedShellEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_setInjectedShellEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setInjectedShellEnabled(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isAudioRecording:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isAudioRecording();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_stopAudioRecording:
        {
          data.enforceInterface(descriptor);
          this.stopAudioRecording();
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_registerAudioRecordingObs:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.IAudioRecordingListener _arg0;
          _arg0 = tornaco.apps.shortx.core.IAudioRecordingListener.Stub.asInterface(data.readStrongBinder());
          this.registerAudioRecordingObs(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_unregisterAudioRecordingObs:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.IAudioRecordingListener _arg0;
          _arg0 = tornaco.apps.shortx.core.IAudioRecordingListener.Stub.asInterface(data.readStrongBinder());
          this.unregisterAudioRecordingObs(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getAllAudioRecordFiles:
        {
          data.enforceInterface(descriptor);
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getAllAudioRecordFiles();
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_deleteAudioRecordFile:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.deleteAudioRecordFile(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_exportAudioRecordFile:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          android.os.ParcelFileDescriptor _arg1;
          if ((0!=data.readInt())) {
            _arg1 = android.os.ParcelFileDescriptor.CREATOR.createFromParcel(data);
          }
          else {
            _arg1 = null;
          }
          this.exportAudioRecordFile(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getBTBondedDevices:
        {
          data.enforceInterface(descriptor);
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getBTBondedDevices();
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_attachNotificationPoster:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          tornaco.apps.shortx.core.notification.INotificationPoster _arg1;
          _arg1 = tornaco.apps.shortx.core.notification.INotificationPoster.Stub.asInterface(data.readStrongBinder());
          this.attachNotificationPoster(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_uncaughtException:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.uncaughtException(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_crashSystemServer:
        {
          data.enforceInterface(descriptor);
          this.crashSystemServer();
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_executeHttpRequest:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          java.util.List<java.lang.String> _result = this.executeHttpRequest(_arg0);
          reply.writeNoException();
          reply.writeStringList(_result);
          return true;
        }
        case TRANSACTION_getActiveSubscriptionInfoList:
        {
          data.enforceInterface(descriptor);
          java.util.List<android.telephony.SubscriptionInfo> _result = this.getActiveSubscriptionInfoList();
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_addGestureRecord:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.addGestureRecord(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getGestureRecordById:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result = this.getGestureRecordById(_arg0);
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
        case TRANSACTION_deleteGestureRecordById:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.deleteGestureRecordById(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getAllGestureRecords:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getAllGestureRecords(_arg0);
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_injectGestureRecord:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          float _arg1;
          _arg1 = data.readFloat();
          tornaco.apps.shortx.core.os.SynchronousResultReceiver _arg2;
          if ((0!=data.readInt())) {
            _arg2 = tornaco.apps.shortx.core.os.SynchronousResultReceiver.CREATOR.createFromParcel(data);
          }
          else {
            _arg2 = null;
          }
          this.injectGestureRecord(_arg0, _arg1, _arg2);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_startGestureRecording:
        {
          data.enforceInterface(descriptor);
          this.startGestureRecording();
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_stopGestureRecording:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.stopGestureRecording(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isGestureRecording:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isGestureRecording();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_getAllStatusBarTileSettings:
        {
          data.enforceInterface(descriptor);
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getAllStatusBarTileSettings();
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_getStatusBarTileSettingsByNumber:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result = this.getStatusBarTileSettingsByNumber(_arg0);
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
        case TRANSACTION_updateStatusBarTileSetting:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.updateStatusBarTileSetting(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_registerStatusBarTileSettingsChangeListener:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener _arg0;
          _arg0 = tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener.Stub.asInterface(data.readStrongBinder());
          this.registerStatusBarTileSettingsChangeListener(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_unregisterStatusBarTileSettingsChangeListener:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener _arg0;
          _arg0 = tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener.Stub.asInterface(data.readStrongBinder());
          this.unregisterStatusBarTileSettingsChangeListener(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_onTileClick:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          this.onTileClick(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isQSTileOptEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isQSTileOptEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_setQSTileOptEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setQSTileOptEnabled(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isSettingsCatcherEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isSettingsCatcherEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_setSettingsCatcherEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setSettingsCatcherEnabled(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_onReadSettings:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          this.onReadSettings(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_onWriteSettings:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          this.onWriteSettings(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getReadSettingsRecord:
        {
          data.enforceInterface(descriptor);
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getReadSettingsRecord();
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_getWriteSettingsRecord:
        {
          data.enforceInterface(descriptor);
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getWriteSettingsRecord();
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_clearWriteSettingsRecord:
        {
          data.enforceInterface(descriptor);
          this.clearWriteSettingsRecord();
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_clearReadSettingsRecord:
        {
          data.enforceInterface(descriptor);
          this.clearReadSettingsRecord();
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_setRuleEngineEnabled:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          this.setRuleEngineEnabled(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isRuleEngineEnabled:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _result = this.isRuleEngineEnabled(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_executeJS:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _result = this.executeJS(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        case TRANSACTION_addOrUpdateRuleSet:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.addOrUpdateRuleSet(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_deleteRuleSet:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.deleteRuleSet(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getRuleSetById:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result = this.getRuleSetById(_arg0, _arg1);
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
        case TRANSACTION_getAllRuleSets:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getAllRuleSets(_arg0);
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_addOrUpdateDASet:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.addOrUpdateDASet(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_deleteDASet:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.deleteDASet(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getDASetById:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result = this.getDASetById(_arg0, _arg1);
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
        case TRANSACTION_getAllDASets:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getAllDASets(_arg0);
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_reportFATALError:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.reportFATALError(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_attachQSTileHost:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.systemui.IQSTileHost _arg0;
          _arg0 = tornaco.apps.shortx.core.systemui.IQSTileHost.Stub.asInterface(data.readStrongBinder());
          this.attachQSTileHost(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getAllQSTiles:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result = this.getAllQSTiles();
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
        case TRANSACTION_getSwipeDistanceThresholdDp:
        {
          data.enforceInterface(descriptor);
          int _result = this.getSwipeDistanceThresholdDp();
          reply.writeNoException();
          reply.writeInt(_result);
          return true;
        }
        case TRANSACTION_setSwipeDistanceThresholdDp:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          this.setSwipeDistanceThresholdDp(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_log:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.log(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getStreamMinVolume:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          int _result = this.getStreamMinVolume(_arg0);
          reply.writeNoException();
          reply.writeInt(_result);
          return true;
        }
        case TRANSACTION_getStreamMaxVolume:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          int _result = this.getStreamMaxVolume(_arg0);
          reply.writeNoException();
          reply.writeInt(_result);
          return true;
        }
        case TRANSACTION_getStreamVolume:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          int _result = this.getStreamVolume(_arg0);
          reply.writeNoException();
          reply.writeInt(_result);
          return true;
        }
        case TRANSACTION_getEvaluatingActions:
        {
          data.enforceInterface(descriptor);
          java.util.List<java.lang.String> _result = this.getEvaluatingActions();
          reply.writeNoException();
          reply.writeStringList(_result);
          return true;
        }
        case TRANSACTION_registerActionEvaluateListener:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.IActionEvaluateListener _arg0;
          _arg0 = tornaco.apps.shortx.core.IActionEvaluateListener.Stub.asInterface(data.readStrongBinder());
          this.registerActionEvaluateListener(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_unregisterActionEvaluateListener:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.IActionEvaluateListener _arg0;
          _arg0 = tornaco.apps.shortx.core.IActionEvaluateListener.Stub.asInterface(data.readStrongBinder());
          this.unregisterActionEvaluateListener(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_setStartActivityResult:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          int _arg1;
          _arg1 = data.readInt();
          android.os.Bundle _arg2;
          if ((0!=data.readInt())) {
            _arg2 = android.os.Bundle.CREATOR.createFromParcel(data);
          }
          else {
            _arg2 = null;
          }
          this.setStartActivityResult(_arg0, _arg1, _arg2);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_shortcutAppLaunched:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.shortcutAppLaunched(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_setRuleFeatureEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setRuleFeatureEnabled(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isRuleFeatureEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isRuleFeatureEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_querySensorUsages:
        {
          data.enforceInterface(descriptor);
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.querySensorUsages();
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_registerSensorUsageListener:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.ISensorUsageListener _arg0;
          _arg0 = tornaco.apps.shortx.core.ISensorUsageListener.Stub.asInterface(data.readStrongBinder());
          this.registerSensorUsageListener(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_unregisterSensorUsageListener:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.ISensorUsageListener _arg0;
          _arg0 = tornaco.apps.shortx.core.ISensorUsageListener.Stub.asInterface(data.readStrongBinder());
          this.unregisterSensorUsageListener(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_setPostNByAppEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setPostNByAppEnabled(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isPostNByAppEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isPostNByAppEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_installApk:
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
          tornaco.apps.shortx.core.ICallback _arg2;
          _arg2 = tornaco.apps.shortx.core.ICallback.Stub.asInterface(data.readStrongBinder());
          tornaco.apps.shortx.core.ICallback _arg3;
          _arg3 = tornaco.apps.shortx.core.ICallback.Stub.asInterface(data.readStrongBinder());
          this.installApk(_arg0, _arg1, _arg2, _arg3);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getInstalledShortcutApps:
        {
          data.enforceInterface(descriptor);
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getInstalledShortcutApps();
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_setHttpRequestLogEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setHttpRequestLogEnabled(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isHttpRequestLogEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isHttpRequestLogEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_getHttpRequestLogFD:
        {
          data.enforceInterface(descriptor);
          android.os.ParcelFileDescriptor _result = this.getHttpRequestLogFD();
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
        case TRANSACTION_getHttpRequestLogPath:
        {
          data.enforceInterface(descriptor);
          java.lang.String _result = this.getHttpRequestLogPath();
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        case TRANSACTION_clearHttpRequestLogs:
        {
          data.enforceInterface(descriptor);
          this.clearHttpRequestLogs();
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_addToggle:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _arg0;
          if ((0!=data.readInt())) {
            _arg0 = tornaco.apps.shortx.core.rule.action.ByteArrayWrapper.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.addToggle(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_setToggleEnabled:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          this.setToggleEnabled(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isToggleEnabled:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _result = this.isToggleEnabled(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_deleteToggle:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.deleteToggle(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getToggleById:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result = this.getToggleById(_arg0);
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
        case TRANSACTION_getAllToggles:
        {
          data.enforceInterface(descriptor);
          java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result = this.getAllToggles();
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_getToggleCount:
        {
          data.enforceInterface(descriptor);
          int _result = this.getToggleCount();
          reply.writeNoException();
          reply.writeInt(_result);
          return true;
        }
        case TRANSACTION_registerToggleObs:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.IToggleObserver _arg0;
          _arg0 = tornaco.apps.shortx.core.rule.IToggleObserver.Stub.asInterface(data.readStrongBinder());
          this.registerToggleObs(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_unregisterToggleObs:
        {
          data.enforceInterface(descriptor);
          tornaco.apps.shortx.core.rule.IToggleObserver _arg0;
          _arg0 = tornaco.apps.shortx.core.rule.IToggleObserver.Stub.asInterface(data.readStrongBinder());
          this.unregisterToggleObs(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_hasHookFlags:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _result = this.hasHookFlags(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_addHookFlags:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.addHookFlags(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_removeHookFlags:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.removeHookFlags(_arg0);
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
      @Override public void executeDirectionActionById(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper evaluateContext, java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((evaluateContext!=null)) {
            _data.writeInt(1);
            evaluateContext.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_executeDirectionActionById, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().executeDirectionActionById(evaluateContext, id);
            return;
          }
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
          if ((evaluateContext!=null)) {
            _data.writeInt(1);
            evaluateContext.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          if ((da!=null)) {
            _data.writeInt(1);
            da.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_executeDirectionAction, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().executeDirectionAction(evaluateContext, da);
            return;
          }
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
          if ((evaluateContext!=null)) {
            _data.writeInt(1);
            evaluateContext.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          if ((action!=null)) {
            _data.writeInt(1);
            action.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_executeAction, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().executeAction(evaluateContext, action);
            return;
          }
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
          if ((evaluateContext!=null)) {
            _data.writeInt(1);
            evaluateContext.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          if ((action!=null)) {
            _data.writeInt(1);
            action.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_executeActionBlocking, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().executeActionBlocking(evaluateContext, action);
            return;
          }
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
          if ((evaluateContext!=null)) {
            _data.writeInt(1);
            evaluateContext.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          _data.writeString(ruleId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_directExecuteRuleActions, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().directExecuteRuleActions(evaluateContext, ruleId);
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
      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getFactPublishRecords() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getFactPublishRecords, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getFactPublishRecords();
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
      @Override public void clearEvaluateRecords() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_clearEvaluateRecords, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().clearEvaluateRecords();
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void setEvaluateRecordEnabled(java.lang.String id, boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setEvaluateRecordEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setEvaluateRecordEnabled(id, enable);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean isEvaluateRecordEnabled(java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isEvaluateRecordEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isEvaluateRecordEnabled(id);
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

      @Override public boolean evaluateCondition(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper evaluateContext, tornaco.apps.shortx.core.rule.action.ByteArrayWrapper condition) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((evaluateContext!=null)) {
            _data.writeInt(1);
            evaluateContext.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          if ((condition!=null)) {
            _data.writeInt(1);
            condition.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_evaluateCondition, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().evaluateCondition(evaluateContext, condition);
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
      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getRunningServices(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper pkg) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((pkg!=null)) {
            _data.writeInt(1);
            pkg.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_getRunningServices, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getRunningServices(pkg);
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
      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getRunningAppPkgs(java.lang.String pkgSetLabel) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(pkgSetLabel);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getRunningAppPkgs, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getRunningAppPkgs(pkgSetLabel);
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
      @Override public int getGestureAreaSize() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getGestureAreaSize, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getGestureAreaSize();
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
      @Override public void setGestureAreaSize(int dp) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(dp);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setGestureAreaSize, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setGestureAreaSize(dp);
            return;
          }
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
          _data.writeStrongBinder((((obs!=null))?(obs.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_registerPkgSetObs, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().registerPkgSetObs(obs);
            return;
          }
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
          _data.writeStrongBinder((((obs!=null))?(obs.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_unregisterPkgSetObs, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().unregisterPkgSetObs(obs);
            return;
          }
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
          _data.writeStrongBinder((((obs!=null))?(obs.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_registerRuleObs, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().registerRuleObs(obs);
            return;
          }
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
          _data.writeStrongBinder((((obs!=null))?(obs.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_unregisterRuleObs, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().unregisterRuleObs(obs);
            return;
          }
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
          _data.writeStrongBinder((((obs!=null))?(obs.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_registerDAObs, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().registerDAObs(obs);
            return;
          }
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
          _data.writeStrongBinder((((obs!=null))?(obs.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_unregisterDAObs, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().unregisterDAObs(obs);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setKeyEventPromptEnabled(enable);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isKeyEventPromptEnabled();
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
      @Override public void onMediaStoreInsert(android.net.Uri uri, java.lang.String path) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((uri!=null)) {
            _data.writeInt(1);
            uri.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          _data.writeString(path);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onMediaStoreInsert, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onMediaStoreInsert(uri, path);
            return;
          }
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
          if ((uri!=null)) {
            _data.writeInt(1);
            uri.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          _data.writeString(path);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onMediaStoreDelete, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onMediaStoreDelete(uri, path);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().executeShellCommand(command);
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
      @Override public java.util.List<java.lang.String> getWifiScanResults() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<java.lang.String> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getWifiScanResults, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getWifiScanResults();
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
      // Called by SystemUI

      @Override public void attachSUImpl(java.lang.String from, tornaco.apps.shortx.core.rule.action.su.ISu su) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(from);
          _data.writeStrongBinder((((su!=null))?(su.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_attachSUImpl, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().attachSUImpl(from, su);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isInjectedShellEnabled();
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
      @Override public void setInjectedShellEnabled(boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setInjectedShellEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setInjectedShellEnabled(enable);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isAudioRecording();
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
      @Override public void stopAudioRecording() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_stopAudioRecording, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().stopAudioRecording();
            return;
          }
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
          _data.writeStrongBinder((((obs!=null))?(obs.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_registerAudioRecordingObs, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().registerAudioRecordingObs(obs);
            return;
          }
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
          _data.writeStrongBinder((((obs!=null))?(obs.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_unregisterAudioRecordingObs, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().unregisterAudioRecordingObs(obs);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getAllAudioRecordFiles();
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
      @Override public void deleteAudioRecordFile(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper file) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((file!=null)) {
            _data.writeInt(1);
            file.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_deleteAudioRecordFile, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().deleteAudioRecordFile(file);
            return;
          }
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
          if ((file!=null)) {
            _data.writeInt(1);
            file.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          if ((pfd!=null)) {
            _data.writeInt(1);
            pfd.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_exportAudioRecordFile, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().exportAudioRecordFile(file, pfd);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getBTBondedDevices();
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
      // Called by SystemUI

      @Override public void attachNotificationPoster(java.lang.String from, tornaco.apps.shortx.core.notification.INotificationPoster poster) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(from);
          _data.writeStrongBinder((((poster!=null))?(poster.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_attachNotificationPoster, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().attachNotificationPoster(from, poster);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().uncaughtException(error);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().crashSystemServer();
            return;
          }
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
          if ((request!=null)) {
            _data.writeInt(1);
            request.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_executeHttpRequest, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().executeHttpRequest(request);
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
      // Phone

      @Override public java.util.List<android.telephony.SubscriptionInfo> getActiveSubscriptionInfoList() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<android.telephony.SubscriptionInfo> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getActiveSubscriptionInfoList, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getActiveSubscriptionInfoList();
          }
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
          if ((record!=null)) {
            _data.writeInt(1);
            record.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_addGestureRecord, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().addGestureRecord(record);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getGestureRecordById(id);
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
      @Override public void deleteGestureRecordById(java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_deleteGestureRecordById, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().deleteGestureRecordById(id);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getAllGestureRecords(withEventData);
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
      @Override public void injectGestureRecord(java.lang.String id, float speed, tornaco.apps.shortx.core.os.SynchronousResultReceiver onComplete) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          _data.writeFloat(speed);
          if ((onComplete!=null)) {
            _data.writeInt(1);
            onComplete.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_injectGestureRecord, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().injectGestureRecord(id, speed, onComplete);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().startGestureRecording();
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().stopGestureRecording(save);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isGestureRecording();
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
      // Tile

      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllStatusBarTileSettings() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAllStatusBarTileSettings, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getAllStatusBarTileSettings();
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
      @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getStatusBarTileSettingsByNumber(int tileNumber) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(tileNumber);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getStatusBarTileSettingsByNumber, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getStatusBarTileSettingsByNumber(tileNumber);
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
      @Override public void updateStatusBarTileSetting(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper tileSetting) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((tileSetting!=null)) {
            _data.writeInt(1);
            tileSetting.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_updateStatusBarTileSetting, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().updateStatusBarTileSetting(tileSetting);
            return;
          }
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
          _data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_registerStatusBarTileSettingsChangeListener, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().registerStatusBarTileSettingsChangeListener(listener);
            return;
          }
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
          _data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_unregisterStatusBarTileSettingsChangeListener, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().unregisterStatusBarTileSettingsChangeListener(listener);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onTileClick(tileNumber);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isQSTileOptEnabled();
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
      @Override public void setQSTileOptEnabled(boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setQSTileOptEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setQSTileOptEnabled(enable);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isSettingsCatcherEnabled();
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
      @Override public void setSettingsCatcherEnabled(boolean enabled) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((enabled)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setSettingsCatcherEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setSettingsCatcherEnabled(enabled);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onReadSettings(url, value);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onWriteSettings(url, value);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getReadSettingsRecord();
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
      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getWriteSettingsRecord() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getWriteSettingsRecord, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getWriteSettingsRecord();
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
      @Override public void clearWriteSettingsRecord() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_clearWriteSettingsRecord, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().clearWriteSettingsRecord();
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().clearReadSettingsRecord();
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setRuleEngineEnabled(engine, enable);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isRuleEngineEnabled(engine);
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
      @Override public java.lang.String executeJS(java.lang.String expression) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(expression);
          boolean _status = mRemote.transact(Stub.TRANSACTION_executeJS, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().executeJS(expression);
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
      // Rule Set

      @Override public void addOrUpdateRuleSet(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper rs) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((rs!=null)) {
            _data.writeInt(1);
            rs.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_addOrUpdateRuleSet, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().addOrUpdateRuleSet(rs);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().deleteRuleSet(id);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getRuleSetById(id, withRules);
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
      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllRuleSets(boolean withRules) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((withRules)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAllRuleSets, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getAllRuleSets(withRules);
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
      @Override public void addOrUpdateDASet(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper ds) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((ds!=null)) {
            _data.writeInt(1);
            ds.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_addOrUpdateDASet, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().addOrUpdateDASet(ds);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().deleteDASet(id);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getDASetById(id, withDAs);
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
      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllDASets(boolean withDAs) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((withDAs)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAllDASets, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getAllDASets(withDAs);
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
      @Override public void reportFATALError(java.lang.String errorMessage) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(errorMessage);
          boolean _status = mRemote.transact(Stub.TRANSACTION_reportFATALError, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().reportFATALError(errorMessage);
            return;
          }
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
          _data.writeStrongBinder((((host!=null))?(host.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_attachQSTileHost, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().attachQSTileHost(host);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getAllQSTiles();
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
      // Swipe gesture.

      @Override public int getSwipeDistanceThresholdDp() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getSwipeDistanceThresholdDp, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getSwipeDistanceThresholdDp();
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
      @Override public void setSwipeDistanceThresholdDp(int dp) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(dp);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setSwipeDistanceThresholdDp, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setSwipeDistanceThresholdDp(dp);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().log(message);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getStreamMinVolume(type);
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
      @Override public int getStreamMaxVolume(int type) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(type);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getStreamMaxVolume, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getStreamMaxVolume(type);
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
      @Override public int getStreamVolume(int type) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(type);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getStreamVolume, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getStreamVolume(type);
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
      @Override public java.util.List<java.lang.String> getEvaluatingActions() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<java.lang.String> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getEvaluatingActions, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getEvaluatingActions();
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
      @Override public void registerActionEvaluateListener(tornaco.apps.shortx.core.IActionEvaluateListener listener) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_registerActionEvaluateListener, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().registerActionEvaluateListener(listener);
            return;
          }
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
          _data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_unregisterActionEvaluateListener, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().unregisterActionEvaluateListener(listener);
            return;
          }
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
          if ((result!=null)) {
            _data.writeInt(1);
            result.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_setStartActivityResult, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setStartActivityResult(requestCode, resultCode, result);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().shortcutAppLaunched(packageName);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setRuleFeatureEnabled(enable);
            return;
          }
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
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isRuleFeatureEnabled();
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
      // Sensor usage

      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> querySensorUsages() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_querySensorUsages, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().querySensorUsages();
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
      @Override public void registerSensorUsageListener(tornaco.apps.shortx.core.ISensorUsageListener listener) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_registerSensorUsageListener, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().registerSensorUsageListener(listener);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void unregisterSensorUsageListener(tornaco.apps.shortx.core.ISensorUsageListener listener) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_unregisterSensorUsageListener, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().unregisterSensorUsageListener(listener);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      // N

      @Override public void setPostNByAppEnabled(boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setPostNByAppEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setPostNByAppEnabled(enable);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean isPostNByAppEnabled() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isPostNByAppEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isPostNByAppEnabled();
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
      @Override public void installApk(java.lang.String packageName, android.os.ParcelFileDescriptor fd, tornaco.apps.shortx.core.ICallback onSuccess, tornaco.apps.shortx.core.ICallback onFailure) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(packageName);
          if ((fd!=null)) {
            _data.writeInt(1);
            fd.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          _data.writeStrongBinder((((onSuccess!=null))?(onSuccess.asBinder()):(null)));
          _data.writeStrongBinder((((onFailure!=null))?(onFailure.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_installApk, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().installApk(packageName, fd, onSuccess, onFailure);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getInstalledShortcutApps() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getInstalledShortcutApps, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getInstalledShortcutApps();
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
      // Http request log.

      @Override public void setHttpRequestLogEnabled(boolean enabled) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((enabled)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setHttpRequestLogEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setHttpRequestLogEnabled(enabled);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean isHttpRequestLogEnabled() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isHttpRequestLogEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isHttpRequestLogEnabled();
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
      @Override public android.os.ParcelFileDescriptor getHttpRequestLogFD() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.os.ParcelFileDescriptor _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getHttpRequestLogFD, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getHttpRequestLogFD();
          }
          _reply.readException();
          if ((0!=_reply.readInt())) {
            _result = android.os.ParcelFileDescriptor.CREATOR.createFromParcel(_reply);
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
      @Override public java.lang.String getHttpRequestLogPath() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getHttpRequestLogPath, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getHttpRequestLogPath();
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
      @Override public void clearHttpRequestLogs() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_clearHttpRequestLogs, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().clearHttpRequestLogs();
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      // Toggle start

      @Override public void addToggle(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper toggle) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((toggle!=null)) {
            _data.writeInt(1);
            toggle.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_addToggle, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().addToggle(toggle);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void setToggleEnabled(java.lang.String id, boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setToggleEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setToggleEnabled(id, enable);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean isToggleEnabled(java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isToggleEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isToggleEnabled(id);
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
      @Override public void deleteToggle(java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_deleteToggle, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().deleteToggle(id);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getToggleById(java.lang.String id) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        tornaco.apps.shortx.core.rule.action.ByteArrayWrapper _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getToggleById, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getToggleById(id);
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
      @Override public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllToggles() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAllToggles, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getAllToggles();
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
      @Override public int getToggleCount() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getToggleCount, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getToggleCount();
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
      @Override public void registerToggleObs(tornaco.apps.shortx.core.rule.IToggleObserver obs) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder((((obs!=null))?(obs.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_registerToggleObs, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().registerToggleObs(obs);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void unregisterToggleObs(tornaco.apps.shortx.core.rule.IToggleObserver obs) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder((((obs!=null))?(obs.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_unregisterToggleObs, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().unregisterToggleObs(obs);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      // Toggle end
      // Flags, used to disable hook

      @Override public boolean hasHookFlags(java.lang.String flag) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(flag);
          boolean _status = mRemote.transact(Stub.TRANSACTION_hasHookFlags, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().hasHookFlags(flag);
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
      @Override public void addHookFlags(java.lang.String flag) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(flag);
          boolean _status = mRemote.transact(Stub.TRANSACTION_addHookFlags, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().addHookFlags(flag);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void removeHookFlags(java.lang.String flag) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(flag);
          boolean _status = mRemote.transact(Stub.TRANSACTION_removeHookFlags, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().removeHookFlags(flag);
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
    static final int TRANSACTION_reportFATALError = (android.os.IBinder.FIRST_CALL_TRANSACTION + 132);
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
    static final int TRANSACTION_querySensorUsages = (android.os.IBinder.FIRST_CALL_TRANSACTION + 148);
    static final int TRANSACTION_registerSensorUsageListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 149);
    static final int TRANSACTION_unregisterSensorUsageListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 150);
    static final int TRANSACTION_setPostNByAppEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 151);
    static final int TRANSACTION_isPostNByAppEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 152);
    static final int TRANSACTION_installApk = (android.os.IBinder.FIRST_CALL_TRANSACTION + 153);
    static final int TRANSACTION_getInstalledShortcutApps = (android.os.IBinder.FIRST_CALL_TRANSACTION + 154);
    static final int TRANSACTION_setHttpRequestLogEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 155);
    static final int TRANSACTION_isHttpRequestLogEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 156);
    static final int TRANSACTION_getHttpRequestLogFD = (android.os.IBinder.FIRST_CALL_TRANSACTION + 157);
    static final int TRANSACTION_getHttpRequestLogPath = (android.os.IBinder.FIRST_CALL_TRANSACTION + 158);
    static final int TRANSACTION_clearHttpRequestLogs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 159);
    static final int TRANSACTION_addToggle = (android.os.IBinder.FIRST_CALL_TRANSACTION + 160);
    static final int TRANSACTION_setToggleEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 161);
    static final int TRANSACTION_isToggleEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 162);
    static final int TRANSACTION_deleteToggle = (android.os.IBinder.FIRST_CALL_TRANSACTION + 163);
    static final int TRANSACTION_getToggleById = (android.os.IBinder.FIRST_CALL_TRANSACTION + 164);
    static final int TRANSACTION_getAllToggles = (android.os.IBinder.FIRST_CALL_TRANSACTION + 165);
    static final int TRANSACTION_getToggleCount = (android.os.IBinder.FIRST_CALL_TRANSACTION + 166);
    static final int TRANSACTION_registerToggleObs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 167);
    static final int TRANSACTION_unregisterToggleObs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 168);
    static final int TRANSACTION_hasHookFlags = (android.os.IBinder.FIRST_CALL_TRANSACTION + 169);
    static final int TRANSACTION_addHookFlags = (android.os.IBinder.FIRST_CALL_TRANSACTION + 170);
    static final int TRANSACTION_removeHookFlags = (android.os.IBinder.FIRST_CALL_TRANSACTION + 171);
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
  public void setEvaluateRecordEnabled(java.lang.String id, boolean enable) throws android.os.RemoteException;
  public boolean isEvaluateRecordEnabled(java.lang.String id) throws android.os.RemoteException;
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
  public void reportFATALError(java.lang.String errorMessage) throws android.os.RemoteException;
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
  // Sensor usage

  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> querySensorUsages() throws android.os.RemoteException;
  public void registerSensorUsageListener(tornaco.apps.shortx.core.ISensorUsageListener listener) throws android.os.RemoteException;
  public void unregisterSensorUsageListener(tornaco.apps.shortx.core.ISensorUsageListener listener) throws android.os.RemoteException;
  // N

  public void setPostNByAppEnabled(boolean enable) throws android.os.RemoteException;
  public boolean isPostNByAppEnabled() throws android.os.RemoteException;
  public void installApk(java.lang.String packageName, android.os.ParcelFileDescriptor fd, tornaco.apps.shortx.core.ICallback onSuccess, tornaco.apps.shortx.core.ICallback onFailure) throws android.os.RemoteException;
  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getInstalledShortcutApps() throws android.os.RemoteException;
  // Http request log.

  public void setHttpRequestLogEnabled(boolean enabled) throws android.os.RemoteException;
  public boolean isHttpRequestLogEnabled() throws android.os.RemoteException;
  public android.os.ParcelFileDescriptor getHttpRequestLogFD() throws android.os.RemoteException;
  public java.lang.String getHttpRequestLogPath() throws android.os.RemoteException;
  public void clearHttpRequestLogs() throws android.os.RemoteException;
  // Toggle start

  public void addToggle(tornaco.apps.shortx.core.rule.action.ByteArrayWrapper toggle) throws android.os.RemoteException;
  public void setToggleEnabled(java.lang.String id, boolean enable) throws android.os.RemoteException;
  public boolean isToggleEnabled(java.lang.String id) throws android.os.RemoteException;
  public void deleteToggle(java.lang.String id) throws android.os.RemoteException;
  public tornaco.apps.shortx.core.rule.action.ByteArrayWrapper getToggleById(java.lang.String id) throws android.os.RemoteException;
  public java.util.List<tornaco.apps.shortx.core.rule.action.ByteArrayWrapper> getAllToggles() throws android.os.RemoteException;
  public int getToggleCount() throws android.os.RemoteException;
  public void registerToggleObs(tornaco.apps.shortx.core.rule.IToggleObserver obs) throws android.os.RemoteException;
  public void unregisterToggleObs(tornaco.apps.shortx.core.rule.IToggleObserver obs) throws android.os.RemoteException;
  // Toggle end
  // Flags, used to disable hook

  public boolean hasHookFlags(java.lang.String flag) throws android.os.RemoteException;
  public void addHookFlags(java.lang.String flag) throws android.os.RemoteException;
  public void removeHookFlags(java.lang.String flag) throws android.os.RemoteException;
}
