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
}