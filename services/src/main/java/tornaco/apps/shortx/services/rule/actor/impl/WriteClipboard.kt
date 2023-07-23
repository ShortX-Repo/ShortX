package tornaco.apps.shortx.services.rule.actor.impl

import android.content.ClipData
import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.rule.replaceLocalVarValues
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.clipboardManager
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object WriteClipboard : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.WriteClipboard::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) {
        with(params) {
            val data =
                action unpack_ tornaco.apps.shortx.core.proto.action.WriteClipboard::class.java
            val allGlobalVars = { shortXContext.rule.getAllGlobalVars() }
            context.clipboardManager.primaryClip = ClipData.newPlainText(
                data.note ?: "shortx-action",
                data.text.inflateContextMap(contextData)
                    .replaceLocalVarValues(localVars)
                    .replaceGlobalVarValues(allGlobalVars)
            )
        }
    }
}