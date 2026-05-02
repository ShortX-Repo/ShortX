package tornaco.apps.shortx.services.gesture.bpok.gesture.handlers

import android.content.Context
import android.media.AudioManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.gesture.bpok.gesture.ContinuousActionHandler
import kotlin.math.roundToInt

class VolumeContinuousHandler(
    private val context: Context,
    private val streamType: Int = AudioManager.STREAM_MUSIC,
    private val sensitivityScale: Float = 1f,
    private val overlayClient: ContinuousAdjustOverlayClient = ContinuousAdjustOverlayClient(context),
) : ContinuousActionHandler {
    private val logger = Logger("VolumeHandler")
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val maxVolume = audioManager.getStreamMaxVolume(streamType)
    private var startY = 0f
    private var startVolume = 0
    private var currentVolume = 0

    override fun onStart(edgeGroup: Int, x: Float, y: Float, time: Long) {
        startY = y
        startVolume = audioManager.getStreamVolume(streamType)
        currentVolume = startVolume

        overlayClient.showVolume(startVolume, maxVolume)
        logger.d("onStart: y=$y, volume=$startVolume, maxVolume=$maxVolume, streamType=$streamType")
    }

    override fun onMove(x: Float, y: Float, time: Long) {
        val dy = y - startY
        val delta = (-dy * sensitivityScale * maxVolume / 1000f).roundToInt()
        currentVolume = (startVolume + delta).coerceIn(0, maxVolume)
        audioManager.setStreamVolume(streamType, currentVolume, 0)
        overlayClient.updateValue(currentVolume)
        logger.d("onMove: dy=$dy, delta=$delta, volume=$currentVolume")
    }

    override fun onFinish(x: Float, y: Float, time: Long) {
        logger.d("onFinish")
        overlayClient.dismiss()
    }

    override fun onCancel() {
        logger.d("onCancel: reverting to $startVolume")
        audioManager.setStreamVolume(streamType, startVolume, 0)
        overlayClient.dismiss()
    }
}
