package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.cond.Cond
import tornaco.apps.shortx.services.wm.NodeMatcher
import tornaco.apps.shortx.services.wm.NodeMatcher.extractMatchers

object HasNodeOnScreen : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.HasNodeOnScreen::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        val data =
            param.condition.unpack_(tornaco.apps.shortx.core.proto.condition.HasNodeOnScreen::class.java)

        runBlocking {
            param.shortXContext.wm.withUiAutomation("condition.HasNodeOnScreen.") {
            }
        }

        val matchers = data.matchersList.extractMatchers()
        logger.d("HasNodeOnScreen, matchers: $matchers")

        val isMatched = param.shortXContext.wm.hasFoundNodeOnScreen { node ->
            matchers.matchers.any { matcher ->
                NodeMatcher.matchNode(matcher, node).isMatched
            }
        }

        return if (data.isInvert) {
            !isMatched
        } else {
            isMatched
        }.toResult()
    }
}