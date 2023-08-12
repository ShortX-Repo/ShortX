package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.fact.ClipboardContentChanged
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.clipboardManager
import tornaco.apps.shortx.services.ShortXContext
import java.util.concurrent.atomic.AtomicReference

// Need ClipboardServiceHook to hook clipboardAccessAllowed
class ClipboardTrigger(override val context: Context, override val shortXContext: ShortXContext) :
    Trigger {
    private val logger = Logger("ClipboardTrigger")
    private val lastText = AtomicReference("")
    private val lastTriggerTime = AtomicReference(0L)

    override fun register() {
        context.clipboardManager.addPrimaryClipChangedListener {
            val clipText =
                runCatching { context.clipboardManager.primaryClip?.getItemAt(0)?.text.toString() }.getOrElse { "" }
            logger.p("Clipboard change: $clipText")

            if (System.currentTimeMillis() - lastTriggerTime.get() >= 1000
                || lastText.get() != clipText
            ) {
                shortXContext.rule.publishFact(
                    reason = "Clipboard",
                    fact = ClipboardContentChanged.newBuilder().setContent(clipText).build(),
                    initialContextData = mapOf(ContextDataMapping.ClipboardContentChanged.clipboardContent.name to clipText)
                )
            } else {
                logger.d("Ignore dup clipboard content request.")
            }
            lastTriggerTime.set(System.currentTimeMillis())
            lastText.set(clipText)
        }
    }

    override fun unRegister() {
    }

}