package tornaco.apps.shortx.services.rule

import io.kotest.matchers.shouldBe
import org.junit.Before
import org.junit.Test
import tornaco.apps.shortx.core.proto.fact.ActivityStarted
import tornaco.apps.shortx.core.proto.fact.AnyFact
import tornaco.apps.shortx.core.proto.fact.AppProcessStarted
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.EdgeGesture
import tornaco.apps.shortx.core.proto.fact.EdgeGestureStep
import tornaco.apps.shortx.core.proto.fact.EdgeGestureStepType
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.util.logAdapter
import tornaco.apps.shortx.services.rule.fact.FactMatcher
import tornaco.apps.shortx.services.rule.fact.FactMatcherImpl


class FactMatcherImplTest {
    @Before
    fun installLogger() {
        logAdapter = { _: Int, tag: String, msg: String ->
            println("[$tag] $msg")
        }
    }

    @Test
    fun should_match_when_has_AnyFact() {
        val impl = FactMatcherImpl {
            throw Error()
        }
        impl.match(
            actualFact = ActivityStarted.getDefaultInstance().pack_(),
            ruleFacts = listOf(
                AppProcessStarted.getDefaultInstance().pack_(),
                AnyFact.newBuilder().setTag("Test").build().pack_()
            )
        ) shouldBe FactMatcher.MatcherResult.Match("Test")
    }

    @Test
    fun should_not_match_when_type_different() {
        val impl = FactMatcherImpl {
            throw Error()
        }
        impl.match(
            ActivityStarted.getDefaultInstance().pack_(), listOf(
                AppProcessStarted.getDefaultInstance().pack_(),
            )
        ) shouldBe FactMatcher.MatcherResult.NotMatch
    }

    @Test
    fun should_match_legacy_edge_gesture_without_child_steps() {
        val impl = FactMatcherImpl {
            throw Error()
        }
        val actual = EdgeGesture.newBuilder()
            .setEdge(Edge.BottomCenter)
            .setGesture(Gesture.SwipeFromBottom)
            .build()
        val rule = EdgeGesture.newBuilder()
            .setTag("EdgeLegacy")
            .setEdge(Edge.BottomCenter)
            .setGesture(Gesture.SwipeFromBottom)
            .build()

        impl.match(actual, listOf(rule.pack_())) shouldBe FactMatcher.MatcherResult.Match("EdgeLegacy")
    }

    @Test
    fun should_match_edge_gesture_with_same_child_steps() {
        val impl = FactMatcherImpl {
            throw Error()
        }
        val childStep = EdgeGestureStep.newBuilder()
            .setType(EdgeGestureStepType.Hover)
            .build()
        val actual = EdgeGesture.newBuilder()
            .setEdge(Edge.BottomCenter)
            .setGesture(Gesture.SwipeFromBottom)
            .addChildSteps(childStep)
            .build()
        val rule = EdgeGesture.newBuilder()
            .setTag("EdgeSequence")
            .setEdge(Edge.BottomCenter)
            .setGesture(Gesture.SwipeFromBottom)
            .addChildSteps(childStep)
            .build()

        impl.match(actual, listOf(rule.pack_())) shouldBe FactMatcher.MatcherResult.Match("EdgeSequence")
    }

    @Test
    fun should_not_match_edge_gesture_when_child_steps_different() {
        val impl = FactMatcherImpl {
            throw Error()
        }
        val actual = EdgeGesture.newBuilder()
            .setEdge(Edge.BottomCenter)
            .setGesture(Gesture.SwipeFromBottom)
            .addChildSteps(
                EdgeGestureStep.newBuilder().setType(EdgeGestureStepType.Hover).build()
            )
            .build()
        val rule = EdgeGesture.newBuilder()
            .setTag("EdgeSequence")
            .setEdge(Edge.BottomCenter)
            .setGesture(Gesture.SwipeFromBottom)
            .addChildSteps(
                EdgeGestureStep.newBuilder().setType(EdgeGestureStepType.SwipeDown).build()
            )
            .build()

        impl.match(actual, listOf(rule.pack_())) shouldBe FactMatcher.MatcherResult.NotMatch
    }
}