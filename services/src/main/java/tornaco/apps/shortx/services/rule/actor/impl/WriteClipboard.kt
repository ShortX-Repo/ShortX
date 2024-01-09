package tornaco.apps.shortx.services.rule.actor.impl

import android.content.ClipData
import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.clipboardManager
import tornaco.apps.shortx.services.rule.RuleContextAndVars
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
            val cv = RuleContextAndVars(params)
            val data =
                action unpack_ tornaco.apps.shortx.core.proto.action.WriteClipboard::class.java
            val allGlobalVars = { shortXContext.rule.getAllGlobalVars() }
            androidContext.clipboardManager.primaryClip = ClipData.newPlainText(
                data.note ?: "shortx-action",
                data.text.compileContextAndVars(cv)
            )
        }
    }
}