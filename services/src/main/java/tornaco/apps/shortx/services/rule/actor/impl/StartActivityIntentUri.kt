package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import android.content.Intent
import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object StartActivityIntentUri : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.StartActivityIntentUri::class.java
    }

    @SuppressLint("MissingPermission")
    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val startActivity =
            action unpack_ tornaco.apps.shortx.core.proto.action.StartActivityIntentUri::class.java
        val intent = Intent.parseUri(startActivity.intentUri, Intent.URI_INTENT_SCHEME)
        logger.d("intent: $intent")
        androidContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}