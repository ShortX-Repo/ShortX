package tornaco.apps.shortx.services35.pm

import android.app.ActivityThread
import android.content.pm.IPackageManager
import android.os.UserHandle
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.util.Logger

object PMV35 {
    private val logger = Logger("PMV35")

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
                0,
                "android",
                UserHandle.USER_SYSTEM,
                pkg.userId
            )
        }.onFailure {
            logger.e(it, "setPackageSuspend error")
        }
    }
}