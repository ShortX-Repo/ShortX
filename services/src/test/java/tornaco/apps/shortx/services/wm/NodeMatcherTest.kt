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
    fun should_match_nodes_id_with_result_false_when_node_is_empty() {
        val matcher = NodeMatcherViewId.newBuilder().setId("id:123").build()
        val res = NodeMatcher.matchNodes(matcher, emptyList())
        res shouldBe NodeMatcherRes(
            matchViewIdContextData = "",
            matchTextContextData = "",
            matchIsRegexContextData = false,
            sourceNodeId = 0,
            windowId = 0,
            isMatched = false
        )
    }

    @Test
    fun should_match_nodes_text_with_result_false_when_node_is_empty() {
        val matcher = NodeMatcherText.newBuilder().setText("haha").setIsRegex(true).build()
        val res = NodeMatcher.matchNodes(matcher, emptyList())
        res shouldBe NodeMatcherRes(
            matchViewIdContextData = "",
            matchTextContextData = "",
            matchIsRegexContextData = false,
            sourceNodeId = 0,
            windowId = 0,
            isMatched = false
        )
    }

    @Test
    fun should_match_nodes_id_with_result_true_when_node_has_same_id() {
        val matcher = NodeMatcherViewId.newBuilder().setId("id:123").build()
        val res = NodeMatcher.matchNodes(
            matcher, listOf(
                ViewNode(
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
        )
        res shouldBe NodeMatcherRes(
            matchViewIdContextData = "id:123",
            matchTextContextData = "",
            matchIsRegexContextData = false,
            sourceNodeId = 123,
            windowId = 2,
            isMatched = true
        )
    }

    @Test
    fun should_match_nodes_text_with_result_true_when_node_has_same_text() {
        val matcher = NodeMatcherText.newBuilder().setText("Hello").setIsRegex(true).build()
        val res = NodeMatcher.matchNodes(
            matcher, listOf(
                ViewNode(
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
        )
        res shouldBe NodeMatcherRes(
            matchViewIdContextData = "",
            matchTextContextData = "Hello",
            matchIsRegexContextData = true,
            sourceNodeId = 123,
            windowId = 2,
            isMatched = true
        )
    }
}