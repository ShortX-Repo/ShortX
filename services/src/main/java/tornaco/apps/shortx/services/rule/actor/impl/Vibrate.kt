package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.os.VibrationEffect
import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.vibrate
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


private const val delay = 400L
private val SEVEN_ELEMENTS_VIBRATION_AMPLITUDE = intArrayOf(
    0,  // No delay before starting
    255,  // Vibrate full amplitude
    0,  // No amplitude while waiting
    255,
    0,
    255,
    0
)

private val VIBRATION_ATTRIBUTES = AudioAttributes.Builder()
    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
    .setUsage(AudioAttributes.USAGE_MEDIA)
    .build()


object Vibrate : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.Vibrate::class.java
    }

    @SuppressLint("MissingPermission")
    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.Vibrate::class.java

        val customVibPattern = longArrayOf(
            0,
            d.vib1.toLong(),
            delay,
            d.vib2.toLong(),
            delay,
            d.vib3.toLong(),  // How long to vibrate
            delay
        )
        val effect = VibrationEffect.createWaveform(
            customVibPattern,
            SEVEN_ELEMENTS_VIBRATION_AMPLITUDE, -1
        )
        androidContext.vibrate.vibrate(effect, VIBRATION_ATTRIBUTES)

        Actor.Res.EMPTY
    }

}