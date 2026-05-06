# Quickstart: Restore Sub-Gesture Sequence Detection

**Date**: 2026-04-30  
**Feature**: Restore Sub-Gesture Sequence Detection

## Prerequisites

- All existing components are already implemented
- `EdgeGestureSequenceDetectorTest.kt` (7 tests) should pass

## Key Files

1. **`services/.../bpok/gesture/EdgeGestureSequenceDetector.kt`** — The sequence detector (already implemented, 578 lines)
2. **`services/.../bpok/gesture/EdgeGestureSequenceHintReporter.kt`** — Hint bridge (already implemented)
3. **`services/.../bpok/gesture/EdgeGestureEngine.kt`** — The engine to modify
4. **`services/.../ShortXGestureMain.kt`** — The entry point to modify

## Implementation Steps

### Step 1: Modify ShortXGestureMain
Create `GestureHintController`, `GestureHapticController`, `EdgeGestureSequenceHintReporter`, and `EdgeGestureSequenceDetector`. Pass `sequenceDetector` to the engine.

### Step 2: Modify EdgeGestureEngine
Add `sequenceDetector` constructor parameter. In `startExecution()`, check for sequence candidates and start tracking. Forward MOVE/UP/CANCEL to the detector when tracking.

### Step 3: Verify
Run existing tests and manually test on device.

## Running Tests

```bash
# Run sequence detector tests
./gradlew :services:test --tests "tornaco.apps.shortx.services.gesture.bpok.gesture.EdgeGestureSequenceDetectorTest"

# Run all gesture tests
./gradlew :services:test --tests "tornaco.apps.shortx.services.gesture.*"
```

## Verification Checklist

- [ ] Simple swipe rules still fire (no regression)
- [ ] Sequence rules fire when child steps are completed
- [ ] Hover steps work (hold finger still for duration)
- [ ] Visual hints appear during sequence tracking
- [ ] Haptic feedback fires at appropriate moments
- [ ] Sequence cancels on wrong direction or finger lift (non-hover)
- [ ] Both simple and sequence rules fire for the same initial swipe
