package tornaco.apps.shortx.services.app.pm

import android.app.ActivityThread
import android.content.Context
import android.content.pm.IPackageManager
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.PkgUtils

class PmSuspendApplicationPackageEnabler(val context: Context) : ApplicationPackageEnabler {
    private val logger = Logger("PmSetAppEnabledApplicationPackageEnabler")

    private val pm: IPackageManager
        get() {
            return ActivityThread.getPackageManager()
        }

    override fun enable(pkg: AppPkg) {
        kotlin.runCatching {
            if (!PkgUtils.isPkgInstalled(context, pkg.pkgName, pkg.userId)) {
                logger.w("Pkg not installed, won't enable $pkg")
                return
            }
            if (isEnabled(pkg)) return
            setPackageSuspend(pkg, false)
        }.onFailure {
            logger.e(it, "PmSuspendApplicationPackageEnabler enable error")
        }
    }

    override fun disable(pkg: AppPkg) {
        kotlin.runCatching {
            if (!PkgUtils.isPkgInstalled(context, pkg.pkgName, pkg.userId)) {
                logger.w("Pkg not installed, won't disable $pkg")
                return
            }
            if (!isEnabled(pkg)) return
            setPackageSuspend(pkg, true)
        }.onFailure {
            logger.e(it, "PmSuspendApplicationPackageEnabler disable error")
        }
    }

    override fun isEnabled(pkg: AppPkg): Boolean {
        return !pm.isPackageSuspendedForUser(pkg.pkgName, pkg.userId)
    }

    private fun setPackageSuspend(pkg: AppPkg, suspend: Boolean) {
        kotlin.runCatching {
            logger.i("PmSuspendApplicationPackageEnabler setPackageSuspend: $pkg $suspend")
            pm.setPackagesSuspendedAsUser(
                arrayOf(pkg.pkgName),
                suspend,
                null,
                null,
                null,
                "android",
                pkg.userId
            )
        }.onFailure {
            logger.e(it, "setPackageSuspend error")
        }
    }
}