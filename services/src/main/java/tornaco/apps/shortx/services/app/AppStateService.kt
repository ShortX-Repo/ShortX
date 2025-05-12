package tornaco.apps.shortx.services.app

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.ActivityTaskManager
import android.app.AlertDialog
import android.app.IActivityManager
import android.app.IApplicationThread
import android.app.TaskStackListener
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.os.Process
import android.os.ServiceManager
import android.os.UserHandle
import android.view.WindowManager
import android.widget.Toast
import com.topjohnwu.superuser.Shell
import github.tornaco.android.thanos.core.app.ThanosManagerNative
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.annotations.DoNotStrip
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.os.UidObserverCompat
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.AppComponent
import tornaco.apps.shortx.core.proto.common.AppComponentInfo
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.ProcessName
import tornaco.apps.shortx.core.proto.common.ProcessRecord
import tornaco.apps.shortx.core.proto.common.RecentTask
import tornaco.apps.shortx.core.proto.fact.AppProcessRemoved
import tornaco.apps.shortx.core.proto.fact.AppProcessStarted
import tornaco.apps.shortx.core.proto.fact.PkgStartRunning
import tornaco.apps.shortx.core.proto.fact.PkgStopRunning
import tornaco.apps.shortx.core.proto.fact.TaskRemoved
import tornaco.apps.shortx.core.store.WRITE_DELAY
import tornaco.apps.shortx.core.toAppComponent
import tornaco.apps.shortx.core.toComponentName
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.PkgUtils
import tornaco.apps.shortx.core.util.activityManager
import tornaco.apps.shortx.core.util.appPkgToString
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.android.ServiceRetriever
import tornaco.apps.shortx.services.app.pm.ComponentMapper
import tornaco.apps.shortx.services.app.pm.ComponentUtil
import tornaco.apps.shortx.services.app.pm.PMSFuncs
import tornaco.apps.shortx.services.app.pm.PmSetAppEnabledApplicationPackageEnabler
import tornaco.apps.shortx.services.app.pm.PmSuspendApplicationPackageEnabler
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.os.UserEvent
import tornaco.apps.shortx.services.rule.FactPublishData
import tornaco.apps.shortx.services.runClearCallingIdentity
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.services.watchdog.launchSafety
import tornaco.apps.shortx.services.watchdog.postSafety
import java.io.File
import java.io.FileInputStream
import java.io.IOException


class AppStateService(shortXContext: ShortXContext) :
    SystemService(shortXContext, "AppStateService") {
    private val pkgPools = mutableListOf<PkgPool>()

    private val onPackageUpdatedListeners: MutableList<((App, App) -> Unit)> = mutableListOf()
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

    private val pkgStopRunningEvents = mutableMapOf<AppPkg, Long>()

    override fun systemReady() {
        super.systemReady()
        systemServiceScope.launchSafety("AppStateService.systemReady.initPoolForUser") {
            shortXContext.user.getUsers().forEach {
                val pool = initPoolForUser(it.id)
                pkgPools.add(pool)
            }
        }

        systemServiceScope.launchSafety("AppStateService.systemReady.userEvent") {
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

        systemServiceScope.launchSafety("AppStateService.systemReady.registerTaskStackListener") {
            ActivityManager.getService().registerTaskStackListener(
                @DoNotStrip
                object : TaskStackListener() {
                    override fun onTaskCreated(taskId: Int, componentName: ComponentName?) {
                        super.onTaskCreated(taskId, componentName)
                        logger.d("TaskStackListener onTaskCreated- $taskId $componentName")
                    }

                    override fun onTaskDescriptionChanged(taskInfo: ActivityManager.RunningTaskInfo?) {
                        super.onTaskDescriptionChanged(taskInfo)
                        taskInfo?.let {
                            val description = if (OsUtils.isQOrAbove()) {
                                taskInfo.taskDescription?.label.orEmpty()
                            } else {
                                taskInfo.description?.toString().orEmpty()
                            }
                            val taskId = if (OsUtils.isQOrAbove()) {
                                taskInfo.taskId
                            } else {
                                taskInfo.id
                            }
                            logger.d("TaskStackListener onTaskDescriptionChanged $taskId - $description")
                            TaskMapping.taskDescriptionCache[taskId] = description
                        }
                    }

                    override fun onTaskStackChanged() {
                        super.onTaskStackChanged()
                        logger.d("TaskStackListener onTaskStackChanged")
                    }

                    override fun onTaskRemoved(taskId: Int) {
                        super.onTaskRemoved(taskId)
                        logger.d("TaskStackListener onTaskRemoved $taskId")
                    }

                    override fun onTaskFocusChanged(taskId: Int, focused: Boolean) {
                        super.onTaskFocusChanged(taskId, focused)
                        logger.d("TaskStackListener onTaskFocusChanged $taskId $focused")
                    }

                    override fun onTaskMovedToFront(taskInfo: ActivityManager.RunningTaskInfo?) {
                        super.onTaskMovedToFront(taskInfo)
                        logger.d("TaskStackListener onTaskMovedToFront- $taskInfo")
                    }

                    override fun onBackPressedOnTaskRoot(taskInfo: ActivityManager.RunningTaskInfo?) {
                        super.onBackPressedOnTaskRoot(taskInfo)
                        logger.d("TaskStackListener onBackPressedOnTaskRoot- $taskInfo")
                    }
                })
        }

        systemServiceScope.launchSafety("AppStateService.systemReady.UidObserverRegisterCompat") {
            UidObserverRegisterCompat.register(object : UidObserverCompat {
                override fun onUidActive(uid: Int) {
                    super.onUidActive(uid)
                    val app = uidToApp(uid)
                    logger.d("onUidActive: $uid ${app?.pkg?.appPkgToString()}")
                    if (app != null) {
                        shortXContext.rule.publishFactChecked(
                            PkgStartRunning.getDefaultInstance()
                        ) {
                            FactPublishData(
                                "onUidActive",
                                PkgStartRunning.newBuilder()
                                    .addAllApps(listOf(app.pkg))
                                    .build()
                            ) {
                                mapOf(
                                    ContextDataMapping.PkgStopRunning.userId.name to "${app.pkg.userId}",
                                    ContextDataMapping.PkgStopRunning.pkgName.name to app.pkg.pkgName,
                                    ContextDataMapping.PkgStopRunning.appLabel.name to app.label,
                                )
                            }
                        }
                    }
                }

                override fun onUidGone(uid: Int) {
                    super.onUidGone(uid)
                    val app = uidToApp(uid)
                    logger.d("onUidGone: $uid ${app?.pkg?.appPkgToString()}")
                }

                override fun onUidIdle(uid: Int) {
                    super.onUidIdle(uid)
                    val app = uidToApp(uid)
                    logger.d("onUidIdle: $uid ${app?.pkg?.appPkgToString()}")
                }

                override fun onUidStateChanged(uid: Int, procState: Int) {
                    super.onUidStateChanged(uid, procState)
                    val app = uidToApp(uid)
                    logger.d("onUidStateChanged: $uid $procState ${app?.pkg?.appPkgToString()}")
                }
            })
        }

        logger.w("AMS: $ams")
    }

    private suspend fun initPoolForUser(id: Int): PkgPool {
        val pool = PkgPool(
            context = context,
            handler = handler,
            userId = id,
            onPackageUpdateFinished = { from, to ->
                handler.postSafety("onPackageAdded") {
                    onPackageUpdatedListeners.forEach { it(from, to) }
                }
            },
            onPackageAdded = { added ->
                handler.postSafety("onPackageAdded") {
                    onPackageAddedListeners.forEach { it(added) }
                }
            },
            onPackageRemoved = { removed ->
                handler.postSafety("onPackageRemoved") {
                    if (removed.pkg.pkgName == BuildPropInternal.APP_PACKAGE_NAME) {
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

        fun resetAndClearData() {
            // Reset
            shortXContext.rule.getAllEnabledRules().map { it.id }.forEach {
                shortXContext.rule.setRuleEnabled(it, false)
            }
            shortXContext.rule.cancelJobs(shortXContext.rule.getActiveJobs(), "ShortX uninstalled")
            systemServiceScope.launchSafety("onShortXRemoved.resetAndClearData") {
                // After all file persisted.
                delay(WRITE_DELAY + 100L)

                Dirs.baseServerDir.deleteRecursively()
                logger.w("onShortXRemoved, all file deleted!")
                Toast.makeText(context, i18NIns["ui.shortx.removed.toast"], Toast.LENGTH_LONG)
                    .show()

            }
        }

        systemServiceScope.launchSafety("onShortXRemoved") {
            // Let system handle some UI changes first.
            delay(2000)

            AlertDialog.Builder(context)
                .setTitle(i18NIns["ui.shortx.uninstalled.title"])
                .setMessage(i18NIns["ui.shortx.uninstalled.message"])
                .setCancelable(false)
                .setPositiveButton(
                    i18NIns["ui.shortx.uninstalled.clear"]
                ) { _, _ -> resetAndClearData() }
                .setNegativeButton(i18NIns["ui.shortx.uninstalled.keep"]) { dialog, which ->
                    // Noop.
                }
                .create().apply {
                    window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG)
                }.show()
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

    fun currentResumedActivities(): List<AppComponent> {
        return activityState.currentResumedActivities.toList()
    }

    fun onActivityResumed(
        componentName: ComponentName,
        userId: Int,
        intentUri: String,
        taskId: Int
    ) {
        systemServiceScope.launchSafety("onActivityResumed") {
            val label = getTaskLabel(componentName, taskId)
            activityState.onActivityResumed(componentName, userId, intentUri, label)
        }
    }

    fun onActivityStopped(componentName: ComponentName, userId: Int, taskId: Int) {
        systemServiceScope.launchSafety("onActivityStopped") {
            val label = getTaskLabel(componentName, taskId)
            activityState.onActivityStopped(componentName, userId, label)
        }
    }

    fun onActivityDestroyed(componentName: ComponentName, userId: Int, taskId: Int) {
        systemServiceScope.launchSafety("onActivityDestroyed") {
            val label = getTaskLabel(componentName, taskId)
            activityState.onActivityDestroyed(componentName, userId, label)
        }
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

    fun onPackageUpdated(listener: (App, App) -> Unit) {
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
        systemServiceScope.launchSafety("forceStopApp1") {
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
        systemServiceScope.launchSafety("forceStopApp2") {
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
        systemServiceScope.launchSafety("startAppProcess") {
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
        systemServiceScope.launchSafety("setAppEnabled") {
            appPkgList.forEach {
                if (enable) enableAppPkg.enable(it) else enableAppPkg.disable(it)
            }
        }
    }

    fun setAppSuspend(appPkgList: List<AppPkg>, suspend: Boolean, reason: String) {
        logger.w("setAppSuspend, suspend: $suspend $appPkgList $reason")
        systemServiceScope.launchSafety("setAppSuspend") {
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
        handler.postSafety("setAppInactive") {
            pkgs.forEach { USMFuncs.setAppInactive(it) }
        }
    }

    fun setAppInactive(pkg: AppPkg) {
        handler.postSafety("setAppInactive") {
            USMFuncs.setAppInactive(pkg)
        }
    }

    fun isAppInactive(pkg: AppPkg): Boolean {
        return runClearCallingIdentity { USMFuncs.isAppInactive(pkg) } ?: false
    }

    fun onTaskRemoved(task: RecentTask) {
        logger.d("onTaskRemoved: ${task.owner.pkgName} - ${task.owner.userId} - ${task.taskId}")
        val app = pkgToApp(task.owner)
        shortXContext.rule.publishFactChecked(TaskRemoved.getDefaultInstance()) {
            FactPublishData(
                reason = "onTaskRemoved",
                fact = TaskRemoved.newBuilder()
                    .addAllApps(listOf(task.owner))
                    .build()
            ) {
                mapOf(
                    ContextDataMapping.TaskRemoved.appLabel.name to (app?.label
                        ?: task.owner.pkgName),
                    ContextDataMapping.TaskRemoved.taskId.name to "${task.taskId}",
                    ContextDataMapping.TaskRemoved.userId.name to "${task.owner.userId}",
                    ContextDataMapping.TaskRemoved.pkgName.name to task.owner.pkgName,
                )
            }
        }
    }

    @Synchronized
    fun onProcessRemoved(process: ProcessRecord) {
        systemServiceScope.launch {
            logger.d("onProcessRemoved: ${process.owner.pkgName} - ${process.owner.userId} - ${process.processName}")

            if (isSystemReady) {
                shortXContext.rule.publishFactChecked(AppProcessRemoved.getDefaultInstance()) {
                    FactPublishData(
                        reason = "onProcessRemoved",
                        fact = AppProcessRemoved.newBuilder().addAllProcessName(
                            listOf(
                                ProcessName.newBuilder().setUserId(process.owner.userId)
                                    .setProcessName(process.processName).build()
                            )
                        )
                            .build(),
                        initialContextDataLazy = {
                            mapOf(
                                ContextDataMapping.AppProcessRemoved.processName.name to process.processName,
                                ContextDataMapping.AppProcessRemoved.appLabel.name to (pkgToApp(
                                    process.owner
                                )?.label.fallbackOnEmpty(
                                    ""
                                )),
                                ContextDataMapping.AppProcessRemoved.userId.name to process.owner.userId,
                                ContextDataMapping.AppProcessRemoved.pkgName.name to process.owner.pkgName,
                            )
                        }
                    )
                }
            }

            val isPkgStillRunning = process.owner.isRunning()
            if (isPkgStillRunning) {
                logger.d("${process.owner} is still running.")
            } else {
                val alreadyBroadcastStopEvent =
                    System.currentTimeMillis() - (pkgStopRunningEvents[process.owner] ?: 0L) <= 600L

                if (!alreadyBroadcastStopEvent) {
                    onPkgStopRunning(process.owner)
                    // Cache to prevent call on stop more that 1 time with a second.
                    pkgStopRunningEvents[process.owner] = System.currentTimeMillis()
                } else {
                    logger.i("alreadyBroadcastStopEvent skip.")
                }
            }
        }
    }

    fun onProcessStarted(process: ProcessRecord) {
        logger.d("onProcessStarted: ${process.owner.pkgName} - ${process.owner.userId} - ${process.processName}")
        if (isSystemReady) {
            shortXContext.rule.publishFactChecked(
                AppProcessStarted.getDefaultInstance()
            ) {
                FactPublishData(
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
        }
    }

    private fun onPkgStopRunning(pkg: AppPkg) {
        logger.d("onPkgStopRunning: $pkg")

        shortXContext.rule.publishFactChecked(PkgStopRunning.getDefaultInstance()) {
            FactPublishData(
                "onPkgStopRunning",
                PkgStopRunning.newBuilder()
                    .addAllApps(listOf(pkg))
                    .build()
            ) {
                val appLabel = pkgToApp(pkg)?.label
                mapOf(
                    ContextDataMapping.PkgStopRunning.userId.name to "${pkg.userId}",
                    ContextDataMapping.PkgStopRunning.pkgName.name to pkg.pkgName,
                    ContextDataMapping.PkgStopRunning.appLabel.name to appLabel.orEmpty(),
                )
            }
        }
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
        systemServiceScope.launchSafety("startLastApp") {
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
    fun isShortXStubAppUid(uid: Int) = pkgPools.any { it.isShortXStubAppUid(uid) }
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

    fun installApk(
        packageName: String,
        fd: ParcelFileDescriptor,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        systemServiceScope.launchSafety("installApk") {
            withContext(Dispatchers.IO) {
                runCatching {
                    installApkInternal(packageName, fd).apply {
                        if (this) onSuccess() else onFailure("Install failed.")
                    }
                }.onFailure {
                    logger.e(it, "installApkInternal")
                    onFailure(it.message ?: it.javaClass.toString())
                }
            }
        }
    }

    private fun installApkInternal(packageName: String, fd: ParcelFileDescriptor): Boolean {
        val tmpApkFile = File(Dirs.baseServerTmpDir(), "$packageName.apk").apply {
            parentFile.mkdirs()
        }
        logger.d("installApk: $tmpApkFile")
        fd.use {
            val input = FileInputStream(fd.fileDescriptor)
            input.use {
                it.copyTo(tmpApkFile.outputStream())
            }
        }
        logger.d("apk copy complete.")

        val onFailure = {
            logger.e("startSession failure.")
        }
        val onSuccess = {
            logger.i("startSession success.")
        }
        val session =
            APKInstall.startSession(context, packageName, onFailure, onSuccess)
        val cmd = "adb_pm_install $tmpApkFile $packageName"
        if (Shell.cmd(cmd).exec().isSuccess) return true

        try {
            session.install(context, tmpApkFile)
        } catch (e: IOException) {
            logger.e(e, "install")
            return false
        }

        val intent = session.waitIntent()
        logger.i("waitIntent: $intent")

        intent?.let {
            context.startActivity(it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
        return true
    }

    fun hasRecentTaskForPkg(
        context: Context,
        pkg: AppPkg,
        ignoreNotRunning: Boolean
    ): Boolean {
        return TaskMapping.hasRecentTaskForPkg(context, pkg, ignoreNotRunning)
    }

    private fun getTaskLabel(name: ComponentName, taskId: Int): String {
        val label = TaskMapping.taskDescriptionCache[taskId]
        if (label != null) return label

        return kotlin.runCatching {
            val activityInfo =
                context.packageManager.getActivityInfo(name, PackageManager.GET_META_DATA)
            val pm = context.packageManager
            activityInfo.loadLabel(pm)?.toString().orEmpty()
        }.getOrElse { "" }
    }

    fun attachApplication(appThread: IApplicationThread): IApplicationThread? {
        if (BuildPropInternal.APP_IS_DEBUG) {
            val appThreadProxy = AppThread.make(appThread)
            return appThreadProxy
        }
        return null
    }
}