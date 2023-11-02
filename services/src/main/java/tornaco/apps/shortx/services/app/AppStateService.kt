package tornaco.apps.shortx.services.app

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.ActivityTaskManager
import android.app.IActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.os.Process
import android.os.ServiceManager
import android.os.UserHandle
import android.widget.Toast
import github.tornaco.android.thanos.core.app.ThanosManagerNative
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import tornaco.apps.shortx.core.*
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.AppComponent
import tornaco.apps.shortx.core.proto.common.AppComponentInfo
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.ProcessName
import tornaco.apps.shortx.core.proto.common.ProcessRecord
import tornaco.apps.shortx.core.proto.common.RecentTask
import tornaco.apps.shortx.core.proto.fact.AppProcessStarted
import tornaco.apps.shortx.core.proto.fact.PkgStopRunning
import tornaco.apps.shortx.core.proto.fact.TaskRemoved
import tornaco.apps.shortx.core.store.WRITE_DELAY
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.PkgUtils
import tornaco.apps.shortx.core.util.activityManager
import tornaco.apps.shortx.core.util.appPkgToString
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.*
import tornaco.apps.shortx.services.android.ServiceRetriever
import tornaco.apps.shortx.services.app.pm.*
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.os.UserEvent
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.services.watchdog.launchSafety
import tornaco.apps.shortx.services.watchdog.postSafety
import java.util.*

class AppStateService(shortXContext: ShortXContext) :
    SystemService(shortXContext, "AppStateService") {
    private val pkgPools = mutableListOf<PkgPool>()

    private val onPackageUpdatedListeners: MutableList<((App) -> Unit)> = mutableListOf()
    private val onPackageAddedListeners: MutableList<((App) -> Unit)> = mutableListOf()
    private val onPackageRemovedListeners: MutableList<((App) -> Unit)> = mutableListOf()

    private val enableAppPkg by lazy { PmSetAppEnabledApplicationPackageEnabler(context) }
    private val suspendAppPkg by lazy { PmSuspendApplicationPackageEnabler(context) }

    private val activityStartResultOneTimeListeners =
        mutableListOf<ActivityStartResultOneTimeListener>()

    private val ams by lazy {
        ServiceRetriever(context.classLoader).getAMS()
    }
    private val activeServices by lazy {
        ServiceRetriever(context.classLoader).getActiveServices()
    }

    private val activityState by lazy {
        ActivityStateHandlerImpl(context, shortXContext)
    }

    private val homePkgs = mutableListOf<String>()

    override fun systemReady() {
        super.systemReady()
        systemServiceScope.launchSafety {
            shortXContext.user.getUsers().forEach {
                val pool = initPoolForUser(it.id)
                pkgPools.add(pool)
            }
        }

        systemServiceScope.launchSafety {
            shortXContext.user.userEvent.collectLatest { event ->
                when (event) {
                    is UserEvent.Added -> {
                        if (pkgPools.firstOrNull { it.userId == event.userId } == null) {
                            logger.w("User added: ${event.userId}, init pool for it.")
                            val pool = initPoolForUser(event.userId)
                            pkgPools.add(pool)
                        }
                    }

                    is UserEvent.Removed -> {
                        logger.w("User removed: ${event.userId}, remove pool for it.")
                        pkgPools.removeIf { it.userId == event.userId }
                    }
                }
            }
        }

        logger.w("AMS: $ams")
    }

    private suspend fun initPoolForUser(id: Int): PkgPool {
        val pool = PkgPool(
            context = context,
            handler = handler,
            userId = id,
            onPackageUpdateFinished = { changed ->
                handler.postSafety {
                    onPackageUpdatedListeners.forEach { it(changed) }
                }
            },
            onPackageAdded = { added ->
                handler.postSafety {
                    onPackageAddedListeners.forEach { it(added) }
                }
            },
            onPackageRemoved = { removed ->
                handler.postSafety {
                    if (removed.pkg.pkgName == BuildProp.APP_PACKAGE_NAME) {
                        onShortXRemoved()
                    }
                    onPackageRemovedListeners.forEach { it(removed) }
                }
            }
        ).apply {
            init()
            load()
        }
        return pool
    }

    private fun onShortXRemoved() {
        logger.w("onShortXRemoved.")
        // Reset
        shortXContext.rule.getAllEnabledRules().map { it.id }.forEach {
            shortXContext.rule.setRuleEnabled(it, false)
        }
        shortXContext.rule.cancelJobs(shortXContext.rule.getActiveJobs())



        systemServiceScope.launchSafety {
            // After all file persisted.
            delay(WRITE_DELAY + 100L)

            Dirs.baseServerDir().deleteRecursively()
            logger.w("onShortXRemoved, all file deleted!")
            Toast.makeText(context, i18NIns["ui.shortx.removed.toast"], Toast.LENGTH_LONG)
                .show()

        }
    }

    override fun shutdown() {
        super.shutdown()
        pkgPools.forEach {
            it.destroy()
        }
    }

    fun AppPkg.isFront(): Boolean {
        return activityState.isFront(this)
    }

    fun currentFrontPkgs(): List<AppPkg> {
        return activityState.currentFrontAppPkgs.toList()
    }

    fun onActivityResumed(componentName: ComponentName, userId: Int, intentUri: String) {
        activityState.onActivityResumed(componentName, userId, intentUri)
    }

    fun onActivityStopped(componentName: ComponentName, userId: Int) {
        activityState.onActivityStopped(componentName, userId)
    }

    fun AppPkg.isRunning() = AMSFuncs.hasRunningAppProcessForPackage(context, this)

    fun AppComponent.isServiceRunning() = AMSFuncs.isServiceRunning(context, this)

    private fun poolForUser(userId: Int) = pkgPools.firstOrNull { it.userId == userId }

    fun getInstalledApps(): List<App> {
        return pkgPools.map {
            it.withAllApps { values }
        }.flatten()
    }

    fun uidToApp(uid: Int): App? {
        val userId = UserHandle.getUserId(uid)
        return poolForUser(userId)?.let { pool ->
            pool.withAllApps {
                values.firstOrNull { it.uid == uid }
            }
        }
    }

    fun pkgToApp(pkg: AppPkg): App? {
        return pkgPools.firstOrNull { it.userId == pkg.userId }?.withAllApps {
            get(pkg.pkgName)
        }
    }

    fun getAppLabel(pkg: AppPkg): String? =
        pkgPools.firstOrNull { it.userId == pkg.userId }?.withAllApps {
            get(pkg.pkgName)?.label
        }

    fun onPackageUpdated(listener: (App) -> Unit) {
        onPackageUpdatedListeners.add(listener)
    }

    fun onPackageAdded(listener: (App) -> Unit) {
        onPackageAddedListeners.add(listener)
    }

    fun onPackageRemoved(listener: (App) -> Unit) {
        onPackageRemovedListeners.add(listener)
    }

    @SuppressLint("MissingPermission")
    fun launchApp(appPkg: AppPkg?) {
        logger.d("launchApp: $appPkg")
        appPkg?.let {
            context.packageManager.getLaunchIntentForPackage(appPkg.pkgName)?.let { intent ->
                logger.d("launchApp, intent: $intent")
                context.startActivityAsUser(intent, UserHandle.of(appPkg.userId))
            }
        }
    }

    fun forceStopApp(appPkg: AppPkg, reason: String) {
        logger.w("forceStopApp, $appPkg $reason")
        systemServiceScope.launchSafety {
            val amForceStop =
                { context.activityManager.forceStopPackageAsUser(appPkg.pkgName, appPkg.userId) }
            amForceStop()
            delay(500)
            if (!appPkg.isFront() && appPkg.isRunning()) {
                logger.w("forceStopApp should kill again: $appPkg")
                amForceStop()
            }
        }
    }

    fun forceStopApp(appPkgList: List<AppPkg>, reason: String) {
        logger.w("forceStopApp, $appPkgList $reason")
        systemServiceScope.launchSafety {
            appPkgList.forEach {
                context.activityManager.forceStopPackageAsUser(it.pkgName, it.userId)
            }

            delay(500)

            appPkgList.forEach {
                if (!it.isFront() && it.isRunning()) {
                    logger.w("forceStopApp should kill again: $it")
                    context.activityManager.forceStopPackageAsUser(it.pkgName, it.userId)
                }
            }
        }
    }

    fun startAppProcess(appPkgList: List<AppPkg>, reason: String) {
        logger.w("startAppProcess, $appPkgList $reason")
        systemServiceScope.launchSafety {
            appPkgList.forEach { targetPkg ->
                kotlin.runCatching {
                    val info: ApplicationInfo? = PkgUtils.getApplicationInfoAsUser(
                        context,
                        targetPkg.pkgName,
                        targetPkg.userId
                    )
                    logger.d("startAppProcess, app: $info ams: $ams")
                    info?.let { app ->
                        ams?.let {
                            AMSFuncs.addAppLocked(
                                it,
                                app
                            )
                        }
                    }
                }.onFailure {
                    logger.e(it, "startAppProcess got error")
                }
                // Not sure if this is really needed.
                delay(200)
            }
        }
    }

    fun setAppEnabled(appPkgList: List<AppPkg>, enable: Boolean, reason: String) {
        logger.w("setAppEnabled, enable: $enable $appPkgList $reason")
        systemServiceScope.launchSafety {
            appPkgList.forEach {
                if (enable) enableAppPkg.enable(it) else enableAppPkg.disable(it)
            }
        }
    }

    fun setAppSuspend(appPkgList: List<AppPkg>, suspend: Boolean, reason: String) {
        logger.w("setAppSuspend, suspend: $suspend $appPkgList $reason")
        systemServiceScope.launchSafety {
            appPkgList.forEach {
                if (suspend) suspendAppPkg.disable(it) else suspendAppPkg.enable(it)
            }
        }
    }

    fun getActivities(
        userId: Int,
        packageName: String
    ): List<AppComponentInfo> {
        val pm = context.packageManager
        return runClearCallingIdentity {
            ComponentUtil.getActivities(context, packageName)
                .mapNotNull {
                    ComponentMapper.activityInfoToAppComponentInfo(userId, pm, it)
                }
        } ?: emptyList()
    }

    fun getReceivers(
        userId: Int,
        packageName: String
    ): List<AppComponentInfo> {
        val pm = context.packageManager
        return runClearCallingIdentity {
            ComponentUtil.getBroadcasts(context, packageName)
                .mapNotNull {
                    ComponentMapper.activityInfoToAppComponentInfo(userId, pm, it)
                }
        } ?: emptyList()
    }

    fun getServices(userId: Int, packageName: String): List<AppComponentInfo> {
        val pm = context.packageManager
        return runClearCallingIdentity {
            ComponentUtil.getServices(context, packageName)
                .mapNotNull {
                    ComponentMapper.serviceInfoToAppComponentInfo(userId, pm, it)
                }
        } ?: emptyList()
    }

    fun getProviders(userId: Int, packageName: String): List<AppComponentInfo> {
        val pm = context.packageManager
        return runClearCallingIdentity {
            ComponentUtil.getProviders(context, packageName)
                .mapNotNull {
                    ComponentMapper.providerInfoToAppComponentInfo(userId, pm, it)
                }
        } ?: emptyList()
    }

    fun getRunningAppPkgs(pkgSetLabel: String): List<AppPkg> {
        val pkgSet = shortXContext.pkgSet.getPkgSetByLabel(pkgSetLabel)
        if (pkgSet == null) {
            logger.w("pkgSet not found: $pkgSetLabel")
            return emptyList()
        }
        return pkgSet.appPkgsList.filter { it.isRunning() }
    }

    fun getRunningServices(pkg: AppPkg): List<AppComponent> {
        if (!pkg.isRunning()) {
            logger.d("getRunningServices, pkg is not running.")
            return emptyList()
        }
        return getServices(pkg.userId, pkg.pkgName)
            .map { it.component }
            .filter { it.isServiceRunning() }
    }

    fun setComponentEnabledSetting(
        component: AppComponent,
        newState: Int,
        flags: Int
    ) {
        PMSFuncs.setComponentEnabledSetting(
            component.pkg.userId,
            component.toComponentName(),
            newState,
            flags
        )
    }

    fun getComponentEnabledSetting(component: AppComponent): Int {
        return PMSFuncs.getComponentEnabledSetting(
            component.pkg.userId,
            component.toComponentName()
        )
    }

    fun getAppComponentInfo(component: AppComponent): AppComponentInfo? {
        return null
    }

    fun setAppInactive(pkgs: List<AppPkg>) {
        handler.postSafety {
            pkgs.forEach { USMFuncs.setAppInactive(it) }
        }
    }

    fun setAppInactive(pkg: AppPkg) {
        handler.postSafety {
            USMFuncs.setAppInactive(pkg)
        }
    }

    fun isAppInactive(pkg: AppPkg): Boolean {
        return runClearCallingIdentity { USMFuncs.isAppInactive(pkg) } ?: false
    }

    fun onTaskRemoved(task: RecentTask) {
        logger.d("onTaskRemoved: ${task.owner.pkgName} - ${task.owner.userId} - ${task.taskId}")
        val app = pkgToApp(task.owner)
        shortXContext.rule.publishFact(
            "onTaskRemoved",
            TaskRemoved.newBuilder()
                .addAllApps(listOf(task.owner))
                .build(),
            mapOf(
                ContextDataMapping.TaskRemoved.appLabel.name to (app?.label ?: task.owner.pkgName),
                ContextDataMapping.TaskRemoved.taskId.name to "${task.taskId}",
                ContextDataMapping.TaskRemoved.userId.name to "${task.owner.userId}",
                ContextDataMapping.TaskRemoved.pkgName.name to task.owner.pkgName,
            )
        )
    }

    fun onProcessRemoved(process: ProcessRecord) {
        logger.d("onProcessRemoved: ${process.owner.pkgName} - ${process.owner.userId} - ${process.processName}")
        val isPkgStillRunning = process.owner.isRunning()
        if (isPkgStillRunning) {
            logger.d("${process.owner} is still running.")
        } else {
            onPkgStopRunning(process.owner)
        }
    }

    fun onProcessStarted(process: ProcessRecord) {
        logger.d("onProcessStarted: ${process.owner.pkgName} - ${process.owner.userId} - ${process.processName}")
        shortXContext.rule.publishFact(
            reason = "onProcessStarted",
            fact = AppProcessStarted.newBuilder().addAllProcessName(
                listOf(
                    ProcessName.newBuilder().setUserId(process.owner.userId)
                        .setProcessName(process.processName).build()
                )
            )
                .build(),
            initialContextDataLazy = {
                mapOf(
                    ContextDataMapping.AppProcessStarted.processName.name to process.processName,
                    ContextDataMapping.AppProcessStarted.appLabel.name to (pkgToApp(process.owner)?.label.fallbackOnEmpty(
                        ""
                    )),
                    ContextDataMapping.AppProcessStarted.userId.name to process.owner.userId,
                    ContextDataMapping.AppProcessStarted.pkgName.name to process.owner.pkgName,
                )
            }
        )
    }

    private fun onPkgStopRunning(pkg: AppPkg) {
        logger.d("onPkgStopRunning: $pkg")

        shortXContext.rule.publishFact(
            "onPkgStopRunning",
            PkgStopRunning.newBuilder()
                .addAllApps(listOf(pkg))
                .build(),
            mapOf(
                ContextDataMapping.PkgStopRunning.userId.name to "${pkg.userId}",
                ContextDataMapping.PkgStopRunning.pkgName.name to pkg.pkgName,
            )
        )
    }

    fun stopServices(services: List<AppComponent>) =
        synchronized(requireNotNull(ams) { "ams is null." }) {
            val targetUsers = services.map { it.pkg.userId }.distinct()
            logger.d("stopServices, targetUsers: $targetUsers")
            val asIns = requireNotNull(activeServices) { "activeServices is null." }
            logger.d("stopServices with asIns: $asIns")

            targetUsers.forEach { userId ->
                val nameToServiceRecords = ActiveServiceFunc.getServicesLocked(
                    asIns,
                    userId
                )
                nameToServiceRecords.forEach {
                    val k: ComponentName? = it.key
                    logger.d("nameToServiceRecords: $k - ${it.value}")
                }
                // E  DefaultRuleEngine [DefaultDispatcher-worker-1] evaluate fail --- java.lang.NullPointerException: it.key must not be null
                // at tornaco.apps.shortx.services.app.AppStateService.stopServices(AppStateService.kt:409)
                // at tornaco.apps.shortx.services.rule.actor.impl.StopService.handle(StopService.kt:23)
                // at tornaco.apps.shortx.services.rule.ActionEvaluatorImpl.evaluate(ActionEvaluatorImpl.kt:38)
                nameToServiceRecords.filter { it.key != null }.forEach {
                    val keyName = it.key.toAppComponent(userId)
                    if (services.contains(keyName)) {
                        logger.d("Found target service: $keyName - ${it.value}")
                        val serviceRecord = it.value
                        ActiveServiceFunc.stopService(asIns, serviceRecord)
                    }
                }
            }
        }

    fun removeTasksForPackage(appPkgs: List<AppPkg>) {
        logger.d("removeTasksForPackage: ${appPkgs.map { it.appPkgToString() }}")
        val tasksIds = TaskMapping.getTasksIdForPackage(context, appPkgs, false)
        logger.d("tasksIds: $tasksIds")
        val activity =
            IActivityManager.Stub.asInterface(ServiceManager.getService(Context.ACTIVITY_SERVICE))
        tasksIds.forEach { activity.removeTask(it) }
    }

    fun killProcessByName(processList: List<ProcessName>) {
        val thanoxAvailable = ThanosManagerNative.isServiceInstalled()
        logger.d("killProcessByName, thanoxAvailable: $thanoxAvailable")
        if (thanoxAvailable) {
            ThanosManagerNative.getDefault().activityManager.killProcessByNames(
                processList.map {
                    logger.d("killProcessByName via thanox: $it")
                    github.tornaco.android.thanos.core.os.ProcessName(it.processName, it.userId)
                }
            )
        } else {
            AMSFuncs.getRunningAppProcessLegacy(context).filter {
                processList.contains(
                    ProcessName.newBuilder().setProcessName(it.processName)
                        .setUserId(UserHandle.getUserId(it.uid)).build()
                )
            }.forEach {
                logger.d("killProcess: ${it.processName}-${it.pkgList}")
                Process.killProcessQuiet(it.pid)
            }
        }
    }

    fun startLastApp() {
        systemServiceScope.launchSafety {
            loadHomeAppPkgs()
            TaskMapping.getLastTaskIdExcludeLauncher(context, homePkgs).apply {
                logger.d("startLastApp, taskId: $this")
                if (this > 0) {
                    if (OsUtils.isQOrAbove()) {
                        ActivityTaskManager.getService().startActivityFromRecents(this@apply, null)
                    } else {
                        ActivityManager.getService().startActivityFromRecents(this@apply, null)
                    }
                }
            }
        }
    }

    private fun loadHomeAppPkgs() {
        if (homePkgs.isEmpty()) {
            val homeActivities = mutableListOf<ResolveInfo>()
            context.packageManager.getHomeActivities(homeActivities)
            logger.d("homeActivities: $homeActivities: ${homeActivities.map { it.activityInfo }}")
            homePkgs.addAll(homeActivities.map { it.activityInfo.packageName }
                // Remove Fallback home.
                .filterNot { "com.android.settings" == it }.distinct())
            logger.d("homePkgs: $homePkgs")
        }
    }

    fun stopForegroundApp() {
        loadHomeAppPkgs()
        activityState.currentFrontAppPkgs.filterNot { homePkgs.contains(it.pkgName) }.forEach {
            logger.i("stopForegroundApp: ${it.appPkgToString()}")
            forceStopApp(it, "stopForegroundApp")
        }
    }

    fun isShortXAppUid(uid: Int) = pkgPools.any { it.isShortXAppUid(uid) }
    fun isSystemUIAppUid(uid: Int) = pkgPools.any { it.isSystemUIAppUid(uid) }

    fun setStartActivityResult(requestCode: Int, resultCode: Int, result: Bundle) {
        synchronized(activityStartResultOneTimeListeners) {
            activityStartResultOneTimeListeners.forEach {
                it.onStartActivityResult(requestCode, resultCode, result)
            }

            activityStartResultOneTimeListeners.clear()
        }
    }

    fun registerActivityStartResultOneTimeListeners(listener: ActivityStartResultOneTimeListener) {
        synchronized(activityStartResultOneTimeListeners) {
            activityStartResultOneTimeListeners.add(listener)
        }
    }
}