package tornaco.apps.shortx.services.rule.actor.impl

import android.content.Intent
import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object AppShortcut : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.AppShortcut::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.AppShortcut::class.java
        val intent = Intent.parseUri(d.intentUri, Intent.URI_INTENT_SCHEME)
        logger.d("intent: $intent")
        androidContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

}