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
import tornaco.apps.shortx.services.gesture.bpok.gesture.ContinuousActionHandler
import tornaco.apps.shortx.services.gesture.bpok.gesture.ContinuousGestureActionPayload
import tornaco.apps.shortx.services.gesture.bpok.gesture.ContinuousGestureBinding
import tornaco.apps.shortx.services.gesture.bpok.gesture.DetectedArea
import tornaco.apps.shortx.services.gesture.bpok.gesture.EdgeGestureSequenceCandidate
import tornaco.apps.shortx.services.gesture.bpok.gesture.EdgeGestureSequenceDetector
import tornaco.apps.shortx.services.gesture.bpok.gesture.EdgeGestureSequenceHintSink
import tornaco.apps.shortx.services.gesture.bpok.gesture.GestureBridge
import tornaco.apps.shortx.services.gesture.bpok.gesture.GestureSequencePointerEvent
import tornaco.apps.shortx.services.gesture.bpok.gesture.GestureSequenceScheduler

class EdgeGestureSequenceDetectorTest {
    companion object {
        private const val ACTION_UP = 1
        private const val ACTION_MOVE = 2

        private fun identityDpToPx(dp: Int): Int = dp
    }

    @Test
    fun should_fire_after_all_child_steps_matched() {
        val scheduler = FakeScheduler()
        val service = FakeGestureBridge()
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
    fun should_fire_hover_step_immediately_after_duration_when_release_trigger_disabled() {
        val scheduler = FakeScheduler()
        val service = FakeGestureBridge()
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
        clock.advanceBy(350)
        scheduler.runPending()

        service.firedEvents shouldHaveSize 1
        service.firedEvents.single().childSteps.map { it.type } shouldBe listOf(EdgeGestureStepType.Hover)
        detector.isTracking() shouldBe false
    }

    @Test
    fun should_not_fire_hover_step_before_pointer_release_when_release_trigger_enabled_even_after_delay() {
        val scheduler = FakeScheduler()
        val service = FakeGestureBridge(childSwipeTriggerOnRelease = true)
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
        clock.advanceBy(350)

        service.firedEvents shouldHaveSize 0
        detector.isTracking() shouldBe true
    }

    @Test
    fun should_fire_hover_step_on_pointer_release_after_duration_when_release_trigger_enabled() {
        val scheduler = FakeScheduler()
        val service = FakeGestureBridge(childSwipeTriggerOnRelease = true)
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
        clock.advanceBy(350)
        detector.onTouchEvent(upEvent(pointerId = 1, x = 466f, y = 306f)) shouldBe true

        service.firedEvents shouldHaveSize 1
        service.firedEvents.single().childSteps.map { it.type } shouldBe listOf(EdgeGestureStepType.Hover)
    }

    @Test
    fun should_reset_hover_tracking_when_pointer_moves_out_of_radius_and_fire_after_stabilizing() {
        val scheduler = FakeScheduler()
        val service = FakeGestureBridge()
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
    detector.isTracking() shouldBe true
        service.firedEvents shouldHaveSize 0

        clock.advanceBy(350)
        scheduler.runPending()

    service.firedEvents shouldHaveSize 1
    service.firedEvents.single().childSteps.map { it.type } shouldBe listOf(EdgeGestureStepType.Hover)
    detector.isTracking() shouldBe false
    }

    @Test
    fun should_fire_hover_when_hover_and_directional_candidates_exist_together() {
        val scheduler = FakeScheduler()
        val service = FakeGestureBridge()
        val clock = FakeClock()
        val detector = EdgeGestureSequenceDetector(scheduler, service, clock::now, ::identityDpToPx)
        val area = DetectedArea.PredefinedEdge(Edge.RightCenter)
        val candidates = listOf(
            EdgeGestureSequenceCandidate(
                area = area,
                rule = EdgeGesture.newBuilder()
                    .setEdge(Edge.RightCenter)
                    .setGesture(Gesture.SwipeFromBottom)
                    .addChildSteps(step(EdgeGestureStepType.Hover))
                    .build()
            ),
            EdgeGestureSequenceCandidate(
                area = area,
                rule = EdgeGesture.newBuilder()
                    .setEdge(Edge.RightCenter)
                    .setGesture(Gesture.SwipeFromBottom)
                    .addChildSteps(step(EdgeGestureStepType.SwipeLeft))
                    .build()
            )
        )

        detector.startTracking(
            pointerId = 1,
            primaryGesture = Gesture.SwipeFromBottom,
            downX = 500f,
            downY = 1000f,
            anchorX = 500f,
            anchorY = 1000f,
            candidates = candidates,
        ) shouldBe true

        detector.onTouchEvent(moveEvent(pointerId = 1, x = 506f, y = 1004f)) shouldBe true
        clock.advanceBy(350)
        scheduler.runPending()

        service.firedEvents shouldHaveSize 1
        service.firedEvents.single().childSteps.map { it.type } shouldBe listOf(EdgeGestureStepType.Hover)
        detector.isTracking() shouldBe false
    }

    @Test
    fun should_prefer_swipe_down_when_directional_intent_appears_before_hover_timeout() {
        val scheduler = FakeScheduler()
        val service = FakeGestureBridge(hoverRadiusDp = 80)
        val clock = FakeClock()
        val detector = EdgeGestureSequenceDetector(scheduler, service, clock::now, ::identityDpToPx)
        val area = DetectedArea.PredefinedEdge(Edge.RightCenter)
        val candidates = listOf(
            EdgeGestureSequenceCandidate(
                area = area,
                rule = EdgeGesture.newBuilder()
                    .setEdge(Edge.RightCenter)
                    .setGesture(Gesture.SwipeFromBottom)
                    .addChildSteps(step(EdgeGestureStepType.Hover))
                    .build()
            ),
            EdgeGestureSequenceCandidate(
                area = area,
                rule = EdgeGesture.newBuilder()
                    .setEdge(Edge.RightCenter)
                    .setGesture(Gesture.SwipeFromBottom)
                    .addChildSteps(step(EdgeGestureStepType.SwipeDown))
                    .build()
            )
        )

        detector.startTracking(
            pointerId = 1,
            primaryGesture = Gesture.SwipeFromBottom,
            downX = 500f,
            downY = 1000f,
            anchorX = 500f,
            anchorY = 1000f,
            candidates = candidates,
        ) shouldBe true

        detector.onTouchEvent(moveEvent(pointerId = 1, x = 502f, y = 1032f)) shouldBe true
        service.firedEvents shouldHaveSize 0
        detector.isTracking() shouldBe true

    clock.advanceBy(120)
        detector.onTouchEvent(moveEvent(pointerId = 1, x = 502f, y = 1080f)) shouldBe true

        service.firedEvents shouldHaveSize 1
        service.firedEvents.single().childSteps.map { it.type } shouldBe listOf(EdgeGestureStepType.SwipeDown)
        detector.isTracking() shouldBe false
    }

    @Test
    fun should_keep_directional_distance_accumulating_when_hover_window_resets() {
        val scheduler = FakeScheduler()
        val service = FakeGestureBridge(hoverRadiusDp = 40, childSwipeMinDistanceDp = 120)
        val clock = FakeClock()
        val detector = EdgeGestureSequenceDetector(scheduler, service, clock::now, ::identityDpToPx)
        val area = DetectedArea.PredefinedEdge(Edge.RightCenter)
        val candidates = listOf(
            EdgeGestureSequenceCandidate(
                area = area,
                rule = EdgeGesture.newBuilder()
                    .setEdge(Edge.RightCenter)
                    .setGesture(Gesture.SwipeFromBottom)
                    .addChildSteps(step(EdgeGestureStepType.Hover))
                    .build()
            ),
            EdgeGestureSequenceCandidate(
                area = area,
                rule = EdgeGesture.newBuilder()
                    .setEdge(Edge.RightCenter)
                    .setGesture(Gesture.SwipeFromBottom)
                    .addChildSteps(step(EdgeGestureStepType.SwipeLeft))
                    .build()
            )
        )

        detector.startTracking(
            pointerId = 1,
            primaryGesture = Gesture.SwipeFromBottom,
            downX = 500f,
            downY = 1000f,
            anchorX = 500f,
            anchorY = 1000f,
            candidates = candidates,
        ) shouldBe true

        detector.onTouchEvent(moveEvent(pointerId = 1, x = 455f, y = 1008f)) shouldBe true
        service.firedEvents shouldHaveSize 0
        detector.isTracking() shouldBe true

        detector.onTouchEvent(moveEvent(pointerId = 1, x = 378f, y = 1014f)) shouldBe true

        service.firedEvents shouldHaveSize 1
        service.firedEvents.single().childSteps.map { it.type } shouldBe listOf(EdgeGestureStepType.SwipeLeft)
        detector.isTracking() shouldBe false
    }

    @Test
    fun should_fire_swipe_down_with_hover_candidate_after_minor_jitter() {
        val scheduler = FakeScheduler()
        val service = FakeGestureBridge(hoverRadiusDp = 80)
        val clock = FakeClock()
        val detector = EdgeGestureSequenceDetector(scheduler, service, clock::now, ::identityDpToPx)
        val area = DetectedArea.PredefinedEdge(Edge.RightCenter)
        val candidates = listOf(
            EdgeGestureSequenceCandidate(
                area = area,
                rule = EdgeGesture.newBuilder()
                    .setEdge(Edge.RightCenter)
                    .setGesture(Gesture.SwipeFromBottom)
                    .addChildSteps(step(EdgeGestureStepType.Hover))
                    .build()
            ),
            EdgeGestureSequenceCandidate(
                area = area,
                rule = EdgeGesture.newBuilder()
                    .setEdge(Edge.RightCenter)
                    .setGesture(Gesture.SwipeFromBottom)
                    .addChildSteps(step(EdgeGestureStepType.SwipeDown))
                    .build()
            )
        )

        detector.startTracking(
            pointerId = 1,
            primaryGesture = Gesture.SwipeFromBottom,
            downX = 500f,
            downY = 1000f,
            anchorX = 500f,
            anchorY = 1000f,
            candidates = candidates,
        ) shouldBe true

        detector.onTouchEvent(moveEvent(pointerId = 1, x = 506f, y = 1028f)) shouldBe true
        detector.onTouchEvent(moveEvent(pointerId = 1, x = 510f, y = 1078f)) shouldBe true

        service.firedEvents shouldHaveSize 1
        service.firedEvents.single().childSteps.map { it.type } shouldBe listOf(EdgeGestureStepType.SwipeDown)
        detector.isTracking() shouldBe false
    }

    @Test
    fun should_fire_hover_after_swipe_up_when_jitter_stays_within_hover_radius() {
        val scheduler = FakeScheduler()
        val service = FakeGestureBridge()
        val clock = FakeClock()
        val detector = EdgeGestureSequenceDetector(scheduler, service, clock::now, ::identityDpToPx)
        val area = DetectedArea.PredefinedEdge(Edge.BottomCenter)
        val candidates = listOf(
            EdgeGestureSequenceCandidate(
                area = area,
                rule = EdgeGesture.newBuilder()
                    .setEdge(Edge.BottomCenter)
                    .setGesture(Gesture.SwipeFromBottom)
                    .addChildSteps(step(EdgeGestureStepType.SwipeUp))
                    .addChildSteps(step(EdgeGestureStepType.Hover))
                    .build()
            )
        )

        detector.startTracking(
            pointerId = 1,
            primaryGesture = Gesture.SwipeFromBottom,
            downX = 300f,
            downY = 900f,
            anchorX = 300f,
            anchorY = 900f,
            candidates = candidates,
        ) shouldBe true

        detector.onTouchEvent(moveEvent(pointerId = 1, x = 300f, y = 780f)) shouldBe true
        service.firedEvents shouldHaveSize 0
        detector.isTracking() shouldBe true

        detector.onTouchEvent(moveEvent(pointerId = 1, x = 304f, y = 764f)) shouldBe true
        service.firedEvents shouldHaveSize 0
        detector.isTracking() shouldBe true

        clock.advanceBy(350)
        scheduler.runPending()

        service.firedEvents shouldHaveSize 1
        service.firedEvents.single().childSteps.map { it.type } shouldBe listOf(
            EdgeGestureStepType.SwipeUp,
            EdgeGestureStepType.Hover,
        )
        detector.isTracking() shouldBe false
    }

    @Test
    fun should_fire_hover_after_fast_swipe_up_without_extra_move() {
        val scheduler = FakeScheduler()
        val service = FakeGestureBridge()
        val clock = FakeClock()
        val detector = EdgeGestureSequenceDetector(scheduler, service, clock::now, ::identityDpToPx)
        val area = DetectedArea.PredefinedEdge(Edge.BottomCenter)
        val candidates = listOf(
            EdgeGestureSequenceCandidate(
                area = area,
                rule = EdgeGesture.newBuilder()
                    .setEdge(Edge.BottomCenter)
                    .setGesture(Gesture.SwipeFromBottom)
                    .addChildSteps(step(EdgeGestureStepType.SwipeUp))
                    .addChildSteps(step(EdgeGestureStepType.Hover))
                    .build()
            )
        )

        detector.startTracking(
            pointerId = 1,
            primaryGesture = Gesture.SwipeFromBottom,
            downX = 300f,
            downY = 900f,
            anchorX = 300f,
            anchorY = 900f,
            candidates = candidates,
        ) shouldBe true

        detector.onTouchEvent(moveEvent(pointerId = 1, x = 300f, y = 780f)) shouldBe true
        service.firedEvents shouldHaveSize 0
        detector.isTracking() shouldBe true

        clock.advanceBy(350)
        scheduler.runPending()

        service.firedEvents shouldHaveSize 1
        service.firedEvents.single().childSteps.map { it.type } shouldBe listOf(
            EdgeGestureStepType.SwipeUp,
            EdgeGestureStepType.Hover,
        )
        detector.isTracking() shouldBe false
    }

    @Test
    fun should_keep_waiting_for_directional_child_step_without_timeout() {
        val scheduler = FakeScheduler()
        val service = FakeGestureBridge()
        val clock = FakeClock()
        val detector = EdgeGestureSequenceDetector(scheduler, service, clock::now, ::identityDpToPx)
        val area = DetectedArea.PredefinedEdge(Edge.LeftCenter)
        val candidates = listOf(
            EdgeGestureSequenceCandidate(
                area = area,
                rule = EdgeGesture.newBuilder()
                    .setEdge(Edge.LeftCenter)
                    .setGesture(Gesture.SwipeFromLeft)
                    .addChildSteps(step(EdgeGestureStepType.SwipeUp))
                    .build()
            )
        )

        detector.startTracking(
            pointerId = 1,
            primaryGesture = Gesture.SwipeFromLeft,
            downX = 20f,
            downY = 400f,
            anchorX = 80f,
            anchorY = 400f,
            candidates = candidates,
        ) shouldBe true

        clock.advanceBy(5_000)

        detector.isTracking() shouldBe true
        detector.onTouchEvent(moveEvent(pointerId = 1, x = 80f, y = 280f)) shouldBe true

        service.firedEvents shouldHaveSize 1
        service.firedEvents.single().childSteps.map { it.type } shouldBe listOf(EdgeGestureStepType.SwipeUp)
    }

    @Test
    fun should_fire_directional_child_step_on_pointer_release_when_setting_enabled() {
        val scheduler = FakeScheduler()
        val service = FakeGestureBridge(childSwipeTriggerOnRelease = true)
        val clock = FakeClock()
        val detector = EdgeGestureSequenceDetector(scheduler, service, clock::now, ::identityDpToPx)
        val area = DetectedArea.PredefinedEdge(Edge.LeftCenter)
        val candidates = listOf(
            EdgeGestureSequenceCandidate(
                area = area,
                rule = EdgeGesture.newBuilder()
                    .setEdge(Edge.LeftCenter)
                    .setGesture(Gesture.SwipeFromLeft)
                    .addChildSteps(step(EdgeGestureStepType.SwipeUp))
                    .build()
            )
        )

        detector.startTracking(
            pointerId = 1,
            primaryGesture = Gesture.SwipeFromLeft,
            downX = 20f,
            downY = 400f,
            anchorX = 80f,
            anchorY = 400f,
            candidates = candidates,
        ) shouldBe true

        detector.onTouchEvent(moveEvent(pointerId = 1, x = 80f, y = 280f)) shouldBe true
        service.firedEvents shouldHaveSize 0
        detector.isTracking() shouldBe true

        detector.onTouchEvent(upEvent(pointerId = 1, x = 80f, y = 280f)) shouldBe true

        service.firedEvents shouldHaveSize 1
        service.firedEvents.single().childSteps.map { it.type } shouldBe listOf(EdgeGestureStepType.SwipeUp)
    }

    @Test
    fun should_keep_hint_visible_for_best_direction_when_child_steps_diverge() {
        val scheduler = FakeScheduler()
        val service = FakeGestureBridge()
        val clock = FakeClock()
        val hintSink = FakeHintSink()
        val detector = EdgeGestureSequenceDetector(
            scheduler = scheduler,
            service = service,
            elapsedRealtime = clock::now,
            dpToPx = ::identityDpToPx,
            hintSink = hintSink,
            debugLog = {},
        )
        val candidates = listOf(
            EdgeGestureSequenceCandidate(
                area = DetectedArea.PredefinedEdge(Edge.RightCenter),
                rule = EdgeGesture.newBuilder()
                    .setEdge(Edge.RightCenter)
                    .setGesture(Gesture.SwipeFromBottom)
                    .addChildSteps(step(EdgeGestureStepType.SwipeLeft))
                    .build()
            ),
            EdgeGestureSequenceCandidate(
                area = DetectedArea.PredefinedEdge(Edge.RightCenter),
                rule = EdgeGesture.newBuilder()
                    .setEdge(Edge.RightCenter)
                    .setGesture(Gesture.SwipeFromBottom)
                    .addChildSteps(step(EdgeGestureStepType.SwipeDown))
                    .build()
            )
        )

        detector.startTracking(
            pointerId = 1,
            primaryGesture = Gesture.SwipeFromBottom,
            downX = 500f,
            downY = 1000f,
            anchorX = 500f,
            anchorY = 1000f,
            candidates = candidates,
        ) shouldBe true

        hintSink.stepHints shouldHaveSize 0
        detector.onTouchEvent(moveEvent(pointerId = 1, x = 460f, y = 990f)) shouldBe true

        hintSink.stepHints.size shouldBe 1
        hintSink.stepHints.last().type shouldBe EdgeGestureStepType.SwipeLeft
    }

    @Test
    fun should_choose_best_matching_direction_when_multiple_candidates_pass_threshold() {
        val scheduler = FakeScheduler()
        val service = FakeGestureBridge(childSwipeMaxAngleDeviationDeg = 60)
        val clock = FakeClock()
        val detector = EdgeGestureSequenceDetector(scheduler, service, clock::now, ::identityDpToPx)
        val candidates = listOf(
            EdgeGestureSequenceCandidate(
                area = DetectedArea.PredefinedEdge(Edge.RightCenter),
                rule = EdgeGesture.newBuilder()
                    .setEdge(Edge.RightCenter)
                    .setGesture(Gesture.SwipeFromBottom)
                    .addChildSteps(step(EdgeGestureStepType.SwipeLeft))
                    .build()
            ),
            EdgeGestureSequenceCandidate(
                area = DetectedArea.PredefinedEdge(Edge.RightCenter),
                rule = EdgeGesture.newBuilder()
                    .setEdge(Edge.RightCenter)
                    .setGesture(Gesture.SwipeFromBottom)
                    .addChildSteps(step(EdgeGestureStepType.SwipeDown))
                    .build()
            )
        )

        detector.startTracking(
            pointerId = 1,
            primaryGesture = Gesture.SwipeFromBottom,
            downX = 500f,
            downY = 1000f,
            anchorX = 500f,
            anchorY = 1000f,
            candidates = candidates,
        ) shouldBe true

        detector.onTouchEvent(moveEvent(pointerId = 1, x = 400f, y = 1058f)) shouldBe true

        service.firedEvents shouldHaveSize 1
        service.firedEvents.single().childSteps.map { it.type } shouldBe listOf(EdgeGestureStepType.SwipeLeft)
    }

    private fun moveEvent(pointerId: Int, x: Float, y: Float): GestureSequencePointerEvent =
        FakePointerEvent(
            actionMasked = ACTION_MOVE,
            actionIndex = 0,
            pointerId = pointerId,
            x = x,
            y = y,
        )

    private fun upEvent(pointerId: Int, x: Float, y: Float): GestureSequencePointerEvent =
        FakePointerEvent(
            actionMasked = ACTION_UP,
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

        fun hasPendingDelayed(): Boolean {
            return lastRunnable != null
        }

        fun runPending() {
            val runnable = lastRunnable ?: return
            lastRunnable = null
            runnable.run()
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

    private class FakeHintSink : EdgeGestureSequenceHintSink {
        val stepHints = mutableListOf<EdgeGestureStep>()

        override fun onStepHint(
            anchorX: Float,
            anchorY: Float,
            step: EdgeGestureStep,
            progress: Float,
            isReady: Boolean,
            hintEnabled: Boolean,
            hapticEnabled: Boolean,
        ) {
            stepHints += step
        }

        override fun onSequenceSuccess() = Unit

        override fun onSequenceCancelled() = Unit
    }

    private class FakeGestureBridge(
        private val childSwipeMaxAngleDeviationDeg: Int = 35,
        private val childSwipeTriggerOnRelease: Boolean = false,
        private val hoverRadiusDp: Int = 24,
        private val childSwipeMinDistanceDp: Int = 72,
    ) : GestureBridge {
        data class FiredEvent(
            val area: DetectedArea,
            val gesture: Gesture,
            val childSteps: List<EdgeGestureStep>,
            val downX: Float,
            val downY: Float,
        )

        val firedEvents = mutableListOf<FiredEvent>()

        override fun getAreaGestureProfile(areas: List<DetectedArea>): AreaGestureProfile {
            return AreaGestureProfile(
                hasAnyTrigger = false,
                hasTapTrigger = false,
                hasLongPressTrigger = false,
                hasSwipeTrigger = false,
            )
        }

        override fun handleEdgeGestureEvent(
            areas: List<DetectedArea>,
            gestureEvent: Gesture,
            downX: Float,
            downY: Float,
            coordinateMode: PredefinedEdgeCoordinateMode?,
        ): Boolean = false

        override fun findMatchingEdgeGestureSequences(
            areas: List<DetectedArea>,
            gestureEvent: Gesture,
            coordinateMode: PredefinedEdgeCoordinateMode?,
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
        override fun getEdgeGestureSequenceHoverDurationMs(): Int = 300
        override fun getEdgeGestureSequenceHoverRadiusDp(): Int = hoverRadiusDp
        override fun getEdgeGestureSequenceChildSwipeMinDistanceDp(): Int = childSwipeMinDistanceDp
        override fun getEdgeGestureSequenceChildSwipeMaxAngleDeviationDeg(): Int = childSwipeMaxAngleDeviationDeg
        override fun isEdgeGestureSequenceChildSwipeTriggerOnRelease(): Boolean = childSwipeTriggerOnRelease
        override fun obsGestureSettingsChanged(obs: () -> Unit) = Unit
        override fun obsConfigChanged(obs: () -> Unit) = Unit
        override fun obsUserDataChanged(obs: () -> Unit) = Unit
        override fun findContinuousGestureBinding(
            areas: List<DetectedArea>,
            gestureEvent: Gesture,
            coordinateMode: PredefinedEdgeCoordinateMode?,
        ): ContinuousGestureBinding? = null
        override fun createContinuousActionHandler(payload: ContinuousGestureActionPayload): ContinuousActionHandler? = null
        override val enabledEdgeGestures: Set<EdgeGesture> = emptySet()
        override val enabledCornerSwipeToCenterFacts: Set<CornerSwipeToCenter> = emptySet()
    }
}
