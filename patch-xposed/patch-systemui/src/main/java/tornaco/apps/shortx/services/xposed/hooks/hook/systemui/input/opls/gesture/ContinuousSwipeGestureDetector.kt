package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture

import android.graphics.PointF
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration
import tornaco.apps.shortx.core.util.Logger
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * 持续滑动手势检测器
 * 专门用于检测类似视频播放器中音量、亮度调节的持续滑动手势
 */
class ContinuousSwipeGestureDetector(
    private val config: ContinuousSwipeConfig = ContinuousSwipeConfig()
) {
    companion object {
        private const val TAG = "ContinuousSwipeDetector"
        private const val INVALID_POINTER_ID = -1
    }

    private val logger = Logger(TAG)
    private val listeners = CopyOnWriteArrayList<ContinuousSwipeListener>()
    private val handler = Handler(Looper.getMainLooper())
    
    // 手势状态
    private var isDetecting = false
    private var currentDirection = SwipeDirection.UNKNOWN
    private var startPoint = PointF()
    private var lastPoint = PointF()
    private var currentPoint = PointF()
    private var startTime = 0L
    private var lastEventTime = 0L
    private var activePointerId = INVALID_POINTER_ID
    
    // 速度追踪
    private var velocityTracker: VelocityTracker? = null
    private val velocityHistory = mutableListOf<Float>()
    
    // 距离和强度计算
    private var totalDistance = 0f
    private var lastDistance = 0f
    
    // 采样控制
    private var lastSampleTime = 0L
    
    /**
     * 添加持续滑动监听器
     */
    fun addListener(listener: ContinuousSwipeListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }
    
    /**
     * 移除持续滑动监听器
     */
    fun removeListener(listener: ContinuousSwipeListener) {
        listeners.remove(listener)
    }
    
    /**
     * 清除所有监听器
     */
    fun clearListeners() {
        listeners.clear()
    }
    
    /**
     * 处理触摸事件
     * @param event 触摸事件
     * @return true 表示消费了该事件
     */
    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                return handleActionDown(event)
            }
            MotionEvent.ACTION_MOVE -> {
                return handleActionMove(event)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                return handleActionUp(event)
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                // 多指触摸处理
                if (!config.enableMultiTouch) {
                    cancelDetection(event)
                    return false
                }
                return handlePointerDown(event)
            }
            MotionEvent.ACTION_POINTER_UP -> {
                return handlePointerUp(event)
            }
        }
        return false
    }
    
    private fun handleActionDown(event: MotionEvent): Boolean {
        reset()
        
        activePointerId = event.getPointerId(0)
        startPoint.set(event.x, event.y)
        lastPoint.set(event.x, event.y)
        currentPoint.set(event.x, event.y)
        startTime = event.eventTime
        lastEventTime = event.eventTime
        lastSampleTime = event.eventTime
        
        // 初始化速度追踪器
        velocityTracker?.recycle()
        velocityTracker = VelocityTracker.obtain()
        velocityTracker?.addMovement(event)
        
        logger.d("Action down at (${event.x}, ${event.y})")
        return true
    }
    
    private fun handleActionMove(event: MotionEvent): Boolean {
        if (activePointerId == INVALID_POINTER_ID) {
            return false
        }
        
        val pointerIndex = event.findPointerIndex(activePointerId)
        if (pointerIndex < 0) {
            cancelDetection(event)
            return false
        }
        
        currentPoint.set(event.getX(pointerIndex), event.getY(pointerIndex))
        velocityTracker?.addMovement(event)
        
        val deltaX = currentPoint.x - startPoint.x
        val deltaY = currentPoint.y - startPoint.y
        val distance = sqrt(deltaX * deltaX + deltaY * deltaY)
        
        // 检查是否达到最小滑动距离
        if (!isDetecting && distance >= config.minSwipeDistance) {
            startDetection(event, deltaX, deltaY)
        }
        
        // 如果正在检测，处理持续滑动
        if (isDetecting) {
            return handleContinuousSwipe(event)
        }
        
        return false
    }
    
    private fun handleActionUp(event: MotionEvent): Boolean {
        val wasDetecting = isDetecting
        
        if (isDetecting) {
            endDetection(event)
        }
        
        reset()
        return wasDetecting
    }
    
    private fun handlePointerDown(event: MotionEvent): Boolean {
        // 多指触摸时取消检测
        if (isDetecting) {
            cancelDetection(event)
        }
        return false
    }
    
    private fun handlePointerUp(event: MotionEvent): Boolean {
        val actionIndex = event.actionIndex
        val pointerId = event.getPointerId(actionIndex)
        
        if (pointerId == activePointerId) {
            cancelDetection(event)
        }
        
        return false
    }
    
    private fun startDetection(event: MotionEvent, deltaX: Float, deltaY: Float) {
        isDetecting = true
        currentDirection = SwipeDirection.fromDelta(deltaX, deltaY, config.directionThreshold)
        
        if (currentDirection == SwipeDirection.UNKNOWN) {
            cancelDetection(event)
            return
        }
        
        val swipeEvent = createSwipeEvent(SwipePhase.START, event)
        logger.d("Start detection: direction=$currentDirection, distance=${swipeEvent.totalDistance}")
        
        // 通知监听器
        notifyListeners { listener ->
            listener.onSwipeStart(swipeEvent)
            listener.onContinuousSwipe(swipeEvent)
        }
    }
    
    private fun handleContinuousSwipe(event: MotionEvent): Boolean {
        val currentTime = event.eventTime
        
        // 采样控制
        if (currentTime - lastSampleTime < config.sampleInterval) {
            return true
        }
        
        lastSampleTime = currentTime
        
        // 检查方向是否一致
        val deltaX = currentPoint.x - startPoint.x
        val deltaY = currentPoint.y - startPoint.y
        val newDirection = SwipeDirection.fromDelta(deltaX, deltaY, config.directionThreshold)
        
        if (newDirection != currentDirection && newDirection != SwipeDirection.UNKNOWN) {
            // 方向改变，取消检测
            cancelDetection(event)
            return false
        }
        
        val swipeEvent = createSwipeEvent(SwipePhase.CONTINUE, event)
        
        // 检查速度是否超过阈值
        if (swipeEvent.velocity > config.maxSwipeVelocity) {
            logger.d("Velocity too high: ${swipeEvent.velocity}, canceling")
            cancelDetection(event)
            return false
        }
        
        logger.v("Continue swipe: distance=${swipeEvent.totalDistance}, velocity=${swipeEvent.velocity}")
        
        // 通知监听器
        var consumed = false
        notifyListeners { listener ->
            listener.onSwipeContinue(swipeEvent)
            if (listener.onContinuousSwipe(swipeEvent)) {
                consumed = true
            }
        }
        
        // 更新状态
        lastPoint.set(currentPoint.x, currentPoint.y)
        lastEventTime = currentTime
        
        return consumed
    }
    
    private fun endDetection(event: MotionEvent) {
        val swipeEvent = createSwipeEvent(SwipePhase.END, event)
        logger.d("End detection: total distance=${swipeEvent.totalDistance}, duration=${swipeEvent.duration}")
        
        // 通知监听器
        notifyListeners { listener ->
            listener.onSwipeEnd(swipeEvent)
            listener.onContinuousSwipe(swipeEvent)
        }
    }
    
    private fun cancelDetection(event: MotionEvent) {
        if (!isDetecting) return
        
        val swipeEvent = createSwipeEvent(SwipePhase.CANCEL, event)
        logger.d("Cancel detection")
        
        // 通知监听器
        notifyListeners { listener ->
            listener.onSwipeCancel(swipeEvent)
            listener.onContinuousSwipe(swipeEvent)
        }
        
        reset()
    }
    
    private fun createSwipeEvent(phase: SwipePhase, event: MotionEvent): ContinuousSwipeEvent {
        val deltaX = currentPoint.x - startPoint.x
        val deltaY = currentPoint.y - startPoint.y
        val distance = sqrt(deltaX * deltaX + deltaY * deltaY)
        
        // 计算速度
        val velocity = calculateVelocity(event)
        
        // 计算强度
        val intensity = if (config.enableIntensityCalculation) {
            (distance / config.intensityBaseDistance).coerceIn(0f, 1f)
        } else {
            0f
        }
        
        val duration = event.eventTime - startTime
        
        return ContinuousSwipeEvent(
            direction = currentDirection,
            phase = phase,
            startPoint = PointF(startPoint.x, startPoint.y),
            currentPoint = PointF(currentPoint.x, currentPoint.y),
            totalDistance = distance,
            velocity = velocity,
            duration = duration,
            intensity = intensity,
            timestamp = event.eventTime
        )
    }
    
    private fun calculateVelocity(event: MotionEvent): Float {
        velocityTracker?.let { tracker ->
            tracker.computeCurrentVelocity(1000) // 计算每秒像素数
            val vx = tracker.xVelocity
            val vy = tracker.yVelocity
            val velocity = sqrt(vx * vx + vy * vy) / 1000f // 转换为每毫秒像素数
            
            // 维护速度历史记录用于平滑
            velocityHistory.add(velocity)
            if (velocityHistory.size > config.velocityWindowSize) {
                velocityHistory.removeAt(0)
            }
            
            // 返回平均速度
            return velocityHistory.average().toFloat()
        }
        
        return 0f
    }
    
    private fun notifyListeners(action: (ContinuousSwipeListener) -> Unit) {
        for (listener in listeners) {
            try {
                action(listener)
            } catch (e: Exception) {
                logger.e("Error notifying listener",)
            }
        }
    }
    
    private fun reset() {
        isDetecting = false
        currentDirection = SwipeDirection.UNKNOWN
        activePointerId = INVALID_POINTER_ID
        totalDistance = 0f
        lastDistance = 0f
        velocityHistory.clear()
        
        velocityTracker?.recycle()
        velocityTracker = null
    }
    
    /**
     * 获取当前检测状态
     */
    fun isDetecting(): Boolean = isDetecting
    
    /**
     * 获取当前滑动方向
     */
    fun getCurrentDirection(): SwipeDirection = currentDirection
    
    /**
     * 获取当前配置
     */
    fun getConfig(): ContinuousSwipeConfig = config
}