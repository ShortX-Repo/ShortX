package tornaco.apps.shortx.ui.demo

import android.media.AudioAttributes
import android.os.VibrationEffect
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import tornaco.apps.shortx.core.util.vibrate

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

@Composable
fun VibrateDemo() {
    val context = LocalContext.current
    FilledTonalButton(onClick = {
        val customVibPattern = longArrayOf(
            0,
            200,
            delay,
            200,
            delay,
            200,  // How long to vibrate
            delay
        )
        val effect = VibrationEffect.createWaveform(
            customVibPattern,
            SEVEN_ELEMENTS_VIBRATION_AMPLITUDE, -1
        )
        context.vibrate.vibrate(
            effect,
            VIBRATION_ATTRIBUTES
        )
    }) {
        Text(text = "Vibrate")
    }


}