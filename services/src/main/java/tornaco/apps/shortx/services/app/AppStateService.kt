package tornaco.apps.shortx.services.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.pm.PackageManager
import android.content.pm.UserInfo
import android.os.UserHandle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tornaco.apps.shortx.core.App
import tornaco.apps.shortx.core.Pkg
import tornaco.apps.shortx.core.UserComponent
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.fact.AppBecomeBg
import tornaco.apps.shortx.core.proto.fact.AppBecomeFg
import tornaco.apps.shortx.core.unknownUserComponent
import tornaco.apps.shortx.core.util.userManager
import tornaco.apps.shortx.services.*

class AppStateService(shortXContext: ShortXContext) : SystemService(shortXContext) {
    private val _currentFgComponentName = MutableStateFlow(unknownUserComponent)
    val currentFgComponentName = _currentFgComponentName.asStateFlow()

    private val pkgPools = mutableListOf<PkgPool>()

    private val onPackageChangedListeners: MutableList<((Pkg) -> Unit)> = mutableListOf()

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

    fun onComponentMoveToBg(componentName: ComponentName, userId: Int) {
        logger.d("onComponentMoveToBg: $componentName user: $userId")
        shortXContext.rule.publishFact(
            "onComponentMoveToBg",
            AppBecomeBg.newBuilder().addApps(
                AppPkg.newBuilder().setPkgName(componentName.packageName).setUserId(userId).build()
            ).build()
        )
    }

    fun onComponentMoveToFg(componentName: ComponentName, userId: Int) {
        logger.d("onComponentMoveToFg: $componentName user: $userId")
        _currentFgComponentName.value =
            UserComponent(Pkg(componentName.packageName, userId), componentName.className)

        shortXContext.rule.publishFact(
            "onComponentMoveToFg",
            AppBecomeFg.newBuilder().addApps(
                AppPkg.newBuilder().setPkgName(componentName.packageName).setUserId(userId).build()
            ).build()
        )
    }

    fun getInstalledApps(): List<App> {
        return pkgPools.map { it.allApps.values }.flatten()
    }

    fun getAppLabel(pkg: Pkg): String? =
        pkgPools.firstOrNull { it.userId == pkg.userId }?.allApps?.get(pkg.pkgName)?.label

    fun onPackageChanged(listener: (Pkg) -> Unit) {
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
}