package tornaco.apps.shortx.services.app

import android.content.Context
import android.os.Binder
import android.os.UserHandle
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.activityManager

object AMSFuncs {
    private val logger = Logger("AMSFuncs")

    fun hasRunningAppProcessForPackage(context: Context, pkg: AppPkg): Boolean {
        return getRunningAppProcessLegacy(context).firstOrNull {
            pkg.userId == UserHandle.getUserId(it.uid)
                    && it.pkgList.contains(pkg.pkgName)
        } != null
    }

    fun getRunningAppProcessLegacy(context: Context): List<RunningAppProcessInfoCompat> {
        val activityManager = context.activityManager
        val id = Binder.clearCallingIdentity()
        try {
            return activityManager.runningAppProcesses.mapNotNull {
                it.compat()
            }
        } catch (e: Throwable) {
            logger.e(e, "getRunningAppProcesses")
        } finally {
            Binder.restoreCallingIdentity(id)
        }
        return emptyList()
    }
}

