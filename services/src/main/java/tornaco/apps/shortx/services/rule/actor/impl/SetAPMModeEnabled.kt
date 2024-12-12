package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.connectivityManager
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object SetAPMModeEnabled : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.SetAPMModeEnabled::class.java
    }

    @SuppressLint("MissingPermission")
    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.SetAPMModeEnabled::class.java
        val enableAPM = d.isEnable
        logger.w("Enable APM? $enableAPM")
        androidContext.connectivityManager.setAirplaneMode(enableAPM)
        Actor.Res.EMPTY
    }
}