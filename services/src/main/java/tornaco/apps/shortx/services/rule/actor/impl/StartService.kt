package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.toIntent
import tornaco.apps.shortx.core.util.UserHandleUtils
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object StartService : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.StartService::class.java
    }

    @SuppressLint("MissingPermission")
    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val data =
            action unpack_ tornaco.apps.shortx.core.proto.action.StartService::class.java
        val intent =
            data.intent.toIntent(contextData, localVars) { shortXContext.rule.getAllGlobalVars() }
        val userId = data.userId
        if (data.isForegroundService) {
            logger.d("start fg service, intent: ${data.intent} $userId")
            androidContext.startForegroundServiceAsUser(
                intent,
                UserHandleUtils.userIdToUserHandle(userId)
            )
        } else {
            logger.d("start service, intent: ${data.intent} $userId")
            androidContext.startServiceAsUser(intent, UserHandleUtils.userIdToUserHandle(userId))
        }
    }
}