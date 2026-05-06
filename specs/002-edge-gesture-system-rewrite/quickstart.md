# Quickstart: Edge Gesture System Rewrite

**Date**: 2026-04-30  
**Feature**: Edge Gesture System Rewrite

## Prerequisites

- Android Studio with Kotlin support
- Gradle build system configured for the ShortX project
- Access to `core-api` module (protobuf definitions)
- Understanding of `AI/EDGE_GESTURE_TECHNICAL_DOC.md`

## Key Files to Understand First

1. **`AI/EDGE_GESTURE_TECHNICAL_DOC.md`** — The design reference. Sections 5-9 are most critical for implementation.
2. **`core-api/src/main/proto/shortx/Facts.proto`** — Protobuf definitions for `Edge`, `Gesture`, `EdgeGesture`, `ScreenCorner` enums/messages.
3. **`services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/DetectorService.kt`** — The interface contract between detection and rule engine.
4. **`services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/Detector.kt`** — Current orchestrator to understand the event flow.
5. **`services/src/main/java/tornaco/apps/shortx/services/input/IMSFunc.kt`** — Input injection API.

## Implementation Order

Follow this sequence to get a working system at each step:

### Step 1: EdgeRegionResolver
Create `EdgeRegionResolver.kt`. Takes (rawX, rawY, screenWidth, screenHeight, edgeWidth, edgeHeight) → returns edge region ID (0-15) or -1.

Test: Input coordinates at various screen positions, verify correct region detection.

### Step 2: GestureDirectionResolver
Create `GestureDirectionResolver.kt`. Takes (startX, startY, currentX, currentY, minThreshold) → returns Direction enum.

Test: Input various swipe paths, verify dominant-axis detection.

### Step 3: GestureTimerController
Create `GestureTimerController.kt`. Manages long-press and double-tap timers with callbacks.

Test: Verify timer start/cancel/expiry callbacks.

### Step 4: GestureActionResolver
Create `GestureActionResolver.kt`. Takes (regionId, gestureType) → returns Action? with parent-edge fallback.

Test: Configure action table, verify fallback behavior.

### Step 5: EdgeGestureEngine
Create `EdgeGestureEngine.kt`. Wire together resolvers, timers, and action resolver. Implement the full state machine.

Test: Simulate touch sequences, verify state transitions and event caching/replay.

### Step 6: Wire to ShortXGestureMain
Update `ShortXGestureMain.kt` to use `EdgeGestureEngine` instead of `Detector`.

Test: End-to-end gesture detection on device.

### Step 7: Adapt EdgeGestureHintDetector
Update hint detector to receive events from new engine.

Test: Visual feedback appears during gestures.

## Running Tests

```bash
# Run all gesture tests
./gradlew :services:test --tests "tornaco.apps.shortx.services.gesture.*"

# Run specific test
./gradlew :services:test --tests "tornaco.apps.shortx.services.gesture.bpok.gesture.EdgeGestureSequenceDetectorTest"
```

## Key Design Decisions

- **Unified state machine**: One `EdgeGestureEngine` handles all gesture types instead of 4 separate detectors
- **Event caching**: Deep-copy `ACTION_DOWN`, replay on cancellation, consume on execution
- **16×7 action table**: Look up细分 region first, fall back to parent edge
- **Preserve interfaces**: `DetectorService` interface unchanged to avoid cross-binder changes
