package tornaco.apps.shortx.services.rule.actor.impl

import android.widget.Toast
import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.proto.action.ShowToast
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object ShowToast : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ShowToast::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val t = action unpack_ ShowToast::class.java
        withContext(Dispatchers.Main) {
            Toast.makeText(
                context,
                t.message.inflateContextMap(contextData).replaceGlobalVarValues {
                    shortXContext.rule.getAllGlobalVars()
                },
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}