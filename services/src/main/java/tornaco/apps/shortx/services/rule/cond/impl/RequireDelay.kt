package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import kotlinx.coroutines.delay
import okhttp3.internal.toLongOrDefault
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.rule.replaceLocalVarValues
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.impl.Delay
import tornaco.apps.shortx.services.rule.cond.Cond

object RequireDelay : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.RequireDelay::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        val data =
            param.condition.unpack_(tornaco.apps.shortx.core.proto.condition.RequireDelay::class.java)
        val timeStr = data.timeString.inflateContextMap(param.contextData)
            .replaceLocalVarValues(param.localVars)
            .replaceGlobalVarValues { param.shortXContext.rule.getAllGlobalVars() }
        Delay.logger.d("timeStr: $timeStr")
        val timeLong = timeStr.toLongOrDefault(0L)
        delay(maxOf(0, timeLong))
        return Cond.Result(true)
    }
}