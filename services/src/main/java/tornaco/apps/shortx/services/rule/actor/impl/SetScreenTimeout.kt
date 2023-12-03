package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object SetScreenTimeout : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.SetScreenTimeout::class.java
    }

    @SuppressLint("MissingPermission")
    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.SetScreenTimeout::class.java
        val timeout: Long = d.timeoutMillis
        shortXContext.systemSettings.setScreenOffTimeout(timeout)
    }
}