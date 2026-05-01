# Interface Contracts: Integrate Corner Swipe-to-Center Detector

**Date**: 2026-04-30  
**Feature**: Integrate Corner Swipe-to-Center Detector

## ShortXGestureMain (modified)

The entry point gains a corner swipe detector and forwards events before the edge engine.

```kotlin
class ShortXGestureMain(private val context: Context) {
    // Existing: service, gestureHintController, gestureHapticController, engine

    // NEW: corner swipe detector
    private val cornerSwipeDetector = CornerSwipeToCenterDetector(
        context = context,
        bridge = service,
        gestureHintController = gestureHintController,
        gestureHapticController = gestureHapticController,
    )

    fun filterMotionEvent(event: MotionEvent): Boolean?
}
```

### Behavioral changes:

1. In `filterMotionEvent()`: Call `cornerSwipeDetector.onTouchEvent(event)` BEFORE `engine.handleTouchSequence(event)`.
2. If corner detector consumes the event, return `false` (consumed) and skip the edge engine.
3. If corner detector does NOT consume the event, proceed with normal edge engine processing.

## Preserved Interfaces (no changes)

- `CornerSwipeToCenterDetector` — public API unchanged
- `CornerSwipeToCenterGeometry` — utility unchanged
- `GestureBridge` — interface unchanged
- `GestureBridgeImpl` — implementation unchanged
- `EdgeGestureEngine` — no changes needed
