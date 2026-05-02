package tornaco.apps.shortx.services.gesture.bpok.gesture.handlers

import android.content.Context
import android.provider.Settings
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.gesture.bpok.gesture.ContinuousActionHandler
import kotlin.math.roundToInt

class BrightnessContinuousHandler(
    private val context: Context,
    private val sensitivityScale: Float = 1f,
    private val minValue: Int = 0,
    private val maxValue: Int = 255,
    private val overlayClient: ContinuousAdjustOverlayClient = ContinuousAdjustOverlayClient(context),
) : ContinuousActionHandler {
    private val logger = Logger("BrightnessHandler")
    private var startY = 0f
    private var startBrightness = 0
    private var autoBrightnessWasEnabled = false
    private var currentBrightness = 0

    override fun onStart(edgeGroup: Int, x: Float, y: Float, time: Long) {
        startY = y
        startBrightness = Settings.System.getInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            128
        )
        currentBrightness = startBrightness
        autoBrightnessWasEnabled = Settings.System.getInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        ) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC

        if (autoBrightnessWasEnabled) {
            Settings.System.putInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
            )
        }

        overlayClient.showBrightness(startBrightness, maxValue)
        logger.d("onStart: y=$y, brightness=$startBrightness, autoBrightness=$autoBrightnessWasEnabled")
    }

    override fun onMove(x: Float, y: Float, time: Long) {
        val dy = y - startY
        val range = maxValue - minValue
        val delta = (-dy * sensitivityScale * range / 1000f).roundToInt()
        currentBrightness = (startBrightness + delta).coerceIn(minValue, maxValue)
        Settings.System.putInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            currentBrightness
        )
        overlayClient.updateValue(currentBrightness)
        logger.d("onMove: dy=$dy, delta=$delta, brightness=$currentBrightness")
    }

    override fun onFinish(x: Float, y: Float, time: Long) {
        logger.d("onFinish")
        overlayClient.dismiss()
    }

    override fun onCancel() {
        logger.d("onCancel: reverting to $startBrightness")
        Settings.System.putInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            startBrightness
        )
        if (autoBrightnessWasEnabled) {
            Settings.System.putInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
            )
        }
        overlayClient.dismiss()
    }
}
