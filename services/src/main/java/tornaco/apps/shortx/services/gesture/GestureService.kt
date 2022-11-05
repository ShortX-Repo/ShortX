package tornaco.apps.shortx.services.gesture

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.InputEvent
import android.view.MotionEvent
import kotlinx.coroutines.delay
import tornaco.apps.shortx.GuideLineView
import tornaco.apps.shortx.core.gesture.Area
import tornaco.apps.shortx.core.gesture.GestureEvent
import tornaco.apps.shortx.data.*
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

    fun isEnabled() = Settings.BoolSettings.GestureEnabled.let {
        store.getBool(it.key, it.defaultValue)
    }

    fun setEnabled(enable: Boolean) {
        Settings.BoolSettings.GestureEnabled.let {
            store.putBool(it.key, enable)
        }
    }

    fun isGestureEnabledOnArea(areaName: String) = isGestureEnabledOnArea(Area.valueOf(areaName))

    fun isGestureEnabledOnArea(area: Area) =
        store.read(Settings.BoolSettings.GestureOnAreaEnabled(area))

    fun setGestureEnabledOnArea(areaName: String, enable: Boolean) {
        val area = Area.valueOf(areaName)
        store.write(Settings.BoolSettings.GestureOnAreaEnabled(area), enable)

        if (enable) {
            showGideLineViewForArea(area)
        }
    }

    fun setGestureAction(areaName: String, gestureEvent: String, data: Bundle) {
        val area = Area.valueOf(areaName)
        val event = GestureEvent.valueOf(gestureEvent)
        store.write(Settings.MapSettings.GestureActionOnArea(area, event), data.toStringMap())
    }

    fun getGestureAction(areaName: String, gestureEvent: String): Bundle {
        val area = Area.valueOf(areaName)
        val event = GestureEvent.valueOf(gestureEvent)
        return store.read(Settings.MapSettings.GestureActionOnArea(area, event)).toBundle()
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