@file:OptIn(ExperimentalCoroutinesApi::class)

package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import android.content.Context
import com.google.protobuf.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.internal.toLongOrDefault
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.proto.action.Delay
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.rule.replaceLocalVarValues
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import java.util.UUID


object Delay : Actor {
    @SuppressLint("StaticFieldLeak")
    private lateinit var delayScheduler: DelayScheduler

    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.Delay::class.java
    }

    override fun init(androidContext: Context, shortXContext: ShortXContext) {
        super.init(androidContext, shortXContext)
        delayScheduler = DelayScheduler(androidContext, shortXContext)
        delayScheduler.init()
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

        if (d.useAlarm) {
            logger.d("delay with alarm")
            suspendCancellableCoroutine { continuation ->
                val requestId = UUID.randomUUID().toString()
                logger.d("delay with requestId: $requestId")
                delayScheduler.schedule(requestId, timeLong) {
                    logger.d("delay alarm tick.")
                    continuation.resume(Unit) {}
                }

                continuation.invokeOnCancellation {
                    delayScheduler.cancel(requestId, "invokeOnCancellation")
                }
            }
        } else {
            delay(maxOf(0, timeLong))
        }
    }

}