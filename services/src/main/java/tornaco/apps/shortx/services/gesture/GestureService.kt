package tornaco.apps.shortx.services.gesture

import android.content.Context
import android.graphics.Color
import android.view.InputEvent
import android.view.MotionEvent
import kotlinx.coroutines.delay
import tornaco.apps.shortx.GuideLineView
import tornaco.apps.shortx.core.gesture.Area
import tornaco.apps.shortx.data.Settings
import tornaco.apps.shortx.data.read
import tornaco.apps.shortx.data.write
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.launchSafety
import tornaco.apps.shortx.services.systemServiceScope

private const val GUIDELINE_VIEW_SHOWN_TIME = 3000L

class GestureService : SystemService() {
    private var gestureDetector: Detector? = null

    override fun start(context: Context) {
        super.start(context)
    }

    override fun systemReady() {
        super.systemReady()
        store.readBlocking()
        gestureDetector = Detector(context, handler, this)
    }

    fun isEnabled() = Settings.BoolSettings.EdgeGestureEnabled.let {
        store.getBool(it.key, it.defaultValue)
    }

    fun setEnabled(enable: Boolean) {
        Settings.BoolSettings.EdgeGestureEnabled.let {
            store.putBool(it.key, enable)
        }
    }

    fun isEdgeGestureEnabledOnArea(areaName: String) =
        isEdgeGestureEnabledOnArea(Area.valueOf(areaName))

    fun isEdgeGestureEnabledOnArea(area: Area) =
        store.read(Settings.BoolSettings.EdgeGestureOnAreaEnabled(area))

    fun setEdgeGestureEnabledOnArea(areaName: String, enable: Boolean) {
        val area = Area.valueOf(areaName)
        store.write(Settings.BoolSettings.EdgeGestureOnAreaEnabled(area), enable)

        if (enable) {
            showGideLineViewForArea(area)
        }
    }

    private fun showGideLineViewForArea(area: Area) {
        systemServiceScope.launchSafety {
            val guideLineView =
                GuideLineView(context, listOf(HotSeat(area, Color.RED))).apply { attach() }
            delay(GUIDELINE_VIEW_SHOWN_TIME)
            guideLineView.detach()
        }
    }

    fun filterInput(input: InputEvent?): Boolean? {
        if (!isSystemReady) return null
        if (input == null) return null
        if (input !is MotionEvent) return null

        return gestureDetector?.onEvent(input)
    }
}