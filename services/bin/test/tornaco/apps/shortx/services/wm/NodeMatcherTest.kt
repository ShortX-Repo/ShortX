package tornaco.apps.shortx.services.wm

import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import tornaco.apps.shortx.core.proto.fact.NodeMatcherText
import tornaco.apps.shortx.core.proto.fact.NodeMatcherViewId
import tornaco.apps.shortx.core.ui.ViewNode
import tornaco.apps.shortx.core.util.logAdapter

class NodeMatcherTest {
    @Before
    fun installLogger() {
        logAdapter = { _: Int, tag: String, msg: String ->
            println("[$tag] $msg")
        }
    }

    @Test
    fun should_match_nodes_id_with_result_false_when_node_not_match() {
        val matcher = NodeMatcherViewId.newBuilder().setId("id:123456").build()
        val res = NodeMatcher.matchNode(
            matcher, ViewNode(
                rootPackageName = "android",
                viewId = "id:123",
                className = "ClassName",
                text = "Hello",
                sourceNodeId = 123,
                windowId = 2,
                position = mockk(),
                color = 1
            )
        )
        res shouldBe NodeMatcherRes.NotMatch
    }

    @Test
    fun should_match_nodes_id_with_result_true_when_node_has_same_id() {
        val matcher = NodeMatcherViewId.newBuilder().setId("id:123").build()
        val res = NodeMatcher.matchNode(
            matcher, ViewNode(
                rootPackageName = "android",
                viewId = "id:123",
                className = "ClassName",
                text = "Hello",
                sourceNodeId = 123,
                windowId = 2,
                position = mockk(),
                color = 1
            )
        )
        res shouldBe NodeMatcherRes.MatchById(
            matchViewIdContextData = "id:123",
            sourceNodeId = 123,
            windowId = 2,
        )
    }

    @Test
    fun should_match_nodes_text_with_result_true_when_node_has_same_text() {
        val matcher = NodeMatcherText.newBuilder().setText("Hello").setIsRegex(true).build()
        val res = NodeMatcher.matchNode(
            matcher, ViewNode(
                rootPackageName = "android",
                viewId = "id:123",
                className = "ClassName",
                text = "Hello",
                sourceNodeId = 123,
                windowId = 2,
                position = mockk(),
                color = 1
            )
        )
        res shouldBe NodeMatcherRes.MatchByText(
            matchTextContextData = "Hello",
            matchIsRegexContextData = true,
            sourceNodeId = 123,
            windowId = 2,
        )
    }
}