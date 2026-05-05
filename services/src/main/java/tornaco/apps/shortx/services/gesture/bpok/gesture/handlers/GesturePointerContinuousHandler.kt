package tornaco.apps.shortx.services.gesture.bpok.gesture.handlers

import android.content.Context
import android.view.InputDevice
import android.view.MotionEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ctrl.Controller
import tornaco.apps.shortx.services.ctrl.Device
import tornaco.apps.shortx.services.ctrl.Options
import tornaco.apps.shortx.services.ctrl.Position
import tornaco.apps.shortx.services.gesture.bpok.gesture.ContinuousActionHandler
import tornaco.apps.shortx.services.ui.GesturePointerOverlay
import kotlin.math.roundToInt

class GesturePointerContinuousHandler(
    context: Context,
    private val sensitivityScale: Float = 1f,
    private val clickOnRelease: Boolean = true,
) : ContinuousActionHandler {
    private val logger = Logger("GesturePointerHandler")
    private val device by lazy { Device(Options()) }
    private val controller by lazy { Controller(device) }
    private val pointerOverlay = GesturePointerOverlay(context)
    private val overlayScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private var edgeGroup = -1
    private var screenWidth = 0
    private var screenHeight = 0
    private var currentX = 0f
    private var currentY = 0f
    private var startX = 0f
    private var startY = 0f
    private var overlayShown = false

    override fun onStart(edgeGroup: Int, x: Float, y: Float, time: Long) {
        this.edgeGroup = edgeGroup
        val contentRect = device.screenInfo.contentRect
        screenWidth = contentRect.width()
        screenHeight = contentRect.height()
        startX = x
        startY = y
        currentX = x.coerceIn(0f, screenWidth.toFloat())
        currentY = y.coerceIn(0f, screenHeight.toFloat())
        showPointerOverlay()
        updatePointerOverlay()
        injectHoverMove(currentX, currentY, time)
        logger.d("onStart: x=$x, y=$y, screen=$screenWidth x $screenHeight, clickOnRelease=$clickOnRelease")
    }

    override fun onMove(x: Float, y: Float, time: Long) {
        val dx = (x - startX) * sensitivityScale
        val dy = (y - startY) * sensitivityScale
        currentX = (startX + dx).coerceIn(0f, screenWidth.toFloat())
        currentY = (startY + dy).coerceIn(0f, screenHeight.toFloat())
        updatePointerOverlay()
        injectHoverMove(currentX, currentY, time)
    }

    override fun onFinish(x: Float, y: Float, time: Long) {
        if (clickOnRelease) {
            injectMouseClick(currentX, currentY)
        }
        hidePointerOverlay()
        logger.d("onFinish: x=$currentX, y=$currentY")
    }

    override fun onCancel() {
        hidePointerOverlay()
        logger.d("onCancel")
    }

    private fun showPointerOverlay() {
        if (overlayShown) {
            return
        }
        overlayShown = true
        overlayScope.launch {
            pointerOverlay.show(edgeGroup = edgeGroup, x = currentX, y = currentY)
        }
    }

    private fun hidePointerOverlay() {
        if (!overlayShown) {
            return
        }
        overlayShown = false
        overlayScope.launch {
            pointerOverlay.hide()
        }
    }

    private fun updatePointerOverlay() {
        if (!overlayShown) {
            return
        }
        overlayScope.launch {
            pointerOverlay.update(
                edgeGroup = edgeGroup,
                anchorX = startX,
                anchorY = startY,
                currentX = currentX,
                currentY = currentY,
            )
        }
    }

    private fun injectHoverMove(x: Float, y: Float, eventTime: Long) {
        val pointerProperties = MotionEvent.PointerProperties().apply {
            id = 0
            toolType = MotionEvent.TOOL_TYPE_MOUSE
        }
        val pointerCoords = MotionEvent.PointerCoords().apply {
            this.x = x
            this.y = y
            orientation = 0f
            size = 1f
        }
        val event = MotionEvent.obtain(
            eventTime,
            eventTime,
            MotionEvent.ACTION_HOVER_MOVE,
            1,
            arrayOf(pointerProperties),
            arrayOf(pointerCoords),
            0,
            0,
            1f,
            1f,
            0,
            0,
            InputDevice.SOURCE_MOUSE,
            0,
        )
        Device.injectEvent(event, 0, Device.INJECT_MODE_ASYNC)
    }

    private fun injectMouseClick(x: Float, y: Float) {
        val position = Position(
            x.roundToInt(),
            y.roundToInt(),
            screenWidth,
            screenHeight,
        )
        val downOk = controller.injectTouch(
            MotionEvent.ACTION_DOWN,
            -3,
            position,
            1f,
            MotionEvent.BUTTON_PRIMARY,
        )
        val upOk = controller.injectTouch(
            MotionEvent.ACTION_UP,
            -3,
            position,
            0f,
            MotionEvent.BUTTON_PRIMARY,
        )
        logger.d("injectMouseClick: downOk=$downOk, upOk=$upOk")
    }
}