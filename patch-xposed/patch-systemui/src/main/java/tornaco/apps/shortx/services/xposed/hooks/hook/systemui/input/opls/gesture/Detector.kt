package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.graphics.RectF
import android.os.Handler
import android.view.GestureDetector
import android.view.MotionEvent
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.proto.common.Rect
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.EdgeGesture
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

    // 缓存计算好的矩形区域
    private var cachedPortraitRects = mutableListOf<CachedGestureRect>()
    private var cachedLandscapeRects = mutableListOf<CachedGestureRect>()
    private var lastOrientation = -1
    private var cacheVersion = 0

    // 复用对象，避免频繁创建
    private val reusableDetectedAreas = mutableListOf<DetectedArea>()
    private val reusableAddedEdges = mutableSetOf<Edge>()

    // 缓存数据结构
    private data class CachedGestureRect(
        val rectF: RectF,
        val detectedArea: DetectedArea,
        val edgeGesture: EdgeGesture
    )

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

    private val cornerSwipeToCenterDetector =
        CornerSwipeToCenterDetector(context, service)

    init {
        service.obsGestureSettingsChanged {
            setSwipeDistanceThresholdDp(service.getSwipeDistanceThresholdDp())
            // 手势设置变化时清除缓存
            invalidateCache()
        }
        service.obsConfigChanged {
            onConfigurationChanged()
        }
        service.obsUserDataChanged {
            invalidateCache()

            val currentOrientation = context.resources.configuration.orientation
            val isPortrait = currentOrientation == Configuration.ORIENTATION_PORTRAIT
            ensureCacheValid(isPortrait)
        }
    }

    private fun onConfigurationChanged() {
        screenSize = context.screenSize().apply {
            logger.w("onConfigurationChanged, screenSize: $this")
        }
        // 屏幕配置变化时清除缓存
        invalidateCache()
    }

    private fun invalidateCache() {
        logger.w("invalidateCache.")
        cachedPortraitRects.clear()
        cachedLandscapeRects.clear()
        cacheVersion++
        lastOrientation = -1
    }

    private fun ensureCacheValid(isPortrait: Boolean) {
        val currentOrientation =
            if (isPortrait) Configuration.ORIENTATION_PORTRAIT else Configuration.ORIENTATION_LANDSCAPE

        // 如果方向没变且缓存有效，直接返回
        if (lastOrientation == currentOrientation &&
            (if (isPortrait) cachedPortraitRects.isNotEmpty() else cachedLandscapeRects.isNotEmpty())
        ) {
            return
        }

        // 重建缓存
        val targetCache = if (isPortrait) cachedPortraitRects else cachedLandscapeRects
        targetCache.clear()

        service.enabledEdgeGestures.forEach { edgeGesture ->
            if (edgeGesture.customRect != null && edgeGesture.customRect != Rect.getDefaultInstance()) {
                // 自定义矩形区域
                val rect = edgeGesture.customRect
                val rectF = RectF(
                    rect.left.toFloat(),
                    rect.top.toFloat(),
                    rect.right.toFloat(),
                    rect.bottom.toFloat()
                )
                val detectedArea = DetectedArea.CustomRect(rect)
                targetCache.add(CachedGestureRect(rectF, detectedArea, edgeGesture))
            } else if (edgeGesture.edge != Edge.UNRECOGNIZED
                && (edgeGesture.customRect == null || edgeGesture.customRect == Rect.getDefaultInstance())
            ) {
                // 预定义边缘区域
                val rectF = edgeGesture.edge.getRect(
                    isPortrait = isPortrait,
                    screenWidth = screenSize.x.toFloat(),
                    screenHeight = screenSize.y.toFloat(),
                    areaSize = service.getGestureAreaWidthDp()._dp.toFloat(),
                    areaLengthScale = service.getGestureLengthScale()
                )
                val detectedArea = DetectedArea.PredefinedEdge(edgeGesture.edge)
                targetCache.add(CachedGestureRect(rectF, detectedArea, edgeGesture))
            }
        }

        lastOrientation = currentOrientation
    }

    private fun handleSwipeEvent(downX: Float, downY: Float, gestureEvent: Gesture): Boolean {
        val areas = detectArea(downX, downY)
        if (areas.isEmpty()) return false
        return handleEdgeGestureEvent(areas, gestureEvent, downX, downY)
    }

    private fun handleTapEvent(motionEvent: MotionEvent, gestureEvent: Gesture): Boolean {
        val areas = detectArea(motionEvent)
        if (areas.isEmpty()) return false
        return handleEdgeGestureEvent(areas, gestureEvent, motionEvent.x, motionEvent.y)
    }

    private fun handleEdgeGestureEvent(
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
        val isPortrait = currentOrientation == Configuration.ORIENTATION_PORTRAIT

        // 确保缓存有效
        ensureCacheValid(isPortrait)

        // 复用集合，避免重复创建
        reusableDetectedAreas.clear()
        reusableAddedEdges.clear()

        val targetCache = if (isPortrait) cachedPortraitRects else cachedLandscapeRects

        // 使用缓存的矩形进行检测
        targetCache.forEach { cachedRect ->
            if (cachedRect.rectF.contains(downX, downY)) {
                when (val area = cachedRect.detectedArea) {
                    is DetectedArea.CustomRect -> {
                        reusableDetectedAreas.add(area)
                        if (LOGV) logger.d("CustomRect: ${cachedRect.rectF} (downX: $downX downY: $downY) contains: true")
                    }

                    is DetectedArea.PredefinedEdge -> {
                        if (!reusableAddedEdges.contains(area.edge)) {
                            reusableDetectedAreas.add(area)
                            reusableAddedEdges.add(area.edge)
                            if (LOGV) logger.d("${area.edge}, rect: ${cachedRect.rectF} (downX: $downX downY: $downY) contains: true")
                        }
                    }
                }
            }
        }

        if (LOGV) logger.d("detectArea, $downX $downY return: $reusableDetectedAreas (screen info: $screenSize)")
        // 返回副本，避免外部修改影响复用对象
        return reusableDetectedAreas.toList()
    }

    fun consumeEvent(event: MotionEvent): Boolean? {
        if (event.isTouchEvent) {
            if (gestureDetector.onTouchEvent(event)) {
                return true
            }
            if (cornerSwipeToCenterDetector.onTouchEvent(event)) {
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