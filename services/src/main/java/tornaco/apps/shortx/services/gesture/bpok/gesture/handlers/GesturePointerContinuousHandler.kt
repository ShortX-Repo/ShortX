package tornaco.apps.shortx.services.gesture.bpok.gesture.handlers

import android.content.Context
import android.view.InputDevice
import android.view.MotionEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.action.GesturePointerDragButton
import tornaco.apps.shortx.core.proto.action.GesturePointerReleaseButton
import tornaco.apps.shortx.core.proto.action.GesturePointerScrollMode
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
    private val releaseButton: GesturePointerReleaseButton = GesturePointerReleaseButton.GesturePointerReleaseButtonPrimary,
    private val dragButton: GesturePointerDragButton = GesturePointerDragButton.GesturePointerDragButtonNone,
    private val dragHoldDurationMs: Int = 350,
    private val scrollMode: GesturePointerScrollMode = GesturePointerScrollMode.GesturePointerScrollModeNone,
    private val scrollScale: Float = 1f,
) : ContinuousActionHandler {
    companion object {
        private const val POINTER_ID = -3L
        private const val TOUCH_POINTER_ID = -4L
        private const val DEFAULT_DRAG_HOLD_MS = 350
        private const val SCROLL_STEP_PX = 48f
    }

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
    private var pointerX = 0f
    private var pointerY = 0f
    private var startX = 0f
    private var startY = 0f
    private var startTime = 0L
    private var lastVisualX = 0f
    private var lastVisualY = 0f
    private var overlayShown = false
    private var dragActive = false

    override fun onStart(edgeGroup: Int, x: Float, y: Float, time: Long) {
        this.edgeGroup = edgeGroup
        val contentRect = device.screenInfo.contentRect
        screenWidth = contentRect.width()
        screenHeight = contentRect.height()
        startX = x
        startY = y
        startTime = time
        currentX = x.coerceIn(0f, screenWidth.toFloat())
        currentY = y.coerceIn(0f, screenHeight.toFloat())
        pointerX = currentX
        pointerY = currentY
        lastVisualX = currentX
        lastVisualY = currentY
        dragActive = false
        showPointerOverlay()
        updatePointerOverlay()
        injectHoverMove(pointerX, pointerY, time)
        logger.d("onStart: x=$x, y=$y, screen=$screenWidth x $screenHeight, release=$releaseButton, drag=$dragButton, scroll=$scrollMode")
    }

    override fun onMove(x: Float, y: Float, time: Long) {
        val dx = (x - startX) * sensitivityScale
        val dy = (y - startY) * sensitivityScale
        currentX = (startX + dx).coerceIn(0f, screenWidth.toFloat())
        currentY = (startY + dy).coerceIn(0f, screenHeight.toFloat())

        if (!dragActive && dragButton != GesturePointerDragButton.GesturePointerDragButtonNone) {
            val holdMs = dragHoldDurationMs.coerceAtLeast(DEFAULT_DRAG_HOLD_MS)
            if (time - startTime >= holdMs) {
                pointerX = currentX
                pointerY = currentY
                dragActive = injectPointerAction(
                    action = MotionEvent.ACTION_DOWN,
                    x = pointerX,
                    y = pointerY,
                    pressure = 1f,
                    button = dragButton.toMotionButton(),
                )
            }
        }

        if (dragActive) {
            pointerX = currentX
            pointerY = currentY
            injectPointerAction(
                action = MotionEvent.ACTION_MOVE,
                x = pointerX,
                y = pointerY,
                pressure = 1f,
                button = dragButton.toMotionButton(),
            )
        } else if (scrollMode != GesturePointerScrollMode.GesturePointerScrollModeNone) {
            injectScroll(currentX - lastVisualX, currentY - lastVisualY)
        } else {
            pointerX = currentX
            pointerY = currentY
            injectHoverMove(pointerX, pointerY, time)
        }

        lastVisualX = currentX
        lastVisualY = currentY
        updatePointerOverlay()
    }

    override fun onFinish(x: Float, y: Float, time: Long) {
        if (dragActive) {
            injectPointerAction(
                action = MotionEvent.ACTION_UP,
                x = pointerX,
                y = pointerY,
                pressure = 0f,
                button = dragButton.toMotionButton(),
            )
            dragActive = false
        } else if (releaseButton != GesturePointerReleaseButton.GesturePointerReleaseButtonNone) {
            when (releaseButton) {
                GesturePointerReleaseButton.GesturePointerReleaseButtonTouch -> injectTouchTap(pointerX, pointerY)
                else -> injectMouseClick(pointerX, pointerY, releaseButton.toMotionButton())
            }
        }
        hidePointerOverlay()
        logger.d("onFinish: pointer=($pointerX,$pointerY)")
    }

    override fun onCancel() {
        if (dragActive) {
            injectPointerAction(
                action = MotionEvent.ACTION_UP,
                x = pointerX,
                y = pointerY,
                pressure = 0f,
                button = dragButton.toMotionButton(),
            )
            dragActive = false
        }
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

    private fun injectMouseClick(x: Float, y: Float, button: Int) {
        val position = Position(
            x.roundToInt(),
            y.roundToInt(),
            screenWidth,
            screenHeight,
        )
        val downOk = controller.injectTouch(
            MotionEvent.ACTION_DOWN,
            POINTER_ID,
            position,
            1f,
            button,
        )
        val upOk = controller.injectTouch(
            MotionEvent.ACTION_UP,
            POINTER_ID,
            position,
            0f,
            button,
        )
        logger.d("injectMouseClick: downOk=$downOk, upOk=$upOk")
    }

    private fun injectTouchTap(x: Float, y: Float) {
        val position = Position(
            x.roundToInt(),
            y.roundToInt(),
            screenWidth,
            screenHeight,
        )
        val downOk = controller.injectTouch(
            MotionEvent.ACTION_DOWN,
            TOUCH_POINTER_ID,
            position,
            1f,
            0,
        )
        val upOk = controller.injectTouch(
            MotionEvent.ACTION_UP,
            TOUCH_POINTER_ID,
            position,
            0f,
            0,
        )
        logger.d("injectTouchTap: downOk=$downOk, upOk=$upOk")
    }

    private fun injectPointerAction(action: Int, x: Float, y: Float, pressure: Float, button: Int): Boolean {
        val position = Position(
            x.roundToInt(),
            y.roundToInt(),
            screenWidth,
            screenHeight,
        )
        return controller.injectTouch(action, POINTER_ID, position, pressure, button)
    }

    private fun injectScroll(deltaX: Float, deltaY: Float) {
        if (deltaX == 0f && deltaY == 0f) {
            return
        }
        val hScroll = when (scrollMode) {
            GesturePointerScrollMode.GesturePointerScrollModeHorizontal,
            GesturePointerScrollMode.GesturePointerScrollModeBoth -> deltaX / SCROLL_STEP_PX * scrollScale

            else -> 0f
        }
        val vScroll = when (scrollMode) {
            GesturePointerScrollMode.GesturePointerScrollModeVertical,
            GesturePointerScrollMode.GesturePointerScrollModeBoth -> -deltaY / SCROLL_STEP_PX * scrollScale

            else -> 0f
        }
        if (hScroll == 0f && vScroll == 0f) {
            return
        }
        val position = Position(
            pointerX.roundToInt(),
            pointerY.roundToInt(),
            screenWidth,
            screenHeight,
        )
        controller.injectScroll(position, hScroll, vScroll, 0)
    }

    private fun GesturePointerReleaseButton.toMotionButton(): Int {
        return when (this) {
            GesturePointerReleaseButton.GesturePointerReleaseButtonPrimary -> MotionEvent.BUTTON_PRIMARY
            GesturePointerReleaseButton.GesturePointerReleaseButtonSecondary -> MotionEvent.BUTTON_SECONDARY
            else -> 0
        }
    }

    private fun GesturePointerDragButton.toMotionButton(): Int {
        return when (this) {
            GesturePointerDragButton.GesturePointerDragButtonPrimary -> MotionEvent.BUTTON_PRIMARY
            GesturePointerDragButton.GesturePointerDragButtonSecondary -> MotionEvent.BUTTON_SECONDARY
            else -> 0
        }
    }
}