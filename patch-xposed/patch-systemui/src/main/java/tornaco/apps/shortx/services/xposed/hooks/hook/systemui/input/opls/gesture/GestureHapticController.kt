package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.os.VibrationEffect
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.vibrate

private enum class GestureHapticStage {
    Candidate,
    Ready,
    Success,
}

class GestureHapticController(private val context: Context) {
    private val logger = Logger("GestureHaptic")
    private val stageByToken = mutableMapOf<Long, GestureHapticStage>()
    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    fun onGestureCandidate(token: Long) {
        vibrateForStage(token, GestureHapticStage.Candidate)
    }

    fun onGestureReady(token: Long) {
        vibrateForStage(token, GestureHapticStage.Ready)
    }

    fun onGestureSuccess(token: Long) {
        vibrateForStage(token, GestureHapticStage.Success)
        stageByToken.remove(token)
    }

    fun onGestureCancel(token: Long?) {
        token?.let(stageByToken::remove)
    }

    @SuppressLint("MissingPermission")
    private fun vibrateForStage(token: Long, stage: GestureHapticStage) {
        val previous = stageByToken[token]
        if (previous != null && previous.ordinal >= stage.ordinal) return
        val vibrator = context.vibrate
        if (!vibrator.hasVibrator()) return
        val effect = when (stage) {
            GestureHapticStage.Candidate -> VibrationEffect.createOneShot(8L, 48)
            GestureHapticStage.Ready -> VibrationEffect.createOneShot(14L, 108)
            GestureHapticStage.Success -> VibrationEffect.createWaveform(
                longArrayOf(0L, 18L, 24L, 28L),
                intArrayOf(0, 140, 0, 220),
                -1,
            )
        }
        runCatching {
            vibrator.vibrate(effect, audioAttributes)
            stageByToken[token] = stage
        }.onFailure {
            logger.e(it, "vibrateForStage: $stage")
        }
    }
}