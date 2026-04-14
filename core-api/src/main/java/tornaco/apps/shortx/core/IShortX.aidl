package tornaco.apps.shortx.core;

import tornaco.apps.shortx.core.SystemUIClient;
import tornaco.apps.shortx.core.IActionEvaluateListener;
import tornaco.apps.shortx.core.IJobStatusListener;
import tornaco.apps.shortx.core.ICallback;
import tornaco.apps.shortx.core.rule.IGlobalVarObserver;
import tornaco.apps.shortx.core.rule.IPkgSetObserver;
import tornaco.apps.shortx.core.rule.IRuleObserver;
import tornaco.apps.shortx.core.rule.IDAObserver;
import tornaco.apps.shortx.core.rule.IToggleObserver;
import tornaco.apps.shortx.core.IAudioRecordingListener;
import tornaco.apps.shortx.core.rule.action.su.ISu;
import tornaco.apps.shortx.core.notification.INotificationPoster;
import tornaco.apps.shortx.core.os.SynchronousResultReceiver;
import tornaco.apps.shortx.core.os.ICancellationSignal;
import tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener;
import tornaco.apps.shortx.core.systemui.IQSTileHost;
import tornaco.apps.shortx.core.ISensorUsageListener;
import tornaco.apps.shortx.core.INFCTagEndpointListener;
import tornaco.apps.shortx.core.IForceDisableListener;
import tornaco.apps.shortx.core.IRuleFeatureStateListener;
import tornaco.apps.shortx.core.cv.ICV;
import tornaco.apps.shortx.core.ocr.IOCR;
import tornaco.apps.shortx.core.phone.ISMSProvider;
import tornaco.apps.shortx.core.media.FileRow;


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

    List<ByteArrayWrapper> getAllRules(String queryId, int pageNumber, int pageSize);
    int getRuleCount();
    // Rules end

    List<ByteArrayWrapper> getInstalledApps();
    String getAppLabel(in ByteArrayWrapper pkg);
    ByteArrayWrapper pkgToApp(in ByteArrayWrapper pkg);

    void showEdgeHint(in int[] edgeNumber);

    // Direct Actions start
    void addDirectAction(in ByteArrayWrapper da);
    void deleteDirectAction(String id);
    ByteArrayWrapper getDirectActionById(String id);
    List<ByteArrayWrapper> getAllDirectAction(String queryId, int pageNumber, int pageSize);
    int getDirectActionCount();

    void executeDirectionActionById(in ByteArrayWrapper evaluateContext, String id);
    void executeDirectionAction(in ByteArrayWrapper evaluateContext, in ByteArrayWrapper da, in ByteArrayWrapper contextAndVars);
    void executeAction(in ByteArrayWrapper evaluateContext, in ByteArrayWrapper action, in ByteArrayWrapper contextAndVars);
    void executeActionBlocking(in ByteArrayWrapper evaluateContext, in ByteArrayWrapper action);
    void directExecuteRuleActions(in ByteArrayWrapper evaluateContext, in String ruleId);

    // Direct Actions end

    List<ByteArrayWrapper> getActionEvaluateRecords();
    List<ByteArrayWrapper> getConditionEvaluateRecords();
    List<ByteArrayWrapper> getFactPublishRecords();
    void clearEvaluateRecords();
    void setEvaluateRecordEnabled(String id, boolean enable);
    boolean isEvaluateRecordEnabled(String id);

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

    String executeMVEL(String expression, int context);

    // Condition start
    boolean evaluateCondition(in ByteArrayWrapper evaluateContext, in ByteArrayWrapper condition);
    // Condition end

    void loadOnlineRules(in String category, in String filterId, in ICallback cb);

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

    void onMediaStoreInsert(in FileRow file);
    void onMediaStoreDelete(in FileRow file);

    // ICallback: ShellRes
    ICancellationSignal executeShellCommand(String command, in ICallback callback);

    List<String> getWifiScanResults();

    // Called by SystemUI
    /** @deprecated use {@link #SystemUIClient} instead */
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
    /** @deprecated use {@link #SystemUIClient} instead */
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

    String executeJS(String expression, int context);

    // Rule Set
    void addOrUpdateRuleSet(in ByteArrayWrapper rs);
    void deleteRuleSet(String id);
    ByteArrayWrapper getRuleSetById(String id, boolean withRules);
    List<ByteArrayWrapper> getAllRuleSets(boolean withRules);

    void addOrUpdateDASet(in ByteArrayWrapper ds);
    void deleteDASet(String id);
    ByteArrayWrapper getDASetById(String id, boolean withDAs);
    List<ByteArrayWrapper> getAllDASets(boolean withDAs);

    void reportFATALError(String errorMessage);

    /** @deprecated use {@link #SystemUIClient} instead */
    void attachQSTileHost(in IQSTileHost host);
    // QSTileList
    ByteArrayWrapper getAllQSTiles();

    // Swipe gesture.
    int getSwipeDistanceThresholdDp();
    void setSwipeDistanceThresholdDp(int dp);

    void log(String message, String redirectTo);

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

    // N
    void setPostNByAppEnabled(boolean enable);
    boolean isPostNByAppEnabled();

    void installApk(String packageName, in ParcelFileDescriptor fd, in ICallback onSuccess, in ICallback onFailure);
    List<ByteArrayWrapper> getInstalledShortcutApps();

    // Http request log.
    void setHttpRequestLogEnabled(boolean enabled);
    boolean isHttpRequestLogEnabled();
    ParcelFileDescriptor getHttpRequestLogFD();
    String getHttpRequestLogPath();
    void clearHttpRequestLogs();

    // Toggle start
    void addToggle(in ByteArrayWrapper toggle);
    void setToggleEnabled(String id, boolean enable);
    boolean isToggleEnabled(String id);
    void deleteToggle(String id);
    ByteArrayWrapper getToggleById(String id);
    List<ByteArrayWrapper> getAllToggles();
    int getToggleCount();
    void registerToggleObs(in IToggleObserver obs);
    void unregisterToggleObs(in IToggleObserver obs);
    // Toggle end

    // Flags, used to disable hook
    boolean hasHookFlags(String flag);
    void addHookFlags(String flag);
    void removeHookFlags(String flag);
    // Flags

    void updateQSTileActiveState(int number);

    List<ByteArrayWrapper> getPrivilegedConfiguredNetworks();

    void deepLinkTriggerCalled(String tag);

    List<ByteArrayWrapper> getAllRingtones();

    int getMaxSignalLevel();

    void setForceDisableShortXByKeyPressEnabled(boolean enable);
    boolean isForceDisableShortXByKeyPressEnabled();

    void dispatchTagEndpoint(in byte[] uid);
    void registerNFCTagEndpointListener(in INFCTagEndpointListener listener);
    void unregisterNFCTagEndpointListener(in INFCTagEndpointListener listener);

    // Swipe gesture.
    float getSwipeLengthScale();
    void setSwipeLengthScale(float scale);

    // Custom hook
    void onHookedMethodCalled(in ByteArrayWrapper hook, in List<ByteArrayWrapper> methodArgs);
    ParcelFileDescriptor getMethodHookLogFD();
    String getMethodHookLogPath();
    void clearMethodHookLogs();

    void setKeyDetectorLongPressTimeoutMS(long timeout);
    long getKeyDetectorLongPressTimeoutMS();

    List<ByteArrayWrapper> getInstalledPlugins();
    List<ByteArrayWrapper> getInstalledPluginActions();
    List<ByteArrayWrapper> getInstalledPluginConditions();
    List<ByteArrayWrapper> getInstalledPluginFacts();

    // Called by SystemUI
    /** @deprecated use {@link #SystemUIClient} instead */
    void attachCV(in ICV cv);
    /** @deprecated use {@link #SystemUIClient} instead */
    void attachOCR(in IOCR ocr);

    // Pin
    List<ShortcutInfo> getRequestPinShortcuts();
    List<AppWidgetProviderInfo> getRequestPinAppWidgets();

    void registerStatusbarClipStateListener(in ICallback listener);
    void unregisterStatusbarClipStateListener(in ICallback listener);
    void registerStatusbarClipSettingsListener(in ICallback listener);
    void unregisterStatusbarClipSettingsListener(in ICallback listener);
    ByteArrayWrapper getCurrentStatusbarClipState();
    ByteArrayWrapper getStatusbarClipSettings();
    void setStatusbarClipSettings(in ByteArrayWrapper data);
    void onStatusBarClipClick(String id);
    void onStatusBarClipLongClick(String id);

    void attachSMSProvider(in ISMSProvider provider);

    // Audio
    void renameAudioRecordFile(in ByteArrayWrapper file, String name);

    ParcelFileDescriptor getJSLogFD();
    String getJSLogPath();
    void clearJSLogs();

    // WebDav
    void addWebDavProfile(in ByteArrayWrapper profile);
    void removeWebDavProfile(String profileId);
    List<ByteArrayWrapper> getAllWebDavProfiles();
    ByteArrayWrapper getWebDavProfileById(String profileId);

    List<ByteArrayWrapper> getAllSystemEnv();

    void setCustomSU(String path);
    String getCustomSU();

    // LightBarTransitionsController
    void registerLightBarTransitionsListener(in ICallback listener);
    void unregisterLightBarTransitionsListener(in ICallback listener);
    void setLightBarTransitionsIconsDark(boolean isDark);
    boolean isLightBarTransitionsIconsDark();

    void setOCREngine(int engine);
    int getOCREngine();

    void attachSystemUIClient(in SystemUIClient token);

    List<ByteArrayWrapper> getAllContextMenuSettings();
    ByteArrayWrapper getContextMenuSettingsByMenuNumber(int menuNumber);
    void updateContextMenuSetting(in ByteArrayWrapper menuSetting);
    void onMenuActionTrigger(int menuNumber, String text);

    void registerGestureSettingsListener(in ICallback listener);
    void unregisterGestureSettingsListener(in ICallback listener);

    boolean shouldFireEdgeGesture(int edge, int gesture);
    void fireEdgeGesture(in ByteArrayWrapper data);

    boolean shouldFireScreenGesture(int gesture, int x, int y);
    void fireScreenGesture(int gesture, int x, int y);

    void registerConfigurationChangedListener(in ICallback listener);
    void unregisterConfigurationChangedListener(in ICallback listener);

    Point getScreenSize();

    void registerRecorderStateListener(in ICallback listener);
    void unregisterRecorderStateListener(in ICallback listener);

    void onMotionEvent(in MotionEvent event);

    boolean isAppFeatureEnabled(int feat);
    void setAppFeatureEnabled(int feat, boolean enabled);

    void registerSystemReadyListener(in ICallback listener);
    void unRegisterSystemReadyListener(in ICallback listener);

    void registerFrontAppChangeListener(in ICallback listener);
    void unRegisterFrontAppChangeListener(in ICallback listener);

    ByteArrayWrapper getShortXServerDirs();

    // Shortcuts
    List<ByteArrayWrapper> getAllDynamicShortcutSettings();
    ByteArrayWrapper getDynamicShortcutSettingsByNumber(int num);
    void updateDynamicShortcutSetting(in ByteArrayWrapper setting);

    void showScreenAreaSelector(in SynchronousResultReceiver onSelectRect);
    void showScreenRectHint(in ByteArrayWrapper rect);

    void registerForceDisableListener(in IForceDisableListener listener);
    void unregisterForceDisableListener(in IForceDisableListener listener);

    void registerRuleFeatureStateListener(in IRuleFeatureStateListener listener);
    void unregisterRuleFeatureStateListener(in IRuleFeatureStateListener listener);

    // Code Library start
    void addCodeLibraryItem(in ByteArrayWrapper item);
    void updateCodeLibraryItem(in ByteArrayWrapper item);
    void deleteCodeLibraryItem(String id);
    ByteArrayWrapper getCodeLibraryItemById(String id);
    List<ByteArrayWrapper> getAllCodeLibraryItems();
    List<ByteArrayWrapper> queryCodeLibraryItems(in ByteArrayWrapper query);
    int getCodeLibraryItemCount();
    String executeCodeLibraryItem(String id, int context);
    // Code Library end

    // Functions start
    void addFunction(in ByteArrayWrapper function);
    void deleteFunction(String id);
    ByteArrayWrapper getFunctionById(String id);
    List<ByteArrayWrapper> getAllFunctions();

    // Functions end

    // Recently added AIDL interfaces
    void showCornerHint(in int[] cornerNumber);
    void fireCornerSwipeToCenter(in ByteArrayWrapper data);
    int getCornerGestureRegionRadiusRatioPercent();
    void setCornerGestureRegionRadiusRatioPercent(int percent);
    int getCornerGestureMinSwipeDistanceDp();
    void setCornerGestureMinSwipeDistanceDp(int dp);
    int getEdgeGestureSequenceStepTimeoutMs();
    void setEdgeGestureSequenceStepTimeoutMs(int timeoutMs);
    int getEdgeGestureSequenceHoverDurationMs();
    void setEdgeGestureSequenceHoverDurationMs(int durationMs);
    int getEdgeGestureSequenceHoverRadiusDp();
    void setEdgeGestureSequenceHoverRadiusDp(int radiusDp);
    int getEdgeGestureSequenceChildSwipeMinDistanceDp();
    void setEdgeGestureSequenceChildSwipeMinDistanceDp(int distanceDp);
    int getEdgeGestureSequenceChildSwipeMaxAngleDeviationDeg();
    void setEdgeGestureSequenceChildSwipeMaxAngleDeviationDeg(int angleDeg);

    String updateWorkdayCalendarCache(String payload);
}