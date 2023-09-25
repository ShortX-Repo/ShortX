package tornaco.apps.shortx.services.gesture

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.os.Handler
import android.view.GestureDetector
import android.view.MotionEvent
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util._dp
import tornaco.apps.shortx.core.util.screenSize
import tornaco.apps.shortx.services.gesture.SystemGesturesPointerEventListener.CallbacksAdapter
import java.io.PrintWriter

class Detector(
    private val context: Context,
    private val handler: Handler,
    private val service: GestureService,
) {
    companion object {
        const val LOGV = BuildProp.APP_IS_DEBUG
    }

    private val logger = Logger("Detector")

    private val screenSize by lazy {
        context.screenSize().let {
            val currentOrientation = context.resources.configuration.orientation
            val isP = currentOrientation == Configuration.ORIENTATION_PORTRAIT
            logger.d("Init screenSize, isP? $isP")
            if (isP) {
                it
            } else {
                logger.i("Init screenSize, swap screen x and y")
                Point(it.y, it.x)
            }
        }.apply {
            logger.d("Init screenSize, return: $this")
        }
    }

    private val gestureDetector: GestureDetector =
        GestureDetector(context, object : OnGestureListenerAdapter() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                logger.d("onDoubleTap")
                return handleEvent(e, Gesture.DoubleTap)
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                logger.d("onSingleTapUp")
                return handleEvent(e, Gesture.SingleTap)
            }

            override fun onLongPress(e: MotionEvent) {
                logger.d("onLongPress")
                handleEvent(e, Gesture.LongTap)
            }
        }, handler)

    private val pointerEventListener =
        SystemGesturesPointerEventListener(context, object : CallbacksAdapter() {
            override fun onSwipeFromBottom(x: Int, y: Int): Boolean {
                return handleEvent(x.toFloat(), y.toFloat(), Gesture.SwipeFromBottom)
            }

            override fun onSwipeFromRight(x: Int, y: Int): Boolean {
                return handleEvent(x.toFloat(), y.toFloat(), Gesture.SwipeFromRight)
            }

            override fun onSwipeFromLeft(x: Int, y: Int): Boolean {
                return handleEvent(x.toFloat(), y.toFloat(), Gesture.SwipeFromLeft)
            }

            override fun onSwipeFromTop(x: Int, y: Int): Boolean {
                return handleEvent(x.toFloat(), y.toFloat(), Gesture.SwipeFromTop)
            }
        }, service.getSwipeDistanceThresholdDp()._dp)

    private fun handleEvent(downX: Float, downY: Float, gestureEvent: Gesture): Boolean {
        val area = detectArea(downX, downY) ?: return false
        return handleEvent(area, gestureEvent)
    }

    private fun handleEvent(motionEvent: MotionEvent, gestureEvent: Gesture): Boolean {
        val area = detectArea(motionEvent) ?: return false
        return handleEvent(area, gestureEvent)
    }

    private fun handleEvent(area: Edge, gestureEvent: Gesture): Boolean {
        return service.handleGestureEvent(area, gestureEvent)
    }

    private fun detectArea(event: MotionEvent): Edge? {
        val downX = event.x
        val downY = event.y
        return detectArea(downX, downY)
    }

    private fun detectArea(downX: Float, downY: Float): Edge? {
        val currentOrientation = context.resources.configuration.orientation
        if (LOGV) logger.d("currentOrientation: $currentOrientation (ORIENTATION_PORTRAIT = 1, ORIENTATION_LANDSCAPE = 2)")
        val area = Edge.values().firstOrNull {
            val rect = if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
                it.getRect(
                    screenSize.x.toFloat(),
                    screenSize.y.toFloat(),
                    service.getGestureAreaWidthDp()._dp.toFloat()
                )
            } else {
                it.getRectL(
                    screenSize.x.toFloat(),
                    screenSize.y.toFloat(),
                    service.getGestureAreaWidthDp()._dp.toFloat()
                )
            }
            if (LOGV) logger.d("rect: $rect (downX: $downX downY: $downY)")
            val res = rect.contains(downX, downY)
            res
        }
        if (LOGV) logger.d("detectArea, $downX $downY return: $area (screen info: $screenSize)")
        return area
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

    fun dump() {
        pointerEventListener.dump(PrintWriter(System.err), "")
    }

    fun setSwipeDistanceThresholdDp(dp: Int) {
        logger.d("setSwipeDistanceThresholdDp: $dp px: ${dp._dp}")
        pointerEventListener.setSwipeDistanceThresholdPx(dp._dp)
    }
}

open class OnGestureListenerAdapter : GestureDetector.SimpleOnGestureListener()