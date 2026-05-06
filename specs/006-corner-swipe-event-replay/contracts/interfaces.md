# Interface Contracts: Corner Swipe Event Caching and Replay

**Date**: 2026-04-30  
**Feature**: Corner Swipe Event Caching and Replay

## CornerSwipeToCenterDetector (modified)

The detector gains an `InputInjectionBridge` dependency and event caching/replay.

```kotlin
class CornerSwipeToCenterDetector(
    private val context: Context,
    private val bridge: GestureBridge,
    private val gestureHintController: GestureHintController,
    private val gestureHapticController: GestureHapticController,
    private val injectionBridge: InputInjectionBridge,  // NEW
) {
    private var cachedDownEvent: MotionEvent? = null  // NEW

    fun onTouchEvent(event: MotionEvent): Boolean
}
```

### Behavioral changes:

1. `ACTION_DOWN`: When corner is detected, cache event via `MotionEvent.obtain(event)`, return `true`.
2. `ACTION_UP/CANCEL/POINTER_UP`: When not triggered, replay cached event via `injectionBridge.injectEvent(copy)`.
3. When triggered: Recycle cached event (consume, don't replay).
4. When no corner detected: Return `false` (pass through to edge engine).

## ShortXGestureMain (modified)

Pass `injectionBridge` to corner detector constructor.

```kotlin
private val cornerSwipeDetector = CornerSwipeToCenterDetector(
    context = context,
    bridge = service,
    gestureHintController = gestureHintController,
    gestureHapticController = gestureHapticController,
    injectionBridge = injectionBridge,  // NEW
)
```

## Preserved Interfaces (no changes)

- `InputInjectionBridge` — interface unchanged
- `InputInjectionBridgeImpl` — implementation unchanged
- `GestureBridge` — interface unchanged
- `EdgeGestureEngine` — no changes needed
