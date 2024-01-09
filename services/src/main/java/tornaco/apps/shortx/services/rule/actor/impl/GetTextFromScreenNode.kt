package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object GetTextFromScreenNode : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.GetTextFromScreenNode::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val d =
            action unpack_ tornaco.apps.shortx.core.proto.action.GetTextFromScreenNode::class.java
        val cv = RuleContextAndVars(params)
        val targetViewId = d.nodeId.compileContextAndVars(cv)
        logger.d("targetViewId: $targetViewId")

        val viewIdAndIndex = parseViewIdAndIndex(targetViewId)
        logger.d("viewIdAndIndex: $viewIdAndIndex")

        runBlocking {
            shortXContext.wm.withUiAutomation("condition.HasNodeOnScreen.") {
            }
        }
        val allNodes = shortXContext.wm.getAllNodesFromScreen()
        // ViewId
        val matchedNodes = if (targetViewId.isEmpty()) {
            allNodes
        } else {
            allNodes.filter { viewNode ->
                (viewNode.viewId == viewIdAndIndex.first || viewNode.shortViewId == viewIdAndIndex.first)
            }
        }
        logger.p("matchedNodes: $matchedNodes")

        // Index
        val matchedNodes2 = matchedNodes.filterIndexed { index, _ ->
            viewIdAndIndex.second == null || index == viewIdAndIndex.second
        }

        logger.p("matchedNodes2: $matchedNodes2")

        val text = matchedNodes2.mapNotNull { it.text }.filter { it.isNotBlank() }
            .joinToString(System.lineSeparator())

        logger.d("add $text to contextData")
        params.contextData.add(
            d.customContextDataKey.forKey(ContextDataMapping.GetTextFromScreenNode.textOfTheView.name)
                .fallbackOnEmpty { ContextDataMapping.GetTextFromScreenNode.textOfTheView.name },
            text
        )
    }

}

fun parseViewIdAndIndex(input: String): Pair<String, Int?> = runCatching {
    // 定义匹配模式，匹配 @ 后面的数字
    val pattern = Regex("@(\\d+)")
    val match = pattern.findAll(input).lastOrNull() ?: return input to null

    // 提取匹配到的数字和原字符串
    val number = match.groupValues[1] // 提取匹配到的数字部分
    val originalString = input.substring(0, match.range.first) // 原字符串部分

    originalString to number.toInt()
}.getOrElse {
    Logger().e(it, "parseViewIdAndIndex")
    input to null
}
