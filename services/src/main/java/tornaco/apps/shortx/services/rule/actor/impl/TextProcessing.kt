package tornaco.apps.shortx.services.rule.actor.impl

import com.github.promeg.pinyinhelper.Pinyin
import com.google.protobuf.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.action.TextProcessingToPinyin
import tornaco.apps.shortx.core.proto.action.TextProcessingTrimLength
import tornaco.apps.shortx.core.proto.action.TextProcessingTrimSpace
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.RuleContextAndVars
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object TextProcessing : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.TextProcessing::class.java
    }

    @ExperimentalCoroutinesApi
    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val data =
            action unpack_ tornaco.apps.shortx.core.proto.action.TextProcessing::class.java
        val cv = RuleContextAndVars(params)
        val result = process(data.processorsList, data.text, cv)
        contextData.add(ContextDataMapping.TextProcessing.textProcessResult.name, result)
        Actor.Res.EMPTY
    }

    fun process(processors: List<ProtoAny>, rawText: String, cv: RuleContextAndVars): String {
        val text = rawText.compileContextAndVars(cv)
        var result = text
        processors.forEach {
            if (it is_ TextProcessingTrimLength::class.java) {
                val ttl = it unpack_ TextProcessingTrimLength::class.java
                val length = ttl.length.compileContextAndVars(cv).toIntOrNull() ?: -1
                logger.d("TextProcessingTrimLength: $length")
                if (length >= 0) {
                    result = result.subSequence(0, length).toString()
                }
            } else if (it is_ TextProcessingTrimSpace::class.java) {
                logger.d("TextProcessingTrimSpace")
                result = result.trim()
            } else if (it is_ TextProcessingToPinyin::class.java) {
                logger.d("TextProcessingToPinyin")
                result = Pinyin.toPinyin(result, " ")
            }
        }
        return result
    }
}