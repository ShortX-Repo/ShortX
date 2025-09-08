package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.os.Handler
import android.view.GestureDetector
import android.view.MotionEvent
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.proto.fact.ScreenGesture
import tornaco.apps.shortx.core.proto.toAndroidRectF
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

    // 添加全局变量存储用户的ScreenGesture列表
    private var customScreenGestures: List<ScreenGesture> = emptyList()

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
        postHandleScreenGestureEvent(downX, downY, gestureEvent)
        val area = detectArea(downX, downY)
        if (area.isEmpty()) return false
        return handleEdgeGestureEvent(area, gestureEvent)
    }

    private fun handleTapEvent(motionEvent: MotionEvent, gestureEvent: Gesture): Boolean {
        postHandleScreenGestureEvent(motionEvent.x, motionEvent.y, gestureEvent)
        val area = detectArea(motionEvent)
        if (area.isEmpty()) return false
        return handleEdgeGestureEvent(area, gestureEvent)
    }

    private fun handleEdgeGestureEvent(area: Array<GestureArea>, gestureEvent: Gesture): Boolean {
        return service.handleEdgeGestureEvent(area, gestureEvent)
    }

    private fun postHandleScreenGestureEvent(downX: Float, downY: Float, gesture: Gesture) {
        handler.post {
            runCatching {
                service.handleScreenGestureEvent(downX, downY, gesture)
            }.onFailure {
                logger.e(it, "postHandleScreenGestureEvent")
            }
        }
    }

    private fun detectArea(event: MotionEvent): Array<GestureArea> {
        val downX = event.x
        val downY = event.y
        return detectArea(downX, downY)
    }

    private fun detectArea(downX: Float, downY: Float): Array<GestureArea> {
        val currentOrientation = context.resources.configuration.orientation
        if (LOGV) logger.d("currentOrientation: $currentOrientation (ORIENTATION_PORTRAIT = 1, ORIENTATION_LANDSCAPE = 2)")
        val isPortrait = currentOrientation == Configuration.ORIENTATION_PORTRAIT

        val detectedAreas = mutableListOf<GestureArea>()

        // 检测预定义的Edge区域
        val edgeAreas = Edge.entries.filter {
            val rect = it.getRect(
                isPortrait = isPortrait,
                screenWidth = screenSize.x.toFloat(),
                screenHeight = screenSize.y.toFloat(),
                areaSize = service.getGestureAreaWidthDp()._dp.toFloat(),
                areaLengthScale = service.getGestureLengthScale()
            )
            val res = rect.contains(downX, downY)
            if (LOGV) logger.d("$it, rect: $rect (downX: $downX downY: $downY) contains: $res")
            res
        }.map { PredefinedEdgeArea(it) }

        detectedAreas.addAll(edgeAreas)

        // 检测自定义ScreenGesture区域
        val customAreas = customScreenGestures.mapNotNull {
            it.gestureArea?.toAndroidRectF()
        }.filter {
            val rect = it
            val res = rect.contains(downX, downY)
            if (LOGV) logger.d("CustomGesture: $it, rect: $rect (downX: $downX downY: $downY) contains: $res")
            res
        }.map { CustomGestureArea(it) }

        detectedAreas.addAll(customAreas)

        if (LOGV) logger.d("detectArea, $downX $downY return: $detectedAreas (screen info: $screenSize)")
        return detectedAreas.toTypedArray()
    }

    // 添加方法用于更新自定义ScreenGesture列表（后期用于读取配置）
    fun updateCustomScreenGestures(gestures: List<ScreenGesture>) {
        customScreenGestures = gestures
        if (LOGV) logger.d("updateCustomScreenGestures: ${gestures.size} gestures")
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