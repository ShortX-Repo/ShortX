package tornaco.apps.shortx.core;

import tornaco.apps.shortx.core.IActionEvaluateListener;
import tornaco.apps.shortx.core.IJobStatusListener;
import tornaco.apps.shortx.core.ICallback;
import tornaco.apps.shortx.core.rule.IGlobalVarObserver;
import tornaco.apps.shortx.core.rule.IPkgSetObserver;
import tornaco.apps.shortx.core.rule.IRuleObserver;
import tornaco.apps.shortx.core.rule.IDAObserver;
import tornaco.apps.shortx.core.IAudioRecordingListener;
import tornaco.apps.shortx.core.rule.action.su.ISu;
import tornaco.apps.shortx.core.notification.INotificationPoster;
import tornaco.apps.shortx.core.os.SynchronousResultReceiver;
import tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener;
import tornaco.apps.shortx.core.systemui.IQSTileHost;
import tornaco.apps.shortx.core.ISensorUsageListener;

interface IShortX {
    Version version();
    String fingerprint();

    void setLogDebugEnable(boolean enable);
    boolean isLogDebugEnable();
    String getLogDir();
    boolean hasFatalError();
    void writeLogsTo(in ParcelFileDescriptor pfd);

    // Rules start
    void addRule(in ByteArrayWrapper rule);
    void deleteRule(String id);
    ByteArrayWrapper getRuleById(String id);

    void setRuleEnabled(String ruleId, boolean enable);

    List<ByteArrayWrapper> getAllRules();
    int getRuleCount();
    // Rules end

    List<ByteArrayWrapper> getInstalledApps();
    String getAppLabel(in ByteArrayWrapper pkg);
    ByteArrayWrapper pkgToApp(in ByteArrayWrapper pkg);

    void showEdgeHint(int edgeNumber);

    // Direct Actions start
    void addDirectAction(in ByteArrayWrapper da);
    void deleteDirectAction(String id);
    ByteArrayWrapper getDirectActionById(String id);
    List<ByteArrayWrapper> getAllDirectAction();
    int getDirectActionCount();

    void executeDirectionActionById(in ByteArrayWrapper evaluateContext, String id);
    void executeDirectionAction(in ByteArrayWrapper evaluateContext, in ByteArrayWrapper da);
    void executeAction(in ByteArrayWrapper evaluateContext, in ByteArrayWrapper action);
    void executeActionBlocking(in ByteArrayWrapper evaluateContext, in ByteArrayWrapper action);
    void directExecuteRuleActions(in ByteArrayWrapper evaluateContext, in String ruleId);

    // Direct Actions end

    List<ByteArrayWrapper> getActionEvaluateRecords();
    List<ByteArrayWrapper> getConditionEvaluateRecords();
    List<ByteArrayWrapper> getFactPublishRecords();
    void clearEvaluateRecords();
    void setEvaluateRecordEnabled(boolean enable);
    boolean isEvaluateRecordEnabled();

    // Pkg Set
    void addPkgSet(in ByteArrayWrapper set);
    void deletePkgSet(String label);
    ByteArrayWrapper getPkgSetByLabel(String label);
    List<ByteArrayWrapper> getAllPkgSets(boolean withPkgList);
    // Pkg Set end

    // UI start
    void showDanmu(String text, String icon);
    ByteArrayWrapper getDanmuUISettings();
    void setDanmuUISettings(in ByteArrayWrapper settings);

    Bitmap getAppIcon(String pkgName, int userId);
    // UI end

    // List with rule id or DA id
    List<String> getActiveJobs();
    void cancelJobs(in List<String> jobs);

    void registerJobStatusListener(in IJobStatusListener listener);
    void unregisterJobStatusListener(in IJobStatusListener listener);

    // Components start
    List<ByteArrayWrapper> getActivities(int userId, String packageName);
    List<ByteArrayWrapper> getReceivers(int userId, String packageName);
    List<ByteArrayWrapper> getServices(int userId, String packageName);
    List<ByteArrayWrapper> getProviders(int userId, String packageName);

    void setComponentEnabledSetting(/*AppComponent*/in ByteArrayWrapper component, int newState, int flags);
    int getComponentEnabledSetting(/*AppComponent*/in ByteArrayWrapper component);

    ByteArrayWrapper getAppComponentInfo(/*AppComponent*/in ByteArrayWrapper component);
    // Components end


    // Global var start
    void addGlobalVar(in ByteArrayWrapper gv);
    void deleteGlobalVar(String name);
    ByteArrayWrapper getGlobalVarByName(String name);
    List<ByteArrayWrapper> getAllGlobalVars();

    void registerGlobalVarObs(in IGlobalVarObserver obs);
    void unregisterGlobalVarObs(in IGlobalVarObserver obs);

    // Global var end

    void reboot(String reason);

    String executeMVEL(String expression);

    // Condition start
    boolean evaluateCondition(in ByteArrayWrapper evaluateContext, in ByteArrayWrapper condition);
    // Condition end

    // Verify binding.
    void vb(in String code, in String deviceId, in ICallback cb);

    // User
    List<ByteArrayWrapper> getAllUsers();

    // Dev settings
    void setPointerLocationEnabled(boolean enable);
    boolean isPointerLocationEnabled();

    List<ByteArrayWrapper> getRunningServices(in ByteArrayWrapper pkg);
    List<ByteArrayWrapper> getRunningAppPkgs(String pkgSetLabel);

    int getGestureAreaSize();
    void setGestureAreaSize(int dp);

    void registerPkgSetObs(in IPkgSetObserver obs);
    void unregisterPkgSetObs(in IPkgSetObserver obs);

    void registerRuleObs(in IRuleObserver obs);
    void unregisterRuleObs(in IRuleObserver obs);

    void registerDAObs(in IDAObserver obs);
    void unregisterDAObs(in IDAObserver obs);

    void setKeyEventPromptEnabled(boolean enable);
    boolean isKeyEventPromptEnabled();

    void onMediaStoreInsert(in Uri uri, String path);
    void onMediaStoreDelete(in Uri uri, String path);

    String executeShellCommand(String command);

    List<String> getWifiScanResults();

    // Called by SystemUI
    void attachSUImpl(String from, in ISu su);
    boolean isInjectedShellEnabled();
    void setInjectedShellEnabled(boolean enable);

    boolean isAudioRecording();
    void stopAudioRecording();
    void registerAudioRecordingObs(in IAudioRecordingListener obs);
    void unregisterAudioRecordingObs(in IAudioRecordingListener obs);
    List<ByteArrayWrapper> getAllAudioRecordFiles();
    void deleteAudioRecordFile(in ByteArrayWrapper file);
    void exportAudioRecordFile(in ByteArrayWrapper file, in ParcelFileDescriptor pfd);

    List<ByteArrayWrapper> getBTBondedDevices();

    // Called by SystemUI
    void attachNotificationPoster(String from, in INotificationPoster poster);

    // Error report
    void uncaughtException(in String error);
    void crashSystemServer();

    // Http test
    List<String> executeHttpRequest(in ByteArrayWrapper request);

    // Phone
    List<SubscriptionInfo> getActiveSubscriptionInfoList();

    // Gesture record
    void addGestureRecord(in ByteArrayWrapper record);
    ByteArrayWrapper getGestureRecordById(String id);
    void deleteGestureRecordById(String id);
    List<ByteArrayWrapper> getAllGestureRecords(boolean withEventData);
    void injectGestureRecord(String id, float speed, in SynchronousResultReceiver onComplete);
    void startGestureRecording();
    void stopGestureRecording(boolean save);
    boolean isGestureRecording();

    // Tile
    List<ByteArrayWrapper> getAllStatusBarTileSettings();
    ByteArrayWrapper getStatusBarTileSettingsByNumber(int tileNumber);
    void updateStatusBarTileSetting(in ByteArrayWrapper tileSetting);
    void registerStatusBarTileSettingsChangeListener(in IStatusBarTileSettingsChangeListener listener);
    void unregisterStatusBarTileSettingsChangeListener(in IStatusBarTileSettingsChangeListener listener);
    void onTileClick(int tileNumber);
    boolean isQSTileOptEnabled();
    void setQSTileOptEnabled(boolean enable);

    // Catcher
    boolean isSettingsCatcherEnabled();
    void setSettingsCatcherEnabled(boolean enabled);
    void onReadSettings(String url, String value);
    void onWriteSettings(String url, String value);
    List<ByteArrayWrapper> getReadSettingsRecord();
    List<ByteArrayWrapper> getWriteSettingsRecord();
    void clearWriteSettingsRecord();
    void clearReadSettingsRecord();

    void setRuleEngineEnabled(String engine, boolean enable);
    boolean isRuleEngineEnabled(String engine);

    String executeJS(String expression);

    // Rule Set
    void addOrUpdateRuleSet(in ByteArrayWrapper rs);
    void deleteRuleSet(String id);
    ByteArrayWrapper getRuleSetById(String id, boolean withRules);
    List<ByteArrayWrapper> getAllRuleSets(boolean withRules);

    void addOrUpdateDASet(in ByteArrayWrapper ds);
    void deleteDASet(String id);
    ByteArrayWrapper getDASetById(String id, boolean withDAs);
    List<ByteArrayWrapper> getAllDASets(boolean withDAs);

    void testFATALError();

    void attachQSTileHost(in IQSTileHost host);
    // QSTileList
    ByteArrayWrapper getAllQSTiles();

    // Swipe gesture.
    int getSwipeDistanceThresholdDp();
    void setSwipeDistanceThresholdDp(int dp);

    void log(String message);

    int getStreamMinVolume(int type);
    int getStreamMaxVolume(int type);
    int getStreamVolume(int type);

    List<String> getEvaluatingActions();
    void registerActionEvaluateListener(in IActionEvaluateListener listener);
    void unregisterActionEvaluateListener(in IActionEvaluateListener listener);

    void setStartActivityResult(int requestCode, int resultCode, in Bundle result);

    void shortcutAppLaunched(String packageName);

    // Global feature toggle.
    void setRuleFeatureEnabled(boolean enable);
    boolean isRuleFeatureEnabled();

    // Sensor usage
    List<ByteArrayWrapper> querySensorUsages();
    void registerSensorUsageListener(in ISensorUsageListener listener);
    void unregisterSensorUsageListener(in ISensorUsageListener listener);
}