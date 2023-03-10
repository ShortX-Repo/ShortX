package tornaco.apps.shortx.services.app

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Binder
import android.os.Process
import android.os.UserHandle
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.activityManager
import util.Reflections

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

    fun addAppLocked(
        ams: Any,
        applicationInfo: ApplicationInfo,
        isolated: Boolean = false,
        abiOverride: String? = null
    ) {
        logger.w("addAppLocked: ${applicationInfo.packageName}")
        // L M N: final addAppLocked(ApplicationInfo applicationInfo, boolean isolated, String abiOverride) {}
        // O: final ProcessRecord addAppLocked(ApplicationInfo info, String customProcess, boolean isolated,String abiOverride) {}
        // Q: final ProcessRecord addAppLocked(ApplicationInfo info, String customProcess, boolean isolated, String abiOverride) {}
        // R S: final ProcessRecord addAppLocked(ApplicationInfo info, String customProcess, boolean isolated, String abiOverride, int zygotePolicyFlags)
        when {
            OsUtils.isROrAbove() -> {
                Reflections.callMethod(
                    ams,
                    "addAppLocked",
                    applicationInfo,
                    null,
                    isolated,
                    abiOverride,
                    Process.ZYGOTE_POLICY_FLAG_EMPTY
                )
            }
            OsUtils.isOOrAbove() -> {
                Reflections.callMethod(
                    ams,
                    "addAppLocked",
                    applicationInfo,
                    null,
                    isolated,
                    abiOverride
                )
            }
            else -> Reflections.callMethod(
                ams,
                "addAppLocked",
                applicationInfo,
                isolated,
                abiOverride
            )
        }
    }
}

