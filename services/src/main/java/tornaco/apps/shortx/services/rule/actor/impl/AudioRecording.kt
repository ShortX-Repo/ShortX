package tornaco.apps.shortx.services.rule.actor.impl

import android.media.MediaRecorder
import com.google.protobuf.Message
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.proto.common.AudioSource
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import java.io.File


object AudioRecording : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.AudioRecording::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.AudioRecording::class.java
        val source = when (val src = d.src) {
            AudioSource.Mic -> {
                MediaRecorder.AudioSource.MIC
            }

            AudioSource.R_Submix -> {
                MediaRecorder.AudioSource.REMOTE_SUBMIX
            }

            AudioSource.VoiceCall -> {
                MediaRecorder.AudioSource.VOICE_CALL
            }

            else -> {
                throw IllegalArgumentException("Bad src: $src")
            }
        }

        val outputFile = File(
            Dirs.baseServerRecordingDir(),
            "${d.fileNamePrefix}-${DateUtils.formatForFileName(System.currentTimeMillis())}.wav"
        )
        outputFile.parentFile.mkdirs()

        shortXContext.audio.startRecording(source, outputFile)

        while (shortXContext.audio.isRecording) {
            delay(500)
        }

        logger.i("DONE")
    }
}