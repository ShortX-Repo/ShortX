package tornaco.apps.shortx.core;

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
    // Rules end

    List<App> getInstalledApps();
    String getAppLabel(in Pkg pkg);

    void showEdgeHint(int edgeNumber);

    // Direct Actions start
    void addDirectAction(in ByteArrayWrapper da);
    void deleteDirectAction(String id);
    ByteArrayWrapper getDirectActionById(String id);
    List<ByteArrayWrapper> getAllDirectAction();
    void executeDirectionActionById(String id);
    // Direct Actions end
}