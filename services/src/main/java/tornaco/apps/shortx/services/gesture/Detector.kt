package tornaco.apps.shortx.services.gesture

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.VibrationEffect
import android.view.GestureDetector
import android.view.MotionEvent
import tornaco.apps.shortx.core.rule.fact.gesture.Area
import tornaco.apps.shortx.core.rule.fact.gesture.GestureEvent
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.screenSize
import tornaco.apps.shortx.core.util.vibrate
import tornaco.apps.shortx.services.gesture.SystemGesturesPointerEventListener.CallbacksAdapter
import tornaco.apps.shortx.services.launchSafety
import tornaco.apps.shortx.services.systemServiceScope

class Detector(
    private val context: Context,
    private val handler: Handler,
    private val service: GestureService,
) {
    private val logger = Logger("Detector")

    private val screenSize by lazy {
        context.screenSize()
    }

    private val gestureDetector: GestureDetector =
        GestureDetector(context, object : OnGestureListenerAdapter() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                logger.d("onDoubleTap")
                return handleEvent(e, GestureEvent.DoubleTap)
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                logger.d("onSingleTapUp")
                return handleEvent(e, GestureEvent.SingleTap)
            }

            override fun onLongPress(e: MotionEvent) {
                logger.d("onLongPress")
                handleEvent(e, GestureEvent.LongTap)
            }
        }, handler)

    private val pointerEventListener =
        SystemGesturesPointerEventListener(context, object : CallbacksAdapter() {
            override fun onSwipeFromBottom(x: Int, y: Int): Boolean {
                return handleEvent(x.toFloat(), y.toFloat(), GestureEvent.SwipeFromBottom)
            }

            override fun onSwipeFromRight(x: Int, y: Int): Boolean {
                return handleEvent(x.toFloat(), y.toFloat(), GestureEvent.SwipeFromRight)
            }

            override fun onSwipeFromLeft(x: Int, y: Int): Boolean {
                return handleEvent(x.toFloat(), y.toFloat(), GestureEvent.SwipeFromLeft)
            }
        })

    private fun handleEvent(downX: Float, downY: Float, gestureEvent: GestureEvent): Boolean {
        val area = detectArea(downX, downY) ?: return false
        return handleEvent(area, gestureEvent)
    }

    private fun handleEvent(motionEvent: MotionEvent, gestureEvent: GestureEvent): Boolean {
        val area = detectArea(motionEvent) ?: return false
        return handleEvent(area, gestureEvent)
    }

    private fun handleEvent(area: Area, gestureEvent: GestureEvent): Boolean {
       return service.handleGestureEvent(area, gestureEvent)
    }

    private fun detectArea(event: MotionEvent): Area? {
        val downX = event.x
        val downY = event.y
        return detectArea(downX, downY)
    }

    private fun detectArea(downX: Float, downY: Float): Area? {
        val area = Area.values().firstOrNull {
            it.getRect(screenSize.x.toFloat(), screenSize.y.toFloat()).contains(downX, downY)
        }
        return area
    }

    @SuppressLint("MissingPermission")
    private fun vibrateOnExecuteAction() {
        service.systemServiceScope.launchSafety {
            context.vibrate.vibrate(VibrationEffect.createOneShot(30,
                VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    fun interceptEvent(event: MotionEvent): Boolean? {
        if (event.isTouchEvent) {
            if (gestureDetector.onTouchEvent(event)) {
                return false
            }
            if (pointerEventListener.onPointerEvent(event)) {
                return false
            }
        }
        return null
    }
}

open class OnGestureListenerAdapter : GestureDetector.SimpleOnGestureListener()