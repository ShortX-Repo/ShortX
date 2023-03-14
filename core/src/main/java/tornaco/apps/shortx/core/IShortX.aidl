package tornaco.apps.shortx.core;

import tornaco.apps.shortx.core.IJobStatusListener;

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

    List<ByteArrayWrapper> getInstalledApps();
    String getAppLabel(in ByteArrayWrapper pkg);
    ByteArrayWrapper pkgToApp(in ByteArrayWrapper pkg);

    void showEdgeHint(int edgeNumber);

    // Direct Actions start
    void addDirectAction(in ByteArrayWrapper da);
    void deleteDirectAction(String id);
    ByteArrayWrapper getDirectActionById(String id);
    List<ByteArrayWrapper> getAllDirectAction();
    void executeDirectionActionById(String id);
    // Direct Actions end


    // Pkg Set
    void addPkgSet(in ByteArrayWrapper set);
    void deletePkgSet(String id);
    ByteArrayWrapper getPkgSetById(String id);
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
}