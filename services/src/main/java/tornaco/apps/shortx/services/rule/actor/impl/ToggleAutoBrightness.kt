package tornaco.apps.shortx.services.rule.actor.impl

import android.provider.Settings
import com.google.protobuf.Message
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object ToggleAutoBrightness : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ToggleAutoBrightness::class.java
    }

    override suspend fun handle(params: HandleParams) = with(params) {
        val currentMode = Settings.System.getInt(
            androidContext.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        )
        val targetMode = if (currentMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        } else {
            Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
        }
        Settings.System.putInt(
            androidContext.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            targetMode
        )
        Actor.Res.EMPTY
    }
}
