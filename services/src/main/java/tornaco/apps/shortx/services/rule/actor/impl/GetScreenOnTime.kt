package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.proto.common.ScreenOnTimeStartFrom
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object GetScreenOnTime : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.GetScreenOnTime::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val d =
            action unpack_ tornaco.apps.shortx.core.proto.action.GetScreenOnTime::class.java
        val timeInSec: Long =
            if (d.from == ScreenOnTimeStartFrom.ScreenOnTimeStartFrom_LastScreenOff) {
                shortXContext.user.screenOnTimeFromLastScreenOffInSeconds.get()
            } else {
                shortXContext.user.screenOnTimeFromSystemReadyInSeconds.get()
            }
        params.contextData.add(
            d.customContextDataKey.forKey(ContextDataMapping.GetScreenOnTime.screenOnTime.name)
                .fallbackOnEmpty { ContextDataMapping.GetScreenOnTime.screenOnTime.name },
            timeInSec
        )
    }
}