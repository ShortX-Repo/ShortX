package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture

/**
 * 持续滑动手势监听工具使用指南
 * 
 * 这个工具提供了类似视频播放器中音量、亮度调节的持续滑动手势监听能力。
 * 
 * ## 核心组件
 * 
 * 1. **ContinuousSwipeEvent** - 持续滑动事件数据结构
 *    - 包含滑动方向、阶段、坐标、距离、速度、持续时间等信息
 *    - 支持强度计算，可用于实现渐进式控制
 * 
 * 2. **ContinuousSwipeGestureDetector** - 核心检测器
 *    - 负责检测和跟踪持续滑动手势
 *    - 支持多种配置选项（最小距离、采样间隔、速度阈值等）
 *    - 提供精确的手势状态管理
 * 
 * 3. **ContinuousSwipeManager** - 管理器
 *    - 单例模式，管理多个检测器
 *    - 支持全局监听器和命名检测器
 *    - 提供统一的事件分发机制
 * 
 * 4. **ContinuousSwipeListener** - 监听器接口
 *    - 定义手势事件回调方法
 *    - 支持不同阶段的事件处理
 * 
 * ## 基本使用方法
 * 
 * ### 1. 获取管理器实例
 * ```kotlin
 * val swipeManager = ContinuousSwipeManager.getInstance()
 * ```
 * 
 * ### 2. 创建检测器
 * ```kotlin
 * // 使用预定义配置
 * val detector = swipeManager.getOrCreateDetector("my_detector", ContinuousSwipeConfigs.VOLUME_CONTROL)
 * 
 * // 使用自定义配置
 * val customConfig = ContinuousSwipeConfig(
 *     minSwipeDistance = 30f,
 *     directionThreshold = 0.5f,
 *     sampleInterval = 16L,
 *     enableIntensityCalculation = true
 * )
 * val customDetector = swipeManager.getOrCreateDetector("custom", customConfig)
 * ```
 * 
 * ### 3. 添加监听器
 * ```kotlin
 * // 简单监听器
 * detector.addListener(object : SimpleContinuousSwipeListener() {
 *     override fun onSwipeStart(event: ContinuousSwipeEvent) {
 *         // 滑动开始
 *     }
 *     
 *     override fun onSwipeContinue(event: ContinuousSwipeEvent) {
 *         // 持续滑动
 *         val progress = event.intensity // 0.0 - 1.0
 *         adjustVolume(progress)
 *     }
 *     
 *     override fun onSwipeEnd(event: ContinuousSwipeEvent) {
 *         // 滑动结束
 *     }
 * })
 * 
 * // 方向特定监听器
 * detector.addListener(object : DirectionalContinuousSwipeListener(SwipeDirection.VERTICAL) {
 *     override fun onDirectionalSwipe(event: ContinuousSwipeEvent): Boolean {
 *         // 只处理垂直方向的滑动
 *         return handleVerticalSwipe(event)
 *     }
 * })
 * ```
 * 
 * ### 4. 处理触摸事件
 * ```kotlin
 * fun onTouchEvent(event: MotionEvent): Boolean {
 *     return swipeManager.onTouchEvent(event)
 * }
 * ```
 * 
 * ## 高级使用示例
 * 
 * ### 音量控制实现
 * ```kotlin
 * class VolumeController(private val audioManager: AudioManager) {
 *     private val swipeManager = ContinuousSwipeManager.getInstance()
 *     private var baseVolume = 0
 *     
 *     init {
 *         val detector = swipeManager.getOrCreateDetector("volume", ContinuousSwipeConfigs.VOLUME_CONTROL)
 *         detector.addListener(object : DirectionalContinuousSwipeListener(SwipeDirection.VERTICAL) {
 *             override fun onDirectionalSwipe(event: ContinuousSwipeEvent): Boolean {
 *                 when (event.phase) {
 *                     SwipePhase.START -> {
 *                         baseVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
 *                     }
 *                     SwipePhase.CONTINUE -> {
 *                         val deltaY = event.currentPoint.y - event.startPoint.y
 *                         val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
 *                         val volumeChange = (deltaY / 200f * maxVolume).toInt()
 *                         val newVolume = (baseVolume - volumeChange).coerceIn(0, maxVolume)
 *                         audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0)
 *                     }
 *                 }
 *                 return true
 *             }
 *         })
 *     }
 * }
 * ```
 * 
 * ### 亮度控制实现
 * ```kotlin
 * class BrightnessController(private val context: Context) {
 *     private val swipeManager = ContinuousSwipeManager.getInstance()
 *     private var baseBrightness = 0
 *     
 *     init {
 *         val detector = swipeManager.getOrCreateDetector("brightness", ContinuousSwipeConfigs.BRIGHTNESS_CONTROL)
 *         detector.addListener(object : DirectionalContinuousSwipeListener(SwipeDirection.VERTICAL) {
 *             override fun onDirectionalSwipe(event: ContinuousSwipeEvent): Boolean {
 *                 when (event.phase) {
 *                     SwipePhase.START -> {
 *                         baseBrightness = getCurrentBrightness()
 *                     }
 *                     SwipePhase.CONTINUE -> {
 *                         val deltaY = event.currentPoint.y - event.startPoint.y
 *                         val brightnessChange = (deltaY / 300f * 255).toInt()
 *                         val newBrightness = (baseBrightness - brightnessChange).coerceIn(1, 255)
 *                         setBrightness(newBrightness)
 *                     }
 *                 }
 *                 return true
 *             }
 *         })
 *     }
 * }
 * ```
 * 
 * ## 配置选项说明
 * 
 * - **minSwipeDistance**: 触发检测的最小滑动距离（像素）
 * - **directionThreshold**: 方向判断阈值（0.0-1.0，值越大越严格）
 * - **sampleInterval**: 采样间隔（毫秒），控制事件频率
 * - **maxSwipeVelocity**: 最大滑动速度，超过则取消检测
 * - **enableIntensityCalculation**: 是否启用强度计算
 * - **intensityBaseDistance**: 强度计算的基准距离
 * - **velocityWindowSize**: 速度平滑窗口大小
 * - **enableMultiTouch**: 是否支持多指触摸
 * 
 * ## 预定义配置
 * 
 * - **ContinuousSwipeConfigs.VOLUME_CONTROL**: 音量控制优化配置
 * - **ContinuousSwipeConfigs.BRIGHTNESS_CONTROL**: 亮度控制优化配置
 * - **ContinuousSwipeConfigs.PROGRESS_CONTROL**: 进度控制优化配置
 * - **ContinuousSwipeConfigs.GENERAL**: 通用配置
 * 
 * ## 最佳实践
 * 
 * 1. **选择合适的配置**: 根据具体用途选择预定义配置或自定义配置
 * 2. **合理设置采样间隔**: 平衡性能和响应性
 * 3. **使用强度计算**: 实现更自然的渐进式控制
 * 4. **处理边界情况**: 确保在滑动取消时恢复状态
 * 5. **性能优化**: 避免在监听器中执行耗时操作
 * 6. **内存管理**: 及时移除不需要的监听器
 * 
 * ## 注意事项
 * 
 * 1. 持续滑动手势会消费触摸事件，可能影响其他手势检测
 * 2. 多个检测器可能同时触发，需要合理设计优先级
 * 3. 在Activity/Fragment销毁时记得清理监听器
 * 4. 某些系统权限可能影响亮度调节等功能
 * 
 * ## 集成到现有系统
 * 
 * 持续滑动手势已集成到 Detector.kt 中，会在 consumeEvent 方法中优先处理。
 * 可以通过 detector.getContinuousSwipeManager() 获取管理器实例进行进一步配置。
 */