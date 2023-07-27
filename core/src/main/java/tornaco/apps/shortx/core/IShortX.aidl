package tornaco.apps.shortx.core;

import tornaco.apps.shortx.core.IJobStatusListener;
import tornaco.apps.shortx.core.ICallback;
import tornaco.apps.shortx.core.rule.IGlobalVarObserver;
import tornaco.apps.shortx.core.rule.IPkgSetObserver;
import tornaco.apps.shortx.core.rule.IRuleObserver;
import tornaco.apps.shortx.core.rule.IDAObserver;
import tornaco.apps.shortx.core.IAudioRecordingListener;
import tornaco.apps.shortx.core.rule.action.su.ISu;
import tornaco.apps.shortx.core.notification.INotificationPoster;

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
    void executeDirectionActionById(String id);
    
    void executeAction(in ByteArrayWrapper action);
    void executeActionBlocking(in ByteArrayWrapper action);
    // Direct Actions end

    List<ByteArrayWrapper> getActionEvaluateRecords();
    void clearActionEvaluateRecords();
    void setActionEvaluateRecorderEnabled(boolean enable);
    boolean isActionEvaluateRecorderEnabled();

    List<ByteArrayWrapper> getConditionEvaluateRecords();
    void clearConditionEvaluateRecords();
    void setConditionEvaluateRecorderEnabled(boolean enable);
    boolean isConditionEvaluateRecorderEnabled();

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
    boolean evaluateCondition(in ByteArrayWrapper condition);
    // Condition end

    // Verify binding.
    void vb(in String code, in String deviceId, in ICallback cb);

    // User
    List<ByteArrayWrapper> getAllUsers();

    // Dev settings
    void setPointerLocationEnabled(boolean enable);
    boolean isPointerLocationEnabled();

    // Ext
    void directExecuteRuleActions(in String ruleId);

    List<ByteArrayWrapper> getRunningServices(in ByteArrayWrapper pkg);
    List<ByteArrayWrapper> getRunningAppPkgs(String pkgSetLabel);

    List<ByteArrayWrapper> getFactPublishRecords();
    void clearFactPublishRecords();
    void setFactPublishRecordEnabled(boolean enable);
    boolean isFactPublishRecordEnabled();

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

    void executeDirectionAction(in ByteArrayWrapper da);

    void onMediaStoreInsert(in Uri uri, String path);
    void onMediaStoreDelete(in Uri uri, String path);

    // ShellRes
    ByteArrayWrapper executeShellCommand(String command);

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
    void injectGestureRecord(String id);
}