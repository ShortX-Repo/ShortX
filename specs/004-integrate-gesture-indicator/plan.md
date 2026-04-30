# Implementation Plan: Integrate Gesture Indicator for Simple Swipes

**Branch**: `feature/004-integrate-gesture-indicator` | **Date**: 2026-04-30 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/004-integrate-gesture-indicator/spec.md`

## Summary

Wire the existing `EdgeGestureHintDetector` into the new `EdgeGestureEngine` to provide visual and haptic feedback for simple (non-sequence) edge swipes. The hint detector is fully implemented (340 lines) but was never instantiated after the refactor from `Detector.kt` to `EdgeGestureEngine.kt`.

Key changes:
- Create `EdgeGestureHintDetector` instance in `ShortXGestureMain`
- Forward touch events from the engine to the hint detector
- Call `onEdgeGestureTriggered()` when a simple gesture fires
- Call `onEdgeGestureSequenceStarted()` when a sequence starts (to cancel the simple hint)

## Technical Context

**Language/Version**: Kotlin (primary), targeting JVM/Android  
**Primary Dependencies**: Android SDK (MotionEvent, WindowManager, VibrationEffect)  
**Storage**: N/A  
**Testing**: N/A (existing components, integration only)  
**Target Platform**: Android 10+ (API 29+)  
**Project Type**: Android system service module  
**Performance Goals**: No perceptible latency added to gesture recognition  
**Constraints**: Must not interfere with sequence hint reporter  
**Scale/Scope**: ~2 files modified, ~30 lines of new integration code

## Constitution Check

The project constitution is a template (not filled in). No gates to evaluate.

## Project Structure

### Source Code

```text
services/src/main/java/tornaco/apps/shortx/services/gesture/
├── ShortXGestureMain.kt                      # MODIFY: Create hint detector, wire to engine
└── bpok/gesture/
    ├── EdgeGestureEngine.kt                  # MODIFY: Forward events to hint detector
    ├── EdgeGestureHintDetector.kt            # PRESERVE: Already implemented
    ├── GestureHintController.kt              # PRESERVE: Already implemented
    ├── GestureHapticController.kt            # PRESERVE: Already implemented
    └── GestureHintModel.kt                   # PRESERVE: Already implemented
```

## Implementation

### Step 1: Create hint detector in ShortXGestureMain

Create `EdgeGestureHintDetector` with:
- `service` — the existing `DetectorServiceImpl`
- `gestureHintController` — already created
- `gestureHapticController` — already created
- `detectAreas` — lambda that uses `EdgeRegionResolver` to resolve edge and wrap in list

### Step 2: Forward events from engine to hint detector

The engine needs a reference to the hint detector. Add it as a constructor parameter. In `handleEdgeDownStart()`, `handleMove()`, `handleUp()`, and `handleCancel()`, forward events to the hint detector.

### Step 3: Call onEdgeGestureTriggered when gesture fires

In `startExecution()` (when a simple rule fires), call `hintDetector.onEdgeGestureTriggered()` so the hint transitions to success state.

### Step 4: Call onEdgeGestureSequenceStarted when sequence starts

In `startExecution()` (when sequence candidates exist), call `hintDetector.onEdgeGestureSequenceStarted()` so the simple hint is cancelled.

## Research

### Decision: Where to forward events

**Decision**: Forward events in the engine's handler methods, BEFORE the engine's own logic.

**Rationale**: The hint detector needs to see DOWN events to show the initial candidate indicator. If we forward AFTER the engine processes, the engine might cancel/replay before the hint detector sees the event.

**Alternative**: Forward in `ShortXGestureMain.filterMotionEvent()` — rejected because the hint detector should be co-located with the engine, not in the service layer.

### Decision: detectAreas implementation

**Decision**: Use `EdgeRegionResolver.resolve()` and wrap the result in a list.

**Rationale**: The hint detector expects `detectAreas: (Float, Float) -> List<DetectedArea>`. The `EdgeRegionResolver` returns a single `Edge`. Wrapping in `listOf(DetectedArea.PredefinedEdge(edge))` is sufficient.

**Alternative**: Duplicate the area detection logic — rejected because `EdgeRegionResolver` already handles this.

## Contracts

### EdgeGestureEngine (modified)

```kotlin
class EdgeGestureEngine(
    // ... existing params ...
    private val hintDetector: EdgeGestureHintDetector,  // NEW
) {
    // No new public methods
    // handleTouchSequence() now forwards events to hintDetector
}
```

### ShortXGestureMain (modified)

```kotlin
class ShortXGestureMain(private val context: Context) {
    // Existing: gestureHintController, gestureHapticController
    
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

    // Pass hintDetector to engine
    val engine = EdgeGestureEngine(
        // ... existing params ...
        hintDetector = hintDetector,
    )
}
```

## Quickstart

### Verification
1. Configure a simple swipe rule with hint enabled
2. Touch the edge region — observe the hint dot appears
3. Swipe in the configured direction — observe the hint shows progress
4. Reach the threshold — observe the "ready" state
5. Release — observe the success animation
6. Run existing tests — all should pass

### Running Tests
```bash
./gradlew :services:test --tests "tornaco.apps.shortx.services.gesture.*"
```
