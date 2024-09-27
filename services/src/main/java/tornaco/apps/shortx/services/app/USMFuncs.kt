package tornaco.apps.shortx.services.app

import android.app.usage.IUsageStatsManager
import android.content.Context
import android.os.ServiceManager
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.appPkgToString
import util.Reflections

object USMFuncs {
    private val logger = Logger("USMFuncs")
    private val usm by lazy { IUsageStatsManager.Stub.asInterface(ServiceManager.getService(Context.USAGE_STATS_SERVICE)) }

    fun setAppInactive(pkg: AppPkg) {
        try {
            usm.setAppInactive(pkg.pkgName, true, pkg.userId)
            logger.d("Finish setAppInactive: ${pkg.appPkgToString()}")
            val isIdleNow: Boolean = isAppInactive(pkg)
            logger.d("Is pkg idle now? $isIdleNow")
        } catch (e: Throwable) {
            logger.e(e, "Error calling usm.setAppInactive")
        }
    }

    fun isAppInactive(pkg: AppPkg): Boolean {
        return if (OsUtils.isROrAbove()) {
            isAppInactive30AndAbove(pkg)
        } else isAppInactive29AndBelow(pkg)
    }

    private fun isAppInactive29AndBelow(pkg: AppPkg): Boolean {
        return try {
            val res = Reflections.callMethod(
                usm,
                "isAppInactive",
                pkg.pkgName,
                pkg.userId
            ) as Boolean
            logger.d("Finish query isAppInactive29AndBelow: ${pkg.appPkgToString()}")
            res
        } catch (e: Throwable) {
            logger.e(e, "Error call usm.isAppInactive29AndBelow, Please file a bug!")
            false
        }
    }

    private fun isAppInactive30AndAbove(pkg: AppPkg): Boolean {
        return try {
            // boolean isAppInactive(String packageName, int userId, String callingPackage) throws RemoteException;
            val res = Reflections.callMethod(
                usm,
                "isAppInactive",
                pkg.pkgName,
                pkg.userId,
                "android"
            ) as Boolean
            logger.d("Finish query isAppInactive30AndAbove: ${pkg.appPkgToString()}")
            res
        } catch (e: Throwable) {
            logger.e(e, "Error call usm.isAppInactive30AndAbove, Please file a bug!")
            false
        }
    }
}