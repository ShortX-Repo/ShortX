# Quickstart: Continuous Adjustment Actions

**Date**: 2026-04-30  
**Feature**: Continuous Adjustment Actions

## Prerequisites

- `ContinuousActionHandler` interface already defined
- `BrightnessContinuousHandler` and `VolumeContinuousHandler` exist as skeletons
- `GestureBridgeImpl.createContinuousActionHandler()` factory exists
- `EdgeGestureEngine` has continuous handler lifecycle code
- One-shot `SetBrightness` and `SetVolume` actors exist for reference

## Key Files

1. **`core-api/.../proto/shortx/Actions.proto`** — Add proto messages
2. **`services/.../gesture/bpok/gesture/EdgeGestureEngine.kt`** — Detect continuous actions
3. **`services/.../gesture/bpok/gesture/GestureBridgeImpl.kt`** — Implement factory
4. **`services/.../gesture/bpok/gesture/handlers/BrightnessContinuousHandler.kt`** — Real brightness control
5. **`services/.../gesture/bpok/gesture/handlers/VolumeContinuousHandler.kt`** — Real volume control
6. **`services-ui/.../overlay/ContinuousAdjustOverlayView.kt`** — Visual feedback overlay

## Implementation Steps

### Step 1: Define Proto Messages
Add `ContinuousAdjustBrightness` and `ContinuousAdjustVolume` to Actions.proto.

### Step 2: Implement Real Brightness Control
Modify `BrightnessContinuousHandler` to read/write `Settings.System.SCREEN_BRIGHTNESS`.

### Step 3: Implement Real Volume Control
Modify `VolumeContinuousHandler` to use `AudioManager.setStreamVolume`.

### Step 4: Wire Detection in Engine
Modify `EdgeGestureEngine.startExecution()` to detect continuous action types.

### Step 5: Create Visual Feedback Overlay
Create `ContinuousAdjustOverlayView` for showing adjustment level.

### Step 6: Add Rule Editor UI
Create selectors for configuring continuous actions.

## Verification Checklist

- [ ] Proto messages compile without errors
- [ ] Brightness changes in real-time during gesture
- [ ] Volume changes in real-time during gesture
- [ ] Cancelled gesture reverts to original value
- [ ] Auto-brightness disabled during manual adjustment
- [ ] Visual overlay shows current level
- [ ] Rule editor allows configuring continuous actions

## Running Tests

```bash
./gradlew :services:test --tests "tornaco.apps.shortx.services.gesture.*"
```
