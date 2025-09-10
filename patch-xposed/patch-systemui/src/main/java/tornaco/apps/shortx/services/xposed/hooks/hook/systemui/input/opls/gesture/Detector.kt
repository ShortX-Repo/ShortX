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
                val rectF = android.graphics.RectF(
                    rect.left.toFloat(),
                    rect.top.toFloat(),
                    rect.right.toFloat(),
                    rect.bottom.toFloat()
                )
                val detectedArea = DetectedArea.CustomRect(rect)
                targetCache.add(CachedGestureRect(rectF, detectedArea, edgeGesture))
            } else if (edgeGesture.edge != Edge.UNRECOGNIZED) {
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
        val isPortrait = currentOrientation == Configuration.ORIENTATION_PORTRAIT

        // 确保缓存有效
        ensureCacheValid(isPortrait)

        val detectedAreas = mutableListOf<DetectedArea>()
        val targetCache = if (isPortrait) cachedPortraitRects else cachedLandscapeRects
        val addedEdges = mutableSetOf<Edge>() // 避免重复添加相同的Edge

        // 使用缓存的矩形进行检测
        targetCache.forEach { cachedRect ->
            if (cachedRect.rectF.contains(downX, downY)) {
                when (val area = cachedRect.detectedArea) {
                    is DetectedArea.CustomRect -> {
                        detectedAreas.add(area)
                        if (LOGV) logger.d("CustomRect: ${cachedRect.rectF} (downX: $downX downY: $downY) contains: true")
                    }

                    is DetectedArea.PredefinedEdge -> {
                        if (!addedEdges.contains(area.edge)) {
                            detectedAreas.add(area)
                            addedEdges.add(area.edge)
                            if (LOGV) logger.d("${area.edge}, rect: ${cachedRect.rectF} (downX: $downX downY: $downY) contains: true")
                        }
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