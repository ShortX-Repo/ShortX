package tornaco.apps.shortx.services.gesture.bpok.gesture

import android.graphics.PointF
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
import kotlin.math.max

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
    private val toAbsolutePortraitPoint: (Float, Float) -> PointF,
    private val debugLog: (String) -> Unit,
) {
    constructor(handler: Handler, service: GestureBridge) : this(
        scheduler = AndroidGestureSequenceScheduler(handler),
        service = service,
        elapsedRealtime = SystemClock::elapsedRealtime,
        dpToPx = { it._dp },
        hintSink = NoOpEdgeGestureSequenceHintSink,
        toAbsolutePortraitPoint = { x, y -> PointF(x, y) },
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
        toAbsolutePortraitPoint = { x, y -> PointF(x, y) },
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
        toAbsolutePortraitPoint = { x, y -> PointF(x, y) },
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
        toAbsolutePortraitPoint = { x, y -> PointF(x, y) },
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
        toAbsolutePortraitPoint = { x, y -> PointF(x, y) },
        debugLog = tornaco.apps.shortx.core.util.Logger("EdgeGestureSequence")::d,
    )

    constructor(
        scheduler: GestureSequenceScheduler,
        service: GestureBridge,
        elapsedRealtime: () -> Long,
        dpToPx: (Int) -> Int,
        hintSink: EdgeGestureSequenceHintSink,
        debugLog: (String) -> Unit,
    ) : this(
        scheduler = scheduler,
        service = service,
        elapsedRealtime = elapsedRealtime,
        dpToPx = dpToPx,
        hintSink = hintSink,
        toAbsolutePortraitPoint = { x, y -> PointF(x, y) },
        debugLog = debugLog,
    )

    constructor(
        scheduler: GestureSequenceScheduler,
        service: GestureBridge,
        elapsedRealtime: () -> Long,
        dpToPx: (Int) -> Int,
        hintSink: EdgeGestureSequenceHintSink,
        toAbsolutePortraitPoint: (Float, Float) -> PointF,
    ) : this(
        scheduler = scheduler,
        service = service,
        elapsedRealtime = elapsedRealtime,
        dpToPx = dpToPx,
        hintSink = hintSink,
        toAbsolutePortraitPoint = toAbsolutePortraitPoint,
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

    private sealed interface StepPhase {
        object Undecided : StepPhase

        data class HoverTracking(
            val anchorX: Float,
            val anchorY: Float,
            val startedAtElapsed: Long,
        ) : StepPhase

        data class LockedDirection(
            val type: EdgeGestureStepType,
            val peakDistancePx: Float,
        ) : StepPhase
    }

    // 当前正在跟踪的那根手指。sequence 必须沿用同一触摸流，换指或抬起都直接取消。
    private var trackedPointerId = INVALID_POINTER_ID
    private var primaryGesture: Gesture? = null
    private var downX = 0f
    private var downY = 0f
    private var downAbsoluteX = 0f
    private var downAbsoluteY = 0f

    // anchor 表示“当前这一步”开始计算位移的起点。
    // 每匹配完一步，anchor 会推进到最新位置，下一步从这里继续判断。
    private var anchorX = 0f
    private var anchorY = 0f
    private var anchorAbsoluteX = 0f
    private var anchorAbsoluteY = 0f
    private var hoverAnchorX = 0f
    private var hoverAnchorY = 0f
    private var latestX = 0f
    private var latestY = 0f
    private var latestAbsoluteX = 0f
    private var latestAbsoluteY = 0f

    // 当前步骤开始的时间，而不是整个 sequence 的开始时间。
    // 仅用于 Hover 这类需要持续时长的步骤，不用于等待下一步时的自动超时取消。
    private var startedAtElapsed = 0L
    private var currentStepIndex = 0
    private var stepPhase: StepPhase = StepPhase.Undecided
    private val hoverAutoCompleteRunnable = Runnable {
        handleHoverAutoCompletion()
    }

    // candidates 表示“到当前步骤为止仍然可能匹配成功”的规则集合。
    // 每命中一步都会收缩一次，直到只剩下完成项或全部淘汰。
    private var candidates: List<EdgeGestureSequenceCandidate> = emptyList()
    private var currentStepCandidates: List<CurrentStepCandidate> = emptyList()
    private var hoverStepCandidates: List<CurrentStepCandidate> = emptyList()
    private var directionalStepCandidates: List<CurrentStepCandidate> = emptyList()
    private var currentStepTypes: Set<EdgeGestureStepType> = emptySet()
    private var cachedCurrentStepHintEnabled = false
    private var cachedCurrentStepHapticEnabled = false
    private var cachedHoverDurationMs = 0L
    private var cachedHoverRadiusPx = 0f
    private var cachedChildSwipeMinDistancePx = 0f
    private var cachedChildSwipeMaxAngleDeviationDeg = 0f
    private var cachedMinDirectionDot = 0f
    private var cachedChildSwipeTriggerOnRelease = false
    private var cachedDirectionalIntentLockDistancePx = 0f
    private var cachedDirectionalRollbackTolerancePx = 0f
    private val matchedSteps = mutableListOf<EdgeGestureStep>()

    private data class CurrentStepCandidate(
        val candidate: EdgeGestureSequenceCandidate,
        val step: EdgeGestureStep,
    )

    private data class DirectionalStepCandidate(
        val step: EdgeGestureStep,
        val dot: Float,
        val projectedDistance: Float,
    )

    private data class DirectionalMotionMetrics(
        val deltaX: Float,
        val deltaY: Float,
        val distance: Float,
        val normalizedX: Float,
        val normalizedY: Float,
    ) {
        fun dotFor(direction: Pair<Float, Float>): Float {
            return normalizedX * direction.first + normalizedY * direction.second
        }

        fun projectedDistanceFor(type: EdgeGestureStepType): Float {
            return when (type) {
                EdgeGestureStepType.SwipeLeft -> max(0f, -deltaX)
                EdgeGestureStepType.SwipeRight -> max(0f, deltaX)
                EdgeGestureStepType.SwipeUp -> max(0f, -deltaY)
                EdgeGestureStepType.SwipeDown -> max(0f, deltaY)
                else -> 0f
            }
        }

        fun dominantType(): EdgeGestureStepType {
            val absX = kotlin.math.abs(deltaX)
            val absY = kotlin.math.abs(deltaY)
            return if (absX >= absY) {
                if (deltaX < 0f) EdgeGestureStepType.SwipeLeft else EdgeGestureStepType.SwipeRight
            } else {
                if (deltaY < 0f) EdgeGestureStepType.SwipeUp else EdgeGestureStepType.SwipeDown
            }
        }
    }

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
        toAbsolutePortraitPoint(downX, downY).also {
            this.downAbsoluteX = it.x
            this.downAbsoluteY = it.y
        }
        this.anchorX = anchorX
        this.anchorY = anchorY
        toAbsolutePortraitPoint(anchorX, anchorY).also {
            this.anchorAbsoluteX = it.x
            this.anchorAbsoluteY = it.y
            this.latestAbsoluteX = it.x
            this.latestAbsoluteY = it.y
        }
        this.hoverAnchorX = anchorX
        this.hoverAnchorY = anchorY
        this.latestX = anchorX
        this.latestY = anchorY
        this.startedAtElapsed = elapsedRealtime()
        this.candidates = candidates
        this.currentStepIndex = 0
        this.matchedSteps.clear()
        refreshThresholdCache()
        refreshCurrentStepSnapshot()
        initializeStepPhase()
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
                updateLatestAbsoluteCoordinates(latestX, latestY)
                return handleMove()
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
                    updateLatestAbsoluteCoordinates(latestX, latestY)
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

    private fun handleMove(): Boolean {
        updateDirectionalIntentTracking()

        // hint 是连续更新的，用户在做当前步骤时可以实时看到进度和 ready 状态。
        updateCurrentStepHint()

        val matchedStep = findMatchedDirectionalStep(latestX, latestY)
        if (matchedStep != null) {
            val matchedCandidates = matchedCandidatesFor(matchedStep.type)
            if (isChildSwipeTriggerOnRelease()) {
                debugLog(
                    "$DIAG_PREFIX directional move ready: stepIndex=$currentStepIndex, matched=${matchedStep.type}, waiting for release"
                )
                return true
            }
            advanceCandidates(matchedCandidates, matchedStep)
            return true
        }

        if (resetHoverTrackingWindowIfNeeded()) {
            return true
        }
        if (!isChildSwipeTriggerOnRelease() && tryCompleteHoverOnMove()) {
            return true
        }
        logDirectionalMiss()
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
        resetDetectorState()
        if (wasTracking) {
            debugLog("$DIAG_PREFIX cancelTracking: reason=$reason")
            hintSink.onSequenceCancelled()
        }
    }

    private fun findMatchedDirectionalStep(x: Float, y: Float): EdgeGestureStep? {
        findLockedDirectionalMatch(x, y)?.let { return it.step }
        return findBestDirectionalCandidate(x, y, requireThreshold = true)?.step
    }

    private fun findBestDirectionalCandidate(
        x: Float,
        y: Float,
        requireThreshold: Boolean,
    ): DirectionalStepCandidate? {
        return directionalStepCandidates.asSequence()
            .mapNotNull { candidate ->
                val step = candidate.step
                val direction = directionFor(step.type) ?: return@mapNotNull null
                val metrics = directionalMotionMetrics(candidate, x, y) ?: return@mapNotNull null
                if (requireThreshold && metrics.distance < childSwipeMinDistancePx()) return@mapNotNull null
                val dot = metrics.dotFor(direction)
                val projectedDistance = metrics.projectedDistanceFor(step.type)

                // 用点积做方向判断：越接近 1 越同向，低于阈值就认为偏得太多。
                if (requireThreshold && dot < minDirectionDot()) return@mapNotNull null
                DirectionalStepCandidate(step = step, dot = dot, projectedDistance = projectedDistance)
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
            anchorAbsoluteX = latestAbsoluteX
            anchorAbsoluteY = latestAbsoluteY
            hoverAnchorX = latestX
            hoverAnchorY = latestY
            startedAtElapsed = elapsedRealtime()
            refreshCurrentStepSnapshot()
            initializeStepPhase()
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
        resetDetectorState()
    }

    private fun resetDetectorState() {
        clearHoverAutoCompletion()
        trackedPointerId = INVALID_POINTER_ID
        primaryGesture = null
        downX = 0f
        downY = 0f
        downAbsoluteX = 0f
        downAbsoluteY = 0f
        anchorX = 0f
        anchorY = 0f
        anchorAbsoluteX = 0f
        anchorAbsoluteY = 0f
        hoverAnchorX = 0f
        hoverAnchorY = 0f
        latestX = 0f
        latestY = 0f
        latestAbsoluteX = 0f
        latestAbsoluteY = 0f
        startedAtElapsed = 0L
        currentStepIndex = 0
        candidates = emptyList()
        clearCurrentStepSnapshot()
        clearThresholdCache()
        stepPhase = StepPhase.Undecided
        matchedSteps.clear()
    }

    private fun updateLatestCoordinatesForTrackedPointer(event: GestureSequencePointerEvent) {
        val pointerIndex = event.findPointerIndex(trackedPointerId)
        if (pointerIndex >= 0) {
            latestX = event.getX(pointerIndex)
            latestY = event.getY(pointerIndex)
            updateLatestAbsoluteCoordinates(latestX, latestY)
        }
    }

    private fun tryCompleteHoverOnPointerRelease(): Boolean {
        if (!isChildSwipeTriggerOnRelease()) return false

        val hoverCandidates = hoverCandidates()
        if (hoverCandidates.isEmpty()) return false
        if (hasLockedDirectionalIntent()) return false

        val hoverPhase = stepPhase as? StepPhase.HoverTracking ?: return false
        val elapsed = elapsedRealtime() - hoverPhase.startedAtElapsed
        val distance = hoverDistanceFromAnchor(latestX, latestY)
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
        advanceCandidates(hoverCandidates.map { it.candidate }, hoverCandidates.first().step)
        return true
    }

    private fun tryCompleteHoverOnMove(): Boolean {
        val hoverCandidates = hoverCandidates()
        if (hoverCandidates.isEmpty()) return false
        if (hasLockedDirectionalIntent()) return false

        val hoverPhase = stepPhase as? StepPhase.HoverTracking ?: return false
        val elapsed = elapsedRealtime() - hoverPhase.startedAtElapsed
        val distance = hoverDistanceFromAnchor(latestX, latestY)
        if (elapsed < hoverDurationMs()) return false
        if (distance > hoverRadiusPx()) return false

        debugLog(
            "$DIAG_PREFIX hover move matched: stepIndex=$currentStepIndex, elapsed=$elapsed/${hoverDurationMs()}, distance=$distance, radiusPx=${hoverRadiusPx()}"
        )
        advanceCandidates(hoverCandidates.map { it.candidate }, hoverCandidates.first().step)
        return true
    }

    private fun handleHoverAutoCompletion() {
        if (!isTracking()) return
        if (isChildSwipeTriggerOnRelease()) return
        if (!hasHoverCandidates()) return
        if (hasLockedDirectionalIntent()) return
        if (tryCompleteHoverOnMove()) return

        val distance = hoverDistanceFromAnchor(latestX, latestY)
        debugLog(
            "$DIAG_PREFIX hover auto-complete skipped: stepIndex=$currentStepIndex, elapsed=${elapsedRealtime() - hoverTrackingStartedAtElapsed()}/${hoverDurationMs()}, distance=$distance, radiusPx=${hoverRadiusPx()}"
        )
    }

    private fun scheduleHoverAutoCompletionIfNeeded() {
        clearHoverAutoCompletion()
        if (isChildSwipeTriggerOnRelease()) return
        if (!hasHoverCandidates()) return
        scheduler.postDelayed(hoverAutoCompleteRunnable, hoverDurationMs())
    }

    private fun clearHoverAutoCompletion() {
        scheduler.removeCallbacks(hoverAutoCompleteRunnable)
    }

    private fun tryCompleteDirectionalStepOnPointerRelease(): Boolean {
        if (!isChildSwipeTriggerOnRelease()) return false

        val matchedStep = findMatchedDirectionalStep(
            x = latestX,
            y = latestY,
        ) ?: return false

        val matchedCandidates = matchedCandidatesFor(matchedStep.type)
        if (matchedCandidates.isEmpty()) return false

        debugLog(
            "$DIAG_PREFIX directional release matched: stepIndex=$currentStepIndex, matched=${matchedStep.type}, anchor=($anchorX,$anchorY), latest=($latestX,$latestY)"
        )
        advanceCandidates(matchedCandidates, matchedStep)
        return true
    }

    private fun emitCurrentStepHint() {
        val step = resolveHintStep(anchorX, anchorY) ?: return
        val hintAnchorX = if (step.type == EdgeGestureStepType.Hover) hoverTrackingAnchorX() else anchorX
        val hintAnchorY = if (step.type == EdgeGestureStepType.Hover) hoverTrackingAnchorY() else anchorY
        hintSink.onStepHint(
            anchorX = hintAnchorX,
            anchorY = hintAnchorY,
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
                val progress = (elapsedRealtime() - hoverTrackingStartedAtElapsed()).toFloat() / hoverDurationMs()
                hintSink.onStepHint(
                    anchorX = hoverTrackingAnchorX(),
                    anchorY = hoverTrackingAnchorY(),
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
                val hintCandidate = directionalStepCandidates.firstOrNull { it.step.type == step.type } ?: return
                val metrics = directionalMotionMetrics(hintCandidate, latestX, latestY) ?: return
                val readyDistance = childSwipeMinDistancePx()
                val dot = metrics.dotFor(direction)
                hintSink.onStepHint(
                    anchorX = anchorX,
                    anchorY = anchorY,
                    step = step,
                    progress = (metrics.distance / readyDistance).coerceIn(0.12f, 1f),
                    isReady = metrics.distance >= readyDistance && dot >= minDirectionDot(),
                    hintEnabled = currentStepHintEnabled(),
                    hapticEnabled = currentStepHapticEnabled(),
                )
            }
        }
    }

    private fun currentStepHapticEnabled(): Boolean {
        return cachedCurrentStepHapticEnabled
    }

    private fun currentStepHintEnabled(): Boolean {
        return cachedCurrentStepHintEnabled
    }

    private fun currentHintStep(): EdgeGestureStep? {
        val phase = stepPhase
        if (phase is StepPhase.LockedDirection) {
            val lockedStep = currentStepCandidates.firstNotNullOfOrNull {
                it.step.takeIf { step -> step.type == phase.type }
            }
            if (lockedStep != null) return lockedStep
        }

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
        val missCandidate = directionalStepCandidates.firstOrNull { it.step.type == step.type } ?: return
        val metrics = directionalMotionMetrics(missCandidate, latestX, latestY) ?: return
        val minDistancePx = childSwipeMinDistancePx()
        val dot = metrics.dotFor(direction)
        val angleDeg = Math.toDegrees(acos(dot.coerceIn(-1f, 1f).toDouble()))
        debugLog(
            "$DIAG_PREFIX directional miss: stepIndex=$currentStepIndex, expected=${step.type}, distance=${metrics.distance}/$minDistancePx, dot=$dot/${minDirectionDot()}, angleDeg=$angleDeg/${childSwipeMaxAngleDeviationDeg()}, anchor=($anchorX,$anchorY), latest=($latestX,$latestY)"
        )
    }

    private fun matchedCandidatesFor(type: EdgeGestureStepType): List<EdgeGestureSequenceCandidate> {
        return currentStepCandidates.filter {
            it.step.type == type
        }.map { it.candidate }
    }

    private fun hoverDurationMs(): Long = cachedHoverDurationMs

    private fun hoverRadiusPx(): Float = cachedHoverRadiusPx

    private fun childSwipeMinDistancePx(): Float = cachedChildSwipeMinDistancePx

    private fun childSwipeMaxAngleDeviationDeg(): Float =
        cachedChildSwipeMaxAngleDeviationDeg

    private fun minDirectionDot(): Float = cachedMinDirectionDot

    private fun isChildSwipeTriggerOnRelease(): Boolean = cachedChildSwipeTriggerOnRelease

    private fun formatCandidates(candidates: List<EdgeGestureSequenceCandidate>): String {
        return candidates.joinToString(prefix = "[", postfix = "]") {
            "{area=${it.area}, childSteps=${it.rule.childStepsList.map { step -> step.type }}}"
        }
    }

    private fun directionalMotionMetrics(candidate: CurrentStepCandidate, x: Float, y: Float): DirectionalMotionMetrics? {
        // 子手势候选可能同时存在“跟随当前屏幕方向”和“保持竖屏绝对语义”两类规则，
        // 所以这里按候选规则逐个选择位移坐标系，避免横屏时把两类规则混到同一套 delta 上。
        return if (candidate.candidate.rule.usesAbsoluteDirection()) {
            directionalMotionMetricsAbsolute(x, y)
        } else {
            directionalMotionMetricsRelative(x, y)
        }
    }

    private fun directionalMotionMetricsRelative(x: Float, y: Float): DirectionalMotionMetrics? {
        val deltaX = x - anchorX
        val deltaY = y - anchorY
        val distance = hypot(deltaX.toDouble(), deltaY.toDouble()).toFloat()
        if (distance <= 0f) return null
        return DirectionalMotionMetrics(
            deltaX = deltaX,
            deltaY = deltaY,
            distance = distance,
            normalizedX = deltaX / distance,
            normalizedY = deltaY / distance,
        )
    }

    private fun directionalMotionMetricsAbsolute(x: Float, y: Float): DirectionalMotionMetrics? {
        val absolutePoint = toAbsolutePortraitPoint(x, y)
        val deltaX = absolutePoint.x - anchorAbsoluteX
        val deltaY = absolutePoint.y - anchorAbsoluteY
        val distance = hypot(deltaX.toDouble(), deltaY.toDouble()).toFloat()
        if (distance <= 0f) return null
        return DirectionalMotionMetrics(
            deltaX = deltaX,
            deltaY = deltaY,
            distance = distance,
            normalizedX = deltaX / distance,
            normalizedY = deltaY / distance,
        )
    }

    private fun distanceFromAnchor(x: Float, y: Float): Float {
        return hypot((x - anchorX).toDouble(), (y - anchorY).toDouble()).toFloat()
    }

    private fun hoverDistanceFromAnchor(x: Float, y: Float): Float {
        return hypot((x - hoverTrackingAnchorX()).toDouble(), (y - hoverTrackingAnchorY()).toDouble()).toFloat()
    }

    private fun projectedDistanceFor(type: EdgeGestureStepType, x: Float, y: Float): Float {
        val candidate = directionalStepCandidates.firstOrNull { it.step.type == type } ?: return 0f
        return directionalMotionMetrics(candidate, x, y)?.projectedDistanceFor(type) ?: 0f
    }

    private fun updateDirectionalIntentTracking() {
        val dominantCandidate = findDominantDirectionalCandidate(latestX, latestY) ?: return
        val lockDistancePx = directionalIntentLockDistancePx()

        val phase = stepPhase
        if (phase !is StepPhase.LockedDirection) {
            if (dominantCandidate.projectedDistance < lockDistancePx) return
            stepPhase = StepPhase.LockedDirection(
                type = dominantCandidate.step.type,
                peakDistancePx = dominantCandidate.projectedDistance,
            )
            debugLog(
                "$DIAG_PREFIX directional intent locked: stepIndex=$currentStepIndex, type=${dominantCandidate.step.type}, distance=${dominantCandidate.projectedDistance}, lockDistance=$lockDistancePx"
            )
            return
        }

        if (phase.type == dominantCandidate.step.type) {
            stepPhase = phase.copy(
                peakDistancePx = max(phase.peakDistancePx, dominantCandidate.projectedDistance)
            )
        }
    }

    private fun findDominantDirectionalCandidate(x: Float, y: Float): DirectionalStepCandidate? {
        return directionalStepCandidates.mapNotNull { candidate ->
            val metrics = directionalMotionMetrics(candidate, x, y) ?: return@mapNotNull null
            DirectionalStepCandidate(
                step = candidate.step,
                dot = 1f,
                projectedDistance = metrics.projectedDistanceFor(candidate.step.type),
            )
        }.maxByOrNull { it.projectedDistance }
    }

    private fun findLockedDirectionalMatch(x: Float, y: Float): DirectionalStepCandidate? {
        val phase = stepPhase as? StepPhase.LockedDirection ?: return null
        if (phase.peakDistancePx < childSwipeMinDistancePx()) return null

        val step = directionalStepCandidates.firstNotNullOfOrNull {
            it.step.takeIf { step -> step.type == phase.type }
        } ?: return null

        val currentProjectedDistance = projectedDistanceFor(phase.type, x, y)
        if (phase.peakDistancePx - currentProjectedDistance > directionalRollbackTolerancePx()) return null

        return DirectionalStepCandidate(
            step = step,
            dot = 1f,
            projectedDistance = phase.peakDistancePx,
        )
    }

    private fun hasLockedDirectionalIntent(): Boolean {
        return stepPhase is StepPhase.LockedDirection
    }

    private fun directionalIntentLockDistancePx(): Float {
        return cachedDirectionalIntentLockDistancePx
    }

    private fun directionalRollbackTolerancePx(): Float {
        return cachedDirectionalRollbackTolerancePx
    }

    private fun resetHoverTrackingWindowIfNeeded(): Boolean {
        if (!hasHoverCandidates()) return false
        val phase = stepPhase as? StepPhase.HoverTracking ?: return false

        val distance = hoverDistanceFromAnchor(latestX, latestY)
        if (distance <= hoverRadiusPx()) return false

        clearHoverAutoCompletion()
        stepPhase = phase.copy(
            anchorX = latestX,
            anchorY = latestY,
            startedAtElapsed = elapsedRealtime(),
        )
        debugLog(
            "$DIAG_PREFIX hover tracking reset: stepIndex=$currentStepIndex, hoverAnchor=(${hoverTrackingAnchorX()},${hoverTrackingAnchorY()}), radiusPx=${hoverRadiusPx()}"
        )
        emitCurrentStepHint()
        scheduleHoverAutoCompletionIfNeeded()
        return true
    }

    private fun hasHoverCandidates(): Boolean = hoverCandidates().isNotEmpty()

    private fun hoverCandidates(): List<CurrentStepCandidate> {
        return hoverStepCandidates
    }

    private fun refreshCurrentStepSnapshot() {
        currentStepCandidates = candidates.mapNotNull { candidate ->
            val step = candidate.rule.getChildStepsOrNull(currentStepIndex) ?: return@mapNotNull null
            CurrentStepCandidate(candidate = candidate, step = step)
        }
        hoverStepCandidates = currentStepCandidates.filter { it.step.type == EdgeGestureStepType.Hover }
        directionalStepCandidates = currentStepCandidates.filter { directionFor(it.step.type) != null }
        currentStepTypes = currentStepCandidates.map { it.step.type }.toSet()
        cachedCurrentStepHintEnabled = currentStepCandidates.any {
            it.candidate.rule.hasEnableGestureHint() && it.candidate.rule.enableGestureHint
        }
        cachedCurrentStepHapticEnabled = currentStepCandidates.any {
            it.candidate.rule.hasEnableGestureHaptic() && it.candidate.rule.enableGestureHaptic
        }
    }

    private fun clearCurrentStepSnapshot() {
        currentStepCandidates = emptyList()
        hoverStepCandidates = emptyList()
        directionalStepCandidates = emptyList()
        currentStepTypes = emptySet()
        cachedCurrentStepHintEnabled = false
        cachedCurrentStepHapticEnabled = false
    }

    private fun refreshThresholdCache() {
        cachedHoverDurationMs = service.getEdgeGestureSequenceHoverDurationMs().toLong()
        cachedHoverRadiusPx = dpToPx(service.getEdgeGestureSequenceHoverRadiusDp()).toFloat()
        cachedChildSwipeMinDistancePx = dpToPx(service.getEdgeGestureSequenceChildSwipeMinDistanceDp()).toFloat()
        cachedChildSwipeMaxAngleDeviationDeg = service.getEdgeGestureSequenceChildSwipeMaxAngleDeviationDeg().toFloat()
        cachedChildSwipeTriggerOnRelease = service.isEdgeGestureSequenceChildSwipeTriggerOnRelease()
        cachedMinDirectionDot = cos(Math.toRadians(childSwipeMaxAngleDeviationDeg().toDouble())).toFloat()
        cachedDirectionalIntentLockDistancePx = (cachedChildSwipeMinDistancePx * 0.4f)
            .coerceAtLeast(dpToPx(12).toFloat())
            .coerceAtMost(cachedHoverRadiusPx * 0.5f)
        cachedDirectionalRollbackTolerancePx = cachedHoverRadiusPx.coerceAtLeast(dpToPx(16).toFloat())
    }

    private fun clearThresholdCache() {
        cachedHoverDurationMs = 0L
        cachedHoverRadiusPx = 0f
        cachedChildSwipeMinDistancePx = 0f
        cachedChildSwipeMaxAngleDeviationDeg = 0f
        cachedMinDirectionDot = 0f
        cachedChildSwipeTriggerOnRelease = false
        cachedDirectionalIntentLockDistancePx = 0f
        cachedDirectionalRollbackTolerancePx = 0f
    }

    private fun initializeStepPhase() {
        stepPhase = if (hoverStepCandidates.isNotEmpty()) {
            StepPhase.HoverTracking(
                anchorX = hoverAnchorX,
                anchorY = hoverAnchorY,
                startedAtElapsed = startedAtElapsed,
            )
        } else {
            StepPhase.Undecided
        }
    }

    private fun hoverTrackingAnchorX(): Float {
        return (stepPhase as? StepPhase.HoverTracking)?.anchorX ?: hoverAnchorX
    }

    private fun hoverTrackingAnchorY(): Float {
        return (stepPhase as? StepPhase.HoverTracking)?.anchorY ?: hoverAnchorY
    }

    private fun hoverTrackingStartedAtElapsed(): Long {
        return (stepPhase as? StepPhase.HoverTracking)?.startedAtElapsed ?: startedAtElapsed
    }

    private fun updateLatestAbsoluteCoordinates(x: Float, y: Float) {
        toAbsolutePortraitPoint(x, y).also {
            latestAbsoluteX = it.x
            latestAbsoluteY = it.y
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
