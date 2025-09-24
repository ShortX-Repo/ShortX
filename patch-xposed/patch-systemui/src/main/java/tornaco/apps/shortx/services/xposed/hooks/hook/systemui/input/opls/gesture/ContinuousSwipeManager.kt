package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture

import android.content.Context
import android.graphics.PointF
import android.view.MotionEvent
import tornaco.apps.shortx.core.util.Logger
import java.util.concurrent.ConcurrentHashMap

/**
 * 持续滑动手势管理器
 * 负责管理多个持续滑动检测器和配置
 */
class ContinuousSwipeManager private constructor() {
    
    companion object {
        private const val TAG = "ContinuousSwipeManager"
        
        @Volatile
        private var INSTANCE: ContinuousSwipeManager? = null
        
        fun getInstance(): ContinuousSwipeManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ContinuousSwipeManager().also { INSTANCE = it }
            }
        }
    }
    
    private val logger = Logger(TAG)
    private val detectors = ConcurrentHashMap<String, ContinuousSwipeGestureDetector>()
    private val globalListeners = mutableSetOf<ContinuousSwipeListener>()
    private var isEnabled = true
    
    /**
     * 创建或获取指定名称的手势检测器
     */
    fun getOrCreateDetector(
        name: String, 
        config: ContinuousSwipeConfig = ContinuousSwipeConfig()
    ): ContinuousSwipeGestureDetector {
        return detectors.getOrPut(name) {
            logger.d("Creating new detector: $name")
            ContinuousSwipeGestureDetector(config)
        }
    }
    
    /**
     * 移除指定的手势检测器
     */
    fun removeDetector(name: String) {
        detectors.remove(name)?.let {
            it.clearListeners()
            logger.d("Removed detector: $name")
        }
    }
    
    /**
     * 获取所有检测器名称
     */
    fun getDetectorNames(): Set<String> = detectors.keys.toSet()
    
    /**
     * 添加全局监听器（会被添加到所有检测器）
     */
    fun addGlobalListener(listener: ContinuousSwipeListener) {
        globalListeners.add(listener)
        detectors.values.forEach { it.addListener(listener) }
    }
    
    /**
     * 移除全局监听器
     */
    fun removeGlobalListener(listener: ContinuousSwipeListener) {
        globalListeners.remove(listener)
        detectors.values.forEach { it.removeListener(listener) }
    }
    
    /**
     * 处理触摸事件（分发给所有检测器）
     */
    fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) return false
        
        var consumed = false
        for (detector in detectors.values) {
            if (detector.onTouchEvent(event)) {
                consumed = true
            }
        }
        return consumed
    }
    
    /**
     * 启用/禁用手势检测
     */
    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
        logger.d("Gesture detection ${if (enabled) "enabled" else "disabled"}")
    }
    
    /**
     * 检查是否启用
     */
    fun isEnabled(): Boolean = isEnabled
    
    /**
     * 清除所有检测器
     */
    fun clearAll() {
        detectors.values.forEach { it.clearListeners() }
        detectors.clear()
        globalListeners.clear()
        logger.d("Cleared all detectors")
    }
    
    /**
     * 获取检测器状态信息
     */
    fun getDetectorStatus(): Map<String, DetectorStatus> {
        return detectors.mapValues { (name, detector) ->
            DetectorStatus(
                name = name,
                isDetecting = detector.isDetecting(),
                currentDirection = detector.getCurrentDirection(),
                config = detector.getConfig()
            )
        }
    }
}

/**
 * 检测器状态信息
 */
data class DetectorStatus(
    val name: String,
    val isDetecting: Boolean,
    val currentDirection: SwipeDirection,
    val config: ContinuousSwipeConfig
)

/**
 * 预定义的手势检测器配置
 */
object ContinuousSwipeConfigs {
    
    /**
     * 音量调节手势配置
     */
    val VOLUME_CONTROL = ContinuousSwipeConfig(
        minSwipeDistance = 30f,
        directionThreshold = 0.5f,
        sampleInterval = 16L, // 60fps
        maxSwipeVelocity = 5000f,
        enableIntensityCalculation = true,
        intensityBaseDistance = 200f,
        velocityWindowSize = 5,
        enableMultiTouch = false
    )
    
    /**
     * 亮度调节手势配置
     */
    val BRIGHTNESS_CONTROL = ContinuousSwipeConfig(
        minSwipeDistance = 25f,
        directionThreshold = 0.6f,
        sampleInterval = 20L,
        maxSwipeVelocity = 4000f,
        enableIntensityCalculation = true,
        intensityBaseDistance = 150f,
        velocityWindowSize = 3,
        enableMultiTouch = false
    )
    
    /**
     * 进度调节手势配置
     */
    val PROGRESS_CONTROL = ContinuousSwipeConfig(
        minSwipeDistance = 40f,
        directionThreshold = 0.3f,
        sampleInterval = 12L,
        maxSwipeVelocity = 6000f,
        enableIntensityCalculation = true,
        intensityBaseDistance = 300f,
        velocityWindowSize = 7,
        enableMultiTouch = false
    )
    
    /**
     * 通用手势配置
     */
    val GENERAL = ContinuousSwipeConfig()
}

/**
 * 简化的持续滑动监听器实现
 */
abstract class SimpleContinuousSwipeListener : ContinuousSwipeListener {
    
    override fun onSwipeStart(event: ContinuousSwipeEvent) {
        // 默认空实现
    }
    
    override fun onSwipeContinue(event: ContinuousSwipeEvent) {
        // 默认空实现
    }
    
    override fun onSwipeEnd(event: ContinuousSwipeEvent) {
        // 默认空实现
    }
    
    override fun onSwipeCancel(event: ContinuousSwipeEvent) {
        // 默认空实现
    }
    
    override fun onContinuousSwipe(event: ContinuousSwipeEvent): Boolean {
        return when (event.phase) {
            SwipePhase.START -> {
                onSwipeStart(event)
                true
            }
            SwipePhase.CONTINUE -> {
                onSwipeContinue(event)
                true
            }
            SwipePhase.END -> {
                onSwipeEnd(event)
                true
            }
            SwipePhase.CANCEL -> {
                onSwipeCancel(event)
                false
            }
        }
    }
}

/**
 * 方向特定的持续滑动监听器
 */
abstract class DirectionalContinuousSwipeListener(
    private val targetDirection: SwipeDirection
) : ContinuousSwipeListener {
    
    override fun onContinuousSwipe(event: ContinuousSwipeEvent): Boolean {
        return if (event.direction == targetDirection) {
            onDirectionalSwipe(event)
        } else {
            false
        }
    }
    
    /**
     * 处理指定方向的滑动事件
     */
    abstract fun onDirectionalSwipe(event: ContinuousSwipeEvent): Boolean
    
    override fun onSwipeStart(event: ContinuousSwipeEvent) {}
    override fun onSwipeContinue(event: ContinuousSwipeEvent) {}
    override fun onSwipeEnd(event: ContinuousSwipeEvent) {}
    override fun onSwipeCancel(event: ContinuousSwipeEvent) {}
}