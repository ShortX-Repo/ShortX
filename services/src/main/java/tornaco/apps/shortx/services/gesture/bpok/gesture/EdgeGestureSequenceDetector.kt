package tornaco.apps.shortx.services.gesture.bpok.gesture

import android.os.Handler
import android.os.SystemClock
import android.view.MotionEvent
import tornaco.apps.shortx.core.proto.fact.EdgeGestureStep
import tornaco.apps.shortx.core.proto.fact.EdgeGestureStepType
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.util._dp
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.hypot

interface GestureSequenceScheduler {
    fun removeCallbacks(runnable: Runnable)
    fun postDelayed(runnable: Runnable, delayMillis: Long): Boolean
}

interface GestureSequencePointerEvent {
    val actionMasked: Int
    val actionIndex: Int
    fun findPointerIndex(pointerId: Int): Int
    fun getX(pointerIndex: Int): Float
    fun getY(pointerIndex: Int): Float
    fun getPointerId(pointerIndex: Int): Int
}

interface EdgeGestureSequenceHintSink {
    fun onStepHint(
        anchorX: Float,
        anchorY: Float,
        step: EdgeGestureStep,
        progress: Float,
        isReady: Boolean,
        hintEnabled: Boolean,
        hapticEnabled: Boolean,
    )

    fun onSequenceSuccess()
    fun onSequenceCancelled()
}

private object NoOpEdgeGestureSequenceHintSink : EdgeGestureSequenceHintSink {
    override fun onStepHint(
        anchorX: Float,
        anchorY: Float,
        step: EdgeGestureStep,
        progress: Float,
        isReady: Boolean,
        hintEnabled: Boolean,
        hapticEnabled: Boolean,
    ) = Unit

    override fun onSequenceSuccess() = Unit
    override fun onSequenceCancelled() = Unit
}

private class AndroidGestureSequenceScheduler(
    private val handler: Handler,
) : GestureSequenceScheduler {
    override fun removeCallbacks(runnable: Runnable) {
        handler.removeCallbacks(runnable)
    }

    override fun postDelayed(runnable: Runnable, delayMillis: Long): Boolean {
        return handler.postDelayed(runnable, delayMillis)
    }
}

private class AndroidGestureSequencePointerEvent(
    private val event: MotionEvent,
) : GestureSequencePointerEvent {
    override val actionMasked: Int
        get() = event.actionMasked

    override val actionIndex: Int
        get() = event.actionIndex

    override fun findPointerIndex(pointerId: Int): Int = event.findPointerIndex(pointerId)
    override fun getX(pointerIndex: Int): Float = event.getX(pointerIndex)
    override fun getY(pointerIndex: Int): Float = event.getY(pointerIndex)
    override fun getPointerId(pointerIndex: Int): Int = event.getPointerId(pointerIndex)
}

class EdgeGestureSequenceDetector(
    private val scheduler: GestureSequenceScheduler,
    private val service: GestureBridge,
    private val elapsedRealtime: () -> Long,
    private val dpToPx: (Int) -> Int,
    private val hintSink: EdgeGestureSequenceHintSink,
    private val debugLog: (String) -> Unit,
) {
    constructor(handler: Handler, service: GestureBridge) : this(
        scheduler = AndroidGestureSequenceScheduler(handler),
        service = service,
        elapsedRealtime = SystemClock::elapsedRealtime,
        dpToPx = { it._dp },
        hintSink = NoOpEdgeGestureSequenceHintSink,
        debugLog = tornaco.apps.shortx.core.util.Logger("EdgeGestureSequence")::d,
    )

    constructor(
        scheduler: GestureSequenceScheduler,
        service: GestureBridge,
    ) : this(
        scheduler = scheduler,
        service = service,
        elapsedRealtime = SystemClock::elapsedRealtime,
        dpToPx = { it._dp },
        hintSink = NoOpEdgeGestureSequenceHintSink,
        debugLog = {},
    )

    constructor(
        scheduler: GestureSequenceScheduler,
        service: GestureBridge,
        elapsedRealtime: () -> Long,
    ) : this(
        scheduler = scheduler,
        service = service,
        elapsedRealtime = elapsedRealtime,
        dpToPx = { it._dp },
        hintSink = NoOpEdgeGestureSequenceHintSink,
        debugLog = {},
    )

    constructor(
        scheduler: GestureSequenceScheduler,
        service: GestureBridge,
        elapsedRealtime: () -> Long,
        dpToPx: (Int) -> Int,
    ) : this(
        scheduler = scheduler,
        service = service,
        elapsedRealtime = elapsedRealtime,
        dpToPx = dpToPx,
        hintSink = NoOpEdgeGestureSequenceHintSink,
        debugLog = {},
    )

    constructor(
        scheduler: GestureSequenceScheduler,
        service: GestureBridge,
        elapsedRealtime: () -> Long,
        dpToPx: (Int) -> Int,
        hintSink: EdgeGestureSequenceHintSink,
    ) : this(
        scheduler = scheduler,
        service = service,
        elapsedRealtime = elapsedRealtime,
        dpToPx = dpToPx,
        hintSink = hintSink,
        debugLog = tornaco.apps.shortx.core.util.Logger("EdgeGestureSequence")::d,
    )

    companion object {
        private const val DIAG_PREFIX = "[SX_EDGE_DIAG]"

        // 纯 JVM 测试路径不直接依赖 MotionEvent 常量，所以这里保留一份本地副本。
        private const val INVALID_POINTER_ID = -1
        private const val ACTION_UP = 1
        private const val ACTION_MOVE = 2
        private const val ACTION_CANCEL = 3
        private const val ACTION_POINTER_UP = 6
    }

    // 当前正在跟踪的那根手指。sequence 必须沿用同一触摸流，换指或抬起都直接取消。
    private var trackedPointerId = INVALID_POINTER_ID
    private var primaryGesture: Gesture? = null
    private var downX = 0f
    private var downY = 0f

    // anchor 表示“当前这一步”开始计算位移的起点。
    // 每匹配完一步，anchor 会推进到最新位置，下一步从这里继续判断。
    private var anchorX = 0f
    private var anchorY = 0f
    private var latestX = 0f
    private var latestY = 0f

    // 当前步骤开始的时间，而不是整个 sequence 的开始时间。
    // 仅用于 Hover 这类需要持续时长的步骤，不用于等待下一步时的自动超时取消。
    private var startedAtElapsed = 0L
    private var currentStepIndex = 0
    private val hoverAutoCompleteRunnable = Runnable {
        handleHoverAutoCompletion()
    }

    // candidates 表示“到当前步骤为止仍然可能匹配成功”的规则集合。
    // 每命中一步都会收缩一次，直到只剩下完成项或全部淘汰。
    private var candidates: List<EdgeGestureSequenceCandidate> = emptyList()
    private val matchedSteps = mutableListOf<EdgeGestureStep>()

    private data class DirectionalStepCandidate(
        val step: EdgeGestureStep,
        val dot: Float,
    )

    fun startTracking(
        pointerId: Int,
        primaryGesture: Gesture,
        downX: Float,
        downY: Float,
        anchorX: Float,
        anchorY: Float,
        candidates: List<EdgeGestureSequenceCandidate>,
    ): Boolean {
        if (candidates.isEmpty()) return false
        cancelTracking("restart tracking")
        this.trackedPointerId = pointerId
        this.primaryGesture = primaryGesture
        this.downX = downX
        this.downY = downY
        this.anchorX = anchorX
        this.anchorY = anchorY
        this.latestX = anchorX
        this.latestY = anchorY
        this.startedAtElapsed = elapsedRealtime()
        this.candidates = candidates
        this.currentStepIndex = 0
        this.matchedSteps.clear()
        // 一进入二段识别，就先把当前目标步骤的提示显示出来。
        emitCurrentStepHint()
        scheduleHoverAutoCompletionIfNeeded()
        debugLog(
            "$DIAG_PREFIX startTracking: primary=$primaryGesture, candidates=${candidates.size}, anchor=($anchorX,$anchorY), down=($downX,$downY), rules=${formatCandidates(candidates)}"
        )
        return true
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        return onTouchEvent(AndroidGestureSequencePointerEvent(event))
    }

    fun onTouchEvent(event: GestureSequencePointerEvent): Boolean {
        if (!isTracking()) return false
        when (event.actionMasked) {
            ACTION_MOVE -> {
                val pointerIndex = event.findPointerIndex(trackedPointerId)
                if (pointerIndex < 0) {
                    debugLog("$DIAG_PREFIX cancel: tracked pointer missing, pointerId=$trackedPointerId")
                    cancelTracking("tracked pointer missing")
                    return false
                }
                latestX = event.getX(pointerIndex)
                latestY = event.getY(pointerIndex)

                // hint 是连续更新的，用户在做当前步骤时可以实时看到进度和 ready 状态。
                updateCurrentStepHint()
                val matchedStep = findMatchedDirectionalStep(latestX, latestY) ?: run {
                    val hoverOnly = hasOnlyHoverCandidates()
                    if (hoverOnly && distanceFromAnchor(latestX, latestY) > hoverRadiusPx()) {
                        resetHoverAnchor(latestX, latestY)
                        return true
                    }
                    if (!service.isEdgeGestureSequenceChildSwipeTriggerOnRelease() && tryCompleteHoverOnMove()) {
                        return true
                    }
                    logDirectionalMiss()
                    return true
                }
                val matchedCandidates = candidates.filter {
                    it.rule.getChildStepsOrNull(currentStepIndex)?.type == matchedStep.type
                }
                if (service.isEdgeGestureSequenceChildSwipeTriggerOnRelease()) {
                    debugLog(
                        "$DIAG_PREFIX directional move ready: stepIndex=$currentStepIndex, matched=${matchedStep.type}, waiting for release"
                    )
                    return true
                }
                advanceCandidates(matchedCandidates, matchedStep)
                return true
            }

            ACTION_UP -> {
                updateLatestCoordinatesForTrackedPointer(event)
                if (tryCompleteHoverOnPointerRelease()) {
                    return true
                }
                if (tryCompleteDirectionalStepOnPointerRelease()) {
                    return true
                }
                cancelTracking("pointer up")
                return true
            }

            ACTION_CANCEL -> {
                cancelTracking("pointer cancel")
                return true
            }

            ACTION_POINTER_UP -> {
                val pointerId = event.getPointerId(event.actionIndex)
                if (pointerId == trackedPointerId) {
                    latestX = event.getX(event.actionIndex)
                    latestY = event.getY(event.actionIndex)
                    if (tryCompleteHoverOnPointerRelease()) {
                        return true
                    }
                    if (tryCompleteDirectionalStepOnPointerRelease()) {
                        return true
                    }
                    cancelTracking("tracked pointer pointer-up")
                    return true
                }
            }
        }
        return true
    }

    fun isTracking(): Boolean {
        return trackedPointerId != INVALID_POINTER_ID && candidates.isNotEmpty() && primaryGesture != null
    }

    fun cancelTracking() {
        cancelTracking("manual")
    }

    private fun cancelTracking(reason: String) {
        val wasTracking = isTracking()
        clearHoverAutoCompletion()
        trackedPointerId = INVALID_POINTER_ID
        primaryGesture = null
        downX = 0f
        downY = 0f
        anchorX = 0f
        anchorY = 0f
        latestX = 0f
        latestY = 0f
        startedAtElapsed = 0L
        currentStepIndex = 0
        candidates = emptyList()
        matchedSteps.clear()
        if (wasTracking) {
            debugLog("$DIAG_PREFIX cancelTracking: reason=$reason")
            hintSink.onSequenceCancelled()
        }
    }

    private fun findMatchedDirectionalStep(x: Float, y: Float): EdgeGestureStep? {
        return findBestDirectionalCandidate(x, y, requireThreshold = true)?.step
    }

    private fun findBestDirectionalCandidate(
        x: Float,
        y: Float,
        requireThreshold: Boolean,
    ): DirectionalStepCandidate? {
        val deltaX = x - anchorX
        val deltaY = y - anchorY
        val distance = hypot(deltaX.toDouble(), deltaY.toDouble()).toFloat()
        if (distance <= 0f) return null
        if (requireThreshold && distance < childSwipeMinDistancePx()) return null
        val normalizedX = deltaX / distance
        val normalizedY = deltaY / distance
        return candidates.asSequence()
            .mapNotNull { candidate ->
                val step = candidate.rule.getChildStepsOrNull(currentStepIndex) ?: return@mapNotNull null
                val direction = directionFor(step.type) ?: return@mapNotNull null
                val dot = normalizedX * direction.first + normalizedY * direction.second

                // 用点积做方向判断：越接近 1 越同向，低于阈值就认为偏得太多。
                if (requireThreshold && dot < minDirectionDot()) return@mapNotNull null
                DirectionalStepCandidate(step = step, dot = dot)
            }
            .maxByOrNull { it.dot }
    }

    private fun advanceCandidates(
        matchedCandidates: List<EdgeGestureSequenceCandidate>,
        matchedStep: EdgeGestureStep,
    ) {
        clearHoverAutoCompletion()
        matchedSteps.add(matchedStep)
        debugLog(
            "$DIAG_PREFIX advanceCandidates: matched=${matchedStep.type}, stepIndex=$currentStepIndex, matchedSteps=${matchedSteps.map { it.type }}, matchedCandidates=${matchedCandidates.size}"
        )

        val completedCandidates = matchedCandidates.filter {
            it.rule.childStepsCount == currentStepIndex + 1
        }
        val pendingCandidates = matchedCandidates.filter {
            it.rule.childStepsCount > currentStepIndex + 1
        }

        if (pendingCandidates.isNotEmpty()) {
            // 还有后续步骤时，不立即触发事实，而是推进到下一步继续跟踪。
            candidates = pendingCandidates
            currentStepIndex += 1
            anchorX = latestX
            anchorY = latestY
            startedAtElapsed = elapsedRealtime()
            emitCurrentStepHint()
            scheduleHoverAutoCompletionIfNeeded()
            debugLog(
                "$DIAG_PREFIX advanceCandidates: continue stepIndex=$currentStepIndex, nextStep=${currentHintStep()?.type}, pending=${formatCandidates(pendingCandidates)}"
            )
            return
        }

        completedCandidates
            .distinctBy { it.area }
            .forEach {
                debugLog(
                    "$DIAG_PREFIX fire sequence: area=${it.area}, gesture=${requireNotNull(primaryGesture)}, childSteps=${matchedSteps.map { step -> step.type }}"
                )
                service.fireEdgeGestureSequence(
                    area = it.area,
                    gesture = requireNotNull(primaryGesture),
                    childSteps = matchedSteps.toList(),
                    downX = downX,
                    downY = downY,
                )
            }
        hintSink.onSequenceSuccess()
        clearTrackingState()
    }

    private fun clearTrackingState() {
        clearHoverAutoCompletion()
        trackedPointerId = INVALID_POINTER_ID
        primaryGesture = null
        downX = 0f
        downY = 0f
        anchorX = 0f
        anchorY = 0f
        latestX = 0f
        latestY = 0f
        startedAtElapsed = 0L
        currentStepIndex = 0
        candidates = emptyList()
        matchedSteps.clear()
    }

    private fun updateLatestCoordinatesForTrackedPointer(event: GestureSequencePointerEvent) {
        val pointerIndex = event.findPointerIndex(trackedPointerId)
        if (pointerIndex >= 0) {
            latestX = event.getX(pointerIndex)
            latestY = event.getY(pointerIndex)
        }
    }

    private fun tryCompleteHoverOnPointerRelease(): Boolean {
        if (!service.isEdgeGestureSequenceChildSwipeTriggerOnRelease()) return false

        val hoverCandidates = candidates.filter {
            it.rule.getChildStepsOrNull(currentStepIndex)?.type == EdgeGestureStepType.Hover
        }
        if (hoverCandidates.isEmpty() || !hasOnlyHoverCandidates()) return false

        val elapsed = elapsedRealtime() - startedAtElapsed
        val distance = distanceFromAnchor(latestX, latestY)
        if (elapsed < hoverDurationMs()) {
            debugLog(
                "$DIAG_PREFIX hover release miss: stepIndex=$currentStepIndex, elapsed=$elapsed/${hoverDurationMs()}, distance=$distance, radiusPx=${hoverRadiusPx()}"
            )
            return false
        }
        if (distance > hoverRadiusPx()) {
            debugLog(
                "$DIAG_PREFIX hover release cancel: stepIndex=$currentStepIndex, elapsed=$elapsed/${hoverDurationMs()}, distance=$distance, radiusPx=${hoverRadiusPx()}"
            )
            return false
        }

        debugLog(
            "$DIAG_PREFIX hover release matched: stepIndex=$currentStepIndex, elapsed=$elapsed/${hoverDurationMs()}, distance=$distance, radiusPx=${hoverRadiusPx()}"
        )
        advanceCandidates(hoverCandidates, hoverCandidates.first().rule.getChildSteps(currentStepIndex))
        return true
    }

    private fun tryCompleteHoverOnMove(): Boolean {
        val hoverCandidates = candidates.filter {
            it.rule.getChildStepsOrNull(currentStepIndex)?.type == EdgeGestureStepType.Hover
        }
        if (hoverCandidates.isEmpty() || !hasOnlyHoverCandidates()) return false

        val elapsed = elapsedRealtime() - startedAtElapsed
        val distance = distanceFromAnchor(latestX, latestY)
        if (elapsed < hoverDurationMs()) return false
        if (distance > hoverRadiusPx()) return false

        debugLog(
            "$DIAG_PREFIX hover move matched: stepIndex=$currentStepIndex, elapsed=$elapsed/${hoverDurationMs()}, distance=$distance, radiusPx=${hoverRadiusPx()}"
        )
        advanceCandidates(hoverCandidates, hoverCandidates.first().rule.getChildSteps(currentStepIndex))
        return true
    }

    private fun handleHoverAutoCompletion() {
        if (!isTracking()) return
        if (service.isEdgeGestureSequenceChildSwipeTriggerOnRelease()) return
        if (!hasOnlyHoverCandidates()) return
        if (tryCompleteHoverOnMove()) return

        val distance = distanceFromAnchor(latestX, latestY)
        debugLog(
            "$DIAG_PREFIX hover auto-complete skipped: stepIndex=$currentStepIndex, elapsed=${elapsedRealtime() - startedAtElapsed}/${hoverDurationMs()}, distance=$distance, radiusPx=${hoverRadiusPx()}"
        )
    }

    private fun resetHoverAnchor(x: Float, y: Float) {
        debugLog(
            "$DIAG_PREFIX hover anchor reset: stepIndex=$currentStepIndex, from=($anchorX,$anchorY), to=($x,$y), radiusPx=${hoverRadiusPx()}"
        )
        anchorX = x
        anchorY = y
        startedAtElapsed = elapsedRealtime()
        emitCurrentStepHint()
        scheduleHoverAutoCompletionIfNeeded()
    }

    private fun scheduleHoverAutoCompletionIfNeeded() {
        clearHoverAutoCompletion()
        if (service.isEdgeGestureSequenceChildSwipeTriggerOnRelease()) return
        if (!hasOnlyHoverCandidates()) return
        scheduler.postDelayed(hoverAutoCompleteRunnable, hoverDurationMs())
    }

    private fun clearHoverAutoCompletion() {
        scheduler.removeCallbacks(hoverAutoCompleteRunnable)
    }

    private fun tryCompleteDirectionalStepOnPointerRelease(): Boolean {
        if (!service.isEdgeGestureSequenceChildSwipeTriggerOnRelease()) return false

        val matchedStep = findBestDirectionalCandidate(
            x = latestX,
            y = latestY,
            requireThreshold = true,
        )?.step ?: return false

        val matchedCandidates = candidates.filter {
            it.rule.getChildStepsOrNull(currentStepIndex)?.type == matchedStep.type
        }
        if (matchedCandidates.isEmpty()) return false

        debugLog(
            "$DIAG_PREFIX directional release matched: stepIndex=$currentStepIndex, matched=${matchedStep.type}, anchor=($anchorX,$anchorY), latest=($latestX,$latestY)"
        )
        advanceCandidates(matchedCandidates, matchedStep)
        return true
    }

    private fun emitCurrentStepHint() {
        val step = resolveHintStep(anchorX, anchorY) ?: return
        hintSink.onStepHint(
            anchorX = anchorX,
            anchorY = anchorY,
            step = step,
            progress = if (step.type == EdgeGestureStepType.Hover) 0.28f else 0.12f,
            isReady = false,
            hintEnabled = currentStepHintEnabled(),
            hapticEnabled = currentStepHapticEnabled(),
        )
    }

    private fun updateCurrentStepHint() {
        val step = resolveHintStep(latestX, latestY) ?: return
        when (step.type) {
            EdgeGestureStepType.Hover -> {
                // Hover 用时间推进 progress，而不是位移。
                val progress = (elapsedRealtime() - startedAtElapsed).toFloat() / hoverDurationMs()
                hintSink.onStepHint(
                    anchorX = anchorX,
                    anchorY = anchorY,
                    step = step,
                    progress = progress.coerceIn(0.12f, 1f),
                    isReady = progress >= 1f,
                    hintEnabled = currentStepHintEnabled(),
                    hapticEnabled = currentStepHapticEnabled(),
                )
            }

            else -> {
                // 方向步骤用“朝目标方向移动了多远”来表达当前进度。
                val direction = directionFor(step.type) ?: return
                val deltaX = latestX - anchorX
                val deltaY = latestY - anchorY
                val distance = hypot(deltaX.toDouble(), deltaY.toDouble()).toFloat()
                val readyDistance = childSwipeMinDistancePx()
                val normalizedX = if (distance > 0f) deltaX / distance else 0f
                val normalizedY = if (distance > 0f) deltaY / distance else 0f
                val dot = normalizedX * direction.first + normalizedY * direction.second
                hintSink.onStepHint(
                    anchorX = anchorX,
                    anchorY = anchorY,
                    step = step,
                    progress = (distance / readyDistance).coerceIn(0.12f, 1f),
                    isReady = distance >= readyDistance && dot >= minDirectionDot(),
                    hintEnabled = currentStepHintEnabled(),
                    hapticEnabled = currentStepHapticEnabled(),
                )
            }
        }
    }

    private fun currentStepHapticEnabled(): Boolean {
        return candidates.any {
            it.rule.hasEnableGestureHaptic() && it.rule.enableGestureHaptic
        }
    }

    private fun currentStepHintEnabled(): Boolean {
        return candidates.any {
            it.rule.hasEnableGestureHint() && it.rule.enableGestureHint
        }
    }

    private fun currentHintStep(): EdgeGestureStep? {
        val currentStepTypes = candidates.mapNotNull {
            it.rule.getChildStepsOrNull(currentStepIndex)?.type
        }.distinct()

        // 只有当前剩余候选在“下一步类型”上一致时，hint 才有明确方向。
        // 如果规则分叉到了不同的下一步，这里宁可不画，也不误导用户。
        if (currentStepTypes.size != 1) return null
        return EdgeGestureStep.newBuilder().setType(currentStepTypes.single()).build()
    }

    private fun resolveHintStep(x: Float, y: Float): EdgeGestureStep? {
        return currentHintStep() ?: findBestDirectionalCandidate(
            x = x,
            y = y,
            requireThreshold = false,
        )?.step
    }

    private fun logDirectionalMiss() {
        val step = resolveHintStep(latestX, latestY) ?: return
        if (step.type == EdgeGestureStepType.Hover) return
        val direction = directionFor(step.type) ?: return
        val deltaX = latestX - anchorX
        val deltaY = latestY - anchorY
        val distance = hypot(deltaX.toDouble(), deltaY.toDouble()).toFloat()
        val minDistancePx = childSwipeMinDistancePx()
        val normalizedX = if (distance > 0f) deltaX / distance else 0f
        val normalizedY = if (distance > 0f) deltaY / distance else 0f
        val dot = normalizedX * direction.first + normalizedY * direction.second
        val angleDeg = Math.toDegrees(acos(dot.coerceIn(-1f, 1f).toDouble()))
        debugLog(
            "$DIAG_PREFIX directional miss: stepIndex=$currentStepIndex, expected=${step.type}, distance=$distance/$minDistancePx, dot=$dot/${minDirectionDot()}, angleDeg=$angleDeg/${childSwipeMaxAngleDeviationDeg()}, anchor=($anchorX,$anchorY), latest=($latestX,$latestY)"
        )
    }

    private fun hoverDurationMs(): Long = service.getEdgeGestureSequenceHoverDurationMs().toLong()

    private fun hoverRadiusPx(): Float = dpToPx(service.getEdgeGestureSequenceHoverRadiusDp()).toFloat()

    private fun childSwipeMinDistancePx(): Float =
        dpToPx(service.getEdgeGestureSequenceChildSwipeMinDistanceDp()).toFloat()

    private fun childSwipeMaxAngleDeviationDeg(): Float =
        service.getEdgeGestureSequenceChildSwipeMaxAngleDeviationDeg().toFloat()

    private fun minDirectionDot(): Float =
        cos(Math.toRadians(childSwipeMaxAngleDeviationDeg().toDouble())).toFloat()

    private fun formatCandidates(candidates: List<EdgeGestureSequenceCandidate>): String {
        return candidates.joinToString(prefix = "[", postfix = "]") {
            "{area=${it.area}, childSteps=${it.rule.childStepsList.map { step -> step.type }}}"
        }
    }

    private fun distanceFromAnchor(x: Float, y: Float): Float {
        return hypot((x - anchorX).toDouble(), (y - anchorY).toDouble()).toFloat()
    }

    private fun hasOnlyHoverCandidates(): Boolean {
        return candidates.isNotEmpty() && candidates.all {
            it.rule.getChildStepsOrNull(currentStepIndex)?.type == EdgeGestureStepType.Hover
        }
    }

    private fun directionFor(type: EdgeGestureStepType): Pair<Float, Float>? {
        return when (type) {
            EdgeGestureStepType.SwipeLeft -> -1f to 0f
            EdgeGestureStepType.SwipeRight -> 1f to 0f
            EdgeGestureStepType.SwipeUp -> 0f to -1f
            EdgeGestureStepType.SwipeDown -> 0f to 1f
            else -> null
        }
    }

    private fun tornaco.apps.shortx.core.proto.fact.EdgeGesture.getChildStepsOrNull(index: Int): EdgeGestureStep? {
        return if (childStepsCount > index) getChildSteps(index) else null
    }
}