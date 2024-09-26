package tornaco.apps.shortx.services.statusbar

import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.services.android.ServiceHolder
import util.Reflections

object StatusBarManagerServiceFuncs {

    fun setIcon(
        slot: String,
        iconId: Int,
        iconPackage: String = BuildPropInternal.APP_PACKAGE_NAME,
        iconLevel: Int = 0,
        contentDescription: String = ""
    ) {
        val sbms = requireNotNull(ServiceHolder.statusBarManagerService) {
            "statusBarManagerService is missing."
        }
        Reflections.callMethod(
            sbms,
            "setIcon",
            slot,
            iconPackage,
            iconId,
            iconLevel,
            contentDescription
        )
    }

    fun removeIcon(
        slot: String,
    ) {
        val sbms = requireNotNull(ServiceHolder.statusBarManagerService) {
            "statusBarManagerService is missing."
        }
        Reflections.callMethod(
            sbms,
            "removeIcon",
            slot
        )
    }

    fun showRecentApps() {
        // void showRecentApps(boolean triggeredFromAltTab)
        val internal = getInternalService()
        Reflections.callMethod(
            internal,
            "showRecentApps",
            false
        )
    }

    fun hideRecentApps() {
        // void hideRecentApps(boolean triggeredFromAltTab, boolean triggeredFromHomeKey)
        val internal = getInternalService()
        Reflections.callMethod(
            internal,
            "hideRecentApps",
            false,
            false
        )
    }

    private fun getInternalService(): Any {
        val sbms = requireNotNull(ServiceHolder.statusBarManagerService) {
            "statusBarManagerService is missing."
        }
        // StatusBarManagerInternal mInternalService:
        val internal = requireNotNull(Reflections.getObjectField(sbms, "mInternalService")) {
            "Can not get mInternalService"
        }
        return internal
    }
}