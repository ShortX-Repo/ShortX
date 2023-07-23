package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.rule.replaceLocalVarValues
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.UserHandleUtils
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object StartActivityUrlSchema : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.StartActivityUrlSchema::class.java
    }

    @SuppressLint("MissingPermission")
    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val startActivity =
            action unpack_ tornaco.apps.shortx.core.proto.action.StartActivityUrlSchema::class.java

        val userId = startActivity.userId
        val url = startActivity.urlSchema.inflateContextMap(contextData)
            .replaceLocalVarValues(localVars)
            .replaceGlobalVarValues {
            shortXContext.rule.getAllGlobalVars()
        }
        logger.d("startActivity: $url")
        context.startActivityAsUser(
            Intent(Intent.ACTION_VIEW).setData(Uri.parse(url))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            UserHandleUtils.userIdToUserHandle(userId)
        )
    }
}