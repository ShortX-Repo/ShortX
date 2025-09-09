package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.os.Handler
import android.view.GestureDetector
import android.view.MotionEvent
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.proto.common.Rect
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util._dp
import tornaco.apps.shortx.core.util.screenSize
import java.io.PrintWriter

class Detector(
    private val context: Context,
    private val handler: Handler,
    private val service: DetectorService
) {
    companion object {
        const val LOGV = BuildPropInternal.APP_IS_DEBUG
    }

    private val logger = Logger("GestureDetector")

    private var screenSize: Point = context.screenSize().apply {
        logger.w("Init screenSize: $this")
    }

    private val gestureDetector: GestureDetector =
        GestureDetector(context, object : OnGestureListenerAdapter() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                logger.d("onDoubleTap")
                return handleTapEvent(e, Gesture.DoubleTap)
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                logger.d("onSingleTapUp")
                return handleTapEvent(e, Gesture.SingleTap)
            }

            override fun onLongPress(e: MotionEvent) {
                logger.d("onLongPress")
                handleTapEvent(e, Gesture.LongTap)
            }
        }, handler)

    private val pointerEventListener =
        SystemGesturesPointerEventListener(
            context,
            object : SystemGesturesPointerEventListener.CallbacksAdapter() {
                override fun onSwipeFromBottom(x: Int, y: Int): Boolean {
                    return handleSwipeEvent(x.toFloat(), y.toFloat(), Gesture.SwipeFromBottom)
                }

                override fun onSwipeFromRight(x: Int, y: Int): Boolean {
                    return handleSwipeEvent(x.toFloat(), y.toFloat(), Gesture.SwipeFromRight)
                }

                override fun onSwipeFromLeft(x: Int, y: Int): Boolean {
                    return handleSwipeEvent(x.toFloat(), y.toFloat(), Gesture.SwipeFromLeft)
                }

                override fun onSwipeFromTop(x: Int, y: Int): Boolean {
                    return handleSwipeEvent(x.toFloat(), y.toFloat(), Gesture.SwipeFromTop)
                }
            },
            service.getSwipeDistanceThresholdDp()._dp
        )

    init {
        service.obsGestureSettingsChanged {
            setSwipeDistanceThresholdDp(service.getSwipeDistanceThresholdDp())
        }
        service.obsConfigChanged {
            onConfigurationChanged()
        }
    }

    private fun onConfigurationChanged() {
        screenSize = context.screenSize().apply {
            logger.w("onConfigurationChanged, screenSize: $this")
        }
    }

    private fun handleSwipeEvent(downX: Float, downY: Float, gestureEvent: Gesture): Boolean {
        val areas = detectArea(downX, downY)
        if (areas.isEmpty()) return false
        return handleEdgeGestureEvent(areas, gestureEvent)
    }

    private fun handleTapEvent(motionEvent: MotionEvent, gestureEvent: Gesture): Boolean {
        val areas = detectArea(motionEvent)
        if (areas.isEmpty()) return false
        return handleEdgeGestureEvent(areas, gestureEvent)
    }

    private fun handleEdgeGestureEvent(areas: List<DetectedArea>, gestureEvent: Gesture): Boolean {
        return service.handleEdgeGestureEvent(areas, gestureEvent)
    }

    private fun detectArea(event: MotionEvent): List<DetectedArea> {
        val downX = event.x
        val downY = event.y
        return detectArea(downX, downY)
    }

    private fun detectArea(downX: Float, downY: Float): List<DetectedArea> {
        val currentOrientation = context.resources.configuration.orientation
        if (LOGV) logger.d("currentOrientation: $currentOrientation (ORIENTATION_PORTRAIT = 1, ORIENTATION_LANDSCAPE = 2)")
        val isPortrait = currentOrientation == Configuration.ORIENTATION_PORTRAIT

        val detectedAreas = mutableListOf<DetectedArea>()

        // 从enabledEdgeGestures中检测所有启用的手势
        service.enabledEdgeGestures.forEach { edgeGesture ->
            if (edgeGesture.customRect != null && edgeGesture.customRect != Rect.getDefaultInstance()) {
                // 检测自定义Rect
                val rect = edgeGesture.customRect!!
                val rectF = android.graphics.RectF(
                    rect.left.toFloat(),
                    rect.top.toFloat(),
                    rect.right.toFloat(),
                    rect.bottom.toFloat()
                )
                if (rectF.contains(downX, downY)) {
                    detectedAreas.add(DetectedArea.CustomRect(rect))
                    if (LOGV) logger.d("CustomRect: $rectF (downX: $downX downY: $downY) contains: true")
                }
            } else if (edgeGesture.edge != Edge.UNRECOGNIZED) {
                // 检测预定义的Edge（只检测enabledEdgeGestures中的Edge）
                val rect = edgeGesture.edge.getRect(
                    isPortrait = isPortrait,
                    screenWidth = screenSize.x.toFloat(),
                    screenHeight = screenSize.y.toFloat(),
                    areaSize = service.getGestureAreaWidthDp()._dp.toFloat(),
                    areaLengthScale = service.getGestureLengthScale()
                )
                if (rect.contains(downX, downY)) {
                    // 避免重复添加相同的Edge
                    val predefinedEdge = DetectedArea.PredefinedEdge(edgeGesture.edge)
                    if (!detectedAreas.contains(predefinedEdge)) {
                        detectedAreas.add(predefinedEdge)
                        if (LOGV) logger.d("${edgeGesture.edge}, rect: $rect (downX: $downX downY: $downY) contains: true")
                    }
                }
            }
        }

        if (LOGV) logger.d("detectArea, $downX $downY return: $detectedAreas (screen info: $screenSize)")
        return detectedAreas
    }

    fun consumeEvent(event: MotionEvent): Boolean? {
        if (event.isTouchEvent) {
            if (gestureDetector.onTouchEvent(event)) {
                return true
            }
            if (pointerEventListener.onPointerEvent(event)) {
                return true
            }
        }
        return null
    }

    fun dump() {
        pointerEventListener.dump(PrintWriter(System.err), "")
    }

    private fun setSwipeDistanceThresholdDp(dp: Int) {
        logger.d("setSwipeDistanceThresholdDp: $dp px: ${dp._dp}")
        pointerEventListener.setSwipeDistanceThresholdPx(dp._dp)
    }
}

open class OnGestureListenerAdapter : GestureDetector.SimpleOnGestureListener()