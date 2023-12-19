package tornaco.apps.shortx.services.wm

import tornaco.apps.shortx.core.proto.fact.NodeMatcherText
import tornaco.apps.shortx.core.proto.fact.NodeMatcherViewId
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.ui.ViewNode
import tornaco.apps.shortx.core.util.Logger


sealed interface NodeMatcherRes {
    val isMatched: Boolean

    data class MatchByText(
        val matchTextContextData: String,
        val matchIsRegexContextData: Boolean,
        val sourceNodeId: Long,
        val windowId: Int,
    ) : NodeMatcherRes {
        override val isMatched: Boolean = true
    }

    data class MatchById(
        val matchViewIdContextData: String,
        val sourceNodeId: Long,
        val windowId: Int,
    ) : NodeMatcherRes {
        override val isMatched: Boolean = true
    }

    data object NotMatch : NodeMatcherRes {
        override val isMatched: Boolean = false
    }
}

object NodeMatcher {
    private val logger = Logger("NodeMatcher")

    fun matchNode(matcherAny: Any, viewNode: ViewNode): NodeMatcherRes {
        val res = when (matcherAny) {
            is NodeMatcherViewId -> {
                val isMatch =
                    (viewNode.viewId == matcherAny.id || viewNode.shortViewId == matcherAny.id)
                if (isMatch) {
                    NodeMatcherRes.MatchById(
                        sourceNodeId = viewNode.sourceNodeId,
                        windowId = viewNode.windowId,
                        matchViewIdContextData = viewNode.viewId
                    )
                } else {
                    NodeMatcherRes.NotMatch
                }
            }

            is NodeMatcherText -> {
                val isMatch =
                    matchTextOrRegex(viewNode.text, matcherAny)
                if (isMatch) {
                    NodeMatcherRes.MatchByText(
                        matchTextContextData = matcherAny.text,
                        matchIsRegexContextData = matcherAny.isRegex,
                        sourceNodeId = viewNode.sourceNodeId,
                        windowId = viewNode.windowId
                    )
                } else {
                    NodeMatcherRes.NotMatch
                }
            }

            else -> NodeMatcherRes.NotMatch
        }

        logger.d("Match $viewNode result: $res")

        return res
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
