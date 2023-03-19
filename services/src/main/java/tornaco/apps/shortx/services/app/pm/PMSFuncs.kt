package tornaco.apps.shortx.services.app.pm

import android.content.ComponentName
import android.content.pm.IPackageManager
import android.content.pm.PackageManager
import android.os.ServiceManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.runClearCallingIdentity

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
            pm.setComponentEnabledSetting(componentName, newState, flags, userId)
            val stateChecked = pm.getComponentEnabledSetting(componentName, userId)
            if (stateChecked != newState) {
                logger.w("trySetComponentEnabledSetting, stateChecked is not expected: $stateChecked")
            }
        }
    }
}