package github.tornaco.android.thanos.core;

import github.tornaco.android.thanos.core.app.IActivityManager;
import github.tornaco.android.thanos.core.profile.IProfileManager;

import android.content.IntentFilter;

// DO NOT CHANGE ORDER.
interface IThanos {

    IBinder getServiceManager();
    IBinder getPrefManager();
    IActivityManager getActivityManager();
    IBinder getPkgManager();
    IBinder getActivityStackSupervisor();
    IBinder getPrivacyManager();
    IBinder getAppOpsService();
    IBinder getPushManager();
    IBinder getNotificationManager();
    IBinder getAudioManager();
    IProfileManager getProfileManager();
    IBinder getBackupAgent();
    IBinder getWindowManager();
    IBinder getPowerManager();
    IBinder getInputManager();

    void registerEventSubscriber(in IntentFilter filter, in IBinder subscriber);
    void unRegisterEventSubscriber(in IBinder subscriber);

    String fingerPrint();
    String getVersionName();

    String whoAreYou();

    boolean isLoggingEnabled();
    void setLoggingEnabled(boolean enable);

    boolean hasFeature(String feature);
    boolean hasFrameworkInitializeError();

    IBinder getPluginLogger(String pluginAlias);

    IBinder getInfiniteZ();

    List<String> getPatchingSource();

    IBinder getRS();

    IBinder getUsageStatsManager();
    IBinder getPushDelegateManager();
    IBinder getNetworkManager();
}