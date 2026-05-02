# Interface Contracts: Continuous Adjustment Actions

**Date**: 2026-04-30  
**Feature**: Continuous Adjustment Actions

## GestureBridge (modified)

The bridge gains a method for creating continuous action handlers.

```kotlin
interface GestureBridge {
    // Existing methods...
    
    fun createContinuousActionHandler(actionType: String): ContinuousActionHandler?
}
```

## EdgeGestureEngine.startExecution() (modified)

The engine detects continuous action types and creates handlers.

```kotlin
private fun startExecution(action: Any?, direction: Direction, eventTime: Long, currentX: Float, currentY: Float) {
    val gesture = directionToGesture(direction)
    val areas = listOf(DetectedArea.PredefinedEdge(hitRegion))
    
    val sequenceCandidates = bridge.findMatchingEdgeGestureSequences(areas, gesture)
    if (action == null && sequenceCandidates.isEmpty()) {
        cancelAndReplay()
        return
    }
    
    executionStarted = true
    consumeCachedEvent()
    
    // NEW: Check if action is continuous
    val actionType = resolveContinuousActionType(action)
    if (actionType != null) {
        continuousHandler = bridge.createContinuousActionHandler(actionType)
        continuousHandler?.onStart(edgeGroup(hitRegion), downX, downY, eventTime)
        return
    }
    
    // Existing one-shot logic...
    if (action != null) {
        listener?.onGestureTriggered(hitRegion, gestureToGestureType(direction) ?: GestureType.SwipeLeft)
        bridge.handleEdgeGestureEvent(areas, gesture, downX, downY)
        hintDetector.onEdgeGestureTriggered(areas, gesture, true)
    }
    
    // Existing sequence logic...
}

private fun resolveContinuousActionType(action: Any?): String? {
    return when (action) {
        is ContinuousAdjustBrightness -> "brightness"
        is ContinuousAdjustVolume -> "volume"
        else -> null
    }
}
```

## BrightnessContinuousHandler (modified)

Real brightness control implementation.

```kotlin
class BrightnessContinuousHandler(
    private val context: Context,
    private val sensitivityScale: Float = 1f,
    private val minValue: Int = 0,
    private val maxValue: Int = 255,
) : ContinuousActionHandler {
    private var startY = 0f
    private var startBrightness = 0
    private var autoBrightnessWasEnabled = false
    
    override fun onStart(edgeGroup: Int, x: Float, y: Float, time: Long) {
        startY = y
        startBrightness = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
        autoBrightnessWasEnabled = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
        if (autoBrightnessWasEnabled) {
            Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)
        }
    }
    
    override fun onMove(x: Float, y: Float, time: Long) {
        val dy = y - startY
        val delta = (-dy * sensitivityScale * (maxValue - minValue) / 1000f).toInt()
        val newBrightness = (startBrightness + delta).coerceIn(minValue, maxValue)
        Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, newBrightness)
    }
    
    override fun onFinish(x: Float, y: Float, time: Long) {
        // Final value already set by last onMove
    }
    
    override fun onCancel() {
        Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, startBrightness)
        if (autoBrightnessWasEnabled) {
            Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC)
        }
    }
}
```

## VolumeContinuousHandler (modified)

Real volume control implementation.

```kotlin
class VolumeContinuousHandler(
    private val context: Context,
    private val streamType: Int = AudioManager.STREAM_MUSIC,
    private val sensitivityScale: Float = 1f,
) : ContinuousActionHandler {
    private var startY = 0f
    private var startVolume = 0
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val maxVolume = audioManager.getStreamMaxVolume(streamType)
    
    override fun onStart(edgeGroup: Int, x: Float, y: Float, time: Long) {
        startY = y
        startVolume = audioManager.getStreamVolume(streamType)
    }
    
    override fun onMove(x: Float, y: Float, time: Long) {
        val dy = y - startY
        val delta = (-dy * sensitivityScale * maxVolume / 1000f).toInt()
        val newVolume = (startVolume + delta).coerceIn(0, maxVolume)
        audioManager.setStreamVolume(streamType, newVolume, 0)
    }
    
    override fun onFinish(x: Float, y: Float, time: Long) {
        // Final value already set by last onMove
    }
    
    override fun onCancel() {
        audioManager.setStreamVolume(streamType, startVolume, 0)
    }
}
```

## ContinuousAdjustOverlayView (new)

System overlay for adjustment feedback.

```kotlin
class ContinuousAdjustOverlayView(context: Context) : View(context) {
    fun show(type: AdjustType, initialValue: Int, maxValue: Int)
    fun updateValue(value: Int)
    fun dismiss()
}

enum class AdjustType {
    BRIGHTNESS, VOLUME
}
```

## Preserved Interfaces (no changes)

- `ContinuousActionHandler` — interface already correct
- `GestureHintController` — no changes needed
- `GestureHintView` — no changes needed
