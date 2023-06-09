package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object SetAutoBrightness : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.SetAutoBrightness::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val data =
            action unpack_ tornaco.apps.shortx.core.proto.action.SetAutoBrightness::class.java
        val mode = if (data.enable) {
            android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
        } else {
            android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        }
        android.provider.Settings.System.putInt(
            context.contentResolver,
            android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE,
            mode
        )
    }
}