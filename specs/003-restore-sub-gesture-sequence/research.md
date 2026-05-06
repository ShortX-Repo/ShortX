# Research: Restore Sub-Gesture Sequence Detection

**Date**: 2026-04-30  
**Feature**: Restore Sub-Gesture Sequence Detection

## Decision 1: Where to start sequence tracking

**Decision**: Start sequence tracking in `EdgeGestureEngine.startExecution()`, right after firing the simple rule.

**Rationale**: The sequence detector needs the primary gesture and anchor coordinates, which are available when the swipe threshold is met. The finger is already moving, so the sequence detector can immediately start matching child steps. Starting in `handleUp()` would be too late — hover steps need the finger to be down.

**Alternatives considered**:
- Start in `handleUp()`: Rejected — hover steps need the finger to still be down.
- Start in `handleMove()` before threshold: Rejected — we don't know the primary gesture yet.

## Decision 2: Event forwarding strategy

**Decision**: When `sequenceDetector.isTracking()`, forward MOVE/UP/CANCEL to the detector. Skip the engine's normal UP handling (which would fire the simple rule again).

**Rationale**: The sequence detector needs to see UP events for hover completion. The engine's normal UP handling should be skipped because the sequence detector will fire the final action via `service.fireEdgeGestureSequence()`.

**Alternatives considered**:
- Forward only MOVE, let engine handle UP: Rejected — hover completion needs UP.
- Clone events before forwarding: Rejected — unnecessary, the detector doesn't recycle events.

## Decision 3: Coexistence of simple and sequence rules

**Decision**: Both fire for the same initial swipe. The simple rule fires immediately via `service.handleEdgeGestureEvent()`, and the sequence detector starts tracking if candidates exist.

**Rationale**: `DetectorServiceImpl.shouldFireEdgeGesture()` already excludes rules with `childStepsCount > 0`. So the simple path and sequence path are naturally separated. No conflict.

**Alternatives considered**:
- Only fire one or the other: Rejected — the spec explicitly requires both to coexist.

## Decision 4: Sequence detector creation

**Decision**: Create the sequence detector in `ShortXGestureMain` and inject it into the engine.

**Rationale**: The sequence detector needs `Handler`, `DetectorService`, and `EdgeGestureSequenceHintSink`. All are available in `ShortXGestureMain`. Creating it once avoids unnecessary allocations.

**Alternatives considered**:
- Create inside the engine: Rejected — the engine shouldn't know about hint/haptic controllers.
- Lazy creation: Rejected — the detector is lightweight, no benefit to lazy creation.
