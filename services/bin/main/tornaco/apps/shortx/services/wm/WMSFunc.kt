package tornaco.apps.shortx.services.wm

import tornaco.apps.shortx.services.android.ServiceHolder
import util.Reflections

object WMSFunc {
    fun lockDeviceNow() {
        requireNotNull(ServiceHolder.windowManagerService) {
            "windowManagerService is null."
        }
        Reflections.callMethod(ServiceHolder.windowManagerService, "hideTransientBars", 0)
    }
}