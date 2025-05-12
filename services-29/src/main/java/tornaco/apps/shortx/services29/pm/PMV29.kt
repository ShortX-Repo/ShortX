package tornaco.apps.shortx.services29.pm

import android.app.ActivityThread
import android.content.pm.IPackageManager
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.util.Logger

object PMV29 {
    private val logger = Logger("PMV29")

    private val pm: IPackageManager
        get() {
            return ActivityThread.getPackageManager()
        }

    fun setPackageSuspend(pkg: AppPkg, suspend: Boolean) {
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