package tornaco.apps.shortx.services.app

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.pm.ApplicationInfo
import android.os.UserHandle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import tornaco.apps.shortx.core.*
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.AppComponent
import tornaco.apps.shortx.core.proto.common.AppComponentInfo
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.ProcessRecord
import tornaco.apps.shortx.core.proto.common.RecentTask
import tornaco.apps.shortx.core.proto.fact.ActivityStarted
import tornaco.apps.shortx.core.proto.fact.AppBecomeBg
import tornaco.apps.shortx.core.proto.fact.AppBecomeFg
import tornaco.apps.shortx.core.proto.fact.PkgStopRunning
import tornaco.apps.shortx.core.proto.fact.TaskRemoved
import tornaco.apps.shortx.core.util.PkgUtils
import tornaco.apps.shortx.core.util.activityManager
import tornaco.apps.shortx.services.*
import tornaco.apps.shortx.services.android.ServiceRetriever
import tornaco.apps.shortx.services.app.pm.*
import tornaco.apps.shortx.services.os.UserEvent
import java.util.*

class AppStateService(shortXContext: ShortXContext) : SystemService(shortXContext) {
    private val _currentResumedActivities: MutableSet<AppComponent> = mutableSetOf()
    val currentResumedActivities: Set<AppComponent> = _currentResumedActivities.toSet()

    private val pkgPools = mutableListOf<PkgPool>()

    private val onPackageUpdatedListeners: MutableList<((App) -> Unit)> = mutableListOf()
    private val onPackageAddedListeners: MutableList<((App) -> Unit)> = mutableListOf()
    private val onPackageRemovedListeners: MutableList<((App) -> Unit)> = mutableListOf()

    private val enableAppPkg by lazy { PmSetAppEnabledApplicationPackageEnabler(context) }
    private val suspendAppPkg by lazy { PmSuspendApplicationPackageEnabler(context) }

    private val ams by lazy {
        ServiceRetriever(context.classLoader).getAMS()
    }

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
                    onPackageRemovedListeners.forEach { it(removed) }
                }
            }
        ).apply {
            init()
            load()
        }
        return pool
    }

    override fun shutdown() {
        super.shutdown()
        pkgPools.forEach {
            it.destroy()
        }
    }

    fun AppPkg.isFront() = _currentResumedActivities.firstOrNull { it.pkg == this } != null

    fun AppPkg.isRunning() = AMSFuncs.hasRunningAppProcessForPackage(context, this)

    fun AppComponent.isServiceRunning() = AMSFuncs.isServiceRunning(context, this)

    fun onActivityResumed(componentName: ComponentName, userId: Int) {
        logger.d("onActivityResumed: $componentName userId: $userId")
        val activity = AppComponent(
            Pkg(componentName.packageName, userId),
            componentName.className
        )
        val activityPkg = Pkg(componentName.packageName, userId)
        val isThisPkgFrontBefore = activityPkg.isFront()

        if (!isThisPkgFrontBefore) {
            logger.i("This package changed from back to front: $activityPkg")
            shortXContext.rule.publishFact(
                reason = "onActivityResumed",
                fact = AppBecomeFg.newBuilder().addApps(activityPkg).build(),
                initialContextData = mapOf(
                    ContextDataMapping.AppBecomeFg.componentName.name to componentName.flattenToShortString(),
                    ContextDataMapping.AppBecomeFg.pkgName.name to activityPkg.pkgName,
                    ContextDataMapping.AppBecomeFg.appLabel.name to (getAppLabel(activityPkg)
                        ?: activityPkg.pkgName),
                )
            )
        }


        logger.i("Publish Activity resume fact.")
        _currentResumedActivities.add(activity)
        shortXContext.rule.publishFact(
            reason = "onComponentMoveToFg",
            fact = ActivityStarted.newBuilder()
                .addComponents(componentName.toAppComponent(userId))
                .build(),
            initialContextData = mapOf(
                ContextDataMapping.ActivityStarted.pkgName.name to activityPkg.pkgName,
                ContextDataMapping.ActivityStarted.componentName.name to componentName.flattenToShortString(),
                ContextDataMapping.ActivityStarted.appLabel.name to (getAppLabel(activityPkg)
                    ?: activityPkg.pkgName),
            )
        )
    }

    fun onActivityPaused(componentName: ComponentName, userId: Int) {
        logger.d("onActivityPaused: $componentName userId: $userId")
        val activity = AppComponent(
            Pkg(componentName.packageName, userId),
            componentName.className
        )
        val activityPkg = Pkg(componentName.packageName, userId)
        val isThisPkgFrontBefore = activityPkg.isFront()

        _currentResumedActivities.remove(activity)
        val isThisPkgFrontNow = activityPkg.isFront()

        if (isThisPkgFrontBefore && !isThisPkgFrontNow) {
            logger.i("This pkg has moved to background: $activityPkg")
            shortXContext.rule.publishFact(
                reason = "onActivityPaused",
                fact = AppBecomeBg.newBuilder().addApps(activityPkg).build(),
                initialContextData = mapOf(
                    ContextDataMapping.AppBecomeBg.pkgName.name to activityPkg.pkgName,
                    ContextDataMapping.AppBecomeBg.appLabel.name to (getAppLabel(activityPkg)
                        ?: activityPkg.pkgName),
                )
            )
        }


    }

    private fun poolForUser(userId: Int) = pkgPools.firstOrNull { it.userId == userId }

    fun getInstalledApps(): List<App> {
        return pkgPools.map { it.allApps.values }.flatten()
    }

    fun uidToApp(uid: Int): App? {
        val userId = UserHandle.getUserId(uid)
        return poolForUser(userId)?.let { pool ->
            pool.allApps.values.firstOrNull { it.uid == uid }
        }
    }

    fun pkgToApp(pkg: AppPkg): App? {
        return pkgPools.firstOrNull { it.userId == pkg.userId }?.allApps?.get(pkg.pkgName)
    }

    fun getAppLabel(pkg: AppPkg): String? =
        pkgPools.firstOrNull { it.userId == pkg.userId }?.allApps?.get(pkg.pkgName)?.label

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

        shortXContext.rule.publishFact(
            "onTaskRemoved",
            TaskRemoved.newBuilder()
                .addAllApps(listOf(task.owner))
                .build(),
            mapOf(
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
}