package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture

import android.graphics.PointF

/**
 * 持续滑动手势事件数据类
 * 用于描述持续滑动过程中的各种状态和参数
 */
data class ContinuousSwipeEvent(
    /** 滑动方向 */
    val direction: SwipeDirection,
    /** 滑动阶段 */
    val phase: SwipePhase,
    /** 起始点坐标 */
    val startPoint: PointF,
    /** 当前点坐标 */
    val currentPoint: PointF,
    /** 累计滑动距离 */
    val totalDistance: Float,
    /** 当前滑动速度 (像素/毫秒) */
    val velocity: Float,
    /** 滑动持续时间 (毫秒) */
    val duration: Long,
    /** 滑动强度 (0.0-1.0) */
    val intensity: Float,
    /** 事件时间戳 */
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 滑动方向枚举
 */
enum class SwipeDirection {
    /** 向上滑动 */
    UP,
    /** 向下滑动 */
    DOWN,
    /** 向左滑动 */
    LEFT,
    /** 向右滑动 */
    RIGHT,
    /** 未确定方向 */
    UNKNOWN;

    companion object {
        /**
         * 根据坐标差值计算滑动方向
         */
        fun fromDelta(deltaX: Float, deltaY: Float, threshold: Float = 10f): SwipeDirection {
            val absDeltaX = kotlin.math.abs(deltaX)
            val absDeltaY = kotlin.math.abs(deltaY)
            
            // 如果移动距离太小，认为是未确定方向
            if (absDeltaX < threshold && absDeltaY < threshold) {
                return UNKNOWN
            }
            
            return if (absDeltaX > absDeltaY) {
                if (deltaX > 0) RIGHT else LEFT
            } else {
                if (deltaY > 0) DOWN else UP
            }
        }
    }
}

/**
 * 滑动阶段枚举
 */
enum class SwipePhase {
    /** 开始滑动 */
    START,
    /** 持续滑动中 */
    CONTINUE,
    /** 滑动结束 */
    END,
    /** 滑动被取消 */
    CANCEL
}

/**
 * 持续滑动手势监听器接口
 */
interface ContinuousSwipeListener {
    /**
     * 持续滑动事件回调
     * @param event 滑动事件数据
     * @return true 表示消费了该事件，false 表示不处理
     */
    fun onContinuousSwipe(event: ContinuousSwipeEvent): Boolean
    
    /**
     * 滑动开始回调
     * @param event 滑动事件数据
     */
    fun onSwipeStart(event: ContinuousSwipeEvent) {}
    
    /**
     * 滑动进行中回调
     * @param event 滑动事件数据
     */
    fun onSwipeContinue(event: ContinuousSwipeEvent) {}
    
    /**
     * 滑动结束回调
     * @param event 滑动事件数据
     */
    fun onSwipeEnd(event: ContinuousSwipeEvent) {}
    
    /**
     * 滑动取消回调
     * @param event 滑动事件数据
     */
    fun onSwipeCancel(event: ContinuousSwipeEvent) {}
}

/**
 * 持续滑动配置类
 */
data class ContinuousSwipeConfig(
    /** 最小滑动距离阈值 (像素) */
    val minSwipeDistance: Float = 50f,
    /** 最大滑动速度阈值 (像素/毫秒) */
    val maxSwipeVelocity: Float = 5f,
    /** 方向确定阈值 (像素) */
    val directionThreshold: Float = 20f,
    /** 采样间隔 (毫秒) */
    val sampleInterval: Long = 16L, // 约60fps
    /** 速度计算窗口大小 */
    val velocityWindowSize: Int = 5,
    /** 是否启用强度计算 */
    val enableIntensityCalculation: Boolean = true,
    /** 强度计算基准距离 */
    val intensityBaseDistance: Float = 200f,
    /** 是否启用多指检测 */
    val enableMultiTouch: Boolean = false,
    /** 最大支持手指数 */
    val maxFingerCount: Int = 1
)