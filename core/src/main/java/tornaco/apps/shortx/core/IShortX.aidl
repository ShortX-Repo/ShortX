package tornaco.apps.shortx.core;

import tornaco.apps.shortx.core.overlay.IOverlayButtonSettingsChangeListener;

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

    void showEdgeHint(int edgeNumber);

    String getOverlayButtonIconName(int index);
    void setOverlayButtonIconName(int index, String name);
    void registerOverlayButtonSettingsChangeListener(in IOverlayButtonSettingsChangeListener listener);
    void unRegisterOverlayButtonSettingsChangeListener(in IOverlayButtonSettingsChangeListener listener);
}