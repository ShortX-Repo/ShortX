package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.fact.ClipboardContentChanged
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.clipboardManager
import tornaco.apps.shortx.services.ShortXContext


class ClipboardTrigger(override val context: Context, override val shortXContext: ShortXContext) :
    Trigger {
    private val logger = Logger("ClipboardTrigger")

    override fun register() {
        context.clipboardManager.addPrimaryClipChangedListener {
            val it =
                runCatching { context.clipboardManager.primaryClip?.getItemAt(0)?.text.toString() }.getOrElse { "" }
            logger.p("Clipboard change: $it")
            shortXContext.rule.publishFact(
                reason = "Clipboard",
                fact = ClipboardContentChanged.newBuilder().setContent(it).build(),
                initialContextData = mapOf(ContextDataMapping.ClipboardContentChanged.clipboardContent.name to it)
            )
        }
    }

    override fun unRegister() {
    }

}