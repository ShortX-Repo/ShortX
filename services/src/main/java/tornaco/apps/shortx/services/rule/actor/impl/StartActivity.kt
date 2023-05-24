package tornaco.apps.shortx.services.rule.actor.impl

import android.content.ComponentName
import android.content.Intent
import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object StartActivity : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.StartActivity::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val startActivity =
            action unpack_ tornaco.apps.shortx.core.proto.action.StartActivity::class.java

        context.startActivity(
            Intent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setComponent(ComponentName.unflattenFromString(startActivity.componentNameAsString))
        )
    }
}