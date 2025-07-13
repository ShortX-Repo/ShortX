package tornaco.apps.shortx.services.rule.actor.impl

import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.action.RequestAudioFocus
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.audioManager
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object RequestAudioFocus : Actor {
    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    private val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
        .setAudioAttributes(audioAttributes)
        .setOnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    // 获得焦点，可以播放或继续
                    logger.d("AUDIOFOCUS_GAIN")
                }

                AudioManager.AUDIOFOCUS_LOSS -> {
                    // 永久失去焦点，应该停止播放
                    logger.d("AUDIOFOCUS_LOSS")
                }

                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    // 临时失去焦点，应该暂停
                }

                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    // 可以播放，但音量要变小
                }
            }
        }
        .build()

    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.RequestAudioFocus::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        withContext(Dispatchers.Main) {
            val d = action unpack_ RequestAudioFocus::class.java
            val audioManager = androidContext.audioManager
            val result =
                if (d.isRequest) {
                    audioManager.requestAudioFocus(focusRequest)
                } else {
                    audioManager.abandonAudioFocusRequest(
                        focusRequest
                    )
                }
            Actor.Res("Result: $result")
        }
    }
}