# Data Model: Integrate Gesture Indicator for Simple Swipes

**Date**: 2026-04-30  
**Feature**: Integrate Gesture Indicator for Simple Swipes

No new entities. All entities already exist in the codebase.

## Existing Entities (preserved)

### EdgeGestureHintDetector

Already implemented (340 lines). Key state:
- `trackedToken: Long` — unique ID for current gesture lifecycle
- `trackedAreas: List<DetectedArea>` — areas with feedback-enabled rules
- `downX/Y: Float` — touch-down position
- `hasHintShown: Boolean` — whether hint overlay is currently visible
- `hasTriggered: Boolean` — whether gesture has fired

### GestureHintModel

Already implemented. Immutable data for rendering one frame:
- `token: Long` — correlates gesture lifecycle to overlay
- `anchorX/Y: Float` — touch-down position
- `directionX/Y: Float` — normalized direction vector
- `progress: Float` — 0.0 to 1.0 progress toward threshold
- `state: GestureHintState` — Candidate/Showing/Ready/Success
- `showArrow: Boolean` — whether to draw directional arrow

### GestureHintState

Already implemented. Enum: `Candidate`, `Showing`, `Ready`, `Success`.

## State Transitions

The `EdgeGestureHintDetector` manages its own state:
- `IDLE` → `TRACKING` (on ACTION_DOWN with feedback-enabled areas)
- `TRACKING` → `SHOWING` (on ACTION_MOVE with direction match)
- `SHOWING` → `READY` (when distance >= threshold and angle within 30°)
- `READY` → `SUCCESS` (on `onEdgeGestureTriggered()` callback)
- `ANY` → `IDLE` (on ACTION_UP/CANCEL or `onEdgeGestureSequenceStarted()`)
