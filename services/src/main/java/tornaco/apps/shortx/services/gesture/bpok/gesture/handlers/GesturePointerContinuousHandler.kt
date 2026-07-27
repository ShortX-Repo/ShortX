package tornaco.apps.shortx.services.gesture.bpok.gesture.handlers

import android.content.Context
import android.view.InputDevice
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import android.os.SystemClock
import tornaco.apps.shortx.core.proto.action.GesturePointerCenterSwitchMode
import tornaco.apps.shortx.core.proto.action.GesturePointerDragButton
import tornaco.apps.shortx.core.proto.action.GesturePointerReleaseButton
import tornaco.apps.shortx.core.proto.action.GesturePointerScrollMode
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ctrl.Controller
import tornaco.apps.shortx.services.ctrl.Device
import tornaco.apps.shortx.services.ctrl.Options
import tornaco.apps.shortx.services.ctrl.Position
import tornaco.apps.shortx.services.gesture.bpok.gesture.ContinuousActionHandler
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.services.ui.GesturePointerOverlay
import tornaco.apps.shortx.services.ui.GesturePointerVisualMode
import kotlin.math.hypot
import kotlin.math.roundToInt
import tornaco.apps.shortx.core.i18n.I18N_R

class GesturePointerContinuousHandler(
    private val context: Context,
    private val sensitivityScale: Float = 1f,
    private val releaseButton: GesturePointerReleaseButton = GesturePointerReleaseButton.GesturePointerReleaseButtonPrimary,
    private val dragButton: GesturePointerDragButton = GesturePointerDragButton.GesturePointerDragButtonNone,
    private val dragHoldDurationMs: Int = 350,
    private val scrollMode: GesturePointerScrollMode = GesturePointerScrollMode.GesturePointerScrollModeNone,
    private val scrollScale: Float = 1f,
    private val cancelRegionRadiusDp: Int = 48,
    private val enableCenterActionSwitch: Boolean = false,
    private val centerSwitchModes: List<GesturePointerCenterSwitchMode> = emptyList(),
) : ContinuousActionHandler {
    companion object {
        private const val POINTER_ID = -3L
        private const val TOUCH_POINTER_ID = -4L
        private const val DEFAULT_DRAG_HOLD_MS = 350
        private const val SCROLL_STEP_PX = 48f
        private const val CENTER_SWITCH_HYSTERESIS_DP = 8f
        private const val LONG_PRESS_SETTLE_MS = 120L
    }

    private enum class PointerMode {
        SingleClick,
        LongPress,
        DoubleClick,
        Drag,
        Scroll,
    }

    private val logger = Logger("GesturePointerHandler")
    private val device by lazy { Device(Options()) }
    private val controller by lazy { Controller(device) }
    private val pointerOverlay = GesturePointerOverlay(context)
    private val overlayScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val cancelRegionRadiusPx = cancelRegionRadiusDp.coerceIn(16, 160) * context.resources.displayMetrics.density
    private val centerSwitchHysteresisPx = CENTER_SWITCH_HYSTERESIS_DP * context.resources.displayMetrics.density
    private val longPressDurationMs = ViewConfiguration.getLongPressTimeout().toLong().coerceAtLeast(DEFAULT_DRAG_HOLD_MS.toLong())
    private val doubleTapIntervalMs = 96L

    private var edgeGroup = -1
    private var screenWidth = 0
    private var screenHeight = 0
    private var currentX = 0f
    private var currentY = 0f
    private var pointerX = 0f
    private var pointerY = 0f
    private var fingerX = 0f
    private var fingerY = 0f
    private var startX = 0f
    private var startY = 0f
    private var startTime = 0L
    private var lastVisualX = 0f
    private var lastVisualY = 0f
    private var overlayShown = false
    private var dragActive = false
    private var pointerMode = PointerMode.SingleClick
    private var pointerModeIndex = 0
    private val hasCenterSwitchModes = centerSwitchModes.isNotEmpty()
    private val isDragViaCenter = centerSwitchModes.any { it == GesturePointerCenterSwitchMode.GesturePointerCenterSwitchModeDrag }
    private val isScrollViaCenter = centerSwitchModes.any { it == GesturePointerCenterSwitchMode.GesturePointerCenterSwitchModeScroll }
    private var hasExitedCancelRegion = false
    private var switchArmed = false
    private var dragModeEnteredTime = 0L

    override fun onStart(edgeGroup: Int, x: Float, y: Float, time: Long) {
        this.edgeGroup = edgeGroup
        val contentRect = device.screenInfo.contentRect
        screenWidth = contentRect.width()
        screenHeight = contentRect.height()
        startX = x
        startY = y
        fingerX = x
        fingerY = y
        startTime = time
        currentX = x.coerceIn(0f, screenWidth.toFloat())
        currentY = y.coerceIn(0f, screenHeight.toFloat())
        pointerX = currentX
        pointerY = currentY
        lastVisualX = currentX
        lastVisualY = currentY
        dragActive = false
        pointerMode = if (hasCenterSwitchModes) centerSwitchModes.first().toPointerMode() else PointerMode.SingleClick
        pointerModeIndex = 0
        dragModeEnteredTime = time
        hasExitedCancelRegion = false
        switchArmed = false
        showPointerOverlay()
        updatePointerOverlay()
        injectHoverMove(pointerX, pointerY, time)
        logger.d("onStart: x=$x, y=$y, screen=$screenWidth x $screenHeight, release=$releaseButton, drag=$dragButton, scroll=$scrollMode")
    }

    override fun onMove(x: Float, y: Float, time: Long) {
        fingerX = x
        fingerY = y
        updateCenterActionSwitchState()
        val dx = (x - startX) * sensitivityScale
        val dy = (y - startY) * sensitivityScale
        currentX = (startX + dx).coerceIn(0f, screenWidth.toFloat())
        currentY = (startY + dy).coerceIn(0f, screenHeight.toFloat())

        if (pointerMode == PointerMode.Drag) {
            val holdMs = dragHoldDurationMs.coerceAtLeast(DEFAULT_DRAG_HOLD_MS)
            if (!dragActive && time - dragModeEnteredTime >= holdMs) {
                pointerX = currentX
                pointerY = currentY
                dragActive = injectPointerAction(
                    action = MotionEvent.ACTION_DOWN,
                    x = pointerX,
                    y = pointerY,
                    pressure = 1f,
                    button = dragButton.toMotionButton(),
                )
                if (dragActive) {
                    showDragStartedToast()
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
            } else {
                pointerX = currentX
                pointerY = currentY
                injectHoverMove(pointerX, pointerY, time)
            }
        } else if (pointerMode == PointerMode.Scroll) {
            injectScroll(currentX - lastVisualX, currentY - lastVisualY)
        } else {
            if (!isDragViaCenter && !dragActive && dragButton != GesturePointerDragButton.GesturePointerDragButtonNone) {
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
            } else if (!isScrollViaCenter && scrollMode != GesturePointerScrollMode.GesturePointerScrollModeNone) {
                injectScroll(currentX - lastVisualX, currentY - lastVisualY)
            } else {
                pointerX = currentX
                pointerY = currentY
                injectHoverMove(pointerX, pointerY, time)
            }
        }

        lastVisualX = currentX
        lastVisualY = currentY
        updatePointerOverlay()
    }

    override fun onFinish(x: Float, y: Float, time: Long) {
        fingerX = x
        fingerY = y
        val shouldCancel = isInCancelRegion(fingerX, fingerY)
        if (dragActive) {
            injectPointerAction(
                action = MotionEvent.ACTION_UP,
                x = pointerX,
                y = pointerY,
                pressure = 0f,
                button = dragButton.toMotionButton(),
            )
            dragActive = false
        } else if (!shouldCancel && pointerMode != PointerMode.Drag && pointerMode != PointerMode.Scroll
            && (hasCenterSwitchModes || releaseButton != GesturePointerReleaseButton.GesturePointerReleaseButtonNone)) {
            performReleaseAction(pointerX, pointerY)
        }
        hidePointerOverlay()
        logger.d("onFinish: pointer=($pointerX,$pointerY), shouldCancel=$shouldCancel, pointerMode=$pointerMode")
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
            pointerOverlay.show(
                edgeGroup = edgeGroup,
                x = currentX,
                y = currentY,
                pointerMode = pointerMode.toVisualMode(),
            )
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
                cancelRegionRadius = cancelRegionRadiusPx,
                isInCancelRegion = isInCancelRegion(fingerX, fingerY),
                pointerMode = pointerMode.toVisualMode(),
            )
        }
    }

    private fun isInCancelRegion(x: Float, y: Float): Boolean {
        return hypot(x - startX, y - startY) <= cancelRegionRadiusPx
    }

    private fun updateCenterActionSwitchState() {
        if (!hasCenterSwitchModes) return

        val distance = hypot(fingerX - startX, fingerY - startY)
        val isOutsideSwitchRegion = distance >= cancelRegionRadiusPx + centerSwitchHysteresisPx

        if (!hasExitedCancelRegion) {
            if (isOutsideSwitchRegion) {
                hasExitedCancelRegion = true
            }
            return
        }

        if (!switchArmed && isInCancelRegion(fingerX, fingerY)) {
            switchArmed = true
            return
        }

        if (switchArmed && isOutsideSwitchRegion) {
            val previousMode = pointerMode
            pointerModeIndex = (pointerModeIndex + 1) % centerSwitchModes.size
            pointerMode = centerSwitchModes[pointerModeIndex].toPointerMode()
            switchArmed = false
            if (pointerMode == PointerMode.Drag) {
                dragModeEnteredTime = SystemClock.uptimeMillis()
                showDragAimingToast()
            }
            if (previousMode == PointerMode.Drag && dragActive) {
                injectPointerAction(
                    action = MotionEvent.ACTION_UP,
                    x = pointerX,
                    y = pointerY,
                    pressure = 0f,
                    button = dragButton.toMotionButton(),
                )
                dragActive = false
            }
            logger.d("Center action switch -> $pointerMode")
            showModeSwitchToast(pointerMode)
        }
    }

    private fun showModeSwitchToast(mode: PointerMode) {
        overlayScope.launch {
            Toast.makeText(
                context,
                i18NIns[I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_CENTER_SWITCH_SHORT] + ": " + mode.label(),
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    private fun showDragAimingToast() {
        overlayScope.launch {
            Toast.makeText(
                context,
                i18NIns[I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_MODE_DRAG_AIMING],
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    private fun showDragStartedToast() {
        overlayScope.launch {
            Toast.makeText(
                context,
                i18NIns[I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_MODE_DRAG_STARTED],
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    private fun performReleaseAction(x: Float, y: Float) {
        when (pointerMode) {
            PointerMode.SingleClick -> performSingleReleaseAction(x, y)
            PointerMode.LongPress -> performLongPressReleaseAction(x, y)
            PointerMode.DoubleClick -> performDoubleReleaseAction(x, y)
            PointerMode.Drag, PointerMode.Scroll -> Unit
        }
    }

    private fun performSingleReleaseAction(x: Float, y: Float) {
        if (hasCenterSwitchModes) {
            injectTouchTap(x, y)
            return
        }
        when (releaseButton) {
            GesturePointerReleaseButton.GesturePointerReleaseButtonTouch -> injectTouchTap(x, y)
            else -> injectMouseClick(x, y, releaseButton.toMotionButton())
        }
    }

    private fun performLongPressReleaseAction(x: Float, y: Float) {
        if (hasCenterSwitchModes) {
            injectTouchLongPress(x, y)
            return
        }
        when (releaseButton) {
            GesturePointerReleaseButton.GesturePointerReleaseButtonTouch -> injectTouchLongPress(x, y)
            else -> injectMouseLongPress(x, y, releaseButton.toMotionButton())
        }
    }

    private fun performDoubleReleaseAction(x: Float, y: Float) {
        if (hasCenterSwitchModes) {
            injectTouchDoubleTap(x, y)
            return
        }
        when (releaseButton) {
            GesturePointerReleaseButton.GesturePointerReleaseButtonTouch -> injectTouchDoubleTap(x, y)
            else -> injectMouseDoubleClick(x, y, releaseButton.toMotionButton())
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
        injectTouchPress(x, y, 0L)
    }

    private fun injectTouchLongPress(x: Float, y: Float) {
        injectTouchPress(x, y, longPressDurationMs)
    }

    private fun injectTouchDoubleTap(x: Float, y: Float) {
        injectTouchTap(x, y)
        SystemClock.sleep(doubleTapIntervalMs)
        injectTouchTap(x, y)
    }

    private fun injectTouchPress(x: Float, y: Float, holdDurationMs: Long) {
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
        if (holdDurationMs > 0) {
            val firstHoldMs = longPressDurationMs.coerceAtMost(holdDurationMs)
            SystemClock.sleep(firstHoldMs)
            injectTouchMove(position, 1f)
            val remainingHoldMs = holdDurationMs - firstHoldMs
            if (remainingHoldMs > 0) {
                SystemClock.sleep(remainingHoldMs)
            }
            SystemClock.sleep(LONG_PRESS_SETTLE_MS)
        }
        val upOk = controller.injectTouch(
            MotionEvent.ACTION_UP,
            TOUCH_POINTER_ID,
            position,
            0f,
            0,
        )
        logger.d("injectTouchPress: downOk=$downOk, upOk=$upOk, holdDurationMs=$holdDurationMs")
    }

    private fun injectTouchMove(position: Position, pressure: Float) {
        controller.injectTouch(
            MotionEvent.ACTION_MOVE,
            TOUCH_POINTER_ID,
            position,
            pressure,
            0,
        )
    }

    private fun injectMouseLongPress(x: Float, y: Float, button: Int) {
        injectMousePress(x, y, button, longPressDurationMs)
    }

    private fun injectMouseDoubleClick(x: Float, y: Float, button: Int) {
        injectMouseClick(x, y, button)
        SystemClock.sleep(doubleTapIntervalMs)
        injectMouseClick(x, y, button)
    }

    private fun injectMousePress(x: Float, y: Float, button: Int, holdDurationMs: Long) {
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
        if (holdDurationMs > 0) {
            SystemClock.sleep(holdDurationMs)
        }
        val upOk = controller.injectTouch(
            MotionEvent.ACTION_UP,
            POINTER_ID,
            position,
            0f,
            button,
        )
        logger.d("injectMousePress: downOk=$downOk, upOk=$upOk, holdDurationMs=$holdDurationMs")
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

    private fun PointerMode.toVisualMode(): GesturePointerVisualMode {
        return when (this) {
            PointerMode.SingleClick -> GesturePointerVisualMode.SingleClick
            PointerMode.LongPress -> GesturePointerVisualMode.LongPress
            PointerMode.DoubleClick -> GesturePointerVisualMode.DoubleClick
            PointerMode.Drag -> GesturePointerVisualMode.Drag
            PointerMode.Scroll -> GesturePointerVisualMode.Scroll
        }
    }

    private fun PointerMode.label(): String {
        return when (this) {
            PointerMode.SingleClick -> i18NIns[I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_MODE_SINGLE]
            PointerMode.LongPress -> i18NIns[I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_MODE_LONG]
            PointerMode.DoubleClick -> i18NIns[I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_MODE_DOUBLE]
            PointerMode.Drag -> i18NIns[I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_MODE_DRAG]
            PointerMode.Scroll -> i18NIns[I18N_R.UI_ACTION_CONTINUOUS_GESTURE_POINTER_MODE_SCROLL]
        }
    }

    private fun GesturePointerCenterSwitchMode.toPointerMode(): PointerMode {
        return when (this) {
            GesturePointerCenterSwitchMode.GesturePointerCenterSwitchModeSingleClick -> PointerMode.SingleClick
            GesturePointerCenterSwitchMode.GesturePointerCenterSwitchModeLongPress -> PointerMode.LongPress
            GesturePointerCenterSwitchMode.GesturePointerCenterSwitchModeDoubleClick -> PointerMode.DoubleClick
            GesturePointerCenterSwitchMode.GesturePointerCenterSwitchModeDrag -> PointerMode.Drag
            GesturePointerCenterSwitchMode.GesturePointerCenterSwitchModeScroll -> PointerMode.Scroll
            else -> PointerMode.SingleClick
        }
    }
}
