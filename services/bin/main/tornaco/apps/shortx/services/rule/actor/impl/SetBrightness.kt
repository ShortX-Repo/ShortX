package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object SetBrightness : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.SetBrightness::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val data = action unpack_ tornaco.apps.shortx.core.proto.action.SetBrightness::class.java
        android.provider.Settings.System.putInt(
            androidContext.contentResolver,
            android.provider.Settings.System.SCREEN_BRIGHTNESS,
            minOf(255, maxOf(0, data.value)).apply {
                logger.d("set brightness to: $this")
            }
        )
        Actor.Res.EMPTY
    }
}