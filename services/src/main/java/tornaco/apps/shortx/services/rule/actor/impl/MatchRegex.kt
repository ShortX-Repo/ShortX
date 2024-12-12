package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.fact.emptyOrRegexMatchAndResults


object MatchRegex : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.MatchRegex::class.java
    }

    @ExperimentalCoroutinesApi
    override suspend fun handle(
        params: HandleParams
    ): Actor.Res = with(params) {
        val data =
            action unpack_ tornaco.apps.shortx.core.proto.action.MatchRegex::class.java

        val cv = RuleContextAndVars(params)

        val matchRes = emptyOrRegexMatchAndResults(
            data.regex.compileContextAndVars(cv),
            data.string.compileContextAndVars(cv),
            data.matchOptions
        )
        contextData.add(
            data.customContextDataKey.forKey(ContextDataMapping.MatchRegex.isMatch.name)
                .fallbackOnEmpty(ContextDataMapping.MatchRegex.isMatch.name), matchRes.first
        )
        contextData.add(
            data.customContextDataKey.forKey(ContextDataMapping.MatchRegex.matchResult.name)
                .fallbackOnEmpty(ContextDataMapping.MatchRegex.matchResult.name), matchRes.second
        )

        Actor.Res(matchRes.first.toString() + System.lineSeparator() + matchRes.second)
    }
}