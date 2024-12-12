package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.action.SetDarkModeEnabled
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.mvel.handle.impl.SettingsImpl


object SetDarkModeEnabled : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.SetDarkModeEnabled::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val settings = SettingsImpl(androidContext)
        val d = action unpack_ SetDarkModeEnabled::class.java
        if (d.enable) settings.enableDarkMode() else settings.disableDarkMode()
        Actor.Res.EMPTY
    }
}