# Implementation Plan: Edge Gesture System Rewrite

**Branch**: `feature/002-edge-gesture-system-rewrite` | **Date**: 2026-04-30 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/002-edge-gesture-system-rewrite/spec.md`

## Summary

Rewrite the ShortX edge gesture detection system to match the consolidated runtime description in `AI/EDGE_GESTURE_OVERVIEW.html`. The current system uses a `Detector` orchestrator with multiple sub-detectors (`SystemGesturesPointerEventListener`, `EdgeGestureSequenceDetector`, `CornerSwipeToCenterDetector`, Android `GestureDetector`). The new system will replace this with a unified state machine (`EdgeGestureEngine`) that intercepts MotionEvents at the system input filter level, implements proper event caching/replay, supports a 16×7 action table with parent-edge fallback, and handles continuous adjustment actions and sub-gesture containers.

Key architectural changes:
- Replace `SystemGesturesPointerEventListener` + `Detector` with a single `EdgeGestureEngine` state machine
- Add event caching and replay (current system lacks this — cancelled gestures lose the down event)
- Implement the 16-region × 7-slot action table with parent-edge fallback
- Add continuous action handler lifecycle (start/move/end/cancel)
- Preserve existing `DetectorService` interface boundary, visual feedback, and recording/playback

## Technical Context

**Language/Version**: Kotlin (primary), Java (legacy interop), targeting JVM/Android  
**Primary Dependencies**: Android SDK (InputManagerService, WindowManager, VibrationEffect), Protobuf (core-api Facts.proto), Xposed framework  
**Storage**: Protobuf-based configuration via `ShortXManager` (AIDL binder service)  
**Testing**: JUnit 5 + Kotest + MockK (see existing `EdgeGestureSequenceDetectorTest.kt`)  
**Target Platform**: Android 10+ (API 29+), with version-compatible input injection APIs  
**Project Type**: Android system service module (part of ShortX Xposed module)  
**Performance Goals**: <16ms gesture recognition latency (one frame at 60fps), zero false positives on normal edge touches  
**Constraints**: Must run in privileged system context; must not break existing rule engine integration via `DetectorService` interface  
**Scale/Scope**: 16 edge regions × 7 gesture slots = 112 action bindings; supports ~20 concurrent gesture configurations

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

The project constitution is a template (not filled in). No gates to evaluate. Proceeding with standard best practices.

## Project Structure

### Documentation (this feature)

```text
specs/002-edge-gesture-system-rewrite/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output
└── tasks.md             # Phase 2 output (/speckit.tasks command)
```

### Source Code (repository root)

```text
services/src/main/java/tornaco/apps/shortx/services/gesture/
├── ShortXGestureMain.kt              # Entry point (preserve, adapt wiring)
├── GestureService.kt                 # System service facade (preserve, adapt settings)
├── GestureRecorder.kt                # Gesture recording (preserve as-is)
├── GesturePlayer.kt                  # Gesture playback (preserve as-is)
├── PointerEventDispatcher.java       # Legacy, non-functional (remove)
├── WithPointerLocation.kt            # Overlay utility (preserve as-is)
└── bpok/gesture/
    ├── EdgeGestureEngine.kt          # NEW: Unified state machine (replaces Detector + SystemGesturesPointerEventListener)
    ├── EdgeRegionResolver.kt         # NEW: 16-region hit testing with fallback
    ├── GestureDirectionResolver.kt   # NEW: Dominant-axis direction detection
    ├── GestureTimerController.kt     # NEW: Long-press + double-tap timer management
    ├── GestureActionResolver.kt      # NEW: 16×7 action table lookup with fallback
    ├── ContinuousActionHandler.kt    # NEW: Interface for continuous adjustment actions
    ├── InputInjectionBridge.kt       # NEW: Abstracts legacy vs modern input injection
    ├── DetectorService.kt            # PRESERVE: Interface boundary
    ├── DetectorServiceImpl.kt        # ADAPT: Wire to new engine instead of old Detector
    ├── EdgeGestureSequenceDetector.kt # PRESERVE: Multi-step sequence detection
    ├── EdgeGestureSequenceHintReporter.kt  # PRESERVE: Sequence hint bridge
    ├── EdgeGestureHintDetector.kt    # ADAPT: Wire to new engine events
    ├── CornerSwipeToCenterDetector.kt     # PRESERVE: Corner gesture detection
    ├── CornerSwipeToCenterGeometry.kt     # PRESERVE: Corner geometry
    ├── GestureHintController.kt      # PRESERVE: Hint overlay management
    ├── GestureHapticController.kt    # PRESERVE: Haptic feedback
    ├── GestureHintModel.kt           # PRESERVE: Hint data model
    ├── GestureHintState.kt           # PRESERVE: Hint state enum
    ├── GestureHintView.kt            # PRESERVE: Hint rendering
    ├── Area.kt                       # ADAPT: Add edge region rect computation for 16 regions
    ├── DetectedArea.kt               # PRESERVE: Sealed interface
    └── GlobalSettings.kt             # PRESERVE: Global enable flag
```

**Structure Decision**: Extend the existing `services/src/main/java/tornaco/apps/shortx/services/gesture/bpok/gesture/` package. New classes go in the same package to minimize import changes. The old `Detector.kt` and `SystemGesturesPointerEventListener.java` will be deprecated and replaced by `EdgeGestureEngine.kt`.

## Implementation Phases

### Phase 1: Core State Machine (P1 — User Stories 1-4)

**Goal**: Replace the current swipe detection with a proper state machine that supports edge region detection, direction determination, tap/double-tap/long-press, and event caching/replay.

#### Step 1.1: EdgeRegionResolver

Create `EdgeRegionResolver.kt`:
- Implement 16-region hit testing based on `Area.kt` geometry
- Map touch coordinates to edge region ID (0-15) or -1 (no edge)
- Support configurable edge width/height
- Support parent-edge fallback (细分 → 大边缘)

#### Step 1.2: GestureDirectionResolver

Create `GestureDirectionResolver.kt`:
- Implement dominant-axis direction detection
- Compute dx, dy from start to current position
- Return direction only when displacement exceeds minimum threshold
- Return direction enum: UNKNOWN/LEFT/RIGHT/UP/DOWN

#### Step 1.3: GestureTimerController

Create `GestureTimerController.kt`:
- Manage long-press timer (configurable timeout)
- Manage double-tap timer (configurable timeout)
- Provide callbacks on timer expiry
- Support cancel/reset

#### Step 1.4: GestureActionResolver

Create `GestureActionResolver.kt`:
- Implement 16×7 action table lookup
- Support parent-edge fallback (check细分 region first, then parent)
- Read bindings from `DetectorService` configuration
- Support condition-based action resolution

#### Step 1.5: InputInjectionBridge

Create `InputInjectionBridge.kt`:
- Abstract the two input injection paths (legacy native vs modern)
- Provide `injectEvent(event)` and `enableFilter(enabled)` methods
- Wrap `IMSFunc` with version-aware dispatch

#### Step 1.6: EdgeGestureEngine

Create `EdgeGestureEngine.kt` — the core state machine:
- State fields: `downX`, `downY`, `hitRegion`, `recognizedDirection`, `gestureCaptured`, `executionStarted`, `cachedDownEvent`, `firstTapUpEvent`, `continuousHandler`
- `handleTouchSequence(hookParam, event)` — main entry, dispatches to:
  - `handleEdgeDownStart()` — reset state, detect region, start long-press timer
  - `handleMoveBeforeTrigger()` — determine direction, check action, start execution if threshold met
  - `finalizeGestureOnFingerUp()` — handle tap/double-tap/long-press/swipe completion
  - `handleSecondTapForDoubleTap()` — double-tap window handling
  - Continuous action lifecycle forwarding for move/up/cancel
- Event caching: copy down event, replay on cancellation, consume on execution

#### Step 1.7: Wire Engine to ShortXGestureMain

Update `ShortXGestureMain.kt`:
- Replace `Detector` with `EdgeGestureEngine`
- Keep `filterMotionEvent()` entry point
- Wire `DetectorServiceImpl` to new engine

#### Step 1.8: Wire Engine to DetectorServiceImpl

Update `DetectorServiceImpl.kt`:
- Update action resolution to use `GestureActionResolver`
- Keep `DetectorService` interface unchanged

### Phase 2: Visual & Haptic Feedback (P2 — User Stories 5-6)

**Goal**: Connect the new engine to existing hint/haptic infrastructure.

#### Step 2.1: EdgeGestureHintDetector Adaptation

Update `EdgeGestureHintDetector.kt`:
- Receive events from `EdgeGestureEngine` instead of `Detector`
- Trigger candidate/showing/ready/success states based on engine state
- Report to existing `GestureHintController`

#### Step 2.2: Continuous Action Handler

Create `ContinuousActionHandler.kt` interface:
- `onStart(edgeGroup, x, y, time)`
- `onMove(x, y, time)`
- `onFinish(x, y, time)`
- `onCancel()`
- Factory method in `SystemActionCoordinator` to create handlers by action type

### Phase 3: Advanced Features (P2-P3 — User Stories 5, 7, 8)

**Goal**: Implement continuous actions, haptic feedback integration, and preserve recording/playback.

#### Step 3.1: Continuous Action Handlers

Implement concrete handlers:
- Brightness handler (displacement → brightness value)
- Volume handler (displacement → volume level)
- Generic value handler (displacement → arbitrary value)

#### Step 3.2: Haptic Integration

Connect `GestureHapticController` to new engine events:
- Candidate haptic on edge touch
- Ready haptic on threshold reached
- Success haptic on action trigger

#### Step 3.3: Recording/Playback Preservation

Verify `GestureRecorder` and `GesturePlayer` work with new engine:
- Recording: `GestureService.filterInput()` routes to recorder before engine
- Playback: `GesturePlayer` injects events directly, bypasses engine
- No changes expected

### Phase 4: Cleanup

#### Step 4.1: Remove Legacy Components

- Remove `Detector.kt` (replaced by `EdgeGestureEngine.kt`)
- Remove `SystemGesturesPointerEventListener.java` (functionality absorbed by engine)
- Remove `PointerEventDispatcher.java` (already non-functional)

#### Step 4.2: Update Tests

- Add unit tests for `EdgeGestureEngine` state machine
- Add unit tests for `EdgeRegionResolver` geometry
- Add unit tests for `GestureDirectionResolver` logic
- Preserve existing `EdgeGestureSequenceDetectorTest.kt`

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| New state machine class (`EdgeGestureEngine`) | The technical doc requires a unified state machine with event caching/replay, which the current multi-detector architecture cannot support | Keeping separate detectors would require cross-detector state sharing for event caching, leading to more complex coupling than a single state machine |
