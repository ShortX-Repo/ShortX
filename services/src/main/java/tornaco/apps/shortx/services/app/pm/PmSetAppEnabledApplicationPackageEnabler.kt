package tornaco.apps.shortx.services.app.pm

import android.app.ActivityThread
import android.content.Context
import android.content.pm.IPackageManager
import android.content.pm.PackageManager
import android.os.Binder
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.util.OsUtils

class PmSetAppEnabledApplicationPackageEnabler(val context: Context) : ApplicationPackageEnabler {
    private val logger = Logger("PmSetAppEnabledApplicationPackageEnabler")

    private val androidPM: IPackageManager
        get() {
            return ActivityThread.getPackageManager()
        }

    override fun enable(pkg: AppPkg) {
        kotlin.runCatching {
            val newState = PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            logger.w(
                "PmSetAppEnabledApplicationPackageEnabler setApplicationEnabledSetting, setup enable state to: $newState"
            )
            // void setApplicationEnabledSetting(in String packageName, in int newState, int flags, int userId, String callingPackage);
            androidPM.setApplicationEnabledSetting(pkg.pkgName, newState, 0, pkg.userId, "android")

            logger.w("PmSetAppEnabledApplicationPackageEnabler setApplicationEnabledSetting, setup hide state to false")
            // boolean setApplicationHiddenSettingAsUser(String packageName, boolean hidden, int userId)
            androidPM.setApplicationHiddenSettingAsUserNoThrow(pkg.pkgName, false, pkg.userId)
        }.onFailure {
            logger.e(it, "PmSetAppEnabledApplicationPackageEnabler, enable error")
        }
    }

    override fun disable(pkg: AppPkg) {
        kotlin.runCatching {
            val newState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            logger.w(
                "PmSetAppEnabledApplicationPackageEnabler setApplicationEnabledSetting, setup enable state to: $newState"
            )
            androidPM.setApplicationEnabledSetting(pkg.pkgName, newState, 0, pkg.userId, "android")
            // Check state.
            // int getApplicationEnabledSetting(String packageName, int userId)
            if (androidPM.getApplicationEnabledSetting(pkg.pkgName, pkg.userId) != newState) {
                logger.w("PmSetAppEnabledApplicationPackageEnabler setApplicationEnabledSetting, setup hide state to true, since enable state not expected.")
                androidPM.setApplicationHiddenSettingAsUserNoThrow(pkg.pkgName, true, pkg.userId)
            }
        }.onFailure {
            logger.e(it, "PmSetAppEnabledApplicationPackageEnabler, disable error")
        }
    }

    override fun isEnabled(pkg: AppPkg): Boolean {
        val ident = Binder.clearCallingIdentity()
        return try {
            val state = androidPM.getApplicationEnabledSetting(pkg.pkgName, pkg.userId)
            val disabled = state >= PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            // Check hide state.
            val isMOrAbove = OsUtils.isMOrAbove()
            // java.lang.NoSuchMethodError: No static method of(I)Landroid/os/UserHandle; in class Landroid/os/UserHandle;
            val isHide =
                isMOrAbove && androidPM.getApplicationHiddenSettingAsUserNoThrow(
                    pkg.pkgName,
                    pkg.userId
                )
            val res = !disabled && !isHide
            logger.d(
                "PmSetAppEnabledApplicationPackageEnabler getApplicationEnableState: disabled? $disabled, isHide? $isHide, return: $res"
            )
            res
        } finally {
            Binder.restoreCallingIdentity(ident)
        }
    }

    private fun IPackageManager.setApplicationHiddenSettingAsUserNoThrow(
        packageName: String?,
        hidden: Boolean,
        userId: Int
    ): Boolean {
        return kotlin.runCatching {
            this.setApplicationHiddenSettingAsUser(packageName, hidden, userId)
        }.getOrElse {
            logger.e(it, "setApplicationHiddenSettingAsUserNoThrow error")
            false
        }
    }

    private fun IPackageManager.getApplicationHiddenSettingAsUserNoThrow(
        packageName: String?,
        userId: Int
    ): Boolean {
        return kotlin.runCatching {
            this.getApplicationHiddenSettingAsUser(packageName, userId)
        }.getOrElse {
            logger.e(it, "getApplicationHiddenSettingAsUserNoThrow error")
            false
        }
    }
}