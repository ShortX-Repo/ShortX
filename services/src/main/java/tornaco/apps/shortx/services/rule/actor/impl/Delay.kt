package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.delay
import okhttp3.internal.toLongOrDefault
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.proto.action.Delay
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.rule.replaceLocalVarValues
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object Delay : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.Delay::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) {
        val d = params.action unpack_ Delay::class.java
        val timeStr = if (d.timeString.isNotEmpty()) {
            d.timeString.inflateContextMap(params.contextData)
                .replaceLocalVarValues(params.localVars)
                .replaceGlobalVarValues { params.shortXContext.rule.getAllGlobalVars() }
        } else {
            d.time.toString()
        }
        logger.d("timeStr: $timeStr")
        val timeLong = timeStr.toLongOrDefault(0L)
        delay(maxOf(0, timeLong))
    }
}