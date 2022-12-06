package tornaco.apps.shortx.services.gesture

import android.content.Context
import android.graphics.Color
import android.view.InputEvent
import android.view.KeyEvent
import android.view.MotionEvent
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.services.*

private const val GUIDELINE_VIEW_SHOWN_TIME = 3000L

class GestureService(shortXContext: ShortXContext) : SystemService(shortXContext) {
    private var gestureDetector: Detector? = null

    private var onKeyEvent: ((KeyEvent) -> Boolean?)? = null
    private var onGestureEvent: ((Pair<Edge, Gesture>) -> Boolean?)? = null

    override fun start(context: Context) {
        super.start(context)
    }

    override fun systemReady() {
        super.systemReady()
        gestureDetector = Detector(context, handler, this)
    }

    fun onKeyEvent(onKeyEvent: (KeyEvent) -> Boolean?) {
        this.onKeyEvent = onKeyEvent
    }

    fun onGestureEvent(onGestureEvent: (Pair<Edge, Gesture>) -> Boolean?) {
        this.onGestureEvent = onGestureEvent
    }

    private fun showGideLineViewForArea(area: Edge) {
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
        if (input is MotionEvent) return gestureDetector?.interceptEvent(input)
        if (input is KeyEvent) return handleKeyEvent(input)
        return null
    }

    private fun handleKeyEvent(event: KeyEvent): Boolean? {
        return onKeyEvent?.invoke(event)
    }

    fun handleGestureEvent(area: Edge, gestureEvent: Gesture): Boolean {
        return onGestureEvent?.invoke(area to gestureEvent) ?: false
    }
}