package tornaco.apps.shortx.services.gesture

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.graphics.RectF
import android.os.Handler
import android.view.GestureDetector
import android.view.MotionEvent
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.logOnDebug
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
    private val service: DetectorService,
) {
    companion object {
        const val LOGV = BuildPropInternal.APP_IS_DEBUG
    }

    private val logger = Logger("Detector")

    private var screenSize: Point = context.screenSize().apply {
        logger.w("Init screenSize: $this")
    }

    private data class CachedGestureRect(
        val rectF: RectF,
        val detectedArea: DetectedArea,
        val edgeGesture: tornaco.apps.shortx.core.proto.fact.EdgeGesture,
    )

    private var cachedPortraitRects = mutableListOf<CachedGestureRect>()
    private var cachedLandscapeRects = mutableListOf<CachedGestureRect>()
    private val reusableDetectedAreas = mutableListOf<DetectedArea>()
    private val reusableAddedEdges = mutableSetOf<Edge>()

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
        invalidateCache()
    }

    fun invalidateCache() {
        cachedPortraitRects.clear()
        cachedLandscapeRects.clear()

        // Build all cache
        ensureCacheValid(true)
        ensureCacheValid(false)
    }

    private fun ensureCacheValid(isPortrait: Boolean) {
        val targetCache = if (isPortrait) cachedPortraitRects else cachedLandscapeRects
        targetCache.clear()
        logger.d("ensureCacheValid isPortrait $isPortrait")

        service.enabledEdgeGestures().apply {
            logger.d("ensureCacheValid, enabledEdgeGestures size: $size")
        }.forEach { edgeGesture ->
            logger.logOnDebug { "ensureCacheValid, check edgeGesture: $edgeGesture" }
            if (edgeGesture.customRect != null && edgeGesture.customRect != tornaco.apps.shortx.core.proto.common.Rect.getDefaultInstance()) {
                val rect = edgeGesture.customRect
                val rectF = RectF(
                    rect.left.toFloat(),
                    rect.top.toFloat(),
                    rect.right.toFloat(),
                    rect.bottom.toFloat()
                )
                targetCache.add(
                    CachedGestureRect(
                        rectF,
                        DetectedArea.CustomRect(rect),
                        edgeGesture
                    )
                )
            } else if (edgeGesture.edge != Edge.UNRECOGNIZED) {
                val rectF = edgeGesture.edge.getRect(
                    isPortrait = isPortrait,
                    screenWidth = screenSize.x.toFloat(),
                    screenHeight = screenSize.y.toFloat(),
                    areaSize = service.getGestureAreaWidthDp()._dp.toFloat(),
                    areaLengthScale = service.getGestureLengthScale()
                )
                targetCache.add(
                    CachedGestureRect(
                        rectF,
                        DetectedArea.PredefinedEdge(edgeGesture.edge),
                        edgeGesture
                    )
                )
            }
        }
        logger.d("ensureCacheValid targetCache size: ${targetCache.size}")
    }

    private fun handleEvent(downX: Float, downY: Float, gestureEvent: Gesture): Boolean {
        val areas = detectArea(downX, downY)
        if (areas.isEmpty()) return false
        return handleEvent(areas, gestureEvent, downX, downY)
    }

    private fun handleEvent(motionEvent: MotionEvent, gestureEvent: Gesture): Boolean {
        val areas = detectArea(motionEvent)
        logger.logOnDebug { "detectArea $areas" }
        if (areas.isEmpty()) return false
        return handleEvent(areas, gestureEvent, motionEvent.x, motionEvent.y)
    }

    private fun handleEvent(
        areas: List<DetectedArea>,
        gestureEvent: Gesture,
        downX: Float,
        downY: Float,
    ): Boolean {
        return service.handleEdgeGestureEvent(areas, gestureEvent, downX, downY)
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

        reusableDetectedAreas.clear()
        reusableAddedEdges.clear()

        val targetCache = if (isPortrait) cachedPortraitRects else cachedLandscapeRects
        logger.logOnDebug { "detectArea, targetCache size: ${targetCache.size}" }
        targetCache.forEach { cachedRect ->
            if (cachedRect.rectF.contains(downX, downY)) {
                when (val area = cachedRect.detectedArea) {
                    is DetectedArea.CustomRect -> reusableDetectedAreas.add(area)
                    is DetectedArea.PredefinedEdge -> {
                        if (!reusableAddedEdges.contains(area.edge)) {
                            reusableDetectedAreas.add(area)
                            reusableAddedEdges.add(area.edge)
                        }
                    }
                }
            }
        }

        if (LOGV) logger.d("detectArea, $downX $downY return: $reusableDetectedAreas (screen info: $screenSize)")
        return reusableDetectedAreas.toList()
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