package tornaco.apps.shortx.services.rule.actor.impl

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.google.protobuf.Message
import kotlinx.coroutines.suspendCancellableCoroutine
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.resume

object TTS : Actor {
    private var tts: TextToSpeech? = null

    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.TTS::class.java
    }

    override suspend fun handle(params: HandleParams) = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.TTS::class.java

        val cv = RuleContextAndVars(params)
        val text = d.text.compileContextAndVars(cv)

        // 尝试 speak，两次机会
        trySpeakWithRetry(androidContext, text)

        Actor.Res.EMPTY
    }

    private suspend fun trySpeakWithRetry(context: Context, text: String) {
        if (tts == null) {
            initTTS(context)
        }

        val success = trySpeak(text)
        logger.w("trySpeak1. $success")
        if (!success) {
            logger.w("First speak failed, retrying after reinitializing TTS.")
            tts?.shutdown()
            tts = null
            initTTS(context)

            val retrySuccess = trySpeak(text)
            logger.w("trySpeak2. $retrySuccess")
            if (!retrySuccess) {
                throw IllegalStateException("TTS speak failed after retry.")
            }
        }
    }

    private suspend fun trySpeak(text: String): Boolean {
        val uuid = UUID.randomUUID().toString()
        logger.w("trySpeak. $uuid")

        return suspendCancellableCoroutine { cont ->
            var resumed = false

            val listener = object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String) {
                    logger.w("TTS onStart: $utteranceId")
                }

                override fun onDone(id: String) {
                    logger.w("TTS onDone: $id")
                    if (id == uuid && !resumed) {
                        resumed = true
                        cont.resume(true)
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(id: String) {
                    logger.w("TTS onError: $id")
                    if (id == uuid && !resumed) {
                        resumed = true
                        cont.resume(false)
                    }
                }

                override fun onError(utteranceId: String, errorCode: Int) {
                    logger.w("TTS onError: $utteranceId $errorCode")
                    if (uuid == utteranceId && !resumed) {
                        resumed = true
                        cont.resume(false)
                    }
                }
            }

            tts?.setOnUtteranceProgressListener(listener)

            val paramsMap = hashMapOf(
                TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID to uuid
            )

            val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, paramsMap)

            if (result != TextToSpeech.SUCCESS) {
                logger.e("TTS speak() immediately failed with code: $result")
                cont.resume(false)
            }

            logger.w("trySpeak. $uuid. result: $result")
        }
    }

    private suspend fun initTTS(context: Context) {
        logger.w("initTTS.")
        val status = suspendCancellableCoroutine { cont ->
            tts = TextToSpeech(context) { status ->
                logger.d("TTS init status: $status")
                cont.resume(status)
            }
        }
        logger.w("initTTS. status: $status")
        require(status == TextToSpeech.SUCCESS) {
            "TTS init failed: status=$status"
        }

        val langResult = tts!!.setLanguage(Locale.getDefault())
        if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            throw IllegalStateException("TTS setLanguage failed: result=$langResult")
        }

        tts!!.setPitch(1.0f)
    }
}
