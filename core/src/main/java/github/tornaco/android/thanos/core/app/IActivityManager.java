/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package github.tornaco.android.thanos.core.app;
public interface IActivityManager extends android.os.IInterface
{
  /** Default implementation for IActivityManager. */
  public static class Default implements github.tornaco.android.thanos.core.app.IActivityManager
  {
    @Override public java.lang.String getCurrentFrontApp() throws android.os.RemoteException
    {
      return null;
    }
    @Override public void forceStopPackage(github.tornaco.android.thanos.core.pm.Pkg pkg, java.lang.String reason) throws android.os.RemoteException
    {
    }
    @Override public void idlePackage(github.tornaco.android.thanos.core.pm.Pkg pkg) throws android.os.RemoteException
    {
    }
    @Override public boolean isPackageIdle(github.tornaco.android.thanos.core.pm.Pkg pkg) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean checkBroadcastingIntent(android.content.Intent intent) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean checkService(android.content.Intent intent, android.content.ComponentName service, int callerUid, int userId) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean checkRestartService(java.lang.String packageName, android.content.ComponentName componentName) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean checkBroadcast(android.content.Intent intent, int receiverUid, int callerUid) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean checkStartProcess(java.lang.String processName, android.content.pm.ApplicationInfo applicationInfo, java.lang.String hostType, java.lang.String hostName) throws android.os.RemoteException
    {
      return false;
    }
    @Override public void onStartProcessLocked(android.content.pm.ApplicationInfo appInfo) throws android.os.RemoteException
    {
    }
    @Override public github.tornaco.android.thanos.core.process.ProcessRecord[] getRunningAppProcess() throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.util.List<github.tornaco.android.thanos.core.pm.Pkg> getRunningAppPackages() throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.util.List<android.app.ActivityManager.RunningServiceInfo> getRunningServiceLegacy(int max) throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.util.List<github.tornaco.android.thanos.core.app.RunningAppProcessInfoCompat> getRunningAppProcessLegacy() throws android.os.RemoteException
    {
      return null;
    }
    @Override public int getRunningAppsCount() throws android.os.RemoteException
    {
      return 0;
    }
    @Override public java.util.List<github.tornaco.android.thanos.core.process.ProcessRecord> getRunningAppProcessForPackage(github.tornaco.android.thanos.core.pm.Pkg pkg) throws android.os.RemoteException
    {
      return null;
    }
    @Override public boolean isPackageRunning(github.tornaco.android.thanos.core.pm.Pkg pkg) throws android.os.RemoteException
    {
      return false;
    }
    @Override public java.util.List<github.tornaco.android.thanos.core.app.start.StartRecord> getStartRecordsByPackageName(java.lang.String pkgName) throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.util.List<java.lang.String> getStartRecordBlockedPackages() throws android.os.RemoteException
    {
      return null;
    }
    @Override public long getStartRecordsBlockedCount() throws android.os.RemoteException
    {
      return 0L;
    }
    @Override public long getStartRecordBlockedCountByPackageName(java.lang.String pkgName) throws android.os.RemoteException
    {
      return 0L;
    }
    // 启动管理设置

    @Override public boolean isStartBlockEnabled() throws android.os.RemoteException
    {
      return false;
    }
    @Override public void setStartBlockEnabled(boolean enable) throws android.os.RemoteException
    {
    }
    @Override public void setPkgStartBlockEnabled(github.tornaco.android.thanos.core.pm.Pkg pkg, boolean enable) throws android.os.RemoteException
    {
    }
    @Override public boolean isPkgStartBlocking(github.tornaco.android.thanos.core.pm.Pkg pkg) throws android.os.RemoteException
    {
      return false;
    }
    // Task removal

    @Override public boolean isCleanUpOnTaskRemovalEnabled() throws android.os.RemoteException
    {
      return false;
    }
    @Override public void setCleanUpOnTaskRemovalEnabled(boolean enable) throws android.os.RemoteException
    {
    }
    @Override public void setPkgCleanUpOnTaskRemovalEnabled(github.tornaco.android.thanos.core.pm.Pkg pkg, boolean enable) throws android.os.RemoteException
    {
    }
    @Override public boolean isPkgCleanUpOnTaskRemovalEnabled(github.tornaco.android.thanos.core.pm.Pkg pkg) throws android.os.RemoteException
    {
      return false;
    }
    // 后台运行设置

    @Override public boolean isBgRestrictEnabled() throws android.os.RemoteException
    {
      return false;
    }
    @Override public void setBgRestrictEnabled(boolean enable) throws android.os.RemoteException
    {
    }
    @Override public void setPkgBgRestrictEnabled(github.tornaco.android.thanos.core.pm.Pkg pkg, boolean enable) throws android.os.RemoteException
    {
    }
    @Override public boolean isPkgBgRestricted(github.tornaco.android.thanos.core.pm.Pkg pkg) throws android.os.RemoteException
    {
      return false;
    }
    @Override public void setBgRestrictNotificationEnabled(boolean enabled) throws android.os.RemoteException
    {
    }
    @Override public boolean isBgRestrictNotificationEnabled() throws android.os.RemoteException
    {
      return false;
    }
    // Task blur

    @Override public boolean isRecentTaskBlurEnabled() throws android.os.RemoteException
    {
      return false;
    }
    @Override public void setRecentTaskBlurEnabled(boolean enable) throws android.os.RemoteException
    {
    }
    @Override public void setPkgRecentTaskBlurEnabled(github.tornaco.android.thanos.core.pm.Pkg pkg, boolean enable) throws android.os.RemoteException
    {
    }
    @Override public boolean isPkgRecentTaskBlurEnabled(github.tornaco.android.thanos.core.pm.Pkg pkg) throws android.os.RemoteException
    {
      return false;
    }
    // Audio focused app.

    @Override public boolean isBgTaskCleanUpSkipAudioFocusedAppEnabled() throws android.os.RemoteException
    {
      return false;
    }
    @Override public void setBgTaskCleanUpSkipAudioFocusedAppEnabled(boolean enable) throws android.os.RemoteException
    {
    }
    // Notification record app.

    @Override public boolean isBgTaskCleanUpSkipWhichHasNotificationEnabled() throws android.os.RemoteException
    {
      return false;
    }
    @Override public void setBgTaskCleanUpSkipWhichHasNotificationEnabled(boolean enable) throws android.os.RemoteException
    {
    }
    // 后台运行锁屏清理延迟

    @Override public void setBgTaskCleanUpDelayTimeMills(long delayMills) throws android.os.RemoteException
    {
    }
    @Override public long getBgTaskCleanUpDelayTimeMills() throws android.os.RemoteException
    {
      return 0L;
    }
    @Override public void notifyTaskCreated(int taskId, android.content.ComponentName componentName) throws android.os.RemoteException
    {
    }
    @Override public android.app.ActivityManager.MemoryInfo getMemoryInfo() throws android.os.RemoteException
    {
      return null;
    }
    @Override public long[] getProcessPss(int[] pids) throws android.os.RemoteException
    {
      return null;
    }
    @Override public void onApplicationCrashing(java.lang.String eventType, java.lang.String processName, github.tornaco.android.thanos.core.process.ProcessRecord process, java.lang.String stackTrace) throws android.os.RemoteException
    {
    }
    @Override public java.lang.String getPackageNameForTaskId(int taskId) throws android.os.RemoteException
    {
      return null;
    }
    @Override public int isPlatformAppIdleEnabled() throws android.os.RemoteException
    {
      return 0;
    }
    @Override public boolean isSmartStandByEnabled() throws android.os.RemoteException
    {
      return false;
    }
    @Override public void setSmartStandByEnabled(boolean enable) throws android.os.RemoteException
    {
    }
    @Override public void setPkgSmartStandByEnabled(github.tornaco.android.thanos.core.pm.Pkg pkg, boolean enable) throws android.os.RemoteException
    {
    }
    @Override public boolean isPkgSmartStandByEnabled(github.tornaco.android.thanos.core.pm.Pkg pkg) throws android.os.RemoteException
    {
      return false;
    }
    @Override public java.util.List<github.tornaco.android.thanos.core.pm.Pkg> getLastRecentUsedPackages(int count) throws android.os.RemoteException
    {
      return null;
    }
    @Override public int getRecentTaskExcludeSettingForPackage(github.tornaco.android.thanos.core.pm.Pkg pkg) throws android.os.RemoteException
    {
      return 0;
    }
    @Override public void setRecentTaskExcludeSettingForPackage(github.tornaco.android.thanos.core.pm.Pkg pkg, int setting) throws android.os.RemoteException
    {
    }
    // Keep when has recent task.

    @Override public boolean isBgTaskCleanUpSkipWhenHasRecentTaskEnabled() throws android.os.RemoteException
    {
      return false;
    }
    @Override public void setBgTaskCleanUpSkipWhenHasRecentTaskEnabled(boolean enable) throws android.os.RemoteException
    {
    }
    @Override public void launchAppDetailsActivity(java.lang.String pkgName) throws android.os.RemoteException
    {
    }
    @Override public void resetStartRecordsBlocked() throws android.os.RemoteException
    {
    }
    @Override public void addApp(github.tornaco.android.thanos.core.pm.Pkg pkg) throws android.os.RemoteException
    {
    }
    @Override public boolean isStartRuleEnabled() throws android.os.RemoteException
    {
      return false;
    }
    @Override public void setStartRuleEnabled(boolean enable) throws android.os.RemoteException
    {
    }
    @Override public void addStartRule(java.lang.String rule) throws android.os.RemoteException
    {
    }
    @Override public void deleteStartRule(java.lang.String rule) throws android.os.RemoteException
    {
    }
    @Override public java.lang.String[] getAllStartRules() throws android.os.RemoteException
    {
      return null;
    }
    @Override public boolean isStandbyRuleEnabled() throws android.os.RemoteException
    {
      return false;
    }
    @Override public void setStandbyRuleEnabled(boolean enable) throws android.os.RemoteException
    {
    }
    @Override public void addStandbyRule(java.lang.String rule) throws android.os.RemoteException
    {
    }
    @Override public void deleteStandbyRule(java.lang.String rule) throws android.os.RemoteException
    {
    }
    @Override public java.lang.String[] getAllStandbyRules() throws android.os.RemoteException
    {
      return null;
    }
    @Override public github.tornaco.android.thanos.core.app.RunningServiceInfoCompat[] getRunningAppServiceForPackage(java.lang.String pkgName, int userId) throws android.os.RemoteException
    {
      return null;
    }
    @Override public boolean hasRunningServiceForPackage(java.lang.String pkgName, int userId) throws android.os.RemoteException
    {
      return false;
    }
    @Override public android.content.pm.UserInfo getUserInfo(int userHandle) throws android.os.RemoteException
    {
      return null;
    }
    @Override public boolean stopService(android.content.Intent intent) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean killBackgroundProcesses(github.tornaco.android.thanos.core.pm.Pkg pkg) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean isSmartStandByStopServiceEnabled() throws android.os.RemoteException
    {
      return false;
    }
    @Override public void setSmartStandByStopServiceEnabled(boolean enable) throws android.os.RemoteException
    {
    }
    @Override public boolean isSmartStandByInactiveEnabled() throws android.os.RemoteException
    {
      return false;
    }
    @Override public void setSmartStandByInactiveEnabled(boolean enable) throws android.os.RemoteException
    {
    }
    @Override public boolean isSmartStandByByPassIfHasNotificationEnabled() throws android.os.RemoteException
    {
      return false;
    }
    @Override public void setSmartStandByByPassIfHasNotificationEnabled(boolean enable) throws android.os.RemoteException
    {
    }
    @Override public boolean isSmartStandByBlockBgServiceStartEnabled() throws android.os.RemoteException
    {
      return false;
    }
    @Override public void setSmartStandByBlockBgServiceStartEnabled(boolean enable) throws android.os.RemoteException
    {
    }
    @Override public java.util.List<java.lang.String> getStartRecordAllowedPackages() throws android.os.RemoteException
    {
      return null;
    }
    @Override public long getStartRecordsAllowedCount() throws android.os.RemoteException
    {
      return 0L;
    }
    @Override public long getStartRecordAllowedCountByPackageName(java.lang.String pkgName) throws android.os.RemoteException
    {
      return 0L;
    }
    @Override public java.util.List<github.tornaco.android.thanos.core.app.start.StartRecord> getStartRecordsAllowedByPackageName(java.lang.String pkgName) throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.util.List<github.tornaco.android.thanos.core.app.start.StartRecord> getStartRecordsBlockedByPackageName(java.lang.String pkgName) throws android.os.RemoteException
    {
      return null;
    }
    @Override public void resetStartRecordsAllowed() throws android.os.RemoteException
    {
    }
    @Override public java.util.List<github.tornaco.android.thanos.core.app.start.StartRecord> getAllStartRecordsWithRes(int appFlags, boolean allowed, boolean blocked) throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.util.List<github.tornaco.android.thanos.core.app.start.StartRecord> getAllStartRecords(int appFlags) throws android.os.RemoteException
    {
      return null;
    }
    @Override public void dump(android.os.IBinder p) throws android.os.RemoteException
    {
    }
    @Override public void dumpCpu(android.os.IBinder p) throws android.os.RemoteException
    {
    }
    @Override public void setNetStatTrackerEnabled(boolean enabled) throws android.os.RemoteException
    {
    }
    @Override public boolean isNetStatTrackerEnabled() throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean checkGetContentProvider(java.lang.String callerPkg, java.lang.String name, int userId) throws android.os.RemoteException
    {
      return false;
    }
    @Override public java.util.List<github.tornaco.android.thanos.core.app.start.StartRecord> getAllStartRecordsForPackageSetWithRes(java.lang.String pkgSetId, boolean allowed, boolean blocked) throws android.os.RemoteException
    {
      return null;
    }
    // ******************************************************************
    // CAF API
    // https://source.android.com/devices/tech/perf/cached-apps-freezer
    //
    // ******************************************************************

    @Override public boolean isCachedAppsFreezerSupported() throws android.os.RemoteException
    {
      return false;
    }
    @Override public void freezeApp(github.tornaco.android.thanos.core.pm.Pkg pkg) throws android.os.RemoteException
    {
    }
    @Override public void unfreezeApp(github.tornaco.android.thanos.core.pm.Pkg pkg) throws android.os.RemoteException
    {
    }
    @Override public void freezeAppProcess(long pid) throws android.os.RemoteException
    {
    }
    @Override public void unfreezeAppProcess(long pid) throws android.os.RemoteException
    {
    }
    @Override public void updateProcessCpuUsageStats() throws android.os.RemoteException
    {
    }
    @Override public java.util.List<github.tornaco.android.thanos.core.app.usage.ProcessCpuUsageStats> queryProcessCpuUsageStats(long[] pids, boolean update) throws android.os.RemoteException
    {
      return null;
    }
    @Override public float queryCpuUsageRatio(long[] pids, boolean update) throws android.os.RemoteException
    {
      return 0.0f;
    }
    @Override public boolean killProcess(long pid) throws android.os.RemoteException
    {
      return false;
    }
    @Override public github.tornaco.android.thanos.core.os.SwapInfo getSwapInfo() throws android.os.RemoteException
    {
      return null;
    }
    // ******************************************************************
    // Block API
    // Block Receiver/Service/Provider all the time, event the package is running at foreground,
    // may cause the app crash.
    // ******************************************************************

    @Override public void setBlockAllReceiver(github.tornaco.android.thanos.core.pm.Pkg pkg, boolean block) throws android.os.RemoteException
    {
    }
    @Override public boolean isBlockAllReceiver(github.tornaco.android.thanos.core.pm.Pkg pkg) throws android.os.RemoteException
    {
      return false;
    }
    @Override public void setBlockAllService(github.tornaco.android.thanos.core.pm.Pkg pkg, boolean block) throws android.os.RemoteException
    {
    }
    @Override public boolean isBlockAllService(github.tornaco.android.thanos.core.pm.Pkg pkg) throws android.os.RemoteException
    {
      return false;
    }
    @Override public void setBlockAllProvider(github.tornaco.android.thanos.core.pm.Pkg pkg, boolean block) throws android.os.RemoteException
    {
    }
    @Override public boolean isBlockAllProvider(github.tornaco.android.thanos.core.pm.Pkg pkg) throws android.os.RemoteException
    {
      return false;
    }
    // Return 0 if it fail

    @Override public long getProcessStartTime(int pid) throws android.os.RemoteException
    {
      return 0L;
    }
    @Override public boolean isAppForeground(github.tornaco.android.thanos.core.pm.Pkg pkg) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean hasRunningForegroundService(github.tornaco.android.thanos.core.pm.Pkg pkg, int foregroundServicetype) throws android.os.RemoteException
    {
      return false;
    }
    @Override public java.util.List<github.tornaco.android.thanos.core.app.ActivityAssistInfo> getTopVisibleActivities() throws android.os.RemoteException
    {
      return null;
    }
    @Override public boolean checkStartActivity(android.content.Intent intent, int callerUid) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean isBgTaskCleanUpSkipForegroundEnabled() throws android.os.RemoteException
    {
      return false;
    }
    @Override public void setBgTaskCleanUpSkipForegroundEnabled(boolean enable) throws android.os.RemoteException
    {
    }
    /* return the pid of process. or -1 if no process found */
    @Override public int getPid(github.tornaco.android.thanos.core.os.ProcessName processName) throws android.os.RemoteException
    {
      return 0;
    }
    /* return the pid of killed process. or -1 if no process found */
    @Override public int killProcessByName(github.tornaco.android.thanos.core.os.ProcessName processName) throws android.os.RemoteException
    {
      return 0;
    }
    @Override public void killProcessByNames(java.util.List<github.tornaco.android.thanos.core.os.ProcessName> processNames) throws android.os.RemoteException
    {
    }
    @Override public boolean dumpHeap(java.lang.String process) throws android.os.RemoteException
    {
      return false;
    }
    @Override public github.tornaco.android.thanos.core.pm.Pkg getCurrentFrontPkg() throws android.os.RemoteException
    {
      return null;
    }
    @Override public boolean isSmartStandByByPassIfHasVisibleWindows() throws android.os.RemoteException
    {
      return false;
    }
    @Override public void setSmartStandByByPassIfHasVisibleWindowsEnabled(boolean enable) throws android.os.RemoteException
    {
    }
    @Override public float getTotalCpuPercent(boolean update) throws android.os.RemoteException
    {
      return 0.0f;
    }
    @Override public java.util.List<github.tornaco.android.thanos.core.app.usage.PkgCpuUsageStats> getTopNCpuUsagePackages(int n, boolean update) throws android.os.RemoteException
    {
      return null;
    }
    @Override public boolean isPkgResident(github.tornaco.android.thanos.core.pm.Pkg pkg) throws android.os.RemoteException
    {
      return false;
    }
    @Override public void setPkgResident(github.tornaco.android.thanos.core.pm.Pkg pkg, boolean resident) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements github.tornaco.android.thanos.core.app.IActivityManager
  {
    private static final java.lang.String DESCRIPTOR = "github.tornaco.android.thanos.core.app.IActivityManager";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an github.tornaco.android.thanos.core.app.IActivityManager interface,
     * generating a proxy if needed.
     */
    public static github.tornaco.android.thanos.core.app.IActivityManager asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof github.tornaco.android.thanos.core.app.IActivityManager))) {
        return ((github.tornaco.android.thanos.core.app.IActivityManager)iin);
      }
      return new github.tornaco.android.thanos.core.app.IActivityManager.Stub.Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      java.lang.String descriptor = DESCRIPTOR;
      switch (code)
      {
        case INTERFACE_TRANSACTION:
        {
          reply.writeString(descriptor);
          return true;
        }
        case TRANSACTION_getCurrentFrontApp:
        {
          data.enforceInterface(descriptor);
          java.lang.String _result = this.getCurrentFrontApp();
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        case TRANSACTION_forceStopPackage:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          java.lang.String _arg1;
          _arg1 = data.readString();
          this.forceStopPackage(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_idlePackage:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.idlePackage(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isPackageIdle:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          boolean _result = this.isPackageIdle(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_checkBroadcastingIntent:
        {
          data.enforceInterface(descriptor);
          android.content.Intent _arg0;
          if ((0!=data.readInt())) {
            _arg0 = android.content.Intent.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          boolean _result = this.checkBroadcastingIntent(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_checkService:
        {
          data.enforceInterface(descriptor);
          android.content.Intent _arg0;
          if ((0!=data.readInt())) {
            _arg0 = android.content.Intent.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          android.content.ComponentName _arg1;
          if ((0!=data.readInt())) {
            _arg1 = android.content.ComponentName.CREATOR.createFromParcel(data);
          }
          else {
            _arg1 = null;
          }
          int _arg2;
          _arg2 = data.readInt();
          int _arg3;
          _arg3 = data.readInt();
          boolean _result = this.checkService(_arg0, _arg1, _arg2, _arg3);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_checkRestartService:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          android.content.ComponentName _arg1;
          if ((0!=data.readInt())) {
            _arg1 = android.content.ComponentName.CREATOR.createFromParcel(data);
          }
          else {
            _arg1 = null;
          }
          boolean _result = this.checkRestartService(_arg0, _arg1);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_checkBroadcast:
        {
          data.enforceInterface(descriptor);
          android.content.Intent _arg0;
          if ((0!=data.readInt())) {
            _arg0 = android.content.Intent.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          int _arg1;
          _arg1 = data.readInt();
          int _arg2;
          _arg2 = data.readInt();
          boolean _result = this.checkBroadcast(_arg0, _arg1, _arg2);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_checkStartProcess:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          android.content.pm.ApplicationInfo _arg1;
          if ((0!=data.readInt())) {
            _arg1 = android.content.pm.ApplicationInfo.CREATOR.createFromParcel(data);
          }
          else {
            _arg1 = null;
          }
          java.lang.String _arg2;
          _arg2 = data.readString();
          java.lang.String _arg3;
          _arg3 = data.readString();
          boolean _result = this.checkStartProcess(_arg0, _arg1, _arg2, _arg3);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_onStartProcessLocked:
        {
          data.enforceInterface(descriptor);
          android.content.pm.ApplicationInfo _arg0;
          if ((0!=data.readInt())) {
            _arg0 = android.content.pm.ApplicationInfo.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.onStartProcessLocked(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getRunningAppProcess:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.process.ProcessRecord[] _result = this.getRunningAppProcess();
          reply.writeNoException();
          reply.writeTypedArray(_result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          return true;
        }
        case TRANSACTION_getRunningAppPackages:
        {
          data.enforceInterface(descriptor);
          java.util.List<github.tornaco.android.thanos.core.pm.Pkg> _result = this.getRunningAppPackages();
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_getRunningServiceLegacy:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          java.util.List<android.app.ActivityManager.RunningServiceInfo> _result = this.getRunningServiceLegacy(_arg0);
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_getRunningAppProcessLegacy:
        {
          data.enforceInterface(descriptor);
          java.util.List<github.tornaco.android.thanos.core.app.RunningAppProcessInfoCompat> _result = this.getRunningAppProcessLegacy();
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_getRunningAppsCount:
        {
          data.enforceInterface(descriptor);
          int _result = this.getRunningAppsCount();
          reply.writeNoException();
          reply.writeInt(_result);
          return true;
        }
        case TRANSACTION_getRunningAppProcessForPackage:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          java.util.List<github.tornaco.android.thanos.core.process.ProcessRecord> _result = this.getRunningAppProcessForPackage(_arg0);
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_isPackageRunning:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          boolean _result = this.isPackageRunning(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_getStartRecordsByPackageName:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.util.List<github.tornaco.android.thanos.core.app.start.StartRecord> _result = this.getStartRecordsByPackageName(_arg0);
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_getStartRecordBlockedPackages:
        {
          data.enforceInterface(descriptor);
          java.util.List<java.lang.String> _result = this.getStartRecordBlockedPackages();
          reply.writeNoException();
          reply.writeStringList(_result);
          return true;
        }
        case TRANSACTION_getStartRecordsBlockedCount:
        {
          data.enforceInterface(descriptor);
          long _result = this.getStartRecordsBlockedCount();
          reply.writeNoException();
          reply.writeLong(_result);
          return true;
        }
        case TRANSACTION_getStartRecordBlockedCountByPackageName:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          long _result = this.getStartRecordBlockedCountByPackageName(_arg0);
          reply.writeNoException();
          reply.writeLong(_result);
          return true;
        }
        case TRANSACTION_isStartBlockEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isStartBlockEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_setStartBlockEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setStartBlockEnabled(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_setPkgStartBlockEnabled:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          this.setPkgStartBlockEnabled(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isPkgStartBlocking:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          boolean _result = this.isPkgStartBlocking(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_isCleanUpOnTaskRemovalEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isCleanUpOnTaskRemovalEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_setCleanUpOnTaskRemovalEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setCleanUpOnTaskRemovalEnabled(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_setPkgCleanUpOnTaskRemovalEnabled:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          this.setPkgCleanUpOnTaskRemovalEnabled(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isPkgCleanUpOnTaskRemovalEnabled:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          boolean _result = this.isPkgCleanUpOnTaskRemovalEnabled(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_isBgRestrictEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isBgRestrictEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_setBgRestrictEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setBgRestrictEnabled(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_setPkgBgRestrictEnabled:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          this.setPkgBgRestrictEnabled(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isPkgBgRestricted:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          boolean _result = this.isPkgBgRestricted(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_setBgRestrictNotificationEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setBgRestrictNotificationEnabled(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isBgRestrictNotificationEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isBgRestrictNotificationEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_isRecentTaskBlurEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isRecentTaskBlurEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_setRecentTaskBlurEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setRecentTaskBlurEnabled(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_setPkgRecentTaskBlurEnabled:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          this.setPkgRecentTaskBlurEnabled(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isPkgRecentTaskBlurEnabled:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          boolean _result = this.isPkgRecentTaskBlurEnabled(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_isBgTaskCleanUpSkipAudioFocusedAppEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isBgTaskCleanUpSkipAudioFocusedAppEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_setBgTaskCleanUpSkipAudioFocusedAppEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setBgTaskCleanUpSkipAudioFocusedAppEnabled(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isBgTaskCleanUpSkipWhichHasNotificationEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isBgTaskCleanUpSkipWhichHasNotificationEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_setBgTaskCleanUpSkipWhichHasNotificationEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setBgTaskCleanUpSkipWhichHasNotificationEnabled(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_setBgTaskCleanUpDelayTimeMills:
        {
          data.enforceInterface(descriptor);
          long _arg0;
          _arg0 = data.readLong();
          this.setBgTaskCleanUpDelayTimeMills(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getBgTaskCleanUpDelayTimeMills:
        {
          data.enforceInterface(descriptor);
          long _result = this.getBgTaskCleanUpDelayTimeMills();
          reply.writeNoException();
          reply.writeLong(_result);
          return true;
        }
        case TRANSACTION_notifyTaskCreated:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          android.content.ComponentName _arg1;
          if ((0!=data.readInt())) {
            _arg1 = android.content.ComponentName.CREATOR.createFromParcel(data);
          }
          else {
            _arg1 = null;
          }
          this.notifyTaskCreated(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getMemoryInfo:
        {
          data.enforceInterface(descriptor);
          android.app.ActivityManager.MemoryInfo _result = this.getMemoryInfo();
          reply.writeNoException();
          if ((_result!=null)) {
            reply.writeInt(1);
            _result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          }
          else {
            reply.writeInt(0);
          }
          return true;
        }
        case TRANSACTION_getProcessPss:
        {
          data.enforceInterface(descriptor);
          int[] _arg0;
          _arg0 = data.createIntArray();
          long[] _result = this.getProcessPss(_arg0);
          reply.writeNoException();
          reply.writeLongArray(_result);
          return true;
        }
        case TRANSACTION_onApplicationCrashing:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          github.tornaco.android.thanos.core.process.ProcessRecord _arg2;
          if ((0!=data.readInt())) {
            _arg2 = github.tornaco.android.thanos.core.process.ProcessRecord.CREATOR.createFromParcel(data);
          }
          else {
            _arg2 = null;
          }
          java.lang.String _arg3;
          _arg3 = data.readString();
          this.onApplicationCrashing(_arg0, _arg1, _arg2, _arg3);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getPackageNameForTaskId:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          java.lang.String _result = this.getPackageNameForTaskId(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        case TRANSACTION_isPlatformAppIdleEnabled:
        {
          data.enforceInterface(descriptor);
          int _result = this.isPlatformAppIdleEnabled();
          reply.writeNoException();
          reply.writeInt(_result);
          return true;
        }
        case TRANSACTION_isSmartStandByEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isSmartStandByEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_setSmartStandByEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setSmartStandByEnabled(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_setPkgSmartStandByEnabled:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          this.setPkgSmartStandByEnabled(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isPkgSmartStandByEnabled:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          boolean _result = this.isPkgSmartStandByEnabled(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_getLastRecentUsedPackages:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          java.util.List<github.tornaco.android.thanos.core.pm.Pkg> _result = this.getLastRecentUsedPackages(_arg0);
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_getRecentTaskExcludeSettingForPackage:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          int _result = this.getRecentTaskExcludeSettingForPackage(_arg0);
          reply.writeNoException();
          reply.writeInt(_result);
          return true;
        }
        case TRANSACTION_setRecentTaskExcludeSettingForPackage:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          int _arg1;
          _arg1 = data.readInt();
          this.setRecentTaskExcludeSettingForPackage(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isBgTaskCleanUpSkipWhenHasRecentTaskEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isBgTaskCleanUpSkipWhenHasRecentTaskEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_setBgTaskCleanUpSkipWhenHasRecentTaskEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setBgTaskCleanUpSkipWhenHasRecentTaskEnabled(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_launchAppDetailsActivity:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.launchAppDetailsActivity(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_resetStartRecordsBlocked:
        {
          data.enforceInterface(descriptor);
          this.resetStartRecordsBlocked();
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_addApp:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.addApp(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isStartRuleEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isStartRuleEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_setStartRuleEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setStartRuleEnabled(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_addStartRule:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.addStartRule(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_deleteStartRule:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.deleteStartRule(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getAllStartRules:
        {
          data.enforceInterface(descriptor);
          java.lang.String[] _result = this.getAllStartRules();
          reply.writeNoException();
          reply.writeStringArray(_result);
          return true;
        }
        case TRANSACTION_isStandbyRuleEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isStandbyRuleEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_setStandbyRuleEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setStandbyRuleEnabled(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_addStandbyRule:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.addStandbyRule(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_deleteStandbyRule:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.deleteStandbyRule(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getAllStandbyRules:
        {
          data.enforceInterface(descriptor);
          java.lang.String[] _result = this.getAllStandbyRules();
          reply.writeNoException();
          reply.writeStringArray(_result);
          return true;
        }
        case TRANSACTION_getRunningAppServiceForPackage:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          int _arg1;
          _arg1 = data.readInt();
          github.tornaco.android.thanos.core.app.RunningServiceInfoCompat[] _result = this.getRunningAppServiceForPackage(_arg0, _arg1);
          reply.writeNoException();
          reply.writeTypedArray(_result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          return true;
        }
        case TRANSACTION_hasRunningServiceForPackage:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          int _arg1;
          _arg1 = data.readInt();
          boolean _result = this.hasRunningServiceForPackage(_arg0, _arg1);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_getUserInfo:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          android.content.pm.UserInfo _result = this.getUserInfo(_arg0);
          reply.writeNoException();
          if ((_result!=null)) {
            reply.writeInt(1);
            _result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          }
          else {
            reply.writeInt(0);
          }
          return true;
        }
        case TRANSACTION_stopService:
        {
          data.enforceInterface(descriptor);
          android.content.Intent _arg0;
          if ((0!=data.readInt())) {
            _arg0 = android.content.Intent.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          boolean _result = this.stopService(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_killBackgroundProcesses:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          boolean _result = this.killBackgroundProcesses(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_isSmartStandByStopServiceEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isSmartStandByStopServiceEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_setSmartStandByStopServiceEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setSmartStandByStopServiceEnabled(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isSmartStandByInactiveEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isSmartStandByInactiveEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_setSmartStandByInactiveEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setSmartStandByInactiveEnabled(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isSmartStandByByPassIfHasNotificationEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isSmartStandByByPassIfHasNotificationEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_setSmartStandByByPassIfHasNotificationEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setSmartStandByByPassIfHasNotificationEnabled(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isSmartStandByBlockBgServiceStartEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isSmartStandByBlockBgServiceStartEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_setSmartStandByBlockBgServiceStartEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setSmartStandByBlockBgServiceStartEnabled(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getStartRecordAllowedPackages:
        {
          data.enforceInterface(descriptor);
          java.util.List<java.lang.String> _result = this.getStartRecordAllowedPackages();
          reply.writeNoException();
          reply.writeStringList(_result);
          return true;
        }
        case TRANSACTION_getStartRecordsAllowedCount:
        {
          data.enforceInterface(descriptor);
          long _result = this.getStartRecordsAllowedCount();
          reply.writeNoException();
          reply.writeLong(_result);
          return true;
        }
        case TRANSACTION_getStartRecordAllowedCountByPackageName:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          long _result = this.getStartRecordAllowedCountByPackageName(_arg0);
          reply.writeNoException();
          reply.writeLong(_result);
          return true;
        }
        case TRANSACTION_getStartRecordsAllowedByPackageName:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.util.List<github.tornaco.android.thanos.core.app.start.StartRecord> _result = this.getStartRecordsAllowedByPackageName(_arg0);
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_getStartRecordsBlockedByPackageName:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.util.List<github.tornaco.android.thanos.core.app.start.StartRecord> _result = this.getStartRecordsBlockedByPackageName(_arg0);
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_resetStartRecordsAllowed:
        {
          data.enforceInterface(descriptor);
          this.resetStartRecordsAllowed();
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getAllStartRecordsWithRes:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          boolean _arg2;
          _arg2 = (0!=data.readInt());
          java.util.List<github.tornaco.android.thanos.core.app.start.StartRecord> _result = this.getAllStartRecordsWithRes(_arg0, _arg1, _arg2);
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_getAllStartRecords:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          java.util.List<github.tornaco.android.thanos.core.app.start.StartRecord> _result = this.getAllStartRecords(_arg0);
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_dump:
        {
          data.enforceInterface(descriptor);
          android.os.IBinder _arg0;
          _arg0 = data.readStrongBinder();
          this.dump(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_dumpCpu:
        {
          data.enforceInterface(descriptor);
          android.os.IBinder _arg0;
          _arg0 = data.readStrongBinder();
          this.dumpCpu(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_setNetStatTrackerEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setNetStatTrackerEnabled(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isNetStatTrackerEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isNetStatTrackerEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_checkGetContentProvider:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          int _arg2;
          _arg2 = data.readInt();
          boolean _result = this.checkGetContentProvider(_arg0, _arg1, _arg2);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_getAllStartRecordsForPackageSetWithRes:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          boolean _arg2;
          _arg2 = (0!=data.readInt());
          java.util.List<github.tornaco.android.thanos.core.app.start.StartRecord> _result = this.getAllStartRecordsForPackageSetWithRes(_arg0, _arg1, _arg2);
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_isCachedAppsFreezerSupported:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isCachedAppsFreezerSupported();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_freezeApp:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.freezeApp(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_unfreezeApp:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.unfreezeApp(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_freezeAppProcess:
        {
          data.enforceInterface(descriptor);
          long _arg0;
          _arg0 = data.readLong();
          this.freezeAppProcess(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_unfreezeAppProcess:
        {
          data.enforceInterface(descriptor);
          long _arg0;
          _arg0 = data.readLong();
          this.unfreezeAppProcess(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_updateProcessCpuUsageStats:
        {
          data.enforceInterface(descriptor);
          this.updateProcessCpuUsageStats();
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_queryProcessCpuUsageStats:
        {
          data.enforceInterface(descriptor);
          long[] _arg0;
          _arg0 = data.createLongArray();
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          java.util.List<github.tornaco.android.thanos.core.app.usage.ProcessCpuUsageStats> _result = this.queryProcessCpuUsageStats(_arg0, _arg1);
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_queryCpuUsageRatio:
        {
          data.enforceInterface(descriptor);
          long[] _arg0;
          _arg0 = data.createLongArray();
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          float _result = this.queryCpuUsageRatio(_arg0, _arg1);
          reply.writeNoException();
          reply.writeFloat(_result);
          return true;
        }
        case TRANSACTION_killProcess:
        {
          data.enforceInterface(descriptor);
          long _arg0;
          _arg0 = data.readLong();
          boolean _result = this.killProcess(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_getSwapInfo:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.os.SwapInfo _result = this.getSwapInfo();
          reply.writeNoException();
          if ((_result!=null)) {
            reply.writeInt(1);
            _result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          }
          else {
            reply.writeInt(0);
          }
          return true;
        }
        case TRANSACTION_setBlockAllReceiver:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          this.setBlockAllReceiver(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isBlockAllReceiver:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          boolean _result = this.isBlockAllReceiver(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_setBlockAllService:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          this.setBlockAllService(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isBlockAllService:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          boolean _result = this.isBlockAllService(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_setBlockAllProvider:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          this.setBlockAllProvider(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_isBlockAllProvider:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          boolean _result = this.isBlockAllProvider(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_getProcessStartTime:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          long _result = this.getProcessStartTime(_arg0);
          reply.writeNoException();
          reply.writeLong(_result);
          return true;
        }
        case TRANSACTION_isAppForeground:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          boolean _result = this.isAppForeground(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_hasRunningForegroundService:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          int _arg1;
          _arg1 = data.readInt();
          boolean _result = this.hasRunningForegroundService(_arg0, _arg1);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_getTopVisibleActivities:
        {
          data.enforceInterface(descriptor);
          java.util.List<github.tornaco.android.thanos.core.app.ActivityAssistInfo> _result = this.getTopVisibleActivities();
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_checkStartActivity:
        {
          data.enforceInterface(descriptor);
          android.content.Intent _arg0;
          if ((0!=data.readInt())) {
            _arg0 = android.content.Intent.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          int _arg1;
          _arg1 = data.readInt();
          boolean _result = this.checkStartActivity(_arg0, _arg1);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_isBgTaskCleanUpSkipForegroundEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isBgTaskCleanUpSkipForegroundEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_setBgTaskCleanUpSkipForegroundEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setBgTaskCleanUpSkipForegroundEnabled(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getPid:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.os.ProcessName _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.os.ProcessName.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          int _result = this.getPid(_arg0);
          reply.writeNoException();
          reply.writeInt(_result);
          return true;
        }
        case TRANSACTION_killProcessByName:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.os.ProcessName _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.os.ProcessName.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          int _result = this.killProcessByName(_arg0);
          reply.writeNoException();
          reply.writeInt(_result);
          return true;
        }
        case TRANSACTION_killProcessByNames:
        {
          data.enforceInterface(descriptor);
          java.util.List<github.tornaco.android.thanos.core.os.ProcessName> _arg0;
          _arg0 = data.createTypedArrayList(github.tornaco.android.thanos.core.os.ProcessName.CREATOR);
          this.killProcessByNames(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_dumpHeap:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _result = this.dumpHeap(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_getCurrentFrontPkg:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _result = this.getCurrentFrontPkg();
          reply.writeNoException();
          if ((_result!=null)) {
            reply.writeInt(1);
            _result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          }
          else {
            reply.writeInt(0);
          }
          return true;
        }
        case TRANSACTION_isSmartStandByByPassIfHasVisibleWindows:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isSmartStandByByPassIfHasVisibleWindows();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_setSmartStandByByPassIfHasVisibleWindowsEnabled:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setSmartStandByByPassIfHasVisibleWindowsEnabled(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getTotalCpuPercent:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          float _result = this.getTotalCpuPercent(_arg0);
          reply.writeNoException();
          reply.writeFloat(_result);
          return true;
        }
        case TRANSACTION_getTopNCpuUsagePackages:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          java.util.List<github.tornaco.android.thanos.core.app.usage.PkgCpuUsageStats> _result = this.getTopNCpuUsagePackages(_arg0, _arg1);
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_isPkgResident:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          boolean _result = this.isPkgResident(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_setPkgResident:
        {
          data.enforceInterface(descriptor);
          github.tornaco.android.thanos.core.pm.Pkg _arg0;
          if ((0!=data.readInt())) {
            _arg0 = github.tornaco.android.thanos.core.pm.Pkg.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          this.setPkgResident(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements github.tornaco.android.thanos.core.app.IActivityManager
    {
      private android.os.IBinder mRemote;
      Proxy(android.os.IBinder remote)
      {
        mRemote = remote;
      }
      @Override public android.os.IBinder asBinder()
      {
        return mRemote;
      }
      public java.lang.String getInterfaceDescriptor()
      {
        return DESCRIPTOR;
      }
      @Override public java.lang.String getCurrentFrontApp() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getCurrentFrontApp, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getCurrentFrontApp();
          }
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void forceStopPackage(github.tornaco.android.thanos.core.pm.Pkg pkg, java.lang.String reason) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((pkg!=null)) {
            _data.writeInt(1);
            pkg.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          _data.writeString(reason);
          boolean _status = mRemote.transact(Stub.TRANSACTION_forceStopPackage, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().forceStopPackage(pkg, reason);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void idlePackage(github.tornaco.android.thanos.core.pm.Pkg pkg) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((pkg!=null)) {
            _data.writeInt(1);
            pkg.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_idlePackage, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().idlePackage(pkg);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean isPackageIdle(github.tornaco.android.thanos.core.pm.Pkg pkg) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((pkg!=null)) {
            _data.writeInt(1);
            pkg.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_isPackageIdle, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isPackageIdle(pkg);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean checkBroadcastingIntent(android.content.Intent intent) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((intent!=null)) {
            _data.writeInt(1);
            intent.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_checkBroadcastingIntent, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().checkBroadcastingIntent(intent);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean checkService(android.content.Intent intent, android.content.ComponentName service, int callerUid, int userId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((intent!=null)) {
            _data.writeInt(1);
            intent.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          if ((service!=null)) {
            _data.writeInt(1);
            service.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          _data.writeInt(callerUid);
          _data.writeInt(userId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_checkService, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().checkService(intent, service, callerUid, userId);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean checkRestartService(java.lang.String packageName, android.content.ComponentName componentName) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(packageName);
          if ((componentName!=null)) {
            _data.writeInt(1);
            componentName.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_checkRestartService, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().checkRestartService(packageName, componentName);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean checkBroadcast(android.content.Intent intent, int receiverUid, int callerUid) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((intent!=null)) {
            _data.writeInt(1);
            intent.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          _data.writeInt(receiverUid);
          _data.writeInt(callerUid);
          boolean _status = mRemote.transact(Stub.TRANSACTION_checkBroadcast, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().checkBroadcast(intent, receiverUid, callerUid);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean checkStartProcess(java.lang.String processName, android.content.pm.ApplicationInfo applicationInfo, java.lang.String hostType, java.lang.String hostName) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(processName);
          if ((applicationInfo!=null)) {
            _data.writeInt(1);
            applicationInfo.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          _data.writeString(hostType);
          _data.writeString(hostName);
          boolean _status = mRemote.transact(Stub.TRANSACTION_checkStartProcess, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().checkStartProcess(processName, applicationInfo, hostType, hostName);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void onStartProcessLocked(android.content.pm.ApplicationInfo appInfo) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((appInfo!=null)) {
            _data.writeInt(1);
            appInfo.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_onStartProcessLocked, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onStartProcessLocked(appInfo);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public github.tornaco.android.thanos.core.process.ProcessRecord[] getRunningAppProcess() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        github.tornaco.android.thanos.core.process.ProcessRecord[] _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getRunningAppProcess, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getRunningAppProcess();
          }
          _reply.readException();
          _result = _reply.createTypedArray(github.tornaco.android.thanos.core.process.ProcessRecord.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.util.List<github.tornaco.android.thanos.core.pm.Pkg> getRunningAppPackages() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<github.tornaco.android.thanos.core.pm.Pkg> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getRunningAppPackages, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getRunningAppPackages();
          }
          _reply.readException();
          _result = _reply.createTypedArrayList(github.tornaco.android.thanos.core.pm.Pkg.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.util.List<android.app.ActivityManager.RunningServiceInfo> getRunningServiceLegacy(int max) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<android.app.ActivityManager.RunningServiceInfo> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(max);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getRunningServiceLegacy, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getRunningServiceLegacy(max);
          }
          _reply.readException();
          _result = _reply.createTypedArrayList(android.app.ActivityManager.RunningServiceInfo.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.util.List<github.tornaco.android.thanos.core.app.RunningAppProcessInfoCompat> getRunningAppProcessLegacy() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<github.tornaco.android.thanos.core.app.RunningAppProcessInfoCompat> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getRunningAppProcessLegacy, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getRunningAppProcessLegacy();
          }
          _reply.readException();
          _result = _reply.createTypedArrayList(github.tornaco.android.thanos.core.app.RunningAppProcessInfoCompat.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public int getRunningAppsCount() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getRunningAppsCount, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getRunningAppsCount();
          }
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.util.List<github.tornaco.android.thanos.core.process.ProcessRecord> getRunningAppProcessForPackage(github.tornaco.android.thanos.core.pm.Pkg pkg) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<github.tornaco.android.thanos.core.process.ProcessRecord> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((pkg!=null)) {
            _data.writeInt(1);
            pkg.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_getRunningAppProcessForPackage, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getRunningAppProcessForPackage(pkg);
          }
          _reply.readException();
          _result = _reply.createTypedArrayList(github.tornaco.android.thanos.core.process.ProcessRecord.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean isPackageRunning(github.tornaco.android.thanos.core.pm.Pkg pkg) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((pkg!=null)) {
            _data.writeInt(1);
            pkg.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_isPackageRunning, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isPackageRunning(pkg);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.util.List<github.tornaco.android.thanos.core.app.start.StartRecord> getStartRecordsByPackageName(java.lang.String pkgName) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<github.tornaco.android.thanos.core.app.start.StartRecord> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(pkgName);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getStartRecordsByPackageName, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getStartRecordsByPackageName(pkgName);
          }
          _reply.readException();
          _result = _reply.createTypedArrayList(github.tornaco.android.thanos.core.app.start.StartRecord.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.util.List<java.lang.String> getStartRecordBlockedPackages() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<java.lang.String> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getStartRecordBlockedPackages, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getStartRecordBlockedPackages();
          }
          _reply.readException();
          _result = _reply.createStringArrayList();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public long getStartRecordsBlockedCount() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        long _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getStartRecordsBlockedCount, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getStartRecordsBlockedCount();
          }
          _reply.readException();
          _result = _reply.readLong();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public long getStartRecordBlockedCountByPackageName(java.lang.String pkgName) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        long _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(pkgName);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getStartRecordBlockedCountByPackageName, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getStartRecordBlockedCountByPackageName(pkgName);
          }
          _reply.readException();
          _result = _reply.readLong();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      // 启动管理设置

      @Override public boolean isStartBlockEnabled() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isStartBlockEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isStartBlockEnabled();
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void setStartBlockEnabled(boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setStartBlockEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setStartBlockEnabled(enable);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void setPkgStartBlockEnabled(github.tornaco.android.thanos.core.pm.Pkg pkg, boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((pkg!=null)) {
            _data.writeInt(1);
            pkg.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setPkgStartBlockEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setPkgStartBlockEnabled(pkg, enable);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean isPkgStartBlocking(github.tornaco.android.thanos.core.pm.Pkg pkg) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((pkg!=null)) {
            _data.writeInt(1);
            pkg.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_isPkgStartBlocking, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isPkgStartBlocking(pkg);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      // Task removal

      @Override public boolean isCleanUpOnTaskRemovalEnabled() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isCleanUpOnTaskRemovalEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isCleanUpOnTaskRemovalEnabled();
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void setCleanUpOnTaskRemovalEnabled(boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setCleanUpOnTaskRemovalEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setCleanUpOnTaskRemovalEnabled(enable);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void setPkgCleanUpOnTaskRemovalEnabled(github.tornaco.android.thanos.core.pm.Pkg pkg, boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((pkg!=null)) {
            _data.writeInt(1);
            pkg.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setPkgCleanUpOnTaskRemovalEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setPkgCleanUpOnTaskRemovalEnabled(pkg, enable);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean isPkgCleanUpOnTaskRemovalEnabled(github.tornaco.android.thanos.core.pm.Pkg pkg) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((pkg!=null)) {
            _data.writeInt(1);
            pkg.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_isPkgCleanUpOnTaskRemovalEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isPkgCleanUpOnTaskRemovalEnabled(pkg);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      // 后台运行设置

      @Override public boolean isBgRestrictEnabled() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isBgRestrictEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isBgRestrictEnabled();
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void setBgRestrictEnabled(boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setBgRestrictEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setBgRestrictEnabled(enable);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void setPkgBgRestrictEnabled(github.tornaco.android.thanos.core.pm.Pkg pkg, boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((pkg!=null)) {
            _data.writeInt(1);
            pkg.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setPkgBgRestrictEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setPkgBgRestrictEnabled(pkg, enable);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean isPkgBgRestricted(github.tornaco.android.thanos.core.pm.Pkg pkg) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((pkg!=null)) {
            _data.writeInt(1);
            pkg.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_isPkgBgRestricted, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isPkgBgRestricted(pkg);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void setBgRestrictNotificationEnabled(boolean enabled) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((enabled)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setBgRestrictNotificationEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setBgRestrictNotificationEnabled(enabled);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean isBgRestrictNotificationEnabled() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isBgRestrictNotificationEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isBgRestrictNotificationEnabled();
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      // Task blur

      @Override public boolean isRecentTaskBlurEnabled() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isRecentTaskBlurEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isRecentTaskBlurEnabled();
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void setRecentTaskBlurEnabled(boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setRecentTaskBlurEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setRecentTaskBlurEnabled(enable);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void setPkgRecentTaskBlurEnabled(github.tornaco.android.thanos.core.pm.Pkg pkg, boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((pkg!=null)) {
            _data.writeInt(1);
            pkg.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setPkgRecentTaskBlurEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setPkgRecentTaskBlurEnabled(pkg, enable);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean isPkgRecentTaskBlurEnabled(github.tornaco.android.thanos.core.pm.Pkg pkg) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((pkg!=null)) {
            _data.writeInt(1);
            pkg.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_isPkgRecentTaskBlurEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isPkgRecentTaskBlurEnabled(pkg);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      // Audio focused app.

      @Override public boolean isBgTaskCleanUpSkipAudioFocusedAppEnabled() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isBgTaskCleanUpSkipAudioFocusedAppEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isBgTaskCleanUpSkipAudioFocusedAppEnabled();
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void setBgTaskCleanUpSkipAudioFocusedAppEnabled(boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setBgTaskCleanUpSkipAudioFocusedAppEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setBgTaskCleanUpSkipAudioFocusedAppEnabled(enable);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      // Notification record app.

      @Override public boolean isBgTaskCleanUpSkipWhichHasNotificationEnabled() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isBgTaskCleanUpSkipWhichHasNotificationEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isBgTaskCleanUpSkipWhichHasNotificationEnabled();
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void setBgTaskCleanUpSkipWhichHasNotificationEnabled(boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setBgTaskCleanUpSkipWhichHasNotificationEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setBgTaskCleanUpSkipWhichHasNotificationEnabled(enable);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      // 后台运行锁屏清理延迟

      @Override public void setBgTaskCleanUpDelayTimeMills(long delayMills) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeLong(delayMills);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setBgTaskCleanUpDelayTimeMills, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setBgTaskCleanUpDelayTimeMills(delayMills);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public long getBgTaskCleanUpDelayTimeMills() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        long _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getBgTaskCleanUpDelayTimeMills, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getBgTaskCleanUpDelayTimeMills();
          }
          _reply.readException();
          _result = _reply.readLong();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void notifyTaskCreated(int taskId, android.content.ComponentName componentName) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(taskId);
          if ((componentName!=null)) {
            _data.writeInt(1);
            componentName.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_notifyTaskCreated, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().notifyTaskCreated(taskId, componentName);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public android.app.ActivityManager.MemoryInfo getMemoryInfo() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.app.ActivityManager.MemoryInfo _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getMemoryInfo, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getMemoryInfo();
          }
          _reply.readException();
          if ((0!=_reply.readInt())) {
            _result = android.app.ActivityManager.MemoryInfo.CREATOR.createFromParcel(_reply);
          }
          else {
            _result = null;
          }
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public long[] getProcessPss(int[] pids) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        long[] _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeIntArray(pids);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getProcessPss, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getProcessPss(pids);
          }
          _reply.readException();
          _result = _reply.createLongArray();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void onApplicationCrashing(java.lang.String eventType, java.lang.String processName, github.tornaco.android.thanos.core.process.ProcessRecord process, java.lang.String stackTrace) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(eventType);
          _data.writeString(processName);
          if ((process!=null)) {
            _data.writeInt(1);
            process.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          _data.writeString(stackTrace);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onApplicationCrashing, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onApplicationCrashing(eventType, processName, process, stackTrace);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public java.lang.String getPackageNameForTaskId(int taskId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(taskId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getPackageNameForTaskId, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getPackageNameForTaskId(taskId);
          }
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public int isPlatformAppIdleEnabled() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isPlatformAppIdleEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isPlatformAppIdleEnabled();
          }
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean isSmartStandByEnabled() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isSmartStandByEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isSmartStandByEnabled();
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void setSmartStandByEnabled(boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setSmartStandByEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setSmartStandByEnabled(enable);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void setPkgSmartStandByEnabled(github.tornaco.android.thanos.core.pm.Pkg pkg, boolean enable) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((pkg!=null)) {
            _data.writeInt(1);
            pkg.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          _data.writeInt(((enable)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setPkgSmartStandByEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setPkgSmartStandByEnabled(pkg, enable);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean isPkgSmartStandByEnabled(github.tornaco.android.thanos.core.pm.Pkg pkg) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((pkg!=null)) {
            _data.writeInt(1);
            pkg.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_isPkgSmartStandByEnabled, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isPkgSmartStandByEnabled(pkg);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.util.List<github.tornaco.android.thanos.core.pm.Pkg> getLastRecentUsedPackages(int count) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<github.tornaco.android.thanos.core.pm.Pkg> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(count);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getLastRecentUsedPackages, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getLastRecentUsedPackages(count);
          }
          _reply.readException();
          _result = _reply.createTypedArrayList(github.tornaco.android.thanos.core.pm.Pkg.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
       