# Research: Edge Gesture System Rewrite

**Date**: 2026-04-30  
**Feature**: Edge Gesture System Rewrite

## Decision 1: Replace multi-detector architecture with unified state machine

**Decision**: Replace `Detector.kt` + `SystemGesturesPointerEventListener.java` + Android `GestureDetector` with a single `EdgeGestureEngine` state machine.

**Rationale**: The technical doc (`AI/EDGE_GESTURE_TECHNICAL_DOC.md`) describes a unified state machine (`b0`) that handles all gesture types (tap, double-tap, long-press, swipe) in a single flow with shared state (cached down event, recognized direction, execution phase). The current architecture splits detection across 4 independent detectors, making it impossible to implement event caching/replay (a cancelled gesture in one detector can't replay the cached down event that another detector consumed).

**Alternatives considered**:
- Keep separate detectors + add a shared state layer: Rejected because it adds coupling without simplifying the logic. The state machine is inherently sequential (DOWN → MOVE → UP with branching).
- Keep `SystemGesturesPointerEventListener` for simple swipes + add state machine for complex gestures: Rejected because the technical doc requires all gestures to share the same event cache and replay mechanism.

## Decision 2: Preserve DetectorService interface boundary

**Decision**: Keep `DetectorService` interface unchanged. Adapt `DetectorServiceImpl` to call the new engine.

**Rationale**: `DetectorService` is the contract between the gesture detection layer and the ShortX rule engine. It's called from `DetectorServiceImpl` which talks to `ShortXManager` via AIDL binder. Changing this interface would require changes across the binder boundary, affecting the core service module. The interface is well-designed and testable (existing tests use fakes).

**Alternatives considered**:
- Redesign `DetectorService` to match new engine API: Rejected because the existing interface already supports all needed operations (`handleEdgeGestureEvent`, `findMatchingEdgeGestureSequences`, `fireEdgeGestureSequence`, `handleCornerSwipeToCenterEvent`).

## Decision 3: Event caching strategy

**Decision**: Cache the initial `ACTION_DOWN` event as a deep copy (`MotionEvent.obtain(event)`). Replay via `InputInjectionBridge.injectEvent()` on cancellation. Recycle on execution.

**Rationale**: The technical doc (Section 13) explicitly requires caching the down event and replaying it when the gesture is invalid. This is critical for preventing "lost touches" at screen edges. The current `IMSFunc` already supports input injection with proper policy flags.

**Alternatives considered**:
- Don't cache, just let touches pass through: Rejected because the system must first determine if a touch is an edge gesture before deciding to consume it. Without caching, the down event would be lost during the determination window.
- Cache as serialized bytes: Rejected because `MotionEvent.obtain(event)` is the standard Android approach and preserves all fields including pointer data.

## Decision 4: Direction detection algorithm

**Decision**: Use dominant-axis logic: compute `dx²` and `dy²`, compare which is larger, then check sign for direction.

**Rationale**: The technical doc (Section 7.2) explicitly states this algorithm. It's simpler and more predictable than angle-based bucketing. The current `SystemGesturesPointerEventListener` already uses a similar displacement-threshold approach, so this is a refinement rather than a complete change.

**Alternatives considered**:
- Angle-based bucketing (atan2): Rejected because the technical doc specifies dominant-axis, and angle calculations are more expensive (trigonometry vs multiplication).
- Dot-product against direction vectors: Used only for `EdgeGestureSequenceDetector` (which is preserved), not for the primary direction resolver.

## Decision 5: 16-region geometry computation

**Decision**: Extend `Area.kt` to provide edge region rectangles for all 16 regions. Each region is a `RectF` computed from screen dimensions, edge width/height, and 3-segment subdivision.

**Rationale**: `Area.kt` already has `Edge.getRect()` for the existing edge zones. The new system needs the same geometry but with explicit 16-region mapping (0-15) matching the protobuf `Edge` enum.

**Alternatives considered**:
- Compute regions on-the-fly in `EdgeRegionResolver`: Rejected because the geometry is already defined in `Area.kt` and cached in `Detector`. Keeping the geometry in one place avoids duplication.

## Decision 6: Preserve visual feedback infrastructure

**Decision**: Keep `GestureHintController`, `GestureHintView`, `GestureHapticController`, `GestureHintModel`, `GestureHintState` unchanged. Adapt `EdgeGestureHintDetector` to receive events from the new engine.

**Rationale**: The visual feedback system is well-designed (token-based lifecycle, state machine, overlay management) and works independently of the detection engine. The only coupling is through `EdgeGestureHintDetector` which receives touch events and reports hint state. Changing the event source is a small adapter change.

**Alternatives considered**:
- Rewrite hint system to match technical doc's `d3`/`y2` layers: Rejected because the existing hint system already provides the required functionality (candidate/showing/ready/success states, directional arrow, ring mode) and is proven to work.

## Decision 7: Input injection version compatibility

**Decision**: Create `InputInjectionBridge` that wraps the three injection paths in `IMSFunc` (InputFilterHost, pre-13 native, 13+ mNative).

**Rationale**: The technical doc (Section 3.4) emphasizes version compatibility. `IMSFunc` already handles this but the logic is complex. Wrapping it in a clean interface makes the engine code version-agnostic.

**Alternatives considered**:
- Use only one injection path: Rejected because different Android versions require different APIs.
- Direct calls to `IMSFunc`: Accepted as fallback if the bridge adds no value beyond delegation.

## Decision 8: Preserve recording/playback

**Decision**: Keep `GestureRecorder` and `GesturePlayer` unchanged. They operate on the input event stream before/after the engine.

**Rationale**: Recording captures raw events from `GestureService.filterInput()` before they reach the engine. Playback injects events via `IMSFunc` directly. Neither depends on the engine's internal state.

**Alternatives considered**:
- Integrate recording into the engine: Rejected because recording is a separate concern (capture all events, not just edge gestures).
