package tornaco.apps.shortx.services.rule.cond.impl

import android.content.Context
import com.google.protobuf.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.internal.toLongOrDefault
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.rule.replaceLocalVarValues
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.impl.DelaySchedulerHolder
import tornaco.apps.shortx.services.rule.cond.Cond
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
object RequireDelay : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.RequireDelay::class.java

    override fun init(androidContext: Context, shortXContext: ShortXContext) {
        super.init(androidContext, shortXContext)
        DelaySchedulerHolder.init(androidContext, shortXContext)
    }

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        val data =
            param.condition.unpack_(tornaco.apps.shortx.core.proto.condition.RequireDelay::class.java)
        val timeStr = data.timeString.inflateContextMap(param.contextData)
            .replaceLocalVarValues(param.localVars)
            .replaceGlobalVarValues { param.shortXContext.rule.getAllGlobalVars() }
        logger.d("timeStr: $timeStr")
        val timeLong = timeStr.toLongOrDefault(0L)

        if (data.useAlarm) {
            logger.d("delay with alarm")
            suspendCancellableCoroutine { continuation ->
                val requestId = UUID.randomUUID().toString()
                logger.d("delay with requestId: $requestId")
                DelaySchedulerHolder.delayScheduler.schedule(requestId, timeLong) {
                    logger.d("delay alarm tick.")
                    continuation.resume(Unit) {}
                }
                continuation.invokeOnCancellation {
                    DelaySchedulerHolder.delayScheduler.cancel(requestId, "invokeOnCancellation")
                }
            }
        } else {
            delay(maxOf(0, timeLong))
        }

        return Cond.Result(true)
    }
}