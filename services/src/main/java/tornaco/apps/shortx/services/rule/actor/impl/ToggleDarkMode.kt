package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.mvel.handle.impl.SettingsImpl

object ToggleDarkMode : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ToggleDarkMode::class.java
    }

    override suspend fun handle(params: HandleParams) = with(params) {
        val settings = SettingsImpl(androidContext)
        if (settings.isDarkModeEnabled()) settings.disableDarkMode() else settings.enableDarkMode()
        Actor.Res.EMPTY
    }
}
