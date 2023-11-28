package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty
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
        val targetViewId = d.nodeId.inflateContextMap(contextData)
            .replaceGlobalVarValues { shortXContext.rule.getAllGlobalVars() }
        logger.d("targetViewId: $targetViewId")
        runBlocking {
            shortXContext.wm.withUiAutomation("condition.HasNodeOnScreen.") {
            }
        }
        val allNodes = shortXContext.wm.getAllNodesFromScreen()
        val matchedNodes = allNodes.filter {
            targetViewId.isEmpty() || (it.viewId == targetViewId || it.shortViewId == targetViewId)
        }
        logger.p("matchedNodes: $matchedNodes")

        val text = matchedNodes.mapNotNull { it.text }.filter { it.isNotBlank() }
            .joinToString(System.lineSeparator())

        logger.d("add $text to contextData")
        params.contextData.add(
            d.customContextDataKey.forKey(ContextDataMapping.GetTextFromScreenNode.textOfTheView.name)
                .fallbackOnEmpty { ContextDataMapping.GetTextFromScreenNode.textOfTheView.name },
            text
        )
    }

}