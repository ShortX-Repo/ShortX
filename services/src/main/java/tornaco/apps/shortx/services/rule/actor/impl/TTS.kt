@file:OptIn(ExperimentalCoroutinesApi::class)

package tornaco.apps.shortx.services.rule.actor.impl

import android.content.Context
import android.speech.tts.TextToSpeech
import com.google.protobuf.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.rule.replaceLocalVarValues
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import java.util.Locale

object TTS : Actor {
    private var tts: TextToSpeech? = null

    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.TTS::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.TTS::class.java
        if (tts == null) {
            initTTS(context)
        }

        val text = d.text.inflateContextMap(contextData)
            .replaceLocalVarValues(localVars)
            .replaceGlobalVarValues { shortXContext.rule.getAllGlobalVars() }
        tts!!.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null
        )
        Unit
    }

    private suspend fun initTTS(context: Context) {
        var tts: TextToSpeech? = null
        val status = suspendCancellableCoroutine {
            tts = TextToSpeech(
                context
            ) { status ->
                logger.d("TextToSpeech status: $status")
                it.resume(status) {}
            }
        }
        require(status == TextToSpeech.SUCCESS) {
            "TTS init fail: $status"
        }

        val result = tts!!.setLanguage(Locale.getDefault())
        if (result == TextToSpeech.LANG_MISSING_DATA
            || result == TextToSpeech.LANG_NOT_SUPPORTED
        ) {
            throw IllegalStateException("TTS setLanguage fail: $result")
        }
        tts!!.setPitch(1.0f)
    }
}