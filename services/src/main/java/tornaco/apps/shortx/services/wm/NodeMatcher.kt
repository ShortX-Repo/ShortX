package tornaco.apps.shortx.services.wm

import tornaco.apps.shortx.core.proto.fact.NodeMatcherText
import tornaco.apps.shortx.core.proto.fact.NodeMatcherViewId
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.ui.ViewNode
import tornaco.apps.shortx.core.util.Logger

data class NodeMatcherRes(
    val matchViewIdContextData: String,
    val matchTextContextData: String,
    val matchIsRegexContextData: Boolean,
    val sourceNodeId: Long,
    val windowId: Int,
    val isMatched: Boolean
)

object NodeMatcher {
    private val logger = Logger("NodeMatcher")

    fun matchNodes(matcherAny: Any, allNodes: List<ViewNode>): NodeMatcherRes {
        var matchViewIdContextData = ""
        var matchTextContextData = ""
        var matchIsRegexContextData = false
        var sourceNodeId = 0L
        var windowId = 0

        return when (matcherAny) {
            is NodeMatcherViewId -> {
                val isMatch =
                    allNodes.any { viewNode ->
                        (viewNode.viewId == matcherAny.id || viewNode.shortViewId == matcherAny.id).also {
                            if (it) {
                                sourceNodeId = viewNode.sourceNodeId
                                windowId = viewNode.windowId
                                matchViewIdContextData = viewNode.viewId
                            }
                        }
                    }
                if (isMatch) {
                    logger.d("Match viewId: $matchViewIdContextData $sourceNodeId")
                }
                NodeMatcherRes(
                    matchViewIdContextData = matchViewIdContextData,
                    matchTextContextData = "",
                    matchIsRegexContextData = false,
                    sourceNodeId = sourceNodeId,
                    windowId = windowId,
                    isMatched = isMatch
                )
            }

            is NodeMatcherText -> {
                val isMatch =
                    allNodes.any { viewNode ->
                        matchTextOrRegex(viewNode.text, matcherAny).also {
                            matchTextContextData = matcherAny.text
                            matchIsRegexContextData = matcherAny.isRegex
                            sourceNodeId = viewNode.sourceNodeId
                            windowId = viewNode.windowId
                        }
                    }
                if (isMatch) {
                    logger.d("Match text: $matchTextContextData $sourceNodeId")
                }
                NodeMatcherRes(
                    matchViewIdContextData = "",
                    matchTextContextData = matchTextContextData,
                    matchIsRegexContextData = matchIsRegexContextData,
                    sourceNodeId = sourceNodeId,
                    windowId = windowId,
                    isMatched = isMatch
                )
            }

            else -> NodeMatcherRes(
                matchViewIdContextData = "",
                matchTextContextData = "",
                matchIsRegexContextData = false,
                sourceNodeId = 0,
                windowId = 0,
                isMatched = false
            )
        }

    }


    fun List<com.google.protobuf.Any>.extractMatchers(): MatcherList {
        val matchers = mutableListOf<Any>()
        this.forEach {
            val realMatcher = if (it is_ NodeMatcherText::class.java) {
                it unpack_ NodeMatcherText::class.java
            } else if (it is_ NodeMatcherViewId::class.java) {
                it unpack_ NodeMatcherViewId::class.java
            } else null
            realMatcher?.let {
                matchers.add(realMatcher)
            }
        }
        return MatcherList(matchers)
    }

    data class MatcherList(val matchers: List<Any>)

    private fun matchTextOrRegex(text: CharSequence?, matcher: NodeMatcherText): Boolean {
        if (text == null) return false
        if (matcher.isRegex && matcher.text.toRegex().matches(text.toString())) return true
        return !matcher.isRegex && matcher.text == text.toString()
    }
}
