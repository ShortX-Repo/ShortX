package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import com.google.protobuf.Message
import tornaco.apps.shortx.core.os.myUserId
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.IntentConverter
import tornaco.apps.shortx.core.util.UserHandleUtils
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object LaunchPinedItem : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.LaunchPinedItem::class.java
    }

    @SuppressLint("MissingPermission")
    override suspend fun handle(
        params: HandleParams
    ) {
        with(params) {
            val data =
                action unpack_ tornaco.apps.shortx.core.proto.action.LaunchPinedItem::class.java
            androidContext.startActivityAsUser(
                IntentConverter.bytesToIntent(data.intent.toByteArray()),
                UserHandleUtils.userIdToUserHandle(data.userId)
            )
        }
    }
}