package tornaco.apps.shortx.core;

import tornaco.apps.shortx.core.IJobStatusListener;
import tornaco.apps.shortx.core.ICallback;
import tornaco.apps.shortx.core.rule.IGlobalVarObserver;

interface IShortX {
    Version version();
    String fingerprint();

    void setLogDebugEnable(boolean enable);
    boolean isLogDebugEnable();

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
    // Direct Actions end


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
}