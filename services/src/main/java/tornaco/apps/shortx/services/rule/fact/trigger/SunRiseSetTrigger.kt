package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.fact.SunRiseSet
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.newHandlerOfNewThread
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.location.TwilightListener
import tornaco.apps.shortx.services.location.TwilightState

class SunRiseSetTrigger(
    context: Context,
    shortXContext: ShortXContext
) :
    DAAndRuleAwareTrigger(context, shortXContext) {
    companion object {
        const val LOG_TAG = "SunRiseSetTrigger"
    }

    private val logger = Logger(LOG_TAG)

    private val handler by lazy { newHandlerOfNewThread(LOG_TAG) }
    private val listener = TwilightListener {
        logger.d("TwilightListener: $it")
    }

    override fun register() {
        updateTriggerState()
    }

    override fun unRegister() {
    }

    override fun updateTriggerState() {
        var anyFat = false
        run loop@{
            shortXContext.rule.forEachEnabledFacts { _, factProtoAny ->
                if (factProtoAny is_ SunRiseSet::class.java) {
                    val fact = factProtoAny unpack_ SunRiseSet::class.java
                    if (!fact.isDisabled) {
                        anyFat = true
                        return@loop
                    }
                }
            }
        }
        logger.d("anyFat: $anyFat")
        if (anyFat) {
            shortXContext.twilight.manager.registerListener(listener, handler)
        } else {
            shortXContext.twilight.manager.unregisterListener(listener)
        }
    }

    override val factClass: Class<out Message> = SunRiseSet::class.java
}