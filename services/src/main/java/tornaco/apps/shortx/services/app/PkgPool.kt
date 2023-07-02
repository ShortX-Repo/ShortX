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
            packageName?.let { pkgName ->
                val removed = remove(pkgName)
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
    }

    private val _allApps: MutableMap<String, App> = mutableMapOf()
    val allApps: Map<String, App> get() = _allApps.toMap()

    private val shortXAppUids = mutableListOf<Int>()

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
        val apps =
            PkgUtils.getInstalledApplicationsAsUser(context, userId)
                .mapNotNull { applicationInfo ->
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

    fun load(pkgName: String): App? {
        logger.d("load $pkgName")
        return kotlin.runCatching {
            _allApps.remove(pkgName)
            val app = PkgUtils.getApplicationInfoAsUser(context, pkgName, userId)?.toApp()
            app?.let {
                _allApps.put(app.pkg.pkgName, app)
            }
            app
        }.getOrElse {
            logger.e(it, "load: $pkgName")
            null
        }
    }

    fun remove(pkgName: String): App? {
        logger.d("remove $pkgName")
        return _allApps.remove(pkgName)
    }

    fun isShortXAppUid(uid: Int): Boolean {
        return shortXAppUids.contains(uid)
    }

    private fun ApplicationInfo.toApp(): App {
        val isXposedMod =
            kotlin.runCatching { this.metaData.get("xposedmodule").toString().toBoolean() }
                .getOrElse { false }
        val isGame =
            (this.flags and ApplicationInfo.FLAG_IS_GAME) != 0 || kotlin.runCatching { this.category == ApplicationInfo.CATEGORY_GAME }
                .getOrElse { false }


        // Check if it is ShortX
        if (BuildProp.APP_PACKAGE_NAME == packageName) {
            logger.i("ShortX app uid: $uid")
            shortXAppUids.add(uid)
        }

        return tornaco.apps.shortx.core.App(
            pkg = Pkg(packageName, userId),
            category = getCategory(),
            label = getLabel(),
            uid = uid,
            isGame = isGame,
            isXposedMode = isXposedMod
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