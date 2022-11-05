package tornaco.apps.shortx.services.gesture

import android.content.Context
import android.os.Handler
import android.view.GestureDetector
import android.view.MotionEvent
import tornaco.apps.shortx.actor.TakeScreenShot
import tornaco.apps.shortx.core.gesture.Area
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.screenSize

class Detector(
    private val context: Context,
    private val handler: Handler,
    private val service: GestureService,
) {
    private val logger = Logger("Detector")
    private val takeScreenShot = TakeScreenShot(context, handler)
    private val screenSize by lazy {
        context.screenSize()
    }

    private val gestureDetector: GestureDetector =
        GestureDetector(context, object : OnGestureListenerAdapter() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                return if (isGestureEnabledOnEvent(e)) {
                    logger.d("onDoubleTap")
                    takeScreenShot()
                    true
                } else false
            }

            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                logger.d("onSingleTapConfirmed")
                return super.onSingleTapConfirmed(e)
            }
        }, handler)

    fun onEvent(event: MotionEvent): Boolean? {
        if (event.isTouchEvent) {
            if (gestureDetector.onTouchEvent(event)) {
                return false
            }
        }

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (isGestureEnabledOnEvent(event)) {
                    return false
                }
            }
        }
        return null
    }

    private fun isGestureEnabledOnEvent(event: MotionEvent): Boolean {
        val downX = event.x
        val downY = event.y
        val area = Area.values().firstOrNull {
            it.getRect(screenSize.x.toFloat(), screenSize.y.toFloat())
                .contains(downX, downY)
        }
        return area != null && service.isEdgeGestureEnabledOnArea(area)
    }
}

open class OnGestureListenerAdapter : GestureDetector.SimpleOnGestureListener()