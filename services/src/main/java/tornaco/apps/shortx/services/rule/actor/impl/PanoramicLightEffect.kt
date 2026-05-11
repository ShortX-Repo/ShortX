package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import kotlin.math.max

object PanoramicLightEffect : Actor {
    private const val FALLBACK_COLOR = 0x55FF4018

    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.PanoramicLightEffect::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        withContext(Dispatchers.Main) {
            val d = action unpack_ tornaco.apps.shortx.core.proto.action.PanoramicLightEffect::class.java
            val cv = RuleContextAndVars(params)

            val color = d.colorInt.toIntOrNull() ?: d.colorInt.compileContextAndVars(cv).trim()
                .toIntOrNull() ?: FALLBACK_COLOR
            shortXContext.overlay.showPanoramicLightEffect(color, max(d.times.toIntOrNull() ?: 2, 1))
            Actor.Res.EMPTY
        }
    }
}