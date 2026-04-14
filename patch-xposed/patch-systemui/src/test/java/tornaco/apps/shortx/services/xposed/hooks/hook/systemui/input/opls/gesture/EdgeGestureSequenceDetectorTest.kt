package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.Test
import tornaco.apps.shortx.core.proto.common.Rect
import tornaco.apps.shortx.core.proto.fact.CornerSwipeToCenter
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.EdgeGesture
import tornaco.apps.shortx.core.proto.fact.EdgeGestureStep
import tornaco.apps.shortx.core.proto.fact.EdgeGestureStepType
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.proto.fact.ScreenCorner

class EdgeGestureSequenceDetectorTest {
    companion object {
        private const val ACTION_MOVE = 2

        private fun identityDpToPx(dp: Int): Int = dp
    }

    @Test
    fun should_fire_after_all_child_steps_matched() {
        val scheduler = FakeScheduler()
        val service = FakeDetectorService()
        val clock = FakeClock()
        val detector = EdgeGestureSequenceDetector(scheduler, service, clock::now, ::identityDpToPx)
        val area = DetectedArea.PredefinedEdge(Edge.BottomCenter)
        val candidates = listOf(
            EdgeGestureSequenceCandidate(
                area = area,
                rule = EdgeGesture.newBuilder()
                    .setEdge(Edge.BottomCenter)
                    .setGesture(Gesture.SwipeFromBottom)
                    .addChildSteps(step(EdgeGestureStepType.SwipeLeft))
                    .addChildSteps(step(EdgeGestureStepType.SwipeDown))
                    .build()
            )
        )

        detector.startTracking(
            pointerId = 1,
            primaryGesture = Gesture.SwipeFromBottom,
            downX = 100f,
            downY = 200f,
            anchorX = 100f,
            anchorY = 200f,
            candidates = candidates,
        ) shouldBe true

        detector.onTouchEvent(moveEvent(pointerId = 1, x = 10f, y = 200f)) shouldBe true
        service.firedEvents shouldHaveSize 0

        detector.onTouchEvent(moveEvent(pointerId = 1, x = 10f, y = 320f)) shouldBe true
        service.firedEvents shouldHaveSize 1
        service.firedEvents.single().gesture shouldBe Gesture.SwipeFromBottom
        service.firedEvents.single().childSteps.map { it.type } shouldBe listOf(
            EdgeGestureStepType.SwipeLeft,
            EdgeGestureStepType.SwipeDown,
        )
    }

    @Test
    fun should_fire_hover_step_after_delay_when_pointer_stays_in_radius() {
        val scheduler = FakeScheduler()
        val service = FakeDetectorService()
        val clock = FakeClock()
        val detector = EdgeGestureSequenceDetector(scheduler, service, clock::now, ::identityDpToPx)
        val area = DetectedArea.PredefinedEdge(Edge.RightCenter)
        val candidates = listOf(
            EdgeGestureSequenceCandidate(
                area = area,
                rule = EdgeGesture.newBuilder()
                    .setEdge(Edge.RightCenter)
                    .setGesture(Gesture.SwipeFromRight)
                    .addChildSteps(step(EdgeGestureStepType.Hover))
                    .build()
            )
        )

        detector.startTracking(
            pointerId = 1,
            primaryGesture = Gesture.SwipeFromRight,
            downX = 500f,
            downY = 300f,
            anchorX = 460f,
            anchorY = 300f,
            candidates = candidates,
        ) shouldBe true

        detector.onTouchEvent(moveEvent(pointerId = 1, x = 466f, y = 306f)) shouldBe true
        scheduler.runLastDelayed()

        service.firedEvents shouldHaveSize 1
        service.firedEvents.single().childSteps.map { it.type } shouldBe listOf(EdgeGestureStepType.Hover)
    }

    @Test
    fun should_cancel_hover_when_pointer_moves_out_of_radius() {
        val scheduler = FakeScheduler()
        val service = FakeDetectorService()
        val clock = FakeClock()
        val detector = EdgeGestureSequenceDetector(scheduler, service, clock::now, ::identityDpToPx)
        val area = DetectedArea.CustomRect(
            Rect.newBuilder().setLeft(0).setTop(0).setRight(100).setBottom(100).build()
        )
        val candidates = listOf(
            EdgeGestureSequenceCandidate(
                area = area,
                rule = EdgeGesture.newBuilder()
                    .setCustomRect((area as DetectedArea.CustomRect).rect)
                    .setGesture(Gesture.SwipeFromLeft)
                    .addChildSteps(step(EdgeGestureStepType.Hover))
                    .build()
            )
        )

        detector.startTracking(
            pointerId = 1,
            primaryGesture = Gesture.SwipeFromLeft,
            downX = 10f,
            downY = 20f,
            anchorX = 40f,
            anchorY = 20f,
            candidates = candidates,
        ) shouldBe true

        detector.onTouchEvent(moveEvent(pointerId = 1, x = 120f, y = 20f)) shouldBe true

        service.firedEvents shouldHaveSize 0
        scheduler.hasPendingDelayed() shouldBe false
        detector.isTracking() shouldBe false
    }

    private fun moveEvent(pointerId: Int, x: Float, y: Float): GestureSequencePointerEvent =
        FakePointerEvent(
            actionMasked = ACTION_MOVE,
            actionIndex = 0,
            pointerId = pointerId,
            x = x,
            y = y,
        )

    private fun step(type: EdgeGestureStepType): EdgeGestureStep {
        return EdgeGestureStep.newBuilder().setType(type).build()
    }

    private class FakeScheduler : GestureSequenceScheduler {
        private var lastRunnable: Runnable? = null

        override fun removeCallbacks(runnable: Runnable) {
            if (lastRunnable === runnable) {
                lastRunnable = null
            }
        }

        override fun postDelayed(runnable: Runnable, delayMillis: Long): Boolean {
            lastRunnable = runnable
            return true
        }

        fun runLastDelayed() {
            requireNotNull(lastRunnable).run()
        }

        fun hasPendingDelayed(): Boolean {
            return lastRunnable != null
        }
    }

    private class FakeClock(
        private var now: Long = 0L,
    ) {
        fun now(): Long = now
        fun advanceBy(deltaMillis: Long) {
            now += deltaMillis
        }
    }

    private data class FakePointerEvent(
        override val actionMasked: Int,
        override val actionIndex: Int,
        private val pointerId: Int,
        private val x: Float,
        private val y: Float,
    ) : GestureSequencePointerEvent {
        override fun findPointerIndex(pointerId: Int): Int {
            return if (this.pointerId == pointerId) 0 else -1
        }

        override fun getX(pointerIndex: Int): Float = x
        override fun getY(pointerIndex: Int): Float = y
        override fun getPointerId(pointerIndex: Int): Int = pointerId
    }

    private class FakeDetectorService : DetectorService {
        data class FiredEvent(
            val area: DetectedArea,
            val gesture: Gesture,
            val childSteps: List<EdgeGestureStep>,
            val downX: Float,
            val downY: Float,
        )

        val firedEvents = mutableListOf<FiredEvent>()

        override fun handleEdgeGestureEvent(
            areas: List<DetectedArea>,
            gestureEvent: Gesture,
            downX: Float,
            downY: Float,
        ): Boolean = false

        override fun findMatchingEdgeGestureSequences(
            areas: List<DetectedArea>,
            gestureEvent: Gesture,
        ): List<EdgeGestureSequenceCandidate> = emptyList()

        override fun fireEdgeGestureSequence(
            area: DetectedArea,
            gesture: Gesture,
            childSteps: List<EdgeGestureStep>,
            downX: Float,
            downY: Float,
        ): Boolean {
            firedEvents += FiredEvent(area, gesture, childSteps, downX, downY)
            return true
        }

        override fun handleCornerSwipeToCenterEvent(
            corner: ScreenCorner,
            downX: Float,
            downY: Float,
        ): Boolean = false

        override fun getSwipeDistanceThresholdDp(): Int = 24
        override fun getGestureAreaWidthDp(): Int = 28
        override fun getGestureLengthScale(): Float = 1f
        override fun getCornerGestureRegionRadiusRatio(): Float = 0.18f
        override fun getCornerSwipeMinDistanceDp(): Int = 72
        override fun obsGestureSettingsChanged(obs: () -> Unit) = Unit
        override fun obsConfigChanged(obs: () -> Unit) = Unit
        override fun obsUserDataChanged(obs: () -> Unit) = Unit
        override val enabledEdgeGestures: Set<EdgeGesture> = emptySet()
        override val enabledCornerSwipeToCenterFacts: Set<CornerSwipeToCenter> = emptySet()
    }
}