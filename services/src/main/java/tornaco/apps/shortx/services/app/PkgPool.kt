package tornaco.apps.shortx.services.app

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.os.Handler
import android.os.UserHandle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.Pkg
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.AppCategory
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.PkgUtils
import tornaco.apps.shortx.services.app.pm.PmSetAppEnabledApplicationPackageEnabler
import tornaco.apps.shortx.services.app.pm.PmSuspendApplicationPackageEnabler
import tornaco.apps.shortx.services.util.PackageMonitor

class PkgPool(
    private val context: Context,
    private val handler: Handler,
    val userId: Int,
    val onPackageAdded: (App) -> Unit = {},
    val onPackageRemoved: (App) -> Unit = {},
    val onPackageUpdateFinished: (App) -> Unit = {}
) {
    private val logger = Logger("PkgPool")
    private val pm by lazy { context.packageManager }

    private val enableAppPkg by lazy { PmSetAppEnabledApplicationPackageEnabler(context) }
    private val suspendAppPkg by lazy { PmSuspendApplicationPackageEnabler(context) }

    private val pkgMonitor = object : PackageMonitor() {
        override fun onPackageAdded(packageName: String?, uid: Int) {
            super.onPackageAdded(packageName, uid)
            packageName?.let { pkgName ->
                val loaded = load(pkgName)
                loaded?.let { onPackageAdded(it) }
            }
        }

        override fun onPackageRemoved(packageName: String?, uid: Int) {
            super.onPackageRemoved(packageName, uid)
            logger.d("onPackageRemoved: $packageName")
            packageName?.let { pkgName ->
                val removed = remove(pkgName)
                logger.d("removed: $removed")
                removed?.let { onPackageRemoved(it) }
            }
        }

        override fun onPackageUpdateFinished(packageName: String?, uid: Int) {
            super.onPackageUpdateFinished(packageName, uid)
            packageName?.let { pkgName ->
                val loaded = load(pkgName)
                loaded?.let { onPackageUpdateFinished(it) }
            }
        }

        override fun onSomePackagesChanged() {
            super.onSomePackagesChanged()
            logger.d("onSomePackagesChanged")
        }

        override fun onPackageModified(packageName: String) {
            super.onPackageModified(packageName)
            logger.d("onPackageModified: $packageName")
            load(packageName)
        }

        override fun onPackageChanged(
            packageName: String?,
            uid: Int,
            components: Array<out String>?
        ): Boolean {
            logger.d("onPackageChanged: $packageName ${components?.map { it }}")
            return super.onPackageChanged(packageName, uid, components)
        }

        override fun onPackagesSuspended(packages: Array<out String>?) {
            super.onPackagesSuspended(packages)
            logger.d("onPackagesSuspended: ${packages?.map { it }}")
            packages?.forEach { load(it) }
        }

        override fun onPackagesUnsuspended(packages: Array<out String>?) {
            super.onPackagesUnsuspended(packages)
            logger.d("onPackagesUnsuspended: ${packages?.map { it }}")
            packages?.forEach { load(it) }
        }
    }

    private val _allApps: MutableMap<String, App> = mutableMapOf()
    private val allApps: Map<String, App> get() = _allApps

    @Synchronized
    fun <T> withAllApps(call: Map<String, App>.() -> T): T {
        return call.invoke(allApps)
    }

    private val shortXAppUids = mutableListOf<Int>()
    private val systemUIAppUids = mutableListOf<Int>()

    fun init() {
        logger.d("init")
        pkgMonitor.register(context, UserHandle.of(userId), true, handler)
    }

    fun destroy() {
        logger.d("destroy")
        pkgMonitor.unregister()
    }

    suspend fun load() = withContext(Dispatchers.IO) {
        logger.d("Load at thread: ${Thread.currentThread()}")
        loadLocked()
    }

    @Synchronized
    private fun loadLocked() {
        val apps =
            PkgUtils.getInstalledApplicationsAsUser(context, userId)
                .mapNotNull { applicationInfo ->
                    logger.p(
                        "loadLocked, applicationInfo: ${applicationInfo.packageName} ${
                            UserHandle.getUserId(
                                applicationInfo.uid
                            )
                        }"
                    )
                    kotlin.runCatching { applicationInfo.toApp() }.getOrElse {
                        logger.e(it, "applicationInfo toApp error.")
                        null
                    }
                }

        _allApps.clear()
        apps.forEach {
            _allApps[it.pkg.pkgName] = it
        }
    }

    @Synchronized
    fun load(pkgName: String): App? {
        logger.d("load $pkgName")
        return kotlin.runCatching {
            val app = PkgUtils.getApplicationInfoAsUser(context, pkgName, userId)?.toApp()
            if (app != null) {
                logger.d("add: $app")
                _allApps[app.pkg.pkgName] = app
            }
            app
        }.getOrElse {
            logger.e(it, "load: $pkgName")
            null
        }
    }

    @Synchronized
    fun remove(pkgName: String): App? {
        logger.d("remove $pkgName")
        return _allApps.remove(pkgName)
    }

    fun isShortXAppUid(uid: Int): Boolean {
        return shortXAppUids.contains(uid)
    }

    fun isSystemUIAppUid(uid: Int): Boolean {
        return systemUIAppUids.contains(uid)
    }

    private fun ApplicationInfo.toApp(): App {
        val pkg = Pkg(packageName, userId)

        val isXposedMod =
            kotlin.runCatching { this.metaData.get("xposedmodule").toString().toBoolean() }
                .getOrElse { false }
        val isShortcutStub =
            kotlin.runCatching { this.metaData.get("shortx-stub").toString().toBoolean() }
                .getOrElse { false }
        val isGame =
            (this.flags and ApplicationInfo.FLAG_IS_GAME) != 0 || kotlin.runCatching { this.category == ApplicationInfo.CATEGORY_GAME }
                .getOrElse { false }

        val isDisabled = !enableAppPkg.isEnabled(pkg)
        val isSuspend = !suspendAppPkg.isEnabled(pkg)

        // Check if it is ShortX
        if (BuildProp.APP_PACKAGE_NAME == packageName) {
            logger.i("ShortX app uid: $uid")
            shortXAppUids.add(uid)
        } else if ("com.android.systemui" == packageName) {
            logger.i("systemui app uid: $uid")
            systemUIAppUids.add(uid)
        }

        return tornaco.apps.shortx.core.App(
            pkg = pkg,
            category = getCategory(),
            label = getLabel(),
            uid = uid,
            isGame = isGame,
            isXposedMode = isXposedMod,
            isDisabled = isDisabled,
            isSuspend = isSuspend,
            isShortcutStub = isShortcutStub
        )
    }

    private fun ApplicationInfo.getLabel(): String {
        var loadedLabel = loadLabel(pm)
        if (loadedLabel.isNullOrEmpty()) loadedLabel = packageName
        return loadedLabel.toString()
    }

    private fun ApplicationInfo.getCategory(): AppCategory {
        return if (flags and ApplicationInfo.FLAG_SYSTEM != 0) {
            AppCategory.System
        } else AppCategory.User
    }

    private fun ApplicationInfo.getPackageInfo(): PackageInfo? {
        val pkgName = this.packageName
        return PkgUtils.getPackageInfoAsUser(context, pkgName, userId)
    }
}