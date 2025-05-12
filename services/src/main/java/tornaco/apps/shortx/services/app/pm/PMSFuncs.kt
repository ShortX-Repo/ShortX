package tornaco.apps.shortx.services.app.pm

import android.content.ComponentName
import android.content.pm.IPackageManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.ServiceManager
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.runClearCallingIdentity
import tornaco.apps.shortx.services28.pm.PMV28
import tornaco.apps.shortx.services29.pm.PMV29
import tornaco.apps.shortx.services30.pm.PMV30
import tornaco.apps.shortx.services31.pm.PMV31
import tornaco.apps.shortx.services32.pm.PMV32
import tornaco.apps.shortx.services33.pm.PMV33
import tornaco.apps.shortx.services34.pm.PMV34
import tornaco.apps.shortx.services35.pm.PMV35
import util.Reflections

object PMSFuncs {
    private val logger = Logger("PMSFuncs")

    private val pm by lazy {
        IPackageManager.Stub.asInterface(ServiceManager.getService("package"))
    }

    fun getComponentEnabledSetting(userId: Int, componentName: ComponentName): Int {
        return runClearCallingIdentity {
            pm.getComponentEnabledSetting(componentName, userId)
        } ?: PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
    }

    fun setComponentEnabledSetting(
        userId: Int,
        componentName: ComponentName,
        newState: Int,
        flags: Int
    ) {
        logger.w(
            "setComponentEnabledSetting: ${
                arrayOf(
                    userId,
                    componentName,
                    newState,
                    flags
                )
            }",
        )
        runClearCallingIdentity {
            // Android 14? 15.
            //     /**
            //     * As per {@link android.content.pm.PackageManager#setComponentEnabledSetting}.
            //     */
            //    @UnsupportedAppUsage
            //    void setComponentEnabledSetting(in ComponentName componentName,
            //            in int newState, in int flags, int userId, String callingPackage);
            runCatching {
                Reflections.callMethod(
                    pm, "setComponentEnabledSetting",
                    componentName, newState, flags, userId, "android"
                )
            }.onFailure {
                logger.w("setComponentEnabledSetting(callingPackage) fail, try legacy one.")
                pm.setComponentEnabledSetting(componentName, newState, flags, userId)
            }
            val stateChecked = pm.getComponentEnabledSetting(componentName, userId)
            if (stateChecked != newState) {
                logger.w("trySetComponentEnabledSetting, stateChecked is not expected: $stateChecked")
            }
        }
    }

    fun setPackageSuspend(pkg: AppPkg, suspend: Boolean) {
        when (Build.VERSION.SDK_INT) {
            28 -> {
                PMV28.setPackageSuspend(pkg, suspend)
            }

            29 -> {
                PMV29.setPackageSuspend(pkg, suspend)
            }

            30 -> {
                PMV30.setPackageSuspend(pkg, suspend)
            }

            31 -> {
                PMV31.setPackageSuspend(pkg, suspend)
            }

            32 -> {
                PMV32.setPackageSuspend(pkg, suspend)
            }

            33 -> {
                PMV33.setPackageSuspend(pkg, suspend)
            }

            34 -> {
                PMV34.setPackageSuspend(pkg, suspend)
            }

            else -> {
                PMV35.setPackageSuspend(pkg, suspend)
            }
        }
    }
}