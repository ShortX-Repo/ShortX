package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.toIntent
import tornaco.apps.shortx.core.util.UserHandleUtils
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object StartActivityIntent : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.StartActivityIntent::class.java
    }

    @SuppressLint("MissingPermission")
    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val startActivity =
            action unpack_ tornaco.apps.shortx.core.proto.action.StartActivityIntent::class.java

        val intent = startActivity.intent.toIntent()
        val userId = startActivity.userId
        logger.d("startActivityAsUser: $intent $userId")
        context.startActivityAsUser(intent, UserHandleUtils.userIdToUserHandle(userId))
    }
}