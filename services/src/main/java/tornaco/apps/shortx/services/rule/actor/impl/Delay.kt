@file:OptIn(ExperimentalCoroutinesApi::class)

package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import android.content.Context
import com.google.protobuf.Message
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.internal.toLongOrDefault
import tornaco.apps.shortx.core.proto.action.Delay
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.countdown.CountdownViewManager
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean

object DelaySchedulerHolder {
    @SuppressLint("StaticFieldLeak")
    lateinit var delayScheduler: DelayScheduler
    private val isInitCalled = AtomicBoolean(false)

    fun init(androidContext: Context, shortXContext: ShortXContext) {
        if (isInitCalled.compareAndSet(false, true)) {
            delayScheduler = DelayScheduler(androidContext, shortXContext)
            delayScheduler.init()
        }
    }
}

object Delay : Actor {
    @SuppressLint("StaticFieldLeak")
    private var cdView: CountdownViewManager? = null

    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.Delay::class.java
    }

    override fun init(androidContext: Context, shortXContext: ShortXContext) {
        super.init(androidContext, shortXContext)
        DelaySchedulerHolder.init(androidContext, shortXContext)
        cdView = CountdownViewManager(androidContext, shortXContext)
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res {
        val d = params.action unpack_ Delay::class.java
        val cv = RuleContextAndVars(params)

        val timeStr = if (d.timeString.isNotEmpty()) {
            d.timeString.compileContextAndVars(cv)
        } else {
            d.time.toString()
        }
        logger.d("timeStr: $timeStr")
        val timeLong = timeStr.toLongOrDefault(0L)
        val dialog = if (d.showCD) {
            withContext(Dispatchers.Main) {
                cdView?.showCDDialog(maxOf(0, timeLong / 1000).toInt())
            }
        } else null

        if (d.useAlarm) {
            logger.d("delay with alarm")
            suspendCancellableCoroutine<Unit> { continuation ->
                val requestId = UUID.randomUUID().toString()
                logger.d("delay with requestId: $requestId")
                DelaySchedulerHolder.delayScheduler.schedule(requestId, timeLong) {
                    logger.d("delay alarm tick.")
                    continuation.resume(Unit) {}
                }

                continuation.invokeOnCancellation {
                    DelaySchedulerHolder.delayScheduler.cancel(requestId, "invokeOnCancellation")
                    dialog?.dismiss()
                }

            }
        } else {
            kotlin.runCatching {
                delay(maxOf(0, timeLong))
            }.onFailure {
                if (it is CancellationException) {
                    dialog?.dismiss()
                    throw it
                }
            }
        }
        dialog?.dismiss()
        return Actor.Res.EMPTY
    }

}