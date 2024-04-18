package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object ReplaceRegex : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ReplaceRegex::class.java
    }

    @ExperimentalCoroutinesApi
    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val data =
            action unpack_ tornaco.apps.shortx.core.proto.action.ReplaceRegex::class.java
        val cv = RuleContextAndVars(params)
        val regex = data.regex.compileContextAndVars(cv).toRegex()
        val result = data.string.compileContextAndVars(cv)
            .replace(regex, data.replacement.compileContextAndVars(cv))
        contextData.add(ContextDataMapping.ReplaceRegex.replaceResult.name, result)
    }
}