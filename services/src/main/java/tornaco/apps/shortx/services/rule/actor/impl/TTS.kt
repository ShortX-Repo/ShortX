package tornaco.apps.shortx.services.rule.actor.impl

import android.speech.tts.TextToSpeech
import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.rule.replaceLocalVarValues
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import java.util.Locale

object TTS : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.TTS::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.TTS::class.java
        var tts: TextToSpeech? = null
        tts = TextToSpeech(
            context
        ) { status ->
            logger.d("TextToSpeech status: $status")
            if (status == TextToSpeech.SUCCESS) {
                val result = tts!!.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    throw IllegalStateException("TTS init fail: $result")
                }
                tts!!.setPitch(1.0f)
                tts!!.speak(
                    d.text.inflateContextMap(contextData)
                        .replaceLocalVarValues(localVars)
                        .replaceGlobalVarValues { shortXContext.rule.getAllGlobalVars() },
                    TextToSpeech.QUEUE_FLUSH,
                    null
                )
            }
        }
    }
}