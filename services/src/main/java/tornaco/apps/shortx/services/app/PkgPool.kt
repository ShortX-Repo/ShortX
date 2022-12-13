package tornaco.apps.shortx.services.app

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.os.Handler
import android.os.UserHandle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.App
import tornaco.apps.shortx.core.AppCategory
import tornaco.apps.shortx.core.Pkg
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.PkgUtils
import tornaco.apps.shortx.services.util.PackageMonitor

class PkgPool(
    private val context: Context,
    private val handler: Handler,
    val userId: Int,
    val onPackageUpdateFinished: (Pkg) -> Unit = {}
) {
    private val logger = Logger("PkgPool")
    private val pm by lazy { context.packageManager }
    private val pkgMonitor = object : PackageMonitor() {
        override fun onPackageAdded(packageName: String?, uid: Int) {
            super.onPackageAdded(packageName, uid)
            packageName?.let {
                load(it)
            }
        }

        override fun onPackageRemoved(packageName: String?, uid: Int) {
            super.onPackageRemoved(packageName, uid)
            packageName?.let {
                remove(it)
            }
        }

        override fun onPackageUpdateFinished(packageName: String?, uid: Int) {
            super.onPackageUpdateFinished(packageName, uid)
            packageName?.let {
                onPackageUpdateFinished(Pkg(it, UserHandle.getUserId(uid)))
            }
        }
    }

    private val _allApps: MutableMap<String, App> = mutableMapOf()
    val allApps: Map<String, App> get() = _allApps.toMap()

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
        val apps = PkgUtils.getInstalledApplicationsAsUser(context, userId, 0)
            .mapNotNull { applicationInfo ->
                kotlin.runCatching { applicationInfo.toApp() }.getOrElse {
                    logger.e(it, "applicationInfo toApp error.")
                    null
                }
            }

        _allApps.clear()
        apps.forEach {
            _allApps.put(it.pkg.pkgName, it)
        }
    }

    fun load(pkgName: String) {
        logger.d("load $pkgName")
        kotlin.runCatching {
            _allApps.remove(pkgName)
            val app = PkgUtils.getApplicationInfoAsUser(context, pkgName, userId)?.toApp()
            app?.let {
                _allApps.put(app.pkg.pkgName, app)
            }
        }.onFailure {
            logger.e(it, "load: $pkgName")
        }
    }

    fun remove(pkgName: String) {
        logger.d("remove $pkgName")
        _allApps.remove(pkgName)
    }

    private fun ApplicationInfo.toApp(): App {
        return App(
            pkg = Pkg(packageName, userId), category = getCategory(), label = getLabel()
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