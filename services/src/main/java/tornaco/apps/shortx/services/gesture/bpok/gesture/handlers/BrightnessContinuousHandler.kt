package tornaco.apps.shortx.services.gesture.bpok.gesture.handlers

import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.gesture.bpok.gesture.ContinuousActionHandler

class BrightnessContinuousHandler : ContinuousActionHandler {
    private val logger = Logger("BrightnessHandler")
    private var startY = 0f
    private var startBrightness = 0f

    override fun onStart(edgeGroup: Int, x: Float, y: Float, time: Long) {
        startY = y
        logger.d("onStart: y=$y")
    }

    override fun onMove(x: Float, y: Float, time: Long) {
        val dy = y - startY
        val adjustment = (-dy / 1000f).coerceIn(-1f, 1f)
        logger.d("onMove: dy=$dy, adjustment=$adjustment")
    }

    override fun onFinish(x: Float, y: Float, time: Long) {
        logger.d("onFinish")
    }

    override fun onCancel() {
        logger.d("onCancel")
    }
}
