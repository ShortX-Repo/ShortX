# Quickstart: Corner Swipe Event Caching and Replay

**Date**: 2026-04-30  
**Feature**: Corner Swipe Event Caching and Replay

## Prerequisites

- `CornerSwipeToCenterDetector` is already implemented (219 lines)
- `InputInjectionBridge` and `InputInjectionBridgeImpl` are already implemented
- `EdgeGestureEngine` already uses the same pattern for event replay

## Key Files

1. **`services/.../bpok/gesture/CornerSwipeToCenterDetector.kt`** — The detector to modify
2. **`services/.../ShortXGestureMain.kt`** — Pass injectionBridge to detector

## Implementation Steps

### Step 1: Modify CornerSwipeToCenterDetector
Add `injectionBridge` constructor parameter. Add `cachedDownEvent` field. Cache on DOWN, replay on cancel, consume on success.

### Step 2: Modify ShortXGestureMain
Pass `injectionBridge` to `CornerSwipeToCenterDetector` constructor.

### Step 3: Verify
Test on device with corner swipe rule configured.

## Verification Checklist

- [ ] Touch corner, don't swipe, release → app receives tap (event replayed)
- [ ] Touch corner, swipe wrong direction → app receives tap (event replayed)
- [ ] Touch corner, swipe toward center → gesture fires (event consumed)
- [ ] Touch edge (not corner) → edge gestures still work
- [ ] Existing tests pass
