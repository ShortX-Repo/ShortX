package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.rule.unpack_
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
    ): Unit = with(params) {
        val data =
            action unpack_ tornaco.apps.shortx.core.proto.action.MatchRegex::class.java
        emptyOrRegexMatchAndResults(data.regex, data.string, data.matchOptions).apply {
            contextData.add(ContextDataMapping.MatchRegex.isMatch.name, first)
            contextData.add(ContextDataMapping.MatchRegex.matchResult.name, second)
        }
    }
}