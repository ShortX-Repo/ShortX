# Quickstart: Integrate Corner Swipe-to-Center Detector

**Date**: 2026-04-30  
**Feature**: Integrate Corner Swipe-to-Center Detector

## Prerequisites

- `CornerSwipeToCenterDetector` is already fully implemented (219 lines)
- `CornerSwipeToCenterGeometry` is already implemented (41 lines)
- The backend pipeline (`GestureBridge.handleCornerSwipeToCenterEvent()`) is already working

## Key Files

1. **`services/.../bpok/gesture/CornerSwipeToCenterDetector.kt`** — The corner detector (already implemented)
2. **`services/.../ShortXGestureMain.kt`** — The entry point to modify

## Implementation Steps

### Step 1: Modify ShortXGestureMain
Create `CornerSwipeToCenterDetector` with `context`, `service`, `gestureHintController`, `gestureHapticController`. In `filterMotionEvent()`, forward events to the corner detector BEFORE the edge engine.

### Step 2: Verify
Test on device with a corner swipe rule configured.

## Verification Checklist

- [ ] Touch corner region → hint appears (if enabled)
- [ ] Swipe toward center → gesture fires
- [ ] Swipe in wrong direction → gesture does NOT fire
- [ ] Touch outside corner → edge gestures still work
- [ ] Existing tests pass
