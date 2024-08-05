package tornaco.apps.shortx.services.gesture

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.os.Handler
import android.view.GestureDetector
import android.view.MotionEvent
import tornaco.apps.shortx.core.BuildPropInternal
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
        const val LOGV = BuildPropInternal.APP_IS_DEBUG
    }

    private val logger = Logger("Detector")

    private var screenSize: Point = context.screenSize().apply {
        logger.w("Init screenSize: $this")
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

    fun onConfigurationChanged() {
        screenSize = context.screenSize().apply {
            logger.w("onConfigurationChanged, screenSize: $this")
        }
    }

    private fun handleEvent(downX: Float, downY: Float, gestureEvent: Gesture): Boolean {
        val area = detectArea(downX, downY)
        if (area.isEmpty()) return false
        return handleEvent(area, gestureEvent)
    }

    private fun handleEvent(motionEvent: MotionEvent, gestureEvent: Gesture): Boolean {
        val area = detectArea(motionEvent)
        if (area.isEmpty()) return false
        return handleEvent(area, gestureEvent)
    }

    private fun handleEvent(area: Array<Edge>, gestureEvent: Gesture): Boolean {
        return service.handleGestureEvent(area, gestureEvent)
    }

    private fun detectArea(event: MotionEvent): Array<Edge> {
        val downX = event.x
        val downY = event.y
        return detectArea(downX, downY)
    }

    private fun detectArea(downX: Float, downY: Float): Array<Edge> {
        val currentOrientation = context.resources.configuration.orientation
        if (LOGV) logger.d("currentOrientation: $currentOrientation (ORIENTATION_PORTRAIT = 1, ORIENTATION_LANDSCAPE = 2)")
        val isPortrait = currentOrientation == Configuration.ORIENTATION_PORTRAIT
        val area = Edge.entries.filter {
            val rect = it.getRect(
                isPortrait = isPortrait,
                screenWidth = screenSize.x.toFloat(),
                screenHeight = screenSize.y.toFloat(),
                areaSize = service.getGestureAreaWidthDp()._dp.toFloat(),
                areaLengthScale = service.getGestureLengthScale()
            )
            if (LOGV) logger.d("rect: $rect (downX: $downX downY: $downY)")
            val res = rect.contains(downX, downY)
            res
        }
        if (LOGV) logger.d("detectArea, $downX $downY return: $area (screen info: $screenSize)")
        return area.toTypedArray()
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