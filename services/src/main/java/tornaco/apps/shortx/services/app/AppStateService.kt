package tornaco.apps.shortx.services.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.UserInfo
import android.os.UserHandle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tornaco.apps.shortx.core.Pkg
import tornaco.apps.shortx.core.UserComponent
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.fact.AppBecomeBg
import tornaco.apps.shortx.core.proto.fact.AppBecomeFg
import tornaco.apps.shortx.core.unKnownPkg
import tornaco.apps.shortx.core.unknownUserComponent
import tornaco.apps.shortx.core.util.PkgUtils
import tornaco.apps.shortx.core.util.activityManager
import tornaco.apps.shortx.core.util.userManager
import tornaco.apps.shortx.services.*
import tornaco.apps.shortx.services.android.ServiceRetriever
import tornaco.apps.shortx.services.app.pm.PmSetAppEnabledApplicationPackageEnabler
import tornaco.apps.shortx.services.app.pm.PmSuspendApplicationPackageEnabler
import java.util.*

class AppStateService(shortXContext: ShortXContext) : SystemService(shortXContext) {
    private val _currentFgComponentName = MutableStateFlow(unknownUserComponent)
    val currentFgComponentName = _currentFgComponentName.asStateFlow()

    private val _currentFgAppPkg = MutableStateFlow(unKnownPkg)
    val currentFgAppPkg = _currentFgAppPkg.asStateFlow()

    private val pkgPools = mutableListOf<PkgPool>()

    private val onPackageChangedListeners: MutableList<((AppPkg) -> Unit)> = mutableListOf()

    private val enableAppPkg by lazy { PmSetAppEnabledApplicationPackageEnabler(context) }
    private val suspendAppPkg by lazy { PmSuspendApplicationPackageEnabler(context) }

    private val ams by lazy {
        ServiceRetriever(context.classLoader).getAMS()
    }

    override fun systemReady() {
        super.systemReady()
        systemServiceScope.launchSafety {
            getUsers().forEach {
                val pool = PkgPool(
                    context = context,
                    handler = handler,
                    userId = it.id,
                    onPackageUpdateFinished = { changed ->
                        handler.postSafety {
                            onPackageChangedListeners.forEach { it(changed) }
                        }
                    }).apply {

                    init()
                    load()
                }
                pkgPools.add(pool)
            }
        }
    }

    override fun shutdown() {
        super.shutdown()
        pkgPools.forEach {
            it.destroy()
        }
    }

    fun AppPkg.isFront() = currentFgComponentName.value.pkg == this

    fun AppPkg.isRunning() = AMSFuncs.hasRunningAppProcessForPackage(context, this)

    fun onComponentMoveToBg(componentName: ComponentName, userId: Int) {
        logger.d("onComponentMoveToBg: $componentName user: $userId")
    }

    fun onComponentMoveToFg(componentName: ComponentName, userId: Int) {
        logger.d("onComponentMoveToFg: $componentName user: $userId")
        _currentFgComponentName.value =
            UserComponent(Pkg(componentName.packageName, userId), componentName.className)

        val lastFgPkg = _currentFgAppPkg.value
        val currentFgPkg = Pkg(componentName.packageName, userId)
        if (lastFgPkg != currentFgPkg) {
            _currentFgAppPkg.value = currentFgPkg

            shortXContext.rule.publishFact(
                reason = "onComponentMoveToFg",
                fact = AppBecomeFg.newBuilder().addApps(currentFgPkg).build(),
                contextMap = mapOf(
                    "componentName" to componentName.flattenToShortString(),
                    "pkgName" to currentFgPkg.pkgName
                )
            )

            shortXContext.rule.publishFact(
                reason = "onComponentMoveToBg",
                fact = AppBecomeBg.newBuilder().addApps(lastFgPkg).build(),
                contextMap = mapOf(
                    "pkgName" to lastFgPkg.pkgName
                )
            )
        }
    }

    fun getInstalledApps(): List<App> {
        return pkgPools.map { it.allApps.values }.flatten()
    }

    fun pkgToApp(pkg: AppPkg): App? {
        return pkgPools.firstOrNull { it.userId == pkg.userId }?.allApps?.get(pkg.pkgName)
    }

    fun getAppLabel(pkg: AppPkg): String? =
        pkgPools.firstOrNull { it.userId == pkg.userId }?.allApps?.get(pkg.pkgName)?.label

    fun onPackageChanged(listener: (AppPkg) -> Unit) {
        onPackageChangedListeners.add(listener)
    }

    @SuppressLint("MissingPermission")
    private fun getUsers(): List<UserInfo> {
        val hasManageUserPerm =
            context.checkCallingOrSelfPermission(Manifest.permission.MANAGE_USERS) == PackageManager.PERMISSION_GRANTED
        logger.w("getUsers, hasManageUserPerm? $hasManageUserPerm")
        val um = context.userManager
        return if (hasManageUserPerm) {
            um.getUsers(true) ?: emptyList()
        } else {
            listOf(um.getUserInfo(UserHandle.USER_SYSTEM))
        }
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
}