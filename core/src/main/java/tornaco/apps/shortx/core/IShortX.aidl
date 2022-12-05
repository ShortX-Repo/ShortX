package tornaco.apps.shortx.core;

interface IShortX {
    Version version();
    String fingerprint();

    void addRule(in ByteArrayWrapper rule);
    void deleteRule(String id);
    ByteArrayWrapper getRuleById(String id);

    void setRuleEnabled(String ruleId, boolean enable);

    List<ByteArrayWrapper> getAllRules();

    List<App> getInstalledApps();
    String getAppLabel(in Pkg pkg);
}