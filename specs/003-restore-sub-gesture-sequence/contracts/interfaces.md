# Interface Contracts: Restore Sub-Gesture Sequence Detection

**Date**: 2026-04-30  
**Feature**: Restore Sub-Gesture Sequence Detection

## EdgeGestureEngine (modified)

The engine gains a new constructor parameter for the sequence detector. No new public methods.

```kotlin
class EdgeGestureEngine(
    private val service: DetectorService,
    private val handler: Handler,
    private val regionResolver: EdgeRegionResolver,
    private val directionResolver: GestureDirectionResolver,
    private val timerController: GestureTimerController,
    private val actionResolver: GestureActionResolver,
    private val injectionBridge: InputInjectionBridge,
    private val sequenceDetector: EdgeGestureSequenceDetector,  // NEW
) {
    // Existing public API unchanged
    fun handleTouchSequence(event: MotionEvent): Boolean
    fun cancelAndReplay()
    fun isTracking(): Boolean
    var listener: GestureEngineListener?
}
```

### Behavioral changes:

1. In `startExecution()`: After firing the simple rule, check `service.findMatchingEdgeGestureSequences()`. If candidates exist, call `sequenceDetector.startTracking()`.

2. In `handleMove()`: If `sequenceDetector.isTracking()`, forward event to `sequenceDetector.onTouchEvent()` and return true.

3. In `handleUp()`: If `sequenceDetector.isTracking()`, forward event to `sequenceDetector.onTouchEvent()` and return true (skip normal UP handling).

4. In `handleCancel()`: If `sequenceDetector.isTracking()`, call `sequenceDetector.cancelTracking()`.

5. In `handleEdgeDownStart()`: Call `sequenceDetector.cancelTracking()` before processing new touch.

## ShortXGestureMain (modified)

Creates the sequence infrastructure and injects it into the engine.

```kotlin
class ShortXGestureMain(private val context: Context) {
    val service = DetectorServiceImpl(context)
    private val detectorHandler = newHandlerOfNewThread("shortx-detector")

    // NEW: Sequence feedback infrastructure
    private val gestureHintController = GestureHintController(context)
    private val gestureHapticController = GestureHapticController(context)
    private val sequenceHintReporter = EdgeGestureSequenceHintReporter(
        gestureHintController, gestureHapticController
    )

    // NEW: Sequence detector
    private val sequenceDetector = EdgeGestureSequenceDetector(
        handler = detectorHandler,
        service = service,
        hintSink = sequenceHintReporter,
    )

    // Engine with sequence support
    val engine = EdgeGestureEngine(
        service = service,
        handler = detectorHandler,
        regionResolver = regionResolver,
        directionResolver = directionResolver,
        timerController = timerController,
        actionResolver = actionResolver,
        injectionBridge = injectionBridge,
        sequenceDetector = sequenceDetector,  // NEW
    )

    fun filterMotionEvent(event: MotionEvent): Boolean?
}
```

## Preserved Interfaces (no changes)

- `EdgeGestureSequenceDetector` — public API unchanged
- `EdgeGestureSequenceHintSink` — interface unchanged
- `EdgeGestureSequenceHintReporter` — implementation unchanged
- `DetectorService` — interface unchanged
- `DetectorServiceImpl` — implementation unchanged
