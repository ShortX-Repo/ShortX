package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.ClipData
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.platform.Classes.clipboardServiceClass
import tornaco.apps.shortx.core.proto.fact.ClipboardContentChanged
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXService
import util.Reflections

object ClipboardServiceHook {
    private val logger = Logger("ShortXHook.ClipboardServiceHook")

    fun hookClipboardService(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        afterMethod(
            lpparam.classLoader.clipboardServiceClass(),
            "sendClipChangedBroadcast"
        ) { param ->
            val clip: ClipData =
                Reflections.getObjectField(param.args[0], "primaryClip") as ClipData
            logger.d("sendClipChangedBroadcast: $clip")
            val text = clip.getItemAt(0)?.text?.toString() ?: ""
            ShortXService.shortXContext.rule.publishFact(
                reason = "Clipboard",
                fact = ClipboardContentChanged.newBuilder().setContent(text).build(),
                initialContextData = mapOf(ContextDataMapping.ClipboardContentChanged.clipboardContent.name to text)
            )
        }
    }.onFailure {
        logger.e(it, "hookClipboardService")
    }
}
