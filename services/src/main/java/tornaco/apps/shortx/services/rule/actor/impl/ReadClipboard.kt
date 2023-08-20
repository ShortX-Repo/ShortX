package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.clipboardManager
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object ReadClipboard : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ReadClipboard::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) {
        with(params) {
            val d =
                params.action unpack_ tornaco.apps.shortx.core.proto.action.ReadClipboard::class.java
            kotlin.runCatching { androidContext.clipboardManager.primaryClip?.getItemAt(0)?.text }
                .getOrElse {
                    logger.e(it, "read primaryClip")
                    null
                }?.let {
                    logger.d("add $it to contextData")
                    params.contextData.add(
                        d.customContextDataKey.forKey(ContextDataMapping.ReadClipboard.clipboardContent.name)
                            .fallbackOnEmpty { ContextDataMapping.ReadClipboard.clipboardContent.name },
                        it
                    )
                }
        }
    }
}