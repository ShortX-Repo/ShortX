package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.impl.getTextFromViewId
import tornaco.apps.shortx.services.rule.cond.Cond
import tornaco.apps.shortx.services.rule.fact.emptyOrRegexMatchAndResults

object TextFromScreenNodeMatches : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.TextFromScreenNodeMatches::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        val data =
            param.condition.unpack_(tornaco.apps.shortx.core.proto.condition.TextFromScreenNodeMatches::class.java)
        val cv = RuleContextAndVars(param)
        val targetViewId = data.nodeId.compileContextAndVars(cv)
        logger.d("targetViewId: $targetViewId")
        val text = getTextFromViewId(targetViewId, param.shortXContext)
        logger.d("text: $text")
        val (res, list) = emptyOrRegexMatchAndResults(data.regex, text, data.matchOptions)
        logger.d("Matched: $list")
        return if (data.isInvert) {
            !res
        } else {
            res
        }.toResult()
    }

}
