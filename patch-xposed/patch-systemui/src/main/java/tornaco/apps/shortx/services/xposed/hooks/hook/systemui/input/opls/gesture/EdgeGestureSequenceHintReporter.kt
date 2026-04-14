package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture

import tornaco.apps.shortx.core.proto.fact.EdgeGestureStep
import tornaco.apps.shortx.core.proto.fact.EdgeGestureStepType

class EdgeGestureSequenceHintReporter(
    private val gestureHintController: GestureHintController,
    private val gestureHapticController: GestureHapticController,
) : EdgeGestureSequenceHintSink {
    private var nextToken = 10_000L
    private var activeToken: Long? = null
    private var activeHapticEnabled: Boolean = false

    override fun onStepHint(
        anchorX: Float,
        anchorY: Float,
        step: EdgeGestureStep,
        progress: Float,
        isReady: Boolean,
        hapticEnabled: Boolean,
    ) {
        val token = activeToken ?: nextToken++.also { activeToken = it }
        activeHapticEnabled = activeHapticEnabled || hapticEnabled
        if (hapticEnabled) {
            gestureHapticController.onGestureCandidate(token)
            if (isReady) {
                gestureHapticController.onGestureReady(token)
            }
        }
        val (directionX, directionY) = directionFor(step.type)
        gestureHintController.onGestureProgress(
            GestureHintModel(
                token = token,
                anchorX = anchorX,
                anchorY = anchorY,
                directionX = directionX,
                directionY = directionY,
                progress = progress.coerceIn(0f, 1f),
                state = if (isReady) GestureHintState.Ready else GestureHintState.Showing,
            )
        )
    }

    override fun onSequenceSuccess() {
        activeToken?.let(gestureHintController::onGestureSuccess)
        if (activeHapticEnabled) {
            activeToken?.let(gestureHapticController::onGestureSuccess)
        }
        activeToken = null
        activeHapticEnabled = false
    }

    override fun onSequenceCancelled() {
        gestureHintController.onGestureCancel(activeToken)
        gestureHapticController.onGestureCancel(activeToken)
        activeToken = null
        activeHapticEnabled = false
    }

    private fun directionFor(type: EdgeGestureStepType): Pair<Float, Float> {
        return when (type) {
            EdgeGestureStepType.SwipeLeft -> -1f to 0f
            EdgeGestureStepType.SwipeRight -> 1f to 0f
            EdgeGestureStepType.SwipeUp -> 0f to -1f
            EdgeGestureStepType.SwipeDown -> 0f to 1f
            EdgeGestureStepType.Hover -> 0f to 0f
            else -> 0f to 0f
        }
    }
}