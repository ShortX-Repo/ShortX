package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.common.AndroidIntentComponentType
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

        val intent =
            startActivity.intent.toIntent(
                contextData = contextData,
                localVars = localVars,
                parameterInputs = parameterInputs,
            ) { shortXContext.rule.getAllGlobalVars() }
        val userId = startActivity.userId
        val componentType: AndroidIntentComponentType = startActivity.componentType
        logger.d("startActivityAsUser: $intent $userId $componentType")
        val userHandle = UserHandleUtils.userIdToUserHandle(userId)

        when (componentType) {
            AndroidIntentComponentType.UNRECOGNIZED, AndroidIntentComponentType.AndroidIntentComponentTypeActivity -> {
                androidContext.startActivityAsUser(
                    intent,
                    userHandle
                )
            }

            AndroidIntentComponentType.AndroidIntentComponentTypeBroadcast -> {
                androidContext.sendBroadcastAsUser(
                    intent,
                    userHandle
                )
            }

            AndroidIntentComponentType.AndroidIntentComponentTypeService -> {
                androidContext.startServiceAsUser(
                    intent,
                    userHandle
                )
            }
        }

        Actor.Res.EMPTY
    }
}