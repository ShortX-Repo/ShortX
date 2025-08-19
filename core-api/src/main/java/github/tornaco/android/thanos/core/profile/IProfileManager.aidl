package github.tornaco.android.thanos.core.profile;

interface IProfileManager {

    void setAutoApplyForNewInstalledAppsEnabled(boolean enable);
    boolean isAutoApplyForNewInstalledAppsEnabled();

    void addRule(String author, int versionCode, String ruleJson, in IBinder callback, int format);
    void deleteRule(int ruleId);

    boolean enableRule(int ruleId);
    boolean disableRule(int ruleId);
    boolean isRuleEnabled(int ruleId);
    boolean isRuleExists(int ruleId);

    void checkRule(String ruleJson, in IBinder callback, int format);

    String[] getAllRules();
    String[] getEnabledRules();

    void setProfileEnabled(boolean enable);
    boolean isProfileEnabled();

    boolean addGlobalRuleVar(String varName, in String[] varArray);
    boolean appendGlobalRuleVar(String varName, in String[] varArray);
    boolean removeGlobalRuleVar(String varName);

    String[] getAllGlobalRuleVarNames();
    String[] getGlobalRuleVarByName(String varName);
    String[] getAllGlobalRuleVar();
    boolean isGlobalRuleVarByNameExists(String varName);

    void setProfileEngineUiAutomationEnabled(boolean enabled);
    boolean isProfileEngineUiAutomationEnabled();

    void setProfileEnginePushEnabled(boolean enabled);
    boolean isProfileEnginePushEnabled();

    void setShellSuSupportInstalled(boolean enable);
    boolean isShellSuSupportInstalled();

    boolean addConfigTemplate(in String template);
    boolean deleteConfigTemplate(in String template);
    List<String> getAllConfigTemplates();

    String getConfigTemplateById(String id);

    void setAutoConfigTemplateSelection(String id);
    String getAutoConfigTemplateSelectionId();

    boolean applyConfigTemplateForPackage(in Pkg pkg, in String template);

    void addRuleIfNotExists(String author, int versionCode, String ruleJson, in IBinder callback, int format);

    void publishStringFact(int source, String factValue, long delayMills, in String[] args);

    void updateRule(int ruleId, String ruleJson, in IBinder callback, int format);

    void registerRuleChangeListener(in IBinder listener);
    void unRegisterRuleChangeListener(in IBinder listener);

    String getRuleById(int ruleId);

    boolean enableRuleByName(String ruleName);
    boolean disableRuleByName(String ruleName);

    void dump(in IBinder p);

    ParcelFileDescriptor getLogFD();
    String getLogPath();
    void clearLogs();
    void setLogEnabled(boolean enable);
    boolean isLogEnabled();

    void executeAction(String action);
    void addConsoleLogSink(in IBinder sink);
    void removeConsoleLogSink(in IBinder sink);


    String parseRuleOrNull(String ruleString, int format);
    String getRuleByName(String ruleName);

    void setCustomSuCommand(String command);
    String getCustomSuCommand();

    // Engines.
    void addAlarmEngine(in String alarm);
    void removeAlarmEngine(in String alarm);
    List<String> getAllAlarms();
    void setAlarmEnabled(in String alarm, boolean enabled);

    // Danmu API
    void setDanmuUISettings(in String settings);
    String getDanmuUISettings();

}