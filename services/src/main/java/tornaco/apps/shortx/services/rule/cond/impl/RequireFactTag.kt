package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.GenericContextData
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.cond.Cond

object RequireFactTag : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.RequireFactTag::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        val data =
            param.condition.unpack_(tornaco.apps.shortx.core.proto.condition.RequireFactTag::class.java)
        val matchedTag = param.contextData.get(GenericContextData.CONTEXT_FACT_TAG)?.toString()
        logger.d("matchedTag: $matchedTag")
        val res = matchedTag != null && matchedTag == data.tag
        return if (data.isInvert) {
            !res
        } else {
            res
        }.toResult()
    }

}
