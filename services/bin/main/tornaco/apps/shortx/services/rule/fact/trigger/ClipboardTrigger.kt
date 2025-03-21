package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.fact.ClipboardContentChanged
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.FactPublishData
import java.util.concurrent.atomic.AtomicReference

// Need ClipboardServiceHook to hook clipboardAccessAllowed
class ClipboardTrigger(override val context: Context, override val shortXContext: ShortXContext) :
    Trigger {
    private val logger = Logger("ClipboardTrigger")
    private val lastText = AtomicReference("")
    private val lastTriggerTime = AtomicReference(0L)

    override fun register() {
        shortXContext.input.addOnClipboardListener { clipText ->
            // logger.logOnDebug("Clipboard change: $clipText")

            if (System.currentTimeMillis() - lastTriggerTime.get() >= 1000
                || lastText.get() != clipText
            ) {
                shortXContext.rule.publishFactChecked(ClipboardContentChanged.getDefaultInstance()) {
                    FactPublishData(
                        reason = "Clipboard",
                        fact = ClipboardContentChanged.newBuilder().setContent(clipText).build(),
                    ) {
                        mapOf(ContextDataMapping.ClipboardContentChanged.clipboardContent.name to clipText)
                    }
                }

                shortXContext.input.addClipboardText(clipText)
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