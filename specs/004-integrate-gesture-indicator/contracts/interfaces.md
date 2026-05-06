# Interface Contracts: Integrate Gesture Indicator for Simple Swipes

**Date**: 2026-04-30  
**Feature**: Integrate Gesture Indicator for Simple Swipes

## EdgeGestureEngine (modified)

The engine gains a new constructor parameter for the hint detector. No new public methods.

```kotlin
class EdgeGestureEngine(
    private val service: DetectorService,
    private val handler: Handler,
    private val regionResolver: EdgeRegionResolver,
    private val directionResolver: GestureDirectionResolver,
    private val timerController: GestureTimerController,
    private val actionResolver: GestureActionResolver,
    private val injectionBridge: InputInjectionBridge,
    private val sequenceDetector: EdgeGestureSequenceDetector,
    private val hintDetector: EdgeGestureHintDetector,  // NEW
) {
    // Existing public API unchanged
    fun handleTouchSequence(event: MotionEvent): Boolean
    fun cancelAndReplay()
    fun isTracking(): Boolean
    var listener: GestureEngineListener?
}
```

### Behavioral changes:

1. In `handleEdgeDownStart()`: Forward event to `hintDetector.onTouchEvent(event)` before processing.

2. In `handleMove()`: Forward event to `hintDetector.onTouchEvent(event)` before processing.

3. In `handleUp()`: Forward event to `hintDetector.onTouchEvent(event)` before processing.

4. In `handleCancel()`: Forward event to `hintDetector.onTouchEvent(event)` before processing.

5. In `startExecution()`: After firing simple rule, call `hintDetector.onEdgeGestureTriggered(areas, gesture, true)`.

6. In `startExecution()`: When sequence candidates exist, call `hintDetector.onEdgeGestureSequenceStarted(areas, gesture)`.

## ShortXGestureMain (modified)

Creates the hint detector and injects it into the engine.

```kotlin
class ShortXGestureMain(private val context: Context) {
    // Existing: gestureHintController, gestureHapticController, regionResolver

    // NEW: hint detector
    private val hintDetector = EdgeGestureHintDetector(
        service = service,
        gestureHintController = gestureHintController,
        gestureHapticController = gestureHapticController,
        detectAreas = { x, y ->
            val edge = regionResolver.resolve(x, y)
            if (edge != Edge.UNRECOGNIZED) {
                listOf(DetectedArea.PredefinedEdge(edge))
            } else {
                emptyList()
            }
        },
    )

    // Engine with hint support
    val engine = EdgeGestureEngine(
        // ... existing params ...
        hintDetector = hintDetector,
    )
}
```

## Preserved Interfaces (no changes)

- `EdgeGestureHintDetector` — public API unchanged
- `GestureHintController` — implementation unchanged
- `GestureHapticController` — implementation unchanged
- `GestureHintModel` — data class unchanged
- `GestureHintState` — enum unchanged
