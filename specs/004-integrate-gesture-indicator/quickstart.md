# Quickstart: Integrate Gesture Indicator for Simple Swipes

**Date**: 2026-04-30  
**Feature**: Integrate Gesture Indicator for Simple Swipes

## Prerequisites

- `EdgeGestureHintDetector` is already fully implemented (340 lines)
- `GestureHintController`, `GestureHintView`, `GestureHapticController` are already implemented
- The sequence hint path (`EdgeGestureSequenceHintReporter`) is already active

## Key Files

1. **`services/.../bpok/gesture/EdgeGestureHintDetector.kt`** — The hint detector (already implemented)
2. **`services/.../bpok/gesture/EdgeGestureEngine.kt`** — The engine to modify
3. **`services/.../ShortXGestureMain.kt`** — The entry point to modify

## Implementation Steps

### Step 1: Modify ShortXGestureMain
Create `EdgeGestureHintDetector` with `detectAreas` lambda using `EdgeRegionResolver`. Pass it to the engine.

### Step 2: Modify EdgeGestureEngine
Add `hintDetector` constructor parameter. Forward events in `handleEdgeDownStart`, `handleMove`, `handleUp`, `handleCancel`. Call `onEdgeGestureTriggered` in `startExecution`. Call `onEdgeGestureSequenceStarted` when sequence candidates exist.

### Step 3: Verify
Test on device with a simple swipe rule configured with hint enabled.

## Verification Checklist

- [ ] Touch edge region → hint dot appears
- [ ] Swipe in correct direction → hint shows progress with arrow
- [ ] Reach threshold → hint changes to "ready" state
- [ ] Release → success animation plays
- [ ] Cancel/swipe wrong direction → hint fades out
- [ ] Sequence gestures still work (hint transitions from simple to sequence)
- [ ] Existing tests pass
